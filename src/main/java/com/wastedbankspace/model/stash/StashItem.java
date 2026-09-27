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
package com.wastedbankspace.model.stash;

import com.wastedbankspace.model.StorableItem;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An item that can be stored in a particular STASH unit
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public final class StashItem implements StorableItem
{
	/** One entry for every item of every STASH unit, in unit order */
	public static final List<StashItem> ALL;
	/** The STASH units that take each item, in unit order */
	private static final Map<Integer, List<StashUnit>> UNITS_BY_ITEM = new HashMap<>();

	static
	{
		List<StashItem> items = new ArrayList<>();
		for (StashUnit unit : StashUnit.values())
		{
			for (int itemId : unit.getItemIds())
			{
				items.add(new StashItem(itemId, unit));
				UNITS_BY_ITEM.computeIfAbsent(itemId, id -> new ArrayList<>()).add(unit);
			}
		}
		ALL = Collections.unmodifiableList(items);
	}

	/**
	 * @return every STASH unit that takes the item, in unit order; empty if none does
	 */
	public static List<StashUnit> getUnits(int itemId)
	{
		return Collections.unmodifiableList(UNITS_BY_ITEM.getOrDefault(itemId, Collections.emptyList()));
	}

	private final int itemID;
	private final StashUnit unit;

	@Override
	public String getLocation()
	{
		return unit.getLocation();
	}

	@Override
	public boolean isBis()
	{
		return false;
	}
}
