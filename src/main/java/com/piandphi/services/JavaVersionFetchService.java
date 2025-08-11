package com.piandphi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.cache.SyncCache;
import io.micronaut.cache.CacheManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class JavaVersionFetchService {
    private static final String JSON_URL = "https://github.com/amankrmj01/capyverse_cli/raw/44ba59c46cfdcde47f9463384f7ee80514e9ffdc/.github/java_versions/java_version.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    private final SyncCache<Object> cache;

    @Inject
    public JavaVersionFetchService(CacheManager<?> cacheManager) {
        this.cache = (SyncCache<Object>) cacheManager.getCache("java-versions");
    }

    public List<String> getAllUrls() throws Exception {
        List<JavaVersion> versions = getJavaVersions();
        return versions.stream().map(JavaVersion::url).collect(Collectors.toList());
    }

    public String getAllVersions() throws Exception {
        List<JavaVersion> versions = getJavaVersions();
        return versions.stream().map(JavaVersion::version).collect(Collectors.joining(";"));
    }

    public List<JavaVersion> getJavaVersions() {
        Object cachedObj = cache.get("data", Object.class).orElse(null);
        if (cachedObj instanceof List<?>) {
            List<?> rawList = (List<?>) cachedObj;
            boolean allJavaVersion = rawList.stream().allMatch(o -> o instanceof JavaVersion);
            if (allJavaVersion) {
                List<JavaVersion> cached = (List<JavaVersion>) rawList;
                System.out.println("Returning Java versions from cache");
                return cached;
            }
        }
        try {
            URL url = URI.create(JSON_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            List<JavaVersion> versions = mapper.readValue(conn.getInputStream(), new TypeReference<List<JavaVersion>>() {});
            cache.put("data", versions);
            System.out.println("Returning Java versions from remote and caching");
            return versions;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public record JavaVersion(String version, String url) {}
}
