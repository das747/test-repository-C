package com.das747.commitfinder.client;

import com.das747.commitfinder.GitHubService;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;



public abstract class GitHubClientBase implements GitHubClient {

    protected GitHubService service;
    protected String repoOwner;
    protected String repoName;
    protected String authorisation;

    protected GitHubClientBase(GitHubService service, String repoOwner, String repoName,
        String token) {
        this.service = service;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.authorisation = token == null ? null : "Bearer " + token;
    }


    @NotNull protected <T> T makeCall(@NotNull Call<T> call) throws IOException {
        var response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            throw new RuntimeException("Failure: " + response.code());
        }
    }

}
