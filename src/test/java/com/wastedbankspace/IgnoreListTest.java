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
import com.wastedbankspace.model.ItemNameFixture;
import com.wastedbankspace.model.StorableItem;
import com.wastedbankspace.model.locations.CapeRack;
import com.wastedbankspace.model.locations.NightmareZone;
import com.wastedbankspace.model.locations.TackleBox;
import com.wastedbankspace.model.locations.ToolLeprechaun;
import com.wastedbankspace.ui.WastedBankSpacePanel;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.events.ConfigChanged;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Tests for the "Non Flagged Items" list in the plugin panel: the comma separated item names, item ids and
 * wildcards that stop storable items from being flagged.
 * <p>
 * The plugin applies the list in processIgnoreListChanged, which the panel calls after an edit and
 * {@link WastedBankSpacePlugin#onConfigChanged} calls with the panel's text whenever a filter setting changes. The
 * tests go in through onConfigChanged and check which items are left to flag in
 * {@link WastedBankSpacePlugin#getEnabledItems()} and in the bank tag tab.
 */
@RunWith(MockitoJUnitRunner.class)
public class IgnoreListTest
{
	/**
	 * Every item of the storage locations switched on in {@link #setUp()}.
	 */
	private static final Set<Integer> ENABLED_LOCATION_ITEMS = itemIds(
		TackleBox.values(), ToolLeprechaun.values(), NightmareZone.values(), CapeRack.values());

	private static final Set<Integer> WATERING_CANS = ids(
		ItemID.WATERING_CAN_0, ItemID.WATERING_CAN_1, ItemID.WATERING_CAN_2, ItemID.WATERING_CAN_3,
		ItemID.WATERING_CAN_4, ItemID.WATERING_CAN_5, ItemID.WATERING_CAN_6, ItemID.WATERING_CAN_7,
		ItemID.WATERING_CAN_8);

	private static final Set<Integer> HARPOONS_WITHOUT_ORNAMENT = ids(
		ItemID.HARPOON, ItemID.HUNTING_BARBED_HARPOON, ItemID.DRAGON_HARPOON, ItemID.INFERNAL_HARPOON,
		ItemID.CRYSTAL_HARPOON);

	private static final Set<Integer> ORNAMENTED_HARPOONS = ids(
		ItemID.TRAILBLAZER_HARPOON_NO_INFERNAL, ItemID.TRAILBLAZER_HARPOON);

	private static ItemNameFixture itemNames;

	@Mock
	private WastedBankSpaceConfig config;

	@Mock
	private WastedBankTag bankTag;

	@Mock
	private WastedBankSpacePanel panel;

	@InjectMocks
	private WastedBankSpacePlugin plugin;

	private String ignoreListText = "";
	private boolean filterEnabled = true;
	private boolean bisFilterEnabled = false;

	@BeforeClass
	public static void loadItemNames()
	{
		itemNames = ItemNameFixture.load();
	}

	@AfterClass
	public static void restoreItemNames()
	{
		// Null when loading failed, in which case the fixture has already put the names back
		if (itemNames != null)
		{
			itemNames.restore();
		}
	}

	@Before
	public void setUp()
	{
		when(panel.getFilterdItemsText()).thenAnswer(invocation -> ignoreListText);
		when(config.filterEnabledCheck()).thenAnswer(invocation -> filterEnabled);
		when(config.bisFilterEnabledCheck()).thenAnswer(invocation -> bisFilterEnabled);
		when(config.tackleBoxStorageCheck()).thenReturn(true);
		when(config.toolLeprechaunStorageCheck()).thenReturn(true);
		when(config.nightmareZoneStorageCheck()).thenReturn(true);
		when(config.capeRackStorageCheck()).thenReturn(true);
	}

	@After
	public void waitForPanelUpdate() throws Exception
	{
		// Applying the list hands a panel refresh to the Swing thread; let it finish before the next test starts
		SwingUtilities.invokeAndWait(() ->
		{
		});
	}

	@Test
	public void emptyListFlagsEveryItemOfTheEnabledStorageLocations()
	{
		applyIgnoreList("");

		assertEquals(ENABLED_LOCATION_ITEMS, plugin.getEnabledItems());
	}

	@Test
	public void itemNameStopsOnlyThatItemBeingFlagged()
	{
		assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying("Harpoon"));
	}

	@Test
	public void itemNameMatchesRegardlessOfCaseAndWhitespace()
	{
		assertEquals(ids(ItemID.DRAGON_HARPOON), ignoredAfterApplying("dRaGoN   HaRpOoN"));
		assertEquals(ids(ItemID.DRAGON_HARPOON), ignoredAfterApplying("DRAGONHARPOON"));
		// The list is edited in a text area, so a name can also be broken over two lines
		assertEquals(ids(ItemID.DRAGON_HARPOON), ignoredAfterApplying("Dragon\nharpoon"));
	}

	@Test
	public void itemNameWithBracketsIsMatchedAsWritten()
	{
		assertEquals(ids(ItemID.WATERING_CAN_8), ignoredAfterApplying("Watering can(8)"));
		assertEquals(ids(ItemID.TRAILBLAZER_HARPOON_NO_INFERNAL), ignoredAfterApplying("Dragon harpoon (or)"));
	}

	@Test
	public void itemIdStopsThatItemBeingFlagged()
	{
		assertEquals(ids(ItemID.LOBSTER_POT), ignoredAfterApplying(String.valueOf(ItemID.LOBSTER_POT)));
	}

	@Test
	public void everyCommaSeparatedEntryIsApplied()
	{
		String ignoreList = " Harpoon ,, " + ItemID.FISHING_ROD + " ,  Watering can(8), ";

		assertEquals(ids(ItemID.HARPOON, ItemID.FISHING_ROD, ItemID.WATERING_CAN_8), ignoredAfterApplying(ignoreList));
	}

	@Test
	public void entriesThatMatchNoItemAreSkipped()
	{
		assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying("Not an item, Harp, Harpoon"));
	}

	@Test
	public void trailingWildcardMatchesAnyEnding()
	{
		// Also matches the empty can, which is just called "Watering can", but not "Gricoller's can"
		assertEquals(WATERING_CANS, ignoredAfterApplying("Watering can*"));
	}

	@Test
	public void wildcardHasToMatchTheWholeName()
	{
		// The "(or)" harpoons contain "harpoon" but do not end with it
		assertEquals(HARPOONS_WITHOUT_ORNAMENT, ignoredAfterApplying("*harpoon"));
	}

	@Test
	public void wildcardsCanBeUsedAnywhereInTheName()
	{
		Set<Integer> allHarpoons = new HashSet<>(HARPOONS_WITHOUT_ORNAMENT);
		allHarpoons.addAll(ORNAMENTED_HARPOONS);

		assertEquals(allHarpoons, ignoredAfterApplying("*harpoon*"));
		assertEquals(ids(ItemID.TRAILBLAZER_HARPOON_NO_INFERNAL), ignoredAfterApplying("Dragon*(or)"));
	}

	@Test
	public void bracketsInWildcardEntriesAreMatchedLiterally()
	{
		assertEquals(ORNAMENTED_HARPOONS, ignoredAfterApplying("*harpoon (or)"));
		assertEquals(ids(ItemID.SKILLCAPE_ATTACK_TRIMMED, ItemID.SKILLCAPE_AGILITY_TRIMMED),
			ignoredAfterApplying("*cape(t)"));
		assertEquals(ids(ItemID.WATERING_CAN_4, ItemID._4DOSEFISHERSPOTION, ItemID.NZONE4DOSEOVERLOADPOTION,
			ItemID.NZONE4DOSEABSORPTIONPOTION), ignoredAfterApplying("*(4)"));
	}

	@Test
	public void wildcardMatchingIgnoresCase()
	{
		assertEquals(ORNAMENTED_HARPOONS, ignoredAfterApplying("*HARPOON (OR)"));
	}

	@Test
	public void strayBracketInWildcardEntryDoesNotStopTheRestOfTheList()
	{
		assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying("*[, Harpoon"));
	}

	@Test
	public void nothingIsIgnoredWhileTheFilterIsDisabled()
	{
		filterEnabled = false;

		assertEquals(ids(), ignoredAfterApplying("Harpoon, " + ItemID.FISHING_ROD + ", Watering can*"));
	}

	@Test
	public void turningTheFilterOffFlagsTheListedItemsAgain()
	{
		assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying("Harpoon"));

		filterEnabled = false;
		assertEquals(ids(), ignoredAfterApplying("Harpoon"));

		filterEnabled = true;
		assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying("Harpoon"));
	}

	@Test
	public void editingTheListReplacesThePreviousOne()
	{
		assertEquals(ids(), ignoredAfterApplying(""));
		assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying("Harpoon"));
		assertEquals(ids(ItemID.DRAGON_HARPOON), ignoredAfterApplying("Dragon harpoon"));
		assertEquals(ids(), ignoredAfterApplying(""));
	}

	@Test
	public void bisFilterStopsBestInSlotItemsBeingFlagged()
	{
		bisFilterEnabled = true;
		ignoreListText = "Attack hood";
		plugin.onConfigChanged(configChanged(WastedBankSpaceConfig.BIS_FILTER_ENABLED_CHECK_KEY));

		Set<Integer> enabled = plugin.getEnabledItems();
		assertFalse(enabled.contains(ItemID.SKILLCAPE_ATTACK));
		assertFalse(enabled.contains(ItemID.SKILLCAPE_AGILITY_TRIMMED));
		assertFalse("ignored", enabled.contains(ItemID.SKILLCAPE_ATTACK_HOOD));
		assertTrue(enabled.contains(ItemID.SKILLCAPE_AGILITY_HOOD));
		assertTrue(enabled.contains(ItemID.HARPOON));
		for (CapeRack item : CapeRack.values())
		{
			boolean expected = !item.isBis() && item.getItemID() != ItemID.SKILLCAPE_ATTACK_HOOD;
			assertEquals(item.name(), expected, enabled.contains(item.getItemID()));
		}
	}

	@Test
	public void bestInSlotItemsAreFlaggedWhileTheBisFilterIsOff()
	{
		bisFilterEnabled = false;
		ignoreListText = "Attack hood";
		plugin.onConfigChanged(configChanged(WastedBankSpaceConfig.BIS_FILTER_ENABLED_CHECK_KEY));

		Set<Integer> enabled = plugin.getEnabledItems();
		assertTrue(enabled.contains(ItemID.SKILLCAPE_ATTACK));
		assertTrue(enabled.contains(ItemID.SKILLCAPE_AGILITY_TRIMMED));
		assertFalse("ignored", enabled.contains(ItemID.SKILLCAPE_ATTACK_HOOD));
		assertTrue(enabled.contains(ItemID.SKILLCAPE_AGILITY_HOOD));
	}

	@Test
	public void bankTagTabIsUpdatedWithTheItemsStillFlagged()
	{
		// The bank tag copies the set it is given, so keep a copy of each call rather than the live set
		List<Set<Integer>> bankTagItems = new ArrayList<>();
		doAnswer(invocation ->
		{
			Set<Integer> items = invocation.getArgument(0);
			bankTagItems.add(new HashSet<>(items));
			return null;
		}).when(bankTag).setItems(any());

		applyIgnoreList("Harpoon");

		Set<Integer> stillFlagged = new HashSet<>(ENABLED_LOCATION_ITEMS);
		stillFlagged.remove(ItemID.HARPOON);
		assertFalse("the bank tag was not updated", bankTagItems.isEmpty());
		assertEquals(stillFlagged, bankTagItems.get(bankTagItems.size() - 1));
	}

	/**
	 * Applies the list and returns the items of the enabled storage locations that are no longer flagged.
	 */
	private Set<Integer> ignoredAfterApplying(String ignoreList)
	{
		applyIgnoreList(ignoreList);

		Set<Integer> ignored = new HashSet<>(ENABLED_LOCATION_ITEMS);
		ignored.removeAll(plugin.getEnabledItems());
		return ignored;
	}

	private void applyIgnoreList(String ignoreList)
	{
		ignoreListText = ignoreList;
		plugin.onConfigChanged(configChanged(WastedBankSpaceConfig.FILTER_ENABLED_CHECK_KEY));
	}

	private static ConfigChanged configChanged(String key)
	{
		ConfigChanged event = new ConfigChanged();
		event.setGroup(WastedBankSpaceConfig.GROUP);
		event.setKey(key);
		return event;
	}

	private static Set<Integer> ids(Integer... itemIds)
	{
		return new HashSet<>(Arrays.asList(itemIds));
	}

	private static Set<Integer> itemIds(StorableItem[]... locations)
	{
		Set<Integer> itemIds = new HashSet<>();
		for (StorableItem[] location : locations)
		{
			for (StorableItem item : location)
			{
				itemIds.add(item.getItemID());
			}
		}
		return itemIds;
	}
}
