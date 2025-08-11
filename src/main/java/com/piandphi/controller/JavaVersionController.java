package com.piandphi.controller;

import com.piandphi.services.JavaVersionFetchService;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import java.util.List;

@Controller("/javaversions")
public class JavaVersionController {
    @Inject
    JavaVersionFetchService javaVersionFetchService;

    @Get("/urls")
    public HttpResponse<List<String>> getAllUrls() {
        try {
            List<String> urls = javaVersionFetchService.getAllUrls();
            return HttpResponse.ok(urls);
        } catch (Exception e) {
            return HttpResponse.serverError();
        }
    }

    @Get("/versions")
    public HttpResponse<String> getAllVersions() {
        try {
            String versions = javaVersionFetchService.getAllVersions();
            return HttpResponse.ok(versions);
        } catch (Exception e) {
            return HttpResponse.serverError();
        }
    }

    @Get()
    public HttpResponse<String> getJavaVersion(
            @Parameter String version
    ) {
        try {
            JavaVersionFetchService.JavaVersion javaVersion = javaVersionFetchService.getJavaVersions()
                .stream()
                .filter(v -> v.version().equals(version))
                .findFirst()
                .orElse(null);

            if (javaVersion != null) {
                return HttpResponse.ok(javaVersion.url());
            } else {
                return HttpResponse.notFound("Java version " + version + " is not available.");
            }
        } catch (Exception e) {
            return HttpResponse.serverError();
        }
    }
}
