/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import fr.crazycat256.cipherclient.utils.GLUtils;
import net.minecraft.util.MathHelper;

public abstract class Slider<T extends Number> extends InputWidget<T> {

    protected static final int SLIDER_WIDTH = 3;
    protected static final int BAR_HEIGHT = 2;
    private int dragOffset = -999;
    public final T min;
    public final T max;


    public Slider(T min, T max, T defaultValue) {
        super(defaultValue);
        this.min = min;
        this.max = max;

        this.setWidth(300 + SLIDER_WIDTH);
        this.setHeight(10);
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        int progress = getProgress();
        if (dragOffset != -999) {
            progress = mouseX - dragOffset;
            updateProgress(progress);
        }
        progress = MathHelper.clamp_int(progress, 0, this.width - SLIDER_WIDTH);
        int barY = this.height / 2 - BAR_HEIGHT / 2;

        // Bar
        GLUtils.drawBorderedRect(0, barY, this.width, barY + BAR_HEIGHT, borderColor, highlightColor);
        GLUtils.drawRect(0.5f, barY + 0.5f, progress, barY + BAR_HEIGHT - 0.5f, guiColor);

        // Slider
        GLUtils.drawBorderedRect(progress, 0, progress + SLIDER_WIDTH, this.height, borderColor, guiColor);

    }


    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        int progress = getProgress();
        if (button == 0 && mouseX >= progress && mouseX <= progress + SLIDER_WIDTH) {
            dragOffset = mouseX - progress;
        } else {
            int p = mouseX - SLIDER_WIDTH / 2;
            p = MathHelper.clamp_int(p, 0, this.width - SLIDER_WIDTH);
            updateProgress(p);
            dragOffset = mouseX - p;
        }
    }

    @Override
    protected void onMouseMoveOrUp(int mouseX, int mouseY, int button) {
        if (button == 0) {
            dragOffset = -999;
        }
    }

    private void updateProgress(int progress) {
        if (progress != getProgress()) {
            onChanged(this.value);
            setProgress(progress);
        }
    }

    /**
     * @return A value between 0 and width - SLIDER_WIDTH
     */
    protected abstract int getProgress();

    /**
     * @param progress A value between 0 and width - SLIDER_WIDTH
     */
    protected abstract void setProgress(int progress);
}
