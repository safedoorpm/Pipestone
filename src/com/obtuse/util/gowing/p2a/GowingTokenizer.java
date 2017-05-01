package com.obtuse.util.gowing.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;

/**
 %%% Something clever goes here.
 */

public interface GowingTokenizer extends Closeable {

    void putBackToken( StdGowingTokenizer.GowingToken2 token );

    @NotNull
    StdGowingTokenizer.GowingToken2 getNextToken( boolean identifierAllowed, @NotNull StdGowingTokenizer.TokenType requiredType )
	    throws IOException, GowingUnPackerParsingException;

    @NotNull
    StdGowingTokenizer.GowingToken2 getNextToken( boolean identifierAllowed )
	    throws IOException, GowingUnPackerParsingException;

}
