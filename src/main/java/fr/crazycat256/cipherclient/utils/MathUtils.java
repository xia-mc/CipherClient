/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class MathUtils {

    public static Vec3 vec3FromPolar(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * ((float) (Math.PI / 180.0)) - (float) Math.PI);
        float g = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float h = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
        float i = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
        return Vec3.createVectorHelper(g * h, i, f * h);
    }

    public static Vec3 resize(Vec3 vec, double length) {
        Vec3 newVec = vec.normalize();
        newVec.xCoord *= length;
        newVec.yCoord *= length;
        newVec.zCoord *= length;
        return newVec;
    }

    public static Vec3 multiply(Vec3 vec, double factor) {
        return resize(vec, vec.lengthVector() * factor);
    }

    public static Vec3 negate(Vec3 vec) {
        return Vec3.createVectorHelper(-vec.xCoord, -vec.yCoord, -vec.zCoord);
    }

    public static Vec3 centerOf(Vec3i vec3i) {
        return centerOf(vec3i.x, vec3i.y, vec3i.z);
    }
    public static Vec3 centerOf(int x, int y, int z) {
        return Vec3.createVectorHelper(x + 0.5, y + 0.5, z + 0.5);
    }

    public static int min(int... values) {
        int min = Integer.MAX_VALUE;
        for (int value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public static int max(int... values) {
        int max = Integer.MIN_VALUE;
        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static double min(double... values) {
        double min = Double.MAX_VALUE;
        for (double value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public static double max(double... values) {
        double max = Double.MIN_VALUE;
        for (double value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
