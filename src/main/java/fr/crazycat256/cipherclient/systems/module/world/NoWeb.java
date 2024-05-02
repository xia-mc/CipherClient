/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.entity.Entity;

public class NoWeb extends Module {

    public NoWeb() {
        super("no-web", "Death to spiders", Category.WORLD);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        ReflectUtils.set(Entity.class, mc.thePlayer,"isInWeb", false);
    }
}
