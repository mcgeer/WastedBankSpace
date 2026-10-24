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

import com.wastedbankspace.ui.overlay.HouseIcon;
import com.wastedbankspace.ui.overlay.OverlayImage;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WastedBankSpaceConfigTest
{
	/**
	 * Nothing overridden except the blacklist setter, so every option returns its default value.
	 */
	private final WastedBankSpaceConfig defaults = new WastedBankSpaceConfig()
	{
		@Override
		public void nonFlaggedItems(String str)
		{
		}
	};

	@Test
	public void inventoryAndEquipmentMarkingIsOffByDefault()
	{
		// Agreed with the user: marking items outside the bank is opt-in
		assertFalse(defaults.markInventoryItems());
		assertFalse(defaults.markEquippedItems());
	}

	@Test
	public void alreadyInHouseDefaults()
	{
		// Agreed with the user: house icon shown everywhere, as the teleport spell, beside the marker
		assertTrue(defaults.houseIconInBank());
		assertTrue(defaults.houseIconInInventory());
		assertEquals(HouseIcon.TELEPORT_SPELL, defaults.houseIcon());
		assertFalse(defaults.houseIconReplacesMarker());
	}

	@Test
	public void bankInterfaceDefaults()
	{
		assertEquals(OverlayImage.DEFAULT, defaults.overlayImage());

		// Agreed with the user: wasted tab on, placeholders marked and listed in the tab
		assertTrue(defaults.bankTagTab());
		assertTrue(defaults.markPlaceholders());
		assertTrue(defaults.bankTagPlaceholders());
	}

	@Test
	public void filteringIsOnAndBlacklistIsEmptyByDefault()
	{
		assertTrue(defaults.filterEnabledCheck());
		assertTrue(defaults.bisFilterEnabledCheck());
		assertEquals("", defaults.nonFlaggedItems());
	}

	@Test
	public void storageLocationDefaults()
	{
		// Off: best-in-slot armour and pets would otherwise be flagged in almost every bank
		assertFalse(defaults.armourCaseStorageCheck());
		assertFalse(defaults.petHouseStorageCheck());

		// On: every other storage location
		assertTrue(defaults.treasureChestStorageCheck());
		assertTrue(defaults.fancyDressBoxStorageCheck());
		assertTrue(defaults.magicWardrobeStorageCheck());
		assertTrue(defaults.seedVaultStorageCheck());
		assertTrue(defaults.tackleBoxStorageCheck());
		assertTrue(defaults.steelKeyRingStorageCheck());
		assertTrue(defaults.toolLeprechaunStorageCheck());
		assertTrue(defaults.masterScrollBookStorageCheck());
		assertTrue(defaults.fossilStorageStrorageCheck());
		assertTrue(defaults.elnockInquisitorStorageCheck());
		assertTrue(defaults.flamtaerBagStorageCheck());
		assertTrue(defaults.nightmareZoneStorageCheck());
		assertTrue(defaults.toyBoxStorageCheck());
		assertTrue(defaults.spiceRackStorageCheck());
		assertTrue(defaults.forestryKitStorageCheck());
		assertTrue(defaults.mysteriousStrangerStorageCheck());
		assertTrue(defaults.bookcaseStorageCheck());
		assertTrue(defaults.capeRackStorageCheck());
		assertTrue(defaults.huntsmansKitStorageCheck());
	}

	@Test
	public void configGroupNameIsUnchanged()
	{
		// Every saved setting lives under this group, so renaming it resets everyone's config
		assertEquals("Wasted Bank Space", WastedBankSpaceConfig.GROUP);
		assertEquals(WastedBankSpaceConfig.GROUP, WastedBankSpaceConfig.class.getAnnotation(ConfigGroup.class).value());
	}

	@Test
	public void savedSettingKeysAreUnchanged()
	{
		// RuneLite saves each option under its keyName; renaming a key silently resets that setting for every user
		Map<String, String> expectedKeys = new LinkedHashMap<>();
		expectedKeys.put("markInventoryItems", "markInventoryItems");
		expectedKeys.put("markEquippedItems", "markEquippedItems");
		expectedKeys.put("houseIconInBank", "houseIconInBank");
		expectedKeys.put("houseIconInInventory", "houseIconInInventory");
		expectedKeys.put("houseIcon", "houseIcon");
		expectedKeys.put("houseIconReplacesMarker", "houseIconReplacesMarker");
		expectedKeys.put("filterEnabledCheck", "filterEnabledCheck");
		expectedKeys.put("bisFilterEnabledCheck", "bisFilterEnabledCheck");
		expectedKeys.put("nonFlaggedItems", "nonFlaggedItems");
		expectedKeys.put("treasureChestStorageCheck", "clueItemCheck");
		expectedKeys.put("fancyDressBoxStorageCheck", "poHFancyDressBox");
		expectedKeys.put("magicWardrobeStorageCheck", "poHMagicWardrobe");
		expectedKeys.put("seedVaultStorageCheck", "seedCheck");
		expectedKeys.put("tackleBoxStorageCheck", "tackleBoxCheck");
		expectedKeys.put("steelKeyRingStorageCheck", "keyRingCheck");
		expectedKeys.put("toolLeprechaunStorageCheck", "toolLepCheck");
		expectedKeys.put("masterScrollBookStorageCheck", "masterScrollBookCheck");
		expectedKeys.put("fossilStorageStrorageCheck", "fossilStorageCheck");
		expectedKeys.put("elnockInquisitorStorageCheck", "elnockInquisitorCheck");
		expectedKeys.put("flamtaerBagStorageCheck", "flamtaerBagCheck");
		expectedKeys.put("nightmareZoneStorageCheck", "nightmareZoneCheck");
		expectedKeys.put("toyBoxStorageCheck", "toyBoxCheck");
		expectedKeys.put("spiceRackStorageCheck", "spiceRackCheck");
		expectedKeys.put("forestryKitStorageCheck", "forestryKitCheck");
		expectedKeys.put("armourCaseStorageCheck", "armourCaseCheck");
		expectedKeys.put("mysteriousStrangerStorageCheck", "mysteriousStrangerCheck");
		expectedKeys.put("petHouseStorageCheck", "petHouseSpaceCheck");
		expectedKeys.put("bookcaseStorageCheck", "bookcaseHouseSpaceCheck");
		expectedKeys.put("capeRackStorageCheck", "capeRackCheck");
		expectedKeys.put("huntsmansKitStorageCheck", "huntsmansKitSpaceCheck");
		expectedKeys.put("overlayImage", "overlayImage");
		expectedKeys.put("bankTagTab", "bankTagTab");
		expectedKeys.put("markPlaceholders", "markPlaceholders");
		expectedKeys.put("bankTagPlaceholders", "bankTagPlaceholders");

		for (Map.Entry<String, String> expected : expectedKeys.entrySet())
		{
			Method getter;
			try
			{
				getter = WastedBankSpaceConfig.class.getMethod(expected.getKey());
			}
			catch (NoSuchMethodException e)
			{
				throw new AssertionError("Option " + expected.getKey() + "() no longer exists", e);
			}
			assertEquals("keyName of " + expected.getKey() + "()", expected.getValue(), item(getter).keyName());
		}
	}

	@Test
	public void everyOptionHasItsOwnKey()
	{
		Map<String, List<Method>> methodsByKey = new HashMap<>();
		for (Method method : configItemMethods())
		{
			methodsByKey.computeIfAbsent(item(method).keyName(), k -> new ArrayList<>()).add(method);
		}

		for (Map.Entry<String, List<Method>> entry : methodsByKey.entrySet())
		{
			List<Method> methods = entry.getValue();
			if (methods.size() > 1 && !isGetterSetterPair(methods))
			{
				fail("keyName \"" + entry.getKey() + "\" is used by more than one option: " + methods);
			}
		}
	}

	@Test
	public void blacklistSetterSavesUnderTheKeyTheGetterReads() throws NoSuchMethodException
	{
		// The side panel saves the blacklist through the setter and reads it back through the getter
		Method getter = WastedBankSpaceConfig.class.getMethod("nonFlaggedItems");
		Method setter = WastedBankSpaceConfig.class.getMethod("nonFlaggedItems", String.class);

		assertNotNull("setter needs @ConfigItem or RuneLite never saves it", setter.getAnnotation(ConfigItem.class));
		assertEquals(item(getter).keyName(), item(setter).keyName());
		assertTrue("blacklist is edited in the side panel, not the config panel", item(getter).hidden());
	}

	@Test
	public void everyOptionIsInADeclaredSection()
	{
		Set<String> sections = sectionKeys().keySet();
		for (Method getter : configGetters())
		{
			ConfigItem item = item(getter);
			if (!item.hidden())
			{
				assertFalse(getter.getName() + "() is not in any section", item.section().isEmpty());
			}
			if (!item.section().isEmpty())
			{
				assertTrue(getter.getName() + "() uses undeclared section \"" + item.section() + "\"",
					sections.contains(item.section()));
			}
		}
	}

	@Test
	public void everySectionIsUniqueAndShowsAtLeastOneOption()
	{
		Set<String> used = new HashSet<>();
		for (Method getter : configGetters())
		{
			if (!item(getter).hidden())
			{
				used.add(item(getter).section());
			}
		}

		for (Map.Entry<String, String> section : sectionKeys().entrySet())
		{
			assertTrue("section " + section.getValue() + " has no visible options", used.contains(section.getKey()));
		}
	}

	@Test
	public void sectionsAndVisibleOptionsHaveTheirOwnPositions()
	{
		// The config panel orders by position, so a copy-pasted position leaves the order to the names instead
		Map<Integer, String> sectionPositions = new HashMap<>();
		for (Field field : WastedBankSpaceConfig.class.getDeclaredFields())
		{
			ConfigSection section = field.getAnnotation(ConfigSection.class);
			if (section != null)
			{
				String previous = sectionPositions.put(section.position(), field.getName());
				assertNull("sections " + previous + " and " + field.getName() + " share position " + section.position(),
					previous);
			}
		}

		Map<String, Map<Integer, String>> positionsBySection = new HashMap<>();
		for (Method getter : configGetters())
		{
			ConfigItem item = item(getter);
			if (item.hidden())
			{
				continue;
			}
			String previous = positionsBySection.computeIfAbsent(item.section(), k -> new HashMap<>())
				.put(item.position(), getter.getName());
			assertNull(previous + "() and " + getter.getName() + "() share position " + item.position()
				+ " in section " + item.section(), previous);
		}
	}

	@Test
	public void storageLocationKeysAreExactlyTheStorageLocationToggles()
	{
		// The plugin only rebuilds its flagged items when one of these keys changes
		Set<String> toggleKeys = new HashSet<>();
		for (Method getter : configGetters())
		{
			if (getter.getReturnType() == boolean.class
				&& WastedBankSpaceConfig.generalConfig.equals(item(getter).section()))
			{
				toggleKeys.add(item(getter).keyName());
			}
		}

		assertFalse(toggleKeys.isEmpty());
		assertEquals(toggleKeys, WastedBankSpaceConfig.getStorageLocationKeys());
	}

	@Test
	public void everyKeyConstantBelongsToAnOption() throws IllegalAccessException
	{
		// The plugin compares ConfigChanged keys against these constants, so each must be a real option's key
		Set<String> keyNames = new HashSet<>();
		for (Method getter : configGetters())
		{
			keyNames.add(item(getter).keyName());
		}

		int constants = 0;
		for (Field field : WastedBankSpaceConfig.class.getDeclaredFields())
		{
			if (field.getType() == String.class && field.getName().endsWith("_KEY"))
			{
				constants++;
				String key = (String) field.get(null);
				assertTrue(field.getName() + " = \"" + key + "\" is not the key of any option", keyNames.contains(key));
			}
		}
		assertTrue(constants > 0);
	}

	@Test
	public void everyConfigMethodIsAnnotated()
	{
		// RuneLite answers an unannotated method with null, which crashes any caller expecting a boolean
		for (Method method : WastedBankSpaceConfig.class.getDeclaredMethods())
		{
			if (Modifier.isStatic(method.getModifiers()) || method.isSynthetic())
			{
				continue;
			}
			assertNotNull(method + " has no @ConfigItem", method.getAnnotation(ConfigItem.class));
		}
	}

	private static ConfigItem item(Method method)
	{
		ConfigItem item = method.getAnnotation(ConfigItem.class);
		assertNotNull(method + " has no @ConfigItem", item);
		return item;
	}

	/**
	 * @return every method annotated with {@link ConfigItem}, setters included
	 */
	private static List<Method> configItemMethods()
	{
		List<Method> methods = new ArrayList<>();
		for (Method method : WastedBankSpaceConfig.class.getDeclaredMethods())
		{
			if (method.isAnnotationPresent(ConfigItem.class))
			{
				methods.add(method);
			}
		}
		return methods;
	}

	/**
	 * @return the options RuneLite shows and reads: annotated methods without parameters
	 */
	private static List<Method> configGetters()
	{
		List<Method> getters = new ArrayList<>();
		for (Method method : configItemMethods())
		{
			if (method.getParameterCount() == 0)
			{
				getters.add(method);
			}
		}
		return getters;
	}

	/**
	 * @return section key to field name for every {@link ConfigSection}, failing if two sections share a key
	 */
	private static Map<String, String> sectionKeys()
	{
		Map<String, String> sections = new HashMap<>();
		for (Field field : WastedBankSpaceConfig.class.getDeclaredFields())
		{
			if (!field.isAnnotationPresent(ConfigSection.class))
			{
				continue;
			}
			try
			{
				String previous = sections.put((String) field.get(null), field.getName());
				assertNull("sections " + previous + " and " + field.getName() + " share a key", previous);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError(e);
			}
		}
		assertFalse(sections.isEmpty());
		return sections;
	}

	/**
	 * A getter and its setter may share a key, e.g. nonFlaggedItems() and nonFlaggedItems(String).
	 */
	private static boolean isGetterSetterPair(List<Method> methods)
	{
		if (methods.size() != 2 || !methods.get(0).getName().equals(methods.get(1).getName()))
		{
			return false;
		}
		Method getter = methods.get(0).getParameterCount() == 0 ? methods.get(0) : methods.get(1);
		Method setter = getter == methods.get(0) ? methods.get(1) : methods.get(0);
		return getter.getParameterCount() == 0
			&& setter.getParameterCount() == 1
			&& setter.getParameterTypes()[0] == getter.getReturnType();
	}
}
