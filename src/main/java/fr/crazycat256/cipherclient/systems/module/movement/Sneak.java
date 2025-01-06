/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.movement;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.custom.UpdateMoveStateEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.PacketEvent;
import fr.crazycat256.cipherclient.gui.settings.EnumSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class Sneak extends Module {
    public Sneak() {
        super("sneak", "Sneaks automatically", Category.MOVEMENT);
    }

    private final Setting<Mode> mode = addSetting(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The mode of sneaking")
        .defaultValue(Mode.PACKET)
        .onChanged((oldVal, newVal) -> {
            if (newVal == Mode.PACKET) {
                sendSneakPacket();
            } else {
                ReflectUtils.setPressed(mc.gameSettings.keyBindSneak, false);
            }
        })
        .build()
    );


    @Override
    public void onEnable() {
        sendSneakPacket();
    }

    @Override
    public void onDisable() {
        if (mode.get() == Mode.VANILLA) {
            ReflectUtils.setPressed(mc.gameSettings.keyBindSneak, false);
        } else {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 2));
        }
    }

    @Handler
    private void onMoveState(UpdateMoveStateEvent event) {
        if (mode.get() == Mode.VANILLA) {
            ReflectUtils.setPressed(mc.gameSettings.keyBindSneak, true);
        }
    }

    @Handler
    private void onSendPacket(PacketEvent.Send event) {
        if (mode.get() != Mode.PACKET) return;
        if ((event.packet instanceof C0BPacketEntityAction)) {
            C0BPacketEntityAction actionPacket = (C0BPacketEntityAction) event.packet;
            if (actionPacket.func_149513_d() == 2) { // 2 is the action ID for stop sneaking
                event.setCanceled(true);
            }
        }
    }

    private void sendSneakPacket() {
        if (mode.get() == Mode.PACKET) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 1));
        }
    }


    public enum Mode {
        VANILLA,
        PACKET
    }
}
