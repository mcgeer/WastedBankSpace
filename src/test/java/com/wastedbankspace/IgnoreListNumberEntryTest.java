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
import com.wastedbankspace.model.locations.TackleBox;
import com.wastedbankspace.ui.WastedBankSpacePanel;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.events.ConfigChanged;
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
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests for item id entries in the "Non Flagged Items" list that contain whitespace or do not fit in an int.
 * <p>
 * The plugin decides that an entry is an item id by looking at it with all whitespace removed, so an id typed as
 * "210 28", or wrapped over two lines in the text area, has to be read as 21028 rather than handed to
 * Integer.parseInt as written. A number too large for an int cannot be an item and is skipped. Neither may throw
 * out of processIgnoreListChanged: that abandons the rest of the list and escapes
 * {@link WastedBankSpacePlugin#onConfigChanged} into the event bus.
 * <p>
 * Same setup as {@link IgnoreListTest}: the list is applied through onConfigChanged with only the tackle box
 * switched on, and the tests check which of its items are no longer in {@link WastedBankSpacePlugin#getEnabledItems()}.
 */
@RunWith(MockitoJUnitRunner.class)
public class IgnoreListNumberEntryTest
{
	private static final Set<Integer> TACKLE_BOX_ITEMS = itemIds(TackleBox.values());

	/**
	 * One more than the largest int, so the smallest number the plugin has to refuse.
	 */
	private static final String JUST_ABOVE_INT_RANGE = Long.toString((long) Integer.MAX_VALUE + 1);

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
		itemNames = ItemNameFixture.load();
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
	public void itemIdWithAnInnerSpaceIsReadWithTheSpaceRemoved()
	{
		assertEquals(ids(ItemID.DRAGON_HARPOON), ignoredAfterApplying(splitInTheMiddle(ItemID.DRAGON_HARPOON, " ")));
	}

	@Test
	public void itemIdWithWhitespaceBetweenEveryDigitIsReadAsOneNumber()
	{
		String digits = String.valueOf(ItemID.DRAGON_HARPOON);

		assertEquals(ids(ItemID.DRAGON_HARPOON), ignoredAfterApplying(String.join(" ", digits.split(""))));
	}

	@Test
	public void itemIdWrappedOverTwoLinesIsReadAsOneNumber()
	{
		// The list is edited in a text area, so an id can be broken over lines just like a name
		assertEquals(ids(ItemID.DRAGON_HARPOON), ignoredAfterApplying(splitInTheMiddle(ItemID.DRAGON_HARPOON, "\n")));
		assertEquals(ids(ItemID.DRAGON_HARPOON), ignoredAfterApplying(splitInTheMiddle(ItemID.DRAGON_HARPOON, " \t\n ")));
	}

	@Test
	public void itemIdWithInnerSpaceDoesNotStopTheEntriesAfterIt()
	{
		String ignoreList = splitInTheMiddle(ItemID.DRAGON_HARPOON, " ") + ", Harpoon, " + ItemID.LOBSTER_POT;

		assertEquals(ids(ItemID.DRAGON_HARPOON, ItemID.HARPOON, ItemID.LOBSTER_POT), ignoredAfterApplying(ignoreList));
	}

	@Test
	public void numberTooLargeForAnItemIdIsSkippedAndTheRestOfTheListIsStillApplied()
	{
		assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying("99999999999, Harpoon"));
		assertEquals(ids(ItemID.HARPOON, ItemID.LOBSTER_POT),
			ignoredAfterApplying("Harpoon, 99999999999, " + ItemID.LOBSTER_POT));
	}

	@Test
	public void numberJustAboveTheIntRangeIsSkippedAndTheLargestIntIsAccepted()
	{
		assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying(JUST_ABOVE_INT_RANGE + ", Harpoon"));
		// The largest int is a valid number, just not a storable item, so nothing extra is ignored
		assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying(Integer.MAX_VALUE + ", Harpoon"));
	}

	@Test
	public void numberTooLargeForAnItemIdWithInnerWhitespaceIsSkippedToo()
	{
		assertEquals(ids(ItemID.HARPOON), ignoredAfterApplying("999 99999999, Harpoon"));
	}

	@Test
	public void replacingTheListWithAnOutOfRangeNumberFlagsThePreviousItemAgain()
	{
		assertEquals(ids(ItemID.LOBSTER_POT), ignoredAfterApplying(String.valueOf(ItemID.LOBSTER_POT)));

		// The list is cleared before the new entries are read, so a skipped entry must not leave the old one behind
		assertEquals(ids(), ignoredAfterApplying(JUST_ABOVE_INT_RANGE));
	}

	/**
	 * A guard rather than a regression test: Text.fromCSV already trims each entry, so whitespace around an id never
	 * reached Integer.parseInt. It pins that down in case the entries are ever split differently.
	 */
	@Test
	public void whitespaceAroundAnItemIdIsIgnored()
	{
		assertEquals(ids(ItemID.LOBSTER_POT), ignoredAfterApplying("   " + ItemID.LOBSTER_POT + "   "));
		assertEquals(ids(ItemID.LOBSTER_POT), ignoredAfterApplying("\t" + ItemID.LOBSTER_POT + "\n"));
		assertEquals(ids(ItemID.HARPOON, ItemID.LOBSTER_POT),
			ignoredAfterApplying(" Harpoon , " + ItemID.LOBSTER_POT + " "));
	}

	/**
	 * Applies the list and returns the tackle box items that are no longer flagged.
	 */
	private Set<Integer> ignoredAfterApplying(String ignoreList)
	{
		ignoreListText = ignoreList;
		plugin.onConfigChanged(configChanged(WastedBankSpaceConfig.FILTER_ENABLED_CHECK_KEY));

		Set<Integer> ignored = new HashSet<>(TACKLE_BOX_ITEMS);
		ignored.removeAll(plugin.getEnabledItems());
		return ignored;
	}

	/**
	 * @return the item id's digits with the separator inserted halfway, e.g. "210 28" for 21028
	 */
	private static String splitInTheMiddle(int itemId, String separator)
	{
		String digits = String.valueOf(itemId);
		assertTrue("item id needs at least two digits to be split: " + digits, digits.length() >= 2);
		int middle = digits.length() / 2;
		return digits.substring(0, middle) + separator + digits.substring(middle);
	}

	private static ConfigChanged configChanged(String key)
	{
		ConfigChanged event = new ConfigChanged();
		event.setGroup(WastedBankSpaceConfig.GROUP);
		event.setKey(key);
		return event;
	}

	private static Set<Integer> ids(Integer... itemIds)
	{
		return new HashSet<>(Arrays.asList(itemIds));
	}

	private static Set<Integer> itemIds(StorableItem... items)
	{
		Set<Integer> itemIds = new HashSet<>();
		for (StorableItem item : items)
		{
			itemIds.add(item.getItemID());
		}
		return itemIds;
	}
}
