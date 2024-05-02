/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.friend;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.crazycat256.cipherclient.systems.ISavable;
import fr.crazycat256.cipherclient.systems.System;
import fr.crazycat256.cipherclient.systems.SystemManager;

import java.util.ArrayList;

public class Friends extends System<Friend> implements ISavable {

    public Friends() {
        super("friends");
    }

    public static Friends get() {
        return SystemManager.get(Friends.class);
    }

    public Friend getFriend(String name) {
        for (Friend friend : this.getAll()) {
            if (friend.name.equalsIgnoreCase(name)) {
                return friend;
            }
        }
        return null;
    }
    public boolean isFriend(String name) {
        for (Friend friend : this.getAll()) {
            if (friend.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getFriends() {
        ArrayList<String> friends = new ArrayList<>();
        for (Friend friend : this.getAll()) {
            friends.add(friend.name);
        }
        return friends;
    }

    @Override
    public JsonObject serialize() {
        JsonObject friendData = new JsonObject();
        JsonArray friends = new JsonArray();
        for (Friend friend : this.getAll()) {
            friends.add(new JsonPrimitive(friend.name));
        }
        friendData.add("friends", friends);
        return friendData;
    }

    @Override
    public void deserialize(JsonObject data) {
        this.clear();
        JsonArray friends = data.getAsJsonArray("friends");
        for (int i = 0; i < friends.size(); ++i) {
            this.add(new Friend(friends.get(i).getAsString()));
        }
    }
}
