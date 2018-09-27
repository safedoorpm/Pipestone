package com.obtuse.ui;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.exceptions.ObtuseImageLoadFailed;
import com.obtuse.util.Logger;
import com.obtuse.util.Measure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 Created by danny on 2018/09/04.
 */

public class ObtuseImageUtils {

    @NotNull
    public static ImageIcon maybeRegenerateThumbnail(
            final @NotNull ImageIcon originalImageIcon,
            @SuppressWarnings("SameParameterValue") @Nullable final ImageIcon scaledImageIcon,
            int thumbnailSize,
            boolean verbose
    ) {

        int origW = originalImageIcon.getIconWidth();
        int origH = originalImageIcon.getIconHeight();

        // Just return the original if the thumbnail would be the same size or bigger than the original.

        if ( thumbnailSize >= origW && thumbnailSize >= origH ) {

            return originalImageIcon;

        }

        Dimension newSize = getMinimumScalingFactor( thumbnailSize, thumbnailSize, origW, origH );

        ImageIcon rval = scaledImageIcon;

        if ( rval == null || newSize.width != rval.getIconWidth() || newSize.height != rval.getIconHeight() ) {

            if ( verbose ) Logger.logMsg(
                    ( rval == null ? "" : "re" ) + "scaling (" + origW + 'x' + origH + ") to (" + newSize.width + 'x' + newSize.height +
                    ")" );

            rval = new ImageIcon(
                    originalImageIcon.getImage().getScaledInstance(
                            newSize.width,
                            newSize.height,
                            Image.SCALE_SMOOTH
                    ),
                    originalImageIcon.getDescription()
            );

        }

        return rval;

    }

    @SuppressWarnings("WeakerAccess")
    public static Dimension getMinimumScalingFactor( final int boxW, final int boxH, final int origW, final int origH ) {

        // Compute the scaling factor which yields the desired width
        // and the scaling factor which yields the desired height.

        double sf1 = boxW / (double)origW;
        double sf2 = boxH / (double)origH;

        // The larger of the two could yield an image that doesn't fit in the box so we pick the smaller of the two if they aren't equal.

        Dimension rval;
        if ( sf1 < sf2 ) {

            rval = new Dimension( boxW, (int)( origH * sf1 ) );
            if ( rval.width != boxW && rval.height != boxH ) {

//                setImageState( ImageState.BROKEN );
                String msg = "orig " + origW + 'x' + origH + " yielded width-based sf " + sf1 + " which yields scaled " + rval.width + 'x' +
                             rval.height + " which is not exact for either width or height";
                throw new HowDidWeGetHereError( msg );

            }

        } else {

            rval = new Dimension( (int)( origW * sf2 ), boxH );
            if ( rval.width != boxW && rval.height != boxH ) {

//                setImageState( ImageState.BROKEN );
                String msg =
                        "orig " + origW + 'x' + origH + " yielded height-based sf " + sf2 + " which yields scaled " + rval.width + 'x' +
                        rval.height + " which is not exact for either width or height";
                throw new HowDidWeGetHereError( msg );

            }

        }

        return rval;

    }

    @NotNull
    public static BufferedImage loadImage( final @NotNull File imageLocation, boolean verbose )
            throws IOException, ObtuseImageLoadFailed {

        if ( verbose ) Logger.logMsg( "loading primary image from " + imageLocation );

        Measure m = new Measure( "OIU.loadImage" );

        try {

            @SuppressWarnings("UnnecessaryLocalVariable")
            BufferedImage image = ImageIO.read( imageLocation );

            if ( image == null ) {

                throw new ObtuseImageLoadFailed( "ObtuseImageFile.loadPrimaryImage:  image decoder returned null (no idea why - sorry)" );

            }
//            else if ( _cachedImageWidth == -1 || _cachedImageHeight == -1 ) {
//
//                _cachedImageWidth = image.getWidth();
//                _cachedImageHeight = image.getHeight();
//
//            }

            return image;

        } finally {

            m.done();

        }

    }

