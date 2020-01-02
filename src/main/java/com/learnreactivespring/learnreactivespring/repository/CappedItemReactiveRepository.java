package com.learnreactivespring.learnreactivespring.repository;

import com.learnreactivespring.learnreactivespring.document.CappedItem;
import com.learnreactivespring.learnreactivespring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface CappedItemReactiveRepository extends ReactiveMongoRepository<CappedItem, String> {

    @Tailable
    Flux<CappedItem> findItemsBy();
}
