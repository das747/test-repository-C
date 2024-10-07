package com.das747;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public interface GitHubClient {

    @NotNull String getHeadCommitSha(@NotNull String branch) throws IOException;

    @NotNull Commit getCommit(@NotNull String sha) throws IOException;

}
