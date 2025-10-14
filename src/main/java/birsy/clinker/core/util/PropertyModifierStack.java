package birsy.clinker.core.util;

import birsy.clinker.core.Clinker;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class PropertyModifierStack<T> {
    final T baseValue;
    final Map<String, ModifierEntry<T>> entryByName;
    final PriorityQueue<ModifierEntry<T>> entryHeap;

    public PropertyModifierStack(T baseValue, int initialCapacity) {
        this.baseValue = baseValue;
        entryByName = new HashMap<>(initialCapacity);
        entryHeap = new PriorityQueue<>(initialCapacity, Comparator.comparingInt(tModifierEntry -> -tModifierEntry.priority()));
    }

    public void pushModifier(String name, int priority, T value) {
        ModifierEntry<T> entry = new ModifierEntry<T>(name, priority, value);
        entryByName.put(entry.name, entry);
        entryHeap.add(entry);
    }

    public void popModifier(String name) {
        if (!entryByName.containsKey(name)) {
            Clinker.LOGGER.warn("No property modifier known as {} found in stack!", name);
            return;
        }
        ModifierEntry<T> entry = entryByName.get(name);
        entryHeap.remove(entry);
        entryByName.remove(name);
    }

    public T value() {
        if (entryHeap.isEmpty()) return baseValue;
        return entryHeap.peek().object;
    }

    private record ModifierEntry<T>(String name, int priority, T object) {}
}
