package kz.danke.react.spring.playground.flux;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxBackPressureTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        StepVerifier
                .create(integerFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux
                .subscribe(
                        System.out::println,
                        e -> System.err.println(e.toString()),
                        () -> System.out.println("Done"),
                        subscription -> subscription.request(2)
                );
    }

    @Test
    public void backPressure_Cancel() {
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux
                .subscribe(
                        System.out::println,
                        e -> System.err.println(e.toString()),
                        () -> System.out.println("Done"),
                        Subscription::cancel
                );
    }

    @Test
    public void backPressure_Customized() {
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnNext(Integer value) {
                        request(1);

                        System.out.println("Value received is " + value);

                        if (value == 4) {
                            cancel();
                        }
                    }
                });
    }
}
