package org.bigraphs.model.provider.bigridservice.spatial.signature;

import org.bigraphs.framework.core.impl.signature.DynamicSignature;
import org.bigraphs.framework.core.impl.signature.DynamicSignatureBuilder;
import org.bigraphs.model.provider.base.BAbstractSignatureProvider;

import static org.bigraphs.framework.core.factory.BigraphFactory.pureSignatureBuilder;

/**
 * A signature provider for directional bi-spatial bigraphs.
 * Extends the basic signature with directional route types:
 * - LeftRoute: connects to the left neighbor
 * - RightRoute: connects to the right neighbor
 * - ForwardRoute: connects to the forward (up) neighbor
 * - BackRoute: connects to the back (down) neighbor
 *
 * @author Tianxiong Zhang
 */
public class DirectionalBiSpaceSignatureProvider extends BAbstractSignatureProvider<DynamicSignature> {

    public static final String LOCALE_TYPE = "Locale";
    public static final String LEFT_ROUTE_TYPE = "LeftRoute";
    public static final String RIGHT_ROUTE_TYPE = "RightRoute";
    public static final String FORWARD_ROUTE_TYPE = "ForwardRoute";
    public static final String BACK_ROUTE_TYPE = "BackRoute";
    public static final String OCCUPIED_TYPE = "OccupiedBy";

    private static DirectionalBiSpaceSignatureProvider instance;
    private static final Object lock = new Object();

    private DirectionalBiSpaceSignatureProvider() {
    }

    public static DirectionalBiSpaceSignatureProvider getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DirectionalBiSpaceSignatureProvider();
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
                .add(LEFT_ROUTE_TYPE, 1)
                .add(RIGHT_ROUTE_TYPE, 1)
                .add(FORWARD_ROUTE_TYPE, 1)
                .add(BACK_ROUTE_TYPE, 1)
                .add(OCCUPIED_TYPE, 0);
        return defaultBuilder.create();
    }
}

