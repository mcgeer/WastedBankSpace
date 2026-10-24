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
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.banktags.BankTagsService;
import net.runelite.client.plugins.banktags.TagManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Set;

/**
 * Dynamic "wasted" bank tag holding every item flagged as wasting bank space. Searching the bank for tag:wasted
 * shows them, and so does a tag tab named "wasted" that the player makes. The plugin doesn't add the tab itself,
 * since that would mean changing the Bank Tags plugin's config.
 */
@Slf4j
@Singleton
public class WastedBankTag
{
	public static final String TAG_NAME = "wasted";

	private final ClientThread clientThread;
	private final TagManager tagManager;
	private final BankTagsService bankTagsService;
	private final WastedBankSpaceConfig config;

	private volatile Set<Integer> items = Collections.emptySet();
	private boolean registered = false;

	@Inject
	WastedBankTag(ClientThread clientThread, TagManager tagManager, BankTagsService bankTagsService,
		WastedBankSpaceConfig config)
	{
		this.clientThread = clientThread;
		this.tagManager = tagManager;
		this.bankTagsService = bankTagsService;
		this.config = config;
	}

	public void startUp()
	{
		clientThread.invokeLater(this::sync);
	}

	public void shutDown()
	{
		clientThread.invokeLater(() ->
		{
			unregisterTag();
			refreshIfActive();
		});
	}

	/**
	 * Applies the bank tag config option. Must be called on the client thread.
	 */
	public void sync()
	{
		if (config.bankTagTab())
		{
			registerTag();
		}
		else
		{
			unregisterTag();
		}
		refreshIfActive();
	}

	/**
	 * Updates the items in the tag, i.e. the items currently flagged as storable elsewhere.
	 */
	public void setItems(Set<Integer> flaggedItems)
	{
		Set<Integer> copy = Set.copyOf(flaggedItems);
		if (copy.equals(items))
		{
			return;
		}
		items = copy;
		clientThread.invokeLater(this::refreshIfActive);
	}

	public boolean isEnabled()
	{
		return registered;
	}

	private void registerTag()
	{
		if (!registered)
		{
			tagManager.registerTag(TAG_NAME, itemId -> items.contains(itemId));
			registered = true;
			log.debug("Registered bank tag '{}'", TAG_NAME);
		}
	}

	private void unregisterTag()
	{
		if (registered)
		{
			tagManager.unregisterTag(TAG_NAME);
			registered = false;
			log.debug("Unregistered bank tag '{}'", TAG_NAME);
		}
	}

	/**
	 * Re-opens the tag if it's the one being viewed, so the bank reflects the current flagged items.
	 */
	private void refreshIfActive()
	{
		if (TAG_NAME.equals(bankTagsService.getActiveTag()))
		{
			bankTagsService.openBankTag(TAG_NAME, BankTagsService.OPTION_ALLOW_MODIFICATIONS);
		}
	}
}
