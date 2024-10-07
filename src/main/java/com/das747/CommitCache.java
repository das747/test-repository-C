package com.das747;

import org.jetbrains.annotations.NotNull;

interface CommitCache {

    Commit put(@NotNull String sha, @NotNull Commit commit);

    Commit get(@NotNull String sha);
}