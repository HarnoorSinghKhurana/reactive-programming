package com.learnreactivespring.learnreactivespring.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200))
                .log(); //starts from 0
        infiniteFlux.subscribe(System.out::println);
        Thread.sleep(3000);
    }

    @Test
    public void infiniteSequence_Test() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200))//starts from 0
                .take(3)
                .log();
        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap_Test() throws InterruptedException {
        Flux<Integer> infiniteFlux = Flux.interval(Duration.ofMillis(200))//starts from 0
                .map( l -> new Integer(l.intValue()))
                .take(3)
                .log();
        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap_Test_Delay() throws InterruptedException {
        Flux<Integer> infiniteFlux = Flux.interval(Duration.ofMillis(200))//starts from 0
                .delayElements(Duration.ofSeconds(1))
                .map( l -> new Integer(l.intValue()))
                .take(3)
                .log();
        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }
}
