package ru.test.cache.lru;

import ru.test.cache.Cache;

import java.util.LinkedHashMap;

public class LRUCache<K, V> extends Cache<K, V> {
    private LinkedHashMap<K, V> data;

    public LRUCache(DataRetriever<K, V> dataRetriever, int capacity) {
        super(dataRetriever, capacity);
        data = new LinkedHashMap<>(capacity, 1F, true);
    }

    @Override
    public V get(K key) {
        V val = data.get(key);
        if (val == null) {
            val = dataRetriever.getData(key);
            if (size == capacity) {
                removeOneFromCachedData();
            }
            data.put(key, val);
            size++;
        }
        return val;
    }

    @Override
    public V put(K key, V value) {
        if (data.containsKey(key)) {
            data.remove(key);
            size--;
        } else if (size == capacity) {
            removeOneFromCachedData();
        }
        V newVal = data.put(key, value);
        size++;
        return newVal;
    }

    @Override
    public boolean contain(K key) {
        return data.containsKey(key);
    }

    private void removeOneFromCachedData() {
        K key = data.entrySet().iterator().next().getKey();
        data.remove(key);
        size--;
    }
}
