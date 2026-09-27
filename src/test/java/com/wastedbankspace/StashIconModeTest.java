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

import com.wastedbankspace.model.stash.StashIconMode;
import com.wastedbankspace.model.stash.StashUnit;
import com.wastedbankspace.model.stash.StashUnitFilter;
import com.wastedbankspace.stash.StashTracker;
import net.runelite.api.gameval.ItemID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Whether an item gets the STASH icon, under each "Show STASH Icon When" option. A gold ring goes in three STASH
 * units: near a shed in Lumbridge Swamp and Aris' tent, both built and filled here, and Rimmington mine, which isn't
 * built.
 */
@RunWith(MockitoJUnitRunner.class)
public class StashIconModeTest
{
	@Mock
	private WastedBankSpaceConfig config;

	@Mock
	private StashTracker stashTracker;

	@InjectMocks
	private WastedBankSpacePlugin plugin;

	@Before
	public void setUp()
	{
		lenient().when(config.stashUnits()).thenReturn(true);
		builtAndFilled(StashUnit.NEAR_A_SHED_IN_LUMBRIDGE_SWAMP, true, true);
		builtAndFilled(StashUnit.GYPSY_TENT_ENTRANCE, true, true);
		builtAndFilled(StashUnit.RIMMINGTON_MINE, false, false);
	}

	@Test
	public void atLeastOneUnitShowsIconWhileAnotherCountedUnitIsEmpty()
	{
		show(StashIconMode.ANY_UNIT, StashUnitFilter.ALL);

		assertTrue(goldRingStashed());
	}

	@Test
	public void everyUnitHidesIconWhileAnotherCountedUnitIsEmpty()
	{
		show(StashIconMode.EVERY_UNIT, StashUnitFilter.ALL);

		assertFalse(goldRingStashed());
	}

	@Test
	public void everyUnitOnlyCountsBuiltUnitsWhenOnlyBuiltUnitsAreChosen()
	{
		// Rimmington mine isn't built, so it doesn't count, and both units that do count are filled
		show(StashIconMode.EVERY_UNIT, StashUnitFilter.BUILT);

		assertTrue(goldRingStashed());
	}

	@Test
	public void atLeastOneUnitHidesIconWhenNoUnitIsFilled()
	{
		show(StashIconMode.ANY_UNIT, StashUnitFilter.ALL);
		builtAndFilled(StashUnit.NEAR_A_SHED_IN_LUMBRIDGE_SWAMP, true, false);
		builtAndFilled(StashUnit.GYPSY_TENT_ENTRANCE, true, false);

		assertFalse(goldRingStashed());
	}

	@Test
	public void neitherOptionShowsIconForItemsNoStashUnitTakes()
	{
		for (StashIconMode mode : StashIconMode.values())
		{
			lenient().when(config.stashIconMode()).thenReturn(mode);
			assertFalse(mode.name(), plugin.isStashed(Collections.emptyList()));
		}
	}

	private boolean goldRingStashed()
	{
		return plugin.isStashed(plugin.getStashUnits(ItemID.GOLD_RING));
	}

	private void show(StashIconMode mode, StashUnitFilter filter)
	{
		when(config.stashIconMode()).thenReturn(mode);
		when(config.stashUnitFilter()).thenReturn(filter);
	}

	private void builtAndFilled(StashUnit unit, boolean built, boolean filled)
	{
		// Not every test asks about every unit, e.g. "at least one" stops at the first filled unit
		lenient().when(stashTracker.isBuilt(unit)).thenReturn(built);
		lenient().when(stashTracker.isFilled(unit)).thenReturn(filled);
	}
}
