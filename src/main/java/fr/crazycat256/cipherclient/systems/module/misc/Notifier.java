/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.misc;

import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.PacketEvent;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.EntityFakePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class Notifier extends Module {

    public Notifier() {
        super("Notifier", "Notifies you when a player joins or leaves the server", Category.MISC);
    }

    private final Setting<Boolean> join = addSetting(new BoolSetting.Builder()
        .name("join")
        .description("Notify when a player joins the server")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> leave = addSetting(new BoolSetting.Builder()
        .name("leave")
        .description("Notify when a player leaves the server")
        .defaultValue(true)
        .build()
    );

    @Handler
    private void onEntityJoin(EntityJoinWorldEvent event) {
        if (join.get() && event.entity instanceof EntityPlayer && event.entity != mc.thePlayer && !(event.entity instanceof EntityFakePlayer)) {
            EntityPlayer p = (EntityPlayer) event.entity;
            info(p.getDisplayName() + " entered your visual range (" + (int) mc.thePlayer.getDistanceToEntity(p) + "m)");
        }
    }

    @Handler
    private void onPacketReceived(PacketEvent.Receive event) {
        if (leave.get() && event.packet instanceof S13PacketDestroyEntities) {
            S13PacketDestroyEntities packet = (S13PacketDestroyEntities) event.packet;
            for (int id : packet.func_149098_c()) {
                if (mc.theWorld.getEntityByID(id) instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) mc.theWorld.getEntityByID(id);
                    if (p == mc.thePlayer) continue;
                    info(p.getDisplayName() + " left your visual range");
                }
            }
        }
    }
}
