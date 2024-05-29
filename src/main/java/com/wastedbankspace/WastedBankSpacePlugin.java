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

import com.google.inject.Provides;
import com.wastedbankspace.model.StorableItem;
import com.wastedbankspace.model.StorageLocation;
import com.wastedbankspace.model.StorageLocationEnabler;
import com.wastedbankspace.ui.WastedBankSpacePanel;
import com.wastedbankspace.ui.overlay.StorageItemOverlay;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.*;


@Slf4j
@PluginDescriptor(
	name = "Wasted Bank Space"
)
public class WastedBankSpacePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private StorageItemOverlay storageItemOverlay;

	@Inject
	private TooltipManager tooltipManager;

	@Inject
	private WastedBankSpaceConfig config;

	@Provides
	WastedBankSpaceConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WastedBankSpaceConfig.class);
	}

	private static final BufferedImage ICON = ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/overlaySmoll.png");
	public static final String CONFIG_GROUP = "Wasted Bank Space";
	private static boolean prepared = false;

	private final List<StorageLocationEnabler> storageLocationEnablers = Arrays.asList(
			new StorageLocationEnabler(StorageLocation.TACKLE_BOX, () -> config.tackleBoxCheck(), StorableItem.tackleBoxItems),
			new StorageLocationEnabler(StorageLocation.STEEL_KEY_RING, () -> config.keyRingCheck(), StorableItem.steelKeyRingItems),
			new StorageLocationEnabler(StorageLocation.TOOL_LEP, () -> config.toolLepCheck(), StorableItem.toolLepItems),
			new StorageLocationEnabler(StorageLocation.MASTER_SCROLL_BOOK, () -> config.masterScrollBookCheck(), StorableItem.masterScrollBookItems),
			new StorageLocationEnabler(StorageLocation.FOSSIL_STORAGE, () -> config.fossilStorageCheck(), StorableItem.fossilStorageItems),
			new StorageLocationEnabler(StorageLocation.PURO_PURO, () -> config.elnockInquisitorCheck(), StorableItem.puroPuroItems),
			new StorageLocationEnabler(StorageLocation.FLAMTAER_BAG, () -> config.flamtaerBagCheck(), StorableItem.flamtaerBagItems),
			new StorageLocationEnabler(StorageLocation.NIGHTMARE_ZONE, () -> config.nightmareZoneCheck(), StorableItem.nightmareZoneItems),
			new StorageLocationEnabler(StorageLocation.SEEDS, () -> config.seedCheck(), StorableItem.seedItems),
			new StorageLocationEnabler(StorageLocation.OAK_TREASURE_CHEST, () -> config.treasureChestStorageCheck(), StorableItem.oakTreasureChestItems),
			new StorageLocationEnabler(StorageLocation.TEAK_TREASURE_CHEST, () -> config.treasureChestStorageCheck(), StorableItem.teakTreasureChestItems),
			new StorageLocationEnabler(StorageLocation.MAHOGANY_TREASURE_CHEST, () -> config.treasureChestStorageCheck(), StorableItem.mahoganyTreasureChestItems),
			new StorageLocationEnabler(StorageLocation.FANCY_DRESS_BOX, () -> config.fancyDressBoxStorageCheck(), StorableItem.fancyDressBoxItems),
			new StorageLocationEnabler(StorageLocation.MAGIC_WARDROBE, () -> config.magicWardrobeStorageCheck(), StorableItem.magicWardrobeItems),
			new StorageLocationEnabler(StorageLocation.TOY_BOX, () -> config.toyBoxCheck(), StorableItem.toyBoxItems),
			new StorageLocationEnabler(StorageLocation.SPICE_RACK, () -> config.spiceRackStorageCheck(), StorableItem.spiceRackItems),
			new StorageLocationEnabler(StorageLocation.FORESTRY_KIT, () -> config.forestryKitCheck(), StorableItem.forestryKitItems),
			new StorageLocationEnabler(StorageLocation.ARMOUR_CASE, () -> config.armourCaseStorageCheck(), StorableItem.armourCaseItems),
			new StorageLocationEnabler(StorageLocation.MYSTERIOUS_STRANGER, () -> config.mysteriousStrangerCheck(), StorableItem.mysteriousStrangerItems),
			new StorageLocationEnabler(StorageLocation.PET_HOUSE_SPACE, () -> config.petHouseSpaceCheck(), StorableItem.petHouseSpaceItems),
			new StorageLocationEnabler(StorageLocation.BOOKCASE_HOUSE_SPACE, () -> config.houseBookcaseCheck(), StorableItem.bookcaseHouseSpaceItems)
	);

	private NavigationButton navButton;
	private WastedBankSpacePanel panel;
	private Map<Integer, Integer> inventoryMap = new HashMap<>();

	@Override
	protected void startUp() throws Exception
	{
		panel = new WastedBankSpacePanel(client, tooltipManager, config, itemManager);
		navButton = NavigationButton.builder()
				.tooltip("Wasted Bank Space")
				.priority(8)
				.icon(ICON)
				.panel(panel)
				.build();
		clientToolbar.addNavigation(navButton);

		overlayManager.add(storageItemOverlay);

		if (!prepared)
		{
			clientThread.invoke(() ->
			{
				switch (client.getGameState())
				{
					case LOGIN_SCREEN:
					case LOGIN_SCREEN_AUTHENTICATOR:
					case LOGGING_IN:
					case LOADING:
					case LOGGED_IN:
					case CONNECTION_LOST:
					case HOPPING:
						StorableItem.prepareStorableItemNames(itemManager);
						prepared = true;
						return true;
					default:
						return false;
				}
			});
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
		overlayManager.remove(storageItemOverlay);

		navButton = null;
		panel = null;
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged ev)
	{
		if (ev.getContainerId() == InventoryID.BANK.getId())
		{
			updateItemsFromBankContainer(ev.getItemContainer());
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

	private void updateItemsFromBankContainer(final ItemContainer c)
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
				() -> panel.setWastedBankSpaceItems(storableItemsInBank)
		);
	}

	public List<StorableItem>  getEnabledItemLists()
	{
		List<StorableItem> ret = new ArrayList<>();
		for (StorageLocationEnabler sle:
				storageLocationEnablers) {
			ret.addAll(sle.GetStorableItems());
		}
		return ret;
	}
}
