/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.kv;

import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnPackerParsingException;
import com.obtuse.util.gowing.p2a.holders.GowingStringHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Describe a keyword-value pair.
 <p>Note that instances of this class are also instances of the {@link ObtuseKeyword} class because this class is derived from that class.</p>
 <p>Instances of this class are immutable.</p>
 */

public class ObtuseKeywordValue extends ObtuseKeyword {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( ObtuseKeywordValue.class );
    private static final int VERSION = 1;

    private static final EntityName VALUE = new EntityName( "_v" );

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;
        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;
        }

        @SuppressWarnings("RedundantThrows")
        @NotNull
        @Override
        public GowingPackable createEntity(
                @NotNull final GowingUnPacker unPacker, @NotNull final GowingPackedEntityBundle bundle, final GowingEntityReference er
        )
                throws GowingUnPackerParsingException {

            return new ObtuseKeywordValue( unPacker, bundle );

        }

    };

//    private final ObtuseKeyword _keywordInfo;
    private final String _value;

    /**
     Create a clone of a keyword-value pair.

     @param keywordValue the keyword-value pair to be cloned.
     */

    public ObtuseKeywordValue( @NotNull final ObtuseKeywordValue keywordValue ) {
        super( keywordValue );

        _value = keywordValue.getValue();

    }

    /**
     Create a keyword-value pair.

     @param keywordInfo the keyword.
     @param value       its value.
     */

    public ObtuseKeywordValue( @NotNull final ObtuseKeyword keywordInfo, @Nullable final String value ) {
        super( keywordInfo );

        _value = value;

    }

    public ObtuseKeywordValue(
            @SuppressWarnings("unused") @NotNull final GowingUnPacker unPacker,
            @NotNull final GowingPackedEntityBundle bundle
    ) {

        super( unPacker, bundle.getSuperBundle() );

        _value = bundle.MandatoryStringValue( VALUE );

    }

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, @NotNull final GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleThyself( true, packer ),
                packer.getPackingContext()
        );

        bundle.addHolder( new GowingStringHolder( VALUE, _value, true ) );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) {

        return true;

    }

//    /**
//     Get this instance's keyword's info.
//
//     @return this instance's keyword's info.
//     */
//
//    @NotNull
//    public ObtuseKeyword getKeywordInfo() {
//
//        return _keywordInfo;
//
//    }

    /**
     Determine if this instance has a non-null value.
     @return {@code true} if this instance has a non-null value; {@code false} otherwise.
     */

    public boolean hasNonNullValue() {

        return _value != null;

    }

    /**
     Get this instance's value.

     @return this instance's value (could be {code null}).
     */

    @Nullable
    public String getValue() {

        return _value;

    }

    /**
     Get this instance's non-null value.

     @return this instance's non-null value.
     @throws IllegalArgumentException if this instance's value is {@code null}.
     */

    @NotNull
    public String getNonNullValue() {

        if ( _value == null ) {

            throw new IllegalArgumentException( "value for " + getKeywordName() + " is null" );

        }

        return _value;

    }

    @Override
    public String toString() {

        return getKeywordName() + "=" + ObtuseUtil.enquoteToJavaString( getValue() );

    }

}
