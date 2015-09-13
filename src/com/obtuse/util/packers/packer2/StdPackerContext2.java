package com.obtuse.util.packers.packer2;

import com.obtuse.util.*;
import com.obtuse.util.packers.packer2.p2a.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Provide a packing id space for entities that are packed and/or unpacked together.
 */

public class StdPackerContext2 implements PackerContext2 {

//    private final SortedMap<EntityTypeName2,Integer> _seenTypeNames = new TreeMap<EntityTypeName2, Integer>();
//    private final SortedMap<Integer,EntityTypeName2> _usedTypeIds = new TreeMap<Integer, EntityTypeName2>();

    private final SortedSet<Integer> _seenTypeIds = new TreeSet<Integer>();
    private final SortedSet<Integer> _newTypeIds = new TreeSet<Integer>();

    private final SortedMap<InstanceId,Packable2> _seenInstanceIds = new TreeMap<InstanceId,Packable2>();

    private int _nextTypeReferenceId = 1;

    public static class PackingAssociation {

	private final InstanceId _instanceId;

	private final PackingId2 _packingId;

	private final Packable2 _packable;

	public PackingAssociation( InstanceId instanceId, PackingId2 packingId, Packable2 packable ) {
	    super();

	    _instanceId = instanceId;
	    _packingId = packingId;
	    _packable = packable;

	}

	public InstanceId getInstanceId() {

	    return _instanceId;

	}

	public Packable2 getPackable() {

	    return _packable;

	}

//	public PackingId2 getPackingId() {
//
//	    return _packingId;
//
//	}

	public String toString() {

	    return "PackingAssociation( " + _instanceId + ", " + _packingId + ", " + _packable + " )";

	}

    }

    private final Accumulator<EntityTypeName2> _highestPackingIdByType = new TreeAccumulator<EntityTypeName2>();

    @NotNull
    private final TypeIndex2 _typeIndex;

    public StdPackerContext2( @NotNull TypeIndex2 typeIndex ) {
	super();

	_typeIndex = typeIndex;

    }

//    @Override
//    public void saveTypeAlias( P2ATokenizer.P2AToken typeIdToken, P2ATokenizer.P2AToken typeNameToken )
//	    throws UnPacker2ParseError {
//
//	int typeReferenceId = typeIdToken.intValue();
//	EntityTypeName2 typeName = new EntityTypeName2( typeNameToken.stringValue() );
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
//     to {@link #allocatePackingId(EntityTypeName2, long)}. Continue reading for more (pretty much useless) info.
//     <p/>
//     This exception could, in theory, be thrown if more than 9,223,372,036,854,775,807 ids are allocated for a given type (that huge number is the largest
//     value that can be represented in a 64-bit long). It is also possible to cause this exception to be thrown by passing absurdly large positive
//     values to {@link #allocatePackingId( EntityTypeName2, long )} and then calling this method enough times to cause a type's next available id to wrap.
//     Should this exception ever be thrown then the state of this instance will have become undefined (not a place that you want to be so be careful about
//     how you call {@link #allocatePackingId( EntityTypeName2, long )}). Note that this exception, if thrown, is likely to be thrown by one of the utility classes
//     that this class implementation uses. Consequently, the message in the thrown exception instance is not likely to be very meaningful (the stack traceback is your friend).
//     */
//
//    @Override
//    public synchronized PackingId2 allocatePackingId( EntityTypeName2 entityTypeName ) {
//
//	// Get the entity's factory. Throws an IllegalArgumentException if there is no such factory.
//
//	EntityTypeInfo2 typeInfo = getTypeInfo( entityTypeName );
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
//	EntityTypeName2 typeName = entity.getInstanceId().getTypeName();
//	return getOrAllocateTypeReferenceId( typeName );
//
//    }

//    public int getOrAllocateTypeReferenceId( EntityTypeName2 typeName ) {
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
//    public EntityTypeName2 findTypeByTypeReferenceId( int typeReferenceId ) {
//
//	return _usedTypeIds.get( typeReferenceId );
//
//    }
//
//    @Override
//    @Nullable
//    public EntityTypeName2 getTypeByTypeReferenceId( int typeReferenceId ) {
//
//	EntityTypeName2 typeName = findTypeByTypeReferenceId( typeReferenceId );
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
//    public Integer findTypeReferenceId( EntityTypeName2 typeName ) {
//
//	return _seenTypeNames.get( typeName );
//
//    }

//    @Override
//    public int getTypeReferenceId( EntityTypeName2 typeName ) {
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
    public void rememberPackableEntity( Packable2 entity ) {

//	int typeReferenceId = getOrAllocateTypeReferenceId( entity.getInstanceId().getTypeName() );
	int typeReferenceId = InstanceId.allocateTypeId( entity.getInstanceId().getTypeName() );

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

	_seenInstanceIds.put( entity.getInstanceId(), entity );

//	return entity.getInstanceId();

    }

