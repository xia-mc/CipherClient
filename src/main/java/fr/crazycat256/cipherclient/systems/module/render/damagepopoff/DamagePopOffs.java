/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render.damagepopoff;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.events.Handler;
import java.util.HashMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/modulesystem/classes/vanilla/DamagePopOffs.java">ehacks-pro</a>
 */
public class DamagePopOffs extends Module {

    public DamagePopOffs() {
        super("damage-pop-offs", "Shows damage taken by entities", Category.RENDER);
    }

    private final HashMap<Integer, Integer> healths = new HashMap<>();

    @Handler
    private void onLiving(LivingEvent.LivingUpdateEvent event) {
        updateHealth(event.entityLiving);
    }

    private void updateHealth(EntityLivingBase el) {
        int lastHealth;
        int currentHealth = MathHelper.ceiling_float_int(el.getHealth());
        if (healths.containsKey(el.getEntityId()) && (lastHealth = healths.get(el.getEntityId())) != 0 && lastHealth != currentHealth) {
            int damage = lastHealth - currentHealth;
            Particle customParticle = new Particle(mc.theWorld, el.posX, el.posY + el.height, el.posZ, 0.001, 0.05f * 1.5f, 0.001, damage);
            customParticle.shouldOnTop = true;
            if (el != mc.thePlayer || mc.gameSettings.thirdPersonView != 0) {
                mc.effectRenderer.addEffect(customParticle);
            }
        }
        healths.put(el.getEntityId(), currentHealth);
    }
}
