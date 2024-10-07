package com.das747.commitfinder.client.caching;

import java.util.LinkedHashMap;

public class LRUCommitCache extends CommitCacheBase {
    public LRUCommitCache(int sizeLimit) {
        super(sizeLimit, new LinkedHashMap<>(16, 0.75f, true));
    }

    @Override
    protected String getVictim() {
        return storage.keySet().iterator().next();
    }
}
