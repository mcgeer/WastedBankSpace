/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, Brettgod1355
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

package com.wastedbankspace.banktag;

import com.google.common.base.Strings;
import com.wastedbankspace.WastedBankSpaceConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.banktags.BankTagsPlugin;
import net.runelite.client.plugins.banktags.BankTagsService;
import net.runelite.client.plugins.banktags.TagManager;
import net.runelite.client.util.Text;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Dynamic "wasted" bank tag holding every item flagged as wasting bank space, plus a tag tab to open it.
 * The tab's item icon is made transparent so {@link com.wastedbankspace.ui.overlay.BankTagTabOverlay}
 * can draw the configured overlay image in its place.
 */
@Slf4j
@Singleton
public class WastedBankTag
{
	public static final String TAG_NAME = "wasted";

	/** Shown in places the overlay doesn't reach, e.g. the "View tag tabs" list */
	private static final int FALLBACK_ICON_ITEM_ID = ItemID.BANK_FILLER;
	/** Set when this plugin created the tab, so it only ever removes a tab it added */
	private static final String TAB_CREATED_KEY = "bankTagTabCreated";
	private static final int TRANSPARENT = 255;

	private final Client client;
	private final ClientThread clientThread;
	private final ConfigManager configManager;
	private final TagManager tagManager;
	private final BankTagsService bankTagsService;
	private final WastedBankSpaceConfig config;

	private volatile Set<Integer> items = Collections.emptySet();
	private boolean registered = false;

