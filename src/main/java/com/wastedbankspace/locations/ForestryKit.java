package com.wastedbankspace.locations;

import com.wastedbankspace.model.StorableItem;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum ForestryKit implements StorableItem {
    ANIMAINFUSED_BARK(ItemID.ANIMAINFUSED_BARK),
    FORESTERS_RATION(ItemID.FORESTERS_RATION),
    SECATEURS_ATTACHMENT(ItemID.SECATEURS_ATTACHMENT),
    NATURE_OFFERINGS(ItemID.NATURE_OFFERINGS),
    WOODCUTTING_CAPE(ItemID.WOODCUTTING_CAPE),
    LUMBERJACK_TOP(ItemID.LUMBERJACK_TOP),
    LUMBERJACK_HAT(ItemID.LUMBERJACK_HAT),
    LUMBERJACK_BOOTS(ItemID.LUMBERJACK_BOOTS),
    LUMBERJACK_LEGS(ItemID.LUMBERJACK_LEGS),
    FORESTRY_TOP(ItemID.FORESTRY_TOP),
    FORESTRY_HAT(ItemID.FORESTRY_HAT),
    FORESTRY_BOOTS(ItemID.FORESTRY_BOOTS),
    FORESTRY_LEGS(ItemID.FORESTRY_LEGS),
    BEE_ON_A_STICK(ItemID.BEE_ON_A_STICK),
    LEPRECHAUN_CHARM(ItemID.LEPRECHAUN_CHARM),
    PADDED_SPOON(ItemID.PADDED_SPOON),
    PETAL_CIRCLET(ItemID.PETAL_CIRCLET),
    SMOKER_CANISTER(ItemID.SMOKER_CANISTER),
    TRAP_DISARMER(ItemID.TRAP_DISARMER),
    MAGIC_LEAVES(ItemID.MAGIC_LEAVES),
    YEW_LEAVES(ItemID.YEW_LEAVES),
    MAPLE_LEAVES(ItemID.MAPLE_LEAVES),
    WILLOW_LEAVES(ItemID.WILLOW_LEAVES),
    OAK_LEAVES(ItemID.OAK_LEAVES),
    LEAVES(ItemID.LEAVES);

    private final int itemID;
    @Getter
    private final String location = "Forestry Kit";

    ForestryKit(int itemID) {
        this.itemID = itemID;
    }
}