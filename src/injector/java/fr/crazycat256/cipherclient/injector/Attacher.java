/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.injector;

import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.File;

/**
 * This is the main class of the injector. <br>
 * It is responsible for attaching the agent to the target JVM. <br>
 * It will attach itself to the target JVM as a java agent.
 */
public class Attacher {

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println("Usage: java -jar cipher-client-injector.jar <pid>");
                System.exit(1);
            }

            long pid;
            try {
                pid = Long.parseLong(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse PID");
                return;
            }

            String selfJarPath = Attacher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File selfJar = new File(selfJarPath);

            // The JAR attaches itself to the target JVM
            try {
                ByteBuddyAgent.attach(selfJar, String.valueOf(pid));
            } catch (IllegalStateException e) {
                // Empty catch block, this is expected
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
