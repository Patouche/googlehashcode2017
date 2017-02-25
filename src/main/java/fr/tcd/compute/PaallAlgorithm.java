package fr.tcd.compute;

import fr.tcd.Cache;
import fr.tcd.Endpoint;
import fr.tcd.Request;
import fr.tcd.Video;
import fr.tcd.input.InputData;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
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
        // On définie le compteur d'avancement
        final AtomicLong counter = new AtomicLong(0L);

        // Pour chaque endpoint
        this.inputData.endpoints.stream()
                // Les endpoints connectés à un maximum de cache en premier
                // Mais ça devrait plutôt être ceux recevant le plus de requêtes importantes il me semble
                // Mais ça entraine de récuper les requêtes pour chaque endpoints
                .sorted(Comparator.<Endpoint>comparingInt(e -> e.datacenterLatency * e.datacenterLatency))
                .forEach((endpoint -> {

                    System.out.printf("[%d/%d] Compute endpoint: %d \n", counter.getAndIncrement(), inputData.nbEndpoints, endpoint.id);

                    // On commence par définir les vidéos qui ont déjà été ajouté en cache par les autres endpoints
                    final Set<Video> videoAccumulators = this.inputData.caches.stream()
                            .flatMap(cache -> cache.videos.stream())
                            .collect(Collectors.toSet());

                    // On récupère les requêtes associées à ce endpoint
                    final List<Request> requests = this.inputData.requests.stream()
                            .filter(r -> r.endpoint.id == endpoint.id)
                            // On trie par les requêtes les plus fréquentes & ayant le poids le plus élevés puis par le nombre
                            .sorted(Comparator.<Request>comparingInt(r -> r.nbRequest * r.video.weight)
                                    // Fait descendre le score ... Mais pourquoi ?
                                    // .reversed()
                                    .thenComparingInt(r -> r.nbRequest)
                            )
                            .collect(Collectors.toList());

                    // Pour chaque cache
                    this.inputData.caches.stream()
                            // Qui a de la place
                            .filter(Cache::hasAvailableSpace)
                            // Qui est associé au endpoint
                            .filter(c -> c.endpoints.keySet().stream().anyMatch(e -> e.id == endpoint.id))
                            .forEach(cache -> {
                                // Pour toutes les requests
                                requests.stream()
                                        // Uniquement les vidéos dont la taille est inférieur à la taille du cache
                                        .filter(r -> r.video.weight <= cache.getAvailableSpace())
                                        // Uniquement les vidéos qui n'ont pas été associée à un cache pour le endpoint
                                        .filter(r -> videoAccumulators.stream().noneMatch(v -> v.id == r.video.id))
                                        .findFirst()
                                        // Si on en trouve une
                                        .ifPresent(request -> {
                                            // On l'ajoute à l'accumulator pour éviter les doublons
                                            videoAccumulators.add(request.video);
                                            // On l'ajoute au cache pour le résultat
                                            cache.videos.add(request.video);
                                            // Plus besoin de supprimer puisque on a l'accumulator sur les vidéos
                                            // requests.remove(request);
                                        });

                            });

                }));
    }
}
