/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.kv;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 Describe a keyword.
 <p>Instances of this class are immutable.</p>
 */

public class ObtuseKeywordInfo implements Comparable<ObtuseKeywordInfo> {

    /**
     Describes the form of valid keywords.
     <p>Keywords must:
     <ol>
     <li>start and end with an uppercase letter (A-Z)</li>
     <li>contain only uppercase letters and underscores</li>
     <li>not contain more than one underscore in a row ({@code "A_B_C"} is ok but {code "A__B_C"} is not)</li>
     </ol>
     </p>
     */
    public static Pattern VALID_KEYWORD_PATTERN = Pattern.compile( "[A-Z][A-Z_]*[A-Z]" );

    private final String _keywordString;

    /**
     Create a clone of an existing keyword description.
     */

    public ObtuseKeywordInfo( @NotNull final ObtuseKeywordInfo rValue ) {
        this( rValue.getKeywordName() );

    }

    /**
     Create a keyword description.

     @param keywordString the keyword's string name.
     @throws IllegalArgumentException if the keyword is not matched by {@link #VALID_KEYWORD_PATTERN}.
     */

    public ObtuseKeywordInfo( @NotNull final String keywordString ) {
        super();

        Matcher m = VALID_KEYWORD_PATTERN.matcher( keywordString );

        if ( m.matches() ) {

            _keywordString = keywordString;

        } else {

            throw new IllegalArgumentException( "ObtuseKeywordInfo:  invalid keyword \"" + keywordString + "\"" );

        }

    }

    /**
     Get this keyword in {@link String} form.

     @return this keyword in {@link String} form.
     */

    public String getKeywordName() {

        return _keywordString;

    }

    /**
     Get the proper form of a reference to this keyword.

     @return returns {@code "$(" + getKeywordName() + ")"}
     */

    public String getKeywordReferenceString() {

        return "$(" + getKeywordName() + ")";

    }

    @Override
    public String toString() {

        return getKeywordName();

    }

    @Override
    public int compareTo( @NotNull final ObtuseKeywordInfo rhs ) {

        return getKeywordName().compareTo( rhs.getKeywordName() );

    }

    @Override
    public int hashCode() {

        return getKeywordName().hashCode();

    }

    @Override
    public boolean equals( @Nullable final Object rhs ) {

        return rhs instanceof ObtuseKeywordInfo && getKeywordName().equals( ( (ObtuseKeywordInfo)rhs ).getKeywordName() );

    }

}
