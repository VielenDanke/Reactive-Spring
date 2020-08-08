package kz.danke.react.spring.playground.mono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class MonoFactoryTest {

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<String> mono = Mono.justOrEmpty(null);

        StepVerifier
                .create(mono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        Mono<String> stringMono = Mono.fromSupplier(() -> "String")
                .log();

        StepVerifier
                .create(stringMono)
                .expectNext("String")
                .verifyComplete();
    }
}
