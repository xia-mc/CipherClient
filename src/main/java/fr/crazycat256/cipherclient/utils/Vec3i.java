/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import net.minecraft.util.Vec3;

public class Vec3i {

    public static final Vec3i ZERO = new Vec3i(0, 0, 0);

    public final int x;
    public final int y;
    public final int z;

    public Vec3i(double x, double y, double z) {
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);
        this.z = (int) Math.floor(z);
    }

    public Vec3i(Vec3 vec3) {
        this.x = (int) Math.floor(vec3.xCoord);
        this.y = (int) Math.floor(vec3.yCoord);
        this.z = (int) Math.floor(vec3.zCoord);
    }

    public static Vec3i roundVec3(Vec3 vec) {
        return new Vec3i(Math.round((float) vec.xCoord), Math.round((float) vec.yCoord), Math.round((float) vec.zCoord));
    }

    public Vec3i add(Vec3i vec) {
        return new Vec3i(x + vec.x, y + vec.y, z + vec.z);
    }

    public Vec3i add(Vec3 vec) {
        return this.add(new Vec3i(vec));
    }

    public Vec3i add(int x, int y, int z) {
        return new Vec3i(this.x + x, this.y + y, this.z + z);
    }

    public Vec3i subtract(Vec3i vec) {
        return new Vec3i(x - vec.x, y - vec.y, z - vec.z);
    }

    public Vec3i offset(Direction direction) {
        return add(direction.offsetVec);
    }

    public Vec3i subtract(Vec3 vec) {
        return this.subtract(new Vec3i(vec));
    }

    public Vec3i subtract(int x, int y, int z) {
        return new Vec3i(this.x - x, this.y - y, this.z - z);
    }

    public Vec3i multiply(int factor) {
        return new Vec3i(x * factor, y * factor, z * factor);
    }

    public int length() {
        return (int) Math.sqrt(x * x + y * y + z * z);
    }

    public Vec3i copy() {
        return new Vec3i(x, y, z);
    }
}
