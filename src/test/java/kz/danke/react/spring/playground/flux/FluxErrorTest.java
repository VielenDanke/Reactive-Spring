package kz.danke.react.spring.playground.flux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static kz.danke.react.spring.data.TestData.NAMES;

public class FluxErrorTest {

    @Test
    public void fluxErrorResume() {
        Flux<String> namesFlux = Flux
                .fromIterable(NAMES)
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("After error element"))
                .onErrorResume(e -> {
                    System.out.println(e.toString());
                    return Flux.just("Default");
                });

        StepVerifier
                .create(namesFlux)
                .expectSubscription()
                .expectNext(NAMES.toArray(String[]::new))
                .expectNext("Default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorReturn() {
        Flux<String> namesFlux = Flux
                .fromIterable(NAMES)
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("After error element"))
                .onErrorReturn("Default")
                .log();

        StepVerifier
                .create(namesFlux)
                .expectSubscription()
                .expectNext(NAMES.toArray(String[]::new))
                .expectNext("Default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorMap() {
        Flux<String> namesFlux = Flux
                .fromIterable(NAMES)
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("After error element"))
                .onErrorMap(CustomException::new)
                .log();

        StepVerifier
                .create(namesFlux)
                .expectSubscription()
                .expectNext(NAMES.toArray(String[]::new))
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorMap_WithRetry() {
        Flux<String> namesFlux = Flux
                .fromIterable(NAMES)
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("After error element"))
                .onErrorMap(CustomException::new)
                .retry(2)
                .log();

        StepVerifier
                .create(namesFlux)
                .expectSubscription()
                .expectNext(NAMES.toArray(String[]::new))
                .expectNext(NAMES.toArray(String[]::new))
                .expectNext(NAMES.toArray(String[]::new))
                .expectError(CustomException.class)
                .verify();
    }

    private static class CustomException extends RuntimeException {

        public CustomException(String message) {
            super(message);
        }

        public CustomException(Throwable cause) {
            super(cause);
        }
    }
}
