package kz.danke.react.spring.playground.flux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.*;

import static kz.danke.react.spring.data.TestData.NAMES;

public class FluxTransformTest {

    @Test
    public void transformUsingMap() {
        Flux<User> userFlux = Flux.fromIterable(NAMES)
                .map(User::new)
                .log();

        StepVerifier
                .create(userFlux)
                .expectNext(new User("Adam"))
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length() {
        Flux<Integer> integerFlux = Flux.fromIterable(NAMES)
                .map(String::length)
                .log();

        StepVerifier
                .create(integerFlux)
                .expectNext(4)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length_Repeat() {
        Flux<Integer> integerFlux = Flux.fromIterable(NAMES)
                .map(String::length)
                .repeat(1)
                .log();

        StepVerifier
                .create(integerFlux)
                .expectNext(4)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    public void transformUsingMapAndFilter_Length_Repeat() {
        Flux<Integer> integerFlux = Flux.fromIterable(NAMES)
                .map(String::length)
                .repeat(1)
                .filter(i -> i == 5)
                .log();

        StepVerifier
                .create(integerFlux)
                .expectNext(5)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> namesFlux = Flux.fromIterable(NAMES)
                .flatMap(str -> Flux.fromIterable(convertToList(str))) // db call or external service call returns flux
                .log();

        StepVerifier
                .create(namesFlux)
                .expectNextCount(NAMES.size() * 2)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_Parallel() {
        Flux<String> namesFlux = Flux.fromIterable(NAMES)
                .window(2) // Flux<Flux<String>>
                .flatMap(str -> str
                        .map(this::convertToList)
                        .subscribeOn(Schedulers.parallel())
                        .flatMap(Flux::fromIterable)
                ) // db call or external service call returns flux
                .log();

        StepVerifier
                .create(namesFlux)
                .expectNextCount(NAMES.size() * 2)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_Parallel_MaintainOrder() {
        Flux<String> namesFlux = Flux.fromIterable(NAMES)
                .window(2) // Flux<Flux<String>>
//                .concatMap(str -> str // concatMap support order
//                        .map(this::convertToList)
//                        .subscribeOn(Schedulers.parallel())
//                        .flatMap(Flux::fromIterable)
                 // db call or external service call returns flux
                .flatMapSequential(
                        str -> str // concatMap support order
                            .map(this::convertToList)
                            .subscribeOn(Schedulers.parallel())
                            .flatMap(Flux::fromIterable)
                )
                .log();

        StepVerifier
                .create(namesFlux)
                .expectNextCount(NAMES.size() * 2)
                .verifyComplete();
    }

    private List<String> convertToList(String str) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return Arrays.asList(str, "newValue");
    }

    private static class User {
        private final String username;

        public User(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return "User{" +
                    "username='" + username + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return Objects.equals(username, user.username);
        }

        @Override
        public int hashCode() {
            return Objects.hash(username);
        }
    }
}
