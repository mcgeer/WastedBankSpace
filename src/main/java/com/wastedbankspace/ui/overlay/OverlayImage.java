package com.wastedbankspace.ui.overlay;

import com.wastedbankspace.WastedBankSpacePlugin;
import lombok.Getter;

import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.util.ImageUtil;

@Getter
public enum OverlayImage {
    DEFAULT("Default", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/000-overlaySmoller.png"))),
    X("X", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/001-close.png"))),
    ARROW("Arrow", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/002-arrow-bottom.png"))),
    PUMPKIN("Spooky", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/003-pumpkin.png"))),
    TRASH_1("Trash 1", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/004-trash.png"))),
    TRASH_2("Trash 2", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/005-trash-bin.png"))),
    MAX("Max", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/006-maximize.png"))),
    W("W",new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/007-letter-w.png"))),
    ONE("Finger", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/008-one.png"))),
    PRETTY_1("Pretty 1", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/009-thai-pattern.png"))),
    PRETTY_2("Pretty 2", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/010-Pretty2.png"))),
    PRETTY_3("Pretty 3", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/011-Pretty3.png"))),
    DOT_BLUE("Blue Dot", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/012-Dot_Blue.png"))),
    DOT_RED("Red Dot", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/013-Dot_Red.png"))),
    DOT_GREEN("Green Dot", new ImageComponent(ImageUtil.loadImageResource(WastedBankSpacePlugin.class, "/014-Dot_green.png")));

    private final String name;
    private final ImageComponent image;

    private OverlayImage(String name, ImageComponent image)
    {
        this.name = name;
        this.image = image;
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
