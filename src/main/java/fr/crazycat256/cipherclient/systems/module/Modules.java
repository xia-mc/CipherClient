/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module;

import com.google.gson.JsonObject;
import fr.crazycat256.cipherclient.systems.System;
import fr.crazycat256.cipherclient.systems.IPopulable;
import fr.crazycat256.cipherclient.systems.ISavable;
import fr.crazycat256.cipherclient.systems.SystemManager;
import fr.crazycat256.cipherclient.systems.module.combat.*;
import fr.crazycat256.cipherclient.systems.module.misc.*;
import fr.crazycat256.cipherclient.systems.module.render.*;
import fr.crazycat256.cipherclient.systems.module.render.damagepopoff.DamagePopOffs;
import fr.crazycat256.cipherclient.systems.module.render.xray.XRay;
import fr.crazycat256.cipherclient.systems.module.movement.*;
import fr.crazycat256.cipherclient.systems.module.player.*;
import fr.crazycat256.cipherclient.systems.module.world.*;

import java.util.Comparator;

public class Modules extends System<Module> implements IPopulable, ISavable {

    public Modules() {
        super("modules");
    }

    public static Modules get() {
        return SystemManager.get(Modules.class);
    }

    @Override
    public  void populate() {

        // Player
        add(new AntiFire());
        add(new AntiPotion());
        add(new AutoFeed());
        add(new AutoTool());
        add(new Blink());
        add(new ChestStealer());
        add(new FastEat());
        add(new NoJumpDelay());
        add(new NoRotate());
        add(new Regen());


        // Combat
        add(new AimAssist());
        add(new AimBot());
        add(new AutoArmor());
        add(new Criticals());
        add(new FastBow());
        add(new AutoClicker());
        add(new Forcefield());
        add(new Hitboxes());
        add(new KillAura());
        add(new Reach());
        add(new TriggerBot());


        // Render
        add(new BlockOverlay());
        add(new Breadcrumb());
        add(new DamagePopOffs());
        add(new Chams());
        add(new Freecam());
        add(new Freelook());
        add(new FreezeCam());
        add(new Fullbright());
        add(new HUD());
        add(new LogoutSpots());
        add(new Nametags());
        add(new NoRender());
        add(new Projectiles());
        add(new TileESP());
        add(new Tracers());
        add(new XRay());


        // Movement
        add(new AirJump());
        add(new Flight());
        add(new GuiMove());
        add(new HighJump());
        add(new NoFall());
        add(new Speed());
        add(new Sneak());
        add(new Spider());
        add(new Sprint());
        add(new Step());
        add(new Velocity());


        // World
        add(new AirPlace());
        add(new AutoMlg());
        add(new DeathCoords());
        add(new FakeDestroy());
        add(new FastBreak());
        add(new FastPlace());
        add(new NoClip());
        add(new NoWeb());
        add(new Nuker());
        add(new Scaffold());
        add(new Timer());


        // Misc
        add(new CoordLogger());
        add(new Derp());
        add(new DrawerPeek());
        add(new MiddleClickFriend());
        add(new NBTTooltip());
        add(new NEITweaks());
        add(new Notifier());
        add(new ShowArmor());


        this.sort(Comparator.comparing((Module module) -> module.name));
    }

    @Override
    public JsonObject serialize() {
        JsonObject moduleData = new JsonObject();
        for (Module module : this.getAll()) {
            moduleData.add(module.name, module.serialize());
        }
        return moduleData;
    }

    @Override
    public void deserialize(JsonObject data) {
        JsonObject moduleData = data.getAsJsonObject();
        for (Module module : this.getAll()) {
            if (moduleData.has(module.name)) {
                module.deserialize(moduleData.getAsJsonObject(module.name));
            }
        }
    }
}
