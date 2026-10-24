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

package com.wastedbankspace.stash;

import com.wastedbankspace.WastedBankSpaceConfig;
import com.wastedbankspace.model.stash.StashUnit;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.ScriptEvent;
import net.runelite.api.ScriptID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.config.ConfigManager;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link StashTracker} against a fake of the game's STASH check script, which answers (built, filled) for a STASH
 * unit, and of the per-account config the tracker saves to.
 */
public class StashTrackerTest
{
	/** Saved in the user's config, so they must not change between plugin versions */
	private static final String FILLED_KEY = "stashFilledUnits";
	private static final String CHART_STATE_KEY = "stashChartState";

	/** Filled states from a real STASH chart */
	private static final int[] CHART_STATE = {134221829, 0, 4308992};

	private static final StashUnit SHED = StashUnit.NEAR_A_SHED_IN_LUMBRIDGE_SWAMP;
	private static final StashUnit ARIS = StashUnit.GYPSY_TENT_ENTRANCE;
	/** Treated as one of the units kept in the hidden STASH item container */
	private static final StashUnit CONTAINER_UNIT = StashUnit.RIMMINGTON_MINE;

	private final Map<String, String> savedConfig = new HashMap<>();
	/** What the fake STASH check script answers for each unit's object id; units missing aren't built */
	private final Map<Integer, int[]> scriptAnswers = new HashMap<>();
	/** The chart states each run of the STASH check script was given, by object id */
	private final Map<Integer, int[]> scriptChartStates = new HashMap<>();
	private final int[] intStack = new int[2];
	private int scriptRuns;

	private Client client;
	private EnumComposition containerUnits;
	private StashTracker tracker;

	@Before
	public void setUp()
	{
		client = mock(Client.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getIntStack()).thenReturn(intStack);
		doAnswer(invocation ->
		{
			Object[] args = invocation.getArguments();
			assertEquals(ScriptID.WATSON_STASH_UNIT_CHECK, args[0]);
			int objectId = (Integer) args[1];
			scriptChartStates.put(objectId, new int[]{(Integer) args[2], (Integer) args[3], (Integer) args[4]});
			int[] answer = scriptAnswers.getOrDefault(objectId, new int[]{0, 0});
			intStack[0] = answer[0];
			intStack[1] = answer[1];
			scriptRuns++;
			return null;
		}).when(client).runScript(any(Object[].class));

		containerUnits = mock(EnumComposition.class);
		when(containerUnits.getIntValue(anyInt())).thenReturn(-1);
		when(containerUnits.getIntValue(CONTAINER_UNIT.getObjectId())).thenReturn(3);
		when(client.getEnum(StashTracker.CONTAINER_UNITS_ENUM)).thenReturn(containerUnits);

		ConfigManager configManager = mock(ConfigManager.class);
		when(configManager.getRSProfileConfiguration(eq(WastedBankSpaceConfig.GROUP), anyString()))
			.thenAnswer(invocation -> savedConfig.get(invocation.<String>getArgument(1)));
		doAnswer(invocation ->
		{
			savedConfig.put(invocation.getArgument(1), String.valueOf(invocation.<Object>getArgument(2)));
			return null;
		}).when(configManager).setRSProfileConfiguration(eq(WastedBankSpaceConfig.GROUP), anyString(), any());

		tracker = new StashTracker(client, configManager);
	}

	// region loading

	@Test
	public void loadRestoresSavedFilledUnitsAndSkipsUnknownOnes()
	{
		savedConfig.put(FILLED_KEY, SHED.name() + ",A_UNIT_FROM_A_NEWER_VERSION");

		tracker.load();

		assertTrue(tracker.isFilled(SHED));
		assertFalse(tracker.isFilled(ARIS));
		// Built units always come from the game
		assertFalse(tracker.isBuilt(SHED));
	}

	@Test
	public void nothingIsFilledBeforeAnythingIsSaved()
	{
		tracker.load();

		for (StashUnit unit : StashUnit.values())
		{
			assertFalse(unit.name(), tracker.isFilled(unit));
		}
	}

	// endregion

	// region depositing and withdrawing

	@Test
	public void depositIntoClickedUnitFillsItAndSavesIt()
	{
		tracker.load();

		click(SHED);
		message(StashTracker.DEPOSIT_MESSAGE);

		assertTrue(tracker.isFilled(SHED));
		assertEquals(SHED.name(), savedConfig.get(FILLED_KEY));
	}

	@Test
	public void withdrawFromClickedUnitEmptiesIt()
	{
		savedConfig.put(FILLED_KEY, SHED.name() + "," + ARIS.name());
		tracker.load();

		click(ARIS);
		message(StashTracker.WITHDRAW_MESSAGE);

		assertFalse(tracker.isFilled(ARIS));
		assertTrue(tracker.isFilled(SHED));
		assertEquals(SHED.name(), savedConfig.get(FILLED_KEY));
	}

