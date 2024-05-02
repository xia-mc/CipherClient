/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.combat;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.*;
import fr.crazycat256.cipherclient.utils.CombatUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;

import java.util.Iterator;

public class Forcefield extends Module {

    public Forcefield() {
        super("forcefield", "Attacks all entities around you", Category.COMBAT);
    }

    private final Setting<Double> reach = addSetting(new DoubleSetting.Builder()
        .name("reach")
        .description("The reach of the killaura")
        .defaultValue(6.0)
        .min(0.0)
        .max(8.0)
        .build()
    );

    private final Setting<Boolean> blockHit = addSetting(new BoolSetting.Builder()
        .name("block-hit")
        .description("Automatically block when attacking")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> noSwing = addSetting(new BoolSetting.Builder()
        .name("no-swing")
        .description("Disable the swing animation")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> players = addSetting(new BoolSetting.Builder()
        .name("players")
        .description("Attack players")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> hostile = addSetting(new BoolSetting.Builder()
        .name("hostile")
        .description("Attack hostile mobs")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> passive = addSetting(new BoolSetting.Builder()
        .name("passive")
        .description("Attack passive mobs")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> other = addSetting(new BoolSetting.Builder()
        .name("other")
        .description("Attack other entities")
        .defaultValue(true)
        .build()
    );

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        EntityClientPlayerMP player = mc.thePlayer;
        Iterator<?> iter = mc.theWorld.loadedEntityList.iterator();

        if (iter.hasNext() && Modules.get().get(Criticals.class).isActive() && !mc.thePlayer.isInWater()) {
            CombatUtils.performCrit();
        }
        while(iter.hasNext()) {
            Object o = iter.next();

            if(o instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) o;

                if (!CombatUtils.isValid(entity)) continue;
                if (!CombatUtils.isInReach(entity, reach.get())) continue;

                if (!CombatUtils.canHit(entity, players.get(), hostile.get(), passive.get(), other.get())) continue;

                if ((player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemSword && blockHit.get() || player.isBlocking())) {
                    mc.playerController.sendUseItem(player, mc.theWorld, player.getHeldItem());
                }

                if(noSwing.get()) {
                    player.sendQueue.addToSendQueue(new C0APacketAnimation(player, 1));
                } else {
                    player.swingItem();
                }

                player.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            }
        }
    }
}
