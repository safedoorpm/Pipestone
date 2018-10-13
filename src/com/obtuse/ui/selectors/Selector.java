/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.GenericTag;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.function.Consumer;

/**
 Describe a combo-box's alternatives (choices).
 <p>Instances of this class are immutable if frozen.
 An instance of this class cannot be used to create a {@link JComboBox}{@code <}{@link Alternative}{@code >}
 until either {@link #freeze()} or {@link #keepMutable(boolean)} has been called.</p>
 */

public class Selector<K extends Alternative> implements Comparable<Selector>, Iterable<K> {

//    private static final GenericTag SELECTOR_TAGS_CATEGORY = GenericTag.createNewTagCategory( Selector.class.getCanonicalName() );

    private static final SortedMap<String, ? super Selector> s_allKnownSelectors = new TreeMap<>();

    private final String _selectorsLabel;
    private final GenericTag.GenericTagCategory _selectorsTagCategory;

    private final List<K> _knownAlternativesList = new ArrayList<>();
    private final SortedMap<GenericTag,Alternative> _knownAlternativesByTagMap = new TreeMap<>();
    private final SortedMap<String,Alternative> _knownAlternativesByLabelMap = new TreeMap<>();

    private boolean _mutable;
    private boolean _mutabilityEstablished = false;

    /**
     Create a new selector.
     @param selectorsLabel the label that should appear when this selector is presented to the human.
     @param thisSelectorsName this selector's name.
     The name must match the {@link GenericTag#VALID_CATEGORY_TAG} pattern.
     @throws IllegalArgumentException if there is already a selector with the specified name or if the
     specified name does not match the {@link GenericTag#VALID_CATEGORY_TAG} pattern.
     */

    public Selector( final @NotNull String selectorsLabel, final @NotNull String thisSelectorsName ) {
        super();

        if ( s_allKnownSelectors.containsKey( thisSelectorsName ) ) {

            throw new IllegalArgumentException(
                    "Selector:  duplicate selector tag category name " +
                    ObtuseUtil.enquoteToJavaString( thisSelectorsName )
            );

        }

        _selectorsTagCategory = GenericTag.maybeAllocTagCategory( thisSelectorsName );

        this._selectorsLabel = selectorsLabel;
//        this._selectorsTagName = thisSelectorsName;

        s_allKnownSelectors.put( thisSelectorsName, this );

    }

    public void freeze() {

        keepMutable( "freeze()", false );

    }

    public void keepMutable( final boolean mutable ) {

        keepMutable( "keepMutable( " + mutable + " )", mutable );

    }

    public void keepMutable( final @NotNull String toString, final boolean mutable ) {

        if ( _mutabilityEstablished ) {

            throw new IllegalArgumentException(
                    "Selector." + toString + ":  " +
                    "mutability cannot be changed once established " +
                    "(we are already established as " + getMutabilityString() + ")"
            );

        }

        _mutable = mutable;
        _mutabilityEstablished = true;

    }

    @NotNull
    public String getMutabilityString() {

        return _mutable ? "mutable" : "frozen";

    }

    public boolean isMutable() {

        return _mutable;

    }

    public boolean isFrozen() {

        return !_mutable;

    }

    public boolean isMutabilityEstablished() {

        return _mutabilityEstablished;

    }

    @NotNull
    public GenericTag.GenericTagCategory getSelectorsTagCategory() {

        return _selectorsTagCategory;

    }

    @NotNull
    public String getSelectorsTagName() {

        return _selectorsTagCategory.getTagName();

    }

    public Optional<Alternative> findAlternativeByTag( final @NotNull GenericTag tag ) {

        return Alternative.findChoiceByKey( tag );

    }

//    public Optional<Alternative> findChoiceByLabel( final @NotNull String label ) {
//
//        return Alternative.findChoiceByKey( Alternative.makeUniqueKey( this, label ) );
//
//    }

    public void addAlternative( final @NotNull K alternative ) {

        if ( _knownAlternativesByTagMap.containsKey( alternative.getTag() ) ) {

            throw new IllegalArgumentException( "Selector.addAlternative(key=" + alternative.getTag() + ") already exists" );

        }

        if ( _knownAlternativesByLabelMap.containsKey( alternative.toString() ) ) {

            throw new IllegalArgumentException( "Selector.addAlternative(label=" + ObtuseUtil.enquoteToJavaString( alternative.toString() ) + ") already exists" );

        }

        _knownAlternativesList.add( alternative );
        _knownAlternativesByTagMap.put( alternative.getTag(), alternative );
        _knownAlternativesByLabelMap.put( alternative.toString(), alternative );

    }

    /**
     Create and return a {@link Vector}{@code <Alternative>} containing this selector's alternatives.
     <p>Note that each call to this method returns a newly created {@code Vector<Alternative>}</p> containing this selector's alternatives.
     This allows the call to do whatever they like with the result including, in particular, use it to create a {@link ComboBoxModel <Alternative>}.
     @return a newly created {@code Vector<Alternative>}</p> containing this selector's alternatives.
     */

    public Vector<K> createAlternativesVector() {

        return new Vector<>( _knownAlternativesList );

    }

    public SortedMap<GenericTag, Alternative> getAlternativesMap() {

        return Collections.unmodifiableSortedMap( _knownAlternativesByTagMap );

    }

    public List<K> getAlternatives() {

        return Collections.unmodifiableList( _knownAlternativesList );

    }

