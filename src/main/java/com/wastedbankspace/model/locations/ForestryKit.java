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
import net.runelite.api.gameval.ItemID;

@Getter
public enum ForestryKit implements StorableItem {
    ANIMAINFUSED_BARK(ItemID.FORESTRY_CURRENCY),
    FORESTERS_RATION(ItemID.FORESTRY_RATION),
    SECATEURS_ATTACHMENT(ItemID.FORESTRY_SECATEURS_ATTACHMENT),
    NATURE_OFFERINGS(ItemID.NATURE_OFFERINGS),
    WOODCUTTING_CAPE(ItemID.SKILLCAPE_WOODCUTTING),
    LUMBERJACK_TOP(ItemID.RAMBLE_LUMBERJACK_TOP),
    LUMBERJACK_HAT(ItemID.RAMBLE_LUMBERJACK_HAT),
    LUMBERJACK_BOOTS(ItemID.RAMBLE_LUMBERJACK_BOOTS),
    LUMBERJACK_LEGS(ItemID.RAMBLE_LUMBERJACK_LEGS),
    FORESTRY_TOP(ItemID.FORESTRY_LUMBERJACK_TOP),
    FORESTRY_HAT(ItemID.FORESTRY_LUMBERJACK_HAT),
    FORESTRY_BOOTS(ItemID.FORESTRY_LUMBERJACK_BOOTS),
    FORESTRY_LEGS(ItemID.FORESTRY_LUMBERJACK_LEGS),
    BEE_ON_A_STICK(ItemID.GATHERING_EVENT_FLOWERING_TREE_BEESTICK),
    LEPRECHAUN_CHARM(ItemID.GATHERING_EVENT_LEPRECHAUN_CHARM),
    PADDED_SPOON(ItemID.GATHERING_EVENT_PHEASANT_EGG_SPOON),
    PETAL_CIRCLET(ItemID.GATHERING_EVENT_ENCHANTED_RITUAL_CIRCLET),
    SMOKER_CANISTER(ItemID.GATHERING_EVENT_BEES_SMOKERCANISTER),
    TRAP_DISARMER(ItemID.GATHERING_EVENT_POACHERS_DISARMER),
    MAGIC_LEAVES(ItemID.LEAVES_MAGIC),
    YEW_LEAVES(ItemID.LEAVES_YEW),
    MAPLE_LEAVES(ItemID.LEAVES_MAPLE),
    WILLOW_LEAVES(ItemID.LEAVES_WILLOW),
    OAK_LEAVES(ItemID.LEAVES_OAK),
    LEAVES(ItemID.LEAVES);

    private final int itemID;
    @Getter
    private final String location = "Forestry Kit";
    @Getter
    private final boolean isBis;

    ForestryKit(int itemID) {
        this.itemID = itemID;
        this.isBis = false;
    }
}
