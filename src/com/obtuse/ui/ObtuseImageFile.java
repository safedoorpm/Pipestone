/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.exceptions.ObtuseImageLoadFailed;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.Measure;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnPackedEntityGroup;
import com.obtuse.util.gowing.p2a.StdGowingPacker;
import com.obtuse.util.gowing.p2a.StdGowingUnPacker;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 Manage a user-provided image.
 */

public class ObtuseImageFile extends GowingAbstractPackableEntity {

//    static {
//
//        Measure.setGloballyEnabled( true );
//
//    }

    public static final String OBTUSE_IMAGE_INFO_FILENAME_SUFFIX = ".binfo";

    public static final String GENERATED_IMAGE_FILE_PREFIX = "image_";

    public static final int GENERATED_IMAGE_FILE_SN_LENGTH = 6;

    public static final Pattern GENERATED_IMAGE_INFO_FILENAME_PATTERN;

    static {

        GENERATED_IMAGE_INFO_FILENAME_PATTERN = Pattern.compile(
                GENERATED_IMAGE_FILE_PREFIX + '(' +
                ObtuseUtil.replicate( "\\d", GENERATED_IMAGE_FILE_SN_LENGTH ) +
                ')' + OBTUSE_IMAGE_INFO_FILENAME_SUFFIX
        );

        ObtuseUtil.doNothing();

    }

    @SuppressWarnings("unused")
    public static class ObtuseImageFileInstanceCreationFailed extends Exception {

        public ObtuseImageFileInstanceCreationFailed() {

            super();
        }

        public ObtuseImageFileInstanceCreationFailed(final String message ) {

            super( message );
        }

        public ObtuseImageFileInstanceCreationFailed(final String message, final Throwable cause ) {

            super( message, cause );
        }

        public ObtuseImageFileInstanceCreationFailed(final Throwable cause ) {

            super( cause );
        }

    }

    private static boolean s_loadLoggingEnabled = true;

    public static final int THUMBNAIL_EDGE_LENGTH = 128;

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( ObtuseImageFile.class );

    private static final int VERSION = 1;

