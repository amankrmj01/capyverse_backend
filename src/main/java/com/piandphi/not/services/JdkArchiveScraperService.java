package com.piandphi.not.services;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;
import java.util.HashMap;

@Singleton
public class JdkArchiveScraperService {
    private static final String ARCHIVE_URL = "https://jdk.java.net/archive/";
    @Inject
    JdkArchiveCacheService cacheService;

    public CompletableFuture<String> fetchArchivePage() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ARCHIVE_URL))
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::extractJdkLinksAsJson);
    }

    private String extractJdkLinksAsJson(String html) {
        Document doc = Jsoup.parse(html);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        Elements versionHeaders = doc.select("th");
        for (Element versionHeader : versionHeaders) {
            String version = versionHeader.text();
            Element tr = versionHeader.parent();
            Elements tds = tr.select("td");
            for (Element td : tds) {
                Elements links = td.select("a[href]");
                for (Element link : links) {
                    String url = link.absUrl("href");
                    String text = link.text();
                    ObjectNode node = mapper.createObjectNode();
                    node.put("version", version);
                    node.put("text", text);
                    node.put("url", url);
                    arrayNode.add(node);
                }
            }
        }
        return arrayNode.toString();
    }

    public CompletableFuture<String> fetchArchivePageByOs(String os) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ARCHIVE_URL))
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(html -> extractJdkLinksByOs(html, os));
    }

    private String extractJdkLinksByOs(String html, String os) {
        Document doc = Jsoup.parse(html);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        Elements trs = doc.select("tr");
        String osLower = os.toLowerCase();
        for (Element tr : trs) {
            Elements ths = tr.select("th");
            Elements tds = tr.select("td");
            String osTag = "";
            if (ths.size() > 0) {
                osTag = ths.get(0).text().toLowerCase();
            }
            for (Element td : tds) {
                Elements links = td.select("a[href]");
                for (Element link : links) {
                    String url = link.absUrl("href");
                    String text = link.text();
                    boolean match = false;
                    if (osLower.equals("windows") && (osTag.contains("windows") || url.contains("windows"))) {
                        match = true;
                    } else if (osLower.equals("mac") && (osTag.contains("mac") || osTag.contains("osx") || url.contains("mac") || url.contains("osx"))) {
                        match = true;
                    } else if (osLower.equals("linux") && (osTag.contains("linux") || url.contains("linux"))) {
                        match = true;
                    }
                    if (match) {
                        ObjectNode node = mapper.createObjectNode();
                        node.put("version", osTag);
                        node.put("text", text);
                        node.put("url", url);
                        arrayNode.add(node);
                    }
                }
            }
        }
        return arrayNode.toString();
    }

    public CompletableFuture<List<String>> fetchJdkVersionBuildStrings() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ARCHIVE_URL))
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::extractJdkVersionBuildStrings);
    }

    private List<String> extractJdkVersionBuildStrings(String html) {
        Document doc = Jsoup.parse(html);
        Elements versionHeaders = doc.select("th");
        List<String> result = new ArrayList<>();
        for (Element versionHeader : versionHeaders) {
            String version = versionHeader.text();
            // Only add if it matches the expected format
            if (version.matches("\\d+\\.?[\\d+]* GA \\(.+\\)") || version.matches("\\d+\\.?[\\d+]* \\(build .+\\)")) {
                result.add(version);
            }
        }
        return result;
    }

    public CompletableFuture<String> fetchJdkVersionBuildStringsJson() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ARCHIVE_URL))
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::extractJdkVersionBuildStringsJson);
    }

    public CompletableFuture<Void> updateBuildsCache() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ARCHIVE_URL))
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::parseAndCacheBuildsTable);
    }

    private void parseAndCacheBuildsTable(String html) {
        Document doc = Jsoup.parse(html);
        Element buildsTable = doc.selectFirst("table.builds");
        Map<String, List<Map<String, String>>> cache = new HashMap<>();
        if (buildsTable != null) {

            String ver = null;
            Elements rows = buildsTable.select("tr");
            for (Element row : rows) {
                Elements ths = row.select("th");
                Elements tds = row.select("td");
                if (ths.size() > 0) {
                    String thText = ths.get(0).text();
                    if (thText.toLowerCase().contains("build")) {
                        ver = thText;
                    }
                }
                if (ver != null && tds.size() > 0) {
                    String osRaw = null;
                    // Find OS th in this row (usually after build th)
                    for (Element th : ths) {
                        String txt = th.text().toLowerCase();
                        if (txt.contains("windows")) {
                            osRaw = "windows";
                            break;
                        } else if (txt.contains("linux")) {
                            osRaw = "linux";
                            break;
                        } else if (txt.contains("mac") || txt.contains("osx")) {
                            osRaw = "macos";
                            break;
                        }
                    }
                    if (osRaw == null) continue;
                    List<Map<String, String>> osList = cache.computeIfAbsent(osRaw, k -> new ArrayList<>());
                    for (Element td : tds) {
                        Elements links = td.select("a[href]");
                        for (Element link : links) {
                            Map<String, String> entry = new HashMap<>();
                            entry.put("version", ver);
                            entry.put("url", link.absUrl("href"));
                            entry.put("type", ths.get(0).text());
                            osList.add(entry);
                        }
                    }
                }
            }
        }
        cacheService.updateCache(cache);
    }

    public String getBuildsCacheJson() {
        return cacheService.getCacheAsJson();
    }

    private String extractJdkVersionBuildStringsJson(String html) {
        updateBuildsCache().join();
        return getBuildsCacheJson();
    }

    public List<String> extractJdkBuildStrings(String html) {
        Document doc = Jsoup.parse(html);
        Element buildsTable = doc.selectFirst("table.builds");
        List<String> result = new ArrayList<>();
        if (buildsTable != null) {
            Elements ths = buildsTable.select("th");
            for (Element th : ths) {
                result.add(th.text());
            }
        }
        return result;
    }
}
