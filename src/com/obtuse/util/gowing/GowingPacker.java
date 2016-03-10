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

    GowingInstanceId queuePackEntity( GowingPackable entity );

    GowingInstanceId queuePackEntity( EntityName entityName, GowingPackable entity );

    @NotNull
    GowingPackerContext getPackingContext();

    int finish();

    void emitName( EntityName name );

    void emitEntityReference( int typeId, long entityId );

    void emit( GowingInstanceId instanceId );

    void emit( String s );

    void emit( char c );

    void emit( long l );

    void emit( double d );

    void emit( float f );

    void emit( int i );

    void emit( short s );

    void emit( byte b );

    void emit( boolean b );

    void emitNull();

    void emit( EntityTypeName typeName );

//    void actuallyPackEntities();

}
