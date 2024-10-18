package com.das747.commitfinder.client.caching;

import com.das747.commitfinder.Commit;
import org.jetbrains.annotations.NotNull;

public interface CommitCache {
    int DEFAULT_SIZE = 1000;

    Commit put(@NotNull String sha, @NotNull Commit commit);

    Commit get(@NotNull String sha);
}