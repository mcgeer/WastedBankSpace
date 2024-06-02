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

package com.wastedbankspace.model;

import lombok.Getter;

@Getter
public enum StorageLocation {
    TACKLE_BOX("Tackle Box"),
    STEEL_KEY_RING("Key Ring"),
    TOOL_LEP("Tool Lep"),
    MASTER_SCROLL_BOOK("Master Scroll Book"),
    FOSSIL_STORAGE("Fossil Storage"),
    BOLT_POUCH("Bolt Pouch"),
    PURO_PURO("Puro Puro"),
    FLAMTAER_BAG("Flamtaer Bag"),
    NIGHTMARE_ZONE("Nightmare Zone"),
    SEEDS("Seed Box/Vault"),
    OAK_TREASURE_CHEST("PoH Treasure Chest"),
    TEAK_TREASURE_CHEST("PoH Treasure Chest"),
    MAHOGANY_TREASURE_CHEST("PoH Treasure Chest"),
    FANCY_DRESS_BOX("PoH Fancy Dress Box"),
    MAGIC_WARDROBE("PoH Magic Wardrobe"),
    TOY_BOX("PoH Toy Box"),
    SPICE_RACK("PoH Spice rack"),
    FORESTRY_KIT("Forestry kit"),
    ARMOUR_CASE("PoH Armour Case"),
    MYSTERIOUS_STRANGER("Mysterious Stranger"),
    PET_HOUSE_SPACE("Pet House Space"),
    CAPE_RACK("PoH Cape rack"),
    HUNTSMANS_KIT("Huntsman's Kit")
    ;

    private final String uiRepresentation;

    StorageLocation(String uiRepresentation)
    {
        this.uiRepresentation = uiRepresentation;
    }

    @Override
    public String toString() {
        return this.uiRepresentation;
    }
}
