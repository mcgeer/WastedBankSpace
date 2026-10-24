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
import com.wastedbankspace.model.ItemNameFixture;
import com.wastedbankspace.model.StorableItem;
import com.wastedbankspace.model.StorageLocations;
import com.wastedbankspace.model.locations.TackleBox;
import com.wastedbankspace.ui.WastedBankSpacePanel;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for wildcard entries in the "Non Flagged Items" list whose text contains characters that have a meaning in
 * regular expressions: "+", "?", "|", "[", ".", "^", "$" and "{". The plugin turns a wildcard entry into a regular
 * expression in processIgnoreListChanged, and everything but the "*" has to be matched literally: item names such as
 * "Antidote+(4)" or "Dragon dagger(p+)" contain such characters, and an entry like "Harpoon|*" must not quietly turn
 * into "Harpoon, or anything at all".
 * <p>
 * The tests go in through {@link WastedBankSpacePlugin#onConfigChanged} with the panel's text, the way
 * {@link IgnoreListTest} does, and read which items are left to flag from {@link WastedBankSpacePlugin#getEnabledItems()}.
 * Only the tackle box is switched on, and a few of its items are given the names with regex characters; which items
 * carry the names does not matter, since the list matches names, never ids.
 */
@RunWith(MockitoJUnitRunner.class)
public class WildcardIgnoreListTest
{
	// Tackle box items that get the names with regex characters, see names()
	private static final int ANTIDOTE_PLUS_4 = ItemID.TINY_NET;
	private static final int ANTIDOTE_4 = ItemID.NET;
	private static final int SUPER_ANTIFIRE_4 = ItemID.BIG_NET;
	private static final int DRAGON_DAGGER_P_PLUS = ItemID.FEATHER;
	private static final int DRAGON_DAGGER_P = ItemID.FISHING_BAIT;
	private static final int DRAGON_DAGGER = ItemID.SPIRIT_FLAKES;

	/**
	 * The names with regex characters, by item id. Every other item keeps {@link ItemNameFixture#nameOf(int)}.
	 */
	private static final Map<Integer, String> NAMES = names();

	private static final Set<Integer> TACKLE_BOX_ITEMS = itemIds(TackleBox.values());

	private static final Set<Integer> HARPOONS_WITHOUT_ORNAMENT = ids(
		ItemID.HARPOON, ItemID.HUNTING_BARBED_HARPOON, ItemID.DRAGON_HARPOON, ItemID.INFERNAL_HARPOON,
		ItemID.CRYSTAL_HARPOON);

	private static final Set<Integer> ORNAMENTED_HARPOONS = ids(
		ItemID.TRAILBLAZER_HARPOON_NO_INFERNAL, ItemID.TRAILBLAZER_HARPOON);

	private static ItemNameFixture itemNames;

	@Mock
	private WastedBankSpaceConfig config;

	@Mock
	private WastedBankTag bankTag;

	@Mock
	private WastedBankSpacePanel panel;

	@InjectMocks
	private WastedBankSpacePlugin plugin;

	private String ignoreListText = "";

	@BeforeClass
	public static void loadItemNames()
	{
		// The fixture saves the names prepared before this class and puts them back in restoreItemNames(). It has
		// no way to name items itself, so the names are prepared once more, the way the plugin does on startup,
		// with the names above on top of the fixture's
		itemNames = ItemNameFixture.load();
		try
		{
			StorageLocations.getModifiedItemNameMap().clear();
			StorageLocations.prepareStorableItemNames(itemManager());
		}
		catch (RuntimeException e)
		{
			itemNames.restore();
			throw e;
		}
	}

	@AfterClass
	public static void restoreItemNames()
	{
		// Null when loading failed, in which case the fixture has already put the names back
		if (itemNames != null)
		{
			itemNames.restore();
		}
	}

	@Before
	public void setUp()
	{
		when(panel.getFilterdItemsText()).thenAnswer(invocation -> ignoreListText);
		when(config.filterEnabledCheck()).thenReturn(true);
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
	public void plusIsMatchedLiterally()
	{
		// As a regex, "Antidote+" would be "Antidot" followed by one or more "e", which "Antidote(4)" also is
		assertEquals(ids(ANTIDOTE_PLUS_4), ignoredAfterApplying("Antidote+*"));
	}

	@Test
	public void plusInsideBracketsIsMatchedLiterally()
	{
		// As a regex, "(p+)" would be one or more "p" in brackets, which is "(p)" and not "(p+)"
		assertEquals(ids(DRAGON_DAGGER_P_PLUS), ignoredAfterApplying("*(p+)"));
		assertEquals(ids(DRAGON_DAGGER_P_PLUS), ignoredAfterApplying("Dragon dagger(p+*"));
	}

	@Test
	public void questionMarkIsMatchedLiterally()
	{
		// As a regex, "dagger?" would make the "r" optional and match "Dragon dagger"
		assertEquals(ids(), ignoredAfterApplying("*dagger?"));
		assertEquals(ids(DRAGON_DAGGER), ignoredAfterApplying("*dagger"));
	}

	@Test
	public void pipeDoesNotTurnTheEntryIntoAnAlternation()
	{
		// As a regex, "Harpoon|*" would be "Harpoon" or anything at all, and ignore every item
		assertEquals(ids(), ignoredAfterApplying("Harpoon|*"));
		assertEquals(ids(), ignoredAfterApplying("*|Harpoon"));
	}

	@Test
	public void strayBracketIsMatchedLiterallyAndKeepsTheRestOfTheList()
	{
		assertEquals(ids(), ignoredAfterApplying("[*"));
		assertEquals(ids(), ignoredAfterApplying("*]"));
		assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying("[*, Harpoon"));
		assertEquals(ids(ItemID.HARPOON, ANTIDOTE_PLUS_4), ignoredAfterApplying("Harpoon, *[, Antidote+*"));
	}

	@Test
	public void otherRegexCharactersAreMatchedLiterally()
	{
		// No name contains ".", "^", "$" or "{", so none of these entries may match anything. As regexes, "^*", "*$"
		// and "*{4}" would match every name, and "*.(4)" every name ending in "(4)"
		assertEquals(ids(), ignoredAfterApplying("*.(4)"));
		assertEquals(ids(), ignoredAfterApplying("^*"));
		assertEquals(ids(), ignoredAfterApplying("*$"));
		assertEquals(ids(), ignoredAfterApplying("*{4}"));
	}

	@Test
	public void wildcardsStillMatchAnywhereInTheName()
	{
		Set<Integer> allHarpoons = new HashSet<>(HARPOONS_WITHOUT_ORNAMENT);
		allHarpoons.addAll(ORNAMENTED_HARPOONS);

		assertEquals(allHarpoons, ignoredAfterApplying("*harpoon*"));
		assertEquals(ids(ItemID.TRAILBLAZER_HARPOON_NO_INFERNAL), ignoredAfterApplying("Dragon*(or)"));
		assertEquals(ids(SUPER_ANTIFIRE_4), ignoredAfterApplying("*antifire*"));
		assertEquals(ids(DRAGON_DAGGER_P_PLUS, DRAGON_DAGGER_P), ignoredAfterApplying("*dagger(*)"));
	}

	@Test
	public void wildcardMatchingStillIgnoresCase()
	{
		assertEquals(ORNAMENTED_HARPOONS, ignoredAfterApplying("*HARPOON (OR)"));
		assertEquals(ids(ANTIDOTE_PLUS_4), ignoredAfterApplying("aNtIdOtE+*"));
		assertEquals(ids(DRAGON_DAGGER_P_PLUS), ignoredAfterApplying("*DAGGER(P+)"));
	}

	@Test
	public void wildcardStillHasToMatchTheWholeName()
	{
		// The "(or)" harpoons contain "harpoon" but do not end with it
		assertEquals(HARPOONS_WITHOUT_ORNAMENT, ignoredAfterApplying("*harpoon"));
		assertEquals(ids(ANTIDOTE_PLUS_4, ANTIDOTE_4), ignoredAfterApplying("Antidote*"));
		assertEquals(ids(DRAGON_DAGGER), ignoredAfterApplying("Dragon*dagger"));
		assertEquals(ids(), ignoredAfterApplying("*dagger(p"));
	}

	@Test
	public void adjacentWildcardsMatchLikeASingleOne()
	{
		// Every "*" between two others matches the empty string, so "**" and "*" ignore the same items; a change
		// that collapses runs of "*" before compiling (see the finding about "**********x") must keep that
		assertEquals(ids(ANTIDOTE_PLUS_4, ANTIDOTE_4), ignoredAfterApplying("Antidote**"));
		assertEquals(HARPOONS_WITHOUT_ORNAMENT, ignoredAfterApplying("**harpoon"));
		assertEquals(ids(DRAGON_DAGGER_P_PLUS), ignoredAfterApplying("**(p+)"));
		assertEquals(TACKLE_BOX_ITEMS, ignoredAfterApplying("***"));
	}

	@Test
	public void bracketsInWildcardEntriesAreStillLiteral()
	{
		assertEquals(ids(ItemID._4DOSEFISHERSPOTION, ANTIDOTE_PLUS_4, ANTIDOTE_4, SUPER_ANTIFIRE_4),
			ignoredAfterApplying("*(4)"));
		assertEquals(ids(DRAGON_DAGGER_P), ignoredAfterApplying("*dagger(p)"));
		assertEquals(ORNAMENTED_HARPOONS, ignoredAfterApplying("*harpoon (or)"));
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

	/**
	 * @return an item manager that names the items in {@link #NAMES} as listed there and every other storable item
	 * the way {@link ItemNameFixture} does
	 */
	private static ItemManager itemManager()
	{
		Map<Integer, ItemComposition> compositions = new HashMap<>();
		for (int itemId : ItemNameFixture.storableItemIds())
		{
			ItemComposition composition = mock(ItemComposition.class);
			when(composition.getName()).thenReturn(NAMES.getOrDefault(itemId, ItemNameFixture.nameOf(itemId)));
			compositions.put(itemId, composition);
		}

		ItemManager itemManager = mock(ItemManager.class);
		when(itemManager.getItemComposition(anyInt()))
			.thenAnswer(invocation -> compositions.get(invocation.getArgument(0)));
		return itemManager;
	}

	private static Map<Integer, String> names()
	{
		Map<Integer, String> names = new HashMap<>();
		names.put(ANTIDOTE_PLUS_4, "Antidote+(4)");
		names.put(ANTIDOTE_4, "Antidote(4)");
		names.put(SUPER_ANTIFIRE_4, "Super antifire potion(4)");
		names.put(DRAGON_DAGGER_P_PLUS, "Dragon dagger(p+)");
		names.put(DRAGON_DAGGER_P, "Dragon dagger(p)");
		names.put(DRAGON_DAGGER, "Dragon dagger");
		return Collections.unmodifiableMap(names);
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
