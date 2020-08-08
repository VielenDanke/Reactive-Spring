package kz.danke.react.spring.playground.flux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

import static kz.danke.react.spring.data.TestData.NAMES;
import static kz.danke.react.spring.data.TestData.NAMES_SECOND;

public class FluxCombineTest {

    @Test
    public void combineUsingMerge() {
        Flux<String> fluxNames = Flux.fromIterable(NAMES_SECOND);
        Flux<String> namesOld = Flux.fromIterable(NAMES);

        Flux<String> mergeFlux = Flux
                .merge(fluxNames, namesOld)
                .log();

        StepVerifier
                .create(mergeFlux)
                .expectSubscription()
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    public void combineUsingMerge_WithDelay() {
        Flux<String> fluxNames = Flux
                .fromIterable(NAMES_SECOND)
                .delayElements(Duration.ofSeconds(1));

        Flux<String> namesOld = Flux
                .fromIterable(NAMES)
                .delayElements(Duration.ofSeconds(1));

        Flux<String> mergeFlux = Flux
                .merge(fluxNames, namesOld)
                .log();

        StepVerifier
                .create(mergeFlux)
                .expectSubscription()
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat() {
        Flux<String> fluxNames = Flux
                .fromIterable(NAMES_SECOND);

        Flux<String> namesOld = Flux
                .fromIterable(NAMES);

        Flux<String> mergeFlux = Flux
                .concat(fluxNames, namesOld)
                .log();

        StepVerifier
                .create(mergeFlux)
                .expectSubscription()
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat_WithDelay() {
        VirtualTimeScheduler.getOrSet();

        Flux<String> fluxNames = Flux
                .fromIterable(NAMES_SECOND)
                .delayElements(Duration.ofSeconds(1));

        Flux<String> namesOld = Flux
                .fromIterable(NAMES)
                .delayElements(Duration.ofSeconds(1));

        Flux<String> mergeFlux = Flux
                .concat(fluxNames, namesOld)
                .log();

        StepVerifier
                .withVirtualTime(() -> mergeFlux)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(7))
                .expectNextCount(7)
                .verifyComplete();

//        StepVerifier
//                .create(mergeFlux)
//                .expectSubscription()
//                .expectNextCount(7)
//                .verifyComplete();
    }

    @Test
    public void combineUsingZip() {
        Flux<String> fluxNames = Flux
                .fromIterable(NAMES_SECOND)
                .delayElements(Duration.ofSeconds(1));

        Flux<String> namesOld = Flux
                .fromIterable(NAMES)
                .delayElements(Duration.ofSeconds(1));

        // Order of each flux (one from fluxNames, one from namesOld)
        // if any doesn't have a pair, it won't be count
        Flux<String> mergeFlux = Flux
                .zip(fluxNames, namesOld, String::concat)
                .log();

        StepVerifier
                .create(mergeFlux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }
}
