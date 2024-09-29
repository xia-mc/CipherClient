/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render.xray;

import com.google.gson.JsonObject;
import fr.crazycat256.cipherclient.systems.ISerializable;
import java.util.ArrayList;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/gui/xraysettings/XRayBlock.java">ehacks-pro</a>
 * @author radioegor146
 */
public class XRayBlock implements ISerializable<JsonObject> {

    public static ArrayList<XRayBlock> blocks = new ArrayList<>();
    public int r;
    public int g;
    public int b;
    public int a;
    public int meta;
    public String id = "";
    public boolean enabled = true;

    public XRayBlock() {
    }

    public XRayBlock(int r, int g, int b2, int a2, int meta, String id, boolean enabled) {
        this.r = r;
        this.g = g;
        this.b = b2;
        this.a = a2;
        this.id = id;
        this.meta = meta;
        this.enabled = enabled;
    }

    @Override
    public JsonObject serialize() {
        JsonObject data = new JsonObject();
        data.addProperty("r", this.r);
        data.addProperty("g", this.g);
        data.addProperty("b", this.b);
        data.addProperty("a", this.a);
        data.addProperty("meta", this.meta);
        data.addProperty("id", this.id);
        data.addProperty("enabled", this.enabled);
        return data;
    }

    @Override
    public void deserialize(JsonObject data) {
        JsonObject obj = data.getAsJsonObject();
        this.r = obj.get("r").getAsInt();
        this.g = obj.get("g").getAsInt();
        this.b = obj.get("b").getAsInt();
        this.a = obj.get("a").getAsInt();
        this.meta = obj.get("meta").getAsInt();
        this.id = obj.get("id").getAsString();
        this.enabled = obj.get("enabled").getAsBoolean();
    }
}
