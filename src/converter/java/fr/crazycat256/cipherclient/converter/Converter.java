/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.converter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Resource generation class <br>
 * This class is responsible for converting the classes of an input Jar file into a binary file <br>
 * It is only used during the build process <br>
 * Some parts of this class are taken from <a href="https://github.com/radioegor146/jar-to-dll/blob/master/injector-src/ForgeInjector.java">jar-to-dll</a>
 */
public class Converter {

    public static void main(String[] args) {

        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: java -jar converter.jar <input.jar> <output.bin>");
        }

        new File(args[1]).getParentFile().mkdirs();
        System.out.println(Paths.get(args[1]).toAbsolutePath());

        try {
            processInputJar(args[0], args[1]);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process input jar", e);
        }

    }

    public static void processInputJar(String inputPath, String outputPath) throws IOException {
        Map<String, ClassFile> classes = new HashMap<>();
        try (JarFile jarFile = new JarFile(inputPath)) {
            for (ZipEntry entry : jarFile.stream().collect(Collectors.toList())) {
                if (!entry.getName().endsWith(".class")) {
                    continue;
                }
                try (InputStream stream = jarFile.getInputStream(entry)) {
                    ClassFile classFile = new ClassFile(readStreamFully(stream));
                    classes.put(classFile.getName(), classFile);
                }
            }
        }

        System.out.println("Processing " + classes.size() + " classes");

        for (int i = 0; i < classes.size(); i++) {
            for (ClassFile klass : classes.values()) {
                if (classes.containsKey(klass.getSuperClass())) {
                    classes.get(klass.getSuperClass()).setPriority(
                        Math.max(classes.get(klass.getSuperClass()).getPriority(),
                            klass.getPriority() + 1)
                    );
                }

                for (String superInterface : klass.getInterfaces()) {
                    if (classes.containsKey(superInterface)) {
                        classes.get(superInterface).setPriority(
                            Math.max(classes.get(superInterface).getPriority(),
                                klass.getPriority() + 1)
                        );
                    }
                }
            }
        }

        System.out.println("Load priority calculated, sorting...");

        List<ClassFile> resultClasses = new ArrayList<>(classes.values());

        Collections.sort(resultClasses);

        byte[][] classData = new byte[resultClasses.size()][];
        for (int i = 0; i < resultClasses.size(); i++) {
            classData[i] = resultClasses.get(i).getClassData();
        }

        try (OutputStream outputStream = Files.newOutputStream(Paths.get(outputPath))) {
            byte[] serialized = serialize(classData);
            outputStream.write(serialized);
        }
    }

    /**
     * Equivalent {@code deserialize} method is in {@code fr.crazycat256.cipherclient.injector.Agent} in the {@code injector} module
     */
    private static byte[] serialize(byte[][] data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data.length).array());
            for (byte[] bytes : data) {
                outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(bytes.length).array());
                outputStream.write(bytes);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize data", e);
        }
        return outputStream.toByteArray();
    }

    private static byte[] readStreamFully(InputStream stream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        while (true) {
            int read = stream.read(buffer);

            if (read == -1) {
                break;
            }

            outputStream.write(buffer, 0, read);
        }
        return outputStream.toByteArray();
    }
}
