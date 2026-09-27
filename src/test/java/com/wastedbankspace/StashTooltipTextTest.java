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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;

/**
 * The STASH parts of an item's tooltip. A gold ring goes in three STASH units: near a shed in Lumbridge Swamp and
 * Aris' tent, both built and filled here, and Rimmington mine, which isn't built.
 */
@RunWith(MockitoJUnitRunner.class)
public class StashTooltipTextTest
{
	private static final String SHED = "Near a shed in Lumbridge Swamp (Easy STASH)";
	private static final String ARIS = "Aris' tent (Beginner STASH)";
	private static final String RIMMINGTON = "Rimmington mine (Easy STASH)";

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
		lenient().when(config.stashUnitFilter()).thenReturn(StashUnitFilter.ALL);
		builtAndFilled(StashUnit.NEAR_A_SHED_IN_LUMBRIDGE_SWAMP, true, true);
		builtAndFilled(StashUnit.GYPSY_TENT_ENTRANCE, true, true);
		builtAndFilled(StashUnit.RIMMINGTON_MINE, false, false);
	}

	@Test
	public void storeLineNamesTheUnitsStillEmpty()
	{
		assertEquals(RIMMINGTON, plugin.getStorageLocationText(ItemID.GOLD_RING, false));
	}

	@Test
	public void stashedLineNamesTheFilledUnits()
	{
		assertEquals(List.of("Already stashed @ " + SHED + " / " + ARIS),
			plugin.getStashTooltipLines(ItemID.GOLD_RING, false));
	}

	@Test
	public void noStoreLineOnceEveryUnitIsFilled()
	{
		builtAndFilled(StashUnit.RIMMINGTON_MINE, true, true);

		assertEquals("", plugin.getStorageLocationText(ItemID.GOLD_RING, false));
		assertEquals(List.of("Already stashed @ " + SHED + " / " + RIMMINGTON + " / " + ARIS),
			plugin.getStashTooltipLines(ItemID.GOLD_RING, false));
	}

	@Test
	public void onlyBuiltUnitsCountWhenOnlyBuiltUnitsAreChosen()
	{
		lenient().when(config.stashUnitFilter()).thenReturn(StashUnitFilter.BUILT);

		assertEquals("", plugin.getStorageLocationText(ItemID.GOLD_RING, false));
		assertEquals(List.of("Already stashed @ " + SHED + " / " + ARIS),
			plugin.getStashTooltipLines(ItemID.GOLD_RING, false));
	}

	@Test
	public void itemInTheHouseAlsoGetsAStoreLineForEmptyUnits()
	{
		// The first line names the costume room, so the STASH units it could still go in get their own line
		assertEquals(List.of("Store @ " + RIMMINGTON, "Already stashed @ " + SHED + " / " + ARIS),
			plugin.getStashTooltipLines(ItemID.GOLD_RING, true));
	}

	@Test
	public void noStashLinesWhileStashUnitsAreOff()
	{
		lenient().when(config.stashUnits()).thenReturn(false);

		assertEquals(List.of(), plugin.getStashTooltipLines(ItemID.GOLD_RING, true));
	}

	private void builtAndFilled(StashUnit unit, boolean built, boolean filled)
	{
		lenient().when(stashTracker.isBuilt(unit)).thenReturn(built);
		lenient().when(stashTracker.isFilled(unit)).thenReturn(filled);
	}
}
