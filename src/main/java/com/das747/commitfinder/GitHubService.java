package com.das747.commitfinder;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubService {
    String BASE_URL = "https://api.github.com/";

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}/branches/{branch}")
    Call<Branch> getBranch(
        @Path("owner") String owner,
        @Path("repo") String repo,
        @Path("branch") String branch,
        @Header("Authorization") String authorisation
    );

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}/commits?per_page=100")
    Call<List<Commit>> listCommits(
        @Path("owner") String owner,
        @Path("repo") String repo,
        @Query("sha") String sha,
        @Header("Authorization") String authorisation
    );

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}/commits/{sha}")
    Call<Commit> getCommit(
        @Path("owner") String owner,
        @Path("repo") String repo,
        @Path("sha") String sha,
        @Header("Authorization") String authorisation
    );
}
