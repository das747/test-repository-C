package com.das747.commitfinder.example;

import com.das747.commitfinder.api.LastCommonCommitsFinderFactoryImpl;

public class Main {

    public static void main(String[] args) {
        var finder = new LastCommonCommitsFinderFactoryImpl().create("nadnes-team", "nadnes", null);
        try {
            var result = finder.findLastCommonCommits("main", "dan/apu");
            result.forEach(s -> System.out.println(s));
            result = finder.findLastCommonCommits("dan/apu", "main");
            result.forEach(s -> System.out.println(s));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        client.dispatcher().executorService().shutdown();
//        client.connectionPool().evictAll();
    }
}
