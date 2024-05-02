/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.macro;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

public class GuiMacroList extends GuiListExtended {

    private final MacroControls parentScreen;
    private final Minecraft mc;
    private final GuiListExtended.IGuiListEntry[] listEntries;
    private int maxListLabelWidth = 0;

    public GuiMacroList(MacroControls parentScreen, Minecraft mc) {
        super(mc, parentScreen.width, parentScreen.height, 63, parentScreen.height - 32, 20);
        this.parentScreen = parentScreen;
        this.mc = mc;
        Macro[] var3 = ArrayUtils.clone(Macros.get().getAll().toArray(new Macro[0]));
        ArrayList<GuiListExtended.IGuiListEntry> arrayListEntries = new ArrayList<>();
        Arrays.sort(var3);

        for (Macro macro : var3) {
            if (mc.fontRenderer.getStringWidth(macro.getKeyDescription()) > this.maxListLabelWidth) {
                this.maxListLabelWidth = mc.fontRenderer.getStringWidth(macro.getKeyDescription());
            }
            arrayListEntries.add(new KeyEntry(macro));
        }

        listEntries = arrayListEntries.toArray(new IGuiListEntry[0]);
    }

    @Override
    protected int getSize() {
        return this.listEntries.length;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    @Override
    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.listEntries[index];
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 15;
    }

    /**
     * Gets the width of the list
     */
    @Override
    public int getListWidth() {
        return super.getListWidth() + 32;
    }

    public class KeyEntry implements GuiListExtended.IGuiListEntry {

        private final Macro entryKeybinding;
        private final String keyDesctiption;
        private final GuiButton btnChangeKeyBinding;
        private final GuiButton btnRemove;

        private KeyEntry(Macro keybinding) {
            this.entryKeybinding = keybinding;
            this.keyDesctiption = keybinding.getKeyDescription();
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 18, keybinding.getKeyDescription());
            this.btnRemove = new GuiButton(0, 0, 0, 50, 18, "Remove");
        }

        @Override
        public void drawEntry(int index, int x, int y, int width, int height, Tessellator tesselator, int buttonX, int buttonY, boolean p_148279_9_) {
            boolean var10 = GuiMacroList.this.parentScreen.currentKeyBinding == this.entryKeybinding;
            GuiMacroList.this.mc.fontRenderer.drawString(this.keyDesctiption, x + 90 - GuiMacroList.this.maxListLabelWidth, y + height / 2 - GuiMacroList.this.mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
            this.btnRemove.xPosition = x + 190;
            this.btnRemove.yPosition = y;
            this.btnRemove.drawButton(GuiMacroList.this.mc, buttonX, buttonY);
            this.btnChangeKeyBinding.xPosition = x + 105;
            this.btnChangeKeyBinding.yPosition = y;
            this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(this.entryKeybinding.getKeyCode());
            if (var10) {
                this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + this.btnChangeKeyBinding.displayString + EnumChatFormatting.WHITE + " <";
            }
            this.btnChangeKeyBinding.drawButton(GuiMacroList.this.mc, buttonX, buttonY);
        }

        @Override
        public boolean mousePressed(int p_148278_1_, int x, int y, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
            if (this.btnChangeKeyBinding.mousePressed(GuiMacroList.this.mc, x, y)) {
                GuiMacroList.this.parentScreen.currentKeyBinding = this.entryKeybinding;
                return true;
            } else if (this.btnRemove.mousePressed(GuiMacroList.this.mc, x, y)) {
                Macros.get().remove(entryKeybinding);
                GuiMacroList.this.mc.displayGuiScreen(new MacroControls(GuiMacroList.this.parentScreen.parentScreen));
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void mouseReleased(int p_148277_1_, int x, int y, int p_148277_4_, int p_148277_5_, int p_148277_6_) {
            this.btnChangeKeyBinding.mouseReleased(x, y);
            this.btnRemove.mouseReleased(x, y);
        }
    }
}
