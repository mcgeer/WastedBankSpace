package com.wastedbankspace.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class StorageLocationEnabler {

    public final StorageLocation storageLocation;
    private final Supplier<Boolean> storageLocationEnabled;
    private final List<StorableItem> storableItems;

    public StorageLocationEnabler(StorageLocation storageLocation, Supplier<Boolean> storageLocationEnabled, List<StorableItem> storableItems)
    {
        this.storageLocation = storageLocation;
        this.storageLocationEnabled = storageLocationEnabled;
        this.storableItems = storableItems;
    }

    public List<StorableItem> GetStorableItems()
    {
        return storageLocationEnabled.get() ? storableItems : new ArrayList<>();
    }

}
