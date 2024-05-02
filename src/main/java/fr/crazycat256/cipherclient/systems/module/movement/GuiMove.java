/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.movement;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.command.ConsoleInputGui;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

public class GuiMove extends Module {
    public GuiMove() {
        super("gui-move", "Lets you move in GUIs", Category.MOVEMENT);
    }

    private final Setting<Double> rotationSpeed = addSetting(new DoubleSetting.Builder()
        .name("sotation-speed")
        .description("The speed of rotation in GUIs")
        .min(0.0)
        .max(10.0)
        .defaultValue(1.0)
        .build()
    );

    private boolean screen;
    long t = System.currentTimeMillis();

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        GuiScreen currentScreen = mc.currentScreen;
        if (currentScreen != null && !(currentScreen instanceof GuiChat || currentScreen instanceof ConsoleInputGui || currentScreen instanceof GuiEditSign || currentScreen instanceof GuiCommandBlock)) {
            screen = true;
            update();
        }
        if(currentScreen == null && screen) {
            screen = false;
            update();
        }
    }

    @Handler
    private void onWorldRender(RenderWorldLastEvent event) {
        float factor = (float) ((System.currentTimeMillis() - t) / 10.0 * rotationSpeed.get());
        t = System.currentTimeMillis();

        GuiScreen currentScreen = mc.currentScreen;
        if(currentScreen != null && !(currentScreen instanceof GuiChat || currentScreen instanceof ConsoleInputGui || currentScreen instanceof GuiEditSign || currentScreen instanceof GuiCommandBlock)) {

            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) mc.thePlayer.rotationPitch += factor;
            if (Keyboard.isKeyDown(Keyboard.KEY_UP)) mc.thePlayer.rotationPitch -= factor;
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) mc.thePlayer.rotationYaw += factor;
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) mc.thePlayer.rotationYaw -= factor;

            mc.thePlayer.rotationPitch = MathHelper.clamp_float(mc.thePlayer.rotationPitch, -90.0F, 90.0F);
        }
    }

    private final KeyBinding[] binds = new KeyBinding[] {
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump,
            mc.gameSettings.keyBindSprint
    };

    public void update() {
        for(KeyBinding k : binds) {
            ReflectUtils.setPressed(k, Keyboard.isKeyDown(k.getKeyCode()));
        }
    }
}
