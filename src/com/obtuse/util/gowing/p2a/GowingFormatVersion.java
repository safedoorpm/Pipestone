package com.obtuse.util.gowing.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.gowing.EntityName;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Describe the format version of a parsed file.
 */

public class GowingFormatVersion {

    private final int _majorVersion;

    private final int  _minorVersion;

    private final EntityName _groupName;

    public GowingFormatVersion( final StdGowingTokenizer.GowingToken2 token, final EntityName groupName )
            throws GowingUnpackingException {
	super();

	if ( token.isError() ) {

	    throw new HowDidWeGetHereError( "error token passed to GowingFormatVersion constructor - " + token );

	}

	_groupName = groupName;

	if ( token.type() == StdGowingTokenizer.TokenType.FORMAT_VERSION ) {

	    long longVersion = token.longValue();

	    long longMajorVersion = longVersion / GowingConstants.FORMAT_VERSION_MULTIPLIER;
	    long longMinorVersion = longVersion % GowingConstants.FORMAT_VERSION_MULTIPLIER;

	    if ( longMajorVersion <= 0 || longMajorVersion > Integer.MAX_VALUE ) {

		throw new GowingUnpackingException( "invalid version (" + longVersion + ") - major version (" + longMajorVersion + ") out of range", token );

	    }

	    if ( longMinorVersion <= 0 || longMinorVersion > Integer.MAX_VALUE ) {

		throw new GowingUnpackingException( "invalid version (" + longVersion + ") - minor version (" + longMajorVersion + ") out of range", token );

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

    public EntityName getGroupName() {

	return _groupName;

    }

    public String toString() {

	return "GowingFormatVersion( " + getMajorVersion() + "." + getMinorVersion() + ", " + getGroupName() + " )";

    }

}
