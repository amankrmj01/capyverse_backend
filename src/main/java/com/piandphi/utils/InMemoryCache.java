package com.piandphi.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

public class InMemoryCache<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

    public Optional<V> get(K key) {
        return Optional.ofNullable(cache.get(key));
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public void clear() {
        cache.clear();
    }
}
