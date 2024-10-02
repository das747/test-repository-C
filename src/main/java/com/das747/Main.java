package com.das747;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main {

    public static void main(String[] args) {
        var finder = new LastCommonCommitsFinderFactoryImpl().create("nadnes-team", "nadnes", null);
        try {
            var result = finder.findLastCommonCommits("main", "dan/apu");
            result.forEach(s -> System.out.println(s));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        client.dispatcher().executorService().shutdown();
//        client.connectionPool().evictAll();
    }
}
