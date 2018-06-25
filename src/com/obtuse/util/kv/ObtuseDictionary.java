/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.kv;

import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.UniqueEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 A dictionary of keyword-value pairs.
 <p>Instances of this class are immutable.</p>
 */

public class ObtuseDictionary extends UniqueEntity implements Iterable<ObtuseKeywordValue> {

    /**
     An unmodifiable mapping of {@link ObtuseKeyword}s to {@link ObtuseKeywordValue}s.
     */

    private final SortedMap<ObtuseKeyword, ObtuseKeywordValue> _dictionary;

    /**
     An unmodifiable mapping of the string values of keywords to {@link ObtuseKeywordValue}s.
     */

    private final SortedMap<String, ObtuseKeyword> _keywordStringMapping;

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

        SortedMap<ObtuseKeyword, ObtuseKeywordValue> tmpDictionary = new TreeMap<>();
        SortedMap<String,ObtuseKeywordValue> keywordStringMapping = new TreeMap<>();

        for ( ObtuseKeywordValue keywordValue : keywordValues ) {

            if ( validKeywords == null || validKeywords.contains( keywordValue.getKeywordName() ) ) {

                if ( tmpDictionary.containsKey( keywordValue ) ) {

                    throw new IllegalArgumentException( "ObtuseDictionary:  duplicate keyword \"" + keywordValue + "\"" );

                }

                ObtuseKeyword keyword = new ObtuseKeyword( keywordValue.getKeywordName() );
                tmpDictionary.put( keyword, keywordValue );
                keywordStringMapping.put(
                        keywordValue.getKeywordName(),
                        keywordValue
                );

            } else {

                throw new IllegalArgumentException( "ObtuseDictionary:  unknown keyword \"" + keywordValue + "\"" );

            }

        }

