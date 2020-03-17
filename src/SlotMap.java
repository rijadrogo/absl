// This is a personal academic project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++, C#, and Java:
// http://www.viva64.com

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

public final class SlotMap<K> {
    // Class invariant:
    // Either next_available_slot_index_ == last_available_slot_index_ ==
    // slots_.size(), or else 0 <= next_available_slot_index_ < slots_.size() and
    // the "key" of that slot entry points to the subsequent available slot, and
    // so on, until reaching last_available_slot_index_ (which might equal
    // next_available_slot_index_ if there is only one available slot at the
    // moment).
    private final ArrayList<Pair<Integer, Integer>> slots;
    private final ArrayList<Integer> reverseMap;
    private final ArrayList<K> values;
    private int nextAvailableSlotIndex;
    private int lastAvailableSlotIndex;

    public SlotMap() {
        slots = new ArrayList<>();
        reverseMap = new ArrayList<>();
        values = new ArrayList<>();
    }

    public SlotMap(final int initialCapacity) {
        slots = new ArrayList<>();
        reserveSlots(initialCapacity);
        reverseMap = new ArrayList<>(initialCapacity);
        values = new ArrayList<>(initialCapacity);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public int size() {
        return values.size();
    }

    public K get(Pair<Integer, Integer> slotIndex) {
        return values.get(findUnchecked(slotIndex));
    }

    public int findUnchecked(final Pair<Integer, Integer> key) {
        final Pair<Integer, Integer> slot = slots.get(getIndex(key));
        return getIndex(slot);
    }

    public void ensuresCapacity(final int minCapacity) {
        values.ensureCapacity(minCapacity);
        reverseMap.ensureCapacity(minCapacity);
        reserveSlots(minCapacity);
    }

    public void reserveSlots(int minCapacity) {
        slots.ensureCapacity(minCapacity);
        final int originalNumSlots = slots.size();
        if (originalNumSlots < minCapacity) {
            slots.add(Pair.of(nextAvailableSlotIndex, 0));
            int lastNewSlot = originalNumSlots;
            --minCapacity;
            while (lastNewSlot != minCapacity) {
                slots.add(Pair.of(lastNewSlot, 0));
                ++lastNewSlot;
            }
            nextAvailableSlotIndex = lastNewSlot;
        }
    }

    public int slotCount() {
        return slots.size();
    }

    public @NotNull Pair<Integer, Integer> put(final K key) {
        final int valuePos = values.size();
        values.add(key);
        reverseMap.add(nextAvailableSlotIndex);
        if (nextAvailableSlotIndex == slots.size()) {
            final int idx = nextAvailableSlotIndex + 1;
            slots.add(Pair.of(idx, 0));
            lastAvailableSlotIndex = idx;
        }
        final Pair<Integer, Integer> slotValue = slots.get(nextAvailableSlotIndex);
        final int saveNextAvailableSlotIndex = nextAvailableSlotIndex;
        if (nextAvailableSlotIndex == lastAvailableSlotIndex) {
            nextAvailableSlotIndex = slots.size();
            lastAvailableSlotIndex = nextAvailableSlotIndex;
        }
        else {
            nextAvailableSlotIndex = getIndex(slotValue);
        }
        slotValue.setFirst(valuePos);
        return Pair.of(saveNextAvailableSlotIndex, slotValue.getSecond());
    }

    public void clear() {
        slots.clear();
        reverseMap.clear();
        values.clear();
        nextAvailableSlotIndex = 0;
        lastAvailableSlotIndex = 0;
    }

    public @NotNull Optional<Integer> remove(final int from, int to) {
        if (from >= to) {
            return Optional.empty();
        }
        while (to != from) {
            --to;
            this.remove(to);
        }
        return Optional.of(from);
    }

    public Integer remove(final int index) {
        final int slotIndex = slotFromValueIndex(index);
        return removeSlot(slotIndex);
    }

    public @NotNull Optional<Integer> remove(final Pair<Integer, Integer> value) {
        return find(value).map(this::remove);
    }

    public @NotNull Optional<Integer> find(final Pair<Integer, Integer> key) {
        final Integer slotIndex = getIndex(key);
        if (slotIndex >= slots.size()) {
            return Optional.empty();
        }
        final Pair<Integer, Integer> slotValue = slots.get(slotIndex);
        if (!getGeneration(slotValue).equals(getGeneration(key))) {
            return Optional.empty();
        }
        return Optional.of(getIndex(slotValue));
    }

    public boolean removeIf(final Predicate<K> predicate) {
        int i = 0;
        boolean removed = false;
        for (K elem : values) {
            if (predicate.test(elem)) {
                removed = true;
                this.remove(i);
            }
            ++i;
        }
        return removed;
    }

    private Integer removeSlot(final int index) {
        final int valueIndex = getIndex(slots.get(index));
        final int valueBackIndex = values.size() - 1;
        if (valueIndex != valueBackIndex) {
            final int slotBackIndex = slotFromValueIndex(valueBackIndex);
            values.set(valueIndex, values.get(valueBackIndex));
            slots.get(slotBackIndex).setFirst(valueIndex);
            reverseMap.set(valueIndex, slotBackIndex);
        }
        values.remove(values.size() - 1);
        reverseMap.remove(reverseMap.size() - 1);
        if (nextAvailableSlotIndex == slots.size()) {
            nextAvailableSlotIndex = index;
        }
        else {
            final Pair<Integer, Integer> slotValue = slots.get(lastAvailableSlotIndex);
            slots.get(lastAvailableSlotIndex).setFirst(slotValue.getFirst());
        }
        lastAvailableSlotIndex = index;

        final Pair<Integer, Integer> slotValue = slots.get(index);
        slots.get(index).setFirst(getGeneration(slotValue) + 1);
        return valueIndex;
    }

    private Integer getIndex(final @NotNull Pair<Integer, Integer> integerPair) {
        return integerPair.getFirst();
    }

    private int slotFromValueIndex(final int valueIndex) {
        return reverseMap.get(valueIndex);
    }

    private Integer getGeneration(final @NotNull Pair<Integer, Integer> integerPair) {
        return integerPair.getSecond();
    }

    public @NotNull Iterator<K> iterator() {
        return values.iterator();
    }

    public Spliterator<K> spliterator() {
        return values.spliterator();
    }

    public void forEach(final Consumer<? super K> consumer) {
        values.forEach(consumer);
    }
}
