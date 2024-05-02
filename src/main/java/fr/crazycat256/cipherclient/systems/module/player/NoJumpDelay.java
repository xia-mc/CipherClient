/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.movement.Flight;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.entity.EntityLivingBase;

public class NoJumpDelay extends Module {
    public NoJumpDelay() {
        super("no-jump-delay", "Suppresses the delay between jumps", Category.PLAYER);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (Modules.get().get(Flight.class).isActive()) return;
        if (mc.thePlayer.noClip) return;
        ReflectUtils.set(EntityLivingBase.class, mc.thePlayer, "jumpTicks", 0);

    }
}
