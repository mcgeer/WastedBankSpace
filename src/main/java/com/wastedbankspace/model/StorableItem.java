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
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum StorableItem {
    /**
     * TACKLE BOX
     */
    //Equipment
    ANGLER_BOOTS(ItemID.ANGLER_BOOTS, StorageLocation.TACKLE_BOX),
    ANGLER_HAT(ItemID.ANGLER_HAT, StorageLocation.TACKLE_BOX),
    ANGLER_TOP(ItemID.ANGLER_TOP, StorageLocation.TACKLE_BOX),
    ANGLER_WADERS(ItemID.ANGLER_WADERS, StorageLocation.TACKLE_BOX),

    SPIRIT_ANGLER_BOOTS(ItemID.SPIRIT_ANGLER_BOOTS, StorageLocation.TACKLE_BOX),
    SPIRIT_ANGLER_HEADBAND(ItemID.SPIRIT_ANGLER_HEADBAND, StorageLocation.TACKLE_BOX),
    SPIRIT_ANGLER_TOP(ItemID.SPIRIT_ANGLER_TOP, StorageLocation.TACKLE_BOX),
    SPIRIT_ANGLER_WADERS(ItemID.SPIRIT_ANGLER_WADERS, StorageLocation.TACKLE_BOX),

    FISHBOWL_HELMET(ItemID.FISHBOWL_HELMET, StorageLocation.TACKLE_BOX),
    FLIPPERS(ItemID.FLIPPERS, StorageLocation.TACKLE_BOX),
    DARK_FLIPPERS(ItemID.DARK_FLIPPERS, StorageLocation.TACKLE_BOX),
    DIVING_APPARATUS(ItemID.DIVING_APPARATUS, StorageLocation.TACKLE_BOX),

    RADAS_BLESSING_1(ItemID.RADAS_BLESSING_1, StorageLocation.TACKLE_BOX),
    RADAS_BLESSING_2(ItemID.RADAS_BLESSING_2, StorageLocation.TACKLE_BOX),
    RADAS_BLESSING_3(ItemID.RADAS_BLESSING_3, StorageLocation.TACKLE_BOX),
    RADAS_BLESSING_4(ItemID.RADAS_BLESSING_4, StorageLocation.TACKLE_BOX),

    TINY_NET(ItemID.TINY_NET, StorageLocation.TACKLE_BOX),
    HARPOON(ItemID.HARPOON, StorageLocation.TACKLE_BOX),
    BARBTAIL_HARPOON(ItemID.BARBTAIL_HARPOON, StorageLocation.TACKLE_BOX),
    DRAGON_HARPOON(ItemID.DRAGON_HARPOON, StorageLocation.TACKLE_BOX),
    INFERNAL_HARPOON(ItemID.INFERNAL_HARPOON, StorageLocation.TACKLE_BOX),
    DRAGON_HARPOON_OR(ItemID.DRAGON_HARPOON_OR, StorageLocation.TACKLE_BOX),
    INFERNAL_HARPOON_OR(ItemID.INFERNAL_HARPOON_OR, StorageLocation.TACKLE_BOX),
    CRYSTAL_HARPOON(ItemID.CRYSTAL_HARPOON, StorageLocation.TACKLE_BOX),
    MERFOLK_TRIDENT(ItemID.MERFOLK_TRIDENT, StorageLocation.TACKLE_BOX),
    FISHING_ROD(ItemID.FISHING_ROD, StorageLocation.TACKLE_BOX),
    FLY_FISHING_ROD(ItemID.FLY_FISHING_ROD, StorageLocation.TACKLE_BOX),
    OILY_FISHING_ROD(ItemID.OILY_FISHING_ROD, StorageLocation.TACKLE_BOX),
    BARBARIAN_ROD(ItemID.BARBARIAN_ROD, StorageLocation.TACKLE_BOX),
    PEARL_FISHING_ROD(ItemID.PEARL_FISHING_ROD, StorageLocation.TACKLE_BOX),
    PEARL_FLY_FISHING_ROD(ItemID.PEARL_FLY_FISHING_ROD, StorageLocation.TACKLE_BOX),
    OILY_PEARL_FISHING_ROD(ItemID.OILY_PEARL_FISHING_ROD, StorageLocation.TACKLE_BOX),
    PEARL_BARBARIAN_ROD(ItemID.PEARL_BARBARIAN_ROD, StorageLocation.TACKLE_BOX),
    SMALL_FISHING_NET(ItemID.SMALL_FISHING_NET, StorageLocation.TACKLE_BOX),
    BIG_FISHING_NET(ItemID.BIG_FISHING_NET, StorageLocation.TACKLE_BOX),
    DRIFT_NET(ItemID.DRIFT_NET, StorageLocation.TACKLE_BOX),
    LOBSTER_POT(ItemID.LOBSTER_POT, StorageLocation.TACKLE_BOX),
    KARAMBWAN_VESSEL(ItemID.KARAMBWAN_VESSEL, StorageLocation.TACKLE_BOX),
    KARAMBWAN_VESSEL_FILLED(ItemID.KARAMBWAN_VESSEL_3159, StorageLocation.TACKLE_BOX),

    //Bait
    SPIRIT_FLAKES(ItemID.SPIRIT_FLAKES, StorageLocation.TACKLE_BOX),
    FEATHERS(ItemID.FEATHER, StorageLocation.TACKLE_BOX),
    RAW_KARAMBWANJI(ItemID.RAW_KARAMBWANJI, StorageLocation.TACKLE_BOX),
    FISHING_BAIT(ItemID.FISHING_BAIT, StorageLocation.TACKLE_BOX),
    DARK_FISHING_BAIT(ItemID.DARK_FISHING_BAIT, StorageLocation.TACKLE_BOX),
    SANDWORMS(ItemID.SANDWORMS, StorageLocation.TACKLE_BOX),
    FISH_OFFCUTS(ItemID.FISH_OFFCUTS, StorageLocation.TACKLE_BOX),
    FISH_CHUNKS(ItemID.FISH_CHUNKS, StorageLocation.TACKLE_BOX),
    //Misc
    FISHING_POTION1(ItemID.FISHING_POTION1, StorageLocation.TACKLE_BOX),
    FISHING_POTION2(ItemID.FISHING_POTION2, StorageLocation.TACKLE_BOX),
    FISHING_POTION3(ItemID.FISHING_POTION3, StorageLocation.TACKLE_BOX),
    FISHING_POTION4(ItemID.FISHING_POTION4, StorageLocation.TACKLE_BOX),
    MOLCH_PEARLS(ItemID.MOLCH_PEARL, StorageLocation.TACKLE_BOX),

    /**
     * Steel Key Ring
     */
    ANCESTREAL_KEY(ItemID.ANCESTRAL_KEY, StorageLocation.STEEL_KEY_RING),
    BATTERED_KEY(ItemID.BATTERED_KEY, StorageLocation.STEEL_KEY_RING),
    BONE_KEY(ItemID.BONE_KEY, StorageLocation.STEEL_KEY_RING),
    BRASS_KEY(ItemID.BRASS_KEY, StorageLocation.STEEL_KEY_RING),
    CRYSTALMINE_KEY(ItemID.CRYSTALMINE_KEY, StorageLocation.STEEL_KEY_RING),
    DUSTY_KEY(ItemID.DUSTY_KEY, StorageLocation.STEEL_KEY_RING),
    ENCHANTED_KEY(ItemID.ENCHANTED_KEY, StorageLocation.STEEL_KEY_RING),
    WEAPON_STORE_KEY(ItemID.WEAPON_STORE_KEY, StorageLocation.STEEL_KEY_RING),
    MAZE_KEY(ItemID.MAZE_KEY, StorageLocation.STEEL_KEY_RING),
    METAL_KEY(ItemID.METAL_KEY, StorageLocation.STEEL_KEY_RING),
    NEW_KEY(ItemID.NEW_KEY, StorageLocation.STEEL_KEY_RING),
    SHINY_KEY(ItemID.SHINY_KEY, StorageLocation.STEEL_KEY_RING),
    WROUGHT_IRON_KEY(ItemID.WROUGHT_IRON_KEY, StorageLocation.STEEL_KEY_RING),

    /**
     * Tool Lep
     */
    RAKE(ItemID.RAKE, StorageLocation.TOOL_LEP),
    SPADE(ItemID.SPADE, StorageLocation.TOOL_LEP),
    SEED_DIBBER(ItemID.SEED_DIBBER, StorageLocation.TOOL_LEP),
    SECATEURS(ItemID.SECATEURS, StorageLocation.TOOL_LEP),
    MAGIC_SECATEURS(ItemID.MAGIC_SECATEURS, StorageLocation.TOOL_LEP),
    GARDENING_TROWEL(ItemID.GARDENING_TROWEL, StorageLocation.TOOL_LEP),
    BOTOMLESS_COMPOST(ItemID.BOTTOMLESS_COMPOST_BUCKET, StorageLocation.TOOL_LEP),
    BOTTOMLESS_COMPOST_BUCKET_22997(ItemID.BOTTOMLESS_COMPOST_BUCKET_22997, StorageLocation.TOOL_LEP),
    PLANT_CURE(ItemID.PLANT_CURE, StorageLocation.TOOL_LEP),
    PLANT_CURE_6468(ItemID.PLANT_CURE_6468, StorageLocation.TOOL_LEP),
    COMPOST(ItemID.COMPOST, StorageLocation.TOOL_LEP),
    SUPERCOMPOST(ItemID.SUPERCOMPOST, StorageLocation.TOOL_LEP),
    ULTRACOMPOST(ItemID.ULTRACOMPOST, StorageLocation.TOOL_LEP),
    WATERING_CAN(ItemID.WATERING_CAN, StorageLocation.TOOL_LEP),
    WATERING_CAN1(ItemID.WATERING_CAN1, StorageLocation.TOOL_LEP),
    WATERING_CAN2(ItemID.WATERING_CAN2, StorageLocation.TOOL_LEP),
    WATERING_CAN3(ItemID.WATERING_CAN3, StorageLocation.TOOL_LEP),
    WATERING_CAN4(ItemID.WATERING_CAN4, StorageLocation.TOOL_LEP),
    WATERING_CAN5(ItemID.WATERING_CAN5, StorageLocation.TOOL_LEP),
    WATERING_CAN6(ItemID.WATERING_CAN6, StorageLocation.TOOL_LEP),
    WATERING_CAN7(ItemID.WATERING_CAN7, StorageLocation.TOOL_LEP),
    WATERING_CAN8(ItemID.WATERING_CAN8, StorageLocation.TOOL_LEP),
    GRICOLLERS_CAN(ItemID.GRICOLLERS_CAN, StorageLocation.TOOL_LEP),

    /**
     * Master Scroll Book
     * https://oldschool.runescape.wiki/w/Teleport_scrolls
     */
    NARDAH_TELEPORT(ItemID.NARDAH_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    DIGSITE_TELEPORT(ItemID.DIGSITE_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    FELDIP_HILLS_TELEPORT(ItemID.FELDIP_HILLS_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    LUNAR_ISLE_TELEPORT(ItemID.LUNAR_ISLE_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    MORTTON_TELEPORT(ItemID.MORTTON_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    PEST_CONTROL_TELEPORT(ItemID.PEST_CONTROL_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    PISCATORIS_TELEPORT(ItemID.PISCATORIS_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    TAI_BWO_WANNAI_TELEPORT(ItemID.TAI_BWO_WANNAI_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    IORWERTH_CAMP_TELEPORT(ItemID.IORWERTH_CAMP_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    MOS_LEHARMLESS_TELEPORT(ItemID.MOS_LEHARMLESS_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    LUMBERYARD_TELEPORT(ItemID.LUMBERYARD_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    ZULANDRA_TELEPORT(ItemID.ZULANDRA_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    KEY_MASTER_TELEPORT(ItemID.KEY_MASTER_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    REVENANT_CAVE_TELEPORT(ItemID.REVENANT_CAVE_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),
    WATSON_TELEPORT(ItemID.WATSON_TELEPORT, StorageLocation.MASTER_SCROLL_BOOK),

    /**
     * Fossil Storage
     */
    UNIDENTIFIED_SMALL_FOSSIL(ItemID.UNIDENTIFIED_SMALL_FOSSIL, StorageLocation.FOSSIL_STORAGE),
    SMALL_FOSSILISED_LIMBS(ItemID.SMALL_FOSSILISED_LIMBS, StorageLocation.FOSSIL_STORAGE),
    SMALL_FOSSILISED_SPINE(ItemID.SMALL_FOSSILISED_SPINE, StorageLocation.FOSSIL_STORAGE),
    SMALL_FOSSILISED_RIBS(ItemID.SMALL_FOSSILISED_RIBS, StorageLocation.FOSSIL_STORAGE),
    SMALL_FOSSILISED_PELVIS(ItemID.SMALL_FOSSILISED_PELVIS, StorageLocation.FOSSIL_STORAGE),
    SMALL_FOSSILISED_SKULL(ItemID.SMALL_FOSSILISED_SKULL, StorageLocation.FOSSIL_STORAGE),
    UNIDENTIFIED_MEDIUM_FOSSIL(ItemID.UNIDENTIFIED_MEDIUM_FOSSIL, StorageLocation.FOSSIL_STORAGE),
    MEDIUM_FOSSILISED_LIMBS(ItemID.MEDIUM_FOSSILISED_LIMBS, StorageLocation.FOSSIL_STORAGE),
    MEDIUM_FOSSILISED_SPINE(ItemID.MEDIUM_FOSSILISED_SPINE, StorageLocation.FOSSIL_STORAGE),
    MEDIUM_FOSSILISED_RIBS(ItemID.MEDIUM_FOSSILISED_RIBS, StorageLocation.FOSSIL_STORAGE),
    MEDIUM_FOSSILISED_PELVIS(ItemID.MEDIUM_FOSSILISED_PELVIS, StorageLocation.FOSSIL_STORAGE),
    MEDIUM_FOSSILISED_SKULL(ItemID.MEDIUM_FOSSILISED_SKULL, StorageLocation.FOSSIL_STORAGE),
    UNIDENTIFIED_LARGE_FOSSIL(ItemID.UNIDENTIFIED_LARGE_FOSSIL, StorageLocation.FOSSIL_STORAGE),
    LARGE_FOSSILISED_LIMBS(ItemID.LARGE_FOSSILISED_LIMBS, StorageLocation.FOSSIL_STORAGE),
    LARGE_FOSSILISED_SPINE(ItemID.LARGE_FOSSILISED_SPINE, StorageLocation.FOSSIL_STORAGE),
    LARGE_FOSSILISED_RIBS(ItemID.LARGE_FOSSILISED_RIBS, StorageLocation.FOSSIL_STORAGE),
    LARGE_FOSSILISED_PELVIS(ItemID.LARGE_FOSSILISED_PELVIS, StorageLocation.FOSSIL_STORAGE),
    LARGE_FOSSILISED_SKULL(ItemID.LARGE_FOSSILISED_SKULL, StorageLocation.FOSSIL_STORAGE),
    FOSSILISED_ROOTS(ItemID.FOSSILISED_ROOTS, StorageLocation.FOSSIL_STORAGE),
    FOSSILISED_STUMP(ItemID.FOSSILISED_STUMP, StorageLocation.FOSSIL_STORAGE),
    FOSSILISED_BRANCH(ItemID.FOSSILISED_BRANCH, StorageLocation.FOSSIL_STORAGE),
    FOSSILISED_LEAF(ItemID.FOSSILISED_LEAF, StorageLocation.FOSSIL_STORAGE),
    FOSSILISED_MUSHROOM(ItemID.FOSSILISED_MUSHROOM, StorageLocation.FOSSIL_STORAGE),

    /**
     * Puro Puro @ Elnock Inquisitor
     */

    BUTTERFLY_NET(ItemID.BUTTERFLY_NET, StorageLocation.PURO_PURO),
    MAGIC_BUTTERFLY_NET(ItemID.MAGIC_BUTTERFLY_NET, StorageLocation.PURO_PURO),
    IMPLING_JAR(ItemID.IMPLING_JAR, StorageLocation.PURO_PURO),
    IMP_REPELLENT(ItemID.IMP_REPELLENT, StorageLocation.PURO_PURO),

    /**
     * Flamtaer Bag
     */
    TIMBER_BEAMS(ItemID.TIMBER_BEAM, StorageLocation.FLAMTAER_BAG),
    LIMESTONE_BRICK(ItemID.LIMESTONE_BRICK, StorageLocation.FLAMTAER_BAG),
    SWAMP_PASTE(ItemID.SWAMP_PASTE, StorageLocation.FLAMTAER_BAG),

    /**
     * Nightmare Zone
     */
    ABSORPTION_1(ItemID.ABSORPTION_1, StorageLocation.NIGHTMARE_ZONE),
    ABSORPTION_2(ItemID.ABSORPTION_2, StorageLocation.NIGHTMARE_ZONE),
    ABSORPTION_3(ItemID.ABSORPTION_3, StorageLocation.NIGHTMARE_ZONE),
    ABSORPTION_4(ItemID.ABSORPTION_4, StorageLocation.NIGHTMARE_ZONE),
    OVERLOAD_1(ItemID.OVERLOAD_1, StorageLocation.NIGHTMARE_ZONE),
    OVERLOAD_2(ItemID.OVERLOAD_2, StorageLocation.NIGHTMARE_ZONE),
    OVERLOAD_3(ItemID.OVERLOAD_4, StorageLocation.NIGHTMARE_ZONE),
    OVERLOAD_4(ItemID.OVERLOAD_3, StorageLocation.NIGHTMARE_ZONE),
    SUPER_MAGIC_POTION_1(ItemID.SUPER_MAGIC_POTION_1, StorageLocation.NIGHTMARE_ZONE),
    SUPER_MAGIC_POTION_2(ItemID.SUPER_MAGIC_POTION_2, StorageLocation.NIGHTMARE_ZONE),
    SUPER_MAGIC_POTION_3(ItemID.SUPER_MAGIC_POTION_3, StorageLocation.NIGHTMARE_ZONE),
    SUPER_MAGIC_POTION_4(ItemID.SUPER_MAGIC_POTION_4, StorageLocation.NIGHTMARE_ZONE),
    SUPER_RANGING_1(ItemID.SUPER_RANGING_1, StorageLocation.NIGHTMARE_ZONE),
    SUPER_RANGING_2(ItemID.SUPER_RANGING_2, StorageLocation.NIGHTMARE_ZONE),
    SUPER_RANGING_3(ItemID.SUPER_RANGING_3, StorageLocation.NIGHTMARE_ZONE),
    SUPER_RANGING_4(ItemID.SUPER_RANGING_4, StorageLocation.NIGHTMARE_ZONE),

    /**
     * Seeds
     */
    //ALLOTMENT SEEDS
    POTATO_SEED(ItemID.POTATO_SEED, StorageLocation.SEEDS),
    ONION_SEED(ItemID.ONION_SEED, StorageLocation.SEEDS),
    CABBAGE_SEED(ItemID.CABBAGE_SEED, StorageLocation.SEEDS),
    TOMATO_SEED(ItemID.TOMATO_SEED, StorageLocation.SEEDS),
    SWEETCORN_SEED(ItemID.SWEETCORN_SEED, StorageLocation.SEEDS),
    STRAWBERRY_SEED(ItemID.STRAWBERRY_SEED, StorageLocation.SEEDS),
    WATERMELON_SEED(ItemID.WATERMELON_SEED, StorageLocation.SEEDS),
    SNAPE_GRASS_SEED(ItemID.SNAPE_GRASS_SEED, StorageLocation.SEEDS),
    //FLOWER SEEDS
    MARIGOLD_SEED(ItemID.MARIGOLD_SEED, StorageLocation.SEEDS),
    ROSEMARY_SEED(ItemID.ROSEMARY_SEED, StorageLocation.SEEDS),
    NASTURTIUM_SEED(ItemID.NASTURTIUM_SEED, StorageLocation.SEEDS),
    WOAD_SEED(ItemID.WOAD_SEED, StorageLocation.SEEDS),
    LIMPWURT_SEED(ItemID.LIMPWURT_SEED, StorageLocation.SEEDS),
    WHITE_LILY_SEED(ItemID.WHITE_LILY_SEED, StorageLocation.SEEDS),
    //HERB SEEDS
    GUAM_SEED(ItemID.GUAM_SEED, StorageLocation.SEEDS),
    MARRENTILL_SEED(ItemID.MARRENTILL_SEED, StorageLocation.SEEDS),
    TARROMIN_SEED(ItemID.TARROMIN_SEED, StorageLocation.SEEDS),
    HARRALANDER_SEED(ItemID.HARRALANDER_SEED, StorageLocation.SEEDS),
    RANARR_SEED(ItemID.RANARR_SEED, StorageLocation.SEEDS),
    TOADFLAX_SEED(ItemID.TOADFLAX_SEED, StorageLocation.SEEDS),
    IRIT_SEED(ItemID.IRIT_SEED, StorageLocation.SEEDS),
    AVANTOE_SEED(ItemID.AVANTOE_SEED, StorageLocation.SEEDS),
    KWUARM_SEED(ItemID.KWUARM_SEED, StorageLocation.SEEDS),
    SNAPDRAGON_SEED(ItemID.SNAPDRAGON_SEED, StorageLocation.SEEDS),
    CADANTINE_SEED(ItemID.CADANTINE_SEED, StorageLocation.SEEDS),
    LANTADYME_SEED(ItemID.LANTADYME_SEED, StorageLocation.SEEDS),
    DWARF_WEED_SEED(ItemID.DWARF_WEED_SEED, StorageLocation.SEEDS),
    TORSTOL_SEED(ItemID.TORSTOL_SEED, StorageLocation.SEEDS),
    //HOP SEEDS
    BARLEY_SEED(ItemID.BARLEY_SEED, StorageLocation.SEEDS),
    HAMMERSTONE_SEED(ItemID.HAMMERSTONE_SEED, StorageLocation.SEEDS),
    ASGARNIAN_SEED(ItemID.ASGARNIAN_SEED, StorageLocation.SEEDS),
    JUTE_SEED(ItemID.JUTE_SEED, StorageLocation.SEEDS),
    YANILLIAN_SEED(ItemID.YANILLIAN_SEED, StorageLocation.SEEDS),
    KRANDORIAN_SEED(ItemID.KRANDORIAN_SEED, StorageLocation.SEEDS),
    WILDBLOOD_SEED(ItemID.WILDBLOOD_SEED, StorageLocation.SEEDS),
    //BUSH SEEDS
    REDBERRY_SEED(ItemID.REDBERRY_SEED, StorageLocation.SEEDS),
    CADAVABERRY_SEED(ItemID.CADAVABERRY_SEED, StorageLocation.SEEDS),
    DWELLBERRY_SEED(ItemID.DWELLBERRY_SEED, StorageLocation.SEEDS),
    JANGERBERRY_SEED(ItemID.JANGERBERRY_SEED, StorageLocation.SEEDS),
    WHITEBERRY_SEED(ItemID.WHITEBERRY_SEED, StorageLocation.SEEDS),
    POISON_IVY_SEED(ItemID.POISON_IVY_SEED, StorageLocation.SEEDS),
    //TREE SEEDS
    ACORN(ItemID.ACORN, StorageLocation.SEEDS),
    WILLOW_SEED(ItemID.WILLOW_SEED, StorageLocation.SEEDS),
    MAPLE_SEED(ItemID.MAPLE_SEED, StorageLocation.SEEDS),
    YEW_SEED(ItemID.YEW_SEED, StorageLocation.SEEDS),
    MAGIC_SEED(ItemID.MAGIC_SEED, StorageLocation.SEEDS),
    //FRUIT TREE SEEDS
    APPLE_TREE_SEED(ItemID.APPLE_TREE_SEED, StorageLocation.SEEDS),
    BANANA_TREE_SEED(ItemID.BANANA_TREE_SEED, StorageLocation.SEEDS),
    ORANGE_TREE_SEED(ItemID.ORANGE_TREE_SEED, StorageLocation.SEEDS),
    PINEAPPLE_SEED(ItemID.PINEAPPLE_SEED, StorageLocation.SEEDS),
    PALM_TREE_SEED(ItemID.PALM_TREE_SEED, StorageLocation.SEEDS),
    DRAGONFRUIT_TREE_SEED(ItemID.DRAGONFRUIT_TREE_SEED, StorageLocation.SEEDS),
    //SPECIAL SEEDS
    SEAWEED_SPORE(ItemID.SEAWEED_SPORE, StorageLocation.SEEDS),
    GRAPE_SEED(ItemID.GRAPE_SEED, StorageLocation.SEEDS),
    MUSHROOM_SPORE(ItemID.MUSHROOM_SPORE, StorageLocation.SEEDS),
    BELLADONNA_SEED(ItemID.BELLADONNA_SEED, StorageLocation.SEEDS),
    HESPORI_SEED(ItemID.HESPORI_SEED, StorageLocation.SEEDS),
    //ANIMA SEEDS
    KRONOS_SEED(ItemID.KRONOS_SEED, StorageLocation.SEEDS),
    IASOR_SEED(ItemID.IASOR_SEED, StorageLocation.SEEDS),
    ATTAS_SEED(ItemID.ATTAS_SEED, StorageLocation.SEEDS),
    //SPECIAL TREES
    TEAK_SEED(ItemID.TEAK_SEED, StorageLocation.SEEDS),
    MAHOGANY_SEED(ItemID.MAHOGANY_SEED, StorageLocation.SEEDS),
    CALQUAT_TREE_SEED(ItemID.CALQUAT_TREE_SEED, StorageLocation.SEEDS),
    CRYSTAL_ACORN(ItemID.CRYSTAL_ACORN, StorageLocation.SEEDS),
    SPIRIT_SEED(ItemID.SPIRIT_SEED, StorageLocation.SEEDS),
    CELASTRUS_SEED(ItemID.CELASTRUS_SEED, StorageLocation.SEEDS),
    REDWOOD_TREE_SEED(ItemID.REDWOOD_TREE_SEED, StorageLocation.SEEDS),
    //CACTI SEEDS
    CACTUS_SEED(ItemID.CACTUS_SEED, StorageLocation.SEEDS),
    POTATO_CACTUS_SEED(ItemID.POTATO_CACTUS_SEED, StorageLocation.SEEDS),
    //TREE SAPLINGS
    OAK_SAPLING(ItemID.OAK_SAPLING, StorageLocation.SEEDS),
    WILLOW_SAPLING(ItemID.WILLOW_SAPLING, StorageLocation.SEEDS),
    MAPLE_SAPLING(ItemID.MAPLE_SAPLING, StorageLocation.SEEDS),
    YEW_SAPLING(ItemID.YEW_SAPLING, StorageLocation.SEEDS),
    MAGIC_SAPLING(ItemID.MAGIC_SAPLING, StorageLocation.SEEDS),
    //FRUIT TREES
    APPLE_SAPLING(ItemID.APPLE_SAPLING, StorageLocation.SEEDS),
    BANANA_SAPLING(ItemID.BANANA_SAPLING, StorageLocation.SEEDS),
    ORANGE_SAPLING(ItemID.ORANGE_SAPLING, StorageLocation.SEEDS),
    CURRY_SAPLING(ItemID.CURRY_SAPLING, StorageLocation.SEEDS),
    PINEAPPLE_SAPLING(ItemID.PINEAPPLE_SAPLING, StorageLocation.SEEDS),
    PALM_SAPLING(ItemID.PALM_SAPLING, StorageLocation.SEEDS),
    DRAGONFRUIT_SAPLING(ItemID.DRAGONFRUIT_SAPLING, StorageLocation.SEEDS),
    TEAK_SAPLING(ItemID.TEAK_SAPLING, StorageLocation.SEEDS),
    MAHOGANY_SAPLING(ItemID.MAHOGANY_SAPLING, StorageLocation.SEEDS),
    CALQUAT_SAPLING(ItemID.CALQUAT_SAPLING, StorageLocation.SEEDS),
    SPIRIT_SAPLING(ItemID.SPIRIT_SAPLING, StorageLocation.SEEDS),
    CELASTRUS_SAPLING(ItemID.CELASTRUS_SAPLING, StorageLocation.SEEDS),
    REDWOOD_SAPLING(ItemID.REDWOOD_SAPLING, StorageLocation.SEEDS)
    ;

    public final int itemID;
    public final StorageLocation location;

    public static final List<StorableItem> tackleBoxItems = storableItemsAtLocation(StorageLocation.TACKLE_BOX);
    public static final List<StorableItem> steelKeyRingItems = storableItemsAtLocation(StorageLocation.STEEL_KEY_RING);
    public static final List<StorableItem> toolLepItems = storableItemsAtLocation(StorageLocation.TOOL_LEP);
    public static final List<StorableItem> masterScrollBookItems = storableItemsAtLocation(StorageLocation.MASTER_SCROLL_BOOK);
    public static final List<StorableItem> fossilStorageItems = storableItemsAtLocation(StorageLocation.FOSSIL_STORAGE);
    public static final List<StorableItem> puroPuroItems = storableItemsAtLocation(StorageLocation.PURO_PURO);
    public static final List<StorableItem> flamtaerBagItems = storableItemsAtLocation(StorageLocation.FLAMTAER_BAG);
    public static final List<StorableItem> nightmareZoneItems = storableItemsAtLocation(StorageLocation.NIGHTMARE_ZONE);
    public static final List<StorableItem> seedItems = storableItemsAtLocation(StorageLocation.SEEDS);

    private static final Map<StorableItem, String> storableItemNameMap = new HashMap<>();
    private static final Map<Integer, StorableItem> ITEM_ID_MAP = new HashMap<>();
    static
    {
        for (StorableItem i : values())
        {
            ITEM_ID_MAP.put(i.getItemID(), i);
        }
    }

    StorableItem(int itemID, StorageLocation location)
    {
        this.itemID = itemID;
        this.location = location;
    }

    private static List<StorableItem> storableItemsAtLocation(StorageLocation storageLocation)
    {
        return Arrays.stream(StorableItem.values())
                .filter(x ->x.location == storageLocation)
                .collect(Collectors.toList());
    }

    public static void prepareStorableItemNames(ItemManager itemManager)
    {
        for(StorableItem i : StorableItem.values())
        {
            storableItemNameMap.put(i, itemManager.getItemComposition(i.itemID).getName());
        }
    }

    public static List<String> storableListToString(List<StorableItem> items)
    {
        return items.stream()
                .filter(storableItemNameMap::containsKey)
                .map(i -> String.format("%s", storableItemNameMap.get(i)))
                .collect(Collectors.toList());
    }

}
