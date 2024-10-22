# Last Common Commits Finder


## Overview
This project includes a [Java library](library) that provides an algorithm for finding last common commits between two branches of a GitHub repository and an [example application](example) that illustrates its usage. The library uses [retrofit2](https://square.github.io/retrofit/) for requests and [SLF4J](https://slf4j.org/) for logging.

## Usage
Library API is specified by [LastCommonCommitsFinder](library/src/main/java/com/das747/commitfinder/api/LastCommonCommitsFinder.java) and [LastCommonCommitsFinderFactory](library/src/main/java/com/das747/commitfinder/api/LastCommonCommitsFinderFactory.java) interfaces. 
### Configuration
Factory can be configured through following JVM properties: 

| Property                   | Description                                                                       | Values          | Default |
|----------------------------|-----------------------------------------------------------------------------------|-----------------|---------|
| commitFinder.cache         | If set to 'enable', caching GitHub client will be used                            | enable, disable | disable |
| commitFinder.cache.type    | Type of cache to use (if cache is enabled)                                        | lru, lfu        | lru     |
| commitFinder.cache.maxSize | Cache size limit (in commits)                                                     | [1, INT_MAX)    | 100     |
| commitFinder.algorithm     | Commit tree traversal algorithm (see [implementations](#lastcommoncommitsfinder)) | chrono, dfs     | chrono  |

### Tests
Tests are written with TestNG testing framework. To execute them run
```bash
./gradlew :library:test
```
Algorithm tests use client mock that returns data according to commit graphs described with JSON
files ([schema](library/src/test/resources/testData/schemas/testDataSchema.json)).

### Logging
This library uses [SLF4J](https://github.com/qos-ch/slf4j) for logging, so end-application can use any supported logging provider (or none).

### Example application
Example application can be executed by running
```bash
./gradlew :example:run 
```
Library settings can be passed as gradle properties:
```bash
./gradlew :example:run -PcommitFinder.cache=enable -PcommitFinder.algorithm=dfs
```

## Implementation overview
Library functionality is divided between two components:
### GitHubClient
[GitHubClient](library/src/main/java/com/das747/commitfinder/client/GitHubClient.java) interface provides methods for requesting commit data (implementations are expected to make calls to GitHub REST API)
#### [DefaultGitHubClient](library/src/main/java/com/das747/commitfinder/client/DefaultGitHubClient.java)
- Straightforward client that makes request on each method call
#### [CachingGitHubClient](library/src/main/java/com/das747/commitfinder/client/caching/CachingGitHubClient.java)
- This client uses [CommitCache](library/src/main/java/com/das747/commitfinder/client/caching/CommitCache.java) to store commit data and returns cached commits when possible. Commit data can be cached because each commit is immutable once created (on the other hand, branch data cannot be cached). [LFU](library/src/main/java/com/das747/commitfinder/client/caching/LFUCommitCache.java) and [LRU](library/src/main/java/com/das747/commitfinder/client/caching/LRUCommitCache.java) cache implementations are available. 
- It also makes use of bulk requests and prefetches and caches a batch of chronologically following commits.

### LastCommonCommitsFinder
Actual graph traversal strategies are handled by [LastCommonCommitsFinder](library/src/main/java/com/das747/commitfinder/api/LastCommonCommitsFinder.java) implementations: 
#### [ChronologicalTraversalCommitsFinder](library/src/main/java/com/das747/commitfinder/finder/ChronologicalTraversalCommitsFinder.java)
- Dijkstra-like traversal that uses commit timestamps to determine next commit to visit. Such traversal order makes good use of cache since GitHub API bulk commit requests return commits in chronological order. 
- Commits are colored according to their reachability from branches heads. If queue contains only one common commit and commits reachable from only one of the branches, search can be terminated. This optimisation allows to avoid loading of branch histories past divergence point.

#### [DepthFirstTraversalCommitsFinder](library/src/main/java/com/das747/commitfinder/finder/DepthFirstTraversalCommitsFinder.java)
- DFS style traversal that colors commits according to their reachability from branches heads. Each commit will be traversed no more than two times.

Both implementations also cache results, so repeating requests with same head commits can be served effectively.

# Afterthoughts
## Extensibility and customizability
I designed this library with extensibility in mind. Obviously, it is possible to add more algorithms and cache policies by providing implementations of respective interfaces, but there is more to it:
- The client can be reused for any kind of commit graph queries
- The current algorithms can be modified to work with the arbitrary number of branches. The major change would be to swap node colours for bitmasks. 
- Other git hosting services can be supported by generalizing the GitHubClient interface and implementing corresponding clients (as long as it is possible to adapt their APIs to the current client interface).

## Concurrency
Currently, everything is implemented in the sequential way, though some elements could potentially benefit from the concurrent execution. Concurrent fetching of the parent commits in the chronological traversal might improve performance, especially if only API requests are multiplexed and cache access remains sequential. In the dfs algorithm the whole traversal could be made asynchronous, but that would require synchronisation of both the cache and every node state (now that I think about it, Guava cache and atomic references in nodes would allow for easy concurrent traversal).

## Cache 
I tried to estimate the minimum viable cache sizes for both types of cache, but arrived at no strong conclusion. LRU is mostly intended to be used with the chronological traversal, holding commits from the 'active zone' (those reachable directly from commits that would be dequeued next), so its max size should depend on how 'wide' this zone can get. LFU, on the other hand,  fits the dfs algorithm if it is large enough to hold commits from more than one execution, forming a sort of 'graph skeleton' of the most visited nodes. 
Perhaps a better option would have been to make client interface cache-aware and provide way for an algorithm to 'pin' commits and have more control over caching and fetching (for example, we know that once commit was dequeued it won't be requested again; or if number of pinned commits approaches size limit we can switch to single commit fetches). The current approach sacrifices some potential performance gains for reduced coupling.
I also considered a repository-level cache (shared between finder instances), but that again would require to synchronize it and more careful user authorization.

## Security
Two major considerations are potential injections and user token safety. Injections (and user input sanitizing in general) should be covered by retrofit through encoding of the request template parameters. The token should be fine as long as it doesn't get persisted or logged. 

## Configuration
System properties might not be the most popular choice, but I wanted to preserve library interfaces. Though realistically, configuration should have been implemented via factory parameters or through builder pattern.




