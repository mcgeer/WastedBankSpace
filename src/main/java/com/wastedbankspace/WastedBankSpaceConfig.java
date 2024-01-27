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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Wasted Bank Space")
public interface WastedBankSpaceConfig extends Config
{
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

	String FORESTRY_KIT_CHECK_KEY = "forestryKitCheck";

	@ConfigItem(keyName = CLUE_ITEM_CHECK_KEY,
			name = "PoH Clue Item Storage",
			description = "Treasure Chest Storage",
			position = 1
	)
	default boolean treasureChestStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = FANCY_DRESS_BOX_KEY,
			name = "PoH Fancy Dress Box",
			description = "Random Event Items",
			position = 2
	)
	default boolean fancyDressBoxStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = MAGIC_WARDROBE_KEY,
			name = "PoH Magic Wardrobe Storage",
			description = "Magic armour and robe sets",
			position = 3
	)
	default boolean magicWardrobeStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = SEED_CHECK_KEY,
			name = "Seed Box/Vault",
			description = "Seeds can be stored in a seed box, saplings and seeds in the vault",
			position = 4
	)
	default boolean seedCheck()
	{
		return true;
	}

	@ConfigItem(keyName = TACKLE_BOX_CHECK_KEY,
			name = "Tackle Box Items",
			description = "Check for items that could be in your tackle box",
			position = 5
	)
	default boolean tackleBoxCheck()
	{
		return true;
	}

	@ConfigItem(keyName = STEEL_KEY_RING_CHECK_KEY,
			name = "Steel Key Ring",
			description = "Stores some keys - One small favor reward",
			position = 6
	)
	default boolean keyRingCheck()
	{
		return true;
	}

	@ConfigItem(keyName = TOOL_LEP_CHECK_KEY,
			name = "Tool Leprechaun",
			description = "Stores farming items, note low limits on many items",
			position = 7
	)
	default boolean toolLepCheck()
	{
		return true;
	}

	@ConfigItem(keyName = MASTER_SCROLL_BOOK_CHECK_KEY,
			name = "Master Scroll Book",
			description = "Stores teleport scrolls",
			position = 8
	)
	default boolean masterScrollBookCheck()
	{
		return true;
	}

	@ConfigItem(keyName = FOSSIL_STORAGE_CHECK_KEY,
			name = "Fossil Storage",
			description = "Storage for fossils on Fossil Island and in Varrok Museum",
			position = 9
	)
	default boolean fossilStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = ELNOCK_INQUISITOR_CHECK_KEY,
			name = "Puro Puro Storage",
			description = "Elnock Inquisitor will hold some items in Puro Puro",
			position = 10
	)
	default boolean elnockInquisitorCheck()
	{
		return true;
	}

	@ConfigItem(keyName = FLAMTAER_BAG_CHECK_KEY,
			name = "Flamtaer Bag Storage",
			description = "Storage for Shades of Morton (minigame) items",
			position = 11
	)
	default boolean flamtaerBagCheck()
	{
		return true;
	}

	@ConfigItem(keyName = NIGHTMARE_ZONE_CHECK_KEY,
			name = "Nightmare Zone Potion Storage",
			description = "Potion's which store in the barrels at the nmz (minigame)",
			position = 12
	)
	default boolean nightmareZoneCheck()
	{
		return true;
	}

	@ConfigItem(keyName = TOY_BOX_CHECK_KEY,
			name = "PoH Toy Box Storage",
			description = "Toy and holiday item  storage",
			position = 13
	)
	default	boolean toyBoxCheck()
	{
		return true;
	}

	@ConfigItem(keyName = SPICE_RACK_CHECK_KEY,
			name = "Spice rack storage",
			description = "Storage for spicy stew spices in the PoH kitchen",
			position = 13)
	default boolean spiceRackStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = FORESTRY_KIT_CHECK_KEY,
			name = "Forestry kit storage",
			description = "Storage for Forestry-related items",
			position = 14)
	default boolean forestryKitCheck()
	{
		return true;
	}
}