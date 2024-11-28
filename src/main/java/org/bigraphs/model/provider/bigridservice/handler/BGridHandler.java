package org.bigraphs.model.provider.bigridservice.handler;

import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.model.provider.base.BLocationModelData;
import org.bigraphs.model.provider.bigridservice.data.GridSpec;
import org.bigraphs.model.provider.bigridservice.data.ResponseData_GenerateGrid;
import org.bigraphs.model.provider.spatial.bigrid.BLocationModelDataFactory;
import org.bigraphs.model.provider.spatial.bigrid.BiGridProvider;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;

/**
 * @author Dominik Grzelak
 */
@Component
public class BGridHandler extends ServiceHandlerSupport {

    public Mono<ServerResponse> createUniformBigrid(ServerRequest request) {
        Mono<GridSpec> gridSpecMono = request.bodyToMono(GridSpec.class);
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
        GridSpec gridSpec = new GridSpec(0, 0, 1, 1);
        return getServerResponseMono(format, rows, cols, gridSpec);
    }

    private Mono<ServerResponse> getServerResponseMono(String format, int rows, int cols, GridSpec gridSpec) {
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
}