    /**
     Convert, if necessary, an image to a {@link BufferedImage}.
     <p>Note that if {@code inputImage} actually is a {@code BufferedImage} then the return value of this method is
     {@code inputImage}.</p>
     <p><b>DO NOT ASSUME THAT THIS METHOD ALWAYS YIELDS A NEWLY CREATED IMAGE.</b></p>
     @param inputImage the image.
     @return the image as a {@link BufferedImage}.
     Note that if {@code inputImage} actually is a {@code BufferedImage} then the return value of this method is
     {@code inputImage}. <p><b>DO NOT ASSUME THAT THIS METHOD ALWAYS YIELDS A NEWLY CREATED IMAGE.</b></p>
     */

    public static BufferedImage convertImageToBufferedImage( Image inputImage ) {

        if ( inputImage instanceof BufferedImage ) {

            return (BufferedImage)inputImage;

        }

        BufferedImage newImage = new BufferedImage(
                inputImage.getWidth( null ),
                inputImage.getHeight( null ),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics g = newImage.createGraphics();
        g.drawImage( inputImage, 0, 0, null );
        g.dispose();

        return newImage;

    }

    /**
     Flip an image horizontally.
     @param inputImage the image to be flipped.
     @return the flipped image as a {@link BufferedImage}.
     */

    public static BufferedImage flipHorizontally( Image inputImage ) {

        BufferedImage bufferedInputImage = convertImageToBufferedImage( inputImage );
        BufferedImage newImage = new BufferedImage(
                bufferedInputImage.getWidth(),
                bufferedInputImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D gg = newImage.createGraphics();
        gg.drawImage( bufferedInputImage, bufferedInputImage.getWidth(), 0, -bufferedInputImage.getWidth(), bufferedInputImage.getHeight(), null );
        gg.dispose();

        return newImage;
    }

    /**
     Flip an image vertically.
     @param inputImage the image to be flipped.
     @return the flipped image as a {@link BufferedImage}.
     */

    public static BufferedImage flipVertically( Image inputImage ) {

        BufferedImage bufferedInputImage = convertImageToBufferedImage( inputImage );
        BufferedImage newImage = new BufferedImage(
                bufferedInputImage.getWidth(),
                bufferedInputImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D gg = newImage.createGraphics();
        gg.drawImage( bufferedInputImage, 0, bufferedInputImage.getHeight(), bufferedInputImage.getWidth(), -bufferedInputImage.getHeight(), null );
        gg.dispose();

        return newImage;
    }

    public static BufferedImage rotateDegrees( Image image, double degrees ) {

        return rotateRadians( image, Math.toRadians( degrees ) );

    }

    /**
     Rotate an image a specified angle in radians.
     @param inputImage the image.
     @param rotationRadians how far to rotate it.
     @return the rotated image as a {@link BufferedImage}.
     The returned image is exactly large enough to contain the rotated image.
     A rotation through some multiple of &pi;/2 radians (90 degrees) yields the obvious result.
     Other rotations yield images with triangles of transparent pixels in the space in the four 'corners' of the rotated result that are not covered by pixels from the input image.
     <p>From the proposed Stack Overflow solution by "Reverend Gonzi" at</p>
     <blockquote><a href="https://stackoverflow.com/questions/4156518/rotate-an-image-in-java">
     https://stackoverflow.com/questions/4156518/rotate-an-image-in-java</a>
     (referenced 2018-09-05)</blockquote>
     */

    public static BufferedImage rotateRadians( Image inputImage, double rotationRadians ) {

        BufferedImage bufferedInputImage = convertImageToBufferedImage( inputImage );

        double sin = Math.abs( Math.sin( rotationRadians ) ), cos = Math.abs( Math.cos( rotationRadians ) );
        int w = bufferedInputImage.getWidth(), h = bufferedInputImage.getHeight();
        int neww = (int)Math.floor( w * cos + h * sin ), newh = (int)Math.floor( h * cos + w * sin );

        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage( neww, newh, Transparency.TRANSLUCENT );
        Graphics2D g = result.createGraphics();

        g.translate( ( neww - w ) / 2, ( newh - h ) / 2 );
        g.rotate( rotationRadians, w / 2.0, h / 2.0 );
        g.drawRenderedImage( bufferedInputImage, null );

        g.dispose();

        return result;

    }

    public static GraphicsConfiguration getDefaultConfiguration() {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();

    }

}
