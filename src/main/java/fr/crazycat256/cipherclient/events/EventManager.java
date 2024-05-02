/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.events;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.CipherClient;
import fr.crazycat256.cipherclient.systems.Element;
import fr.crazycat256.cipherclient.systems.System;
import fr.crazycat256.cipherclient.systems.SystemManager;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.utils.RenderUtils;
import fr.crazycat256.cipherclient.utils.ServerUtils;

import static fr.crazycat256.cipherclient.CipherClient.mc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventManager {

    // This needs to be thread safe because events can be posted during the initialization
    private static final Set<Class<?>> LISTENED_EVENTS = new CopyOnWriteArraySet<>();
    private static final List<ListenerMethod> HANDLERS = new CopyOnWriteArrayList<>();


    public static void init() {

        LISTENED_EVENTS.clear();
        HANDLERS.clear();

        // Add all Element as a listener
        List<Object> listeners = new ArrayList<>();
        for (System<?> system : SystemManager.getAll()) {
            Set<Class<?>> alreadyUsed = new HashSet<>();
            for (Element element : system.getAll()) {
                if (!alreadyUsed.contains(element.getClass())) {
                    listeners.add(element);
                    alreadyUsed.add(element.getClass());
                }
            }
        }
        listeners.add(new MainListener());
        listeners.add(new ServerUtils());
        listeners.add(new RenderUtils());

        // Find all methods with the @Handler annotation
        for (Object o : listeners) {
            for (Method method : o.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Handler.class)) continue;
                if (method.getParameterTypes().length != 1) continue;
                if (method.getReturnType() != void.class) continue;
                LISTENED_EVENTS.add(method.getParameterTypes()[0]);
                HANDLERS.add(new ListenerMethod(o, method, method.getAnnotation(Handler.class).priority()));
            }
        }
        Collections.sort(HANDLERS);

        // Remove all classes that are subclasses of other listened classes
        Set<Class<?>> classesToRemove = new HashSet<>();
        for (Class<?> listened: LISTENED_EVENTS) {
            for (Class<?> listened2: LISTENED_EVENTS) {
                if (listened == listened2) continue;
                if (listened.isAssignableFrom(listened2)) {
                    classesToRemove.add(listened);
                }
            }
        }
        LISTENED_EVENTS.removeAll(classesToRemove);
    }

    /**
     * Post an event to all listeners <br>
     * This is (among other) called by ASM in {@link fr.crazycat256.cipherclient.transform.transformers.EventBusTransformer}
     */
    public static void postEvent(Object event) {

        if (!CipherClient.isInitialized) {
            // We need to check if the event is a client tick event to ensure that this is called in the correct thread
            if (event instanceof TickEvent.ClientTickEvent) {
                CipherClient.isInitialized = true;
                CipherClient.init();
            } else {
                return;
            }
        }

        boolean isListened = false;
        for (Class<?> listenedEvent : LISTENED_EVENTS) {
            if (listenedEvent.isAssignableFrom(event.getClass())) {
                isListened = true;
                break;
            }
        }
        if (!isListened) return;

        for (ListenerMethod listenerMethod : HANDLERS) {
            if (!listenerMethod.method.getParameterTypes()[0].isAssignableFrom(event.getClass())) continue;

            Handler annotation = listenerMethod.method.getAnnotation(Handler.class);
            if (mc.theWorld == null && annotation.worldRequired()) continue;

            if (listenerMethod.object instanceof Module) {
                Module module = (Module) listenerMethod.object;
                if (!module.isActive()) continue;
            }

            try {
                listenerMethod.method.invoke(listenerMethod.object, event);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException("Error invoking event handler", e);
            }
        }
    }

    private static class ListenerMethod implements Comparable<ListenerMethod> {
        private final Object object;
        private final Method method;
        private final int priority;
        public ListenerMethod(Object object, Method method, int priority) {
            this.object = object;
            this.method = method;
            this.priority = priority;

            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
        }
        @Override
        public int compareTo(ListenerMethod other) {
            return Integer.compare(this.priority, other.priority);
        }
    }
}
