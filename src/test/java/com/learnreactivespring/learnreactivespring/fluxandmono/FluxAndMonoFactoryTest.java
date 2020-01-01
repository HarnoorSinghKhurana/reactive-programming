package com.learnreactivespring.learnreactivespring.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("hsk", "hsk2");

    @Test
    public void fluxUsingIterable(){
        Flux<String> namesFlux =Flux.fromIterable(names).log();
        StepVerifier.create(namesFlux)
                .expectNext("hsk", "hsk2")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray(){
        String [] names = new String[] {"hsk", "hsk2"};
        Flux<String> namesFlux = Flux.fromArray(names);
        StepVerifier.create(namesFlux)
                .expectNext("hsk", "hsk2")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStreams(){
        Flux<String> namesFlux = Flux.fromStream(names.stream());
        StepVerifier.create(namesFlux)
                .expectNext("hsk", "hsk2")
                .verifyComplete();
    }

    @Test
    public void MonoUsingJustOrEmpty(){
        Mono<String> namesMono = Mono.justOrEmpty(null);
        StepVerifier.create(namesMono).verifyComplete();
    }

    @Test
    public void MonoUsingSupplier(){
        Supplier<String> stringSupplier = () -> "hsk";
        Mono<String> namesMono = Mono.fromSupplier(stringSupplier);
        StepVerifier.create(namesMono.log()).expectNext("hsk").verifyComplete();
    }

    @Test
    public void fluxUsingRange(){
        Flux<Integer> integers = Flux.range(1,5);
        StepVerifier.create(integers.log())
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }
}
