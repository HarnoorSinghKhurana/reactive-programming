package com.learnreactivespring.learnreactivespring.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> merge = Flux.merge(flux1, flux2);
        StepVerifier.create(merge.log())
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMerge_Delay() { //order changes
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> merge = Flux.merge(flux1, flux2);
        StepVerifier.create(merge.log())
                .expectNextCount(6)
                //.expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMerge_Delay_OrderMaintained() { //order does not change
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> merge = Flux.concat(flux1, flux2);
        StepVerifier.create(merge.log())
                //.expectNextCount(6)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMerge_Delay_OrderMaintained_VirtualTime() { //order does not change, time reduced to help compile faster - with virtual time
        VirtualTimeScheduler.getOrSet();
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> merge = Flux.concat(flux1, flux2);
        StepVerifier.withVirtualTime(() -> merge.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingZip() { //order changes
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> merge = Flux.zip(flux1, flux2, (t1,t2) ->{
            return t1.concat(t2); // AD , BE, CF
        });
        StepVerifier.create(merge.log())
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
}
