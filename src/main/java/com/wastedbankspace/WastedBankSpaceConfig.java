package com.wastedbankspace;

import lombok.Getter;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Wasted Bank Space")
public interface WastedBankSpaceConfig extends Config
{

	String TACKLE_BOX_CHECK_KEY = "tackleBoxCheck";
	String STEEL_KEY_RING_CHECK_KEY = "keyRingCheck";
	String TOOL_LEP_CHECK_KEY = "toolLepCheck";
	/*Order
		PoH Storage
		Check Stash Units
		Tackle Box
		Bolt Pouch
		-- Below here is for the future and Ideas--
		Master Scroll Book
		Key Ring
		Seeds - Box or Vault
		Herb Sack
		Gem Bag
		Rune Pouch
		Fossil Island Chest
		Tool Lep

		Other storage spaces
		Looting bag
		Hespori
		Flag useless quest items
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
			description = "Stores Farming items, note low limits on many items",
			position = 6
	)
	default boolean toolLepCheck()
	{
		return true;
	}
}
