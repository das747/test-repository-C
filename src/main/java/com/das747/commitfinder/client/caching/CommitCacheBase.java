package com.das747.commitfinder.client.caching;

import com.das747.commitfinder.Commit;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

abstract class CommitCacheBase implements CommitCache {
    final protected Map<String, Commit> storage;
    final private int sizeLimit;

    CommitCacheBase(int sizeLimit, Map<String, Commit> storage) {
        if (sizeLimit <= 0) {
            throw new IllegalArgumentException("Cache size limit should be > 0, got " + sizeLimit);
        }
        this.sizeLimit = sizeLimit;
        this.storage = storage;
    }

    @Override
    public Commit put(@NotNull String sha, @NotNull Commit commit) {
        Commit evicted = null;
        if (storage.size() == sizeLimit) {
            String victimSha = getVictim();
            evicted = storage.remove(victimSha);
        }
        storage.put(sha, commit);
        return evicted;
    }

    @Override
    public Commit get(@NotNull String sha) {
        return storage.get(sha);
    }

    protected abstract String getVictim();
}
