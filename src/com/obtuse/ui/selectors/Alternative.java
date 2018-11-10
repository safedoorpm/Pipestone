package com.obtuse.ui.selectors;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.GenericTag;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.holders.GowingStringHolder;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A simple yet structured alternative to put into a {@link Selector} and eventually into a combo-box.
 <p>Instances of this class are immutable.</p>
 */

public class Alternative implements Comparable<Alternative>, GowingPackable {

//    public static final GenericTag.GenericTagCategory ALTERNATIVE_TAG_CATEGORY =
//            GenericTag.createNewTagCategory( Alternative.class.getCanonicalName() );

    public interface AlternativeFactory<K extends Alternative> {

        K createInstance(
                final @NotNull Selector<K> selector,
                final @NotNull GenericTag tag,
                final @NotNull String toString
        );

    }

    private static final SortedMap<GenericTag, Alternative> s_availableChoicesByKey = new TreeMap<>();

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( Alternative.class );
    private static final int VERSION = 1;

    private static final EntityName G_SELECTOR_TAG = new EntityName( "_vt" );
    private static final EntityName G_TO_STRING = new EntityName( "_ts" );
    private static final EntityName G_CHOICE_TAG_STRING = new EntityName( "_n" );

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
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        )
                throws GowingUnpackingException {

            String selectorTag = bundle.MandatoryStringValue( G_SELECTOR_TAG );
            Optional<Selector<Alternative>> optSelector = Selector.findSelector( selectorTag );
            if ( !optSelector.isPresent() ) {

                throw new IllegalArgumentException(
                        "Alternative.factory.createEntity:  unknown selector " + ObtuseUtil.enquoteToJavaString( selectorTag )
                );

            }

            String stringTag = bundle.MandatoryStringValue( G_CHOICE_TAG_STRING );
            Selector selector = optSelector.get();
            GenericTag tag = GenericTag.alloc( selector.getSelectorsTagCategory(), stringTag );

            Optional<Alternative> optChoice = findChoiceByKey( tag );

            if ( optChoice.isPresent() ) {

                return optChoice.get();

            } else {

                throw new IllegalArgumentException(
                        "Alternative.createEntity:  unknown tag " +
                        ObtuseUtil.enquoteJavaObject( tag )
                );

            }

        }

    };

    private final GowingInstanceId _instanceId = new GowingInstanceId( GowingPackable.class );
    private final Selector _selector;
    private final String _toString;
    @NotNull
    private final GenericTag _tag;
//    private final String _uniqueKey;

    private boolean _unspecifiedChoice = false;

    /**
     Create an instance which has a tag which might be different than its {@link #toString()} value.
     @throws IllegalArgumentException if this JVM already has an instance of this class with the specified {@code name} and {@code toString} values.
     @param selector the instance's {@link Selector}.
     @param tag the instance's tag.
     @param toString the value to be returned by this instance's {@code toString()} method.
     */

    protected Alternative( final Selector<Alternative> selector, final @NotNull GenericTag tag, final @NotNull String toString ) {
        super();

        _selector = selector;
        _tag = tag;
        _toString = toString;
//        _uniqueKey = makeUniqueKey( selector, tag );

        if ( findChoiceByKey( _tag ).isPresent() ) {

            throw new IllegalArgumentException(
                    "Alternative:  choice with unique key " + ObtuseUtil.enquoteJavaObject( _tag ) + " already exists"
            );

        }

        s_availableChoicesByKey.put( _tag, this );

        selector.addAlternative( this );

        ObtuseUtil.doNothing();

    }

