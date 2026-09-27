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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.runelite.api.gameval.ItemID;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.wastedbankspace.model.StorageLocationEnums.findLocationClasses;
import static com.wastedbankspace.model.StorageLocationEnums.itemsOf;
import static com.wastedbankspace.model.StorageLocationEnums.locationOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Checks the storage location data in {@code model.locations} and its registration in {@link StorageLocations}.
 * The locations are found by scanning the package, so a newly added location is checked too.
 * Only reads the static registry, never changes it.
 */
public class StorageLocationsTest
{
	private static List<Class<?>> locations;

	/** Item ID to every location that lists it */
	private static Map<Integer, Set<Class<?>>> locationsByItemId;

	@BeforeClass
	public static void findLocations()
	{
		locations = findLocationClasses();
		locationsByItemId = new HashMap<>();
		for (Class<?> location : locations)
		{
			if (!location.isEnum())
			{
				// Reported by everyLocationIsAnEnumWithItems
				continue;
			}
			for (StorableItem item : itemsOf(location))
			{
				locationsByItemId.computeIfAbsent(item.getItemID(), id -> new HashSet<>()).add(location);
			}
		}
	}

	@Test
	public void scanFindsTheLocationOfEveryRegisteredItem()
	{
		assertFalse(locations.isEmpty());
		for (StorableItem registered : StorageLocations.getItemIdMap().values())
		{
			assertTrue(locationOf(registered).getName() + " is registered but was not found by the package scan",
				locations.contains(locationOf(registered)));
		}
	}

	@Test
	public void everyLocationIsAnEnumWithItems()
	{
		for (Class<?> location : locations)
		{
			assertTrue(location.getName() + " must be an enum", location.isEnum());
			assertFalse(location.getSimpleName() + " has no items", itemsOf(location).isEmpty());
		}
	}

	@Test
	public void everyItemIdIsAGameItemId()
	{
		Set<Integer> gameItemIds = gameItemIds();
		for (Class<?> location : locations)
		{
			for (StorableItem item : itemsOf(location))
			{
				String name = describe(item);
				assertTrue(name + " has item ID " + item.getItemID(), item.getItemID() > 0);
				assertTrue(name + " has item ID " + item.getItemID() + ", which is not in net.runelite.api.gameval.ItemID",
					gameItemIds.contains(item.getItemID()));
			}
		}
	}

	@Test
	public void noLocationListsTheSameItemTwice()
	{
		for (Class<?> location : locations)
		{
			Map<Integer, StorableItem> seen = new HashMap<>();
			for (StorableItem item : itemsOf(location))
			{
				StorableItem previous = seen.put(item.getItemID(), item);
				assertNull(describe(item) + " has the same item ID as " + (previous == null ? "" : describe(previous)),
					previous);
			}
		}
	}

	@Test
	public void everyItemOfALocationHasTheSameNonBlankLocationName()
	{
		for (Class<?> location : locations)
		{
			String expected = locationName(location);
			assertFalse(location.getSimpleName() + " has a blank location name", expected.trim().isEmpty());
			for (StorableItem item : itemsOf(location))
			{
				assertEquals("location name of " + describe(item), expected, item.getLocation());
			}
		}
	}

	@Test
	public void locationNamesAreDistinct()
	{
		Map<String, Class<?>> byName = new HashMap<>();
		for (Class<?> location : locations)
		{
			String name = locationName(location);
			Class<?> previous = byName.put(name.trim().toLowerCase(Locale.ROOT), location);
			assertNull("\"" + name + "\" is the location name of both " + location.getSimpleName() + " and "
				+ (previous == null ? "" : previous.getSimpleName()), previous);
		}
	}

	@Test
	public void everyLocationItemIsStorable()
	{
		for (Class<?> location : locations)
		{
			for (StorableItem item : itemsOf(location))
			{
				int id = item.getItemID();
				assertTrue(describe(item) + " is not registered as storable", StorageLocations.isItemStorable(id));

				StorableItem registered = StorageLocations.getStorableItem(id);
				assertNotNull(describe(item) + " has no registered storable item", registered);
				assertEquals("item ID of the item registered for " + describe(item), id, registered.getItemID());

				Set<Class<?>> holders = locationsByItemId.get(id);
				if (holders.size() == 1)
				{
					assertSame("registered item for ID " + id, item, registered);
				}
				else
				{
					// Only one location can be named in the tooltip, but it must be one that holds the item
					assertTrue(describe(registered) + " is registered for item ID " + id + ", which is only listed by "
						+ holders, holders.contains(locationOf(registered)));
				}
			}
		}
	}

	@Test
	public void registryHoldsExactlyTheLocationItemsKeyedByTheirOwnId()
	{
		Map<Integer, StorableItem> registry = StorageLocations.getItemIdMap();
		assertEquals(locationsByItemId.keySet(), registry.keySet());
		for (Map.Entry<Integer, StorableItem> entry : registry.entrySet())
		{
			assertEquals("item registered under ID " + entry.getKey(), entry.getKey().intValue(),
				entry.getValue().getItemID());
		}
	}

	@Test
	public void itemsInNoLocationAreNotStorable()
	{
		int[] ids = {-1, ItemID.COINS, ItemID.BANK_FILLER, Integer.MAX_VALUE};
		for (int id : ids)
		{
			assertFalse("test precondition: item " + id + " is in a location", locationsByItemId.containsKey(id));
			assertFalse("item " + id + " should not be storable", StorageLocations.isItemStorable(id));
			assertNull("item " + id + " should have no storable item", StorageLocations.getStorableItem(id));
		}
	}

	/**
	 * @return the location name of the first item of a location
	 */
	private static String locationName(Class<?> location)
	{
		List<StorableItem> items = itemsOf(location);
		assertFalse(location.getSimpleName() + " has no items", items.isEmpty());
		String name = items.get(0).getLocation();
		assertNotNull(location.getSimpleName() + " has no location name", name);
		return name;
	}

	private static Set<Integer> gameItemIds()
	{
		Set<Integer> ids = new HashSet<>();
		for (Field field : ItemID.class.getFields())
		{
			if (field.getType() == int.class && Modifier.isStatic(field.getModifiers()))
			{
				try
				{
					ids.add(field.getInt(null));
				}
				catch (IllegalAccessException e)
				{
					throw new IllegalStateException(e);
				}
			}
		}
		return ids;
	}

	private static String describe(StorableItem item)
	{
		return locationOf(item).getSimpleName() + "." + ((Enum<?>) item).name();
	}
}
