package kz.danke.react.spring.router;

import kz.danke.react.spring.handler.SampleHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routeFlux(SampleHandlerFunction sampleHandlerFunction) {
        return RouterFunctions
                .route(
                        RequestPredicates
                                .GET("/functional/flux")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        sampleHandlerFunction::flux
                )
                .andRoute(
                        RequestPredicates
                                .GET("/functional/mono")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        sampleHandlerFunction::mono
                );
    }
}
