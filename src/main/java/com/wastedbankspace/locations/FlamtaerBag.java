package com.wastedbankspace.locations;

import com.wastedbankspace.model.StorableItem;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum FlamtaerBag implements StorableItem {
    TIMBER_BEAMS(ItemID.TIMBER_BEAM),
    LIMESTONE_BRICK(ItemID.LIMESTONE_BRICK),
    SWAMP_PASTE(ItemID.SWAMP_PASTE);

    private final int itemID;
    @Getter
    private final String location = "Flamtaer Bag";

    FlamtaerBag(int itemID) {
        this.itemID = itemID;
    }
}