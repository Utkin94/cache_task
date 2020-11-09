package ru.test.cache;


/**
 *
 * @author Nikita Utkin
 * Date:   26.05.2019
 */
public abstract class Cache<K, V> {
    protected DataRetriever<K, V> dataRetriever;
    protected int capacity;
    protected int size;

    public Cache(DataRetriever<K, V> dataRetriever, int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity of cache must be at least 1");
        }
        this.dataRetriever = dataRetriever;
        this.capacity = capacity;
    }

    abstract public V get(K key);

    abstract public V put(K key, V value);

    public int size() {
        return size;
    }

    public abstract boolean contain(K key);

    @FunctionalInterface
    public interface DataRetriever<K, V> {
        V getData(K key);
    }
}
