package com.learnreactivespring.learnreactivespring.controller.v1;

import com.learnreactivespring.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.learnreactivespring.document.CappedItem;
import com.learnreactivespring.learnreactivespring.repository.CappedItemReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemStreamControllerTest {

    @Autowired
    private CappedItemReactiveRepository cappedItemReactiveRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testStreamItems() {
        mongoOperations.dropCollection(CappedItem.class);
        mongoOperations.createCollection(CappedItem.class, CollectionOptions.empty().maxDocuments(20).size(5000).capped());
        Flux<CappedItem> cappedItemFlux = Flux.interval(Duration.ofMillis(1))
                .map(i -> new CappedItem(null, "Description " + i, (100.00 + i)))
                .take(5);
        cappedItemReactiveRepository.insert(cappedItemFlux)
                .doOnNext(items ->
                        System.out.println("Inserted Item is " + items))
                .blockLast();

        Flux<CappedItem> cappedItemFluxTest = webTestClient.get().uri(ItemConstants.ITEM_STREAM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(CappedItem.class)
                .getResponseBody()
                .take(5);
        StepVerifier.create(cappedItemFluxTest)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }
}