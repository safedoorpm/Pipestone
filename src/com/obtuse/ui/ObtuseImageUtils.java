package com.obtuse.ui;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.exceptions.ObtuseImageLoadFailed;
import com.obtuse.util.DateUtils;
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
 Image manipulation utilities.
 <p>Here's a quite good explanation of the EXIF orientation tag:
 <a href="https://magnushoff.com/jpeg-orientation.html">https://magnushoff.com/jpeg-orientation.html</a></p>
 <p>This looks like it could be really useful - a one .h file and one .cc file basic EXIF parser:
 <a href="https://github.com/mayanklahiri/easyexif">https://github.com/mayanklahiri/easyexif</a></p>
 <p>This one looks truly amazing. It's a Perl EXIF extractor written (owned?) by Phil Harvey and named ExifTool:
 <a href="http://owl.phy.queensu.ca/~phil/exiftool/">http://owl.phy.queensu.ca/~phil/exiftool/</a>
 Not only does ExifTool appear to be very powerful, it provides MacOS, Windows and Unix versions.</p>
 */

public class ObtuseImageUtils {

    private static final JPanel s_mediaTrackerComponent = new JPanel();

    // How to transform an image in one orientation into another . . .

    // Doing a single right rotation on an image in each orientation:

    private static final int[] SINGLE_LEFT_ROTATION = { 8, 5, 6, 7, 4, 1, 2, 3 };

    // Doing a single left rotation on an image in each orientation:

    private static final int[] SINGLE_RIGHT_ROTATION = { 6, 7, 8, 5, 2, 3, 4, 1 };

    // Horizontal flip of an image in each orientation:

    private static final int[] HORIZONTAL_FLIP = { 2, 1, 4, 3, 6, 5, 8, 7 };

    // Vertical flip of an image in each rotation:

    private static final int[] VERTICAL_FLIP = { 4, 3, 2, 1, 8, 7, 6, 5 };

    public static int rotateOrientationRight( int orientation ) {

        int rval = SINGLE_RIGHT_ROTATION[ orientation - 1 ];

        Logger.logMsg( "ObtuseImageUtils.rotateOrientationRight( " + orientation + " ) yielded " + rval );

        return rval;

    }

    public static int rotateOrientationLeft( int orientation ) {

        int rval = SINGLE_LEFT_ROTATION[ orientation - 1 ];

        Logger.logMsg( "ObtuseImageUtils.rotateOrientationLeft( " + orientation + " ) yielded " + rval );

        return rval;

    }

    public static int flipOrientationHorizontally( int orientation ) {

        int rval = HORIZONTAL_FLIP[ orientation - 1 ];

        Logger.logMsg( "ObtuseImageUtils.flipOrientationHorizontally( " + orientation + " ) yielded " + rval );

        return rval;

    }

    public static int flipOrientationVertically( int orientation ) {

        int rval = VERTICAL_FLIP[ orientation - 1 ];

        Logger.logMsg( "ObtuseImageUtils.flipOrientationVertically( " + orientation + " ) yielded " + rval );

        return rval;

    }

    public static Dimension maybeRotateDimension( @NotNull final Dimension d, int orientation ) {

        if ( orientation < 5 ) {

            return d;

        } else {

            //noinspection SuspiciousNameCombination
            return new Dimension( d.height, d.width );

        }

    }

    public static final int ORIENTATION_NORMAL = 1;
    public static final int ORIENTATION_FLIPPED_HORIZONTALLY = 2;
    public static final int ORIENTATION_ROTATED_180 = 3;
    public static final int ORIENTATION_FLIPPED_VERTICALLY = 4;
    public static final int ORIENTATION_RIGHT90_THEN_FLIPPED_HORIZONTALLY = 5;
    public static final int ORIENTATION_LEFT90 = 6;
    public static final int ORIENTATION_LEFT90_THEN_FLIPPED_HORIZONTALLY = 7;
    public static final int ORIENTATION_RIGHT90 = 8;

//    @NotNull
//    public static ImageIcon rotateImage( @NotNull final ImageIcon srcImageIcon, final int orientation ) {
//
//        Image rotatedImage = ObtuseImageUtils.rotateImage( srcImageIcon.getImage(), orientation );
//        ImageIcon rotatedImageIcon = new ImageIcon( rotatedImage );
//
//        return rotatedImageIcon;
//
//    }

