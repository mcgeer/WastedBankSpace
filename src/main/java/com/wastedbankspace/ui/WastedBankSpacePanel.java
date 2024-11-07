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

import com.google.common.collect.Sets;
import com.wastedbankspace.WastedBankSpaceConfig;
import com.wastedbankspace.model.StorableItem;
import com.wastedbankspace.model.StorageLocations;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.Text;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class WastedBankSpacePanel extends PluginPanel
{
    private final WastedBankSpaceConfig config;
    private final JTextArea filtersEditor;
    private final JLabel numberOfItemsText;
    private final JList<String> data;
    private List<StorableItem> items;
    private Document filterDoc;
    private Consumer<String> filterUiCallback;

    public WastedBankSpacePanel(Client client, TooltipManager tooltipManager, WastedBankSpaceConfig config,
                                ItemManager itemManager, Consumer<String> filterUi,
                                ScheduledExecutorService scheduledExecutorService)
    {
        super();
        this.config = config;
        this.filterUiCallback = filterUi;

        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        /* FILTERS */
        JLabel filtersLabel = new JLabel("Filters");

        filtersEditor = new JTextArea();
        String filterData = config.nonFlaggedItems();
        filtersEditor.setText(filterData);
        filtersEditor.setRows(6);
        filtersEditor.setLineWrap(true);
        filtersEditor.addFocusListener(new FocusListener()
        {
            private Future<?> updateFilterFuture = CompletableFuture.completedFuture(null);

            @Override
            public void focusGained(FocusEvent e)
            {

            }

            @Override
            public void focusLost(FocusEvent e)
            {
                this.updateFilterFuture.cancel(false);
                this.updateFilterFuture = scheduledExecutorService.schedule(
                        WastedBankSpacePanel.this::updatePluginFilter,
                        250,
                        TimeUnit.MILLISECONDS);
            }

        });
        filtersEditor.setToolTipText("Format as comma separated values " +
                "\n Item ID, <OR> name of item ignore case");
        add(filtersLabel,c);
        c.gridy++;
        add(filtersEditor, c);
        c.gridy+=6;

        /* WASTED SLOTS PRINT */
        JLabel statsLabel = new JLabel("\nWasted Space Stats");
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

        /* TIPS */
        final String tipTab = "    ";
        final String[] tips = new String[]{
                "\nTips",
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


        add(numberOfItemsText,c);
        c.gridy++;
        add(data, c);
        c.gridy++;
        for (String tipString : tips) {
            add(new JLabel(tipString),c);
            c.gridy++;
        }

    }

	// TODO: update data structure
    public void setWastedBankSpaceItems(Set<Integer> items)
    {
		// use StorageLocations.getitemIdMap() to get an Int/StorableItem list and use items set for all keys to get
		// values
		this.items = items.stream()
						  .map(StorageLocations.getItemIdMap()::get)
						  .collect(Collectors.toList());
        //Update number of items that can be moved
        numberOfItemsText.setText("Number of Items Wasting Space: " + items.size());
        data.setListData(new Vector<>(StorageLocations.storableListToString(items)));
        this.updateUI();
    }

    public void updatePluginFilter()
    {
        try
        {
            Document doc = filtersEditor.getDocument();
            String data = doc.getText(0, doc.getLength());
            //Save data to config
            config.nonFlaggedItems(data);

            //Update the UI with changed filters
            filterUiCallback.accept(data);
        }
        catch (BadLocationException ex)
        {
            //Should not get here
        }
    }

    public void removeFilteredItem(String item, int id)
    {
        Document doc = filtersEditor.getDocument();
        try
        {
            String data = doc.getText(0, doc.getLength());
            List<String> filtersReadOnly = Text.fromCSV(data);
            List<String> filters = filtersReadOnly.stream().collect(Collectors.toList());
            List<Integer> pop = new ArrayList<>();

            for(int i = 0; i < filters.size(); i++)
            {
                if(item.equalsIgnoreCase(filters.get(i))
                        || Integer.toString(id).equalsIgnoreCase(filters.get(i)))
                {
                    //Add to start to loop reverse for no issues when popping
                    pop.add(0, i);
                }
            }

            for(Integer i : pop)
            {
                filters.remove((int)i);
            }

            filtersEditor.setText(String.join(",", filters));
            updatePluginFilter();
        }
        catch (BadLocationException ex)
        {
            //Should not get here
        }

    }

    public void addFilteredItem(String item)
    {
        try
        {
            //ID isn't needed as the tool only adds the name for ease of reading by user
            Document doc = filtersEditor.getDocument();
            String data = doc.getText(0, doc.getLength());
            filtersEditor.setText(data + (data.isEmpty() ? "" : ",") + item);
            updatePluginFilter();
        }
        catch (BadLocationException ex)
        {
            //Should not get here
        }
    }
}
