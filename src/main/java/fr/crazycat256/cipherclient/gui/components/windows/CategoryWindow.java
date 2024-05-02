/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.windows;

import fr.crazycat256.cipherclient.CipherClient;
import fr.crazycat256.cipherclient.gui.clickgui.ModuleButton;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Modules;

import java.util.ArrayList;

public class CategoryWindow extends Window {

    public final String title;
    protected final Category category;
    public final ArrayList<ModuleButton> buttons = new ArrayList<>();

    public CategoryWindow(Category category, int x, int y) {
        super(x, y, category.getName());
        this.title = category.getName();
        this.category = category;

        for (Module mod : Modules.get().getAll()) {
            if (mod.getCategory() == this.category && mod.isWorking()) {
                ModuleButton button = new ModuleButton(this, mod, mod.formattedName, this.height);
                this.buttons.add(button);
                this.height += button.getHeight();
                width = Math.max(width, button.getMinWidth());
            }
        }
    }


    @Override
    protected void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);

        int yPos = this.getBarHeight();
        for (ModuleButton button : this.buttons) {
            button.setState(button.module.isActive());
            button.setPos(0, yPos);
            button.setWidth(width);
            button.drawComponent(mouseX, mouseY);
            yPos += button.getHeight();
        }
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        super.onMouseClick(mouseX, mouseY, button);
        if (mouseY < this.getBarHeight()) {
            CipherClient.clickGui.sendPanelToFront(this);
        }
        for (ModuleButton modButton : this.buttons) {
            if (modButton.mouseClicked(mouseX, mouseY, button)) {
                break;
            }
        }
    }
}