	@Test
	public void messageIsAboutTheUnitClickedLast()
	{
		tracker.load();

		click(SHED);
		click(ARIS);
		message(StashTracker.DEPOSIT_MESSAGE);

		assertTrue(tracker.isFilled(ARIS));
		assertFalse(tracker.isFilled(SHED));
	}

	@Test
	public void messageWithoutAClickedUnitChangesNothing()
	{
		tracker.load();

		message(StashTracker.DEPOSIT_MESSAGE);
		clickObject(MenuAction.GAME_OBJECT_FIRST_OPTION, 12345);
		message(StashTracker.DEPOSIT_MESSAGE);

		assertNull(savedConfig.get(FILLED_KEY));
	}

	@Test
	public void otherMessagesAreIgnoredUntilTheDepositMessage()
	{
		tracker.load();
		click(SHED);

		message(ChatMessageType.SPAM, StashTracker.DEPOSIT_MESSAGE);
		message("You build a STASH unit.");
		assertFalse(tracker.isFilled(SHED));

		message(StashTracker.DEPOSIT_MESSAGE);
		assertTrue(tracker.isFilled(SHED));
	}

	@Test
	public void clickedUnitIsForgottenOnceItsMessageArrives()
	{
		tracker.load();

		click(SHED);
		message(StashTracker.DEPOSIT_MESSAGE);
		// A message without a new click, e.g. from a unit clicked before the plugin knew about it
		message(StashTracker.WITHDRAW_MESSAGE);

		assertTrue(tracker.isFilled(SHED));
	}

	@Test
	public void usingAnItemOnTheUnitCountsAsClickingIt()
	{
		tracker.load();

		clickObject(MenuAction.WIDGET_TARGET_ON_GAME_OBJECT, SHED.getObjectId());
		message(StashTracker.DEPOSIT_MESSAGE);

		assertTrue(tracker.isFilled(SHED));
	}

	// endregion

	// region the STASH chart

	@Test
	public void openingTheChartSavesItsFilledStates()
	{
		tracker.load();

		openChart(CHART_STATE);

		assertEquals("134221829,0,4308992", savedConfig.get(CHART_STATE_KEY));
	}

	@Test
	public void chartStatesAreReadFromTheIntStackWithoutAScriptEvent()
	{
		tracker.load();
		when(client.getIntStackSize()).thenReturn(5);
		when(client.getIntStack()).thenReturn(new int[]{7, 8, 1, 2, 4});

		tracker.onScriptPreFired(new ScriptPreFired(StashTracker.STASH_CHART_BUILD_SCRIPT));

		assertEquals("1,2,4", savedConfig.get(CHART_STATE_KEY));
	}

	@Test
	public void otherScriptsAreIgnored()
	{
		tracker.load();
		ScriptPreFired event = new ScriptPreFired(ScriptID.WATSON_STASH_UNIT_CHECK);
		event.setScriptEvent(scriptEvent(1, 2, 3));

		tracker.onScriptPreFired(event);

		assertNull(savedConfig.get(CHART_STATE_KEY));
	}

	@Test
	public void refreshGivesTheCheckScriptTheSavedChartStates()
	{
		savedConfig.put(CHART_STATE_KEY, "134221829,0,4308992");
		tracker.load();
		answer(SHED, true, true);

		tracker.onGameTick();

		assertArrayEquals(CHART_STATE, scriptChartStates.get(SHED.getObjectId()));
		assertTrue(tracker.isBuilt(SHED));
		assertFalse(tracker.isBuilt(ARIS));
	}

	@Test
	public void freshChartStatesDecideWhichUnitsAreFilled()
	{
		savedConfig.put(FILLED_KEY, SHED.name());
		answer(SHED, true, false);
		answer(ARIS, true, true);
		tracker.load();

		openChart(CHART_STATE);
		tracker.onGameTick();

		assertFalse(tracker.isFilled(SHED));
		assertTrue(tracker.isFilled(ARIS));
		assertEquals(ARIS.name(), savedConfig.get(FILLED_KEY));
	}

	@Test
	public void savedChartStatesAreTheBestGuessUntilFilledUnitsAreSaved()
	{
		answer(SHED, true, true);
		tracker.load();

		tracker.onGameTick();

		assertTrue(tracker.isFilled(SHED));
	}

	@Test
	public void depositsSinceTheChartWinOverItsOldStates()
	{
		// Going by the saved chart states, Aris' tent is filled and the shed unit isn't, but since the chart was last
		// opened the player emptied Aris' tent and filled the shed unit, which the saved filled units say
		savedConfig.put(FILLED_KEY, SHED.name());
		answer(SHED, true, false);
		answer(ARIS, true, true);
		tracker.load();

		tracker.onGameTick();

		assertTrue(tracker.isFilled(SHED));
		assertFalse(tracker.isFilled(ARIS));
	}

	@Test
	public void unitsThatAreNotBuiltAreNeverFilled()
	{
		savedConfig.put(FILLED_KEY, SHED.name());
		tracker.load();

		tracker.onGameTick();

		assertFalse(tracker.isBuilt(SHED));
		assertFalse(tracker.isFilled(SHED));
	}

