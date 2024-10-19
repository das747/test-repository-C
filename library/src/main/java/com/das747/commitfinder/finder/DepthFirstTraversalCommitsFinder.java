package com.das747.commitfinder.finder;


import static com.das747.commitfinder.finder.DepthFirstTraversalCommitsFinder.CommitColors.*;

import com.das747.commitfinder.client.GitHubClient;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class DepthFirstTraversalCommitsFinder extends LastCommonCommitsFinderBase {

    private static class ExecutionState {

        private final @NotNull Map<String, CommitColors> commitColors = new HashMap<>();
        private final @NotNull Set<String> result = new HashSet<>();
    }

    protected enum CommitColors {
        BRANCH_A,
        BRANCH_B,
        COMMON,
        UNASSIGNED
    }

    private ExecutionState state;

    public DepthFirstTraversalCommitsFinder(@NotNull GitHubClient client) {
        super(client);
    }

    @Override
    protected @NotNull Collection<String> doFindLastCommonCommits(
        @NotNull String headA,
        @NotNull String headB
    ) throws IOException {
        state = new ExecutionState();
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
        for (var parent : commit.parents()) {
            visitCommit(parent.sha(), currentColor);
        }
    }
}
