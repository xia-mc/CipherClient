/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtils {

    private static final Map<Class<?>, Map<String, String>> mappings = new HashMap<>();

    static {
        mappings.put(Minecraft.class, new HashMap<>());
        mappings.get(Minecraft.class).put("timer", "field_71428_T");
        mappings.get(Minecraft.class).put("rightClickDelayTimer", "field_71467_ac");
        mappings.get(Minecraft.class).put("blockHitDelay", "field_78781_i");
        mappings.get(Minecraft.class).put("debugFPS", "field_71470_ab");

        mappings.put(Entity.class, new HashMap<>());
        mappings.get(Entity.class).put("isInWeb", "field_70134_J");

        mappings.put(ChunkProviderClient.class, new HashMap<>());
        mappings.get(ChunkProviderClient.class).put("chunkListing", "field_73237_c");

        mappings.put(PlayerControllerMP.class, new HashMap<>());
        mappings.get(PlayerControllerMP.class).put("blockHitDelay", "field_78781_i");

        mappings.put(EntityLivingBase.class, new HashMap<>());
        mappings.get(EntityLivingBase.class).put("jumpTicks", "field_70773_bE");

        mappings.put(GuiScreen.class, new HashMap<>());
        mappings.get(GuiScreen.class).put("eventButton", "field_146287_f");
        mappings.get(GuiScreen.class).put("lastMouseEvent", "field_146288_g");

        mappings.put(KeyBinding.class, new HashMap<>());
        mappings.get(KeyBinding.class).put("pressed", "field_74513_e");
        mappings.get(KeyBinding.class).put("pressTime", "field_151474_i");

        mappings.put(WorldClient.class, new HashMap<>());
        mappings.get(WorldClient.class).put("entityList", "field_73032_d");
    }

    private static final boolean MCP = Utils.getMinecraftEnvironment() == Utils.McEnv.MCP;

    private static final Map<String, Field> fieldCache = new HashMap<>();
    private static final Map<String, Method> methodCache = new HashMap<>();



    public static Field getField(Class<?> klass, String fieldName) {
        String key = klass.getName() + "." + fieldName;
        if (fieldCache.containsKey(key)) {
            return fieldCache.get(key);
        }
        if (!mappings.containsKey(klass) || !mappings.get(klass).containsKey(fieldName)) {
            if (!fieldName.startsWith("field_")) {
                throw new IllegalArgumentException("Field '" + fieldName + "' not found in mappings for class " + klass.getName());
            }
        } else if (!MCP){
            fieldName = mappings.get(klass).get(fieldName);
        }
        Field field = ReflectionHelper.findField(klass, fieldName);
        fieldCache.put(key, field);
        return field;
    }

    public static <E> Method getMethod(Class<? super E> klass, String methodName, Class<?>... parameterTypes) {
        String key = klass.getName() + "." + methodName;
        if (methodCache.containsKey(key)) {
            return methodCache.get(key);
        }
        Method method = ReflectionHelper.findMethod(klass, null, new String[]{methodName}, parameterTypes);
        methodCache.put(key, method);
        return method;
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T get(Class <? super E> classToAccess, E instance, String fieldName) {
        Field field = getField(classToAccess, fieldName);
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, E> void set(Class <? super E> classToAccess, E instance, String fieldName, T value) {
        Field field = getField(classToAccess, fieldName);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object call(Method method, Object instance, Object... args) {
        try {
            return method.invoke(instance, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setPressed(KeyBinding keyBinding, boolean pressed) {
        set(KeyBinding.class, keyBinding, "pressed", pressed);
        if (!pressed) {
            set(KeyBinding.class, keyBinding, "pressTime", 0);
        }
    }

}
