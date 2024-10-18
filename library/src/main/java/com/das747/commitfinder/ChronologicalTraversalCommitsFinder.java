package com.das747.commitfinder;

import static com.das747.commitfinder.ChronologicalTraversalCommitsFinder.CommitColor.*;

import com.das747.commitfinder.api.LastCommonCommitsFinder;
import com.das747.commitfinder.client.GitHubClient;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;


public class ChronologicalTraversalCommitsFinder implements LastCommonCommitsFinder {

    private class ExecutionState {

        private final Queue<Commit> queue = new PriorityQueue<>(
            Comparator.comparing((Commit c) -> c.commit().author().date()).reversed());
        private final Map<String, CommitColor> colors = new HashMap<>();
        private final Map<CommitColor, Integer> queuedColorsCount = new HashMap<>();

        private CommitColor getColor(String sha) {
            return colors.getOrDefault(sha, UNASSIGNED);
        }

        private Commit dequeueCommit() {
            var commit = queue.remove();
            queuedColorsCount.computeIfPresent(getColor(commit.sha()), (c, n) -> n - 1);
            return commit;
        }

        private void enqueueCommit(String sha, CommitColor color) throws IOException {
            queue.add(client.getCommit(sha));
            changeColor(sha, color);
        }

        private void changeColor(String sha, CommitColor newColor) {
            var oldColor = getColor(sha);
            queuedColorsCount.computeIfPresent(oldColor, (c, n) -> n - 1);
            colors.put(sha, newColor);
            queuedColorsCount.merge(newColor, 1, (c, n) -> n + 1);
        }

        private boolean shouldContinueSearch() {
            return queuedColorsCount.getOrDefault(COMMON, 0) > 1
                || (queuedColorsCount.getOrDefault(BRANCH_A, 0) > 0
                && queuedColorsCount.getOrDefault(BRANCH_B, 0) > 0);
        }
    }

    protected enum CommitColor {
        BRANCH_A,
        BRANCH_B,
        COMMON,
        UNASSIGNED
    }

    private final GitHubClient client;

    public ChronologicalTraversalCommitsFinder(GitHubClient client) {
        this.client = client;
    }


    @Override
    public Collection<String> findLastCommonCommits(String branchA, String branchB)
        throws IOException {
        ExecutionState state = new ExecutionState();
        var headA = client.getHeadCommitSha(branchA);
        var headB = client.getHeadCommitSha(branchB);
        if (Objects.equals(headA, headB)) {
            return List.of(headA);
        }
        state.enqueueCommit(headA, BRANCH_A);
        state.enqueueCommit(headB, BRANCH_B);
        Set<String> result = new HashSet<>();
        while (state.shouldContinueSearch()) {
            var currentCommit = state.dequeueCommit();
            var currentColor = state.getColor(currentCommit.sha());
            for (var parent : currentCommit.parents()) {
                var parentColor = state.getColor(parent.sha());
                switch (parentColor) {
                    case UNASSIGNED -> state.enqueueCommit(parent.sha(), currentColor);
                    case COMMON -> {
                        if (currentColor == COMMON) {
                            result.remove(parent.sha());
                        }
                    }
                    default -> {
                        if (parentColor != currentColor) {
                            result.add(parent.sha());
                            state.changeColor(parent.sha(), COMMON);
                        }
                    }
                }
            }
            System.out.println("Processed " + currentCommit.sha());
        }
        return result;
    }
}
