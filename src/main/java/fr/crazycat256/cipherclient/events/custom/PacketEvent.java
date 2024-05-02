/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.events.custom;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.network.Packet;

@Cancelable
public abstract class PacketEvent extends Event {
    public final Packet packet;

    public PacketEvent(Packet packet) {
        this.packet = packet;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public static class Send extends PacketEvent {
        public Send(Packet packet) {
            super(packet);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet packet) {
            super(packet);
        }
    }
}
