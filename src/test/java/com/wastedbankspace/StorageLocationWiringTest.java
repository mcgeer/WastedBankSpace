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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.runelite.client.config.ConfigItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import static com.wastedbankspace.model.StorageLocationEnums.findLocationClasses;
import static com.wastedbankspace.model.StorageLocationEnums.itemIdsOf;
import static com.wastedbankspace.model.StorageLocationEnums.locationOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;

/**
 * Checks that every storage location in {@code model.locations} is wired into the plugin: it has a config key,
 * an enabler that switches all of its items on and off with that key, and a category of item IDs that the key
 * adds or removes when the config changes.
 */
public class StorageLocationWiringTest
{
	private List<Class<?>> locations;
	private WastedBankSpacePlugin plugin;

	/** The one storage location key the fake config reports as switched on, or null for none */
	private String switchedOnKey;

	/**
	 * Copies of the plugin's static sets and maps, which initializeItemSets() fills. They are put back after each
	 * test so no state leaks into other test classes.
	 */
	private final Map<Field, Object> savedStaticState = new HashMap<>();

	@Before
	public void setUp() throws Exception
	{
		locations = findLocationClasses();

		WastedBankSpaceConfig config = mock(WastedBankSpaceConfig.class, this::answerConfig);
		plugin = new WastedBankSpacePlugin();
		setField(plugin, "config", config);
		setField(plugin, "bankTag", mock(WastedBankTag.class));

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
	public void restoreStaticState() throws Exception
	{
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
	public void everyLocationHasOneEnablerHoldingAllOfItsItems() throws Exception
	{
		Set<Class<?>> covered = new HashSet<>();
		for (StorageLocationEnabler enabler : enablers())
		{
			StorableItem[] items = enabler.getStorableItems();
			assertTrue("an enabler has no items", items.length > 0);
			Class<?> location = locationOf(items[0]);
			assertEquals("items of the " + location.getSimpleName() + " enabler",
				Arrays.asList(location.getEnumConstants()), Arrays.asList(items));
			assertTrue(location.getSimpleName() + " has more than one enabler", covered.add(location));
		}
		assertEquals("locations with an enabler", new HashSet<>(locations), covered);
	}

	@Test
	public void noLocationIsEnabledWhileEveryStorageKeyIsOff() throws Exception
	{
		switchedOnKey = null;
		assertEquals(new ArrayList<>(), enabledLocations());
	}

	@Test
	public void storageLocationKeysAndLocationsMatchOneToOne() throws Exception
	{
		Map<String, Class<?>> locationByKey = locationSwitchedOnByEachKey();
		assertEquals("locations switched on by a storage location key",
			new HashSet<>(locations), new HashSet<>(locationByKey.values()));
		assertEquals("storage location keys", locations.size(), locationByKey.size());
	}

	@Test
	public void eachKeysConfigChangeCategoryHoldsTheItemsOfTheLocationItSwitchesOn() throws Exception
	{
		// Start from no categories, so only what initializeItemSets() adds is checked
		Map<String, Set<Integer>> itemsByCategory = itemsByCategory();
		itemsByCategory.clear();
		Method initializeItemSets = WastedBankSpacePlugin.class.getDeclaredMethod("initializeItemSets");
		initializeItemSets.setAccessible(true);
		initializeItemSets.invoke(plugin);

		assertEquals("config change categories", WastedBankSpaceConfig.getStorageLocationKeys(),
			itemsByCategory.keySet());
		for (Map.Entry<String, Class<?>> entry : locationSwitchedOnByEachKey().entrySet())
		{
			assertEquals("items added/removed when " + entry.getKey() + " changes",
				itemIdsOf(entry.getValue()), itemsByCategory.get(entry.getKey()));
		}
	}

	/**
	 * Switches on each storage location key alone and records which location's enabler turns on.
	 */
	private Map<String, Class<?>> locationSwitchedOnByEachKey() throws Exception
	{
		Map<String, Class<?>> locationByKey = new HashMap<>();
		try
		{
			for (String key : WastedBankSpaceConfig.getStorageLocationKeys())
			{
				switchedOnKey = key;
				List<Class<?>> switchedOn = enabledLocations();
				assertEquals("locations switched on by config key " + key, 1, switchedOn.size());
				locationByKey.put(key, switchedOn.get(0));
			}
		}
		finally
		{
			switchedOnKey = null;
		}
		return locationByKey;
	}

	private List<Class<?>> enabledLocations() throws Exception
	{
		List<Class<?>> enabled = new ArrayList<>();
		for (StorageLocationEnabler enabler : enablers())
		{
			StorableItem[] items = enabler.GetStorableItemsIfEnabled();
			if (items.length > 0)
			{
				enabled.add(locationOf(items[0]));
			}
		}
		return enabled;
	}

	/**
	 * Fake config: a storage location toggle is on only if its key is {@link #switchedOnKey}; everything else
	 * returns Mockito's defaults (false for booleans, so the BIS filter is off).
	 */
	private Object answerConfig(InvocationOnMock invocation) throws Throwable
	{
		ConfigItem configItem = invocation.getMethod().getAnnotation(ConfigItem.class);
		if (configItem != null && WastedBankSpaceConfig.getStorageLocationKeys().contains(configItem.keyName()))
		{
			return configItem.keyName().equals(switchedOnKey);
		}
		return RETURNS_DEFAULTS.answer(invocation);
	}

	@SuppressWarnings("unchecked")
	private List<StorageLocationEnabler> enablers() throws Exception
	{
		return (List<StorageLocationEnabler>) getField(plugin, "storageLocationEnablers");
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Set<Integer>> itemsByCategory() throws Exception
	{
		return (Map<String, Set<Integer>>) getField(null, "allStorableItemsByCategory");
	}

	private static Object getField(Object target, String name) throws Exception
	{
		Field field = WastedBankSpacePlugin.class.getDeclaredField(name);
		field.setAccessible(true);
		return field.get(target);
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
