package org.bigraphs.model.provider.bigridservice.handler;

import org.bigraphs.model.provider.base.BLocationModelData;
import org.bigraphs.model.provider.bigridservice.data.request.GridSpecRequest;
import org.swarmwalker.messages.*;

import java.util.LinkedList;

public class BLocationToBiGridConverter {

    public static BiGrid convert(BLocationModelData modelData, GridSpecRequest gridSpec) {
        BiGrid.Builder gridBuilder = BiGrid.newBuilder();

        LinkedList<BLocationModelData.Locale> locales = modelData.getLocales();

        for (BLocationModelData.Locale locale : locales) {
            Cell.Builder cellBuilder = Cell.newBuilder();

            // ID
            cellBuilder.setId(locale.getName());

            // Pose (assume z and w = 0 for 2D)
            Pose pose = Pose.newBuilder()
                    .setX((float) locale.getCenter().getX())
                    .setY((float) locale.getCenter().getY())
                    .setZ(0f)
                    .setW(0f)
                    .setType(Type.Cartesian)
                    .build();
            cellBuilder.setPose(pose);

            // Size (height = 0, units assumed to be meters)
            Size size = Size.newBuilder()
                    .setUnit(Unit.Meter)
                    .setWidth(locale.getWidth())
                    .setLength(locale.getDepth())
                    .setHeight(1f)
                    .build();
            cellBuilder.setSize(size);

            // Optional DataPayload: e.g., add locale name
            Cell.DataPayload namePayload = Cell.DataPayload.newBuilder()
                    .setType("name")
                    .setPayload(locale.getName())
                    .build();
            cellBuilder.addAttributes(namePayload);

            // Add cell to grid
            gridBuilder.addItems(cellBuilder.build());
        }

        return gridBuilder.build();
    }
}
