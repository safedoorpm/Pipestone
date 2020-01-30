/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.ImageIconUtils;
import com.obtuse.util.ThreeDimensionalSortedMap;
import com.obtuse.util.ThreeDimensionalTreeMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.obtuse.ui.MultiPointSlider.OrientedImage;
import static com.obtuse.ui.MultiPointSlider.PositionOnLine;

/**
 * The implementation of {@link MpsKnob} used by default by the {@link com.obtuse.ui.MultiPointSlider} class.
 */

public class DefaultMpsKnob extends MpsKnob {

    public static final float ORIENTED_IMAGE_BRIGHTNESS_FACTOR = 0.9f;
    private final SortedMap<PositionOnLine, Double> _rotations =
	    new TreeMap<>();
    private final ThreeDimensionalSortedMap<MpsKnobSize, PositionOnLine, Boolean, OrientedImage>
            _rotatedSelectedScaledImages =
	    new ThreeDimensionalTreeMap<>();

    public DefaultMpsKnob( final Image image ) {

        super( image );

        for ( PositionOnLine position : PositionOnLine.values() ) {

            switch ( position ) {

                case ABOVE:
                    _rotations.put( PositionOnLine.ABOVE, 0.0 );
                    break;

                case BELOW:
                    _rotations.put( PositionOnLine.BELOW, Math.PI );
                    break;

                case LEFT:
                    _rotations.put( PositionOnLine.LEFT, -Math.PI / 2 );
                    break;

                case RIGHT:
                    _rotations.put( PositionOnLine.RIGHT, Math.PI / 2 );
                    break;

            }

        }

    }

    @Override
    public boolean isPointOnKnob(
            final Point hotSpot,
            final MpsKnobSize knobSize,
            final boolean isSelected,
            final PositionOnLine positionOnLine,
            final Point point
    ) {

        MultiPointSlider.OrientedImage orientedImage = getOrientedImage( knobSize, positionOnLine, isSelected );

        return orientedImage.isPointInImage( hotSpot, point );

    }

    @Override
    public void drawKnob(
            final Graphics2D g,
            final Point hotSpot,
            final MpsKnobSize knobSize,
            final boolean isSelected,
            final PositionOnLine positionOnLine,
            final ImageObserver imageObserver
    ) {

        MultiPointSlider.OrientedImage img = getOrientedImage( knobSize, positionOnLine, isSelected );

        img.drawImage( g, hotSpot );

    }

    @Override
    public MultiPointSlider.OrientedImage getOrientedImage(
            final MpsKnobSize knobSize,
            final PositionOnLine positionOnLine,
            final boolean isSelected
    ) {

        int ks = knobSize.integerSize();

        MultiPointSlider.OrientedImage orientedImage =
                _rotatedSelectedScaledImages.get( knobSize, positionOnLine, isSelected );
        if ( orientedImage == null ) {

            BufferedImage scaledImage = ImageIconUtils.getAsBufferedImage(
                    getImage().getScaledInstance(
                            knobSize.integerSize(),
                            -1,
                            Image.SCALE_SMOOTH
                    )
            );
            BufferedImage selectedScaledImage = ImageIconUtils.changeImageBrightness(
                    scaledImage,
                    DefaultMpsKnob.ORIENTED_IMAGE_BRIGHTNESS_FACTOR,
                    0f
            );
            BufferedImage rotatedImage;
            BufferedImage sourceImage = isSelected ? selectedScaledImage : scaledImage;
            Point hotSpot;
            if ( positionOnLine == PositionOnLine.ABOVE ) {

                rotatedImage = sourceImage;
                hotSpot = new Point( ks / 2, ks + 1 );

            } else {

                if ( positionOnLine == PositionOnLine.LEFT || positionOnLine == PositionOnLine.RIGHT ) {

                    rotatedImage = new BufferedImage(
                            sourceImage.getHeight( null ),
                            sourceImage.getWidth( null ),
                            BufferedImage.TYPE_INT_ARGB
                    );

                    Graphics2D g2d = (Graphics2D)rotatedImage.getGraphics();
                    Double rotation = _rotations.get( positionOnLine );
                    g2d.rotate( rotation.doubleValue() );

                    @SuppressWarnings("UnusedDeclaration")
                    boolean drawImageRval;
                    if ( positionOnLine == PositionOnLine.LEFT ) {

                        //noinspection UnusedAssignment
                        drawImageRval = g2d.drawImage( sourceImage, -rotatedImage.getWidth( null ), 0, null );
                        hotSpot = new Point( ks + 1, ks / 2 );

                    } else {

                        //noinspection UnusedAssignment
                        drawImageRval = g2d.drawImage( sourceImage, 0, -rotatedImage.getWidth( null ), null );
                        hotSpot = new Point( -2, ks / 2 );

                    }

                    g2d.dispose();

                } else {

                    rotatedImage = new BufferedImage(
                            sourceImage.getWidth( null ),
                            sourceImage.getHeight( null ),
                            BufferedImage.TYPE_INT_ARGB
                    );

                    Graphics2D g2d = (Graphics2D)rotatedImage.getGraphics();
                    g2d.rotate( _rotations.get( PositionOnLine.BELOW ).doubleValue() );
                    g2d.drawImage( sourceImage, -rotatedImage.getWidth( null ), -rotatedImage.getHeight( null ), null );
                    hotSpot = new Point( ks / 2, -2 );

                    g2d.dispose();

                }

            }

            orientedImage = new MultiPointSlider.OrientedImage( hotSpot, rotatedImage );
            _rotatedSelectedScaledImages.put( knobSize, positionOnLine, isSelected, orientedImage );

        }

        return orientedImage;

    }

}
