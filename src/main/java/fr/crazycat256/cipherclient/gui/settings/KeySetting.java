/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.settings;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import fr.crazycat256.cipherclient.gui.components.widgets.KeyChoice;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

public class KeySetting extends Setting<Integer> {


    protected KeySetting(String name, String description, Integer defaultValue, Callable<Boolean> isVisible, BiConsumer<Integer, Integer> onChanged) {
        super(name, description, defaultValue, isVisible, onChanged, new KeyChoice(defaultValue));
    }


    @Override
    public void deserialize(JsonElement jsonElement) {
        Double value = new Gson().fromJson(jsonElement, new TypeToken<Double>() {}.getType());
        this.set(value == null ? this.defaultValue : value.intValue());
    }


    public static class Builder extends SettingBuilder<KeySetting.Builder, Integer, KeySetting> {
        public Builder() {
            super(0);
        }

        @Override
        public KeySetting build() {
            return new KeySetting(name, description, defaultValue, isVisible, onChanged);
        }
    }
}
