package com.das747.commitfinder;

import com.das747.commitfinder.api.LastCommonCommitsFinder;
import com.das747.commitfinder.api.LastCommonCommitsFinderFactory;
import com.das747.commitfinder.client.GitHubClient;
import com.das747.commitfinder.client.GitHubClientFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LastCommonCommitsFinderFactoryImpl implements LastCommonCommitsFinderFactory {

    @Override
    public LastCommonCommitsFinder create(String owner, String repo, String token) {
        GitHubService service = new Retrofit.Builder()
            .baseUrl(GitHubService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubService.class);

        GitHubClient client = GitHubClientFactory.create(service, owner, repo, token);
        return new LastCommonCommitsFinderImpl(client);
    }
}
