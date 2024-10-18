package com.das747.commitfinder.client.caching;

import com.das747.commitfinder.Commit;
import java.util.LinkedHashMap;

class LRUCommitCache extends CommitCacheBase {

    public LRUCommitCache(int sizeLimit) {
        super(sizeLimit, new LinkedHashMap<>(16, 0.75f, true));
    }

    @Override
    protected Commit evictCommit() {
        var iterator = storage.entrySet().iterator();
        var victim = iterator.next();
        iterator.remove();
        return victim.getValue();
    }
}
