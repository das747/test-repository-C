package com.das747.commitfinder;

import java.time.Instant;
import java.util.List;

public record Commit(
    String sha,
    List<ParentData> parents,
    CommitData commit
) {

    public record ParentData(String sha) {

    }

    public record CommitData(AuthorData author) {

    }

    public record AuthorData(Instant date) {

    }
}
