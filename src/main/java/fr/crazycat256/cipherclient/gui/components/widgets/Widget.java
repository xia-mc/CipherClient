/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import fr.crazycat256.cipherclient.gui.components.GuiComponent;

/**
 * Base class for all Widgets
 */
public abstract class Widget extends GuiComponent {


    public Widget() {
        super(0, 0, 0, 0); // In most cases, widget positioning is managed during drawing.
    }

    public int getMaxWidth() {
        return width;
    }

    public int getMaxHeight() {
        return height;
    }

}
