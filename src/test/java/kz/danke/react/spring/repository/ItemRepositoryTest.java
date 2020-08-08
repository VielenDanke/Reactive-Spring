package kz.danke.react.spring.repository;

import kz.danke.react.spring.document.Item;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static kz.danke.react.spring.data.ItemData.FIFTH_ITEM_ID;
import static kz.danke.react.spring.data.ItemData.ITEM_LIST;

@RunWith(SpringRunner.class)
@DataMongoTest
@ActiveProfiles("test")
public class ItemRepositoryTest {

    private ItemReactiveRepository itemReactiveRepository;

    @Before
    public void setup() {
        itemReactiveRepository.deleteAll()
                .thenMany(
                    Flux.fromIterable(ITEM_LIST)
                )
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is " + item.toString()))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        Flux<Item> allItems = itemReactiveRepository.findAll();

        StepVerifier
                .create(allItems)
                .expectSubscription()
                .expectNextCount(ITEM_LIST.size())
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        Mono<Item> itemById = itemReactiveRepository.findById(FIFTH_ITEM_ID);

        StepVerifier
                .create(itemById)
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Fifth item"))
                .verifyComplete();
    }

    @Test
    public void findAllItemByDescription() {
        final int indexOfItem = 0;

        Item itemToSearch = ITEM_LIST.get(indexOfItem);

        Flux<Item> itemByDescription = itemReactiveRepository.findAllByDescription(itemToSearch.getDescription());

        StepVerifier
                .create(itemByDescription)
                .expectSubscription()
                .expectNext(itemToSearch)
                .verifyComplete();
    }

    @Test
    public void findAllItemByDescriptionContains() {
        final String description = "item";

        Flux<Item> allByDescriptionContains = itemReactiveRepository.findAllByDescriptionContains(description);

        StepVerifier
                .create(allByDescriptionContains)
                .expectNextCount(ITEM_LIST.size())
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        final String newDescription = "New item";

        Item itemToSave = new Item(null, newDescription, 111.1);

        Mono<Item> savedItem = itemReactiveRepository.save(itemToSave);

        StepVerifier
                .create(savedItem)
                .expectNextMatches(item -> item.getId() != null && item.getDescription().equals(newDescription))
                .verifyComplete();
    }

    @Test
    public void updateItem() {
        final double newPrice = 7.99;

        Mono<Item> updatedMono = itemReactiveRepository.findById(FIFTH_ITEM_ID)
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(itemReactiveRepository::save);

        StepVerifier
                .create(updatedMono)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == newPrice)
                .verifyComplete();
    }

    @Test
    public void deleteItem() {
        Mono<Void> deletedItem = itemReactiveRepository.findById(FIFTH_ITEM_ID)
                .flatMap(itemReactiveRepository::delete);

        StepVerifier
                .create(deletedItem)
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void deleteItem_SecondApproach() {
        Flux<Item> allItems = itemReactiveRepository.findAll();

        Mono<Void> deletedItem = itemReactiveRepository.findById(FIFTH_ITEM_ID)
                .map(Item::getId)
                .flatMap(itemReactiveRepository::deleteById);

        StepVerifier
                .create(deletedItem)
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();

        StepVerifier
                .create(allItems)
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Autowired
    public void setItemReactiveRepository(ItemReactiveRepository itemReactiveRepository) {
        this.itemReactiveRepository = itemReactiveRepository;
    }
}
