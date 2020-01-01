package com.learnreactivespring.learnreactivespring.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void testFlux() {
        Flux<String> stringFlux = Flux.just("str1", "str2", "str3")
                //.concatWith(Flux.error(new RuntimeException("Ex occurred")))
                .concatWith(Flux.just("error"))
                .log();

        stringFlux.subscribe(System.out::println,
                e -> System.out.println("Exception is " + e),
                () -> System.out.println("Complete"));

    }

    @Test
    public void fluxElementsVerify_WithoutError() {
        Flux<String> stringFlux = Flux.just("str1", "str2", "str3")
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("str1")
                .expectNext("str2")
                .expectNext("str3")
                .verifyComplete();

    }

    @Test
    public void fluxElementsVerify_WithError() {
        Flux<String> stringFlux = Flux.just("str1", "str2", "str3")
                .concatWith(Flux.error(new RuntimeException("Ex occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("str1")
                .expectNext("str2")
                .expectNext("str3")
                //.expectError(RuntimeException.class)
                .expectErrorMessage("Ex occurred")
                .verify();

    }

    @Test
    public void fluxElementsCount_WithoutError() {
        Flux<String> stringFlux = Flux.just("str1", "str2", "str3")
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();

    }

    @Test
    public void fluxElementsVerify_WithError1() {
        Flux<String> stringFlux = Flux.just("str1", "str2", "str3")
                .concatWith(Flux.error(new RuntimeException("Ex occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("str1", "str2", "str3")
                .expectErrorMessage("Ex occurred")
                .verify();

    }

    @Test
    public void monoTest() {
        Mono<String> stringMono =  Mono.just("str1").log();
        StepVerifier.create(stringMono).expectNextCount(1).verifyComplete();
    }

    @Test
    public void monoTest_Error() {
        //Mono<String> stringMono =  Mono.error(new RuntimeException("ABC"));
        //StepVerifier.create(stringMono).expectError().verify();
        StepVerifier.create(Mono.error(new RuntimeException()).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
