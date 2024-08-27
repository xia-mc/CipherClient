/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import fr.crazycat256.cipherclient.systems.SystemManager;
import fr.crazycat256.cipherclient.systems.command.ConsoleGui;
import fr.crazycat256.cipherclient.gui.clickgui.ClickGui;
import fr.crazycat256.cipherclient.systems.keybind.Keybinds;
import fr.crazycat256.cipherclient.systems.keybind.keybinds.OpenGui;
import fr.crazycat256.cipherclient.transform.TransformManager;
import fr.crazycat256.cipherclient.events.EventManager;
import fr.crazycat256.cipherclient.utils.Utils;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.lang.instrument.Instrumentation;

@Mod(modid = "cipher-client", name = "CipherClient", version = "1.0", acceptableRemoteVersions = "*")
public class CipherClient {

    public static final String NAME = "CipherClient";
    public static final String VERSION = "0.1";
    public static final String CLIENT_DIR = System.getProperty("user.home") + "/.cipherclient";

    public static ClickGui clickGui;
    public static ConsoleGui consoleGui;

    public static CipherClient INSTANCE;
    public static Minecraft mc;
    public static boolean isInjected;
    public static volatile boolean isInitialized;

    public static volatile Instrumentation instrumentation;

    /**
     * Called by FML
     */
    public CipherClient() {
        try {
            ByteBuddyAgent.install();
            instrumentation = ByteBuddyAgent.getInstrumentation();
        } catch (NoClassDefFoundError e) {
            throw new IllegalStateException(
                "ByteBuddyAgent is not installed\n" +
                "If you're trying to use the client as a forge mod (in a prod environment and without injecting) " +
                "download ByteBuddyAgent and add it to your classpath (or put it in your mod folder)\n" +
                "https://search.maven.org/remotecontent?filepath=net/bytebuddy/byte-buddy-agent/1.14.14/byte-buddy-agent-1.14.14.jar"
            );
        } catch (IllegalStateException e) {
            if (e.getMessage().equals("No compatible attachment provider is available")) {
                throw new IllegalStateException(
                    "No compatible attachment provider is available\n" +
                    "If you're trying to use the client as a forge mod (in a prod environment and without injecting) " +
                    "you're probably using a JRE instead of a JDK\n" +
                    "You need to use a JDK to use the client as a mod\n"
                );
            } else {
                throw e;
            }
        }
        isInjected = false;
    }

    /**
     * Called by the injector
     */
    public CipherClient(Instrumentation inst) {
        instrumentation = inst;
        isInjected = true;
        fmlInit(null);
    }

    @Mod.EventHandler
    public void fmlInit(FMLInitializationEvent event) {

        INSTANCE = this;
        mc = Minecraft.getMinecraft();
        isInitialized = false;

        TransformManager.init();
        TransformManager.applyTransformers(instrumentation);
    }


    /**
     * Initialize the client <br>
     * This needs to be called in the main game thread
     */
    public static void init() {

        SystemManager.init();
        EventManager.init();

        clickGui = new ClickGui();
        consoleGui = new ConsoleGui();

        Utils.log(NAME, "Press " + Keyboard.getKeyName(Keybinds.get().get(OpenGui.class).getKey()) + " to open the GUI");

        isInitialized = true;
    }
}
