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
import net.runelite.client.game.ItemManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class StorageLocations {
    private static final Map<StorableItem, String> storableItemNameMap = new HashMap<>();
    private static final Map<Integer, StorableItem> ITEM_ID_MAP = new HashMap<>();

    static {
        //Currently just run ([A-Za-z]+)\.java replaced with registerItems($1.class);
        // on the ls of the model.locations folder
        registerItems(new ArmourCase());
        registerItems(new Bookcase());
        registerItems(new CapeRack());
        registerItems(new ElnockInquisitor());
        registerItems(new FancyDressBox());
        registerItems(new FlamtaerBag());
        registerItems(new ForestryKit());
        registerItems(new FossilStorage());
        registerItems(new HuntsmansKit());
        registerItems(new MagicWardrobe());
        registerItems(new MasterScrollBook());
        registerItems(new MysteriousStranger());
        registerItems(new NightmareZone());
        registerItems(new PetHouse());
        registerItems(new SeedVault());
        registerItems(new SpiceRack());
        registerItems(new SteelKeyRing());
        registerItems(new TackleBox());
        registerItems(new ToolLeprechaun());
        registerItems(new ToyBox());
        registerItems(new TreasureChest());
    }

    private static void registerItems(StorageLocation storageLocation) {
        for (int itemId : storageLocation.getStorableItems()) {
            StorableItem storableItem = new StorableItem(
                    itemId, storageLocation.getName(), BestInSlot.ITEMS.contains(itemId));
            ITEM_ID_MAP.put(itemId, storableItem);
        }
    }

    public static void prepareStorableItemNames(ItemManager itemManager) {
        storableItemNameMap.clear();
        for (StorableItem item : ITEM_ID_MAP.values()) {
            storableItemNameMap.put(item, itemManager.getItemComposition(item.getItemId()).getName());
        }
    }

    public static boolean isItemStorable(int id)
    {
        return ITEM_ID_MAP.containsKey(id);
    }

    public static List<String> storableListToString(List<? extends StorableItem> items) {
        return items.stream()
                .filter(storableItemNameMap::containsKey)
                .map(i -> String.format("%s", storableItemNameMap.get(i)))
                .collect(Collectors.toList());
    }

    public static String getStorableItemName(Integer id) { return getStorableItemName(ITEM_ID_MAP.get(id)); }
    public static String getStorableItemName(StorableItem item)
    {
        return storableItemNameMap.getOrDefault(item, null);
    }
}
