package com.das747;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubService {

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}/branches/{branch}")
    Call<Branch> getBranch(
        @Path("owner") String owner,
        @Path("repo") String repo,
        @Path("branch") String branch,
        @Header("Authorization") String authorisation
    );

    @Headers("Accept: application/vnd.github+json")
    @GET("/repos/{owner}/{repo}/commits")
    Call<List<Commit>> listCommits(
        @Path("owner") String owner,
        @Path("repo") String repo,
        @Query("sha") String sha,
        @Header("Authorization") String authorisation
    );

}
