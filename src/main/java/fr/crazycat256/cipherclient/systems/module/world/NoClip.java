/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.render.Freecam;
import fr.crazycat256.cipherclient.utils.PlayerUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.util.Vec3;

public class NoClip extends Module {

    public NoClip() {
        super("no-clip", "Allows you to move through blocks", Category.WORLD);
    }

    private final Setting<Double> horizontalSpeed = addSetting(new DoubleSetting.Builder()
        .name("horizontal-speed")
        .description("The speed of horizontal movement")
        .min(0.0)
        .max(10.0)
        .defaultValue(1.0)
        .build()
    );

    private final Setting<Double> verticalSpeed = addSetting(new DoubleSetting.Builder()
        .name("vertical-speed")
        .description("The speed of vertical movement")
        .min(0.0)
        .max(10.0)
        .defaultValue(1.0)
        .build()
    );

    private final Setting<Boolean> onlyWhenInsideBlock = addSetting(new BoolSetting.Builder()
        .name("only-when-inside-block")
        .description("Only enable NoClip when you are inside a block")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> antiVoid = addSetting(new BoolSetting.Builder()
        .name("anti-void")
        .description("Prevents you from falling into the void")
        .defaultValue(true)
        .build()
    );

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (Modules.get().get(Freecam.class).isActive()) return;


        mc.thePlayer.noClip = !onlyWhenInsideBlock.get() || PlayerUtils.isInsideWall();


        if (mc.thePlayer.noClip) {

            Vec3 move = PlayerUtils.getMovementVec3();
            move.xCoord *= horizontalSpeed.get();
            move.yCoord *= verticalSpeed.get();
            move.zCoord *= horizontalSpeed.get();

            if (antiVoid.get()) {
                double stance = mc.thePlayer.posY - mc.thePlayer.boundingBox.minY;
                move.yCoord = Math.max(move.yCoord, stance - mc.thePlayer.boundingBox.minY);
            }

            mc.thePlayer.setVelocity(move.xCoord, move.yCoord, move.zCoord);
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.noClip = false;
    }
}
