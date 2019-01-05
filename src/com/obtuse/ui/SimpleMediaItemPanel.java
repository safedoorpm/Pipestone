package com.obtuse.ui;

import com.obtuse.util.lrucache.CachedThing;
import com.obtuse.util.lrucache.LruCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 A customized {@link JPanel} for displaying images.
 */

public class SimpleMediaItemPanel<IID extends SimpleMediaItemPanel.ImageIdentifier> extends JPanel {

    public interface ImageIdentifier extends Comparable<ImageIdentifier> {

        String format();

    }

    public static class ScaledImageId<IID extends ImageIdentifier> implements Comparable {

        private final int _scaleGranularity;
        private final IID _imageID;
        private final int _orientation;

        public ScaledImageId( @NotNull final IID imageID, final int orientation, final int scaleGranularity ) {

            super();

            _imageID = imageID;
            _scaleGranularity = scaleGranularity;
            _orientation = orientation;

        }

        @NotNull
        public IID getImageIdentifier() {

            return _imageID;

        }

        @NotNull
        public String format() {

            return "imageIdentifier=" + _imageID.format();

        }

        @Override
        public int compareTo( @NotNull final Object rhs ) {

            @SuppressWarnings("unchecked") ScaledImageId<IID> rhsAsId = (ScaledImageId<IID>)rhs;
            int rval = _imageID.compareTo( rhsAsId._imageID );
            if ( rval == 0 ) {

                rval = Integer.compare( _scaleGranularity, rhsAsId._scaleGranularity );
                if ( rval == 0 ) {

                    rval = Integer.compare( _orientation, rhsAsId._orientation );

                }

            }

            return rval;

        }

        public int getScaleGranularity() {

            return _scaleGranularity;

        }

        public int getOrientation() {

            return _orientation;

        }

        public String toString() {

            return "ScaledImageId( " +
                   format() + ", " +
                   "scaleGranularity=" + getScaleGranularity() + ", " +
                   "orientation=" + getOrientation() +
                   " )";

        }

    }

    private final LruCache<ScaledImageId, ImageIcon> _imageIconCache;

    private @Nullable final ImageIcon _fullSizeImage;
    private final ImageIdentifier _imageIdentifier;
    private int _orientation;

    private final int _scaleGranularity;
    private final Dimension _naturalSize;
    private Dimension _currentSize;
    @Nullable private ImageIcon _currentSizeImage;

    public SimpleMediaItemPanel(
            @Nullable final ImageIcon fullSizeImage,
            @NotNull final ImageIdentifier imageIdentifier,
            @NotNull final LruCache<ScaledImageId, ImageIcon> scaledImageIconCache,
            int scaleGranularity,
            int initialOrientation
    ) {
        super();

        _fullSizeImage = fullSizeImage;
        _imageIdentifier = imageIdentifier;
        _imageIconCache = scaledImageIconCache;
        _orientation = initialOrientation;

        if ( scaleGranularity <= 0 ) {

            throw new IllegalArgumentException( "SimpleMediaItemPanelBackup:  invalid scaleGranularity=" +
                                                scaleGranularity +
                                                " (must be a positive value)" );

        }

        _scaleGranularity = scaleGranularity;
        if ( fullSizeImage == null ) {

            _naturalSize = new Dimension( 100, 100 );

        } else {

            _naturalSize = new Dimension( fullSizeImage.getIconWidth(), fullSizeImage.getIconHeight() );

        }

        _currentSizeImage = fullSizeImage;
        _currentSize = new Dimension( _naturalSize );

    }

    public SimpleMediaItemPanel(
            @Nullable final Image fullSizeImage,
            @NotNull final ImageIdentifier imageIdentifier,
            @NotNull final LruCache<ScaledImageId, ImageIcon> scaledImageIconCache,
            int scaleGranularity,
            int initialOrientation
    ) {
        this(
                fullSizeImage == null ? null : new ImageIcon( fullSizeImage ),
                imageIdentifier,
                scaledImageIconCache,
                scaleGranularity,
                initialOrientation
        );
    }

    public void setOrientation( int orientation ) {

        if ( _orientation != orientation ) {

            _orientation = orientation;

            repaint();

        }

    }

    public void setZoomFactor( double zoomFactor ) {

        _currentSize = new Dimension(
                (int)Math.round( _naturalSize.width * zoomFactor ),
                (int)Math.round(_naturalSize.height * zoomFactor )
        );

        repaint();

    }

    public int getOrientation() {

        return _orientation;

    }

    @NotNull
    public Dimension getNaturalSize() {

        return new Dimension( _naturalSize );

    }

    @NotNull
    public Dimension getCurrentSize() {

        return new Dimension( _currentSize );

    }

    public boolean hasActualImage() {

        return _fullSizeImage != null;

    }

    @NotNull
    public Optional<ImageIcon> getOptFullSizeImage() {

        return Optional.ofNullable( _fullSizeImage );

    }

    @NotNull Optional<ImageIcon> getOptCurrentSizeImage() {

        return Optional.ofNullable( _currentSizeImage );

    }

    //    public abstract ImageIcon getImageIcon();

    public Optional<ImageIcon> getScaledImage() {

        if ( _fullSizeImage == null ) {

            return Optional.empty();

        }

        Optional<CachedThing<ScaledImageId, ImageIcon>> optScaledImage = _imageIconCache.getOptional(
                new ScaledImageId<>(
                        _imageIdentifier,
                        _orientation,
                        Math.max( _currentSize.width, _currentSize.height )
                )
        );

        return optScaledImage.map( CachedThing::getThing );

    }

    public void paint( Graphics g ) {

//        Logger.logMsg( "painting " + getLmiId().format() );

        Graphics gx = g.create();
//        Graphics2D g2d = gx instanceof Graphics2D ? (Graphics2D)gx : null;

//        Logger.logMsg( "redrawing image" );
        @NotNull Optional<ImageIcon> optImage = getScaledImage();

        gx.setColor( getBackground() );
        int regionWidth = getWidth();
        int regionHeight = getHeight();
        gx.fillRect( 0, 0, regionWidth, regionHeight );
//        gx.setColor( isSelected() ? Color.BLUE : Color.WHITE );
        gx.drawRect( 0, 0, regionWidth - 1, regionHeight - 1 );
        if ( optImage.isPresent() ) {

            ImageIcon imageIcon = optImage.get();
            int imageWidth = imageIcon.getIconWidth();
            int imageHeight = imageIcon.getIconHeight();
            gx.setColor( Color.BLACK );

            gx.drawRect(
                    0,
                    0,
                    imageWidth,
                    imageHeight
            );
            gx.drawImage(
                    imageIcon.getImage(),
                    1,
                    1,
                    this
            );

        } else {

            gx.setColor( Color.WHITE );
            gx.drawLine( 1, 1, regionWidth - 1, regionHeight - 1 );
            gx.drawLine( 1, regionHeight - 1, regionWidth - 1, 1 );

        }

        gx.dispose();

    }

}
