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
 Something simple yet structured to put into a combo-box.
 <p>Instances of this class are immutable.</p>
 */

public class SimpleComboBoxChoice implements Comparable<SimpleComboBoxChoice>, GowingPackable {

    private static final SortedMap<String, SimpleComboBoxChoice> s_availableChoices = new TreeMap<>();

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( SimpleComboBoxChoice.class );
    private static final int VERSION = 1;

    private static final EntityName G_VARIANT_TAG = new EntityName( "_vt" );
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

            String variantTag = bundle.MandatoryStringValue( G_VARIANT_TAG );
            Optional<ChoiceVariant> optVariant = ChoiceVariant.findVariant( variantTag );
            if ( !optVariant.isPresent() ) {

                throw new IllegalArgumentException(
                        "SimpleComboBoxChoice.factory.createEntity:  unknown variant " + ObtuseUtil.enquoteToJavaString( variantTag )
                );

            }

            String choiceTag = bundle.MandatoryStringValue( G_CHOICE_TAG );
            ChoiceVariant choiceVariant = optVariant.get();
            String uniqueKey = makeUniqueKey( choiceVariant, choiceTag );

            Optional<SimpleComboBoxChoice> optChoice = findChoice( choiceVariant, choiceTag );

            if ( optChoice.isPresent() ) {

                return optChoice.get();

            } else {

                throw new IllegalArgumentException( "SimpleComboBoxChoice.createEntity:  unknown unique key " + ObtuseUtil.enquoteToJavaString( uniqueKey ) );

            }

        }

    };

    private final GowingInstanceId _instanceId = new GowingInstanceId( GowingPackable.class );
    private final ChoiceVariant _choiceVariant;
    private final String _toString;
    private final String _tag;
    private final String _uniqueKey;

    private boolean _unspecifiedChoice = false;

    /**
     Create an instance which has a name which is equal to its {@link #toString()} value.
     @param choiceVariant the instance's {@link ChoiceVariant}.
     @param tag the instance's name.
     @param toString the value returned by this instance's {@code toString()} method.
     @throws IllegalArgumentException if this JVM already has an instance of this class with the specified {@code name} and {@code toString} values.
     */

    private SimpleComboBoxChoice( final @NotNull ChoiceVariant choiceVariant, final @NotNull String tag, final @NotNull String toString ) {
        super();

        _choiceVariant = choiceVariant;
        _tag = tag;
        _toString = toString;
        _uniqueKey = makeUniqueKey( choiceVariant, tag );

        if ( findChoice( _uniqueKey ).isPresent() ) {

            throw new IllegalArgumentException(
                    "SimpleComboBoxChoice:  choice with unique key " + ObtuseUtil.enquoteToJavaString( _uniqueKey ) + " already exists"
            );

        }

        s_availableChoices.put( _uniqueKey, this );

        Logger.logMsg( "SimpleComboBoxChoice:  created " + describeInstance( _uniqueKey, this ) );

        ObtuseUtil.doNothing();

    }

    /**
     Create an instance which has a name which is equal to its {@link #toString()} value.
     @param choiceVariant the instance's {@link ChoiceVariant}.
     @param tag the instance's name and the value returned by its {@code toString()} method.
     @throws IllegalArgumentException if this JVM already has an instance of this class with
     a name and {@code toString()} value equal to the specified {@code name}.
     */

    public SimpleComboBoxChoice( final @NotNull ChoiceVariant choiceVariant, final @NotNull String tag ) {
        this( choiceVariant, tag, tag );
    }

    public static Optional<SimpleComboBoxChoice> findChoice( final @NotNull ChoiceVariant choiceVariant, final @NotNull String name ) {

        return findChoice( makeUniqueKey( choiceVariant, name ) );

    }

    public static Optional<SimpleComboBoxChoice> findChoice( final @NotNull String uniqueKey ) {

        Logger.logMsg( "SimpleComboBoxChoice.findChoice:  looking for " + ObtuseUtil.enquoteToJavaString( uniqueKey ) );

        SimpleComboBoxChoice choice = s_availableChoices.get( uniqueKey );

        Optional<SimpleComboBoxChoice> rval = Optional.ofNullable( choice );

        if ( !rval.isPresent() ) {

            Logger.logMsg( "SimpleComboBoxChoice.findChoice:  did not find " + ObtuseUtil.enquoteToJavaString( uniqueKey ) );
            for ( String key : s_availableChoices.keySet() ) {

                SimpleComboBoxChoice tmpChoice = s_availableChoices.get( key );
                Logger.logMsg(
                        describeInstance( key, tmpChoice )
                );

            }

            ObtuseUtil.doNothing();

        }

        return rval;

    }

    @NotNull
    private static String describeInstance( final String key, final SimpleComboBoxChoice tmpChoice ) {

        return "    variant=" + ObtuseUtil.enquoteJavaObject( tmpChoice.getChoiceVariant() ) +
               ", key=" + ObtuseUtil.enquoteToJavaString( key ) +
               ", uKey=" + ObtuseUtil.enquoteToJavaString( tmpChoice.getUniqueKey() ) +
               ", toString=" + ObtuseUtil.enquoteToJavaString( tmpChoice.toString() );
    }

    public static SimpleComboBoxChoice maybeCreateChoice( final @NotNull ChoiceVariant choiceVariant, final @NotNull String tag, final @NotNull String toString ) {

        Optional<SimpleComboBoxChoice> optChoice = findChoice( choiceVariant, tag );
        return optChoice.orElseGet( () -> new SimpleComboBoxChoice( choiceVariant, tag, toString ) );

    }

