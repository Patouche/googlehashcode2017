package fr.tcd.compute;

import fr.tcd.Cache;
import fr.tcd.Video;
import fr.tcd.input.Input;
import fr.tcd.input.InputData;
import fr.tcd.input.InputReader;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author patouche - 3/2/17.
 */
public class ResultCalculatorTest {

    @Test
    public void calculateAndGet() throws Exception {
        // GIVEN
        final InputData inputData = new InputReader(Input.ME_AT_THE_ZOO.getFilename()).read();
        final InputDataFilters filters = new InputDataFilters(inputData);
        filters.getCache(0).videos.addAll(filters.getVideos(1, 16, 5, 8, 82, 19, 10, 99, 7, 13, 89, 26, 46, 31, 0, 15, 65));
        filters.getCache(1).videos.addAll(filters.getVideos(16, 0, 1, 6, 34, 10, 3, 46, 13, 81, 5, 99, 32, 7, 8, 17, 89, 26, 31, 15, 65));
        filters.getCache(2).videos.addAll(filters.getVideos(1, 2, 4, 10, 5, 0, 16, 99, 82, 13, 62, 19, 65, 23, 7, 8, 26, 43, 31));
        filters.getCache(3).videos.addAll(filters.getVideos(1, 2, 4, 10, 5, 0, 16, 3, 24, 27, 54, 26, 8));
        filters.getCache(4).videos.addAll(filters.getVideos(16, 0, 1, 3, 13, 4, 81, 10, 32, 8, 17, 7));
        filters.getCache(5).videos.addAll(filters.getVideos(1, 2, 4, 10, 5, 0, 16, 81, 3, 24, 32, 13, 27, 21, 7, 54, 17, 44, 26, 8, 30));
        filters.getCache(6).videos.addAll(filters.getVideos(1, 2, 4, 10, 5, 0, 16, 3, 24, 13, 27, 21, 54, 44, 26, 30, 8));
        filters.getCache(7).videos.addAll(filters.getVideos(65, 7, 1, 11, 5, 74, 16, 4, 27, 0, 3, 24, 13, 26, 44, 54, 21, 8));
        filters.getCache(8).videos.addAll(filters.getVideos(1, 16, 5, 8, 82, 19, 4, 6, 0, 34, 3, 13, 10, 26, 44, 2, 24, 21, 27, 30, 54));
        filters.getCache(9).videos.addAll(filters.getVideos(1, 16, 5, 8, 82, 19, 4, 10, 62, 0, 23, 2, 43, 30));

        // WHEN
        final long actual = new ResultCalculator(inputData).calculateAndGet();

        // THEN
        Assertions.assertThat(actual).as("calculated points").isEqualTo(157850L);
    }



    static class InputDataFilters {

        private final InputData inputData;

        public InputDataFilters(InputData inputData) {
            this.inputData = inputData;
        }

        public Video getVideo(final int id) {
            return inputData.videos.stream()
                    .filter(v -> id == v.id)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Video with id '" + id + "' not found"));
        }

        public List<Video> getVideos(final int... ids) {
            final List<Integer> idList = IntStream.of(ids).boxed().collect(Collectors.toList());
            return inputData.videos.stream()
                    .filter(v -> idList.contains(v.id))
                    .collect(Collectors.toList());
        }

        public Cache getCache(final int id) {
            return inputData.caches.stream()
                    .filter(c -> c.id == 0)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Cache with id '" + id + "' not found"));
        }

    }

}