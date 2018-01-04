package com.obtuse.util.gowing.p2a.examples;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.holders.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A simple example of a class that uses Gowing for packing and unpacking instances.
 */

public class SortedSetExample extends GowingAbstractPackableEntity implements GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( SortedSetExample.class.getCanonicalName() );

    private static final int VERSION = 1;

    private SortedSet<String> _myCollection = new TreeSet<>();

    private static final EntityName DATA_COLLECTION_NAME = new EntityName( "_dc" );
    private GowingEntityReference _dataCollectionReference = null;

    private static final EntityName NAME_NAME = new EntityName( "_n" );
    private String _name;

    private static final EntityName DESCRIPTION_NAME = new EntityName( "_d" );
    private String _description;

    public static GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;

        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;

        }

        @Override
        @NotNull
        public GowingPackable createEntity(
                @NotNull final GowingUnPacker unPacker,
                @NotNull final GowingPackedEntityBundle bundle,
                final GowingEntityReference er
        ) {

            return new SortedSetExample( unPacker, bundle );

        }

    };

    public SortedSetExample( @NotNull final String name, @Nullable final String description, final @NotNull String@NotNull[] contents ) {

        super( new GowingNameMarkerThing() );

        _name = name;
        _description = description;

        Collections.addAll( _myCollection, contents );

    }

    public String[] getStrings() {

        return _myCollection.toArray( new String[_myCollection.size()] );

    }

    private SortedSetExample( final GowingUnPacker unPacker, final GowingPackedEntityBundle bundle ) {

        super( unPacker, bundle.getSuperBundle() );

        _dataCollectionReference = bundle.getMandatoryEntityReference( DATA_COLLECTION_NAME );
        _name = bundle.getNotNullField( NAME_NAME ).StringValue();
        _description = bundle.getNullableField( DESCRIPTION_NAME ).StringValue();

        Logger.logMsg(
                "SortedSetExample constructed from Gowing:  name=" + ObtuseUtil.enquoteToJavaString( _name ) + ", description=" +
                ObtuseUtil.enquoteToJavaString( _description ) + ", dataCollectionReference=" + _dataCollectionReference
        );

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( final boolean isPackingSuper, @NotNull final GowingPacker packer ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleRoot( packer ),
                packer.getPackingContext()
        );

        GowingPackableCollection<String> p2c = new GowingPackableCollection<>( _myCollection );
        bundle.addHolder( new GowingPackableEntityHolder( DATA_COLLECTION_NAME, p2c, packer, true ) );
        bundle.addHolder( new GowingStringHolder( NAME_NAME, _name, true ) );
        bundle.addHolder( new GowingStringHolder( DESCRIPTION_NAME, _description, false ) );
        bundle.addHolder( new GowingBooleanHolder( new EntityName( "_booleanPA" ), new boolean[]{ true, false, true, false }, true ) );
        bundle.addHolder( new GowingBooleanHolder( new EntityName( "_booleanCA" ), new Boolean[]{ true, false, null, false }, true ) );
        bundle.addHolder( new GowingByteHolder( new EntityName( "_bytePA" ), new byte[]{ 1, 2, 3, 4 }, true ) );
        bundle.addHolder( new GowingByteHolder( new EntityName( "_byteCA" ), new Byte[]{ 1, 2, null, 4 }, true ) );
        bundle.addHolder( new GowingShortHolder( new EntityName( "_shortPA" ), new short[]{ 1, 2, 3, 4 }, true ) );
        bundle.addHolder( new GowingShortHolder( new EntityName( "_shortCA" ), new Short[]{ 1, 2, null, 4 }, true ) );
        bundle.addHolder( new GowingIntegerHolder( new EntityName( "_integerPA" ), new int[]{ 1234567, 2, 3, 4 }, true ) );
        bundle.addHolder( new GowingIntegerHolder( new EntityName( "_integerCA" ), new Integer[]{ 1234567, 2, null, 4 }, true ) );
        bundle.addHolder( new GowingLongHolder( new EntityName( "_longPA" ), new long[]{ 1234567898765L, 2L, 3L, 4L }, true ) );
        bundle.addHolder( new GowingLongHolder( new EntityName( "_longCA" ), new Long[]{ 1234567898765L, 2L, null, 4L }, true ) );
        bundle.addHolder( new GowingFloatHolder( new EntityName( "_floatPA" ), new float[]{ 1.23456789f, 2f, 3f, 4f }, true ) );
        bundle.addHolder( new GowingFloatHolder( new EntityName( "_floatCA" ), new Float[]{ 1.23456789f, 2f, null, 4f }, true ) );
        bundle.addHolder( new GowingDoubleHolder( new EntityName( "_doublePA" ), new double[]{ 1.23456789098765d, 2d, 3d, 4d }, true ) );
        bundle.addHolder( new GowingDoubleHolder( new EntityName( "_doubleCA" ), new Double[]{ 1.23456789098765d, 2d, null, 4d }, true ) );
        bundle.addHolder( new GowingIntegerHolder( new EntityName( "_int" ), -1, true ) );

        return bundle;

    }

    @NotNull
    public String getName() {

        return _name;

    }

    @Nullable
    public String getDescription() {

        return _description;

    }

    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) {

        if ( !unPacker.isEntityFinished( _dataCollectionReference ) ) {

            return false;

        }

        @SuppressWarnings("unchecked") GowingPackableCollection<String> dataCollection
                = (GowingPackableCollection<String>)unPacker.resolveReference( _dataCollectionReference );

        _myCollection.addAll( dataCollection );

        Logger.logMsg(
                "SortedSetExample constructed and finished unpacking from Gowing:  " + this
        );

        return true;

    }

    public String toString() {

        return "SortedSetExample( " +
               "name=" + ObtuseUtil.enquoteToJavaString( _name ) + ", " +
               "description=" + ObtuseUtil.enquoteToJavaString( _description ) + ", " +
               "values=" + _myCollection + " )";

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Experimenting", "Misc", null );

        int[] primitive = new int[3];
        Integer[] container = new Integer[3];

        for ( int i = 0; i < 3; i += 1 ) {

            Array.set( primitive, i, i );
            Array.set( container, i, -i );

        }

        Logger.logMsg( "done" );

    }
}
