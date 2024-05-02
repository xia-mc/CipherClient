/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems;

/**
 * Base interface for classes that can be populated.
 */
public interface IPopulable {

    /**
     * Populates the class, this should generally be called at launch.
     */
    void populate();

}
