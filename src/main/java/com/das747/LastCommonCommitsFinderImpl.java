package com.das747;

import static com.das747.LastCommonCommitsFinderImpl.CommitColor.*;

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


public class LastCommonCommitsFinderImpl implements LastCommonCommitsFinder {
    GitHubClient client;

    LastCommonCommitsFinderImpl(GitHubClient client) {
        this.client = client;
    }

    protected enum CommitColor {
        BRANCH_A,
        BRANCH_B,
        COMMON,
        UNASSIGNED
    }

    @Override
    public Collection<String> findLastCommonCommits(String branchA, String branchB)
        throws IOException {
        var headA = client.getHeadCommit(branchA);
        var headB = client.getHeadCommit(branchB);
        if (Objects.equals(headA.sha, headB.sha)) {
            return List.of(headA.sha);
        }
        Map<String, CommitColor> colors = new HashMap<>();
        colors.put(headA.sha, BRANCH_A);
        colors.put(headB.sha, BRANCH_B);
        Queue<Commit> queue = new PriorityQueue<>(Comparator.comparing(c -> c.commit.author.date));
        queue.add(headA);
        queue.add(headB);
        int commonCounter = 0;
        Set<String> result = new HashSet<>();
        while (queue.size() > commonCounter) {
            var currentCommit = queue.remove();
            var currentColor = colors.get(currentCommit.sha);
            if (currentColor == COMMON) {
                commonCounter--;
            }
            for (var parent: currentCommit.parents) {
                var parentColor = colors.getOrDefault(parent.sha, UNASSIGNED);
                switch (parentColor) {
                    case UNASSIGNED -> {
                        colors.put(parent.sha, currentColor);
                        // TODO: this should be async
                        queue.add(client.getCommit(parent.sha));
                    }
                    case COMMON -> {
                        if (currentColor == COMMON) {
                            result.remove(parent.sha);
                        }
                    }
                    default -> {
                        if (currentColor != parentColor) {
                            colors.put(parent.sha, COMMON);
                            result.add(parent.sha);
                        }
                    }
                }
                if (parentColor != COMMON && colors.get(parent.sha) == COMMON) {
                    commonCounter++;
                }
            }
        }
        return result;
    }
}
