/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.PacketEvent;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class DeathCoords extends Module {

    private int cooldown = 80;

    public DeathCoords() {
        super("death-coords", "Show coordinates in chat when you die", Category.WORLD);
    }

    private final Setting<Boolean> spawnPacket = addSetting(new BoolSetting.Builder()
        .name("spawn-packet")
        .description("Use spawn packet to detect death")
        .defaultValue(false)
        .build()
    );

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }
        if (mc.thePlayer.isDead) {
            this.cooldown = 100;
            info("Coordinates on death: x:" + (int) mc.thePlayer.posX + " y:" + (int) mc.thePlayer.posY + " z:" + (int) mc.thePlayer.posZ);
        }
    }

    @Handler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (!this.spawnPacket.get()) return;
        if (this.cooldown > 0) return;
        if (event.packet instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook posPacket = (S08PacketPlayerPosLook) event.packet;

            if (posPacket.func_148932_c() == 0.5 && posPacket.func_148933_e() == 0.5) {
                info("Coordinates on death: x:" + (int) mc.thePlayer.posX + " y:" + (int) mc.thePlayer.posY + " z:" + (int) mc.thePlayer.posZ);
                this.cooldown = 100;
            }
        }
    }
}
