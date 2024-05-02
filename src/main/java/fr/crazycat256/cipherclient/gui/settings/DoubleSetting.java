/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.settings;

import fr.crazycat256.cipherclient.gui.components.widgets.DoubleEntry;
import fr.crazycat256.cipherclient.gui.components.widgets.DoubleSlider;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

public class DoubleSetting extends Setting<Double> {

    public final double minValue;
    public final double maxValue;

    public DoubleSetting(String title, String description, double defaultValue, double minValue, double maxValue, Callable<Boolean> isVisible, BiConsumer<Double, Double> onChanged) {
        super(title, description, defaultValue, isVisible, onChanged, new DoubleEntry(maxValue), new DoubleSlider(minValue, maxValue, defaultValue));
        this.minValue = minValue;
        this.maxValue = maxValue;
    }


    public static class Builder extends SettingBuilder<Builder, Double, DoubleSetting> {
        private double minValue = 0.0;
        private double maxValue = 10.0;

        public Builder() {
            super(0.0);
        }

        public Builder min(double minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder max(double maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        @Override
        public DoubleSetting build() {
            return new DoubleSetting(name, description, defaultValue, minValue, maxValue, isVisible, onChanged);
        }
    }
}

