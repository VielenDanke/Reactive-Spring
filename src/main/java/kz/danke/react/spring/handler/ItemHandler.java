package kz.danke.react.spring.handler;

import kz.danke.react.spring.document.Item;
import kz.danke.react.spring.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ItemHandler {

    private final ItemService itemService;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItemById(ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");

        final Mono<Item> byId = itemService.findById(id);

        return byId
                .flatMap(item -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(item))
                ).switchIfEmpty(
                        ServerResponse.notFound().build()
                );
    }

    public Mono<ServerResponse> saveItem(ServerRequest serverRequest) {
        final Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);

        return itemMono.flatMap(item ->
                    ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(itemService.save(item), Item.class)
        );
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");

        Mono<Void> deletedItem = itemService.deleteById(id);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deletedItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");
        final Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);

        Mono<Item> byId = itemService.findById(id);

        return byId
                .flatMap(
                        item -> {
                            Mono<Item> updatedItem = itemMono.flatMap(itemBody -> {
                                item.setPrice(itemBody.getPrice());
                                item.setDescription(itemBody.getDescription());

                                return itemService.save(item);
                            });
                            return ServerResponse
                                    .ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(updatedItem, Item.class);
                        }
                ).switchIfEmpty(
                        ServerResponse.notFound().build()
                );
    }

    public Mono<ServerResponse> itemsException(ServerRequest serverRequest) {
        throw new RuntimeException("Runtime exception occurred");
    }
}
