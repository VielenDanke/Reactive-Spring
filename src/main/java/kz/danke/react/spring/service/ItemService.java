package kz.danke.react.spring.service;

import kz.danke.react.spring.document.Item;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemService {

    Flux<Item> findAll();

    Mono<Item> findById(String id);

    Mono<Item> save(Item item);

    Mono<Void> deleteById(String id);

    Mono<Item> updateItem(String id, Item item);
}
