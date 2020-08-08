package kz.danke.react.spring.router;

import kz.danke.react.spring.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static kz.danke.react.spring.constants.ItemConstants.ITEM_ROUTER_MAPPING;

@Configuration
public class ItemRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemHandler itemHandler) {
        return RouterFunctions
                .route(
                        RequestPredicates
                                .GET(ITEM_ROUTER_MAPPING)
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        itemHandler::getAllItems
                )
                .andRoute(
                        RequestPredicates
                                .GET(ITEM_ROUTER_MAPPING + "/{id}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        itemHandler::getItemById
                )
                .andRoute(
                        RequestPredicates
                                .POST(ITEM_ROUTER_MAPPING)
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        itemHandler::saveItem
                )
                .andRoute(
                        RequestPredicates
                                .DELETE(ITEM_ROUTER_MAPPING + "/{id}"),
                        itemHandler::deleteItem
                )
                .andRoute(
                        RequestPredicates
                                .PUT(ITEM_ROUTER_MAPPING + "/{id}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        itemHandler::updateItem
                );
    }

    @Bean
    public RouterFunction<ServerResponse> error(ItemHandler itemHandler) {
        return RouterFunctions
                .route(
                        RequestPredicates
                                .GET(ITEM_ROUTER_MAPPING + "/exception/occurred")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        itemHandler::itemsException
                );
    }
}
