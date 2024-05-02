/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import com.google.common.base.Strings;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTUtils {

    /**
     * Format an NBT tag compound into a readable json-like string
     * @param compound The NBT tag compound to format
     * @param indentationLevel The number of spaces to indent each level
     * @param colors Whether to colorize the output
     * @return The formatted NBT tag compound
     */
    public static String formatNBT(NBTTagCompound compound, int indentationLevel, boolean colors) {
        return formatNBT(compound, indentationLevel, indentationLevel, colors);
    }

    /**
     * Recursive function used in {@link #formatNBT(NBTTagCompound, int, boolean)}
     */
    public static String formatNBT(NBTTagCompound compound, int currentIndentation, int indentationLevel, boolean colors) {
        String keyColor = "\u00a7b";
        String bracketColor = "\u00a7f";
        String resetColor = "\u00a7r";
        if (!colors) {
            keyColor = "";
            bracketColor = "";
            resetColor = "";
        }
        StringBuilder result = new StringBuilder(bracketColor + "{" + resetColor);

        String indentation = Strings.repeat(" ", currentIndentation);

        for (Object key : compound.func_150296_c()) {
            NBTBase tag = compound.getTag((String) (key));
            result.append("\n").append(indentation).append(keyColor).append(key).append(resetColor).append(": ");

            if (tag instanceof NBTTagCompound) {
                result.append(formatNBT((NBTTagCompound) tag, currentIndentation + indentationLevel, indentationLevel, colors));
            } else if (tag instanceof NBTTagList) {
                result.append(formatNBT((NBTTagList) tag, currentIndentation + indentationLevel, indentationLevel, colors));
            } else {
                result.append(colors ? colorTag(tag) : tag.toString());
            }

            result.append(",");
        }

        if (result.charAt(result.length() - 1) == ',') {
            result.setCharAt(result.length() - 1, ' ');
        }

        for (int i = result.length(); i < 0; i++) {
            if (result.charAt(i) == ' ' || result.charAt(i) == '\n') {
                continue;
            }
            if (result.charAt(i) == '{') {
                break;
            }
            break;
        }
        if (!compound.func_150296_c().isEmpty()) {
            result.append("\n").append(Strings.repeat(" ", Math.max(0, currentIndentation - indentationLevel)));
        }
        result.append(bracketColor).append("}").append(resetColor);

        return result.toString();
    }

    /**
     * Recursive function used in {@link #formatNBT(NBTTagCompound, int, boolean)}
     */
    public static String formatNBT(NBTTagList list, int currentIndentation, int indentationLevel, boolean colors) {
        String bracketColor = "\u00a7f";
        String resetColor = "\u00a7r";
        if (!colors) {
            bracketColor = "";
            resetColor = "";
        }
        StringBuilder result = new StringBuilder(bracketColor + "[" + resetColor);

        String indentation = Strings.repeat(" ", currentIndentation);

        for (int i = 0; i < list.tagCount(); i++) {
            NBTBase tag = list.getCompoundTagAt(i);
            result.append("\n").append(indentation);

            if (tag instanceof NBTTagCompound) {
                result.append(formatNBT((NBTTagCompound) tag, currentIndentation + indentationLevel, indentationLevel, colors));
            } else if (tag instanceof NBTTagList) {
                result.append(formatNBT((NBTTagList) tag, currentIndentation + indentationLevel, indentationLevel, colors));
            } else {
                result.append(colors ? colorTag(tag) : tag.toString());
            }

            result.append(",");
        }

        if (result.charAt(result.length() - 1) == ',') {
            result.setCharAt(result.length() - 1, ' ');
        }

        if (list.tagCount() != 0) {
            result.append("\n").append(Strings.repeat(" ", Math.max(0, currentIndentation - indentationLevel)));
        }
        result.append(bracketColor).append("]").append(resetColor);

        return result.toString();
    }

    /**
     * Colorize an NBT tag
     * @param tag The NBT tag to colorize
     * @return The colorized NBT tag
     */
    public static String colorTag(NBTBase tag) {
        switch (tag.getId()) {
            case 1: { // Byte
                return "\u00a72" + tag + "\u00a7r";
            }
            case 2: { // Short
                return "\u00a72" + tag + "\u00a7r";
            }
            case 3: { // Int
                return "\u00a72" + tag + "\u00a7r";
            }
            case 4: { // Long
                return "\u00a72" + tag + "\u00a7r";
            }
            case 5: { // Float
                return "\u00a72" + tag + "\u00a7r";
            }
            case 6: { // Double
                return "\u00a72" + tag + "\u00a7r";
            }
            case 7: { // Byte Array
                return "\u00a79" + tag + "\u00a7r";
            }
            case 8: { // String
                return "\u00a76" + tag + "\u00a7r";
            }
            case 11: { // Int Array
                return "\u00a79" + tag + "\u00a7r";
            }
        }
        return "\u00a7f" + tag + "\u00a7r";
    }

    public static String deIndent(String nbt) {
        StringBuilder result = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < nbt.length(); i++) {
            char c = nbt.charAt(i);
            if (c == '"') {
                insideQuotes = !insideQuotes;
            }
            if (!Character.isWhitespace(c) || insideQuotes) {
                result.append(c);
            }
        }

        return result.toString();
    }

}
