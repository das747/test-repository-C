package com.das747;

import java.util.Date;
import java.util.List;

public class Commit {
    public String sha;
    public List<ParentData> parents;
    public CommitData commit;

    public static class ParentData {
        public String sha;
    }

    public static class CommitData {
        public AuthorData author;

        public static class AuthorData {
            public Date date;
        }
    }
}
