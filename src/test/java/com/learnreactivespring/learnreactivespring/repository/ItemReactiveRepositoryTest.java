package com.learnreactivespring.learnreactivespring.repository;

import com.learnreactivespring.learnreactivespring.document.Item;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
class ItemReactiveRepositoryTest {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    private List<Item> items = Arrays.asList(new Item("1", "item1", 30.0),
            new Item("2", "item2", 20.0));

    @Test
    public void getAllItems() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item added is : " + item);
                }).blockLast();
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void getById(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item added is : " + item);
                }).blockLast();
        StepVerifier.create(itemReactiveRepository.findById("1"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    public void getByDescription(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item added is : " + item);
                }).blockLast();
        StepVerifier.create(itemReactiveRepository.findByDescription("item1"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    public void saveItem(){
        Item item = new Item(null, "Google Home", 200.00);
        Mono<Item> savedItem = itemReactiveRepository.save(item);
        StepVerifier.create(savedItem.log("log is : "))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().matches("Google Home"))
                .verifyComplete();
    }

    @Test
    public void updateItem(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item added is : " + item);
                }).blockLast();
        Double newPrice = 20.22;
        Flux<Item> updatedItems = itemReactiveRepository.findByDescription("item1")
                .map(item -> {
                     item.setPrice(newPrice);
                     return item;
                }).flatMap( item -> {
                    return itemReactiveRepository.save(item);
                });
        StepVerifier.create(updatedItems.log("log is : "))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null && item1.getPrice() == newPrice)
                .verifyComplete();
    }

    @Test
    public void deleteItem(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item added is : " + item);
                }).blockLast();
        itemReactiveRepository.findById("1")
                .map(Item::getId)
                .flatMap(id -> {
                    return itemReactiveRepository.deleteById(id);
                }).block();
        StepVerifier.create(itemReactiveRepository.findAll().log("log is : "))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }
}