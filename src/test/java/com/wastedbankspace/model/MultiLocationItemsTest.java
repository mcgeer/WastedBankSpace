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

package com.wastedbankspace.model;

import com.wastedbankspace.model.locations.ArmourCase;
import com.wastedbankspace.model.locations.CapeRack;
import com.wastedbankspace.model.locations.FancyDressBox;
import com.wastedbankspace.model.locations.ForestryKit;
import com.wastedbankspace.model.locations.HuntsmansKit;
import com.wastedbankspace.model.locations.MagicWardrobe;
import com.wastedbankspace.model.locations.TackleBox;
import com.wastedbankspace.model.locations.ToyBox;
import com.wastedbankspace.model.locations.TreasureChest;
import net.runelite.api.gameval.ItemID;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wastedbankspace.model.StorageLocationEnums.findLocationClasses;
import static com.wastedbankspace.model.StorageLocationEnums.itemsOf;
import static com.wastedbankspace.model.StorageLocationEnums.locationOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Some items can be stored in more than one location, e.g. the woodcutting cape in the cape rack and in the forestry
 * kit. {@link StorageLocations#getItemIdMap()} keeps one entry per item ID (the location registered last), so
 * tooltips need {@link StorageLocations#getStorableItems(int)}, which keeps every entry, and the checks built on it.
 * Only reads the static registry, never changes it.
 */
public class MultiLocationItemsTest
{
	/** The POH costume room storages, whose contents the game reports together */
	private static final Set<Class<?>> COSTUME_ROOM_LOCATIONS = Set.of(
		ArmourCase.class, CapeRack.class, FancyDressBox.class, MagicWardrobe.class, ToyBox.class, TreasureChest.class);

	/** Item ID to every location entry that lists it, from scanning the locations package */
	private static Map<Integer, List<StorableItem>> entriesByItemId;

	@BeforeClass
	public static void scanLocations()
	{
		entriesByItemId = new HashMap<>();
		for (Class<?> location : findLocationClasses())
		{
			for (StorableItem item : itemsOf(location))
			{
				entriesByItemId.computeIfAbsent(item.getItemID(), id -> new ArrayList<>()).add(item);
			}
		}
	}

	// getStorableItems

	@Test
	public void woodcuttingCapeIsListedByTheCapeRackAndTheForestryKit()
	{
		// Guards the test data: the forestry kit registered the cape last, so the single-entry registry only has that
		assertSame(ForestryKit.WOODCUTTING_CAPE, StorageLocations.getStorableItem(ItemID.SKILLCAPE_WOODCUTTING));

		assertEquals(List.of(CapeRack.WOODCUTTING_CAPE_2, ForestryKit.WOODCUTTING_CAPE),
			StorageLocations.getStorableItems(ItemID.SKILLCAPE_WOODCUTTING));
	}

	@Test
	public void everyLocationEntryOfAnItemIsReturned()
	{
		assertFalse(entriesByItemId.isEmpty());
		for (Map.Entry<Integer, List<StorableItem>> expected : entriesByItemId.entrySet())
		{
			int id = expected.getKey();
			List<StorableItem> entries = StorageLocations.getStorableItems(id);

			assertEquals("location entries of item " + id, new HashSet<>(expected.getValue()), new HashSet<>(entries));
			assertEquals("item " + id + " has a location entry more than once", expected.getValue().size(),
				entries.size());
			for (StorableItem entry : entries)
			{
				assertEquals("item ID of " + describe(entry), id, entry.getItemID());
			}
		}
	}

	@Test
	public void itemsListedByTwoLocationsExist()
	{
		// Guards the test data of the tooltip tests: without such items there is nothing to test
		Set<Integer> twoLocations = new HashSet<>();
		for (Map.Entry<Integer, List<StorableItem>> entry : entriesByItemId.entrySet())
		{
			if (entry.getValue().size() > 1)
			{
				twoLocations.add(entry.getKey());
			}
		}
		assertTrue(twoLocations.contains(ItemID.SKILLCAPE_WOODCUTTING));
		assertTrue(twoLocations.contains(ItemID.NET));
		assertTrue(twoLocations.contains(ItemID.HUNTING_HAT_JAGUAR));
		assertTrue(twoLocations.contains(ItemID.DARK_FLIPPERS));
	}

	@Test
	public void singleLocationItemHasOneEntry()
	{
		assertEquals(List.of(ForestryKit.LEAVES), StorageLocations.getStorableItems(ItemID.LEAVES));
		assertEquals(List.of(TreasureChest.JESTER_CAPE), StorageLocations.getStorableItems(ItemID.JESTER_CAPE));
	}

	@Test
	public void itemInNoLocationHasNoEntries()
	{
		int[] ids = {-1, ItemID.COINS, Integer.MAX_VALUE};
		for (int id : ids)
		{
			assertFalse("test precondition: item " + id + " is in a location", entriesByItemId.containsKey(id));
			assertEquals("entries of item " + id, Collections.emptyList(), StorageLocations.getStorableItems(id));
			assertFalse("item " + id + " is best in slot", StorageLocations.isBestInSlot(id));
		}
	}

	// isBestInSlot

	@Test
	public void bestInSlotWhenAnyLocationMarksTheItemAsSuch()
	{
		// The cape rack marks the cape as best in slot; the forestry kit, which holds the single registry entry, does not
		assertTrue(CapeRack.WOODCUTTING_CAPE_2.isBis());
		assertFalse(ForestryKit.WOODCUTTING_CAPE.isBis());
		assertFalse(StorageLocations.getStorableItem(ItemID.SKILLCAPE_WOODCUTTING).isBis());

		assertTrue(StorageLocations.isBestInSlot(ItemID.SKILLCAPE_WOODCUTTING));
	}

	@Test
	public void notBestInSlotWhenNoLocationMarksTheItemAsSuch()
	{
		assertFalse(TackleBox.SMALL_FISHING_NET.isBis());
		assertFalse(HuntsmansKit.SMALL_FISHING_NET_.isBis());
		assertFalse(StorageLocations.isBestInSlot(ItemID.NET));

		assertFalse(ForestryKit.LEAVES.isBis());
		assertFalse(StorageLocations.isBestInSlot(ItemID.LEAVES));
	}

	@Test
	public void bestInSlotAgreesWithTheLocationEntriesOfEveryItem()
	{
		for (Map.Entry<Integer, List<StorableItem>> entry : entriesByItemId.entrySet())
		{
			boolean expected = false;
			for (StorableItem item : entry.getValue())
			{
				expected |= item.isBis();
			}
			assertEquals("best in slot for item " + entry.getKey(), expected,
				StorageLocations.isBestInSlot(entry.getKey()));
		}
	}

	// isCostumeRoomItem

	@Test
	public void costumeRoomItemsAreThoseOfThePohCostumeRoomStorages()
	{
		assertTrue(StorageLocations.isCostumeRoomItem(ArmourCase.LARUPIA_HAT));
		assertTrue(StorageLocations.isCostumeRoomItem(CapeRack.WOODCUTTING_CAPE_2));
		assertTrue(StorageLocations.isCostumeRoomItem(MagicWardrobe.DARK_FLIPPERS_MAGIC_WARDROBE));
		assertTrue(StorageLocations.isCostumeRoomItem(TreasureChest.JESTER_CAPE));

		// The same items, listed by locations outside the house
		assertFalse(StorageLocations.isCostumeRoomItem(HuntsmansKit.LARUPIA_HAT_));
		assertFalse(StorageLocations.isCostumeRoomItem(ForestryKit.WOODCUTTING_CAPE));
		assertFalse(StorageLocations.isCostumeRoomItem(TackleBox.DARK_FLIPPERS));
		assertFalse(StorageLocations.isCostumeRoomItem(TackleBox.SMALL_FISHING_NET));
	}

	@Test
	public void everyLocationIsEitherInTheCostumeRoomOrNot()
	{
		Set<Class<?>> costumeRoom = new HashSet<>();
		for (Class<?> location : findLocationClasses())
		{
			for (StorableItem item : itemsOf(location))
			{
				boolean expected = COSTUME_ROOM_LOCATIONS.contains(location);
				assertEquals(describe(item) + " in the costume room", expected, StorageLocations.isCostumeRoomItem(item));
				if (expected)
				{
					costumeRoom.add(location);
				}
			}
		}
		assertEquals(COSTUME_ROOM_LOCATIONS, costumeRoom);
	}

	@Test
	public void itemThatIsNotAnEnumConstantIsNotInTheCostumeRoom()
	{
		StorableItem item = new StorableItem()
		{
			@Override
			public int getItemID()
			{
				return ItemID.JESTER_CAPE;
			}

			@Override
			public String getLocation()
			{
				return "Treasure Chest";
			}

			@Override
			public boolean isBis()
			{
				return false;
			}
		};

		assertFalse(StorageLocations.isCostumeRoomItem(item));
	}

	private static String describe(StorableItem item)
	{
		return locationOf(item).getSimpleName() + "." + ((Enum<?>) item).name();
	}
}
