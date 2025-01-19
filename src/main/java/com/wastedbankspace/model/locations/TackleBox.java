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

public class TackleBox implements StorageLocation {

    private static final Set<Integer> ITEMS = Set.of(
            // Equipment
            ItemID.ANGLER_BOOTS,
            ItemID.ANGLER_HAT,
            ItemID.ANGLER_TOP,
            ItemID.ANGLER_WADERS,

            ItemID.SPIRIT_ANGLER_BOOTS,
            ItemID.SPIRIT_ANGLER_HEADBAND,
            ItemID.SPIRIT_ANGLER_TOP,
            ItemID.SPIRIT_ANGLER_WADERS,

            ItemID.FISHBOWL_HELMET,
            ItemID.FLIPPERS,
            ItemID.DARK_FLIPPERS,
            ItemID.DIVING_APPARATUS,

            ItemID.RADAS_BLESSING_1,
            ItemID.RADAS_BLESSING_2,
            ItemID.RADAS_BLESSING_3,
            ItemID.RADAS_BLESSING_4,

            ItemID.TINY_NET,
            ItemID.HARPOON,
            ItemID.BARBTAIL_HARPOON,
            ItemID.DRAGON_HARPOON,
            ItemID.INFERNAL_HARPOON,
            ItemID.DRAGON_HARPOON_OR,
            ItemID.INFERNAL_HARPOON_OR,
            ItemID.CRYSTAL_HARPOON,
            ItemID.MERFOLK_TRIDENT,
            ItemID.FISHING_ROD,
            ItemID.FLY_FISHING_ROD,
            ItemID.OILY_FISHING_ROD,
            ItemID.BARBARIAN_ROD,
            ItemID.PEARL_FISHING_ROD,
            ItemID.PEARL_FLY_FISHING_ROD,
            ItemID.OILY_PEARL_FISHING_ROD,
            ItemID.PEARL_BARBARIAN_ROD,
            ItemID.SMALL_FISHING_NET,
            ItemID.BIG_FISHING_NET,
            ItemID.DRIFT_NET,
            ItemID.LOBSTER_POT,
            ItemID.KARAMBWAN_VESSEL,
            ItemID.KARAMBWAN_VESSEL_3159,

            // Bait
            ItemID.SPIRIT_FLAKES,
            ItemID.FEATHER,
            ItemID.RAW_KARAMBWANJI,
            ItemID.FISHING_BAIT,
            ItemID.DARK_FISHING_BAIT,
            ItemID.SANDWORMS,
            ItemID.FISH_OFFCUTS,
            ItemID.FISH_CHUNKS,

            // Misc
            ItemID.FISHING_POTION1,
            ItemID.FISHING_POTION2,
            ItemID.FISHING_POTION3,
            ItemID.FISHING_POTION4,
            ItemID.MOLCH_PEARL
    );

    @Override
    public String getName() {
        return "Tackle Box";
    }

    @Override
    public Set<Integer> getStorableItems() {
        return ITEMS;
    }
}
