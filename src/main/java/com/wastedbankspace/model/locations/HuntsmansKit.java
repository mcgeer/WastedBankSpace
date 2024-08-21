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
public enum HuntsmansKit implements StorableItem {
    BIRD_SNARE(ItemID.BIRD_SNARE),
    NOOSE_WAND(ItemID.NOOSE_WAND),
    BUTTERFLY_NET_(ItemID.BUTTERFLY_NET),
    MAGIC_BUTTERFLY_NET_(ItemID.MAGIC_BUTTERFLY_NET),
    BUTTERFLY_JAR_(ItemID.BUTTERFLY_JAR),
    BOX_TRAP(ItemID.BOX_TRAP),
    RABBIT_SNARE(ItemID.RABBIT_SNARE),
    UNLIT_TORCH(ItemID.UNLIT_TORCH),
    SMALL_FISHING_NET_(ItemID.SMALL_FISHING_NET),
    ROPE(ItemID.ROPE),
    MAGIC_BOX(ItemID.MAGIC_BOX),
    IMPINABOX1(ItemID.IMPINABOX1),
    IMPINABOX2(ItemID.IMPINABOX2),
    TEASING_STICK(ItemID.TEASING_STICK),
    HUNTERS_SPEAR(ItemID.HUNTERS_SPEAR),
    WOOD_CAMO_TOP_(ItemID.WOOD_CAMO_TOP),
    WOOD_CAMO_LEGS_(ItemID.WOOD_CAMO_LEGS),
    POLAR_CAMO_TOP_(ItemID.POLAR_CAMO_TOP),
    POLAR_CAMO_LEGS_(ItemID.POLAR_CAMO_LEGS),
    JUNGLE_CAMO_TOP_(ItemID.JUNGLE_CAMO_TOP),
    JUNGLE_CAMO_LEGS_(ItemID.JUNGLE_CAMO_LEGS),
    DESERT_CAMO_TOP_(ItemID.DESERT_CAMO_TOP),
    DESERT_CAMO_LEGS_(ItemID.DESERT_CAMO_LEGS),
    LARUPIA_HAT_(ItemID.LARUPIA_HAT),
    LARUPIA_TOP_(ItemID.LARUPIA_TOP),
    LARUPIA_LEGS_(ItemID.LARUPIA_LEGS),
    GRAAKH_HEADDRESS(ItemID.GRAAHK_HEADDRESS),
    GRAAKH_TOP(ItemID.GRAAHK_TOP),
    GRAAKH_LEGS(ItemID.GRAAHK_LEGS),
    KYATT_HAT_(ItemID.KYATT_HAT),
    KYATT_TOP_(ItemID.KYATT_TOP),
    KYATT_LEGS_(ItemID.KYATT_LEGS),
    HUNTER_HOOD(ItemID.HUNTER_HOOD),
    HUNTER_CAPE(ItemID.HUNTER_CAPE),
    HUNTER_CAPET(ItemID.HUNTER_CAPET),
    GUILD_HUNTER_HEADWEAR(ItemID.GUILD_HUNTER_HEADWEAR),
    GUILD_HUNTER_TOP(ItemID.GUILD_HUNTER_TOP),
    GUILD_HUNTER_LEGS(ItemID.GUILD_HUNTER_LEGS),
    GUILD_HUNTER_BOOTS(ItemID.GUILD_HUNTER_BOOTS),
    RING_OF_PURSUIT(ItemID.RING_OF_PURSUIT),
    IMPLING_JAR_(ItemID.IMPLING_JAR);

    private final int itemID;
    @Getter
    private final String location = "Huntsman's Kit";
    @Getter
    private final boolean isBis;

    HuntsmansKit(int itemID) {
        this.itemID = itemID;
        this.isBis = false;
    }
}
