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

import com.wastedbankspace.model.StorageLocation;
import net.runelite.api.ItemID;

import java.util.Set;

public class ForestryKit implements StorageLocation {

    private static final Set<Integer> ITEMS = Set.of(
            ItemID.ANIMAINFUSED_BARK,
            ItemID.FORESTERS_RATION,
            ItemID.SECATEURS_ATTACHMENT,
            ItemID.NATURE_OFFERINGS,
            ItemID.WOODCUTTING_CAPE,
            ItemID.LUMBERJACK_TOP,
            ItemID.LUMBERJACK_HAT,
            ItemID.LUMBERJACK_BOOTS,
            ItemID.LUMBERJACK_LEGS,
            ItemID.FORESTRY_TOP,
            ItemID.FORESTRY_HAT,
            ItemID.FORESTRY_BOOTS,
            ItemID.FORESTRY_LEGS,
            ItemID.BEE_ON_A_STICK,
            ItemID.LEPRECHAUN_CHARM,
            ItemID.PADDED_SPOON,
            ItemID.PETAL_CIRCLET,
            ItemID.SMOKER_CANISTER,
            ItemID.TRAP_DISARMER,
            ItemID.MAGIC_LEAVES,
            ItemID.YEW_LEAVES,
            ItemID.MAPLE_LEAVES,
            ItemID.WILLOW_LEAVES,
            ItemID.OAK_LEAVES,
            ItemID.LEAVES
    );

    @Override
    public String getName() {
        return "Forestry Kit";
    }

    @Override
    public Set<Integer> getStorableItems() {
        return ITEMS;
    }
}
