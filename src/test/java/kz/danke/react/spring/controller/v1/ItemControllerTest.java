package kz.danke.react.spring.controller.v1;

import kz.danke.react.spring.constants.ItemConstants;
import kz.danke.react.spring.document.Item;
import kz.danke.react.spring.repository.ItemReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static kz.danke.react.spring.data.ItemData.FIFTH_ITEM_ID;
import static kz.danke.react.spring.data.ItemData.ITEM_LIST;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
public class ItemControllerTest {

    private ItemReactiveRepository itemReactiveRepository;
    private WebTestClient webTestClient;

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
    public void getAllItems_FirstApproach() {
        Flux<Item> itemFlux = webTestClient
                .get()
                .uri(ItemConstants.ITEM_REST_MAPPING)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier
                .create(itemFlux)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getAllItems_SecondApproach() {
        webTestClient
                .get()
                .uri(ItemConstants.ITEM_REST_MAPPING)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getAllItems_ThirdApproach() {
        webTestClient
                .get()
                .uri(ItemConstants.ITEM_REST_MAPPING)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(result -> {
                    List<Item> itemList = result.getResponseBody();

                    Assert.assertNotNull(itemList);

                    itemList.forEach(
                            item -> Assert.assertNotNull(item.getId())
                    );
                });
    }

    @Test
    public void getItemById() {
        webTestClient
                .get()
                .uri(String.format(ItemConstants.ITEM_REST_MAPPING + "/%s", FIFTH_ITEM_ID))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id", FIFTH_ITEM_ID);
    }

    @Test
    public void getItemById_NotFound() {
        final String notValidId = "CDA";

        webTestClient
                .get()
                .uri(String.format(ItemConstants.ITEM_REST_MAPPING + "/%s", notValidId))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void saveNewItem() {
        final String description = "Sixth item";

        final double price = 99.9;

        Item item = new Item(null, description, price);

        webTestClient
                .post()
                .uri(ItemConstants.ITEM_REST_MAPPING)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Item.class)
                .consumeWith(result -> {
                    Item savedItem = result.getResponseBody();

                    Assert.assertNotNull(savedItem);
                    Assert.assertNotNull(savedItem.getId());
                    Assert.assertEquals(description, savedItem.getDescription());
                    Assert.assertEquals(price, savedItem.getPrice(), 0.01);
                });
    }

    @Test
    public void deleteItemById() {
        webTestClient
                .delete()
                .uri(String.format(ItemConstants.ITEM_REST_MAPPING + "/%s", FIFTH_ITEM_ID))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem() {
        final String newDescription = "New description";

        final double newPrice = 1.1;

        final Item itemToUpdate = new Item(null, newDescription, newPrice);

        webTestClient
                .put()
                .uri(String.format(ItemConstants.ITEM_REST_MAPPING + "/%s", FIFTH_ITEM_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemToUpdate), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.description", newDescription)
                .hasJsonPath()
                .jsonPath("$.price", newPrice);
    }

    @Test
    public void updateItem_Failed() {
        final Item itemToUpdate = new Item();
        final String incorrectId = "CDA";

        webTestClient
                .put()
                .uri(String.format(ItemConstants.ITEM_REST_MAPPING + "/%s", incorrectId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemToUpdate), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void runtimeException() {
        webTestClient
                .get()
                .uri(ItemConstants.ITEM_REST_MAPPING + "/exception")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Runtime exception occurred");
    }

    @Autowired
    public void setItemReactiveRepository(ItemReactiveRepository itemReactiveRepository) {
        this.itemReactiveRepository = itemReactiveRepository;
    }

    @Autowired
    public void setWebTestClient(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }
}
