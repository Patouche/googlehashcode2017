package fr.tcd.input;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author patouche - 2/24/17.
 */
public enum Input {

    KITTENS("kittens.in"),
    ME_AT_THE_ZOO("me_at_the_zoo.in"),
    TRENDING_TODAY("trending_today.in"),
    VIDEOS_WORTH_SPREADING("videos_worth_spreading.in"),;

    private static final Map<String, Input> MAPPING = Stream.of(values()).collect(Collectors.toMap(i -> i.filename, Function.identity()));

    private final String filename;

    Input(final String filename) {
        this.filename = filename;
    }

    public static Input get(String filename) {
        return MAPPING.get(filename);
    }

    public String getFilename() {
        return filename;
    }
}
