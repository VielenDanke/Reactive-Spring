package kz.danke.react.spring.initializer;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.internal.operation.CreateCollectionOperation;
import kz.danke.react.spring.document.Item;
import kz.danke.react.spring.document.ItemCapped;
import kz.danke.react.spring.repository.ItemReactiveCappedRepository;
import kz.danke.react.spring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;

@Component
@Profile("dev")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    private final ItemReactiveRepository itemReactiveRepository;
    private final ReactiveMongoOperations mongoOperations;
    private final ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    public ItemDataInitializer(ItemReactiveRepository itemReactiveRepository,
                               ReactiveMongoOperations mongoOperations,
                               ItemReactiveCappedRepository itemReactiveCappedRepository) {
        this.itemReactiveRepository = itemReactiveRepository;
        this.mongoOperations = mongoOperations;
        this.itemReactiveCappedRepository = itemReactiveCappedRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
        createCappedCollection();
        dataSetupCappedCollection();
    }

    public void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class)
                .block();
        mongoOperations.createCollection(
                ItemCapped.class,
                CollectionOptions.empty().size(20).maxDocuments(50000).capped()
        ).block();
    }

    public void dataSetupCappedCollection() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(l -> new ItemCapped(null, "Random item " + l, (100.00 + l)));

        itemReactiveCappedRepository
                .insert(itemCappedFlux)
                .subscribe(item -> log.info("Inserted Item Capped is: " + item));
    }

    public void initialDataSetup() {
        Item first = new Item(null, "First item", 127.1);
        Item second = new Item(null, "Second item", 68.4);
        Item third = new Item(null, "Third item", 91.1);
        Item fourth = new Item(null, "Fourth item", 34.7);
        Item fifth = new Item("ABC", "Fifth item", 56.5);

        itemReactiveRepository.deleteAll()
                .thenMany(
                        Flux.fromIterable(
                                Arrays.asList(
                                    first, second, third, fourth, fifth
                                )
                        )
                )
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(
                        item -> System.out.println("Item inserted from command line runner: " + item.toString())
                );
    }
}