    private static File s_imageRepositoryFile;

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;
        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;
        }

        @NotNull
        @Override
        public GowingPackable createEntity(
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        ) throws GowingUnpackingException {

            return new ObtuseImageFile( unPacker, bundle );

        }

    };

    private static final GowingTypeIndex s_gowingTypeIndex;

    static {

        s_gowingTypeIndex = new GowingTypeIndex( "obtuse type index" );
        Optional<EntityTypeInfo> rval = s_gowingTypeIndex.findTypeInfo( ObtuseImageFile.FACTORY.getTypeName() );
        if ( rval.isEmpty() ) {

            s_gowingTypeIndex.addFactory( ObtuseImageFile.FACTORY );

        }

    }

    public enum ImageState {

        /**
         Something really bad has happened while copying and vetting the image - it is beyond repair.
         */

        BROKEN,

        /**
         The constructor has just barely gotten started.
         */

        UNINITIALIZED,

        /**
         A serial number has been selected.
         */

        SERIAL_NUMBER_SET,

        /**
         This instance's info file has been written to disk for the first time.
         */

        INFO_FILE_CREATED,

        /**
         The image is in the cache with a temporary ".timage" suffix.
         */

        IN_CACHE_TEMP,

        /**
         The image is in the cache at its permanent location and is completely ready to use.
         */

        READY

    }

    @NotNull
    ImageState _imageState = ImageState.UNINITIALIZED;

    private static final EntityName IMAGE_STATE_NAME = new EntityName( "_is" );

    /**
     Where the image came from (assuming that we know).
     <p/>There are three cases of interest:
     <ol>
     <li>the image's location on a locally accessible filesystem was somehow provided to us
     (probably the result of a drag-and-drop or a copy-and-paste from the OS's "Finder",
     or via a file selection dialog box launched by ourselves).</li>
     <li>the image's location was provided to us via a network accessible URL.</li>
     <li>the image was provided to us as an in-memory resource
     (probably the result of a drag-and-drop or a copy-and-paste of an image
     from some other application running on our local box).</li>
     </ol>
     The first two alternatives are actually equivalent in the sense that we
     have immediate access to the image and we know where we got it from.
     The third alternative isn't quite as good in that we have immediate
     access to the image but don't really know its provenance.
     Fortunately, while generally useful in a genealogy application,
     the immediate source of the image isn't exactly mission critical knowledge.
     <p/>The {@code _originalURL} attribute covers the first two cases.
     It will be {@code null} for the third case.
     */

    private final URL _originalURL;
    private static final EntityName ORIGINAL_URI_GTAG = new EntityName( "_oURI" );

    private final URI _originalURI;
    private static final EntityName ORIGINAL_URL_GTAG = new EntityName( "_oURL" );

    private final File _originalFile;
    private static final EntityName ORIGINAL_FILE_GTAG = new EntityName( "_oFile" );

    /**
     The format of the original image.
     <p/>This is obtained from the original image file's suffix if we know it.
     Otherwise, we may have to wait until we load the image during the image caching process.
     Alternatively, if we are initialized with an ImageIcon then we will never know the original image's format.
     <p/>This probably means that we should not care.
     */

    private String _originalImageFormat;
    private static final EntityName ORIGINAL_IMAGE_FORMAT_GTAG = new EntityName( "_oFormat" );

    /**
     The format of the cached copy of the image.
     This will currently always be the same as the original image's format except when we are
     initialized with an ImageIcon (in which case, we don't know the original image's format).
     In that event, we currently cache the image as a png.
     */

    private String _cachedImageFormat;
    private static final EntityName CACHED_IMAGE_FORMAT_GTAG = new EntityName( "_cFormat" );

    private int _cachedImageWidth = -1;
    private static final EntityName CACHED_IMAGE_WIDTH_GTAG = new EntityName( "_ciw" );

    private int _cachedImageHeight = -1;
    private static final EntityName CACHED_IMAGE_HEIGHT_GTAG = new EntityName( "_cih" );

    /*
    %%% this is no longer correct.

    Where we have decided to cache the image's info file (the serialization of this instance).
    <p/>The first time that we are asked to save ourselves to disk, this attribute will be {@code null}.
    Consequently, we pick a place for our info file and then serialize ourselves to that location.
    Our info file will have a suffix as specified by {@link OBTUSE_IMAGE_INFO_FILENAME_SUFFIX}.
    We then convert our info file name into our cached image file's name by replacing the suffix
    of our info file with whatever suffix is appropriate for the format of our image file
    (if the image arrived at our door as an in-memory image then we'll
    save it as a ".jpg").
    <p/>
    VERY IMPORTANT: we create our info file first using a mechanism which ensures that we do not
    accidentally overwrite any previously existing info file. This prior existence of our info file
    on disk when we write our image file to disk and the mechanism that we use to name our image
    file together ensure that we cannot accidentally overwrite some other image file.
    <p/>This isn't strictly quite true as there could be an image file in our image repository
    that has somehow lost its info file.
    We protect against this possibility by not picking an image file name which can be 'transformed'
    into an existant file name in our image repository by the replacement of the existant file's
    suffix with our standard info file suffix.
     */

    /**
     The basename of where we have decided to cache our info file, our temporary image file
     and our permanent image file. This is here for diagnostic and debugging purposes.
     Do not use this for other purposes.
     */

    private final File _diagnosticCachedFilesBasename;

    private static final EntityName CACHED_IMAGE_FILE_BASENAME_GTAG = new EntityName( "_cifb" );

    /**
     Our serial number.
     <p/>Used to derive any and all filenames associated with our image.
     */

    private int _ourSerialNumber;
    private static final EntityName OUR_SERIAL_NUMBER_NAME = new EntityName( "_osn" );

    private String _imageFileMD5;
    private static final EntityName IMAGE_MD5_GTAG = new EntityName( "_ciifl" );

    private long _cachedImageFileSize;
    private static final EntityName CACHED_IMAGE_FILE_SIZE_GTAG = new EntityName( "_cifs" );

    private String _title;
    private static final EntityName TITLE_GTAG = new EntityName( "_t" );

    private byte[] _thumbnailImageBytes;
    private static final EntityName THUMBNAIL_IMAGE_BYTES_NAME = new EntityName( "_tIb" );

    private static final TreeMap<String, String> s_supportedImageFormatSuffixes;

    static {

        // Make sure that we're ready to rock and roll.

        s_supportedImageFormatSuffixes = new TreeMap<>();
        s_supportedImageFormatSuffixes.put( "jpg", "jpg" );
        s_supportedImageFormatSuffixes.put( "jpeg", "jpg" );
        s_supportedImageFormatSuffixes.put( "gif", "gif" );
        s_supportedImageFormatSuffixes.put( "png", "png" );
        s_supportedImageFormatSuffixes.put( "tif", "tif" );
        s_supportedImageFormatSuffixes.put( "tiff", "tif" );
        s_supportedImageFormatSuffixes.put( "heic", "heic" );

    }

    public ObtuseImageFile( final @NotNull File f )
            throws ObtuseImageFileInstanceCreationFailed {

        this( "ObtuseImageFile( File )", null, f, null );

    }

    public ObtuseImageFile( final @NotNull ImageIcon imageIcon )
            throws ObtuseImageFileInstanceCreationFailed {

        this( "ObtuseImageFile( ImageIcon )", null, null, imageIcon );

    }

    public ObtuseImageFile( final @NotNull URL originalURL )
            throws ObtuseImageFileInstanceCreationFailed {

        this( "ObtuseImageFile( URL )", originalURL, null, null );

    }

    private ObtuseImageFile(
            final String who,
            final URL originalURL,
            final File originalFile,
            final ImageIcon imageIcon
    ) throws ObtuseImageFileInstanceCreationFailed {

        super( new GowingNameMarkerThing() );

        try {

            int setCount = 0;
            setCount += ( originalURL == null ? 0 : 1 );
            setCount += ( originalFile == null ? 0 : 1 );
            setCount += ( imageIcon == null ? 0 : 1 );
            if ( setCount != 1 ) {

                throw new IllegalArgumentException(
                        who + ":  exactly one of originalURL " +
                        "(" + originalURL + "), " +
                        "originalFile( " + originalFile + "), " +
                        "or imageIcon(" + ( imageIcon == null ? "null" : "not null" ) + ") " +
                        "must be non-null"
                );

            }

            if ( originalURL != null ) {

                _originalURI = originalURL.toURI();
                _originalURL = originalURL;
                _originalFile = null;

                _title = originalURL.getPath();

                createImageInfoFile( null );

            } else if ( originalFile != null ) {

                _originalFile = originalFile;
                _originalURI = originalFile.toURI();
                _originalURL = _originalURI.toURL();

                _title = originalFile.getName();

                createImageInfoFile( null );

            } else //noinspection ConstantConditions
                if ( imageIcon != null ) {

                _originalURI = null;
                _originalURL = null;
                _originalFile = null;

                _title = "ImageIcon( " + imageIcon.getIconWidth() + 'x' + imageIcon.getIconHeight() + " )";

                createImageInfoFile( imageIcon.getImage() );

            } else {

                    throw new HowDidWeGetHereError(
                        who + ":  cannot get here from there (" +
                        "oURL=" + originalURL + ", " +
                        "oFile=" + originalFile + ", " +
                        "imageIcon=" + imageIcon + ")"
                );

            }

            if ( verifyImageIsReady() ) {

                _diagnosticCachedFilesBasename = constructCachedImageRepositoryBasename( _ourSerialNumber );

            } else {

                _diagnosticCachedFilesBasename = null;

            }

        } catch ( URISyntaxException | IOException e ) {

            throw new ObtuseImageFileInstanceCreationFailed(
                    who + " initialization failed (using original URL=" + originalURL + "), " +
                    "original File( " + originalFile + "), " +
                    "or imageIcon(" + imageIcon + ") ",
                    e
            );

        }

    }

    public static synchronized void setImageRepositoryFile(
            final File imageRepositoryFile,
            final boolean createIfNecessary
    ) {

        if ( s_imageRepositoryFile == null ) {

            s_imageRepositoryFile = imageRepositoryFile;

        } else {

            throw new IllegalArgumentException(
                    "ObtuseImageFile.setImageRepositoryFile:  image repository file may only be set once"
            );

        }

        if ( imageRepositoryFile.isDirectory() ) {

            //noinspection UnnecessaryReturnStatement
            return;

        } else if ( imageRepositoryFile.exists() ) {

            throw new IllegalArgumentException(
                    "ObtuseImageFile.setImageRepositoryFile:  " +
                    "image repository " + ObtuseUtil.enquoteJavaObject( imageRepositoryFile ) +
                    " exists but is not a directory"
            );

        } else if ( createIfNecessary ) {

            Logger.logMsg(
                    "ObtuseImageFile.setImageRepositoryFile:  " +
                    "attempting to create image repository at/in " +
                    ObtuseUtil.enquoteJavaObject( imageRepositoryFile )
            );

            if ( !imageRepositoryFile.mkdirs() ) {

                throw new IllegalArgumentException(
                        "ObtuseImageFile.setImageRepositoryFile:  " +
                        "unable to create image repository " + ObtuseUtil.enquoteJavaObject( imageRepositoryFile )
                );

            }

            Logger.logMsg(
                    "ObtuseImageFile.setImageRepositoryFile:  " +
                    "image repository " + ObtuseUtil.enquoteJavaObject( imageRepositoryFile ) + " created"
            );

        }

    }

    public static synchronized boolean isImageRepositoryFileSet() {

        return s_imageRepositoryFile != null;

    }

    public static synchronized File getImageRepositoryFile() {

        if ( isImageRepositoryFileSet() ) {

            return s_imageRepositoryFile;

        } else {

            throw new HowDidWeGetHereError(
                    "ObtuseImageFile.getImageRepositoryFile:  " +
                    "request for image repository file before setImageRepositoryFile has been called"
            );

        }

    }

    public static void setLoadLogging( final boolean loadLogging ) {

        s_loadLoggingEnabled = loadLogging;

    }

    @SuppressWarnings("unused")
    public static boolean isLoadLoggingEnabled() {

        return s_loadLoggingEnabled;

    }

    /**
     Get the size of the image (as specified by the binfo file).

     @return size of the image (could be width and height of -1 if size is not known;
     load the cached image to for these values to be set although keep in mind that
     loading the cached image is fairly expensive).
     */

    @NotNull
    public Dimension getCachedImageSize() {

        return new Dimension( _cachedImageWidth, _cachedImageHeight );

    }

    /**
     Use Gowing to recover a ObtuseImageFile instance.
     */

    public static ObtuseImageFile recoverObtuseImageFile(final File obtuseImageFileFile )
            throws IOException, GowingUnpackingException {

        boolean oldLoggingEnabled = Logger.setLoggingEnabled( s_loadLoggingEnabled );
        try ( Measure ignored = new Measure( "recover BIF" ) ) {

            StdGowingUnPacker unPacker = new StdGowingUnPacker( s_gowingTypeIndex, obtuseImageFileFile );
            GowingUnPackedEntityGroup unPackResult = unPacker.unPack();

            ObtuseImageFile bif = null;

                for ( GowingPackable entity : unPackResult.getAllEntities() ) {

                    if ( entity instanceof ObtuseImageFile ) {

                        if ( bif == null ) {

                            bif = (ObtuseImageFile)entity;

                        } else {

                            Logger.logMsg( "got more than one ObtuseImageFile with serial number " +
                                           bif.getSerialNumber() +
                                           " (ignoring all but the first)" );

                        }

                    }

                }

            ObtuseUtil.doNothing();

            return bif;

        } finally {

            Logger.setLoggingEnabled( oldLoggingEnabled );

        }

    }

    /**
     Recover ObtuseImageFile instances in bulk.
     This method attempts to recover {@link ObtuseImageFile} instances for each of a specified array
     of {@link File} references to their {@code .binfo} files.

     @param binfoFiles a {@link SortedMap}{@code <Integer,ObtuseImageFile>} containing what was recoverable
     from the provided array of {@code .binfo} files.
     @return an array of the ObtuseImageFiles which were recoverable from the provided {@code .binfo} files.
     @throws ObtuseMessageLabel.AugmentedIllegalArgumentException if {@code dir} is not actually a directory.
     */

    @NotNull
    public static SortedMap<Integer, ObtuseImageFile> getSpecifiedObtuseInfoFiles(
            final @NotNull File@NotNull[] binfoFiles
    ) {

        SortedMap<Integer, ObtuseImageFile> rval = new TreeMap<>();
        for ( File f : binfoFiles ) {

            try ( Measure ignored = new Measure( "load a bif in bulk" ) ) {

                ObtuseImageFile bif = recoverObtuseImageFile( f );
                if ( bif != null && !rval.containsKey( bif.getSerialNumber() ) ) {

                    rval.put( bif.getSerialNumber(), bif );

                }

            } catch ( Throwable e ) {

                e.printStackTrace();

                if ( e instanceof  HowDidWeGetHereError ) {

                    throw (HowDidWeGetHereError)e;

                }

            }

        }

        return rval;

    }

    /**
     Find all of the {@code .binfo} files in our image repository.

     @return a {@link SortedMap}{@code <Integer,File>} of the {@code .binfo} files in our image repository
     (the key is the {@code .binfo} file's serial number and the value is a {@link File} instance referencing
     the corresponding {@code .binfo}.
     {@code null} if there is no image repository or if an I/O error occurs.
     An empty array if there are no {@code .binfo} files in our image repository.
     */

    @Nullable
    public static SortedMap<Integer, File> getAllBinfoFiles() {

        File[] binfoFiles = ObtuseImageFile.getImageRepositoryFile().listFiles(
                ( dir, name ) -> {

                    Matcher m = ObtuseImageFile.GENERATED_IMAGE_INFO_FILENAME_PATTERN.matcher( name );
                    return m.matches();

                }

        );

        if ( binfoFiles == null ) {

            Logger.logErr( "ObtuseImageFile.getAllObtuseInfoFiles:  no image repository" );

            return null;

        }

        SortedMap<Integer, File> map = new TreeMap<>();
        for ( File binfoFile : binfoFiles ) {

            Matcher m = ObtuseImageFile.GENERATED_IMAGE_INFO_FILENAME_PATTERN.matcher( binfoFile.getName() );
            if ( m.matches() ) {

                try {

                    int thisSn = Integer.parseInt( m.group( 1 ) );
                    map.put( thisSn, binfoFile );

                } catch ( NumberFormatException e ) {

                    throw new HowDidWeGetHereError(
                            "our info file filename pattern did not properly parse " +
                            "a filename that matches the pattern " +
                            "(classic software developer cowardice of blaming it on the pattern)"
                    );

                }

            }

        }

        return map;

    }

    /**
     Verify that this image has been saved in the repository.
     */

    @SuppressWarnings("WeakerAccess")
    public boolean verifyImageIsReady() {

        //noinspection RedundantIfStatement
        if ( _imageState == ImageState.READY ) {

            return true;

        } else {

            return false;

        }

    }

    public int getSerialNumber() {

        return _ourSerialNumber;

    }

    public String getImageFileMD5() {

        return _imageFileMD5;

    }

    public ObtuseImageFile( @SuppressWarnings("unused") final GowingUnPacker unPacker, final GowingPackedEntityBundle bundle )
            throws GowingUnpackingException {

        super( unPacker, bundle.getSuperBundle() );

        try {

            _imageState = ImageState.valueOf( bundle.MandatoryStringValue( IMAGE_STATE_NAME ) );
            _originalURI = bundle.recoverURI( ORIGINAL_URI_GTAG );
            _originalURL = bundle.recoverURL( ORIGINAL_URL_GTAG );
            _originalFile = bundle.recoverFile( ORIGINAL_FILE_GTAG );
            _originalImageFormat = bundle.optString( ORIGINAL_IMAGE_FORMAT_GTAG ).orElse( null );
            _cachedImageFormat = bundle.optString( CACHED_IMAGE_FORMAT_GTAG ).orElse( null );
            _diagnosticCachedFilesBasename = bundle.recoverFile( CACHED_IMAGE_FILE_BASENAME_GTAG );

            if ( bundle.doesFieldExist( CACHED_IMAGE_WIDTH_GTAG ) ) {

                _cachedImageWidth = bundle.intValue( CACHED_IMAGE_WIDTH_GTAG );
                _cachedImageHeight = bundle.intValue( CACHED_IMAGE_HEIGHT_GTAG );

            } else {

                _cachedImageWidth = -1;
                _cachedImageHeight = -1;

            }

            _ourSerialNumber = bundle.intValue( OUR_SERIAL_NUMBER_NAME );
            _imageFileMD5 = bundle.optString( IMAGE_MD5_GTAG ).orElse( null );
            _cachedImageFileSize = bundle.getNotNullField( CACHED_IMAGE_FILE_SIZE_GTAG ).longValue();
            _thumbnailImageBytes = bundle.getNullableField( THUMBNAIL_IMAGE_BYTES_NAME ).PrimitiveByteArrayValue();

            _title = bundle.optString( TITLE_GTAG ).orElse( null );

            ObtuseUtil.doNothing();

        } catch ( URISyntaxException e ) {

            throw new GowingUnpackingException(
                    "ObtuseImageFile:  unable to parse " +
                            ObtuseUtil.enquoteToJavaString( bundle.MandatoryStringValue( ORIGINAL_URI_GTAG ) ),
                    unPacker.curLoc()
            );

        } catch ( MalformedURLException e ) {

            throw new GowingUnpackingException(
                    "ObtuseImageFile:  unable to parse " +
                            ObtuseUtil.enquoteToJavaString( bundle.MandatoryStringValue( ORIGINAL_URL_GTAG ) ),
                    unPacker.curLoc()
            );

        }

    }

    @SuppressWarnings("unused")
    @Nullable
    public File getDiagnosticCachedFileBasename() {

        return _diagnosticCachedFilesBasename;

    }

    /**
     Determine if this image file is broken (unusable).
     <p/>If this method returns {@code false} then calling any of the other methods
     in this instance could fail in ugly and undocumented ways.
     @return {@code true} if this image file is broken. {@code false} if this image
     file might not be ready to use but it is not (yet) broken.
     */

    @SuppressWarnings("unused")
    public boolean isBroken() {

        return _imageState == ImageState.BROKEN;

    }

    @NotNull
    public String getTitle() {

        if ( _originalURI == null && _originalURL == null ) {

            return "<<unknown - no URI or URL>>";

        } else if ( _originalURI != null ) {

            return _originalURI.toString();

        } else {

            return _originalURL.toString();

        }

    }

    @SuppressWarnings("unused")
    @Nullable
    public URI getOriginalURI() {

        return _originalURI;

    }

    @SuppressWarnings("WeakerAccess")
    public URL getOriginalURL() {

        return _originalURL;

    }

    private void setImageState( final @NotNull ImageState newState ) {

        if ( _imageState == ImageState.BROKEN && newState != ImageState.BROKEN ) {

            throw new HowDidWeGetHereError(
                    "image state is already broken, cannot change it to " +
                    newState +
                    " (" + ( _ourSerialNumber > 0 ? "sn=" + _ourSerialNumber : "unknown image" ) + ")"
            );

        }

        _imageState = newState;

    }

    @NotNull
    public ImageState getImageState() {

        return _imageState;

    }

    @SuppressWarnings("UnusedReturnValue")
    private Image copyImageFileToCache( @Nullable final Image originalImage )
            throws IOException {

        if ( _ourSerialNumber <= 0 ) {

            setImageState( ImageState.BROKEN );
            throw new ObtuseMessageLabel.AugmentedIllegalArgumentException(
                    "too early to copy the image file to the cache - we haven't decided where it goes yet",
                    "need to call actuallyWriteImageInfoFile() method first"
            );

        }

        final BufferedImage image;

        File tmpFile = getCachedTemporaryImageFileLocation();
        if ( _originalURL == null ) {

            if ( originalImage == null ) {

                setImageState( ImageState.BROKEN );
                throw new HowDidWeGetHereError( "originalImage is null on File path" );

            }

            try ( Measure ignored = new Measure( "caching from ImageIcon" ) ) {

                _originalImageFormat = null;
                _cachedImageFormat = "jpg";
                try ( OutputStream os = new FileOutputStream( tmpFile ) ) {

                    captureImageViaOutputStream( originalImage, _cachedImageFormat, _title, os );

                }

            }

            try ( Measure ignored = new Measure( "test loading image" ) ) {

                // Make sure that we can load the image.

                image = ImageIO.read( tmpFile );

                if ( image == null ) {

                    setImageState( ImageState.BROKEN );
                    throw new ObtuseMessageLabel.AugmentedIllegalArgumentException(
                            "ObtuseImageFile#copyImageFileToCache:  didn't get an image from the File handling path",
                            "Tell Danny."
                    );

                }

            }

        } else {

            try (
                    Measure ignored = new Measure(
                            "caching from " +
                            ( _originalFile == null ? "net?" : "local file" ) + " URL" )
            ) {

                // First step is to copy the image into our cache.

                _cachedImageFileSize = copyURLContentsToFile( _originalURL, tmpFile );

            }

            try ( Measure ignored = new Measure( "loading image and learning its format" ) ) {

                // Second step is to load the image using a technique which reveals the image file's format.
                // Since we end up with the image in memory, we also use that image to create this instance's
                // _imageIcon.

                try ( ImageInputStream stream = ImageIO.createImageInputStream( tmpFile ) ) {

                    Iterator<ImageReader> readers = ImageIO.getImageReaders( stream );
                    if ( readers.hasNext() ) {

                        ImageReader reader = readers.next();

                        try {

                            reader.setInput( stream );

                            image = reader.read( 0 );  // Read the same image as ImageIO.read

                            if ( image == null ) {

                                setImageState( ImageState.BROKEN );
                                throw new ObtuseMessageLabel.AugmentedIllegalArgumentException(
                                        "ObtuseImageFile#copyImageFileToCache:  " +
                                        "didn't get an image from the URL handling path",
                                        "Tell Danny."
                                );

                            }                //		    _imageIcon = new ImageIcon( image );

                            _originalImageFormat = reader.getFormatName();

                        } finally {

                            reader.dispose();

                        }

                    } else {

                        setImageState( ImageState.BROKEN );
                        throw new ObtuseMessageLabel.AugmentedIllegalArgumentException(
                                "unable to load an image obtained via \"" + _originalURL + "\"" );

                    }

                }

                String cleanedFormat = s_supportedImageFormatSuffixes.get( _originalImageFormat.toLowerCase() );
                if ( cleanedFormat == null ) {

                    cleanedFormat = _originalImageFormat.toLowerCase();

                    OkPopupMessageWindow.doit( "########## a supposedly unsupported format \"" +
                                               _originalImageFormat +
                                               "\" worked with \"" +
                                               _originalURL +
                                               "\"", OkPopupMessageWindow.OK_BUTTON_LABEL );

                }

                _cachedImageFormat = cleanedFormat;

            }

        }

        setImageState( ImageState.IN_CACHE_TEMP );
        actuallyWriteImageInfoFile();

        File permanentName = getCachedImageFileLocation();
        if ( !tmpFile.renameTo( permanentName ) ) {

            setImageState( ImageState.BROKEN );
            throw new IOException( "unable to rename \"" + tmpFile + "\" as \"" + permanentName + "\"" );

        }

        _cachedImageFileSize = permanentName.length();

        _imageFileMD5 = ObtuseUtil.computeMD5( getCachedImageFileLocation() );

        @NotNull Optional<ImageIcon> optThumbnailImageIcon = ObtuseImageUtils.maybeRegenerateThumbnail(
                "image",
                new ImageIcon( image ),
                null,
                THUMBNAIL_EDGE_LENGTH
        );

        if ( optThumbnailImageIcon.isPresent() ) {

            Optional<byte[]> thumbnailBytes = captureImageAsFile(
                    optThumbnailImageIcon.get().getImage(),
                    "jpg",
                    _title
            );

            if ( thumbnailBytes.isPresent() ) {

                _thumbnailImageBytes = thumbnailBytes.get();
                setImageState( ImageState.READY );

                actuallyWriteImageInfoFile();

                _cachedImageWidth = image.getWidth();
                _cachedImageHeight = image.getHeight();

            } else {

                setImageState( ImageState.BROKEN );

            }

        } else {

            setImageState( ImageState.BROKEN );

        }

        return image;

    }

    private int copyURLContentsToFile( final URL inputURL, final File outputFile )
            throws IOException {

        int bytesRead = 0;
        try (
                BufferedOutputStream os = new BufferedOutputStream( new FileOutputStream( outputFile ) );
                BufferedInputStream is = new BufferedInputStream( inputURL.openStream() )
        ) {

            byte[] buffer = new byte[4096 * 4];
            while ( true ) {

                int rLength = is.read( buffer );
                if ( rLength < 0 ) {

                    break;

                }

                if ( rLength > 0 ) {

                    bytesRead += rLength;

                    os.write( buffer, 0, rLength );

                }

            }

        }

        return bytesRead;

    }

    /**
     Do a rather simplistic bakeoff between the cost of loading our image
     via the ImageIcon facility and via the ImageIO facility.
     */

    @SuppressWarnings("unused")
    private void bakeOff() {

        if ( _originalFile == null ) {

            Logger.logMsg(
                    "this bakeoff assumes that the image file is local and is accessible " +
                    "via our _originalFile instance"
            );
            Logger.logMsg( "no cookies for you!" );

            return;

        }

        try ( Measure ignored = new Measure( "ImageIcon" ) ) {

            ImageIcon testImageIcon = new ImageIcon( _originalFile.getPath() );

        }

        Logger.logMsg( "We've got the image via a File reference" );

        BufferedImage image;
        try ( Measure ignored = new Measure( "ImageIO" ) ) {

            image = ImageIO.read( _originalURL );

        } catch ( IOException e ) {

            setImageState( ImageState.BROKEN );
            throw new HowDidWeGetHereError(
                    "ObtuseImageFile:  unable to load image from \"" + _originalURL + "\" via ImageIO",
                    e
            );

        }

        try ( Measure ignored = new Measure( "Image wrapped in ImageIcon" ) ) {

            ImageIcon wrappedImage = new ImageIcon( image );

        }

        Measure.showStats();
        ObtuseUtil.doNothing();

    }

    private void createImageInfoFile( final Image originalImage )
            throws IOException {

        // Make sure that we've not done this before.

        if ( _ourSerialNumber > 0 ) {

            setImageState( ImageState.BROKEN );
            throw new HowDidWeGetHereError( "the image info file may only be created once" );

        }

        // Pick a place for the image info file, the temporary image file and the permanent image file to live.

        int maxSn = findMaximumSerialNumber();

        // Generate new image info file names until we find one that nobody else is using.

        while ( true ) {

            maxSn += 1;

            File candidateImageInfoFileObject = constructCachedImagRepositoryFileObject(
                    maxSn,
                    ObtuseImageFile.OBTUSE_IMAGE_INFO_FILENAME_SUFFIX
            );

            // Do we have a winner?
            // Note that creating our empty image info file reserves places for any and all
            // image repository files that we might create for this image.

            if ( candidateImageInfoFileObject.createNewFile() ) {

                // We have a winner. We'll use it over time to derive our various file names.

                _ourSerialNumber = maxSn;

                setImageState( ImageState.SERIAL_NUMBER_SET );

                break;

            }

        }

        // We've got a place to put it so serialize ourselves to our image info file.

        actuallyWriteImageInfoFile();

        // Write or copy the original image file into the repository.

        copyImageFileToCache( originalImage );

        // Re-write our image info file to memorialize the image's file format
        // and thus precisely where our image file landed.

        actuallyWriteImageInfoFile();

        // We're done.

    }

    /**
     Figure out where our cached files will live by generating a known-to-be-unique
     basename for said files.
     <p>
     This involves getting a list of all filenames which could be info files and then
     picking a basename that isn't the basename for any of them. Since our cached files
     have embedded serial numbers, this really boils down to finding an unused serial
     number. Once we have a candidate basename, we try to atomically create the info file.
     If that fails then we lost a race of some sort so we just keep looking until we find
     a basename that works.
     */

    private int findMaximumSerialNumber() {

        File[] existingNames = ObtuseImageFile.getImageRepositoryFile().listFiles(
                ( dir, name ) -> {

                    Matcher m = ObtuseImageFile.GENERATED_IMAGE_INFO_FILENAME_PATTERN.matcher( name );
                    return m.matches();

                }

        );

        // Find the numerically largest serial number.

        int maxSn = 0;
        if ( existingNames != null ) {

            for ( File f : existingNames ) {

                Matcher m = ObtuseImageFile.GENERATED_IMAGE_INFO_FILENAME_PATTERN.matcher( f.getName() );
                if ( m.matches() ) {

                    try {

                        int thisSn = Integer.parseInt( m.group( 1 ) );
                        maxSn = Math.max( thisSn, maxSn );

                    } catch ( NumberFormatException e ) {

                        setImageState( ImageState.BROKEN );
                        throw new HowDidWeGetHereError(
                                "our info file filename pattern did not properly parse " +
                                "a filename that matches the pattern " +
                                "(classic software developer cowardice of blaming it on the pattern)"
                        );

                    }

                }

            }

        }

        return maxSn;

    }

    @NotNull
    public File getCachedImageInfoFileLocation() {

        return constructCachedImagRepositoryFileObject(
                _ourSerialNumber,
                ObtuseImageFile.OBTUSE_IMAGE_INFO_FILENAME_SUFFIX
        );

    }

    @NotNull
    public File getCachedImageFileLocation() {

        return constructCachedImagRepositoryFileObject( _ourSerialNumber, "." + _cachedImageFormat );

    }

    @NotNull
    public File getCachedTemporaryImageFileLocation() {

        return constructCachedImagRepositoryFileObject( _ourSerialNumber, ".timage" );

    }

    @NotNull
    public static File constructCachedImagRepositoryFileObject( final int sn, final String suffix ) {

        return new File(
                ObtuseImageFile.getImageRepositoryFile(),
                constructCachedImageRepositorySimpleBasename( sn ) + suffix
        );

    }

    @NotNull
    public static File constructCachedImageRepositoryBasename( final int sn ) {

        return new File(
                ObtuseImageFile.getImageRepositoryFile(),
                constructCachedImageRepositorySimpleBasename( sn )
        );

    }

    @NotNull
    private static String constructCachedImageRepositorySimpleBasename( final int sn ) {

        return ObtuseImageFile.GENERATED_IMAGE_FILE_PREFIX +
               ObtuseUtil.lpad( "" + sn, ObtuseImageFile.GENERATED_IMAGE_FILE_SN_LENGTH, '0'
               );

    }

    private void actuallyWriteImageInfoFile()
            throws FileNotFoundException {

        int entityCount;
        ImageState statePriorToWrite = _imageState;
        boolean worked = false;

        try ( Measure ignored = new Measure( "writing image info file" ) ) {

            // If the recorded image state has not reached the point where the image info file has
            // been written BEFORE we write it this time then set the state to indicate that it has been written.
            // We will rewind the state back to what it was when we entered this block if the write fails.

            if ( _imageState.compareTo( ImageState.INFO_FILE_CREATED ) < 0 ) {

                setImageState( ImageState.INFO_FILE_CREATED );

            }

            try (
                    StdGowingPacker packer = new StdGowingPacker(
                            new EntityName( "info file save" ),
                            getCachedImageInfoFileLocation()
                    )
            ) {

                packer.queuePackableEntity( this );

                entityCount = packer.finish();

                worked = true;

            }

        } finally {

            if ( !worked ) {

                setImageState( statePriorToWrite );

            }

        }

        Logger.logMsg(
                "wrote " + entityCount + " entities while saving image to " + getCachedImageInfoFileLocation()
        );

    }

    /**
     Get the full size image into memory by loading it from our image cache/repository.
     <p/>This method is fairly paranoid. You will either the image returned to you or a checked exception
     thrown at you (see below for which checked exceptions you'll need to deal with).

     @return the full size image.
     @throws IOException if an error occurs reading the cached image file.
     @throws ObtuseImageLoadFailed if {@link ImageIO#read} returns {@code null}
     (documentation for {@code ImageIO.read} says that the method can return {@code null}
     but doesn't specify the circumstances under which it actually does return {@code null}). Sigh.
     <p/>The 'good news' is that the constructors for this class are designed to fail
     (by throwing an exception) if they are unable to create a thumbnail for the image
     and write/copy the image to our image cache/repository. Since creating the thumbnail
     requires that the image be loaded, it is almost certainly impossible for this method
     to be unable to load the image (famous last words).
     */

    @NotNull
    public Image loadPrimaryImageFromCache()
            throws IOException, ObtuseImageLoadFailed {

        Logger.logMsg( "loading primary image for " + this );

        try ( Measure ignored = new Measure( "load primary image" ) ) {

            BufferedImage image = ImageIO.read( getCachedImageFileLocation() );

            if ( image == null ) {

                throw new ObtuseImageLoadFailed(
                        "ObtuseImageFile.loadPrimaryImage:  " +
                        "image decoder returned null (no idea why - sorry)"
                );

            } else if ( _cachedImageWidth == -1 || _cachedImageHeight == -1 ) {

                _cachedImageWidth = image.getWidth();
                _cachedImageHeight = image.getHeight();

            }

            return image;

        }

    }

    @NotNull
    public static Optional<byte[]> captureImageAsFile(
            final Image image,
            @SuppressWarnings("SameParameterValue") final String formatName,
            final String what
    ) {

        ByteArrayOutputStream baos = null;

        try {

            baos = new ByteArrayOutputStream();

            captureImageViaOutputStream( image, formatName, what, baos );

            byte[] rval = baos.toByteArray();

            Logger.logMsg( "image consumed " + rval.length + " bytes capturing " + what );

            return Optional.of( rval );

        } finally {

            ObtuseUtil.closeQuietly( baos );

        }

    }

    public static void captureImageViaOutputStream(
            final Image image,
            final String formatName,
            final String what,
            final OutputStream os
    ) {

        try {

            try {

                ImageIcon imageIcon = new ImageIcon( image );
                BufferedOutputStream bos = new BufferedOutputStream( os );

                BufferedImage bi = new BufferedImage(
                        imageIcon.getIconWidth(),
                        imageIcon.getIconHeight(),
                        BufferedImage.TYPE_INT_RGB
                );

                Graphics g = bi.createGraphics();

                // paint the Icon onto the BufferedImage.

                imageIcon.paintIcon( null, g, 0, 0 );
                g.dispose();

                ImageIO.write( bi, formatName, bos );

                bos.flush();

                //noinspection UnnecessaryReturnStatement
                return;

            } catch ( IOException e ) {

                Logger.logErr( "captureImageAsFile:  exception trying to capture image " + what, e );

            }

        } catch ( Throwable e ) {

            Logger.logErr( "captureImageAsFile:  runtime error trying to capture image " + what, e );

        }

    }

    @NotNull
    public ImageIcon getThumbnailImageIcon() {

        if ( _thumbnailImageBytes == null ) {

            Logger.logMsg( "no thumbnail" );

        }

        return new ImageIcon( _thumbnailImageBytes );

    }

    @SuppressWarnings("unused")
    private double getGeometricScalingFactor( final int boxW, final int boxH, final int origW, final int origH ) {

        // The scaling factor (sf) to convert the original image into
        // the thumbnail image must satisfy at least one of a or b:
        // a: origW * sf == boxW and origH * sf <= boxH
        // b: origW * sf <= boxW and origH * sf == boxH

        // Compute the scaling factors which satisfy the equalities in each of a and b.

        double sf1 = boxW / (double)origW;
        double sf2 = boxH / (double)origH;

        // Figure out which of the two candidate scaling factors also satisfy their respective inequalities.

        double sf;
        if ( origH * sf1 <= boxH ) {

            sf = sf1;

        } else {

            if ( origW * sf2 <= boxW ) {

                sf = sf2;

            } else {

                setImageState( ImageState.BROKEN );
                String msg =
                        "cannot compute scaling factor " +
                        "(original is " + origW + "x" + origH + ", box is " + boxW + "x" + boxH + ", " +
                        "sf1=" + sf1 + ", sf2=" + sf2 + ")";

                throw new HowDidWeGetHereError( msg );

            }

        }
        return sf;
    }

    public String toString() {

        return "ObtuseImageFile( " +
               "sn = " + getSerialNumber() + ", " +
               "state = " + getImageState() + ", " +
               "original URI = " + getOriginalURL() + " " +
               ")";

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( final boolean isPackingSuper, final @NotNull GowingPacker packer ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleRoot( packer ),
                packer.getPackingContext()
        );

        bundle.addStringHolder( IMAGE_STATE_NAME, _imageState.name(), true );
        bundle.addStringHolder( ORIGINAL_URI_GTAG, _originalURI == null ? null : _originalURI.toString(), false );
        bundle.addStringHolder( ORIGINAL_URL_GTAG, _originalURL == null ? null : _originalURL.toString(), false );
        bundle.addStringHolder( ORIGINAL_FILE_GTAG, _originalFile == null ? null : _originalFile.getPath(), false );
        bundle.addStringHolder( ORIGINAL_IMAGE_FORMAT_GTAG, _originalImageFormat, false );
        bundle.addStringHolder( CACHED_IMAGE_FORMAT_GTAG, _cachedImageFormat, true );
        bundle.addIntegerHolder( CACHED_IMAGE_WIDTH_GTAG, _cachedImageWidth, false );
        bundle.addIntegerHolder( CACHED_IMAGE_HEIGHT_GTAG, _cachedImageHeight, false );
        bundle.addIntegerHolder( OUR_SERIAL_NUMBER_NAME, _ourSerialNumber, false );
        bundle.addStringHolder(
                        CACHED_IMAGE_FILE_BASENAME_GTAG,
                        _diagnosticCachedFilesBasename == null ? null : _diagnosticCachedFilesBasename.toString(),
                        false
        );
        bundle.addStringHolder( IMAGE_MD5_GTAG, _imageFileMD5, false );
        bundle.addStringHolder( TITLE_GTAG, _title, false );
        bundle.addLongHolder( CACHED_IMAGE_FILE_SIZE_GTAG, _cachedImageFileSize, false );
        bundle.addByteHolder( THUMBNAIL_IMAGE_BYTES_NAME, _thumbnailImageBytes, false );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        return true;

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "Obtuse", "testing" );

        setImageRepositoryFile( new File( "TestImageDirectory" ), true );

        ObtuseImageFile bf = null;
        Image im = null;
        try {

            bf = new ObtuseImageFile( new File( "testImage.jpg" ) );
            im = bf.loadPrimaryImageFromCache();

        } catch ( ObtuseImageFileInstanceCreationFailed | ObtuseImageLoadFailed | IOException e ) {

            e.printStackTrace();

        }

        final ObtuseImageFile bif = bf;
        final Image image = im;

        JFrame jf = new JFrame( "sanity test ObtuseImageFile" );
        JPanel jp = new JPanel() {

            public void paintComponent( final Graphics g ) {

                super.paintComponent( g );

                paintImage( g, bif, this, image );

            }

            public void paintImage(
                    final @NotNull Graphics origGraphics,
                    @Nullable final ObtuseImageFile bif,
                    final JPanel panel,
                    final Image image
            ) {

                Graphics g = null;

                try {

                    g = origGraphics.create();

                    if ( panel.isOpaque() ) {

                        Logger.logMsg( "panel is opague" );

                        g.setColor( Color.ORANGE );
                        g.fillRect( 0, 0, panel.getWidth(), panel.getHeight() );

                    }

                    Graphics2D g2d = (Graphics2D)g;

                    if ( image == null ) {

                        Logger.logMsg( "image is null" );

                        RenderingHints oldHints = g2d.getRenderingHints();
                        g2d.setRenderingHint(
                                RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                        );

                        Font oldFont = g2d.getFont();
                        Font f = oldFont.deriveFont( 36f );
                        g2d.setFont( f );
                        FontMetrics fm = g2d.getFontMetrics();

                        String str = bif == null ? "❦" : "?";

                        int x = ( panel.getWidth() - fm.stringWidth( str ) ) / 2;
                        int y = ( panel.getHeight() + fm.getAscent() ) / 2 - fm.getDescent();

                        g2d.drawString( str, x, y );

                        g2d.setFont( oldFont );

                        g2d.setRenderingHints( oldHints );

                    } else {

                        Logger.logMsg( "drawing image" );
                        boolean done = g2d.drawImage(
                                image,
                                0,
                                0,
                                panel
                        );

                        if ( !done ) {

                            Logger.logMsg( "still more work to do drawing image " + image + " into " + panel );

                        }

                        g2d.drawLine( 0, 0, panel.getWidth(), panel.getHeight() );

                    }

                } finally {

                    if ( g != null ) {

                        g.dispose();

                    }

                }

            }

        };

        jf.setMinimumSize( new Dimension( 400, 400 ) );
        jf.setContentPane( jp );

        jf.pack();
        jf.setVisible( true );

    }

}
