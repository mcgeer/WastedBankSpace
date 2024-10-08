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
public enum ToyBox implements StorableItem {
    BUNNY_EARS(ItemID.BUNNY_EARS),
    SCYTHE(ItemID.SCYTHE),
    YOYO(ItemID.YOYO),
    EASTER_BASKET(ItemID.EASTER_BASKET),
    RED_MARIONETTE(ItemID.RED_MARIONETTE),
    BLUE_MARIONETTE(ItemID.BLUE_MARIONETTE),
    GREEN_MARIONETTE(ItemID.GREEN_MARIONETTE),
    RUBBER_CHICKEN(ItemID.RUBBER_CHICKEN),
    BOBBLE_HAT(ItemID.BOBBLE_HAT),
    JESTER_HAT(ItemID.JESTER_HAT),
    TRIJESTER_HAT(ItemID.TRIJESTER_HAT),
    WOOLLY_HAT(ItemID.WOOLLY_HAT),
    BOBBLE_SCARF(ItemID.BOBBLE_SCARF),
    JESTER_SCARF(ItemID.JESTER_SCARF),
    TRIJESTER_SCARF(ItemID.TRIJESTER_SCARF),
    WOOLLY_SCARF(ItemID.WOOLLY_SCARF),
    ZOMBIE_HEAD(ItemID.ZOMBIE_HEAD),
    JACK_LANTERN_MASK(ItemID.JACK_LANTERN_MASK),
    REINDEER_HAT(ItemID.REINDEER_HAT),
    EASTER_RING(ItemID.EASTER_RING),
    SKELETON_MASK(ItemID.SKELETON_MASK),
    SKELETON_SHIRT(ItemID.SKELETON_SHIRT),
    SKELETON_LEGGINS(ItemID.SKELETON_LEGGINGS),
    SKELETON_GLOVES(ItemID.SKELETON_GLOVES),
    SKELETON_BOOTS(ItemID.SKELETON_BOOTS),
    CHICKEN_HEAD(ItemID.CHICKEN_HEAD),
    CHICKEN_WINGS(ItemID.CHICKEN_WINGS),
    CHICKEN_LEGS(ItemID.CHICKEN_LEGS),
    CHICKEN_FEET(ItemID.CHICKEN_FEET),
    GRIM_REAPER_HOOD(ItemID.GRIM_REAPER_HOOD),
    BLACK_HWEEN_MASK(ItemID.BLACK_HWEEN_MASK),
    BLACK_PARTY_HAT(ItemID.BLACK_PARTYHAT),
    RAINBOW_PARTY_HAT(ItemID.RAINBOW_PARTYHAT),
    COW_MASK(ItemID.COW_MASK),
    COW_TOP(ItemID.COW_TOP),
    COW_TROUSERS(ItemID.COW_TROUSERS),
    COW_GLOVES(ItemID.COW_GLOVES),
    COW_SHOES(ItemID.COW_SHOES),
    ANTISANTA_MASK(ItemID.ANTISANTA_MASK),
    ANTISANTA_JACKET(ItemID.ANTISANTA_JACKET),
    ANTISANTA_PANTALOONS(ItemID.ANTISANTA_PANTALOONS),
    ANTISANTA_GLOVES(ItemID.ANTISANTA_GLOVES),
    ANTISANTA_BOOTS(ItemID.ANTISANTA_BOOTS),
    DRUIDIC_WREATH(ItemID.DRUIDIC_WREATH),
    SANTA_MASK(ItemID.SANTA_MASK),
    SANTA_JACKET(ItemID.SANTA_JACKET),
    SANTA_PANTALOONS(ItemID.SANTA_PANTALOONS),
    SANTA_GLOVES(ItemID.SANTA_GLOVES),
    SANTA_BOOTS(ItemID.SANTA_BOOTS),
    BUNNY_TOP(ItemID.BUNNY_TOP),
    BUNNY_LEGS(ItemID.BUNNY_LEGS),
    BUNNY_PAWS(ItemID.BUNNY_PAWS),
    BUNNY_FEET(ItemID.BUNNY_FEET),
    ANTIPANTIES(ItemID.ANTIPANTIES),
    BLACK_SANTA_HAT(ItemID.BLACK_SANTA_HAT),
    INVERTED_SANTA_HAT(ItemID.INVERTED_SANTA_HAT),
    MASK_OF_BALANCE(ItemID.MASK_OF_BALANCE),
    TIGER_TOY(ItemID.TIGER_TOY),
    LION_TOY(ItemID.LION_TOY),
    SNOW_LEOPARD_TOY(ItemID.SNOW_LEOPARD_TOY),
    AMUR_LEOPARAD_TOY(ItemID.AMUR_LEOPARD_TOY),
    GRAVEDIGGER_MASK(ItemID.GRAVEDIGGER_MASK),
    GRAVEDIGGER_TOP(ItemID.GRAVEDIGGER_TOP),
    GRAVEDIGGER_LEGGINGS(ItemID.GRAVEDIGGER_LEGGINGS),
    GRAVEDIGGER_GLOVES(ItemID.GRAVEDIGGER_GLOVES),
    GRAVEDIGGER_BOOTS(ItemID.GRAVEDIGGER_BOOTS),
    CABBAGE_CAPE(ItemID.CABBAGE_CAPE),
    CRUCIFERROUS_CODEX(ItemID.CRUCIFEROUS_CODEX),
    GIANT_PRESENT(ItemID.GIANT_PRESENT),
    GNOME_CHILD_HAT(ItemID.GNOME_CHILD_HAT),
    HORNWOOD_HELM(ItemID.HORNWOOD_HELM),
    BANSHEE_MASK(ItemID.BANSHEE_MASK),
    BANSHEE_TOP(ItemID.BANSHEE_TOP),
    BANSHEE_ROBE(ItemID.BANSHEE),
    HUNTING_KNIFE(ItemID.HUNTING_KNIFE),
    SACK_OF_PRESENTS(ItemID.SACK_OF_PRESENTS),
    SNOW_GLOBE(ItemID.SNOW_GLOBE),
    _4TH_BIRTHDAY_HAT(ItemID._4TH_BIRTHDAY_HAT),
    BIRTHDAY_BALLOONS(ItemID.BIRTHDAY_BALLOONS),
    EASTER_EGG_HELM(ItemID.EASTER_EGG_HELM),
    HAND_FAN(ItemID.HAND_FAN),
    JONAS_MASK(ItemID.JONAS_MASK),
    RAINBOW_SCARF(ItemID.RAINBOW_SCARF),
    RUNEFEST_SHIELD(ItemID.RUNEFEST_SHIELD),
    SNOW_IMP_COSTUME_HEAD(ItemID.SNOW_IMP_COSTUME_HEAD),
    SNOW_IMP_COSTUME_BODY(ItemID.SNOW_IMP_COSTUME_BODY),
    SNOW_IMP_COSTUME_LEGS(ItemID.SNOW_IMP_COSTUME_LEGS),
    SNOW_IMP_COSTUME_TAIL(ItemID.SNOW_IMP_COSTUME_TAIL),
    SNOW_IMP_COSTUME_GLOVES(ItemID.SNOW_IMP_COSTUME_GLOVES),
    SNOW_IMP_COSTUME_FEET(ItemID.SNOW_IMP_COSTUME_FEET),
    WISE_OLD_MANS_SANTA_HAT(ItemID.WISE_OLD_MANS_SANTA_HAT),
    PROP_SWORD(ItemID.PROP_SWORD),
    STARFACE(ItemID.STARFACE),
    TREE_TOP(ItemID.TREE_TOP),
    TREE_SKIRT(ItemID.TREE_SKIRT),
    CANDY_CANE(ItemID.CANDY_CANE),
    CLOWN_MASK(ItemID.CLOWN_MASK),
    CLOWN_GOWN(ItemID.CLOWN_GOWN),
    CLOW_BOW_TIE(ItemID.CLOWN_BOW_TIE),
    CLOWN_TROUSERS(ItemID.CLOWN_TROUSERS),
    CLOWN_SHOES(ItemID.CLOWN_SHOES),
    EEK(ItemID.EEK),
    EGGSHELL_PLATEBODY(ItemID.EGGSHELL_PLATEBODY),
    EGGSHELL_PLATELEGS(ItemID.EGGSHELL_PLATELEGS),
    BIRTHDAY_CAKE(ItemID.BIRTHDAY_CAKE),
    BUNNYMAN_MASK(ItemID.BUNNYMAN_MASK),
    GIANT_EASTER_EGG(ItemID.GIANT_EASTER_EGG),
    GREEN_GINGERBREAD_SHIELD(ItemID.GREEN_GINGERBREAD_SHIELD),
    PUMPKIN_LANTERN(ItemID.PUMPKIN_LANTERN),
    SKELETON_LANTERN(ItemID.SKELETON_LANTERN),
    SPOOKIER_HOOD(ItemID.SPOOKIER_HOOD),
    SPOOKIER_ROBE(ItemID.SPOOKIER_ROBE),
    SPOOKIER_SKIRT(ItemID.SPOOKIER_SKIRT),
    SPOOKIER_GLOVES(ItemID.SPOOKIER_GLOVES),
    SPOOKIER_BOOTS(ItemID.SPOOKIER_BOOTS),
    SPOOKY_HOOD(ItemID.SPOOKY_HOOD),
    SPOOKY_ROBE(ItemID.SPOOKY_ROBE),
    SPOOKY_SKIRT(ItemID.SPOOKY_SKIRT),
    SPOOKY_GLOVES(ItemID.SPOOKY_GLOVES),
    SPOOKY_BOOTS(ItemID.SPOOKY_BOOTS),
    CAT_EARS(ItemID.CAT_EARS),
    GIANT_BOULDER(ItemID.GIANT_BOULDER),
    CARROT_SWORD(ItemID.CARROT_SWORD),
    _24CARAT_SWORD(ItemID._24CARAT_SWORD),
    MAGIC_EGG_BALL(ItemID.MAGIC_EGG_BALL),
    GOBLIN_DECORATIONS(ItemID.GOBLIN_DECORATIONS),
    HEADLESS_HEAD(ItemID.HEADLESS_HEAD),
    MAGICAL_PUMPKIN(ItemID.MAGICAL_PUMPKIN),
    _20th_ANNIVERSARY_HAT(ItemID._20TH_ANNIVERSARY_HAT),
    _20th_ANNIVERSARY_TOP(ItemID._20TH_ANNIVERSARY_TOP),
    _20th_ANNIVERSARY_BOTTOM(ItemID._20TH_ANNIVERSARY_BOTTOM),
    _20th_ANNIVERSARY_NECKLACE(ItemID._20TH_ANNIVERSARY_NECKLACE),
    _20th_ANNIVERSARY_CAPE(ItemID._20TH_ANNIVERSARY_CAPE),
    _20th_ANNIVERSARY_GLOVES(ItemID._20TH_ANNIVERSARY_GLOVES),
    _20th_ANNIVERSARY_BOOTS(ItemID._20TH_ANNIVERSARY_BOOTS),
    BANANA_CAPE(ItemID.BANANA_CAPE),
    BANANA_HAT(ItemID.BANANA_HAT),
    CURSED_BANANA(ItemID.CURSED_BANANA),
    FESTIVE_ELF_HAT(ItemID.FESTIVE_ELF_HAT), // find ID
    FESTIVE_ELF_SLIPPERS(ItemID.FESTIVE_ELF_SLIPPERS),
    GNOME_CHILD_MASK(ItemID.GNOME_CHILD_MASK),
    GNOME_CHILD_ICON(ItemID.GNOME_CHILD_ICON),
    GREGGS_EASTDOOR(ItemID.GREGGS_EASTDOOR),
    HAUNTED_WINE_BOTTLE(ItemID.HAUNTED_WINE_BOTTLE),
    SNOWMAN_RING(ItemID.SNOWMAN_RING),
    PROPELLER_HAT(ItemID.PROPELLER_HAT),
    SAUCEPAN(ItemID.SAUCEPAN),
    SECRET_SANTA_PRESENT_RED(ItemID.SECRET_SANTA_PRESENT_RED),
    SECRET_SANTA_PRESENT_BLUE(ItemID.SECRET_SANTA_PRESENT_BLUE),
    SECRET_SANTA_PRESENT_GREEN(ItemID.SECRET_SANTA_PRESENT_GREEN),
    SECRET_SANTA_PRESENT_BLACK(ItemID.SECRET_SANTA_PRESENT_BLACK),
    SECRET_SANTA_PRESENT_GOLD(ItemID.SECRET_SANTA_PRESENT_GOLD),
    ORANGE_HALLOWEEN_JUMPER(ItemID.UGLY_HALLOWEEN_JUMPER_ORANGE),
    BLACK_HALLOWEEN_JUMPER(ItemID.UGLY_HALLOWEEN_JUMPER_BLACK),
    SACK_OF_COAL(ItemID.SACK_OF_COAL),
    CRATE_RING(ItemID.CRATE_RING),
    EASTER_HAT(ItemID.EASTER_HAT),
    FESTIVE_GAMES_CROWN(ItemID.FESTIVE_GAMES_CROWN),
    CHRISTMAS_JUMPER(ItemID.CHRISTMAS_JUMPER),
    HALLOWEEN_WIG(ItemID.HALLOWEEN_WIG),
    HALLOWEEN_WIG_27499(ItemID.HALLOWEEN_WIG_27499),
    HALLOWEEN_WIG_27501(ItemID.HALLOWEEN_WIG_27501),
    HALLOWEEN_WIG_27503(ItemID.HALLOWEEN_WIG_27503),
    HALLOWEEN_WIG_27505(ItemID.HALLOWEEN_WIG_27505),
    HALLOWEEN_WIG_27507(ItemID.HALLOWEEN_WIG_27507),
    FESTIVE_NUTCRACKER_HAT(ItemID.FESTIVE_NUTCRACKER_HAT ),
    FEFSTIVE_NUTCRACKER_TOP(ItemID.FESTIVE_NUTCRACKER_TOP),
    FEFSTIVE_NUTCRACKER_TROUSERS(ItemID.FESTIVE_NUTCRACKER_TROUSERS),
    FEFSTIVE_NUTCRACKER_BOOTS(ItemID.FESTIVE_NUTCRACKER_BOOTS),
    FEFSTIVE_NUTCRACKER_STAFF(ItemID.FESTIVE_NUTCRACKER_STAFF),
    SWEET_NUTCRACKER_HAT(ItemID.SWEET_NUTCRACKER_HAT),
    SWEET_NUTCRACKER_TOP(ItemID.SWEET_NUTCRACKER_TOP),
    SWEET_NUTCRACKER_TROUSERS(ItemID.SWEET_NUTCRACKER_TROUSERS),
    SWEET_NUTCRACKER_BOOTS(ItemID.SWEET_NUTCRACKER_BOOTS),
    SWEET_NUTCRACKER_STAFF(ItemID.SWEET_NUTCRACKER_STAFF),
    FLOWER_CROWN(ItemID.FLOWER_CROWN),
    FLOWER_CROWN_27141(ItemID.FLOWER_CROWN_27141),
    FLOWER_CROWN_27143(ItemID.FLOWER_CROWN_27143),
    FLOWER_CROWN_27145(ItemID.FLOWER_CROWN_27145),
    FLOWER_CROWN_27147(ItemID.FLOWER_CROWN_27147),
    FLOWER_CROWN_27149(ItemID.FLOWER_CROWN_27149),
    FLOWER_CROWN_27151(ItemID.FLOWER_CROWN_27151),
    FLOWER_CROWN_27153(ItemID.FLOWER_CROWN_27153),
    FLOWER_CROWN_27155(ItemID.FLOWER_CROWN_27155),
    SKIS(ItemID.SKIS),
    SANTAS_LIST(ItemID.SANTAS_LIST),
    SNOW_GOGGLES__HAT(ItemID.SNOW_GOGGLES__HAT),
    TREAT_CAULDRON(ItemID.TREAT_CAULDRON),
    TREAT_CAULDRON_27465(ItemID.TREAT_CAULDRON_27465),
    TREAT_CAULDRON_27467(ItemID.TREAT_CAULDRON_27467),
    TREAT_CAULDRON_27469(ItemID.TREAT_CAULDRON_27469),
    TREAT_CAULDRON_27471(ItemID.TREAT_CAULDRON_27471),
    WITCH_HAT(ItemID.WITCH_HAT),
    WITCH_TOP(ItemID.WITCH_TOP),
    WITCH_ROBES(ItemID.WITCH_ROBES),
    WITCH_BOOTS(ItemID.WITCH_BOOTS),
    WITCH_CAPE(ItemID.WITCH_CAPE),
    _10TH_BIRTHDAY_CAPE(ItemID._10TH_BIRTHDAY_CAPE),
    _10TH_BIRTHDAY_BALLOONS(ItemID._10TH_BIRTHDAY_BALLOONS),
    CAKE_HAT(ItemID.CAKE_HAT),
    OLDSCHOOL_JUMPER(ItemID.OLDSCHOOL_JUMPER),
    OLDSCHOOL_JUMPER_27823(ItemID.OLDSCHOOL_JUMPER_27823),
    OLDSCHOOL_JUMPER_27824(ItemID.OLDSCHOOL_JUMPER_27824),
    OLDSCHOOL_JUMPER_27825(ItemID.OLDSCHOOL_JUMPER_27825),
    OLDSCHOOL_JUMPER_27826(ItemID.OLDSCHOOL_JUMPER_27826),
    OLDSCHOOL_JUMPER_27827(ItemID.OLDSCHOOL_JUMPER_27827),
    GNOME_CHILD_PLUSH(ItemID.GNOME_CHILD_PLUSH),
    STRAY_DOG_PLUSH(ItemID.STRAY_DOG_PLUSH),
    JAD_PLUSH(ItemID.JAD_PLUSH),
    BOB_THE_CAT_SLIPPERS(ItemID.BOB_THE_CAT_SLIPPERS),
    JAD_SLIPPERS(ItemID.JAD_SLIPPERS),
    DRAGON_CANDLE_DAGGER(ItemID.DRAGON_CANDLE_DAGGER),
    GNOME_CHILD_BACKPACK(ItemID.GNOME_CHILD_BACKPACK),
    MYSTIC_CARDS(ItemID.MYSTIC_CARDS),
    SILVER_PARTYHAT(ItemID.SILVER_PARTYHAT),
    WAR_SHIP(ItemID.WAR_SHIP),
    GIANT_BRONZE_DAGGER(ItemID.GIANT_BRONZE_DAGGER),
    EASTFLOOR_SPADE(ItemID.EASTFLOOR_SPADE),
    NEST_HAT(ItemID.NEST_HAT),
    NEST_HAT_27877(ItemID.NEST_HAT_27877),
    COLOURFUL_CAPE(ItemID.COLOURFUL_CAPE),
    COLOURFUL_CAPE_29493(ItemID.COLOURFUL_CAPE_29493),
    COLOURFUL_CAPE_29497(ItemID.COLOURFUL_CAPE_29497),
    COLOURFUL_CAPE_29501(ItemID.COLOURFUL_CAPE_29501),
    COLOURFUL_CAPE_29495(ItemID.COLOURFUL_CAPE_29495),
    COLOURFUL_CAPE_29499(ItemID.COLOURFUL_CAPE_29499),
    COLOURFUL_CAPE_29503(ItemID.COLOURFUL_CAPE_29503),
    COLOURFUL_CAPE_29505(ItemID.COLOURFUL_CAPE_29505),
    RAINBOW_CAPE(ItemID.RAINBOW_CAPE),

