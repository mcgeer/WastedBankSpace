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
import net.runelite.api.gameval.ItemID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.banktags.BankTagsPlugin;
import net.runelite.client.plugins.banktags.BankTagsService;
import net.runelite.client.plugins.banktags.TagManager;
import net.runelite.client.util.Text;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The fallback tab icon belongs only to a tab this plugin created. A "wasted" tab the user made themselves,
 * with or without an icon of their own, is left exactly as found: {@code removeTab()} never touches a user tab,
 * so an icon written onto one would have stuck for good.
 */
public class WastedBankTagIconTest
{
	/** Written into the user's bank tags config, so it must not change between plugin versions */
	private static final String TAG = "wasted";
	private static final String TABS_KEY = BankTagsPlugin.TAG_TABS_CONFIG;
	private static final String ICON_KEY = BankTagsPlugin.TAG_ICON_PREFIX + TAG;
	private static final String CREATED_KEY = "bankTagTabCreated";
	private static final String FALLBACK_ICON = String.valueOf(ItemID.BANK_FILLER);

	/** Made-up item id for an icon the user picked themselves */
	private static final int USER_ICON_ITEM = 4151;

	/** Persisted config keyed by "group.key"; it outlives a WastedBankTag instance, like across a client restart */
	private final Map<String, String> storedConfig = new HashMap<>();

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
		clientThread = immediateClientThread();
		configManager = fakeConfigManager();
	}

	// region user's own tab

	@Test
	public void syncWithOptionOnLeavesUsersOwnTabWithoutIconAlone()
	{
		// The user made a "wasted" tab before this plugin ever ran; bank tags shows it with its default icon
		setBankTagsConfig(TABS_KEY, "herbs," + TAG + ",runes");

		syncedBankTag(true);

		assertNull("a user's tab must not be given the plugin's fallback icon", bankTagsConfig(ICON_KEY));
		assertNull("a tab the plugin did not add must not be marked as created by it", pluginConfig(CREATED_KEY));
		assertEquals(Arrays.asList("herbs", TAG, "runes"), tabNames());
	}

	@Test
	public void syncWithOptionOnKeepsIconOfUsersOwnTab()
	{
		setBankTagsConfig(TABS_KEY, "herbs," + TAG);
		setBankTagsConfig(ICON_KEY, String.valueOf(USER_ICON_ITEM));

		syncedBankTag(true);

		assertEquals(String.valueOf(USER_ICON_ITEM), bankTagsConfig(ICON_KEY));
		assertNull(pluginConfig(CREATED_KEY));
	}

	@Test
	public void repeatedSyncNeverGivesUsersOwnTabAnIcon()
	{
		setBankTagsConfig(TABS_KEY, TAG);
		WastedBankTag bankTag = syncedBankTag(true);

		// Every option change and startup re-syncs; none of them may claim the tab
		bankTag.sync();
		bankTag.sync();
		syncedBankTag(true);

		assertNull(bankTagsConfig(ICON_KEY));
		assertNull(pluginConfig(CREATED_KEY));
		assertEquals(Collections.singletonList(TAG), tabNames());
	}

	@Test
	public void turningOptionOnThenOffLeavesUsersOwnIconlessTabExactlyAsFound()
	{
		setBankTagsConfig(TABS_KEY, "herbs," + TAG);
		WastedBankTag bankTag = syncedBankTag(true);

		disable(bankTag);

		// removeTab() leaves a user tab alone, so anything addTab() wrote onto it would have stuck for good
		assertEquals(Arrays.asList("herbs", TAG), tabNames());
		assertNull(bankTagsConfig(ICON_KEY));
		assertNull(pluginConfig(CREATED_KEY));
	}

	@Test
	public void createdFlagStoredAsFalseDoesNotClaimUsersOwnTab()
	{
		// Ownership is the flag's value, not the key merely being present
		setBankTagsConfig(TABS_KEY, "herbs," + TAG);
		setPluginConfig(CREATED_KEY, "false");

		syncedBankTag(true);

		assertNull(bankTagsConfig(ICON_KEY));
		assertEquals("false", pluginConfig(CREATED_KEY));
		assertEquals(Arrays.asList("herbs", TAG), tabNames());
	}

	@Test
	public void pluginRestartLeavesUsersOwnTabWithoutIconAlone()
	{
		// The plugin's real entry points: enabling and disabling the whole plugin, not just the option
		setBankTagsConfig(TABS_KEY, "herbs," + TAG);
		when(config.bankTagTab()).thenReturn(true);
		WastedBankTag bankTag = newBankTag();

		bankTag.startUp();
		bankTag.shutDown();
		newBankTag().startUp();

		assertNull(bankTagsConfig(ICON_KEY));
		assertNull(pluginConfig(CREATED_KEY));
		assertEquals(Arrays.asList("herbs", TAG), tabNames());
	}

	// endregion

	// region plugin's own tab

	@Test
	public void syncWithOptionOnSetsFallbackIconAndCreatedFlagOnFreshlyAddedTab()
	{
		setBankTagsConfig(TABS_KEY, "herbs");

		syncedBankTag(true);

		assertEquals(Arrays.asList("herbs", TAG), tabNames());
		assertEquals(FALLBACK_ICON, bankTagsConfig(ICON_KEY));
		assertEquals("true", pluginConfig(CREATED_KEY));
	}

	@Test
	public void syncWithOptionOnSetsFallbackIconOnTabPluginCreatedWhenIconIsMissing()
	{
		// The plugin's tab from an earlier session, whose icon config has since gone
		setBankTagsConfig(TABS_KEY, "herbs," + TAG);
		setPluginConfig(CREATED_KEY, "true");

		syncedBankTag(true);

		assertEquals(FALLBACK_ICON, bankTagsConfig(ICON_KEY));
		assertEquals("true", pluginConfig(CREATED_KEY));
		assertEquals(Arrays.asList("herbs", TAG), tabNames());
	}

	@Test
	public void newInstanceRestoresFallbackIconOfTabPluginCreatedBeforeRestart()
	{
		setBankTagsConfig(TABS_KEY, "herbs");
		syncedBankTag(true);
		assertEquals(FALLBACK_ICON, bankTagsConfig(ICON_KEY));

		// The icon config is lost between sessions, then a client restart brings a new instance
		storedConfig.remove(wholeKey(BankTagsPlugin.CONFIG_GROUP, ICON_KEY));
		syncedBankTag(true);

		assertEquals(FALLBACK_ICON, bankTagsConfig(ICON_KEY));
		assertEquals(Arrays.asList("herbs", TAG), tabNames());
	}

	@Test
	public void syncWithOptionOnKeepsIconChosenByUserOnTabPluginCreated()
	{
		setBankTagsConfig(TABS_KEY, "herbs," + TAG);
		setBankTagsConfig(ICON_KEY, String.valueOf(USER_ICON_ITEM));
		setPluginConfig(CREATED_KEY, "true");

		syncedBankTag(true);

		assertEquals(String.valueOf(USER_ICON_ITEM), bankTagsConfig(ICON_KEY));
	}

	@Test
	public void syncWithOptionOnReAddsPluginTabDeletedByHandWithFallbackIcon()
	{
		// The user deleted the plugin's tab in the bank UI (bank tags drops its icon too) while the option stayed on
		setBankTagsConfig(TABS_KEY, "herbs");
		setPluginConfig(CREATED_KEY, "true");

		syncedBankTag(true);

		assertEquals(Arrays.asList("herbs", TAG), tabNames());
		assertEquals(FALLBACK_ICON, bankTagsConfig(ICON_KEY));
		assertEquals("true", pluginConfig(CREATED_KEY));
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

	private void disable(WastedBankTag bankTag)
	{
		when(config.bankTagTab()).thenReturn(false);
		bankTag.sync();
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

	private void setPluginConfig(String key, String value)
	{
		storedConfig.put(wholeKey(WastedBankSpaceConfig.GROUP, key), value);
	}

	/** Runs client thread work straight away; these tests only care about what ends up in config */
	private static ClientThread immediateClientThread()
	{
		ClientThread fake = mock(ClientThread.class);
		doAnswer(invocation ->
		{
			((Runnable) invocation.getArgument(0)).run();
			return null;
		}).when(fake).invokeLater(any(Runnable.class));
		return fake;
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
