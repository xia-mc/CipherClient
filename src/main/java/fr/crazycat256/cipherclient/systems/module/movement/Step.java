/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.movement;

import fr.crazycat256.cipherclient.events.custom.EntityMoveEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;

public class Step extends Module {

    public Step() {
        super("step", "Increases your step height", Category.MOVEMENT);
    }

    private final Setting<Double> height = addSetting(new DoubleSetting.Builder()
        .name("height")
        .description("Height of the step")
        .min(0.0)
        .max(10.0)
        .defaultValue(2.0)
        .build()
    );

    @Handler
    private void onEntityMove(EntityMoveEvent event) {
        // Using EntityMoveEvent instead of TickEvent is necessary because some mods update the player's step height every tick
        if (mc.thePlayer == event.entity) {
            mc.thePlayer.stepHeight = height.get().floatValue();
        }
    }


    @Override
    public void onDisable() {
        mc.thePlayer.stepHeight = 0.5f;
    }
}
