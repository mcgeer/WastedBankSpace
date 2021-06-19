package com.wastedbankspace.model;

import lombok.Getter;
import net.runelite.api.ItemID;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum StorableItem {
    /**
     * TACKLE BOX
     */
    //Equipment
    ANGLER_BOOTS(ItemID.ANGLER_BOOTS, StorageLocation.TACKLE_BOX),
    ANGLER_HAT(ItemID.ANGLER_HAT, StorageLocation.TACKLE_BOX),
    ANGLER_TOP(ItemID.ANGLER_TOP, StorageLocation.TACKLE_BOX),
    ANGLER_WADERS(ItemID.ANGLER_WADERS, StorageLocation.TACKLE_BOX),

    SPIRIT_ANGLER_BOOTS(ItemID.SPIRIT_ANGLER_BOOTS, StorageLocation.TACKLE_BOX),
    SPIRIT_ANGLER_HEADBAND(ItemID.SPIRIT_ANGLER_HEADBAND, StorageLocation.TACKLE_BOX),
    SPIRIT_ANGLER_TOP(ItemID.SPIRIT_ANGLER_TOP, StorageLocation.TACKLE_BOX),
    SPIRIT_ANGLER_WADERS(ItemID.SPIRIT_ANGLER_WADERS, StorageLocation.TACKLE_BOX),

    FISHBOWL_HELMET(ItemID.FISHBOWL_HELMET, StorageLocation.TACKLE_BOX),
    FLIPPERS(ItemID.FLIPPERS, StorageLocation.TACKLE_BOX),
    DARK_FLIPPERS(ItemID.DARK_FLIPPERS, StorageLocation.TACKLE_BOX),
    DIVING_APPARATUS(ItemID.DIVING_APPARATUS, StorageLocation.TACKLE_BOX),

    RADAS_BLESSING_1(ItemID.RADAS_BLESSING_1, StorageLocation.TACKLE_BOX),
    RADAS_BLESSING_2(ItemID.RADAS_BLESSING_2, StorageLocation.TACKLE_BOX),
    RADAS_BLESSING_3(ItemID.RADAS_BLESSING_3, StorageLocation.TACKLE_BOX),
    RADAS_BLESSING_4(ItemID.RADAS_BLESSING_4, StorageLocation.TACKLE_BOX),

    TINY_NET(ItemID.TINY_NET, StorageLocation.TACKLE_BOX),
    HARPOON(ItemID.HARPOON, StorageLocation.TACKLE_BOX),
    BARBTAIL_HARPOON(ItemID.BARBTAIL_HARPOON, StorageLocation.TACKLE_BOX),
    DRAGON_HARPOON(ItemID.DRAGON_HARPOON, StorageLocation.TACKLE_BOX),
    INFERNAL_HARPOON(ItemID.INFERNAL_HARPOON, StorageLocation.TACKLE_BOX),
    DRAGON_HARPOON_OR(ItemID.DRAGON_HARPOON_OR, StorageLocation.TACKLE_BOX),
    INFERNAL_HARPOON_OR(ItemID.INFERNAL_HARPOON_OR, StorageLocation.TACKLE_BOX),
    CRYSTAL_HARPOON(ItemID.CRYSTAL_HARPOON, StorageLocation.TACKLE_BOX),
    MERFOLK_TRIDENT(ItemID.MERFOLK_TRIDENT, StorageLocation.TACKLE_BOX),
    FISHING_ROD(ItemID.FISHING_ROD, StorageLocation.TACKLE_BOX),
    FLY_FISHING_ROD(ItemID.FLY_FISHING_ROD, StorageLocation.TACKLE_BOX),
    OILY_FISHING_ROD(ItemID.OILY_FISHING_ROD, StorageLocation.TACKLE_BOX),
    BARBARIAN_ROD(ItemID.BARBARIAN_ROD, StorageLocation.TACKLE_BOX),
    PEARL_FISHING_ROD(ItemID.PEARL_FISHING_ROD, StorageLocation.TACKLE_BOX),
    PEARL_FLY_FISHING_ROD(ItemID.PEARL_FLY_FISHING_ROD, StorageLocation.TACKLE_BOX),
    OILY_PEARL_FISHING_ROD(ItemID.OILY_PEARL_FISHING_ROD, StorageLocation.TACKLE_BOX),
    PEARL_BARBARIAN_ROD(ItemID.PEARL_BARBARIAN_ROD, StorageLocation.TACKLE_BOX),
    SMALL_FISHING_NET(ItemID.SMALL_FISHING_NET, StorageLocation.TACKLE_BOX),
    BIG_FISHING_NET(ItemID.BIG_FISHING_NET, StorageLocation.TACKLE_BOX),
    DRIFT_NET(ItemID.DRIFT_NET, StorageLocation.TACKLE_BOX),
    LOBSTER_POT(ItemID.LOBSTER_POT, StorageLocation.TACKLE_BOX),
    KARAMBWAN_VESSEL(ItemID.KARAMBWAN_VESSEL, StorageLocation.TACKLE_BOX),
    KARAMBWAN_VESSEL_FILLED(ItemID.KARAMBWAN_VESSEL_3159, StorageLocation.TACKLE_BOX),

    //Bait
    SPIRIT_FLAKES(ItemID.SPIRIT_FLAKES, StorageLocation.TACKLE_BOX),
    FEATHERS(ItemID.FEATHER, StorageLocation.TACKLE_BOX),
    RAW_KARAMBWANJI(ItemID.RAW_KARAMBWANJI, StorageLocation.TACKLE_BOX),
    FISHING_BAIT(ItemID.FISHING_BAIT, StorageLocation.TACKLE_BOX),
    DARK_FISHING_BAIT(ItemID.DARK_FISHING_BAIT, StorageLocation.TACKLE_BOX),
    SANDWORMS(ItemID.SANDWORMS, StorageLocation.TACKLE_BOX),
    FISH_OFFCUTS(ItemID.FISH_OFFCUTS, StorageLocation.TACKLE_BOX),
    FISH_CHUNKS(ItemID.FISH_CHUNKS, StorageLocation.TACKLE_BOX),
    //Misc
    FISHING_POTION1(ItemID.FISHING_POTION1, StorageLocation.TACKLE_BOX),
    FISHING_POTION2(ItemID.FISHING_POTION2, StorageLocation.TACKLE_BOX),
    FISHING_POTION3(ItemID.FISHING_POTION3, StorageLocation.TACKLE_BOX),
    FISHING_POTION4(ItemID.FISHING_POTION4, StorageLocation.TACKLE_BOX),
    MOLCH_PEARLS(ItemID.MOLCH_PEARL, StorageLocation.TACKLE_BOX);

    public final int itemID;
    public final StorageLocation location;

    StorableItem(int itemID, StorageLocation location)
    {
        this.itemID = itemID;
        this.location = location;
    }

    public static final List<StorableItem> tackleBoxItems = Arrays.stream(StorableItem.values())
            .filter(x ->x.location == StorageLocation.TACKLE_BOX)
            .collect(Collectors.toList());

    private static final Map<Integer, StorableItem> ITEM_ID_MAP = new HashMap<>();
    static
    {
        for (StorableItem i : values())
        {
            ITEM_ID_MAP.put(i.getItemID(), i);
        }
    }

}
