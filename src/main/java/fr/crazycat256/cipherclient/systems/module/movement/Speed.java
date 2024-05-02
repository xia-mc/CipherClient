/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.movement;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.render.Freecam;
import fr.crazycat256.cipherclient.utils.PlayerUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

public class Speed extends Module {
    public Speed() {
        super("speed", "Makes you faster", Category.MOVEMENT);
    }

    private final Setting<Double> speed = addSetting(new DoubleSetting.Builder()
        .name("speed")
        .description("Speed of the player")
        .min(0.0)
        .max(10.0)
        .defaultValue(0.3)
        .build()
    );

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (Modules.get().get(Freecam.class).isActive()) return;
        if (Modules.get().get(Flight.class).isActive()) return;

        Vec3 movement = PlayerUtils.getMovementVec3();
        mc.thePlayer.motionX = movement.xCoord * speed.get();
        mc.thePlayer.motionZ = movement.zCoord * speed.get();
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) {
            mc.thePlayer.motionX *= 1.25;
            mc.thePlayer.motionZ *= 1.25;
        }
    }
}
