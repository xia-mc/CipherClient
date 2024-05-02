/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import net.minecraft.util.MathHelper;

public class DoubleSlider extends Slider<Double>{

    public DoubleSlider(double min, double max, double defaultValue) {
        super(min, max, defaultValue);
    }

    @Override
    protected int getProgress() {
        return (int) ((this.width - SLIDER_WIDTH) * (this.getValue() - this.min) / (this.max - this.min));
    }

    @Override
    protected void setProgress(int progress) {
        this.setValue(MathHelper.clamp_double(this.min + (this.max - this.min) * progress / (this.width - SLIDER_WIDTH), min, max));
    }
}
