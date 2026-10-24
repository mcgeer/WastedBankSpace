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
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
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
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Tracks which STASH units the player has built and filled, by asking the game's own STASH check script, the one
 * the POH STASH chart and Watson's noticeboard run.
 * <ul>
 * <li>Built units come from varps the client always has.</li>
 * <li>Most units' filled state is in three values the server only sends when the STASH chart or Watson's noticeboard
 * is opened, so the script's answer for them is only fresh then.</li>
 * <li>Some units are tracked in a hidden STASH item container instead, which the script can only read once the
 * game has sent it.</li>
 * <li>In between, depositing into or withdrawing from a STASH unit updates the unit last clicked.</li>
 * </ul>
 * The chart's values and the filled units are saved per account, so they survive restarts.
 */
@Slf4j
@Singleton
public class StashTracker
{
	private static final String FILLED_KEY = "stashFilledUnits";
	private static final String CHART_STATE_KEY = "stashChartState";
	/** The STASH chart's build script, also run by Watson's noticeboard. Its three arguments are the filled states. */
	static final int STASH_CHART_BUILD_SCRIPT = 1475;
	/** Maps the STASH units tracked in the hidden STASH container to their slot in it */
	static final int CONTAINER_UNITS_ENUM = 1525;
	/** Some item containers are also sent with this bit set */
	private static final int CONTAINER_ID_FLAG = 0x8000;
	static final String DEPOSIT_MESSAGE = "You deposit your items into the STASH unit.";
	static final String WITHDRAW_MESSAGE = "You withdraw your items from the STASH unit.";
	private static final Set<Integer> CONSTRUCTED_VARPS = Set.of(
		VarPlayerID.HH_CONSTRUCTED_BEGINNER, VarPlayerID.HH_CONSTRUCTED_EASY, VarPlayerID.HH_CONSTRUCTED_MEDIUM,
		VarPlayerID.HH_CONSTRUCTED_HARD, VarPlayerID.HH_CONSTRUCTED_ELITE, VarPlayerID.HH_CONSTRUCTED_MASTER);
	private static final Set<MenuAction> OBJECT_ACTIONS = EnumSet.of(
		MenuAction.GAME_OBJECT_FIRST_OPTION, MenuAction.GAME_OBJECT_SECOND_OPTION,
		MenuAction.GAME_OBJECT_THIRD_OPTION, MenuAction.GAME_OBJECT_FOURTH_OPTION,
		MenuAction.GAME_OBJECT_FIFTH_OPTION, MenuAction.WIDGET_TARGET_ON_GAME_OBJECT);
	private static final Map<Integer, StashUnit> UNITS_BY_OBJECT = Arrays.stream(StashUnit.values())
		.collect(Collectors.toMap(StashUnit::getObjectId, Function.identity()));

	private final Client client;
	private final ConfigManager configManager;

	// Replaced whole, never modified, as the panel reads them from the Swing thread
	private volatile Set<StashUnit> builtUnits = Collections.emptySet();
	private volatile Set<StashUnit> filledUnits = Collections.emptySet();

	private final int[] chartState = new int[3];
	/** Whether filled units have been saved for this account yet; if not, the saved chart states are the best guess */
	private boolean filledSaved;
	/** Set when the chart sends fresh filled states, so the next refresh takes the script's answer for them */
	private boolean chartOpened;
	private boolean refreshPending;
	/** The STASH unit the player last clicked, which a deposit or withdraw message is about */
	private StashUnit lastClickedUnit;

	@Inject
	StashTracker(Client client, ConfigManager configManager)
	{
		this.client = client;
		this.configManager = configManager;
	}

