package com.das747.commitfinder.api;

import com.das747.commitfinder.finder.ChronologicalTraversalCommitsFinder;
import com.das747.commitfinder.client.GitHubClient;
import com.das747.commitfinder.client.GitHubClientFactory;
import com.das747.commitfinder.finder.DepthFirstTraversalCommitsFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastCommonCommitsFinderFactoryImpl implements LastCommonCommitsFinderFactory {

    private static final Logger logger = LoggerFactory.getLogger(
        LastCommonCommitsFinderFactoryImpl.class
    );

    @Override
    public LastCommonCommitsFinder create(String owner, String repo, String token) {

        GitHubClient client = GitHubClientFactory.create(owner, repo, token);

        var algorithmSetting = System.getProperty("commitFinder.algorithm", "chrono");

        switch (algorithmSetting) {
            case "chrono" -> {
                logger.info("Using 'chrono' finder algorithm");
                return new ChronologicalTraversalCommitsFinder(client);
            }
            case "dfs" -> {
                logger.info("Using 'dfs' finder algorithm");
                return new DepthFirstTraversalCommitsFinder(client);
            }
            default -> {
                logger.warn("Invalid algorithm setting '{}'. Using 'chrono'.", algorithmSetting);
                return new ChronologicalTraversalCommitsFinder(client);
            }
        }

    }
}
