/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.kv;

import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
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
                final @NotNull GowingUnPacker unPacker, final @NotNull GowingPackedEntityBundle bundle, final @NotNull GowingEntityReference er
        )
                throws GowingUnpackingException {

            return new ObtuseKeywordValue( unPacker, bundle );

        }

    };

    private final String _value;

    /**
     Create a clone of a keyword-value pair.

     @param keywordValue the keyword-value pair to be cloned.
     */

    public ObtuseKeywordValue( final @NotNull ObtuseKeywordValue keywordValue ) {
        super( keywordValue );

        _value = keywordValue.getValue();

    }

    /**
     Create a keyword-value pair.

     @param keywordInfo the keyword.
     @param value       its value.
     */

    public ObtuseKeywordValue( final @NotNull ObtuseKeyword keywordInfo, @Nullable final String value ) {
        super( keywordInfo );

        _value = value;

    }

    public ObtuseKeywordValue(
            @SuppressWarnings("unused") final @NotNull GowingUnPacker unPacker,
            final @NotNull GowingPackedEntityBundle bundle
    ) {

        super( unPacker, bundle.getSuperBundle() );

        _value = bundle.MandatoryStringValue( VALUE );

    }

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, final @NotNull GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleThyself( true, packer ),
                packer.getPackingContext()
        );

        bundle.addStringHolder( VALUE, _value, true );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        return true;

    }

    /**
     Determine if this instance has a non-null value.
     @return {@code true} if this instance has a non-null value; {@code false} otherwise.
     */

    public boolean hasNonNullValue() {

        return _value != null;

    }

    /**
     Determine if this instance has a null value.
     @return the opposite of {@link #hasNonNullValue()}.
     */

    public boolean hasNullValue() {

        return _value == null;

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
