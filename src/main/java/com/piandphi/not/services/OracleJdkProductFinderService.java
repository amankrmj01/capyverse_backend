package com.piandphi.not.services;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.List;
import java.util.Map;

@Singleton
public class OracleJdkProductFinderService {
    @Inject
    OracleJdkArchiveCacheService cacheService;

    public String findProductDownloadUrl(String versionName, String productKeyword) {
        // Step 1: Search in cache for the version name and get its URL
        List<Map<String, String>> cache = cacheService.getCache();
        String versionUrl = null;
        for (Map<String, String> entry : cache) {
            if (entry.get("name").equalsIgnoreCase(versionName)) {
                versionUrl = entry.get("url");
                break;
            }
        }
        if (versionUrl == null) return null;

        // Step 2: Fetch and parse the product page
        try {
            Document doc = Jsoup.connect(versionUrl).get();
            Elements rows = doc.select("div.otable-w1 table.otable-w2 tbody tr");
            for (Element row : rows) {
                String productDesc = row.select("td").get(0).text();
                if (productDesc.toLowerCase().contains(productKeyword.toLowerCase())) {
                    Element link = row.select("td").get(2).selectFirst("a[href]");
                    if (link != null) {
                        return link.absUrl("href");
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public String findProductDownloadUrl(String versionName, String productKeyword,boolean it) {
        // Step 1: Search in cache for the version name and get its URL
        List<Map<String, String>> cache = cacheService.getCache();
        String versionUrl = null;
        for (Map<String, String> entry : cache) {
            if (entry.get("name").equalsIgnoreCase(versionName)) {
                versionUrl = entry.get("url");
                break;
            }
        }
        if (versionUrl == null) return null;

        try {
            Document doc = Jsoup.connect(versionUrl).get();
            Elements rows = doc.select("div.otable-w1 table.otable-w2 tbody tr");
            for (Element row : rows) {
                String productDesc = row.select("td").get(0).text();
                if (productDesc.toLowerCase().contains(productKeyword.toLowerCase())) {
                    Element link = row.select("td").get(2).selectFirst("a[href]");
                    if (link != null) {
                        String downloadUrl = link.absUrl("href");
                        return downloadUrl;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
