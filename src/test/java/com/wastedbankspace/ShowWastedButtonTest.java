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
import com.wastedbankspace.ui.WastedBankSpacePanel;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import org.junit.Before;
import org.junit.Test;

import javax.swing.SwingUtilities;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The side panel's "Show Wasted Items in Bank" button: it opens the wasted bank tag in the open bank, or says why
 * it can't.
 */
public class ShowWastedButtonTest
{
	private Client client;
	private WastedBankTag bankTag;
	private WastedBankSpacePanel panel;
	private WastedBankSpacePlugin plugin;

	@Before
	public void setUp() throws Exception
	{
		client = mock(Client.class);
		bankTag = mock(WastedBankTag.class);
		panel = mock(WastedBankSpacePanel.class);
		ClientThread clientThread = mock(ClientThread.class);
		doAnswer(invocation ->
		{
			((Runnable) invocation.getArgument(0)).run();
			return null;
		}).when(clientThread).invoke(any(Runnable.class));

		plugin = new WastedBankSpacePlugin();
		setField(plugin, "client", client);
		setField(plugin, "clientThread", clientThread);
		setField(plugin, "bankTag", bankTag);
		setField(plugin, "panel", panel);
	}

	@Test
	public void opensTheWastedTagWhileTheBankIsOpen() throws Exception
	{
		showBank(true);
		when(bankTag.open()).thenReturn(true);

		press();

		verify(bankTag).open();
		verify(panel).setShowWastedHint("");
	}

	@Test
	public void asksForTheBankWhileItIsClosed() throws Exception
	{
		showBank(false);

		press();

		verify(bankTag, never()).open();
		verify(panel).setShowWastedHint("Open your bank first");
	}

	@Test
	public void pointsAtTheSettingWhileTheTagIsOff() throws Exception
	{
		showBank(true);
		when(bankTag.open()).thenReturn(false);

		press();

		verify(panel).setShowWastedHint("Turn on Wasted Bank Tag in the settings");
	}

	private void showBank(boolean showing)
	{
		when(client.getWidget(InterfaceID.Bankmain.ITEMS)).thenReturn(showing ? mock(Widget.class) : null);
	}

	/** Does what the button does, then waits for the hint the plugin hands to the Swing thread */
	private void press() throws Exception
	{
		Method show = WastedBankSpacePlugin.class.getDeclaredMethod("showWastedItemsInBank");
		show.setAccessible(true);
		show.invoke(plugin);
		SwingUtilities.invokeAndWait(() ->
		{
		});
	}

	private static void setField(Object target, String name, Object value) throws Exception
	{
		Field field = target.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(target, value);
	}
}
