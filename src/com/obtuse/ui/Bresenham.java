package com.obtuse.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Compute Bresenham lines.
 */

public class Bresenham {

    /**
     * Compute the points on a line.
     * @param start the start of the line.
     * @param end the end of the line.
     * @return the line as a list of points.
     */

    public List<Point> computeLine( final Point start, final Point end ) {

        int x0 = start.x;
        int y0 = start.y;
        int x1 = end.x;
        int y1 = end.y;

        List<Point> line = new ArrayList<>();

        int dx = Math.abs( x1 - x0 );
        int dy = Math.abs( y1 - y0 );

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;
        int e2;

        while ( true ) {

            line.add( new Point( x0, y0 ) );

            if ( x0 == x1 && y0 == y1 ) {

                break;

            }

            e2 = 2 * err;
            if ( e2 > -dy ) {

                err = err - dy;
                x0 = x0 + sx;

            }

            if ( e2 < dx ) {

                err = err + dx;
                y0 = y0 + sy;

            }

        }

        return line;

    }

}
