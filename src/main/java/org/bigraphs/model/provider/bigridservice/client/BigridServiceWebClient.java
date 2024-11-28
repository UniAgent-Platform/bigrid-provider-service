package org.bigraphs.model.provider.bigridservice.client;

import org.bigraphs.model.provider.bigridservice.data.PointData;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * A simple service client to request the endpoints.
 *
 * @author Dominik Grzelak
 */
@Service
public class BigridServiceWebClient {

    // TODO make configurable
    private final WebClient client = WebClient.create("http://localhost:8080");

    public Mono<String> fetchRandomPoints() {
        return client.get()
                .uri("/generate/points/random")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PointData.class)
                .map(x -> x.getPoints().stream().map(y -> String.format("(%s, %s)",y.x,y.y)).collect(Collectors.joining(", ")));
    }

    // TODO add more endpoints
}