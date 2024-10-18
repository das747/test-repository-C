package com.das747.commitfinder.client;

import com.das747.commitfinder.service.GitHubService;
import com.das747.commitfinder.client.caching.CachingGitHubClient;
import com.das747.commitfinder.client.caching.CommitCacheFactory;

public interface GitHubClientFactory {
    static GitHubClient create(GitHubService service, String owner, String repo, String token) {
        var cacheSetting = System.getProperty("commitFinder.cache", "disable");
        switch (cacheSetting) {
            case "enable" -> {
                var cache = CommitCacheFactory.create();
                return new CachingGitHubClient(cache, service, owner, repo, token);
            }
            case "disable" -> {
                return new DefaultGitHubClient(service, owner, repo, token);
            }
            default -> {
                System.err.println("Invalid cache setting '" + cacheSetting + "'. Cache is disabled.");
                return new DefaultGitHubClient(service, owner, repo, token);
            }
        }
    }
}
