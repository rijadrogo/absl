// This is an open source non-commercial project. Dear PVS-Studio, please check
// it. PVS-Studio Static Code Analyzer for C, C++, C#, and Java:
// http://www.viva64.com

/*
 * Created by Rijad 13-Mar-20
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

@NotNull
public final class Algorithms {
    private Algorithms() {
    }

    /**
     * @param collection collection of elements
     * @param <T>        Any type that is hashable
     * @return Optional of most frequent element in collection, if collection
     *     is null or empty returns Optional.empty()
     */
    public static <T> Optional<T> mostFrequentElement(final Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Optional.empty();
        }

        final HashMap<T, Integer> map = new HashMap<>(collection.size());
        collection.forEach(elem
            -> map.compute(elem, (key, value) -> key != null && value != null ? value + 1 : 1));

        return mostFrequentElement(map);
    }

    /**
     * @param iterator iterator of some collection
     * @param <T>      Any type that is hashable
     * @return Optional of most frequent element, if iterator
     *     is null or there are no next iterator returns Optional.empty()
     */
    public static <T> Optional<T> mostFrequentElement(final Iterator<T> iterator) {
        if (iterator == null || !iterator.hasNext()) {
            return Optional.empty();
        }

        final HashMap<T, Integer> map = new HashMap<>();
        iterator.forEachRemaining(elem
            -> map.compute(elem, (key, value) -> key != null && value != null ? value + 1 : 1));

        return mostFrequentElement(map);
    }

    /**
     * @param map Map of elements ond number of their occurrences
     * @param <T> Any type that is hashable
     * @return Optional of element that has biggest value in map
     */
    private static <T> Optional<T> mostFrequentElement(final @NotNull HashMap<T, Integer> map) {
        return map.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }

    /**
     * @param collection Collection of elements
     * @param <T> Any type that is hashable
     * @return Optional of first element that occurred more than once in collection
     *     or empty if collection is null or empty
     */
    public static <T> Optional<T> firstRepeatingElement(final Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Optional.empty();
        }
        final ArrayHashSet<T> set = new ArrayHashSet<>(collection.size());

        for (T elem : collection) {
            if (set.add(elem)) {
                return Optional.of(elem);
            }
        }
        return Optional.of(collection.iterator().next());
    }

    /**
     * @param iterator iterator to elements
     * @param <T> Any type that is hashable
     * @return Optional of first element that occurred more than once in collection
     *     or Optional.empty() if there is no iterator.nex() or iterator is null
     */
    public static <T> Optional<T> firstRepeatingElement(final Iterator<T> iterator) {
        if (iterator == null || !iterator.hasNext()) {
            return Optional.empty();
        }

        final ArrayHashSet<T> set = new ArrayHashSet<>();

        while (iterator.hasNext()) {
            final T value = iterator.next();
            if (set.add(value)) {
                return Optional.of(value);
            }
        }
        return Optional.of(set.iterator().next());
    }

    /**
     * @param collection collection to elements
     * @param <T> Any type that is hashable
     * @return Optional of first element that occurred more than once in collection
     *     or Optional.empty() if collection is null or empty
     */
    public static <T> Optional<T> firstNonRepeatingElement(final Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Optional.empty();
        }

        final ArrayHashMap<T, Integer> map = new ArrayHashMap<>(collection.size());
        collection.forEach(elem
            -> map.compute(elem, (key, value) -> key != null && value != null ? value + 1 : 1));
        return firstNonRepeatingElementImpl(map);
    }

    /**
     * @param iterator iterator to elements
     * @param <T> Any type that is hashable
     * @return Optional of first element that occurred only once in
     *     or Optional.empty() if there is no iterator.nex() or iterator is null
     */
    public static <T> Optional<T> firstNonRepeatingElement(final Iterator<T> iterator) {
        if (iterator == null || !iterator.hasNext()) {
            return Optional.empty();
        }

        final ArrayHashMap<T, Integer> map = new ArrayHashMap<>();
        iterator.forEachRemaining(elem
            -> map.compute(elem, (key, value) -> key != null && value != null ? value + 1 : 1));

        return firstNonRepeatingElementImpl(map);
    }

    /**
     * @param map Map of elements ond number of their occurrences
     * @param <T> Any type that is hashable
     * @return Optional of first element that occurred only once
     */
    private static <T> Optional<T> firstNonRepeatingElementImpl(
        final @NotNull ArrayHashMap<T, Integer> map) {
        return map.stream()
            .filter(elem -> elem.getValue() == 1)
            .findFirst()
            .or(() -> Optional.of(map.iterator().next()))
            .map(Map.Entry::getKey);
    }

    /**
     * @param collection1 collection of elements
     * @param collection2 collection of elements
     * @param <T> equality comparable
     * @return returns true if two collections have same elements
     */
    public static <T> boolean equals(
        final Collection<T> collection1, final Collection<T> collection2) {
        if (collection1 == null || collection2 == null) {
            return collection1 == null && collection2 == null;
        }
        return collection1.equals(collection2);
    }

    /**
     * @param iterator1 iterator to elements
     * @param iterator2 iterator to elements
     * @param <T> equality comparable
     * @return returns true if two iterators have same elements
     */
    public static <T> boolean equals(final Iterator<T> iterator1, final Iterator<T> iterator2) {
        if (iterator1 == null || iterator2 == null) {
            return iterator1 == null && iterator2 == null;
        }
        if (!iterator1.hasNext() || !iterator2.hasNext()) {
            return !iterator1.hasNext() && !iterator2.hasNext();
        }

        while (iterator1.hasNext() && iterator2.hasNext()) {
            if (!iterator1.next().equals(iterator2.next())) {
                return false;
            }
        }
        return !iterator1.hasNext() && !iterator2.hasNext();
    }

    /**
     * @param collection collection of elements
     * @param <T> Any type that is hashable
     * @return Returns number of elements that are unique in collection
     */
    public static <T> long numOfUniqueElements(final Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return 0;
        }
        final HashSet<T> set = new HashSet<>(collection.size());
        set.addAll(collection);
        return set.size();
    }

    /**
     * @param iterator iterator of elements
     * @param <T> Any type that is hashable
     * @return Returns number of elements that are unique in collection
     */
    public static <T> long numOfUniqueElements(final Iterator<T> iterator) {
        if (iterator == null || !iterator.hasNext()) {
            return 0;
        }
        final HashSet<T> set = new HashSet<>();
        iterator.forEachRemaining(set::add);
        return set.size();
    }

    /**
     * @param collection collection of elements
     * @param count
     * @param predicate predicates if element should be counted
     * @param <T> no requirements
     * @return true if collection has exactly N elements, otherwise false
     */
    public static <T> boolean hasNItems(
        final Collection<T> collection, final long count, final @NotNull Predicate<T> predicate) {
        if (collection == null) {
            return false;
        }
        return hasNItemsUnchecked(collection.iterator(), count, predicate);
    }

    /**
     * @param iterator iterator to elements
     * @param count
     * @param predicate predicates if element should be counted
     * @param <T> no requirements
     * @return true if collection has exactly N elements, otherwise false
     */
    public static <T> boolean hasNItems(
        final Iterator<T> iterator, final long count, final @NotNull Predicate<T> predicate) {
        if (iterator == null) {
            return false;
        }
        return hasNItemsUnchecked(iterator, count, predicate);
    }

    /**
     * @param iterator iterator to elements
     * @param count
     * @param predicate predicates if element should be counted
     * @param <T> no requirements
     * @return true if collection has exactly N elements, otherwise false
     */
    private static <T> boolean hasNItemsUnchecked(final @NotNull Iterator<T> iterator,
        final long count, final @NotNull Predicate<T> predicate) {
        return countIf(iterator, predicate) == count;
    }

    /**
     * @param collection collection of elements
     * @param predicate predicate returns true if element should be counted
     * @param <T> no requirements
     * @return number of elements for which predicate.test() returns true
     * @throws NullPointerException if predicate is null
     */
    public static <T> long countIf(
        final Collection<T> collection, final @NotNull Predicate<T> predicate) {
        return getStreamOrEmpty(collection).filter(predicate).count();
    }

    /**
     * @param iterator iterator to elements
     * @param predicate predicate returns true if element should be counted
     * @param <T> no requirements
     * @return return number of elements for which predicate.test() returns true
     */
    public static <T> long countIf(
        final Iterator<T> iterator, final @NotNull Predicate<T> predicate) {
        if (iterator == null) {
            return 0;
        }

        long count = 0;
        while (iterator.hasNext()) {
            if (predicate.test(iterator.next())) {
                ++count;
            }
        }
        return count;
    }

    /**
     * @param collection collection to elements
     * @param count
     * @param predicate predicates if element should be counted
     * @param <T> no requirements
     * @return true if collection has N elements or more, otherwise false
     */
    public static <T> boolean hasNItemsOrMore(
        final Collection<T> collection, final long count, final @NotNull Predicate<T> predicate) {
        if (collection == null || count < 0) {
            return false;
        }
        if (count == 0) {
            return !collection.isEmpty();
        }
        return hasNItemsOrMore(collection.iterator(), count, predicate);
    }

    /**
     * @param iterator iterator to elements
     * @param count
     * @param predicate predicates if element should be counted
     * @param <T> no requirements
     * @return true if collection has N elements or more, otherwise false
     */
    public static <T> boolean hasNItemsOrMore(
        final Iterator<T> iterator, final long count, final @NotNull Predicate<T> predicate) {
        if (iterator == null || count < 0) {
            return false;
        }
        if (count == 0) {
            return !iterator.hasNext();
        }
        return hasNItemsOrMoreUnchecked(iterator, count, predicate);
    }

    /**
     * @param iterator iterator to elements
     * @param count
     * @param predicate predicates if element should be counted
     * @param <T> no requirements
     * @return true if collection has N elements or more, otherwise false
     */
    private static <T> boolean hasNItemsOrMoreUnchecked(final @NotNull Iterator<T> iterator,
        final long count, final @NotNull Predicate<T> predicate) {
        int goodItems = 0;
        while (iterator.hasNext()) {
            if (predicate.test(iterator.next())) {
                if (++goodItems >= count) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> boolean anyMatch(
        final Iterator<T> iterator, final @NotNull Predicate<T> predicate) {
        return !noneMatch(iterator, predicate);
    }

    public static <T> boolean anyMatch(
        final Collection<T> collection, final @NotNull Predicate<T> predicate) {
        return !noneMatch(collection, predicate);
    }

    public static <T> boolean noneMatch(
        final Iterator<T> iterator, final @NotNull Predicate<T> predicate) {
        if (iterator == null) {
            return false;
        }

        while (iterator.hasNext()) {
            if (predicate.test(iterator.next())) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean noneMatch(
        final Collection<T> collection, final @NotNull Predicate<T> predicate) {
        return getStreamOrEmpty(collection).noneMatch(predicate);
    }

    public static <T> boolean allMatch(
        final Collection<T> collection, final @NotNull Predicate<T> predicate) {
        return getStreamOrEmpty(collection).allMatch(predicate);
    }

    public static <T> boolean allMatch(
        final Iterator<T> iterator, final @NotNull Predicate<T> predicate) {
        return findIfNot(iterator, predicate).isPresent();
    }

    /**
     * @param collection collection of elements
     * @param predicate predicate – a non-interfering, stateless predicate to
     *                  apply to each element to determine
     *                  if it should be returned
     * @param <T> no requirements
     * @return Optional of first element that doesn't satisfies predicate,
     *     null if collection is null or empty
     *     none of elements doesn't satisfies predicate
     * @throws NullPointerException if specified predicate is null
     */
    public static <T> @NotNull Optional<T> findIfNot(
        final Collection<T> collection, final @NotNull Predicate<T> predicate) {
        return findIf(collection, elem -> !predicate.test(elem));
    }

    /**
     * @param iterator iterator
     * @param predicate predicate – a non-interfering, stateless predicate to
     *                  apply to each element to determine
     *                  if it should be returned
     * @param <T> no requirements
     * @return Optional of first element that doesn't satisfies predicate,
     *     null if iterator is null or empty
     *     none of elements doesn't satisfies predicate
     * @throws NullPointerException if specified predicate is null
     */
    public static <T> Optional<T> findIfNot(
        final Iterator<T> iterator, final @NotNull Predicate<T> predicate) {
        return findIf(iterator, elem -> !predicate.test(elem));
    }

    /**
     * @param collection collection of elements
     * @param predicate predicate – a non-interfering, stateless predicate to
     *                  apply to each element to determine
     *                  if it should be returned
     * @param <T> no requirements
     * @return Optional of first element that satisfies predicate, or null if
     *     collection is null or empty or none of elements satisfies predicate
     * @throws NullPointerException if specified predicate is null
     */
    public static <T> @NotNull Optional<T> findIf(
        final Collection<T> collection, final @NotNull Predicate<T> predicate) {
        return getStreamOrEmpty(collection).filter(predicate).findFirst();
    }

    /**
     * @param iterator iterator
     * @param predicate predicate – a non-interfering, stateless predicate to
     *                  apply to each element to determine
     *                  if it should be returned
     * @param <T> no requirements
     * @return Optional of first element that satisfies predicate, or null if
     *     iterator is null or empty or none of elements satisfies predicate
     * @throws NullPointerException if specified predicate is null
     */
    public static <T> Optional<T> findIf(
        final Iterator<T> iterator, final @NotNull Predicate<T> predicate) {
        if (iterator == null) {
            return Optional.empty();
        }
        while (iterator.hasNext()) {
            final T value = iterator.next();
            if (predicate.test(value)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * @param iterator iterator to elements
     * @param value value to be counted
     * @param <T> no requirements
     * @return number of occurrence's of value in iterator range
     */
    public static <T> long count(final Iterator<T> iterator, final T value) {
        if (iterator == null) {
            return 0;
        }
        long count = 0;
        while (iterator.hasNext()) {
            if (iterator.next().equals(value)) {
                ++count;
            }
        }
        return count;
    }

    /**
     * @param collection collection of elements
     * @param value value to be counted
     * @param <T> no requirements
     * @return number of occurrence's of value in iterator range
     */
    public static <T> long count(final Collection<T> collection, final T value) {
        return getStreamOrEmpty(collection).filter(elem -> elem.equals(value)).count();
    }

    /**
     * @param iterator iterator to elements
     * @param value value to be found
     * @param <T> no requirements
     * @return return optional of value from iterator range if founded, if iterator is null
     *     or there is no occurrences of value in range Optional.empty()
     */
    public static <T> Optional<T> find(final Iterator<T> iterator, final T value) {
        if (iterator == null) {
            return Optional.empty();
        }
        while (iterator.hasNext()) {
            final T val = iterator.next();
            if (val.equals(value)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * @param collection collection of elements
     * @param value value to be found
     * @param <T> no requirements
     * @return return optional of value from iterator range if founded, if iterator is null
     *     or there is no occurrences of value in range Optional.empty()
     */
    public static <T> @NotNull Optional<T> find(final Collection<T> collection, final T value) {
        return getStreamOrEmpty(collection).filter(val -> val.equals(value)).findFirst();
    }

    private static <T> Stream<T> getStreamOrEmpty(final Collection<T> collection) {
        if (collection == null) {
            return Stream.empty();
        }
        return collection.stream();
    }

    /**
     * @param iterator iterator to elements
     * @param count number of times action will be applied
     * @param <T> no requirements
     * @param action The action to be performed for each element
     * @throws NullPointerException if specified action is null
     */
    public static <T> void forEachN(
        final Iterator<T> iterator, long count, final @NotNull Consumer<T> action) {
        if (iterator == null || count < 0) {
            return;
        }
        forEachNUnchecked(iterator, count, action);
    }

    /**
     * @param collection collection of elements
     * @param count number of times action will be applied
     * @param <T> no requirements
     * @param action The action to be performed for each element
     * @throws NullPointerException if specified action is null
     */
    public static <T> void forEachN(
        final Collection<T> collection, final long count, final @NotNull Consumer<T> action) {
        if (collection == null || count < 0) {
            return;
        }

        forEachNUnchecked(collection.iterator(), count, action);
    }

    /**
     * @param iterator iterator to elements
     * @param count number of times action will be applied
     * @param <T> no requirements
     * @param action The action to be performed for each element
     * @throws NullPointerException if specified action or iterator are null
     */
    private static <T> void forEachNUnchecked(
        final Iterator<T> iterator, long count, final @NotNull Consumer<T> action) {
        while (count != 0) {
            action.accept(iterator.next());
            --count;
        }
    }

    /**
     * @param list list of elements
     * @param value value to find
     * @param <T> no requirements
     * @return index of value in list, if there is no value -1
     */
    static <T> int binarySearch(final List<? extends Comparable<T>> list, final T value) {
        if (list == null || list.isEmpty()) {
            return -1;
        }

        int low = 0;
        int high = list.size() - 1;
        int index = -1;
        while (low <= high) {
            final int mid = low + (high - low) / 2;
            if (list.get(mid).compareTo(value) < 0) {
                low = mid + 1;
            }
            else {
                high = mid - 1;
                index = mid;
            }
        }
        return index;
    }
}
