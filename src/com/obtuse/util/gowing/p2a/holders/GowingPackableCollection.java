package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Provide an easy way to pack an arbitrary collection and retrieve it during the subsequent unpack operation.
 <p/>
 The idea is to create an instance of this class in your {@link GowingPackable#bundleThyself} method and queue it for packing.
 When the unpack happens, you preserve the {@link GowingEntityReference} to the {@link GowingPackableCollection} in your unpack constructor.
 When your {@link GowingPackable#finishUnpacking} method gets called, you resolve that {@link GowingEntityReference} and then spin through
 the {@link GowingPackableCollection} retrieving the entities and popping them into your collection.
 <p/>
 Here's an example using a {@link java.util.SortedSet} as the collection to be packed.
 We start with the declaration of the {@link java.util.SortedSet} for clarity and then the {@link GowingPackable#bundleThyself} method:
 <blockquote>
 <pre> private SortedSet&lt;String> _myCollection = new TreeSet&lt;String>();
 .
 .
 .
 public PackedEntityBundle bundleThyself( boolean isPackingSuper, Packer2 packer ) {
 PackedEntityBundle bundle = new ...;
 Packable2Collection&lt;String> pc = new Packable2Collection&lt;String>( _myCollection );
 bundle.addHolder( new StringHolder2( new EntityName( "_xxx" ), (Packable2)pc, packer, true ) );
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

@SuppressWarnings("unchecked")
public class GowingPackableCollection<E> extends ArrayList<E> implements GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GowingPackableCollection.class );

    @SuppressWarnings("FieldCanBeLocal")
    private static final int VERSION = 1;

    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    private List _things;

    public static GowingPackableCollection EMPTY = new GowingPackableCollection<>( Collections.EMPTY_LIST );

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
        public GowingPackable createEntity( final @NotNull GowingUnPacker unPacker, final @NotNull GowingPackedEntityBundle bundle, final @NotNull GowingEntityReference er ) {

            return new GowingPackableCollection( unPacker, bundle );

        }

    };

    public GowingPackableCollection() {

        super();

        _things = null;

    }

    public GowingPackableCollection( final Collection<? extends E> collection ) {

        super( collection );

        _things = null;

    }

    public GowingPackableCollection(
            @SuppressWarnings("unused") final GowingUnPacker unPacker,
            final GowingPackedEntityBundle bundle
    ) {

        super();

        if ( bundle.getVersion() != VERSION ) {

            throw new IllegalArgumentException( GowingPackableCollection.class.getCanonicalName() +
                                                ":  expected version " +
                                                VERSION +
                                                " but received version " +
                                                bundle.getVersion() );

        }

        int ix = 0;
        _things = new FormattingLinkedList();
        while ( true ) {

            GowingPackableThingHolder holder = bundle.get( new EntityName( "_" + ix ) );
            if ( holder == null ) {

                break;

            }

            _things.add( holder.getObjectValue() );

            ix += 1;

        }

        ObtuseUtil.doNothing();

    }

    /**
     Copy a {@link GowingPackableCollection} into a {@link Collection}.
     @param dest the destination {@link Collection}.
     @param unPacker the {@link GowingUnPacker} that is driving the car right now.
     @param src the {@link GowingEntityReference} to the source {@link GowingPackableCollection}
     (must be <em>finished</em> in the sense that {@code unPacker.isEntityFinished(src)} returns {@code true}).
     @param <T> the kind of thing that the two collections contain
     (in theory, this is implied by how the caller provided {@code dest} is declared;
     in practise, it doesn't matter at all for this method although something is likely to explode with a
     {@link ClassCastException} if what is in {@code src} does not match what has been declared to be in {@code dest}).
     @return {@code true} if {@code dest} has changed as a result of this call; {@code false} otherwise.
     */

    public static <T> boolean addAll(
            final @NotNull Collection<? super T> dest,
            final @NotNull GowingUnPacker unPacker,
            final @NotNull GowingEntityReference src
    ) {

        if ( unPacker.isEntityFinished( src ) ) {

            @SuppressWarnings("unchecked") GowingPackableCollection<T> tmp =
                    (GowingPackableCollection<T>)unPacker.resolveReference( src ).orElse( EMPTY );

            return dest.addAll( tmp );

        } else {

            return false;

        }

    }

    /**
     Finish unpacking.
     <p>VERY IMPORTANT POINT: we're not finished until every single {@link GowingPackable} instance that we refer to is finished.
     Violating this rule will result in all sorts of chaos because those who reference this instance will expect to be able to
     use our {@link GowingPackable} instances to finish themselves.</p>
     @param unPacker the {@link GowingUnPacker} that running this circus.
     @return {@code true} if we are finished; {@code false} otherwise.
     */

    @Override
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        ObtuseUtil.doNothing();

        for ( Object obj : _things ) {

            if ( obj instanceof GowingEntityReference ) {

                if ( !unPacker.isEntityFinished( (GowingEntityReference)obj ) ) {

                    return false;

                }

            }

        }

        ObtuseUtil.doNothing();

        for ( Object obj : _things ) {

            if ( obj == null ) {

                add( null );

            } else if ( obj instanceof GowingEntityReference ) {

                E value = (E)unPacker.resolveReference( (GowingEntityReference)obj ).orElse( null );
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
    public GowingPackedEntityBundle bundleThyself( final boolean isPackingSuper, final @NotNull GowingPacker packer ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                packer.getPackingContext()
        );

        int ix = 0;

        for ( Object obj : this ) {

            GowingPackableKeyValuePair.packObj( bundle, new EntityName( "_" + ix ), obj, packer );

            ix += 1;

        }

        return bundle;

    }

    public String toString() {

        return "GowingPackableCollection( size=" + size() + " )";

    }

}
