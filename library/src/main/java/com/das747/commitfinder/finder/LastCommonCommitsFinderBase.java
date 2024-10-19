package com.das747.commitfinder.finder;

import com.das747.commitfinder.api.LastCommonCommitsFinder;
import com.das747.commitfinder.client.GitHubClient;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

abstract class LastCommonCommitsFinderBase implements LastCommonCommitsFinder {

    private record CommitPair(@NotNull String sha1, @NotNull String sha2) {

        public CommitPair(String sha1, String sha2) {
            this.sha1 = sha1.compareTo(sha2) < 0 ? sha1 : sha2;
            this.sha2 = sha1.compareTo(sha2) < 0 ? sha2 : sha1;
        }
    }

    protected @NotNull GitHubClient client;

    private final @NotNull Map<CommitPair, Collection<String>> resultCache = new HashMap<>();

    LastCommonCommitsFinderBase(@NotNull GitHubClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public Collection<String> findLastCommonCommits(String branchA, String branchB)
        throws IOException {
        Objects.requireNonNull(branchA);
        Objects.requireNonNull(branchB);
        var headA = client.getHeadCommitSha(branchA);
        var headB = client.getHeadCommitSha(branchB);
        if (headA.equals(headB)) {
            return List.of(headA);
        }
        var pair = new CommitPair(headA, headB);
        return resultCache.computeIfAbsent(pair, k -> {
            try {
                return doFindLastCommonCommits(headA, headB);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void shutdown() {
        client.shutdown();
    }

    protected abstract @NotNull Collection<String> doFindLastCommonCommits(
        @NotNull String headA,
        @NotNull String headB
    ) throws IOException;
}
