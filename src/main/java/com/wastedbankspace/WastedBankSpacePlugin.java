package com.wastedbankspace;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(
	name = "Wasted Bank Space"
)

public class WastedBankSpacePlugin extends Plugin
{
	//=====Consts

	//=====Inject
	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private WastedBankSpaceConfig config;

	//=====Provides
	@Provides
	WastedBankSpaceConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WastedBankSpaceConfig.class);
	}

	//=====Local Properties
	private final Map<Integer, Integer> inventoryHashMap = new HashMap<>();

	//=====Override Runelite Functions
	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	//=====Event Subscriptions
	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.boltPouchCheck(), null);
		}
	}


	/*
	 * Adapted from thestonedturtle.bankedexperience;
	 */
	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged ev)
	{
		if (ev.getContainerId() == InventoryID.BANK.getId()
				/*|| (ev.getContainerId() == InventoryID.SEED_VAULT.getId() && config.grabFromSeedVault())
				|| (ev.getContainerId() == InventoryID.INVENTORY.getId() && config.grabFromInventory())
				|| (ev.getContainerId() == LOOTING_BAG_ID && config.grabFromLootingBag())*/)
		{
			updateItemsFromItemContainer(ev.getContainerId(), ev.getItemContainer());
		}
	}

	//=====Private Functions
	private void updateItemsFromItemContainer(final int inventoryId, final ItemContainer c)
	{
		// Check if the contents have changed.
		if (c == null)
		{
			return;
		}

		final Map<Integer, Integer> m = new HashMap<>();
		for (Item item : c.getItems())
		{
			if (item.getId() == -1)
			{
				continue;
			}

			// Account for noted items, ignore placeholders.
			int itemID = item.getId();
			final ItemComposition itemComposition = itemManager.getItemComposition(itemID);
			if (itemComposition.getPlaceholderTemplateId() != -1)
			{
				continue;
			}

			if (itemComposition.getNote() != -1)
			{
				itemID = itemComposition.getLinkedNoteId();
			}

			final int qty = m.getOrDefault(itemID, 0) + item.getQuantity();
			m.put(itemID, qty);
		}

		updateInventoryMap(inventoryId, m);
	}

	private void updateInventoryMap(final int inventoryId, final Map<Integer, Integer> m)
	{
		final int curHash = m.hashCode();
		if (curHash != inventoryHashMap.getOrDefault(inventoryId, -1))
		{
			inventoryHashMap.put(inventoryId, curHash);
			log.debug("Inventory Id Added to Inventory Map: " + String.valueOf(inventoryId));
			//SwingUtilities.invokeLater(() -> panel.setInventoryMap(inventoryId, m));
		}

		if(m.containsKey(314))
		{
			log.debug("FEATHER FOUND");
		}
	}
}
