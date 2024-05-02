/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import net.minecraft.util.MathHelper;

public class Color {

    public final int red;
    public final int green;
    public final int blue;
    public final int alpha;

    public Color(int red, int green, int blue, int alpha) {
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
        this.alpha = clamp(alpha);

    }

    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public Color(int rgba) {
        this(rgba >> 16 & 255, rgba >> 8 & 255, rgba & 255, rgba >> 24 & 255);
    }

    public int getValue() {
        return (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | (blue & 255);
    }

    public Color withAlpha(int alpha) {
        return new Color(red, green, blue, alpha);
    }

    private static int clamp(int value) {
        return MathHelper.clamp_int(value, 0, 255);
    }
}
