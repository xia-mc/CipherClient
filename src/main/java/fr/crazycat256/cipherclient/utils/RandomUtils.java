/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    public static int randint(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
