package com.das747.commitfinder.api;

import com.das747.commitfinder.finder.ChronologicalTraversalCommitsFinder;
import com.das747.commitfinder.client.GitHubClient;
import com.das747.commitfinder.client.GitHubClientFactory;
import com.das747.commitfinder.finder.DepthFirstTraversalCommitsFinder;
import com.das747.commitfinder.service.GitHubService;
import com.das747.commitfinder.service.GitHubServiceFactory;

public class LastCommonCommitsFinderFactoryImpl implements LastCommonCommitsFinderFactory {

    @Override
    public LastCommonCommitsFinder create(String owner, String repo, String token) {
        GitHubService service = GitHubServiceFactory.create();
        GitHubClient client = GitHubClientFactory.create(service, owner, repo, token);

        var algorithmSetting = System.getProperty("commitFinder.algorithm", "chrono");

        switch (algorithmSetting) {
            case "chrono" -> {
                return new ChronologicalTraversalCommitsFinder(client);
            }
            case "dfs" -> {
                return new DepthFirstTraversalCommitsFinder(client);
            }
            default -> {
                System.err.println(
                    "Invalid algorithm setting '" + algorithmSetting + "'. Using 'chrono'."
                );
                return new ChronologicalTraversalCommitsFinder(client);
            }
        }

    }
}
