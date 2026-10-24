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
import com.wastedbankspace.model.CustomItemNames;
import com.wastedbankspace.model.ItemNameFixture;
import com.wastedbankspace.model.StorableItem;
import com.wastedbankspace.model.locations.TackleBox;
import com.wastedbankspace.ui.WastedBankSpacePanel;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.events.ConfigChanged;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Tests that the "Non Flagged Items" list matches item names the same way whatever the client's default locale is.
 * <p>
 * The item names are indexed in lower case. Under a Turkish default locale, {@link String#toLowerCase()} turns a
 * capital "I" into a dotless "ı" (U+0131), so "Infernal harpoon" used to be indexed as "ınfernalharpoon", which
 * a wildcard entry such as "infernal*" or "INFERNAL*" no longer matched: a case-insensitive pattern without
 * {@code UNICODE_CASE} only folds ASCII letters. Every test prepares the names with Turkish as the default locale,
 * applies the list the way {@link IgnoreListTest} does, and puts the locale and the names back afterwards.
 */
@RunWith(MockitoJUnitRunner.class)
public class IgnoreListLocaleTest
{
	private static final Locale TURKISH = new Locale("tr", "TR");

	/**
	 * Every item of the only storage location switched on in {@link #setUp()}.
	 */
	private static final Set<Integer> TACKLE_BOX_ITEMS = itemIds(TackleBox.values());

	/**
	 * "Infernal harpoon" and "Infernal harpoon (or)": the tackle box items whose names start with a capital "I".
	 */
	private static final Set<Integer> INFERNAL_HARPOONS = ids(ItemID.INFERNAL_HARPOON, ItemID.TRAILBLAZER_HARPOON);

	@Mock
	private WastedBankSpaceConfig config;

	@Mock
	private WastedBankTag bankTag;

	@Mock
	private WastedBankSpacePanel panel;

	@InjectMocks
	private WastedBankSpacePlugin plugin;

	private String ignoreListText = "";

	@Before
	public void setUp()
	{
		when(panel.getFilterdItemsText()).thenAnswer(invocation -> ignoreListText);
		when(config.filterEnabledCheck()).thenReturn(true);
		when(config.bisFilterEnabledCheck()).thenReturn(false);
		when(config.tackleBoxStorageCheck()).thenReturn(true);
	}

	@After
	public void waitForPanelUpdate() throws Exception
	{
		// Applying the list hands a panel refresh to the Swing thread; let it finish before the next test starts
		SwingUtilities.invokeAndWait(() ->
		{
		});
	}

	@Test
	public void lowerCaseWildcardEntryMatchesNamesWithACapitalI()
	{
		underTurkishLocale(() -> assertEquals(INFERNAL_HARPOONS, ignoredAfterApplying("infernal*")));
	}

	@Test
	public void upperCaseWildcardEntryMatchesNamesWithACapitalI()
	{
		underTurkishLocale(() -> assertEquals(INFERNAL_HARPOONS, ignoredAfterApplying("INFERNAL*")));
	}

	@Test
	public void wildcardEntryWrittenLikeTheItemNameMatchesIt()
	{
		underTurkishLocale(() ->
		{
			assertEquals(INFERNAL_HARPOONS, ignoredAfterApplying("Infernal harpoon*"));
			assertEquals(INFERNAL_HARPOONS, ignoredAfterApplying("*Infernal*"));
			assertEquals(ids(ItemID.TRAILBLAZER_HARPOON), ignoredAfterApplying("Infernal*(or)"));
		});
	}

	@Test
	public void wildcardEntriesMatchAllUpperCaseAndMixedCaseNames()
	{
		underTurkishLocale(() ->
		{
			// Spellings the fixture does not have: all upper case, and a capital I after the first word
			CustomItemNames.prepare(Map.of(
				ItemID.HARPOON, "Iron dagger",
				ItemID.LOBSTER_POT, "IRON BAR",
				ItemID.FISHING_ROD, "Ring of Iron"));

			assertEquals(ids(ItemID.HARPOON, ItemID.LOBSTER_POT), ignoredAfterApplying("iron*"));
			assertEquals(ids(ItemID.HARPOON, ItemID.LOBSTER_POT), ignoredAfterApplying("IRON*"));
			assertEquals(ids(ItemID.HARPOON, ItemID.LOBSTER_POT, ItemID.FISHING_ROD), ignoredAfterApplying("*iron*"));
			assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying("iron dagger"));
			assertEquals(ids(ItemID.LOBSTER_POT), ignoredAfterApplying("iron bar"));
		});
	}

	@Test
	public void wildcardEntryTypedWithATurkishDottedCapitalIMatchesTheAsciiName()
	{
		// "İnfernal*": what a Turkish keyboard produces for a capital i, which only UNICODE_CASE folds to "i"
		underTurkishLocale(() -> assertEquals(INFERNAL_HARPOONS, ignoredAfterApplying("İnfernal*")));
	}

	@Test
	public void exactNameEntryMatchesRegardlessOfCase()
	{
		underTurkishLocale(() ->
		{
			assertEquals(ids(ItemID.INFERNAL_HARPOON), ignoredAfterApplying("infernal harpoon"));
			assertEquals(ids(ItemID.INFERNAL_HARPOON), ignoredAfterApplying("INFERNAL HARPOON"));
			assertEquals(ids(ItemID.INFERNAL_HARPOON), ignoredAfterApplying("Infernal harpoon"));
		});
	}

	@Test
	public void namesWithoutACapitalIAreStillMatched()
	{
		underTurkishLocale(() ->
		{
			assertEquals(ids(ItemID.DRAGON_HARPOON), ignoredAfterApplying("Dragon harpoon"));
			assertEquals(ids(ItemID.FISHING_ROD, ItemID._4DOSEFISHERSPOTION), ignoredAfterApplying("Fishing*"));
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

	/**
	 * Applies the list and returns the tackle box items that are no longer flagged.
	 */
	private Set<Integer> ignoredAfterApplying(String ignoreList)
	{
		ignoreListText = ignoreList;
		ConfigChanged event = new ConfigChanged();
		event.setGroup(WastedBankSpaceConfig.GROUP);
		event.setKey(WastedBankSpaceConfig.FILTER_ENABLED_CHECK_KEY);
		plugin.onConfigChanged(event);

		Set<Integer> ignored = new HashSet<>(TACKLE_BOX_ITEMS);
		ignored.removeAll(plugin.getEnabledItems());
		return ignored;
	}

	private static Set<Integer> ids(Integer... itemIds)
	{
		return new HashSet<>(Arrays.asList(itemIds));
	}

	private static Set<Integer> itemIds(StorableItem[] location)
	{
		Set<Integer> itemIds = new HashSet<>();
		for (StorableItem item : location)
		{
			itemIds.add(item.getItemID());
		}
		return itemIds;
	}
}
