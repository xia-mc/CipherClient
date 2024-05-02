/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import java.util.function.Consumer;

public abstract class InputWidget<T> extends Widget {

    protected T value;
    private Consumer<T> onChanged;

    public InputWidget(T defaultValue) {
        this.value = defaultValue;
    }


    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setOnChanged(Consumer<T> onChanged) {
        this.onChanged = onChanged;
    }

    protected void onChanged(T newValue) {
        if (onChanged != null) {
            onChanged.accept(newValue);
        }
    }
}
