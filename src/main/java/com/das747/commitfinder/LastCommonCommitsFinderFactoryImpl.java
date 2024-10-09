package com.das747.commitfinder;

import com.das747.commitfinder.api.LastCommonCommitsFinder;
import com.das747.commitfinder.api.LastCommonCommitsFinderFactory;
import com.das747.commitfinder.client.caching.CachingGitHubClient;
import com.das747.commitfinder.client.caching.LFUCommitCache;
import com.das747.commitfinder.client.caching.LRUCommitCache;
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

        var cache = new LRUCommitCache(10);
        var client = new CachingGitHubClient(cache, service, owner, repo, token);
        return new LastCommonCommitsFinderImpl(client);
    }
}
