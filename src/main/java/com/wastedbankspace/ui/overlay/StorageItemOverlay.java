/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2021, Riley McGee
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.wastedbankspace.ui.overlay;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.wastedbankspace.WastedBankSpaceConfig;
import com.wastedbankspace.WastedBankSpacePlugin;
import com.wastedbankspace.model.StorageLocations;
import com.wastedbankspace.poh.PohStorageTracker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class StorageItemOverlay extends WidgetItemOverlay
{
	/** Size of the house and STASH icons */
	private static final int ICON_SIZE = 13;

	private final Point point;

	private final Client client;
	private final WastedBankSpacePlugin plugin;
	private final WastedBankSpaceConfig config;
	private final ItemManager itemManager;
	private final TooltipManager tooltipManager;
	private final PohStorageTracker pohStorage;

	private final SpriteManager spriteManager;

	private static final Color TOOLTIP_COLOR = new Color(238, 238, 238);
	/** Tooltip lines wrap after this many characters, about half the width of the game in fixed mode */
	static final int MAX_TOOLTIP_LINE_LENGTH = 60;
	/** Between the locations in a tooltip line, where it may wrap */
	private static final String LOCATION_SEPARATOR = " / ";

	/** Icons drawn bottom-left on items already stored in the POH, loaded on first use */
	private final Map<HouseIcon, BufferedImage> houseIcons = new EnumMap<>(HouseIcon.class);

	/** STASH icons, trimmed of transparent borders, loaded on first use */
	private final Map<StashIcon, BufferedImage> stashIcons = new EnumMap<>(StashIcon.class);
	/** Item image STASH icons still loading */
	private final Set<StashIcon> loadingStashIcons = EnumSet.noneOf(StashIcon.class);

	@Getter
	private final Cache<Integer, BufferedImage> wastedSpaceImages = CacheBuilder.newBuilder()
		.maximumSize(160)
		.expireAfterWrite(2, TimeUnit.MINUTES)
		.build();

	@Inject
	StorageItemOverlay(Client client, WastedBankSpacePlugin plugin, WastedBankSpaceConfig config, ItemManager itemManager,
		TooltipManager tooltipManager, PohStorageTracker pohStorage, SpriteManager spriteManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.itemManager = itemManager;
		this.tooltipManager = tooltipManager;
		this.pohStorage = pohStorage;
		this.spriteManager = spriteManager;
		this.point = new Point();
		showOnBank();
		showOnInventory();
		showOnEquipment();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{
		Set<Integer> items = plugin.getEnabledItems();
		if (items.isEmpty())
		{
			return;
		}

		Area area = getArea(itemWidget.getWidget());
		if (area == Area.BANK && config.markPlaceholders())
		{
			itemId = getPlaceholderItemId(itemId);
		}

		// Outside the bank, Mark Inventory Items and Mark Equipped Items turn off everything, icons and tooltip too
		if (!items.contains(itemId) || !shouldMark(area))
		{
			return;
		}

		boolean inHouse = pohStorage.isStored(itemId);
		boolean showHouseIcon = inHouse && shouldShowHouseIcon(area);
		// Like the house icon, the STASH icon only shows once the item is stored; the marker already says it can be
		boolean showStashIcon = shouldShowStashIcon(area) && plugin.isStashed(plugin.getStashUnits(itemId));

		Rectangle bounds = itemWidget.getCanvasBounds();

		if (bounds.contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()))
		{
			List<String> lines = new ArrayList<>();
			String where = plugin.getStorageLocationText(itemId, inHouse);
			if (!where.isEmpty())
			{
				lines.add((inHouse ? "Already stored @ " : "Store @ ") + where);
			}
			lines.addAll(plugin.getStashTooltipLines(itemId, inHouse));
			if (!lines.isEmpty())
			{
				tooltipManager.add(new Tooltip(lines.stream()
					.flatMap(line -> wrapTooltipLine(line).stream())
					.map(line -> ColorUtil.prependColorTag(line, TOOLTIP_COLOR))
					.collect(Collectors.joining("</br>"))));
			}
		}

		// The house icon can take the marker's place in the bottom-right instead of sitting beside it
		boolean replaceMarker = showHouseIcon && config.houseIconReplacesMarker();

		if (!replaceMarker)
		{
			renderRibbon(graphics, plugin.getOverlayImage().getImage(), bounds.x + bounds.width - 12, bounds.y + bounds.height - 12);
		}

		if (showHouseIcon)
		{
			renderHouseIcon(graphics, bounds, replaceMarker);
		}

		if (showStashIcon)
		{
			// Items that can also go in the POH costume room may show the house icon bottom-left, so their STASH
			// icon always goes top-right instead
			boolean topRight = StorageLocations.canGoInCostumeRoom(itemId) || (showHouseIcon && !replaceMarker);
			renderStashIcon(graphics, bounds, config.stashIcon(), topRight);
		}
	}

	/**
	 * Wraps a tooltip line naming several locations, e.g. every STASH unit an item goes in, so the tooltip doesn't
	 * run off the side of the game. It only breaks between locations, leaving a " /" at the end of the line before,
	 * so a location longer than a line on its own still gets a line of its own rather than being split.
	 */
	static List<String> wrapTooltipLine(String line)
	{
		String[] locations = line.split(LOCATION_SEPARATOR);
		List<String> wrapped = new ArrayList<>();
		StringBuilder current = new StringBuilder(locations[0]);
		for (int i = 1; i < locations.length; i++)
		{
			if (current.length() + LOCATION_SEPARATOR.length() + locations[i].length() > MAX_TOOLTIP_LINE_LENGTH)
			{
				wrapped.add(current.append(LOCATION_SEPARATOR.stripTrailing()).toString());
				current = new StringBuilder(locations[i]);
			}
			else
			{
				current.append(LOCATION_SEPARATOR).append(locations[i]);
			}
		}
		wrapped.add(current.toString());
		return wrapped;
	}

	/**
	 * Draws the configured house icon in the bottom-left (or bottom-right) corner, scaled to fit while keeping its
	 * aspect ratio.
	 */
	private void renderHouseIcon(Graphics2D graphics, Rectangle bounds, boolean bottomRight)
	{
		HouseIcon selected = config.houseIcon();
		BufferedImage icon = houseIcons.get(selected);
		if (icon == null)
		{
			// Overlays render on the client thread, so the sprite can be read directly
			icon = spriteManager.getSprite(selected.getSpriteId(), 0);
			if (icon == null)
			{
				return;
			}
			houseIcons.put(selected, icon);
		}

		double scale = (double) ICON_SIZE / Math.max(icon.getWidth(), icon.getHeight());
		int width = (int) Math.round(icon.getWidth() * scale);
		int height = (int) Math.round(icon.getHeight() * scale);
		int x = bottomRight ? bounds.x + bounds.width - width : bounds.x;
		graphics.drawImage(icon, x, bounds.y + bounds.height - height, width, height, null);
	}

	/**
	 * Draws a STASH icon in the bottom-left (or top-right) corner, scaled to fit while keeping its aspect ratio.
	 */
	private void renderStashIcon(Graphics2D graphics, Rectangle bounds, StashIcon selected, boolean topRight)
	{
		BufferedImage icon = getStashIcon(selected);
		if (icon == null)
		{
			return;
		}

		double scale = (double) ICON_SIZE / Math.max(icon.getWidth(), icon.getHeight());
		int width = (int) Math.round(icon.getWidth() * scale);
		int height = (int) Math.round(icon.getHeight() * scale);
		int x = topRight ? bounds.x + bounds.width - width : bounds.x;
		int y = topRight ? bounds.y : bounds.y + bounds.height - height;
		graphics.drawImage(icon, x, y, width, height, null);
	}

	/**
	 * @return the icon's image, or null while it's still loading
	 */
	private BufferedImage getStashIcon(StashIcon selected)
	{
		BufferedImage icon = stashIcons.get(selected);
		if (icon != null)
		{
			return icon;
		}

		if (selected.getSpriteId() != -1)
		{
			// Overlays render on the client thread, so the sprite can be read directly
			icon = spriteManager.getSprite(selected.getSpriteId(), 0);
			if (icon != null)
			{
				stashIcons.put(selected, trim(icon));
			}
			return icon;
		}

		// Item images load in the background, and have wide transparent borders that would make the icon tiny
		if (loadingStashIcons.add(selected))
		{
			AsyncBufferedImage image = itemManager.getImage(selected.getItemId());
			image.onLoaded(() -> stashIcons.put(selected, trim(image)));
		}
		return null;
	}

	/**
	 * @return the image cropped to its non-transparent pixels
	 */
	static BufferedImage trim(BufferedImage image)
	{
		int minX = image.getWidth(), minY = image.getHeight(), maxX = -1, maxY = -1;
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				if ((image.getRGB(x, y) >>> 24) != 0)
				{
					minX = Math.min(minX, x);
					minY = Math.min(minY, y);
					maxX = Math.max(maxX, x);
					maxY = Math.max(maxY, y);
				}
			}
		}
		return maxX < 0 ? image : image.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
	}

	/**
	 * @return the real item a bank placeholder stands for, or the item id unchanged if it isn't a placeholder
	 */
	private int getPlaceholderItemId(int itemId)
	{
		ItemComposition composition = itemManager.getItemComposition(itemId);
		return composition.getPlaceholderTemplateId() != -1 ? composition.getPlaceholderId() : itemId;
	}

	private enum Area
	{
		BANK,
		INVENTORY,
		EQUIPMENT,
		/** Other bank widgets, e.g. the worn equipment shown inside the bank */
		NONE
	}

	private static Area getArea(Widget widget)
	{
		switch (WidgetUtil.componentToInterface(widget.getId()))
		{
			case InterfaceID.BANKMAIN:
			case InterfaceID.SHARED_BANK:
				return widget.getParentId() == ComponentID.BANK_ITEM_CONTAINER ? Area.BANK : Area.NONE;
			case InterfaceID.WORNITEMS:
				return Area.EQUIPMENT;
			default:
				return Area.INVENTORY;
		}
	}

	/**
	 * Bank items are always marked; inventory and worn equipment items only when enabled in the config, which also
	 * decides whether they get the house and STASH icons.
	 */
	private boolean shouldMark(Area area)
	{
		switch (area)
		{
			case BANK:
				return true;
			case INVENTORY:
				return config.markInventoryItems();
			case EQUIPMENT:
				return config.markEquippedItems();
			default:
				return false;
		}
	}

	private boolean shouldShowHouseIcon(Area area)
	{
		switch (area)
		{
			case BANK:
				return config.houseIconInBank();
			case INVENTORY:
			case EQUIPMENT:
				return config.houseIconInInventory();
			default:
				return false;
		}
	}

	private boolean shouldShowStashIcon(Area area)
	{
		switch (area)
		{
			case BANK:
				return config.stashIconInBank();
			case INVENTORY:
			case EQUIPMENT:
				return config.stashIconInInventory();
			default:
				return false;
		}
	}

	private void renderRibbon(Graphics2D graphics, ImageComponent ribbon, int x, int y)
	{
		this.point.setLocation(x, y);
		ribbon.setPreferredLocation(this.point);
		ribbon.render(graphics);
	}
}