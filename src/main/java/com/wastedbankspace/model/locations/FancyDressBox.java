
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
public enum FancyDressBox implements StorableItem {
    BEEKEEPERS_BOOTS(ItemID.BEEKEEPER_BOOTS),
    BEEKEEPERS_GLOVES(ItemID.BEEKEEPER_GLOVES),
    BEEKEEPERS_HAT(ItemID.BEEKEEPER_HAT),
    BEEKEEPERS_LEGS(ItemID.BEEKEEPER_LEGS),
    BEEKEEPERS_TOP(ItemID.BEEKEEPER_TOP),
    CAMO_BOTTOMS(ItemID.DRILL_BOTTOMS),
    CAMO_HELMET(ItemID.DRILL_HELM),
    CAMO_TOP(ItemID.DRILL_TOP),
    FROG_MASK(ItemID.MACRO_FROG_MASK),
    ROYAL_FROG_BLOUSE(ItemID.MACRO_PRINCESS_TORSO),
    ROYAL_FROG_LEGGINGS(ItemID.MACRO_PRINCE_LEGS),
    ROYAL_FROG_SKIRT(ItemID.MACRO_PRINCESS_LEGS),
    ROYAL_FROG_TUNIC(ItemID.MACRO_PRINCE_TORSO),
    LEDERHOSEN_HAT(ItemID.LADERHOSEN_HAT),
    LEDERHOSEN_SHORTS(ItemID.LADERHOSEN_LEGS),
    LEDERHOSEN_TOP(ItemID.LADERHOSEN_TOP),
    MIME_BOOTS(ItemID.MACRO_MIME_BOOTS),
    MIME_GLOVES(ItemID.MACRO_MIME_GLOVES),
    MIME_LEGS(ItemID.MACRO_MIME_LEGS),
    MIME_MASK(ItemID.MACRO_MIME_MASK),
    MIME_TOP(ItemID.MACRO_MIME_TOP),
    SHADE_ROBE(ItemID.BLACKROBEBOTTOM),
    SHADE_ROBE_TOP(ItemID.BLACKROBETOP),
    STALE_BAGUETTE(ItemID.STALE_BAGUETTE),
    ZOMBIE_BOOTS(ItemID.MACRO_DIGGER_BOOTS),
    ZOMBIE_GLOVES(ItemID.MACRO_DIGGER_GLOVES),
    ZOMBIE_MASK(ItemID.MACRO_DIGGER_MASK),
    ZOMBIE_SHIRT(ItemID.MACRO_DIGGER_SHIRT),
    ZOMBIE_TROUSERS(ItemID.MACRO_DIGGER_LEGS);

    private final int itemID;
    @Getter
    private final String location = "Fancy Dress Box";
    @Getter
    private final boolean isBis;

    FancyDressBox(int itemID) {
        this.itemID = itemID;
        this.isBis = false;
    }
}
