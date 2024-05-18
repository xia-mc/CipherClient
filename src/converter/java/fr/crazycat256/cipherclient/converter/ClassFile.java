/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.converter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class ClassFile implements Comparable<ClassFile> {
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
                case CONSTANT_InvokeDynamic:
                    buf.getChar();
                    buf.getChar();
                    break;
                case CONSTANT_MethodHandle:
                    buf.get();
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
                sb.append((char) b);
            } else {
                int b2 = buf.get();
                if ((b & 240) != 224) {
                    sb.append((char) ((b & 31) << 6 | b2 & 63));
                } else {
                    int b3 = buf.get();
                    sb.append((char) ((b & 15) << 12 | (b2 & 63) << 6 | b3 & 63));
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
