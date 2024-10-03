package com.das747;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class DefaultGitHubClient extends GitHubClientBase {

    DefaultGitHubClient(GitHubService service, String repoOwner, String repoName, String token) {
        super(service, repoOwner, repoName, token);
    }

    @Override
    public @NotNull Commit getHeadCommit(@NotNull String branch) throws IOException {
        var call = service.getBranch(repoOwner, repoName, branch, authorisation);
        return makeCall(call).commit;
    }

    @Override
    public @NotNull Commit getCommit(@NotNull String sha) throws IOException {
        var commits = makeCall(service.listCommits(repoOwner, repoName, sha, authorisation));
        return commits.get(0);
    }

}
