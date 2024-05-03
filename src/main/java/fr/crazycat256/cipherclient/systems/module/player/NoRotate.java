/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.PacketEvent;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Module {

    public NoRotate() {
        super("no-rotate", "Prevents the server from rotating you", Category.PLAYER);
    }

    @Handler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.packet;
            ReflectUtils.set(S08PacketPlayerPosLook.class, packet, "field_148936_d", mc.thePlayer.rotationYaw);
            ReflectUtils.set(S08PacketPlayerPosLook.class, packet, "field_148937_e", mc.thePlayer.rotationPitch);
        }

    }
}
