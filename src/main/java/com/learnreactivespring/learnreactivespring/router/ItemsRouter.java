package com.learnreactivespring.learnreactivespring.router;

import com.learnreactivespring.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.learnreactivespring.handler.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                        .and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getAllItems)
                .andRoute(GET(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}")
                        .and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getOneItem)
                .andRoute(POST(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                        .and(accept(MediaType.APPLICATION_JSON)), itemsHandler::saveItem)
                .andRoute(DELETE(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}")
                        .and(accept(MediaType.APPLICATION_JSON)), itemsHandler::delete)
                .andRoute(PUT(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}")
                        .and(accept(MediaType.APPLICATION_JSON)), itemsHandler::updateItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/runtimeException"))
                        .and(accept(MediaType.APPLICATION_JSON)), itemsHandler::itemsException);
    }

    @Bean
    public RouterFunction<ServerResponse> itemStremR(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET(ItemConstants.ITEM_STREAM_FUNCTIONAL_END_POINT_V1)
                        .and(accept(MediaType.APPLICATION_JSON)), itemsHandler::itemsStream);
    }
}
