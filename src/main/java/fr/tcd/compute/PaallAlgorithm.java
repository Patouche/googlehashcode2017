package fr.tcd.compute;

import fr.tcd.Cache;
import fr.tcd.Endpoint;
import fr.tcd.Request;
import fr.tcd.input.InputData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author patouche - 2/24/17.
 */
public class PaallAlgorithm implements Algorithm {

    private final InputData inputData;

    public PaallAlgorithm(final InputData inputData) {
        this.inputData = inputData;
    }

    @Override
    public void compute() {

        // Pour chaque endpoint
        this.inputData.endpoints.stream()
                // Les endpoints connectés à un maximum de cache en premier
                .sorted(Comparator.<Endpoint>comparingInt(e -> e.numberConnectedCaches).reversed())
                .forEach((endpoint -> {

                    // On récupère les requêtes associées à ce endpoint
                    final List<Request> requests = this.inputData.requests.stream()
                            .filter(r -> r.endpoint.id == endpoint.id)
                            // On trie par les requêtes les plus fréquentes & ayant le poids le plus élevés puis par le nombre
                            .sorted(Comparator.<Request>comparingInt(Request::getKnapsackWeigtht)
//                                    .reversed()
                                    .thenComparingInt(r -> r.nbRequest)
                            )
                            .collect(Collectors.toList());

                    // Pour chaque cache
                    this.inputData.caches.stream()
                            // Ayant de la place
                            .filter(Cache::hasAvailableSpace)
                            // Associé au endpoint
                            .filter(c -> c.endpoints.keySet().stream().anyMatch(e -> e.id == endpoint.id))
                            .forEach(cache -> {
                                // On cherche la vidéo la plus interessante à mettre en cache
                                requests.stream()
                                        // On ne prend que les vidéos dont la taille est inférien à la taille du cache
                                        .filter(r -> r.video.weight <= cache.getAvailableSpace())
                                        .filter(r -> cache.videos.stream().noneMatch(v -> v.id == r.video.id))
                                        .findFirst()

                                        // Si on en trouve une
                                        .ifPresent(request -> {
                                            cache.videos.add(request.video);
                                            requests.remove(request);
                                        });

                            });

                }));
    }
}
