/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils.font;

import fr.crazycat256.cipherclient.utils.JVMUtils;
import fr.crazycat256.cipherclient.utils.Utils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Copied from <a href="https://github.com/xtrm-en/delta-client-v3/blob/main/src/main/java/me/xtrm/delta/client/utils/Fonts.java">Delta Client</a>
 */
public class Fonts {

	private static Font font;
	private static final char[] chars = new char[256];
	private static final GlyphPageFontRenderer[] cache = new GlyphPageFontRenderer[1024];

	static {

		try (InputStream is = JVMUtils.getResourceAsStream("comfortaa.ttf")) {
			font = Font.createFont(0, is);
		} catch (FontFormatException | IOException e) {
			font = new Font("comfortaa", Font.PLAIN, 100);
			try {
				Utils.log("Warning", "Font is shit");
			} catch (NullPointerException e2) {
				System.err.println("Warning: Font is shit");
			}
		}

		for(int i = 0; i < chars.length; i++) {
			chars[i] = (char) i;
		}
	}

	public static GlyphPageFontRenderer getFont(int size) {
		if (size < cache.length && cache[size] != null) {
			return cache[size];
		} else {
			GlyphPage page = new GlyphPage(font.deriveFont(Font.PLAIN, size), true, true);
			page.generateGlyphPage(chars);
			page.setupTexture();
			GlyphPageFontRenderer fontRenderer = new GlyphPageFontRenderer(page, page, page, page);
			cache[size] = fontRenderer;
			return fontRenderer;
		}
	}

}
