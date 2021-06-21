/*
 * Copyright (c) 2019, Ron Young <https://github.com/raiyni>
 * All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.wastedbankspace.ui.overlay;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.wastedbankspace.WastedBankSpacePlugin;
import com.wastedbankspace.model.StorableItem;
import lombok.Getter;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.util.ImageUtil;

public class StorageItemOverlay extends WidgetItemOverlay
{
    private static final BufferedImage ICON = ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/overlaySmoll.png");
    private static final int ALPHA = 135;
    private static final int COLOR_BOUNDARY = 240;

    private final WastedBankSpacePlugin plugin;
    private final ItemManager itemManager;

    @Getter
    private final Cache<Integer, BufferedImage> wastedSpaceImages = CacheBuilder.newBuilder()
            .maximumSize(160)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    @Inject
    StorageItemOverlay(WastedBankSpacePlugin plugin, ItemManager itemManager)
    {
        this.plugin = plugin;
        this.itemManager = itemManager;
        showOnBank();
        super.onMouseOver();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
    {
        List<StorableItem> items =  plugin.getEnabledItemLists();

        if (items.isEmpty()
                || itemWidget.getWidget().getParentId() != WidgetInfo.BANK_ITEM_CONTAINER.getId()
                || items.stream().noneMatch(i -> i.itemID == itemId)
        )
        {
            return;
        }

        Rectangle bounds = itemWidget.getCanvasBounds();
        BufferedImage image = wastedSpaceImages.getIfPresent(itemId);
        if (image == null)
        {
            image = getImage(itemId);
            wastedSpaceImages.put(itemId, image);
        }

        graphics.drawImage(image, bounds.x, bounds.y, null);
    }

    private static Color getColor(float value)
    {
        float h = (1 - value) * COLOR_BOUNDARY / 360;
        Color c = Color.getHSBColor(h, 1, 1f);

        return new Color(c.getRed(), c.getGreen(), c.getBlue(), ALPHA);
    }

    private BufferedImage getImage(int id)
    {
        ItemComposition itemComposition = itemManager.getItemComposition(id);
        //boolean stackable = item.getQuantity() > 1 || itemComposition.isStackable();
        BufferedImage image = itemManager.getImage(id);
        Color color = new Color(255, 183, 0, ALPHA);
        return ImageUtil.fillImage(image, color);
    }
}