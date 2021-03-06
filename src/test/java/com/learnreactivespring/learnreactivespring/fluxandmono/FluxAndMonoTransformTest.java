package com.learnreactivespring.learnreactivespring.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("hsk", "hsk2");

    @Test
    public void transFormTest() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase()).log();
        StepVerifier.create(stringFlux)
                .expectNext("HSK", "HSK2")
                .verifyComplete();

    }

    @Test
    public void transFormTest_Length() {
        Flux<Integer> stringFlux = Flux.fromIterable(names)
                .map(s -> s.length()).log();
        StepVerifier.create(stringFlux)
                .expectNext(3, 4)
                .verifyComplete();

    }

    @Test
    public void transFormTest_LengthRepeat() {
        Flux<Integer> stringFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1)
                .log();
        StepVerifier.create(stringFlux)
                .expectNext(3, 4, 3, 4)
                .verifyComplete();

    }

    @Test
    public void transformUsingMap_Filter() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(l -> l.length() > 3)
                .map(s -> s.toUpperCase())
                .repeat(1)
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("HSK2", "HSK2")
                .verifyComplete();

    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                }).log(); // dbCall or External Service Call s-> returns flux of String
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_UsingParallel() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) //Flux<String>
                .window(2) //Flux<Flux<String>>
                .flatMap(s ->
                        s.map(this::convertToList).subscribeOn(parallel()))  //Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s))  //Flux<String>
                .log();

        // dbCall or External Service Call s-> returns flux of String
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "new Value");
    }


    @Test
    public void transformUsingFlatMap_UsingParallel_MaintainOrder() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) //Flux<String>
                .window(2) //Flux<Flux<String>>
                /*.concatMap(s ->
                        s.map(this::convertToList).subscribeOn(parallel())) */ //Flux<List<String>>
                .flatMapSequential(s ->
                        s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s))  //Flux<String>
                .log();

        // dbCall or External Service Call s-> returns flux of String
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

}
