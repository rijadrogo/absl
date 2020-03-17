// This is an open source non-commercial project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++, C#, and Java: http://www.viva64.com

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jetbrains.annotations.NotNull;

public class ArrayHashMap<K, V> extends HashMap<K, V> implements Iterable<Map.Entry<K, V>> {
    private final ArrayList<K> storage;

    public ArrayHashMap() {
        this(16);
    }

    public ArrayHashMap(final @NotNull Map<? extends K, ? extends V> map) {
        this(map.size());
        this.putAll(map);
    }

    public ArrayHashMap(final int initialCapacity) {
        this(initialCapacity, 0.75F);
    }

    public ArrayHashMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        storage = new ArrayList<>(initialCapacity);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        if (m != null) {
            storage.ensureCapacity(storage.size() + m.size());
            this.forEach(this::put);
        }
    }

    @Override
    public void forEach(final Consumer<? super Entry<K, V>> action) {
        storage.forEach(elem -> action.accept(new AbstractMap.SimpleEntry<>(elem, get(elem))));
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        storage.forEach(key -> action.accept(key, super.get(key)));
    }

    @Override
    public void clear() {
        super.clear();
        storage.clear();
    }

    @Override
    public boolean isEmpty() {
        return storage.isEmpty();
    }

    @Override
    public @NotNull Iterator<Map.Entry<K, V>> iterator() {
        return new MapListIterator();
    }

    @Override
    public V compute(
        final K key, final @NotNull BiFunction<? super K, ? super V, ? extends V> mappingFunction) {
        final V value = mappingFunction.apply(key, super.get(key));

        if (value == null) {
            return this.remove(key);
        }
        if (!super.containsKey(key)) {
            storage.add(key);
        }
        return super.put(key, value);
    }

    @Override
    public V computeIfPresent(
        final K key, final @NotNull BiFunction<? super K, ? super V, ? extends V> mappingFunction) {
        final V value = mappingFunction.apply(key, super.get(key));
        if (value == null) {
            return this.remove(key);
        }
        return super.put(key, value);
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        if (!super.containsKey(key)) {
            storage.add(key);
        }
        return super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V remove(final Object key) {
        storage.remove(key);
        return super.remove(key);
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        return super.remove(key, value) && storage.remove(key);
    }

    @Override
    public V put(final K key, final V value) {
        if (!super.containsKey(key)) {
            storage.add(key);
        }
        return super.put(key, value);
    }

    @Override
    public V merge(final K key, final V value,
        final BiFunction<? super V, ? super V, ? extends V> mappingFunction) {
        final V oldValue = super.get(key);
        final V v;
        if (oldValue != null) {
            v = mappingFunction.apply(oldValue, value);
        }
        else {
            v = value;
        }
        if (v != null) {
            super.replace(key, v);
        }
        else {
            this.remove(key);
        }
        return v;
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        if (!super.containsKey(key)) {
            storage.add(key);
        }
        return super.putIfAbsent(key, value);
    }

    boolean removeIf(final Predicate<Map.Entry<K, V>> predicate) {
        int i = 0;
        int j = 0;
        boolean removed = false;
        for (final Map.Entry<K, V> elem : super.entrySet()) {
            if (predicate.test(elem)) {
                removed = true;
                super.remove(elem.getKey());
                ++i;
                continue;
            }
            if (i != j) {
                storage.set(j, elem.getKey());
            }
            ++j;
            ++i;
        }
        storage.subList(j, storage.size()).clear();
        return removed;
    }

    Stream<Entry<K, V>> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("Entry Set unsupported");
    }

    public static void main(String[] args) {
        ArrayList<Integer> a = new ArrayList<>();
        a.add(6);
        a.add(2);
        a.add(6);
        a.add(3);
        a.add(6);
        a.add(8);
        a.add(9);
        a.add(7);
        a.subList(0, 3).clear();

        for (var e : a) {
            System.out.println(e);
        }
    }

    private class MapListIterator implements Iterator<Map.Entry<K, V>> {
        private int current = 0;

        @Override
        public boolean hasNext() {
            return current < storage.size();
        }

        @Override
        public Map.Entry<K, V> next() {
            K key = storage.get(current);
            V value = get(key);
            ++current;
            return new AbstractMap.SimpleEntry<>(key, value);
        }
    }
}
