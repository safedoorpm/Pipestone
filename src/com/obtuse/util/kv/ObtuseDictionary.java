/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.kv;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 A dictionary of keyword-value pairs.
 <p>Instances of this class are immutable.</p>
 */

public class ObtuseDictionary implements Iterable<ObtuseKeywordValue> {

    private final SortedMap<ObtuseKeyword, ObtuseKeywordValue> _dictionary = new TreeMap<>();
    private final SortedMap<String, ObtuseKeyword> _keywordStringMapping = new TreeMap<>();

    /**
     Create a dictionary of keyword-value pairs.
     See {@link #ObtuseDictionary(Set, Collection)} for a constructor that only accepts keywords from caller-specified set of keywords.
     @param keywordValues the keyword-value pairs which are to populate this dictionary.
     @throws IllegalArgumentException if a keyword has the wrong syntax (see {@link ObtuseKeyword#VALID_KEYWORD_PATTERN}
     for more info).
     */

    public ObtuseDictionary( final @NotNull Collection<ObtuseKeywordValue> keywordValues ) {
        this( null, keywordValues );
    }

    /**
     Create a dictionary of keyword-value pairs.

     @param validKeywords an optional set containing the valid keyword values.
     If {@code validKeywords} is {@code null} then any keyword is valid.
     Attempts to add a keyword that is not in this set will earn you an {@link IllegalArgumentException} badge.
     Note that if {@code validKeywords} refers to an empty set then NO keywords are valid.
     @param keywordValues the keyword-value pairs which are to populate this dictionary.
     @throws IllegalArgumentException if a keyword has the wrong syntax (see {@link ObtuseKeyword#VALID_KEYWORD_PATTERN}
     for more info).
     */

    public ObtuseDictionary( @Nullable final Set<String> validKeywords, final @NotNull Collection<ObtuseKeywordValue> keywordValues ) {

        super();

        for ( ObtuseKeywordValue keywordValue : keywordValues ) {

            if ( validKeywords == null || validKeywords.contains( keywordValue.getKeywordName() ) ) {

                if ( _dictionary.containsKey( keywordValue ) ) {

                    throw new IllegalArgumentException( "ObtuseDictionary:  duplicate keyword \"" + keywordValue + "\"" );

                }

                _dictionary.put( new ObtuseKeyword( keywordValue.getKeywordName() ), keywordValue );
                _keywordStringMapping.put(
                        keywordValue.getKeywordName(),
                        keywordValue
                );

            } else {

                throw new IllegalArgumentException( "ObtuseDictionary:  unknown keyword \"" + keywordValue + "\"" );

            }

        }

    }

    /**
     Get an iterator for this dictionary.

     @return an iterator for this dictionary.
     */

    @NotNull
    @Override
    public Iterator<ObtuseKeywordValue> iterator() {

        return _dictionary.values().iterator();

    }

    @Override
    public void forEach( final Consumer<? super ObtuseKeywordValue> action ) {

        _dictionary.values().forEach( action );

    }

    @Override
    public Spliterator<ObtuseKeywordValue> spliterator() {

        return _dictionary.values().spliterator();

    }

    @SuppressWarnings("unused")
    public String getValue( final @NotNull String keywordString ) {

        ObtuseKeyword keywordInfo = _keywordStringMapping.get( keywordString );
        return getValue( keywordInfo );

    }

    /**
     Get the value of a particular keyword.

     @param keywordInfo the keyword of interest.
     @return the value of the keyword or {@code "<<unknown>>"}
     if there is no keyword-value pair corresponding to the requested keyword in this map.
     */

    public String getValue( @Nullable final ObtuseKeyword keywordInfo ) {

        ObtuseKeywordValue keywordValue = _dictionary.get( keywordInfo );
        if ( keywordValue == null ) {

            return "<<unknown>>";

        } else {

            return keywordValue.getValue();

        }

    }

    public String toString() {

        return _dictionary.toString();

    }

}
