package com.das747.commitfinder;


import static com.das747.commitfinder.DepthFirstTraversalCommitsFinder.CommitColors.*;

import com.das747.commitfinder.api.LastCommonCommitsFinder;
import com.das747.commitfinder.client.GitHubClient;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DepthFirstTraversalCommitsFinder implements LastCommonCommitsFinder {

    private static class ExecutionState {
        private final Map<String, CommitColors> commitColors = new HashMap<>();
        private final Set<String> result = new HashSet<>();
    }

    protected enum CommitColors {
        BRANCH_A,
        BRANCH_B,
        COMMON,
        UNASSIGNED
    }

    private final GitHubClient client;

    private ExecutionState state;

    DepthFirstTraversalCommitsFinder(GitHubClient client) {
        this.client = client;
    }



    @Override
    public Collection<String> findLastCommonCommits(String branchA, String branchB)
        throws IOException {
        state = new ExecutionState();
        var headA = client.getHeadCommitSha(branchA);
        var headB = client.getHeadCommitSha(branchB);
        if (Objects.equals(headA, headB)) {
            return List.of(headA);
        }
        visitCommit(headA, BRANCH_A);
        visitCommit(headB, BRANCH_B);
        return state.result;
    }

    private void visitCommit(String sha, CommitColors color) throws IOException {
        var commit = client.getCommit(sha);
        var currentColor = state.commitColors.getOrDefault(sha, UNASSIGNED);
        switch (currentColor) {
            case UNASSIGNED -> state.commitColors.put(sha, color);
            case COMMON -> {
                if (color == COMMON) {
                    state.result.remove(sha);
                }
                return;
            }
            default -> {
                if (currentColor != color && color != COMMON) {
                    state.result.add(sha);
                    state.commitColors.put(sha, COMMON);
                }
            }
        }
        currentColor = state.commitColors.get(sha);
        for (var parent: commit.parents()) {
            visitCommit(parent.sha(), currentColor);
        }
    }
}
