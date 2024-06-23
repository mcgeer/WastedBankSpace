package com.wastedbankspace.locations;

import com.wastedbankspace.model.StorableItem;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum ToolLeprechaun implements StorableItem {
    RAKE(ItemID.RAKE),
    SPADE(ItemID.SPADE),
    SEED_DIBBER(ItemID.SEED_DIBBER),
    SECATEURS(ItemID.SECATEURS),
    MAGIC_SECATEURS(ItemID.MAGIC_SECATEURS),
    GARDENING_TROWEL(ItemID.GARDENING_TROWEL),
    BOTOMLESS_COMPOST(ItemID.BOTTOMLESS_COMPOST_BUCKET),
    BOTTOMLESS_COMPOST_BUCKET_22997(ItemID.BOTTOMLESS_COMPOST_BUCKET_22997),
    PLANT_CURE(ItemID.PLANT_CURE),
    PLANT_CURE_6468(ItemID.PLANT_CURE_6468),
    COMPOST(ItemID.COMPOST),
    SUPERCOMPOST(ItemID.SUPERCOMPOST),
    ULTRACOMPOST(ItemID.ULTRACOMPOST),
    WATERING_CAN(ItemID.WATERING_CAN),
    WATERING_CAN1(ItemID.WATERING_CAN1),
    WATERING_CAN2(ItemID.WATERING_CAN2),
    WATERING_CAN3(ItemID.WATERING_CAN3),
    WATERING_CAN4(ItemID.WATERING_CAN4),
    WATERING_CAN5(ItemID.WATERING_CAN5),
    WATERING_CAN6(ItemID.WATERING_CAN6),
    WATERING_CAN7(ItemID.WATERING_CAN7),
    WATERING_CAN8(ItemID.WATERING_CAN8),
    GRICOLLERS_CAN(ItemID.GRICOLLERS_CAN);

    private final int itemID;
    @Getter
    private final String location = "Tool Leprechaun";

    ToolLeprechaun(int itemID) {
        this.itemID = itemID;
    }
}
