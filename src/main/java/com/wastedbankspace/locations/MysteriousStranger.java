package com.wastedbankspace.locations;

import com.wastedbankspace.model.StorableItem;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum MysteriousStranger implements StorableItem {
    HALLOWED_GRAPPLE(ItemID.HALLOWED_GRAPPLE),
    HALLOWED_HAMMER(ItemID.HALLOWED_HAMMER),
    HALLOWED_FOCUS(ItemID.HALLOWED_FOCUS),
    HALLOWED_SYMBOL(ItemID.HALLOWED_SYMBOL),
    HALLOWED_RING(ItemID.HALLOWED_RING),
    HALLOWED_TOKEN(ItemID.HALLOWED_TOKEN);

    private final int itemID;
    @Getter
    private final String location = "Mysterious Stranger";

    MysteriousStranger(int itemID) {
        this.itemID = itemID;
    }
}
