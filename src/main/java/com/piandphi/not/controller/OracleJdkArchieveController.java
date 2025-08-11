package com.piandphi.not.controller;


import com.piandphi.not.services.OracleJdkArchiveCacheService;
import com.piandphi.not.services.OracleJdkArchiveScraperService;
import com.piandphi.not.services.OracleJdkProductFinderService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@Controller("/oraclejdkarchive")
public class OracleJdkArchieveController {

    @Inject
    OracleJdkArchiveScraperService scraperService;

    @Inject
    OracleJdkArchiveCacheService cacheService;

    @Inject
    OracleJdkProductFinderService productFinderService;

    @Get
    public CompletableFuture<HttpResponse<String>> getList() {
        return scraperService.fetchJavaSEVersionsLinksJson()
            .thenApply(HttpResponse::ok);
    }

    @Get("/getcache")
    public HttpResponse<String> getCache() {
        return HttpResponse.ok(cacheService.getCacheAsJson());
    }

    @Get("/install")
    public HttpResponse<?> install(@QueryValue String name, @QueryValue String product, @Nullable @QueryValue("it") Boolean it) {
        String url;
        if (it == null || !it) {
            url = productFinderService.findProductDownloadUrl(name, product);
            if (url == null) {
                return HttpResponse.notFound();
            }
            return HttpResponse.ok(url);
        } else {
            url = productFinderService.findProductDownloadUrl(name, product, it);
            if (url == null) {
                return HttpResponse.notFound();
            }
            return HttpResponse.redirect(URI.create(url));
        }
    }
}
