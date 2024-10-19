package com.das747.commitfinder.api;

import com.das747.commitfinder.finder.ChronologicalTraversalCommitsFinder;
import com.das747.commitfinder.client.GitHubClient;
import com.das747.commitfinder.client.GitHubClientFactory;
import com.das747.commitfinder.service.GitHubService;
import com.das747.commitfinder.service.GitHubServiceFactory;

public class LastCommonCommitsFinderFactoryImpl implements LastCommonCommitsFinderFactory {

    @Override
    public LastCommonCommitsFinder create(String owner, String repo, String token) {
        GitHubService service = GitHubServiceFactory.create();
        GitHubClient client = GitHubClientFactory.create(service, owner, repo, token);
        return new ChronologicalTraversalCommitsFinder(client);
    }
}
