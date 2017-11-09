/*
 * Copyright © 2016 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.linear;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnPackerParsingException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 */

public class LinearFlagName extends GowingPackableName {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( LinearFlagName.class.getCanonicalName() );
    private static final int VERSION = 1;

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
	public GowingPackable createEntity( @NotNull final GowingUnPacker unPacker, @NotNull final GowingPackedEntityBundle bundle, final GowingEntityReference er )
		throws GowingUnPackerParsingException {

	    return new LinearFlagName( unPacker, bundle, er );

	}

    };

    public static final LinearFlagName LEFT_JUSTIFIED;

    public static final LinearFlagName RIGHT_JUSTIFIED;

    public static final LinearFlagName TOP_JUSTIFIED;

    public static final LinearFlagName BOTTOM_JUSTIFIED;

//    private final String _name;

    private static final SortedMap<String,LinearFlagName> s_knownFlags;

    static {

	s_knownFlags = new TreeMap<>();

	LEFT_JUSTIFIED = new LinearFlagName( "left_justified" );

	RIGHT_JUSTIFIED = new LinearFlagName( "right_justified" );

	TOP_JUSTIFIED = new LinearFlagName( "top_justified" );

	BOTTOM_JUSTIFIED = new LinearFlagName( "bottom_justified" );

    }

    private LinearFlagName( final String name ) {
	super( name );

	synchronized ( s_knownFlags ) {

	    if ( !s_knownFlags.containsKey( name ) )
	    s_knownFlags.put( name, this );

	}

    }

    public LinearFlagName( final GowingUnPacker unPacker, final GowingPackedEntityBundle bundle, final GowingEntityReference er )
	    throws GowingUnPackerParsingException {
	super(
		unPacker,
		bundle.getSuperBundle(),
		er
	);

    }

    @NotNull
    public GowingPackedEntityBundle bundleThyself( final boolean isPackingSuper, final GowingPacker packer ) {

	@SuppressWarnings("UnnecessaryLocalVariable")
	GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
		LinearFlagName.ENTITY_TYPE_NAME,
		LinearFlagName.VERSION,
		super.bundleThyself( true, packer ),
		packer.getPackingContext()
	);

	return bundle;

    }

    public boolean equals( final Object rhs ) {

	return rhs instanceof LinearFlagName && super.equals( rhs );

    }

    public static String describe( @NotNull final Collection<LinearFlagNameValue> flags ) {

	synchronized ( s_knownFlags ) {

	    StringBuilder sb = new StringBuilder();
	    String comma = "{ ";

	    for ( LinearFlagNameValue linearFlagNameValue : flags ) {

		sb.append( comma ).append( linearFlagNameValue );

		comma = ", ";

	    }

	    if ( sb.length() == 0 ) {

		return "{}";

	    } else {

		return sb.append( " }" ).toString();

	    }

	}

    }

    public static void main( final String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "testing", null );

	if ( s_knownFlags.isEmpty() ) {

	    Logger.logMsg( "no known LinearFlags" );

	    //noinspection UnnecessaryReturnStatement
	    return;

	}

//	int mask = 0;
//	for ( Set<LinearFlagName> flags : new PowerSet<LinearFlagName>( s_knownFlags ) ) {
////	int maxPower = s_knownFlags.lastKey().intValue();
////	long maxFlagBits = ( 1L << ( maxPower + 1 ) ) - 1;
//
////	for ( long flagBits = 0; flagBits <= maxFlagBits; flagBits += 1 ) {
//
//	    Logger.logMsg( ObtuseUtil.lpad( Long.toString( mask, 2 ), 15 ) + ":  " + describe( flags.values() ) );
//
//	    mask += 1;
//
//	}

    }

}