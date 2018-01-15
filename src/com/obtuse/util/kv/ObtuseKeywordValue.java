/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.kv;

import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Describe a keyword-value pair.
 <p>Note that instances of this class are also instances of the {@link ObtuseKeywordInfo} class because this class is derived from that class.</p>
 <p>Instances of this class are immutable.</p>
 */

public class ObtuseKeywordValue extends ObtuseKeywordInfo {

//    private final ObtuseKeywordInfo _keywordInfo;
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

    public ObtuseKeywordValue( @NotNull final ObtuseKeywordInfo keywordInfo, @Nullable final String value ) {
        super( keywordInfo );

        _value = value;

    }

//    /**
//     Get this instance's keyword's info.
//
//     @return this instance's keyword's info.
//     */
//
//    @NotNull
//    public ObtuseKeywordInfo getKeywordInfo() {
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
