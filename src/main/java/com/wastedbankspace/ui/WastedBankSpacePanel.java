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

package com.wastedbankspace.ui;

import com.wastedbankspace.WastedBankSpaceConfig;
import com.wastedbankspace.model.StorableItem;
import com.wastedbankspace.model.StorageLocations;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

@Slf4j
public class WastedBankSpacePanel extends PluginPanel
{

    private final JLabel numberOfItemsText;
    private final JList<String> data;
    private List<StorableItem> items;

    public WastedBankSpacePanel(Client client, TooltipManager tooltipManager, WastedBankSpaceConfig config, ItemManager itemManager)
    {
        super();

        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());

        numberOfItemsText = new JLabel("Please Visit Your Bank");

        data = new JList<String>()
        {
            public String getToolTipText(MouseEvent me) {
                int index = locationToIndex(me.getPoint());
                if (index > -1 && items != null) {
                    return items.get(index).getLocation();
                }
                return null;
            }
        };
        data.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        data.setSelectedIndex(0);
        data.setVisibleRowCount(10);

        final String tipTab = "    ";
        final String[] tips = new String[]{
                "Tips",
                "1: Talk to the Wise Old Man",
                tipTab + "to remove Junk Quest Items",
                "2: Murky Matt combines ",
                tipTab + "jewelery charges",
                "3: Make item sets at the ge",
                "3.1: PHat and Mask can be combined",
                "4: Clue items in PoH (future update)",
                "5: Costumes, sets, and capes in PoH",
                "6: Store bolts in a bolt pouch"
        };

        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        add(numberOfItemsText,c);
        c.gridy++;
        add(data, c);
        c.gridy++;
        for (String tipString : tips) {
            add(new JLabel(tipString),c);
            c.gridy++;
        }
    }

    public void setWastedBankSpaceItems(List<StorableItem> items)
    {
        this.items = items;
        //Update number of items that can be moved
        numberOfItemsText.setText("Number of Items Wasting Space: " + items.size());
        data.setListData(new Vector<>(StorageLocations.storableListToString(items)));
        this.updateUI();
    }

}
