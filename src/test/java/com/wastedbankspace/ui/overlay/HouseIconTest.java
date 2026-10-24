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

import net.runelite.client.util.Text;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HouseIconTest
{
	@Test
	public void everyHouseIconUsesItsOwnSprite()
	{
		Map<Integer, HouseIcon> bySprite = new HashMap<>();
		for (HouseIcon icon : HouseIcon.values())
		{
			assertTrue(icon.name() + " has an invalid sprite id " + icon.getSpriteId(), icon.getSpriteId() >= 0);
			HouseIcon previous = bySprite.put(icon.getSpriteId(), icon);
			assertNull(previous + " and " + icon + " show the same sprite", previous);
		}
	}

	@Test
	public void dropdownShowsEachHouseIconByItsOwnName()
	{
		Set<String> labels = new HashSet<>();
		for (HouseIcon icon : HouseIcon.values())
		{
			// RuneLite's config dropdown labels enum values with Text.titleCase
			String label = Text.titleCase(icon);
			assertEquals(icon.getName(), icon.toString());
			assertEquals(icon.getName(), label);
			assertFalse(icon.name() + " has a blank name", label.trim().isEmpty());
			assertTrue("two house icons are both called " + label, labels.add(label));
		}
	}
}
