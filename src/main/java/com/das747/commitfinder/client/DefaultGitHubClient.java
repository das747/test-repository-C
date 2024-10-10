package com.das747.commitfinder.client;

import com.das747.commitfinder.Commit;
import com.das747.commitfinder.service.GitHubService;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class DefaultGitHubClient extends GitHubClientBase {

    DefaultGitHubClient(GitHubService service, String repoOwner, String repoName, String token) {
        super(service, repoOwner, repoName, token);
    }

    @Override
    public @NotNull String getHeadCommitSha(@NotNull String branch) throws IOException {
        var branchData = makeCall(service.getBranch(repoOwner, repoName, branch, authorisation));
        return branchData.commit().sha();
    }

    @Override
    public @NotNull Commit getCommit(@NotNull String sha) throws IOException {
        return makeCall(service.getCommit(repoOwner, repoName, sha, authorisation));
    }

}
