package fr.tcd.compute;

import fr.tcd.input.Input;
import fr.tcd.input.InputData;
import fr.tcd.input.InputReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author pallain - 2/28/17.
 */
//@Ignore
@RunWith(Parameterized.class)
public class PaallCacheAlgorithmTest {

    private final int bestScore;

    private Input input;

    public PaallCacheAlgorithmTest(final Input input, final int bestScore) {
        this.input = input;
        this.bestScore = bestScore;
    }

    @Parameterized.Parameters(name = "{index}: Attempt {0} with best score {1}")
    public static List<Object[]> data() {
//        return Collections.singletonList(new Object[]{Input.ME_AT_THE_ZOO, 411561L});
//        return Arrays.asList(
//                new Object[]{Input.KITTENS, 613514L},
//                new Object[]{Input.TRENDING_TODAY, 486255},
//                new Object[]{Input.VIDEOS_WORTH_SPREADING, 485975},
//                new Object[]{Input.ME_AT_THE_ZOO, 411561}
//        );

        return Arrays.<Object[]>asList(
                new Object[]{Input.KITTENS, 613514},
                new Object[]{Input.TRENDING_TODAY, 499991},
                new Object[]{Input.VIDEOS_WORTH_SPREADING, 501220},
                new Object[]{Input.ME_AT_THE_ZOO, 440205}
        );
    }

    @Test
    public void compute() throws Exception {

        // GIVEN
        final InputData inputData = new InputReader(this.input.getFilename()).read();
        final PaallCacheAlgorithm algorithm = new PaallCacheAlgorithm(inputData);
        final ResultCalculator calculator = new ResultCalculator(inputData);

        // WHEN
        algorithm.compute();

        // THEN
        assertThat(calculator.calculateAndGet())
                .as("Best scrore %d", this.bestScore)
                .isGreaterThanOrEqualTo(this.bestScore);
    }

}