    /**
     Rotate an image according to its EXIF-style orientation tag.
     @param srcImage the {@link Image} to be rotated.
     @param orientation the EXIF-style orientation tag (see below).
     @return the possibly rotated {@code Image}.
     If the {@code Image} is already correctly oriented then this could be the original {@code Image}.
     It might not be since this method takes an
     {@link Image} and returns a {@link BufferedImage} which means that the provided {@code Image} might need to
     be converted to a {@code BufferedImage} to satisfy the requirement that this method returns a {@link BufferedImage}.
     <p>
     Assuming that the goal of the exercise is to get a correctly oriented letter F,
     here is what a letter F would need to look like prior to being rotated according to each of
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
     <p>This next table shows an alternative way of thinking about this method where one considers what happens
     if an already correctly oriented letter F is rotated according to each of the orientations:</p>
     <pre>
     1        2       3      4         5            6           7          8<br>
     <br>
     NA       FH      R180    FV      R90 FH      R90         L90 FH      L90<br>
     <br>
     888888   888888      88  88      8888888888  8888888888          88  88<br>
     88           88      88  88      88  88          88  88      88  88  88  88<br>
     8888       8888    8888  8888    88                  88  8888888888  8888888888<br>
     88           88      88  88<br>
     88           88  888888  888888<br>
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

     It might be worth noting that what this method accomplishes can be described by the following pseudo-code:

     <ol>
     <li>if {@code ( ( orientation - 1 ) & 100B ) != 0} then flip the image diagonally</li>
     <li>if {@code ( ( orientation - 1 ) & 010B ) != 0} then rotate the image 180°</li>
     <li>if {@code ( ( orientation - 1 ) & 001B ) != 0} then flip the horizontally</li>
     </ol>
     Notes:
     <ol>
     <li>the three image manipulations applied in the above pseudo-code are not commutative
     (in other words, if you perform the last three statements in a different order then you will
     often get an incorrect result).</li>
     </ol>
     Note: do not confuse "what this method accomplishes" with "the algorithm this method uses".
     <p>See <a href="https://magnushoff.com/jpeg-orientation.html">https://magnushoff.com/jpeg-orientation.html</a>
     for a quite good explanation of what this method accomplishes and what can go wrong if how it
     does its magic isn't quite correct.
     The linked page above is where I found the pseudo-code that appears a little further above.</p>
     */

    public static BufferedImage rotateImage( @NotNull final Image srcImage, final int orientation ) {

        BufferedImage bufferedImage =
                srcImage instanceof BufferedImage
                        ?
                        (BufferedImage)srcImage
                        :
                        convertImageToBufferedImage( srcImage );

        BufferedImage rotated;
        try ( Measure ignored = new Measure( "maybe rotate" ) ) {

            BufferedImage tmpImage;
            switch ( orientation ) {

                case 1:
                    rotated = bufferedImage;
                    break;

                case 2:
                    rotated = flipHorizontally( bufferedImage );
                    break;

                case 3:
                    rotated = rotateDegrees( bufferedImage, 180 );
                    break;

                case 4:
                    rotated = flipVertically( bufferedImage );
                    break;

                case 5:
                    tmpImage = rotateDegrees( bufferedImage, 90 );
                    rotated = flipHorizontally( tmpImage );
                    break;

                case 6:
                    rotated = rotateDegrees( bufferedImage, 90 );
                    break;

                case 7:
                    tmpImage = rotateDegrees( bufferedImage, -90 );
                    rotated = flipHorizontally( tmpImage );
                    break;

                case 8:
                    rotated = rotateDegrees( bufferedImage, -90 );
                    break;

                default:
                    throw new IllegalArgumentException(
                            "LancotFiles.rotatedImage:  invalid orientation value " + orientation
                    );

            }

        }

        return rotated;

    }

    /**
     Rotate an {@link ImageIcon} according to its EXIF-style orientation tag.
     @param srcImageIcon the {@link ImageIcon} to be rotated.
     @param orientation the EXIF-style orientation tag (see below).
     @return the possibly rotated {@code ImageIcon} (if the {@code ImageIcon} is already correctly
     oriented then the return value will be the original {@code ImageIcon}).
     <p>IMPORTANT: While the {@link ImageIcon} class provides at least one way to create an instance
     which contains a {@code null} {@code Image} reference, the commonly used {@link ImageIcon#ImageIcon(Image)}
     constructor throws an {@link NullPointerException} if provided with a {@code null} {@code Image} reference.
     I chose to take this to mean that supporting {@link ImageIcon} instances which contain {@code null}
     {@code Image} references is not worth any serious effort (read "not worth any effort at all").
     In other words, do not be surprised if an exception is thrown if the {@code ImageIcon} passed to this method
     contains a {@code null} {@code Image} reference (you'll probably get a {@link NullPointerException}
     but other exceptions or even no exception at all are possible as the implementation of this class evolves).</p>
     */

