package com.das747.commitfinder.client.caching;

import static org.testng.Assert.*;

import com.das747.commitfinder.Commit;
import org.testng.annotations.Test;

abstract class CommitCacheTestBase {
    protected abstract CommitCache createCache(int maxSize);

    protected Commit createDummyCommit() {
        return new Commit(null, null, null);
    }

    @Test
    public void testPutAndGet() {
        var cache = createCache(10);
        var commit = createDummyCommit();
        cache.put("1", commit);
        assertEquals(cache.get("1"), commit);
    }

    @Test
    public void testGetNonExisting() {
        var cache = createCache(10);
        assertNull(cache.get("1"));
    }

    @Test
    public void testInvalidMaxSize() {
        assertThrows(IllegalArgumentException.class, () -> createCache(0));
        assertThrows(IllegalArgumentException.class, () -> createCache(-10));
    }

    @Test
    public void testSizeOverflow() {
        int size = 10;
        var cache = createCache(size);
        var count = 0;
        for (int i = 0; i < size; i++) {
            assertNull(cache.put(String.valueOf(count++), createDummyCommit()));
        }
        for (int i = 0; i < size; i++) {
            assertNotNull(cache.put(String.valueOf(count++), createDummyCommit()));
        }
    }
}