    @NotNull
    public Optional<K> findUnspecifiedAlternative() {

        if ( _knownAlternativesList.isEmpty() ) {

            throw new IllegalArgumentException( "Selector.findUnspecifiedAlternative:  no alternatives provided" );

        }

        K unspecifiedAlternative = null;

        for ( K alternative : _knownAlternativesList ) {

            if ( alternative.isUnspecifiedAlternative() ) {

                if ( unspecifiedAlternative == null ) {

                    unspecifiedAlternative = alternative;

                } else {

                    throw new IllegalArgumentException( "WikiTreeDbComboBoxSelector.findUnspecifiedAlternative:  more than one unspecified alternative" );

                }

            }

        }

        return Optional.ofNullable( unspecifiedAlternative );

    }

    @NotNull
    public K getUnspecifiedAlternative() {

        @NotNull Optional<K> optUnspecifiedAlternative = findUnspecifiedAlternative();
        if ( optUnspecifiedAlternative.isPresent() ) {

            return optUnspecifiedAlternative.get();

        } else {

            throw new IllegalArgumentException(
                    "WikiTreeDbComboBoxSelector.getUnspecifiedAlternative:  no unspecified alternative"
            );

        }

    }

    @NotNull
    public K getInitialAlternative() {

        if ( _knownAlternativesList.isEmpty() ) {

            throw new IllegalArgumentException( "Selector.findInitialAlternative:  no alternatives provided" );

        }

        Optional<K> optUnspecifiedAlternative = findUnspecifiedAlternative();
        return optUnspecifiedAlternative.orElseGet( () -> _knownAlternativesList.get( 0 ) );

    }

    public static Optional<Selector<Alternative>> findSelector( final @NotNull String selectorTag ) {

        @SuppressWarnings( "unchecked" )
        Selector<Alternative> selector = (Selector<Alternative>)s_allKnownSelectors.get( selectorTag );

        return Optional.ofNullable( selector );

    }

    public static Selector<? extends Alternative> maybeCreateSelector( final @NotNull String selectorLabel, final @NotNull String selectorTag ) {

        GenericTag.checkTagNameValid( "Selector.maybeCreateSelector", selectorTag );
        Optional<Selector<Alternative>> optSelector = findSelector( selectorTag );

        return optSelector.orElseGet( () -> new Selector<>( selectorLabel, selectorTag ) );

    }

    public String toString() {

        String toString = this._selectorsLabel + " | tag=" + _selectorsTagCategory;
        return toString;

    }

    public int compareTo( final @NotNull Selector rhs ) {

        return getSelectorsTagName().compareTo( rhs.getSelectorsTagName() );

    }

    public boolean equals( final Object rhs ) {

        return rhs instanceof Selector && compareTo( (Selector)rhs ) == 0;

    }

    public int hashCode() {

        return this.getSelectorsTagName().hashCode();

    }

    /**
     Find an alternative within this selector's set of alternatives.
     <p>This method is typically used to determine if an {@link Alternative} instance that the caller happens to have
     a reference to is one of the {@code Alternative}s in this selector.</p>
     @param alternative the {@link Alternative} in question.
     @return {@code true} if the {@code alternative} parameter is non-null and references an {@code Alternative}
     instance which is in this selector, {@code false} otherwise.
     */

    public boolean hasAlternative( final @Nullable Alternative alternative ) {

        return alternative != null && findAlternativeByTag( alternative.getTag() ).isPresent();

    }

    @NotNull
    public Optional<Alternative> findAlternativeByLabel( final @NotNull String label ) {

        Optional<Alternative> optAlternative = Optional.ofNullable( _knownAlternativesByLabelMap.get( label ) );
        return optAlternative;

    }

//    @NotNull
//    public Optional<Alternative> findAlternativeByTag( final @NotNull GenericTag tag ) {
//
//        Optional<Alternative> optAlternative = Optional.ofNullable( _knownAlternativesByTagMap.get( tag ) );
//        return optAlternative;
//
//    }

    /**
     Create a simple {@link JCheckBox}{@code <}{@link Alternative}{@code >} with its current selection
     set to the first element.
     The combo-box will be empty (have no alternatives) if this selector has no {@link Alternative} instances.
     @return a possibly empty {@code JComboBox<Alternative>}.
     */

    @NotNull
    public JComboBox<K> createComboBox() {

        return createComboBox( null );

    }

    /**
     Create a simple {@link JComboBox}{@code <}{@link Alternative}{@code >}.
     @param currentAlternative the alternative that should be the combo-box's current selection.
     The first alternative is used if {@code currentAlternative} is {@code null} or if there are no alternatives.
     @return the combo-box.
     */

    @NotNull
    public JComboBox<K> createComboBox( final @Nullable Alternative currentAlternative ) {

        if ( isMutabilityEstablished() ) {

            Vector<K> alternativesVector = createAlternativesVector();
            JComboBox<K> comboBox = new JComboBox<>( alternativesVector );

            if ( hasAlternative( currentAlternative ) ) {

                comboBox.setSelectedItem( currentAlternative );

            } else if ( !alternativesVector.isEmpty() ) {

                comboBox.setSelectedIndex( 0 );

            }

            return comboBox;

        }

        throw new HowDidWeGetHereError(
                "Selector(" + getSelectorsTagName() + "):  " +
                "mutability must be established before a JComboBox is created"
        );

    }

    /**
     Get an iterator that goes through this instance's {@link Alternative} instances in the order that they
     were added to this instance.
     <p>The returned list iterator does not support removals or additions to the list
     (you are iterating through an immutable copy of this instance's known alternatives).</p>
     @return an iterator that goes through this instance's {@link Alternative} instances in the order that they
     were added to this instance.
     */

    @NotNull
    @Override
    public Iterator<K> iterator() {

        return getAlternatives().iterator();

    }

    @Override
    public void forEach( final Consumer<? super K> action ) {

        _knownAlternativesList.forEach( action );

    }

    @Override
    public Spliterator<K> spliterator() {

        return _knownAlternativesList.spliterator();

    }

}