    @NotNull
    public static ImageIcon rotateImage( @NotNull final ImageIcon srcImageIcon, final int orientation ) {

        if ( orientation == ORIENTATION_NORMAL ) {

            return srcImageIcon;

        }

        Image rotatedImage = ObtuseImageUtils.rotateImage( srcImageIcon.getImage(), orientation );
        ImageIcon rotatedImageIcon = new ImageIcon( rotatedImage );

        return rotatedImageIcon;

    }

    /**
     * Draw a semi-transparent area.
     * <p>From <a href="https://www.programcreek.com/java-api-examples/?class=java.awt.Graphics2D&method=setComposite">
     *     https://www.programcreek.com/java-api-examples/?class=java.awt.Graphics2D&method=setComposite</a></p>
     * @param g The graphic object
     * @param dragPoint The first point
     * @param beginPoint The second point
     * @param c The color of the area
     */

    public static void drawDragArea(Graphics2D g, Point dragPoint, Point beginPoint, Color c) {

        g.setColor(c);

        Polygon poly = new Polygon();

        poly.addPoint((int) beginPoint.getX(), (int) beginPoint.getY());
        poly.addPoint((int) beginPoint.getX(), (int) dragPoint.getY());
        poly.addPoint((int) dragPoint.getX(), (int) dragPoint.getY());
        poly.addPoint((int) dragPoint.getX(), (int) beginPoint.getY());

        //Set the widths of the shape's outline
        Stroke oldStro = g.getStroke();
        Stroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(stroke);
        g.drawPolygon(poly);
        g.setStroke(oldStro);

        //Set the trasparency of the iside of the rectangle
        Composite oldComp = g.getComposite();
        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        g.setComposite(alphaComp);
        g.fillPolygon(poly);
        g.setComposite(oldComp);

    }

    /**
     Make an image partially or entirely transparent (not much point to the latter).
     @param sourceImage the original/source image.
     @param alpha the alpha value to be applied to every pixel in the image.
     An alpha value is a {@code float} value between 0F (completely transparent) and 1F (completely opaque).
     @return a newly created {@link BufferedImage#TYPE_INT_ARGB} image with the specified alpha value applied.
     Note that a new image is returned even if the call does not actually change the appearance of the original image.
     */

    public static BufferedImage makeTransparent( BufferedImage sourceImage, float alpha ) {

//        BufferedImage sourceImage;
////        if ( original.getType() == BufferedImage.TYPE_INT_ARGB ) {
//
//            Logger.logMsg( "already has alpha channel" );
//
//            sourceImage = original;
//
////        } else {
////
////            Logger.logMsg( "need to convert to have alpha channel" );
////
////            sourceImage = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB );
////            Graphics2D gt = sourceImage.createGraphics();
////            gt.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha) );
////            gt.drawImage(original, 0, 0, original.getWidth(), original.getHeight(), null);
////            gt.dispose();
////
////            return alpha;
////
////        }

        BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = newImage.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.drawImage(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), null);
        g.dispose();

        return newImage;

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

            Optional<BufferedImage> scaledImage = getOptArbitrarilyScaledImage( what, originalImageIcon.getImage(), newSize );

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

    public static Optional<BufferedImage> getOptArbitrarilyScaledImage(
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

        Image img = waitToFinishLoading(
                false,
                "getScaledImage",
                "getScaledImage( " + what + " )",
                scaledInstance
        );

//        try {

            Optional<BufferedImage> optBufferedImage = optConvertImageToBufferedImage( img );

            return optBufferedImage;

//            if ( optBufferedImage.isPresent() ) {
//
//                return optBufferedImage;
//
//            }
//
//

//        } catch ( RuntimeException e ) {
//
//            // Inspect the image. Why can't we turn it into a BufferedImage?
//
//            throw e;
//
//        }

    }

    public static Optional<ImageIcon> getOptArbitrarilyScaledImageIcon(
            @Nullable final String what,
            @NotNull ImageIcon originalImageIcon,
            @NotNull Dimension newSize
    ) {

        Optional<BufferedImage> scaledImage = ObtuseImageUtils.getOptArbitrarilyScaledImage(
                what,
                originalImageIcon.getImage(),
                newSize
        );

        ImageIcon rval = scaledImage.map( ImageIcon::new )
                                    .orElse( null );

        return Optional.ofNullable( rval );

    }

    public static Optional<ImageIcon> getScaledImageIcon(
            @Nullable final String what,
            @NotNull ImageIcon originalImageIcon,
            int newSize
    ) {

        Optional<BufferedImage> scaledImage = ObtuseImageUtils.getOptScaledImage(
                what,
                originalImageIcon.getImage(),
                newSize
//                ObtuseImageUtils.getMinimumScalingFactor(
//                        newSize,
//                        newSize,
//                        originalImageIcon.getIconWidth(),
//                        originalImageIcon.getIconHeight()
//                )
        );

        ImageIcon rval = scaledImage.map( ImageIcon::new )
                                    .orElse( null );

        return Optional.ofNullable( rval );

    }

