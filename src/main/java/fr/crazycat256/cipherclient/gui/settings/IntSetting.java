/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.settings;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import fr.crazycat256.cipherclient.gui.components.widgets.IntEntry;
import fr.crazycat256.cipherclient.gui.components.widgets.IntSlider;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

public class IntSetting extends Setting<Integer> {

    public final int minValue;
    public final int maxValue;

    public IntSetting(String title, String description, int defaultValue, int minValue, int maxValue, Callable<Boolean> isVisible, BiConsumer<Integer, Integer> onChanged) {
        super(title, description, defaultValue, isVisible, onChanged, new IntEntry(defaultValue), new IntSlider(minValue, maxValue, defaultValue));
        this.minValue = minValue;
        this.maxValue = maxValue;
    }


    @Override
    public void deserialize(JsonElement jsonElement) {
        Double value = new Gson().fromJson(jsonElement, new TypeToken<Double>() {}.getType());
        this.set(value == null ? this.defaultValue : value.intValue());
    }


    public static class Builder extends SettingBuilder<Builder, Integer, IntSetting> {
        private int minValue = 0;
        private int maxValue = 10;

        public Builder() {
            super(0);
        }

        public Builder min(int minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder max(int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        @Override
        public IntSetting build() {
            return new IntSetting(name, description, defaultValue, minValue, maxValue, isVisible, onChanged);
        }
    }
}