	/**
	 * Loads the saved STASH state for the current account. Call on startup and when the RuneScape profile changes.
	 */
	public void load()
	{
		List<String> state = Text.fromCSV(nullToEmpty(getConfig(CHART_STATE_KEY)));
		for (int i = 0; i < chartState.length; i++)
		{
			chartState[i] = i < state.size() ? parseInt(state.get(i)) : 0;
		}

		String saved = getConfig(FILLED_KEY);
		filledSaved = saved != null;
		Set<StashUnit> filled = EnumSet.noneOf(StashUnit.class);
		for (String name : Text.fromCSV(nullToEmpty(saved)))
		{
			try
			{
				filled.add(StashUnit.valueOf(name));
			}
			catch (IllegalArgumentException e)
			{
				log.debug("Ignoring unknown saved STASH unit {}", name);
			}
		}
		filledUnits = Collections.unmodifiableSet(filled);
		builtUnits = Collections.emptySet();
		chartOpened = false;
		lastClickedUnit = null;
		refreshPending = true;
	}

	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			refreshPending = true;
		}
	}

	public void onVarbitChanged(VarbitChanged event)
	{
		if (CONSTRUCTED_VARPS.contains(event.getVarpId()))
		{
			refreshPending = true;
		}
	}

	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if ((event.getContainerId() & ~CONTAINER_ID_FLAG) == InventoryID.HH_INV)
		{
			refreshPending = true;
		}
	}

	public void onScriptPreFired(ScriptPreFired event)
	{
		if (event.getScriptId() != STASH_CHART_BUILD_SCRIPT)
		{
			return;
		}

		int[] args = chartArguments(event.getScriptEvent());
		if (args == null)
		{
			log.debug("Couldn't read the STASH chart's arguments");
			return;
		}
		System.arraycopy(args, 0, chartState, 0, chartState.length);
		setConfig(CHART_STATE_KEY, Text.toCSV(List.of(
			String.valueOf(args[0]), String.valueOf(args[1]), String.valueOf(args[2]))));
		log.debug("STASH chart opened, saved its filled states {} {} {}", args[0], args[1], args[2]);
		chartOpened = true;
		refreshPending = true;
	}

	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (OBJECT_ACTIONS.contains(event.getMenuAction()))
		{
			StashUnit unit = UNITS_BY_OBJECT.get(event.getId());
			if (unit != null)
			{
				lastClickedUnit = unit;
			}
		}
	}

	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE || lastClickedUnit == null)
		{
			return;
		}

		String message = Text.removeTags(event.getMessage());
		boolean deposited = message.equals(DEPOSIT_MESSAGE);
		if (!deposited && !message.equals(WITHDRAW_MESSAGE))
		{
			return;
		}

		Set<StashUnit> filled = EnumSet.noneOf(StashUnit.class);
		filled.addAll(filledUnits);
		if (deposited)
		{
			filled.add(lastClickedUnit);
		}
		else
		{
			filled.remove(lastClickedUnit);
		}
		log.debug("{} STASH unit {}", deposited ? "Filled" : "Emptied", lastClickedUnit);
		lastClickedUnit = null;
		setFilled(filled);
	}

	/**
	 * Refreshes the built and filled units if anything changed since the last refresh. Call on the client thread
	 * once a game tick.
	 *
	 * @return whether the set of built units changed
	 */
	public boolean onGameTick()
	{
		if (!refreshPending || client.getGameState() != GameState.LOGGED_IN)
		{
			return false;
		}
		refreshPending = false;
		return refresh();
	}

	private boolean refresh()
	{
		boolean containerLoaded = client.getItemContainer(InventoryID.HH_INV) != null;
		EnumComposition containerUnits = client.getEnum(CONTAINER_UNITS_ENUM);
		// Without saved filled units yet, the saved chart states are the best there is
		boolean chartStatesFresh = chartOpened || !filledSaved;

		Set<StashUnit> built = EnumSet.noneOf(StashUnit.class);
		Set<StashUnit> filled = EnumSet.noneOf(StashUnit.class);
		for (StashUnit unit : StashUnit.values())
		{
			client.runScript(ScriptID.WATSON_STASH_UNIT_CHECK, unit.getObjectId(),
				chartState[0], chartState[1], chartState[2]);
			// The script returns (built, filled)
			int[] results = client.getIntStack();
			if (results[0] != 1)
			{
				continue;
			}
			built.add(unit);

			boolean scriptSaysFilled = results[1] == 1;
			boolean inContainer = containerUnits != null && containerUnits.getIntValue(unit.getObjectId()) != -1;
			boolean isFilled;
			if (inContainer && !containerLoaded)
			{
				// The script can't see the container yet, though a few of these units also use the chart states
				isFilled = filledUnits.contains(unit) || (chartStatesFresh && scriptSaysFilled);
			}
			else if (inContainer || chartStatesFresh)
			{
				isFilled = scriptSaysFilled;
			}
			else
			{
				// The chart states may be out of date, and deposits since then are in filledUnits
				isFilled = filledUnits.contains(unit);
			}
			if (isFilled)
			{
				filled.add(unit);
			}
		}
		chartOpened = false;
		log.debug("STASH units: {} built, {} filled", built.size(), filled.size());

		boolean builtChanged = !built.equals(builtUnits);
		builtUnits = Collections.unmodifiableSet(built);
		setFilled(filled);
		return builtChanged;
	}

	private void setFilled(Set<StashUnit> filled)
	{
		if (!filledSaved || !filled.equals(filledUnits))
		{
			setConfig(FILLED_KEY, Text.toCSV(filled.stream().map(Enum::name).collect(Collectors.toList())));
			filledSaved = true;
		}
		filledUnits = Collections.unmodifiableSet(filled);
	}

	public boolean isBuilt(StashUnit unit)
	{
		return builtUnits.contains(unit);
	}

	public boolean isFilled(StashUnit unit)
	{
		return filledUnits.contains(unit);
	}

	/**
	 * @return the STASH chart's three int arguments, from the script event if there is one, else from the top of
	 * the int stack
	 */
	private int[] chartArguments(ScriptEvent scriptEvent)
	{
		Object[] arguments = scriptEvent == null ? null : scriptEvent.getArguments();
		if (arguments != null && arguments.length >= 4
			&& arguments[1] instanceof Integer && arguments[2] instanceof Integer && arguments[3] instanceof Integer)
		{
			// The first argument is the script id
			return new int[]{(Integer) arguments[1], (Integer) arguments[2], (Integer) arguments[3]};
		}

		int size = client.getIntStackSize();
		if (size < 3)
		{
			return null;
		}
		int[] stack = client.getIntStack();
		return new int[]{stack[size - 3], stack[size - 2], stack[size - 1]};
	}

	private String getConfig(String key)
	{
		return configManager.getRSProfileConfiguration(WastedBankSpaceConfig.GROUP, key);
	}

	private void setConfig(String key, String value)
	{
		configManager.setRSProfileConfiguration(WastedBankSpaceConfig.GROUP, key, value);
	}

	private static String nullToEmpty(String value)
	{
		return value == null ? "" : value;
	}

	private static int parseInt(String value)
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
}
