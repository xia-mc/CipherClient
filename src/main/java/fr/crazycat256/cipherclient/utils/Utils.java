/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import cpw.mods.fml.relauncher.ReflectionHelper;
import fr.crazycat256.cipherclient.CipherClient;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    /**
     * Get the contents of the clipboard as a string
     * @return The text from the clipboard
     */
    @Nonnull
    public static String getClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                // Empty catch block
            }
        }
        return "";
    }

    /**
     * Set the contents of the clipboard to a string
     * @param str The string to set the clipboard to
     */
    public static void setClipboard(String str) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(str), null);
    }

    /**
     * Get the path to a config file
     * @param name The name of the config file
     * @return The path to the config file
     */
    public static String getConfig(String name) {
        Path path = Paths.get(CipherClient.CLIENT_DIR, "config");
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        return path.resolve(name).toString();
    }

    public static boolean isKeyDown(int key) {
        if (key == 0) {
            return false;
        }
        return Keyboard.isKeyDown(key);
    }

    /**
     * Get the Minecraft environment
     * @return MCP or Forge
     */
    public static McEnv getMinecraftEnvironment() {
        try {
            ReflectionHelper.findField(Minecraft.class, "theMinecraft");
            return McEnv.MCP;
        } catch (Exception e) {
            return McEnv.FORGE;
        }
    }

    public enum McEnv {
        FORGE,
        MCP
    }

    /**
     * Log a message to the console
     */
    public static void log(String prefix, String message) {
        message = StringUtils.colorize("&9[&b" + prefix + "&9] &r" + message);
        CipherClient.consoleGui.printChatMessage(new ChatComponentText(message));
    }

}
