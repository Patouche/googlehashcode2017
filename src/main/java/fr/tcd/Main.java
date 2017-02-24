package fr.tcd;

import fr.tcd.compute.PaallAlgorithm;
import fr.tcd.input.InputData;
import fr.tcd.input.InputReader;
import fr.tcd.result.ResultWriter;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        try {
            for (String file : args) {
                System.out.printf("Main : Read input");
                final InputData inputData = new InputReader(file).read();
                System.out.printf("Main : Algorithm");
                new PaallAlgorithm(inputData).compute();
                System.out.printf("Main : Write output");
                ResultWriter.write(file, inputData.caches);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
