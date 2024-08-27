/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.injector;

import fr.crazycat256.cipherclient.injector.gui.Gui;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            args = new String[]{"gui"};
        }

        switch (args[0].toLowerCase()) {
            case "gui":
                gui();
                break;
            case "inject":
                inject(args);
                break;
            default:
                help();
                break;
        }
    }


    private static void gui() {
        Gui.start();
    }

    private static void inject(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar " + getJarName() + " inject <PID>");
            return;
        }

        long pid;
        try {
            pid = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid PID");
            return;
        }

        Attacher.attach(pid);
    }

    private static void help() {
        System.out.println("Usage: java -jar " + getJarName() + " <gui|inject|check-update>\n");
        System.out.println("  gui               Open the GUI");
        System.out.println("  inject <PID>      Inject the agent into a target JVM");
        System.out.println("  check-update      Check for updates");
    }


    private static String getJarName() {
        Path jarPath;
        try {
            jarPath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jarPath.getFileName().toString();
    }


}
