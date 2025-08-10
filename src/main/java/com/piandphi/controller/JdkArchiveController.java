package com.piandphi.controller;

import com.piandphi.services.JdkArchiveScraperService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Inject;
import java.util.concurrent.CompletableFuture;

@Controller("/jdkarchive")
public class JdkArchiveController {
    @Inject
    JdkArchiveScraperService scraperService;

    @Get
    public CompletableFuture<HttpResponse<String>> getArchiveByOs(HttpRequest<?> request) {
        String userAgent = request.getHeaders().get("User-Agent");
        String os = "mac";
        if (userAgent != null) {
            String ua = userAgent.toLowerCase();
            if (ua.contains("windows")) {
                os = "windows";
            } else if (ua.contains("mac")) {
                os = "mac";
            } else if (ua.contains("linux")) {
                os = "linux";
            }
        }
        return scraperService.fetchArchivePageByOs(os)
                .thenApply(HttpResponse::ok);
    }

    @Get("/list")
    public CompletableFuture<HttpResponse<String>> getJdkArchiveList() {
        return scraperService.fetchJdkVersionBuildStringsJson()
            .thenApply(HttpResponse::ok);
    }

    @Get("/getcache")
    public HttpResponse<String> getJdkArchiveCache() {
        return HttpResponse.ok(scraperService.getBuildsCacheJson());
    }
}
