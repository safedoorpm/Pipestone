package com.obtuse.util.gowing.p2a;

import org.jetbrains.annotations.Nullable;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Something when wrong unpacking a packed 'lump'.
 */

public class GowingUnPackerParsingException extends Exception {

    private StdGowingTokenizer.GowingToken2 _causeToken;

    public GowingUnPackerParsingException( final String msg ) {

        this( msg, null, null );

    }

    public GowingUnPackerParsingException( final String msg, @Nullable final StdGowingTokenizer.GowingToken2 causeToken ) {

        this( msg, causeToken, null );

    }

    public GowingUnPackerParsingException( final String msg, @Nullable final Throwable cause ) {

        this( msg, null, cause );

    }

    public GowingUnPackerParsingException(
            final String msg,
            @Nullable final StdGowingTokenizer.GowingToken2 causeToken,
            @Nullable final Throwable cause
    ) {

        super( msg, cause );

        _causeToken = causeToken;

    }

    @Nullable
    public StdGowingTokenizer.GowingToken2 getCauseToken() {

        return _causeToken;

    }

    public void setCauseToken( final StdGowingTokenizer.GowingToken2 token ) {

        if ( _causeToken == null ) {

            _causeToken = token;

        } else {

            throw new IllegalArgumentException( "token already set in " + this, this );

        }

    }

    public String toString() {

        return "GowingUnPackerParsingException( \"" + getMessage() + "\", causeToken = " + _causeToken + " )";

    }

}