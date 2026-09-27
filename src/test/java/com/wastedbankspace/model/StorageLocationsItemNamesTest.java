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

import net.runelite.api.gameval.ItemID;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the item name lookups in {@link StorageLocations} that the "Non Flagged Items" list relies on to turn
 * the names a player types into item ids.
 */
public class StorageLocationsItemNamesTest
{
	private static ItemNameFixture itemNames;

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

	@Test
	public void everyStorableItemIsNamedFromTheItemManager()
	{
		for (int itemId : StorageLocations.getItemIdMap().keySet())
		{
			assertEquals(ItemNameFixture.nameOf(itemId), StorageLocations.getStorableItemName(itemId));
		}
	}

	@Test
	public void displayNameKeepsItsOriginalSpelling()
	{
		assertEquals("Dragon harpoon (or)", StorageLocations.getStorableItemName(ItemID.TRAILBLAZER_HARPOON_NO_INFERNAL));
	}

	@Test
	public void namesAreIndexedInLowerCaseWithoutWhitespace()
	{
		Set<String> keys = new HashSet<>(StorageLocations.getModifiedItemNameMap().keySet());

		assertTrue(keys.contains("dragonharpoon(or)"));
		assertTrue(keys.contains("gricoller'scan"));
		for (String key : keys)
		{
			assertEquals(key.toLowerCase(), key);
			assertTrue(key, key.chars().noneMatch(Character::isWhitespace));
		}
	}

	@Test
	public void everyIndexedNameLeadsBackToItsItem()
	{
		Map<String, Integer> index = StorageLocations.getModifiedItemNameMap();
		for (int itemId : StorageLocations.getItemIdMap().keySet())
		{
			// The fixture's names only contain single spaces
			String cleanedName = ItemNameFixture.nameOf(itemId).toLowerCase().replace(" ", "");
			assertEquals(Integer.valueOf(itemId), index.get(cleanedName));
		}
	}

	@Test
	public void storableItemIdLookupIgnoresCase()
	{
		Integer expected = ItemID.TRAILBLAZER_HARPOON_NO_INFERNAL;

		assertEquals(expected, StorageLocations.getStorableItemId("dragonharpoon(or)"));
		assertEquals(expected, StorageLocations.getStorableItemId("DRAGONHARPOON(OR)"));
		assertEquals(expected, StorageLocations.getStorableItemId("DragonHarpoon(Or)"));
	}

	@Test
	public void unknownNameHasNoItemId()
	{
		assertNull(StorageLocations.getStorableItemId("notarealitem"));
		assertNull(StorageLocations.getStorableItemId("harp"));
	}
}
