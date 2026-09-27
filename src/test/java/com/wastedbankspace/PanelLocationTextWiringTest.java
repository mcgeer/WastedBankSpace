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
import com.wastedbankspace.model.StorageLocations;
import com.wastedbankspace.model.locations.CapeRack;
import com.wastedbankspace.model.locations.ForestryKit;
import com.wastedbankspace.poh.PohStorageTracker;
import com.wastedbankspace.ui.WastedBankSpacePanel;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The plugin builds its side panel on start up and hands it the function that names where an item can be stored.
 * {@link com.wastedbankspace.ui.WastedBankSpacePanelTooltipTest} checks the panel with a function of its own, so
 * this test checks the wiring: the tooltip over an item wasting bank space is
 * {@link WastedBankSpacePlugin#getStorageLocationText(int, boolean)} for that item, as if it were not in the house,
 * and follows the enabled storage locations. The real panel is built by {@link WastedBankSpacePlugin#startUp()} and
 * taken from the navigation button the plugin adds to the toolbar; the bank is fed through
 * {@link WastedBankSpacePlugin#onItemContainerChanged} so the item is listed the way it is in the client.
 */
@RunWith(MockitoJUnitRunner.class)
public class PanelLocationTextWiringTest
{
	/** Woodcutting cape: cape rack (registered first, costume room) and forestry kit (whose config option comes first) */
	private static final int WOODCUTTING_CAPE = ItemID.SKILLCAPE_WOODCUTTING;
	/** Leaves: forestry kit only */
	private static final int LEAVES = ItemID.LEAVES;

	private static ItemNameFixture itemNames;

	@Mock
	private Client client;

	@Mock
	private ClientThread clientThread;

	@Mock
	private ClientToolbar clientToolbar;

	@Mock
	private ItemManager itemManager;

	@Mock
	private OverlayManager overlayManager;

	@Mock
	private WastedBankTag bankTag;

	@Mock
	private PohStorageTracker pohStorage;

	@Mock
	private TooltipManager tooltipManager;

	@Mock
	private WastedBankSpaceConfig config;

	@Mock
	private ConfigManager configManager;

	@Mock
	private ScheduledExecutorService scheduledExecutorService;

	@InjectMocks
	private WastedBankSpacePlugin plugin;

	private boolean capeRack;
	private boolean forestryKit;

	private JList<?> list;

	@BeforeClass
	public static void loadItemNames()
	{
		// The panel lists items by name, so the names have to be prepared for the rows to exist
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

	@Before
	public void startUp() throws Exception
	{
		// Guards the test data: the registry's single entry for the cape is the forestry kit's, the cape rack came first
		assertEquals(List.of(CapeRack.WOODCUTTING_CAPE_2, ForestryKit.WOODCUTTING_CAPE),
			StorageLocations.getStorableItems(WOODCUTTING_CAPE));

		when(config.nonFlaggedItems()).thenReturn("");
		when(config.capeRackStorageCheck()).thenAnswer(invocation -> capeRack);
		when(config.forestryKitStorageCheck()).thenAnswer(invocation -> forestryKit);

		// Whatever is in the bank is a real item, not a placeholder
		ItemComposition realItem = mock(ItemComposition.class);
		when(realItem.getPlaceholderTemplateId()).thenReturn(-1);
		when(itemManager.getItemComposition(anyInt())).thenReturn(realItem);

		// The plugin builds its Swing panel on start up, so start it on the Swing thread
		onSwingThread(() ->
		{
			plugin.startUp();
			return null;
		});

		ArgumentCaptor<NavigationButton> navigationButton = ArgumentCaptor.forClass(NavigationButton.class);
		verify(clientToolbar).addNavigation(navigationButton.capture());
		list = findList((WastedBankSpacePanel) navigationButton.getValue().getPanel());
	}

	@After
	public void shutDown() throws Exception
	{
		// Also lets the panel updates the plugin handed to the Swing thread finish before the next test starts
		onSwingThread(() ->
		{
			plugin.shutDown();
			return null;
		});
	}

	@Test
	public void panelTooltipNamesTheEnabledLocationsOfTheItem() throws Exception
	{
		capeRack = true;
		forestryKit = true;
		recalculate();
		bankContains(WOODCUTTING_CAPE, LEAVES);

		assertEquals("Forestry Kit / Cape Rack", tooltipOf(WOODCUTTING_CAPE));
		assertEquals("Forestry Kit", tooltipOf(LEAVES));

		capeRack = false;
		recalculate();

		assertEquals("Forestry Kit", tooltipOf(WOODCUTTING_CAPE));

		forestryKit = false;
		recalculate();

		assertEquals("nothing is wasting space any more", List.of(), rows());
	}

	@Test
	public void panelNamesWhereToStoreTheItemEvenWhenItIsAlreadyInTheCostumeRoom() throws Exception
	{
		// The cape is in the cape rack, which is disabled; the panel lists it because of the forestry kit
		lenient().when(pohStorage.isStored(WOODCUTTING_CAPE)).thenReturn(true);
		forestryKit = true;
		recalculate();
		bankContains(WOODCUTTING_CAPE);

		assertEquals("Forestry Kit", tooltipOf(WOODCUTTING_CAPE));
	}

	/**
	 * Recalculates the flagged items and their location text from the enabled storage locations.
	 */
	private void recalculate()
	{
		ConfigChanged event = new ConfigChanged();
		event.setGroup(WastedBankSpaceConfig.GROUP);
		event.setKey(WastedBankSpaceConfig.FILTER_ENABLED_CHECK_KEY);
		plugin.onConfigChanged(event);
	}

	private void bankContains(int... itemIds)
	{
		Item[] items = new Item[itemIds.length];
		for (int i = 0; i < itemIds.length; i++)
		{
			items[i] = new Item(itemIds[i], 1);
		}
		ItemContainer container = mock(ItemContainer.class);
		when(container.getItems()).thenReturn(items);
		plugin.onItemContainerChanged(new ItemContainerChanged(InventoryID.BANK, container));
	}

	/**
	 * @return the tooltip over the row of the item, which the panel finds by the point of the mouse event
	 */
	private String tooltipOf(int itemId) throws Exception
	{
		String name = ItemNameFixture.nameOf(itemId);
		return onSwingThread(() ->
		{
			List<String> rows = rowsOnSwingThread();
			int row = rows.indexOf(name);
			assertTrue(name + " is not listed, only " + rows, row >= 0);

			list.setSize(list.getPreferredSize());
			Rectangle cell = list.getCellBounds(row, row);
			MouseEvent mouse = new MouseEvent(list, MouseEvent.MOUSE_MOVED, 0, 0,
				cell.x + cell.width / 2, cell.y + cell.height / 2, 0, false);
			return list.getToolTipText(mouse);
		});
	}

	/**
	 * @return the names in the panel's list of items wasting space
	 */
	private List<String> rows() throws Exception
	{
		return onSwingThread(this::rowsOnSwingThread);
	}

	private List<String> rowsOnSwingThread()
	{
		ListModel<?> model = list.getModel();
		List<String> rows = new ArrayList<>();
		for (int row = 0; row < model.getSize(); row++)
		{
			rows.add(String.valueOf(model.getElementAt(row)));
		}
		return rows;
	}

	/**
	 * @return the panel's item list, a direct child of the panel; the panel has no accessor for it
	 */
	private static JList<?> findList(WastedBankSpacePanel panel)
	{
		for (Component component : panel.getComponents())
		{
			if (component instanceof JList)
			{
				return (JList<?>) component;
			}
		}
		throw new AssertionError("no JList in the panel");
	}

	/**
	 * Runs the task on the Swing thread and waits for it, rethrowing whatever it threw on this thread.
	 */
	private static <T> T onSwingThread(Callable<T> task) throws Exception
	{
		AtomicReference<T> result = new AtomicReference<>();
		AtomicReference<Throwable> failure = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() ->
		{
			try
			{
				result.set(task.call());
			}
			catch (Throwable t)
			{
				failure.set(t);
			}
		});
		Throwable thrown = failure.get();
		if (thrown instanceof Error)
		{
			throw (Error) thrown;
		}
		if (thrown != null)
		{
			throw (Exception) thrown;
		}
		return result.get();
	}
}
