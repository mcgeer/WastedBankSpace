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

import com.wastedbankspace.WastedBankSpaceConfig;
import com.wastedbankspace.banktag.WastedBankTag;
import org.junit.Before;
import org.junit.Test;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BankTagTabOverlayTest
{
	private WastedBankTag bankTag;
	private Graphics2D graphics;
	private BankTagTabOverlay overlay;

	@Before
	public void setUp()
	{
		bankTag = mock(WastedBankTag.class);
		WastedBankSpaceConfig config = mock(WastedBankSpaceConfig.class);
		when(config.overlayImage()).thenReturn(OverlayImage.DEFAULT);
		graphics = mock(Graphics2D.class);
		overlay = new BankTagTabOverlay(bankTag, config);
	}

	@Test
	public void drawsIconCentredOnTab()
	{
		when(bankTag.isEnabled()).thenReturn(true);
		when(bankTag.getTabBounds()).thenReturn(new Rectangle(104, 257, 41, 40));

		overlay.render(graphics);

		// A 24x24 icon in the middle of the 41x40 tab
		verify(graphics).drawImage(OverlayImage.DEFAULT.getIcon(), 112, 265, 24, 24, null);
	}

	@Test
	public void drawsNothingWhenTabIsNotShowing()
	{
		when(bankTag.isEnabled()).thenReturn(true);
		when(bankTag.getTabBounds()).thenReturn(null);

		overlay.render(graphics);

		verifyNothingDrawn();
	}

	@Test
	public void drawsNothingWhileTabIsDisabled()
	{
		when(bankTag.getTabBounds()).thenReturn(new Rectangle(104, 257, 41, 40));

		overlay.render(graphics);

		verifyNothingDrawn();
	}

	private void verifyNothingDrawn()
	{
		verify(graphics, never()).drawImage(any(Image.class), anyInt(), anyInt(), anyInt(), anyInt(),
			isNull(ImageObserver.class));
	}
}
