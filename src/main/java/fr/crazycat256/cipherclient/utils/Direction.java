/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import net.minecraft.util.Vec3;

public enum Direction {

    DOWN("Down", 0, -1, 0, 0),
    UP("Up", 0, 1, 0, 1),
    NORTH("North", 0, 0, -1, 2),
    SOUTH("South", 0, 0, 1, 3),
    WEST("West", -1, 0, 0, 4),
    EAST("East", 1, 0, 0, 5);


    public final String name;
    public final int offsetX;
    public final int offsetY;
    public final int offsetZ;
    public final Vec3 offsetVec;
    public final int side;
    public final int oppositeSide;


    Direction(String name, int offsetX, int offsetY, int offsetZ, int side) {
        this.name = name;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.offsetVec = Vec3.createVectorHelper(offsetX, offsetY, offsetZ);
        this.side = side;
        this.oppositeSide = side ^ 1;
    }
}
