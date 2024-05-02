/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.combat;

import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.MouseOverEvent;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.CombatUtils;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;

public class Reach extends Module {

    public Reach() {
        super("reach", "Modifies your reach distance", Category.COMBAT);
    }

    private final Setting<Double> blockReach = addSetting(new DoubleSetting.Builder()
        .name("block-reach")
        .description("How far you can aim the south")
        .min(3.0)
        .max(6)
        .defaultValue(5.0)
        .build()
    );
    private final Setting<Double> entityReach = addSetting(new DoubleSetting.Builder()
        .name("entity-reach")
        .description("How far you can hit entities")
        .min(3.0)
        .max(16.0)
        .defaultValue(5.0)
        .build()
    );

    private final Setting<Boolean> teleport = addSetting(new BoolSetting.Builder()
        .name("teleport")
        .description("Teleport to the entity if it's too far")
        .defaultValue(false)
        .build()
    );

    @Handler
    private void onMouseOver(MouseOverEvent event) {
        event.blockReach = blockReach.get();
        event.entityReach = entityReach.get();
    }

    @Handler
    private void onMouse(MouseEvent event) {
        EntityClientPlayerMP player = mc.thePlayer;
        if (event.button == 0 && event.buttonstate) {

            if (player.isBlocking()) return;
            if (!(mc.objectMouseOver.entityHit instanceof EntityLivingBase)) return;

            EntityLivingBase entity = (EntityLivingBase) mc.objectMouseOver.entityHit;
            Vec3 playerPos = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
            double distance = playerPos.distanceTo(entity.getPosition(1F));

            if (teleport.get() && distance > 5.75 && distance <= 16) {
                CombatUtils.teleportHit(entity, false);
            }
        }
    }
}
