package com.das747.commitfinder.client;

import com.das747.commitfinder.service.GitHubService;
import com.das747.commitfinder.client.caching.CachingGitHubClient;
import com.das747.commitfinder.client.caching.CommitCacheFactory;
import com.das747.commitfinder.service.GitHubServiceFactory;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface GitHubClientFactory {

    static GitHubClient create(String owner, String repo, String token) {
        Logger logger = LoggerFactory.getLogger(GitHubClientFactory.class);

        var okHttpClient = new OkHttpClient();
        GitHubService service = GitHubServiceFactory.create(okHttpClient);

        var cacheSetting = System.getProperty("commitFinder.cache", "disable");
        switch (cacheSetting) {
            case "enable" -> {
                logger.info("Using caching client");
                var cache = CommitCacheFactory.create();
                return new CachingGitHubClient(cache, service, owner, repo, token, okHttpClient);
            }
            case "disable" -> {
                logger.info("Using default client");
                return new DefaultGitHubClient(service, owner, repo, token, okHttpClient);
            }
            default -> {
                logger.warn("Invalid cache setting '{}'. Cache is disabled.", cacheSetting);
                return new DefaultGitHubClient(service, owner, repo, token, okHttpClient);
            }
        }
    }
}
