package com.das747.commitfinder;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import com.das747.commitfinder.Commit.CommitData;
import com.das747.commitfinder.Commit.CommitData.AuthorData;
import com.das747.commitfinder.Commit.ParentData;
import com.das747.commitfinder.LastCommonCommitsFinderTest.TestData.TestCommitData;
import com.das747.commitfinder.client.GitHubClient;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

public class LastCommonCommitsFinderTest {

    private static final String BRANCH_A = "branchA";
    private static final String BRANCH_B = "branchB";
    private static final String OWNER = "owner";
    private static final String REPO = "repo";

    public static class TestData {
        public Map<String, String> branches;
        public List<TestCommitData> commits;
        public List<String> solution;

        static class TestCommitData {
            public String sha;
            public List<String> parents;
        }
    }

    private TestData loadTestData(String file) throws IOException {
        String path = getClass().getClassLoader().getResource("testData/" + file).getPath();
        var json = new String(Files.readAllBytes(Paths.get(path)));
        var gson = new Gson();
        return gson.fromJson(json, TestData.class);
    }

    private @NotNull Commit createCommit(TestCommitData commitData, Date timestamp) {
        var commit = new Commit();
        commit.sha = commitData.sha;
        commit.commit = new CommitData();
        commit.commit.author = new AuthorData();
        commit.commit.author.date = timestamp;
        commit.parents = commitData.parents.stream().map(sha -> {
            var d = new ParentData();
            d.sha = sha;
            return d;
        }).toList();
        return commit;
    }

    private @NotNull GitHubClient prepareMockClient(@NotNull TestData testData) throws IOException {
        var mockedClient = mock(GitHubClient.class);
        var timestamp = Instant.now();
        for (var commitData : testData.commits) {
            when(mockedClient.getCommit(commitData.sha)).thenReturn(createCommit(commitData, Date.from(timestamp)));
            timestamp = timestamp.plus(Duration.ofMinutes(1));
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
    public void simpleGraph() {
        doTest("simpleGraph");
    }

    @Test
    public void graphWithoutBranching() {
        doTest("straightGraph");
    }

    @Test
    public void branchesWithEqualHeads() {
        doTest("sameCommit");
    }

    @Test
    public void branchesWithMutualMerges() {
        doTest("mutualMerge");
    }

    @Test
    public void relatedCommonCommits() {
        doTest("relatedCommonCommits");
    }

}