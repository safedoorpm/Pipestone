package com.obtuse.util.gowing.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.EntityName;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Describe the format version of a parsed file.
 */

public class GowingFormatVersion {

    private final int _majorVersion;

    private final int _minorVersion;

    private final boolean _textFormat;

    private final EntityName _groupName;

    public GowingFormatVersion( final StdGowingTokenizer.GowingToken2 fileFormatToken, final EntityName groupName )
            throws GowingUnpackingException {

        super();

        _textFormat = true;

        if ( fileFormatToken.isError() ) {

            throw new HowDidWeGetHereError( "error token passed to GowingFormatVersion constructor - " + fileFormatToken );

        }

        _groupName = groupName;

        if ( fileFormatToken.type() == StdGowingTokenizer.TokenType.FORMAT_VERSION ) {

            long longVersion = fileFormatToken.longValue();

            long longMajorVersion = longVersion / GowingConstants.FORMAT_VERSION_MULTIPLIER;
            long longMinorVersion = longVersion % GowingConstants.FORMAT_VERSION_MULTIPLIER;

            if ( longMajorVersion <= 0 || longMajorVersion > Integer.MAX_VALUE ) {

                throw new GowingUnpackingException(
                        "invalid version (" + longVersion + ") - major version (" + longMajorVersion + ") out of range",
                        fileFormatToken
                );

            }

            if ( longMinorVersion <= 0 || longMinorVersion > Integer.MAX_VALUE ) {

                throw new GowingUnpackingException(
                        "invalid version (" + longVersion + ") - minor version (" + longMajorVersion + ") out of range",
                        fileFormatToken
                );

            }

            _majorVersion = (int)longMajorVersion;
            _minorVersion = (int)longMinorVersion;

        } else {

            throw new HowDidWeGetHereError( "token passed to GowingFormatVersion constructor is not a format version token - " + fileFormatToken );

        }

    }

    public boolean isTextFormat() {

        return _textFormat;

    }

    public int getMajorVersion() {

        return _majorVersion;

    }

    public int getMinorVersion() {

        return _minorVersion;

    }

    public EntityName getGroupName() {

        return _groupName;

    }

    public String toString() {

        return "GowingFormatVersion( " + getVersionAsString() + " )";

    }

    @NotNull
    public String getVersionAsString() {

        return "[[Gowing " +
               ( isTextFormat() ? "text" : "binary" ) +
               " format=v" + getMajorVersion() + "." + getMinorVersion() + "," +
               " file-level-group=" + ObtuseUtil.enquoteJavaObject( getGroupName() ) +
               "]]";

    }

}
