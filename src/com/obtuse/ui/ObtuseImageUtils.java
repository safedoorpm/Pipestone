package com.obtuse.ui;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.exceptions.ObtuseImageLoadFailed;
import com.obtuse.util.Logger;
import com.obtuse.util.Measure;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 Created by danny on 2018/09/04.
 */

public class ObtuseImageUtils {

    private static final JPanel s_mediaTrackerComponent = new JPanel();

    /**
     Rotate an image according to its EXIF-style orientation tag.
     @param srcImage the image to be rotated.
     @param orientation the EXIF-style orientation tag (see below).
     @return the possibly rotated image (if the image is already correctly
     oriented then this will be the original image).
     <p>
     Here's what a letter F would look like prior to being rotated according to
     the eight possible orientations:</p>
     <pre>
     1        2       3      4         5            6           7          8<br>
     <br>
     888888  888888      88  88      8888888888  88                  88  8888888888<br>
     88          88      88  88      88  88      88  88          88  88      88  88<br>
     8888      8888    8888  8888    88          8888888888  8888888888          88<br>
     88          88      88  88<br>
     88          88  888888  888888<br>
     </pre>
     <p>This guides the next section of software. For example, an image with orientation 3
     needs to be rotated 180 degrees to be correct for the viewer. Similarly, an image
     with orientation 7 needs to be either rotated left 90 degrees* and then flipped
     horizontally OR flipped horizontally and then rotated right 90 degrees.</p>

     <blockquote>1 - no action required.<br>
     2 - flip horizontally.<br>
     3 - rotate 180 degrees.<br>
     4 - flip vertically.<br>
     5 - rotate right 90 degrees and then flip horizontally (amongst other possibilities).<br>
     6 - rotate right 90 degrees.<br>
     7 - rotate left 90 degrees and then flip horizontally (amongst other possibilities).<br>
     8 - rotate left 90 degrees.</blockquote>

     <p>* a left rotation could also be called a counterclockwise rotation. Similarly, a right
     rotation could also be called a clockwise rotation.</p>
     */

    public static BufferedImage rotateImage( BufferedImage srcImage, int orientation ) {

        if ( orientation != 1 ) {

            Logger.logMsg( "LancotMediaUtils.rotateImage: this one requires rotation" );

            ObtuseUtil.doNothing();

        }

        BufferedImage rotated;
        try ( Measure ignored = new Measure( "maybe rotate" ) ) {

            // Here's what a letter F would look like prior to being rotated according to
            // the eight possible orientations:
            //
            //    1        2       3      4         5            6           7          8
            //
            //  888888  888888      88  88      8888888888  88                  88  8888888888
            //  88          88      88  88      88  88      88  88          88  88      88  88
            //  8888      8888    8888  8888    88          8888888888  8888888888          88
            //  88          88      88  88
            //  88          88  888888  888888
            //
            // This guides the next section of software. For example, an image with orientation 3
            // needs to be rotated 180 degrees to be correct for the viewer. Similarly, an image
            // with orientation 7 needs to be either rotated left 90 degrees* and then flipped
            // horizontally OR flipped horizontally and then rotated right 90 degrees.
            //
            // 1 - no action required.
            // 2 - flip horizontally.
            // 3 - rotate 180 degrees.
            // 4 - flip vertically.
            // 5 - rotate right 90 degrees and then flip horizontally (amongst other possibilities).
            // 6 - rotate right 90 degrees.
            // 7 - rotate left 90 degrees and then flip horizontally (amongst other possibilities).
            // 8 - rotate left 90 degrees.
            //
            // * a left rotation could also be called a counterclockwise rotation. Similarly, a right
            //   rotation could also be called a clockwise rotation.

            BufferedImage tmpImage;
            switch ( orientation ) {

                case 1:
                    rotated = srcImage;
                    break;

                case 2:
                    rotated = flipHorizontally( srcImage );
                    break;

                case 3:
                    rotated = rotateDegrees( srcImage, 180 /*, new BufferedImage( srcImage.getWidth(), srcImage.getHeight(), srcImage.getType() )*/ );
                    break;

                case 4:
                    rotated = flipVertically( srcImage );
                    break;

                case 5:
                    tmpImage = rotateDegrees( srcImage, 90 );
                    rotated = flipHorizontally( tmpImage );
                    break;

                case 6:
                    rotated = rotateDegrees( srcImage, 90 );
                    break;

                case 7:
                    tmpImage = rotateDegrees( srcImage, -90 );
                    rotated = flipHorizontally( tmpImage );
                    break;

                case 8:
                    rotated = rotateDegrees( srcImage, -90 );
                    break;

                default:
                    throw new IllegalArgumentException(
                            "LancotFiles.rotatedImage:  invalid orientation value " + orientation
                    );

            }

        }

        return rotated;

    }
    @SuppressWarnings("unused")
    public static class MyImageObserver implements ImageObserver {

