package com.wastedbankspace.ui.overlay;

import com.wastedbankspace.WastedBankSpacePlugin;
import lombok.Getter;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

@Getter
public enum OverlayImage {
    DEFAULT("Default", "/000-overlaySmoller.png"),
    X("X", "/001-close.png"),
    ARROW("Arrow", "/002-arrow-bottom.png"),
    PUMPKIN("Spooky", "/003-pumpkin.png"),
    TRASH_1("Trash 1", "/004-trash.png"),
    TRASH_2("Trash 2", "/005-trash-bin.png"),
    MAX("Max", "/006-maximize.png"),
    W("W","/007-letter-w.png"),
    ONE("Finger", "/008-one.png"),
    PRETTY_1("Pretty 1", "/009-thai-pattern.png"),
    PRETTY_2("Pretty 2", "/010-Pretty2.png"),
    PRETTY_3("Pretty 3", "/011-Pretty3.png"),
    DOT_BLUE("Blue Dot", "/012-Dot_Blue.png"),
    DOT_RED("Red Dot", "/013-Dot_Red.png"),
    DOT_GREEN("Green Dot", "/014-Dot_green.png");

    private final String name;
    /** Raw image, used for the bank tag tab icon */
    private final BufferedImage icon;
    private final ImageComponent image;

    OverlayImage(String name, String resource)
    {
        this.name = name;
        this.icon = ImageUtil.loadImageResource(WastedBankSpacePlugin.class, resource);
        this.image = new ImageComponent(icon);
    }

    @Override
    public String toString()
    {
        return getName();
    }

    public ImageComponent getImage()
    {
        return image;
    }
}
