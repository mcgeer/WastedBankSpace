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

public class PetHouse implements StorageLocation {

    private static final Set<Integer> ITEMS = Set.of(
            ItemID.PET_CHAOS_ELEMENTAL,
            ItemID.PET_DAGANNOTH_SUPREME,
            ItemID.PET_DAGANNOTH_PRIME,
            ItemID.PET_DAGANNOTH_REX,
            ItemID.PET_PENANCE_QUEEN,
            ItemID.PET_KREEARRA,
            ItemID.PET_GENERAL_GRAARDOR,
            ItemID.PET_ZILYANA,
            ItemID.PET_KRIL_TSUTSAROTH,
            ItemID.BABY_MOLE,
            ItemID.PRINCE_BLACK_DRAGON,
            ItemID.KALPHITE_PRINCESS,
            ItemID.PET_SMOKE_DEVIL,
            ItemID.PET_KRAKEN,
            ItemID.PET_DARK_CORE,
            ItemID.PET_SNAKELING,
            ItemID.CHOMPY_CHICK,
            ItemID.VENENATIS_SPIDERLING,
            ItemID.CALLISTO_CUB,
            ItemID.CALLISTO_CUB_27649,
            ItemID.VETION_JR,
            ItemID.SCORPIAS_OFFSPRING,
            ItemID.TZREKJAD,
            ItemID.HELLPUPPY,
            ItemID.ABYSSAL_ORPHAN,
            ItemID.HERON,
            ItemID.ROCK_GOLEM,
            ItemID.BEAVER,
            ItemID.BABY_CHINCHOMPA,
            ItemID.BLOODHOUND,
            ItemID.GIANT_SQUIRREL,
            ItemID.TANGLEROOT,
            ItemID.RIFT_GUARDIAN,
            ItemID.ROCKY,
            ItemID.PHOENIX,
            ItemID.OLMLET,
            ItemID.SKOTOS,
            ItemID.JALNIBREK,
            ItemID.HERBI,
            ItemID.NOON,
            ItemID.VORKI,
            ItemID.LIL_ZIK,
            ItemID.IKKLE_HYDRA,
            ItemID.SRARACHA,
            ItemID.YOUNGLLEF,
            ItemID.SMOLCANO,
            ItemID.LITTLE_NIGHTMARE,
            ItemID.LIL_CREATOR,
            ItemID.TINY_TEMPOR,
            ItemID.NEXLING,
            ItemID.ABYSSAL_PROTECTOR,
            ItemID.TUMEKENS_GUARDIAN,
            ItemID.MUPHIN,
            ItemID.WISP,
            ItemID.BUTCH,
            ItemID.LILVIATHAN,
            ItemID.BARON,
            ItemID.SCURRY,
            ItemID.SMOL_HEREDIT,
            ItemID.QUETZIN,
            ItemID.NID,
            ItemID.HUBERTE,
            ItemID.MOXI
    );

    @Override
    public String getName() {
        return "Pet House";
    }

    @Override
    public Set<Integer> getStorableItems() {
        return ITEMS;
    }
}
