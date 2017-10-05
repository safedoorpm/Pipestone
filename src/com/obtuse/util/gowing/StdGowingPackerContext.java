package com.obtuse.util.gowing;

import com.obtuse.util.Accumulator;
import com.obtuse.util.TreeAccumulator;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.holders.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Provide a packing id space for entities that are packed and/or unpacked together.
 */

public class StdGowingPackerContext implements GowingPackerContext {

//    private final SortedMap<EntityTypeName,Integer> _seenTypeNames = new TreeMap<EntityTypeName, Integer>();
//    private final SortedMap<Integer,EntityTypeName> _usedTypeIds = new TreeMap<Integer, EntityTypeName>();

    private final SortedSet<Integer> _seenTypeIds = new TreeSet<>();
    private final SortedSet<Integer> _newTypeIds = new TreeSet<>();

    private final TreeMap<GowingInstanceId, EntityNames> _seenInstanceIds = new TreeMap<>();

    private int _nextTypeReferenceId = 1;

    private final Accumulator<EntityTypeName> _highestPackingIdByType = new TreeAccumulator<>();

    private GowingRequestorContext _requestorContext;

    public StdGowingPackerContext() {

        super();

    }

    @Override
    public void setRequestorContext( GowingRequestorContext requestorContext ) {

        if ( _requestorContext != null ) {

            throw new IllegalArgumentException( "a packer's requestor's context may only be set once" );

        }

        _requestorContext = requestorContext;

    }

    @Override
    public GowingRequestorContext getRequestorContext() {

        return _requestorContext;

    }

    @Override
    public void rememberPackableEntity( EntityName entityName, GowingPackable entity ) {

        @SuppressWarnings("UnusedAssignment")
        int typeReferenceId = GowingInstanceId.allocateTypeId( entity.getInstanceId().getTypeName() );

        if ( !_seenInstanceIds.containsKey( entity.getInstanceId() ) ) {

            _seenInstanceIds.put( entity.getInstanceId(), new EntityNames( new LinkedList<>(), entity ) );

        }

        if ( entityName != null ) {

            _seenInstanceIds.get( entity.getInstanceId() ).add( entityName );

        }

    }

    @Override
    @NotNull
    public EntityNames getEntityNames( GowingInstanceId instanceId ) {

        return _seenInstanceIds.get( instanceId );

    }

    public int rememberTypeName( EntityTypeName typeName ) {

        int typeId = GowingInstanceId.allocateTypeId( typeName.getTypeName() );

        if ( !_seenTypeIds.contains( typeId ) ) {

            _seenTypeIds.add( typeId );
            _newTypeIds.add( typeId );

        }

        return typeId;

    }

    @Override
    @NotNull
    public Collection<GowingInstanceId> getSeenInstanceIds() {

        return Collections.unmodifiableCollection( _seenInstanceIds.keySet() );

    }

    @Override
    @NotNull
    public Collection<Integer> getNewTypeIds() {

        LinkedList<Integer> rval = new LinkedList<>( _newTypeIds );
        _newTypeIds.clear();

        return rval;

    }

    public static class TestPackableClass extends GowingAbstractPackableEntity implements GowingPackable {

        private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( StdGowingPackerContext.TestPackableClass.class );

