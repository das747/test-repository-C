package com.das747.commitfinder.client.caching;

import com.das747.commitfinder.Commit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class LFUCommitCache extends CommitCacheBase {

    private record FrequencyEntry(String sha, int frequency) {

    }

    private final @NotNull SortedSet<FrequencyEntry> frequencyMap = new TreeSet<>(
        Comparator.comparing(e -> e.frequency));
    private final @NotNull HashMap<String, Integer> frequencies = new HashMap<>();

    public LFUCommitCache(int sizeLimit) {
        super(sizeLimit, new HashMap<>());
    }

    @Override
    protected @NotNull Commit evictCommit() {
        var victimEntry = frequencyMap.first();
        frequencyMap.remove(victimEntry);
        frequencies.remove(victimEntry.sha);
        return storage.remove(victimEntry.sha);
    }

    @Override
    public Commit put(@NotNull String sha, @NotNull Commit commit) {
        var victim = super.put(sha, commit);
        if (!frequencies.containsKey(sha)) {
            frequencies.put(sha, 0);
            frequencyMap.add(new FrequencyEntry(sha, 0));
        }
        return victim;
    }

    @Override
    public @Nullable Commit get(@NotNull String sha) {
        var commit = super.get(sha);
        if (commit == null) {
            return null;
        }
        int newFrequency = frequencies.compute(sha, (k, v) -> v + 1);
        frequencyMap.remove(new FrequencyEntry(sha, newFrequency - 1));
        frequencyMap.add(new FrequencyEntry(sha, newFrequency));
        return commit;
    }
}
