package com.wastedbankspace.model;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;

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
    MOLCH_PEARLS(ItemID.MOLCH_PEARL, StorageLocation.TACKLE_BOX),

    /**
     * Steel Key Ring
     */
    ANCESTREAL_KEY(ItemID.ANCESTRAL_KEY, StorageLocation.STEEL_KEY_RING),
    BATTERED_KEY(ItemID.BATTERED_KEY, StorageLocation.STEEL_KEY_RING),
    BONE_KEY(ItemID.BONE_KEY, StorageLocation.STEEL_KEY_RING),
    BRASS_KEY(ItemID.BRASS_KEY, StorageLocation.STEEL_KEY_RING),
    CRYSTALMINE_KEY(ItemID.CRYSTALMINE_KEY, StorageLocation.STEEL_KEY_RING),
    DUSTY_KEY(ItemID.DUSTY_KEY, StorageLocation.STEEL_KEY_RING),
    ENCHANTED_KEY(ItemID.ENCHANTED_KEY, StorageLocation.STEEL_KEY_RING),
    WEAPON_STORE_KEY(ItemID.WEAPON_STORE_KEY, StorageLocation.STEEL_KEY_RING),
    MAZE_KEY(ItemID.MAZE_KEY, StorageLocation.STEEL_KEY_RING),
    METAL_KEY(ItemID.METAL_KEY, StorageLocation.STEEL_KEY_RING),
    NEW_KEY(ItemID.NEW_KEY, StorageLocation.STEEL_KEY_RING),
    SHINY_KEY(ItemID.SHINY_KEY, StorageLocation.STEEL_KEY_RING),
    WROUGHT_IRON_KEY(ItemID.WROUGHT_IRON_KEY, StorageLocation.STEEL_KEY_RING),

    /**
     * Tool Lep
     */
    RAKE(ItemID.RAKE, StorageLocation.TOOL_LEP),
    SPADE(ItemID.SPADE, StorageLocation.TOOL_LEP),
    SEED_DIBBER(ItemID.SEED_DIBBER, StorageLocation.TOOL_LEP),
    SECATEURS(ItemID.SECATEURS, StorageLocation.TOOL_LEP),
    MAGIC_SECATEURS(ItemID.MAGIC_SECATEURS, StorageLocation.TOOL_LEP),
    GARDENING_TROWEL(ItemID.GARDENING_TROWEL, StorageLocation.TOOL_LEP),
    BOTOMLESS_COMPOST(ItemID.BOTTOMLESS_COMPOST_BUCKET, StorageLocation.TOOL_LEP),
    BOTTOMLESS_COMPOST_BUCKET_22997(ItemID.BOTTOMLESS_COMPOST_BUCKET_22997, StorageLocation.TOOL_LEP),
    PLANT_CURE(ItemID.PLANT_CURE, StorageLocation.TOOL_LEP),
    PLANT_CURE_6468(ItemID.PLANT_CURE_6468, StorageLocation.TOOL_LEP),
    COMPOST(ItemID.COMPOST, StorageLocation.TOOL_LEP),
    SUPERCOMPOST(ItemID.SUPERCOMPOST, StorageLocation.TOOL_LEP),
    ULTRACOMPOST(ItemID.ULTRACOMPOST, StorageLocation.TOOL_LEP),
    WATERING_CAN(ItemID.WATERING_CAN, StorageLocation.TOOL_LEP),
    WATERING_CAN1(ItemID.WATERING_CAN1, StorageLocation.TOOL_LEP),
    WATERING_CAN2(ItemID.WATERING_CAN2, StorageLocation.TOOL_LEP),
    WATERING_CAN3(ItemID.WATERING_CAN3, StorageLocation.TOOL_LEP),
    WATERING_CAN4(ItemID.WATERING_CAN4, StorageLocation.TOOL_LEP),
    WATERING_CAN5(ItemID.WATERING_CAN5, StorageLocation.TOOL_LEP),
    WATERING_CAN6(ItemID.WATERING_CAN6, StorageLocation.TOOL_LEP),
    WATERING_CAN7(ItemID.WATERING_CAN7, StorageLocation.TOOL_LEP),
    WATERING_CAN8(ItemID.WATERING_CAN8, StorageLocation.TOOL_LEP),
    GRICOLLERS_CAN(ItemID.GRICOLLERS_CAN, StorageLocation.TOOL_LEP),

    ;
    public final int itemID;
    public final StorageLocation location;

    StorableItem(int itemID, StorageLocation location)
    {
        this.itemID = itemID;
        this.location = location;
    }

    private static List<StorableItem> storableItemsAtLocation(StorageLocation storageLocation)
    {
        return Arrays.stream(StorableItem.values())
                .filter(x ->x.location == storageLocation)
                .collect(Collectors.toList());
    }

    public static final List<StorableItem> tackleBoxItems = storableItemsAtLocation(StorageLocation.TACKLE_BOX);
    public static final List<StorableItem> steelKeyRingItems = storableItemsAtLocation(StorageLocation.STEEL_KEY_RING);
    public static final List<StorableItem> toolLepItems = storableItemsAtLocation(StorageLocation.TOOL_LEP);

    private static final Map<Integer, StorableItem> ITEM_ID_MAP = new HashMap<>();
    static
    {
        for (StorableItem i : values())
        {
            ITEM_ID_MAP.put(i.getItemID(), i);
        }
    }

    public static List<String> StorableListToString(List<StorableItem> items, ItemManager itemManager)
    {
        return items.stream()
                .map(i -> String.format("%d --> %s", i.itemID /*itemManager.getItemComposition(i.itemID).getName()*/, i.location.toString()))
                .collect(Collectors.toList());
    }

}
