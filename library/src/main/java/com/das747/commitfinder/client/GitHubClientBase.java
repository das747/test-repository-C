package com.das747.commitfinder.client;

import com.das747.commitfinder.service.GitHubService;
import java.io.IOException;
import java.util.Objects;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;


public abstract class GitHubClientBase implements GitHubClient {

    protected final @NotNull GitHubService service;
    protected final @NotNull String repoOwner;
    protected final @NotNull String repoName;
    protected final @Nullable String authorisation;
    private final  @NotNull OkHttpClient client;

    private boolean isShutDown = false;

    protected GitHubClientBase(
        @NotNull GitHubService service,
        @NotNull String repoOwner,
        @NotNull String repoName,
        @Nullable String token,
        @NotNull OkHttpClient client
    ) {
        this.service = Objects.requireNonNull(service);
        this.repoOwner = Objects.requireNonNull(repoOwner);
        this.repoName = Objects.requireNonNull(repoName);
        this.authorisation = token == null ? null : "Bearer " + token;
        this.client = Objects.requireNonNull(client);
    }


    @NotNull
    protected <T> T makeCall(@NotNull Call<T> call) throws IOException {
        assert !isShutDown;
        var response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            throw new RuntimeException("Failure: " + response.code());
        }
    }

    @Override
    public void shutdown() {
        assert !isShutDown;
        // https://github.com/square/retrofit/issues/3144
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        isShutDown = true;
    }
}
