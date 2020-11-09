package ru.test.cache.test;


import org.junit.Assert;
import org.junit.Test;
import ru.test.cache.lfu.LFUCache;
import ru.test.cache.lfu.LFUFrequencyList;

import java.lang.reflect.Field;

public class LFUTest {

    private int requestCounter = 0;

    private LFUCache<String, String> cache = new LFUCache<>(key -> {
        requestCounter++;
        return key.toUpperCase();
    }, 3);

    @Test
    public void testPutThatReqCountRestartedAfterPutValueWithExistedKeyWithHighWeight() throws NoSuchFieldException, IllegalAccessException {
        cache.get("a");
        cache.get("a");
        cache.get("a");

        cache.put("a", "A");
        Assert.assertEquals(1, requestCounter);
        Assert.assertEquals(1, cache.size());

        Field f = cache.getClass().getDeclaredField("frequencyList");
        f.setAccessible(true);
        LFUFrequencyList frequencyList = (LFUFrequencyList) f.get(cache);
        LFUFrequencyList.Item first = frequencyList.getFirst();
        long requestCount = first.getRequestCount();
        Assert.assertEquals(1, requestCount);


        Assert.assertNotNull(cache.get("a"));
    }

    @Test
    public void testPut() {
        cache.put("a", "A");
        Assert.assertEquals(0, requestCounter);
        Assert.assertEquals(1, cache.size());
        Assert.assertNotNull(cache.get("a"));
        Assert.assertEquals(0, requestCounter);
        Assert.assertEquals(1, cache.size());
    }

    @Test
    public void testThatCachedDataWillNotBeRequestedFromSource() {
        cache.get("a");
        cache.get("a");
        Assert.assertEquals(1, requestCounter);
        cache.get("b");
        cache.get("b");
        Assert.assertEquals(2, requestCounter);
        cache.get("c");
        cache.get("c");
        Assert.assertEquals(3, requestCounter);
        cache.get("d");
        Assert.assertEquals(4, requestCounter);
    }

    @Test
    public void testSizeChanges() {
        cache.get("a");
        cache.get("a");
        Assert.assertEquals(1, cache.size());
        cache.get("b");
        Assert.assertEquals(2, cache.size());
        cache.get("c");
        Assert.assertEquals(3, cache.size());
        cache.get("d");
        Assert.assertEquals(3, cache.size());
    }

    @Test
    public void testThatOldestOneWithHighestWeightRemainInCacheWhileSecondOldestWillRemove() {
        cache.get("a");
        cache.get("a");
        cache.get("a");
        cache.get("b");
        cache.get("b");
        cache.get("c");
        cache.get("c");
        Assert.assertTrue(cache.contain("a"));
        Assert.assertTrue(cache.contain("b"));
        Assert.assertTrue(cache.contain("c"));

        cache.get("d");
        Assert.assertTrue(!cache.contain("b"));

        Assert.assertTrue(cache.contain("a"));
        Assert.assertTrue(cache.contain("c"));
        Assert.assertTrue(cache.contain("d"));
    }

    @Test
    public void testEvictionThatOldestOneWillBeRemovedIfAllEntriesHaveSameWeight() {
        cache.get("a");
        cache.get("a");
        cache.get("b");
        cache.get("b");
        cache.get("c");
        cache.get("c");
        Assert.assertTrue(cache.contain("a"));
        Assert.assertTrue(cache.contain("b"));
        Assert.assertTrue(cache.contain("c"));

        cache.get("d");
        Assert.assertTrue(!cache.contain("a"));

        Assert.assertTrue(cache.contain("b"));
        Assert.assertTrue(cache.contain("c"));
        Assert.assertTrue(cache.contain("d"));
    }

}
