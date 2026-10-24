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

import com.wastedbankspace.model.locations.TackleBox;
import com.wastedbankspace.poh.PohStorageTracker;
import com.wastedbankspace.stash.StashTracker;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.KeyCode;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.Widget;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The shift-click "Flag Item" / "Unflag Item" option on bank items. It must follow whether the bank is showing, not
 * which item container changed last: depositing changes the inventory while the bank stays open.
 */
public class FlagMenuTest
{
	private static final int STORABLE_ITEM = TackleBox.values()[0].getItemID();

	private Client client;
	private Menu menu;
	private Widget bankItems;
	private WastedBankSpacePlugin plugin;

	@Before
	public void setUp() throws Exception
	{
		client = mock(Client.class);
		menu = mock(Menu.class);
		when(client.getMenu()).thenReturn(menu);
		when(menu.createMenuEntry(anyInt())).thenAnswer(invocation -> mock(MenuEntry.class, RETURNS_SELF));
		when(client.isKeyPressed(KeyCode.KC_SHIFT)).thenReturn(true);
		bankItems = mock(Widget.class);

		plugin = new WastedBankSpacePlugin();
		setField(plugin, "client", client);
		setField(plugin, "pohStorage", mock(PohStorageTracker.class));
		setField(plugin, "stashTracker", mock(StashTracker.class));
	}

	@Test
	public void flagOptionIsAddedWhileTheBankIsShowing()
	{
		showBank(true);

		plugin.onMenuOpened(menuOverBankItem());

		verify(menu).createMenuEntry(anyInt());
	}

	@Test
	public void flagOptionStaysAfterTheInventoryChangesWithTheBankOpen()
	{
		showBank(true);
		ItemContainer inventory = mock(ItemContainer.class);
		when(inventory.getItems()).thenReturn(new Item[0]);
		plugin.onItemContainerChanged(new ItemContainerChanged(InventoryID.INV, inventory));

		plugin.onMenuOpened(menuOverBankItem());

		verify(menu).createMenuEntry(anyInt());
	}

	@Test
	public void noFlagOptionOnceTheBankIsClosed()
	{
		showBank(false);

		plugin.onMenuOpened(menuOverBankItem());

		verify(menu, never()).createMenuEntry(anyInt());
	}

	private void showBank(boolean showing)
	{
		when(client.getWidget(InterfaceID.Bankmain.ITEMS)).thenReturn(showing ? bankItems : null);
	}

	private static MenuOpened menuOverBankItem()
	{
		Widget item = mock(Widget.class);
		when(item.getId()).thenReturn(InterfaceID.Bankmain.ITEMS);
		when(item.getItemId()).thenReturn(STORABLE_ITEM);
		MenuEntry entry = mock(MenuEntry.class);
		when(entry.getWidget()).thenReturn(item);
		when(entry.getTarget()).thenReturn("item");
		MenuOpened event = new MenuOpened();
		event.setMenuEntries(new MenuEntry[]{entry});
		return event;
	}

	private static void setField(Object target, String name, Object value) throws Exception
	{
		Field field = target.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(target, value);
	}
}
