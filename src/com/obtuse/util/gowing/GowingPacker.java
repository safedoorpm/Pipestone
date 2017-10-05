package com.obtuse.util.gowing;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Top-level packing API.
 */

public interface GowingPacker extends Closeable {

    GowingInstanceId queuePackableEntity( GowingPackable entity );

//    GowingInstanceId queuePackableEntity( EntityName entityName, GowingPackable entity );

    @NotNull
    GowingPackerContext getPackingContext();

    int finish();

    void emitName( EntityName name );

    void emitEntityReference( int typeId, long entityId );

    void emit( GowingInstanceId instanceId );

    void emit( String s );

    void emit( char c );

    void emit( long l );

    void emit( long[] lv );

    void emit( Long[] v );

    void emit( double d );

    void emit( double[] v );

    void emit( Double[] v );

    void emit( float f );

    void emit( float[] v );

    void emit( Float[] v );

    void emit( int i );

    void emit( int[] v );

    void emit( Integer[] v );

    void emit( short s );

    void emit( short[] v );

    void emit( Short[] v );

    void emit( byte b );

    void emit( byte[] v );

    void emit( Byte[] v );

    void emit( boolean b );

    void emit( boolean[] v );

    void emit( Boolean[] v );

    void emitNull();

    void emit( EntityTypeName typeName );



//    void actuallyPackEntities();

}
