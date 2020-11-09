package ru.test.cache.lfu;

import java.util.LinkedHashSet;


public class LFUFrequencyList<K> {
    private Item<K> first;

    Item<K> addFirst(K key) {
        if (first != null) {
            Item<K> previousFirst = this.first;
            first = new Item<>(1, null, previousFirst, key);
            previousFirst.previous = first;
            return first;
        }
        return (first = new Item<>(1, null, null, key));
    }

    Item<K> incrementReqCount(Item<K> item, K key) {
        long newRequestCount = item.requestCount + 1;
        Item<K> next = item.next;
        if (next != null) {
            if (next.requestCount == newRequestCount) {
                removeKeyFromSet(key, item);
                next.addValueToSet(key);
                return next;
            }
        }
        if (item.valueCount() == 1) {
            item.requestCount++;
            return item;
        }
        removeKeyFromSet(key, item);
        Item<K> newItem = new Item<>(newRequestCount, item, item.next, key);
        item.next = newItem;
        return newItem;
    }

    public void removeKeyFromSet(K key, Item<K> item) {
        item.keySet.remove(key);
        removeItemIfEmpty(item);
    }

    private void removeItemIfEmpty(Item<K> item) {
        if (item.valueCount() > 0) return;
        Item<K> next = item.next;
        Item<K> previous = item.previous;
        if (next != null) {
            next.previous = item.previous;
        }
        if (previous != null) {
            previous.next = item.next;
        }
        if (first == item) {
            first = previous != null ? previous : next;
        }

    }

    public Item<K> getFirst() {
        return first;
    }

    K removeLFUValue() {
        K lfuValue = first.keySet.iterator().next();
        first.keySet.remove(lfuValue);
        return lfuValue;
    }

    public static class Item<K> {
        private long requestCount;
        private LinkedHashSet<K> keySet;
        private Item<K> next;
        private Item<K> previous;

        Item(long requestCount, Item<K> previous, Item<K> next, K value) {
            this.requestCount = requestCount;
            this.next = next;
            this.previous = previous;
            this.keySet = new LinkedHashSet<>();
            keySet.add(value);
        }

        void addValueToSet(K value) {
            keySet.add(value);
        }

        int valueCount() {
            return keySet.size();
        }

        public long getRequestCount() {
            return requestCount;
        }
    }

}
