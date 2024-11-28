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
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData_GenerateGrid {
    private String content;
    private int rows;
    private int cols;
}