        private boolean _errored = false;
        private boolean _aborted = false;
        private boolean _complete = false;
        private boolean _isDone = false;

        private boolean _gotWidth = false;
        private boolean _gotHeight = false;
        private boolean _gotAllBits = false;
        private boolean _gotProperties = false;

        @Override
        public boolean imageUpdate(
                final Image img,
                final int infoflags,
                final int x,
                final int y,
                final int width,
                final int height
        ) {

            SortedSet<String> flags = interpretImageUpdateFlags( infoflags );

            _errored = _errored || ( ( infoflags & ERROR ) != 0 );
            _aborted = _aborted || ( ( infoflags & ABORT ) != 0 );
            _gotWidth = _gotWidth || ( ( infoflags & WIDTH ) != 0 );
            _gotHeight = _gotHeight || ( ( infoflags & HEIGHT ) != 0 );
            _gotAllBits = _gotAllBits || ( ( infoflags & ALLBITS ) != 0 );
            _gotProperties = _gotProperties || ( ( infoflags & PROPERTIES ) != 0 );
            _complete = _complete || ( _gotWidth && _gotHeight && _gotAllBits && _gotProperties );
            _isDone = hasCompleted() || hasFailed();

            Logger.logMsg( "imageUpdate( img=?, x=" +
                           x +
                           ", y=" +
                           y +
                           ", width=" +
                           width +
                           ", height=" +
                           height +
                           ", infoflags=" +
                           flags +
                           " ), this=" + this
            );

            if ( ( infoflags & ERROR ) != 0 || ( infoflags & ABORT ) != 0 ) {

                ObtuseUtil.doNothing();

            }

            return !hasCompleted();

        }

        public boolean isDone() {

            return _isDone;

        }

        public boolean hasErrored() {

            return _errored;

        }

        public boolean hasAborted() {

            return _aborted;

        }

        public boolean hasCompleted() {

            return _complete;

        }

        public boolean hasFailed() {

            return _errored || _aborted;

        }

        public boolean hasWorked() {

            return isDone() && !hasFailed();

        }

        public String toString() {

            return "MyMediaObserver( errored=" + _errored + ", aborted=" + _aborted + ", gw=" + _gotWidth + ", gh=" + _gotHeight + ", gAB=" + _gotAllBits + ", gP=" + _gotProperties + ", complete=" + _complete + ", done=" + _isDone + " )";

        }

    }

    @NotNull
    public static Optional<ImageIcon> maybeRegenerateThumbnail(
            @Nullable final String what,
            final @NotNull ImageIcon originalImageIcon,
            @SuppressWarnings("SameParameterValue") @Nullable final ImageIcon scaledImageIcon,
            int thumbnailSize
    ) {

        int origW = originalImageIcon.getIconWidth();
        int origH = originalImageIcon.getIconHeight();

        // Just return the original if the thumbnail would be the same size or bigger than the original.

        if ( thumbnailSize >= origW && thumbnailSize >= origH ) {

            return Optional.of( originalImageIcon );

        }

        Dimension newSize = getMinimumScalingFactor( thumbnailSize, thumbnailSize, origW, origH );

        if (
                scaledImageIcon == null ||
                newSize.width != scaledImageIcon.getIconWidth() ||
                newSize.height != scaledImageIcon.getIconHeight()
        ) {

            if ( what != null ) Logger.logMsg(
                    ( scaledImageIcon == null ? "" : "re" ) + "scaling " + what +
                    " (" + origW + 'x' + origH + ") to" +
                    " (" + newSize.width + 'x' + newSize.height + ")"
            );

            Optional<Image> scaledImage = getScaledImage( what, originalImageIcon.getImage(), newSize );

            return scaledImage.map( ImageIcon::new );

        } else {

            return Optional.of( scaledImageIcon );

        }

    }

