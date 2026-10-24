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

package com.wastedbankspace;

import com.wastedbankspace.banktag.WastedBankTag;
import com.wastedbankspace.model.StorableItem;
import com.wastedbankspace.model.StorageLocationEnabler;
import com.wastedbankspace.ui.WastedBankSpacePanel;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.events.ConfigChanged;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import javax.swing.SwingUtilities;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wastedbankspace.model.StorageLocationEnums.findLocationClasses;
import static com.wastedbankspace.model.StorageLocationEnums.itemsOf;
import static com.wastedbankspace.model.StorageLocationEnums.locationOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for "Never Filter BIS" ({@link WastedBankSpaceConfig#bisFilterEnabledCheck()}) on items that more than
 * one storage location can hold, such as the Woodcutting cape (Cape Rack, marked best in slot, and Forestry Kit,
 * not marked) or the Hunter cape (Cape Rack and Huntsman's Kit).
 * <p>
 * Best in slot is a property of the item, not of the location it is listed under: while the filter is on such an
 * item must not be flagged, whichever of its locations are enabled. The plugin used to apply the filter per
 * location entry, so the kit's entry put the cape into {@link WastedBankSpacePlugin#getEnabledItems()} again.
 * <p>
 * The items are derived from the location enums rather than named, so the tests keep covering whichever items are
 * shared. Both ways the plugin builds the enabled items are checked: the start-up path (initializeItemSets) and
 * the config change / ignore list path ({@link WastedBankSpacePlugin#onConfigChanged}).
 */
public class BisFilterSharedItemsTest
{
	/** Every location entry of each storable item ID, over all storage locations */
	private static Map<Integer, List<StorableItem>> entriesByItemId;

	/** Item IDs listed by more than one location, at least one of which marks them best in slot */
	private static Set<Integer> sharedBisItems;

	/** Item IDs listed by exactly one location */
	private static Set<Integer> singleLocationItems;

	private WastedBankSpacePlugin plugin;
	private WastedBankTag bankTag;

	/** The storage location keys the fake config reports as switched on */
	private final Set<String> enabledKeys = new HashSet<>();
	private boolean bisFilterEnabled;

	/** Storage location enum to the config key that switches it on */
	private Map<Class<?>, String> keyByLocation;

	/**
	 * Copies of the plugin's static sets and maps, which initializeItemSets() fills. They are put back after each
	 * test so no state leaks into other test classes.
	 */
	private final Map<Field, Object> savedStaticState = new HashMap<>();

	@BeforeClass
	public static void groupItemsByLocation()
	{
		entriesByItemId = new HashMap<>();
		for (Class<?> location : findLocationClasses())
		{
			for (StorableItem item : itemsOf(location))
			{
				entriesByItemId.computeIfAbsent(item.getItemID(), id -> new ArrayList<>()).add(item);
			}
		}

		sharedBisItems = new HashSet<>();
		singleLocationItems = new HashSet<>();
		for (Map.Entry<Integer, List<StorableItem>> entry : entriesByItemId.entrySet())
		{
			List<StorableItem> entries = entry.getValue();
			if (entries.size() == 1)
			{
				singleLocationItems.add(entry.getKey());
			}
			else if (entries.stream().anyMatch(StorableItem::isBis))
			{
				sharedBisItems.add(entry.getKey());
			}
		}
	}

	@Before
	public void setUp() throws Exception
	{
		WastedBankSpaceConfig config = mock(WastedBankSpaceConfig.class, this::answerConfig);
		bankTag = mock(WastedBankTag.class);
		WastedBankSpacePanel panel = mock(WastedBankSpacePanel.class);
		when(panel.getFilterdItemsText()).thenReturn("");

		plugin = new WastedBankSpacePlugin();
		setField(plugin, "config", config);
		setField(plugin, "bankTag", bankTag);
		setField(plugin, "panel", panel);

		keyByLocation = keyOfEachLocation();

		for (Field field : WastedBankSpacePlugin.class.getDeclaredFields())
		{
			if (Modifier.isStatic(field.getModifiers())
				&& (Set.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType())))
			{
				field.setAccessible(true);
				savedStaticState.put(field, copyOf(field.get(null)));
			}
		}
	}

	@After
	public void tearDown() throws Exception
	{
		// Recalculating hands a panel refresh to the Swing thread; let it finish before the next test starts
		SwingUtilities.invokeAndWait(() ->
		{
		});

		for (Map.Entry<Field, Object> saved : savedStaticState.entrySet())
		{
			Object current = saved.getKey().get(null);
			if (!current.equals(saved.getValue()))
			{
				replaceContents(current, copyOf(saved.getValue()));
			}
		}
	}

	@Test
	public void bisFilterSkipsItemsThatAnotherEnabledLocationMarksBestInSlot()
	{
		assertSharedItemsWithABisAndANonBisEntryExist();
		bisFilterEnabled = true;
		enableLocationsListing(sharedBisItems);

		Set<Integer> enabled = enabledItemsAfterConfigChange();

		assertFalse("nothing is flagged, so the locations were not switched on", enabled.isEmpty());
		for (int itemId : sharedBisItems)
		{
			assertFalse(describe(itemId) + " is flagged although the BIS filter is on", enabled.contains(itemId));
		}
	}

	@Test
	public void sharedBisItemsAreFlaggedWhileTheBisFilterIsOff()
	{
		bisFilterEnabled = false;
		enableLocationsListing(sharedBisItems);

		Set<Integer> enabled = enabledItemsAfterConfigChange();

		for (int itemId : sharedBisItems)
		{
			assertTrue(describe(itemId) + " is not flagged although the BIS filter is off", enabled.contains(itemId));
		}
	}

	@Test
	public void bisFilterSkipsSharedItemsEvenWhenOnlyTheLocationsNotMarkingThemBisAreEnabled()
	{
		assertSharedItemsWithABisAndANonBisEntryExist();
		bisFilterEnabled = true;

		for (int itemId : sharedBisItems)
		{
			List<StorableItem> nonBisEntries = entriesByItemId.get(itemId).stream()
				.filter(entry -> !entry.isBis())
				.collect(Collectors.toList());
			if (nonBisEntries.isEmpty())
			{
				continue;
			}

			enabledKeys.clear();
			for (StorableItem entry : nonBisEntries)
			{
				enable(locationOf(entry));
			}
			Set<Integer> enabled = enabledItemsAfterConfigChange();

			assertFalse("nothing is flagged, so the locations of " + describe(itemId) + " were not switched on",
				enabled.isEmpty());
			assertFalse(describe(itemId) + " is flagged by " + names(nonBisEntries) + " although the BIS filter is on",
				enabled.contains(itemId));
		}
	}

	@Test
	public void singleLocationItemsAreFlaggedUnlessBestInSlotWhileTheBisFilterIsOn()
	{
		bisFilterEnabled = true;
		enableAllLocations();

		Set<Integer> enabled = enabledItemsAfterConfigChange();

		for (int itemId : singleLocationItems)
		{
			StorableItem item = entriesByItemId.get(itemId).get(0);
			assertEquals(describe(itemId) + " flagged", !item.isBis(), enabled.contains(itemId));
		}
	}

	@Test
	public void singleLocationItemsAreAllFlaggedWhileTheBisFilterIsOff()
	{
		bisFilterEnabled = false;
		enableAllLocations();

		Set<Integer> enabled = enabledItemsAfterConfigChange();

		for (int itemId : singleLocationItems)
		{
			assertTrue(describe(itemId) + " is not flagged", enabled.contains(itemId));
		}
	}

	@Test
	public void enabledItemsAreTheEnabledLocationsItemsMinusEveryItemMarkedBisAnywhere()
	{
		bisFilterEnabled = true;
		enableAllLocations();

		assertEquals(expectedEnabledItems(), enabledItemsAfterConfigChange());
	}

	@Test
	public void enabledItemsAreAllTheEnabledLocationsItemsWhileTheBisFilterIsOff()
	{
		bisFilterEnabled = false;
		enableAllLocations();

		assertEquals(expectedEnabledItems(), enabledItemsAfterConfigChange());
	}

	@Test
	public void startUpAppliesTheBisFilterToSharedItems() throws Exception
	{
		assertSharedItemsWithABisAndANonBisEntryExist();
		bisFilterEnabled = true;
		enableLocationsListing(sharedBisItems);

		Set<Integer> enabled = enabledItemsAfterInitializeItemSets();

		assertFalse("nothing is flagged, so the locations were not switched on", enabled.isEmpty());
		for (int itemId : sharedBisItems)
		{
			assertFalse(describe(itemId) + " is flagged although the BIS filter is on", enabled.contains(itemId));
		}
		verify(bankTag).setItems(new HashSet<>(enabled));
	}

	@Test
	public void startUpFlagsSharedBisItemsWhileTheBisFilterIsOff() throws Exception
	{
		bisFilterEnabled = false;
		enableLocationsListing(sharedBisItems);

		Set<Integer> enabled = enabledItemsAfterInitializeItemSets();

		for (int itemId : sharedBisItems)
		{
			assertTrue(describe(itemId) + " is not flagged although the BIS filter is off", enabled.contains(itemId));
		}
		verify(bankTag).setItems(new HashSet<>(enabled));
	}

	@Test
	public void startUpMatchesTheConfigChangePath() throws Exception
	{
		bisFilterEnabled = true;
		enableAllLocations();

		Set<Integer> atStartUp = new HashSet<>(enabledItemsAfterInitializeItemSets());
		Set<Integer> afterConfigChange = enabledItemsAfterConfigChange();

		assertEquals(expectedEnabledItems(), atStartUp);
		assertEquals(atStartUp, afterConfigChange);
	}

	@Test
	public void switchingOnALocationByItsConfigKeyAppliesTheBisFilterToSharedItems() throws Exception
	{
		assertSharedItemsWithABisAndANonBisEntryExist();
		bisFilterEnabled = true;
		// Start up with every location off; this also fills the per-key item sets the config change path adds from
		Set<Integer> enabled = enabledItemsAfterInitializeItemSets();
		assertTrue("something is flagged although every location is off", enabled.isEmpty());

		for (Class<?> location : locationsNotMarkingBis(sharedBisItems))
		{
			switchOn(location);
		}

		assertFalse("nothing is flagged, so the locations were not switched on", enabled.isEmpty());
		for (int itemId : sharedBisItems)
		{
			assertFalse(describe(itemId) + " is flagged after switching on a location that does not mark it BIS",
				enabled.contains(itemId));
		}
	}

	/**
	 * The bug needs an item that one location marks best in slot and another does not. Without one these tests
	 * would pass for the wrong reason, so say so instead.
	 */
	private static void assertSharedItemsWithABisAndANonBisEntryExist()
	{
		boolean found = sharedBisItems.stream()
			.anyMatch(itemId -> entriesByItemId.get(itemId).stream().anyMatch(entry -> !entry.isBis()));
		assertTrue("no item is marked best in slot by one location and listed as not by another; "
			+ "these tests no longer cover anything", found);
	}

	/**
	 * Goes in the way a config change or an edit of the ignore list does.
	 *
	 * @return the live set of items the plugin will flag
	 */
	private Set<Integer> enabledItemsAfterConfigChange()
	{
		ConfigChanged event = new ConfigChanged();
		event.setGroup(WastedBankSpaceConfig.GROUP);
		event.setKey(WastedBankSpaceConfig.BIS_FILTER_ENABLED_CHECK_KEY);
		plugin.onConfigChanged(event);
		return plugin.getEnabledItems();
	}

	/**
	 * Goes in the way start-up does, once the client is ready.
	 *
	 * @return the live set of items the plugin will flag
	 */
	private Set<Integer> enabledItemsAfterInitializeItemSets() throws Exception
	{
		Method initializeItemSets = WastedBankSpacePlugin.class.getDeclaredMethod("initializeItemSets");
		initializeItemSets.setAccessible(true);
		initializeItemSets.invoke(plugin);
		return plugin.getEnabledItems();
	}

	/**
	 * @return the items of the enabled locations, minus every item some location marks best in slot while the
	 * filter is on
	 */
	private Set<Integer> expectedEnabledItems()
	{
		Set<Integer> expected = new HashSet<>();
		for (Map.Entry<Class<?>, String> location : keyByLocation.entrySet())
		{
			if (enabledKeys.contains(location.getValue()))
			{
				for (StorableItem item : itemsOf(location.getKey()))
				{
					expected.add(item.getItemID());
				}
			}
		}
		if (bisFilterEnabled)
		{
			expected.removeIf(itemId -> entriesByItemId.get(itemId).stream().anyMatch(StorableItem::isBis));
		}
		return expected;
	}

	/**
	 * @return the locations that list any of the items without marking them best in slot, in a stable order
	 */
	private static Set<Class<?>> locationsNotMarkingBis(Collection<Integer> itemIds)
	{
		Set<Class<?>> locations = new LinkedHashSet<>();
		for (int itemId : itemIds)
		{
			for (StorableItem entry : entriesByItemId.get(itemId))
			{
				if (!entry.isBis())
				{
					locations.add(locationOf(entry));
				}
			}
		}
		return locations;
	}

	/**
	 * Switches a location on the way the config panel does: the fake config starts reporting its key as on, and
	 * the plugin gets the ConfigChanged event for that key.
	 */
	private void switchOn(Class<?> location)
	{
		enable(location);
		ConfigChanged event = new ConfigChanged();
		event.setGroup(WastedBankSpaceConfig.GROUP);
		event.setKey(keyByLocation.get(location));
		event.setOldValue("false");
		event.setNewValue("true");
		plugin.onConfigChanged(event);
	}

	private void enableAllLocations()
	{
		enabledKeys.addAll(WastedBankSpaceConfig.getStorageLocationKeys());
	}

	/**
	 * Switches on every location that lists any of the items.
	 */
	private void enableLocationsListing(Collection<Integer> itemIds)
	{
		for (int itemId : itemIds)
		{
			for (StorableItem entry : entriesByItemId.get(itemId))
			{
				enable(locationOf(entry));
			}
		}
	}

	private void enable(Class<?> location)
	{
		String key = keyByLocation.get(location);
		assertNotNull(location.getSimpleName() + " has no config key that switches it on", key);
		enabledKeys.add(key);
	}

	/**
	 * Switches on each storage location key alone and records which location's enabler turns on. That every
	 * location has exactly one key is StorageLocationWiringTest's business; here the pairing is only looked up.
	 */
	private Map<Class<?>, String> keyOfEachLocation() throws Exception
	{
		Map<Class<?>, String> keyByLocation = new HashMap<>();
		for (String key : WastedBankSpaceConfig.getStorageLocationKeys())
		{
			enabledKeys.clear();
			enabledKeys.add(key);
			for (StorageLocationEnabler enabler : enablers())
			{
				StorableItem[] items = enabler.GetStorableItemsIfEnabled();
				if (items.length > 0)
				{
					keyByLocation.put(locationOf(items[0]), key);
				}
			}
		}
		enabledKeys.clear();
		return keyByLocation;
	}

	/**
	 * Fake config: a storage location toggle is on if its key is in {@link #enabledKeys}, the BIS filter follows
	 * {@link #bisFilterEnabled}, filtering is on, and everything else returns Mockito's defaults.
	 */
	private Object answerConfig(InvocationOnMock invocation) throws Throwable
	{
		ConfigItem configItem = invocation.getMethod().getAnnotation(ConfigItem.class);
		if (configItem != null)
		{
			String key = configItem.keyName();
			if (WastedBankSpaceConfig.getStorageLocationKeys().contains(key))
			{
				return enabledKeys.contains(key);
			}
			if (key.equals(WastedBankSpaceConfig.BIS_FILTER_ENABLED_CHECK_KEY))
			{
				return bisFilterEnabled;
			}
			if (key.equals(WastedBankSpaceConfig.FILTER_ENABLED_CHECK_KEY))
			{
				return true;
			}
		}
		return RETURNS_DEFAULTS.answer(invocation);
	}

	/**
	 * @return e.g. "item 9948 (CapeRack.HUNTER_CAPE_PoHCapeRack [BIS], HuntsmansKit.HUNTER_CAPE)"
	 */
	private static String describe(int itemId)
	{
		return "item " + itemId + " (" + names(entriesByItemId.get(itemId)) + ")";
	}

	private static String names(List<StorableItem> entries)
	{
		return entries.stream()
			.map(entry -> locationOf(entry).getSimpleName() + "." + ((Enum<?>) entry).name()
				+ (entry.isBis() ? " [BIS]" : ""))
			.collect(Collectors.joining(", "));
	}

	@SuppressWarnings("unchecked")
	private List<StorageLocationEnabler> enablers() throws Exception
	{
		Field field = WastedBankSpacePlugin.class.getDeclaredField("storageLocationEnablers");
		field.setAccessible(true);
		return (List<StorageLocationEnabler>) field.get(plugin);
	}

	private static void setField(Object target, String name, Object value) throws Exception
	{
		Field field = WastedBankSpacePlugin.class.getDeclaredField(name);
		field.setAccessible(true);
		field.set(target, value);
	}

	/**
	 * @return a copy of a set, or of a map with each set value copied too, so later changes don't reach the copy
	 */
	private static Object copyOf(Object setOrMap)
	{
		if (setOrMap instanceof Set)
		{
			return new HashSet<>((Set<?>) setOrMap);
		}
		Map<Object, Object> copy = new HashMap<>();
		for (Map.Entry<?, ?> entry : ((Map<?, ?>) setOrMap).entrySet())
		{
			Object value = entry.getValue();
			copy.put(entry.getKey(), value instanceof Set ? new HashSet<>((Set<?>) value) : value);
		}
		return copy;
	}

	@SuppressWarnings("unchecked")
	private static void replaceContents(Object setOrMap, Object contents)
	{
		if (setOrMap instanceof Set)
		{
			((Set<Object>) setOrMap).clear();
			((Set<Object>) setOrMap).addAll((Set<?>) contents);
		}
		else
		{
			((Map<Object, Object>) setOrMap).clear();
			((Map<Object, Object>) setOrMap).putAll((Map<?, ?>) contents);
		}
	}
}
