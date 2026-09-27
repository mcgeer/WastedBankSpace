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

import net.runelite.api.gameval.SpriteID;
import net.runelite.client.ui.components.TitleCaseListCellRenderer;
import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * The "House Icon" option is an enum dropdown. RuneLite's config panel renders each entry with
 * {@link TitleCaseListCellRenderer}, which honours {@code toString()} only when it differs from
 * {@code name()}; otherwise it title-cases the constant name. POH's display name used to be "POH",
 * equal to its constant name, so the dropdown showed "Poh".
 */
public class HouseIconDropdownTest
{
	/**
	 * The constant names RuneLite has persisted for this option: the config panel stores
	 * {@code Enum.name()} and reads it back with {@code Enum.valueOf}, never the display name.
	 */
	private static final List<String> SAVED_CONSTANT_NAMES = Arrays.asList(
		"HOUSE_OPTIONS", "POH", "SIDE_ICON", "TELEPORT_SPELL", "MAP_PORTAL");

	@Test
	public void dropdownRendererLabelsEveryHouseIconByItsDisplayName() throws Exception
	{
		Map<HouseIcon, String> labels = renderDropdownLabels();
		for (HouseIcon icon : HouseIcon.values())
		{
			assertEquals("dropdown label for " + icon.name(), icon.getName(), labels.get(icon));
		}
	}

	@Test
	public void pohKeepsItsAcronymInTheDropdown() throws Exception
	{
		String label = renderDropdownLabels().get(HouseIcon.POH);
		assertNotEquals("POH was title-cased from its constant name", "Poh", label);
		assertTrue("POH's dropdown label \"" + label + "\" should keep the POH acronym", label.contains("POH"));
	}

	@Test
	public void everyHouseIconDisplayNameDiffersFromItsConstantName()
	{
		for (HouseIcon icon : HouseIcon.values())
		{
			// Text.titleCase falls back to title-casing name() whenever toString() equals it
			assertNotEquals(icon.name() + "'s display name equals its constant name, so RuneLite would"
				+ " title-case it in the dropdown", icon.name(), icon.toString());
		}
	}

	@Test
	public void savedHouseIconSettingsStillResolveAfterTheRename()
	{
		for (String saved : SAVED_CONSTANT_NAMES)
		{
			assertEquals(saved, resolveSaved(saved).name());
		}
		// A user who picked "POH" before the rename still gets the POH sprite, not the default icon
		assertEquals(SpriteID.OPTIONS_POH_ICON, resolveSaved("POH").getSpriteId());
	}

	/**
	 * Reads a saved value the way ConfigManager.stringToObject does for enum config items.
	 */
	private static HouseIcon resolveSaved(String saved)
	{
		try
		{
			return Enum.valueOf(HouseIcon.class, saved);
		}
		catch (IllegalArgumentException e)
		{
			throw new AssertionError("saved House Icon setting \"" + saved + "\" no longer resolves; RuneLite would"
				+ " log a warning and fall back to the default icon", e);
		}
	}

	/**
	 * Renders every icon the way RuneLite's config panel does: a {@link TitleCaseListCellRenderer}
	 * on a list of the enum's constants, on the Swing thread.
	 */
	private static Map<HouseIcon, String> renderDropdownLabels() throws Exception
	{
		Map<HouseIcon, String> labels = new LinkedHashMap<>();
		SwingUtilities.invokeAndWait(() ->
		{
			JList<HouseIcon> list = new JList<>(HouseIcon.values());
			TitleCaseListCellRenderer renderer = new TitleCaseListCellRenderer();
			HouseIcon[] icons = HouseIcon.values();
			for (int i = 0; i < icons.length; i++)
			{
				Component cell = renderer.getListCellRendererComponent(list, icons[i], i, false, false);
				labels.put(icons[i], ((JLabel) cell).getText());
			}
		});
		return labels;
	}
}
