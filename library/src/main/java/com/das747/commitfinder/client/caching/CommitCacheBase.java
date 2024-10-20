package com.das747.commitfinder.client.caching;

import com.das747.commitfinder.Commit;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class CommitCacheBase implements CommitCache {

    @NotNull
    final protected Map<String, Commit> storage;
    private final int sizeLimit;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    CommitCacheBase(int sizeLimit, @NotNull Map<String, Commit> storage) {
        if (sizeLimit <= 0) {
            var exception = new IllegalArgumentException(
                "Cache size limit should be > 0, got " + sizeLimit
            );
            logger.error("Invalid max size", exception);
            throw exception;
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
