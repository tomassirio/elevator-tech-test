package com.tekal.elevatortechtest.service.impl;

import com.tekal.elevatortechtest.model.Elevator;
import com.tekal.elevatortechtest.model.request.ElevatorCall;
import com.tekal.elevatortechtest.service.ElevatorService;
import com.tekal.elevatortechtest.service.PersonService;
import com.tekal.elevatortechtest.service.StatisticsService;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SstfElevatorService extends ElevatorCallServer implements ElevatorService {

    private final Set<Elevator> elevators;
    private final Queue<ElevatorCall> elevatorCalls;

    @Autowired
    public SstfElevatorService(Set<Elevator> elevators, Queue<ElevatorCall> elevatorCalls, PersonService personService, StatisticsService statisticsService) {
        super(personService, statisticsService);
        this.elevators = elevators;
        this.elevatorCalls = elevatorCalls;
        startElevatorServiceThread();
    }

    @Override
    public void processElevatorCall(ElevatorCall elevatorCall) {
        log.info("Processing Elevator Call: " + elevatorCall.getCalledFromFloor() + " -> " + elevatorCall.getDestinationFloor());
        elevatorCalls.offer(elevatorCall);
    }

    @Override
    protected void serveElevatorCalls() {
        synchronized (elevatorCalls) {
            if (elevatorCalls.isEmpty()) {
                log.trace("No elevator calls to serve");
                return;
            }
            synchronized (elevators) {
                for (Elevator elevator : elevators.stream().filter(elevator -> !elevator.isMoving()).toList()) {
                    ElevatorCall elevatorCall = getNextElevatorCall(elevator);
                    if (elevatorCall != null) {
                        log.info("Elevator " + elevator.getElevatorId() + " is not moving, serving call");
                        serveElevatorCall(elevator, elevatorCall);
                    }
                }
            }
        }
    }

    private ElevatorCall getNextElevatorCall(Elevator elevator) {
        synchronized (elevatorCalls) {
            if (elevatorCalls.isEmpty()) {
                return null;
            }

            // Find the elevator call with the shortest seek time
            Optional<ElevatorCall> nextCall = elevatorCalls.stream()
                    .min(Comparator.comparingInt(call -> Math.abs(call.getCalledFromFloor() - elevator.getCurrentFloor())));

            if (nextCall.isPresent()) {
                elevatorCalls.remove(nextCall.get());
                return nextCall.get();
            }

            return null;
        }
    }

    @Async
    protected void startElevatorServiceThread() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .subscribe(tick -> serveElevatorCalls(),
                        throwable -> log.error("Error in elevator service thread", throwable),
                        () -> log.info("Elevator service thread stopped"));
    }
}
