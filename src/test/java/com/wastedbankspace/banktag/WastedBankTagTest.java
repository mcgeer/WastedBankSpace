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

package com.wastedbankspace.banktag;

import com.wastedbankspace.WastedBankSpaceConfig;
import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.banktags.BankTag;
import net.runelite.client.plugins.banktags.BankTagsService;
import net.runelite.client.plugins.banktags.TagManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WastedBankTagTest
{
	/** Players search for it and name their tab after it, so it must not change between plugin versions */
	private static final String TAG = "wasted";

	/** Made-up item ids; a real item and its placeholder point at each other, as in the game's item definitions */
	private static final int FLAGGED_ITEM = 1001;
	private static final int FLAGGED_ITEM_PLACEHOLDER = 1002;
	private static final int OTHER_ITEM = 2001;
	private static final int OTHER_ITEM_PLACEHOLDER = 2002;
	private static final int PLACEHOLDER_TEMPLATE = 14401;

	private final List<Runnable> pendingClientThreadTasks = new ArrayList<>();
	private boolean runClientThreadImmediately = true;

	private ClientThread clientThread;
	private TagManager tagManager;
	private BankTagsService bankTagsService;
	private WastedBankSpaceConfig config;
	private ItemManager itemManager;

	@Before
	public void setUp()
	{
		tagManager = mock(TagManager.class);
		bankTagsService = mock(BankTagsService.class);
		config = mock(WastedBankSpaceConfig.class);
		itemManager = mock(ItemManager.class);
		clientThread = fakeClientThread();

		mockItem(FLAGGED_ITEM, -1, FLAGGED_ITEM_PLACEHOLDER);
		mockItem(FLAGGED_ITEM_PLACEHOLDER, PLACEHOLDER_TEMPLATE, FLAGGED_ITEM);
		mockItem(OTHER_ITEM, -1, OTHER_ITEM_PLACEHOLDER);
		mockItem(OTHER_ITEM_PLACEHOLDER, PLACEHOLDER_TEMPLATE, OTHER_ITEM);
	}

	// region enabling and disabling the tag

	@Test
	public void syncWithOptionOnRegistersTag()
	{
		WastedBankTag bankTag = syncedBankTag(true);

		verify(tagManager).registerTag(eq(TAG), any(BankTag.class));
		assertTrue(bankTag.isEnabled());
	}

	@Test
	public void startUpAppliesOptionOnClientThread()
	{
		when(config.bankTagTab()).thenReturn(true);
		WastedBankTag bankTag = newBankTag();
		runClientThreadImmediately = false;

		bankTag.startUp();
		assertFalse(bankTag.isEnabled());

		runPendingClientThreadTasks();
		assertTrue(bankTag.isEnabled());
	}

	@Test
	public void syncWithOptionOffUnregistersTag()
	{
		WastedBankTag bankTag = syncedBankTag(true);

		disable(bankTag);

		verify(tagManager).unregisterTag(TAG);
		assertFalse(bankTag.isEnabled());
	}

	@Test
	public void needsNothingThatCouldChangeOtherPluginsConfig()
	{
		// The Plugin Hub doesn't accept plugins that change another plugin's config, e.g. the Bank Tags tab list
		for (Constructor<?> constructor : WastedBankTag.class.getDeclaredConstructors())
		{
			for (Class<?> parameter : constructor.getParameterTypes())
			{
				assertFalse(parameter.getName(), parameter.getName().equals("net.runelite.client.config.ConfigManager"));
			}
		}
	}

	@Test
	public void openShowsTheTagInTheBank()
	{
		WastedBankTag bankTag = syncedBankTag(true);
		clearInvocations(bankTagsService);

		assertTrue(bankTag.open());

		verify(bankTagsService).openBankTag(TAG, BankTagsService.OPTION_ALLOW_MODIFICATIONS);
	}

	@Test
	public void openDoesNothingWhileTheTagIsSwitchedOff()
	{
		WastedBankTag bankTag = syncedBankTag(false);

		assertFalse(bankTag.open());

		verify(bankTagsService, never()).openBankTag(anyString(), anyInt());
	}

	@Test
	public void shutDownUnregistersTagAndRefreshesTheViewedTag()
	{
		viewTag(TAG);
		WastedBankTag bankTag = syncedBankTag(true);
		clearInvocations(bankTagsService);

		bankTag.shutDown();

		verify(tagManager).unregisterTag(TAG);
		assertFalse(bankTag.isEnabled());
		verify(bankTagsService).openBankTag(TAG, BankTagsService.OPTION_ALLOW_MODIFICATIONS);
	}

	@Test
	public void shutDownRunsOnClientThread()
	{
		WastedBankTag bankTag = syncedBankTag(true);
		runClientThreadImmediately = false;

		bankTag.shutDown();
		assertTrue(bankTag.isEnabled());
		verify(tagManager, never()).unregisterTag(anyString());

		runPendingClientThreadTasks();
		assertFalse(bankTag.isEnabled());
		verify(tagManager).unregisterTag(TAG);
	}

	// endregion

	// region tag contents

	@Test
	public void tagContainsOnlyFlaggedItems()
	{
		WastedBankTag bankTag = syncedBankTag(true);
		bankTag.setItems(setOf(FLAGGED_ITEM));

		BankTag tag = registeredTag();

		assertTrue(tag.contains(FLAGGED_ITEM));
		assertFalse(tag.contains(OTHER_ITEM));
	}

	@Test
	public void tagFollowsLatestFlaggedItems()
	{
		WastedBankTag bankTag = syncedBankTag(true);
		BankTag tag = registeredTag();

		bankTag.setItems(setOf(FLAGGED_ITEM));
		bankTag.setItems(setOf(OTHER_ITEM));

		assertFalse(tag.contains(FLAGGED_ITEM));
		assertTrue(tag.contains(OTHER_ITEM));
	}

	@Test
	public void tagIncludesPlaceholdersOfFlaggedItemsWhenEnabled()
	{
		when(config.bankTagPlaceholders()).thenReturn(true);
		WastedBankTag bankTag = syncedBankTag(true);
		bankTag.setItems(setOf(FLAGGED_ITEM));

		BankTag tag = registeredTag();

		assertTrue(tag.contains(FLAGGED_ITEM_PLACEHOLDER));
		assertTrue("real items must still match when placeholders are included", tag.contains(FLAGGED_ITEM));
		assertFalse(tag.contains(OTHER_ITEM_PLACEHOLDER));
		assertFalse(tag.contains(OTHER_ITEM));
	}

	@Test
	public void tagExcludesPlaceholdersWhenDisabled()
	{
		when(config.bankTagPlaceholders()).thenReturn(false);
		WastedBankTag bankTag = syncedBankTag(true);
		bankTag.setItems(setOf(FLAGGED_ITEM));

		BankTag tag = registeredTag();

		assertFalse(tag.contains(FLAGGED_ITEM_PLACEHOLDER));
		assertTrue(tag.contains(FLAGGED_ITEM));
	}

	@Test
	public void changingPlaceholderOptionAppliesToRegisteredTag()
	{
		when(config.bankTagPlaceholders()).thenReturn(false);
		WastedBankTag bankTag = syncedBankTag(true);
		bankTag.setItems(setOf(FLAGGED_ITEM));
		BankTag tag = registeredTag();
		assertFalse(tag.contains(FLAGGED_ITEM_PLACEHOLDER));

		// The plugin re-syncs when the option changes; the tag is already registered by then
		when(config.bankTagPlaceholders()).thenReturn(true);
		bankTag.sync();

		assertTrue(tag.contains(FLAGGED_ITEM_PLACEHOLDER));
	}

	@Test
	public void setItemsKeepsItsOwnCopy()
	{
		WastedBankTag bankTag = syncedBankTag(true);
		BankTag tag = registeredTag();
		Set<Integer> flagged = setOf(FLAGGED_ITEM);

		bankTag.setItems(flagged);
		flagged.remove(FLAGGED_ITEM);
		flagged.add(OTHER_ITEM);

		assertTrue(tag.contains(FLAGGED_ITEM));
		assertFalse(tag.contains(OTHER_ITEM));
	}

	// endregion

	// region refreshing the open tag

	@Test
	public void setItemsReopensTagWhenItIsBeingViewed()
	{
		WastedBankTag bankTag = syncedBankTagWhileViewing(TAG);

		bankTag.setItems(setOf(FLAGGED_ITEM));

		verify(bankTagsService).openBankTag(TAG, BankTagsService.OPTION_ALLOW_MODIFICATIONS);
	}

	@Test
	public void setItemsDoesNotOpenTagWhenAnotherTagOrNoTagIsViewed()
	{
		WastedBankTag bankTag = syncedBankTagWhileViewing("herbs");

		bankTag.setItems(setOf(FLAGGED_ITEM));
		viewTag(null);
		bankTag.setItems(setOf(OTHER_ITEM));

		verify(bankTagsService, never()).openBankTag(anyString(), anyInt());
	}

	@Test
	public void setItemsWithUnchangedItemsDoesNotReopenTag()
	{
		WastedBankTag bankTag = syncedBankTagWhileViewing(TAG);

		bankTag.setItems(setOf(FLAGGED_ITEM, OTHER_ITEM));
		bankTag.setItems(setOf(OTHER_ITEM, FLAGGED_ITEM));

		verify(bankTagsService, times(1)).openBankTag(TAG, BankTagsService.OPTION_ALLOW_MODIFICATIONS);
		verify(bankTagsService, times(1)).openBankTag(anyString(), anyInt());
	}

	@Test
	public void setItemsReopensTagOnClientThread()
	{
		WastedBankTag bankTag = syncedBankTagWhileViewing(TAG);
		runClientThreadImmediately = false;

		bankTag.setItems(setOf(FLAGGED_ITEM));
		verify(bankTagsService, never()).openBankTag(anyString(), anyInt());

		runPendingClientThreadTasks();
		verify(bankTagsService).openBankTag(TAG, BankTagsService.OPTION_ALLOW_MODIFICATIONS);
	}

	@Test
	public void syncWithOptionOnReopensViewedTagAfterRegisteringIt()
	{
		viewTag(TAG);

		syncedBankTag(true);

		InOrder inOrder = inOrder(tagManager, bankTagsService);
		inOrder.verify(tagManager).registerTag(eq(TAG), any(BankTag.class));
		inOrder.verify(bankTagsService).openBankTag(TAG, BankTagsService.OPTION_ALLOW_MODIFICATIONS);
	}

	@Test
	public void syncWithOptionOffReopensViewedTagAfterUnregisteringIt()
	{
		WastedBankTag bankTag = syncedBankTagWhileViewing(TAG);

		disable(bankTag);

		InOrder inOrder = inOrder(tagManager, bankTagsService);
		inOrder.verify(tagManager).unregisterTag(TAG);
		inOrder.verify(bankTagsService).openBankTag(TAG, BankTagsService.OPTION_ALLOW_MODIFICATIONS);
	}

	@Test
	public void syncDoesNotOpenTagWhenAnotherTagIsViewed()
	{
		viewTag("herbs");

		WastedBankTag bankTag = syncedBankTag(true);
		disable(bankTag);

		verify(bankTagsService, never()).openBankTag(anyString(), anyInt());
	}

	// endregion

	// region helpers

	private WastedBankTag newBankTag()
	{
		return new WastedBankTag(clientThread, tagManager, bankTagsService, config, itemManager);
	}

	private WastedBankTag syncedBankTag(boolean bankTagTab)
	{
		when(config.bankTagTab()).thenReturn(bankTagTab);
		WastedBankTag bankTag = newBankTag();
		bankTag.sync();
		return bankTag;
	}

	/** Enables the tab while the given tag is open in the bank, then forgets the refresh the sync itself did */
	private WastedBankTag syncedBankTagWhileViewing(String tag)
	{
		viewTag(tag);
		WastedBankTag bankTag = syncedBankTag(true);
		clearInvocations(bankTagsService);
		return bankTag;
	}

	private void disable(WastedBankTag bankTag)
	{
		when(config.bankTagTab()).thenReturn(false);
		bankTag.sync();
	}

	private BankTag registeredTag()
	{
		ArgumentCaptor<BankTag> captor = ArgumentCaptor.forClass(BankTag.class);
		verify(tagManager).registerTag(eq(TAG), captor.capture());
		return captor.getValue();
	}

	private void viewTag(String tag)
	{
		when(bankTagsService.getActiveTag()).thenReturn(tag);
	}

	private void mockItem(int itemId, int placeholderTemplateId, int placeholderId)
	{
		ItemComposition composition = mock(ItemComposition.class);
		when(composition.getPlaceholderTemplateId()).thenReturn(placeholderTemplateId);
		when(composition.getPlaceholderId()).thenReturn(placeholderId);
		when(itemManager.getItemComposition(itemId)).thenReturn(composition);
	}

	private static Set<Integer> setOf(Integer... itemIds)
	{
		return new HashSet<>(Arrays.asList(itemIds));
	}

	private void runPendingClientThreadTasks()
	{
		List<Runnable> tasks = new ArrayList<>(pendingClientThreadTasks);
		pendingClientThreadTasks.clear();
		tasks.forEach(Runnable::run);
	}

	/**
	 * Runs client thread work straight away, or queues it for {@link #runPendingClientThreadTasks()}
	 * when a test needs to check that work waits for the client thread.
	 */
	private ClientThread fakeClientThread()
	{
		ClientThread fake = mock(ClientThread.class);
		doAnswer(invocation ->
		{
			submit(invocation.getArgument(0));
			return null;
		}).when(fake).invokeLater(any(Runnable.class));
		doAnswer(invocation ->
		{
			BooleanSupplier task = invocation.getArgument(0);
			submit(task::getAsBoolean);
			return null;
		}).when(fake).invokeLater(any(BooleanSupplier.class));
		return fake;
	}

	private void submit(Runnable task)
	{
		if (runClientThreadImmediately)
		{
			task.run();
		}
		else
		{
			pendingClientThreadTasks.add(task);
		}
	}

	// endregion
}
