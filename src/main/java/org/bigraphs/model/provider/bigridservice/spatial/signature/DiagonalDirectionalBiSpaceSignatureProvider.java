package org.bigraphs.model.provider.bigridservice.spatial.signature;

import org.bigraphs.framework.core.impl.signature.DynamicSignature;
import org.bigraphs.framework.core.impl.signature.DynamicSignatureBuilder;
import org.bigraphs.model.provider.base.BAbstractSignatureProvider;

import static org.bigraphs.framework.core.factory.BigraphFactory.pureSignatureBuilder;

/**
 * A signature provider for diagonal directional bi-spatial bigraphs.
 * Extends the directional signature with diagonal route types (8 directions total).
 * 
 * Directions:
 * - 4 cardinal directions: Left, Right, Forward, Back
 * - 4 diagonal directions: ForwardLeft, ForwardRight, BackLeft, BackRight
 * 
 * @author Tianxiong Zhang
 */
public class DiagonalDirectionalBiSpaceSignatureProvider extends BAbstractSignatureProvider<DynamicSignature> {

    public static final String LOCALE_TYPE = "Locale";
    
    // Cardinal directions (4)
    public static final String LEFT_ROUTE_TYPE = "LeftRoute";
    public static final String RIGHT_ROUTE_TYPE = "RightRoute";
    public static final String FORWARD_ROUTE_TYPE = "ForwardRoute";
    public static final String BACK_ROUTE_TYPE = "BackRoute";
    
    // Diagonal directions (4)
    public static final String FORWARD_LEFT_ROUTE_TYPE = "ForwardLeftRoute";
    public static final String FORWARD_RIGHT_ROUTE_TYPE = "ForwardRightRoute";
    public static final String BACK_LEFT_ROUTE_TYPE = "BackLeftRoute";
    public static final String BACK_RIGHT_ROUTE_TYPE = "BackRightRoute";
    
    public static final String OCCUPIED_TYPE = "OccupiedBy";

    private static DiagonalDirectionalBiSpaceSignatureProvider instance;
    private static final Object lock = new Object();

    private DiagonalDirectionalBiSpaceSignatureProvider() {
    }

    public static DiagonalDirectionalBiSpaceSignatureProvider getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DiagonalDirectionalBiSpaceSignatureProvider();
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
                // Cardinal directions
                .add(LEFT_ROUTE_TYPE, 1)
                .add(RIGHT_ROUTE_TYPE, 1)
                .add(FORWARD_ROUTE_TYPE, 1)
                .add(BACK_ROUTE_TYPE, 1)
                // Diagonal directions
                .add(FORWARD_LEFT_ROUTE_TYPE, 1)
                .add(FORWARD_RIGHT_ROUTE_TYPE, 1)
                .add(BACK_LEFT_ROUTE_TYPE, 1)
                .add(BACK_RIGHT_ROUTE_TYPE, 1)
                .add(OCCUPIED_TYPE, 0);
        return defaultBuilder.create();
    }
}

