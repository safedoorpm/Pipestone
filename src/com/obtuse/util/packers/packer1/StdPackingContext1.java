package com.obtuse.util.packers.packer1;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.Accumulator;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.TreeAccumulator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Provide a packing id space for entities that are packed and/or unpacked together.
 */

public class StdPackingContext1 implements PackingContext1 {

    private final Accumulator<Short> _highestPackingIdByType = new TreeAccumulator<Short>();
//    private final SortedMap<String,Short> _knownTypes = new TreeMap<String, Short>();

    @NotNull
    private final TypeIndex1 _typeIndex;

    public StdPackingContext1( @NotNull TypeIndex1 typeIndex ) {
	super();

	_typeIndex = typeIndex;

    }

    /**
     Allocate a new packing id using the next known-to-be-available id for a specified type.
     <p/>
     Two calls to this method on a particular instance of this class will never result in the same packing id being returned.
     @param typeId the type id that the new packing id should describe.
     @return the new packing id.
     @throws IllegalArgumentException if the about-to-be-allocated packing id is negative.
     <p/>
     I know of no way that this can occur unless you allocate well over 9e12 packet ids or use a value in that range as the second parameter
     to {@link #allocatePackingId(short, long)}. Continue reading for more (pretty much useless) info.
     <p/>
     This exception could, in theory, be thrown if more than 9,223,372,036,854,775,807 ids are allocated for a given type (that huge number is the largest
     value that can be represented in a 64-bit long). It is also possible to cause this exception to be thrown by passing absurdly large positive
     values to {@link #allocatePackingId( short, long )} and then calling this method enough times to cause a type's next available id to wrap.
     Should this exception ever be thrown then the state of this instance will have become undefined (not a place that you want to be so be careful about
     how you call {@link #allocatePackingId( short, long )}). Note that this exception, if thrown, is likely to be thrown by one of the utility classes
     that this class implementation uses. Consequently, the message in the thrown exception instance is not likely to be very meaningful (the stack traceback is your friend).
     */

    @Override
    public synchronized PackingId1 allocatePackingId( short typeId ) {

	// Get the entity's factory. Throws an IllegalArgumentException if there is no such factory.

	EntityFactory1 entityFactory = getFactory( typeId );
	long highestPackingIdForType = getHighestPackingIdForType( typeId );
	if ( highestPackingIdForType == Long.MAX_VALUE ) {

	    throw new IllegalArgumentException( "no more automatically allocatable packing ids for " + entityFactory );

	}

	if ( highestPackingIdForType < 0 ) {

	    throw new IllegalArgumentException( "attempt to allocate an automatically assigned negative id (" + highestPackingIdForType + ") for " +
						entityFactory
	    );

	}

	long allocatedPackingId = _highestPackingIdByType.accumulate( typeId, 1 );
	return new PackingId1( typeId, allocatedPackingId );

//
//	return new PackingId( typeId, idWithinType );

    }

    @Override
    public long getHighestPackingIdForType( short typeId ) {

	long highestPackingIdForType = _highestPackingIdByType.getCount( typeId );
	return highestPackingIdForType;

    }

    @NotNull
    public TypeIndex1 getTypeIndex() {

	return _typeIndex;

    }

    @Override
    public short getTypeId( String typeName ) {

	short typeId = _typeIndex.getFactory( typeName ).getEntityTypeId();

	return typeId;

    }

    @Override
    @NotNull
    public String getTypeName( short typeId ) {

	String typeName = _typeIndex.getFactory( typeId ).getEntityName();

	return typeName;

    }

    /**
     Find an {@link EntityFactory1} via its type name.
     @param typeName the name of the type of interest.
     @return the corresponding entity factory or <code>null</code> if no such factory exists within this type index.
     */

    @Override
    @Nullable
    public EntityFactory1 findFactory( @NotNull String typeName ) {

	return _typeIndex.findFactory( typeName );

    }

