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

import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StorageTooltipTest
{
	private static final Color BACKGROUND = new Color(70, 61, 50, 156);
	private static final Color TEXT = new Color(238, 238, 238);
	private static final int RED = 0xffff0000;

	@Test
	public void boxGrowsWithItsTextAndHasRoomForEveryRow()
	{
		StorageTooltip shortBox = box();
		shortBox.addRow(null, List.of("Store @ Tackle Box"));
		StorageTooltip longBox = box();
		longBox.addRow(null, List.of("Store @ Tackle Box / Cape Rack / Forestry Kit"));
		longBox.addRow(null, List.of("Already stashed @ Draynor Village market (Easy STASH) /",
			"Aris' tent (Beginner STASH)"));

		Dimension small = render(shortBox, new Point(0, 0));
		Dimension large = render(longBox, new Point(0, 0));

		assertTrue(large.width > small.width);
		Graphics2D graphics = canvas().createGraphics();
		int lineHeight = graphics.getFontMetrics().getHeight();
		graphics.dispose();
		assertTrue("three lines of text", large.height >= 3 * lineHeight);
		assertEquals(large, longBox.getBounds().getSize());
	}

	@Test
	public void iconIsDrawnInFrontOfItsRowAtTheBoxsPosition()
	{
		BufferedImage icon = new BufferedImage(StorageTooltip.ICON_SIZE, StorageTooltip.ICON_SIZE,
			BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < icon.getHeight(); y++)
		{
			for (int x = 0; x < icon.getWidth(); x++)
			{
				icon.setRGB(x, y, RED);
			}
		}
		StorageTooltip tooltip = box();
		tooltip.addRow(icon, List.of("Already stored @ Cape Rack"));

		BufferedImage canvas = canvas();
		Graphics2D graphics = canvas.createGraphics();
		tooltip.setPreferredLocation(new Point(20, 30));
		tooltip.render(graphics);
		graphics.dispose();

		// Inside the border, at the left of the first line
		assertEquals(RED, canvas.getRGB(20 + 4 + StorageTooltip.ICON_SIZE / 2, 30 + 4 + StorageTooltip.ICON_SIZE / 2));
		assertEquals(20, tooltip.getBounds().x);
		assertEquals(30, tooltip.getBounds().y);
	}

	@Test
	public void linesComeBackInOrder()
	{
		StorageTooltip tooltip = box();
		tooltip.addRow(null, List.of("a", "b"));
		tooltip.addRow(null, List.of("c"));

		assertEquals(List.of("a", "b", "c"), tooltip.getLines());
	}

	private static StorageTooltip box()
	{
		return new StorageTooltip(BACKGROUND, TEXT);
	}

	private static Dimension render(StorageTooltip tooltip, Point location)
	{
		Graphics2D graphics = canvas().createGraphics();
		tooltip.setPreferredLocation(location);
		Dimension size = tooltip.render(graphics);
		graphics.dispose();
		return size;
	}

	private static BufferedImage canvas()
	{
		return new BufferedImage(600, 200, BufferedImage.TYPE_INT_ARGB);
	}
}
