package com.das747.commitfinder.client.caching;

import com.das747.commitfinder.Commit;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class CommitCacheBase implements CommitCache {
    @NotNull final protected Map<String, Commit> storage;
    final private int sizeLimit;

    CommitCacheBase(int sizeLimit, @NotNull Map<String, Commit> storage) {
        if (sizeLimit <= 0) {
            throw new IllegalArgumentException("Cache size limit should be > 0, got " + sizeLimit);
        }
        this.sizeLimit = sizeLimit;
        this.storage = Objects.requireNonNull(storage);
    }

    @Override
    public Commit put(@NotNull String sha, @NotNull Commit commit) {
        Commit evicted = null;
        if (!storage.containsKey(sha) && storage.size() == sizeLimit) {
            evicted = evictCommit();
        }
        storage.put(sha, commit);
        return evicted;
    }

    @Override
    public @Nullable Commit get(@NotNull String sha) {
        return storage.get(sha);
    }

    protected abstract @NotNull Commit evictCommit();
}
