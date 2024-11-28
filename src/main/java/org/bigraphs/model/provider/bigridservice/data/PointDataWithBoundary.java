package org.bigraphs.model.provider.bigridservice.data;

import lombok.Getter;
import lombok.Setter;
import org.bigraphs.model.provider.spatial.quadtree.impl.QuadtreeImpl;

/**
 * @author Dominik Grzelak
 */
@Getter
@Setter
public class PointDataWithBoundary {
    private PointData pointData;
    private QuadtreeImpl.Boundary boundary;

    public PointDataWithBoundary() {
        this.pointData = new PointData();
        this.boundary = new QuadtreeImpl.Boundary(0, 0, 1, 1);
    }

    public PointDataWithBoundary(QuadtreeImpl.Boundary boundary, PointData pointData) {
        this.pointData = pointData;
        this.boundary = boundary;
    }
}