package com.obtuse.util.packers.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.packers.packer2.EntityName2;

/**
 Describe the format version of a parsed file.
 */
public class FormatVersion {

    private final int _majorVersion;

    private final int  _minorVersion;

    private final EntityName2 _groupName;

    public FormatVersion( P2ATokenizer.P2AToken token, EntityName2 groupName )
	    throws UnPacker2ParseError {
	super();

	if ( token.isError() ) {

	    throw new HowDidWeGetHereError( "error token passed to FormatVersion constructor - " + token );

	}

	_groupName = groupName;

	if ( token.type() == P2ATokenizer.TokenType.FORMAT_VERSION ) {

	    long longVersion = token.longValue();

	    long longMajorVersion = longVersion / Constants.FORMAT_VERSION_MULTIPLIER;
	    long longMinorVersion = longVersion % Constants.FORMAT_VERSION_MULTIPLIER;

	    if ( longMajorVersion <= 0 || longMajorVersion > Integer.MAX_VALUE ) {

		throw new UnPacker2ParseError( "invalid version (" + longVersion + ") - major version (" + longMajorVersion + ") out of range", token );

	    }

	    if ( longMinorVersion <= 0 || longMinorVersion > Integer.MAX_VALUE ) {

		throw new UnPacker2ParseError( "invalid version (" + longVersion + ") - minor version (" + longMajorVersion + ") out of range", token );

	    }

	    _majorVersion = (int) longMajorVersion;
	    _minorVersion = (int) longMinorVersion;

	} else {

	    throw new HowDidWeGetHereError( "token passed to FormatVersion constructor is not a format version token - " + token );

	}

    }

    public int getMajorVersion() {

	return _majorVersion;

    }

    public int getMinorVersion() {

	return _minorVersion;

    }

    public EntityName2 getGroupName() {

	return _groupName;

    }

    public String toString() {

	return "FormatVersion( " + getMajorVersion() + "." + getMinorVersion() + ", " + getGroupName() + " )";

    }

}
