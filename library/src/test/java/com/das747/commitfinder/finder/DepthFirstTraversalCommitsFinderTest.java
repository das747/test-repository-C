package com.das747.commitfinder.finder;

import com.das747.commitfinder.api.LastCommonCommitsFinder;
import com.das747.commitfinder.client.GitHubClient;

public class DepthFirstTraversalCommitsFinderTest extends LastCommonCommitsFinderTestBase {

    @Override
    protected LastCommonCommitsFinder createFinder(GitHubClient client) {
        return new DepthFirstTraversalCommitsFinder(client);
    }
}