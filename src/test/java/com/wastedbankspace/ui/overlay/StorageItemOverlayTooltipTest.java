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

package com.wastedbankspace.ui.overlay;

import com.wastedbankspace.WastedBankSpaceConfig;
import com.wastedbankspace.WastedBankSpacePlugin;
import com.wastedbankspace.model.StorageLocations;
import com.wastedbankspace.model.locations.CapeRack;
import com.wastedbankspace.model.locations.ForestryKit;
import com.wastedbankspace.poh.PohStorageTracker;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.Text;
import org.junit.Before;
import org.junit.Test;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The storage location in the tooltip of {@link StorageItemOverlay} comes from
 * {@link WastedBankSpacePlugin#getStorageLocationText(int, boolean)}, which knows the enabled locations and every
 * location of an item, rather than from the single entry per item in {@link StorageLocations}. The plugin is mocked
 * the way {@link StorageItemOverlayTest} does it, so the text can be anything the registry would not produce.
 */
public class StorageItemOverlayTooltipTest
{
	/** The woodcutting cape, listed by the cape rack (a costume room storage) and the forestry kit */
	private static final int ITEM_ID = ItemID.SKILLCAPE_WOODCUTTING;
	/** Bank placeholder standing in for ITEM_ID (placeholders have their own item id) */
	private static final int PLACEHOLDER_ID = 1_000_001;
	/** Placeholders are built from this template item; real items have a template id of -1 */
	private static final int PLACEHOLDER_TEMPLATE_ID = 14401;

	/** What the plugin says while the cape is not in the house: both locations, which no registry entry names */
	private static final String STORE_TEXT = "Forestry Kit / Cape Rack";
	/** What the plugin says while the cape is in the house: the costume room storage */
	private static final String STORED_TEXT = "Cape Rack";

	/** Item slot on the canvas: x 100..135, y 200..231 */
	private static final Rectangle SLOT = new Rectangle(100, 200, 36, 32);
	private static final Point MOUSE_INSIDE = new Point(110, 210);
	private static final Point MOUSE_OUTSIDE = new Point(136, 232);

	private Client client;
	private WastedBankSpacePlugin plugin;
	private WastedBankSpaceConfig config;
	private ItemManager itemManager;
	private TooltipManager tooltipManager;
	private PohStorageTracker pohStorage;
	private SpriteManager spriteManager;
	private Graphics2D graphics;

	private StorageItemOverlay overlay;

	@Before
	public void setUp()
	{
		// Guards the choice of test item: the registry names one of its two locations, and the texts above name others
		assertEquals(List.of(CapeRack.WOODCUTTING_CAPE_2, ForestryKit.WOODCUTTING_CAPE),
			StorageLocations.getStorableItems(ITEM_ID));

		client = mock(Client.class);
		plugin = mock(WastedBankSpacePlugin.class);
		config = mock(WastedBankSpaceConfig.class);
		itemManager = mock(ItemManager.class);
		tooltipManager = new TooltipManager();
		pohStorage = mock(PohStorageTracker.class);
		spriteManager = mock(SpriteManager.class);
		graphics = mock(Graphics2D.class);

		Set<Integer> enabledItems = new HashSet<>(Set.of(ITEM_ID));
		when(plugin.getEnabledItems()).thenReturn(enabledItems);
		when(plugin.getOverlayImage()).thenReturn(OverlayImage.DOT_RED);
		when(plugin.getStorageLocationText(ITEM_ID, false)).thenReturn(STORE_TEXT);
		when(plugin.getStorageLocationText(ITEM_ID, true)).thenReturn(STORED_TEXT);

		// Same values as the config defaults
		when(config.markInventoryItems()).thenReturn(false);
		when(config.markEquippedItems()).thenReturn(false);
		when(config.houseIconInBank()).thenReturn(true);
		when(config.houseIconInInventory()).thenReturn(true);
		when(config.houseIcon()).thenReturn(HouseIcon.TELEPORT_SPELL);
		when(config.houseIconReplacesMarker()).thenReturn(false);
		when(config.markPlaceholders()).thenReturn(true);

		when(client.getMouseCanvasPosition()).thenReturn(MOUSE_INSIDE);
		BufferedImage houseSprite = new BufferedImage(26, 20, BufferedImage.TYPE_INT_ARGB);
		when(spriteManager.getSprite(anyInt(), anyInt())).thenReturn(houseSprite);

		// Real items point at their placeholder but have no template; placeholders point back at the real item
		ItemComposition noPlaceholder = composition(-1, -1);
		ItemComposition item = composition(PLACEHOLDER_ID, -1);
		ItemComposition placeholder = composition(ITEM_ID, PLACEHOLDER_TEMPLATE_ID);
		when(itemManager.getItemComposition(anyInt())).thenReturn(noPlaceholder);
		when(itemManager.getItemComposition(ITEM_ID)).thenReturn(item);
		when(itemManager.getItemComposition(PLACEHOLDER_ID)).thenReturn(placeholder);

		overlay = new StorageItemOverlay(client, plugin, config, itemManager, tooltipManager, pohStorage, spriteManager);
	}

	@Test
	public void tooltipNamesWhereThePluginSaysToStoreTheItem()
	{
		render(ITEM_ID, bankItem());

		assertEquals(List.of("Store @ " + STORE_TEXT), tooltips());
		verify(plugin).getStorageLocationText(ITEM_ID, false);
		verify(plugin, never()).getStorageLocationText(anyInt(), eq(true));
	}

	@Test
	public void tooltipOfItemInTheHouseNamesWhereThePluginSaysItIsStored()
	{
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());

		assertEquals(List.of("Already stored @ " + STORED_TEXT), tooltips());
		verify(plugin).getStorageLocationText(ITEM_ID, true);
		verify(plugin, never()).getStorageLocationText(anyInt(), eq(false));
	}

	@Test
	public void inventoryAndWornTooltipsUseTheSameLookup()
	{
		when(config.markInventoryItems()).thenReturn(true);
		render(ITEM_ID, inventoryItem());
		assertEquals(List.of("Store @ " + STORE_TEXT), tooltips());

		tooltipManager.clear();
		when(config.markEquippedItems()).thenReturn(true);
		storedInHouse(ITEM_ID);
		render(ITEM_ID, wornItem());
		assertEquals(List.of("Already stored @ " + STORED_TEXT), tooltips());
	}

	@Test
	public void placeholderTooltipAsksAboutTheItemItStandsFor()
	{
		render(PLACEHOLDER_ID, bankItem());

		assertEquals(List.of("Store @ " + STORE_TEXT), tooltips());
		verify(plugin).getStorageLocationText(ITEM_ID, false);
		verify(plugin, never()).getStorageLocationText(eq(PLACEHOLDER_ID), anyBoolean());
	}

	@Test
	public void noLookupWhileTheMouseIsNotOverTheItem()
	{
		when(client.getMouseCanvasPosition()).thenReturn(MOUSE_OUTSIDE);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());

		assertEquals(Collections.emptyList(), tooltips());
		verify(plugin, never()).getStorageLocationText(anyInt(), anyBoolean());
	}

	@Test
	public void longTooltipWrapsBetweenLocations()
	{
		// A blue wizard robe goes in two STASH units, whose names together are wider than the game
		when(plugin.getStorageLocationText(ITEM_ID, false)).thenReturn("Road junction south of Sinclair Mansion"
			+ " (Easy STASH) / Where the River Ortus meets the Proudspire (Medium STASH)");
		when(plugin.getStashTooltipLines(ITEM_ID, false)).thenReturn(List.of("Already stashed @ Draynor Village"
			+ " market (Easy STASH) / Aris' tent (Beginner STASH) / Rimmington mine (Easy STASH)"));

		render(ITEM_ID, bankItem());

		assertEquals(List.of(
			"Store @ Road junction south of Sinclair Mansion (Easy STASH) /",
			"Where the River Ortus meets the Proudspire (Medium STASH)",
			"Already stashed @ Draynor Village market (Easy STASH) /",
			"Aris' tent (Beginner STASH) / Rimmington mine (Easy STASH)"),
			tooltipLines());
	}

	@Test
	public void shortTooltipLineStaysOnOneLine()
	{
		assertEquals(List.of("Store @ " + STORE_TEXT), StorageItemOverlay.wrapTooltipLine("Store @ " + STORE_TEXT));
	}

	@Test
	public void wrappedLinesFitUnlessOneLocationIsLongerThanALine()
	{
		String longName = "x".repeat(StorageItemOverlay.MAX_TOOLTIP_LINE_LENGTH + 5);
		List<String> lines = StorageItemOverlay.wrapTooltipLine(
			"Store @ Cape Rack / " + longName + " / Forestry Kit / Tackle Box / Tool Leprechaun");

		// The long name isn't split, it just gets a line of its own
		assertEquals(List.of("Store @ Cape Rack /", longName + " /", "Forestry Kit / Tackle Box / Tool Leprechaun"),
			lines);
	}

	private static ItemComposition composition(int placeholderId, int placeholderTemplateId)
	{
		ItemComposition composition = mock(ItemComposition.class);
		when(composition.getPlaceholderId()).thenReturn(placeholderId);
		when(composition.getPlaceholderTemplateId()).thenReturn(placeholderTemplateId);
		return composition;
	}

	private static Widget widget(int componentId, int parentId)
	{
		Widget widget = mock(Widget.class);
		when(widget.getId()).thenReturn(componentId);
		when(widget.getParentId()).thenReturn(parentId);
		return widget;
	}

	/** An item in the bank's item container */
	private static Widget bankItem()
	{
		return widget(InterfaceID.Bankmain.ITEMS, InterfaceID.Bankmain.ITEMS);
	}

	private static Widget inventoryItem()
	{
		return widget(InterfaceID.Inventory.ITEMS, InterfaceID.Inventory.ITEMS);
	}

	private static Widget wornItem()
	{
		return widget(InterfaceID.Wornitems.SLOT1, InterfaceID.Wornitems.UNIVERSE);
	}

	private void render(int itemId, Widget widget)
	{
		overlay.renderItemOverlay(graphics, itemId, new WidgetItem(itemId, 1, SLOT, widget, null));
	}

	private void storedInHouse(int itemId)
	{
		when(pohStorage.isStored(itemId)).thenReturn(true);
	}

	private List<String> tooltips()
	{
		return tooltipManager.getTooltips().stream()
			.map(tooltip -> Text.removeTags(tooltip.getText()))
			.collect(Collectors.toList());
	}

	/** The lines of the one tooltip shown, which removing the tags would run together */
	private List<String> tooltipLines()
	{
		assertEquals(1, tooltipManager.getTooltips().size());
		return Arrays.stream(tooltipManager.getTooltips().get(0).getText().split("</br>"))
			.map(Text::removeTags)
			.collect(Collectors.toList());
	}
}
