package com.das747;

import java.util.LinkedHashMap;

public class LRUCommitCache extends CommitCacheBase {
    LRUCommitCache(int sizeLimit) {
        super(sizeLimit, new LinkedHashMap<>(16, 0.75f, true));
    }

    @Override
    protected String getVictim() {
        return storage.keySet().iterator().next();
    }
}
