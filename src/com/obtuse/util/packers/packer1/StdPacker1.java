package com.obtuse.util.packers.packer1;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A 'standard' implementation of an entity packer.
 */

public class StdPacker1 implements Packer1, Closeable {

    public static final PackingId1 NULL_ENTITY_ID = new PackingId1( (short) 0, 0L );

    private final PackingContext1 _context;

    private final File _outputFile;

    private SortedMap<PackingId1,Packable1> _packedThings = new TreeMap<PackingId1, Packable1>();

    private PrintWriter _writer;

    public StdPacker1( File outputFile, PackingContext1 context )
	    throws FileNotFoundException {
        super();

	_outputFile = outputFile;
	_writer = new PrintWriter( _outputFile );

        _context = context;

    }

    public void startPackingEntity( Packable1 entity ) {

	throw new IllegalArgumentException( "unimplemented" );

    }

    /**
     * Pack a packable entity.
     * @param entity the entity to be packed (must not be null).
     * @return the entity's packed id (only unique within the context of this packer).
     * @throws NullPointerException if <code>entity</code> is null.
     */

    @NotNull
    @Override
    public PackingId1 packEntity( Packable1 entity ) {

        if ( entity == null ) {

            throw new NullPointerException( "mandatory entity not provided" );

        }

        return null;

    }

    @Override
    @NotNull
    public PackingId1 packOptionalEntity( Packable1 optEntity ) {

        if ( optEntity == null ) {

            return NULL_ENTITY_ID;

        } else {

            return packEntity( optEntity );

        }

    }

    public void packMandatoryString( String s ) {


    }
    public String toString() {

	return "StdPacker( " + _context + " )";

    }

    public File getOutputFile() {

	return _outputFile;
    }

    @Override
    public void close()
	    throws IOException {

	_writer.close();

    }

}
