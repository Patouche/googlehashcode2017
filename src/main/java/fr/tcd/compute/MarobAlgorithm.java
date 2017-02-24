package fr.tcd.compute;

import fr.tcd.Cache;
import fr.tcd.CacheEnpointCouple;
import fr.tcd.Endpoint;
import fr.tcd.Request;
import fr.tcd.input.InputData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author patouche - 2/24/17.
 */
public class MarobAlgorithm implements Algorithm {

    private final InputData inputData;

    private int coupleIndex = 0;

    public MarobAlgorithm(final InputData inputData) {
        this.inputData = inputData;
    }

    public void compute() {
        List<Cache> caches = inputData.caches;

        List<CacheEnpointCouple> cacheEnpointCouples = new ArrayList<>();
        caches.forEach(cache -> cache.endpoints.forEach((endpoint, cacheEnpointLatency) -> cacheEnpointCouples
                .add(new CacheEnpointCouple(cache, endpoint, cacheEnpointLatency))));

        cacheEnpointCouples.sort(Comparator.comparingInt(c -> -c.latencyGain));

        int cacheEnpointCouplesSize = cacheEnpointCouples.size();
        for (CacheEnpointCouple cacheEnpointCouple : cacheEnpointCouples) {
            this.coupleIndex++;
            System.out.println("Remaining couples: " + (cacheEnpointCouplesSize - this.coupleIndex));
            this.storeVideosInCache(cacheEnpointCouple);
        }
    }

    private void storeVideosInCache(CacheEnpointCouple cacheEnpointCouple) {
        List<Request> requests = this.inputData.requests;
        Cache cacheServer = cacheEnpointCouple.cache;
        Endpoint endpoint = cacheEnpointCouple.endpoint;

        System.out.println("Cache:" + cacheEnpointCouple.cache.id);
        System.out.println("Endpoint:" + endpoint.id);

        System.out.println("Remaining requests:" + requests.size());

        if (cacheServer.getAvailableSpace(this.inputData.cacheSize) != 0) {
            final List<Request> filteredRequests = requests.stream()
                    .filter(request -> request.endpoint.id == endpoint.id)
                    .collect(Collectors.toList());
            while (true) {
                Optional<Request> requestToCache = filteredRequests.stream()
                        .filter(request -> cacheServer.videos.stream().mapToInt(video -> video.id)
                                .noneMatch(value -> value == request.video.id))
                        .filter(request -> request.video.weight <= cacheServer.getAvailableSpace(this.inputData.cacheSize))
                        .sorted(Comparator.comparingInt(r -> r.nbRequest * r.video.weight))
                        .findFirst();

                if (!requestToCache.isPresent()) {
                    break;
                }

                System.out.println("Nb request: " + requestToCache.get().nbRequest + " | " + "Video weight: " + requestToCache.get()
                        .video.weight);

                // System.out.println("Request:" + requestToCache.get().id);

                // Add video in cacheServer
                cacheServer.videos.add(requestToCache.get().video);

                // Request has been caches => we remove it
                requests.remove(requestToCache.get());
            }
        }
    }
}
