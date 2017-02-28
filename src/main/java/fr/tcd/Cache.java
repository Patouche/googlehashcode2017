package fr.tcd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Cache {

    public final int id;

    public final List<Video> videos = new ArrayList<>();

    public final Map<Endpoint, Integer> endpoints = new HashMap<>();

    private final int cacheSize;

    public Cache(int id, int cacheSize) {
        this.id = id;
        this.cacheSize = cacheSize;
    }

    public void addEnPoint(Endpoint endpoint, int latency) {
        endpoints.put(endpoint, latency);
    }

    public int getAvailableSpace(int cacheSize) {
        return cacheSize - videos.stream().mapToInt(video -> video.weight).sum();
    }

    public int getAvailableSpace() {
        return cacheSize - videos.stream().mapToInt(video -> video.weight).sum();
    }

    public boolean hasAvailableSpace() {
        return getAvailableSpace() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cache cache = (Cache) o;
        return id == cache.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
