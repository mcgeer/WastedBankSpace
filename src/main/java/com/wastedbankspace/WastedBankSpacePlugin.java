package com.wastedbankspace;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

import com.wastedbankspace.model.StorableItem;
import com.wastedbankspace.model.StorageLocation;
import com.wastedbankspace.model.StorageLocationEnabler;
import com.wastedbankspace.ui.WastedBankSpacePanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.*;


@Slf4j
@PluginDescriptor(
	name = "Wasted Bank Space"
)
public class WastedBankSpacePlugin extends Plugin
{
	//=====Constants
	//Images stored in main/resources
	private static final BufferedImage ICON = ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/icon.png");

	//=====Inject
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

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
	public static final String CONFIG_GROUP = "Wasted Bank Space";


	private final Map<Integer, Integer> inventoryHashMap = new HashMap<>();

	private final List<StorageLocationEnabler> storageLocationEnablers = Arrays.asList(
			new StorageLocationEnabler(StorageLocation.TACKLE_BOX, () -> config.tackleBoxCheck(), StorableItem.tackleBoxItems),
			new StorageLocationEnabler(StorageLocation.STEEL_KEY_RING, () -> config.keyRingCheck(), StorableItem.steelKeyRingItems),
			new StorageLocationEnabler(StorageLocation.TOOL_LEP, () -> config.toolLepCheck(), StorableItem.toolLepItems)
	);

	//Local Disposable Properties
	private NavigationButton navButton;
	private WastedBankSpacePanel panel;
	private Map<Integer, Integer> inventoryMap = new HashMap<>();

	//=====Override Runelite Functions
	@Override
	protected void startUp() throws Exception
	{
		panel = new WastedBankSpacePanel(client, config, itemManager);
		navButton = NavigationButton.builder()
				.tooltip("Wasted Bank Space")
				.priority(8)
				.icon(ICON)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		navButton = null;
		panel = null;
	}

	//=====Event Subscriptions
	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
//			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.boltPouchCheck(), null);
		}
	}


	/*
	 * Adapted from thestonedturtle.bankedexperience;
	 */
	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged ev)
	{
		if (ev.getContainerId() == InventoryID.BANK.getId())
		{
			updateItemsFromItemContainer(ev.getContainerId(), ev.getItemContainer());
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(CONFIG_GROUP))
		{
			return;
		}

		updateWastedBankSpace();
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

		inventoryMap = m;
		updateWastedBankSpace();
	}


	private void updateWastedBankSpace()
	{
		List<StorableItem> storableItemsInBank = new ArrayList<>();
		for (StorableItem item:
				getEnabledItemLists()) {
			int id = item.itemID;
			if(inventoryMap.containsKey(id))
			{
				storableItemsInBank.add(item);
			}
		}

		SwingUtilities.invokeLater(
				() -> panel.setWastedBankSpaceItems(StorableItem.StorableListToString(storableItemsInBank, itemManager))
		);
	}

	private List<StorableItem>  getEnabledItemLists()
	{
		List<StorableItem> ret = new ArrayList<>();
		for (StorageLocationEnabler sle:
				storageLocationEnablers) {
			ret.addAll(sle.GetStorableItems());
		}
		return ret;
	}
}
