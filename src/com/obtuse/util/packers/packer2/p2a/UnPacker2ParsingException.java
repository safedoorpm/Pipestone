package com.obtuse.util.packers.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 Something when wrong unpacking a packed 'lump'.
 */

public class UnPacker2ParsingException extends Exception {

    private final P2ATokenizer.P2AToken _causeToken;

//    public UnPacker2ParseError( String msg ) {
//	super( msg );
//
//	_causeToken = null;
//
//    }

    public UnPacker2ParsingException( String msg, @NotNull P2ATokenizer.P2AToken causeToken ) {
	super( msg );

	_causeToken = causeToken;

    }

    public P2ATokenizer.P2AToken getCauseToken() {

	return _causeToken;

    }

    public String toString() {

	return "UnPacker2ParseError( \"" + getMessage() + "\", causeToken = " + _causeToken + " )";

    }

}