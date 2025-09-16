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

package com.wastedbankspace;

import com.wastedbankspace.ui.overlay.OverlayImage;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.util.Map;

import static java.util.Map.entry;

@ConfigGroup(WastedBankSpaceConfig.GROUP)
public interface WastedBankSpaceConfig extends Config
{
	String GROUP = "Wasted Bank Space";
	/* Storage Location Keys */
	String CLUE_ITEM_CHECK_KEY = "clueItemCheck";
	String FANCY_DRESS_BOX_KEY = "poHFancyDressBox";
	String MAGIC_WARDROBE_KEY = "poHMagicWardrobe";
	String SEED_CHECK_KEY = "seedCheck";
	String TACKLE_BOX_CHECK_KEY = "tackleBoxCheck";
	String STEEL_KEY_RING_CHECK_KEY = "keyRingCheck";
	String TOOL_LEP_CHECK_KEY = "toolLepCheck";
	String MASTER_SCROLL_BOOK_CHECK_KEY = "masterScrollBookCheck";
	String FOSSIL_STORAGE_CHECK_KEY = "fossilStorageCheck";
	String ELNOCK_INQUISITOR_CHECK_KEY = "elnockInquisitorCheck";
	String FLAMTAER_BAG_CHECK_KEY = "flamtaerBagCheck";
	String NIGHTMARE_ZONE_CHECK_KEY = "nightmareZoneCheck";
	String TOY_BOX_CHECK_KEY = "toyBoxCheck";
	String SPICE_RACK_CHECK_KEY = "spiceRackCheck";
	String ARMOUR_CASE_CHECK_KEY = "armourCaseCheck";
	String FORESTRY_KIT_CHECK_KEY = "forestryKitCheck";
	String MYSTERIOUS_STRANGER_CHECK_KEY = "mysteriousStrangerCheck";
	String PET_HOUSE_SPACE_CHECK_KEY = "petHouseSpaceCheck";
	String HOUSE_BOOKCASE_CHECK_KEY = "bookcaseHouseSpaceCheck";
	String CAPE_RACK_CHECK_KEY = "capeRackCheck";
	String HUNTSMANS_KIT_SPACE_CHECK_KEY = "huntsmansKitSpaceCheck";

	/* Filtering and Blacklist Flags */
	String FILTER_ENABLED_CHECK_KEY = "filterEnabledCheck";
	String BIS_FILTER_ENABLED_CHECK_KEY = "bisFilterEnabledCheck";

	static Map<String, String> getStorageLocationKeys()
	{
		return Map.ofEntries(
			entry(CLUE_ITEM_CHECK_KEY, CLUE_ITEM_CHECK_KEY),
			entry(FANCY_DRESS_BOX_KEY, FANCY_DRESS_BOX_KEY),
			entry(MAGIC_WARDROBE_KEY, MAGIC_WARDROBE_KEY),
			entry(SEED_CHECK_KEY, SEED_CHECK_KEY),
			entry(TACKLE_BOX_CHECK_KEY, TACKLE_BOX_CHECK_KEY),
			entry(STEEL_KEY_RING_CHECK_KEY, STEEL_KEY_RING_CHECK_KEY),
			entry(TOOL_LEP_CHECK_KEY, TOOL_LEP_CHECK_KEY),
			entry(MASTER_SCROLL_BOOK_CHECK_KEY, MASTER_SCROLL_BOOK_CHECK_KEY),
			entry(FOSSIL_STORAGE_CHECK_KEY, FOSSIL_STORAGE_CHECK_KEY),
			entry(ELNOCK_INQUISITOR_CHECK_KEY, ELNOCK_INQUISITOR_CHECK_KEY),
			entry(FLAMTAER_BAG_CHECK_KEY, FLAMTAER_BAG_CHECK_KEY),
			entry(NIGHTMARE_ZONE_CHECK_KEY, NIGHTMARE_ZONE_CHECK_KEY),
			entry(TOY_BOX_CHECK_KEY, TOY_BOX_CHECK_KEY),
			entry(SPICE_RACK_CHECK_KEY, SPICE_RACK_CHECK_KEY),
			entry(ARMOUR_CASE_CHECK_KEY, ARMOUR_CASE_CHECK_KEY),
			entry(FORESTRY_KIT_CHECK_KEY, FORESTRY_KIT_CHECK_KEY),
			entry(MYSTERIOUS_STRANGER_CHECK_KEY, MYSTERIOUS_STRANGER_CHECK_KEY),
			entry(PET_HOUSE_SPACE_CHECK_KEY, PET_HOUSE_SPACE_CHECK_KEY),
			entry(HOUSE_BOOKCASE_CHECK_KEY, HOUSE_BOOKCASE_CHECK_KEY),
			entry(CAPE_RACK_CHECK_KEY, CAPE_RACK_CHECK_KEY),
			entry(HUNTSMANS_KIT_SPACE_CHECK_KEY, HUNTSMANS_KIT_SPACE_CHECK_KEY));
	}

