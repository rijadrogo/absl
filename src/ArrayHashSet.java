// This is an open source non-commercial project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++, C#, and Java: http://www.viva64.com

/*
 * Created by Rijad 13-Mar-20
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

public class ArrayHashSet<T> extends HashSet<T> implements Iterable<T> {
    private ArrayList<T> storage;

    public ArrayHashSet() {
        this(16, 0.75F);
    }

    public ArrayHashSet(final int capacity) {
        this(capacity, 0.75F);
    }

    public ArrayHashSet(final int capacity, final float loadFactor) {
        super(capacity, loadFactor);
        storage = new ArrayList<>(capacity);
    }

    public ArrayHashSet(final Collection<? extends T> collection) {
        this.addAll(collection);
    }

    @Override
    public boolean addAll(final @NotNull Collection<? extends T> collection) {
        final int oldSize = storage.size();
        collection.forEach(this::add);
        return oldSize != storage.size();
    }

    @Override
    public boolean add(final T value) {
        return super.add(value) && storage.add(value);
    }

    public void clear() {
        super.clear();
        storage.clear();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return storage.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return storage.spliterator();
    }

    @Override
    public boolean retainAll(final @NotNull Collection<?> collection) {
        return this.removeIf(elem -> !collection.contains(elem));
    }

    @Override
    public boolean removeAll(final @NotNull Collection<?> collection) {
        return this.removeIf(collection::contains);
    }

    @Override
    public boolean removeIf(final Predicate<? super T> filter) {
        final int oldSize = storage.size();
        this.forEach(elem -> {
            if (filter.test(elem)) {
                this.remove(elem);
            }
        });
        return oldSize != storage.size();
    }

    @Override
    public void forEach(final Consumer<? super T> action) {
        storage.forEach(action);
    }

    @Override
    public boolean remove(final Object o) {
        return super.remove(o) && storage.remove(o);
    }

    @Override
    public Stream<T> stream() {
        return storage.stream();
    }
}
