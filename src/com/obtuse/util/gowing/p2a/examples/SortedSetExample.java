package com.obtuse.util.gowing.p2a.examples;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.*;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.holders.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A simple but long example of a class that uses Gowing for packing and unpacking instances.
 */

@SuppressWarnings("FieldCanBeLocal")
public class SortedSetExample extends GowingAbstractPackableEntity implements GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( SortedSetExample.class );

    private static final int VERSION = 1;

    private static final EntityName PRIMITIVE_BOOLEAN_ARRAY = new EntityName( "_booleanPA" );
    private static final EntityName BOOLEAN_ARRAY = new EntityName( "_booleanCA" );
    private static final EntityName PRIMITIVE_BOOLEAN = new EntityName( "_boolean" );
    private static final EntityName BOOLEAN = new EntityName( "_Boolean" );
    private static final EntityName PRIMITIVE_BYTE_ARRAY = new EntityName( "_bytePA" );
    private static final EntityName BYTE_ARRAY = new EntityName( "_byteCA" );
    private static final EntityName PRIMITIVE_BYTE = new EntityName( "_byte" );
    private static final EntityName BYTE = new EntityName( "_Byte" );
    private static final EntityName PRIMITIVE_SHORT_ARRAY = new EntityName( "_shortPA" );
    private static final EntityName SHORT_ARRAY = new EntityName( "_shortCA" );
    private static final EntityName PRIMITIVE_SHORT = new EntityName( "_short" );
    private static final EntityName SHORT = new EntityName( "_Short" );
    private static final EntityName PRIMITIVE_INT_ARRAY = new EntityName( "_integerPA" );
    private static final EntityName INTEGER_ARRAY = new EntityName( "_integerCA" );
    private static final EntityName PRIMITIVE_INT = new EntityName( "_int" );
    private static final EntityName INTEGER = new EntityName( "_Integer" );
    private static final EntityName PRIMITIVE_LONG_ARRAY = new EntityName( "_longPA" );
    private static final EntityName LONG_ARRAY = new EntityName( "_longCA" );
    private static final EntityName PRIMITIVE_LONG = new EntityName( "_long" );
    private static final EntityName LONG = new EntityName( "_Long" );
    private static final EntityName PRIMITIVE_FLOAT_ARRAY = new EntityName( "_floatPA" );
    private static final EntityName FLOAT_ARRAY = new EntityName( "_floatCA" );
    private static final EntityName PRIMITIVE_FLOAT = new EntityName( "_float" );
    private static final EntityName FLOAT = new EntityName( "_Float" );
    private static final EntityName PRIMITIVE_DOUBLE_ARRAY = new EntityName( "_doublePA" );
    private static final EntityName DOUBLE_ARRAY = new EntityName( "_doubleCA" );
    private static final EntityName PRIMITIVE_DOUBLE = new EntityName( "_double" );
    private static final EntityName DOUBLE = new EntityName( "_Double" );
    private static final EntityName DATA_COLLECTION_NAME = new EntityName( "_dc" );

    private static final EntityName STRING_ARRAY = new EntityName( "_stringCA" );

    private static final EntityName SORTED_SET = new EntityName( "_sSet" );
    private static final EntityName HASH_SET = new EntityName( "_hSet" );
    private static final EntityName LIST = new EntityName( "_List" );
    private static final EntityName SORTED_MAP = new EntityName( "_sMap" );
    private static final EntityName HASH_MAP = new EntityName( "_hMap" );

    private GowingEntityReference _dataCollectionReference = null;
    private GowingEntityReference _sortedSetReference = null;
    private GowingEntityReference _hashSetReference = null;
    private GowingEntityReference _listReference = null;
    private GowingEntityReference _sortedMapReference = null;
    private GowingEntityReference _hashMapReference = null;

    private static final EntityName NAME_NAME = new EntityName( "_n" );
    private final String _name;

    private ArrayList<String> _stringCollection;
    private SortedSet<String> _stringSortedSet;
    private HashSet<String> _stringHashSet;
    private List<String> _stringSimpleList;
    private SortedMap<String,Integer> _stringSortedMapSorted;
    private HashMap<String,Integer> _stringSortedMapHashed;
    private SortedMap<String,Integer> _stringHashMapSorted;
    private HashMap<String,Integer> _stringHashMapHashed;
    private List<String> _stringSortedSetAsIs;
    private List<String> _stringHashSetAsIs;
    private List<GowingPackableKeyValuePair<String,Integer>> _stringSortedMapAsIs;
    private List<GowingPackableKeyValuePair<String,Integer>> _stringHashMapAsIs;

    private static final EntityName DESCRIPTION_NAME = new EntityName( "_d" );
    private final String _description;

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

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
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        ) {

            return new SortedSetExample( unPacker, bundle );

        }

    };

    public SortedSetExample( final @NotNull String name, @Nullable final String description, final @NotNull String@NotNull[] contents ) {
        super( new GowingNameMarkerThing() );

        _name = name;
        _description = description;
        _stringCollection = new ArrayList<>();

        _stringSortedMapSorted = new TreeMap<>();
        _stringHashMapHashed = new HashMap<>();
        int ix = 0;
        for ( String s : contents ) {

            _stringSortedMapSorted.put( s, ix );
            _stringHashMapHashed.put( s, ix );

            ix += 1;

        }

        Collections.addAll( _stringCollection, contents );

    }

    private SortedSetExample( final GowingUnPacker unPacker, final GowingPackedEntityBundle bundle ) {

        super( unPacker, bundle.getSuperBundle() );

        _dataCollectionReference = bundle.getMandatoryEntityReference( DATA_COLLECTION_NAME );
        _name = bundle.MandatoryStringValue( NAME_NAME );
        _description = bundle.getNullableField( DESCRIPTION_NAME ).StringValue();

        Logger.logMsg(
                "SortedSetExample constructed from Gowing:  name=" + ObtuseUtil.enquoteToJavaString( _name ) + ", description=" +
                ObtuseUtil.enquoteToJavaString( _description ) + ", dataCollectionReference=" + _dataCollectionReference
        );

        String[] stringArray = bundle.MandatoryStringArrayValue( STRING_ARRAY );

        Logger.logMsg( "unpacked strings:  " + _name + ", " + _description + ", " + Arrays.toString( stringArray ) );

        boolean[] pBoolArray = bundle.MandatoryPrimitiveBooleanArrayValue( PRIMITIVE_BOOLEAN_ARRAY );
        Boolean[] BoolArray = bundle.BooleanArrayValue( BOOLEAN_ARRAY );
        boolean bool = bundle.booleanValue( PRIMITIVE_BOOLEAN );
        Boolean Bool = bundle.BooleanValue( BOOLEAN );

        Logger.logMsg( "unpacked booleans:  " + Arrays.toString( pBoolArray ) + ", " + Arrays.toString( BoolArray ) + ", " + bool + ", " + Bool );

        byte[] pByteArray = bundle.MandatoryPrimitiveByteArrayValue( PRIMITIVE_BYTE_ARRAY );
        Byte[] ByteArray = bundle.ByteArrayValue( BYTE_ARRAY );
        byte by = bundle.byteValue( PRIMITIVE_BYTE );
        Byte By = bundle.ByteValue( BYTE );

        Logger.logMsg( "unpacked bytes:  " + Arrays.toString( pByteArray ) + ", " + Arrays.toString( ByteArray ) + ", " + by + ", " + By );

        short[] pShArray = bundle.MandatoryPrimitiveShortArrayValue( PRIMITIVE_SHORT_ARRAY );
        Short[] ShArray = bundle.ShortArrayValue( SHORT_ARRAY );
        short sh = bundle.shortValue( PRIMITIVE_SHORT );
        Short Sh = bundle.ShortValue( SHORT );

        Logger.logMsg( "unpacked shorts:  " + Arrays.toString( pShArray ) + ", " + Arrays.toString( ShArray ) + ", " + sh + ", " + Sh );

        int[] pInArray = bundle.MandatoryPrimitiveIntArrayValue( PRIMITIVE_INT_ARRAY );
        Integer[] InArray = bundle.IntegerArrayValue( INTEGER_ARRAY );
        int in = bundle.intValue( PRIMITIVE_INT );
        Integer In = bundle.IntegerValue( INTEGER );

        Logger.logMsg( "unpacked integers:  " + Arrays.toString( pInArray ) + ", " + Arrays.toString( InArray ) + ", " + in + ", " + In );

        long[] pLoArray = bundle.MandatoryPrimitiveLongArrayValue( PRIMITIVE_LONG_ARRAY );
        Long[] LoArray = bundle.LongArrayValue( LONG_ARRAY );
        long lo = bundle.longValue( PRIMITIVE_LONG );
        Long Lo = bundle.LongValue( LONG );

        Logger.logMsg( "unpacked longs:  " + Arrays.toString( pLoArray ) + ", " + Arrays.toString( LoArray ) + ", " + lo + ", " + Lo );

        float[] pFlArray = bundle.MandatoryPrimitiveFloatArrayValue( PRIMITIVE_FLOAT_ARRAY );
        Float[] FlArray = bundle.FloatArrayValue( FLOAT_ARRAY );
        float fl = bundle.floatValue( PRIMITIVE_FLOAT );
        Float Fl = bundle.FloatValue( FLOAT );

        Logger.logMsg( "unpacked floats:  " + Arrays.toString( pFlArray ) + ", " + Arrays.toString( FlArray ) + ", " + fl + ", " + Fl );

        double[] pDbArray = bundle.MandatoryPrimitiveDoubleArrayValue( PRIMITIVE_DOUBLE_ARRAY );
        Double[] DbArray = bundle.DoubleArrayValue( DOUBLE_ARRAY );
        double db = bundle.doubleValue( PRIMITIVE_DOUBLE );
        Double Db = bundle.DoubleValue( DOUBLE );

        Logger.logMsg( "unpacked doubles:  " + Arrays.toString( pDbArray ) + ", " + Arrays.toString( DbArray ) + ", " + db + ", " + Db );

        GowingEntityReference dataCollectionReference = bundle.getMandatoryEntityReference( SORTED_SET );
        GowingEntityReference sortedSetReference = bundle.getMandatoryEntityReference( SORTED_SET );
        GowingEntityReference hashSetReference = bundle.getMandatoryEntityReference( HASH_SET );
        GowingEntityReference listReference = bundle.getMandatoryEntityReference( LIST );
        GowingEntityReference sortedMapReference = bundle.getMandatoryEntityReference( SORTED_MAP );
        GowingEntityReference hashMapReference = bundle.getMandatoryEntityReference( HASH_MAP );

        _dataCollectionReference = dataCollectionReference;
        _sortedSetReference = sortedSetReference;
        _hashSetReference = hashSetReference;
        _listReference = listReference;
        _sortedMapReference = sortedMapReference;
        _hashMapReference = hashMapReference;

        Logger.logMsg( "entity references:  " +
                       "dC=" + _dataCollectionReference + " same as " +
                       "sS=" + _sortedSetReference + ", " +
                       "hS=" + _hashSetReference + ", " +
                       "l=" + _listReference + ", " +
                       "sM=" + _sortedMapReference + ", " +
                       "hM=" + _hashMapReference
        );

        Logger.logMsg( "SortedSetExample:  done Gowing-based constructor" );

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( final boolean isPackingSuper, final @NotNull GowingPacker packer ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleRoot( packer ),
                packer.getPackingContext()
        );

        bundle.addHolder( new GowingStringHolder( NAME_NAME, _name, true ) );
        bundle.addHolder( new GowingStringHolder( DESCRIPTION_NAME, _description, false ) );
        bundle.addHolder( new GowingStringHolder( STRING_ARRAY, new String[] {"alpha", "beta", "gamma" }, true ) );

        bundle.addHolder( new GowingBooleanHolder( PRIMITIVE_BOOLEAN_ARRAY, new boolean[]{ true, false, true, false }, true ) );
        bundle.addHolder( new GowingBooleanHolder( BOOLEAN_ARRAY, new Boolean[]{ true, false, null, false }, true ) );
        bundle.addHolder( new GowingBooleanHolder( PRIMITIVE_BOOLEAN, true ) );
        bundle.addHolder( new GowingBooleanHolder( BOOLEAN, Boolean.TRUE, true ) );

        bundle.addHolder( new GowingByteHolder( PRIMITIVE_BYTE_ARRAY, new byte[]{ 1, 2, 3, 4 }, true ) );
        bundle.addHolder( new GowingByteHolder( BYTE_ARRAY, new Byte[]{ 1, 2, null, 4 }, true ) );
        bundle.addHolder( new GowingByteHolder( PRIMITIVE_BYTE, (byte)1 ) );
        //noinspection UnnecessaryBoxing
        bundle.addHolder( new GowingByteHolder( BYTE, Byte.valueOf( (byte)1 ), true ) );

        bundle.addHolder( new GowingShortHolder( PRIMITIVE_SHORT_ARRAY, new short[]{ 1, 2, 3, 4 }, true ) );
        bundle.addHolder( new GowingShortHolder( SHORT_ARRAY, new Short[]{ 1, 2, null, 4 }, true ) );
        bundle.addHolder( new GowingShortHolder( PRIMITIVE_SHORT, (short)10 ) );
        //noinspection UnnecessaryBoxing
        bundle.addHolder( new GowingShortHolder( SHORT, Short.valueOf( (short)20 ), true ) );

        bundle.addHolder( new GowingIntegerHolder( PRIMITIVE_INT_ARRAY, new int[]{ 1234567, 2, 3, 4 }, true ) );
        bundle.addHolder( new GowingIntegerHolder( INTEGER_ARRAY, new Integer[]{ 1234567, 2, null, 4 }, true ) );
        bundle.addHolder( new GowingIntegerHolder( PRIMITIVE_INT, 100 ) );
        //noinspection UnnecessaryBoxing
        bundle.addHolder( new GowingIntegerHolder( INTEGER, Integer.valueOf( 200 ), true ) );

        bundle.addHolder( new GowingLongHolder( PRIMITIVE_LONG_ARRAY, new long[]{ 1234567898765L, 2L, 3L, 4L }, true ) );
        bundle.addHolder( new GowingLongHolder( LONG_ARRAY, new Long[]{ 1234567898765L, 2L, null, 4L }, true ) );
        bundle.addHolder( new GowingLongHolder( PRIMITIVE_LONG, 1000L ) );
        //noinspection UnnecessaryBoxing
        bundle.addHolder( new GowingLongHolder( LONG, Long.valueOf( 2000L ), true ) );

        bundle.addHolder( new GowingFloatHolder( PRIMITIVE_FLOAT_ARRAY, new float[]{ 1.23456789f, 2f, 3f, 4f }, true ) );
        bundle.addHolder( new GowingFloatHolder( FLOAT_ARRAY, new Float[]{ 1.23456789f, 2f, null, 4f }, true ) );
        bundle.addHolder( new GowingFloatHolder( PRIMITIVE_FLOAT, (float)Math.PI ) );
        //noinspection UnnecessaryBoxing
        bundle.addHolder( new GowingFloatHolder( FLOAT, Float.valueOf( (float)Math.E ), true ) );

        bundle.addHolder( new GowingDoubleHolder( PRIMITIVE_DOUBLE_ARRAY, new double[]{ 1.23456789098765d, 2d, 3d, 4d }, true ) );
        bundle.addHolder( new GowingDoubleHolder( DOUBLE_ARRAY, new Double[]{ 1.23456789098765d, 2d, null, 4d }, true ) );
        bundle.addHolder( new GowingDoubleHolder( PRIMITIVE_DOUBLE, Math.PI ) );
        //noinspection UnnecessaryBoxing
        bundle.addHolder( new GowingDoubleHolder( DOUBLE, Double.valueOf( Math.E ), true ) );

        // Pack the collection as-is.

        bundle.addHolder(
                new GowingPackableEntityHolder(
                        DATA_COLLECTION_NAME,
                        new GowingPackableCollection<>( _stringCollection ),
                        packer,
                        true
                )
        );

        // Pack the collection as a sorted set.

        bundle.addHolder(
                new GowingPackableEntityHolder(
                        SORTED_SET,
                        new GowingPackableCollection<>( new TreeSet<>( _stringCollection ) ),
                        packer,
                        true
                )
        );

        // Pack the collection as a hash set.

        bundle.addHolder(
                new GowingPackableEntityHolder(
                        HASH_SET,
                        new GowingPackableCollection<>( new HashSet<>( _stringCollection ) ),
                        packer,
                        true
                )
        );

        // Pack the collection as a simple list.

        bundle.addHolder(
                new GowingPackableEntityHolder(
                        LIST,
                        new GowingPackableCollection<>( new LinkedList<>( _stringCollection ) ),
                        packer,
                        true
                )
        );

        // Pack the sorted map.

        bundle.addHolder(
                new GowingPackableEntityHolder(
                        SORTED_MAP,
                        new GowingPackableMapping<>( _stringSortedMapSorted ),
                        packer,
                        true
                )
        );

        // Pack the hash map.

        bundle.addHolder(
                new GowingPackableEntityHolder(
                        HASH_MAP,
                        new GowingPackableMapping<>( _stringHashMapHashed ),
                        packer,
                        true
                )
        );

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
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        List<GowingEntityReference> entityReferences = new ArrayList<>();
        entityReferences.add( _dataCollectionReference );
        entityReferences.add( _sortedSetReference );
        entityReferences.add( _hashSetReference );
        entityReferences.add( _listReference );
        entityReferences.add( _sortedMapReference );
        entityReferences.add( _hashMapReference );

        if (
                !unPacker.areEntitiesAllFinished(
                        _dataCollectionReference,
                        _sortedSetReference,
                        _hashSetReference,
                        _listReference,
                        _sortedMapReference,
                        _hashMapReference
                )
        ) {

            return false;

        }

        // Unpack the collection as-is.

        @SuppressWarnings("unchecked") GowingPackableCollection<String> dataCollection
                = (GowingPackableCollection<String>)unPacker.resolveMandatoryReference( _dataCollectionReference );
        _stringCollection = new ArrayList<>( dataCollection );

        // Unpack the sorted set.

        @SuppressWarnings("unchecked") GowingPackableCollection<String> sortedSet
                = (GowingPackableCollection<String>)unPacker.resolveMandatoryReference( _sortedSetReference );
        _stringSortedSet = new TreeSet<>( sortedSet );
        _stringSortedSetAsIs = new ArrayList<>( sortedSet );

        // Unpack the hash set.

        @SuppressWarnings("unchecked") GowingPackableCollection<String> hashSet
                = (GowingPackableCollection<String>)unPacker.resolveMandatoryReference( _hashSetReference );
        _stringHashSet = new HashSet<>( hashSet );
        _stringHashSetAsIs = new ArrayList<>( hashSet );

        // Unpack the collection that was packed as a simple list.

        @SuppressWarnings("unchecked") GowingPackableCollection<String> simpleList
                = (GowingPackableCollection<String>)unPacker.resolveMandatoryReference( _listReference );
        _stringSimpleList = simpleList;

        Logger.logMsg(
                "SortedSetExample constructed and finished unpacking from Gowing:  " + this
        );

        Logger.logMsg( "unpacked collection:        " + _stringCollection );

        Logger.logMsg( "unpacked sorted set:        " + _stringSortedSet );
        Logger.logMsg( "unpacked sorted set as-is:  " + _stringSortedSetAsIs );

        Logger.logMsg( "unpacked hash set:          " + _stringHashSet );
        Logger.logMsg( "unpacked hash set as-is:    " + _stringHashSetAsIs );

        Logger.logMsg( "unpacked simple list:       " + _stringSimpleList );

        // Unpack the sorted map into a sorted map and into a hash map.

        GowingPackable sortedMapPackableEntity = unPacker.resolveMandatoryReference( _sortedMapReference );
        if ( sortedMapPackableEntity instanceof GowingPackableMapping ) {

            @SuppressWarnings("unchecked")
            GowingPackableMapping<String, Integer> sortedPM = (GowingPackableMapping<String, Integer>)sortedMapPackableEntity;
            _stringSortedMapSorted = sortedPM.rebuildMap( new TreeMap<>() );
            _stringSortedMapHashed = sortedPM.rebuildMap( new HashMap<>() );
            _stringSortedMapAsIs = sortedPM.getMappings();

        } else {

            ObtuseUtil.mumbleQuietly(
                    "SortedSetExample.finishUnpacking",
                    "getting sorted map",
                    "sorted map",
                    GowingPackableMapping.class,
                    sortedMapPackableEntity
            );

        }

        // Unpack the sorted map into a sorted map and into a hash map.

        GowingPackable hashMapPackableEntity = unPacker.resolveMandatoryReference( _sortedMapReference );
        if ( hashMapPackableEntity instanceof GowingPackableMapping ) {

            @SuppressWarnings("unchecked")
            GowingPackableMapping<String, Integer> hashPM = (GowingPackableMapping<String,Integer>)hashMapPackableEntity;
            _stringHashMapSorted = hashPM.rebuildMap( new TreeMap<>() );
            _stringHashMapHashed = hashPM.rebuildMap( new HashMap<>() );
            _stringHashMapAsIs = hashPM.getMappings();

        } else {

            ObtuseUtil.mumbleQuietly(
                    "SortedSetExample.finishUnpacking",
                    "getting hashed map",
                    "hashed map",
                    GowingPackableMapping.class,
                    sortedMapPackableEntity
            );

        }

        Logger.logMsg( "sorted map sorted:  keys=" + _stringSortedMapSorted.keySet() + ", values=" + _stringSortedMapSorted.values() );
        Logger.logMsg( "sorted map hashed:  keys=" + _stringSortedMapHashed.keySet() + ", values=" + _stringSortedMapHashed.values() );
        Logger.logMsg( "sorted map elements:     " + _stringSortedMapAsIs );
        Logger.logMsg( "hash map sorted:    keys=" + _stringHashMapSorted.keySet() + ", values=" + _stringHashMapSorted.values() );
        Logger.logMsg( "hash map hashed:    keys=" + _stringHashMapHashed.keySet() + ", values=" + _stringHashMapHashed.values() );
        Logger.logMsg( "hash map elements:       " + _stringHashMapAsIs );

        return true;

    }

    public String toString() {

        return "SortedSetExample( " +
               "name=" + ObtuseUtil.enquoteToJavaString( _name ) + ", " +
               "description=" + ObtuseUtil.enquoteToJavaString( _description ) + ", " +
               "stringCollection=" + _stringCollection + " )";

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Experimenting", "Misc", null );

        SortedSetExample sse = new SortedSetExample(
                "testSortedSet",
                null,
                new String[]{
                        "alpha",
                        "beta",
                        "gamma",
                        "fred",
                        "wilma",
                        "barney"
                }
        );

        try ( StdGowingPacker p2a = new StdGowingPacker( new EntityName( "test group name" ), new File( "sortedSetExample.p2a" ) ) ) {

            p2a.queuePackableEntity( sse );

            p2a.finish();

        } catch ( FileNotFoundException e ) {

            Logger.logErr( "SortedSetExample.main:  unable to create packer", e );

        }

        Logger.logMsg( "main is done packing" );

        try (
                GowingUnPacker unPacker = new StdGowingUnPacker(
                        new GowingTypeIndex( "test unpacker" ),
                        new File( "sortedSetExample.p2a" )
                )
        ) {

            unPacker.setVerbose( true );

            // Arrange to print out the (relatively limited) metadata in the packed file.

            unPacker.registerMetaDataHandler(
                    new TracingGowingMetaDataHandler()
            );

            unPacker.getUnPackerContext().registerFactory( SortedSetExample.FACTORY );

            GowingUnPackedEntityGroup unPackResult = unPacker.unPack();

//            if ( unPackResult.isPresent() ) {

//                GowingUnPackedEntityGroup result = unPackResult.get();

                for ( GowingPackable entity : unPackResult.getAllEntities() ) {

                    Logger.logMsg( "got " + entity.getClass().getCanonicalName() + " " + entity );

                }

//            }

            ObtuseUtil.doNothing();

        } catch ( IOException | GowingUnpackingException e ) {

            Logger.logErr( "unable to create StdGowingUnPacker instance", e );

        }

        Logger.logMsg( "main is done" );

    }

}
