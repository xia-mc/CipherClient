/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.events.custom;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.entity.EntityClientPlayerMP;

/**
 * Fired when the player's position & rotation is sent to the server
 */
public abstract class PlayerUpdateEvent extends Event {

    public final EntityClientPlayerMP player;

    public PlayerUpdateEvent(EntityClientPlayerMP player) {
        this.player = player;
    }

    public static class Pre extends PlayerUpdateEvent {
        public Pre(EntityClientPlayerMP player) {
            super(player);

        }
    }

    public static class Post extends PlayerUpdateEvent {
        public Post(EntityClientPlayerMP player) {
            super(player);
        }
    }


}
