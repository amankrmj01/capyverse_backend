package com.piandphi.config;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.core.order.Ordered;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;

import java.net.URI;

@Singleton
public class WebConfig implements HttpServerFilter, Ordered {
    private static final String PREFIX = "/api/v1";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        String path = request.getPath();
        if (!path.startsWith(PREFIX)) {
            MutableHttpRequest<?> newRequest = request.mutate().uri(URI.create(PREFIX + path));
            return chain.proceed(newRequest);
        }
        return chain.proceed(request);
    }
}
