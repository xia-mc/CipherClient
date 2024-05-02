/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.settings;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import fr.crazycat256.cipherclient.CipherClient;
import fr.crazycat256.cipherclient.gui.components.GuiComponent;
import fr.crazycat256.cipherclient.gui.components.widgets.InputWidget;
import fr.crazycat256.cipherclient.gui.components.widgets.InputWidgetContainer;
import fr.crazycat256.cipherclient.gui.components.widgets.Label;
import fr.crazycat256.cipherclient.gui.components.widgets.ResetButton;
import fr.crazycat256.cipherclient.systems.ISerializable;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.utils.MathUtils;
import fr.crazycat256.cipherclient.utils.StringUtils;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public abstract class Setting<T> extends GuiComponent implements ISerializable<JsonElement> {

    protected final String title;
    public Module module = null;
    public final String name;
    public final String description;
    public final T defaultValue;
    public final Callable<Boolean> isVisible;
    public final BiConsumer<T, T> onChanged;

    private T value;

    protected final Label label;
    protected final InputWidgetContainer<T> widgets;
    protected final ResetButton resetButton;

    private static long lastSave = System.currentTimeMillis();

    @SafeVarargs
    protected Setting(String name, String description, T defaultValue, InputWidget<T>... widgets) {
        this(name, description, defaultValue, () -> true, (oldValue, newValue) -> {}, widgets);
    }

    @SafeVarargs
    protected Setting(String name, String description, T defaultValue, Callable<Boolean> isVisible, BiConsumer<T, T> onChanged, InputWidget<T>... widgets) {
        super(0, 0, 0, 0);
        this.name = name;
        this.title = StringUtils.nameToTitle(name);
        this.description = description;
        this.defaultValue = defaultValue;
        this.isVisible = isVisible;
        this.onChanged = onChanged;
        this.widgets = new InputWidgetContainer<>(defaultValue, widgets);
        this.widgets.setOnChanged(this::set);

        this.value = defaultValue;

        setTooltip(this.description);

        this.label = new Label(this.title);
        this.label.setPos(0, 0);
        this.widgets.setPos(this.label.getMaxWidth() + 2, 0);

        this.resetButton = new ResetButton(this::reset);

        this.resetSize();
    }

    public T get() {
        return this.value;
    }

    public void set(T newValue) {
        if (module == null || module.isActive() && mc.theWorld != null && mc.thePlayer != null && CipherClient.isInitialized) {
            this.onChanged.accept(this.value, newValue);
        }
        this.widgets.setValue(newValue);
        this.value = newValue;
        if (System.currentTimeMillis() - lastSave > 1000 && CipherClient.isInitialized) {
            Modules.get().save();
            lastSave = System.currentTimeMillis();
        }
    }

    public void reset() {
        set(defaultValue);
    }

    public int getWidgetX() {
        return label.getWidth() + 2;
    }

    public void setWidgetX(int x) {
        this.widgets.setX(x);
        this.setWidth(x + widgets.getWidth() + 2 + resetButton.getWidth());
    }

    public void resetSize() {
        this.width = label.getMaxWidth() + 2 + widgets.getMaxWidth() + 2 + resetButton.getWidth();
        this.height = MathUtils.max(label.getHeight(), widgets.getHeight(), resetButton.getHeight());
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        label.drawComponent(mouseX, mouseY);
        widgets.drawComponent(mouseX, mouseY);
        this.resetButton.setPos(width - resetButton.getWidth(), 0);
        resetButton.drawComponent(mouseX, mouseY);
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        widgets.mouseClicked(mouseX, mouseY, button);
        resetButton.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void onMouseMoveOrUp(int x, int y, int button) {
        widgets.mouseMovedOrUp(x, y, button);
    }

    @Override
    protected void onKeyTyped(char typedChar, int keyCode) {
        widgets.keyTyped(typedChar, keyCode);
    }

    public boolean visible() {
        try {
            return this.isVisible.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonElement serialize() {
        return new Gson().toJsonTree(this.get());
    }

    @Override
    public void deserialize(JsonElement jsonElement) {
        T value = new Gson().fromJson(jsonElement, new TypeToken<T>() {}.getType());
        this.set(value == null || value.getClass() != this.defaultValue.getClass() ? this.defaultValue : value);
    }

    @SuppressWarnings("unchecked")
    protected abstract static class SettingBuilder<B extends SettingBuilder<B, V, S>, V, S extends Setting<V>> {
        protected String name = "unnamed";
        protected String description = "";
        protected V defaultValue;
        protected Callable<Boolean> isVisible = () -> true;
        protected BiConsumer<V, V> onChanged = (oldValue, newValue) -> {};

        protected SettingBuilder(V defaultValue) {
            this.defaultValue = defaultValue;
        }

        public B name(String name) {
            this.name = name;
            return (B) this;
        }

        public B description(String description) {
            this.description = description;
            return (B) this;
        }

        public B defaultValue(V defaultValue) {
            this.defaultValue = defaultValue;
            return (B) this;
        }

        public B visible(Callable<Boolean> isVisible) {
            this.isVisible = isVisible;
            return (B) this;
        }

        public B onChanged(BiConsumer<V, V> onChanged) {
            this.onChanged = onChanged;
            return (B) this;
        }

        public abstract S build();
    }

}
