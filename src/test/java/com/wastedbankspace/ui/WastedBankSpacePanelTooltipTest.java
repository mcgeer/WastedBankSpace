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

package com.wastedbankspace.ui;

import com.wastedbankspace.WastedBankSpaceConfig;
import com.wastedbankspace.model.ItemNameFixture;
import com.wastedbankspace.model.StorageLocations;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.JList;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The tooltip over an item in the panel's "wasting space" list comes from the location text function the plugin
 * hands the panel, keyed by the item's id, rather than from the single location per item in
 * {@link StorageLocations}. The panel is built without showing it, and the tooltip is asked for directly, so no
 * Swing tooltip timers are involved.
 */
public class WastedBankSpacePanelTooltipTest
{
	/** Items of the list, in the order they are given to the panel; the second is listed by two locations */
	private static final List<Integer> ITEMS = List.of(ItemID.HARPOON, ItemID.SKILLCAPE_WOODCUTTING, ItemID.LEAVES);

	private static ItemNameFixture itemNames;

	@BeforeClass
	public static void loadItemNames()
	{
		// The list shows item names, so the names have to be prepared for the rows to have a size
		itemNames = ItemNameFixture.load();
	}

	@AfterClass
	public static void restoreItemNames()
	{
		if (itemNames != null)
		{
			itemNames.restore();
		}
	}

	@Test
	public void listTooltipIsTheLocationTextOfTheItemUnderTheMouse() throws Exception
	{
		List<Integer> asked = new ArrayList<>();
		Function<Integer, String> locationText = itemId ->
		{
			asked.add(itemId);
			return "Location text of item " + itemId;
		};

		SwingUtilities.invokeAndWait(() ->
		{
			WastedBankSpacePanel panel = createPanel(locationText);
			panel.setWastedBankSpaceItems(new LinkedHashSet<>(ITEMS));
			JList<?> list = findList(panel);
			list.setSize(list.getPreferredSize());

			assertEquals(ITEMS.size(), list.getModel().getSize());
			for (int row = 0; row < ITEMS.size(); row++)
			{
				int itemId = ITEMS.get(row);
				assertEquals("row " + row, ItemNameFixture.nameOf(itemId), list.getModel().getElementAt(row));

				Rectangle cell = list.getCellBounds(row, row);
				assertNotNull("row " + row + " has no bounds", cell);
				MouseEvent mouse = mouseOver(list, cell.x + cell.width / 2, cell.y + cell.height / 2);
				assertEquals("row " + row, "Location text of item " + itemId, list.getToolTipText(mouse));
			}
			assertEquals(ITEMS, asked);
		});
	}

	@Test
	public void noTooltipBeforeItemsAreSet() throws Exception
	{
		SwingUtilities.invokeAndWait(() ->
		{
			WastedBankSpacePanel panel = createPanel(itemId ->
			{
				fail("asked for the location text of item " + itemId);
				return null;
			});
			JList<?> list = findList(panel);
			list.setSize(list.getPreferredSize());

			assertNull(list.getToolTipText(mouseOver(list, 5, 5)));
		});
	}

	private static WastedBankSpacePanel createPanel(Function<Integer, String> locationText)
	{
		WastedBankSpaceConfig config = mock(WastedBankSpaceConfig.class);
		when(config.nonFlaggedItems()).thenReturn("");
		return new WastedBankSpacePanel(mock(Client.class), new TooltipManager(), config, mock(ItemManager.class),
			filter ->
			{
			}, locationText, mock(ScheduledExecutorService.class));
	}

	private static MouseEvent mouseOver(Component component, int x, int y)
	{
		return new MouseEvent(component, MouseEvent.MOUSE_MOVED, 0, 0, x, y, 0, false);
	}

	/**
	 * @return the panel's item list; the panel has no accessor for it
	 */
	private static JList<?> findList(Container container)
	{
		for (Component component : container.getComponents())
		{
			if (component instanceof JList)
			{
				return (JList<?>) component;
			}
			if (component instanceof Container)
			{
				JList<?> list = findList((Container) component);
				if (list != null)
				{
					return list;
				}
			}
		}
		if (container instanceof WastedBankSpacePanel)
		{
			throw new AssertionError("no JList in the panel: " + Arrays.toString(container.getComponents()));
		}
		return null;
	}
}
