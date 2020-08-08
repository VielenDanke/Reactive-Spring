package kz.danke.react.spring.repository;

import kz.danke.react.spring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {

    Flux<Item> findAllByDescription(String description);

    Flux<Item> findAllByDescriptionContains(String description);
}
