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
import com.wastedbankspace.banktag.WastedBankTag;
import com.wastedbankspace.model.StorableItem;
import com.wastedbankspace.model.StorageLocationEnabler;
import com.wastedbankspace.model.StorageLocations;
import com.wastedbankspace.poh.PohStorageTracker;
import com.wastedbankspace.model.locations.*;
import com.wastedbankspace.model.stash.StashIconMode;
import com.wastedbankspace.model.stash.StashItem;
import com.wastedbankspace.model.stash.StashUnit;
import com.wastedbankspace.model.stash.StashUnitFilter;
import com.wastedbankspace.stash.StashTracker;
import com.wastedbankspace.ui.WastedBankSpacePanel;
import com.wastedbankspace.ui.overlay.BankTagTabOverlay;
import com.wastedbankspace.ui.overlay.OverlayImage;
import com.wastedbankspace.ui.overlay.StorageItemOverlay;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.banktags.BankTagsPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wastedbankspace.model.StorageLocations.isItemStorable;


@Slf4j
@PluginDescriptor(
	name = "Wasted Bank Space"
)
@PluginDependency(BankTagsPlugin.class)
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
	private BankTagTabOverlay bankTagTabOverlay;

	@Inject
	private WastedBankTag bankTag;

	@Inject
	private PohStorageTracker pohStorage;

	@Inject
	private StashTracker stashTracker;

	@Inject
	private TooltipManager tooltipManager;

	@Inject
	private WastedBankSpaceConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ScheduledExecutorService scheduledExecutorService;

	@Provides
	WastedBankSpaceConfig provideConfig(ConfigManager configManager)
	{

		return configManager.getConfig(WastedBankSpaceConfig.class);
	}

	private static final BufferedImage ICON = ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/overlaySmoll.png");

	private int bankContentsHash = 0;

	private NavigationButton navButton;
	private WastedBankSpacePanel panel;

	private boolean isBankOpen = false;
	private boolean bisFilterEnabled = false;

	private static boolean prepared = false;

	/**
	 * List of storage location enablers (sle) in Plugin Configuration. These map a boolean function to the list of
	 * 	storable items which they enable.
	 */
	private final List<StorageLocationEnabler> storageLocationEnablers = Arrays.asList(
		new StorageLocationEnabler(() -> config.tackleBoxStorageCheck(), TackleBox.values()),
		new StorageLocationEnabler(() -> config.steelKeyRingStorageCheck(), SteelKeyRing.values()),
		new StorageLocationEnabler(() -> config.toolLeprechaunStorageCheck(), ToolLeprechaun.values()),
		new StorageLocationEnabler(() -> config.masterScrollBookStorageCheck(), MasterScrollBook.values()),
		new StorageLocationEnabler(() -> config.fossilStorageStrorageCheck(), FossilStorage.values()),
		new StorageLocationEnabler(() -> config.elnockInquisitorStorageCheck(), ElnockInquisitor.values()),
		new StorageLocationEnabler(() -> config.flamtaerBagStorageCheck(), FlamtaerBag.values()),
		new StorageLocationEnabler(() -> config.nightmareZoneStorageCheck(), NightmareZone.values()),
		new StorageLocationEnabler(() -> config.seedVaultStorageCheck(), SeedVault.values()),
		new StorageLocationEnabler(() -> config.treasureChestStorageCheck(), TreasureChest.values()),
		new StorageLocationEnabler(() -> config.fancyDressBoxStorageCheck(), FancyDressBox.values()),
		new StorageLocationEnabler(() -> config.magicWardrobeStorageCheck(), MagicWardrobe.values()),
		new StorageLocationEnabler(() -> config.toyBoxStorageCheck(), ToyBox.values()),
		new StorageLocationEnabler(() -> config.spiceRackStorageCheck(), SpiceRack.values()),
		new StorageLocationEnabler(() -> config.forestryKitStorageCheck(), ForestryKit.values()),
		new StorageLocationEnabler(() -> config.armourCaseStorageCheck(), ArmourCase.values()),
		new StorageLocationEnabler(() -> config.mysteriousStrangerStorageCheck(), MysteriousStranger.values()),
		new StorageLocationEnabler(() -> config.petHouseStorageCheck(), PetHouse.values()),
		new StorageLocationEnabler(() -> config.bookcaseStorageCheck(), Bookcase.values()),
		new StorageLocationEnabler(() -> config.capeRackStorageCheck(), CapeRack.values()),
		new StorageLocationEnabler(() -> config.huntsmansKitStorageCheck(), HuntsmansKit.values())
	);


	/* Note: Static values, will never change once initialized */
	/**
	 * All items possible for all storage locations
	 * Note: Only set once during Initialize
	 */
	private static final Set<Integer> allStorableItems = new HashSet<>();

	/**
	 * Hashmap containing all itemIds per category. Used for enabling/disabling specific categories on config change.
	 * Note: Only set once during Initialize
	 */
	private static final Map<String, Set<Integer>> allStorableItemsByCategory = new HashMap<>();

	/**
	 * bis (Best in Slot) item ID's as tagged in each respective Storage Location.
	 * Note: Only set once during Initialize
	 */
	private static final Set<Integer> bisItems = new HashSet<>();

	/* The Below Sets are dynamic depending on config change (Panel for Ignore items),
	 items present in bank, filtered, etc. */

	/**
	 All Items ID's found in the players bank
	 */
	private final Set<Integer> itemsInBank = new HashSet<>();

	/**
	 *  Items populated from enabled storage types
	 */
	@Getter
	private final Set<Integer> enabledItems = new HashSet<>();

	/**
	 * Names of the enabled storage locations each item in enabledItems can go to, joined with " / ", by
	 * item ID, for tooltips. The panel reads it from the Swing thread, so it is replaced whole, never modified.
	 */
	private volatile Map<Integer, String> enabledLocationText = Collections.emptyMap();

	/**
	 * Set of Item IDs which are Ignored regardless of being storable
	 * 	This is Managed in the plugin's panel
	 */
	private final Set<Integer> ignoredItemIds = new HashSet<>();

	/**
	 * Items matching enabled items that are also present in bank. AKA the Items Wasting Space.
	 * ID in itemsInBank AND enabledItems AND NOT ignoredItemIds
	 */
	private final Set<Integer> storableItemsInBank = new HashSet<>();

	@Override
	protected void startUp() throws Exception
	{
		panel = new WastedBankSpacePanel(client, tooltipManager, config, itemManager, this::processIgnoreListChanged,
			this::getPanelLocationText, scheduledExecutorService);
		navButton = NavigationButton.builder()
			.tooltip("Wasted Bank Space")
			.priority(8)
			.icon(ICON)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navButton);

		overlayManager.add(storageItemOverlay);
		overlayManager.add(bankTagTabOverlay);
		bankTag.startUp();
		pohStorage.load();
		stashTracker.load();

		log.debug("Attempting to prepare WastedBankSpace upon startup.");
		if (!prepared)
		{
			log.debug("Not prepared, invoking clientThread");
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
						log.debug("Inside switch case for initializing");
						StorageLocations.prepareStorableItemNames(itemManager);
						initializeItemSets();
						bisFilterEnabled = config.bisFilterEnabledCheck();
						panel.updatePluginFilter();
						prepared = true;
						return true;
					default:
						log.debug("In default case, returning false");
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
		overlayManager.remove(bankTagTabOverlay);
		bankTag.shutDown();

		navButton = null;
		panel = null;
	}

	/**
	 * Populate data sets/maps at initialization with items ID's for what is storable/bis for fast lookups
	 * @param enumClass StorableItem child see model.locations
	 * @param configKey	Key for Storage Location from Plugin Configuration
	 * @param bisCheck Flag if bis items exist for storage location
	 * @param <E> enumClass
	 */
	private static <E extends Enum<E> & StorableItem> void populateStorageItemIds(
			Class<E> enumClass, String configKey, boolean bisCheck)
	{
		for (E item : enumClass.getEnumConstants())
		{
			int itemId = item.getItemID();
			allStorableItems.add(itemId);
			allStorableItemsByCategory.computeIfAbsent(configKey, val -> new HashSet<>()).add(itemId);
			if (bisCheck && item.isBis())
			{
				bisItems.add(itemId);
			}
		}
	}

	/**
	 *	1. populateStorageItemIds for all items at each storage location
	 * 	2. Update the initial enabledItems
	 * 	Note: this function is called only once for setup purposes
	 */
	private void initializeItemSets()
	{
		// Get all item ids from each storage category. Those with 'true' contain BIS items and need to identify them.
		populateStorageItemIds(ArmourCase.class, WastedBankSpaceConfig.ARMOUR_CASE_CHECK_KEY, true);
		populateStorageItemIds(Bookcase.class, WastedBankSpaceConfig.HOUSE_BOOKCASE_CHECK_KEY, false);
		populateStorageItemIds(CapeRack.class, WastedBankSpaceConfig.CAPE_RACK_CHECK_KEY, true);
		populateStorageItemIds(ElnockInquisitor.class, WastedBankSpaceConfig.ELNOCK_INQUISITOR_CHECK_KEY, false);
		populateStorageItemIds(FancyDressBox.class, WastedBankSpaceConfig.FANCY_DRESS_BOX_KEY, false);
		populateStorageItemIds(FlamtaerBag.class, WastedBankSpaceConfig.FLAMTAER_BAG_CHECK_KEY, false);
		populateStorageItemIds(ForestryKit.class, WastedBankSpaceConfig.FORESTRY_KIT_CHECK_KEY, false);
		populateStorageItemIds(FossilStorage.class, WastedBankSpaceConfig.FOSSIL_STORAGE_CHECK_KEY, false);
		populateStorageItemIds(HuntsmansKit.class, WastedBankSpaceConfig.HUNTSMANS_KIT_SPACE_CHECK_KEY, false);
		populateStorageItemIds(MagicWardrobe.class, WastedBankSpaceConfig.MAGIC_WARDROBE_KEY, true);
		populateStorageItemIds(MasterScrollBook.class, WastedBankSpaceConfig.MASTER_SCROLL_BOOK_CHECK_KEY, false);
		populateStorageItemIds(MysteriousStranger.class, WastedBankSpaceConfig.MYSTERIOUS_STRANGER_CHECK_KEY, false);
		populateStorageItemIds(NightmareZone.class, WastedBankSpaceConfig.NIGHTMARE_ZONE_CHECK_KEY, false);
		populateStorageItemIds(PetHouse.class, WastedBankSpaceConfig.PET_HOUSE_SPACE_CHECK_KEY, false);
		populateStorageItemIds(SeedVault.class, WastedBankSpaceConfig.SEED_CHECK_KEY, false);
		populateStorageItemIds(SpiceRack.class, WastedBankSpaceConfig.SPICE_RACK_CHECK_KEY, false);
		populateStorageItemIds(SteelKeyRing.class, WastedBankSpaceConfig.STEEL_KEY_RING_CHECK_KEY, false);
		populateStorageItemIds(TackleBox.class, WastedBankSpaceConfig.TACKLE_BOX_CHECK_KEY, false);
		populateStorageItemIds(ToolLeprechaun.class, WastedBankSpaceConfig.TOOL_LEP_CHECK_KEY, false);
		populateStorageItemIds(ToyBox.class, WastedBankSpaceConfig.TOY_BOX_CHECK_KEY, false);
		populateStorageItemIds(TreasureChest.class, WastedBankSpaceConfig.CLUE_ITEM_CHECK_KEY, false);

		// Initialize the enabled item values
		recalculateEnabledItems();
		bankTag.setItems(enabledItems);
	}

	/**
	 * Recalculates enabledItems from the enabled storage locations, minus ignored items and, while
	 * "Never Filter BIS" is on, anything any location marks as best in slot. Also records which enabled
	 * locations each flagged item can go to, for tooltips.
	 */
	private void recalculateEnabledItems()
	{
		enabledItems.clear();
		Map<Integer, Set<String>> locations = new HashMap<>();
		boolean bisFilter = config.bisFilterEnabledCheck();
		for (StorageLocationEnabler sle : storageLocationEnablers)
		{
			for (StorableItem item : sle.GetStorableItemsIfEnabled())
			{
				int itemId = item.getItemID();
				if (ignoredItemIds.contains(itemId) || (bisFilter && StorageLocations.isBestInSlot(itemId)))
				{
					continue;
				}
				enabledItems.add(itemId);
				locations.computeIfAbsent(itemId, id -> new LinkedHashSet<>()).add(item.getLocation());
			}
		}
		// STASH units are named in tooltips from getStashUnits, since which ones are filled changes while playing
		if (config.stashUnits())
		{
			for (StashItem item : StashItem.ALL)
			{
				int itemId = item.getItemID();
				if (isCounted(item.getUnit()) && !ignoredItemIds.contains(itemId)
					&& !(bisFilter && StorageLocations.isBestInSlot(itemId)))
				{
					enabledItems.add(itemId);
				}
			}
		}
		Map<Integer, String> text = new HashMap<>();
		locations.forEach((itemId, names) -> text.put(itemId, String.join(" / ", names)));
		enabledLocationText = Collections.unmodifiableMap(text);
	}

	/**
	 * Names where an item can be stored, for tooltips.
	 *
	 * @param inHouse whether the item is already in the POH costume room. Then the costume room storages
	 *                that take it are named, enabled or not, because that's where it is.
	 * @return e.g. "Cape Rack", or "Cape Rack / Forestry Kit" when more than one enabled location takes it
	 */
	public String getStorageLocationText(int itemId, boolean inHouse)
	{
		if (inHouse)
		{
			String costumeRoom = joinLocations(StorageLocations.getStorableItems(itemId).stream()
				.filter(StorageLocations::isCostumeRoomItem));
			if (!costumeRoom.isEmpty())
			{
				return costumeRoom;
			}
		}
		List<String> names = new ArrayList<>();
		String enabled = enabledLocationText.get(itemId);
		if (enabled != null)
		{
			names.add(enabled);
		}
		List<StashUnit> stashUnits = getStashUnits(itemId);
		names.addAll(stashLocations(stashUnits, false));
		if (!names.isEmpty())
		{
			return String.join(" / ", names);
		}
		if (!stashUnits.isEmpty())
		{
			// Every STASH unit that takes it is filled, which getStashTooltipLines says
			return "";
		}
		return joinLocations(StorageLocations.getStorableItems(itemId).stream());
	}

	/**
	 * Tooltip lines about STASH units, after the one from {@link #getStorageLocationText}: the units that are
	 * already filled, and, for an item in the POH costume room, the units it could still go in.
	 */
	public List<String> getStashTooltipLines(int itemId, boolean inHouse)
	{
		List<StashUnit> stashUnits = getStashUnits(itemId);
		List<String> lines = new ArrayList<>();
		List<String> unfilled = stashLocations(stashUnits, false);
		if (inHouse && !unfilled.isEmpty())
		{
			lines.add("Store @ " + String.join(" / ", unfilled));
		}
		List<String> filled = stashLocations(stashUnits, true);
		if (!filled.isEmpty())
		{
			lines.add("Already stashed @ " + String.join(" / ", filled));
		}
		return lines;
	}

	/**
	 * Panel tooltip: every location the item counts for, filled STASH units included
	 */
	private String getPanelLocationText(int itemId)
	{
		List<String> names = new ArrayList<>();
		String text = getStorageLocationText(itemId, false);
		if (!text.isEmpty())
		{
			names.add(text);
		}
		names.addAll(stashLocations(getStashUnits(itemId), true));
		return String.join(" / ", names);
	}

	private List<String> stashLocations(List<StashUnit> units, boolean filled)
	{
		return units.stream()
			.filter(unit -> stashTracker.isFilled(unit) == filled)
			.map(StashUnit::getLocation)
			.collect(Collectors.toList());
	}

	/**
	 * @return the STASH units that take the item and count under the STASH Units setting, in unit order; empty
	 * while STASH units are switched off
	 */
	public List<StashUnit> getStashUnits(int itemId)
	{
		if (!config.stashUnits())
		{
			return Collections.emptyList();
		}
		return StashItem.getUnits(itemId).stream().filter(this::isCounted).collect(Collectors.toList());
	}

	/**
	 * @return whether the item these units take counts as stashed for the STASH icon: at least one of the units is
	 * filled, or every one of them is, depending on the config
	 */
	public boolean isStashed(List<StashUnit> units)
	{
		if (config.stashIconMode() == StashIconMode.EVERY_UNIT)
		{
			return !units.isEmpty() && units.stream().allMatch(stashTracker::isFilled);
		}
		return units.stream().anyMatch(stashTracker::isFilled);
	}

	private boolean isCounted(StashUnit unit)
	{
		return config.stashUnitFilter() == StashUnitFilter.ALL || stashTracker.isBuilt(unit);
	}

	private static String joinLocations(Stream<StorableItem> items)
	{
		return items.map(StorableItem::getLocation).distinct().collect(Collectors.joining(" / "));
	}

	public OverlayImage getOverlayImage()
	{
		return config.overlayImage();
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged event)
	{
		pohStorage.load();
		stashTracker.load();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		stashTracker.onGameStateChanged(event);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		stashTracker.onVarbitChanged(event);
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		stashTracker.onScriptPreFired(event);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		stashTracker.onMenuOptionClicked(event);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		stashTracker.onChatMessage(event);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		// Built units decide which STASH items are flagged under "Only built units"
		if (stashTracker.onGameTick() && prepared && config.stashUnits()
			&& config.stashUnitFilter() == StashUnitFilter.BUILT)
		{
			recalculateEnabledItems();
			updateWastedBankSpace();
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		pohStorage.onItemContainerChanged(event);
		stashTracker.onItemContainerChanged(event);

		if (event.getContainerId() == InventoryID.BANK.getId())
		{
			isBankOpen = true;
			log.debug("isBankOpen set to true");

			// check if bank contents have changed by utilizing hashcode of items array
			Item[] items = event.getItemContainer().getItems();
			int hash = Arrays.hashCode(items);
			if (hash == bankContentsHash)
			{
				log.debug("Bank contents hash matched ({}), not running updateItemsFromBankContainer()",
					bankContentsHash);
				return;
			}
			bankContentsHash = hash;
			updateItemsFromBankContainer(event.getItemContainer());
		}
		else
		{
			isBankOpen = false;
			log.debug("isBankOpen set to false");
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(WastedBankSpaceConfig.GROUP))
		{
			return;
		}

		String eventKey = event.getKey();
		log.debug("onConfigChanged key: {}", eventKey);

		// only attempt to run updating of enabledItems hashset if event key is one of the keys that affect enabledItems
		if (WastedBankSpaceConfig.getStorageLocationKeys().contains(eventKey))
		{
			boolean result;
			if ((event.getNewValue() == null) || event.getNewValue().equalsIgnoreCase("false"))
			{
				// then config group was disabled, so remove them from the enabledItems set
				result = enabledItems.removeAll(allStorableItemsByCategory.getOrDefault(eventKey, new HashSet<>()));
			}
			else
			{
				result = enabledItems.addAll(allStorableItemsByCategory.getOrDefault(eventKey, new HashSet<>()));
			}

			if (!result)
			{
				// enabledItems failed to update from the call above, indicating an issue with
				log.debug("onConfigChanged(): Attempted update of enabledItems hashset failed.\nEvent: {}\n", event);
			}
			updateWastedBankSpace();
			processIgnoreListChanged(panel.getFilterdItemsText());
		} else if (eventKey.equals(WastedBankSpaceConfig.STASH_UNITS_KEY)
			|| eventKey.equals(WastedBankSpaceConfig.STASH_UNIT_FILTER_KEY)) {
			recalculateEnabledItems();
			updateWastedBankSpace();
		} else if (eventKey.equals(WastedBankSpaceConfig.BANK_TAG_TAB_KEY)
			|| eventKey.equals(WastedBankSpaceConfig.BANK_TAG_PLACEHOLDERS_KEY)) {
			clientThread.invokeLater(bankTag::sync);
		} else if(eventKey.equals(WastedBankSpaceConfig.FILTER_ENABLED_CHECK_KEY) || eventKey.equals(WastedBankSpaceConfig.BIS_FILTER_ENABLED_CHECK_KEY)){
			//Moderate jank to reforce filter and BIS check. TODO These should be separated into two functions
			processIgnoreListChanged(panel.getFilterdItemsText());
		} else {
			//Note this currently is hit when Overlay Image is changed but seems to have no effect on the software
			log.debug("onConfigChanged(): Event not handled! \nEvent: {}\n", event);
		}
	}

	@Subscribe
	public void onBeforeRender(BeforeRender event)
	{
		bankTag.onBeforeRender();
	}

	@Subscribe
	public void onMenuOpened(final MenuOpened event)
	{
		// TODO: refactor this
		if (!client.isKeyPressed(KeyCode.KC_SHIFT) || !isBankOpen)
		{
			return;
		}

		final MenuEntry[] entries = event.getMenuEntries();

		for (int i = entries.length - 1; i >= 0; i--)
		{
			final MenuEntry entry = entries[i];
			final Widget w = entry.getWidget();

			if (w != null && (WidgetUtil.componentToInterface(w.getId()) == InterfaceID.BANK))
			{
				final int itemId = w.getItemId();
				final boolean flagged = !ignoredItemIds.contains(itemId);
				if (isItemStorable(itemId))
				{
					final MenuEntry parent = client.createMenuEntry(i)
						.setOption(flagged ? "Unflag Item" : "Flag Item")
						.setTarget(entry.getTarget())
						.setType(MenuAction.RUNELITE)
						.onClick(x -> toggleItemInIgnoreList(itemId));
				}
				return;
			}
		}
	}

	/**
	 * Toggles whether an item is ignored or not from list of items considered wasting space.
	 *
	 * @param id Item Id for the item being toggled for ignore.
	 */
	private void toggleItemInIgnoreList(int id)
	{
		if (ignoredItemIds.contains(id))
		{
			ignoredItemIds.remove(id);
			panel.removeFilteredItem(StorageLocations.getStorableItemName(id), id);
		}
		else
		{
			ignoredItemIds.add(id);
			panel.addFilteredItem(StorageLocations.getStorableItemName(id));
		}
	}

	private void updateItemsFromBankContainer(final ItemContainer c)
	{
		// Check if the contents have changed.
		if (c == null)
		{
			return;
		}

		itemsInBank.clear();
		for (Item item : c.getItems())
		{
			if (item.getId() == -1)
			{
				continue;
			}

			// Account for noted items, ignore placeholders.
			int itemId = item.getId();
			final ItemComposition itemComposition = itemManager.getItemComposition(itemId);
			if (itemComposition.getPlaceholderTemplateId() != -1)
			{
				continue;
			}

			itemsInBank.add(itemId);
		}

		updateWastedBankSpace();
	}


	/**
	 * Calculate and update the wasted space Panel via the following algorithm:
	 * Wasted Space = Bank Items Where Item is Storable in an enabled Storable Location AND Item is not Ignored by user
	 * 	Wasted Space = (itemsInBank interset enabledItems) - ignoredItemIds
	 */
	private void updateWastedBankSpace()
	{
		log.debug("running updateWastedBankSpace, getting every item after regenerating the enabled item list");
		bankTag.setItems(enabledItems);

		// Recalculate storable items in the bank
		Set<Integer> prevStorableItemsInBank = new HashSet<>(storableItemsInBank);
		log.debug("prevStorableItemsInBank: {}", prevStorableItemsInBank);
		storableItemsInBank.clear();
		storableItemsInBank.addAll(itemsInBank);
		storableItemsInBank.retainAll(enabledItems);
		storableItemsInBank.removeAll(ignoredItemIds);
		log.debug("new storableItemsInBank: {}", storableItemsInBank);

		/* Update the panel UI only if there are changes to storable items in bank,
			except when empty. (empty will occur on startup, and in edge conditions where everything is disabled) */
		if (!storableItemsInBank.isEmpty() && prevStorableItemsInBank.equals(storableItemsInBank))
		{
			log.debug("storableItemsInBank matched previous, not updating panel");
			return;
		}
		// A copy, since this set is rebuilt on other threads while the Swing thread reads it
		Set<Integer> shown = new HashSet<>(storableItemsInBank);
		SwingUtilities.invokeLater(() -> panel.setWastedBankSpaceItems(shown));
	}

	/**
	 * Function to be invoked when the Ignore list is modified by the player in the plugin's panel.
	 * @param filter Is the Comma Separated String of Item's to ignore.
	 *               Acceptable format ItemName Ignore CASE and Spaces, Item ID Number, Item Name with '*' wild cards
	 *
	 *               Example 1: Mystic robe top, Mysticrobetop,Mysticrobetop,Mystic robe     top, mySTicrObETop
	 *               are all the same
	 *               Example 2: 12345 will try and add Item ID 12345 to the set of ignorable items if it exists
	 *               Example 3: *Mystic robe top* can be used to match ALL Mystic robe top variants
	 */
	private void processIgnoreListChanged(String filter)
	{
		log.debug("Starting processIgnoreListChanged() with filter: {}", filter);
		log.debug("Contents of StorageLocations.getItemNameMap(): {}", StorageLocations.getModifiedItemNameMap());

		List<String> ignoredItemList = Text.fromCSV(filter);
		log.debug("processIgnoreListChanged() - ignoredItemList: {}", ignoredItemList);

		ignoredItemIds.clear();
		if(config.filterEnabledCheck()) {
			/* 1. Loop each comma separated ignore Item
			 * 2. If a number, add the item ID directly
			 * 3.1. If a wild card exists
			 * 3.1.1. loop all items and try to match the wildcard pattern to the name, on hit add the
			 * 		ID to the ignore list
			 * 3.2. Otherwise, try and find the name of the item ignore case and space in the map of items
			 * 3.2.1. Add the ID if found
			 * */
			for (String ignoredItem : ignoredItemList) {
				log.debug("processIgnoreListChanged - ignoredItem: {}", ignoredItem);
				String cleanedIgnoredItem = ignoredItem.replaceAll("\\s+", "");
				log.debug("Original value: {}, ModValue: {}", ignoredItem, cleanedIgnoredItem);

				// Check if is only digits, i.e. an itemId
				if (cleanedIgnoredItem.matches("^\\d+$")) {
					try {
						ignoredItemIds.add(Integer.parseInt(cleanedIgnoredItem));
					}
					catch (NumberFormatException e) {
						log.debug("Ignoring item id out of range: {}", cleanedIgnoredItem);
					}
				}
				// Check if cleanedIgnoredItem has a corresponding itemId in the modifiedItemNameMap
				else {
					if(cleanedIgnoredItem.contains("*")) {
						/* Process Wild Card Ignores */
						// Quote everything except the wildcards, so names with regex characters still match literally.
						// Runs of "*" collapse to one: chained ".*" patterns backtrack badly over a long name list.
						String wildcardPattern = Arrays.stream(cleanedIgnoredItem.replaceAll("[*]+", "*").split("\\*", -1))
								.map(Pattern::quote)
								.collect(Collectors.joining(".*"));
						Pattern p = Pattern.compile(wildcardPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
						/* Check all items against the wildcard regex. */
						StorageLocations.getModifiedItemNameMap().entrySet().stream()
								.filter(e -> p.matcher(e.getKey()).matches())
								.forEach(e -> ignoredItemIds.add(e.getValue()));
					}
					else {
						/* Do we have a matching name? */
						Integer itemId = StorageLocations.getStorableItemId(cleanedIgnoredItem);
						if (itemId != null) {
							ignoredItemIds.add(itemId);
						}
					}
				}
			}
		}

		recalculateEnabledItems();
		updateWastedBankSpace();
	}
}
