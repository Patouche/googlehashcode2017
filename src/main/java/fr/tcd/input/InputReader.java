package fr.tcd.input;

import fr.tcd.Cache;
import fr.tcd.Endpoint;
import fr.tcd.Request;
import fr.tcd.Video;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author patouche - 2/24/17.
 */
public class InputReader {

    private final Input input;

    public InputReader(String filename) {
        this.input = Input.get(filename);
    }

    public InputData read() {
        System.out.println("initData");

        final Scanner in = new Scanner(ClassLoader.getSystemResourceAsStream(input.getFilename()));
        final int nbVideos = in.nextInt();
        final int nbEndpoints = in.nextInt();
        final int nbRequestDescriptions = in.nextInt();
        final int nbCaches = in.nextInt();
        final int cacheSize = in.nextInt();

        System.out.printf("nbVideos : %d \n", nbVideos);
        System.out.printf("nbEndpoints : %d \n", nbEndpoints);
        System.out.printf("nbRequestDescriptions : %d \n", nbRequestDescriptions);
        System.out.printf("nbCaches : %d \n", nbCaches);
        System.out.printf("cacheSize : %d \n", cacheSize);

        final List<Cache> caches = IntStream.range(0, nbCaches)
                .mapToObj(cacheId -> new Cache(cacheId, cacheSize))
                .collect(Collectors.toList());

        final List<Video> videos = IntStream.range(0, nbVideos)
                .mapToObj((i) -> new Video().setId(i).setWeight(in.nextInt()))
                .collect(Collectors.toList());

        final List<Endpoint> endpoints = new ArrayList<>();
        for (int endpointId = 0; endpointId < nbEndpoints; endpointId++) {
            final int datacenterLatency = in.nextInt();
            final int numberConnectedCaches = in.nextInt();

            final Endpoint endpoint = new Endpoint()
                    .setId(endpointId)
                    .setDatacenterLatency(datacenterLatency)
                    .setNumberConnectedCaches(numberConnectedCaches);

            for (int i = 0; i < numberConnectedCaches; i++) {
                final int cacheId = in.nextInt();
                final int cacheLatency = in.nextInt();
//                System.out.println("cacheId: " + cacheId);
//                System.out.println("cacheLatency: " + cacheLatency);
                caches.stream()
                        .filter((c) -> c.id == cacheId)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Cache with id " + cacheId + " not found"))
                        .addEnPoint(endpoint, cacheLatency);
            }
            System.out.printf("Creating endpoint : %s \n", endpoint);
            endpoints.add(endpoint);
        }

        final List<Request> requests = new ArrayList<>();
        for (int requestId = 0; requestId < nbRequestDescriptions; requestId++) {
            if (requestId % 100 == 0) {
                System.out.println("requestId: " + requestId + "/" + nbRequestDescriptions);
            }

            int videoId = in.nextInt();
            int endpointId = in.nextInt();
            int nbRequest = in.nextInt();

            final Video video = videos.stream()
                    .filter((v) -> v.id == videoId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Video with id " + videoId + " not found"));

            final Endpoint endpoint = endpoints.stream()
                    .filter((e) -> e.id == endpointId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Endpoint with id " + endpointId + " not found"));
            final Request request = new Request(requestId, video, endpoint, nbRequest);
            requests.add(request);
        }

        System.out.println("initData END");
        return new InputData(
                nbVideos,
                nbEndpoints,
                nbRequestDescriptions,
                nbCaches,
                cacheSize,
                videos,
                caches,
                Collections.unmodifiableList(endpoints),
                Collections.unmodifiableList(requests)
        );
    }
}