    /**
     Scale an image.
     <p>This method scales an image and waits for the scaling to finish before returning.
     </p>
     @param what an optional description of the image being scaled.
     This is used to generate a log message if {@link Image#getScaledInstance(int, int, int)}
     returns a {@code null} value (JavaDocs for that method are not clear about whether the method can return
     {@code null}) or if the wait to finish scaling the image is interrupted.
     No log message is produced if {@code what} is {@code null}.
     @param originalImage the original image.
     @param newSize the desired size of the new image.
     Depending on the relationship of {@code newSize} to the actual size of the original image,
     the result of this scaling operation might yield an image that has a different aspect ratio
     than the original image.
     @return the scaled image wrapped in an {@link Optional} if the scaling operation works.
     An empty {@code Optional} if it fails.
     <p>Failures can occur if the wait for the scaled image to finish loading is interrupted.
     The JavaDocs for {@link Image#getScaledInstance(int, int, int)} do not make it clear if
     that method can return a {@code null} result. If it does then this method will return
     an empty {@code Optional} instance.</p>
     */

    public static Optional<Image> getScaledImage(
            @Nullable final String what,
            @NotNull final Image originalImage,
            @NotNull final Dimension newSize
    ) {

        Image scaledInstance = originalImage.getScaledInstance(
                newSize.width,
                newSize.height,
                Image.SCALE_SMOOTH
        );

        if ( scaledInstance == null ) {

            if ( what != null ) {

                Logger.logErr(
                        "ObtuseImageUtils.getScaledImage:  " +
                        "Image.getScaledInstance returned a null result scaling " + what
                );

            }

            return Optional.empty();

        }

        Image img = waitToFinishLoading( "getScaledImage", scaledInstance );

        return Optional.of( img );

    }

    public static Optional<BufferedImage> getOptScaledImage(
            @Nullable final String what,
            @NotNull final Image originalImage,
            int targetSize
    ) {

        try ( Measure ignored = new Measure( "getOptScaledImage( " + ObtuseUtil.enquoteToJavaString( what ) + " )" ) ) {

            BufferedImage convertedOriginalImage = ObtuseImageUtils.convertImageToBufferedImage( originalImage );

            BufferedImage scaled;
            if ( convertedOriginalImage.getHeight() > targetSize || convertedOriginalImage.getWidth() > targetSize ) {

                Dimension newSize = ObtuseImageUtils.getMinimumScalingFactor(
                        targetSize,
                        targetSize,
                        convertedOriginalImage.getWidth(),
                        convertedOriginalImage.getHeight()
                );
                Optional<Image> optTmpImage = ObtuseImageUtils.getScaledImage( "thing", convertedOriginalImage, newSize );

                scaled = optTmpImage.map( ObtuseImageUtils::convertImageToBufferedImage )
                                    .orElse( null );

            } else {

                scaled = convertedOriginalImage;

            }

            return Optional.ofNullable( scaled );

        }

    }

    @NotNull
    public static SortedSet<String> interpretImageUpdateFlags( final int infoflags ) {

        SortedSet<String> flags = new TreeSet<>();
        if ( ( infoflags & ImageObserver.WIDTH ) != 0 ) flags.add( "WIDTH" );
        if ( ( infoflags & ImageObserver.HEIGHT ) != 0 ) flags.add( "HEIGHT" );
        if ( ( infoflags & ImageObserver.PROPERTIES ) != 0 ) flags.add( "PROPERTIES" );
        if ( ( infoflags & ImageObserver.SOMEBITS ) != 0 ) flags.add( "SOMEBITS" );
        if ( ( infoflags & ImageObserver.FRAMEBITS ) != 0 ) flags.add( "FRAMEBITS" );
        if ( ( infoflags & ImageObserver.ALLBITS ) != 0 ) flags.add( "ALLBITS" );
        if ( ( infoflags & ImageObserver.ERROR ) != 0 ) flags.add( "ERROR" );
        if ( ( infoflags & ImageObserver.ABORT ) != 0 ) flags.add( "ABORT" );

        return flags;

    }

    /**
     Wait for an image to finish asynchronous loading.
     @return the image (same value as {@code img}).
     @param what what sort of operation we are waiting for (scaling, rotating, etc).
     @param img the image.
     */

