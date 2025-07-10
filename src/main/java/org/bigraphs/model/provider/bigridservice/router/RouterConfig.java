package org.bigraphs.model.provider.bigridservice.router;

import org.bigraphs.model.provider.bigridservice.handler.BiSpatialModelHandler;
import org.bigraphs.model.provider.bigridservice.handler.BQuadtreeHandler;
import org.bigraphs.model.provider.bigridservice.handler.PointHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * @author Dominik Grzelak
 */
@Configuration
public class RouterConfig {

    // The Bigraph MetaModel ("Bi-spatial MetaModel")

    @Bean
    public RouterFunction<ServerResponse> get_bigraph_metamodel(BiSpatialModelHandler handler) {
        return RouterFunctions.route()
                .GET("/generate/metamodel", handler::getOrCreateBigraphMetaModel)
                .build();
    }

    // Points

    @Bean
    public RouterFunction<ServerResponse> gen_points_default(PointHandler handler) {
        return RouterFunctions.route()
                .GET("/generate/points/random", handler::generateRandomPointsDefault)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> gen_points_boundary(PointHandler handler) {
        return RouterFunctions.route()
                .POST("/generate/points/random", handler::generateRandomPointsWithinBoundary)
                .build();
    }

    // Convex Shapes

    @Bean
    public RouterFunction<ServerResponse> gen_convex_shape(BiSpatialModelHandler handler) {
        return RouterFunctions.route()
                .POST("/generate/convex", handler::createConvexShape)
                .build();
    }

    // Bigrid

    @Bean
    public RouterFunction<ServerResponse> gen_bigrid_default(BiSpatialModelHandler handler) {
        return RouterFunctions.route()
                .GET("/generate/bigrid", handler::createUniformBigridDefault)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> gen_bigrid(BiSpatialModelHandler handler) {
        return RouterFunctions.route()
                .POST("/generate/bigrid", handler::createUniformBigrid)
                .build();
    }

    // Interpolation

    @Bean
    public RouterFunction<ServerResponse> gen_interpolated_bigraph(BiSpatialModelHandler handler) {
        return RouterFunctions.route()
                .POST("/generate/interpolated", handler::createInterpolatedBigraph)
                .build();
    }

    // Quadtree

    @Bean
    public RouterFunction<ServerResponse> gen_quadtree(BQuadtreeHandler handler) {
        return RouterFunctions.route()
                .POST("/generate/quadtree", handler::createBQuadtree)
                .build();
    }
}
