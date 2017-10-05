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

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GowingAbstractPackableEntity.class.getCanonicalName() );

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

        private GowingNameMarkerThing( String name ) {

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
     <p/>Put another way, use this constructor in all situations except when unpacking a packed instance
     (use the {@link #GowingAbstractPackableEntity(GowingUnPacker,GowingPackedEntityBundle)} constructor when unpacking.
     */

    protected GowingAbstractPackableEntity( @NotNull GowingNameMarkerThing gowingThing ) {

        super();

        _gowingThing = gowingThing;

    }

    /**
     Provide a Gowing-compatible constructor for use when unpacking a packed instance.

     @param unPacker the unpacker that's leading this circus.
     @param bundle   the bundle extracted from the input stream of packed stuff.
     */

    protected GowingAbstractPackableEntity( GowingUnPacker unPacker, @NotNull GowingPackedEntityBundle bundle ) {

        super();

        _gowingThing = new GowingNameMarkerThing( bundle.getNotNullField( GOWING_NAME ).StringValue() );

    }

    /**
     Provide 'capstone' bundling services.
     @param packer
     @return
     */
    protected final GowingPackedEntityBundle bundleRoot( GowingPacker packer ) {

        GowingPackedEntityBundle rval = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                null,
                packer.getPackingContext()
        );

        rval.addHolder( new GowingStringHolder( GOWING_NAME, _gowingThing.getGowingStringName(), true ) );

        return rval;

    }

//    public String getGowingName() {
//
//        return _gowingName;
//
//    }

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

        return _instanceId;

    }

    public abstract String toString();

}
