package kz.danke.react.spring.playground.flux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kz.danke.react.spring.data.TestData.NAMES;
import static kz.danke.react.spring.data.TestData.NAMES_SECOND;

public class ColdAndHotPublisherTest {

    private static final List<String> STRING_LIST;

    static {
        STRING_LIST = new ArrayList<>();

        STRING_LIST.addAll(NAMES);
        STRING_LIST.addAll(NAMES_SECOND);
    }

    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> namesFlux = Flux.fromIterable(STRING_LIST)
                .delayElements(Duration.ofSeconds(1))
                .log();

        namesFlux
                .subscribe(str -> System.out.println("Subscriber 1 " + str)); // When subscribe emits the value from beginning

        Thread.sleep(2000);

        namesFlux
                .subscribe(str -> System.out.println("Subscriber 2 " + str));

        Thread.sleep(4000);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {
        Flux<String> namesFlux = Flux.fromIterable(STRING_LIST)
                .delayElements(Duration.ofSeconds(1))
                .log();

        ConnectableFlux<String> connectableFlux = namesFlux
                .publish();

        connectableFlux.connect();

        connectableFlux
                .subscribe(
                        str -> System.out.println("Subscriber 1 " + str)
                );
        Thread.sleep(3000);


        connectableFlux
                .subscribe(
                        str -> System.out.println("Subscriber 2 " + str)
                );
        Thread.sleep(4000);
    }
}
