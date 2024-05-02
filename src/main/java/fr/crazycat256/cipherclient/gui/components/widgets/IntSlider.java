/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import net.minecraft.util.MathHelper;

public class IntSlider extends Slider<Integer> {


    public IntSlider(int min, int max, int defaultValue) {
        super(min, max, defaultValue);
    }

    protected int getProgress() {
        return (this.width - SLIDER_WIDTH) * (this.getValue() - this.min) / (this.max - this.min);
    }

    protected void setProgress(int progress) {
        this.setValue(MathHelper.clamp_int(this.min + (this.max - this.min) * progress / (this.width - SLIDER_WIDTH), min, max));
    }
}
