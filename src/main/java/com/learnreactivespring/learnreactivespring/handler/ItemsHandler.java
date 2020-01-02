package com.learnreactivespring.learnreactivespring.handler;

import com.learnreactivespring.learnreactivespring.document.CappedItem;
import com.learnreactivespring.learnreactivespring.document.Item;
import com.learnreactivespring.learnreactivespring.repository.CappedItemReactiveRepository;
import com.learnreactivespring.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;


@Component
public class ItemsHandler {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Autowired
    private CappedItemReactiveRepository cappedItemReactiveRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepository.findById(id);
        return itemMono.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(item))
                        .switchIfEmpty(notFound));
    }

    public Mono<ServerResponse> saveItem(ServerRequest serverRequest) {
        Mono<Item> monoItem = serverRequest.bodyToMono(Item.class);
        return monoItem.flatMap(item ->
                ServerResponse.ok()
                        .body(itemReactiveRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Void> deleted = itemReactiveRepository.deleteById(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleted, Void.class);

    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap(item -> {
                    Mono<Item> itemMono = itemReactiveRepository.findById(id)
                            .flatMap(currentItem -> {
                                currentItem.setDescription(item.getDescription());
                                currentItem.setPrice(item.getPrice());
                                return itemReactiveRepository.save(currentItem);
                            });
                    return itemMono;
                });
        return updatedItem.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(item)))
                        .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> itemsException(ServerRequest serverRequest) {
        throw new RuntimeException("Runtime Exception Occurred");
    }

    public Mono<ServerResponse> itemsStream(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(cappedItemReactiveRepository.findItemsBy(), CappedItem.class);
    }
}
