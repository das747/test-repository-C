package com.das747.commitfinder.client;

import com.das747.commitfinder.service.GitHubService;
import java.io.IOException;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;


public abstract class GitHubClientBase implements GitHubClient {

    protected @NotNull GitHubService service;
    protected @NotNull String repoOwner;
    protected @NotNull String repoName;
    protected @Nullable String authorisation;

    protected GitHubClientBase(
        @NotNull GitHubService service,
        @NotNull String repoOwner,
        @NotNull String repoName,
        @Nullable String token
    ) {
        this.service = Objects.requireNonNull(service);
        this.repoOwner = Objects.requireNonNull(repoOwner);
        this.repoName = Objects.requireNonNull(repoName);
        this.authorisation = token == null ? null : "Bearer " + token;
    }


    @NotNull
    protected <T> T makeCall(@NotNull Call<T> call) throws IOException {
        var response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            throw new RuntimeException("Failure: " + response.code());
        }
    }

}
