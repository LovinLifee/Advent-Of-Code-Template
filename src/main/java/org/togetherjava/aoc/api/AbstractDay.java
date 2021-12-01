package org.togetherjava.aoc.api;

import lombok.Getter;
import lombok.extern.java.Log;
import org.openjdk.jmh.annotations.*;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@Getter
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public abstract class AbstractDay<T> {

    private final int year;
    private final int day;
    private final URI baseURI;
    private final PuzzleInput input;

    public AbstractDay(int year, int day) {
        this.year = year;
        this.day = day;
        this.baseURI = URI.create(String.format("https://adventofcode.com/%d/day/%d/", year, day));
        this.input = new PuzzleInput(baseURI.resolve("input"));
    }

    //TODO: add micro benchmarking with JMH
    public abstract T part1Solution();
    public abstract T part2Solution();

    @Benchmark
    public void benchmarkPart1() {
        part1Solution();
    }

    @Benchmark
    public void benchmarkPart2() {
        part2Solution();
    }

    /**
     * @param level - the stage at which you are trying to answer (either 1 or 2)
     * @param answer answer that will be submitted, implicitly sends .toString()
     * @return whether or not you have submitted the correct answer
     */
    protected boolean submitAnswer(int level, Object answer) {
        URI answerURI = baseURI.resolve("answer");
        HttpResponse<Stream<String>> response = HttpUtils.sendFormData(answerURI, Map.of("level", level, "answer", answer));
        String responseBody = response.body().collect(Collectors.joining("\n"));
        if(response.statusCode() != 200) {
            System.out.println(responseBody);
            log.severe("This usually occurs if you are either not logged in, or the challenge hasn't started yet.");
        }
        boolean alreadyCompleted = responseBody.contains("Did you already complete it?"); //TODO: also check for successful solve
        return alreadyCompleted;
    }

    public final boolean submitPart1() {
        return submitAnswer(1, part1Solution());
    }

    public final boolean submitPart2() {
        return submitAnswer(2, part2Solution());
    }
}
