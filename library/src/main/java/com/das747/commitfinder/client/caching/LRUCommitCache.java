package com.das747.commitfinder.client.caching;

import com.das747.commitfinder.Commit;
import java.util.LinkedHashMap;
import org.jetbrains.annotations.NotNull;

class LRUCommitCache extends CommitCacheBase {

    public LRUCommitCache(int sizeLimit) {
        super(sizeLimit, new LinkedHashMap<>(16, 0.75f, true));
    }

    @Override
    protected @NotNull Commit evictCommit() {
        var iterator = storage.entrySet().iterator();
        var victim = iterator.next();
        iterator.remove();
        return victim.getValue();
    }
}
