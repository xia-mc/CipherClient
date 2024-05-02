/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import fr.crazycat256.cipherclient.utils.GLUtils;

import java.util.Arrays;
import java.util.Comparator;

public class EnumChoice<T extends Enum<?>> extends InputWidget<T> {

    private final T[] enumValues;
    public final int maxWidth;

    @SuppressWarnings("unchecked")
    public EnumChoice(T defaultValue) {
        super(defaultValue);
        this.setHeight(10);
        this.enumValues = (T[]) this.value.getClass().getEnumConstants();

        T[] values = this.enumValues.clone();
        Arrays.sort(values, Comparator.comparing(e -> labelFont.getStringWidth(e.name())));
        this.maxWidth = labelFont.getStringWidth(values[values.length - 1].name()) + 2;
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        this.width = labelFont.getStringWidth(this.value.name()) + 4;

        GLUtils.drawBorderedRect(0, 0, this.width, this.height, borderColor, highlightColor);
        labelFont.drawCenteredString(this.value.name(), this.width / 2f - 1, (this.height - labelFont.getFontHeight()) / 2f + 0.5f, textColor);
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        if (button == 0) {
            int ordinal = this.value.ordinal();
            ordinal = (ordinal + 1) % enumValues.length;
            this.value = enumValues[ordinal];
            onChanged(this.value);
        }
    }

    @Override
    public int getMaxWidth() {
        return maxWidth;
    }
}
