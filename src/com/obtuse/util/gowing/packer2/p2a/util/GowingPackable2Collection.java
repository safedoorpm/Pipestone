package com.obtuse.util.gowing.packer2.p2a.util;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.packer2.*;
import com.obtuse.util.gowing.packer2.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 Provide an easy way to pack an arbitrary collection and retrieve it during the subsequent unpack operation.
 <p/>
 The idea is to create an instance of this class in your {@link GowingPackable2#bundleThyself} method and queue it for packing.
 When the unpack happens, you preserve the {@link GowingEntityReference} to the {@link GowingPackable2Collection} in your unpack constructor.
 When your {@link GowingPackable2#finishUnpacking} method gets called, you resolve that {@link GowingEntityReference} and then spin through
 the {@link GowingPackable2Collection} retrieving the entities and popping them into your collection.
 <p/>
 Here's an example using a {@link java.util.SortedSet} as the collection to be packed.
 We start with the declaration of the {@link java.util.SortedSet} for clarity and then the {@link GowingPackable2#bundleThyself} method:
 <blockquote>
 <pre> private SortedSet&lt;String> _myCollection = new TreeSet&lt;String>();
 .
 .
 .
 public PackedEntityBundle bundleThyself( boolean isPackingSuper, Packer2 packer ) {
     PackedEntityBundle bundle = new ...;
     Packable2Collection&lt;String> pc = new Packable2Collection&lt;String>( _myCollection );
     bundle.addHolder( new StringHolder2( new EntityName2( "_xxx" ), (Packable2)pc, packer, true ) );
     return bundle;
 }
 .
 .
 .
 </pre>
 </blockquote>
 Next we see a list to hold the {@link GowingEntityReference}s and the unpack constructor which populates this list:
 <blockquote>
 <pre> .
 .
 .
 private List&lt;GowingEntityReference> _erList = new LinkedList&lt;GowingEntityReference>();
 .
 .
 .
 public ExampleClass( UnPacker2 unPacker, PackedEntityBundle bundle ) {
     super();

 </pre>
 </blockquote>
 */

public class GowingPackable2Collection<E> extends LinkedList<E> implements GowingPackable2 {

    private static final EntityTypeName2 ENTITY_TYPE_NAME = new EntityTypeName2( GowingPackable2Collection.class.getCanonicalName() );

    @SuppressWarnings("FieldCanBeLocal")
    private static final int VERSION = 1;

    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    private List _things;

    public static final GowingEntityFactory2 FACTORY = new GowingEntityFactory2( ENTITY_TYPE_NAME ) {

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
	public GowingPackable2 createEntity( @NotNull GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {

	    return new GowingPackable2Collection( unPacker, bundle );

	}

    };

    public GowingPackable2Collection() {
	super();

	_things = null;

    }

    public GowingPackable2Collection( Collection<? extends E> collection ) {
	super( collection );

	_things = null;

    }

    public GowingPackable2Collection( GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle ) {
	super();

	if ( bundle.getVersion() != VERSION ) {

	    throw new IllegalArgumentException( GowingPackable2Collection.class.getCanonicalName() + ":  expected version " + VERSION + " but received version " + bundle.getVersion() );

	}

	int ix = 0;
	_things = new FormattingLinkedList();
	while ( true ) {

	    GowingPackable2ThingHolder2 holder = bundle.get( new EntityName2( "_" + ix ) );
	    if ( holder == null ) {

		break;

	    }

	    _things.add( holder.getObjectValue() );

	    ix += 1;

	}

	ObtuseUtil.doNothing();

    }

    @Override
    public boolean finishUnpacking( GowingUnPacker2 unPacker ) {

	ObtuseUtil.doNothing();

	for ( Object obj : _things ) {

	    if ( obj == null ) {

		add( null );

	    } else if ( obj instanceof GowingEntityReference ) {

		E value = (E) unPacker.resolveReference( (GowingEntityReference) obj );
		add( value );

	    } else {

		add( (E)obj );

	    }

	}

	_things = null;

	return true;

    }

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

	return _instanceId;

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker2 packer ) {

	GowingPackedEntityBundle rval = new GowingPackedEntityBundle(
		ENTITY_TYPE_NAME,
		VERSION,
		null,
		packer.getPackingContext()
	);

	int ix = 0;

	for ( Object obj : this ) {

	    GowingPackable2KeyValuePair.packObj( rval, new EntityName2( "_" + ix ), obj, packer );

	    ix += 1;

	}

	return rval;

    }

}
