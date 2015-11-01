package com.obtuse.util.gowing.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Something when wrong unpacking a packed 'lump'.
 */

public class GowingUnPacker2ParsingException extends Exception {

    private GowingTokenizer2.GowingToken2 _causeToken;

//    public UnPacker2ParseError( String msg ) {
//	super( msg );
//
//	_causeToken = null;
//
//    }

    public GowingUnPacker2ParsingException( String msg ) {
	this( msg, null );

    }

    public GowingUnPacker2ParsingException( String msg, @Nullable GowingTokenizer2.GowingToken2 causeToken ) {
	this( msg, causeToken, null );

    }

    public GowingUnPacker2ParsingException( String msg, @Nullable GowingTokenizer2.GowingToken2 causeToken, @Nullable Throwable cause ) {
	super( msg, cause );

	_causeToken = causeToken;

    }

    @Nullable
    public GowingTokenizer2.GowingToken2 getCauseToken() {

	return _causeToken;

    }

    public void setCauseToken( GowingTokenizer2.GowingToken2 token ) {

	if ( _causeToken == null ) {

	    _causeToken = token;

	} else {

	    throw new IllegalArgumentException( "token already set in " + this, this );

	}

    }

    public String toString() {

	return "UnPacker2ParseError( \"" + getMessage() + "\", causeToken = " + _causeToken + " )";

    }

}