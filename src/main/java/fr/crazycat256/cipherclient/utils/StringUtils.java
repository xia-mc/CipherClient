/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

public class StringUtils {

    /**
     * Capitalize the first letter of each word (split by "-")
     */
    public static String nameToTitle(String input) {
        String[] words = input.toLowerCase().replace(' ', '-').split("-");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1);
            result.append(capitalizedWord).append(" ");
        }

        return result.toString().trim();
    }

    /**
     * Capitalize the first letter of each word
     * @param input The string to capitalize
     * @return The capitalized string
     */
    public static String colorize(String input) {
        return input.replaceAll("&", "\u00a7").replaceAll("\u00a7\u00a7", "&");
    }

}
