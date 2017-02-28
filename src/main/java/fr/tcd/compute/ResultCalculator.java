package fr.tcd.compute;

import fr.tcd.Request;
import fr.tcd.input.InputData;

import java.util.Map;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

/**
 * @author pallain - 3/1/17.
 */
public class ResultCalculator {

    private final long requestSum;

    private final Map<Request, Long> pointRequests;

    private InputData inputData;

    public ResultCalculator(final InputData inputData) {
        this.requestSum = inputData.requests.stream().mapToLong(request -> request.nbRequest).sum();
        this.inputData = inputData;
        this.pointRequests = inputData.requests.stream().collect(Collectors.toMap(Function.identity(), (v) -> 0L));
    }

    private static ToLongFunction<Request> requestPoint(InputData inputData) {
        return request -> {
            final int dcLatency = request.endpoint.datacenterLatency;
            return inputData.caches.stream()
                    .filter(cache -> cache.videos.contains(request.video))
                    .mapToLong(cache -> dcLatency - cache.endpoints.getOrDefault(request.endpoint, dcLatency))
                    .map(saved -> saved * request.nbRequest)
                    .max()
                    .orElse(0L);
        };
    }

    public ResultCalculator calculate() {
        inputData.requests.forEach(r -> this.pointRequests.put(r, requestPoint(inputData).applyAsLong(r)));
        return this;
    }

    public long get() {
        final long sum = this.pointRequests.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        return sum * 1000 / this.requestSum;
    }

    public long calculateAndGet() {
        return calculate().get();
    }

}
