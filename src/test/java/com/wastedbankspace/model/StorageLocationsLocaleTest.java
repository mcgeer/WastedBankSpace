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

package com.wastedbankspace.model;

import net.runelite.api.gameval.ItemID;
import org.junit.Test;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Checks that {@link StorageLocations#prepareStorableItemNames} indexes item names the same way whatever the
 * client's default locale is.
 * <p>
 * The names are indexed in lower case. Under a Turkish default locale, {@link String#toLowerCase()} turns a capital
 * "I" into a dotless "ı" (U+0131), so "Infernal harpoon" would be indexed as "ınfernalharpoon" and a wildcard
 * entry in the "Non Flagged Items" list could no longer match it. Every test prepares the names with Turkish as the
 * default locale and puts the locale and the names back afterwards.
 */
public class StorageLocationsLocaleTest
{
	private static final Locale TURKISH = new Locale("tr", "TR");
	private static final char DOTLESS_I = 'ı';

	/**
	 * Guards the premise of this class: with the Turkish locale as the default, a capital "I" lower cases to a
	 * dotless "ı", so the names really are prepared under the conditions of the bug.
	 */
	@Test
	public void turkishLocaleLowerCasesCapitalIToDotlessI()
	{
		underTurkishLocale(() -> assertEquals(String.valueOf(DOTLESS_I), "I".toLowerCase()));
	}

	@Test
	public void namesWithACapitalIAreIndexedWithAnAsciiI()
	{
		underTurkishLocale(() ->
		{
			// Compare the keys exactly: the index's case-insensitive comparator would treat a dotless i as an i
			Set<String> keys = new HashSet<>(StorageLocations.getModifiedItemNameMap().keySet());

			assertTrue("not indexed: infernalharpoon", keys.contains("infernalharpoon"));
			assertTrue("not indexed: infernalharpoon(or)", keys.contains("infernalharpoon(or)"));
		});
	}

	@Test
	public void capitalIsAnywhereInANameAreIndexedAsAsciiIs()
	{
		underTurkishLocale(() ->
		{
			// Spellings the fixture does not have: all upper case, and a capital I after the first word
			CustomItemNames.prepare(Map.of(
				ItemID.HARPOON, "Iron dagger",
				ItemID.LOBSTER_POT, "IRON BAR",
				ItemID.FISHING_ROD, "Ring of Iron"));
			Set<String> keys = new HashSet<>(StorageLocations.getModifiedItemNameMap().keySet());

			assertTrue("not indexed: irondagger", keys.contains("irondagger"));
			assertTrue("not indexed: ironbar", keys.contains("ironbar"));
			assertTrue("not indexed: ringofiron", keys.contains("ringofiron"));
		});
	}

	@Test
	public void noIndexedNameContainsADotlessI()
	{
		underTurkishLocale(() ->
		{
			for (String key : StorageLocations.getModifiedItemNameMap().keySet())
			{
				assertEquals(key, -1, key.indexOf(DOTLESS_I));
			}
		});
	}

	@Test
	public void everyNameIsIndexedAsItsRootLocaleLowerCase()
	{
		underTurkishLocale(() ->
		{
			Set<String> keys = new HashSet<>(StorageLocations.getModifiedItemNameMap().keySet());
			for (int itemId : StorageLocations.getItemIdMap().keySet())
			{
				// The fixture's names only contain single spaces
				String expected = ItemNameFixture.nameOf(itemId).toLowerCase(Locale.ROOT).replace(" ", "");
				assertTrue("not indexed: " + expected, keys.contains(expected));
			}
		});
	}

	@Test
	public void storableItemIdLookupIgnoresCaseUnderTurkishLocale()
	{
		underTurkishLocale(() ->
		{
			Integer expected = ItemID.INFERNAL_HARPOON;

			assertEquals(expected, StorageLocations.getStorableItemId("infernalharpoon"));
			assertEquals(expected, StorageLocations.getStorableItemId("INFERNALHARPOON"));
			assertEquals(expected, StorageLocations.getStorableItemId("InfernalHarpoon"));
		});
	}

	/**
	 * Prepares the item names with Turkish as the JVM's default locale, runs the checks, and then puts the names and
	 * the default locale back so that no other test class is affected.
	 */
	private static void underTurkishLocale(Runnable checks)
	{
		Locale previous = Locale.getDefault();
		Locale previousDisplay = Locale.getDefault(Locale.Category.DISPLAY);
		Locale previousFormat = Locale.getDefault(Locale.Category.FORMAT);
		Locale.setDefault(TURKISH);
		ItemNameFixture itemNames = null;
		try
		{
			itemNames = ItemNameFixture.load();
			checks.run();
		}
		finally
		{
			// Null when loading failed, in which case the fixture has already put the names back
			if (itemNames != null)
			{
				itemNames.restore();
			}
			Locale.setDefault(previous);
			Locale.setDefault(Locale.Category.DISPLAY, previousDisplay);
			Locale.setDefault(Locale.Category.FORMAT, previousFormat);
		}
	}
}
