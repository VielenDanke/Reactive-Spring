package kz.danke.react.spring.playground.mono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class MonoTransformTest {

    @Test
    public void monoTransform() {
        Mono<Integer> integerMono = Mono.just("Andrew")
                .map(String::length)
                .log();

        StepVerifier
                .create(integerMono)
                .expectNext(6)
                .expectNextCount(0)
                .verifyComplete();
    }
}
