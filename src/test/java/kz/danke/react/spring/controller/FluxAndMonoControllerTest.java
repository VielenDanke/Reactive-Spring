package kz.danke.react.spring.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class FluxAndMonoControllerTest {

    private WebTestClient webTestClient;

    @Test
    public void flux_FirstApproach() {
        VirtualTimeScheduler.getOrSet();

        Flux<Integer> integerFlux = webTestClient
                .get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier
                .withVirtualTime(() -> integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void flux_SecondApproach() {
        VirtualTimeScheduler.getOrSet();

        webTestClient
                .get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    public void flux_ThirdApproach() {
        VirtualTimeScheduler.getOrSet();

        final List<Integer> expectedList = Arrays.asList(1, 2, 3, 4);

        final int expectedSize = 4;

        EntityExchangeResult<List<Integer>> exchangeResult = webTestClient
                .get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        List<Integer> responseBodyList = exchangeResult.getResponseBody();

        Assert.assertNotNull(responseBodyList);
        Assert.assertEquals(expectedSize, responseBodyList.size());
        Assert.assertEquals(expectedList, responseBodyList);
    }

    @Test
    public void flux_FourthApproach() {
        VirtualTimeScheduler.getOrSet();

        final List<Integer> expectedList = Arrays.asList(1, 2, 3, 4);

        final int expectedSize = 4;

        webTestClient
                .get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(response -> {
                    final List<Integer> responseBodyList = response.getResponseBody();

                    Assert.assertNotNull(responseBodyList);
                    Assert.assertEquals(expectedSize, responseBodyList.size());
                    Assert.assertEquals(expectedList, responseBodyList);
                });
    }

    @Test
    public void fluxStream_FirstApproach() {
        VirtualTimeScheduler.getOrSet();

        Flux<Long> longStreamFlux = webTestClient
                .get()
                .uri("/flux-stream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier
                .withVirtualTime(() -> longStreamFlux)
                .expectSubscription()
                .expectNext(1L, 2L, 3L)
                .thenCancel()
                .verify();
    }

    @Test
    public void monoEndpointTest() {
        final Integer expectedValue = 1;

        webTestClient
                .get()
                .uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> {
                    Assert.assertEquals(expectedValue, response.getResponseBody());
                });
    }

    @Autowired
    public void setWebTestClient(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }
}