	@Inject
	WastedBankTag(Client client, ClientThread clientThread, ConfigManager configManager, TagManager tagManager,
		BankTagsService bankTagsService, WastedBankSpaceConfig config)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.configManager = configManager;
		this.tagManager = tagManager;
		this.bankTagsService = bankTagsService;
		this.config = config;
	}

	public void startUp()
	{
		clientThread.invokeLater(this::sync);
	}

	public void shutDown()
	{
		// The tab itself is left in place so it keeps its position if the plugin is re-enabled
		clientThread.invokeLater(() ->
		{
			unregisterTag();
			restoreTabIcon();
			refreshIfActive();
		});
	}

	/**
	 * Applies the bank tag tab config option. Must be called on the client thread.
	 */
	public void sync()
	{
		if (config.bankTagTab())
		{
			registerTag();
			addTab();
		}
		else
		{
			unregisterTag();
			removeTab();
			restoreTabIcon();
		}
		refreshIfActive();
	}

	/**
	 * Updates the items in the tag, i.e. the items currently flagged as storable elsewhere.
	 */
	public void setItems(Set<Integer> flaggedItems)
	{
		Set<Integer> copy = Set.copyOf(flaggedItems);
		if (copy.equals(items))
		{
			return;
		}
		items = copy;
		clientThread.invokeLater(this::refreshIfActive);
	}

	/**
	 * Hides the tab's item icon every frame, since the bank tags plugin recreates the tab widgets whenever it
	 * rebuilds them. Transparent rather than hidden so the tab can still be dragged to reorder it.
	 */
	public void onBeforeRender()
	{
		if (!registered)
		{
			return;
		}

		Widget[] tab = findTabWidgets();
		if (tab != null && tab[1].getOpacity() != TRANSPARENT)
		{
			tab[1].setOpacity(TRANSPARENT);
		}
	}

	/**
	 * @return the tag tab's {background, icon} widgets, or null if the bank tab bar isn't showing it
	 */
	@Nullable
	public Widget[] findTabWidgets()
	{
		return findTabWidgets(getTabBar());
	}

	/**
	 * @return where the tag tab is on the canvas, or null if the bank tab bar isn't showing it
	 */
	@Nullable
	public Rectangle getTabBounds()
	{
		Widget tabBar = getTabBar();
		Widget[] tab = findTabWidgets(tabBar);
		if (tab == null || tab[0].isHidden())
		{
			return null;
		}

		// The bank tags plugin recreates the tab widgets every time the bank is built, e.g. on each tab switch and
		// search keystroke, and a new widget has no canvas location until the client has drawn it. Its place in the
		// tab bar is set as soon as it's created though, and the tab bar itself is never recreated.
		Widget background = tab[0];
		Point origin = tabBar.getCanvasLocation();
		return new Rectangle(
			origin.getX() + background.getRelativeX() - tabBar.getScrollX(),
			origin.getY() + background.getRelativeY() - tabBar.getScrollY(),
			background.getWidth(),
			background.getHeight());
	}

	/**
	 * @return the widget holding the bank tag tabs, or null if the bank isn't showing it
	 */
	@Nullable
	private Widget getTabBar()
	{
		Widget container = client.getWidget(InterfaceID.Bankmain.ITEMS_CONTAINER);
		if (container == null || container.isHidden() || container.getChildren() == null)
		{
			return null;
		}
		return container;
	}

	@Nullable
	private static Widget[] findTabWidgets(@Nullable Widget container)
	{
		if (container == null)
		{
			return null;
		}

		Widget background = null;
		Widget icon = null;
		for (Widget child : container.getChildren())
		{
			if (child == null || child.getName() == null || !TAG_NAME.equals(Text.removeTags(child.getName())))
			{
				continue;
			}

			if (child.getItemId() > -1)
			{
				icon = child;
			}
			else
			{
				background = child;
			}
		}

		return background != null && icon != null ? new Widget[]{background, icon} : null;
	}

	public boolean isEnabled()
	{
		return registered;
	}

	private void registerTag()
	{
		if (!registered)
		{
			tagManager.registerTag(TAG_NAME, itemId -> items.contains(itemId));
			registered = true;
			log.debug("Registered bank tag '{}'", TAG_NAME);
		}
	}

	private void unregisterTag()
	{
		if (registered)
		{
			tagManager.unregisterTag(TAG_NAME);
			registered = false;
			log.debug("Unregistered bank tag '{}'", TAG_NAME);
		}
	}

	/**
	 * Adds the tab through the bank tags config, which the bank tags plugin loads each time the bank opens.
	 */
	private void addTab()
	{
		List<String> tabs = getTabNames();
		boolean created = Boolean.TRUE.equals(configManager.getConfiguration(WastedBankSpaceConfig.GROUP, TAB_CREATED_KEY, Boolean.class));
		if (!tabs.contains(TAG_NAME))
		{
			tabs.add(TAG_NAME);
			configManager.setConfiguration(BankTagsPlugin.CONFIG_GROUP, BankTagsPlugin.TAG_TABS_CONFIG, Text.toCSV(tabs));
			configManager.setConfiguration(WastedBankSpaceConfig.GROUP, TAB_CREATED_KEY, true);
			created = true;
		}

		// Only give the tab an icon if this plugin created it; a user's own "wasted" tab keeps whatever it has
		String iconKey = BankTagsPlugin.TAG_ICON_PREFIX + TAG_NAME;
		if (created && configManager.getConfiguration(BankTagsPlugin.CONFIG_GROUP, iconKey) == null)
		{
			configManager.setConfiguration(BankTagsPlugin.CONFIG_GROUP, iconKey, FALLBACK_ICON_ITEM_ID);
		}
	}

	private void removeTab()
	{
		if (!Boolean.TRUE.equals(configManager.getConfiguration(WastedBankSpaceConfig.GROUP, TAB_CREATED_KEY, Boolean.class)))
		{
			return;
		}

		List<String> tabs = getTabNames();
		if (tabs.remove(TAG_NAME))
		{
			configManager.setConfiguration(BankTagsPlugin.CONFIG_GROUP, BankTagsPlugin.TAG_TABS_CONFIG, Text.toCSV(tabs));
		}
		configManager.unsetConfiguration(BankTagsPlugin.CONFIG_GROUP, BankTagsPlugin.TAG_ICON_PREFIX + TAG_NAME);
		configManager.unsetConfiguration(WastedBankSpaceConfig.GROUP, TAB_CREATED_KEY);
	}

	private List<String> getTabNames()
	{
		String tabs = configManager.getConfiguration(BankTagsPlugin.CONFIG_GROUP, BankTagsPlugin.TAG_TABS_CONFIG);
		return new ArrayList<>(Text.fromCSV(Strings.nullToEmpty(tabs)));
	}

	private void restoreTabIcon()
	{
		Widget[] tab = findTabWidgets();
		if (tab != null)
		{
			tab[1].setOpacity(0);
		}
	}

	/**
	 * Re-opens the tag if it's the one being viewed, so the bank reflects the current flagged items.
	 */
	private void refreshIfActive()
	{
		if (TAG_NAME.equals(bankTagsService.getActiveTag()))
		{
			bankTagsService.openBankTag(TAG_NAME, BankTagsService.OPTION_ALLOW_MODIFICATIONS);
		}
	}
}
