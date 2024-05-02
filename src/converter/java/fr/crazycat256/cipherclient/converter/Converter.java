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

        classes.values().forEach((class1) -> {
            classes.values().forEach((class2) -> {
                if (classes.containsKey(class2.getSuperClass())) {
                    classes.get(class2.getSuperClass()).setPriority(
                            Math.max(classes.get(class2.getSuperClass()).getPriority(),
                                    class2.getPriority() + 1)
                    );
                }

                for (String superInterface : class2.getInterfaces()) {
                    if (classes.containsKey(superInterface)) {
                        classes.get(superInterface).setPriority(
                                Math.max(classes.get(superInterface).getPriority(),
                                        class2.getPriority() + 1)
                        );
                    }
                }
            });
        });

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


    private static class ClassFile implements Comparable<ClassFile> {
        private String name;
        private String superClass;
        private String[] interfaces;
        private final byte[] classData;
        private int priority = 0;
        private static final int HEAD = -889275714;
        private static final byte CONSTANT_Utf8 = 1;
        private static final byte CONSTANT_Integer = 3;
        private static final byte CONSTANT_Float = 4;
        private static final byte CONSTANT_Long = 5;
        private static final byte CONSTANT_Double = 6;
        private static final byte CONSTANT_Class = 7;
        private static final byte CONSTANT_String = 8;
        private static final byte CONSTANT_FieldRef = 9;
        private static final byte CONSTANT_MethodRef = 10;
        private static final byte CONSTANT_InterfaceMethodRef = 11;
        private static final byte CONSTANT_NameAndType = 12;
        private static final byte CONSTANT_MethodHandle = 15;
        private static final byte CONSTANT_MethodType = 16;
        private static final byte CONSTANT_InvokeDynamic = 18;

        public ClassFile(byte[] classData) {
            this.classData = classData;
            this.parse(ByteBuffer.wrap(classData));
        }

        private void parse(ByteBuffer buf) {
            if (buf.order(ByteOrder.BIG_ENDIAN).getInt() != HEAD) {
                throw new RuntimeException("Not a class file");
            }
            buf.getChar();
            buf.getChar();
            int cpSize = buf.getChar();
            String[] cpStrings = new String[cpSize];
            short[] cpClasses = new short[cpSize];

            for (int i = 1; i < cpSize; i++) {
                byte tag = buf.get();
                switch (tag) {
                    case CONSTANT_Utf8:
                        cpStrings[i] = this.decodeString(buf);
                        break;
                    case CONSTANT_Integer:
                        buf.getInt();
                        break;
                    case CONSTANT_Float:
                        buf.getFloat();
                        break;
                    case CONSTANT_Long:
                        buf.getLong();
                        ++i;
                        break;
                    case CONSTANT_Double:
                        buf.getDouble();
                        ++i;
                        break;
                    case CONSTANT_Class:
                        cpClasses[i] = buf.getShort();
                        break;
                    case CONSTANT_String:
                    case CONSTANT_MethodType:
                        buf.getChar();
                        break;
                    case CONSTANT_FieldRef:
                    case CONSTANT_MethodRef:
                    case CONSTANT_InterfaceMethodRef:
                    case CONSTANT_NameAndType:
                        buf.getChar();
                        buf.getChar();
                        break;
                    case CONSTANT_MethodHandle:
                        buf.get();
                        buf.getChar();
                        break;
                    case CONSTANT_InvokeDynamic:
                        buf.getChar();
                        buf.getChar();
                        break;
                    default:
                        throw new RuntimeException("Failed to read constant pool because of type " + tag);
                }
            }

            buf.getChar();
            this.name = cpStrings[cpClasses[buf.getChar()]];
            this.superClass = cpStrings[cpClasses[buf.getChar()]];
            this.interfaces = new String[buf.getChar()];

            for (int i = 0; i < this.interfaces.length; i++) {
                this.interfaces[i] = cpStrings[cpClasses[buf.getChar()]];
            }
        }

        private String decodeString(ByteBuffer buf) {
            int size = buf.getChar();
            int oldLimit = buf.limit();
            buf.limit(buf.position() + size);
            StringBuilder sb = new StringBuilder(size + (size >> 1) + 16);

            while (buf.hasRemaining()) {
                byte b = buf.get();
                if (b > 0) {
                    sb.append((char)b);
                } else {
                    int b2 = buf.get();
                    if ((b & 240) != 224) {
                        sb.append((char)((b & 31) << 6 | b2 & 63));
                    } else {
                        int b3 = buf.get();
                        sb.append((char)((b & 15) << 12 | (b2 & 63) << 6 | b3 & 63));
                    }
                }
            }

            buf.limit(oldLimit);
            return sb.toString();
        }

        public String getName() {
            return this.name;
        }

        public String getSuperClass() {
            return this.superClass;
        }

        public String[] getInterfaces() {
            return this.interfaces;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public byte[] getClassData() {
            return this.classData;
        }

        @Override
        public int compareTo(ClassFile o) {
            return o.priority - this.priority;
        }
    }
}