package kz.danke.react.spring.playground.flux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static kz.danke.react.spring.data.TestData.NAMES;

public class FluxFactoryTest {

    @Test
    public void fluxUsingIterable() {
        Flux<String> namesFlux = Flux
                .fromIterable(NAMES)
                .log();

        StepVerifier
                .create(namesFlux)
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        String[] namesArray = NAMES.toArray(String[]::new);

        Flux<String> namesFlux = Flux
                .fromArray(namesArray)
                .log();

        StepVerifier
                .create(namesFlux)
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        Stream<String> streamNames = NAMES.stream();

        Flux<String> namesFlux = Flux
                .fromStream(streamNames)
                .log();

        StepVerifier
                .create(namesFlux)
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> rangeFlux = Flux.range(1, 5)
                .log();

        StepVerifier
                .create(rangeFlux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }
}
