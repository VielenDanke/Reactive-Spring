package kz.danke.react.spring.playground.flux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static kz.danke.react.spring.data.TestData.NAMES;

public class FluxFilterTest {

    @Test
    public void fluxFilterTest() {
        Flux<String> namesFlux = Flux.fromIterable(NAMES)
                .filter(str -> str.startsWith("A"))
                .log();

        StepVerifier
                .create(namesFlux)
                .expectNext("Adam")
                .expectNextCount(1)
                .verifyComplete();
    }
}
