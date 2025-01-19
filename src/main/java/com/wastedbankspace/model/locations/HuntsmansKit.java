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

public class HuntsmansKit implements StorageLocation {

    private static final Set<Integer> ITEMS = Set.of(
            ItemID.BIRD_SNARE,
            ItemID.NOOSE_WAND,
            ItemID.BUTTERFLY_NET,
            ItemID.MAGIC_BUTTERFLY_NET,
            ItemID.BUTTERFLY_JAR,
            ItemID.BOX_TRAP,
            ItemID.RABBIT_SNARE,
            ItemID.UNLIT_TORCH,
            ItemID.SMALL_FISHING_NET,
            ItemID.ROPE,
            ItemID.MAGIC_BOX,
            ItemID.IMPINABOX1,
            ItemID.IMPINABOX2,
            ItemID.TEASING_STICK,
            ItemID.HUNTERS_SPEAR,
            ItemID.WOOD_CAMO_TOP,
            ItemID.WOOD_CAMO_LEGS,
            ItemID.POLAR_CAMO_TOP,
            ItemID.POLAR_CAMO_LEGS,
            ItemID.JUNGLE_CAMO_TOP,
            ItemID.JUNGLE_CAMO_LEGS,
            ItemID.DESERT_CAMO_TOP,
            ItemID.DESERT_CAMO_LEGS,
            ItemID.LARUPIA_HAT,
            ItemID.LARUPIA_TOP,
            ItemID.LARUPIA_LEGS,
            ItemID.GRAAHK_HEADDRESS,
            ItemID.GRAAHK_TOP,
            ItemID.GRAAHK_LEGS,
            ItemID.KYATT_HAT,
            ItemID.KYATT_TOP,
            ItemID.KYATT_LEGS,
            ItemID.HUNTER_HOOD,
            ItemID.HUNTER_CAPE,
            ItemID.HUNTER_CAPET,
            ItemID.GUILD_HUNTER_HEADWEAR,
            ItemID.GUILD_HUNTER_TOP,
            ItemID.GUILD_HUNTER_LEGS,
            ItemID.GUILD_HUNTER_BOOTS,
            ItemID.RING_OF_PURSUIT,
            ItemID.IMPLING_JAR
    );

    @Override
    public String getName() {
        return "Huntsman's Kit";
    }

    @Override
    public Set<Integer> getStorableItems() {
        return ITEMS;
    }
}
