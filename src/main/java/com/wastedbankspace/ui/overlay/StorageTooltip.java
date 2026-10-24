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

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.overlay.components.BackgroundComponent;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;

import javax.annotation.Nullable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The hover box over a flagged item: a row for each place it can go or already is, each with its icon in front, so
 * the item itself only needs the one marker.
 */
class StorageTooltip implements LayoutableRenderableEntity
{
	static final int ICON_SIZE = 13;
	/** Between an icon and its text */
	private static final int ICON_GAP = 4;
	/** Between rows */
	private static final int ROW_GAP = 2;
	private static final int BORDER = ComponentConstants.STANDARD_BORDER;

	private final List<Row> rows = new ArrayList<>();
	private final Color backgroundColor;
	private final Color textColor;

	@Getter
	private final Rectangle bounds = new Rectangle();

	@Setter
	private Point preferredLocation = new Point();

	StorageTooltip(Color backgroundColor, Color textColor)
	{
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
	}

	/**
	 * Adds a row. A long line has already been wrapped into several; they all go beside the one icon.
	 *
	 * @param icon drawn in front of the first line, or null for none, e.g. while it's still loading
	 */
	void addRow(@Nullable BufferedImage icon, List<String> lines)
	{
		rows.add(new Row(icon, List.copyOf(lines)));
	}

	boolean isEmpty()
	{
		return rows.isEmpty();
	}

	/**
	 * @return every line of text, top to bottom
	 */
	List<String> getLines()
	{
		return rows.stream().flatMap(row -> row.lines.stream()).collect(Collectors.toList());
	}

	/**
	 * @return the icon of each row, null where a row has none
	 */
	List<BufferedImage> getIcons()
	{
		return rows.stream().map(row -> row.icon).collect(Collectors.toList());
	}

	@Override
	public void setPreferredSize(Dimension dimension)
	{
		// Sized by its own text
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		FontMetrics metrics = graphics.getFontMetrics();
		int lineHeight = metrics.getHeight();
		// Text always starts after the icon column, so rows without an icon still line up
		int textOffset = ICON_SIZE + ICON_GAP;

		int textWidth = 0;
		int height = 0;
		for (Row row : rows)
		{
			for (String line : row.lines)
			{
				textWidth = Math.max(textWidth, metrics.stringWidth(line));
			}
			height += rowHeight(row, lineHeight) + ROW_GAP;
		}
		height -= ROW_GAP;

		Rectangle box = new Rectangle(preferredLocation.x, preferredLocation.y,
			BORDER + textOffset + textWidth + BORDER, BORDER + height + BORDER);
		BackgroundComponent background = new BackgroundComponent();
		background.setRectangle(box);
		background.setBackgroundColor(backgroundColor);
		background.render(graphics);

		int x = box.x + BORDER;
		int y = box.y + BORDER;
		TextComponent text = new TextComponent();
		text.setColor(textColor);
		for (Row row : rows)
		{
			if (row.icon != null)
			{
				drawIcon(graphics, row.icon, x, y + Math.max(0, (lineHeight - ICON_SIZE) / 2));
			}
			int lineY = y;
			for (String line : row.lines)
			{
				text.setText(line);
				text.setPosition(x + textOffset, lineY + metrics.getAscent());
				text.render(graphics);
				lineY += lineHeight;
			}
			y += rowHeight(row, lineHeight) + ROW_GAP;
		}

		bounds.setBounds(box);
		return box.getSize();
	}

	private static int rowHeight(Row row, int lineHeight)
	{
		return Math.max(ICON_SIZE, row.lines.size() * lineHeight);
	}

	/**
	 * Draws the icon scaled to fit the icon column, keeping its aspect ratio.
	 */
	private static void drawIcon(Graphics2D graphics, BufferedImage icon, int x, int y)
	{
		double scale = (double) ICON_SIZE / Math.max(icon.getWidth(), icon.getHeight());
		int width = (int) Math.round(icon.getWidth() * scale);
		int height = (int) Math.round(icon.getHeight() * scale);
		graphics.drawImage(icon, x + (ICON_SIZE - width) / 2, y + (ICON_SIZE - height) / 2, width, height, null);
	}

	private static final class Row
	{
		private final BufferedImage icon;
		private final List<String> lines;

		private Row(BufferedImage icon, List<String> lines)
		{
			this.icon = icon;
			this.lines = lines;
		}
	}
}
