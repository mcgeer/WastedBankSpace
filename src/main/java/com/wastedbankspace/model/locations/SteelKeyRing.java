package com.wastedbankspace.model.locations;

import com.wastedbankspace.model.StorableItem;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum SteelKeyRing implements StorableItem {
    ANCESTREAL_KEY(ItemID.ANCESTRAL_KEY),
    BATTERED_KEY(ItemID.BATTERED_KEY),
    BONE_KEY(ItemID.BONE_KEY),
    BRASS_KEY(ItemID.BRASS_KEY),
    CRYSTALMINE_KEY(ItemID.CRYSTALMINE_KEY),
    DUSTY_KEY(ItemID.DUSTY_KEY),
    ENCHANTED_KEY(ItemID.ENCHANTED_KEY),
    WEAPON_STORE_KEY(ItemID.WEAPON_STORE_KEY),
    MAZE_KEY(ItemID.MAZE_KEY),
    METAL_KEY(ItemID.METAL_KEY),
    NEW_KEY(ItemID.NEW_KEY),
    SHINY_KEY(ItemID.SHINY_KEY),
    WROUGHT_IRON_KEY(ItemID.WROUGHT_IRON_KEY);

    private final int itemID;
    @Getter
    private final String location = "Steel Key Ring";

    SteelKeyRing(int itemID) {
        this.itemID = itemID;
    }
}
