package ru.test.cache.test;

import org.junit.Assert;
import org.junit.Test;
import ru.test.cache.lru.LRUCache;

public class LRUTest {
    private int requestCounter = 0;

    private LRUCache<String, String> cache = new LRUCache<>(key -> {
        requestCounter++;
        return key.toUpperCase();
    }, 3);

    @Test
    public void testIfGetWithSameParameterGetDataFromSourceOnlyOnce() {
        cache.get("a");
        cache.get("a");
        cache.get("a");
        Assert.assertEquals(1, requestCounter);
    }

    @Test
    public void testEvictionThatOldestOneWillBeRemoved() {
        cache.get("a");
        cache.get("b");
        cache.get("c");
        cache.get("a");
        cache.get("d");

        Assert.assertTrue(cache.contain("a"));
        Assert.assertTrue(cache.contain("c"));
        Assert.assertTrue(cache.contain("d"));
        Assert.assertFalse(cache.contain("b"));
    }

    @Test
    public void testPut() {
        cache.put("a", "A");
        Assert.assertEquals(1, cache.size());
        Assert.assertEquals("A", cache.get("a"));
        Assert.assertEquals(0, requestCounter);
    }

    @Test
    public void testPutAfterGet() {
        cache.get("a");
        cache.get("b");
        cache.put("a", "S");

        Assert.assertEquals(2, cache.size());
        Assert.assertEquals("S", cache.get("a"));
        Assert.assertEquals(2, cache.size());
    }

    @Test
    public void testPutWhenFull() {
        cache.get("a");
        cache.get("b");
        cache.get("c");
        cache.put("d", "D");

        Assert.assertEquals(3, cache.size());
        Assert.assertEquals("D", cache.get("d"));
        Assert.assertFalse(cache.contain("a"));
        Assert.assertEquals(3, cache.size());
    }
}
