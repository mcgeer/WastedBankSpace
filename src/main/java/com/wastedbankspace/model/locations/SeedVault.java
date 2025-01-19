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

public class SeedVault implements StorageLocation {

    private static final Set<Integer> ITEMS = Set.of(
            // Allotment Seeds
            ItemID.POTATO_SEED,
            ItemID.ONION_SEED,
            ItemID.CABBAGE_SEED,
            ItemID.TOMATO_SEED,
            ItemID.SWEETCORN_SEED,
            ItemID.STRAWBERRY_SEED,
            ItemID.WATERMELON_SEED,
            ItemID.SNAPE_GRASS_SEED,

            // Flower Seeds
            ItemID.MARIGOLD_SEED,
            ItemID.ROSEMARY_SEED,
            ItemID.NASTURTIUM_SEED,
            ItemID.WOAD_SEED,
            ItemID.LIMPWURT_SEED,
            ItemID.WHITE_LILY_SEED,

            // Herb Seeds
            ItemID.GUAM_SEED,
            ItemID.MARRENTILL_SEED,
            ItemID.TARROMIN_SEED,
            ItemID.HARRALANDER_SEED,
            ItemID.RANARR_SEED,
            ItemID.TOADFLAX_SEED,
            ItemID.IRIT_SEED,
            ItemID.AVANTOE_SEED,
            ItemID.KWUARM_SEED,
            ItemID.SNAPDRAGON_SEED,
            ItemID.CADANTINE_SEED,
            ItemID.LANTADYME_SEED,
            ItemID.DWARF_WEED_SEED,
            ItemID.TORSTOL_SEED,

            // Hop Seeds
            ItemID.BARLEY_SEED,
            ItemID.HAMMERSTONE_SEED,
            ItemID.ASGARNIAN_SEED,
            ItemID.JUTE_SEED,
            ItemID.YANILLIAN_SEED,
            ItemID.KRANDORIAN_SEED,
            ItemID.WILDBLOOD_SEED,

            // Bush Seeds
            ItemID.REDBERRY_SEED,
            ItemID.CADAVABERRY_SEED,
            ItemID.DWELLBERRY_SEED,
            ItemID.JANGERBERRY_SEED,
            ItemID.WHITEBERRY_SEED,
            ItemID.POISON_IVY_SEED,

            // Tree Seeds
            ItemID.ACORN,
            ItemID.WILLOW_SEED,
            ItemID.TEAK_SEED,
            ItemID.MAPLE_SEED,
            ItemID.MAHOGANY_SEED,
            ItemID.YEW_SEED,
            ItemID.MAGIC_SEED,
            ItemID.REDWOOD_TREE_SEED,

            // Fruit Tree Seeds
            ItemID.APPLE_TREE_SEED,
            ItemID.BANANA_TREE_SEED,
            ItemID.ORANGE_TREE_SEED,
            ItemID.CURRY_TREE_SEED,
            ItemID.PINEAPPLE_SEED,
            ItemID.PAPAYA_TREE_SEED,
            ItemID.PALM_TREE_SEED,
            ItemID.DRAGONFRUIT_TREE_SEED,

            // Exotic Seeds
            ItemID.SEAWEED_SPORE,
            ItemID.GRAPE_SEED,
            ItemID.MUSHROOM_SPORE,
            ItemID.CACTUS_SEED,
            ItemID.BELLADONNA_SEED,
            ItemID.POTATO_CACTUS_SEED,
            ItemID.HESPORI_SEED,
            ItemID.CALQUAT_TREE_SEED,
            ItemID.SPIRIT_SEED,
            ItemID.CELASTRUS_SEED,
            ItemID.CRYSTAL_ACORN,

            // Anima Seeds
            ItemID.ATTAS_SEED,
            ItemID.IASOR_SEED,
            ItemID.KRONOS_SEED,

            // Saplings
            ItemID.OAK_SAPLING,
            ItemID.APPLE_SAPLING,
            ItemID.WILLOW_SAPLING,
            ItemID.BANANA_SAPLING,
            ItemID.TEAK_SAPLING,
            ItemID.ORANGE_SAPLING,
            ItemID.CURRY_SAPLING,
            ItemID.MAPLE_SAPLING,
            ItemID.PINEAPPLE_SAPLING,
            ItemID.MAHOGANY_SAPLING,
            ItemID.PAPAYA_SAPLING,
            ItemID.YEW_SAPLING,
            ItemID.PALM_SAPLING,
            ItemID.CALQUAT_SAPLING,
            ItemID.MAGIC_SAPLING,
            ItemID.DRAGONFRUIT_SAPLING,
            ItemID.SPIRIT_SAPLING,
            ItemID.CELASTRUS_SAPLING,
            ItemID.REDWOOD_SAPLING,
            ItemID.CRYSTAL_SAPLING
    );

    @Override
    public String getName() {
        return "Seed vault";
    }

    @Override
    public Set<Integer> getStorableItems() {
        return ITEMS;
    }
}
