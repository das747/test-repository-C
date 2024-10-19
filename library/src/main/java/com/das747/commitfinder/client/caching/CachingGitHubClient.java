package com.das747.commitfinder.client.caching;

import com.das747.commitfinder.Commit;
import com.das747.commitfinder.service.GitHubService;
import com.das747.commitfinder.client.GitHubClientBase;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CachingGitHubClient extends GitHubClientBase {

    private Instant lastAccessTime = Instant.MIN;
    private final CommitCache cache;

    public CachingGitHubClient(
        @NotNull CommitCache cache,
        @NotNull GitHubService service,
        @NotNull String repoOwner,
        @NotNull String repoName,
        @Nullable String token
    ) {
        super(service, repoOwner, repoName, token);
        this.cache = Objects.requireNonNull(cache);
    }

    private boolean accessIsValid() {
        return lastAccessTime.isAfter(Instant.now().minus(Duration.ofMinutes(15)));
    }

    private void updateAccessTime() {
        lastAccessTime = Instant.now();
    }

    @Override
    public @NotNull String getHeadCommitSha(@NotNull String branch) throws IOException {
        var response = makeCall(service.getBranch(repoOwner, repoName, branch, authorisation));
        cache.put(response.commit().sha(), response.commit());
        return response.commit().sha();
    }

    @Override
    public @NotNull Commit getCommit(@NotNull String sha) throws IOException {
        if (accessIsValid()) {
            var commit = cache.get(sha);
            if (commit != null) {
                return commit;
            }
        }
        var response = makeCall(service.listCommits(repoOwner, repoName, sha, authorisation));
        updateAccessTime();
        for (var commit : response) {
            cache.put(commit.sha(), commit);
        }
        return response.get(0);
    }
}
