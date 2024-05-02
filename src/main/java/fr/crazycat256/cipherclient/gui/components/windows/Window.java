/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.windows;

import fr.crazycat256.cipherclient.gui.components.GuiComponent;
import fr.crazycat256.cipherclient.utils.GLUtils;
import fr.crazycat256.cipherclient.utils.font.Fonts;
import fr.crazycat256.cipherclient.utils.font.GlyphPageFontRenderer;

public abstract class Window extends GuiComponent {

    public final String title;
    private int barHeight;
    protected GlyphPageFontRenderer font;
    private boolean dragging = false;
    private int mouseOffsetX;
    private int mouseOffsetY;

    public Window(int x, int y, String title) {
        super(x, y, 92, 20);
        this.title = title;
        this.barHeight = 20;
        this.font = Fonts.getFont(24);
    }

    public int getBarHeight() {
        return barHeight;
    }
    public void setBarHeight(int barHeight) {
        this.barHeight = barHeight;
        this.font = Fonts.getFont(barHeight);
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        if (dragging) {
            x += mouseX - mouseOffsetX;
            y += mouseY - mouseOffsetY;
        }
        GLUtils.drawRect(0, 0, width, barHeight, guiColor);
        font.drawCenteredStringWithShadow(title, (width - 1) / 2f, (barHeight - font.getFontHeight() - 1) / 2f, textColor);
    }

    @Override
    protected void onMouseMoveOrUp(int mouseX, int mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        if (button == 0 && mouseY < barHeight) {
            mouseOffsetX = mouseX;
            mouseOffsetY = mouseY;
            dragging = true;
        }
    }
}
