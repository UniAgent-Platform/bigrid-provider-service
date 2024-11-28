package org.bigraphs.model.provider.bigridservice.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * @author Dominik Grzelak
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GridSpec {
    public float x;
    public float y;
    public float stepSizeX;
    public float stepSizeY;

}
