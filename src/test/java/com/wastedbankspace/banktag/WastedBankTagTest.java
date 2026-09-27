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
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.banktags.BankTag;
import net.runelite.client.plugins.banktags.BankTagsPlugin;
import net.runelite.client.plugins.banktags.BankTagsService;
import net.runelite.client.plugins.banktags.TagManager;
import net.runelite.client.util.Text;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.awt.Rectangle;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
	/** Written into the user's bank tags config, so it must not change between plugin versions */
	private static final String TAG = "wasted";
	private static final String TABS_KEY = BankTagsPlugin.TAG_TABS_CONFIG;
	private static final String ICON_KEY = BankTagsPlugin.TAG_ICON_PREFIX + TAG;
	private static final String CREATED_KEY = "bankTagTabCreated";

	/** Made-up item ids; a real item and its placeholder point at each other, as in the game's item definitions */
	private static final int FLAGGED_ITEM = 1001;
	private static final int FLAGGED_ITEM_PLACEHOLDER = 1002;
	private static final int OTHER_ITEM = 2001;
	private static final int OTHER_ITEM_PLACEHOLDER = 2002;
	private static final int PLACEHOLDER_TEMPLATE = 14401;
	private static final int USER_ICON_ITEM = 4151;

	/** Persisted config keyed by "group.key"; it outlives a WastedBankTag instance, like across a client restart */
	private final Map<String, String> storedConfig = new HashMap<>();
	private final List<Runnable> pendingClientThreadTasks = new ArrayList<>();
	private boolean runClientThreadImmediately = true;

	private Client client;
	private ClientThread clientThread;
	private ConfigManager configManager;
	private TagManager tagManager;
	private BankTagsService bankTagsService;
	private WastedBankSpaceConfig config;
	private ItemManager itemManager;

	@Before
	public void setUp()
	{
		client = mock(Client.class);
		tagManager = mock(TagManager.class);
		bankTagsService = mock(BankTagsService.class);
		config = mock(WastedBankSpaceConfig.class);
		itemManager = mock(ItemManager.class);
		clientThread = fakeClientThread();
		configManager = fakeConfigManager();

		mockItem(FLAGGED_ITEM, -1, FLAGGED_ITEM_PLACEHOLDER);
		mockItem(FLAGGED_ITEM_PLACEHOLDER, PLACEHOLDER_TEMPLATE, FLAGGED_ITEM);
		mockItem(OTHER_ITEM, -1, OTHER_ITEM_PLACEHOLDER);
		mockItem(OTHER_ITEM_PLACEHOLDER, PLACEHOLDER_TEMPLATE, OTHER_ITEM);
	}

	// region enabling the tab

	@Test
	public void syncWithOptionOnRegistersTag()
	{
		WastedBankTag bankTag = syncedBankTag(true);

		verify(tagManager).registerTag(eq(TAG), any(BankTag.class));
		assertTrue(bankTag.isEnabled());
	}

	@Test
	public void syncWithOptionOnCreatesTabWhenThereAreNoTabs()
	{
		syncedBankTag(true);

		assertEquals(Collections.singletonList(TAG), tabNames());
		assertEquals("true", pluginConfig(CREATED_KEY));
	}

	@Test
	public void syncWithOptionOnAppendsTabAfterExistingTabs()
	{
		setBankTagsConfig(TABS_KEY, "herbs,runes");

		syncedBankTag(true);

		assertEquals(Arrays.asList("herbs", "runes", TAG), tabNames());
		assertEquals("true", pluginConfig(CREATED_KEY));
	}

	@Test
	public void syncWithOptionOnSetsFallbackIconWhenTabHasNone()
	{
		syncedBankTag(true);

		assertEquals(String.valueOf(ItemID.BANK_FILLER), bankTagsConfig(ICON_KEY));
	}

	@Test
	public void syncWithOptionOnKeepsIconChosenByUser()
	{
		setBankTagsConfig(ICON_KEY, String.valueOf(USER_ICON_ITEM));

		syncedBankTag(true);

		assertEquals(String.valueOf(USER_ICON_ITEM), bankTagsConfig(ICON_KEY));
	}

	@Test
	public void syncWithOptionOnDoesNotDuplicateOrClaimUsersOwnTab()
	{
		setBankTagsConfig(TABS_KEY, "herbs," + TAG + ",runes");

		syncedBankTag(true);

		assertEquals(Arrays.asList("herbs", TAG, "runes"), tabNames());
		assertNull("a tab the plugin did not add must not be marked as created by it", pluginConfig(CREATED_KEY));
	}

	@Test
	public void repeatedSyncDoesNotDuplicateTab()
	{
		setBankTagsConfig(TABS_KEY, "herbs");
		WastedBankTag bankTag = syncedBankTag(true);

		bankTag.sync();
		bankTag.sync();

		assertEquals(Arrays.asList("herbs", TAG), tabNames());
		assertEquals("true", pluginConfig(CREATED_KEY));
	}

	@Test
	public void startUpAppliesOptionOnClientThread()
	{
		when(config.bankTagTab()).thenReturn(true);
		WastedBankTag bankTag = newBankTag();
		runClientThreadImmediately = false;

		bankTag.startUp();
		assertFalse(bankTag.isEnabled());
		assertNull(bankTagsConfig(TABS_KEY));

		runPendingClientThreadTasks();
		assertTrue(bankTag.isEnabled());
		assertEquals(Collections.singletonList(TAG), tabNames());
	}

	// endregion

	// region disabling the tab

	@Test
	public void syncWithOptionOffUnregistersTag()
	{
		WastedBankTag bankTag = syncedBankTag(true);

		disable(bankTag);

		verify(tagManager).unregisterTag(TAG);
		assertFalse(bankTag.isEnabled());
	}

	@Test
	public void syncWithOptionOffRemovesTabPluginCreated()
	{
		setBankTagsConfig(TABS_KEY, "herbs,runes");
		WastedBankTag bankTag = syncedBankTag(true);

		disable(bankTag);

		assertEquals(Arrays.asList("herbs", "runes"), tabNames());
		assertNull(bankTagsConfig(ICON_KEY));
		assertNull(pluginConfig(CREATED_KEY));
	}

	@Test
	public void syncWithOptionOffKeepsUsersOwnTab()
	{
		setBankTagsConfig(TABS_KEY, "herbs," + TAG);
		setBankTagsConfig(ICON_KEY, String.valueOf(USER_ICON_ITEM));
		WastedBankTag bankTag = syncedBankTag(true);

		disable(bankTag);

		assertEquals(Arrays.asList("herbs", TAG), tabNames());
		assertEquals(String.valueOf(USER_ICON_ITEM), bankTagsConfig(ICON_KEY));
	}

	@Test
	public void syncWithOptionOffAtStartupKeepsUsersOwnTab()
	{
		setBankTagsConfig(TABS_KEY, TAG + ",herbs");
		setBankTagsConfig(ICON_KEY, String.valueOf(USER_ICON_ITEM));

		WastedBankTag bankTag = syncedBankTag(false);

		assertFalse(bankTag.isEnabled());
		verify(tagManager, never()).registerTag(anyString(), any(BankTag.class));
		assertEquals(Arrays.asList(TAG, "herbs"), tabNames());
		assertEquals(String.valueOf(USER_ICON_ITEM), bankTagsConfig(ICON_KEY));
	}

	@Test
	public void tabCreatedBeforeRestartIsStillRemovedWhenDisabled()
	{
		setBankTagsConfig(TABS_KEY, "herbs");
		syncedBankTag(true);

		// Client restart: a new instance finds the tab already there
		WastedBankTag afterRestart = syncedBankTag(true);
		assertEquals(Arrays.asList("herbs", TAG), tabNames());

		disable(afterRestart);

		assertEquals(Collections.singletonList("herbs"), tabNames());
		assertNull(bankTagsConfig(ICON_KEY));
	}

	@Test
	public void userTabAddedAfterPluginTabWasDeletedSurvivesDisabling()
	{
		setBankTagsConfig(TABS_KEY, "herbs");
		WastedBankTag bankTag = syncedBankTag(true);

		// The user deletes the plugin's tab by hand, then turns the option off
		setBankTagsConfig(TABS_KEY, "herbs");
		disable(bankTag);
		assertEquals(Collections.singletonList("herbs"), tabNames());
		assertNull(pluginConfig(CREATED_KEY));

		// Later they make their own "wasted" tab; the option being off must leave it alone
		setBankTagsConfig(TABS_KEY, "herbs," + TAG);
		setBankTagsConfig(ICON_KEY, String.valueOf(USER_ICON_ITEM));
		bankTag.sync();

		assertEquals(Arrays.asList("herbs", TAG), tabNames());
		assertEquals(String.valueOf(USER_ICON_ITEM), bankTagsConfig(ICON_KEY));
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

	// region tab widgets

	@Test
	public void findTabWidgetsReturnsBackgroundAndIconOfWastedTab()
	{
		Widget background = tabWidget(TAG, -1);
		Widget icon = tabWidget(TAG, ItemID.BANK_FILLER);
		showTabBar(null, tabWidget("herbs", -1), tabWidget("herbs", 249), icon, background, mock(Widget.class));

		Widget[] tab = newBankTag().findTabWidgets();

		assertArrayEquals(new Widget[]{background, icon}, tab);
	}

	@Test
	public void findTabWidgetsReturnsNullWhenTabIsNotShowing()
	{
		WastedBankTag bankTag = newBankTag();

		assertNull("bank closed", bankTag.findTabWidgets());

		Widget container = showTabBar(tabWidget(TAG, -1), tabWidget(TAG, ItemID.BANK_FILLER));
		when(container.isHidden()).thenReturn(true);
		assertNull("tab bar hidden", bankTag.findTabWidgets());

		showTabBar(tabWidget("herbs", -1), tabWidget("herbs", 249));
		assertNull("no wasted tab", bankTag.findTabWidgets());

		showTabBar(tabWidget(TAG, -1));
		assertNull("icon missing", bankTag.findTabWidgets());

		showTabBar(tabWidget(TAG, ItemID.BANK_FILLER));
		assertNull("background missing", bankTag.findTabWidgets());

		showTabBar((Widget[]) null);
		assertNull("no children", bankTag.findTabWidgets());
	}

	@Test
	public void getTabBoundsPlacesTabInTabBarBeforeItHasBeenDrawn()
	{
		// Right after the bank tags plugin rebuilds its tabs, as on every tab switch and search keystroke, the new
		// widgets haven't been drawn yet and have no canvas location of their own
		Widget background = placedTabWidget(4, 57, 41, 40);
		when(background.getCanvasLocation()).thenReturn(new Point(0, 0));
		when(background.getBounds()).thenReturn(new Rectangle(0, 0, 41, 40));
		Widget container = showTabBar(background, tabWidget(TAG, ItemID.BANK_FILLER));
		when(container.getCanvasLocation()).thenReturn(new Point(100, 200));

		assertEquals(new Rectangle(104, 257, 41, 40), newBankTag().getTabBounds());
	}

	@Test
	public void getTabBoundsFollowsTabBarScroll()
	{
		Widget container = showTabBar(placedTabWidget(4, 57, 41, 40), tabWidget(TAG, ItemID.BANK_FILLER));
		when(container.getCanvasLocation()).thenReturn(new Point(100, 200));
		when(container.getScrollX()).thenReturn(2);
		when(container.getScrollY()).thenReturn(10);

		assertEquals(new Rectangle(102, 247, 41, 40), newBankTag().getTabBounds());
	}

	@Test
	public void getTabBoundsReturnsNullWhenTabIsNotShowing()
	{
		WastedBankTag bankTag = newBankTag();

		assertNull("bank closed", bankTag.getTabBounds());

		showTabBar(tabWidget("herbs", -1), tabWidget("herbs", 249));
		assertNull("no wasted tab", bankTag.getTabBounds());

		// Tabs scrolled out of the tab bar are hidden
		Widget background = placedTabWidget(4, 57, 41, 40);
		when(background.isHidden()).thenReturn(true);
		showTabBar(background, tabWidget(TAG, ItemID.BANK_FILLER));
		assertNull("tab scrolled away", bankTag.getTabBounds());
	}

	@Test
	public void onBeforeRenderMakesTabIconTransparentWhileEnabled()
	{
		Widget background = tabWidget(TAG, -1);
		Widget icon = tabWidget(TAG, ItemID.BANK_FILLER);
		showTabBar(background, icon);
		WastedBankTag bankTag = syncedBankTag(true);

		bankTag.onBeforeRender();

		verify(icon).setOpacity(255);
		verify(background, never()).setOpacity(anyInt());
	}

	@Test
	public void onBeforeRenderCopesWithBankBeingClosed()
	{
		WastedBankTag bankTag = syncedBankTag(true);
		assertTrue(bankTag.isEnabled());

		// Runs every frame, most of which are drawn with the bank closed
		bankTag.onBeforeRender();
	}

	@Test
	public void onBeforeRenderLeavesTabIconAloneWhileDisabled()
	{
		Widget icon = tabWidget(TAG, ItemID.BANK_FILLER);
		showTabBar(tabWidget(TAG, -1), icon);
		WastedBankTag bankTag = syncedBankTag(false);

		bankTag.onBeforeRender();

		verify(icon, never()).setOpacity(255);
	}

	@Test
	public void disablingRestoresTabIcon()
	{
		Widget icon = tabWidget(TAG, ItemID.BANK_FILLER);
		showTabBar(tabWidget(TAG, -1), icon);
		WastedBankTag bankTag = syncedBankTag(true);
		bankTag.onBeforeRender();

		disable(bankTag);

		verify(icon).setOpacity(0);
	}

	@Test
	public void shutDownUnregistersTagAndRestoresIconButKeepsTab()
	{
		setBankTagsConfig(TABS_KEY, "herbs");
		Widget icon = tabWidget(TAG, ItemID.BANK_FILLER);
		showTabBar(tabWidget(TAG, -1), icon);
		viewTag(TAG);
		WastedBankTag bankTag = syncedBankTag(true);
		clearInvocations(bankTagsService);

		bankTag.shutDown();

		verify(tagManager).unregisterTag(TAG);
		assertFalse(bankTag.isEnabled());
		verify(icon).setOpacity(0);
		verify(bankTagsService).openBankTag(TAG, BankTagsService.OPTION_ALLOW_MODIFICATIONS);
		// The tab stays so it keeps its position when the plugin is turned back on
		assertEquals(Arrays.asList("herbs", TAG), tabNames());
		assertEquals("true", pluginConfig(CREATED_KEY));
	}

	@Test
	public void shutDownRunsOnClientThread()
	{
		Widget icon = tabWidget(TAG, ItemID.BANK_FILLER);
		showTabBar(tabWidget(TAG, -1), icon);
		WastedBankTag bankTag = syncedBankTag(true);
		runClientThreadImmediately = false;

		bankTag.shutDown();
		assertTrue(bankTag.isEnabled());
		verify(tagManager, never()).unregisterTag(anyString());
		verify(icon, never()).setOpacity(anyInt());

		runPendingClientThreadTasks();
		assertFalse(bankTag.isEnabled());
		verify(tagManager).unregisterTag(TAG);
		verify(icon).setOpacity(0);
	}

	// endregion

	// region helpers

	private WastedBankTag newBankTag()
	{
		return new WastedBankTag(client, clientThread, configManager, tagManager, bankTagsService, config, itemManager);
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

	private Widget showTabBar(Widget... children)
	{
		Widget container = mock(Widget.class);
		when(container.getChildren()).thenReturn(children);
		when(client.getWidget(InterfaceID.Bankmain.ITEMS_CONTAINER)).thenReturn(container);
		return container;
	}

	/** Tag tab widgets are named after their tag inside a colour tag, and only the icon holds an item */
	private static Widget tabWidget(String tag, int itemId)
	{
		Widget widget = mock(Widget.class);
		when(widget.getName()).thenReturn("<col=ff9040>" + tag + "</col>");
		when(widget.getItemId()).thenReturn(itemId);
		return widget;
	}

	/** The wasted tab's background, laid out at the given place in the tab bar */
	private static Widget placedTabWidget(int relativeX, int relativeY, int width, int height)
	{
		Widget background = tabWidget(TAG, -1);
		when(background.getRelativeX()).thenReturn(relativeX);
		when(background.getRelativeY()).thenReturn(relativeY);
		when(background.getWidth()).thenReturn(width);
		when(background.getHeight()).thenReturn(height);
		return background;
	}

	private List<String> tabNames()
	{
		String tabs = bankTagsConfig(TABS_KEY);
		return tabs == null ? Collections.emptyList() : Text.fromCSV(tabs);
	}

	private String bankTagsConfig(String key)
	{
		return storedConfig.get(wholeKey(BankTagsPlugin.CONFIG_GROUP, key));
	}

	private void setBankTagsConfig(String key, String value)
	{
		storedConfig.put(wholeKey(BankTagsPlugin.CONFIG_GROUP, key), value);
	}

	private String pluginConfig(String key)
	{
		return storedConfig.get(wholeKey(WastedBankSpaceConfig.GROUP, key));
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

	/**
	 * In-memory ConfigManager that stores every value as a string, like the real one.
	 */
	private ConfigManager fakeConfigManager()
	{
		ConfigManager fake = mock(ConfigManager.class);
		doAnswer(invocation -> storedConfig.get(wholeKey(invocation.getArgument(0), invocation.getArgument(1))))
			.when(fake).getConfiguration(anyString(), anyString());
		doAnswer(invocation -> parse(storedConfig.get(wholeKey(invocation.getArgument(0), invocation.getArgument(1))),
			invocation.getArgument(2)))
			.when(fake).getConfiguration(anyString(), anyString(), any(Type.class));
		doAnswer(invocation -> storedConfig.put(wholeKey(invocation.getArgument(0), invocation.getArgument(1)),
			invocation.getArgument(2)))
			.when(fake).setConfiguration(anyString(), anyString(), anyString());
		doAnswer(invocation -> storedConfig.put(wholeKey(invocation.getArgument(0), invocation.getArgument(1)),
			String.valueOf((Object) invocation.getArgument(2))))
			.when(fake).setConfiguration(anyString(), anyString(), (Object) any());
		doAnswer(invocation -> storedConfig.remove(wholeKey(invocation.getArgument(0), invocation.getArgument(1))))
			.when(fake).unsetConfiguration(anyString(), anyString());
		return fake;
	}

	private static String wholeKey(String group, String key)
	{
		return group + "." + key;
	}

	private static Object parse(String value, Type type)
	{
		if (value == null || value.isEmpty())
		{
			return null;
		}
		if (type == Boolean.class)
		{
			return Boolean.parseBoolean(value);
		}
		if (type == Integer.class)
		{
			return Integer.parseInt(value);
		}
		if (type == String.class)
		{
			return value;
		}
		throw new UnsupportedOperationException("Unsupported config type " + type);
	}

	// endregion
}
