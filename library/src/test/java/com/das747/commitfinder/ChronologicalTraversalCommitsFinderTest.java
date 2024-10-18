package com.das747.commitfinder;

import static org.testng.Assert.*;

import com.das747.commitfinder.api.LastCommonCommitsFinder;
import com.das747.commitfinder.client.GitHubClient;

public class ChronologicalTraversalCommitsFinderTest extends LastCommonCommitsFinderTestBase {

    @Override
    protected LastCommonCommitsFinder createFinder(GitHubClient client) {
        return new ChronologicalTraversalCommitsFinder(client);
    }
}