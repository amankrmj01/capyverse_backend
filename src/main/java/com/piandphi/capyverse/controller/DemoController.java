package com.piandphi.capyverse.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.MediaType;
import java.util.Map;

@Controller("/demo")
public class DemoController {
    @Get(produces = MediaType.APPLICATION_JSON)
    public Map<String, String> index() {
        return Map.of("message", "Hello from Micronaut demo controller!");
    }
}
