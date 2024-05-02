/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems;

import com.google.gson.JsonElement;

/**
 * Base interface for classes that can be serialized and deserialized.
 */
public interface ISerializable<T extends JsonElement> {

    /**
     * Abstract method to serialize the object
     * @return the serialized object
     */
    T serialize();

    /**
     * Abstract method to deserialize the object
     * @param data the serialized object
     */
    void deserialize(T data);


}
