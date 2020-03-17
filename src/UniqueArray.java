// This is a personal academic project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++, C#, and Java: http://www.viva64.com

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

public class UniqueArray<T> implements Iterable<T> {
    private final HashMap<T, Integer> map;
    private final ArrayList<T> storage;

    public UniqueArray() {
        this(16);
    }

    public UniqueArray(final int initialCapacity) {
        map = new HashMap<>(initialCapacity);
        storage = new ArrayList<>(initialCapacity);
    }

    public UniqueArray(final @NotNull Collection<? extends T> collection) {
        this(collection.size());
        addAll(collection);
    }

    public int add(final T value) {
        final Integer v = map.get(value);
        if (v != null) {
            return v;
        }
        final int val = storage.size() + 1;
        map.put(value, val);
        storage.add(value);

        return val;
    }

    public boolean addAll(final @NotNull Collection<? extends T> collection) {
        final int oldStorage = storage.size();

        for (T t : collection) {
            this.add(t);
        }

        return oldStorage != storage.size();
    }

    public void clear() {
        map.clear();
        storage.clear();
    }

    public boolean contains(final T value) {
        return map.containsKey(value);
    }

    public boolean containsAll(final @NotNull Collection<? extends T> collection) {
        for (T t : collection) {
            if (!map.containsKey(t)) {
                return false;
            }
        }
        return true;
    }

    public void ensuresCapacity(final int minCapacity) {
        storage.ensureCapacity(minCapacity);
    }

    public void forEach(final Consumer<? super T> consumer) {
        storage.forEach(consumer);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UniqueArray)) {
            return false;
        }
        final UniqueArray<?> that = (UniqueArray<?>) o;

        return Objects.equals(storage, that.storage);
    }

    @Override
    public int hashCode() {
        return storage != null ? storage.hashCode() : 0;
    }

    public T get(final int index) {
        return storage.get(index);
    }

    public int indexOf(final T value) {
        return this.idFor(value) - 1;
    }

    public int idFor(final T value) {
        final Integer v = map.get(value);
        if (v != null) {
            return v;
        }
        // No value in map
        return 0;
    }

    public T getById(final int id) {
        return this.get(id - 1);
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }

    public @NotNull Iterator<T> iterator() {
        return storage.iterator();
    }

    public int lastIndexOf(final T value) {
        return this.indexOf(value);
    }

    public T remove(final int index) {
        map.remove(this.get(index));
        return storage.remove(index);
    }

    public boolean remove(final T value) {
        final Integer v = map.get(value);
        if (v == null) {
            return false;
        }
        this.removeById(v);
        return true;
    }

    public boolean removeIf(final Predicate<? super T> predicate) {
        final int oldSize = storage.size();
        for (T e : storage) {
            if (predicate.test(e)) {
                this.remove(e);
            }
        }
        return oldSize != storage.size();
    }

    public T removeById(final int id) {
        return this.remove(id - 1);
    }

    public boolean removeAll(final @NotNull Collection<? extends T> collection) {
        final int oldSize = storage.size();
        for (T t : collection) {
            this.remove(t);
        }

        return oldSize != storage.size();
    }

    public int size() {
        return storage.size();
    }

    @Override
    public Spliterator<T> spliterator() {
        return storage.spliterator();
    }

    UniqueArray<T> subList(final int from, final int to) {
        return new UniqueArray<>(storage.subList(from, to));
    }

    void trimToSize() {
        storage.trimToSize();
    }
}
