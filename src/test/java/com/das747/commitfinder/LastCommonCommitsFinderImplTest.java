package com.das747.commitfinder;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import com.das747.commitfinder.client.GitHubClient;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

public class LastCommonCommitsFinderImplTest {

    private static final String BRANCH_A = "branchA";
    private static final String BRANCH_B = "branchB";
    private static final String OWNER = "owner";
    private static final String REPO = "repo";

    public static class TestData {

        public Map<String, String> branches;
        public List<Commit> commits;
        public List<String> solution;
    }

    private TestData loadTestData(String file) throws IOException {
        String path = getClass().getClassLoader().getResource("testData/" + file).getPath();
        var json = new String(Files.readAllBytes(Paths.get(path)));
        var gson = new Gson();
        return gson.fromJson(json, TestData.class);
    }

    private @NotNull GitHubClient prepareMockClient(@NotNull TestData testData) throws IOException {
        var mockedClient = mock(GitHubClient.class);
        for (var commit : testData.commits) {
            when(mockedClient.getCommit(commit.sha)).thenReturn(commit);
        }
        var headA = mockedClient.getCommit(testData.branches.get(BRANCH_A));
        when(mockedClient.getHeadCommitSha(BRANCH_A)).thenReturn(headA.sha);
        var headB = mockedClient.getCommit(testData.branches.get(BRANCH_B));
        when(mockedClient.getHeadCommitSha(BRANCH_B)).thenReturn(headB.sha);
        return mockedClient;
    }

    private void doTest(String name) {
        try {
            var testData = loadTestData(name + ".json");
            var finder = new LastCommonCommitsFinderImpl(prepareMockClient(testData));
            var result = finder.findLastCommonCommits(BRANCH_A, BRANCH_B);
            assertEqualsNoOrder(result, testData.solution);
        } catch (IOException e) {
            assert false;
        }
    }

    @Test
    public void testFindLastCommonCommits() {
        doTest("TestGraph");
    }
}