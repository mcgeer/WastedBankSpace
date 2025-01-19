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

public class FossilStorage implements StorageLocation {

    private final Set<Integer> ITEMS = Set.of(
            ItemID.UNIDENTIFIED_SMALL_FOSSIL,
            ItemID.SMALL_FOSSILISED_LIMBS,
            ItemID.SMALL_FOSSILISED_SPINE,
            ItemID.SMALL_FOSSILISED_RIBS,
            ItemID.SMALL_FOSSILISED_PELVIS,
            ItemID.SMALL_FOSSILISED_SKULL,
            ItemID.UNIDENTIFIED_MEDIUM_FOSSIL,
            ItemID.MEDIUM_FOSSILISED_LIMBS,
            ItemID.MEDIUM_FOSSILISED_SPINE,
            ItemID.MEDIUM_FOSSILISED_RIBS,
            ItemID.MEDIUM_FOSSILISED_PELVIS,
            ItemID.MEDIUM_FOSSILISED_SKULL,
            ItemID.UNIDENTIFIED_LARGE_FOSSIL,
            ItemID.LARGE_FOSSILISED_LIMBS,
            ItemID.LARGE_FOSSILISED_SPINE,
            ItemID.LARGE_FOSSILISED_RIBS,
            ItemID.LARGE_FOSSILISED_PELVIS,
            ItemID.LARGE_FOSSILISED_SKULL,
            ItemID.FOSSILISED_ROOTS,
            ItemID.FOSSILISED_STUMP,
            ItemID.FOSSILISED_BRANCH,
            ItemID.FOSSILISED_LEAF,
            ItemID.FOSSILISED_MUSHROOM,
            ItemID.UNIDENTIFIED_RARE_FOSSIL,
            ItemID.RARE_FOSSILISED_LIMBS,
            ItemID.RARE_FOSSILISED_PELVIS,
            ItemID.RARE_FOSSILISED_RIBS,
            ItemID.RARE_FOSSILISED_SKULL,
            ItemID.RARE_FOSSILISED_SPINE,
            ItemID.RARE_FOSSILISED_TUSK
    );

    @Override
    public String getName() {
        return "Fossil Storage";
    }

    @Override
    public Set<Integer> getStorableItems() {
        return ITEMS;
    }
}
