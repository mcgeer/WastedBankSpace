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

public class Bookcase implements StorageLocation {

    private static final Set<Integer> ITEMS = Set.of(
            ItemID.CONSTRUCTION_GUIDE,
            ItemID.GAME_BOOK,
            ItemID.SECURITY_BOOK,
            ItemID.STRONGHOLD_NOTES,
            ItemID.ABYSSAL_BOOK,
            ItemID.GIANNES_COOK_BOOK,
            ItemID.COCKTAIL_GUIDE,
            ItemID.QUEEN_HELP_BOOK,
            ItemID.DIARY,
            ItemID.INSTRUCTION_MANUAL,
            ItemID.BOOK_ON_CHEMICALS,
            ItemID.BATTERED_BOOK,
            ItemID.VARMENS_NOTES,
            ItemID.GOBLIN_SYMBOL_BOOK,
            ItemID.TRANSLATION,
            ItemID.TRANSLATION_BOOK,
            ItemID.GLOUGHS_JOURNAL,
            ItemID.JOURNAL_3845,
            ItemID.DIARY_3846,
            ItemID.MANUAL,
            ItemID.SHAMANS_TOME,
            ItemID.BINDING_BOOK,
            ItemID.GHRIMS_BOOK,
            ItemID.ASTRONOMY_BOOK,
            ItemID.OLD_JOURNAL,
            ItemID.HISTORY_OF_IBAN,
            ItemID.BIG_BOOK_OF_BANGS,
            ItemID.CADARN_LINEAGE,
            ItemID.CRYSTAL_SINGING_FOR_BEGINNERS,
            ItemID.DIARY_3395,
            ItemID.BOOK,
            ItemID.GUIDE_BOOK,
            ItemID.BOOK_ON_BAXTORIAN,
            ItemID.DWARVEN_LORE,
            ItemID.EMBALMING_MANUAL,
            ItemID.DEMONIC_TOME,
            ItemID.CRUMBLING_TOME,
            ItemID.BOOK_6767,
            ItemID.EDERNS_JOURNAL,
            ItemID.PRIFDDINAS_HISTORY,
            ItemID.EASTERN_DISCOVERY,
            ItemID.EASTERN_SETTLEMENT,
            ItemID.THE_GREAT_DIVIDE,
            ItemID.JOURNAL_6755,
            ItemID.MOONCLAN_MANUAL,
            ItemID.HERMANS_BOOK,
            ItemID.PIE_RECIPE_BOOK,
            ItemID.BOOK_O_PIRACY,
            ItemID.BREWIN_GUIDE,
            ItemID.BURNT_DIARY,
            ItemID.ARENA_BOOK,
            ItemID.SINKETHS_DIARY,
            ItemID.DAGONHAI_HISTORY,
            ItemID.TARNS_DIARY,
            ItemID.BIRD_BOOK,
            ItemID.FEATHERED_JOURNAL,
            ItemID.FARMING_MANUAL,
            ItemID.PRAYER_BOOK,
            ItemID.BEATEN_BOOK,
            ItemID.A_HANDWRITTEN_BOOK,
            ItemID.HISTORIES_OF_THE_HALLOWLAND,
            ItemID.MODERN_DAY_MORYTANIA,
            ItemID.THE_SLEEPING_SEVEN,
            ItemID.CLOCKWORK_BOOK,
            ItemID.GOBLIN_BOOK,
            ItemID.MY_NOTES,
            ItemID.EXPLORERS_NOTES,
            ItemID.GLASSBLOWING_BOOK,
            ItemID.ENTOMOLOGISTS_DIARY,
            ItemID.OHNS_DIARY,
            ItemID.OVERSEERS_BOOK,
            ItemID.BOOK_OF_SPYOLOGY,
            ItemID.CREATURE_KEEPERS_JOURNAL,
            ItemID.NISTIRIOS_MANIFESTO,
            ItemID.TEKTONS_JOURNAL,
            ItemID.TRANSDIMENSIONAL_NOTES,
            ItemID.VANGUARD_JUDGEMENT,
            ItemID.DARK_JOURNAL,
            ItemID.HOUNDMASTERS_DIARY,
            ItemID.FOSSIL_ISLAND_NOTE_BOOK,
            ItemID.ARCHAEOLOGISTS_DIARY,
            ItemID.ANCIENT_DIARY,
            ItemID.VARLAMORE_ENVOY,
            ItemID.AIVAS_DIARY,
            ItemID.MALUMACS_JOURNAL,
            ItemID.UNGAEL_LAB_NOTES,
            ItemID.LITHKREN_VAULT_NOTES,
            ItemID.ABLENKIANS_ESCAPE,
            ItemID.IMCANDORIAS_FALL,
            ItemID.IMAFORES_BETRAYAL,
            ItemID.LUTWIDGE_AND_THE_MOONFLY,
            ItemID.SERAFINA,
            ItemID.THE_WEEPING,
            ItemID.OLD_DIARY,
            ItemID.SERAFINAS_DIARY,
            ItemID.THE_BUTCHER,
            ItemID.ARACHNIDS_OF_VAMPYRIUM,
            ItemID.THE_SHADOW_REALM,
            ItemID.THE_WILD_HUNT,
            ItemID.VERZIK_VITUR__PATIENT_RECORD,
            ItemID.WEISS_FIRE_NOTES,
            ItemID.COLLECTION_LOG,
            ItemID.GIELINORS_FLORA__FLOWERS,
            ItemID.GIELINORS_FLORA__BUSHES,
            ItemID.GIELINORS_FLORA__HOPS,
            ItemID.GIELINORS_FLORA__ALLOTMENTS,
            ItemID.GIELINORS_FLORA__HERBS,
            ItemID.GIELINORS_FLORA__TREES,
            ItemID.GIELINORS_FLORA__FRUIT,
            ItemID.TOME_OF_THE_SUN,
            ItemID.TOME_OF_THE_MOON,
            ItemID.TOME_OF_THE_TEMPLE,
            ItemID.ODE_TO_ETERNITY,
            ItemID.CRAZED_SCRIBBLES,
            ItemID.A_DEAR_FRIEND,
            ItemID.ON_LEPRECHAUNS,
            ItemID.BLOODY_DIARY,
            ItemID.THE_EIGHT_CLANS,
            ItemID.GOLLWYNS_FINAL_STATEMENT,
            ItemID.NIFF__HARRY,
            ItemID.SOGGY_JOURNAL,
            ItemID.EBRILLS_JOURNAL,
            ItemID.STAINED_JOURNAL,
            ItemID.THE_TRUTH_BEHIND_THE_MYTH_EXCERPT,
            ItemID.THE_LIVING_STATUES,
            ItemID.THE_SPURNED_DEMON,
            ItemID.LEGENDS_OF_THE_MOUNTAIN,
            ItemID.HARMONY,
            ItemID.BALLAD_OF_THE_BASILISK,
            ItemID.A_TASTE_OF_HOPE,
            ItemID.STRANGE_HALLOWED_TOME,
            ItemID.NEILANS_JOURNAL,
            ItemID.SOUL_WARS_GUIDE,
            ItemID.TREE_WIZARDS_JOURNAL,
            ItemID.BLOODY_NOTES,
            ItemID.THE_DESERT_TROUT__SHIPS_LOG,
            ItemID.SPIRIT_ANGLERS_RESEARCH_NOTES,
            ItemID.MOUNT_KARUULM_DIARY,
            ItemID.ATLAXS_DIARY,
            ItemID.AKILAS_JOURNAL,
            ItemID.HETS_CAPTURE,
            ItemID.APMEKENS_CAPTURE,
            ItemID.SCABARAS_CAPTURE,
            ItemID.CRONDIS_CAPTURE,
            ItemID.THE_WARDENS,
            ItemID.THE_JACKALS_TORCH,
            ItemID.KASONDES_JOURNAL,
            ItemID.YEWNOCKS_NOTES,
            ItemID.MOVARIOS_NOTES_VOLUME_1,
            ItemID.MOVARIOS_NOTES_VOLUME_2,
            ItemID.THE_HUEYCOATL,
            ItemID.MOONRISE_WINES
    );

    @Override
    public String getName() {
        return "Bookcase";
    }

    @Override
    public Set<Integer> getStorableItems() {
        return ITEMS;
    }
}
