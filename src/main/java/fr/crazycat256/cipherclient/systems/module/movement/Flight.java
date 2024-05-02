/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.movement;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.EnumSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.render.Freecam;
import fr.crazycat256.cipherclient.utils.PlayerUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

public class Flight extends Module {

    public Flight() {
        super("flight", "Allows you to fly", Category.MOVEMENT);
    }

    private final Setting<Mode> mode = addSetting(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The mode of flight")
        .defaultValue(Mode.VELOCITY)
        .onChanged((oldValue, newValue) -> {
            if (newValue == Mode.VELOCITY) {
                mc.thePlayer.capabilities.isFlying = false;
            }
            if (newValue != Mode.ABILITIES) {
                mc.thePlayer.capabilities.setFlySpeed(0.05F);
            }
            if (newValue != Mode.CREATIVE){
                mc.thePlayer.capabilities.allowFlying = false;
            }
        })
        .build()
    );

    private final Setting<Double> horizontalSpeed = addSetting(new DoubleSetting.Builder()
        .name("horizontal-speed")
        .description("The speed of horizontal movement")
        .min(0.0)
        .max(10.0)
        .defaultValue(1.0)
        .visible(() -> mode.get() != Mode.CREATIVE)
        .build()
    );

    private final Setting<Double> verticalSpeed = addSetting(new DoubleSetting.Builder()
        .name("vertical-speed")
        .description("The speed of vertical movement")
        .min(0.0)
        .max(10.0)
        .defaultValue(1.0)
        .visible(() -> mode.get() != Mode.CREATIVE)
        .build()
    );

    private final Setting<Boolean> stopMotion = addSetting(new BoolSetting.Builder()
        .name("stop-motion")
        .description("Stops all motion when toggled off")
        .defaultValue(true)
        .build()
    );


    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {

        switch (mode.get()) {
            case CREATIVE:
                mc.thePlayer.capabilities.allowFlying = true;
                break;
            case ABILITIES:
                mc.thePlayer.capabilities.isFlying = true;
                mc.thePlayer.capabilities.setFlySpeed(horizontalSpeed.get().floatValue() / 10);
                if (mc.gameSettings.keyBindJump.getIsKeyPressed()) {
                    mc.thePlayer.motionY += verticalSpeed.get() / 10;
                }
                if (mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
                    mc.thePlayer.motionY -= verticalSpeed.get() / 10;
                }
                break;
            case VELOCITY: {
                if (Modules.get().get(Freecam.class).isActive()) return;
                Vec3 move = PlayerUtils.getMovementVec3();

                if (mc.currentScreen == null && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                    move.yCoord -= 1;
                }

                mc.thePlayer.motionX = move.xCoord * horizontalSpeed.get();
                mc.thePlayer.motionY = move.yCoord * verticalSpeed.get();
                mc.thePlayer.motionZ = move.zCoord * horizontalSpeed.get();

                if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) {
                    mc.thePlayer.motionX *= 1.5;
                    mc.thePlayer.motionZ *= 1.5;
                }
                break;
            }
        }
    }

    @Handler
    private void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mode.get() == Mode.VELOCITY) {
            ReflectUtils.set(KeyBinding.class, mc.gameSettings.keyBindSneak, "pressed", false);
            ReflectUtils.set(EntityLivingBase.class, mc.thePlayer, "jumpTicks", 2);
        }
    }


    @Override
    public void onDisable() {
        switch (mode.get()) {
            case VELOCITY:
                if (stopMotion.get()) {
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.motionZ = 0;
                }
                break;
            case ABILITIES:
                mc.thePlayer.capabilities.isFlying = false;
                break;
            case CREATIVE:
                mc.thePlayer.capabilities.allowFlying = false;
                break;
        }
    }

    public enum Mode {
        CREATIVE,
        ABILITIES,
        VELOCITY
    }
}
