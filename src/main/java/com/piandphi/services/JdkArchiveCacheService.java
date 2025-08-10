package com.piandphi.services;

import jakarta.inject.Singleton;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
public class JdkArchiveCacheService {
    private final Map<String, List<Map<String, String>>> cache = new HashMap<>();

    public synchronized void updateCache(Map<String, List<Map<String, String>>> newCache) {
        cache.clear();
        cache.putAll(newCache);
    }

    public synchronized String getCacheAsJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(cache);
        } catch (Exception e) {
            return "{}";
        }
    }

    public synchronized Map<String, List<Map<String, String>>> getCache() {
        return new HashMap<>(cache);
    }
}
