package kz.danke.react.spring.handler;

import kz.danke.react.spring.document.ItemCapped;
import kz.danke.react.spring.repository.ItemReactiveCappedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ItemCappedHandler {

    private final ItemReactiveCappedRepository itemReactiveCappedRepository;

    public Mono<ServerResponse> findAllItemsStream(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(itemReactiveCappedRepository.findItemsBy(), ItemCapped.class);
    }
}
