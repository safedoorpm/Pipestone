/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.exceptions.HowDidWeGetHereError;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import java.util.Optional;

/**
 * Utility methods for creating icons from images stored in our resources package.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public class ImageIconUtils {

    private static String s_resourcesBaseDirectory = ".";

    private ImageIconUtils() {

        super();

    }

    public static Optional<ImageIcon> fetchIconImage( final String fileName ) {

        return ImageIconUtils.fetchIconImage( fileName, 0 );

    }

    public static void setDefaultResourcesDirectory( final String resourcesBaseDirectory ) {

        ImageIconUtils.s_resourcesBaseDirectory = resourcesBaseDirectory;

    }

    @NotNull
    public static String getDefaultResourceBaseDirectory() {

        return ImageIconUtils.s_resourcesBaseDirectory;

    }

    public static Optional<ImageIcon> fetchIconImage( final String fileName, final int size ) {

        return ImageIconUtils.fetchIconImage( fileName, size, ImageIconUtils.s_resourcesBaseDirectory );

    }

    @NotNull
    public static Optional<ImageIcon> fetchIconImage(
            @NotNull final String fileName,
            final int size,
            @NotNull final String resourceBaseDirectory
    ) {

        return fetchIconImage(
                fileName,
                size,
                ImageIconUtils.class.getClassLoader(),
                resourceBaseDirectory
        );

    }

    @NotNull
    public static Optional<ImageIcon> fetchIconImage(
            @NotNull final String fileName,
            final int size,
            @NotNull ClassLoader classLoader,
            @NotNull final String resourceBaseDirectory
    ) {

        URL url = null;
        String resourcePath = resourceBaseDirectory + '/' + fileName;
        try {

            url = classLoader.getResource( resourcePath );

        } catch ( Throwable e ) {

            Logger.logErr( "Unable to load resource from " + ObtuseUtil.enquoteToJavaString( resourcePath ), e );

            ObtuseUtil.doNothing();

            // just ignore whatever went wrong

        }

        ImageIcon rval;
        if ( url == null ) {

            return Optional.empty();

        } else {

            rval = new ImageIcon( url );

        }

        if ( size == 0 ) {

            return Optional.of( rval );

        } else {

            int rvalWidth = rval.getIconWidth();
            int rvalHeight = rval.getIconHeight();

            int scaledWidth = rvalWidth >= rvalHeight ? size : -1;
            int scaledHeight = rvalWidth <= rvalHeight ? size : -1;
            if ( scaledWidth == -1 && scaledHeight == -1 ) {

                throw new HowDidWeGetHereError(
                        "ImageIconUtils.fetchIconImage:  " +
                        "rvW=" + rvalWidth + ", rvH=" + rvalHeight + ", s=" + size +
                        " yielded both sW and sH equal to " + -1
                );

            }

            ImageIcon scaledRval = new ImageIcon(
                    rval.getImage()
                        .getScaledInstance(
                                scaledWidth,
                                scaledHeight,
                                Image.SCALE_SMOOTH
                        )
            );

            return Optional.of( scaledRval );

        }

    }

    /**
     * Get a {@link java.awt.image.BufferedImage} version of an {@link java.awt.Image}.
     * Identical to {@link #copyToBufferedImage(java.awt.Image)} except that the original image is returned if it is a {@link java.awt.image
     * .BufferedImage}.
     * <p/>
     * This method came from
     * <blockquote>
     * http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
     * </blockquote>
     * I (danny) don't know the terms of use as their "Terms of Use" link didn't do anything in either Safari or Firefox on my Mac OS X Snow
     * Leopard system.
     *
     * @param xImage the image to be converted.
     * @return the original image if it is a {@link java.awt.image.BufferedImage}; otherwise, the original image converted to a {@link java.awt
     * .image.BufferedImage}.
     */

    @NotNull
    public static BufferedImage toBufferedImage( @NotNull final Image xImage ) {

        if ( xImage instanceof BufferedImage ) {

            return (BufferedImage)xImage;

        }
        return ImageIconUtils.copyToBufferedImage( xImage );

    }

    /**
     * Make a {@link java.awt.image.BufferedImage} copy of an {@link java.awt.Image}.
     * Identical to {@link #toBufferedImage(java.awt.Image)} except that a new image is returned even if the original image is a {@link java.awt
     * .image.BufferedImage}.
     * <p/>
     * This method came from
     * <blockquote>
     * http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
     * </blockquote>
     * I (danny) don't know the terms of use as their "Terms of Use" link didn't do anything in either Safari or Firefox on my Mac OS X Snow
     * Leopard system.
     *
     * @param xImage the image to be converted.
     * @return a copy of the original image.
     */

    @NotNull
    public static BufferedImage copyToBufferedImage( final Image xImage ) {

        // This code ensures that all the pixels in the image are loaded

        Image image = new ImageIcon( xImage ).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see Determining If an Image Has Transparent Pixels

        boolean hasAlpha = ImageIconUtils.hasAlpha( image );

        // Create a buffered image with a format that's compatible with the screen

        BufferedImage bImage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {

            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if ( hasAlpha ) {

                transparency = Transparency.BITMASK;

            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bImage = gc.createCompatibleImage(
                    image.getWidth( null ), image.getHeight( null ), transparency
            );

        } catch ( HeadlessException e ) {

            // The system does not have a screen

        }

        if ( bImage == null ) {

            // Create a buffered image using the default color model

            int type = BufferedImage.TYPE_INT_RGB;
            if ( hasAlpha ) {

                type = BufferedImage.TYPE_INT_ARGB;

            }

            bImage = new BufferedImage( image.getWidth( null ), image.getHeight( null ), type );

        }

        // Copy image to buffered image

        Graphics g = bImage.createGraphics();

        // Paint the image onto the buffered image

        g.drawImage( image, 0, 0, null );
        g.dispose();

        return bImage;

    }

    /**
     * This method returns true if the specified image has transparent pixels.
     * <p/>
     * This method came from
     * <blockquote>
     * http://www.exampledepot.com/egs/java.awt.image/HasAlpha.html
     * </blockquote>
     * I (danny) don't know the terms of use as their "Terms of Use" link didn't do anything in either Safari or Firefox on my Mac OS X Snow
     * Leopard system.
     *
     * @param image the image to be inspected.
     * @return true if the image has transparent pixels; false otherwise.
     */

    public static boolean hasAlpha( final Image image ) {

        // If buffered image, the color model is readily available
        if ( image instanceof BufferedImage ) {

            BufferedImage bImage = (BufferedImage)image;
            return bImage.getColorModel().hasAlpha();

        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber( image, 0, 0, 1, 1, false );
        //noinspection EmptyCatchBlock
        try {

            pg.grabPixels();

        } catch ( InterruptedException e ) {

        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();

    }

    public static int getMaxColorComponentValue( @NotNull final BufferedImage bufferedImage ) {

        ColorModel colorModel = bufferedImage.getColorModel();

        if ( colorModel instanceof DirectColorModel ) {

            DirectColorModel dcm = (DirectColorModel)colorModel;

            int componentSize = dcm.getComponentSize( 0 );
            int maxValue = ( 1 << componentSize ) - 1;

            return maxValue;

        } else {

            throw new IllegalArgumentException(
                    "ImageIconUtils.getMaxColorComponentValue:  " +
                    "image uses unsupported color model (" + colorModel.getClass().getCanonicalName() + ")" +
                    " - it must use the DirectColorModel"
            );

        }

    }

    public static float getScaledOffsetValue( @NotNull final BufferedImage bufferedImage, float unscaledOffset ) {

        return getMaxColorComponentValue( bufferedImage ) * unscaledOffset;

    }

    /**
     * Create a new {@link java.awt.image.BufferedImage} which is brighter or darker than the specified {@link java.awt.Image}.
     *
     * @param image  the image to be brightened or darkened.
     * @param factor how much the image is to be brightened (if greater than 1) or darkened (if less than 1).
     *               For example, {@code 1.2} makes the image 20% brighter whereas {@code 0.8} makes the image 20% darker.
     * @param offset an amount added to each pixel after it is multiplied by {@code scaleFactor}.
     *               This can be used in combination with a {@code scaleFactor} of 1.0f to yield a brighter or darker
     *               image in which the value of each pixel is adjusted by a constant delta.
     *               <p>Note #1: Precisely what you see/get with this approach can be difficult to describe in advance.</p>
     *               <p>Note #2: If you have the offset represented as a {@code float} value in the range {@code [0,1]}
     *               then run it through {@link #getScaledOffsetValue(BufferedImage, float)} to turn it into a colour
     *               component offset value and pass that value as the {@code offset} parameter to this method.
     *               Put another way, if the image uses 8 bits for each colour component
     *               then you need an offset value of 128f if you want to offset each value by 50% since 128f is
     *               0.5f * 255 (close enough).
     *               <blockquote>
     *               {@code ImageIconUtils.getScaledOffsetValue( offset )}
     </blockquote></p>
     * @return the brighter or darker image (always a new image even if the scaling factor is 1.0).
     */

    @NotNull
    public static BufferedImage changeImageBrightness( final Image image, final float factor, final float offset ) {

        BufferedImage bufferedVersion = ImageIconUtils.copyToBufferedImage( image );

        RescaleOp op = new RescaleOp( factor, offset, null );
//        ColorModel colorModel = bufferedVersion.getColorModel();
//        RescaleOp op;
//        if ( colorModel instanceof DirectColorModel ) {
//
//            DirectColorModel dcm = (DirectColorModel)colorModel;
//
//            int componentSize = dcm.getComponentSize( 0 );
//            int maxValue = ( 1 << componentSize ) - 1;
//            op = new RescaleOp( factor, offset * maxValue, null );
//
//            ObtuseUtil.doNothing();
//
//        } else {
//
//            op = new RescaleOp( factor, 0f, null );
//
//        }

        op.filter( bufferedVersion, bufferedVersion );

        return bufferedVersion;

    }

    /**
     * Create a new {@link javax.swing.ImageIcon} which is brighter or darker than the specified {@link javax.swing.ImageIcon}.
     *
     * @param imageIcon   the {@link javax.swing.ImageIcon} to be brightened or darkened.
     * @param scaleFactor how much the {@link javax.swing.ImageIcon} is to be brightened (if greater than 1) or darkened (if less than 1).
     *                    For example, 1.2 makes the image 20% brighter whereas 0.8 makes the image 20% darker.
     * @return the brighter or darker image (always a new {@link javax.swing.ImageIcon} even if the scaling factor is 1.0).
     */

    @NotNull
    public static ImageIcon changeImageIconBrightness( final ImageIcon imageIcon, final float scaleFactor ) {

        return changeImageIconBrightness( imageIcon, scaleFactor, 0f );

    }

    /**
     * Create a new {@link javax.swing.ImageIcon} which is brighter or darker than the specified {@link javax.swing.ImageIcon}.
     *
     * @param imageIcon   the {@link javax.swing.ImageIcon} to be brightened or darkened.
     * @param scaleFactor how much the {@link javax.swing.ImageIcon} is to be brightened (if greater than 1) or darkened (if less than 1).
     *                    For example, 1.2f makes the image 20% brighter whereas 0.8 makes the image 20% darker.
     * @param offset      an amount added to each pixel after it is multiplied by {@code scaleFactor}. This can be used in
     *                    combination with a {@code scaleFactor} of 1.0f to yield a brighter or darker image in which
     *                    the value of each pixel is adjusted by a constant delta as opposed to a multiplicative scaling.
     *                    Precisely what you see/get with this approach can be difficult to describe in advance.
     * @return the brighter or darker image (always a new {@link javax.swing.ImageIcon} even if the scaling factor is 1.0).
     */

    @NotNull
    public static ImageIcon changeImageIconBrightness( final ImageIcon imageIcon, final float scaleFactor, final float offset ) {

        return new ImageIcon( ImageIconUtils.changeImageBrightness( imageIcon.getImage(), scaleFactor, offset ) );

    }

}
