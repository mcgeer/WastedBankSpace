package com.wastedbankspace.model;

import lombok.Getter;

@Getter
public enum StorageLocation {
    POH("PoH"),
    STASH("S.T.A.S.H. Unit"),
    TACKLE_BOX("Tackle Box"),
    STEEL_KEY_RING("Key Ring"),
    TOOL_LEP("Tool Lep"),
    MASTER_SCROLL_BOOK("Master Scroll Book"),
    FOSSIL_STORAGE("Fossil Storage"),
    BOLT_POUCH("Bolt Pouch");

    private String uiRepresentation;

    StorageLocation(String uiRepresentation)
    {
        this.uiRepresentation = uiRepresentation;
    }

    @Override
    public String toString() {
        return this.uiRepresentation;
    }
}
