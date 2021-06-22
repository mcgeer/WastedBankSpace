/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2021, Riley McGee
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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.wastedbankspace.WastedBankSpacePlugin;
import com.wastedbankspace.model.StorableItem;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StorageItemOverlay extends WidgetItemOverlay
{
    private static final ImageComponent ICON = new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/overlaySmoller.png"));
    private final Point point;

    private final Client client;
    private final WastedBankSpacePlugin plugin;
    private final ItemManager itemManager;
    private final TooltipManager tooltipManager;

    @Getter
    private final Cache<Integer, BufferedImage> wastedSpaceImages = CacheBuilder.newBuilder()
            .maximumSize(160)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    @Inject
    StorageItemOverlay(Client client, WastedBankSpacePlugin plugin, ItemManager itemManager, TooltipManager tooltipManager)
    {
        this.client = client;
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.tooltipManager = tooltipManager;
        this.point = new Point();
        showOnBank();
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

        StorableItem item =  items.stream().filter(i -> i.itemID == itemId).findFirst().get();
        Rectangle bounds = itemWidget.getCanvasBounds();

        if (bounds.contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()))
        {
            Tooltip t = new Tooltip(ColorUtil.prependColorTag("Store @ " + item.location.getUiRepresentation(), new Color(238, 238, 238)));
            tooltipManager.add(t);
        }

        renderRibbon(graphics, ICON,bounds.x + bounds.width - 12,bounds.y + bounds.height - 12);
    }

    private void renderRibbon(Graphics2D graphics, ImageComponent ribbon, int x, int y)
    {
        this.point.setLocation(x, y);
        ribbon.setPreferredLocation(this.point);
        ribbon.render(graphics);
    }

    //Fills image with a color
//    private BufferedImage getImage(int id)
//    {
//        BufferedImage image = itemManager.getImage(id);
//        Color color = new Color(255, 251, 0, 62);
//        return ImageUtil.fillImage(image, color);
//    }
}