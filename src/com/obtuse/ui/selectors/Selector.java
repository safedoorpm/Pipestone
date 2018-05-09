/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 Describe a combo-box's choices.
 <p>Instances of this class MUST be immutable.</p>
 */

public class Selector implements Comparable<Selector> {

    private static final SortedMap<String, Selector> s_allKnownSelectors = new TreeMap<>();

    public final String selectorName;
    public final String selectorTag;

    private final List<Alternative> _knownChoicesList = new ArrayList<>();
    private final SortedMap<String,Alternative> _knownChoicesMap = new TreeMap<>();

    public Selector( final @NotNull String selectorName, final @NotNull String selectorTag ) {

        super();

        if ( s_allKnownSelectors.containsKey( selectorTag ) ) {

            throw new IllegalArgumentException( "Selector:  duplicate selector tag " + ObtuseUtil.enquoteToJavaString( selectorTag ) );

        }

        this.selectorName = selectorName;
        this.selectorTag = selectorTag;

        s_allKnownSelectors.put( selectorTag, this );

    }

    public void addChoice( final @NotNull Alternative choice ) {

        if ( _knownChoicesMap.containsKey( choice.getUniqueKey() ) ) {

            throw new IllegalArgumentException( "Selector.addChoice(" + choice.getUniqueKey() + ") already exists" );

        }

        _knownChoicesList.add( choice );
        _knownChoicesMap.put( choice.getUniqueKey(), choice );

    }

    public SortedMap<String,Alternative> getChoicesMap() {

        return Collections.unmodifiableSortedMap( _knownChoicesMap );

    }

    public List<Alternative> getChoices() {

        return Collections.unmodifiableList( _knownChoicesList );

    }

    @NotNull
    public Optional<Alternative> findUnspecifiedChoice() {

        if ( _knownChoicesList.isEmpty() ) {

            throw new IllegalArgumentException( "Selector.findUnspecifiedChoice:  no choices provided" );

        }

        Alternative unspecifiedChoice = null;

        for ( Alternative choice : _knownChoicesList ) {

            if ( choice.isUnspecifiedChoice() ) {

                if ( unspecifiedChoice == null ) {

                    unspecifiedChoice = choice;

                } else {

                    throw new IllegalArgumentException( "WikiTreeDbComboBoxSelector.findUnspecifiedChoice:  more than one unspecified choice" );

                }

            }

        }

        return Optional.ofNullable( unspecifiedChoice );

    }

    @NotNull
    public Alternative getInitialChoice() {

        if ( _knownChoicesList.isEmpty() ) {

            throw new IllegalArgumentException( "Selector.findInitialChoice:  no choices provided" );

        }

        Optional<Alternative> optUnspecifiedChoice = findUnspecifiedChoice();
        return optUnspecifiedChoice.orElseGet( () -> _knownChoicesList.get( 0 ) );

    }

    public static Optional<Selector> findSelector( final @NotNull String selectorTag ) {

        Selector selector = s_allKnownSelectors.get( selectorTag );

        return Optional.ofNullable( selector );

    }

    public static Selector maybeCreateSelector( final @NotNull String selectorName, final @NotNull String selectorTag ) {

        Optional<Selector> optSelector = findSelector( selectorTag );

        return optSelector.orElseGet( () -> new Selector( selectorName, selectorTag ) );

    }

    public String toString() {

        return this.selectorName;

    }

    public int compareTo( final @NotNull Selector rhs ) {

        return this.selectorTag.compareTo( rhs.selectorTag );

    }

    public boolean equals( final Object rhs ) {

        return rhs instanceof Selector && compareTo( (Selector)rhs ) == 0;

    }

    public int hashCode() {

        return this.selectorTag.hashCode();

    }

}
