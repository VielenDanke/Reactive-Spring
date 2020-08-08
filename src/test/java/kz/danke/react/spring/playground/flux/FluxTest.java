package kz.danke.react.spring.playground.flux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxTest {

    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring")
//                .concatWith(Flux.error(new RuntimeException("Exception is happened")))
                .concatWith(Flux.just("After error"))
                .log();

        stringFlux
                .subscribe(
                        System.out::println,
                        e -> System.err.println(e.toString()),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void fluxTestElements_WithoutError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring")
                .log();

        StepVerifier
                .create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactor Spring")
                .verifyComplete();
    }

    @Test
    public void fluxTestElements_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .log();

        StepVerifier
                .create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactor Spring")
//                .expectError(RuntimeException.class)
                .expectErrorMessage("Exception")
                .verify();
    }

    @Test
    public void fluxTestElementsCount() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .log();

        StepVerifier
                .create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception")
                .verify();
    }

    @Test
    public void fluxTestElements_WithErrorOnly() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .log();

        StepVerifier
                .create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactor Spring")
                .expectErrorMessage("Exception")
                .verify();
    }
}
