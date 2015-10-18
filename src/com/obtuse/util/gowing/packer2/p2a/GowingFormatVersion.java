package com.obtuse.util.gowing.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.gowing.packer2.EntityName2;

/**
 Describe the format version of a parsed file.
 */

public class GowingFormatVersion {

    private final int _majorVersion;

    private final int  _minorVersion;

    private final EntityName2 _groupName;

    public GowingFormatVersion( GowingTokenizer2.GowingToken2 token, EntityName2 groupName )
	    throws GowingUnPacker2ParsingException {
	super();

	if ( token.isError() ) {

	    throw new HowDidWeGetHereError( "error token passed to GowingFormatVersion constructor - " + token );

	}

	_groupName = groupName;

	if ( token.type() == GowingTokenizer2.TokenType.FORMAT_VERSION ) {

	    long longVersion = token.longValue();

	    long longMajorVersion = longVersion / GowingConstants.FORMAT_VERSION_MULTIPLIER;
	    long longMinorVersion = longVersion % GowingConstants.FORMAT_VERSION_MULTIPLIER;

	    if ( longMajorVersion <= 0 || longMajorVersion > Integer.MAX_VALUE ) {

		throw new GowingUnPacker2ParsingException( "invalid version (" + longVersion + ") - major version (" + longMajorVersion + ") out of range", token );

	    }

	    if ( longMinorVersion <= 0 || longMinorVersion > Integer.MAX_VALUE ) {

		throw new GowingUnPacker2ParsingException( "invalid version (" + longVersion + ") - minor version (" + longMajorVersion + ") out of range", token );

	    }

	    _majorVersion = (int) longMajorVersion;
	    _minorVersion = (int) longMinorVersion;

	} else {

	    throw new HowDidWeGetHereError( "token passed to GowingFormatVersion constructor is not a format version token - " + token );

	}

    }

    @SuppressWarnings("WeakerAccess")
    public int getMajorVersion() {

	return _majorVersion;

    }

    @SuppressWarnings("WeakerAccess")
    public int getMinorVersion() {

	return _minorVersion;

    }

    public EntityName2 getGroupName() {

	return _groupName;

    }

    public String toString() {

	return "GowingFormatVersion( " + getMajorVersion() + "." + getMinorVersion() + ", " + getGroupName() + " )";

    }

}
