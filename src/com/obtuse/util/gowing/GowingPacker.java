package com.obtuse.util.gowing;

import com.obtuse.util.gowing.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Top-level packing API.
 */

public interface GowingPacker extends Closeable {

    GowingInstanceId queuePackableEntity( GowingPackable entity );

    GowingInstanceId queuePackableEntity( EntityName entityName, GowingPackable entity );

    boolean isVerbose();

    void setVerbose( boolean verbose );

    @NotNull
    GowingPackerContext getPackingContext();

    int finish() throws IOException;

    void emitName( @NotNull EntityName name );

    void emitUsersEntityName( @NotNull EntityName name );

    void emitEntityReference( int typeId, long entityId );

    void emit( @Nullable GowingInstanceId instanceId );

    void emit( @Nullable String s );

    void emit( @Nullable String@NotNull[] v );

    void emit( @NotNull File v );

    void emit( @Nullable File@NotNull[] v );

    void emit( char c );

    void emit( long l );

    void emit( @NotNull long[] lv );

    void emit( @Nullable Long@NotNull[] v );

    void emit( double d );

    void emit( @NotNull double[] v );

    void emit( @Nullable Double@NotNull[] v );

    void emit( float f );

    void emit( @NotNull float[] v );

    void emit( @Nullable Float@NotNull[] v );

    void emit( int i );

    void emit( @NotNull int[] v );

    void emit( @Nullable Integer@NotNull[] v );

    void emit( short s );

    void emit( @NotNull short[] v );

    void emit( @Nullable Short@NotNull[] v );

    void emit( byte b );

    void emit( @NotNull byte[] v );

    void emit( @Nullable Byte@NotNull[] v );

    void emit( boolean b );

    void emit( boolean@NotNull[] v );

    void emit( @Nullable Boolean@NotNull[] v );

    void emitNull();

    void emit( EntityTypeName typeName );

    /**
     Emit a metadata comment with a String value.
     @param name the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value the value (anything that {@link com.obtuse.util.ObtuseUtil#enquoteToJavaString(String)} can handle).
     */

    void emitMetaData( @NotNull String name, @NotNull String value );

    /**
     Emit a metadata comment with a long value.
     @param name the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value any long value.
     */

    void emitMetaData( @NotNull String name, long value );

    /**
     Emit a metadata comment with a boolean value.
     @param name the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value any boolean value (in other words, {@code true} or {@code false}).
     */

    void emitMetaData( @NotNull String name, boolean value );

    /**
     Emit a metadata comment with a double value.
     @param name the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value the value any double value including NaN and the infinities.
     */

    void emitMetaData( @NotNull String name, double value );

    File getOutputFile();

    EntityName getGroupName();

}
