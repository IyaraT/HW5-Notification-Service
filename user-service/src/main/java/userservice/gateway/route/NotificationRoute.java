package userservice.gateway.route;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RequestPredicates.path;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;

@Configuration
public class NotificationRoute {

    @Bean
    public RouterFunction<ServerResponse> notificationRoutes() {
        return route("notification-service")
                .route(path("/api/notifications/{*segments}"), http())
                .filter(lb("notification-service"))
                .filter(circuitBreaker(
                        "notificationServiceCB",
                        URI.create("forward:/fallback/notification-service")
                ))
                .build();
    }
}