	// endregion

	// region the hidden STASH container

	@Test
	public void containerUnitsKeepTheirSavedStateUntilTheContainerLoads()
	{
		savedConfig.put(FILLED_KEY, CONTAINER_UNIT.name());
		answer(CONTAINER_UNIT, true, false);
		tracker.load();

		tracker.onGameTick();

		assertTrue(tracker.isFilled(CONTAINER_UNIT));
	}

	@Test
	public void containerUnitsFollowTheCheckScriptOnceTheContainerLoads()
	{
		savedConfig.put(FILLED_KEY, CONTAINER_UNIT.name());
		answer(CONTAINER_UNIT, true, false);
		tracker.load();
		tracker.onGameTick();

		when(client.getItemContainer(InventoryID.HH_INV)).thenReturn(mock(ItemContainer.class));
		tracker.onItemContainerChanged(new ItemContainerChanged(InventoryID.HH_INV, null));
		tracker.onGameTick();

		assertFalse(tracker.isFilled(CONTAINER_UNIT));
	}

	// endregion

	// region when to refresh

	@Test
	public void refreshWaitsUntilLoggedIn()
	{
		when(client.getGameState()).thenReturn(GameState.LOGIN_SCREEN);
		tracker.load();

		assertFalse(tracker.onGameTick());
		assertEquals(0, scriptRuns);

		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		tracker.onGameTick();
		assertEquals(StashUnit.values().length, scriptRuns);
	}

	@Test
	public void refreshOnlyRunsAfterSomethingChanged()
	{
		tracker.load();
		tracker.onGameTick();
		scriptRuns = 0;

		tracker.onGameTick();
		assertEquals("nothing changed", 0, scriptRuns);

		GameStateChanged loggedIn = new GameStateChanged();
		loggedIn.setGameState(GameState.LOGGED_IN);
		tracker.onGameStateChanged(loggedIn);
		tracker.onGameTick();
		assertEquals("logged in", StashUnit.values().length, scriptRuns);

		scriptRuns = 0;
		tracker.onItemContainerChanged(new ItemContainerChanged(InventoryID.HH_INV | 0x8000, null));
		tracker.onGameTick();
		assertEquals("STASH container sent", StashUnit.values().length, scriptRuns);

		scriptRuns = 0;
		tracker.onItemContainerChanged(new ItemContainerChanged(InventoryID.INV, null));
		tracker.onGameTick();
		assertEquals("inventory changed", 0, scriptRuns);
	}

	@Test
	public void buildingAUnitRefreshesAndSaysBuiltUnitsChanged()
	{
		answer(SHED, true, false);
		tracker.load();
		assertTrue("first refresh", tracker.onGameTick());

		answer(ARIS, true, false);
		VarbitChanged built = new VarbitChanged();
		built.setVarpId(VarPlayerID.HH_CONSTRUCTED_BEGINNER);
		tracker.onVarbitChanged(built);

		assertTrue(tracker.onGameTick());
		assertTrue(tracker.isBuilt(ARIS));

		tracker.onVarbitChanged(built);
		assertFalse("same units built", tracker.onGameTick());
	}

	// endregion

	// region helpers

	private void answer(StashUnit unit, boolean built, boolean filled)
	{
		scriptAnswers.put(unit.getObjectId(), new int[]{built ? 1 : 0, filled ? 1 : 0});
	}

	private void click(StashUnit unit)
	{
		clickObject(MenuAction.GAME_OBJECT_FIRST_OPTION, unit.getObjectId());
	}

	private void clickObject(MenuAction action, int objectId)
	{
		MenuEntry entry = mock(MenuEntry.class);
		when(entry.getType()).thenReturn(action);
		when(entry.getIdentifier()).thenReturn(objectId);
		tracker.onMenuOptionClicked(new MenuOptionClicked(entry));
	}

	private void message(String message)
	{
		message(ChatMessageType.GAMEMESSAGE, message);
	}

	private void message(ChatMessageType type, String message)
	{
		tracker.onChatMessage(new ChatMessage(null, type, "", message, null, 0));
	}

	private void openChart(int[] states)
	{
		ScriptPreFired event = new ScriptPreFired(StashTracker.STASH_CHART_BUILD_SCRIPT);
		event.setScriptEvent(scriptEvent(states[0], states[1], states[2]));
		tracker.onScriptPreFired(event);
	}

	/** A script event's arguments start with the script id */
	private static ScriptEvent scriptEvent(int... states)
	{
		ScriptEvent event = mock(ScriptEvent.class);
		List<Object> arguments = new ArrayList<>();
		arguments.add(StashTracker.STASH_CHART_BUILD_SCRIPT);
		for (int state : states)
		{
			arguments.add(state);
		}
		when(event.getArguments()).thenReturn(arguments.toArray());
		return event;
	}

	// endregion
}
