/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2021, Riley McGee
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.wastedbankspace.model.locations;

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
