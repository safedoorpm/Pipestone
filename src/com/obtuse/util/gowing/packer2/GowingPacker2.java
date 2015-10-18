package com.obtuse.util.gowing.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

/**
 Top-level packing API.
 */

public interface GowingPacker2 extends Closeable {

    GowingInstanceId queuePackEntity( GowingPackable2 entity );

    GowingInstanceId queuePackEntity( EntityName2 entityName, GowingPackable2 entity );

    @NotNull
    GowingPackerContext2 getPackingContext();

    int finish();

    void emitName( EntityName2 name );

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

    void emit( EntityTypeName2 typeName );

//    void actuallyPackEntities();

}