        _dictionary = Collections.unmodifiableSortedMap( tmpDictionary );
        _keywordStringMapping = Collections.unmodifiableSortedMap( keywordStringMapping );

    }

    /**
     Create a new dictionary which contains this instance's keywords with non-null values and a second dictionary's keywords
     with non-null values. Keywords in this dictionary with non-null values take precedence.
     If there are any keywords in the specified {@code requiredKeywords} set for which neither this dictionary nor
     the default values dictionary have entries with non-null values then an {@link IllegalArgumentException} is thrown.
     If a keyword exists in one or both dictionaries but does not have a non-null value in either dictionary then it is treated as if it doesn't
     exist (i.e. it does not appear in the resulting keyword and is considered to be missing if it is in the {@code requiredKeywords} set).
     @param who the caller's name (used in debug logging and in constructing the {@link IllegalArgumentException} should it be necessary to throw one).
     @param requiredKeywords the {@link Set}{@code <}{@link ObtuseKeyword}{@code >} of keywords which will appear in the returned dictionary.
     Provide an empty set if there are no required keywords.
     @param ignoreOptionalKeywords if {@code true} then any keywords that are not in the {@code requiredKeywords} set are discarded.
     @param secondDictionary the second {@link ObtuseDictionary}.
     @return a {@link Collection} of {@link ObtuseKeywordValue} representing the resulting merger.
     @throws IllegalArgumentException if it is not possible to construct a dictionary containing at least the specified required keywords such that each
     of the keywords have non-null values.
     */

    @NotNull
    public Collection<ObtuseKeywordValue> layerOver(
            @NotNull final String who,
            @NotNull final Set<ObtuseKeyword> requiredKeywords,
            final boolean ignoreOptionalKeywords,
            @NotNull final ObtuseDictionary secondDictionary
    ) {

        return layerOver( who, requiredKeywords, ignoreOptionalKeywords, secondDictionary._dictionary );

    }

    /**
     Create a new dictionary which contains this instance's keywords with non-null values and a second dictionary's keywords
     with non-null values. Keywords in this dictionary with non-null values take precedence.
     If there are any keywords in the specified {@code requiredKeywords} set for which neither this dictionary nor
     the default values dictionary have entries with non-null values then an {@link IllegalArgumentException} is thrown.
     If a keyword exists in one or both dictionaries but does not have a non-null value in either dictionary then it is treated as if it doesn't
     exist (i.e. it does not appear in the resulting keyword and is considered to be missing if it is in the {@code requiredKeywords} set).
     @throws IllegalArgumentException if it is not possible to construct a dictionary containing at least the specified required keywords such that each
     of the keywords have non-null values.
     @param who the caller's name (used in debug logging and in constructing the {@link IllegalArgumentException} should it be necessary to throw one).

     @param requiredKeywords the {@link Set}{@code <}{@link ObtuseKeyword}{@code >} of keywords which will appear in the returned dictionary.
 Provide an empty set if there are no required keywords.
     @param ignoreOptionalKeywords if {@code true} then any keywords that are not in the {@code requiredKeywords} set are discarded.
     @param secondMapping the second {@link ObtuseDictionary}.
     @return a {@link Collection} of {@link ObtuseKeywordValue} representing the resulting merger.
     */

    public Collection<ObtuseKeywordValue> layerOver(
            @NotNull final String who,
            @NotNull final Set<ObtuseKeyword> requiredKeywords,
            final boolean ignoreOptionalKeywords,
            @NotNull final SortedMap<ObtuseKeyword, ObtuseKeywordValue> secondMapping
    ) {

        SortedMap<String,ObtuseKeywordValue> mergedKeywordMap = new TreeMap<>();

        for ( ObtuseKeywordValue ourKeywordValue : _dictionary.values() ) {

            // If we are to only include keywords in the required set then ignore any keywords that are not in the required set.

            if ( ignoreOptionalKeywords && !requiredKeywords.contains( ourKeywordValue ) ) {

                continue;

            }

            // Get the keyword's value.
            // Use this dictionary's value if it is non-null.
            // Otherwise, use the second dictionary's value if a keyword by the same name exists and is non-null.
            // Pretend that the keyword doesn't exist if neither this dictionary nor the second dictionary provide a non-null value.

            String keywordValue = ourKeywordValue.getValue();
            if ( keywordValue == null ) {

                ObtuseKeywordValue secondDictionarysKeyword = secondMapping.get( ourKeywordValue );
                if ( secondDictionarysKeyword != null ) {

                    if ( secondDictionarysKeyword.hasNullValue() ) {

                        continue;

                    }

                    mergedKeywordMap.put( ourKeywordValue.getKeywordName(), secondDictionarysKeyword );

                }

            } else {

                mergedKeywordMap.put( ourKeywordValue.getKeywordName(), ourKeywordValue );

            }

        }

        // Now we need to add any keywords from the second dictionary that aren't already in the merged map.
        // We continue to ignore optional keywords if we've been told to only include required keywords.

        for ( ObtuseKeywordValue secondKeywordValue : secondMapping.values() ) {

            // If we are to only include keywords in the required set then ignore any keywords that are not in the required set.

            if ( ignoreOptionalKeywords && !requiredKeywords.contains( secondKeywordValue ) ) {

                continue;

            }

            // Ignore any keywords that are already in our merged map.

            if ( mergedKeywordMap.containsKey( secondKeywordValue.getKeywordName() ) ) {

                continue;

            }

            // Get the keyword's value and put it into the merged map if it is non-null.

            String keywordValue = secondKeywordValue.getValue();
            if ( keywordValue != null ) {

                mergedKeywordMap.put( secondKeywordValue.getKeywordName(), secondKeywordValue );

            }

        }

        // Make sure that we've got all of the required keywords.

        for ( ObtuseKeyword requiredKeyword : requiredKeywords ) {

            if ( !mergedKeywordMap.containsKey( requiredKeyword.getKeywordName() ) ) {

                // Neither we or the second dictionary have a value for a required keyword (oops).

                throw new IllegalArgumentException(
                        "ObtuseDictionary:  required keyword " + ObtuseUtil.enquoteJavaObject( requiredKeyword.getKeywordName() ) +
                        " not found in either dictionary"
                );

            }

        }

        return mergedKeywordMap.values();

    }

    /**

     Create a new dictionary which contains this instance's keywords with non-null values and a separate mapping's keywords
     with non-null values. Keywords in the <b><u>separate mapping</u></b> with non-null values take precedence.
     <p>This is exactly equivalent to
     <blockquote>{@code new ObtuseDictionary( separateMapping.values() ).layerOver( who, requiredKeywords, ignoreOptionalKeywords, this )}</blockquote></p>
     @param who the caller's name (used in debug logging and in constructing the {@link IllegalArgumentException} should it be necessary to throw one).
     @param requiredKeywords the {@link Set}{@code <}{@link ObtuseKeyword}{@code >} of keywords which must appear in the returned dictionary.
     Provide an empty set if there are no required keywords.
     @param ignoreOptionalKeywords if {@code true} then any keywords that are not in the {@code requiredKeywords} set are discarded.
     @param separateMapping the separate mapping.
     @return a {@link Collection} of {@link ObtuseKeywordValue} representing the resulting merger.
     @throws IllegalArgumentException if it is not possible to construct a dictionary containing at least the specified required keywords such that each
     of the keywords have non-null values.
     */

    @NotNull
    public Collection<ObtuseKeywordValue> layerUnder(
            @NotNull final String who,
            @NotNull final Set<ObtuseKeyword> requiredKeywords,
            final boolean ignoreOptionalKeywords,
            @NotNull final SortedMap<ObtuseKeyword, ObtuseKeywordValue> separateMapping
    ) {

        return new ObtuseDictionary( separateMapping.values() ).layerOver( who, requiredKeywords, ignoreOptionalKeywords, _dictionary );

    }

    /**

     Create a new dictionary which contains this instance's keywords with non-null values and a separate dictionary's keywords
     with non-null values. Keywords in the <b><u>other dictionary</u></b> with non-null values take precedence.
     <p>This is exactly equivalent to
     <blockquote>{@code secondDictionary.layerOver( who, requiredKeywords, ignoreOptionalKeywords, this )}</blockquote></p>
     @param who the caller's name (used in debug logging and in constructing the {@link IllegalArgumentException} should it be necessary to throw one).
     @param requiredKeywords the {@link Set}{@code <}{@link ObtuseKeyword}{@code >} of keywords which must appear in the returned dictionary.
     Provide an empty set if there are no required keywords.
     @param ignoreOptionalKeywords if {@code true} then any keywords that are not in the {@code requiredKeywords} set are discarded.
     @param separateDictionary the second {@link ObtuseDictionary}.
     @return a {@link Collection} of {@link ObtuseKeywordValue} representing the resulting merger.
     @throws IllegalArgumentException if it is not possible to construct a dictionary containing at least the specified required keywords such that each
     of the keywords have non-null values.
     */

    @NotNull
    public Collection<ObtuseKeywordValue> layerUnder(
            @NotNull final String who,
            @NotNull final Set<ObtuseKeyword> requiredKeywords,
            final boolean ignoreOptionalKeywords,
            @NotNull final ObtuseDictionary separateDictionary
    ) {

        return separateDictionary.layerOver( who, requiredKeywords, ignoreOptionalKeywords, _dictionary );

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

    @NotNull
    public SortedMap<ObtuseKeyword,ObtuseKeywordValue> getDictionaryMap() {

        return Collections.unmodifiableSortedMap( _dictionary );

    }

    /**
     Get the {@link String} value of a keyword.
     @param keywordString the keyword as a {@link String}.
     @return an {@link Optional}{@code <String>} which contains the keyword's {@code String} value if it exists; {@code null} otherwise.
     */

    @NotNull
    public Optional<String> getStringValue( final @NotNull String keywordString ) {

        ObtuseKeyword keyword = _keywordStringMapping.get( keywordString );
        if ( keyword == null ) {

            return Optional.empty();

        }

        return getStringValue( keyword );

    }

    @NotNull
    private Optional<String> getStringValue( final ObtuseKeyword keyword ) {

        Optional<ObtuseKeywordValue> optKeywordValue = getKeywordValue( keyword );

        if ( optKeywordValue.isPresent() ) {

            ObtuseKeywordValue keywordValue = optKeywordValue.get();

            return Optional.ofNullable( keywordValue.getValue() );

        }

        return Optional.empty();

    }

    @NotNull
    public Optional<ObtuseKeywordValue> getKeywordValue( final @NotNull String keywordString ) {

        ObtuseKeyword keyword = _keywordStringMapping.get( keywordString );
        if ( keyword == null ) {

            return Optional.empty();

        }

        return getKeywordValue( keyword );

    }

    @NotNull
    public Optional<ObtuseKeywordValue> getKeywordValue( final @NotNull ObtuseKeyword keyword ) {

        ObtuseKeywordValue rval = _dictionary.get( keyword );

        return Optional.ofNullable( rval );

    }

    public String toString() {

        return _dictionary.toString();

    }

}
