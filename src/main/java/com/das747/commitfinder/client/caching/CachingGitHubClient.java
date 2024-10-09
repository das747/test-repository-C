package com.das747.commitfinder.client.caching;

import com.das747.commitfinder.Commit;
import com.das747.commitfinder.GitHubService;
import com.das747.commitfinder.client.GitHubClientBase;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

public class CachingGitHubClient extends GitHubClientBase {

    private Instant lastAccessTime = Instant.MIN;
    private final CommitCache cache;

    public CachingGitHubClient(
        CommitCache cache,
        GitHubService service,
        String repoOwner,
        String repoName,
        String token) {
        super(service, repoOwner, repoName, token);
        this.cache = cache;
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
        cache.put(response.commit.sha, response.commit);
        return response.commit.sha;
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
            cache.put(commit.sha, commit);
        }
        return response.get(0);
    }
}
