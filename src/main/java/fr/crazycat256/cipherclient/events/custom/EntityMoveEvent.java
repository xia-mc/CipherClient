/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.events.custom;


import net.minecraft.entity.Entity;

/**
 * Fired when {@link Entity#moveEntity(double, double, double)} is called.
 */
public class EntityMoveEvent {

    public final Entity entity;

    public EntityMoveEvent(Entity entity) {
        this.entity = entity;
    }
}
