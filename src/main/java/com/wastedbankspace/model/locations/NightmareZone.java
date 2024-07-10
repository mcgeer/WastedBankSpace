package com.wastedbankspace.model.locations;

import com.wastedbankspace.model.StorableItem;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum NightmareZone implements StorableItem {
    ABSORPTION_1(ItemID.ABSORPTION_1),
    ABSORPTION_2(ItemID.ABSORPTION_2),
    ABSORPTION_3(ItemID.ABSORPTION_3),
    ABSORPTION_4(ItemID.ABSORPTION_4),
    OVERLOAD_1(ItemID.OVERLOAD_1),
    OVERLOAD_2(ItemID.OVERLOAD_2),
    OVERLOAD_3(ItemID.OVERLOAD_3),
    OVERLOAD_4(ItemID.OVERLOAD_4),
    SUPER_MAGIC_POTION_1(ItemID.SUPER_MAGIC_POTION_1),
    SUPER_MAGIC_POTION_2(ItemID.SUPER_MAGIC_POTION_2),
    SUPER_MAGIC_POTION_3(ItemID.SUPER_MAGIC_POTION_3),
    SUPER_MAGIC_POTION_4(ItemID.SUPER_MAGIC_POTION_4),
    SUPER_RANGING_1(ItemID.SUPER_RANGING_1),
    SUPER_RANGING_2(ItemID.SUPER_RANGING_2),
    SUPER_RANGING_3(ItemID.SUPER_RANGING_3),
    SUPER_RANGING_4(ItemID.SUPER_RANGING_4);

    private final int itemID;
    @Getter
    private final String location = "Nightmare Zone";

    NightmareZone(int itemID) {
        this.itemID = itemID;
    }
}
