/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.exceptions.HowDidWeGetHereError;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Utility methods for creating icons from images stored in our resources package.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public class ImageIconUtils {

    private static String s_resourcesBaseDirectory = ".";
    private static ClassLoader s_ourClassLoader = ImageIconUtils.class.getClassLoader();

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

    private static final SortedMap<String,ImageIcon> s_imageIconCache = new TreeMap<>();

    private static String makeFnameKey(
            @NotNull final String fileName,
            final int size,
            @NotNull final ClassLoader classLoader,
            @NotNull final String resourcesBaseDirectory
    ) {

        return "" +
               "cl=" + classLoader.getName() + ";" +
               "fn=" + fileName + ";" +
               "rbd=" + resourcesBaseDirectory + ";" +
               "s=" + size;

    }

    public static Optional<ImageIcon> fetchCacheableIconImage( final String fileName, final int size ) {

        return ImageIconUtils.fetchIconImage(
                fileName,
                true,
                size,
                s_ourClassLoader,
                getDefaultResourceBaseDirectory()
        );

    }

    public static Optional<ImageIcon> fetchIconImage( final String fileName, final int size ) {

        return ImageIconUtils.fetchIconImage(
                fileName,
                false,
                size,
                s_ourClassLoader,
                getDefaultResourceBaseDirectory()
        );

    }

    @NotNull
    public static Optional<ImageIcon> fetchCacheableIconImage(
            @NotNull final String fileName,
            final int size,
            @NotNull final String resourceBaseDirectory
    ) {

        return fetchIconImage(
                fileName,
                true,
                size,
                s_ourClassLoader,
                resourceBaseDirectory
        );

    }

    @NotNull
    public static Optional<ImageIcon> fetchIconImage(
            @NotNull final String fileName,
            final int size,
            @NotNull final String resourceBaseDirectory
    ) {

        return fetchIconImage(
                fileName,
                false,
                size,
                s_ourClassLoader,
                resourceBaseDirectory
        );

    }

    @NotNull
    private static Optional<ImageIcon> fetchCacheableIconImage(
            @NotNull final String fileName,
            final int size,
            @NotNull ClassLoader classLoader,
            @NotNull final String resourceBaseDirectory
    ) {

        return fetchIconImage(
                fileName,
                true,
                size,
                classLoader,
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

        return fetchIconImage(
                fileName,
                false,
                size,
                classLoader,
                resourceBaseDirectory
        );

    }

    @NotNull
    public static Optional<ImageIcon> fetchCacheableIconImage(
            @NotNull final String fileName,
            final int size,
            @NotNull ClassLoader classLoader,
            @NotNull final String[] resourceBaseDirectories
    ) {

        return fetchIconImage(
                fileName,
                true,
                size,
                classLoader,
                resourceBaseDirectories
        );

    }

    @NotNull
    public static Optional<ImageIcon> fetchIconImage(
            @NotNull final String fileName,
            final int size,
            @NotNull ClassLoader classLoader,
            @NotNull final String[] resourceBaseDirectories
    ) {

        return fetchIconImage(
                fileName,
                false,
                size,
                classLoader,
                resourceBaseDirectories
        );

    }

    @NotNull
    public static Optional<ImageIcon> fetchIconImage(
            @NotNull final String fileName,
            final boolean cacheable,
            final int size,
            @NotNull ClassLoader classLoader,
            @NotNull final String... resourceBaseDirectories
    ) {

        Optional<ImageIcon> rval;
        for ( String resourceBaseDirectory : resourceBaseDirectories ) {

            rval = imageIconFetcher(
                    fileName,
                    cacheable,
                    size,
                    classLoader,
                    resourceBaseDirectory
            );

            if ( rval.isPresent() ) {

                return rval;

            }

        }

        return Optional.empty();

    }

    private static Optional<ImageIcon> imageIconFetcher(
            @NotNull final String fileName,
            final boolean cacheable,
            final int size,
            @NotNull ClassLoader classLoader,
            @NotNull final String resourceBaseDirectory
    ) {

        // If this request is cacheable then do the entire operation with the cache locked.

        if ( cacheable ) {

            synchronized ( s_imageIconCache ) {

                String fnameKey = makeFnameKey(
                        fileName,
                        size,
                        classLoader,
                        resourceBaseDirectory
                );

                Optional<ImageIcon> optImageIcon = Optional.ofNullable( s_imageIconCache.get( fnameKey ) );

                if ( optImageIcon.isEmpty() ) {

                    optImageIcon = actuallyAttemptToFetchIcon(
                            fileName,
                            cacheable,
                            size,
                            classLoader,
                            resourceBaseDirectory
                    );

                    optImageIcon.ifPresent(
                            imageIcon -> {

                                s_imageIconCache.put( fnameKey, imageIcon );

                                ObtuseUtil.doNothing();

                            }
                    );

                }

                return optImageIcon;

            }

        }

        // The request is NOT cacheable so we can proceed without worry about what other threads might be up to.

        return actuallyAttemptToFetchIcon(
                fileName,
                cacheable,
                size,
                classLoader,
                resourceBaseDirectory
        );

    }

    private static Optional<ImageIcon> actuallyAttemptToFetchIcon(
            @NotNull final String fileName,
            final boolean cacheable,
            final int size,
            @NotNull ClassLoader classLoader,
            @NotNull final String resourceBaseDirectory
    ) {

        URL url = null;
        String resourcePath = resourceBaseDirectory + '/' + fileName;
        try {

            url = classLoader.getResource( resourcePath );

            if ( url == null ) {

                return Optional.empty();

            }

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

            ObtuseUtil.doNothing();

        }

        // If there is no valid ImageIcon in the file then don't return anything.

        if ( rval.getIconHeight() < 0 || rval.getIconWidth() < 0 ) {

            return Optional.empty();

        }

        rval = getScaledImageIcon( size, rval );

        return Optional.of( rval );

    }

    @NotNull
    public static ImageIcon getScaledImageIcon( final int size, @NotNull final ImageIcon imageIcon ) {

        if ( size == 0 ) {

            return imageIcon;

        } else {

            int rvalWidth = imageIcon.getIconWidth();
            int rvalHeight = imageIcon.getIconHeight();

            int scaledWidth = rvalWidth >= rvalHeight ? size : -1;
            int scaledHeight = rvalWidth <= rvalHeight ? size : -1;
            if ( scaledWidth == -1 && scaledHeight == -1 ) {

                throw new HowDidWeGetHereError(
                        "ImageIconUtils.getScaledImageIcon:  " +
                        "rvW=" + rvalWidth + ", rvH=" + rvalHeight + ", s=" + size +
                        " yielded both sW and sH equal to " + -1
                );

            }

            ImageIcon scaledRval = new ImageIcon(
                    imageIcon.getImage()
                        .getScaledInstance(
                                scaledWidth,
                                scaledHeight,
                                Image.SCALE_SMOOTH
                        )
            );

            return scaledRval;

        }

    }

    /**
     * Get a {@link java.awt.image.BufferedImage} version of an {@link java.awt.Image}.
     * Identical to {@link #forceCopyToBufferedImage(java.awt.Image)} except that the original image is returned if it is a {@link java.awt.image
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
    public static BufferedImage getAsBufferedImage( @NotNull final Image xImage ) {

        if ( xImage instanceof BufferedImage ) {

            return (BufferedImage)xImage;

        }
        return ImageIconUtils.forceCopyToBufferedImage( xImage );

    }

    /**
     * Make a {@link java.awt.image.BufferedImage} copy of an {@link java.awt.Image}.
     * Identical to {@link #getAsBufferedImage(java.awt.Image)} except that a new image is returned even if the original image is a {@link java.awt
     * .image.BufferedImage}.
     * <p/>
     * This method came from
     * <blockquote>
     * http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
     * </blockquote>
     * (note that the above site is now gone - 2020-02-21)
     * I (danny) don't know the terms of use as their "Terms of Use" link didn't do anything in either
     * Safari or Firefox on my Mac OS X Snow Leopard system.
     *
     * @param xImage the image to be converted.
     * @return a copy of the original image.
     */

    @NotNull
    public static BufferedImage forceCopyToBufferedImage( final Image xImage ) {

        // This code ensures that all the pixels in the image are loaded

        Image image = new ImageIcon( xImage ).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see Determining If an Image Has Transparent Pixels

        boolean hasAlpha = ImageIconUtils.hasAlpha( image );


        // This first approach commented out 2020-02-21 because it sometimes produces a type=0 BufferedImage
        // which other library methods cannot cope with.

    //        BufferedImage bImage = null;
    //        // Create a buffered image with a format that's compatible with the screen
    //
    //        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    //        try {
    //
    //            // Determine the type of transparency of the new buffered image
    //            int transparency = Transparency.OPAQUE;
    //            if ( hasAlpha ) {
    //
    //                transparency = Transparency.BITMASK;
    //
    //            }
    //
    //            // Create the buffered image
    //
    //            GraphicsDevice gs = ge.getDefaultScreenDevice();
    //            GraphicsConfiguration gc = gs.getDefaultConfiguration();
    //            bImage = gc.createCompatibleImage(
    //                    image.getWidth( null ), image.getHeight( null ), transparency
    //            );
    //
    //        } catch ( HeadlessException e ) {
    //
    //            // The system does not have a screen
    //
    //            ObtuseUtil.doNothing();
    //
    //        }
    //
    //        if ( bImage == null ) {
    //
    //            // Create a buffered image using the default color model
    //
    //            int type = BufferedImage.TYPE_INT_RGB;
    //            if ( hasAlpha ) {
    //
    //                type = BufferedImage.TYPE_INT_ARGB;
    //
    //            }
    //
    //            bImage = new BufferedImage( image.getWidth( null ), image.getHeight( null ), type );
    //
    //        }

        // End of what was commented out 2020-02-21 (see above for more info)

        // Create a buffered image using the default color model

        int type = BufferedImage.TYPE_INT_RGB;
        if ( hasAlpha ) {

            type = BufferedImage.TYPE_INT_ARGB;

        }

        BufferedImage bImage = new BufferedImage( image.getWidth( null ), image.getHeight( null ), type );

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

        BufferedImage bufferedVersion = ImageIconUtils.forceCopyToBufferedImage( image );

        RescaleOp op = new RescaleOp( factor, offset, null );

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

    /**
     Fetch an {@link ImageIcon} from a specified filename.
     <p>
     A call to this method is equivalent to calling
     <blockquote>
     <pre>fetchMandatoryIcon( iconFileName, 0 );</pre>
     </blockquote>
     </p>@param iconFileName the name of the file.
     @return the requested {@link ImageIcon}.
     <ul>
     <li>the file is assumed to exist in the directory specified by the most recent call to
     {@link #setDefaultResourcesDirectory(String)}.
     </li>
     <li>
     no scaling, brightening, darkening or any other transformations are applied to the
     {@code ImageIcon} before it is returned
     </li>
     </ul>
     @throws IllegalArgumentException if there is no file with the specified name or if the file does not contain a
     valid {@code ImageIcon}.
     */

    @NotNull
    public static ImageIcon fetchMandatoryIcon(
            @NotNull final String iconFileName
    ) {

        return fetchMandatoryIcon( iconFileName, 0 );

    }

    /**
     Fetch an {@link ImageIcon} from a specified filename.
     <p>
     A call to this method is equivalent to calling
     <blockquote>
     <pre>fetchMandatoryIcon(
         iconFileName,
         size,
         ImageIconUtils.class.getClassLoader(),
         ImageIconUtils.getDefaultResourceBaseDirectory()
     );
     </pre>
     </blockquote>
     </p>
     See {@link #fetchMandatoryIcon(String, int, ClassLoader, String)} for more information.
     @param iconFileName the name of the file.
     @param size the size of a square that the icon must fit within.
     The icon will be scaled (larger or smaller) to make its longest dimension equal to the specified size.
     @return the requested {@link ImageIcon}.
     <ul>
     <li>the file is assumed to exist in the directory specified by the most recent call to
     {@link #setDefaultResourcesDirectory(String)}.
     </li>
     <li>
     no scaling, brightening, darkening or any other transformations are applied to the
     {@code ImageIcon} before it is returned
     </li>
     </ul>
     @throws IllegalArgumentException if there is no file with the specified name or if the file does not contain a
     valid {@code ImageIcon}.
     */

    @NotNull
    public static ImageIcon fetchMandatoryIcon(
            @NotNull final String iconFileName,
            final int size
    ) {

        return fetchMandatoryIcon( iconFileName, size, ImageIconUtils.class.getClassLoader(), s_resourcesBaseDirectory );

    }

    /**
     Fetch an {@link ImageIcon} from a specified filename.
     <p>
     A call to this method is equivalent to calling
     <blockquote>
     <pre>fetchMandatoryIcon(
         iconFileName,
         size,
         ImageIconUtils.class.getClassLoader(),
         resourcesDirectory
     );
     </pre>
     </blockquote>
     </p>
     See {@link #fetchMandatoryIcon(String, int, ClassLoader, String)} for more information.
     @param iconFileName the name of the file.
     @param size the size of a square that the {@code ImageIcon} must fit within.
     The {@code ImageIcon} will be scaled (larger or smaller) to make its longest dimension equal to the specified size.
     @param resourcesDirectory the path of the resource directory that the {@code ImageIcon} should be fetched from.
     @return the requested {@link ImageIcon}.
     <ul>
     <li>the file is assumed to exist in the directory specified by the most recent call to
     {@link #setDefaultResourcesDirectory(String)}.
     </li>
     <li>
     no scaling, brightening, darkening or any other transformations are applied to the
     {@code ImageIcon} before it is returned
     </li>
     </ul>
     @throws IllegalArgumentException if there is no file with the specified name or if the file does not contain a
     valid {@code ImageIcon}.
     */

    @NotNull
    public static ImageIcon fetchMandatoryIcon(
            @NotNull final String iconFileName,
            final int size,
            final String resourcesDirectory
    ) {

        return fetchMandatoryIcon(
                iconFileName,
                size,
                ImageIconUtils.class.getClassLoader(),
                resourcesDirectory
        );

    }

    /**
     Fetch an {@link ImageIcon} from a specified filename.
     @param iconFileName the name of the file.
     @param size the size of a square that the {@code ImageIcon} must fit within.
     The {@code ImageIcon} will be scaled (larger or smaller) to make its longest dimension equal to the specified size.
     @param classLoader the class loader via which the {@code ImageIcon} is to be loaded.
     @param resourcesDirectory the path of the resource directory that the {@code ImageIcon} should be fetched from.
     <p>The path to the file containing the icon is constructed as follows:
     <blockquote>
     {@code String resourceFilePath = resourcesDirectory + "/" + iconFileName;}
     </blockquote>
     This path is then turned into a {@link URL} as follows:
     <blockquote>
     {@code URL url = classLoader.getResource( resourceFilePath );}
     </blockquote>
     The resulting URL is then used to fetch the {@code ImageIcon}.</p>
     @return the requested {@link ImageIcon}.
     <ul>
     <li>the file is assumed to exist in the directory specified by the most recent call to
     {@link #setDefaultResourcesDirectory(String)}.
     </li>
     <li>
     no scaling, brightening, darkening or any other transformations are applied to the
     {@code ImageIcon} before it is returned
     </li>
     </ul>
     @throws IllegalArgumentException if there is no file with the specified name or if the file does not contain a
     valid {@code ImageIcon}.
     */

    @NotNull
    public static ImageIcon fetchMandatoryIcon(
            @NotNull final String iconFileName,
            final int size,
            @NotNull final ClassLoader classLoader,
            final String resourcesDirectory
    ) {

        File iconFile = new File(
                s_resourcesBaseDirectory,
                iconFileName
        );

        Logger.logMsg( "fetching icon from " + iconFile.getAbsolutePath() );

        Optional<ImageIcon> optIcon = fetchCacheableIconImage(
                iconFileName,
                size,
                classLoader,
                resourcesDirectory
        );

        if ( optIcon.isPresent() ) {

            return optIcon.get();

        }

        throw new IllegalArgumentException(
                "ImageIconUtils.fetchIcon:  cannot fetch icon from " +
                                            ObtuseUtil.enquoteJavaObject( iconFile.getAbsolutePath() )
        );

    }

}
