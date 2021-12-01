package org.togetherjava.aoc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.runner.RunnerException;
import org.reflections.Reflections;
import org.togetherjava.aoc.api.AbstractDay;
import org.togetherjava.aoc.api.AocConfig;

import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class Aoc {

    private static Aoc instance;

    private static final ZoneId EASTERN_STANDARD_TIME = ZoneId.of("EST5EDT");

    private final Map<Integer, AbstractDay> days = new HashMap<>(25);

    @Getter
    private AocConfig config;

    public Aoc() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        config = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("/aoc-config.json")), AocConfig.class);
        Reflections reflections = new Reflections("org.togetherjava.aoc.solutions");
        Set<Class<? extends AbstractDay>> classes = reflections.getSubTypesOf(AbstractDay.class);
        classes.removeIf(c -> c.getSimpleName().contains("_"));
        for(Class<? extends AbstractDay> c : classes) {
            AbstractDay day = (AbstractDay) c.getConstructors()[0].newInstance();
            days.put(day.getDay(), day);
            log.info("Loaded %s".formatted(c.getName()));
        }
    }

    public AbstractDay getDay() {
        ZonedDateTime now = ZonedDateTime.now(EASTERN_STANDARD_TIME);
        return getDay(now.getDayOfMonth());
    }

    public AbstractDay getDay(int day) {
        return days.computeIfAbsent(day, d -> { throw new RuntimeException("Day %d cannot be found in org.togetherjava.aoc.solutions".formatted(day)); } );
    }

    //TODO: load session cookie from config file
    @SneakyThrows
    public static Aoc getInstance() {
        if(instance == null) {
            instance = new Aoc();
        }
        return instance;
    }

    public static void main(String[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException, RunnerException {
        log.info("Day 1 output: {}", Aoc.getInstance().getDay(1).part1Solution().toString());
        log.info("Day 2 output: {}", Aoc.getInstance().getDay(1).part2Solution().toString());
        /*Options opt = new OptionsBuilder()
                .include("Day..")
                .forks(1)
                .build();

        new Runner(opt).run();*/
    }
}
