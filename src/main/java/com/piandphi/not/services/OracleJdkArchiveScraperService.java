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
public class OracleJdkArchiveScraperService {
    private static final String URL = "https://www.oracle.com/in/java/technologies/downloads/archive/#JavaSE";


    @Inject
    OracleJdkArchiveCacheService cacheService;

    public CompletableFuture<List<String>> fetchJavaSEVersionsLinks(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::extractJavaSEVersionsLinks);
    }

    private List<String> extractJavaSEVersionsLinks(String html) {
        Document doc = Jsoup.parse(html, "https://www.oracle.com"); // Set base URI for absolute URLs
        List<String> links = new ArrayList<>();
        Elements items = doc.select(".rc30w5 ul.icn-ulist li.icn-chevron-right a");
        for (Element item : items) {
            String text = item.text();
            String href = item.absUrl("href");
            links.add(text + " " + href);
        }
        return links;
    }

    public CompletableFuture<String> fetchJavaSEVersionsLinksJson(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::extractJavaSEVersionsLinksJson);
    }

    private String extractJavaSEVersionsLinksJson(String html) {
        Document doc = Jsoup.parse(html, "https://www.oracle.com");
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        List<Map<String, String>> cacheList = new ArrayList<>();
        Elements items = doc.select(".rc30w5 ul.icn-ulist li.icn-chevron-right a");
        for (Element item : items) {
            ObjectNode node = mapper.createObjectNode();
            node.put("name", item.text());
            node.put("url", item.absUrl("href"));
            arrayNode.add(node);

            Map<String, String> entry = new HashMap<>();
            entry.put("name", item.text());
            entry.put("url", item.absUrl("href"));
            cacheList.add(entry);
        }
        cacheService.updateCache(cacheList);
        return arrayNode.toString();
    }

    public CompletableFuture<Void> updateArchiveCache() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::parseAndCacheArchiveLinks);
    }

    private void parseAndCacheArchiveLinks(String html) {
        Document doc = Jsoup.parse(html, "https://www.oracle.com");
        List<Map<String, String>> cache = new ArrayList<>();
        Elements items = doc.select(".rc30w5 ul.icn-ulist li.icn-chevron-right a");
        for (Element item : items) {
            Map<String, String> entry = new HashMap<>();
            entry.put("name", item.text());
            entry.put("url", item.absUrl("href"));
            cache.add(entry);
        }
        cacheService.updateCache(cache);
    }

    public CompletableFuture<Void> fetchAndCacheJavaSEVersionsLinks() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::extractAndStoreJavaSEVersionsLinks);
    }

    private void extractAndStoreJavaSEVersionsLinks(String html) {
        Document doc = Jsoup.parse(html, "https://www.oracle.com");
        List<Map<String, String>> links = new ArrayList<>();
        Elements items = doc.select(".rc30w5 ul.icn-ulist li.icn-chevron-right a");
        for (Element item : items) {
            Map<String, String> entry = new HashMap<>();
            entry.put("name", item.text());
            entry.put("url", item.absUrl("href"));
            links.add(entry);
        }
        cacheService.updateCache(links);
    }

    public String getArchiveCacheJson() {
        return cacheService.getCacheAsJson();
    }


}