    public static Optional<BufferedImage> getOptScaledImage(
            @Nullable final String what,
            @NotNull final Image originalImage,
            int targetSize
    ) {

        BufferedImage convertedOriginalImage = ObtuseImageUtils.convertImageToBufferedImage( originalImage );

        Optional<BufferedImage> scaledImage = ObtuseImageUtils.getOptArbitrarilyScaledImage(
                what,
                convertedOriginalImage,
                ObtuseImageUtils.getMinimumScalingFactor(
                        targetSize,
                        targetSize,
                        convertedOriginalImage.getWidth(),
                        convertedOriginalImage.getHeight()
                )
        );

        return scaledImage;

//        ImageIcon rval = scaledImage.map( ImageIcon::new )
//                                    .orElse( null );
//
//        return Optional.ofNullable( rval );
//        try ( Measure ignored = new Measure( "getOptScaledImage( " + ObtuseUtil.enquoteToJavaString( what ) + " )" ) ) {
//
//            BufferedImage convertedOriginalImage = ObtuseImageUtils.convertImageToBufferedImage( originalImage );
//
//            BufferedImage scaled;
//            if ( convertedOriginalImage.getHeight() > targetSize || convertedOriginalImage.getWidth() > targetSize ) {
//
//                Dimension newSize = ObtuseImageUtils.getMinimumScalingFactor(
//                        targetSize,
//                        targetSize,
//                        convertedOriginalImage.getWidth(),
//                        convertedOriginalImage.getHeight()
//                );
//                Optional<Image> optTmpImage = ObtuseImageUtils.getScaledImage( "thing", convertedOriginalImage, newSize );
//
//                scaled = optTmpImage.map( ObtuseImageUtils::convertImageToBufferedImage )
//                                    .orElse( null );
//
//            } else {
//
//                scaled = convertedOriginalImage;
//
//            }
//
//            return Optional.ofNullable( scaled );
//
//        }

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
     @param verbose should status messages be printed.
     @param what what sort of operation we are waiting for (scaling, rotating, etc).
     This is used to name {@link Measure} events. Care should be taken to use the same
     {@code what} string for requests that are to be put into the same {@code Measure} bucket.
     @param fullWhat a possibly longer {@code what} variant.
     @param img the image.
     */

    public static Image waitToFinishLoading(
            boolean verbose,
            final String what,
            final String fullWhat,
            @NotNull final Image img
    ) {

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

                long startTime = System.currentTimeMillis();
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

                long endTime = System.currentTimeMillis();
                if ( endTime - startTime > 1000L ) {

                    Logger.logMsg( "wait took " + DateUtils.formatDuration( endTime - startTime ) + " for " + fullWhat );

                    ObtuseUtil.doNothing();

                }

                if ( verbose ) {

                    Logger.logMsg( "ObtuseImageUtils:  checkStatus says " + mediaTracker.checkAll() + " for " + fullWhat );

                }

                if ( errored || aborted || complete ) {

                    break;

                }

                ObtuseUtil.safeSleepMillis( 1 );

            }

            if ( verbose ) {

                Logger.logMsg(
                        "ObtuseImageUtils:  " +
                        "spinCount=" + spinCount + ", " +
                        "errored=" + errored + ", " +
                        "aborted=" + aborted + ", " +
                        "complete=" + complete + ", " +
                        "loading=" + loading +
                        " for " + what
                );

            }

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

        @NotNull Optional<BufferedImage> rval = optConvertImageToBufferedImage( inputImage );
        if ( rval.isPresent() ) {

            return rval.get();

        } else {

            throw new IllegalArgumentException(
                    "ObtuseImageUtils.convertImageToBufferedImage:  " +
                    "unable to convert " + ObtuseUtil.enquoteJavaObject( inputImage ) + " to a buffered image"
            );

        }

    }

    /**
     Try to convert an {@link Image} into a {@link BufferedImage}.
     @param inputImage the {@link Image} to be converted.
     @return an {@link Optional}{@code <BufferedImage>} containing the image if the conversion worked;
     an {@code Optional.empty()} otherwise.
     <p>For reasons which are not at all clear, some images cannot be converted into {@code BufferedImage} instances
     in any obvious way. See the catching of a {@link RuntimeException} below for a tiny bit more elucidation.</p>
     */

    @NotNull
    public static Optional<BufferedImage> optConvertImageToBufferedImage( Image inputImage ) {

        if ( inputImage instanceof BufferedImage ) {

            return Optional.of( (BufferedImage)inputImage );

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

            return Optional.empty();

        }

        g.dispose();

        return Optional.of( newImage );

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