    /**
     Get an {@link EntityFactory1} via its type name when failure is not an option.
     @param typeName the name of the type of interest.
     @return the corresponding entity factory.
     @throws IllegalArgumentException if no such factory exists within this type index.
     */

    @Override
    @NotNull
    public EntityFactory1 getFactory( @NotNull String typeName ) {

	return _typeIndex.getFactory( typeName );

    }

    /**
     Find an {@link EntityFactory1} via its type id.
     @param typeId the id of the type of interest.
     @return the corresponding entity factory or <code>null</code> if no such factory exists within this type index.
     */

    @Override
    @Nullable
    public EntityFactory1 findFactory( short typeId ) {

	return _typeIndex.findFactory( typeId );

    }

    /**
     Get an {@link EntityFactory1} via its type id when failure is not an option.
     @param typeId the id of the type of interest.
     @return the corresponding entity factory.
     @throws IllegalArgumentException if no such factory exists within this type index.
     */

    @Override
    @NotNull
    public EntityFactory1 getFactory( short typeId ) {

	return _typeIndex.getFactory( typeId );

    }

    @Override
    public boolean isTypeNameKnown( String typeName ) {

	return findFactory( typeName ) != null;

    }

    @Override
    public boolean isTypeIdKnown( short typeId ) {

	return findFactory( typeId ) != null;

    }

    /**
     Allocate a new packing id with a specified id for the specified type.
     <p/>
     Note that there is no guarantee that the newly allocated packing id is unique within the specified type. The caller might want
     to ensure that they avoid allocating duplicate packing ids (unless they like really weird "learning opportunities").

     @param typeId the type that the new packing id should describe.
     @param idWithinType the desired id within the specified type.
     @return the new packing id.

     @throws IllegalArgumentException if the specified type id is negative.
     */

    @Override
    public synchronized PackingId1 allocatePackingId( short typeId, long idWithinType ) {

	if ( _typeIndex.findFactory( typeId ) == null ) {

	    throw new IllegalArgumentException( "no factory for type id " + typeId );

	}

	if ( idWithinType < 0 ) {

	    throw new IllegalArgumentException( "attempt to create a packing id with a negative id within type " + typeId );

	}

	if ( idWithinType > _highestPackingIdByType.getCount( typeId ) ) {

	    _highestPackingIdByType.forceCount( typeId, idWithinType );

	}

	return new PackingId1( typeId, idWithinType );

    }

    public static class TestPackableClass implements Packable1 {

	@Override
	public void finishUnpacking( UnPacker1 unPacker ) {

	}

	@Override
	public void packThyself( Packer1 packer ) {

	}

    }

    public static void main( String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "Testing", null );

	Logger.logMsg( "MAX_VALUE = " + Long.MAX_VALUE );
	long v = Long.MAX_VALUE - 10;
	for ( int i = 0; i < 20; i += 1 ) {

	    Logger.logMsg( "v = " + v );
	    v = v + 1;

	}

	TypeIndex1 typeIndex = new TypeIndex1( "testing" );
	typeIndex.appendFactory(
		new EntityFactory1( "Fred" ) {

		    @Override
		    public Packable1 createEntity( @NotNull UnPacker1 unPacker ) {

			throw new IllegalArgumentException( "unimplemented" );

		    }

		}
	);
	typeIndex.appendFactory(
		new EntityFactory1( "Barney" ) {

		    @Override
		    public Packable1 createEntity( @NotNull UnPacker1 unPacker ) {

			throw new IllegalArgumentException( "unimplemented" );

		    }

		}
	);

	StdPackingContext1 context = new StdPackingContext1( typeIndex );

	short typeId = context.getTypeId( "Fred" );
	Logger.logMsg( "force-allocated id is " + context.allocatePackingId( typeId, Long.MAX_VALUE - 10 ) );
	for ( int i = 0; i < 20; i += 1 ) {

	    Logger.logMsg( "auto-allocated id is " + context.allocatePackingId( typeId ) );

	}

    }

}
