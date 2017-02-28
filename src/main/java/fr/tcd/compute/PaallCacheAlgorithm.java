package fr.tcd.compute;

import fr.tcd.Cache;
import fr.tcd.Endpoint;
import fr.tcd.Request;
import fr.tcd.Video;
import fr.tcd.input.InputData;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author patouche - 2/24/17.
 */
public class PaallCacheAlgorithm implements Algorithm {

    private final InputData inputData;

    private final ResultCalculator resultCalculator;

    private final Map<Endpoint, List<Request>> endpointRequests;

    private final Map<Endpoint, List<Cache>> endpointCaches;

    public PaallCacheAlgorithm(final InputData inputData) {
        this(inputData, inputData.requests.stream().collect(Collectors.groupingBy(r -> r.endpoint)), buildEndpointCaches(inputData));
    }

    public PaallCacheAlgorithm(final InputData inputData, final Map<Endpoint, List<Request>> endpointRequests,
                               final Map<Endpoint, List<Cache>> endpointCaches) {
        this.resultCalculator = new ResultCalculator(inputData);
        this.inputData = inputData;
        this.endpointRequests = endpointRequests;
        this.endpointCaches = endpointCaches;
    }

    private static Map<Endpoint, List<Cache>> buildEndpointCaches(final InputData inputData) {
        final Map<Endpoint, List<Cache>> map = new HashMap<>();
        inputData.caches.forEach(cache -> cache.endpoints.keySet().stream()
                .peek(endpoint -> map.putIfAbsent(endpoint, new ArrayList<>()))
                .forEach((endpoint -> map.get(endpoint).add(cache)))
        );
        return map;
    }

    @Override
    public void compute() {
        final AtomicReference<Instant> stopwatch = new AtomicReference<>();

        // Pour chaque cache
        this.inputData.caches.stream()
                // On start le stopwatch
                .peek(c -> stopwatch.set(Instant.now()))
                // On prend en premier les caches ayant le meilleur rapport
//                .sorted(cacheComparator())
                // On définie l'algo de remplissage du cache
                .map(cache -> new CacheFillRunner(cache, endpointRequests, endpointCaches))
                // Petit message
                .peek(r -> System.out.printf("%s start ... \n", r))
                // On cherche à remplir le cache
                .peek(CacheFillRunner::run)
                // Petit message
                .forEach(r -> System.out.printf("%s run in %d ms \n", r, Duration.between(stopwatch.get(), Instant.now()).toMillis()));
        // Petit message
        System.out.printf("[%s] Current point : %d \n", this.getClass().getSimpleName(), resultCalculator.calculateAndGet());
    }

    private Comparator<Cache> cacheComparator() {
        final Function<Cache, Long> capacitor = (c) -> c.endpoints.entrySet().stream().mapToLong(entry -> {
            final Endpoint endpoint = entry.getKey();
            final int endpointNbRequests = this.endpointRequests.get(endpoint).stream()
                    .mapToInt(r -> r.nbRequest)
                    .sum();
            return (endpoint.datacenterLatency - entry.getValue()) * endpointNbRequests;
        }).sum();
        return Comparator.comparingLong(capacitor::apply).reversed();
    }

    static class VideoStatus {

        private final int cached;

        private final long gains;

        VideoStatus(boolean cached, long gains) {
            this.cached = cached ? 1 : 0;
            this.gains = gains;
        }

    }

    static class CacheFillRunner {

        private final Cache cache;

        private final Map<Endpoint, List<Request>> endpointRequests;

        private final Map<Endpoint, List<Cache>> endpointCaches;

        CacheFillRunner(final Cache cache, final Map<Endpoint, List<Request>> endpointRequests,
                        final Map<Endpoint, List<Cache>> endpointCaches) {
            this.cache = cache;
            this.endpointRequests = endpointRequests;
            this.endpointCaches = endpointCaches;
        }

        void run() {
            final Map<Video, VideoStatus> videos = cache.endpoints.keySet().stream()
                    .flatMap(e -> this.endpointRequests.get(e).stream())
                    .map(r -> r.video)
                    .distinct()
                    .collect(Collectors.toMap(Function.identity(), v -> new VideoStatus(false, 0L)));

            cache.endpoints.entrySet().forEach(entry -> {
                final int diff = entry.getKey().datacenterLatency - entry.getValue();
                endpointRequests.get(entry.getKey()).forEach(request -> {
                    final boolean found = endpointCaches.get(entry.getKey()).stream()
                            .anyMatch(c -> c.videos.contains(request.video));

                    videos.compute(request.video, (k, vs) -> new VideoStatus(found, vs.gains + diff * request.nbRequest));
                });
            });

            this.internalRun(videos);
        }

        private void internalRun(final Map<Video, VideoStatus> videos) {
            // On cherche le endpoint qui est le plus prêt
            final int availableSpace = cache.getAvailableSpace();

            // Pour chacun de ces endpoints
            videos.entrySet().stream()
                    // On trie par rapport à la somme des requêtes de la vidéo
                    .sorted(videoComparator())
                    // Plus besoin de la map
                    .map(Entry::getKey)
                    // Pour les vidéos qu'il est possible de cacher
                    .filter(v -> v.weight <= availableSpace)
                    // Pour les vidéos qui ne sont pas déjà dans le cache
                    .filter(video -> !cache.videos.contains(video))
                    // Pour les vidéos que l'on a pas déjà tentée
                    .findFirst()
                    .ifPresent(video -> {
                        // System.out.printf("Storing video %d in cache %d\n", request.video.id, cache.id);
                        cache.videos.add(video);
                        internalRun(videos);
                    });
        }

        private Comparator<? super Entry<Video, VideoStatus>> videoComparator() {
            final Comparator<Entry<Video, VideoStatus>> gains = Comparator.<Entry<Video, VideoStatus>>comparingLong(e -> e.getValue()
                    .gains).reversed();
            final Comparator<Entry<Video, VideoStatus>> cacheOccurrence = Comparator.comparingInt(e -> e.getValue().cached);
            return cacheOccurrence.thenComparing(gains);
        }

        @Override
        public String toString() {
            return "[" + this.getClass().getSimpleName() + "] on cache " + this.cache.id;
        }
    }

}
