package com.piandphi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class OracleJdkArchiveCacheService {
    private final List<Map<String, String>> cache = new ArrayList<>();

    public synchronized void updateCache(List<Map<String, String>> newCache) {
        cache.clear();
        cache.addAll(newCache);
    }

    public synchronized String getCacheAsJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(cache);
        } catch (Exception e) {
            return "{}";
        }
    }

    public synchronized List<Map<String, String>> getCache() {
        return new ArrayList<>(cache);
    }
}
