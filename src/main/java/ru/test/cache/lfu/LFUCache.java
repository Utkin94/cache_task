package ru.test.cache.lfu;

import ru.test.cache.Cache;

import java.util.HashMap;
import java.util.Map;

public class LFUCache<K, V> extends Cache<K, V> {
    private LFUFrequencyList<K> frequencyList;
    private Map<K, Element<V>> data;

    public LFUCache(Cache.DataRetriever<K, V> dataRetriever, int capacity) {
        super(dataRetriever, capacity);
        data = new HashMap<>(capacity, 1F);
        frequencyList = new LFUFrequencyList<>();
    }

    @Override
    public V get(K key) {
        V value = null;
        Element<V> element = data.get(key);
        if (element == null) {
            value = dataRetriever.getData(key);
            removeLFUElementIfFull();
            LFUFrequencyList.Item<K> firstFreqItem = getOrCreateInitialFreqItem(key);
            createNewElement(key, value, firstFreqItem);
        } else {
            value = element.getValue();
            incrementFrequency(element, key);
        }
        return value;

    }

    @Override
    public V put(K key, V value) {
        Element<V> element = data.get(key);
        if (element != null) {
            data.remove(key);
            size--;
            frequencyList.removeKeyFromSet(key, element.item);
        } else if (size == capacity) {
            removeLFUElementIfFull();
        }
        LFUFrequencyList.Item<K> firstFreqItem = getOrCreateInitialFreqItem(key);
        createNewElement(key, value, firstFreqItem);
        return value;
    }

    @Override
    public boolean contain(K key) {
        return data.containsKey(key);
    }

    private void removeLFUElementIfFull() {
        if (size == capacity) {
            K lfuKey = frequencyList.removeLFUValue();
            data.remove(lfuKey);
            size--;
        }
    }

    private void incrementFrequency(Element<V> element, K key) {
        element.item = frequencyList.incrementReqCount(element.item, key);
    }

    private LFUFrequencyList.Item<K> getOrCreateInitialFreqItem(K key) {
        LFUFrequencyList.Item<K> first = frequencyList.getFirst();
        if (first != null && first.getRequestCount() == 1) {
            return first;
        }
        return frequencyList.addFirst(key);
    }


    private void createNewElement(K key, V value, LFUFrequencyList.Item<K> firstFreqItem) {
        Element<V> newElement = new Element<>(value, firstFreqItem);
        firstFreqItem.addValueToSet(key);
        data.put(key, newElement);
        size++;
    }

    private class Element<V> {
        private V value;
        private LFUFrequencyList.Item<K> item;

        Element(V value, LFUFrequencyList.Item<K> item) {
            this.value = value;
            this.item = item;
        }

        V getValue() {
            return value;
        }
    }


}
