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

package com.wastedbankspace.poh;

import com.wastedbankspace.WastedBankSpaceConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PohStorageTrackerTest
{
	/**
	 * The RS-profile key the costume room contents are saved under. Asserted literally on purpose: renaming it would
	 * silently lose the costume room contents every user has already saved.
	 */
	private static final String CONFIG_KEY = "pohCostumeRoomItems";
	/** The game also sends some item containers with this bit set on the id */
	private static final int CONTAINER_ID_FLAG = 0x8000;
	private static final int EMPTY_SLOT = -1;

	/** In-memory stand-in for the RS-profile configuration, keyed by group and key */
	private final Map<String, Object> rsProfileConfig = new HashMap<>();

	private ConfigManager configManager;
	private PohStorageTracker tracker;

	@Before
	public void setUp()
	{
		configManager = mock(ConfigManager.class);
		when(configManager.getRSProfileConfiguration(anyString(), anyString()))
			.thenAnswer(invocation -> (String) rsProfileConfig.get(
				storeKey(invocation.getArgument(0), invocation.getArgument(1))));
		doAnswer(invocation ->
		{
			rsProfileConfig.put(storeKey(invocation.getArgument(0), invocation.getArgument(1)),
				invocation.getArgument(2));
			return null;
		}).when(configManager).setRSProfileConfiguration(anyString(), anyString(), any());
		doAnswer(invocation ->
		{
			rsProfileConfig.remove(storeKey(invocation.getArgument(0), invocation.getArgument(1)));
			return null;
		}).when(configManager).unsetRSProfileConfiguration(anyString(), anyString());

		tracker = new PohStorageTracker(configManager);
	}

	@Test
	public void nothingIsStoredBeforeAnyData()
	{
		assertFalse(tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertFalse(tracker.isStored(ItemID.TZHAAR_CAPE_FIRE));
		assertFalse(tracker.isStored(0));
		assertFalse(tracker.isStored(EMPTY_SLOT));
	}

	@Test
	public void recordsItemsFromCostumeRoomContainer()
	{
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES,
			ItemID.MACRO_MIME_MASK, ItemID.TZHAAR_CAPE_FIRE, ItemID.SKILLCAPE_AGILITY));

		assertTrue(tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertTrue(tracker.isStored(ItemID.TZHAAR_CAPE_FIRE));
		assertTrue(tracker.isStored(ItemID.SKILLCAPE_AGILITY));
		assertFalse(tracker.isStored(ItemID.BRONZE_PLATEBODY_TRIM));
	}

	@Test
	public void recordsItemsWhenContainerIdHasFlagBitSet()
	{
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES | CONTAINER_ID_FLAG,
			ItemID.MACRO_MIME_MASK, ItemID.SANTA_HAT));

		assertTrue(tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertTrue(tracker.isStored(ItemID.SANTA_HAT));
		assertEquals(setOf(ItemID.MACRO_MIME_MASK, ItemID.SANTA_HAT), savedItemIds());
	}

	@Test
	public void ignoresOtherContainers()
	{
		int[] otherContainers = {
			InventoryID.BANK,
			InventoryID.INV,
			InventoryID.WORN,
			InventoryID.BANK | CONTAINER_ID_FLAG,
			InventoryID.POH_COSTUMES + 1,
			InventoryID.POH_COSTUMES - 1,
			// only the 0x8000 flag marks the costume room; any other extra bit makes it a different container
			InventoryID.POH_COSTUMES | 0x4000,
		};
		for (int containerId : otherContainers)
		{
			tracker.onItemContainerChanged(containerChanged(containerId, ItemID.ABYSSAL_WHIP, ItemID.COINS));
		}

		assertFalse(tracker.isStored(ItemID.ABYSSAL_WHIP));
		assertFalse(tracker.isStored(ItemID.COINS));
		verify(configManager, never()).setRSProfileConfiguration(anyString(), anyString(), any());
		assertTrue(rsProfileConfig.isEmpty());
	}

	@Test
	public void otherContainerDoesNotOverwriteRecordedCostumeRoom()
	{
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES,
			ItemID.MACRO_MIME_MASK, ItemID.TZHAAR_CAPE_FIRE));
		tracker.onItemContainerChanged(containerChanged(InventoryID.BANK, ItemID.ABYSSAL_WHIP));
		tracker.onItemContainerChanged(containerChanged(InventoryID.INV, EMPTY_SLOT, EMPTY_SLOT));

		assertTrue(tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertTrue(tracker.isStored(ItemID.TZHAAR_CAPE_FIRE));
		assertFalse(tracker.isStored(ItemID.ABYSSAL_WHIP));
		assertEquals(setOf(ItemID.MACRO_MIME_MASK, ItemID.TZHAAR_CAPE_FIRE), savedItemIds());
	}

	@Test
	public void ignoresEmptySlots()
	{
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES,
			EMPTY_SLOT, ItemID.MACRO_MIME_MASK, EMPTY_SLOT, EMPTY_SLOT, ItemID.SANTA_HAT, EMPTY_SLOT));

		assertFalse(tracker.isStored(EMPTY_SLOT));
		assertTrue(tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertTrue(tracker.isStored(ItemID.SANTA_HAT));
		assertEquals(setOf(ItemID.MACRO_MIME_MASK, ItemID.SANTA_HAT), savedItemIds());
	}

	@Test
	public void eachUpdateReplacesThePreviousContents()
	{
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES,
			ItemID.MACRO_MIME_MASK, ItemID.TZHAAR_CAPE_FIRE));
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES,
			ItemID.TZHAAR_CAPE_FIRE, ItemID.SKILLCAPE_AGILITY));

		assertFalse("an item taken out of the costume room must no longer count as stored",
			tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertTrue(tracker.isStored(ItemID.TZHAAR_CAPE_FIRE));
		assertTrue(tracker.isStored(ItemID.SKILLCAPE_AGILITY));
		assertEquals(setOf(ItemID.TZHAAR_CAPE_FIRE, ItemID.SKILLCAPE_AGILITY), savedItemIds());
	}

	@Test
	public void emptiedCostumeRoomClearsStoredAndSavedItems()
	{
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES,
			ItemID.MACRO_MIME_MASK, ItemID.TZHAAR_CAPE_FIRE));
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES, EMPTY_SLOT, EMPTY_SLOT));

		assertFalse(tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertFalse(tracker.isStored(ItemID.TZHAAR_CAPE_FIRE));
		assertEquals(setOf(), savedItemIds());

		PohStorageTracker afterRestart = new PohStorageTracker(configManager);
		afterRestart.load();
		assertFalse(afterRestart.isStored(ItemID.MACRO_MIME_MASK));
		assertFalse(afterRestart.isStored(ItemID.TZHAAR_CAPE_FIRE));
	}

	@Test
	public void savesContentsAsCsvUnderPluginGroupAndCostumeRoomKey()
	{
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES,
			ItemID.MACRO_MIME_MASK, ItemID.TZHAAR_CAPE_FIRE, ItemID.MACRO_MIME_MASK, EMPTY_SLOT));

		ArgumentCaptor<Object> saved = ArgumentCaptor.forClass(Object.class);
		verify(configManager, times(1))
			.setRSProfileConfiguration(eq(WastedBankSpaceConfig.GROUP), eq(CONFIG_KEY), saved.capture());
		assertTrue("saved value should be a CSV string", saved.getValue() instanceof String);

		List<String> entries = csvEntries((String) saved.getValue());
		assertEquals("each stored item should be saved exactly once", 2, entries.size());
		assertEquals(setOf(ItemID.MACRO_MIME_MASK, ItemID.TZHAAR_CAPE_FIRE), toIntSet(entries));
	}

	@Test
	public void savedContentsRoundTripThroughLoad()
	{
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES,
			ItemID.MACRO_MIME_MASK, EMPTY_SLOT, ItemID.TZHAAR_CAPE_FIRE, ItemID.SKILLCAPE_AGILITY));

		PohStorageTracker afterRestart = new PohStorageTracker(configManager);
		assertFalse(afterRestart.isStored(ItemID.MACRO_MIME_MASK));
		afterRestart.load();

		assertTrue(afterRestart.isStored(ItemID.MACRO_MIME_MASK));
		assertTrue(afterRestart.isStored(ItemID.TZHAAR_CAPE_FIRE));
		assertTrue(afterRestart.isStored(ItemID.SKILLCAPE_AGILITY));
		assertFalse(afterRestart.isStored(ItemID.SANTA_HAT));
		assertFalse(afterRestart.isStored(EMPTY_SLOT));
	}

	@Test
	public void loadReadsCsvSavedUnderPluginGroupAndCostumeRoomKey()
	{
		rsProfileConfig.put(storeKey(WastedBankSpaceConfig.GROUP, CONFIG_KEY),
			ItemID.MACRO_MIME_MASK + "," + ItemID.TZHAAR_CAPE_FIRE);

		tracker.load();

		assertTrue(tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertTrue(tracker.isStored(ItemID.TZHAAR_CAPE_FIRE));
		assertFalse(tracker.isStored(ItemID.SKILLCAPE_AGILITY));
	}

	@Test
	public void loadWithNothingSavedStoresNothing()
	{
		tracker.load();

		assertFalse(tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertFalse(tracker.isStored(0));
		verify(configManager, never()).setRSProfileConfiguration(anyString(), anyString(), any());
	}

	@Test
	public void loadForProfileWithoutSavedDataForgetsPreviousProfilesItems()
	{
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES,
			ItemID.MACRO_MIME_MASK, ItemID.TZHAAR_CAPE_FIRE));

		// switch to an account that has never opened its costume room
		rsProfileConfig.clear();
		tracker.load();

		assertFalse(tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertFalse(tracker.isStored(ItemID.TZHAAR_CAPE_FIRE));
	}

	@Test
	public void loadReplacesItemsHeldInMemory()
	{
		tracker.onItemContainerChanged(containerChanged(InventoryID.POH_COSTUMES,
			ItemID.MACRO_MIME_MASK, ItemID.TZHAAR_CAPE_FIRE));

		// switch to an account with a different costume room
		rsProfileConfig.put(storeKey(WastedBankSpaceConfig.GROUP, CONFIG_KEY),
			String.valueOf(ItemID.SKILLCAPE_AGILITY));
		tracker.load();

		assertFalse(tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertFalse(tracker.isStored(ItemID.TZHAAR_CAPE_FIRE));
		assertTrue(tracker.isStored(ItemID.SKILLCAPE_AGILITY));
	}

	@Test
	public void loadSkipsInvalidEntriesAndKeepsValidOnes()
	{
		rsProfileConfig.put(storeKey(WastedBankSpaceConfig.GROUP, CONFIG_KEY),
			"not-a-number," + ItemID.MACRO_MIME_MASK + ",,1.5, " + ItemID.TZHAAR_CAPE_FIRE
				+ " ,99999999999,abc," + ItemID.SKILLCAPE_AGILITY);

		tracker.load();

		assertTrue(tracker.isStored(ItemID.MACRO_MIME_MASK));
		assertTrue(tracker.isStored(ItemID.TZHAAR_CAPE_FIRE));
		assertTrue(tracker.isStored(ItemID.SKILLCAPE_AGILITY));
		assertFalse(tracker.isStored(1));
		assertFalse(tracker.isStored(0));
		assertFalse("an out-of-range id must not wrap around to some other item",
			tracker.isStored((int) 99999999999L));
	}

	@Test
	public void loadWithOnlyGarbageStoresNothing()
	{
		rsProfileConfig.put(storeKey(WastedBankSpaceConfig.GROUP, CONFIG_KEY), "abc,,  ,-,x1");

		tracker.load();

		assertFalse(tracker.isStored(0));
		assertFalse(tracker.isStored(EMPTY_SLOT));
		assertFalse(tracker.isStored(ItemID.MACRO_MIME_MASK));
	}

	private static String storeKey(String group, String key)
	{
		return group + "\n" + key;
	}

	/**
	 * The item ids currently saved for the costume room. Nothing saved (never saved, or unset) counts as empty.
	 */
	private Set<Integer> savedItemIds()
	{
		Object saved = rsProfileConfig.get(storeKey(WastedBankSpaceConfig.GROUP, CONFIG_KEY));
		if (saved == null)
		{
			return setOf();
		}
		assertTrue("costume room contents should be saved as a CSV string", saved instanceof String);
		List<String> entries = csvEntries((String) saved);
		Set<Integer> ids = toIntSet(entries);
		assertEquals("each stored item should be saved exactly once", ids.size(), entries.size());
		return ids;
	}

	private static List<String> csvEntries(String csv)
	{
		List<String> entries = new ArrayList<>();
		for (String entry : csv.split(","))
		{
			if (!entry.trim().isEmpty())
			{
				entries.add(entry.trim());
			}
		}
		return entries;
	}

	private static Set<Integer> toIntSet(List<String> entries)
	{
		Set<Integer> ids = new HashSet<>();
		for (String entry : entries)
		{
			ids.add(Integer.parseInt(entry));
		}
		return ids;
	}

	private static Set<Integer> setOf(int... ids)
	{
		Set<Integer> set = new HashSet<>();
		for (int id : ids)
		{
			set.add(id);
		}
		return set;
	}

	private static ItemContainerChanged containerChanged(int containerId, int... itemIds)
	{
		Item[] items = new Item[itemIds.length];
		for (int i = 0; i < itemIds.length; i++)
		{
			items[i] = new Item(itemIds[i], itemIds[i] == EMPTY_SLOT ? 0 : 1);
		}
		ItemContainer container = mock(ItemContainer.class);
		when(container.getId()).thenReturn(containerId);
		when(container.getItems()).thenReturn(items);
		return new ItemContainerChanged(containerId, container);
	}
}
