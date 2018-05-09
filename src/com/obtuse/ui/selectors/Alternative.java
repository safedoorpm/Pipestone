package com.obtuse.ui.selectors;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnpackingException;
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

    private static final SortedMap<String, Alternative> s_availableChoices = new TreeMap<>();

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( Alternative.class );
    private static final int VERSION = 1;

    private static final EntityName G_SELECTOR_TAG = new EntityName( "_vt" );
    private static final EntityName G_TO_STRING = new EntityName( "_ts" );
    private static final EntityName G_CHOICE_TAG = new EntityName( "_n" );

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
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        )
                throws GowingUnpackingException {

            String selectorTag = bundle.MandatoryStringValue( G_SELECTOR_TAG );
            Optional<Selector> optSelector = Selector.findSelector( selectorTag );
            if ( !optSelector.isPresent() ) {

                throw new IllegalArgumentException(
                        "Alternative.factory.createEntity:  unknown selector " + ObtuseUtil.enquoteToJavaString( selectorTag )
                );

            }

            String choiceTag = bundle.MandatoryStringValue( G_CHOICE_TAG );
            Selector selector = optSelector.get();
            String uniqueKey = makeUniqueKey( selector, choiceTag );

            Optional<Alternative> optChoice = findChoice( selector, choiceTag );

            if ( optChoice.isPresent() ) {

                return optChoice.get();

            } else {

                throw new IllegalArgumentException( "Alternative.createEntity:  unknown unique key " + ObtuseUtil.enquoteToJavaString( uniqueKey ) );

            }

        }

    };

    private final GowingInstanceId _instanceId = new GowingInstanceId( GowingPackable.class );
    private final Selector _selector;
    private final String _toString;
    private final String _tag;
    private final String _uniqueKey;

    private boolean _unspecifiedChoice = false;

    /**
     Create an instance which has a tag which might be different than its {@link #toString()} value.
     @param selector the instance's {@link Selector}.
     @param tag the instance's tag.
     @param toString the value to be returned by this instance's {@code toString()} method.
     @throws IllegalArgumentException if this JVM already has an instance of this class with the specified {@code name} and {@code toString} values.
     */

    public Alternative( final @NotNull Selector selector, final @NotNull String tag, final @NotNull String toString ) {
        super();

        _selector = selector;
        _tag = tag;
        _toString = toString;
        _uniqueKey = makeUniqueKey( selector, tag );

        if ( findChoice( _uniqueKey ).isPresent() ) {

            throw new IllegalArgumentException(
                    "Alternative:  choice with unique key " + ObtuseUtil.enquoteToJavaString( _uniqueKey ) + " already exists"
            );

        }

        s_availableChoices.put( _uniqueKey, this );

        selector.addChoice( this );

//        Logger.logMsg( "Alternative:  created " + describeInstance( _uniqueKey, this ) );

        ObtuseUtil.doNothing();

    }

    /**
     Create an instance which has a name which is equal to its {@link #toString()} value.
     @param selector the instance's {@link Selector}.
     @param tag the instance's tag and the value returned by its {@code toString()} method.
     @throws IllegalArgumentException if this JVM already has an instance of this class with
     a name and {@code toString()} value equal to the specified {@code name}.
     */

    public Alternative( final @NotNull Selector selector, final @NotNull String tag ) {
        this( selector, tag, tag );
    }

    public static Optional<Alternative> findChoice( final @NotNull Selector selector, final @NotNull String name ) {

        return findChoice( makeUniqueKey( selector, name ) );

    }

    public static Optional<Alternative> findChoice( final @NotNull String uniqueKey ) {

//        Logger.logMsg( "Alternative.findChoice:  looking for " + ObtuseUtil.enquoteToJavaString( uniqueKey ) );

        Alternative choice = s_availableChoices.get( uniqueKey );

        Optional<Alternative> rval = Optional.ofNullable( choice );

        if ( !rval.isPresent() ) {

//            Logger.logMsg( "Alternative.findChoice:  did not find " + ObtuseUtil.enquoteToJavaString( uniqueKey ) );
//            for ( String key : s_availableChoices.keySet() ) {
//
//                Alternative tmpChoice = s_availableChoices.get( key );
//                Logger.logMsg( describeInstance( key, tmpChoice ) );
//
//            }

            ObtuseUtil.doNothing();

        }

        return rval;

    }

    @NotNull
    private static String describeInstance( final String key, final Alternative tmpChoice ) {

        return "    selector=" + ObtuseUtil.enquoteJavaObject( tmpChoice.getSelector() ) +
               ", key=" + ObtuseUtil.enquoteToJavaString( key ) +
               ", uKey=" + ObtuseUtil.enquoteToJavaString( tmpChoice.getUniqueKey() ) +
               ", toString=" + ObtuseUtil.enquoteToJavaString( tmpChoice.toString() );

    }

    public static Alternative maybeCreateChoice(
            final @NotNull Selector selector,
            final @NotNull String tag,
            final @NotNull String toString
    ) {

        Optional<Alternative> optChoice = findChoice( selector, tag );
        return optChoice.orElseGet( () -> new Alternative( selector, tag, toString ) );

    }

    public static void showKeys( final String who ) {

        Logger.logMsg( who + ":  showing Alternative keys" );

        for ( String key : s_availableChoices.keySet() ) {

            Logger.logMsg( key + "->" + s_availableChoices.get( key ) );

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

            throw new IllegalArgumentException( "Alternative.markUnspecifiedChoice:  instance already marked as the unspecified choice" );

        }

        _unspecifiedChoice = true;

        return this;

    }

    public boolean isUnspecifiedChoice() {

        return _unspecifiedChoice;

    }

    @NotNull
    public String getTag() {

        return _tag == null ? "<<unknown Alternative name>>" : _tag;

    }

    public String toString() {

        return _toString == null ? "<<unknown Alternative toString>>" : _toString;

    }

    /**
     Get this instance's unique key.
     <p/>Each instance has a unique key which is equal to
     <blockquote>{@code "" + this.}{@link #getSelector()}{@code  + ":" + this.}{@link #getTag()}</blockquote>
     @return this instance's unique key.
     */

    public String getUniqueKey() {

        return _uniqueKey;

    }

    /**
     Construct a unique key which sorts by a Selector name and then by a String name.
     @param selector the {@link Selector}.
     @param name the {@link String name}.
     @return the unique key resulting from
     <blockquote>
     {@code "" + selector.name() + ":" + name}
     </blockquote>
     */

    public static String makeUniqueKey( final @NotNull Selector selector, final @NotNull String name ) {

        return selector.selectorTag + ":" + name;

    }

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

    public boolean doit(
            final @NotNull ActionEvent actionEvent
    ) {

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

        bundle.addHolder( new GowingStringHolder( G_SELECTOR_TAG, _selector.selectorTag, true ) );
        bundle.addHolder( new GowingStringHolder( G_TO_STRING, _toString, true ) );
        bundle.addHolder( new GowingStringHolder( G_CHOICE_TAG, _tag, true ) );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) {

        return true;

    }

    @Override
    public int compareTo( @NotNull final Alternative rhs ) {

        return _uniqueKey.compareTo( rhs.getUniqueKey() );

    }

    @Override public int hashCode() {

        return _uniqueKey.hashCode();

    }

    @Override public boolean equals( final Object rhs ) {

        return rhs instanceof Alternative && compareTo( (Alternative)rhs ) == 0;

    }

}
