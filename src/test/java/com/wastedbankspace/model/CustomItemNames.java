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

package com.wastedbankspace.model;

import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Prepares the storable item names once more, the way {@link ItemNameFixture} does, but with the given names for
 * some of the items: for spellings the fixture does not have, such as an all upper case name. Every other item keeps
 * its {@link ItemNameFixture#nameOf(int)} name.
 * <p>
 * Meant to be called between {@link ItemNameFixture#load()} and {@link ItemNameFixture#restore()}, which put the
 * previous names back afterwards. The names the fixture indexed stay in the index next to the new ones.
 */
public final class CustomItemNames
{
	private CustomItemNames()
	{
	}

	/**
	 * @param names item ID to the name the mocked {@link ItemManager} gives the item
	 */
	public static void prepare(Map<Integer, String> names)
	{
		StorageLocations.prepareStorableItemNames(itemManager(names));
	}

	private static ItemManager itemManager(Map<Integer, String> names)
	{
		Map<Integer, ItemComposition> compositions = new HashMap<>();
		for (int itemId : StorageLocations.getItemIdMap().keySet())
		{
			ItemComposition composition = mock(ItemComposition.class);
			when(composition.getName()).thenReturn(names.getOrDefault(itemId, ItemNameFixture.nameOf(itemId)));
			compositions.put(itemId, composition);
		}

		ItemManager itemManager = mock(ItemManager.class);
		when(itemManager.getItemComposition(anyInt()))
			.thenAnswer(invocation -> compositions.get(invocation.getArgument(0)));
		return itemManager;
	}
}
