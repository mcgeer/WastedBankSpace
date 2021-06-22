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

	String TACKLE_BOX_CHECK_KEY = "tackleBoxCheck";
	String STEEL_KEY_RING_CHECK_KEY = "keyRingCheck";
	String TOOL_LEP_CHECK_KEY = "toolLepCheck";
	String MASTER_SCROLL_BOOK_CHECK_KEY = "masterScrollBookCheck";
	String FOSSIL_STORAGE_CHECK_KEY = "fossilStorageCheck";
	String ELNOCK_INQUISITOR_CHECK_KEY = "elnockInquisitorCheck";

	/*Order
		Tackle Box
		Key Ring
		Tool Lep
		Master Scroll Book
		Fossil Island Chest

		-- Below here is for the future and Ideas--
		Flamtaer bag
		Bolt Pouch
		PoH Storage
		Check Stash Units
		Seeds - Box or Vault
		Herb Sack
		Gem Bag
		Rune Pouch

		Other storage spaces?
		Elknok for jars, update wiki storage
		Flag useless quest items - Add a tip to ask the WOM to remove Junk

		See https://oldschool.runescape.wiki/w/Storage
	 */
	@ConfigItem(keyName = "pohStorageCheck",
			name = "PoH Storage",
			description = "Check for items that could be in your PoH storage",
			position = 1
	)
	default boolean pohStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = "stashUnitCheck",
			name = "Stash Units",
			description = "Check for items that could be in unused Stash Units",
			position = 2
	)
	default boolean stashUnitCheck()
	{
		return true;
	}

	@ConfigItem(keyName = TACKLE_BOX_CHECK_KEY,
			name = "Tackle Box Items",
			description = "Check for items that could be in your tackle box",
			position = 3
	)
	default boolean tackleBoxCheck()
	{
		return true;
	}

	@ConfigItem(keyName = "boltPouchCheck",
			name = "Bolt Pouch",
			description = "Check for items that could be in your bolt pouch - Hirko in eastern Keldagrim",
			position = 4
	)
	default boolean boltPouchCheck()
	{
		return true;
	}

	@ConfigItem(keyName = STEEL_KEY_RING_CHECK_KEY,
			name = "Steel Key Ring",
			description = "Stores some keys - One small favor reward",
			position = 5
	)
	default boolean keyRingCheck()
	{
		return true;
	}

	@ConfigItem(keyName = TOOL_LEP_CHECK_KEY,
			name = "Tool Leprechaun",
			description = "Stores farming items, note low limits on many items",
			position = 6
	)
	default boolean toolLepCheck()
	{
		return true;
	}

	@ConfigItem(keyName = MASTER_SCROLL_BOOK_CHECK_KEY,
			name = "Master Scroll Book",
			description = "Stores teleport scrolls",
			position = 7
	)
	default boolean masterScrollBookCheck()
	{
		return true;
	}

	@ConfigItem(keyName = FOSSIL_STORAGE_CHECK_KEY,
			name = "Fossil Storage",
			description = "Storage for fossils on Fossil Island and in Varrok Museum",
			position = 8
	)
	default boolean fossilStorageCheck()
	{
		return true;
	}

	@ConfigItem(keyName = ELNOCK_INQUISITOR_CHECK_KEY,
			name = "Puro Pruo Storage",
			description = "Elnock Inquisitor will hold some items in Puro Puro",
			position = 9
	)
	default boolean elnockInquisitorCheck()
	{
		return true;
	}
}
