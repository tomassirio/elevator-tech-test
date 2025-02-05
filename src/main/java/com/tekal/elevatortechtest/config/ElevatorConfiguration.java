package com.tekal.elevatortechtest.config;

import com.tekal.elevatortechtest.model.Elevator;
import com.tekal.elevatortechtest.model.factory.ElevatorFactory;
import com.tekal.elevatortechtest.model.request.ElevatorCall;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class ElevatorConfiguration {
    @Bean
    public Set<Elevator> elevatorSet() {
        return Stream.of(ElevatorFactory.createElevator(), ElevatorFactory.createElevator(), ElevatorFactory.createElevator()).collect(Collectors.toUnmodifiableSet());
    }

    @Bean
    public Queue<ElevatorCall> elevatorCalls() {
        return new ConcurrentLinkedQueue<>();
    }
}
