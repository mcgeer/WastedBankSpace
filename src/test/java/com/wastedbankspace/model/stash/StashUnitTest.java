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

package com.wastedbankspace.model.stash;

import com.wastedbankspace.model.StorageLocations;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Sanity checks on the STASH unit data, which was generated from RuneLite's clue scroll plugin and the OSRS Wiki
 */
public class StashUnitTest
{
	@Test
	public void everyStashUnitIsListedOnce()
	{
		// The OSRS Wiki's count of STASH units per tier
		Map<StashTier, Integer> expected = new EnumMap<>(StashTier.class);
		expected.put(StashTier.BEGINNER, 3);
		expected.put(StashTier.EASY, 31);
		expected.put(StashTier.MEDIUM, 25);
		expected.put(StashTier.HARD, 16);
		expected.put(StashTier.ELITE, 19);
		expected.put(StashTier.MASTER, 25);

		Map<StashTier, Integer> counted = new EnumMap<>(StashTier.class);
		for (StashUnit unit : StashUnit.values())
		{
			counted.merge(unit.getTier(), 1, Integer::sum);
		}
		assertEquals(expected, counted);
	}

	@Test
	public void everyUnitIsAStashObjectOfItsTier() throws IllegalAccessException
	{
		Map<Integer, String> objectNames = new HashMap<>();
		for (Field field : ObjectID.class.getFields())
		{
			if (Modifier.isStatic(field.getModifiers()) && field.getType() == int.class)
			{
				// ObjectID's ids live in package-private classes it extends
				field.setAccessible(true);
				objectNames.put(field.getInt(null), field.getName());
			}
		}

		Set<Integer> objectIds = new HashSet<>();
		for (StashUnit unit : StashUnit.values())
		{
			assertTrue(unit + " shares its object", objectIds.add(unit.getObjectId()));
			String objectName = objectNames.get(unit.getObjectId());
			assertTrue(unit + " is object " + objectName,
				objectName != null && objectName.startsWith("HH_" + unit.getTier().name()));
		}
	}

	@Test
	public void everyUnitHasANameAndItems()
	{
		Set<String> locations = new HashSet<>();
		for (StashUnit unit : StashUnit.values())
		{
			assertFalse(unit + " has no name", unit.getName().isBlank());
			assertTrue(unit + " has the same name as another unit", locations.add(unit.getLocation()));
			assertTrue(unit + " has no items", unit.getItemIds().length > 0);
			assertTrue(unit + " has an item twice",
				Arrays.stream(unit.getItemIds()).distinct().count() == unit.getItemIds().length);
		}
	}

	@Test
	public void locationNamesTheUnitAndItsTier()
	{
		assertEquals("Near a shed in Lumbridge Swamp (Easy STASH)",
			StashUnit.NEAR_A_SHED_IN_LUMBRIDGE_SWAMP.getLocation());
		assertEquals("Aris' tent (Beginner STASH)", StashUnit.GYPSY_TENT_ENTRANCE.getLocation());
	}

	@Test
	public void everyItemOfEveryUnitIsListed()
	{
		int items = Arrays.stream(StashUnit.values()).mapToInt(unit -> unit.getItemIds().length).sum();

		assertEquals(items, StashItem.ALL.size());
		for (StashItem item : StashItem.ALL)
		{
			assertEquals(item.getLocation(), item.getUnit().getLocation());
			assertFalse(item.isBis());
		}
	}

	@Test
	public void getUnitsListsEveryUnitTakingTheItemInUnitOrder()
	{
		assertEquals(List.of(StashUnit.NEAR_A_SHED_IN_LUMBRIDGE_SWAMP, StashUnit.RIMMINGTON_MINE,
			StashUnit.GYPSY_TENT_ENTRANCE), StashItem.getUnits(ItemID.GOLD_RING));
		assertEquals(List.of(), StashItem.getUnits(-1));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void getUnitsCannotBeChanged()
	{
		StashItem.getUnits(ItemID.GOLD_RING).clear();
	}

	@Test
	public void stashItemsAreStorable()
	{
		for (StashItem item : StashItem.ALL)
		{
			assertTrue(item + " not storable", StorageLocations.isItemStorable(item.getItemID()));
		}
	}
}
