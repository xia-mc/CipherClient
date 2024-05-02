/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.movement;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.PacketEvent;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class Velocity extends Module {

    public Velocity() {
        super("velocity", "Reduces your velocity", Category.MOVEMENT);
    }

    private final Setting<Double> horizontalMultiplier = addSetting(new DoubleSetting.Builder()
        .name("horizontal-multiplier")
        .description("Multiplier for horizontal velocity")
        .min(0.0)
        .max(1.0)
        .defaultValue(0.0)
        .build()
    );

    private final Setting<Double> verticalMultiplier = addSetting(new DoubleSetting.Builder()
        .name("vertical-multiplier")
        .description("Multiplier for vertical velocity")
        .min(0.0)
        .max(1.0)
        .defaultValue(0.0)
        .build()
    );

    @Handler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.packet;
            if (mc.theWorld.getEntityByID(packet.func_149412_c()) != mc.thePlayer) return;
            ReflectUtils.set(S12PacketEntityVelocity.class, packet, "field_149415_b", (int)(packet.func_149411_d() * horizontalMultiplier.get()));
            ReflectUtils.set(S12PacketEntityVelocity.class, packet, "field_149416_c", (int)(packet.func_149410_e() * verticalMultiplier.get()));
            ReflectUtils.set(S12PacketEntityVelocity.class, packet, "field_149414_d", (int)(packet.func_149409_f() * horizontalMultiplier.get()));
        }
    }
}
