package kz.danke.react.spring.router;

import kz.danke.react.spring.constants.ItemConstants;
import kz.danke.react.spring.handler.ItemCappedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class ItemCappedRouter {

    @Bean
    public RouterFunction<ServerResponse> streamItemCapped(ItemCappedHandler itemCappedHandler) {
        return RouterFunctions
                .route(
                        RequestPredicates
                                .GET(ItemConstants.ITEM_CAPPED_FUNCTION_MAPPING)
                                .and(RequestPredicates.accept(MediaType.APPLICATION_STREAM_JSON)),
                        itemCappedHandler::findAllItemsStream
                );
    }
}
