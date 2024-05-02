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
import fr.crazycat256.cipherclient.utils.PlayerUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;

import java.util.*;

public class KillAura extends Module {

    long lastAttack = 0;

    public KillAura() {
        super("kill-aura", "Just killaura", Category.COMBAT);
    }

    private final Setting<Double> reach = addSetting(new DoubleSetting.Builder()
        .name("reach")
        .description("The reach of the killaura")
        .defaultValue(4.5)
        .min(0.0)
        .max(16.0)
        .build()
    );

    private final Setting<Boolean> teleport = addSetting(new BoolSetting.Builder()
        .name("teleport")
        .description("Teleport to the entity if it's too far")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> cps = addSetting(new IntSetting.Builder()
        .name("cps")
        .description("The clicks per second of the killaura")
        .defaultValue(13)
        .min(1)
        .max(20)
        .build()
    );

    private final Setting<PriorityMode> priority = addSetting(new EnumSetting.Builder<PriorityMode>()
        .name("priority")
        .description("The priority of the killaura")
        .defaultValue(PriorityMode.RANGE)
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
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> other = addSetting(new BoolSetting.Builder()
        .name("other")
        .description("Attack other entities")
        .defaultValue(false)
        .build()
    );



    private final List<EntityLivingBase> switchTargets = new ArrayList<>();


    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {

        EntityClientPlayerMP player = mc.thePlayer;

        if(switchTargets.isEmpty()) {
            for (Object o : mc.theWorld.loadedEntityList) {
                if (o instanceof EntityLivingBase) {
                    EntityLivingBase e = (EntityLivingBase) o;

                    if (!CombatUtils.isValid(e)) continue;
                    if (!CombatUtils.isInReach(e, reach.get())) continue;

                    if (!CombatUtils.canHit(e, players.get(), hostile.get(), passive.get(), other.get()))
                        continue;

                    switchTargets.add(e);
                }
            }

            switch(priority.get()) {
                case RANGE:
                    // reach compare
                    switchTargets.sort((e1, e2) -> Float.compare(player.getDistanceToEntity(e1), player.getDistanceToEntity(e2)));
                    break;
                case HEALTH:
                    // health compare
                    switchTargets.sort((e1, e2) -> Float.compare(e1.getHealth(), e2.getHealth()));
                    break;
            }
        }

        if(switchTargets.isEmpty()) return;

        EntityLivingBase target = switchTargets.get(0);
        if(!CombatUtils.isValid(target)) {
            switchTargets.remove(target);
            return;
        }
        if(!CombatUtils.isInReach(target, reach.get())) {
            switchTargets.remove(target);
            return;
        }


        if ((player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemSword && blockHit.get() || player.isBlocking())) {
            mc.playerController.sendUseItem(player, mc.theWorld, player.getHeldItem());
        }

        if(System.currentTimeMillis() - lastAttack < (1000 / cps.get())) return;
        lastAttack = System.currentTimeMillis();

        if (teleport.get() && player.getDistanceToEntity(target) > 5) {
            CombatUtils.teleportHit(target, noSwing.get());
        } else {
            if ((Modules.get().get(Criticals.class)).isActive() && !mc.thePlayer.isInWater()) {
                PlayerUtils.sendPositionPacket(player.getPosition(1).addVector(0, 0.0625, 0), true);
                PlayerUtils.sendPositionPacket(player.getPosition(1), false);
            }
            CombatUtils.attack(target, noSwing.get(), true, 0);
        }

        switchTargets.remove(target);
    }



    public enum PriorityMode {
        RANGE,
        HEALTH
    }
}
