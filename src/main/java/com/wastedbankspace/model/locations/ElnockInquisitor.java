package com.wastedbankspace.model.locations;

import com.wastedbankspace.model.StorableItem;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum ElnockInquisitor implements StorableItem {
    BUTTERFLY_NET(ItemID.BUTTERFLY_NET),
    MAGIC_BUTTERFLY_NET(ItemID.MAGIC_BUTTERFLY_NET),
    IMPLING_JAR(ItemID.IMPLING_JAR),
    IMP_REPELLENT(ItemID.IMP_REPELLENT);
    private final int itemID;
    @Getter
    private final String location = "Elnock Inquisitor";

    ElnockInquisitor(int itemID) {
        this.itemID = itemID;
    }
}