        private static final int VERSION = 1;

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
                    @NotNull GowingUnPacker unPacker,
                    @NotNull GowingPackedEntityBundle bundle,
                    GowingEntityReference er
            ) {

                return new TestPackableClass( unPacker, bundle, er );

            }

        };
        private final String _payload;
        private SimplePackableClass _simple;
        private GowingEntityReference _simpleReference;

        private final int _iValue;

        private TestPackableClass _inner;
        private GowingEntityReference _innerReference;

        public TestPackableClass( @NotNull String payload, @Nullable TestPackableClass inner, @Nullable SimplePackableClass simple ) {

            super( new GowingNameMarkerThing() );

//	    context.registerFactory( FACTORY );

            _simple = simple;
            _inner = inner;
            _payload = payload;
            _iValue = 42;

        }

        public TestPackableClass( GowingUnPacker unPacker, @NotNull GowingPackedEntityBundle bundle, GowingEntityReference er ) {

            super( unPacker, bundle.getSuperBundle() );

            if ( bundle.getVersion() != VERSION ) {

                throw new IllegalArgumentException( TestPackableClass.class.getCanonicalName() +
                                                    ":  expected version " +
                                                    VERSION +
                                                    " but received version " +
                                                    bundle.getVersion() );

            }

            _payload = bundle.getNotNullField( new EntityName( "_payload" ) ).StringValue();

            _simpleReference = bundle.getNullableField( new EntityName( "_simple" ) ).EntityTypeReference();

            _iValue = bundle.getNotNullField( new EntityName( "_iValue" ) ).intValue();

            _innerReference = bundle.getNullableField( new EntityName( "_inner" ) ).EntityTypeReference();

        }

        @NotNull
        @Override
        public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker packer ) {

            GowingPackedEntityBundle rval = new GowingPackedEntityBundle(
                    ENTITY_TYPE_NAME,
                    VERSION,
                    super.bundleRoot( packer ),
                    packer.getPackingContext()
            );

            rval.addHolder( new GowingPackableEntityHolder( new EntityName( "_simple" ), _simple, packer, false ) );
            rval.addHolder( new GowingPackableEntityHolder( new EntityName( "_inner" ), _inner, packer, false ) );
            rval.addHolder( new GowingStringHolder( new EntityName( "_payload" ), _payload, true ) );
            rval.addHolder( new GowingIntegerHolder( new EntityName( "_iValue" ), _iValue, false ) );
            rval.addHolder( new GowingBooleanHolder( new EntityName( "_booleanValue" ), true, true ) );
            rval.addHolder( new GowingDoubleHolder( new EntityName( "_doubleValue" ), Math.PI, false ) );
            rval.addHolder( new GowingFloatHolder( new EntityName( "_floatValue" ), 1.1f, true ) );
            rval.addHolder( new GowingShortHolder( new EntityName( "_shortValue" ), (short)15, false ) );
            rval.addHolder( new GowingLongHolder( new EntityName( "_longValue" ), 123L, true ) );
            rval.addHolder( new GowingEntityNameHolder( new EntityName( "_entityName" ), new EntityName( "froz botnick" ), true ) );

            return rval;

        }

        @Override
        public boolean finishUnpacking( GowingUnPacker unPacker ) {

            _simple = (SimplePackableClass)unPacker.resolveReference( _simpleReference );
            _inner = (TestPackableClass)unPacker.resolveReference( _innerReference );

            return true;

        }

        public String toString() {

            return "StdPackingContext2.TestPackableClass( \"" + _payload + "\", " + _iValue + " )";

        }

    }

    public static class SimplePackableClass extends GowingAbstractPackableEntity implements GowingPackable {

        private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( StdGowingPackerContext.SimplePackableClass.class );

        private static final int VERSION = 42;

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
                    @NotNull GowingUnPacker unPacker,
                    @NotNull GowingPackedEntityBundle bundle,
                    GowingEntityReference er
            ) {

                return new SimplePackableClass( unPacker, bundle, er );

            }

        };

        private final String _payload;

        public SimplePackableClass( @NotNull String payload ) {

            super( new GowingNameMarkerThing() );

            _payload = payload;

        }

        public SimplePackableClass( GowingUnPacker unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {

            super( new GowingNameMarkerThing() );

            if ( bundle.getVersion() != VERSION ) {

                throw new IllegalArgumentException( SimplePackableClass.class.getCanonicalName() +
                                                    ":  expected version " +
                                                    VERSION +
                                                    " but received version " +
                                                    bundle.getVersion() );

            }

            _payload = bundle.getNotNullField( new EntityName( "_thing" ) ).StringValue();


        }

        @NotNull
        @Override
        public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker packer ) {

            GowingPackedEntityBundle rval = new GowingPackedEntityBundle(
                    ENTITY_TYPE_NAME,
                    VERSION,
                    // super.bundleThyself( true, packer ),
                    null,
                    packer.getPackingContext()
            );

            rval.addHolder( new GowingStringHolder( new EntityName( "_thing" ), _payload, true ) );

            return rval;

        }

        @Override
        public boolean finishUnpacking( GowingUnPacker unPacker ) {

            // Nothing to be done here.

            return true;

        }

        public String toString() {

            return "StdPackingContext2.SimplePackableClass( \"" + _payload + "\" )";

        }

    }

    public String toString() {

        return "StdGowingPackerContext( seen instances count=" + _seenInstanceIds.size() +
               ", highest packing id by type=" + _highestPackingIdByType +
               ", requestor context=" + _requestorContext +
               " )";

    }

}
