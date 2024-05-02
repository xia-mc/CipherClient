/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.crazycat256.cipherclient.utils.Utils;

import java.io.*;
import java.nio.file.Files;

/**
 * Base interface for objects that can be saved to a config file
 * @see ISerializable
 */
public interface ISavable extends ISerializable<JsonObject> {

    /**
     * Load the object from its config file
     */
    default void load() {
        if (!(this instanceof System)) {
            throw new IllegalArgumentException("ISerializable must be implemented by a System");
        }
        try {
            String filePath = Utils.getConfig(((System<?>) this).name + ".json");
            File configFile = new File(filePath);
            String rawConfig = new String(Files.readAllBytes(configFile.toPath()));
            JsonObject json = new Gson().fromJson(rawConfig, JsonObject.class);
            this.deserialize(json);
        } catch (IOException e) {
            // Empty catch block
        }
    }

    /**
     * Save the object to its config file
     */
    default void save() {
        if (!(this instanceof System)) {
            throw new IllegalArgumentException("ISerializable must be implemented by a System");
        }
        try {
            FileWriter filewriter = new FileWriter(Utils.getConfig(((System<?>) this).name + ".json"));
            try (BufferedWriter bufferedwriter = new BufferedWriter(filewriter)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                bufferedwriter.write(gson.toJson(this.serialize()));
            }
        } catch (IOException e) {
            // Empty catch block
        }
    }

}
