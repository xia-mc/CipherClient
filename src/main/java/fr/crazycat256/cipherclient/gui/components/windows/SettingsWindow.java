/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.windows;

import fr.crazycat256.cipherclient.gui.components.GuiComponent;
import fr.crazycat256.cipherclient.gui.components.widgets.*;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.utils.GLUtils;
import fr.crazycat256.cipherclient.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static fr.crazycat256.cipherclient.CipherClient.mc;
import static fr.crazycat256.cipherclient.gui.clickgui.ClientGuiScreen.FACTOR;

public class SettingsWindow extends Window {

    private final Module module;

    private final Label keybindLabel;
    private final Label enabledLabel;

    private final KeyChoice keybind;
    private final ResetButton resetKeybind;
    private final CheckBox enabled;


    public SettingsWindow(Module module) {
        super(0, 0, module.formattedName);
        this.module = module;

        this.keybindLabel = new Label("Keybind");
        this.enabledLabel = new Label("Enabled");

        this.keybind = new KeyChoice(module.getKeybind());
        this.resetKeybind = new ResetButton(() -> module.setKeybind(0));
        this.enabled = new CheckBox(module.isActive());

        this.keybind.setOnChanged(module::setKeybind);
        this.enabled.setOnChanged(value -> module.toggle());
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);

        keybind.setValue(module.getKeybind());
        enabled.setValue(module.isActive());

        setWidth(Math.max(super.font.getStringWidth(this.module.formattedName), labelFont.getStringWidth(this.module.description)) + 10);
        setHeight(getBarHeight() + 16);

        List<Setting<?>> settings = this.module.settings.stream().filter(Setting::visible).collect(Collectors.toList());

        int widgetsX = 0;
        for (Setting<?> setting : settings) {
            setting.setPos(2, this.height);
            setting.resetSize();
            this.height += setting.getHeight();
            widgetsX = Math.max(widgetsX, setting.getWidgetX());
        }

        for (Widget widget : this.module.widgets) {
            widget.setPos(this.width / 2 - widget.getWidth() / 2, this.height);
            this.width = Math.max(this.width, widget.getWidth() + 4);
            this.height += widget.getHeight();
        }
        this.height += MathUtils.max(keybindLabel.getHeight(), enabledLabel.getHeight(), keybind.getHeight(), enabled.getHeight()) + 10;

        setWidth(Math.max(this.width, 320));
        setHeight(Math.max(this.height, 180));

        for (Setting<?> setting : settings) {
            setting.setWidgetX(widgetsX);
            this.width = Math.max(this.width, setting.getWidth() + 4);
        }
        for (Setting<?> setting : settings) {
            setting.setWidth(this.width - 4);
        }
        for (Widget widget : this.module.widgets) {
            widget.setPos(this.width / 2 - widget.getWidth() / 2, widget.getY());
        }



        keybindLabel.setPos(2, height - 12);
        keybind.setPos(labelFont.getStringWidth(keybindLabel.getText()) + 6, height - 12);
        resetKeybind.setPos(keybind.getX() + keybind.getWidth() + 2, height - 12);
        enabledLabel.setPos(width - labelFont.getStringWidth(enabledLabel.getText()) - 4 - enabled.getWidth() - 2, height - 12);
        enabled.setPos(width - enabled.getWidth() - 2, height - 12);

        setX(mc.displayWidth / 2 / FACTOR - width / 2);
        setY(mc.displayHeight / 2 / FACTOR - height / 2);


        GLUtils.drawRect(0, getBarHeight(), width, height, backColor);
        labelFont.drawCenteredString(this.module.description, width / 2f, getBarHeight() + 2, textColor);

        for (GuiComponent component : getComponents()) {
            component.drawComponent(mouseX, mouseY);
        }
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        for (GuiComponent component : getComponents()) {
            if (component.mouseClicked(mouseX, mouseY, button)) {
                return;
            }
        }
    }

    @Override
    protected void onMouseMoveOrUp(int mouseX, int mouseY, int button) {
        for (GuiComponent component : getComponents()) {
            component.mouseMovedOrUp(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyTyped(char c, int key) {
        for (GuiComponent component : getComponents()) {
            if (component.keyTyped(c, key)) {
                return true;
            }
        }
        return false;
    }

    private List<GuiComponent> getComponents() {
        List<GuiComponent> components = new ArrayList<>();
        components.addAll(this.module.settings.stream().filter(Setting::visible).collect(Collectors.toList()));
        components.addAll(this.module.widgets);
        components.add(keybindLabel);
        components.add(enabledLabel);
        components.add(keybind);
        components.add(resetKeybind);
        components.add(enabled);
        return components;
    }
}
