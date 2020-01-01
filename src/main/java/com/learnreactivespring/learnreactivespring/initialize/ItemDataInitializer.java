package com.learnreactivespring.learnreactivespring.initialize;

import com.learnreactivespring.learnreactivespring.document.Item;
import com.learnreactivespring.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    private List<Item> items = Arrays.asList(new Item(null, "item1", 30.0),
            new Item(null, "item2", 32.0),
            new Item(null, "item3", 30.05),
            new Item("ABC", "item4", 20.0));

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
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
