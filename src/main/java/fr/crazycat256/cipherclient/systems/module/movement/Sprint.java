/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.movement;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/modulesystem/classes/vanilla/Sprint.java">ehacks-pro</a>
 * @author radioegor146
 */
public class Sprint extends Module {

    public Sprint() {
        super("sprint", "Sprints automatically when you should be walking", Category.MOVEMENT);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer.moveForward > 0.0f) {
            mc.thePlayer.setSprinting(true);
        }
    }
}
