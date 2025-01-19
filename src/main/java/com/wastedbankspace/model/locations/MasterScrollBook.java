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

public class MasterScrollBook implements StorageLocation {

    private static final Set<Integer> ITEMS = Set.of(
            ItemID.NARDAH_TELEPORT,
            ItemID.DIGSITE_TELEPORT,
            ItemID.FELDIP_HILLS_TELEPORT,
            ItemID.LUNAR_ISLE_TELEPORT,
            ItemID.MORTTON_TELEPORT,
            ItemID.PEST_CONTROL_TELEPORT,
            ItemID.PISCATORIS_TELEPORT,
            ItemID.TAI_BWO_WANNAI_TELEPORT,
            ItemID.IORWERTH_CAMP_TELEPORT,
            ItemID.MOS_LEHARMLESS_TELEPORT,
            ItemID.LUMBERYARD_TELEPORT,
            ItemID.ZULANDRA_TELEPORT,
            ItemID.KEY_MASTER_TELEPORT,
            ItemID.REVENANT_CAVE_TELEPORT,
            ItemID.WATSON_TELEPORT,
            ItemID.GUTHIXIAN_TEMPLE_TELEPORT,
            ItemID.SPIDER_CAVE_TELEPORT,
            ItemID.COLOSSAL_WYRM_TELEPORT_SCROLL
    );

    @Override
    public String getName() {
        return "Master Scroll Book";
    }

    @Override
    public Set<Integer> getStorableItems() {
        return ITEMS;
    }
}
