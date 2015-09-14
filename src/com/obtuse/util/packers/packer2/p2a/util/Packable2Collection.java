package com.obtuse.util.packers.packer2.p2a.util;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.packers.packer2.*;
import com.obtuse.util.packers.packer2.p2a.EntityReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 Provide an easy way to pack an arbitrary collection and retrieve it during the subsequent unpack operation.
 <p/>
 The idea is to create an instance of this class in your {@link Packable2#bundleThyself} method and queue it for packing.
 When the unpack happens, you preserve the {@link EntityReference} to the {@link Packable2Collection} in your unpack constructor.
 When your {@link Packable2#finishUnpacking} method gets called, you resolve that {@link EntityReference} and then spin through
 the {@link Packable2Collection} retrieving the entities and popping them into your collection.
 <p/>
 Here's an example using a {@link java.util.SortedSet} as the collection to be packed.
 We start with the declaration of the {@link java.util.SortedSet} for clarity and then the {@link Packable2#bundleThyself} method:
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
 Next we see a list to hold the {@link EntityReference}s and the unpack constructor which populates this list:
 <blockquote>
 <pre> .
 .
 .
 private List&lt;EntityReference> _erList = new LinkedList&lt;EntityReference>();
 .
 .
 .
 public ExampleClass( UnPacker2 unPacker, PackedEntityBundle bundle ) {
     super();

 </pre>
 </blockquote>
 */

public class Packable2Collection<E> extends LinkedList<E> implements Packable2 {

    private static final EntityTypeName2 ENTITY_TYPE_NAME = new EntityTypeName2( Packable2Collection.class.getCanonicalName() );

    @SuppressWarnings("FieldCanBeLocal")
    private static final int VERSION = 1;

    private final InstanceId _instanceId = new InstanceId( ENTITY_TYPE_NAME );

    private List _things;

    public static final EntityFactory2 FACTORY = new EntityFactory2( ENTITY_TYPE_NAME ) {

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
	public Packable2 createEntity( @NotNull UnPacker2 unPacker, PackedEntityBundle bundle, EntityReference er ) {

	    return new Packable2Collection( unPacker, bundle );

	}

    };

    public Packable2Collection() {
	super();

	_things = null;

    }

    public Packable2Collection( Collection<? extends E> collection ) {
	super( collection );

	_things = null;

    }

    public Packable2Collection( UnPacker2 unPacker, PackedEntityBundle bundle ) {
	super();

	if ( bundle.getVersion() != VERSION ) {

	    throw new IllegalArgumentException( Packable2Collection.class.getCanonicalName() + ":  expected version " + VERSION + " but received version " + bundle.getVersion() );

	}

	int ix = 0;
	_things = new FormattingLinkedList();
	while ( true ) {

	    Packable2ThingHolder2 holder = bundle.get( new EntityName2( "_" + ix ) );
	    if ( holder == null ) {

		break;

	    }

	    _things.add( holder.getObjectValue() );

	    ix += 1;

	}

	ObtuseUtil.doNothing();

    }

    @Override
    public boolean finishUnpacking( UnPacker2 unPacker ) {

	ObtuseUtil.doNothing();

	for ( Object obj : _things ) {

	    if ( obj == null ) {

		add( null );

	    } else if ( obj instanceof EntityReference ) {

		E value = (E) unPacker.resolveReference( (EntityReference) obj );
		add( value );

	    } else {

		add( (E)obj );

	    }

	}

	_things = null;

	return true;

    }

    @Override
    public InstanceId getInstanceId() {

	return _instanceId;

    }

    @NotNull
    @Override
    public PackedEntityBundle bundleThyself( boolean isPackingSuper, Packer2 packer ) {

	PackedEntityBundle rval = new PackedEntityBundle(
		ENTITY_TYPE_NAME,
		VERSION,
		null,
		packer.getPackingContext()
	);

	int ix = 0;

	for ( Object obj : this ) {

	    Packable2KeyValuePair.packObj( rval, new EntityName2( "_" + ix ), obj, packer );

	    ix += 1;

	}

	return rval;

    }

}
