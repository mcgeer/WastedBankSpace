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
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import lombok.Getter;
import net.runelite.client.game.ItemManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class StorageLocations
{
	@Getter
	private static final Map<StorableItem, String> storableItemNameMap = new HashMap<>();
	@Getter
	private static final Map<Integer, StorableItem> itemIdMap = new HashMap<>();
	// Use TreeMap instead of hashmap so that we can use string insensitive comparison in order to access values
	@Getter
	private static final Map<String, Integer> itemNameMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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
	}

	public static void prepareStorableItemNames(ItemManager itemManager)
	{
		storableItemNameMap.clear();
		for (StorableItem item : itemIdMap.values())
		{
			storableItemNameMap.put(item, itemManager.getItemComposition(item.getItemID()).getName());
		}
	}

	public static boolean isItemStorable(int id)
	{
		return itemIdMap.containsKey(id);
	}

	public static Set<String> storableListToString(Set<Integer> itemIds)
	{
		// TODO: may be able to collapse the for loop into a stream call
		Set<String> itemNames = new HashSet<>();
		for (int i : itemIds) {
			itemNames.add(getStorableItemName(i));
		}
		return itemNames;
	}

	public static StorableItem getStorableItem(Integer id)
	{
		return itemIdMap.get(id);
	}
	public static String getStorableItemName(Integer id)
	{
		return getStorableItemName(itemIdMap.get(id));
	}

	public static String getStorableItemName(StorableItem item)
	{
		return storableItemNameMap.getOrDefault(item, null);
	}

	public static Integer getStorableItemId(String name)
	{
		return itemNameMap.get(name);
	}
}
