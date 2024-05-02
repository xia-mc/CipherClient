/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.settings;

import fr.crazycat256.cipherclient.gui.components.widgets.CheckBox;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

public class BoolSetting extends Setting<Boolean> {
    public BoolSetting(String title, String description, boolean defaultValue, Callable<Boolean> isVisible, BiConsumer<Boolean, Boolean> onChanged) {
        super(title, description, defaultValue, isVisible, onChanged, new CheckBox(defaultValue));
    }


    public static class Builder extends SettingBuilder<Builder, Boolean, BoolSetting> {
        public Builder() {
            super(false);
        }

        @Override
        public BoolSetting build() {
            return new BoolSetting(name, description, defaultValue, isVisible, onChanged);
        }
    }

}
