/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.Vec3;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/modulesystem/classes/vanilla/Nuker.java">ehacks-pro</a>
 * @author radioegor146
 */
public class Nuker extends Module {

    public Nuker() {
        super("nuker", "Breaks blocks around you", Category.WORLD);
    }

    public final Setting<Double> radius = addSetting(new DoubleSetting.Builder()
        .name("radius")
        .description("Radius of nuker")
        .min(1.0)
        .max(6.0)
        .defaultValue(3.0)
        .build()
    );

    public final Setting<Boolean> onlyHarvestable = addSetting(new BoolSetting.Builder()
        .name("only-harvestable")
        .description("Break only harvestable blocks")
        .defaultValue(false)
        .build()
    );

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        for (int i = 6; i >= -6; --i) {
            for (int k = 6; k >= -6; --k) {
                for (int j = -6; j <= 6; ++j) {

                    int x = (int) (mc.thePlayer.posX + i);
                    int y = (int) (mc.thePlayer.posY + j);
                    int z = (int) (mc.thePlayer.posZ + k);

                    Vec3 center = Vec3.createVectorHelper(x + 0.5, y + 0.5, z + 0.5);
                    Block block = mc.theWorld.getBlock(x, y, z);

                    if (onlyHarvestable.get() && !block.canHarvestBlock(mc.thePlayer, mc.theWorld.getBlockMetadata(x, y, z))) {
                        continue;
                    }
                    if (block.getMaterial() == Material.air || center.distanceTo(mc.thePlayer.getPosition(0)) > radius.get()) {
                        continue;
                    }

                    if (mc.playerController.isInCreativeMode()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, 0));
                    } else {
                        if (block.getMaterial().isLiquid()) {
                            continue;
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, 0));
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, 0));
                    }
                }
            }
        }
    }
}
