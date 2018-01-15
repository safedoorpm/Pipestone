package com.obtuse.util.gowing;

import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.p2a.holders.GowingStringHolder;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A reasonable base class for something that is packable.
 */

public abstract class GowingAbstractPackableEntity implements GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GowingAbstractPackableEntity.class );

    private static final int VERSION = 1;

    private static final EntityName GOWING_NAME = new EntityName( "_g" );

    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    private final GowingNameMarkerThing _gowingThing;

    protected static class GowingNameMarkerThing {

        private final String _name;

        public GowingNameMarkerThing() {

            super();

            _name = "!";

        }

        private GowingNameMarkerThing( final String name ) {

            super();

            _name = name;

        }

        protected String getGowingStringName() {

            return _name;

        }

        public String toString() {

            return "GowingNameMarkerThing( " + ObtuseUtil.enquoteToJavaString( _name ) + " )";

        }

    }

    /**
     Provide a simple but non-trivial constructor to ensure that the implementor of our descendant classes at least ponders the question of which of our constructors to call.
     <p>Put another way, use this constructor in all situations except when unpacking a packed instance
     (use the {@link #GowingAbstractPackableEntity(GowingUnPacker,GowingPackedEntityBundle)} constructor when unpacking
     a packed instance).</p>
     <p>If you look at the source for the constructors provided by this class then you might
     conclude that you can use this constructor when unpacking. That might be true today but
     future implementations of this class might actually require the use of the
     {@link #GowingAbstractPackableEntity(GowingUnPacker,GowingPackedEntityBundle)}
     constructor when unpacking.
     Consequently, it is STRONGLY advised that you use the
     {@link #GowingAbstractPackableEntity(GowingUnPacker,GowingPackedEntityBundle)} constructor
     when unpacking and this constructor when creating an instance in other contexts.</p>
     */

    protected GowingAbstractPackableEntity( @NotNull final GowingNameMarkerThing gowingThing ) {

        super();

        _gowingThing = gowingThing;

    }

    /**
     Provide a Gowing-compatible constructor for use when unpacking a packed instance.
     <p>If you look at the source for the constructors provided by this class then you might
     conclude that you can use the {@link #GowingAbstractPackableEntity(GowingNameMarkerThing)}
     constructor when unpacking. That might be true today but
     future implementations of this class might actually require the use of this constructor when unpacking.
     Consequently, it is STRONGLY advised that you use this constructor
     when unpacking and the {@link #GowingAbstractPackableEntity(GowingNameMarkerThing)} constructor when
     creating an instance in other contexts.</p>
     @param unPacker the unpacker that's leading this circus.
     @param bundle   the bundle extracted from the input stream of packed stuff.
     */

    protected GowingAbstractPackableEntity(
            @SuppressWarnings("unused") @NotNull final GowingUnPacker unPacker,
            @NotNull final GowingPackedEntityBundle bundle
    ) {

        super();

        _gowingThing = new GowingNameMarkerThing( bundle.getNotNullField( GOWING_NAME ).StringValue() );

    }

    /**
     Provide 'capstone' bundling services.
     @param packer our packer.
     @return a bundle containing just our Gowing string name.
     Note that nobody really cares about our Gowing string name. The real point of this method is to provide
     something equivalent to {@link #bundleThyself(boolean, GowingPacker)} for our immediate descendant to call
     without having to call that something {@code bundleThyself} (if our bundler uses the standard name then
     any developer of an immediate descendant of this class who forgets to write their own {@code bundleThyself}
     method won't get a "but you forgot to implement the abstract {@code bundleThyself} compile-time error).
     */

    protected final GowingPackedEntityBundle bundleRoot( final GowingPacker packer ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                packer.getPackingContext()
        );

        bundle.addHolder( new GowingStringHolder( GOWING_NAME, _gowingThing.getGowingStringName(), true ) );

        return bundle;

    }

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

        return _instanceId;

    }

    public abstract String toString();

}
