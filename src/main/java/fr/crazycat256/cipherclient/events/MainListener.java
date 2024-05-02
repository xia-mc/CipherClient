/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.events;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.CipherClient;
import fr.crazycat256.cipherclient.systems.command.ConsoleInputGui;
import fr.crazycat256.cipherclient.systems.keybind.Keybind;
import fr.crazycat256.cipherclient.systems.keybind.Keybinds;
import fr.crazycat256.cipherclient.systems.keybind.keybinds.OpenConsole;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.utils.GLUtils;
import fr.crazycat256.cipherclient.systems.macro.Macros;
import fr.crazycat256.cipherclient.utils.Utils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public class MainListener {

    private boolean ready = false;

    public boolean prevCState = false;
    private final boolean[] keyStates;
    private final HashSet<Integer> pressedKeys = new HashSet<>();

    public MainListener() {
        this.keyStates = new boolean[256];
    }

    @Handler(priority = Priority.LOWEST, worldRequired = false)
    private void onClientTickFirst(TickEvent.ClientTickEvent event) {

        CipherClient.clickGui.canInputConsole = mc.currentScreen instanceof ConsoleInputGui;
        boolean nowCState = Utils.isKeyDown(Keybinds.get().get(OpenConsole.class).getKey());
        if (!prevCState && nowCState && (mc.currentScreen == null)) {
            mc.displayGuiScreen(new ConsoleInputGui("/"));
        }
        prevCState = nowCState;
        if (mc.thePlayer != null) {
            if (!ready) {
                new PacketHandler();
                ready = true;
            }
        } else {
            ready = false;
        }
    }

    @Handler(priority = Priority.HIGHEST)
    private void onClientTickLast(TickEvent.ClientTickEvent event) {
        for (Module mod : Modules.get().getAll()) {
            if (mc.theWorld == null || !this.checkAndSaveKeyState(mod.getKeybind())) {
                continue;
            }
            if (Utils.isKeyDown(mod.getKeybind())) {
                mod.toggle();
            }
        }
        for (Keybind keybind : Keybinds.get().getAll()) {
            if (mc.theWorld == null || !this.checkAndSaveKeyState(keybind.getKey())) {
                continue;
            }
            if (Utils.isKeyDown(keybind.getKey())) {
                keybind.onPress();
            }
        }
        for (int key : pressedKeys) {
            this.keyStates[key] = !this.keyStates[key];
        }
        pressedKeys.clear();
        if (mc.currentScreen == null) {
            Macros.get().handle();
        }
    }

    @Handler(worldRequired = false)
    private void onGameOverlay(RenderGameOverlayEvent.Text event) {
        GLUtils.hasClearedDepth = false;

        if (mc.currentScreen == null) {
            GL11.glPushMatrix();
            GL11.glScalef(1f, 1f, 1f);
            GL11.glPopMatrix();
        }

        CipherClient.consoleGui.drawChat(mc.ingameGUI.getUpdateCounter());
    }


    public boolean checkAndSaveKeyState(int key) {
        if (mc.currentScreen != null) {
            return false;
        }
        if (Utils.isKeyDown(key) != this.keyStates[key]) {
            pressedKeys.add(key);
            return true;
        }
        return false;
    }

}
