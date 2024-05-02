/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.macro;

import com.google.gson.JsonObject;
import fr.crazycat256.cipherclient.systems.ISerializable;
import fr.crazycat256.cipherclient.systems.Element;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public class Macro extends Element implements ISerializable<JsonObject>, Comparable<Macro> {

    private String command;
    private int keyCode;

    public Macro(String command, int keyCode) {
        super("Macro");
        this.command = command;
        this.keyCode = keyCode;
    }

    public Macro(JsonObject data) {
        this.deserialize(data);
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public String getKeyDescription() {
        return this.command;
    }

    public void press() {
        mc.thePlayer.sendChatMessage(command);
    }

    @Override
    public int compareTo(Macro o) {
        return this.getKeyDescription().compareTo(o.getKeyDescription());
    }

    @Override
    public JsonObject serialize() {
        JsonObject macroData = new JsonObject();
        macroData.addProperty("command", command);
        macroData.addProperty("keyCode", keyCode);
        return macroData;
    }

    @Override
    public void deserialize(JsonObject data) {
        JsonObject dataObj = data.getAsJsonObject();
        this.keyCode = dataObj.get("keyCode").getAsInt();
        this.command = dataObj.get("command").getAsString();
    }
}
