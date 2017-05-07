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

    private final SortedSet<Integer> _seenTypeIds = new TreeSet<Integer>();
    private final SortedSet<Integer> _newTypeIds = new TreeSet<Integer>();

    private final TreeMap<GowingInstanceId,EntityNames> _seenInstanceIds = new TreeMap<GowingInstanceId,EntityNames>();

    private int _nextTypeReferenceId = 1;

//    public static class PackingAssociation {
//
//	private final GowingInstanceId _instanceId;
//
//	private final GowingPackingId _packingId;
//
//	private final GowingPackable _packable;
//
//	public PackingAssociation( GowingInstanceId instanceId, GowingPackingId packingId, GowingPackable packable ) {
//	    super();
//
//	    _instanceId = instanceId;
//	    _packingId = packingId;
//	    _packable = packable;
//
//	}
//
//	@NotNull
//	public GowingInstanceId getInstanceId() {
//
//	    return _instanceId;
//
//	}
//
//	@NotNull
//	public GowingPackable getPackable() {
//
//	    return _packable;
//
//	}
//
////	public PackingId2 getPackingId() {
////
////	    return _packingId;
////
////	}
//
//	public String toString() {
//
//	    return "PackingAssociation( " + _instanceId + ", " + _packingId + ", " + _packable + " )";
//
//	}
//
//    }

    private final Accumulator<EntityTypeName> _highestPackingIdByType = new TreeAccumulator<EntityTypeName>();

    private GowingRequestorContext _requestorContext;

