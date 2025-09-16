/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2021, Riley McGee
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

import com.wastedbankspace.model.locations.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ItemManager;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class StorageLocations
{
	/**
	 * itemNameMap: Maps a storable item ID to the corresponding item name
	 */
	private static final Map<Integer, String> itemNameMap = new HashMap<>();

	/**
	 *	itemIdMap: Maps item ID to items that are storable (see registerItems)
	 */
	@Getter
	private static final Map<Integer, StorableItem> itemIdMap = new HashMap<>();

	/**
	 *	modifiedItemNameMap: Maps item name to item ID after cleaning name string.
	 *	Note: Use TreeMap instead of hashmap so that we can use string case-insensitive comparison
	 *		in order to access values
	 */
	@Getter
	private static final Map<String, Integer> modifiedItemNameMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	static
	{
		// Currently just run ([A-Za-z]+)\.java replaced with registerItems($1.class);
		// on the ls of the model.locations folder
		registerItems(ArmourCase.class);
		registerItems(Bookcase.class);
		registerItems(CapeRack.class);
		registerItems(ElnockInquisitor.class);
		registerItems(FancyDressBox.class);
		registerItems(FlamtaerBag.class);
		registerItems(ForestryKit.class);
		registerItems(FossilStorage.class);
		registerItems(HuntsmansKit.class);
		registerItems(MagicWardrobe.class);
		registerItems(MasterScrollBook.class);
		registerItems(MysteriousStranger.class);
		registerItems(NightmareZone.class);
		registerItems(PetHouse.class);
		registerItems(SeedVault.class);
		registerItems(SpiceRack.class);
		registerItems(SteelKeyRing.class);
		registerItems(TackleBox.class);
		registerItems(ToolLeprechaun.class);
		registerItems(ToyBox.class);
		registerItems(TreasureChest.class);
	}

	private static <E extends Enum<E> & StorableItem> void registerItems(Class<E> enumClass)
	{
		for (E item : enumClass.getEnumConstants())
		{
			itemIdMap.put(item.getItemID(), item);
		}
		log.debug("Registered items from {}", enumClass.getSimpleName());
	}

	public static void prepareStorableItemNames(ItemManager itemManager)
	{
		log.debug("Starting prepareStorableItemNames()");
		log.debug("itemIdMap contents before preparing: {}", itemIdMap.values());

		for (StorableItem item : itemIdMap.values())
		{
			String item_name = itemManager.getItemComposition(item.getItemID()).getName();
			itemNameMap.put(item.getItemID(), item_name);
			// Standardize name's to lowercase and remove spaces for case-insensitive comparison
			String cleaned_item_name = item_name.toLowerCase().replaceAll("\\s+", "");
			modifiedItemNameMap.put(cleaned_item_name, item.getItemID());
		}
		if (itemIdMap.size() == itemNameMap.size())
		{
			log.debug("Successfully prepared storableItemNameMap");
		}
		else
		{
			log.warn("prepareStorableItemNames() did not successfully prepare storableItemNameMap");
		}
	}

	public static boolean isItemStorable(int id)
	{
		return itemIdMap.containsKey(id);
	}

	/**
	 * Converts a set of item IDs into a set of their corresponding item names.
	 *
	 * @param itemIds A set of item IDs to be converted.
	 * @return A set of item names corresponding to the provided item IDs.
	 */
	public static List<String> itemIdsToString(Set<Integer> itemIds)
	{
		return itemIds.stream()
			.map(itemNameMap::get)
			.collect(Collectors.toList());
	}

	public static StorableItem getStorableItem(Integer id)
	{
		return itemIdMap.get(id);
	}

	public static String getStorableItemName(Integer id)
	{
		return itemNameMap.get(id);
	}

	public static Integer getStorableItemId(String name)
	{
		return modifiedItemNameMap.get(name);
	}
}
