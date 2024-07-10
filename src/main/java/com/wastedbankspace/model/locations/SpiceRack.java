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
public enum SpiceRack implements StorableItem {
    ORANGE_SPICE_1(ItemID.ORANGE_SPICE_1),
    ORANGE_SPICE_2(ItemID.ORANGE_SPICE_2),
    ORANGE_SPICE_3(ItemID.ORANGE_SPICE_3),
    ORANGE_SPICE_4(ItemID.ORANGE_SPICE_4),
    RED_SPICE_1(ItemID.RED_SPICE_1),
    RED_SPICE_2(ItemID.RED_SPICE_2),
    RED_SPICE_3(ItemID.RED_SPICE_3),
    RED_SPICE_4(ItemID.RED_SPICE_4),
    YELLOW_SPICE_1(ItemID.YELLOW_SPICE_1),
    YELLOW_SPICE_2(ItemID.YELLOW_SPICE_2),
    YELLOW_SPICE_3(ItemID.YELLOW_SPICE_3),
    YELLOW_SPICE_4(ItemID.YELLOW_SPICE_4),
    BROWN_SPICE_1(ItemID.BROWN_SPICE_1),
    BROWN_SPICE_2(ItemID.BROWN_SPICE_2),
    BROWN_SPICE_3(ItemID.BROWN_SPICE_3),
    BROWN_SPICE_4(ItemID.BROWN_SPICE_4);

    private final int itemID;
    @Getter
    private final String location = "Spice Rack";

    SpiceRack(int itemID) {
        this.itemID = itemID;
    }
}