//    @SuppressWarnings("FieldCanBeLocal")
//    @NotNull
//    private final GowingTypeIndex _typeIndex;

    public StdGowingPackerContext() {
	super();

//	_typeIndex = typeIndex;

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

//    @Override
//    public void saveTypeAlias( P2ATokenizer.P2AToken typeIdToken, P2ATokenizer.P2AToken typeNameToken )
//	    throws UnPacker2ParseError {
//
//	int typeReferenceId = typeIdToken.intValue();
//	EntityTypeName typeName = new EntityTypeName( typeNameToken.stringValue() );
//
//	if ( InstanceId.lookupTypeName( typeReferenceId ) != null ) {
//
//	    throw new UnPacker2ParseError( "type reference id " + typeReferenceId + " already defined in type alias definition", typeIdToken );
//
//	}
//
//	Integer existingTypeReferenceId = findTypeReferenceId( typeName );
//	if ( existingTypeReferenceId != null ) {
//
//	    throw new UnPacker2ParseError(
//		    "type \"" + typeName + "\" already has type id " + existingTypeReferenceId
//		    + ", cannot associate it with type id " + typeReferenceId,
//		    typeNameToken
//	    );
//
//	}
//
//	_seenTypeNames.put( typeName, typeReferenceId );
//	_newTypeNames.add( typeReferenceId );
//	_usedTypeIds.put( typeReferenceId, typeName );
//	_nextTypeReferenceId = Math.max( typeReferenceId + 1, _nextTypeReferenceId );
//
//    }

//    /**
//     Allocate a new packing id using the next known-to-be-available id for a specified type.
//     <p/>
//     Two calls to this method on a particular instance of this class will never result in the same packing id being returned.
//     @param entityTypeName the type name that the new packing id should describe.
//     @return the new packing id.
//     @throws IllegalArgumentException if the about-to-be-allocated packing id is negative.
//     <p/>
//     I know of no way that this can occur unless you allocate well over 9e12 packet ids or use a value in that range as the second parameter
//     to {@link #allocatePackingId(EntityTypeName, long)}. Continue reading for more (pretty much useless) info.
//     <p/>
//     This exception could, in theory, be thrown if more than 9,223,372,036,854,775,807 ids are allocated for a given type (that huge number is the largest
//     value that can be represented in a 64-bit long). It is also possible to cause this exception to be thrown by passing absurdly large positive
//     values to {@link #allocatePackingId( EntityTypeName, long )} and then calling this method enough times to cause a type's next available id to wrap.
//     Should this exception ever be thrown then the state of this instance will have become undefined (not a place that you want to be so be careful about
//     how you call {@link #allocatePackingId( EntityTypeName, long )}). Note that this exception, if thrown, is likely to be thrown by one of the utility classes
//     that this class implementation uses. Consequently, the message in the thrown exception instance is not likely to be very meaningful (the stack traceback is your friend).
//     */
//
//    @Override
//    public synchronized PackingId2 allocatePackingId( EntityTypeName entityTypeName ) {
//
//	// Get the entity's factory. Throws an IllegalArgumentException if there is no such factory.
//
//	EntityTypeInfo typeInfo = getTypeInfo( entityTypeName );
//	long highestPackingIdForType = getHighestPackingIdForType( entityTypeName );
//	if ( highestPackingIdForType == Long.MAX_VALUE ) {
//
//	    throw new IllegalArgumentException( "no more automatically allocatable packing ids for " + typeInfo );
//
//	}
//
//	if ( highestPackingIdForType < 0 ) {
//
//	    highestPackingIdForType = 0;
//	    _highestPackingIdByType.forceCount( entityTypeName, 0 );
//
////	    throw new IllegalArgumentException( "attempt to allocate an automatically assigned negative id (" + highestPackingIdForType + ") for " +
////						typeInfo
////	    );
//
//	}
//
//	long allocatedPackingId = _highestPackingIdByType.accumulate( entityTypeName, 1 );
//	return new PackingId2( entityTypeName, allocatedPackingId );
//
////
////	return new PackingId( typeId, idWithinType );
//
//    }

//    @Override
//    public int getOrAllocateTypeReferenceId( Packable2 entity ) {
//
//	EntityTypeName typeName = entity.getInstanceId().getTypeName();
//	return getOrAllocateTypeReferenceId( typeName );
//
//    }

//    public int getOrAllocateTypeReferenceId( EntityTypeName typeName ) {
//
//	Integer typeReferenceId = _seenTypeNames.get( typeName );
//	if ( typeReferenceId == null ) {
//
//	    typeReferenceId = _nextTypeReferenceId;
//	    _nextTypeReferenceId += 1;
//	    _seenTypeNames.put( typeName, typeReferenceId );
//	    _newTypeNames.add( typeName );
//	    _usedTypeIds.put( typeReferenceId, typeName );
//
//	}
//
//	return typeReferenceId;
//
//    }

//    @Override
//    @Nullable
//    public EntityTypeName findTypeByTypeReferenceId( int typeReferenceId ) {
//
//	return _usedTypeIds.get( typeReferenceId );
//
//    }
//
//    @Override
//    @Nullable
//    public EntityTypeName getTypeByTypeReferenceId( int typeReferenceId ) {
//
//	EntityTypeName typeName = findTypeByTypeReferenceId( typeReferenceId );
//	if ( typeName == null ) {
//
//	    throw new IllegalArgumentException( "unknown type reference id " + typeReferenceId );
//
//	}
//
//	return typeName;
//
//    }

//    @Override
//    @Nullable
//    public Integer findTypeReferenceId( EntityTypeName typeName ) {
//
//	return _seenTypeNames.get( typeName );
//
//    }

//    @Override
//    public int getTypeReferenceId( EntityTypeName typeName ) {
//
//
//	Integer typeReferenceId = _seenTypeNames.get( typeName );
//	if ( typeReferenceId == null ) {
//
//	    throw new IllegalArgumentException( "unknown type name \"" + typeName + "\"" );
//
//	}
//
//	return typeReferenceId;
//
//    }

    @Override
    public void rememberPackableEntity( EntityName entityName, GowingPackable entity ) {

//	int typeReferenceId = getOrAllocateTypeReferenceId( entity.getInstanceId().getTypeName() );
//	try {

	    @SuppressWarnings("UnusedAssignment")
	    int typeReferenceId = GowingInstanceId.allocateTypeId( entity.getInstanceId().getTypeName() );

//	} catch ( Throwable e ) {
//
//	    e.printStackTrace();
//	    ObtuseUtil.doNothing();
//
//	}

//	PackingId2 packingId;
//
//	PackingAssociation pa = _seenInstanceIds.get( entity.getInstanceId() );
//	if ( pa == null ) {
//
//	    packingId = new PackingId2( entity.getInstanceId().getTypeName(), typeReferenceId, entity.getInstanceId().getEntityId() );
//	    pa = new PackingAssociation( entity.getInstanceId(), packingId, entity );
//
//	    Logger.logMsg( "instance id " + entity.getInstanceId() + " maps to packing id " + packingId );
//
//	} else {
//
//	    packingId = pa.getPackingId();
//
//	}

	if ( !_seenInstanceIds.containsKey( entity.getInstanceId() ) ) {

	    _seenInstanceIds.put( entity.getInstanceId(), new EntityNames( new LinkedList<EntityName>(), entity ) );

	}

	if ( entityName != null ) {

	    _seenInstanceIds.get( entity.getInstanceId() ).add( entityName );

	}

//	return entity.getInstanceId();

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

	LinkedList<Integer> rval = new LinkedList<Integer>( _newTypeIds );
	_newTypeIds.clear();

	return rval;

    }

//    @Override
//    @Nullable
//    public PackingAssociation findPackingAssociation( InstanceId instanceId ) {
//
//	return _seenInstanceIds.get( instanceId );
//
//    }

//    @Override
//    public long getHighestPackingIdForType( EntityTypeName entityTypeName ) {
//
//	long highestPackingIdForType = _highestPackingIdByType.getCount( entityTypeName );
//	return highestPackingIdForType;
//
//    }

//    @Override
//    @NotNull
//    public TypeIndex2 getTypeIndex() {
//
//	return _typeIndex;
//
//    }

//    }

//    }

//    /**
//     Find info about a type via its type name.
//     @param typeName the name of the type of interest.
//     @return the corresponding type's info or <code>null</code> if the specified type is unknown to this type index.
//     */
//
//    @Override
//    @Nullable
//    public EntityTypeInfo findTypeInfo( @NotNull EntityTypeName typeName ) {
//
//	return _typeIndex.findTypeInfo( typeName );
//
//    }
//
//    /**
//     Get info about a type via its type name when failure is not an option.
//     @param typeName the name of the type of interest.
//     @return the corresponding type's info.
//     @throws IllegalArgumentException if the specified type is not known to this type index.
//     */
//
//    @Override
//    @NotNull
//    public EntityTypeInfo getTypeInfo( @NotNull EntityTypeName typeName ) {
//
//	EntityTypeInfo rval = findTypeInfo( typeName );
//	if ( rval == null ) {
//
//	    throw new IllegalArgumentException( "unknown type \"" + typeName + "\"" );
//
//	}
//
//	return rval;
//
//    }
//
//    /**
//     Find info about a type via its type reference id.
//     @param typeReferenceId the id of the type of interest.
//     @return the corresponding type's info or <code>null</code> if the specified type reference id is unknown to this type index.
//     */
//
//    @Override
//    @Nullable
//    public EntityTypeInfo findTypeInfo( int typeReferenceId ) {
//
//	EntityTypeName typeName = findTypeByTypeReferenceId( typeReferenceId );
//	if ( typeName == null ) {
//
//	    return null;
//
//	}
//
//	return _typeIndex.findTypeInfo( typeName );
//
//    }
//
//    /**
//     Get info about a type via its type reference id when failure is not an option.
//     @param typeReferenceId the id of the type of interest.
//     @return the corresponding type's info.
//     @throws IllegalArgumentException if the specified type reference id is not known to this type index.
//     */
//
//    @Override
//    @NotNull
//    public EntityTypeInfo getTypeInfo( int typeReferenceId ) {
//
//	EntityTypeInfo rval = findTypeInfo( typeReferenceId );
//	if ( rval == null ) {
//
//	    throw new IllegalArgumentException( "unknown type reference id " + typeReferenceId );
//
//	}
//
//	return rval;
//
//    }
//
//    public EntityTypeInfo registerFactory( EntityFactory2 factory ) {
//
//	EntityTypeInfo rval = _typeIndex.findTypeInfo( factory.getTypeName() );
//	if ( rval == null ) {
//
//	    return _typeIndex.addFactory( factory );
//
//	} else {
//
//	    return rval;
//
//	}
//    }

//    }

//    @Override
//    public boolean isTypeNameKnown( EntityTypeName typeName ) {
//
//	return findTypeInfo( typeName ) != null;
//
//    }

//    }

//    /**
//     Allocate a new packing id with a specified id for the specified type.
//     <p/>
//     Note that there is no guarantee that the newly allocated packing id is unique within the specified type. The caller might want
//     to ensure that they avoid allocating duplicate packing ids (unless they like really weird "learning opportunities").
//
//     @param entityTypeName the type that the new packing id should describe.
//     @param idWithinType the desired id within the specified type.
//     @return the new packing id.
//
//     @throws IllegalArgumentException if the specified type id is negative.
//     */
//
//    @Override
//    public synchronized PackingId2 allocatePackingId( EntityTypeName entityTypeName, long idWithinType ) {
//
//	if ( _typeIndex.findTypeInfo( entityTypeName ) == null ) {
//
//	    throw new IllegalArgumentException( "no factory for type id " + entityTypeName );
//
//	}
//
//	if ( idWithinType < 0 ) {
//
//	    throw new IllegalArgumentException( "attempt to create a packing id with a negative id within type " + entityTypeName );
//
//	}
//
//	if ( idWithinType > _highestPackingIdByType.getCount( entityTypeName ) ) {
//
//	    _highestPackingIdByType.forceCount( entityTypeName, idWithinType );
//
//	}
//
//	return new PackingId2( entityTypeName, idWithinType );
//
//    }

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
	    public GowingPackable createEntity( @NotNull GowingUnPacker unPacker, @NotNull GowingPackedEntityBundle bundle, GowingEntityReference er ) {

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

		throw new IllegalArgumentException( TestPackableClass.class.getCanonicalName() + ":  expected version " + VERSION + " but received version " + bundle.getVersion() );

	    }

	    _payload = bundle.getNotNullField( new EntityName( "_payload" ) ).StringValue();

//	    Packable2ThingHolder2 holder = bundle.getNullableField( new EntityName( "_simple" ) );
	    _simpleReference = bundle.getNullableField( new EntityName( "_simple" ) ).EntityTypeReference();

	    _iValue = bundle.getNotNullField( new EntityName( "_iValue" ) ).intValue();

//	    holder = bundle.getNullableField( new EntityName( "_inner" ) );
	    _innerReference = bundle.getNullableField( new EntityName( "_inner" ) ).EntityTypeReference();

	}

	@NotNull
	@Override
	public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker packer ) {

	    GowingPackedEntityBundle rval = new GowingPackedEntityBundle(
		    ENTITY_TYPE_NAME,
		    VERSION,
		    super.bundleRoot( packer ),
//		    null,
		    packer.getPackingContext()
	    );

	    rval.addHolder( new GowingPackableEntityHolder( new EntityName( "_simple" ), _simple, packer, false ) );
	    rval.addHolder( new GowingPackableEntityHolder( new EntityName( "_inner" ), _inner, packer, false ) );
	    rval.addHolder( new GowingStringHolder( new EntityName( "_payload" ), _payload, true ) );
	    rval.addHolder( new GowingIntegerHolder( new EntityName( "_iValue" ), _iValue, false ) );
	    rval.addHolder( new GowingBooleanHolder( new EntityName( "_booleanValue" ), true, true ) );
	    rval.addHolder( new GowingDoubleHolder( new EntityName( "_doubleValue" ), Math.PI, false ) );
	    rval.addHolder( new GowingFloatHolder( new EntityName( "_floatValue" ), 1.1f, true ) );
	    rval.addHolder( new GowingShortHolder( new EntityName( "_shortValue" ), (short) 15, false ) );
	    rval.addHolder( new GowingLongHolder( new EntityName( "_longValue" ), 123L, true ) );
	    rval.addHolder( new GowingEntityNameHolder( new EntityName( "_entityName" ), new EntityName( "froz botnick"), true ) );

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
	    public GowingPackable createEntity( @NotNull GowingUnPacker unPacker, @NotNull GowingPackedEntityBundle bundle, GowingEntityReference er ) {

		return new SimplePackableClass( unPacker, bundle, er );

	    }

	};

	private final String _payload;

	public SimplePackableClass( @NotNull String payload ) {
	    super( new GowingNameMarkerThing() );

//	    context.registerFactory( FACTORY );

	    _payload = payload;

	}

	public SimplePackableClass( GowingUnPacker unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {
	    super( new GowingNameMarkerThing() );

	    if ( bundle.getVersion() != VERSION ) {

		throw new IllegalArgumentException( SimplePackableClass.class.getCanonicalName() + ":  expected version " + VERSION + " but received version " + bundle.getVersion() );

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

    //    public static void main( String[] args ) {
//
//	BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "Testing", null );
//
//	Logger.logMsg( "MAX_VALUE = " + Long.MAX_VALUE );
//	long v = Long.MAX_VALUE - 10;
//	for ( int i = 0; i < 20; i += 1 ) {
//
//	    Logger.logMsg( "v = " + v );
//	    v = v + 1;
//
//	}
//
//	TypeIndex2 typeIndex = new TypeIndex2( "testing" );
//	typeIndex.addFactory(
//		new EntityFactory2( TestPackableClass.ENTITY_TYPE_NAME ) {
//
//		    @Override
//		    public Packable2 createEntity( @NotNull UnPacker2 unPacker, PackableState state ) {
//
//			throw new IllegalArgumentException( "unimplemented" );
//
//		    }
//
//		}
//	);
////	typeIndex.appendFactory(
////		new EntityFactory2( "Barney" ) {
////
////		    @Override
////		    public Packable2 createEntity( @NotNull UnPacker2 unPacker ) {
////
////			throw new IllegalArgumentException( "unimplemented" );
////
////		    }
////
////		}
////	);
//
//	StdPackingContext2 context = new StdPackingContext2( typeIndex );
//
//	Logger.logMsg( "force-allocated id is " + context.allocatePackingId( TestPackableClass.ENTITY_TYPE_NAME, Long.MAX_VALUE - 10 ) );
//	for ( int i = 0; i < 20; i += 1 ) {
//
//	    Logger.logMsg( "auto-allocated id is " + context.allocatePackingId( TestPackableClass.ENTITY_TYPE_NAME ) );
//
//	}
//
//    }

}
