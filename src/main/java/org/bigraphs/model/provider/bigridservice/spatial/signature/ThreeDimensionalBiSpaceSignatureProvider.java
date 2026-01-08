package org.bigraphs.model.provider.bigridservice.spatial.signature;

import org.bigraphs.framework.core.impl.signature.DynamicSignature;
import org.bigraphs.framework.core.impl.signature.DynamicSignatureBuilder;
import org.bigraphs.model.provider.base.BAbstractSignatureProvider;

import static org.bigraphs.framework.core.factory.BigraphFactory.pureSignatureBuilder;

/**
 * A signature provider for 3D bi-spatial bigraphs with 10-directional routes.
 * Extends the diagonal directional signature with vertical route types.
 * 
 * Directions (10 total):
 * - 4 cardinal directions (horizontal): Left, Right, Forward, Back
 * - 4 diagonal directions (horizontal): ForwardLeft, ForwardRight, BackLeft, BackRight
 * - 2 vertical directions: Up, Down
 * 
 * @author Tianxiong Zhang
 */
public class ThreeDimensionalBiSpaceSignatureProvider extends BAbstractSignatureProvider<DynamicSignature> {

    public static final String LOCALE_TYPE = "Locale";
    
    // Cardinal directions (4) - horizontal plane
    public static final String LEFT_ROUTE_TYPE = "LeftRoute";
    public static final String RIGHT_ROUTE_TYPE = "RightRoute";
    public static final String FORWARD_ROUTE_TYPE = "ForwardRoute";
    public static final String BACK_ROUTE_TYPE = "BackRoute";
    
    // Diagonal directions (4) - horizontal plane
    public static final String FORWARD_LEFT_ROUTE_TYPE = "ForwardLeftRoute";
    public static final String FORWARD_RIGHT_ROUTE_TYPE = "ForwardRightRoute";
    public static final String BACK_LEFT_ROUTE_TYPE = "BackLeftRoute";
    public static final String BACK_RIGHT_ROUTE_TYPE = "BackRightRoute";
    
    // Vertical directions (2)
    public static final String UP_ROUTE_TYPE = "UpRoute";
    public static final String DOWN_ROUTE_TYPE = "DownRoute";
    
    public static final String OCCUPIED_TYPE = "OccupiedBy";

    private static ThreeDimensionalBiSpaceSignatureProvider instance;
    private static final Object lock = new Object();

    private ThreeDimensionalBiSpaceSignatureProvider() {
    }

    public static ThreeDimensionalBiSpaceSignatureProvider getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ThreeDimensionalBiSpaceSignatureProvider();
                }
            }
        }
        return instance;
    }

    @Override
    public DynamicSignature getSignature() {
        DynamicSignatureBuilder defaultBuilder = pureSignatureBuilder();
        defaultBuilder
                .add(LOCALE_TYPE, 1)
                // Horizontal cardinal directions
                .add(LEFT_ROUTE_TYPE, 1)
                .add(RIGHT_ROUTE_TYPE, 1)
                .add(FORWARD_ROUTE_TYPE, 1)
                .add(BACK_ROUTE_TYPE, 1)
                // Horizontal diagonal directions
                .add(FORWARD_LEFT_ROUTE_TYPE, 1)
                .add(FORWARD_RIGHT_ROUTE_TYPE, 1)
                .add(BACK_LEFT_ROUTE_TYPE, 1)
                .add(BACK_RIGHT_ROUTE_TYPE, 1)
                // Vertical directions
                .add(UP_ROUTE_TYPE, 1)
                .add(DOWN_ROUTE_TYPE, 1)
                .add(OCCUPIED_TYPE, 0);
        return defaultBuilder.create();
    }
}

