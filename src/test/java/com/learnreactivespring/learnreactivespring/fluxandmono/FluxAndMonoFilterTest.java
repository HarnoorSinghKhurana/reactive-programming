package com.learnreactivespring.learnreactivespring.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = Arrays.asList("hsk", "hsk2");

    @Test
    public void filterTest(){
        Flux<String> namesFlux =Flux.fromIterable(names)
                .filter(s-> s.startsWith("h"))
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("hsk", "hsk2")
                .verifyComplete();
    }

    @Test
    public void filterTestLength(){
        Flux<String> namesFlux =Flux.fromIterable(names)
                .filter(s-> s.length() == 3)
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("hsk")
                .verifyComplete();
    }

}
