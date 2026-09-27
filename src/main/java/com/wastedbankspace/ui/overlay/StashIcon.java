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
package com.wastedbankspace.ui.overlay;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.SpriteID;

/**
 * Icons for items that are already stashed. Each is either an item image or a sprite.
 */
@Getter
@RequiredArgsConstructor
public enum StashIcon
{
	CLUE_SCROLL("Clue Scroll", ItemID.TRAIL_CLUE_EASY_SIMPLE001, -1),
	MASTER_CLUE("Master Clue", ItemID.TRAIL_CLUE_MASTER, -1),
	CASKET("Casket", ItemID.TRAIL_REWARD_CASKET_HARD, -1),
	CLUE_SCROLLS_ICON("Clue Scrolls Icon", -1, SpriteID.IconActivities25x25.CLUE_SCROLL_ALL),
	GREEN_TICK("Green Tick", -1, SpriteID.TICK),
	GREEN_CHECK("Green Check", -1, SpriteID.OptionsRadioButtons.CHECK_GREEN);

	private final String name;
	/** The item whose image is the icon, or -1 if the icon is a sprite */
	private final int itemId;
	/** The sprite used as the icon, or -1 if the icon is an item image */
	private final int spriteId;

	@Override
	public String toString()
	{
		return name;
	}
}
