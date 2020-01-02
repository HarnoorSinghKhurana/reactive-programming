package com.learnreactivespring.learnreactivespring.handler;

import com.learnreactivespring.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.learnreactivespring.document.Item;
import com.learnreactivespring.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemsHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    private List<Item> items = Arrays.asList(new Item(null, "item1", 30.0),
            new Item(null, "item2", 32.0),
            new Item(null, "item3", 30.05),
            new Item("ABC", "item4", 20.0));

    @Test
    public void getAllTest() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item Inserted is : " + item);
                })
                .blockLast();
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getAllTest_approach2() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item Inserted is : " + item);
                })
                .blockLast();
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach(item -> {
                        assertTrue(item.getId() != null);
                    });
                });
    }

    @Test
    public void getAllTest_approach3() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item Inserted is : " + item);
                })
                .blockLast();
        Flux<Item> items = webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();
        StepVerifier.create(items.log("Value from network : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItem_Test() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item Inserted is : " + item);
                })
                .blockLast();
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"ABC")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.price", 20.0);
    }

    @Ignore
    @Test
    public void getOneItem_NotFound() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item Inserted is : " + item);
                })
                .blockLast();
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"111")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItemTest() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item Inserted is : " + item);
                })
                .blockLast();
        Item newItem = new Item("3","Item5",1000.00);
        webTestClient.post().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newItem), Item.class)
                .exchange()
                .expectStatus().isOk();
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);

    }

    @Test
    public void deleteItemTest() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item Inserted is : " + item);
                })
                .blockLast();
        webTestClient.delete().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }


    @Test
    public void updateItemTest() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Item Inserted is : " + item);
                })
                .blockLast();
        Item newItem = new Item("ABC","Updated Item",1000.00);
        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newItem), Item.class)
                .exchange()
                .expectStatus().isOk();
        Flux<Item> items = webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();
        StepVerifier.create(items.log("Value from network : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void testUpdateItem_notFound() {
        double newPrice=129.99;
        Item item = new Item(null,"Beats HeadPhones", newPrice);
        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "DEF") //no record with this ids
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Ignore
    @Test
    public void runtimeException() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/runtimeException"))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$message" ,"Runtime Exception Occurred");
    }

}