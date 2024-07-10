package com.wastedbankspace.model.locations;

import com.wastedbankspace.model.StorableItem;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum SpiceRack implements StorableItem {
    ORANGE_SPICE_1(ItemID.ORANGE_SPICE_1),
    ORANGE_SPICE_2(ItemID.ORANGE_SPICE_2),
    ORANGE_SPICE_3(ItemID.ORANGE_SPICE_3),
    ORANGE_SPICE_4(ItemID.ORANGE_SPICE_4),
    RED_SPICE_1(ItemID.RED_SPICE_1),
    RED_SPICE_2(ItemID.RED_SPICE_2),
    RED_SPICE_3(ItemID.RED_SPICE_3),
    RED_SPICE_4(ItemID.RED_SPICE_4),
    YELLOW_SPICE_1(ItemID.YELLOW_SPICE_1),
    YELLOW_SPICE_2(ItemID.YELLOW_SPICE_2),
    YELLOW_SPICE_3(ItemID.YELLOW_SPICE_3),
    YELLOW_SPICE_4(ItemID.YELLOW_SPICE_4),
    BROWN_SPICE_1(ItemID.BROWN_SPICE_1),
    BROWN_SPICE_2(ItemID.BROWN_SPICE_2),
    BROWN_SPICE_3(ItemID.BROWN_SPICE_3),
    BROWN_SPICE_4(ItemID.BROWN_SPICE_4);

    private final int itemID;
    @Getter
    private final String location = "Spice Rack";

    SpiceRack(int itemID) {
        this.itemID = itemID;
    }
}
