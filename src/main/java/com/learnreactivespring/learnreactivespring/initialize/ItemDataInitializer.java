package com.learnreactivespring.learnreactivespring.initialize;

import com.learnreactivespring.learnreactivespring.document.CappedItem;
import com.learnreactivespring.learnreactivespring.document.Item;
import com.learnreactivespring.learnreactivespring.repository.CappedItemReactiveRepository;
import com.learnreactivespring.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Autowired
    private CappedItemReactiveRepository cappedItemReactiveRepository;

    @Autowired
    private MongoOperations mongoOperations;

    private List<Item> items = Arrays.asList(new Item(null, "item1", 30.0),
            new Item(null, "item2", 32.0),
            new Item(null, "item3", 30.05),
            new Item("ABC", "item4", 20.0));

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
        createCappedCollection();
        initialDataSetupForCappedCollection();
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(CappedItem.class);
        mongoOperations.createCollection(CappedItem.class, CollectionOptions.empty().maxDocuments(20).size(5000).capped());
    }

    private void initialDataSetupForCappedCollection() {
        Flux<CappedItem> cappedItemFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new CappedItem(null, "Description " + i, (100.00 + i)));
        cappedItemReactiveRepository.insert(cappedItemFlux)
                .subscribe(items ->
                        log.info("Inserted Item is " + items));
    }

    private void initialDataSetup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> {
                    System.out.println("Item Inserted via CommandLineRunner is :" + item);
                });
    }
}
