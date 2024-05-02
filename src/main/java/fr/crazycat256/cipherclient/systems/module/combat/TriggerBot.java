/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.combat;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.friend.Friends;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.utils.CombatUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;

import java.lang.reflect.Method;

public class TriggerBot extends Module {

    @SuppressWarnings("rawtypes")
    private final Method click = ReflectUtils.getMethod(Minecraft.class, "func_147116_af", (Class[]) null);

    public TriggerBot() {
        super("trigger-bot", "Automatically hits the entity you are looking at", Category.COMBAT);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            if (Friends.get().isFriend(mc.renderViewEntity.getCommandSenderName())) return;

            if ((Modules.get().get(Criticals.class)).isActive() && !mc.thePlayer.isInWater()) {
                CombatUtils.performCrit();
            }
            ReflectUtils.call(click, mc);
        }
    }
}
