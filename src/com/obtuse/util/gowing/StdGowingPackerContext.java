package com.obtuse.util.gowing;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUtil;
import com.obtuse.util.gowing.p2a.holders.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Provide a packing id space for entities that are packed and/or unpacked together.
 */

public class StdGowingPackerContext implements GowingPackerContext {

    private final SortedSet<Integer> _seenTypeIds = new TreeSet<>();
    private final SortedSet<Integer> _newTypeIds = new TreeSet<>();

    private SortedSet<Integer> _topTypeIds = new TreeSet<>();

    private final TreeMap<GowingInstanceId, EntityNames> _seenInstanceIds = new TreeMap<>();

    private int _nextTypeReferenceId = 1;

    private final Accumulator<EntityTypeName> _highestPackingIdByType = new TreeAccumulator<>();

    private GowingRequestorContext _requestorContext;

    public StdGowingPackerContext() {

        super();

    }

    @Override
    public void setRequestorContext( final GowingRequestorContext requestorContext ) {

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
    public void rememberPackableEntity( @Nullable final EntityName entityName, final @NotNull GowingPackable entity ) {

        GowingUtil.verifyActuallyPackable( "StdGowingPackerContext.rememberPackableEntity", entityName, entity );

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
    public EntityNames getEntityNames( final GowingInstanceId instanceId ) {

        return _seenInstanceIds.get( instanceId );

    }

    public int rememberTypeName( final EntityTypeName typeName ) {

        int typeId = GowingInstanceId.allocateTypeId( typeName.getTypeName() );

        if ( !_seenTypeIds.contains( typeId ) ) {

            _seenTypeIds.add( typeId );
            _newTypeIds.add( typeId );

        }

        return typeId;

    }

    @Override
    public void rememberTopTypeId( final int topTypeId ) {

        _topTypeIds.add( topTypeId );

    }

    @Override
    @NotNull
    public Set<Integer> getTopTypeIds() {

        return Collections.unmodifiableSet( _topTypeIds );

    }

    private TwoDimensionalSortedMap<Integer,Long,Long> _entityIdMap = new TwoDimensionalTreeMap<>();
    private final SimpleUniqueLongIdGenerator _entityIdRemapperGenerator =
            new SimpleUniqueLongIdGenerator( GowingInstanceId.class.getCanonicalName() + " - entity id remapper generator" );

    private boolean _doRemap = true;

    @Override
    public long remapEntityId( final int typeId, final long entityId ) {

        // Don't mess with entity ids equal to 0 since they mark super-bundle entities.

        if ( entityId == 0L ) {

            return entityId;

        }

        Long remappedEntityId = _entityIdMap.computeIfAbsent(
                typeId,
                entityId,
                ( typeId1, entityId1 ) -> {

                    long remappedId = _doRemap ? _entityIdRemapperGenerator.getUniqueId() : entityId1;

                    if ( remappedId == 0 ) {

                        throw new HowDidWeGetHereError( "StdGowingPackerContext.remapEntityId:  entity id of 0 generated - violates assumption that 0 marks a super-bundle entity" );

                    }

                    return remappedId;

                }
        );

        return remappedEntityId;

    }

    @Override
    @NotNull
    public Set<Integer> getSeenTypeIds() {

        return Collections.unmodifiableSortedSet( _seenTypeIds );

    }

    @Override
    @NotNull
    public Set<GowingInstanceId> getSeenInstanceIds() {

        return Collections.unmodifiableSet( _seenInstanceIds.keySet() );

    }

    @Override
    @NotNull
    public List<Integer> getAndResetNewTypeIds() {

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
                    final @NotNull GowingUnPacker unPacker,
                    final @NotNull GowingPackedEntityBundle bundle,
                    final @NotNull GowingEntityReference er
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

        public TestPackableClass( final @NotNull String payload, @Nullable final TestPackableClass inner, @Nullable final SimplePackableClass simple ) {

            super( new GowingNameMarkerThing() );

            _simple = simple;
            _inner = inner;
            _payload = payload;
            _iValue = 42;

        }

        public TestPackableClass( final GowingUnPacker unPacker, final @NotNull GowingPackedEntityBundle bundle, final GowingEntityReference er ) {

            super( unPacker, bundle.getSuperBundle() );

            if ( bundle.getVersion() != VERSION ) {

                throw new IllegalArgumentException( TestPackableClass.class.getCanonicalName() +
                                                    ":  expected version " +
                                                    VERSION +
                                                    " but received version " +
                                                    bundle.getVersion() );

            }

            _payload = bundle.MandatoryStringValue( new EntityName( "_payload" ) );

            _simpleReference = bundle.getOptionalEntityReference( new EntityName( "_simple" ) ).orElse( null );

            _iValue = bundle.getNotNullField( new EntityName( "_iValue" ) ).intValue();

            _innerReference = bundle.getOptionalEntityReference( new EntityName( "_inner" ) ).orElse( null );

            @Nullable EntityName froz = bundle.EntityNameValue( new EntityName( "_entityName" ) );

            ObtuseUtil.doNothing();

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

            bundle.addHolder( new GowingPackableEntityHolder( new EntityName( "_simple" ), _simple, packer, false ) );
            bundle.addHolder( new GowingPackableEntityHolder( new EntityName( "_inner" ), _inner, packer, false ) );
            bundle.addHolder( new GowingStringHolder( new EntityName( "_payload" ), _payload, true ) );
            bundle.addHolder( new GowingIntegerHolder( new EntityName( "_iValue" ), _iValue, false ) );
            bundle.addHolder( new GowingBooleanHolder( new EntityName( "_booleanValue" ), true ) );
            bundle.addHolder( new GowingDoubleHolder( new EntityName( "_doubleValue" ), Math.PI ) );
            bundle.addHolder( new GowingFloatHolder( new EntityName( "_floatValue" ), 1.1f ) );
            bundle.addHolder( new GowingShortHolder( new EntityName( "_shortValue" ), (short)15 ) );
            bundle.addHolder( new GowingLongHolder( new EntityName( "_longValue" ), 123L ) );
            bundle.addHolder( new GowingEntityNameHolder( new EntityName( "_entityName" ), new EntityName( "froz_botnick" ), true ) );

            return bundle;

        }

        @Override
        public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

            _simple = (SimplePackableClass)unPacker.resolveReference( _simpleReference ).orElse( null );
            _inner = (TestPackableClass)unPacker.resolveReference( _innerReference ).orElse( null );

            return true;

        }

        public String toString() {

            return "StdPackingContext2.TestPackableClass( \"" + _payload + "\", " + _iValue + " )";

        }

    }

    public static class SimplePackableClass extends GowingAbstractPackableEntity implements GowingPackable {

        private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( StdGowingPackerContext.SimplePackableClass.class );

        private static final int VERSION = 42;

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

                return new SimplePackableClass( unPacker, bundle, er );

            }

        };

        private final String _payload;

        public SimplePackableClass( final @NotNull String payload ) {

            super( new GowingNameMarkerThing() );

            _payload = payload;

        }

        public SimplePackableClass( final GowingUnPacker unPacker, final GowingPackedEntityBundle bundle, final GowingEntityReference er ) {

            super( unPacker, bundle.getSuperBundle() );

            if ( bundle.getVersion() != VERSION ) {

                throw new IllegalArgumentException( SimplePackableClass.class.getCanonicalName() +
                                                    ":  expected version " +
                                                    VERSION +
                                                    " but received version " +
                                                    bundle.getVersion() );

            }

            _payload = bundle.MandatoryStringValue( new EntityName( "_thing" ) );

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

            bundle.addHolder( new GowingStringHolder( new EntityName( "_thing" ), _payload, true ) );

            return bundle;

        }

        @Override
        public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

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
