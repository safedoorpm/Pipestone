package com.obtuse.util;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import org.jetbrains.annotations.NotNull;

/**
 Provide a way to pack and unpack the result of a checksum operation.
 */

public class MessageDigestRecord extends GowingAbstractPackableEntity {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( MessageDigestRecord.class );

    private static final int VERSION = 1;

    private static final EntityName SOURCE_NAME = new EntityName( "_s" );
    private static final EntityName DIGEST_NAME = new EntityName( "_d" );
    private static final EntityName ALGORITHM_NAME = new EntityName( "_a" );

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
                final @NotNull GowingUnPacker unPacker, final @NotNull GowingPackedEntityBundle bundle, final @NotNull GowingEntityReference er
        ) {

            byte[] digest = bundle.MandatoryPrimitiveByteArrayValue( DIGEST_NAME );
            String algorithm = bundle.MandatoryStringValue( ALGORITHM_NAME );
            String sourceName = bundle.MandatoryStringValue( SOURCE_NAME );

            return new MessageDigestRecord( sourceName, digest, algorithm );

        }

    };

    private final String _sourceName;
    private final byte[] _digest;
    private final String _algorithm;
    private String _toString;
    private String _digestString;

    public MessageDigestRecord( @NotNull final String sourceName, @NotNull final byte[] digest, @NotNull final String algorithm ) {
        super( new GowingNameMarkerThing() );

        _sourceName = sourceName;
        _digest = digest;
        _algorithm = algorithm;

    }

    @NotNull
    public String getSourceName() {

        return _sourceName;

    }

    @NotNull
    public byte[] getDigest() {

        byte[] rval = new byte[_digest.length];
        System.arraycopy( _digest, 0, rval, 0, rval.length );

        return rval;

    }

    @NotNull String getDigestString() {

        if ( _digestString == null ) {

            _digestString = ObtuseUtil.hexvalue( _digest );

        }

        return _digestString;

    }

    public String getAlgorithm() {

        return _algorithm;

    }

    @Override
    public String toString() {

        if ( _toString == null ) {

            _toString = _algorithm + "/" + ObtuseUtil.hexvalue( _digest );

        }

        return _toString;

    }

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, @NotNull final GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleRoot( packer ),
                packer.getPackingContext()
        );

        bundle.addStringHolder( SOURCE_NAME, _sourceName, true );
        bundle.addStringHolder( ALGORITHM_NAME, _algorithm, true );
        bundle.addByteHolder( DIGEST_NAME, _digest, true );

        return bundle;

    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) throws GowingUnpackingException {

        return true;

    }

}
