
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
public enum FancyDressBox implements StorableItem {
    BEEKEEPERS_BOOTS(ItemID.BEEKEEPERS_BOOTS),
    BEEKEEPERS_GLOVES(ItemID.BEEKEEPERS_GLOVES),
    BEEKEEPERS_HAT(ItemID.BEEKEEPERS_HAT),
    BEEKEEPERS_LEGS(ItemID.BEEKEEPERS_LEGS),
    BEEKEEPERS_TOP(ItemID.BEEKEEPERS_TOP),
    CAMO_BOTTOMS(ItemID.CAMO_BOTTOMS),
    CAMO_HELMET(ItemID.CAMO_HELMET),
    CAMO_TOP(ItemID.CAMO_TOP),
    FROG_MASK(ItemID.FROG_MASK),
    ROYAL_FROG_BLOUSE(ItemID.ROYAL_FROG_BLOUSE),
    ROYAL_FROG_LEGGINGS(ItemID.ROYAL_FROG_LEGGINGS),
    ROYAL_FROG_SKIRT(ItemID.ROYAL_FROG_SKIRT),
    ROYAL_FROG_TUNIC(ItemID.ROYAL_FROG_TUNIC),
    LEDERHOSEN_HAT(ItemID.LEDERHOSEN_HAT),
    LEDERHOSEN_SHORTS(ItemID.LEDERHOSEN_SHORTS),
    LEDERHOSEN_TOP(ItemID.LEDERHOSEN_TOP),
    MIME_BOOTS(ItemID.MIME_BOOTS),
    MIME_GLOVES(ItemID.MIME_GLOVES),
    MIME_LEGS(ItemID.MIME_LEGS),
    MIME_MASK(ItemID.MIME_MASK),
    MIME_TOP(ItemID.MIME_TOP),
    SHADE_ROBE(ItemID.SHADE_ROBE),
    SHADE_ROBE_TOP(ItemID.SHADE_ROBE_TOP),
    STALE_BAGUETTE(ItemID.STALE_BAGUETTE),
    ZOMBIE_BOOTS(ItemID.ZOMBIE_BOOTS),
    ZOMBIE_GLOVES(ItemID.ZOMBIE_GLOVES),
    ZOMBIE_MASK(ItemID.ZOMBIE_MASK),
    ZOMBIE_SHIRT(ItemID.ZOMBIE_SHIRT),
    ZOMBIE_TROUSERS(ItemID.ZOMBIE_TROUSERS);

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
