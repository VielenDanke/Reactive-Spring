package kz.danke.react.spring.playground.mono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class MonoFilterTest {

    @Test
    public void monoFilterTest() {
        Mono<String> nameAdam = Mono.just("Adam")
                .filter(str -> str.startsWith("P"))
                .log();

        StepVerifier
                .create(nameAdam)
                .expectNextCount(0)
                .verifyComplete();
    }
}
