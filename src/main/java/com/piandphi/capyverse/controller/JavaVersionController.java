package com.piandphi.capyverse.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.MediaType;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;
import io.micronaut.cache.annotation.Cacheable;


@Controller("/javaversion")
public class JavaVersionController {

    @Inject
    @Client("https://raw.githubusercontent.com")
    HttpClient httpClient;

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    @Cacheable("javaversion")
    public Mono<String> fetchJavaVersion() {
        String url = "/amankrmj01/capyverse_cli/main/.github/java_versions/java_version.json";
        return Mono.from(httpClient.retrieve(url));
    }
}
