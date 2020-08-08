package kz.danke.react.spring.playground.flux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> longFlux = Flux.interval(Duration.ofMillis(200))
                .log(); // start from 0

        longFlux
                .subscribe(System.out::println);

        Thread.sleep(3000);
    }

    @Test
    public void infiniteSequenceTest() throws InterruptedException {
        Flux<Long> longFlux = Flux.interval(Duration.ofMillis(200))
                .take(3)
                .log(); // start from 0

        StepVerifier
                .create(longFlux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap() throws InterruptedException {
        Flux<Integer> longFlux = Flux.interval(Duration.ofMillis(200))
                .map(Long::intValue)
                .take(3)
                .log(); // start from 0

        StepVerifier
                .create(longFlux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap_WithDelay() throws InterruptedException {
        Flux<Integer> longFlux = Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map(Long::intValue)
                .take(3)
                .log(); // start from 0

        StepVerifier
                .create(longFlux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }
}
