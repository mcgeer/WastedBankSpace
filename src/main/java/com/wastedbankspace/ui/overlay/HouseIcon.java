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
import net.runelite.api.gameval.SpriteID;

/**
 * Game sprites that can be used as the icon for items already stored in the POH.
 */
@Getter
@RequiredArgsConstructor
public enum HouseIcon
{
	HOUSE_OPTIONS("House Options", SpriteID.OptionsIcons.HOUSE_OPTIONS),
	// The display name must differ from the constant name, or RuneLite title-cases it to "Poh" in the dropdown
	POH("POH Icon", SpriteID.OPTIONS_POH_ICON),
	SIDE_ICON("Side Icon", SpriteID.SideIcons.HOUSE),
	TELEPORT_SPELL("Teleport to House", SpriteID.Magicon2.TELEPORT_TO_HOUSE),
	MAP_PORTAL("Map House Portal", SpriteID.Mapfunction.HOUSE_PORTAL);

	private final String name;
	private final int spriteId;

	@Override
	public String toString()
	{
		return name;
	}
}