//    /**
//     ### bad idea - the toString() value of this instance is what will appear in comboBoxes, pick-lists
//     ### and such. It HAS TO BE HUMAN READABLE which makes it a very poor choice for a tag.
//     Create an instance which has a name which is equal to its {@link #toString()} value.
//     @param selector the instance's {@link Selector}.
//     @param tag the instance's tag and the value returned by its {@code toString()} method.
//     @throws IllegalArgumentException if this JVM already has an instance of this class with
//     a name and {@code toString()} value equal to the specified {@code name}.
//     */
//
//    public Alternative( final @NotNull Selector selector, final @NotNull String tag ) {
//        this( selector, tag, tag );
//    }

    public static Optional<Alternative> findChoiceByKey( final @NotNull GenericTag tag ) {

        Alternative choice = s_availableChoicesByKey.get( tag );

        Optional<Alternative> rval = Optional.ofNullable( choice );

        if ( !rval.isPresent() ) {

          ObtuseUtil.doNothing();

        }

        return rval;

    }

    @SuppressWarnings("unused")
    @NotNull
    private static String describeInstance( final String key, final Alternative tmpChoice ) {

        return "    selector=" + ObtuseUtil.enquoteJavaObject( tmpChoice.getSelector() ) +
               ", key=" + ObtuseUtil.enquoteToJavaString( key ) +
               ", uKey=" + ObtuseUtil.enquoteJavaObject( tmpChoice.getTag() ) +
               ", toString=" + ObtuseUtil.enquoteToJavaString( tmpChoice.toString() );

    }

    public static Alternative maybeCreateChoice(
            final @NotNull Selector<Alternative> selector,
            final @NotNull String toString
    ) {

        return maybeCreateChoice( selector, toString, toString );

    }

    public static <K extends Alternative> K maybeCreateChoice(
            final @NotNull Selector<K> selector,
            final @NotNull String toString,
            final @NotNull AlternativeFactory<K> factory
    ) {

        return maybeCreateChoice( selector, toString, toString, factory );

    }

    public static Alternative maybeCreateChoice(
            final @NotNull Selector<Alternative> selector,
            final @NotNull String stringTag,
            final @NotNull String toString
    ) {

        GenericTag tag = GenericTag.alloc( selector.getSelectorsTagCategory(), stringTag );
        Optional<Alternative> optChoice = selector.findAlternativeByTag( tag );
        return optChoice.orElseGet( () -> new Alternative( selector, tag, toString ) );

    }

    public static Alternative maybeCreateChoice(
            final @NotNull Selector<Alternative> selector,
            final @NotNull GenericTag stringTag,
            final @NotNull String toString
    ) {

        GenericTag tag = GenericTag.alloc( selector.getSelectorsTagCategory(), stringTag.getTagName() );
        Optional<Alternative> optChoice = selector.findAlternativeByTag( tag );
        return optChoice.orElseGet( () -> new Alternative( selector, tag, toString ) );

    }

    public static <K extends Alternative> K maybeCreateChoice(
            final @NotNull Selector<K> selector,
            final @NotNull String stringTag,
            final @NotNull String toString,
            final @NotNull AlternativeFactory<K> factory
    ) {

        GenericTag tag = GenericTag.alloc( selector.getSelectorsTagCategory(), stringTag );
        Optional<Alternative> optChoice = selector.findAlternativeByTag( tag );
        if ( optChoice.isPresent() ) {

            @SuppressWarnings("unchecked") K newInstance = (K)optChoice.get();
            return newInstance;

        } else {

            @SuppressWarnings("UnnecessaryLocalVariable") K newInstance = factory.createInstance( selector, tag, toString );
            return newInstance;

        }

    }

    public static void showKeys( final String who ) {

        Logger.logMsg( who + ":  showing Alternative keys" );

        for ( GenericTag tag : s_availableChoicesByKey.keySet() ) {

            Logger.logMsg( tag + "->" + s_availableChoicesByKey.get( tag ) );

        }

        ObtuseUtil.doNothing();

    }

    /**
     Get this instance's selector.
     @return this instance's {@code Selector}.
     */

    @NotNull
    public Selector getSelector() {

        return _selector;

    }

    public Alternative markAsUnspecifiedChoice() {

        if ( _unspecifiedChoice ) {

            throw new IllegalArgumentException( "Alternative.markUnspecifiedChoice:  instance already marked as the unspecified choice - " + this );

        }

        _unspecifiedChoice = true;

        return this;

    }

    public boolean isUnspecifiedAlternative() {

        return _unspecifiedChoice;

    }

    @NotNull
    public GenericTag getTag() {

        return _tag; // == null ? GenericTag.alloc( selector.getSelectorsTagCategory(), "<<unknown Alternative name>>" ) : _tag;

    }

    /**
     Return the human readable value of this alternative.
     <p>This is the value that appears in the comboBox or pick-list or whatever.</p>
     @return the human readable value of this alternative.
     */

    public String toString() {

//        String toString = ( _toString == null ? "<<unknown Alternative toString>>" : _toString ) + " | tag=" + _tag;

        if ( _toString == null ) {

            throw new HowDidWeGetHereError( "Alternative.toString():  _toString is null" );

        }

        return _toString;

    }

//    /**
//     Get this instance's unique key.
//     <p/>Each instance has a unique key which is equal to
//     <blockquote>{@code "" + this.}{@link #getSelector()}{@code  + ":" + this.}{@link #getTag()}</blockquote>
//     @return this instance's unique key.
//     */
//
//    public String getUniqueKey() {
//
//        return _uniqueKey;
//
//    }

//    /**
//     Construct a unique key which sorts by a Selector tag and then by a String tag.
//     @param selector the {@link Selector}.
//     @param tag the {@link String tag}.
//     @return the unique key resulting from
//     <blockquote>
//     {@code "" + selector.tag() + ":" + tag}
//     </blockquote>
//     */
//
//    public static String makeUniqueKey( final @NotNull Selector selector, final @NotNull GenericTag tag ) {
//
//        return selector.selectorsTagName + "(" + tag.getTagName() + ")";
//
//    }

    @Override
    public @NotNull GowingInstanceId getInstanceId() {

        return _instanceId;

    }

    /**
     Provide a simple way for a combo-box's {@link java.awt.event.ActionListener} to respond to this choice being selected.
     <p>Obviously, if this method's API isn't appropriate then the developer should either write their own
     combo-box choice class (either from scratch or derived from this class).</p>
     @return {@code true} if the doit method worked; {@code false} otherwise.
     The definition of <em>worked</em> is left as an exercise for the developer.
     @param actionEvent the {@link ActionEvent} passed to the {@code ActionListener}.
     */

    public boolean doit( @SuppressWarnings("unused") final @NotNull ActionEvent actionEvent ) {

        return true;

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

        bundle.addHolder( new GowingStringHolder( G_SELECTOR_TAG, _selector.getSelectorsTagCategory().getTagName(), true ) );
        bundle.addHolder( new GowingStringHolder( G_TO_STRING, _toString, true ) );
        bundle.addHolder( new GowingStringHolder( G_CHOICE_TAG_STRING, _tag.getTagName(), true ) );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) {

        return true;

    }

    @Override
    public int compareTo( @NotNull final Alternative rhs ) {

        return _tag.compareTo( rhs.getTag() );

    }

    @Override public int hashCode() {

        return _tag.hashCode();

    }

    @Override public boolean equals( final Object rhs ) {

        return rhs instanceof Alternative && compareTo( (Alternative)rhs ) == 0;

    }

}
