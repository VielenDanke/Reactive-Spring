package kz.danke.react.spring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@RestController
public class FluxAndMonoController {

    @GetMapping(
            value = "/flux"
    )
    public Flux<Integer> getIntegerFlux() {
        return Flux.just(1, 2, 3, 4)
                .log();
    }

    @GetMapping(
            value = "/flux-stream",
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE
    )
    public Flux<Long> getIntegerFluxStream() {
        return Flux.fromIterable(Arrays.asList(1L, 2L, 3L))
                .log();
    }

    @GetMapping("/mono")
    public Mono<Integer> getMonoInteger() {
        return Mono.just(1)
                .log();
    }
}
