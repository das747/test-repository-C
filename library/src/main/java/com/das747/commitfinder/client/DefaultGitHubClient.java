package com.das747.commitfinder.client;

import com.das747.commitfinder.Commit;
import com.das747.commitfinder.service.GitHubService;
import java.io.IOException;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

class DefaultGitHubClient extends GitHubClientBase {

    DefaultGitHubClient(
        @NotNull GitHubService service,
        @NotNull String repoOwner,
        @NotNull String repoName,
        @NotNull String token,
        @NotNull OkHttpClient client
    ) {
        super(service, repoOwner, repoName, token, client);
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
