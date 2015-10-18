package com.obtuse.util.gowing.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 Something when wrong unpacking a packed 'lump'.
 */

public class GowingUnPacker2ParsingException extends Exception {

    private final GowingTokenizer2.GowingToken2 _causeToken;

//    public UnPacker2ParseError( String msg ) {
//	super( msg );
//
//	_causeToken = null;
//
//    }

    public GowingUnPacker2ParsingException( String msg, @NotNull GowingTokenizer2.GowingToken2 causeToken ) {
	super( msg );

	_causeToken = causeToken;

    }

    public GowingTokenizer2.GowingToken2 getCauseToken() {

	return _causeToken;

    }

    public String toString() {

	return "UnPacker2ParseError( \"" + getMessage() + "\", causeToken = " + _causeToken + " )";

    }

}