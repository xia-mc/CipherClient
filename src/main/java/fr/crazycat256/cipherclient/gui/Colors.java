/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui;

import static fr.crazycat256.cipherclient.utils.GLUtils.getColor;

public interface Colors {

    int cipherColor = getColor(104, 254, 255);
    int guiColor = getColor(54, 204, 205);
    int textColor = getColor(255, 255, 255);
    int borderColor = getColor(0, 0, 0);
    int backColor = getColor(200, 0, 0, 0);

    int highlightColor = getColor(64, 64, 64);
    int highlightHoverColor = getColor(48, 48, 48);
    int accentColor = getColor(230, 0, 0, 0);
}
