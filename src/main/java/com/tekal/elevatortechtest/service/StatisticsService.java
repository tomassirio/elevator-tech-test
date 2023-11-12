package com.tekal.elevatortechtest.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class StatisticsService {

    private Map<UUID, MutablePair<Long, Long>> waitingTimes = new HashMap<>();
    private Map<UUID, MutablePair<Long, Long>> travelTimes = new HashMap<>();

    public void recordWaitingTime(UUID personId, Long waitingTime) {
        waitingTimes.put(personId, MutablePair.of(waitingTime, 0L));
        log.info("Person " + personId + " starts waiting at " + waitingTime + "ms");
    }

    public void stopWaitingTime(UUID personId, Long waitingTime) {
        waitingTimes.get(personId).setRight(waitingTime);
        log.info("Person " + personId + " stops waiting at " + waitingTime + "ms");
    }

    public void recordTravelTime(UUID personId, Long travelTime) {
        travelTimes.put(personId, MutablePair.of(travelTime, 0L));
        log.info("Person " + personId + " starts travelling at " + travelTime + "ms");
    }

    public void stopTravelTime(UUID personId, Long travelTime) {
        travelTimes.get(personId).setRight(travelTime);

        log.info("Person " + personId + " stops traveling at " + travelTime + "ms");
    }

    public Double getAverageWaitingTime(Long timeSimulationStopped) {
        return calculateAverage(waitingTimes.values()
                .stream()
                .map(longLongMutablePair -> !longLongMutablePair.getRight().equals(0L) ? longLongMutablePair.getRight() - longLongMutablePair.getLeft() : timeSimulationStopped - longLongMutablePair.getLeft())
                .toList());
    }

    public Double getAverageTravelTime(Long timeSimulationStopped) {
        return calculateAverage(travelTimes.values()
                .stream()
                .map(longLongMutablePair -> !longLongMutablePair.getRight().equals(0L) ? longLongMutablePair.getRight() - longLongMutablePair.getLeft() : timeSimulationStopped - longLongMutablePair.getLeft())
                .toList());
    }

    public Long getMaximumWaitingTime(Long timeSimulationStopped) {
        return calculateMax(waitingTimes.values()
                .stream()
                .map(longLongMutablePair -> !longLongMutablePair.getRight().equals(0L) ? longLongMutablePair.getRight() - longLongMutablePair.getLeft() : timeSimulationStopped - longLongMutablePair.getLeft())
                .toList());
    }

    public Long getMinimumWaitingTime(Long timeSimulationStopped) {
        return calculateMin(waitingTimes.values()
                .stream()
                .map(longLongMutablePair -> !longLongMutablePair.getRight().equals(0L) ? longLongMutablePair.getRight() - longLongMutablePair.getLeft() : timeSimulationStopped - longLongMutablePair.getLeft())
                .toList());
    }

    // Add other methods as needed

    private Double calculateAverage(List<Long> times) {
        return times.stream()
                .mapToLong(Long::valueOf)
                .average()
                .orElse(0.0);
    }

    private Long calculateMax(List<Long> times) {
        return times.stream()
                .mapToLong(Long::valueOf)
                .max()
                .orElse(0L);
    }

    private Long calculateMin(List<Long> times) {
        return times.stream()
                .mapToLong(Long::valueOf)
                .min()
                .orElse(0L);
    }
}
