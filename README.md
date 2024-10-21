# Last Common Commits Finder


## Overview
This project includes a [Java library](library) that provides an algorithm for finding last common commits between two branches of a GitHub repository and an [example application](example) that illustrates its usage. Library uses [retrofit2](https://square.github.io/retrofit/) for requests and [SLF4J](https://slf4j.org/) for logging.

## Usage
Library API is specified by [LastCommonCommitsFinder](library/src/main/java/com/das747/commitfinder/api/LastCommonCommitsFinder.java) and [LastCommonCommitsFinderFactory](library/src/main/java/com/das747/commitfinder/api/LastCommonCommitsFinderFactory.java) interfaces. 
### Configuration
Factory can be configured through following JVM properties: 

| Property                   | Description                                                                       | Values          | Default |
|----------------------------|-----------------------------------------------------------------------------------|-----------------|---------|
| commitFinder.cache         | If set to enable, caching GitHub client will be used                              | enable, disable | disable |
| commitFinder.cache.type    | Type of cache to use (if cache is enabled)                                        | lru, lfu        | lru     |
| commitFinder.cache.maxSize | Cache size limit (in commits)                                                     | [1, INT_MAX)    | 100     |
| commitFinder.algorithm     | Commit tree traversal algorithm (see [implementations](#lastcommoncommitsfinder)) | chrono, dfs     | chrono  |

### Tests
Tests are written with TestNG testing framework. To execute them run
```bash
./gradlew :library:test
```
Tests are using commit graphs described with JSON files ([schema](library/src/test/resources/testData/schemas/testDataSchema.json)).

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