/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import java.util.ArrayList;
import java.util.List;

public class InputWidgetContainer<T> extends InputWidget<T> {

    private final List<InputWidget<T>> widgets = new ArrayList<>();

    @SafeVarargs
    public InputWidgetContainer(T defaultValue, InputWidget<T>... widgets) {
        super(defaultValue);
        for (InputWidget<T> widget : widgets) {
            add(widget);
            widget.setOnChanged(newValue -> {
                setValue(newValue);
                onChanged(newValue);
            });
        }
        update();
    }

    public void add(InputWidget<T> widget) {
        widgets.add(widget);
        update();
    }

    @Override
    public int getMaxWidth() {
        return widgets.stream().mapToInt(widget -> widget.getMaxWidth() + 2).sum() - 2;
    }

    @Override
    public int getMaxHeight() {
        return widgets.stream().mapToInt(InputWidget::getHeight).max().orElse(0);
    }

    @Override
    public T getValue() {
        throw new UnsupportedOperationException("Cannot get value of container");
    }

    @Override
    public void setValue(T value) {
        for (InputWidget<T> widget : widgets) {
            widget.setValue(value);
        }
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        int widgetX = 0;
        for (InputWidget<T> widget : widgets) {
            widget.setPos(widgetX, 0);
            widget.drawComponent(mouseX, mouseY);
            widgetX += widget.getWidth() + 2;
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        for (InputWidget<T> widget : widgets) {
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                break;
            }
        }
    }

    @Override
    public void onMouseMoveOrUp(int mouseX, int mouseY, int button) {
        for (InputWidget<T> widget : widgets) {
            widget.mouseMovedOrUp(mouseX, mouseY, button);
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        for (InputWidget<T> widget : widgets) {
            if (widget.keyTyped(typedChar, keyCode)) {
                break;
            }
        }
    }

    private void update() {
        setWidth(widgets.stream().mapToInt(widget -> widget.getMaxWidth() + 2).sum() - 2);
        setHeight(widgets.stream().mapToInt(InputWidget::getHeight).max().orElse(0));
    }
}
