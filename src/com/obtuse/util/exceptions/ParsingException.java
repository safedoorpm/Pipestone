/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util.exceptions;

@SuppressWarnings({ "SameParameterValue", "UnusedDeclaration" })
public class ParsingException extends Exception {

    public enum ErrorType {
        DATE_FORMAT_ERROR,
        INVALID_FIELD_VALUE,
        NUMBER_FORMAT_ERROR,
        MISSING_DATA,
        JUNK_AT_END_OF_LINE,
        UNKNOWN_RECORD_TYPE,
        TIME_FORMAT_ERROR,
        MISC_ERROR_TYPE
    }

    //public static final String HELLO_WORLD = "hello world";

    private final int _lineNumber;
    private final int _offset;
    private final ErrorType _errorType;

    public ParsingException( final String msg, final int lineNumber, final int offset, final ErrorType errorType ) {
        super( msg );

        _lineNumber = lineNumber;
        _offset = offset;
        _errorType = errorType;

    }

    public int getErrorLineNumber() {

        return _lineNumber;

    }

    public int getErrorOffset() {

        return _offset;

    }

    public ErrorType getErrorType() {

        return _errorType;

    }

    public String toString() {

        return "(line " + _lineNumber + ", offset " + _offset + ", error type " + _errorType + ") " + super.toString();

    }

}
