package org.bigraphs.model.provider.bigridservice.handler;

import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.model.provider.base.BLocationModelData;
import org.bigraphs.model.provider.bigridservice.data.request.ConvexShapeRequest;
import org.bigraphs.model.provider.bigridservice.data.request.GridSpecRequest;
import org.bigraphs.model.provider.bigridservice.data.ResponseData_GenerateGrid;
import org.bigraphs.model.provider.bigridservice.data.request.InterpolationRequest;
import org.bigraphs.model.provider.spatial.bigrid.*;
import org.bigraphs.model.provider.spatial.signature.BiSpaceSignatureProvider;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.swarmwalker.messages.BiGrid;
import reactor.core.publisher.Mono;

import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
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
        String format = request.queryParam("format").orElse("xml");
        int rows = parseQueryParamAsInt(request, "rows", 3);
        int cols = parseQueryParamAsInt(request, "cols", 3);

        return gridSpecMono.flatMap(gridSpec -> {
            return getServerResponseMono(format, rows, cols, gridSpec);
        });
    }

    public Mono<ServerResponse> createUniformBigridDefault(ServerRequest request) {
        String format = request.queryParam("format").orElse("xml");
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
                response.setMimeType(MediaType.APPLICATION_XML.toString());
                BiGridProvider provider = new BiGridProvider(bLMD);
                PureBigraph bigrid = provider.getBigraph();
                ByteArrayOutputStream textStream = new ByteArrayOutputStream();
                BigraphFileModelManagement.Store.exportAsInstanceModel(bigrid, textStream);
                response.setContent(textStream.toString());
            } else if ("json".equalsIgnoreCase(format)) {
                response.setMimeType(MediaType.APPLICATION_JSON.toString());
                String json = BLocationModelDataFactory.toJson(bLMD);
                response.setContent(json);
            } else if ("protobuf".equalsIgnoreCase(format)) {
                response.setMimeType(MediaType.parseMediaType("application/x-protobuf").toString());
                BiGrid gridMessage = BLocationToBiGridConverter.convert(bLMD);
                // (!) Important: Encoding Protobuf payload to Base64
                // Because Protobuf is binary and putting it directly in a JSON string field will corrupt the data
                byte[] protoBytes = gridMessage.toByteArray();
                String base64 = Base64.getEncoder().encodeToString(protoBytes);
                response.setContent(base64);
            }
        } catch (Exception e) {
            return Mono.error(new RuntimeException(e));
        }

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(response), ResponseData_GenerateGrid.class);
    }

    //------------------------------------------------------------------------------------------------------------------
    // Bi-Spatial Bigraph with Convex Shape
    //------------------------------------------------------------------------------------------------------------------

    public Mono<ServerResponse> createConvexShape(ServerRequest request) {
        String format = request.queryParam("format").orElse("xml");

        Mono<ConvexShapeRequest> bodyMono = request.bodyToMono(ConvexShapeRequest.class);

        return bodyMono.flatMap(convexRequest -> {
            ResponseData_GenerateGrid response = new ResponseData_GenerateGrid();
            try {
                List<Point2D.Float> pointList = convexRequest.getPoints();
                float stepSize = convexRequest.getStepSize();
                PureBigraph bigraph = ConvexShapeBuilder.generateAsSingle(
                        pointList, stepSize, BiGridElementFactory.create()
                );

                if ("xml".equalsIgnoreCase(format)) {
                    response.setMimeType(MediaType.APPLICATION_XML.toString());

                    ByteArrayOutputStream textStream = new ByteArrayOutputStream();
                    BigraphFileModelManagement.Store.exportAsInstanceModel(bigraph, textStream);
                    response.setContent(textStream.toString());
                } else if ("json".equalsIgnoreCase(format)) {
                    response.setMimeType(MediaType.APPLICATION_JSON.toString());

                    // ToDo
                    String json = "ToDo: BLocationModelDataFactory.toJson(bigraph);";
                    response.setContent(json);
                } else if ("protobuf".equalsIgnoreCase(format)) {
                    response.setMimeType(MediaType.parseMediaType("application/x-protobuf").toString());

                    BiGrid gridMessage = BLocationToBiGridConverter.convertPureSpatialBigraph(bigraph, stepSize, stepSize);
                    byte[] protoBytes = gridMessage.toByteArray();
                    String base64 = Base64.getEncoder().encodeToString(protoBytes);
                    response.setContent(base64);
                }
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Failed to generate convex bigraph", e));
            }
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(response), ResponseData_GenerateGrid.class);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Interpolation
    // -----------------------------------------------------------------------------------------------------------------

    public Mono<ServerResponse> createInterpolatedBigraph(ServerRequest request) {
        Mono<InterpolationRequest> inputMono = request.bodyToMono(InterpolationRequest.class);
        String format = request.queryParam("format").orElse("xml");

        return inputMono.flatMap(input -> {
            ResponseData_GenerateGrid response = new ResponseData_GenerateGrid();
            try {
                List<Point2D.Float> pointList = input.points.stream()
                        .map(p -> new Point2D.Float(p.x, p.y))
                        .collect(Collectors.toList());

                PureBigraph bigraph = LinearInterpolationBuilder.generate(
                        pointList, input.stepSizeX, input.stepSizeY
                );

                if ("xml".equalsIgnoreCase(format)) {
                    response.setMimeType(MediaType.APPLICATION_XML.toString());

                    ByteArrayOutputStream textStream = new ByteArrayOutputStream();
                    BigraphFileModelManagement.Store.exportAsInstanceModel(bigraph, textStream);
                    response.setContent(textStream.toString());
                } else if ("json".equalsIgnoreCase(format)) {
                    response.setMimeType(MediaType.APPLICATION_JSON.toString());

                    // ToDo
                    String json = "ToDo: BLocationModelDataFactory.toJson(bigraph);";
                    response.setContent(json);
                } else if ("protobuf".equalsIgnoreCase(format)) {
                    response.setMimeType(MediaType.parseMediaType("application/x-protobuf").toString());

                    BiGrid gridMessage = BLocationToBiGridConverter.convertPureSpatialBigraph(bigraph, input.stepSizeX, input.stepSizeY);
                    byte[] protoBytes = gridMessage.toByteArray();
                    String base64 = Base64.getEncoder().encodeToString(protoBytes);
                    response.setContent(base64);
                }
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Failed to interpolate bigraph", e));
            }
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(response), ResponseData_GenerateGrid.class);
        });
    }
}