//    public static SimpleComboBoxChoice maybeCreateChoice( final @NotNull SimpleComboBoxChoice.ChoiceVariant choiceVariant, final @NotNull String name ) {
//
//        return maybeCreateChoice( choiceVariant, name, name );
//
//    }

    /**
     Get this instance's variant.
     @return this instance's {@code ChoiceVariant}.
     */

    @NotNull
    public ChoiceVariant getChoiceVariant() {

        return _choiceVariant;

    }

    public SimpleComboBoxChoice markAsUnspecifiedChoice() {

        if ( _unspecifiedChoice ) {

            throw new IllegalArgumentException( "SimpleComboBoxChoice.markUnspecifiedChoice:  instance already marked as the unspecified choice" );

        }

        _unspecifiedChoice = true;

        return this;

    }

    public boolean isUnspecifiedChoice() {

        return _unspecifiedChoice;

    }

    @NotNull
    public String getTag() {

        return _tag == null ? "<<unknown SimpleComboBoxChoice name>>" : _tag;

    }

    public String toString() {

        return _toString == null ? "<<unknown SimpleComboBoxChoice toString>>" : _toString;

    }

    /**
     Get this instance's unique key.
     <p/>Each instance has a unique key which is equal to
     <blockquote>{@code "" + this.}{@link #getChoiceVariant()}{@code  + ":" + this.}{@link #getTag()}</blockquote>
     @return this instance's unique key.
     */

    public String getUniqueKey() {

        return _uniqueKey;

    }

    /**
     Construct a unique key which sorts by a ChoiceVariant name and then by a String name.
     @param choiceVariant the {@link ChoiceVariant}.
     @param name the {@link String name}.
     @return the unique key resulting from
     <blockquote>
     {@code "" + choiceVariant.name() + ":" + name}
     </blockquote>
     */

    public static String makeUniqueKey( final @NotNull ChoiceVariant choiceVariant, final @NotNull String name ) {

        return choiceVariant.variantTag + ":" + name;

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

        bundle.addHolder( new GowingStringHolder( G_VARIANT_TAG, _choiceVariant.variantTag, true ) );
        bundle.addHolder( new GowingStringHolder( G_TO_STRING, _toString, true ) );
        bundle.addHolder( new GowingStringHolder( G_CHOICE_TAG, _tag, true ) );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) {

        return true;

    }

    @Override
    public int compareTo( @NotNull final SimpleComboBoxChoice rhs ) {

        return _uniqueKey.compareTo( rhs.getUniqueKey() );

    }

    @Override public int hashCode() {

        return _uniqueKey.hashCode();

    }

    @Override public boolean equals( final Object rhs ) {

        return rhs instanceof SimpleComboBoxChoice && compareTo( (SimpleComboBoxChoice)rhs ) == 0;

    }

}
