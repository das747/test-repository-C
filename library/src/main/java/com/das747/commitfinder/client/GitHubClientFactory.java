package com.das747.commitfinder.client;

import com.das747.commitfinder.service.GitHubService;
import com.das747.commitfinder.client.caching.CachingGitHubClient;
import com.das747.commitfinder.client.caching.CommitCacheFactory;
import com.das747.commitfinder.service.GitHubServiceFactory;
import okhttp3.OkHttpClient;

public interface GitHubClientFactory {
    static GitHubClient create(String owner, String repo, String token) {
        var okHttpClient = new OkHttpClient();
        GitHubService service = GitHubServiceFactory.create(okHttpClient);

        var cacheSetting = System.getProperty("commitFinder.cache", "disable");
        switch (cacheSetting) {
            case "enable" -> {
                var cache = CommitCacheFactory.create();
                return new CachingGitHubClient(cache, service, owner, repo, token, okHttpClient);
            }
            case "disable" -> {
                return new DefaultGitHubClient(service, owner, repo, token, okHttpClient);
            }
            default -> {
                System.err.println("Invalid cache setting '" + cacheSetting + "'. Cache is disabled.");
                return new DefaultGitHubClient(service, owner, repo, token, okHttpClient);
            }
        }
    }
}
