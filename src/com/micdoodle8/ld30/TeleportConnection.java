package com.micdoodle8.ld30;

import com.micdoodle8.ld30base.Direction;
import com.micdoodle8.ld30base.Vector2i;

import java.util.Arrays;
import java.util.List;

public class TeleportConnection
{
    public static class DirectionalPoint
    {
        public final Vector2i point;
        public final int layer;
        public final Direction direction;

        public DirectionalPoint(Vector2i point0, Direction direction0)
        {
            this(point0, 1, direction0);
        }

        public DirectionalPoint(Vector2i point0, int layer, Direction direction0)
        {
            this.point = point0;
            this.direction = direction0;
            this.layer = layer;
        }

        @Override
        public String toString() {
            return "Connection{" +
                    "point=" + point +
                    ", direction=" + direction +
                    '}';
        }
    }

    public final List<DirectionalPoint> connections;
    public final int playerType;

    public TeleportConnection(int playerType, DirectionalPoint... connections)
    {
        this.playerType = playerType;
        this.connections = Arrays.asList(connections);
    }
}
