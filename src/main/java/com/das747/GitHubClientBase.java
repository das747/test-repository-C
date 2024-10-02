package com.das747;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;



abstract class GitHubClientBase implements GitHubClient {

    protected GitHubService service;
    protected String repoOwner;
    protected String repoName;
    protected String token;

    GitHubClientBase(GitHubService service, String repoOwner, String repoName, String token) {
        this.service = service;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.token = token;
    }


    @NotNull protected <T> T makeCall(@NotNull Call<T> call) throws IOException {
        var response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            throw new RuntimeException("Failure: " + response);
        }
    }

}
