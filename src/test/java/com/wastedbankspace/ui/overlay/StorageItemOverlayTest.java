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
import com.wastedbankspace.model.StorableItem;
import com.wastedbankspace.model.StorageLocations;
import com.wastedbankspace.model.locations.TreasureChest;
import com.wastedbankspace.model.stash.StashUnit;
import com.wastedbankspace.poh.PohStorageTracker;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Decision matrix for {@link StorageItemOverlay#renderItemOverlay}: which items get the overlay image (marker), the
 * "already in the POH" house icon and the storage location tooltip, depending on where the item is shown and the
 * config options.
 */
public class StorageItemOverlayTest
{
	/** A treasure chest item: storable, and something the POH costume room tracker can report as stored */
	private static final StorableItem ITEM = TreasureChest.JESTER_CAPE;
	private static final int ITEM_ID = ITEM.getItemID();
	/** Another storable item, but not one the plugin has flagged */
	private static final int UNFLAGGED_ITEM_ID = TreasureChest.BEANIE.getItemID();
	/** Bank placeholder standing in for ITEM (placeholders have their own item id) */
	private static final int PLACEHOLDER_ID = 1_000_001;
	/** Placeholders are built from this template item; real items have a template id of -1 */
	private static final int PLACEHOLDER_TEMPLATE_ID = 14401;

	/** Item slot on the canvas: x 100..135, y 200..231 */
	private static final int SLOT_X = 100;
	private static final int SLOT_Y = 200;
	private static final int SLOT_WIDTH = 36;
	private static final int SLOT_HEIGHT = 32;

	/** The marker's top-left corner sits 12px in from the slot's bottom-right corner */
	private static final int MARKER_X = 124;
	private static final int MARKER_Y = 220;

	/** The 26x20 house sprite is scaled down to fit 13px, keeping its aspect ratio: 13x10 */
	private static final int HOUSE_WIDTH = 13;
	private static final int HOUSE_HEIGHT = 10;
	private static final int HOUSE_LEFT_X = 100;
	private static final int HOUSE_RIGHT_X = 123;
	private static final int HOUSE_Y = 222;

	/** The STASH sprite is already 13x13, so it's drawn unscaled */
	private static final int STASH_SIZE = 13;

	private static final Point MOUSE_INSIDE = new Point(110, 210);
	/** Just past the slot's bottom-right corner */
	private static final Point MOUSE_OUTSIDE = new Point(136, 232);

	private static final OverlayImage OVERLAY_IMAGE = OverlayImage.DOT_RED;

	private Client client;
	private WastedBankSpacePlugin plugin;
	private WastedBankSpaceConfig config;
	private ItemManager itemManager;
	private TooltipManager tooltipManager;
	private PohStorageTracker pohStorage;
	private SpriteManager spriteManager;
	private Graphics2D graphics;

	private Set<Integer> enabledItems;
	private BufferedImage houseSprite;
	/** Fully transparent, so trimming leaves it as it is */
	private final BufferedImage stashSprite = new BufferedImage(STASH_SIZE, STASH_SIZE, BufferedImage.TYPE_INT_ARGB);
	private StorageItemOverlay overlay;

	@Before
	public void setUp()
	{
		// Guards the choice of test item: the tooltip location is looked up through StorageLocations
		assertSame(ITEM, StorageLocations.getStorableItem(ITEM_ID));

		client = mock(Client.class);
		plugin = mock(WastedBankSpacePlugin.class);
		config = mock(WastedBankSpaceConfig.class);
		itemManager = mock(ItemManager.class);
		tooltipManager = new TooltipManager();
		pohStorage = mock(PohStorageTracker.class);
		spriteManager = mock(SpriteManager.class);
		graphics = mock(Graphics2D.class);

		enabledItems = new HashSet<>(Set.of(ITEM_ID));
		when(plugin.getEnabledItems()).thenReturn(enabledItems);
		when(plugin.getOverlayImage()).thenReturn(OVERLAY_IMAGE);
		when(plugin.getStorageLocationText(anyInt(), anyBoolean())).thenReturn(ITEM.getLocation());

		// Same values as the config defaults; each test changes what it is about
		when(config.markInventoryItems()).thenReturn(false);
		when(config.markEquippedItems()).thenReturn(false);
		when(config.houseIconInBank()).thenReturn(true);
		when(config.houseIconInInventory()).thenReturn(true);
		when(config.houseIcon()).thenReturn(HouseIcon.TELEPORT_SPELL);
		when(config.houseIconReplacesMarker()).thenReturn(false);
		when(config.markPlaceholders()).thenReturn(true);

		when(client.getMouseCanvasPosition()).thenReturn(MOUSE_OUTSIDE);

		houseSprite = new BufferedImage(26, 20, BufferedImage.TYPE_INT_ARGB);
		when(spriteManager.getSprite(HouseIcon.TELEPORT_SPELL.getSpriteId(), 0)).thenReturn(houseSprite);

		// Real items point at their placeholder but have no template; placeholders point back at the real item
		ItemComposition noPlaceholder = composition(-1, -1);
		when(itemManager.getItemComposition(anyInt())).thenReturn(noPlaceholder);
		ItemComposition item = composition(PLACEHOLDER_ID, -1);
		when(itemManager.getItemComposition(ITEM_ID)).thenReturn(item);
		ItemComposition placeholder = composition(ITEM_ID, PLACEHOLDER_TEMPLATE_ID);
		when(itemManager.getItemComposition(PLACEHOLDER_ID)).thenReturn(placeholder);

		overlay = new StorageItemOverlay(client, plugin, config, itemManager, tooltipManager, pohStorage, spriteManager);
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

	/** Worn equipment shown inside the bank interface, i.e. a bank widget outside the item container */
	private static Widget bankWornItem()
	{
		return widget(InterfaceID.Bankmain.WORNSLOT0, InterfaceID.Bankmain.WORNITEMS_CONTAINER);
	}

	/** An item in group storage, which is hooked like the bank but has its own item container */
	private static Widget groupStorageItem()
	{
		return widget(InterfaceID.SharedBank.ITEMS, InterfaceID.SharedBank.ITEMS);
	}

	private static Widget inventoryItem()
	{
		return widget(InterfaceID.Inventory.ITEMS, InterfaceID.Inventory.ITEMS);
	}

	/** The inventory shown next to the bank */
	private static Widget bankSideInventoryItem()
	{
		return widget(InterfaceID.Bankside.ITEMS, InterfaceID.Bankside.ITEMS_CONTAINER);
	}

	private static Widget wornItem()
	{
		return widget(InterfaceID.Wornitems.SLOT1, InterfaceID.Wornitems.UNIVERSE);
	}

	private void render(int itemId, Widget widget)
	{
		render(graphics, itemId, widget);
	}

	private void render(Graphics2D target, int itemId, Widget widget)
	{
		Rectangle bounds = new Rectangle(SLOT_X, SLOT_Y, SLOT_WIDTH, SLOT_HEIGHT);
		overlay.renderItemOverlay(target, itemId, new WidgetItem(itemId, 1, bounds, widget, null));
	}

	private void hover()
	{
		when(client.getMouseCanvasPosition()).thenReturn(MOUSE_INSIDE);
	}

	private void storedInHouse(int itemId)
	{
		when(pohStorage.isStored(itemId)).thenReturn(true);
	}

	/** The overlay image goes through ImageComponent, which draws it unscaled at its preferred location */
	private void verifyMarkerAt(int x, int y)
	{
		verify(graphics).drawImage(same(OVERLAY_IMAGE.getIcon()), eq(x), eq(y), isNull());
	}

	private void verifyHouseIconAt(BufferedImage sprite, int x, int y, int width, int height)
	{
		verify(graphics).drawImage(same(sprite), eq(x), eq(y), eq(width), eq(height), isNull());
	}

	/** Every STASH unit that takes the item is filled, and the STASH icon is shown in the bank and inventory */
	private void stashed()
	{
		List<StashUnit> units = List.of(StashUnit.NEAR_A_SHED_IN_LUMBRIDGE_SWAMP);
		when(plugin.getStashUnits(ITEM_ID)).thenReturn(units);
		when(plugin.isStashed(units)).thenReturn(true);
		when(config.stashIconInBank()).thenReturn(true);
		when(config.stashIconInInventory()).thenReturn(true);
		when(config.stashIcon()).thenReturn(StashIcon.GREEN_TICK);
		when(spriteManager.getSprite(StashIcon.GREEN_TICK.getSpriteId(), 0)).thenReturn(stashSprite);
	}

	/** The test item can also go in the POH costume room, so its STASH icon sits top-right, full size */
	private void verifyStashIconTopRight()
	{
		verify(graphics).drawImage(same(stashSprite), eq(SLOT_X + SLOT_WIDTH - STASH_SIZE), eq(SLOT_Y),
			eq(STASH_SIZE), eq(STASH_SIZE), isNull());
	}

	private List<String> tooltips()
	{
		return tooltipManager.getTooltips().stream()
			.map(tooltip -> Text.removeTags(tooltip.getText()))
			.collect(Collectors.toList());
	}

	/** Every call made on the given graphics mock, e.g. "drawImage[image, 124, 220, null]" */
	private static List<String> drawCalls(Graphics2D target)
	{
		return mockingDetails(target).getInvocations().stream()
			.map(invocation -> invocation.getMethod().getName() + Arrays.toString(invocation.getArguments()))
			.collect(Collectors.toList());
	}

	private static String storeAt()
	{
		return "Store @ " + ITEM.getLocation();
	}

	private static String alreadyStoredAt()
	{
		return "Already stored @ " + ITEM.getLocation();
	}

	// Where the overlay is drawn

	@Test
	public void drawsOverTheBankInventoryAndWornEquipment()
	{
		List<Integer> hooks = overlay.getDrawHooks();

		assertTrue(hooks.contains(InterfaceID.Bankmain.ITEMS));
		assertTrue(hooks.contains(InterfaceID.INVENTORY << 16 | 0xffff));
		assertTrue(hooks.contains(InterfaceID.WORNITEMS << 16 | 0xffff));
	}

	// Bank

	@Test
	public void flaggedBankItemGetsMarkerInBottomRightCorner()
	{
		render(ITEM_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyNoMoreInteractions(graphics);
		assertEquals(Collections.emptyList(), tooltips());
	}

	@Test
	public void unflaggedBankItemIsNotMarked()
	{
		hover();

		render(UNFLAGGED_ITEM_ID, bankItem());

		verifyNoInteractions(graphics);
		assertEquals(Collections.emptyList(), tooltips());
	}

	@Test
	public void nothingIsMarkedWhenNoItemsAreFlagged()
	{
		enabledItems.clear();
		hover();

		render(ITEM_ID, bankItem());

		verifyNoInteractions(graphics);
		assertEquals(Collections.emptyList(), tooltips());
	}

	@Test
	public void bankWidgetOutsideTheItemContainerIsNotMarked()
	{
		when(config.markInventoryItems()).thenReturn(true);
		when(config.markEquippedItems()).thenReturn(true);
		storedInHouse(ITEM_ID);
		hover();

		render(ITEM_ID, bankWornItem());

		verifyNoInteractions(graphics);
		assertEquals(Collections.emptyList(), tooltips());
	}

	// Group storage

	/**
	 * Group storage items reach the overlay through the bank hook, but they are not inventory items: whether they get
	 * marked must not change with the inventory option. It leaves open whether they should be marked.
	 */
	@Test
	public void groupStorageItemsDoNotFollowTheInventoryOption()
	{
		hover();

		Graphics2D inventoryMarkingOff = mock(Graphics2D.class);
		render(inventoryMarkingOff, ITEM_ID, groupStorageItem());
		List<String> tooltipsWithInventoryMarkingOff = tooltips();

		tooltipManager.clear();
		when(config.markInventoryItems()).thenReturn(true);
		Graphics2D inventoryMarkingOn = mock(Graphics2D.class);
		render(inventoryMarkingOn, ITEM_ID, groupStorageItem());

		assertEquals(drawCalls(inventoryMarkingOff), drawCalls(inventoryMarkingOn));
		assertEquals(tooltipsWithInventoryMarkingOff, tooltips());
	}

	// Inventory

	@Test
	public void inventoryItemNotMarkedWhenMarkInventoryItemsOff()
	{
		when(config.markEquippedItems()).thenReturn(true);
		hover();

		render(ITEM_ID, inventoryItem());

		verifyNoInteractions(graphics);
		assertEquals(Collections.emptyList(), tooltips());
	}

	@Test
	public void inventoryItemMarkedWhenMarkInventoryItemsOn()
	{
		when(config.markInventoryItems()).thenReturn(true);
		hover();

		render(ITEM_ID, inventoryItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyNoMoreInteractions(graphics);
		assertEquals(List.of(storeAt()), tooltips());
	}

	@Test
	public void inventoryNextToTheBankCountsAsInventory()
	{
		render(ITEM_ID, bankSideInventoryItem());
		verifyNoInteractions(graphics);

		when(config.markInventoryItems()).thenReturn(true);
		render(ITEM_ID, bankSideInventoryItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyNoMoreInteractions(graphics);
	}

	// Worn equipment

	@Test
	public void wornItemNotMarkedWhenMarkEquippedItemsOff()
	{
		when(config.markInventoryItems()).thenReturn(true);
		hover();

		render(ITEM_ID, wornItem());

		verifyNoInteractions(graphics);
		assertEquals(Collections.emptyList(), tooltips());
	}

	@Test
	public void wornItemMarkedWhenMarkEquippedItemsOn()
	{
		when(config.markEquippedItems()).thenReturn(true);
		hover();

		render(ITEM_ID, wornItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyNoMoreInteractions(graphics);
		assertEquals(List.of(storeAt()), tooltips());
	}

	// Already stored in the POH

	@Test
	public void bankItemInHouseGetsHouseIconBottomLeftNextToMarker()
	{
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyHouseIconAt(houseSprite, HOUSE_LEFT_X, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
		verifyNoMoreInteractions(graphics);
	}

	@Test
	public void bankHouseIconHiddenWhenHouseIconInBankOff()
	{
		when(config.houseIconInBank()).thenReturn(false);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyNoMoreInteractions(graphics);
	}

	@Test
	public void bankHouseIconDoesNotDependOnInventoryHouseIconOption()
	{
		when(config.houseIconInInventory()).thenReturn(false);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyHouseIconAt(houseSprite, HOUSE_LEFT_X, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
		verifyNoMoreInteractions(graphics);
	}

	@Test
	public void inventoryItemInHouseGetsNothingWhenInventoryMarkingOff()
	{
		storedInHouse(ITEM_ID);
		hover();

		render(ITEM_ID, inventoryItem());

		verifyNoInteractions(graphics);
		assertEquals(Collections.emptyList(), tooltips());
	}

	@Test
	public void wornItemInHouseGetsNothingWhenEquipmentMarkingOff()
	{
		storedInHouse(ITEM_ID);
		hover();

		render(ITEM_ID, wornItem());

		verifyNoInteractions(graphics);
		assertEquals(Collections.emptyList(), tooltips());
	}

	@Test
	public void inventoryItemInHouseGetsHouseIconNextToMarkerWhenInventoryMarkingOn()
	{
		when(config.markInventoryItems()).thenReturn(true);
		storedInHouse(ITEM_ID);
		hover();

		render(ITEM_ID, inventoryItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyHouseIconAt(houseSprite, HOUSE_LEFT_X, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
		verifyNoMoreInteractions(graphics);
		assertEquals(List.of(alreadyStoredAt()), tooltips());
	}

	@Test
	public void wornItemInHouseGetsHouseIconNextToMarkerWhenEquipmentMarkingOn()
	{
		when(config.markEquippedItems()).thenReturn(true);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, wornItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyHouseIconAt(houseSprite, HOUSE_LEFT_X, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
		verifyNoMoreInteractions(graphics);
	}

	@Test
	public void inventoryAndWornHouseIconsHiddenWhenHouseIconInInventoryOff()
	{
		when(config.markInventoryItems()).thenReturn(true);
		when(config.markEquippedItems()).thenReturn(true);
		when(config.houseIconInInventory()).thenReturn(false);
		storedInHouse(ITEM_ID);
		hover();

		render(ITEM_ID, inventoryItem());
		render(ITEM_ID, wornItem());

		verify(graphics, times(2)).drawImage(same(OVERLAY_IMAGE.getIcon()), eq(MARKER_X), eq(MARKER_Y), isNull());
		verifyNoMoreInteractions(graphics);
		assertEquals(List.of(alreadyStoredAt(), alreadyStoredAt()), tooltips());
	}

	@Test
	public void inventoryHouseIconDoesNotDependOnBankHouseIconOption()
	{
		when(config.markInventoryItems()).thenReturn(true);
		when(config.houseIconInBank()).thenReturn(false);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, inventoryItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyHouseIconAt(houseSprite, HOUSE_LEFT_X, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
		verifyNoMoreInteractions(graphics);
	}

	@Test
	public void inventoryItemNotInHouseGetsNoHouseIcon()
	{
		when(config.markInventoryItems()).thenReturn(true);
		hover();

		render(ITEM_ID, inventoryItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyNoMoreInteractions(graphics);
		assertEquals(List.of(storeAt()), tooltips());
	}

	@Test
	public void houseIconReplacesMarkerInBottomRightCorner()
	{
		when(config.houseIconReplacesMarker()).thenReturn(true);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());

		verifyHouseIconAt(houseSprite, HOUSE_RIGHT_X, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
		verifyNoMoreInteractions(graphics);
	}

	@Test
	public void houseIconReplacesMarkerOnMarkedInventoryItems()
	{
		when(config.houseIconReplacesMarker()).thenReturn(true);
		when(config.markInventoryItems()).thenReturn(true);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, inventoryItem());

		verifyHouseIconAt(houseSprite, HOUSE_RIGHT_X, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
		verifyNoMoreInteractions(graphics);
	}

	@Test
	public void houseIconReplacingMarkerStillShowsNothingOnUnmarkedInventoryItems()
	{
		when(config.houseIconReplacesMarker()).thenReturn(true);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, inventoryItem());

		verifyNoInteractions(graphics);
	}

	@Test
	public void replaceMarkerOptionKeepsMarkerForItemsNotInHouse()
	{
		when(config.houseIconReplacesMarker()).thenReturn(true);

		render(ITEM_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyNoMoreInteractions(graphics);
	}

	@Test
	public void replaceMarkerOptionKeepsMarkerWhenHouseIconHidden()
	{
		when(config.houseIconReplacesMarker()).thenReturn(true);
		when(config.houseIconInBank()).thenReturn(false);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyNoMoreInteractions(graphics);
	}

	// Already stashed

	@Test
	public void stashedBankItemGetsStashIconTopRight()
	{
		stashed();

		render(ITEM_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyStashIconTopRight();
		verifyNoMoreInteractions(graphics);
	}

	@Test
	public void stashedInventoryAndWornItemsGetNothingWhenMarkingOff()
	{
		stashed();
		storedInHouse(ITEM_ID);
		hover();

		render(ITEM_ID, inventoryItem());
		render(ITEM_ID, wornItem());

		verifyNoInteractions(graphics);
		assertEquals(Collections.emptyList(), tooltips());
	}

	@Test
	public void stashedInventoryItemGetsStashIconWhenInventoryMarkingOn()
	{
		when(config.markInventoryItems()).thenReturn(true);
		stashed();

		render(ITEM_ID, inventoryItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyStashIconTopRight();
		verifyNoMoreInteractions(graphics);
	}

	@Test
	public void stashedWornItemGetsStashIconWhenEquipmentMarkingOn()
	{
		when(config.markEquippedItems()).thenReturn(true);
		stashed();

		render(ITEM_ID, wornItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyStashIconTopRight();
		verifyNoMoreInteractions(graphics);
	}

	// House icon sprite

	@Test
	public void switchingHouseIconOptionUsesTheNewSprite()
	{
		BufferedImage pohSprite = new BufferedImage(26, 20, BufferedImage.TYPE_INT_ARGB);
		when(spriteManager.getSprite(HouseIcon.POH.getSpriteId(), 0)).thenReturn(pohSprite);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());
		when(config.houseIcon()).thenReturn(HouseIcon.POH);
		render(ITEM_ID, bankItem());

		verifyHouseIconAt(houseSprite, HOUSE_LEFT_X, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
		verifyHouseIconAt(pohSprite, HOUSE_LEFT_X, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
	}

	@Test
	public void houseIconScaledToFitKeepingAspectRatio()
	{
		// 12x20 scaled by 13/20 is 7.8x13, drawn as 8x13 against the slot's bottom-left corner
		BufferedImage tallSprite = new BufferedImage(12, 20, BufferedImage.TYPE_INT_ARGB);
		when(spriteManager.getSprite(HouseIcon.TELEPORT_SPELL.getSpriteId(), 0)).thenReturn(tallSprite);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());

		verifyHouseIconAt(tallSprite, SLOT_X, 219, 8, 13);
	}

	@Test
	public void houseIconSpriteLoadedOnceAndReused()
	{
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());
		render(ITEM_ID, bankItem());

		verify(spriteManager, times(1)).getSprite(HouseIcon.TELEPORT_SPELL.getSpriteId(), 0);
		verify(graphics, times(2)).drawImage(same(houseSprite), eq(HOUSE_LEFT_X), eq(HOUSE_Y), eq(HOUSE_WIDTH),
			eq(HOUSE_HEIGHT), isNull());
	}

	@Test
	public void houseIconSkippedUntilSpriteIsAvailable()
	{
		when(spriteManager.getSprite(HouseIcon.TELEPORT_SPELL.getSpriteId(), 0)).thenReturn(null, houseSprite);
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyNoMoreInteractions(graphics);

		render(ITEM_ID, bankItem());

		verifyHouseIconAt(houseSprite, HOUSE_LEFT_X, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
	}

	// Tooltip

	@Test
	public void hoveringFlaggedItemShowsWhereToStoreIt()
	{
		hover();

		render(ITEM_ID, bankItem());

		assertEquals(List.of(storeAt()), tooltips());
	}

	@Test
	public void hoveringItemInHouseSaysItIsAlreadyStored()
	{
		storedInHouse(ITEM_ID);
		hover();

		render(ITEM_ID, bankItem());

		assertEquals(List.of(alreadyStoredAt()), tooltips());
	}

	@Test
	public void tooltipSaysAlreadyStoredEvenWhenHouseIconHidden()
	{
		when(config.houseIconInBank()).thenReturn(false);
		storedInHouse(ITEM_ID);
		hover();

		render(ITEM_ID, bankItem());

		assertEquals(List.of(alreadyStoredAt()), tooltips());
	}

	@Test
	public void noTooltipWhenMouseIsOutsideTheItem()
	{
		storedInHouse(ITEM_ID);

		render(ITEM_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		assertEquals(Collections.emptyList(), tooltips());
	}

	// Bank placeholders

	@Test
	public void bankPlaceholderOfFlaggedItemIsMarkedLikeTheItem()
	{
		hover();

		render(PLACEHOLDER_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyNoMoreInteractions(graphics);
		assertEquals(List.of(storeAt()), tooltips());
	}

	@Test
	public void bankPlaceholderOfItemInHouseGetsHouseIcon()
	{
		storedInHouse(ITEM_ID);
		hover();

		render(PLACEHOLDER_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyHouseIconAt(houseSprite, HOUSE_LEFT_X, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
		verifyNoMoreInteractions(graphics);
		assertEquals(List.of(alreadyStoredAt()), tooltips());
	}

	@Test
	public void bankPlaceholderNotMarkedWhenMarkPlaceholdersOff()
	{
		when(config.markPlaceholders()).thenReturn(false);
		storedInHouse(ITEM_ID);
		hover();

		render(PLACEHOLDER_ID, bankItem());

		verifyNoInteractions(graphics);
		assertEquals(Collections.emptyList(), tooltips());
	}

	@Test
	public void flaggedBankItemStillMarkedWhenMarkPlaceholdersOff()
	{
		when(config.markPlaceholders()).thenReturn(false);

		render(ITEM_ID, bankItem());

		verifyMarkerAt(MARKER_X, MARKER_Y);
		verifyNoMoreInteractions(graphics);
	}

	@Test
	public void placeholderLookupOnlyAppliesInTheBank()
	{
		when(config.markInventoryItems()).thenReturn(true);
		when(config.markEquippedItems()).thenReturn(true);
		storedInHouse(ITEM_ID);
		hover();

		render(PLACEHOLDER_ID, inventoryItem());
		render(PLACEHOLDER_ID, wornItem());

		verifyNoInteractions(graphics);
		assertEquals(Collections.emptyList(), tooltips());
	}
}
