package com.wastedbankspace.model;

import java.util.Set;

/**
 * A place that items can be stored.
 */
public interface StorageLocation {

    /**
     * Human readable name.
     */
    String getName();

    /**
     * Set of item IDs (from {@link net.runelite.api.ItemID}) that can be stored in this location.
     */
    Set<Integer> getStorableItems();
}
