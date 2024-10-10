package com.das747.commitfinder;

import java.time.Instant;
import java.util.List;

public record Commit(
    String sha,
    List<ParentData> parents,
    CommitData commit
) {

    record ParentData(String sha) {

    }

    record CommitData(AuthorData author) {

    }

    record AuthorData(Instant date) {

    }
}
