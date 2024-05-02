/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.injector;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.ProtectionDomain;

/**
 * This is the main class of the agent. <br>
 * It should be attached to the target JVM by the {@link Agent} class.
 */
public class Agent {

    public static void agentmain(String agentOps, Instrumentation inst) throws Exception {

        ByteArrayOutputStream buffer;
        try (InputStream is = Agent.class.getClassLoader().getResourceAsStream("cipher-client.bin")) {

            buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
        }
        buffer.flush();
        byte[] jarData = buffer.toByteArray();

        byte[][] classesData = deserialize(jarData);

        ClassLoader cl = null;
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread == null) continue;
            ClassLoader threadLoader = thread.getContextClassLoader();
            if (threadLoader == null) continue;

            if (threadLoader.getClass().getName().contains("LaunchClassLoader")) {
                cl = threadLoader;
                break;
            }
        }

        if (cl == null) {
            throw new IllegalStateException("Could not find LaunchClassLoader");
        }
        Thread.currentThread().setContextClassLoader(cl);

        Method loadMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
        loadMethod.setAccessible(true);

        Constructor<?> entryPointMethod = null;
        for (byte[] classData : classesData) {
            Class<?> klass = (Class<?>) loadMethod.invoke(cl, null, classData, 0, classData.length, cl.getClass().getProtectionDomain());

            if (klass.getName().equals("fr.crazycat256.cipherclient.CipherClient")) {
                entryPointMethod = klass.getConstructor(Instrumentation.class);
            }
        }

        if (entryPointMethod != null) {
            entryPointMethod.newInstance(inst);
        }
    }

    /**
     * Equivalent {@code serializes} method is in {@code fr.crazycat256.cipherclient.converter.Converter} in the {@code converter} module
     */
    public static byte[][] deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int count = buffer.getInt();
        byte[][] result = new byte[count][];
        for (int i = 0; i < count; i++) {
            int length = buffer.getInt();
            result[i] = new byte[length];
            buffer.get(result[i]);
        }
        return result;
    }
}