	@ConfigSection(
		name = "General Config",
		description = "General plugin config flags",
		position = 0
	)
	String generalConfig = "generalConfig";

	@ConfigSection(
		name = "Non Flagged Items",
		description = "Items blacklisted for being flagged",
		position = 1
	)
	String nonFlaggedItems = "nonFlaggedItems";

	@ConfigSection(
		name = "Bank Interface Config",
		description = "Configure bank interface changes",
		position = 2
	)
	String bankInterfaceConfig = "bankInterfaceConfig";

	@ConfigItem(keyName = FILTER_ENABLED_CHECK_KEY,
		name = "Enable Filtering",
		description = "Enable Item Filters and Blacklist",
		position = 0,
		section = nonFlaggedItems
	)
	default boolean filterEnabledCheck()
	{
		return true;
	}

	@ConfigItem(keyName = BIS_FILTER_ENABLED_CHECK_KEY,
		name = "Never Filter BIS",
		description = "Never Filter Best in Slot Items",
		position = 1,
		section = nonFlaggedItems
	)
	default boolean bisFilterEnabledCheck()
	{
		return true;
	}

	@ConfigItem(
		keyName = "nonFlaggedItems",
		name = "",
		description = "Configures items to not be flagged as wasted bank space. Format: item, item",
		section = nonFlaggedItems,
		hidden = true
	)
	default String nonFlaggedItems()
	{
		return "";
	}

	@ConfigItem(
		keyName = "nonFlaggedItems",
		name = "",
		description = "Setter for storing notes data"
	)
	void nonFlaggedItems(String str);

