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
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

@Slf4j
public class WastedBankSpacePanel extends PluginPanel
{

    private JLabel numberOfItemsText;
    private JList data;

    private int numberOfItems = 0;

    public WastedBankSpacePanel(Client client, WastedBankSpaceConfig config, ItemManager itemManager)
    {
        super();

        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());

        numberOfItemsText = new JLabel("Please Visit Your Bank");

        data = new JList();
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
                "5: Costumes, sets, capes in PoH",
                "6: Alch, trade, sell"
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

    public void setWastedBankSpaceItems(List<String> items)
    {
        //Update number of items that can be moved
        numberOfItemsText.setText("Number of Items: " + items.size());
        data.setListData(items.toArray());

        this.updateUI();
    }

}
