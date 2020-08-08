package kz.danke.react.spring.handler;

import kz.danke.react.spring.constants.ItemConstants;
import kz.danke.react.spring.document.ItemCapped;
import kz.danke.react.spring.repository.ItemReactiveCappedRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
public class ItemCappedHandlerTest {

    @Autowired
    private ReactiveMongoOperations mongoOperations;
    @Autowired
    private ItemReactiveCappedRepository itemReactiveCappedRepository;
    @Autowired
    private WebTestClient webTestClient;

    @Before
    public void setup() {
        mongoOperations.dropCollection(ItemCapped.class)
                .block();
        mongoOperations.createCollection(
                ItemCapped.class,
                CollectionOptions.empty().size(50000).maxDocuments(20).capped()
        ).block();

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(l -> new ItemCapped(null, "Random item " + l, (100.00 + l)))
                .take(5);

        itemReactiveCappedRepository
                .insert(itemCappedFlux)
                .doOnNext(item -> System.out.println("Inserted Item Capped is: " + item))
                .blockLast();
    }

    @Test
    public void streamEndpointTest_AllItemsCapped() {
        Flux<ItemCapped> itemCappedFlux = webTestClient
                .get()
                .uri(ItemConstants.ITEM_CAPPED_FUNCTION_MAPPING)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody();

        StepVerifier
                .withVirtualTime(() -> itemCappedFlux)
                .expectSubscription()
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }
}