	@ConfigItem(keyName = CLUE_ITEM_CHECK_KEY,
		name = "PoH Clue Item Storage",
		description = "Treasure Chest Storage",
		position = 1,
		section = generalConfig
	)
	default boolean treasureChestStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = FANCY_DRESS_BOX_KEY,
		name = "PoH Fancy Dress Box",
		description = "Random Event Items",
		position = 2,
		section = generalConfig
	)
	default boolean fancyDressBoxStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = MAGIC_WARDROBE_KEY,
		name = "PoH Magic Wardrobe Storage",
		description = "Magic armour and robe sets",
		position = 3,
		section = generalConfig
	)
	default boolean magicWardrobeStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = SEED_CHECK_KEY,
		name = "Seed Box/Vault",
		description = "Seeds can be stored in a seed box, saplings and seeds in the vault",
		position = 4,
		section = generalConfig
	)
	default boolean seedVaultStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = TACKLE_BOX_CHECK_KEY,
		name = "Tackle Box Items",
		description = "Check for items that could be in your tackle box",
		position = 5,
		section = generalConfig
	)
	default boolean tackleBoxStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = STEEL_KEY_RING_CHECK_KEY,
		name = "Steel Key Ring",
		description = "Stores some keys - One small favor reward",
		position = 6,
		section = generalConfig
	)
	default boolean steelKeyRingStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = TOOL_LEP_CHECK_KEY,
		name = "Tool Leprechaun",
		description = "Stores farming items, note low limits on many items",
		position = 7,
		section = generalConfig
	)
	default boolean toolLeprechaunStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = MASTER_SCROLL_BOOK_CHECK_KEY,
		name = "Master Scroll Book",
		description = "Stores teleport scrolls",
		position = 8,
		section = generalConfig
	)
	default boolean masterScrollBookStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = FOSSIL_STORAGE_CHECK_KEY,
		name = "Fossil Storage",
		description = "Storage for fossils on Fossil Island and in Varrock Museum",
		position = 9,
		section = generalConfig
	)
	default boolean fossilStorageStrorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = ELNOCK_INQUISITOR_CHECK_KEY,
		name = "Puro Puro Storage",
		description = "Elnock Inquisitor will hold some items in Puro Puro",
		position = 10,
		section = generalConfig
	)
	default boolean elnockInquisitorStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = FLAMTAER_BAG_CHECK_KEY,
		name = "Flamtaer Bag Storage",
		description = "Storage for Shades of Morton (minigame) items",
		position = 11,
		section = generalConfig
	)
	default boolean flamtaerBagStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = NIGHTMARE_ZONE_CHECK_KEY,
		name = "Nightmare Zone Potion Storage",
		description = "Potion's which store in the barrels at the nmz (minigame)",
		position = 12,
		section = generalConfig
	)
	default boolean nightmareZoneStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = TOY_BOX_CHECK_KEY,
		name = "PoH Toy Box Storage",
		description = "Toy and holiday item storage",
		position = 13,
		section = generalConfig
	)
	default boolean toyBoxStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = SPICE_RACK_CHECK_KEY,
		name = "Spice rack storage",
		description = "Storage for spicy stew spices in the PoH kitchen",
		position = 14,
		section = generalConfig
	)
	default boolean spiceRackStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = FORESTRY_KIT_CHECK_KEY,
		name = "Forestry kit storage",
		description = "Storage for Forestry-related items",
		position = 15,
		section = generalConfig
	)
	default boolean forestryKitStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = ARMOUR_CASE_CHECK_KEY,
		name = "PoH Armour Case storage",
		description = "Armour and outfit storage",
		position = 16,
		section = generalConfig
	)
	default boolean armourCaseStorageCheck()
	{
		return false; // Only for people to turn on if they want it on, nobody wants BIS gear with little triangles
	}

	@ConfigItem(keyName = MYSTERIOUS_STRANGER_CHECK_KEY,
		name = "Mysterious Stranger",
		description = "Storage for Hallowed Sepulchre items",
		position = 17,
		section = generalConfig
	)
	default boolean mysteriousStrangerStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = PET_HOUSE_SPACE_CHECK_KEY,
		name = "Pet House Space",
		description = "Pet House Space inside the POH",
		position = 18,
		section = generalConfig
	)
	default boolean petHouseStorageCheck()
	{
		return false; // Disabled by Default. Most people don't lose pets in their bank alike BIS gear.
	}

	@ConfigItem(keyName = HOUSE_BOOKCASE_CHECK_KEY,
		name = "PoH Bookcase",
		description = "Bookcase inside the PoH",
		position = 19,
		section = generalConfig
	)
	default boolean bookcaseStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = CAPE_RACK_CHECK_KEY,
		name = "Cape rack storage",
		description = "Storage for capes in the costume room",
		position = 20,
		section = generalConfig
	)
	default boolean capeRackStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = HUNTSMANS_KIT_SPACE_CHECK_KEY,
		name = "Huntsman's Kit",
		description = "Check for items that could go in your Huntsman's kit",
		position = 21,
		section = generalConfig
	)
	default boolean huntsmansKitStorageCheck()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overlayImage",
		name = "Overlay Image",
		description = "Configures images to overlay on items wasting space",
		position = 0,
		section = bankInterfaceConfig
	)
	default OverlayImage overlayImage()
	{
		return OverlayImage.DEFAULT;
	}
}


