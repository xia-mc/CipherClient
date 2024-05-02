/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.windows;

import fr.crazycat256.cipherclient.gui.settings.KeySetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.keybind.Keybind;
import fr.crazycat256.cipherclient.systems.keybind.Keybinds;
import fr.crazycat256.cipherclient.utils.GLUtils;

import java.util.ArrayList;

import static fr.crazycat256.cipherclient.CipherClient.mc;
import static fr.crazycat256.cipherclient.gui.clickgui.ClientGuiScreen.FACTOR;

public class KeybindsWindow extends Window {

    private final ArrayList<Setting<?>> settings = new ArrayList<>();

    public KeybindsWindow() {
        super(0, 0, "Keybinds");

        for (Keybind keybind : Keybinds.get().getAll()) {
            KeySetting keySetting = new KeySetting.Builder()
                .name(keybind.name)
                .defaultValue(keybind.defaultKey)
                .onChanged((oldKey, newKey) -> keybind.setKey(newKey))
                .build();
            settings.add(keySetting);
        }
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);

        setHeight(getBarHeight() + 16);

        int widgetsX = 0;
        for (Setting<?> setting : settings) {
            setting.setPos(2, this.height);
            setting.resetSize();
            this.height += setting.getHeight();
            widgetsX = Math.max(widgetsX, setting.getWidgetX());
        }

        setWidth(Math.max(this.width, 320));
        setHeight(Math.max(this.height, 180));

        setX(mc.displayWidth / 2 / FACTOR - width / 2);
        setY(mc.displayHeight / 2 / FACTOR - height / 2);

        for (Setting<?> setting : settings) {
            setting.setWidgetX(widgetsX);
            this.width = Math.max(this.width, setting.getWidth() + 4);
        }
        for (Setting<?> setting : settings) {
            setting.setWidth(this.width - 4);
        }
        GLUtils.drawRect(0, getBarHeight(), width, height, backColor);

        for (Setting<?> setting : settings) {
            setting.drawComponent(mouseX, mouseY);
        }
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        for (Setting<?> setting : settings) {
            if (setting.mouseClicked(mouseX, mouseY, button)) {
                return;
            }
        }
    }

    @Override
    protected void onMouseMoveOrUp(int mouseX, int mouseY, int nutton) {
        for (Setting<?> setting : settings) {
            setting.mouseMovedOrUp(mouseX, mouseY, nutton);
        }
    }

    @Override
    public boolean keyTyped(char c, int key) {
        for (Setting<?> setting : settings) {
            if (setting.keyTyped(c, key)) {
                return true;
            }
        }
        return false;
    }


}
