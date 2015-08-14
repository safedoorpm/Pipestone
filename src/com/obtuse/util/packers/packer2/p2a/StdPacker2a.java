package com.obtuse.util.packers.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.packers.packer2.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 Pack entities using a purely text-based format (no binary data) and explicitly named fields.
 */

public class StdPacker2a implements Packer2 {

    /**
     Back-reference to an entity emitted earlier.
     */
    public static final char TAG_BACK_REFERENCE = 'B';

    /**
     A character value.
     */

    public static final char TAG_CHAR = 'c';

    /**
     A packing id.
     */

    public static final char TAG_PACKING_ID = 'P';

    /**
     An entity/field name.
     */

    private static final char TAG_ENTITY_NAME = 'E';

    /**
     An actual type name.
     */

    public static final char TAG_TYPE_NAME = 'N';

    /**
     An int reference to a type name.
     */

    public static char TAG_TYPE_NAME_REFERENCE = 'n';

    /**
     A long reference to a previously serialized type id.
     */

    public static char TAG_TYPE_ID = 'i';

    private final File _outputFile;

    private int _depth = 0;

    private final SortedMap<EntityTypeName2,Integer> _seenTypeNames = new TreeMap<EntityTypeName2, Integer>();

    private final SortedSet<BackRef2> _seenEntities = new TreeSet<BackRef2>();

    private final PrintWriter _writer;

    public StdPacker2a( File outputFile )
	    throws FileNotFoundException {

	this( outputFile, new PrintWriter( outputFile ) );

    }

    public StdPacker2a( File outputFile, OutputStream outputStream ) {

	this( outputFile, new PrintWriter( outputStream, true ) );

    }

    public StdPacker2a( File outputFile, PrintWriter writer ) {

	_outputFile = outputFile;
	_writer = writer;

    }

    public void close() {

	_writer.close();

    }

    @Override
    public BackRef2 packEntity( EntityName2 entityName, Packable2 entity ) {

	String indent = ObtuseUtil.replicate( "\t", _depth );
	packEntityName( indent, entityName, false );
	BackRef2 backRef = new BackRef2( entity );
	if ( _seenEntities.contains( backRef ) ) {

	    _depth += 1;
	    try {

		packBackRef( indent, backRef );

	    } finally {

		_depth -= 1;

	    }

	} else {

	    _depth += 1;
	    try {

		_seenEntities.add( backRef );
		packEntityName( indent, entityName, true );

	    } finally {

		_depth -= 1;

	    }

	}

	return backRef;

    }

    private void packBackRef( String indent, BackRef2 backRef ) {

	_writer.print( indent );

	_writer.print( TAG_BACK_REFERENCE );

    }

    private void packEntityName( String indent, EntityName2 entityName, boolean newLine ) {

	_writer.print( indent );
	_writer.print( entityName.length() );
	_writer.print( TAG_ENTITY_NAME );
	_writer.print( entityName );
	if ( newLine ) {

	    _writer.println();

	}

    }

    public void packPackingId( PackingId2 id ) {

	String packingIdParams = formatPackingId( id );
	_writer.print( packingIdParams.length() );
	_writer.print( TAG_PACKING_ID );
	_writer.print( packingIdParams );

    }

    @NotNull
    private String formatPackingId( PackingId2 id ) {

	int entityTypeReference = getEntityTypeReferenceId( id.getEntityTypeName() );
	String taggedTypeReference = formatTagged( TAG_TYPE_NAME_REFERENCE, Integer.toString( entityTypeReference ) );
	String rval = taggedTypeReference + ',' + formatTagged( TAG_TYPE_ID, Long.toString( id.getEntityId() ) );

	return rval;

    }

    private String formatTagged( char tag, String sValue ) {

	StringBuilder buf = new StringBuilder();
	buf.append( sValue.length() );
	buf.append( tag );
	buf.append( sValue );

	return buf.toString();

    }

    private int getEntityTypeReferenceId( EntityTypeName2 entityTypeName ) {

	Integer typeReferenceId = _seenTypeNames.get( entityTypeName );
	String typeReferenceIdString;
	if ( typeReferenceId == null ) {

	    typeReferenceId = _seenTypeNames.size() + 1;
	    _seenTypeNames.put( entityTypeName, typeReferenceId );
	    typeReferenceIdString = Integer.toString( typeReferenceId );
	    _writer.print( typeReferenceIdString.length() );
	    _writer.print( TAG_TYPE_NAME );
	    _writer.print( typeReferenceIdString );

//	} else {
//
//	    typeReferenceIdString = Integer.toString( typeReferenceId );

	}

	return typeReferenceId;
    }

    public File getOutputFile() {

	return _outputFile;
    }
}
