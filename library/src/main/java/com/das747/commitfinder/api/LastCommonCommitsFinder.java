package com.das747.commitfinder.api;

import java.io.IOException;
import java.util.Collection;

public interface LastCommonCommitsFinder {

  /**
   * Finds SHAs of last commits that are reachable from both
   * branchA and branchB
   *
   * @param branchA   branch name (e.g. "main")
   * @param branchB   branch name (e.g. "dev")
   * @return  a collection of SHAs of last common commits
   * @throws IOException  if any error occurs
   */
  Collection<String> findLastCommonCommits(String branchA, String branchB) throws IOException;

  /**
   * Should be called once instance of LastCommonCommitsFinder is no longer needed.
   * Otherwise, JVM may not exit until underlying client times out
   * <a href="https://github.com/square/retrofit/issues/3144">(details)</a>
   */
  void shutdown();

}