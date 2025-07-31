/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.combat;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.PacketEvent;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Module;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.lwjgl.input.Mouse;

public class NoCooldown extends Module {
    private final Setting<Boolean> force = addSetting(new BoolSetting.Builder()
        .name("force")
        .description("Force remove cooldowns")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> speed = addSetting(new IntSetting.Builder()
        .name("speed")
        .description("The speed of cooldowns")
        .defaultValue(3)
        .min(1)
        .max(5)
        .visible(() -> !force.get())
        .build()
    );
    private final Setting<Boolean> noHunger = addSetting(new BoolSetting.Builder()
        .name("no-hunger")
        .description("Force remove hunger")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> onlyWhileAttack = addSetting(new BoolSetting.Builder()
        .name("only-on-attack")
        .description("Only remove cooldowns while attacking")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> onlyWhileEat = addSetting(new BoolSetting.Builder()
        .name("only-on-eat")
        .description("Only remove cooldowns while eating")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> onlyWhileRightClick = addSetting(new BoolSetting.Builder()
        .name("only-on-right-click")
        .description("Only remove cooldowns while right click")
        .defaultValue(true)
        .build()
    );

    private boolean isSprinting;

    public NoCooldown() {
        super("NoCooldown", "Remove cooldown of some items", Category.COMBAT);
    }

    @Handler
    private void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof C0BPacketEntityAction) {
            C0BPacketEntityAction actionPacket = (C0BPacketEntityAction) event.packet;
            int action = actionPacket.func_149512_e();
            if (action == 3) {
                isSprinting = true;
            } else if (action == 4) {
                isSprinting = false;
            }
        }

        if (event.packet instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement) event.packet;
            if (packet.func_149574_g().getItem() instanceof ItemFood) {
                if (!onlyWhileEat.get()) return;
                doRemove();
            }
        }
    }

    @Handler
    public void onTick(TickEvent.ClientTickEvent event) {
        if (onlyWhileRightClick.get() && Mouse.isButtonDown(1)) {
            doRemove();
        }

        if (onlyWhileAttack.get() || onlyWhileEat.get() || onlyWhileRightClick.get()) return;
        doRemove();
    }

    @Handler
    public void onAttack(AttackEntityEvent event) {
        if (!onlyWhileAttack.get()) return;
        doRemove();
    }

    private void doRemove() {
        if (noHunger.get() && isSprinting) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 4));
        }

        for (int i = 0; i < (force.get() ? 20 : speed.get() - 1); i++) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
        }

        if (noHunger.get() && isSprinting) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 3));
        }
    }
}
