package com.das747.commitfinder.client.caching;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface CommitCacheFactory {

    static @NotNull CommitCache create() {
        Logger logger = LoggerFactory.getLogger(CommitCacheFactory.class);

        var cacheType = System.getProperty("commitFinder.cache.type", "lru");

        var sizeSetting = System.getProperty("commitFinder.cache.maxSize");
        int maxSize = CommitCache.DEFAULT_SIZE;
        if (sizeSetting != null) {
            try {
                maxSize = Integer.parseInt(sizeSetting);
                if (maxSize < 1) {
                    throw new IllegalArgumentException();
                }
                logger.info("Cache max size is {}", maxSize);
            } catch (IllegalArgumentException ignored) {
                logger.warn(
                    "Invalid cache max size: '{}'. Using default value '{}'",
                    sizeSetting,
                    CommitCache.DEFAULT_SIZE
                );
                maxSize = CommitCache.DEFAULT_SIZE;
            }
        }

        switch (cacheType) {
            case "lru" -> {
                logger.info("Using LRU cache");
                return new LRUCommitCache(maxSize);
            }
            case "lfu" -> {
                logger.info("Using LFU cache");
                return new LFUCommitCache(maxSize);
            }

            default -> {
                logger.warn("Invalid cache type: '{}'. Using default 'lru' cache.", cacheType);
                return new LRUCommitCache(maxSize);
            }
        }
    }

}
