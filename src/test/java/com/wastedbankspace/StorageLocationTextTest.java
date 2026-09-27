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

package com.wastedbankspace;

import com.wastedbankspace.banktag.WastedBankTag;
import com.wastedbankspace.model.StorageLocations;
import com.wastedbankspace.model.locations.ArmourCase;
import com.wastedbankspace.model.locations.CapeRack;
import com.wastedbankspace.model.locations.ForestryKit;
import com.wastedbankspace.model.locations.HuntsmansKit;
import com.wastedbankspace.model.locations.MagicWardrobe;
import com.wastedbankspace.model.locations.TackleBox;
import com.wastedbankspace.model.locations.TreasureChest;
import com.wastedbankspace.ui.WastedBankSpacePanel;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.events.ConfigChanged;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.SwingUtilities;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link WastedBankSpacePlugin#getStorageLocationText(int, boolean)}, the location part of the "Store @"
 * and "Already stored @" tooltips, for items that more than one storage location lists.
 * <p>
 * The plugin only remembers one location per item in {@link StorageLocations#getItemIdMap()}, the one registered
 * last, so the tooltip used to name that location even when it was disabled, or was not the POH costume room the
 * item is sitting in. The text is recalculated with the flagged items, which the tests trigger through
 * {@link WastedBankSpacePlugin#onConfigChanged} the way {@link IgnoreListTest} does.
 */
@RunWith(MockitoJUnitRunner.class)
public class StorageLocationTextTest
{
	/** Woodcutting cape: cape rack (registered first, costume room, best in slot) and forestry kit */
	private static final int WOODCUTTING_CAPE = ItemID.SKILLCAPE_WOODCUTTING;
	/** Small fishing net: huntsman's kit (registered first) and tackle box, whose config option comes first */
	private static final int SMALL_FISHING_NET = ItemID.NET;
	/** Larupia hat: armour case (registered first, costume room) and huntsman's kit */
	private static final int LARUPIA_HAT = ItemID.HUNTING_HAT_JAGUAR;
	/** Dark flippers: magic wardrobe (registered first, costume room) and tackle box */
	private static final int DARK_FLIPPERS = ItemID.DARK_FLIPPERS;
	/** Leaves: forestry kit only */
	private static final int LEAVES = ItemID.LEAVES;
	/** Jester cape: treasure chest only, a costume room storage */
	private static final int JESTER_CAPE = ItemID.JESTER_CAPE;

	@Mock
	private WastedBankSpaceConfig config;

	@Mock
	private WastedBankTag bankTag;

	@Mock
	private WastedBankSpacePanel panel;

	@InjectMocks
	private WastedBankSpacePlugin plugin;

	private boolean tackleBox;
	private boolean huntsmansKit;
	private boolean forestryKit;
	private boolean capeRack;
	private boolean armourCase;
	private boolean magicWardrobe;
	private boolean treasureChest;
	private boolean bisFilterEnabled;

	@Before
	public void setUp()
	{
		when(panel.getFilterdItemsText()).thenReturn("");
		when(config.bisFilterEnabledCheck()).thenAnswer(invocation -> bisFilterEnabled);
		when(config.tackleBoxStorageCheck()).thenAnswer(invocation -> tackleBox);
		when(config.huntsmansKitStorageCheck()).thenAnswer(invocation -> huntsmansKit);
		when(config.forestryKitStorageCheck()).thenAnswer(invocation -> forestryKit);
		when(config.capeRackStorageCheck()).thenAnswer(invocation -> capeRack);
		when(config.armourCaseStorageCheck()).thenAnswer(invocation -> armourCase);
		when(config.magicWardrobeStorageCheck()).thenAnswer(invocation -> magicWardrobe);
		when(config.treasureChestStorageCheck()).thenAnswer(invocation -> treasureChest);
	}

	@After
	public void waitForPanelUpdate() throws Exception
	{
		// Recalculating hands a panel refresh to the Swing thread; let it finish before the next test starts
		SwingUtilities.invokeAndWait(() ->
		{
		});
	}

	@Test
	public void itemsAreListedByTheExpectedLocations()
	{
		// Guards the test data, including which location holds the single entry of the registry (the one registered last)
		assertEquals(List.of(CapeRack.WOODCUTTING_CAPE_2, ForestryKit.WOODCUTTING_CAPE),
			StorageLocations.getStorableItems(WOODCUTTING_CAPE));
		assertEquals(List.of(HuntsmansKit.SMALL_FISHING_NET_, TackleBox.SMALL_FISHING_NET),
			StorageLocations.getStorableItems(SMALL_FISHING_NET));
		assertEquals(List.of(ArmourCase.LARUPIA_HAT, HuntsmansKit.LARUPIA_HAT_),
			StorageLocations.getStorableItems(LARUPIA_HAT));
		assertEquals(List.of(MagicWardrobe.DARK_FLIPPERS_MAGIC_WARDROBE, TackleBox.DARK_FLIPPERS),
			StorageLocations.getStorableItems(DARK_FLIPPERS));
		assertEquals(List.of(ForestryKit.LEAVES), StorageLocations.getStorableItems(LEAVES));
		assertEquals(List.of(TreasureChest.JESTER_CAPE), StorageLocations.getStorableItems(JESTER_CAPE));

		// Uses the config stubs of setUp, which the strict Mockito runner otherwise reports as unnecessary
		recalculate();
	}

	// Store @

	@Test
	public void bothEnabledLocationsAreNamedInTheOrderOfTheConfigOptions()
	{
		tackleBox = true;
		huntsmansKit = true;
		recalculate();

		// The tackle box option comes before the huntsman's kit option, although the huntsman's kit registered the net first
		assertEquals("Tackle Box / Huntsman's Kit", text(SMALL_FISHING_NET));

		forestryKit = true;
		capeRack = true;
		recalculate();

		assertEquals("Forestry Kit / Cape Rack", text(WOODCUTTING_CAPE));
	}

	@Test
	public void onlyTheEnabledLocationIsNamed()
	{
		// The registry holds the forestry kit's entry for the cape, which must not be named while it is disabled
		capeRack = true;
		recalculate();

		assertTrue("flagged", plugin.getEnabledItems().contains(WOODCUTTING_CAPE));
		assertEquals("Cape Rack", text(WOODCUTTING_CAPE));

		capeRack = false;
		forestryKit = true;
		recalculate();

		assertTrue("flagged", plugin.getEnabledItems().contains(WOODCUTTING_CAPE));
		assertEquals("Forestry Kit", text(WOODCUTTING_CAPE));
	}

	@Test
	public void textFollowsTheLatestRecalculation()
	{
		tackleBox = true;
		huntsmansKit = true;
		recalculate();
		assertEquals("Tackle Box / Huntsman's Kit", text(SMALL_FISHING_NET));

		tackleBox = false;
		recalculate();
		assertEquals("Huntsman's Kit", text(SMALL_FISHING_NET));

		huntsmansKit = false;
		recalculate();
		assertFalse("flagged", plugin.getEnabledItems().contains(SMALL_FISHING_NET));
		assertEquals("Huntsman's Kit / Tackle Box", text(SMALL_FISHING_NET));
	}

	@Test
	public void singleLocationItemNamesItsLocation()
	{
		forestryKit = true;
		treasureChest = true;
		recalculate();

		assertEquals("Forestry Kit", text(LEAVES, false));
		assertEquals("Forestry Kit", text(LEAVES, true));
		assertEquals("Treasure Chest", text(JESTER_CAPE, false));
		assertEquals("Treasure Chest", text(JESTER_CAPE, true));
	}

	@Test
	public void itemOfDisabledLocationsNamesAllOfThem()
	{
		// Nothing has been recalculated yet, e.g. before the plugin has finished starting up
		assertEquals("Cape Rack / Forestry Kit", text(WOODCUTTING_CAPE));
		assertEquals("Forestry Kit", text(LEAVES));

		recalculate();

		assertFalse("flagged", plugin.getEnabledItems().contains(WOODCUTTING_CAPE));
		assertEquals("Cape Rack / Forestry Kit", text(WOODCUTTING_CAPE));
		assertEquals("Forestry Kit", text(LEAVES));
		assertEquals("Treasure Chest", text(JESTER_CAPE, true));
	}

	@Test
	public void itemInNoLocationHasNoText()
	{
		recalculate();

		assertEquals("", text(ItemID.COINS, false));
		assertEquals("", text(ItemID.COINS, true));
	}

	// Already stored @

	@Test
	public void itemInTheCostumeRoomNamesTheCostumeRoomStorageEvenWhenDisabled()
	{
		// The huntsman's kit registered the hat last, so it holds the registry entry, and it is the only one enabled
		huntsmansKit = true;
		armourCase = false;
		recalculate();

		assertEquals("Huntsman's Kit", text(LARUPIA_HAT, false));
		assertEquals("Armour Case", text(LARUPIA_HAT, true));
	}

	@Test
	public void itemInTheCostumeRoomNamesOnlyTheCostumeRoomStorage()
	{
		tackleBox = true;
		magicWardrobe = true;
		recalculate();

		assertEquals("Tackle Box / Magic Wardrobe", text(DARK_FLIPPERS, false));
		assertEquals("Magic Wardrobe", text(DARK_FLIPPERS, true));
	}

	@Test
	public void itemInTheHouseWithoutACostumeRoomStorageNamesTheEnabledLocations()
	{
		tackleBox = true;
		recalculate();

		assertEquals("Tackle Box", text(SMALL_FISHING_NET, true));
	}

	// Best in slot

	@Test
	public void itemMarkedBestInSlotByAnyLocationIsNotFlaggedWhileTheBisFilterIsOn()
	{
		// The forestry kit does not mark the cape as best in slot, but the cape rack does
		bisFilterEnabled = true;
		forestryKit = true;
		recalculate();

		assertFalse(plugin.getEnabledItems().contains(WOODCUTTING_CAPE));
		assertTrue(plugin.getEnabledItems().contains(LEAVES));
		assertEquals("Cape Rack / Forestry Kit", text(WOODCUTTING_CAPE));

		bisFilterEnabled = false;
		recalculate();

		assertTrue(plugin.getEnabledItems().contains(WOODCUTTING_CAPE));
		assertEquals("Forestry Kit", text(WOODCUTTING_CAPE));
	}

	private String text(int itemId)
	{
		return text(itemId, false);
	}

	private String text(int itemId, boolean inHouse)
	{
		return plugin.getStorageLocationText(itemId, inHouse);
	}

	/**
	 * Recalculates the flagged items and their location text from the enabled storage locations.
	 */
	private void recalculate()
	{
		ConfigChanged event = new ConfigChanged();
		event.setGroup(WastedBankSpaceConfig.GROUP);
		event.setKey(WastedBankSpaceConfig.FILTER_ENABLED_CHECK_KEY);
		plugin.onConfigChanged(event);
	}
}
