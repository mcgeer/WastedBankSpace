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

public class CapeRack implements StorageLocation {

    private static final Set<Integer> ITEMS = Set.of(
            ItemID.ACHIEVEMENT_DIARY_HOOD,
            ItemID.ACHIEVEMENT_DIARY_CAPE,
            ItemID.ACHIEVEMENT_DIARY_CAPE_T,
            ItemID.AGILITY_HOOD,
            ItemID.AGILITY_CAPE,
            ItemID.AGILITY_CAPET,
            ItemID.ATTACK_HOOD,
            ItemID.ATTACK_CAPE,
            ItemID.ATTACK_CAPET,
            ItemID.CHAMPIONS_CAPE,
            ItemID.CONSTRUCT_HOOD,
            ItemID.CONSTRUCT_CAPE,
            ItemID.CONSTRUCT_CAPET,
            ItemID.COOKING_HOOD,
            ItemID.COOKING_CAPE,
            ItemID.COOKING_CAPET,
            ItemID.CRAFTING_HOOD,
            ItemID.CRAFTING_CAPE,
            ItemID.CRAFTING_CAPET,
            ItemID.DEFENCE_HOOD,
            ItemID.DEFENCE_CAPE,
            ItemID.DEFENCE_CAPET,
            ItemID.FARMING_HOOD,
            ItemID.FARMING_CAPE,
            ItemID.FARMING_CAPET,
            ItemID.FIRE_CAPE,
            ItemID.FIREMAKING_HOOD,
            ItemID.FIREMAKING_CAPE,
            ItemID.FIREMAKING_CAPET,
            ItemID.FISHING_HOOD,
            ItemID.FISHING_CAPE,
            ItemID.FISHING_CAPET,
            ItemID.FLETCHING_HOOD,
            ItemID.FLETCHING_CAPE,
            ItemID.FLETCHING_CAPET,
            ItemID.GAUNTLET_CAPE,
            ItemID.HERBLORE_HOOD,
            ItemID.HERBLORE_CAPE,
            ItemID.HERBLORE_CAPET,
            ItemID.HITPOINTS_HOOD,
            ItemID.HITPOINTS_CAPE,
            ItemID.HITPOINTS_CAPET,
            ItemID.HUNTER_HOOD,
            ItemID.HUNTER_CAPE,
            ItemID.HUNTER_CAPET,
            ItemID.ICTHLARINS_SHROUD_TIER_1,
            ItemID.ICTHLARINS_SHROUD_TIER_2,
            ItemID.ICTHLARINS_SHROUD_TIER_3,
            ItemID.ICTHLARINS_SHROUD_TIER_4,
            ItemID.ICTHLARINS_SHROUD_TIER_5,
            ItemID.INFERNAL_CAPE,
            ItemID.CAPE_OF_LEGENDS,
            ItemID.GUTHIX_CAPE,
            ItemID.IMBUED_GUTHIX_CAPE,
            ItemID.SARADOMIN_CAPE,
            ItemID.IMBUED_SARADOMIN_CAPE,
            ItemID.ZAMORAK_CAPE,
            ItemID.IMBUED_ZAMORAK_CAPE,
            ItemID.MAGIC_HOOD,
            ItemID.MAGIC_CAPE,
            ItemID.MAGIC_CAPET,
            // MAX CAPES
            ItemID.MAX_HOOD,
            ItemID.MAX_CAPE,
            ItemID.ARDOUGNE_MAX_HOOD,
            ItemID.ARDOUGNE_MAX_CAPE,
            ItemID.ACCUMULATOR_MAX_HOOD,
            ItemID.ACCUMULATOR_MAX_CAPE,
            ItemID.ASSEMBLER_MAX_HOOD,
            ItemID.ASSEMBLER_MAX_CAPE,
            ItemID.DIZANAS_MAX_HOOD,
            ItemID.DIZANAS_MAX_CAPE,
            ItemID.FIRE_MAX_HOOD,
            ItemID.FIRE_MAX_CAPE,
            ItemID.INFERNAL_MAX_HOOD,
            ItemID.INFERNAL_MAX_CAPE,
            ItemID.IMBUED_GUTHIX_MAX_HOOD,
            ItemID.IMBUED_GUTHIX_MAX_CAPE,
            ItemID.IMBUED_SARADOMIN_MAX_HOOD,
            ItemID.IMBUED_SARADOMIN_MAX_CAPE,
            ItemID.IMBUED_ZAMORAK_MAX_HOOD,
            ItemID.IMBUED_ZAMORAK_MAX_CAPE,
            ItemID.MASORI_ASSEMBLER_MAX_HOOD,
            ItemID.MASORI_ASSEMBLER_MAX_CAPE,
            ItemID.MYTHICAL_MAX_HOOD,
            ItemID.MYTHICAL_MAX_CAPE,
            ItemID.GUTHIX_MAX_HOOD,
            ItemID.GUTHIX_MAX_CAPE,
            ItemID.SARADOMIN_MAX_HOOD,
            ItemID.SARADOMIN_MAX_CAPE,
            ItemID.ZAMORAK_MAX_HOOD,
            ItemID.ZAMORAK_MAX_CAPE,

            ItemID.MINING_HOOD,
            ItemID.MINING_CAPE,
            ItemID.MINING_CAPET,
            ItemID.MUSIC_HOOD,
            ItemID.MUSIC_CAPE,
            ItemID.MUSIC_CAPET,
            ItemID.MYTHICAL_CAPE,
            ItemID.OBSIDIAN_CAPE,
            ItemID.PRAYER_HOOD,
            ItemID.PRAYER_CAPE,
            ItemID.PRAYER_CAPET,
            ItemID.QUEST_POINT_HOOD,
            ItemID.QUEST_POINT_CAPE,
            ItemID.QUEST_POINT_CAPE_T,
            ItemID.RANGING_HOOD,
            ItemID.RANGING_CAPE,
            ItemID.RANGING_CAPET,
            ItemID.RUNECRAFT_HOOD,
            ItemID.RUNECRAFT_CAPE,
            ItemID.RUNECRAFT_CAPET,
            ItemID.SOUL_CAPE,
            ItemID.SINHAZA_SHROUD_TIER_1,
            ItemID.SINHAZA_SHROUD_TIER_2,
            ItemID.SINHAZA_SHROUD_TIER_3,
            ItemID.SINHAZA_SHROUD_TIER_4,
            ItemID.SINHAZA_SHROUD_TIER_5,
            ItemID.SLAYER_HOOD,
            ItemID.SLAYER_CAPE,
            ItemID.SLAYER_CAPET,
            ItemID.SMITHING_HOOD,
            ItemID.SMITHING_CAPE,
            ItemID.SMITHING_CAPET,
            ItemID.SPOTTED_CAPE,
            ItemID.SPOTTIER_CAPE,
            ItemID.STRENGTH_HOOD,
            ItemID.STRENGTH_CAPE,
            ItemID.STRENGTH_CAPET,
            ItemID.THIEVING_HOOD,
            ItemID.THIEVING_CAPE,
            ItemID.THIEVING_CAPET,
            ItemID.VICTORS_CAPE_1,
            ItemID.VICTORS_CAPE_10,
            ItemID.VICTORS_CAPE_50,
            ItemID.VICTORS_CAPE_100,
            ItemID.VICTORS_CAPE_500,
            ItemID.VICTORS_CAPE_1000,
            // TEAM CAPE
            ItemID.TEAM1_CAPE,
            ItemID.TEAM2_CAPE,
            ItemID.TEAM3_CAPE,
            ItemID.TEAM4_CAPE,
            ItemID.TEAM5_CAPE,
            ItemID.TEAM6_CAPE,
            ItemID.TEAM7_CAPE,
            ItemID.TEAM8_CAPE,
            ItemID.TEAM9_CAPE,
            ItemID.TEAM10_CAPE,
            ItemID.TEAM11_CAPE,
            ItemID.TEAM12_CAPE,
            ItemID.TEAM13_CAPE,
            ItemID.TEAM14_CAPE,
            ItemID.TEAM15_CAPE,
            ItemID.TEAM16_CAPE,
            ItemID.TEAM17_CAPE,
            ItemID.TEAM18_CAPE,
            ItemID.TEAM19_CAPE,
            ItemID.TEAM20_CAPE,
            ItemID.TEAM21_CAPE,
            ItemID.TEAM22_CAPE,
            ItemID.TEAM23_CAPE,
            ItemID.TEAM24_CAPE,
            ItemID.TEAM25_CAPE,
            ItemID.TEAM26_CAPE,
            ItemID.TEAM27_CAPE,
            ItemID.TEAM28_CAPE,
            ItemID.TEAM29_CAPE,
            ItemID.TEAM30_CAPE,
            ItemID.TEAM31_CAPE,
            ItemID.TEAM32_CAPE,
            ItemID.TEAM33_CAPE,
            ItemID.TEAM34_CAPE,
            ItemID.TEAM35_CAPE,
            ItemID.TEAM36_CAPE,
            ItemID.TEAM37_CAPE,
            ItemID.TEAM38_CAPE,
            ItemID.TEAM39_CAPE,
            ItemID.TEAM40_CAPE,
            ItemID.TEAM41_CAPE,
            ItemID.TEAM42_CAPE,
            ItemID.TEAM43_CAPE,
            ItemID.TEAM44_CAPE,
            ItemID.TEAM45_CAPE,
            ItemID.TEAM46_CAPE,
            ItemID.TEAM47_CAPE,
            ItemID.TEAM48_CAPE,
            ItemID.TEAM49_CAPE,
            ItemID.TEAM50_CAPE,

            ItemID.TEAM_CAPE_ZERO,
            ItemID.TEAM_CAPE_I,
            ItemID.TEAM_CAPE_X,

            ItemID.WOODCUTTING_HOOD,
            ItemID.WOODCUTTING_CAPE,
            ItemID.WOODCUT_CAPET,

            ItemID.XERICS_CHAMPION,
            ItemID.XERICS_GENERAL,
            ItemID.XERICS_GUARD,
            ItemID.XERICS_SENTINEL,
            ItemID.XERICS_WARRIOR
    );

    @Override
    public String getName() {
        return "Cape Rack";
    }

    @Override
    public Set<Integer> getStorableItems() {
        return ITEMS;
    }
}