    public static Image waitToFinishLoading( final String what, @NotNull final Image img ) {

        long callStart = System.currentTimeMillis();

        try (
                Measure entireWait =
                        new Measure(
                                "waitToFinishLoading( " + ObtuseUtil.enquoteToJavaString( what ) + " )"
                        )
        ) {

            MediaTracker mediaTracker = new MediaTracker( s_mediaTrackerComponent );
            mediaTracker.addImage(
                    img,
                    12321
            );

            boolean load = true;
            boolean errored = false;
            boolean aborted = false;
            boolean loading = false;
            boolean complete = false;
            int spinCount = 0;

            while ( true ) {

                try ( Measure ignored = new Measure( "waitToFinishLoading-spin" ) ) {
                    spinCount += 1;

                    int status = mediaTracker.statusAll( load );
                    load = false;
                    if ( ( status & MediaTracker.ERRORED ) != 0 ) {

                        errored = true;

                    }

                    if ( ( status & MediaTracker.ABORTED ) != 0 ) {

                        aborted = true;

                    }

                    if ( ( status & MediaTracker.LOADING ) != 0 ) {

                        loading = true;

                    }

                    if ( ( status & MediaTracker.COMPLETE ) != 0 ) {

                        complete = true;

                    }

                }

                Logger.logMsg( "ObtuseImageUtils:  checkStatus says " + mediaTracker.checkAll() );
                if ( errored || aborted || complete ) {

                    break;

                }

                ObtuseUtil.safeSleepMillis( 1 );

            }

            Logger.logMsg( "ObtuseImageUtils:  spinCount=" + spinCount + ", errored=" + errored + ", aborted=" + aborted + ", complete=" + complete + ", loading=" + loading );

            return img;

        }

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

                String msg = "orig " + origW + 'x' + origH + " yielded width-based sf " + sf1 + " which yields scaled " + rval.width + 'x' +
                             rval.height + " which is not exact for either width or height";
                throw new HowDidWeGetHereError( msg );

            }

        } else {

            rval = new Dimension( (int)( origW * sf2 ), boxH );
            if ( rval.width != boxW && rval.height != boxH ) {

                String msg =
                        "orig " + origW + 'x' + origH + " yielded height-based sf " + sf2 + " which yields scaled " + rval.width + 'x' +
                        rval.height + " which is not exact for either width or height";
                throw new HowDidWeGetHereError( msg );

            }

        }

        return rval;

    }

    @SuppressWarnings("unused")
    @NotNull
    public static BufferedImage loadImage( final @NotNull File imageLocation, boolean verbose )
            throws IOException, ObtuseImageLoadFailed {

        if ( verbose ) Logger.logMsg( "loading primary image from " + imageLocation );

        try ( Measure ignored = new Measure( "OIU.loadImage" ) ) {

            BufferedImage image = ImageIO.read( imageLocation );

            if ( image == null ) {

                throw new ObtuseImageLoadFailed( "ObtuseImageFile.loadPrimaryImage:  image decoder returned null (no idea why - sorry)" );

            }

            return image;

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

    @NotNull
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
        try {

            g.drawImage( inputImage, 0, 0, null );

        } catch ( RuntimeException e ) {

            Logger.logErr( "java.lang.Exception caught", e );
            ObtuseUtil.doNothing();

        }

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
        int inputImageWidth = bufferedInputImage.getWidth();
        int inputImageHeight = bufferedInputImage.getHeight();
        BufferedImage newImage = new BufferedImage(
                inputImageWidth,
                inputImageHeight,
                bufferedInputImage.getType()
        );
        Graphics2D gg = newImage.createGraphics();
        boolean allDone = gg.drawImage(
                bufferedInputImage,
                inputImageWidth,
                0,
                -inputImageWidth,
                inputImageHeight,
                null
        );
        Logger.logMsg( "flipHorizontally:  drawImage said " + allDone );
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
        int inputImageWidth = bufferedInputImage.getWidth();
        int inputImageHeight = bufferedInputImage.getHeight();
        BufferedImage newImage = new BufferedImage(
                inputImageWidth,
                inputImageHeight,
                bufferedInputImage.getType()
        );
        Graphics2D gg = newImage.createGraphics();
        boolean allDone = gg.drawImage(
                bufferedInputImage,
                0,
                inputImageHeight,
                inputImageWidth,
                -inputImageHeight,
                null
        );
        Logger.logMsg( "flipVertically:  drawImage said " + allDone );
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
        int w = bufferedInputImage.getWidth();
        int h = bufferedInputImage.getHeight();
        int neww = (int)Math.floor( w * cos + h * sin );
        int newh = (int)Math.floor( h * cos + w * sin );

        @SuppressWarnings("unused") GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = new BufferedImage( neww, newh, bufferedInputImage.getType() );
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
