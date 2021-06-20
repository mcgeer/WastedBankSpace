package com.wastedbankspace.ui;

import com.wastedbankspace.WastedBankSpaceConfig;
import com.wastedbankspace.WastedBankSpacePlugin;
import com.wastedbankspace.model.StorableItem;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class WastedBankSpacePanel extends PluginPanel
{

    private JTextArea numberOfItemsText;
    private JList data;

    private int numberOfItems = 0;

    public WastedBankSpacePanel(Client client, WastedBankSpaceConfig config, ItemManager itemManager)
    {
        super();

        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());

        numberOfItemsText = new JTextArea("Please Visit Your Bank");

        data = new JList();
        data.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        data.setSelectedIndex(0);
        data.setVisibleRowCount(10);
        data.setListData(new String[] {"a", "b"});

        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;


        add(numberOfItemsText,c);
        c.gridy++;
        add(data, c);
    }

    public void setWastedBankSpaceItems(List<String> items)
    {
        //Update number of items that can be moved
        numberOfItemsText.setText("Number of Items: " + items.size());
        data.setListData(items.toArray());

        this.updateUI();
    }

}
