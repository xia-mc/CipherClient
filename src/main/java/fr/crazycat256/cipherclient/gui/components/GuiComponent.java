/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components;

import fr.crazycat256.cipherclient.gui.Colors;
import fr.crazycat256.cipherclient.gui.clickgui.ClientGuiScreen;
import fr.crazycat256.cipherclient.utils.font.Fonts;
import fr.crazycat256.cipherclient.utils.font.GlyphPageFontRenderer;
import org.lwjgl.opengl.GL11;

import static fr.crazycat256.cipherclient.CipherClient.mc;

/**
 * Base class for all GUI components
 */
public abstract class GuiComponent implements Colors {

    /**
     * The font used to render labels
     */
    protected final GlyphPageFontRenderer labelFont = Fonts.getFont(18);

    /**
     * The X position of the component, relative to the parent
     */
    protected int x;

    /**
     * The Y position of the component, relative to the parent
     */
    protected int y;

    /**
     * The width of the component
     */
    protected int width;

    /**
     * The height of the component
     */
    protected int height;

    /**
     * The tooltip to display when the mouse is hovering over the component
     */
    private String tooltip = null;

    /**
     * The time the mouse last hovered over the component
     */
    private long lastHoverTime = 0;

    public GuiComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Called when the component should be drawn
     */
    public void drawComponent(int mouseX, int mouseY) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        this.draw(mouseX - x, mouseY - y);
        GL11.glPopMatrix();

        if (tooltip != null && !tooltip.isEmpty() && isMouseOver(mouseX - x, mouseY - y)) {
            if (lastHoverTime == -1) {
                lastHoverTime = System.currentTimeMillis();
            }
            if (System.currentTimeMillis() - lastHoverTime > 1000 && mc.currentScreen instanceof ClientGuiScreen) {
                ((ClientGuiScreen) mc.currentScreen).setTooltip(tooltip);
            }
        } else {
            lastHoverTime = -1;
        }
    }

    /**
     * Called when the mouse is clicked
     */
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            this.onMouseClick(mouseX - this.x, mouseY - this.y, button);
            return true;
        }
        return false;
    }

    /**
     * Called when the mouse is moved or released <br>
     * button == -1: mouse moved <br>
     * button == 0: left mouse button released <br>
     * button == 1: right mouse button released
     */
    public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        this.onMouseMoveOrUp(mouseX - this.x, mouseY - this.y, button);
    }

    /**
     * Called when a key is typed
     */
    public boolean keyTyped(char typedChar, int keyCode) {
        onKeyTyped(typedChar, keyCode);
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Set the position of the component relative to the parent
     */
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the size of the component
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Check if the mouse is hovering over the component
     * @param mouseX the X position of the mouse relative to the component
     * @param mouseY the Y position of the mouse relative to the component
     */
    protected boolean isMouseOver(int mouseX, int mouseY) {
        return 0 <= mouseX && 0 <= mouseY && mouseX < width && mouseY < height;
    }


    protected abstract void draw(int mouseX, int mouseY);
    protected void onMouseClick(int mouseX, int mouseY, int button) {}
    protected void onMouseMoveOrUp(int mouseX, int mouseY, int button) {}
    protected void onKeyTyped(char typedChar, int keyCode) {}
}
