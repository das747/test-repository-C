package com.das747;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LastCommonCommitsFinderFactoryImpl implements LastCommonCommitsFinderFactory {

    @Override
    public LastCommonCommitsFinder create(String owner, String repo, String token) {
        var service = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubService.class);

        var cache = new LRUCommitCache(100);
        var client = new CachingGitHubClient(cache, service, owner, repo, token);
        return new LastCommonCommitsFinderImpl(client);
    }
}
