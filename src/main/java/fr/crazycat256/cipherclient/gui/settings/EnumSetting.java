/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.settings;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import fr.crazycat256.cipherclient.gui.components.widgets.EnumChoice;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {


    public EnumSetting(String title, String description, T defaultValue, Callable<Boolean> isVisible, BiConsumer<T, T> onChanged) {
        super(title, description, defaultValue, isVisible, onChanged, new EnumChoice<>(defaultValue));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deserialize(JsonElement jsonElement) {
        String value = new Gson().fromJson(jsonElement, String.class);
        this.set(value == null ? this.defaultValue : (T) Enum.valueOf(this.defaultValue.getClass(), value));
    }


    public static class Builder<T extends Enum<?>> extends SettingBuilder<Builder<T>, T, EnumSetting<T>> {

        public Builder() {
            super(null);
        }

        @Override
        public EnumSetting<T> build() {
            return new EnumSetting<>(name, description, defaultValue, isVisible, onChanged);
        }
    }

}
