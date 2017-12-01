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

    /**
     Emit a metadata comment with a String value.
     @param name the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value the value (anything that {@link com.obtuse.util.ObtuseUtil#enquoteToJavaString(String)} can handle).
     */

    void emitMetaData( String name, String value );

    /**
     Emit a metadata comment with a long value.
     @param name the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value any long value.
     */

    void emitMetaData( String name, long value );

    /**
     Emit a metadata comment with a boolean value.
     @param name the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value any boolean value (in other words, {@code true} or {@code false}).
     */

    void emitMetaData( String name, boolean value );

    /**
     Emit a metadata comment with a double value.
     @param name the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value the value any double value including NaN and the infinities.
     */

    void emitMetaData( String name, double value );

}
