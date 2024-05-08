/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.entity.Entity;

public class AntiPortal extends Module {

    public AntiPortal() {
        super("anti-portal", "Prevent you from being stuck in portals", Category.PLAYER);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        ReflectUtils.set(Entity.class, mc.thePlayer, "inPortal", false);
    }
}
