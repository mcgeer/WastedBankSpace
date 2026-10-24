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

import com.wastedbankspace.model.stash.StashItem;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Fills the static item name maps of {@link StorageLocations} the way the plugin does on startup, through
 * {@link StorageLocations#prepareStorableItemNames(ItemManager)} with a mocked {@link ItemManager}, and puts the
 * previous contents back afterwards.
 * <p>
 * The maps are static and Gradle runs every test class in the same JVM. {@link #load()} therefore clears whatever an
 * earlier test class left behind before preparing names, and {@link #restore()} leaves the maps exactly as they were,
 * so no test class depends on another one running first.
 */
public final class ItemNameFixture
{
	/**
	 * In-game names of the storable items the tests refer to by name. Every other storable item gets a unique
	 * placeholder name, see {@link #nameOf(int)}.
	 */
	private static final Map<Integer, String> NAMES = createNames();

	private final Map<Integer, String> savedItemNames;
	private final Map<String, Integer> savedModifiedItemNames;

	private ItemNameFixture(Map<Integer, String> savedItemNames, Map<String, Integer> savedModifiedItemNames)
	{
		this.savedItemNames = savedItemNames;
		this.savedModifiedItemNames = savedModifiedItemNames;
	}

	/**
	 * Replaces the prepared names with {@link #nameOf(int)} for every storable item.
	 *
	 * @return the fixture to {@link #restore()} once the tests are done
	 */
	public static ItemNameFixture load()
	{
		Map<Integer, String> itemNames = itemNameMap();
		Map<String, Integer> modifiedItemNames = StorageLocations.getModifiedItemNameMap();
		ItemNameFixture fixture = new ItemNameFixture(new HashMap<>(itemNames), new HashMap<>(modifiedItemNames));

		itemNames.clear();
		modifiedItemNames.clear();
		try
		{
			StorageLocations.prepareStorableItemNames(itemManager());
		}
		catch (RuntimeException e)
		{
			// Don't leave half prepared names behind for the test classes that run next
			fixture.restore();
			throw e;
		}
		return fixture;
	}

	/**
	 * Puts back the names that were prepared before {@link #load()}.
	 */
	public void restore()
	{
		Map<Integer, String> itemNames = itemNameMap();
		itemNames.clear();
		itemNames.putAll(savedItemNames);

		Map<String, Integer> modifiedItemNames = StorageLocations.getModifiedItemNameMap();
		modifiedItemNames.clear();
		modifiedItemNames.putAll(savedModifiedItemNames);
	}

	/**
	 * @return the name the mocked {@link ItemManager} gives the item
	 */
	public static String nameOf(int itemId)
	{
		return NAMES.getOrDefault(itemId, "Unnamed item " + itemId);
	}

	/**
	 * @return every item whose name StorageLocations prepares: the storage locations' items and the STASH units'
	 */
	public static Set<Integer> storableItemIds()
	{
		Set<Integer> itemIds = new HashSet<>(StorageLocations.getItemIdMap().keySet());
		StashItem.ALL.forEach(item -> itemIds.add(item.getItemID()));
		return itemIds;
	}

	private static ItemManager itemManager()
	{
		Map<Integer, ItemComposition> compositions = new HashMap<>();
		for (int itemId : storableItemIds())
		{
			ItemComposition composition = mock(ItemComposition.class);
			when(composition.getName()).thenReturn(nameOf(itemId));
			compositions.put(itemId, composition);
		}

		ItemManager itemManager = mock(ItemManager.class);
		when(itemManager.getItemComposition(anyInt()))
			.thenAnswer(invocation -> compositions.get(invocation.getArgument(0)));
		return itemManager;
	}

	/**
	 * StorageLocations has no accessor for its item id to name map, so the fixture reaches it by reflection to be
	 * able to put it back.
	 */
	@SuppressWarnings("unchecked")
	private static Map<Integer, String> itemNameMap()
	{
		try
		{
			Field field = StorageLocations.class.getDeclaredField("itemNameMap");
			field.setAccessible(true);
			return (Map<Integer, String>) field.get(null);
		}
		catch (ReflectiveOperationException e)
		{
			throw new IllegalStateException("Cannot reach StorageLocations.itemNameMap", e);
		}
	}

	private static Map<Integer, String> createNames()
	{
		Map<Integer, String> names = new HashMap<>();

		// Tackle box
		names.put(ItemID.HARPOON, "Harpoon");
		names.put(ItemID.HUNTING_BARBED_HARPOON, "Barb-tail harpoon");
		names.put(ItemID.DRAGON_HARPOON, "Dragon harpoon");
		names.put(ItemID.INFERNAL_HARPOON, "Infernal harpoon");
		names.put(ItemID.CRYSTAL_HARPOON, "Crystal harpoon");
		names.put(ItemID.TRAILBLAZER_HARPOON_NO_INFERNAL, "Dragon harpoon (or)");
		names.put(ItemID.TRAILBLAZER_HARPOON, "Infernal harpoon (or)");
		names.put(ItemID.FISHING_ROD, "Fishing rod");
		names.put(ItemID.LOBSTER_POT, "Lobster pot");
		names.put(ItemID._4DOSEFISHERSPOTION, "Fishing potion(4)");

		// Tool leprechaun
		names.put(ItemID.WATERING_CAN_0, "Watering can");
		names.put(ItemID.WATERING_CAN_1, "Watering can(1)");
		names.put(ItemID.WATERING_CAN_2, "Watering can(2)");
		names.put(ItemID.WATERING_CAN_3, "Watering can(3)");
		names.put(ItemID.WATERING_CAN_4, "Watering can(4)");
		names.put(ItemID.WATERING_CAN_5, "Watering can(5)");
		names.put(ItemID.WATERING_CAN_6, "Watering can(6)");
		names.put(ItemID.WATERING_CAN_7, "Watering can(7)");
		names.put(ItemID.WATERING_CAN_8, "Watering can(8)");
		names.put(ItemID.ZEAH_WATERINGCAN, "Gricoller's can");

		// Nightmare Zone
		names.put(ItemID.NZONE4DOSEOVERLOADPOTION, "Overload (4)");
		names.put(ItemID.NZONE4DOSEABSORPTIONPOTION, "Absorption (4)");

		// Cape rack
		names.put(ItemID.SKILLCAPE_ATTACK, "Attack cape");
		names.put(ItemID.SKILLCAPE_ATTACK_TRIMMED, "Attack cape(t)");
		names.put(ItemID.SKILLCAPE_ATTACK_HOOD, "Attack hood");
		names.put(ItemID.SKILLCAPE_AGILITY, "Agility cape");
		names.put(ItemID.SKILLCAPE_AGILITY_TRIMMED, "Agility cape(t)");
		names.put(ItemID.SKILLCAPE_AGILITY_HOOD, "Agility hood");

		return Collections.unmodifiableMap(names);
	}
}
