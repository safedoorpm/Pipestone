package com.obtuse.util.gowing.p2a.backref;

import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 Something to carry around a {@link File} instance.
 */

public class GowingFile extends File implements GowingBackReferenceable {

    private enum ConstructorVariant {
        STRING,
        FILE,
        FILE_STRING,
        STRING_STRING,
        URI
    }

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GowingFile.class );
    private static final int VERSION = 1;
    private static final EntityName G_P1_FILE = new EntityName( "_p1f" );
    private static final EntityName G_P1_STRING = new EntityName( "_p1s" );
    private static final EntityName G_P1_URI = new EntityName( "_p1u" );
    private static final EntityName G_P2_STRING = new EntityName( "_p2s" );
    private static final EntityName G_STRING = new EntityName( "_s" );
    private static final EntityName G_VARIANT = new EntityName( "_v" );

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;
        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;
        }

        @Override
        public @NotNull GowingPackable createEntity(
                @NotNull final GowingUnPacker unPacker,
                @NotNull final GowingPackedEntityBundle bundle,
                @NotNull final GowingEntityReference er
        ) throws GowingUnpackingException {

            String variantString = bundle.MandatoryStringValue( G_VARIANT );
            ConstructorVariant cv;
            try {

                cv = ConstructorVariant.valueOf( variantString );

            } catch ( IllegalArgumentException e ) {

                cv = ConstructorVariant.STRING;
            }

            GowingFile rval;
            @SuppressWarnings("UnusedAssignment") String p1s = null;
            @SuppressWarnings("UnusedAssignment") File p1f = null;
            @SuppressWarnings("UnusedAssignment") String p2s = null;
            @SuppressWarnings("UnusedAssignment") URI uri = null;
            switch ( cv ) {

                case STRING:
                    p1s = bundle.MandatoryStringValue( G_P1_STRING );
                    rval = new GowingFile( p1s );

                    break;

                case FILE:
                    p1s = bundle.MandatoryStringValue( G_P1_FILE );
                    p1f = new File( p1s );
                    rval = new GowingFile( p1f );

                    break;

                case FILE_STRING:
                    p1s = bundle.MandatoryStringValue( G_P1_FILE );
                    p1f = new File( p1s );
                    p2s = bundle.MandatoryStringValue( G_P2_STRING );
                    rval = new GowingFile( p1f, p2s );

                    break;

                case STRING_STRING:
                    p1s = bundle.MandatoryStringValue( G_P1_STRING );
                    p2s = bundle.MandatoryStringValue( G_P2_STRING );
                    rval = new GowingFile( p1s, p2s );

                    break;

                case URI:
                    try {

                        uri = bundle.recoverURI( G_P1_URI );
                        rval = new GowingFile( uri );

                    } catch ( URISyntaxException e ) {

                        // URIs are packed as strings and we know that this URI's string actually exists
                        // since we would have failed via a different path if it didn't.
                        // Hence, it is safe to call bundle.MandatoryStringValue( String ) here.
                        throw new GowingUnpackingException(
                                "GowingFile:  unable to unpack URI's string form " +
                                "(" + ObtuseUtil.enquoteJavaObject( bundle.MandatoryStringValue( G_P1_URI ) ) + ")",
                                unPacker.curLoc(),
                                e
                        );

                    } catch ( IllegalArgumentException e ) {

                        // URIs are packed as strings and we know that this URI's string actually exists
                        // since we would have failed via a different path if it didn't.
                        // Hence, it is safe to call bundle.MandatoryStringValue( String ) here.
                        throw new GowingUnpackingException(
                                "GowingFile:  preconditions on " +
                                "(" + ObtuseUtil.enquoteJavaObject( bundle.MandatoryStringValue( G_P1_URI ) ) + ")" +
                                " do not hold",
                                unPacker.curLoc(),
                                e
                        );

                    }

                    break;

                default:

                    throw new GowingUnpackingException(
                            "GowingFile:  unknown/unsupported constructor variant " + ObtuseUtil.enquoteJavaObject( cv ),
                            unPacker.curLoc()
                    );

            }

            return rval;

        }

    };

    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    private final ConstructorVariant _constructorVariant;
    private final String _p1s;
    private final String _p2s;
    private final File _p1f;
    private final URI _uri;

    public GowingFile( @NotNull final String path ) {
        super( path );

        _constructorVariant = ConstructorVariant.STRING;
        _p1s = path;
        _p1f = null;
        _p2s = null;
        _uri = null;

    }

    public GowingFile( @NotNull final File file ) {
        super( file.getPath() );

        _constructorVariant = ConstructorVariant.FILE;
        _p1s = null;
        _p1f = file;
        _p2s = null;
        _uri = null;

    }

    public GowingFile( @Nullable final String parent, @NotNull final String child ) {
        super( parent, child );

        _constructorVariant = ConstructorVariant.STRING_STRING;
        _p1s = parent;
        _p1f = null;
        _p2s = child;
        _uri = null;

    }

    public GowingFile( @Nullable final File parent, @NotNull final String child ) {
        super( parent, child );

        _constructorVariant = ConstructorVariant.FILE_STRING;
        _p1s = null;
        _p1f = parent;
        _p2s = child;
        _uri = null;

    }

    public GowingFile( @NotNull URI uri ) {
        super( uri );

        _constructorVariant = ConstructorVariant.URI;
        _p1s = uri.toString();
        _p1f = null;
        _p2s = null;
        _uri = uri;

    }

    @Override
    public @NotNull GowingInstanceId getInstanceId() {

        return _instanceId;
    }

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, @NotNull final GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                packer.getPackingContext()
        );

        bundle.addStringHolder( G_VARIANT, _constructorVariant.name(), true );
        switch ( _constructorVariant ) {

            case STRING:
                bundle.addStringHolder( G_P1_STRING, _p1s, true );
                break;

            case FILE:
                bundle.addStringHolder( G_P1_FILE, _p1f.getPath(), true );
                break;

            case FILE_STRING:
                bundle.addStringHolder( G_P1_FILE, _p1f.getPath(), true );
                bundle.addStringHolder( G_P2_STRING, _p2s, true );
                break;

            case STRING_STRING:
                bundle.addStringHolder( G_P1_STRING, _p1s, true );
                bundle.addStringHolder( G_P2_STRING, _p2s, true );
                break;

            case URI:
                bundle.addStringHolder( G_P1_URI, _p1s, true );
                break;

        }

        return bundle;

    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) throws GowingUnpackingException {

        return true;

    }

    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append( "GowingFile( " );
        switch ( _constructorVariant ) {

            case STRING:
                sb.append( "s=" ).append( ObtuseUtil.enquoteToJavaString( _p1s ) );
                break;

            case FILE:
                sb.append( "f=" ).append( ObtuseUtil.enquoteToJavaString( _p1f.toString() ) );
                break;

            case STRING_STRING:
                sb.append( "s1=" ).append( ObtuseUtil.enquoteToJavaString( _p1s ) )
                  .append( ", s2=" ).append( ObtuseUtil.enquoteToJavaString( _p2s ) );
                break;

            case FILE_STRING:
                sb.append( "s1=" ).append( ObtuseUtil.enquoteToJavaString( _p1f.toString() ) )
                  .append( ", s2=" ).append( ObtuseUtil.enquoteToJavaString( _p2s ) );
                break;

            case URI:
                sb.append( "uri=" ).append( ObtuseUtil.enquoteToJavaString( _p1s ) );
                break;

            default:
                sb.append( "unknown-toString=" ).append( ObtuseUtil.enquoteToJavaString( super.toString() ) );

        }
        sb.append( " )" );

        return sb.toString();

    }

}
