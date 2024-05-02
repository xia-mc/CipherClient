/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.movement;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;

public class Spider extends Module {
    public Spider() {
        super("spider", "Climb walls like a spider", Category.MOVEMENT);
    }

    public final Setting<Double> speed = addSetting(new DoubleSetting.Builder()
        .name("speed")
        .description("The speed of climbing")
        .defaultValue(0.25)
        .min(0.0)
        .max(1.0)
        .build()
    );


    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if(mc.thePlayer.isCollidedHorizontally)
            mc.thePlayer.motionY = speed.get();
    }
}