    @Override
    @NotNull
    public Packable2 getInstance( InstanceId instanceId ) {

	return _seenInstanceIds.get( instanceId );

    }

    public int rememberTypeName( EntityTypeName2 typeName ) {

	int typeId = InstanceId.allocateTypeId( typeName );

	if ( !_seenTypeIds.contains( typeId ) ) {

	    _seenTypeIds.add( typeId );
	    _newTypeIds.add( typeId );

	}

	return typeId;

    }

    @Override
    @NotNull
    public Collection<InstanceId> getSeenInstanceIds() {

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
//    public long getHighestPackingIdForType( EntityTypeName2 entityTypeName ) {
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
//    public EntityTypeInfo2 findTypeInfo( @NotNull EntityTypeName2 typeName ) {
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
//    public EntityTypeInfo2 getTypeInfo( @NotNull EntityTypeName2 typeName ) {
//
//	EntityTypeInfo2 rval = findTypeInfo( typeName );
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
//    public EntityTypeInfo2 findTypeInfo( int typeReferenceId ) {
//
//	EntityTypeName2 typeName = findTypeByTypeReferenceId( typeReferenceId );
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
//    public EntityTypeInfo2 getTypeInfo( int typeReferenceId ) {
//
//	EntityTypeInfo2 rval = findTypeInfo( typeReferenceId );
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
//    public EntityTypeInfo2 registerFactory( EntityFactory2 factory ) {
//
//	EntityTypeInfo2 rval = _typeIndex.findTypeInfo( factory.getTypeName() );
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
//    public boolean isTypeNameKnown( EntityTypeName2 typeName ) {
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
//    public synchronized PackingId2 allocatePackingId( EntityTypeName2 entityTypeName, long idWithinType ) {
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

    public static class TestPackableClass extends AbstractPackableEntity2 implements Packable2 {

	private static final EntityTypeName2 ENTITY_NAME = new EntityTypeName2( StdPackerContext2.TestPackableClass.class );

	private static final int VERSION = 1;

	public static EntityFactory2 FACTORY = new EntityFactory2( ENTITY_NAME ) {

	    @Override
	    @NotNull
	    public Packable2 createEntity( @NotNull UnPacker2 unPacker, PackedEntityBundle bundle ) {

		return new TestPackableClass( unPacker, bundle );

	    }

	};
	private final String _payload;
	private SimplePackableClass _simple;
	private EntityReference _simpleReference;

	private final int _iValue;

	private TestPackableClass _inner;
	private EntityReference _innerReference;

	public TestPackableClass( @NotNull String payload, @Nullable TestPackableClass inner, @Nullable SimplePackableClass simple ) {
	    super( ENTITY_NAME );

//	    context.registerFactory( FACTORY );

	    _simple = simple;
	    _inner = inner;
	    _payload = payload;
	    _iValue = 42;

	}

	public TestPackableClass( UnPacker2 unPacker, PackedEntityBundle bundle ) {
	    super( ENTITY_NAME );

	    if ( bundle.getVersion() != VERSION ) {

		throw new IllegalArgumentException( TestPackableClass.class.getCanonicalName() + ":  expected version " + VERSION + " but received version " + bundle.getVersion() );

	    }

	    _payload = bundle.getNotNullField( new EntityName2( "_payload" ) ).StringValue();

//	    Packable2ThingHolder2 holder = bundle.getNullableField( new EntityName2( "_simple" ) );
	    _simpleReference = bundle.getNullableField( new EntityName2( "_simple" ) ).EntityTypeReference();

	    _iValue = bundle.getNotNullField( new EntityName2( "_iValue" ) ).intValue();

//	    holder = bundle.getNullableField( new EntityName2( "_inner" ) );
	    _innerReference = bundle.getNullableField( new EntityName2( "_inner" ) ).EntityTypeReference();

	}

	@Override
	@NotNull
	public PackedEntityBundle bundleThyself( boolean isPackingSuper, Packer2 packer ) {

	    PackedEntityBundle rval = new PackedEntityBundle(
		    ENTITY_NAME,
		    VERSION,
		    super.bundleThyself( true, packer ),
		    packer.getPackingContext()
	    );

	    rval.addHolder( new PackableEntityHolder2( new EntityName2( "_simple" ), _simple, packer, false ) );
	    rval.addHolder( new PackableEntityHolder2( new EntityName2( "_inner" ), _inner, packer, false ) );
	    rval.addHolder( new StringHolder2( new EntityName2( "_payload" ), _payload, true ) );
	    rval.addHolder( new IntegerHolder2( new EntityName2( "_iValue" ), _iValue, false ) );
	    rval.addHolder( new BooleanHolder2( new EntityName2( "_booleanValue" ), true, true ) );
	    rval.addHolder( new DoubleHolder2( new EntityName2( "_doubleValue" ), Math.PI, false ) );
	    rval.addHolder( new FloatHolder2( new EntityName2( "_floatValue" ), 1.1f, true ) );
	    rval.addHolder( new ShortHolder2( new EntityName2( "_shortValue" ), (short) 15, false ) );
	    rval.addHolder( new LongHolder2( new EntityName2( "_longValue" ), 123L, true ) );

	    return rval;

	}

	@Override
	public void finishUnpacking( UnPacker2 unPacker ) {

	    _simple = (SimplePackableClass)unPacker.resolveReference( _simpleReference );
	    _inner = (TestPackableClass)unPacker.resolveReference( _innerReference );

	}

	public String toString() {

	    return "StdPackingContext2.TestPackableClass( \"" + _payload + "\", " + _iValue + " )";

	}

    }

    public static class SimplePackableClass extends AbstractPackableEntity2 implements Packable2 {

	private static final EntityTypeName2 ENTITY_NAME = new EntityTypeName2( StdPackerContext2.SimplePackableClass.class );

	private static final int VERSION = 42;

	public static EntityFactory2 FACTORY = new EntityFactory2( ENTITY_NAME ) {

	    @Override
	    @NotNull
	    public Packable2 createEntity( @NotNull UnPacker2 unPacker, PackedEntityBundle bundle ) {

		return new SimplePackableClass( unPacker, bundle );

	    }

	};
	private final String _payload;

	public SimplePackableClass( @NotNull String payload ) {
	    super( ENTITY_NAME );

//	    context.registerFactory( FACTORY );

	    _payload = payload;

	}

	public SimplePackableClass( UnPacker2 unPacker, PackedEntityBundle bundle ) {
	    super( ENTITY_NAME );

	    if ( bundle.getVersion() != VERSION ) {

		throw new IllegalArgumentException( SimplePackableClass.class.getCanonicalName() + ":  expected version " + VERSION + " but received version " + bundle.getVersion() );

	    }

	    _payload = bundle.getNotNullField( new EntityName2( "_thing" ) ).StringValue();


	}

	@Override
	@NotNull
	public PackedEntityBundle bundleThyself( boolean isPackingSuper, Packer2 packer ) {

	    PackedEntityBundle rval = new PackedEntityBundle(
		    ENTITY_NAME,
		    VERSION, super.bundleThyself( true, packer ),
		    packer.getPackingContext()
	    );

	    rval.addHolder( new StringHolder2( new EntityName2( "_thing" ), _payload, true ) );

	    return rval;

	}

	@Override
	public void finishUnpacking( UnPacker2 unPacker ) {

	    // Nothing to be done here.

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
