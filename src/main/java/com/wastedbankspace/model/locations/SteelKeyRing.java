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
public enum SteelKeyRing implements StorableItem {
    ANCESTREAL_KEY(ItemID.ANCESTRAL_KEY),
    BATTERED_KEY(ItemID.BATTERED_KEY),
    BONE_KEY(ItemID.BONE_KEY),
    BRASS_KEY(ItemID.BRASS_KEY),
    CRYSTALMINE_KEY(ItemID.CRYSTALMINE_KEY),
    DUSTY_KEY(ItemID.DUSTY_KEY),
    ENCHANTED_KEY(ItemID.ENCHANTED_KEY),
    WEAPON_STORE_KEY(ItemID.WEAPON_STORE_KEY),
    MAZE_KEY(ItemID.MAZE_KEY),
    METAL_KEY(ItemID.METAL_KEY),
    NEW_KEY(ItemID.NEW_KEY),
    SHINY_KEY(ItemID.SHINY_KEY),
    WROUGHT_IRON_KEY(ItemID.WROUGHT_IRON_KEY);

    private final int itemID;
    @Getter
    private final String location = "Steel Key Ring";

    SteelKeyRing(int itemID) {
        this.itemID = itemID;
    }
}
