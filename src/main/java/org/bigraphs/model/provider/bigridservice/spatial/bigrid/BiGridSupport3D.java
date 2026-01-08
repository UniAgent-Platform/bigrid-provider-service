package org.bigraphs.model.provider.bigridservice.spatial.bigrid;

import java.awt.geom.Point2D;

/**
 * Support class for 3D bigrid operations.
 * Extends BiGridSupport with 3D coordinate formatting capabilities.
 * 
 * @author Tianxiong Zhang
 */
public abstract class BiGridSupport3D {

    /**
     * Formats a 3D coordinate (x, y, z) into a parameter control string.
     * Format: C_{x}_{y}_{z}
     * Example: (0.0, 0.0, 0.0) -> "C_0_00__0_00__0_00"
     * 
     * @param x the x coordinate (Forward/Back direction)
     * @param y the y coordinate (Left/Right direction)
     * @param z the z coordinate (Up/Down direction, height/layer)
     * @return formatted coordinate string
     */
    public static String formatParamControl3D(float x, float y, float z) {
        String formattedX = formatCoordinate(x);
        String formattedY = formatCoordinate(y);
        String formattedZ = formatCoordinate(z);
        return String.format("C_%s__%s__%s", formattedX, formattedY, formattedZ);
    }

    /**
     * Formats a 3D coordinate using Point2D for x,y and separate z value.
     * 
     * @param point2D the x,y coordinates
     * @param z the z coordinate (height/layer)
     * @return formatted coordinate string
     */
    public static String formatParamControl3D(Point2D.Float point2D, float z) {
        return formatParamControl3D(point2D.x, point2D.y, z);
    }

    /**
     * Formats a single coordinate value (locale-independent).
     * Handles negative values with 'N' prefix.
     * Format: [N]{integer}_{fractional}
     * Example: 1.5 -> "1_50", -2.25 -> "N2_25"
     * 
     * @param value the coordinate value
     * @return formatted coordinate string
     */
    private static String formatCoordinate(float value) {
        boolean isNegative = value < 0;
        value = Math.abs(value);

        int integerPart = (int) value;
        int fractionalPart = Math.round((value - integerPart) * 100); // 2 decimal places

        String prefix = isNegative ? "N" : "";

        return String.format("%s%d_%02d", prefix, integerPart, fractionalPart);
    }

    /**
     * Parses a 3D parameter control string back to coordinates.
     * Opposite of formatParamControl3D().
     * 
     * @param formattedString output of formatParamControl3D()
     * @return float array [x, y, z]
     * @throws IllegalArgumentException if format is invalid
     */
    public static float[] parseParamControl3D(String formattedString) throws IllegalArgumentException {
        if (formattedString == null || !formattedString.startsWith("C_") || formattedString.split("__").length != 3) {
            throw new IllegalArgumentException("Invalid 3D format");
        }

        try {
            String[] parts = formattedString.substring(2).split("__");
            float x = parseCoordinate(parts[0]);
            float y = parseCoordinate(parts[1]);
            float z = parseCoordinate(parts[2]);
            return new float[]{x, y, z};
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid 3D format", e);
        }
    }

    /**
     * Parses a single coordinate string back to float value.
     * 
     * @param part the coordinate string (e.g., "1_50" or "N2_25")
     * @return the float value
     */
    private static float parseCoordinate(String part) {
        part = part.replace("N", "-").replace("_", ".");
        return Float.parseFloat(part);
    }
}

