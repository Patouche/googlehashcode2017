package fr.tcd;

import java.util.List;

/**
 * @author pallain - 2/23/17.
 */
public class InputData {

    public final int nbVideos;

    public final int nbEndpoints;

    public final int nbRequestDescriptions;

    public final int nbCaches;

    public final int cacheSize;

    public final List<Video> videos;

    public final List<Endpoint> endpoints;

    public final List<Request> requests;

    private final List<Cache> caches;

    public InputData(final int nbVideos, final int nbEndpoints, final int nbRequestDescriptions,
                     final int nbCaches, final int cacheSize,
                     List<Video> videos, List<Cache> caches, List<Endpoint> endpoints, List<Request> requests) {
        this.nbVideos = nbVideos;
        this.nbEndpoints = nbEndpoints;
        this.nbRequestDescriptions = nbRequestDescriptions;
        this.nbCaches = nbCaches;
        this.cacheSize = cacheSize;
        this.videos = videos;
        this.caches = caches;
        this.endpoints = endpoints;
        this.requests = requests;
    }

    public int getNbVideos() {
        return nbVideos;
    }

    public int getNbEndpoints() {
        return nbEndpoints;
    }

    public int getNbRequestDescriptions() {
        return nbRequestDescriptions;
    }

    public int getNbCaches() {
        return nbCaches;
    }

    public int getCacheSize() {
        return cacheSize;
    }
}
