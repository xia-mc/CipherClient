/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Base class for Modules, Commands, etc.
 * @param <E> The type of the elements to manage
 * @see Element
 */
public abstract class System<E extends Element> {

    public final String name;
    private final ArrayList<E> elements;

    public System(String name) {
        this.name = name;
        this.elements = new ArrayList<>();
    }

    public ArrayList<E> getAll() {
        return new ArrayList<>(elements);
    }

    public void add(int index, E element) {
        if (!element.getClass().isAnnotationPresent(MultiInstance.class)) {
            for (E e : elements) {
                if (e.getClass() == element.getClass()) {
                    throw new IllegalArgumentException("Element " + element.getClass().getSimpleName() + " already exists");
                }
            }
        }
        elements.add(index, element);
    }

    public void add(E system) {
        add(elements.size(), system);
    }

    public void remove(E system) {
        elements.remove(system);
    }

    public void clear() {
        elements.clear();
    }

    @SuppressWarnings("unchecked")
    public <T extends E> T get(Class<T> klass) {
        if (klass.isAnnotationPresent(MultiInstance.class)) {
            throw new IllegalArgumentException("Class " + klass.getSimpleName() + " is multi-instance");
        }
        for (E system : elements) {
            if (klass.isInstance(system)) {
                return (T) system;
            }
        }
        throw new IllegalArgumentException("Element " + klass.getSimpleName() + " not found");
    }

    public void sort(Comparator<E> comparator) {
        elements.sort(comparator);
    }
}
