/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, Brettgod1355
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

package com.wastedbankspace.poh;

import com.wastedbankspace.WastedBankSpaceConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Remembers which items are stored in the player's POH costume room (treasure chest, armour case, magic wardrobe,
 * cape rack, toy box and fancy dress box). The game sends the full contents of all of these as a single item
 * container whenever one of them is opened, so the stored set is refreshed each time the player opens any of them.
 * Saved per account so it survives restarts.
 */
@Slf4j
@Singleton
public class PohStorageTracker
{
	private static final String CONFIG_KEY = "pohCostumeRoomItems";
	/** Some item containers are also sent with this bit set */
	private static final int CONTAINER_ID_FLAG = 0x8000;

	private final ConfigManager configManager;

	private volatile Set<Integer> storedItems = Collections.emptySet();

	@Inject
	PohStorageTracker(ConfigManager configManager)
	{
		this.configManager = configManager;
	}

	/**
	 * Loads the stored items for the current account. Call on startup and when the RuneScape profile changes.
	 */
	public void load()
	{
		String saved = configManager.getRSProfileConfiguration(WastedBankSpaceConfig.GROUP, CONFIG_KEY);
		Set<Integer> items = new HashSet<>();
		for (String id : Text.fromCSV(saved == null ? "" : saved))
		{
			try
			{
				items.add(Integer.parseInt(id));
			}
			catch (NumberFormatException e)
			{
				log.debug("Ignoring invalid saved POH item id {}", id);
			}
		}
		storedItems = Set.copyOf(items);
	}

	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if ((event.getContainerId() & ~CONTAINER_ID_FLAG) != InventoryID.POH_COSTUMES)
		{
			return;
		}

		Set<Integer> items = new HashSet<>();
		for (Item item : event.getItemContainer().getItems())
		{
			if (item.getId() > -1)
			{
				items.add(item.getId());
			}
		}

		storedItems = Set.copyOf(items);
		configManager.setRSProfileConfiguration(WastedBankSpaceConfig.GROUP, CONFIG_KEY,
			Text.toCSV(items.stream().map(String::valueOf).collect(Collectors.toList())));
		log.debug("Updated POH costume room contents: {} items", items.size());
	}

	public boolean isStored(int itemId)
	{
		return storedItems.contains(itemId);
	}
}
