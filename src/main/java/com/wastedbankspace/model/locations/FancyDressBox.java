
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

public class FancyDressBox implements StorageLocation {

    private static final Set<Integer> ITEMS = Set.of(
            ItemID.BEEKEEPERS_BOOTS,
            ItemID.BEEKEEPERS_GLOVES,
            ItemID.BEEKEEPERS_HAT,
            ItemID.BEEKEEPERS_LEGS,
            ItemID.BEEKEEPERS_TOP,
            ItemID.FROG_MASK,
            ItemID.ROYAL_FROG_LEGGINGS,
            ItemID.ROYAL_FROG_TUNIC,
            ItemID.ROYAL_FROG_BLOUSE,
            ItemID.ROYAL_FROG_SKIRT,
            ItemID.CAMO_BOTTOMS,
            ItemID.CAMO_HELMET,
            ItemID.CAMO_TOP,
            ItemID.LEDERHOSEN_HAT,
            ItemID.LEDERHOSEN_SHORTS,
            ItemID.LEDERHOSEN_TOP,
            ItemID.MIME_BOOTS,
            ItemID.MIME_GLOVES,
            ItemID.MIME_MASK,
            ItemID.MIME_LEGS,
            ItemID.MIME_TOP,
            ItemID.SHADE_ROBE,
            ItemID.SHADE_ROBE_TOP,
            ItemID.STALE_BAGUETTE,
            ItemID.ZOMBIE_BOOTS,
            ItemID.ZOMBIE_GLOVES,
            ItemID.ZOMBIE_MASK,
            ItemID.ZOMBIE_TROUSERS,
            ItemID.ZOMBIE_SHIRT
    );

    @Override
    public String getName() {
        return "Fancy Dress Box";
    }

    @Override
    public Set<Integer> getStorableItems() {
        return ITEMS;
    }
}
