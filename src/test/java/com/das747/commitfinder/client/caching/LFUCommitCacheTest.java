package com.das747.commitfinder.client.caching;

import static org.testng.Assert.*;

import com.das747.commitfinder.Commit;
import org.testng.annotations.Test;

public class LFUCommitCacheTest extends CommitCacheTestBase {

    @Override
    protected CommitCache createCache(int maxSize) {
        return new LFUCommitCache(maxSize);
    }

    @Test
    public void evictionOrder() {
        var cache = createCache(2);
        var firstCommit = new Commit();
        var seocndCommit = new Commit();
        var thirdCommit = new Commit();

        assertNull(cache.put("1", firstCommit));
        assertNull(cache.put("2", seocndCommit));
        assertNull(cache.put("2", seocndCommit));

        assertEquals(cache.get("2"), seocndCommit);
        assertEquals(cache.get("2"), seocndCommit);
        assertEquals(cache.get("1"), firstCommit);

        assertEquals(cache.put("3", thirdCommit), firstCommit);
        assertEquals(cache.get("3"), thirdCommit);
    }
}