    RAINBOW_CROWN_SHIRT(ItemID.RAINBOW_CROWN_SHIRT),
    COLOURFUL_CROWN_SHIRT(ItemID.COLOURFUL_CROWN_SHIRT),
    COLOURFUL_CROWN_SHIRT_29510(ItemID.COLOURFUL_CROWN_SHIRT_29510),
    COLOURFUL_CROWN_SHIRT_29511(ItemID.COLOURFUL_CROWN_SHIRT_29511),
    COLOURFUL_CROWN_SHIRT_29512(ItemID.COLOURFUL_CROWN_SHIRT_29512),
    COLOURFUL_CROWN_SHIRT_29513(ItemID.COLOURFUL_CROWN_SHIRT_29513),
    COLOURFUL_CROWN_SHIRT_29514(ItemID.COLOURFUL_CROWN_SHIRT_29514),
    COLOURFUL_CROWN_SHIRT_29515(ItemID.COLOURFUL_CROWN_SHIRT_29515),
    COLOURFUL_CROWN_SHIRT_29516(ItemID.COLOURFUL_CROWN_SHIRT_29516);

    private final int itemID;
    @Getter
    private final String location = "Toy Box";
    @Getter
    private final boolean isBis;
    ToyBox(int itemID) {
        this.itemID = itemID;
        this.isBis = false;
    }
}
