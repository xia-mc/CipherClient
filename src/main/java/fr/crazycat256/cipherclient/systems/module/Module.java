/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module;

import com.google.gson.JsonObject;
import fr.crazycat256.cipherclient.gui.components.widgets.Widget;
import fr.crazycat256.cipherclient.systems.ISerializable;
import fr.crazycat256.cipherclient.systems.Element;
import fr.crazycat256.cipherclient.events.EventManager;
import fr.crazycat256.cipherclient.events.custom.ModuleEvent;
import fr.crazycat256.cipherclient.gui.clickgui.SettingsGui;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.utils.StringUtils;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class Module extends Element implements ISerializable<JsonObject>, Comparable<Module> {

    protected final Minecraft mc;

    public final SettingsGui gui;
    public final List<Setting<?>> settings = new ArrayList<>();
    public final List<Widget> widgets = new ArrayList<>();
    public final String name;
    public final String formattedName;
    public final String description;
    public final Category category;
    protected boolean autoStartable = true;
    protected int defaultKeybind = 0;
    protected int keybind = 0;

    protected boolean enabled;



    public Module(String name, String description, Category category) {
        super(StringUtils.nameToTitle(name));
        this.category = category;
        this.name = name;
        this.formattedName = StringUtils.nameToTitle(name);
        this.description = description;
        this.mc = Minecraft.getMinecraft();
        this.gui = new SettingsGui(this);
    }

    public void setKeybind(int key) {
        this.keybind = key;
    }

    @Override
    public JsonObject serialize() {
        JsonObject moduleData = new JsonObject();
        moduleData.addProperty("enabled", this.enabled);
        moduleData.addProperty("keybind", this.keybind);
        JsonObject settings = new JsonObject();
        for (Setting<?> setting : this.settings) {
            settings.add(setting.name, setting.serialize());
        }
        moduleData.add("settings", settings);
        return moduleData;
    }

    @Override
    public void deserialize(JsonObject data) {
        JsonObject moduleData = data.getAsJsonObject();
        this.enabled = moduleData.get("enabled").getAsBoolean() && this.canStartOnLaunch();
        this.keybind = moduleData.get("keybind").getAsInt();
        JsonObject settings = moduleData.getAsJsonObject("settings");
        for (Setting<?> setting : this.settings) {
            setting.deserialize(settings.get(setting.name));
        }
    }

    public String getName() {
        return this.name;
    }

    public int getKeybind() {
        return this.keybind;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isActive() {
        return this.enabled;
    }

    protected <T> Setting<T> addSetting(Setting<T> setting) {
        setting.module = this;
        settings.add(setting);
        return setting;
    }

    protected void addWidget(Widget widget) {
        widgets.add(widget);
    }

    public void onEnable() {}
    public void onDisable() {}


    public void enable() {
        ModuleEvent.Enable event = new ModuleEvent.Enable(this);
        EventManager.postEvent(event);
        if (!event.isCanceled()) {
            this.enabled = true;
            this.onEnable();
            Modules.get().save();
        }
    }

    public void disable() {
        ModuleEvent.Disable event = new ModuleEvent.Disable(this);
        EventManager.postEvent(event);
        if (!event.isCanceled()) {
            this.enabled = false;
            this.onDisable();
            Modules.get().save();
        }
    }

    public void toggle() {
        if (this.isActive()) {
            this.disable();
        } else {
            this.enable();
        }
    }

    public boolean isWorking() {
        return true;
    }

    @Override
    public int compareTo(Module module) {
        return this.name.compareTo(module.name);
    }

    public boolean canStartOnLaunch() {
        return this.isWorking() && this.autoStartable;
    }

    public int getDefaultKeybind() {
        return defaultKeybind;
    }
}
