/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.utils.EntityFakePlayer;
import fr.crazycat256.cipherclient.systems.module.Category;

public class FreezeCam extends Module {

    public FreezeCam() {
        super("freeze-cam", "Freeze the player camera for do a control of the PJ from another entity. Useful for make videos", Category.RENDER);
        this.autoStartable = false;
    }

    @Override
    public void onEnable() {
        this.doFreezeCam();
    }

    @Override
    public void onDisable() {
        this.undoFreezeCam();
    }

    public void doFreezeCam() {
        if (mc.theWorld != null) {
            EntityFakePlayer spectator = new EntityFakePlayer(mc.theWorld, mc.thePlayer.getGameProfile());
            spectator.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            spectator.inventory.copyInventory(mc.thePlayer.inventory);
            mc.theWorld.addEntityToWorld(-1, spectator);
            mc.renderViewEntity = spectator;
        }
    }

    public void undoFreezeCam() {
        mc.theWorld.removeEntityFromWorld(-1);
        mc.renderViewEntity = mc.thePlayer;
    }
}
