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
public enum NightmareZone implements StorableItem {
    ABSORPTION_1(ItemID.ABSORPTION_1),
    ABSORPTION_2(ItemID.ABSORPTION_2),
    ABSORPTION_3(ItemID.ABSORPTION_3),
    ABSORPTION_4(ItemID.ABSORPTION_4),
    OVERLOAD_1(ItemID.OVERLOAD_1),
    OVERLOAD_2(ItemID.OVERLOAD_2),
    OVERLOAD_3(ItemID.OVERLOAD_3),
    OVERLOAD_4(ItemID.OVERLOAD_4),
    SUPER_MAGIC_POTION_1(ItemID.SUPER_MAGIC_POTION_1),
    SUPER_MAGIC_POTION_2(ItemID.SUPER_MAGIC_POTION_2),
    SUPER_MAGIC_POTION_3(ItemID.SUPER_MAGIC_POTION_3),
    SUPER_MAGIC_POTION_4(ItemID.SUPER_MAGIC_POTION_4),
    SUPER_RANGING_1(ItemID.SUPER_RANGING_1),
    SUPER_RANGING_2(ItemID.SUPER_RANGING_2),
    SUPER_RANGING_3(ItemID.SUPER_RANGING_3),
    SUPER_RANGING_4(ItemID.SUPER_RANGING_4);

    private final int itemID;
    @Getter
    private final String location = "Nightmare Zone";
    @Getter
    private final boolean isBis;
    NightmareZone(int itemID) {
        this.itemID = itemID;
        this.isBis = false;
    }
}
