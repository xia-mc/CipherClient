/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.PacketEvent;
import fr.crazycat256.cipherclient.utils.EntityFakePlayer;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/modulesystem/classes/vanilla/Blink.java">ehacks-pro</a>
 * @author radioegor146
 */
public class Blink extends Module {

    public EntityFakePlayer freecamEnt = null;

    public Blink() {
        super("blink", "Allows you to move without sending it to the server", Category.PLAYER);
        this.autoStartable = false;
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            this.freecamEnt = new EntityFakePlayer(mc.theWorld, mc.thePlayer.getGameProfile());
            this.freecamEnt.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            this.freecamEnt.inventory = mc.thePlayer.inventory;
            this.freecamEnt.yOffset = mc.thePlayer.yOffset;
            this.freecamEnt.ySize = mc.thePlayer.ySize;
            this.freecamEnt.rotationPitch = mc.thePlayer.rotationPitch;
            this.freecamEnt.rotationYaw = mc.thePlayer.rotationYaw;
            this.freecamEnt.rotationYawHead = mc.thePlayer.rotationYawHead;
            mc.theWorld.spawnEntityInWorld(this.freecamEnt);
        }
    }

    @Override
    public void onDisable() {
        if (this.freecamEnt != null && mc.theWorld != null) {
            mc.theWorld.removeEntity(this.freecamEnt);
            this.freecamEnt = null;
        }
    }

    @Handler
    private void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof C03PacketPlayer) {
            event.setCanceled(true);
        }
    }
}
