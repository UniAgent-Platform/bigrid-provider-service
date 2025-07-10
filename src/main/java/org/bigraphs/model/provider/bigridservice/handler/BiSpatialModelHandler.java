package org.bigraphs.model.provider.bigridservice.handler;

import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.model.provider.base.BLocationModelData;
import org.bigraphs.model.provider.bigridservice.data.request.GridSpecRequest;
import org.bigraphs.model.provider.bigridservice.data.ResponseData_GenerateGrid;
import org.bigraphs.model.provider.bigridservice.data.request.InterpolationRequest;
import org.bigraphs.model.provider.spatial.bigrid.BLocationModelDataFactory;
import org.bigraphs.model.provider.spatial.bigrid.BiGridProvider;
import org.bigraphs.model.provider.spatial.bigrid.LinearInterpolationBuilder;
import org.bigraphs.model.provider.spatial.signature.BiSpaceSignatureProvider;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.bigraphs.framework.core.factory.BigraphFactory.pureBuilder;

/**
 * @author Dominik Grzelak
 */
@Component
public class BiSpatialModelHandler extends ServiceHandlerSupport {

    //------------------------------------------------------------------------------------------------------------------
    // Bi-Spatial Metamodel
    //------------------------------------------------------------------------------------------------------------------
    public Mono<ServerResponse> getOrCreateBigraphMetaModel(ServerRequest request) {
        String format = request.queryParam("format").orElse("xml");

        try {
            // Step 1: Get signature
            DefaultDynamicSignature signature = BiSpaceSignatureProvider.getInstance().getSignature();

            // Step 2: Format and respond
            if ("xml".equalsIgnoreCase(format)) {
                ByteArrayOutputStream xmlStream = new ByteArrayOutputStream();
                BigraphFileModelManagement.Store.exportAsMetaModel(pureBuilder(signature).createBigraph(), xmlStream);
                String xmlOutput = xmlStream.toString();

                return ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_XML)
                        .bodyValue(xmlOutput);
            } else {
                // ToDo
                String jsonLike = "ToDo: metaModel.toString()";
                return ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(jsonLike);
            }
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to create/get bigraph meta-model", e));
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // Bi-Spatial Bigrid
    //------------------------------------------------------------------------------------------------------------------

    public Mono<ServerResponse> createUniformBigrid(ServerRequest request) {
        Mono<GridSpecRequest> gridSpecMono = request.bodyToMono(GridSpecRequest.class);
        String format = request.queryParam("format").orElse("json");
        int rows = parseQueryParamAsInt(request, "rows", 3);
        int cols = parseQueryParamAsInt(request, "cols", 3);

        return gridSpecMono.flatMap(gridSpec -> {
            return getServerResponseMono(format, rows, cols, gridSpec);
        });
    }

    public Mono<ServerResponse> createUniformBigridDefault(ServerRequest request) {
        String format = request.queryParam("format").orElse("json");
        int rows = parseQueryParamAsInt(request, "rows", 3);
        int cols = parseQueryParamAsInt(request, "cols", 3);
        GridSpecRequest gridSpec = new GridSpecRequest(0, 0, 1, 1);
        return getServerResponseMono(format, rows, cols, gridSpec);
    }

    private Mono<ServerResponse> getServerResponseMono(String format, int rows, int cols, GridSpecRequest gridSpec) {
        BLocationModelData bLMD = BLocationModelDataFactory.createGrid(rows, cols,
                gridSpec.x, gridSpec.y, gridSpec.stepSizeX, gridSpec.stepSizeY);

        ResponseData_GenerateGrid response = new ResponseData_GenerateGrid();
        response.setCols(cols);
        response.setRows(rows);

        try {
            if ("xml".equalsIgnoreCase(format)) {
                BiGridProvider provider = new BiGridProvider(bLMD);
                PureBigraph bigrid = provider.getBigraph();
                ByteArrayOutputStream textStream = new ByteArrayOutputStream();
                BigraphFileModelManagement.Store.exportAsInstanceModel(bigrid, textStream);
                response.setContent(textStream.toString());
            } else {
                String json = BLocationModelDataFactory.toJson(bLMD);
                response.setContent(json);
            }
        } catch (Exception e) {
            return Mono.error(new RuntimeException(e));
        }

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(response), ResponseData_GenerateGrid.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Interpolation
    // -----------------------------------------------------------------------------------------------------------------

    public Mono<ServerResponse> createInterpolatedBigraph(ServerRequest request) {
        Mono<InterpolationRequest> inputMono = request.bodyToMono(InterpolationRequest.class);
        String format = request.queryParam("format").orElse("json");

        return inputMono.flatMap(input -> {
            try {
                // Convert DTO to Point2D.Float if needed
                List<Point2D.Float> pointList = input.points.stream()
                        .map(p -> new Point2D.Float(p.x, p.y))
                        .collect(Collectors.toList());

                PureBigraph bigraph = LinearInterpolationBuilder.generate(
                        pointList, input.stepSizeX, input.stepSizeY
                );

                if ("xml".equalsIgnoreCase(format)) {
                    ByteArrayOutputStream textStream = new ByteArrayOutputStream();
                    BigraphFileModelManagement.Store.exportAsInstanceModel(bigraph, textStream);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_XML)
                            .bodyValue(textStream.toString());
                } else {
                    // ToDo
                    String json = "TODO: bigraph.toString();";
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(json);
                }
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Failed to interpolate bigraph", e));
            }
        });
    }
}
