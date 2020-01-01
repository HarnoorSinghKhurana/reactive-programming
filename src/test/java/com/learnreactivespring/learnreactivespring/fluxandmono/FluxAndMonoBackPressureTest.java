package com.learnreactivespring.learnreactivespring.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> finiteFlux = Flux.range(1, 10);
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();
        finiteFlux.subscribe(System.out::println,  //elements
                System.err::println, // error handling
                () -> System.out.println("Done"), //on completion
                subscription -> subscription.request(2)); //number of requests
    }

    @Test
    public void backPressure_cancel() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();
        finiteFlux.subscribe(element -> System.out.println("element is " + element),  //elements
                e -> System.err.println(e), // error handling
                () -> System.out.println("Done"), //on completion
                subscription -> subscription.cancel()); //number of requests
    }

    @Test
    public void customizedBackPressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();
        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("value is : "+value);
                if(value == 4)
                    cancel();
            }
        }); //number of requests
    }
}
