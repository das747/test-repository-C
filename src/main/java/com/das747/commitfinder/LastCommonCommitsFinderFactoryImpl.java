package com.das747.commitfinder;

import com.das747.commitfinder.api.LastCommonCommitsFinder;
import com.das747.commitfinder.api.LastCommonCommitsFinderFactory;
import com.das747.commitfinder.client.GitHubClient;
import com.das747.commitfinder.client.GitHubClientFactory;
import com.das747.commitfinder.service.GitHubService;
import com.das747.commitfinder.service.GitHubServiceFactory;

public class LastCommonCommitsFinderFactoryImpl implements LastCommonCommitsFinderFactory {

    @Override
    public LastCommonCommitsFinder create(String owner, String repo, String token) {
        GitHubService service = GitHubServiceFactory.create();
        GitHubClient client = GitHubClientFactory.create(service, owner, repo, token);
        return new LastCommonCommitsFinderImpl(client);
    }
}
