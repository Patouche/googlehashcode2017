package fr.tcd;

import fr.tcd.compute.PaallCacheAlgorithm;
import fr.tcd.input.InputData;
import fr.tcd.input.InputReader;
import fr.tcd.result.ResultWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        try {
            final Temporal previous = Instant.now();
            System.out.printf("BEGIN Main : Files = %s \n", Arrays.asList(args));
            for (String file : args) {
                System.out.printf("Main : Read input for file %s \n", file);
                final InputData inputData = new InputReader(file).read();
                System.out.printf("Main : Algorithm \n");
                new PaallCacheAlgorithm(inputData).compute();
                System.out.printf("Main : Write output \n");
                ResultWriter.write(file, inputData.caches);
            }
            System.out.printf("END Main : Files = %s (duration: %d sec)\n", Arrays.asList(args), Duration.between(previous, Instant.now()).getSeconds());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
