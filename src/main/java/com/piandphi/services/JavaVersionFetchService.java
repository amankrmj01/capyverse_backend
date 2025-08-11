package com.piandphi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piandphi.utils.InMemoryCache;
import jakarta.inject.Singleton;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class JavaVersionFetchService {
    private static final String JSON_URL = "https://github.com/amankrmj01/capyverse_cli/raw/44ba59c46cfdcde47f9463384f7ee80514e9ffdc/.github/java_versions/java_version.json";

    private static final InMemoryCache<String, List<JavaVersion>> cache = new InMemoryCache<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public List<String> getAllUrls() throws Exception {
        List<JavaVersion> versions = getJavaVersions();
        return versions.stream().map(JavaVersion::url).collect(Collectors.toList());
    }

    public String getAllVersions() throws Exception {
        List<JavaVersion> versions = getJavaVersions();
        return versions.stream().map(JavaVersion::version).collect(Collectors.joining(";"));
    }

    public List<JavaVersion> getJavaVersions() {
        return cache.get("data").orElseGet(() -> {
            try {
                URL url = URI.create(JSON_URL).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                List<JavaVersion> versions = mapper.readValue(conn.getInputStream(), new TypeReference<>() {});
                cache.put("data", versions);
                return versions;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public record JavaVersion(String version, String url) {}
}
