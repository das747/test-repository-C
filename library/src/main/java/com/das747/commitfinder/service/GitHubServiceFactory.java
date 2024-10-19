package com.das747.commitfinder.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import java.time.Instant;
import okhttp3.OkHttpClient;
import okhttp3.internal.tls.OkHostnameVerifier;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface GitHubServiceFactory {

    static @NotNull GitHubService create(OkHttpClient client) {
        var instantAdapter = (JsonDeserializer<Instant>) (json, typeOfT, context) ->
            Instant.parse(json.getAsString());
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, instantAdapter)
            .create();

        return new Retrofit.Builder()
            .client(client)
            .baseUrl(GitHubService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GitHubService.class);
    }
}
