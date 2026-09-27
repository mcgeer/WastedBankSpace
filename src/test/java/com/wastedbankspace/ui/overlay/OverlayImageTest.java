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

import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.util.Text;
import org.junit.Test;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OverlayImageTest
{
	/**
	 * Markers are small corner icons (12 to 16 pixels today). One bigger than a whole item slot (36x32) would cover
	 * the neighbouring items, which usually means a full-size picture was committed by mistake.
	 */
	private static final int MAX_SIZE = 32;

	@Test
	public void everyOverlayImageLoadsItsPicture()
	{
		for (OverlayImage overlay : OverlayImage.values())
		{
			BufferedImage icon = overlay.getIcon();
			assertNotNull(overlay.name() + " picture did not load", icon);
			assertTrue(overlay.name() + " width " + icon.getWidth(), icon.getWidth() > 0 && icon.getWidth() <= MAX_SIZE);
			assertTrue(overlay.name() + " height " + icon.getHeight(), icon.getHeight() > 0 && icon.getHeight() <= MAX_SIZE);
		}
	}

	@Test
	public void everyOverlayImageIsVisibleButLetsTheItemShowThrough()
	{
		for (OverlayImage overlay : OverlayImage.values())
		{
			BufferedImage icon = overlay.getIcon();
			assertTrue(overlay.name() + " has no transparency", icon.getColorModel().hasAlpha());

			int visible = 0;
			int transparent = 0;
			for (int x = 0; x < icon.getWidth(); x++)
			{
				for (int y = 0; y < icon.getHeight(); y++)
				{
					if ((icon.getRGB(x, y) >>> 24) == 0)
					{
						transparent++;
					}
					else
					{
						visible++;
					}
				}
			}
			assertTrue(overlay.name() + " is completely transparent", visible > 0);
			assertTrue(overlay.name() + " is a solid block that hides the item", transparent > 0);
		}
	}

	@Test
	public void everyOverlayImageLooksDifferent()
	{
		// Catches two choices pointing at the same png, e.g. "Red Dot" showing the blue dot
		OverlayImage[] overlays = OverlayImage.values();
		for (int i = 0; i < overlays.length; i++)
		{
			for (int j = i + 1; j < overlays.length; j++)
			{
				assertFalse(overlays[i].name() + " and " + overlays[j].name() + " show the same picture",
					samePixels(overlays[i].getIcon(), overlays[j].getIcon()));
			}
		}
	}

	@Test
	public void itemMarkerDrawsTheSamePictureAsTheBankTagTabIcon()
	{
		for (OverlayImage overlay : OverlayImage.values())
		{
			BufferedImage icon = overlay.getIcon();
			ImageComponent marker = overlay.getImage();

			// The marker is shared, and the item overlay moves it onto every slot it marks, so it may not be at 0,0.
			// Shift the canvas to wherever it draws instead of moving the marker, which would leak into other tests.
			Point location = drawLocation(marker);
			BufferedImage canvas = new BufferedImage(icon.getWidth(), icon.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = canvas.createGraphics();
			graphics.setComposite(AlphaComposite.Src);
			graphics.translate(-location.x, -location.y);
			Dimension drawn = marker.render(graphics);
			graphics.dispose();

			assertEquals(overlay.name(), new Dimension(icon.getWidth(), icon.getHeight()), drawn);
			for (int x = 0; x < icon.getWidth(); x++)
			{
				for (int y = 0; y < icon.getHeight(); y++)
				{
					assertEquals(overlay.name() + " pixel " + x + "," + y, icon.getRGB(x, y), canvas.getRGB(x, y));
				}
			}
		}
	}

	@Test
	public void dropdownShowsEachOverlayImageByItsOwnName()
	{
		Set<String> labels = new HashSet<>();
		for (OverlayImage overlay : OverlayImage.values())
		{
			// RuneLite's config dropdown labels enum values with Text.titleCase
			String label = Text.titleCase(overlay);
			assertEquals(overlay.getName(), label);
			assertEquals(overlay.getName(), overlay.toString());
			assertFalse(overlay.name() + " has a blank name", label.trim().isEmpty());
			assertTrue("two overlay images are both called " + label, labels.add(label));
		}
	}

	@Test
	public void previouslySavedChoicesStillExist()
	{
		// RuneLite saves the chosen constant's name; renaming one resets that user's choice to the default
		List<String> released = Arrays.asList("DEFAULT", "X", "ARROW", "PUMPKIN", "TRASH_1", "TRASH_2", "MAX", "W",
			"ONE", "PRETTY_1", "PRETTY_2", "PRETTY_3", "DOT_BLUE", "DOT_RED", "DOT_GREEN");

		Set<String> current = new HashSet<>();
		for (OverlayImage overlay : OverlayImage.values())
		{
			current.add(overlay.name());
		}
		for (String saved : released)
		{
			assertTrue("saved overlay image " + saved + " no longer exists", current.contains(saved));
		}
	}

	/**
	 * @return where the marker draws, found by drawing it once off-screen, since ImageComponent has no getter for it
	 */
	private static Point drawLocation(ImageComponent marker)
	{
		Graphics2D offScreen = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
		marker.render(offScreen);
		offScreen.dispose();
		return marker.getBounds().getLocation();
	}

	private static boolean samePixels(BufferedImage a, BufferedImage b)
	{
		if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight())
		{
			return false;
		}
		for (int x = 0; x < a.getWidth(); x++)
		{
			for (int y = 0; y < a.getHeight(); y++)
			{
				if (a.getRGB(x, y) != b.getRGB(x, y))
				{
					return false;
				}
			}
		}
		return true;
	}
}
