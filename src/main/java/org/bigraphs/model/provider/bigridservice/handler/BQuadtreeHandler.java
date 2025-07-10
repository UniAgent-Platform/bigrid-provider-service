package org.bigraphs.model.provider.bigridservice.handler;

import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.model.provider.base.BLocationModelData;
import org.bigraphs.model.provider.bigridservice.data.request.PointDataWithBoundaryRequest;
import org.bigraphs.model.provider.bigridservice.data.ResponseData_GenerateQuadtree;
import org.bigraphs.model.provider.spatial.bigrid.BLocationModelDataFactory;
import org.bigraphs.model.provider.spatial.bigrid.BiGridProvider;
import org.bigraphs.model.provider.spatial.quadtree.impl.QuadtreeConvert;
import org.bigraphs.model.provider.spatial.quadtree.impl.QuadtreeImpl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominik Grzelak
 */
@Component
public class BQuadtreeHandler extends BiSpatialModelHandler {

    public Mono<ServerResponse> createBQuadtree(ServerRequest request) {
        Mono<PointDataWithBoundaryRequest> pointDataMono = request.bodyToMono(PointDataWithBoundaryRequest.class);
        String format = request.queryParam("format").orElse("json");
        int maxTreeDepth = parseQueryParamAsInt(request, "maxTreeDepth", 4);
        int maxPointsPerLeaf = parseQueryParamAsInt(request, "maxPointsPerLeaf", 1);

        return pointDataMono.flatMap(pointBoundaryProduct -> {
            List<Point2D.Double> pointData = pointBoundaryProduct.getPointData().getPoints();
            QuadtreeImpl.Boundary boundary = pointBoundaryProduct.getBoundary();
            QuadtreeImpl quadtree = new QuadtreeImpl(boundary, maxPointsPerLeaf, maxTreeDepth);
            List<Point2D.Double> pointsOmitted = new ArrayList<>();
            List<Point2D.Double> pointsAdded = new ArrayList<>();
            if (pointData == null) {
//                System.out.println("PointsData is null!");
                return Mono.error(new RuntimeException("PointsData is null!"));
            }
            pointData.forEach(pt -> {
                try {
                    if (!quadtree.insert(pt)) {
                        pointsOmitted.add(pt);
                    } else {
                        pointsAdded.add(pt);
                    }
                } catch (Exception e) {
                    pointsOmitted.add(pt);
                }
            });

            int pointCount = pointData.size();
            int pointCountOmitted = pointsOmitted.size();
            QuadtreeConvert converter = new QuadtreeConvert();
            BLocationModelData bLMD = converter.createBLocationModelDataFromQuadtree(quadtree);

            ResponseData_GenerateQuadtree responseData = new ResponseData_GenerateQuadtree();
            responseData.setPointsAdded(pointsAdded);
            responseData.setPointsOmitted(pointsOmitted);
            try {
                if ("xml".equalsIgnoreCase(format)) {
                    BiGridProvider provider = new BiGridProvider(bLMD);
                    PureBigraph bigrid = provider.getBigraph();
                    ByteArrayOutputStream textStream = new ByteArrayOutputStream();
                    BigraphFileModelManagement.Store.exportAsInstanceModel(bigrid, textStream);
                    responseData.setContent(textStream.toString());
                } else {
                    String json = BLocationModelDataFactory.toJson(bLMD);
                    responseData.setContent(json);
                }
            } catch (Exception e) {
                return Mono.error(new RuntimeException(e));
            }

            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(responseData), ResponseData_GenerateQuadtree.class);
        });
    }
}
