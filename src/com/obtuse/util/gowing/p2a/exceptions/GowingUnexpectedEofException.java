package com.obtuse.util.gowing.p2a.exceptions;

/**
 An exception used to simplify parsing in a few situations.
 */

public class GowingUnexpectedEofException extends Throwable {

    private final int _lnum;
    private final int _offset;

    public GowingUnexpectedEofException( final int lnum, final int offset ) {
        super( "unexpected EOF @ line " + lnum + " offset " + offset );

        _lnum = lnum;
        _offset = offset;
    }

    public int getLnum() {

        return _lnum;

    }

    public int getOffset() {

        return _offset;

    }

}
