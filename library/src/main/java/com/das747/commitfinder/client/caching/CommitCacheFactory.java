package com.das747.commitfinder.client.caching;

public interface CommitCacheFactory {

    static CommitCache create() {
        var cacheType = System.getProperty("commitFinder.cache.type", "lru");

        var sizeSetting = System.getProperty("commitFinder.cache.maxSize");
        int maxSize = CommitCache.DEFAULT_SIZE;
        if (sizeSetting != null) {
            try {
                maxSize = Integer.valueOf(sizeSetting);
                if (maxSize < 1) {
                    System.err.println(
                        "Invalid cache max size: '" + sizeSetting + "'. Using default value '"
                            + CommitCache.DEFAULT_SIZE);
                    maxSize = CommitCache.DEFAULT_SIZE;
                }
            } catch (NumberFormatException ignored) {
                System.err.println(
                    "Invalid cache max size: '" + sizeSetting + "'. Using default value '"
                        + CommitCache.DEFAULT_SIZE);
            }
        }



        switch (cacheType) {
            case "lru" -> {
                return new LRUCommitCache(maxSize);
            }
            case "lfu" -> {
                return new LFUCommitCache(maxSize);
            }

            default -> {
                System.err.println(
                    "Invalid cache type: '" + cacheType + "'. Using default 'lru' cache.");
                return new LRUCommitCache(maxSize);
            }
        }
    }

}
