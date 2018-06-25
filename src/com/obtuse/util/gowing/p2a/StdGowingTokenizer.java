package com.obtuse.util.gowing.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.holders.*;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Array;
import java.util.*;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A simple tokenizer.
 */

public class StdGowingTokenizer implements GowingTokenizer, Closeable {

    private final GowingUnPackerContext _unPackerContext;

    private final LineNumberReader _reader;

    private int _putBackChar;

    private boolean _hasPutBackChar;

    private GowingToken2 _putBackToken;

    private int _lastLineLength;

    private int _lnum;

    private int _offset;

    private int _lastCh;

    private int _recursiveDepth;

    public enum TokenType {

        ERROR,
        IDENTIFIER,
        PRIMITIVE_ARRAY,
        CONTAINER_ARRAY,
        COMMA,
        SEMI_COLON,
        COLON,
        LEFT_PAREN,
        RIGHT_PAREN,
        LEFT_SQUARE_BRACKET,
        RIGHT_SQUARE_BRACKET,
        LEFT_CURLY_BRACE,
        RIGHT_CURLY_BRACE,
        LESS_ANGLE_BRACKET,
        RIGHT_ANGLE_BRACKET,
        PERIOD,
        AT_SIGN,
        EQUAL_SIGN,
        BOOLEAN {
            public boolean isScalarType() {

                return true;
            }
        },
        USERS_ENTITY_NAME {
            public boolean isScalarType() {

                return true;
            }
        },
        CHAR {
            public boolean isScalarType() {

                return true;
            }
        },
        BYTE {
            public boolean isScalarType() {

                return true;
            }
        },
        SHORT {
            public boolean isScalarType() {

                return true;
            }
        },
        INTEGER {
            public boolean isScalarType() {

                return true;
            }
        },
        LONG {
            public boolean isScalarType() {

                return true;
            }
        },
        DOUBLE {
            public boolean isScalarType() {

                return true;
            }
        },
        FLOAT {
            public boolean isScalarType() {

                return true;
            }
        },
        ENTITY_NAME {
            public boolean isScalarType() {

                return true;
            }
        },
        ENTITY_REFERENCE {
            public boolean isScalarType() {

                return true;
            }
        },
        ENTITY_NAME_CLAUSE_MARKER,
        FORMAT_VERSION,
        STRING {
            public boolean isScalarType() {

                return true;
            }
        },
        NULL,
        EOF;

        public boolean isNumber() {

            int ord = ordinal();
            return byteOrdinal <= ord && ord <= floatOrdinal;
        }

        public boolean isScalarType() {

            return false;

        }

        private static final int byteOrdinal = BYTE.ordinal();
        private static final int floatOrdinal = FLOAT.ordinal();

    }

    public interface ElementParser {

        Object parse( int index )
                throws IOException, NumberFormatException;

    }

    public interface NumericParser {

        Number parse( String strValue );

    }

    public static class GowingToken2 {

        private final TokenType _tokenType;

        private final TokenType _elementType;

        private final Object _value;

        private final int _lnum;

        private final int _offset;

        /**
         Encapsulate any non-array token.

         @param tokenType the token's type.
         @param value     the token's value.
         @param lnum      the line that we found it on.
         @param offset    the offset within the line that we found it at.
         */

        public GowingToken2( final @NotNull TokenType tokenType, final Object value, final int lnum, final int offset ) {

            super();

            _tokenType = tokenType;
            _elementType = null;
            _value = value;
            _lnum = lnum;
            _offset = offset;

        }

        /**
         Encapsulate an array token.

         @param tokenType   the token's type.
         @param elementType the array's element type.
         @param value       the token's value.
         @param lnum        the line that we found it on.
         @param offset      the offset within the line that we found it at.
         */

        public GowingToken2( final @NotNull TokenType tokenType, final @NotNull TokenType elementType, final Object value, final int lnum, final int offset ) {

            super();

            _tokenType = tokenType;
            _elementType = elementType;
            _value = value;
            _lnum = lnum;
            _offset = offset;

            if ( !elementType.isScalarType() ) {

                throw new HowDidWeGetHereError( "attempt to create an array token with a non-scalar element type " + elementType );

            }

        }

        /**
         Encapsulate an error.

         @param errmsg description of the problem.
         @param lnum   the line that we found it on.
         @param offset the offset within the line that we found it at.
         */

        public GowingToken2( final String errmsg, final int lnum, final int offset ) {

            _tokenType = TokenType.ERROR;
            _elementType = null;
            _value = errmsg;
            _lnum = lnum;
            _offset = offset;

        }

        public TokenType type() {

            return _tokenType;

        }

        public TokenType elementType() {

            return _elementType == null ? TokenType.ERROR : _elementType;

        }

        public int getLnum() {

            return _lnum;

        }

        public int getOffset() {

            return _offset;

        }

        public boolean isError() {

            return _tokenType == TokenType.ERROR;

        }

        public boolean isNull() {

            return _tokenType == TokenType.NULL;

        }

        public boolean booleanValue() {

            return ( (Boolean)_value ).booleanValue();

        }

        public byte byteValue() {

            return ( (Number)_value ).byteValue();

        }

        public short shortValue() {

            return ( (Number)_value ).shortValue();

        }

        public int intValue() {

            return ( (Number)_value ).intValue();

        }

        public long longValue() {

            return ( (Number)_value ).longValue();

        }

        public double doubleValue() {

            return ( (Number)_value ).doubleValue();

        }

        public float floatValue() {

            return ( (Number)_value ).floatValue();

        }

        public GowingEntityReference entityReference() {

            return (GowingEntityReference)_value;

        }

        public String stringValue() {

            return (String)_value;

        }

        public Object getObjectValue() {

            return _value;

        }

        public EntityName identifierValue() {

            return new EntityName( (String)_value );

        }

        @SuppressWarnings("unused")
        public String valueToString() {

            Object value = getObjectValue();

            if ( value == null ) {

                return "null";

            }

            if ( type() == TokenType.CONTAINER_ARRAY ) {

                return Arrays.toString( (Object[])value );

            } else if ( type() == TokenType.PRIMITIVE_ARRAY ) {

                switch ( elementType() ) {

                    case BOOLEAN:

                        return Arrays.toString( (boolean[])value );

                    case BYTE:
                        return Arrays.toString( (byte[])value );

                    case SHORT:
                        return Arrays.toString( (short[])value );

                    case INTEGER:
                        return Arrays.toString( (int[])value );

                    case LONG:
                        return Arrays.toString( (long[])value );

                    case FLOAT:
                        return Arrays.toString( (float[])value );

                    case DOUBLE:
                        return Arrays.toString( (double[])value );

                    case STRING:
                        return Arrays.toString( (String[])value );

                    default:
                        throw new HowDidWeGetHereError( "array token has element type " + elementType() );

                }

            } else {

                return value.toString();

            }

        }

        public String toString() {

            return "GowingToken2( " + _tokenType + ", \"" + _value + "\", lnum=" + _lnum + ", offset=" + _offset + " )";

        }

        public String getDescription() {

            @SuppressWarnings("StringBufferReplaceableByString") StringBuilder rval = new StringBuilder();

            rval.append( cleanupTokenType( _tokenType ) );
            rval.append( " (" );
            rval.append( _value.toString() );
            rval.append( ") @ line " ).append( _lnum ).append( ", offset " ).append( _offset );

            return rval.toString();

        }

        @SuppressWarnings("RedundantThrows")
        @NotNull
        public GowingPackableThingHolder createHolder( final EntityName entityName, final GowingToken2 valueToken )
                throws GowingUnpackingException {

            GowingPackableThingHolder holder;
            TokenType tokenType = valueToken.type();
            TokenType elementType = valueToken.elementType();
            switch ( tokenType ) {

                case NULL:
                    holder = new GowingNullHolder( entityName );
                    break;

                case BOOLEAN:
                    holder = new GowingBooleanHolder( entityName, valueToken.booleanValue(), true );
                    break;

                case BYTE:
                    holder = new GowingByteHolder( entityName, valueToken.byteValue(), true );
                    break;

                case SHORT:
                    holder = new GowingShortHolder( entityName, valueToken.shortValue(), true );
                    break;

                case INTEGER:
                    holder = new GowingIntegerHolder( entityName, valueToken.intValue(), true );
                    break;

                case LONG:
                    holder = new GowingLongHolder( entityName, valueToken.longValue(), true );
                    break;

                case FLOAT:
                    holder = new GowingFloatHolder( entityName, valueToken.floatValue(), true );
                    break;

                case DOUBLE:
                    holder = new GowingDoubleHolder( entityName, valueToken.doubleValue(), true );
                    break;

                case STRING:
                    holder = new GowingStringHolder( entityName, valueToken.stringValue(), true );
                    break;

                case PRIMITIVE_ARRAY:
                    if ( elementType.isScalarType() ) {

                        switch ( elementType ) {

                            case BOOLEAN:
                                holder = new GowingBooleanHolder( entityName, (boolean[])valueToken.getObjectValue(), true );
                                break;

                            case BYTE:
                                holder = new GowingByteHolder( entityName, (byte[])valueToken.getObjectValue(), true );
                                break;

                            case SHORT:
                                holder = new GowingShortHolder( entityName, (short[])valueToken.getObjectValue(), true );
                                break;

                            case INTEGER:
                                holder = new GowingIntegerHolder( entityName, (int[])valueToken.getObjectValue(), true );
                                break;

                            case LONG:
                                holder = new GowingLongHolder( entityName, (long[])valueToken.getObjectValue(), true );
                                break;

                            case FLOAT:
                                holder = new GowingFloatHolder( entityName, (float[])valueToken.getObjectValue(), true );
                                break;

                            case DOUBLE:
                                holder = new GowingDoubleHolder( entityName, (double[])valueToken.getObjectValue(), true );
                                break;

                            default:
                                throw new HowDidWeGetHereError( "unsupported primitive array type " + elementType );

                        }

                    } else {

                        throw new HowDidWeGetHereError( "attempt to create a primitive array holder with an invalid element type " +
                                                        elementType );

                    }

                    break;

                case CONTAINER_ARRAY:
                    if ( elementType.isScalarType() ) {

                        switch ( elementType ) {

                            case STRING:
                                holder = new GowingStringHolder( entityName, (String[])valueToken.getObjectValue(), true );
                                break;

                            case BOOLEAN:
                                holder = new GowingBooleanHolder( entityName, (Boolean[])valueToken.getObjectValue(), true );
                                break;

                            case BYTE:
                                holder = new GowingByteHolder( entityName, (Byte[])valueToken.getObjectValue(), true );
                                break;

                            case SHORT:
                                holder = new GowingShortHolder( entityName, (Short[])valueToken.getObjectValue(), true );
                                break;

                            case INTEGER:
                                holder = new GowingIntegerHolder( entityName, (Integer[])valueToken.getObjectValue(), true );
                                break;

                            case LONG:
                                holder = new GowingLongHolder( entityName, (Long[])valueToken.getObjectValue(), true );
                                break;

                            case FLOAT:
                                holder = new GowingFloatHolder( entityName, (Float[])valueToken.getObjectValue(), true );
                                break;

                            case DOUBLE:
                                holder = new GowingDoubleHolder( entityName, (Double[])valueToken.getObjectValue(), true );
                                break;

                            default:
                                throw new HowDidWeGetHereError( "unsupported container array type " + elementType );

                        }

                    } else {

                        throw new HowDidWeGetHereError( "attempt to create a primitive array holder with an invalid element type " +
                                                        elementType );

                    }

                    break;

                case ENTITY_NAME:

                    holder = new GowingEntityNameHolder( entityName, new EntityName( valueToken.stringValue() ), true );
                    break;

                case ENTITY_REFERENCE:

                    holder = new GowingEntityReferenceHolder( entityName, valueToken.entityReference(), true );
                    break;

                default:

                    throw new HowDidWeGetHereError( "token type " + tokenType + " is not a 'value' type" );

            }

            return holder;

        }

    }

    public StdGowingTokenizer( final @NotNull GowingUnPackerContext unPackerContext, final @NotNull LineNumberReader lineNumberReader ) {

        super();

        _unPackerContext = unPackerContext;
        _reader = lineNumberReader;
        _putBackChar = ' ';
        _hasPutBackChar = false;
        _putBackToken = null;
        _recursiveDepth = 0;
        _lnum = 1;
        _offset = 0;

    }

    public void close()
            throws IOException {

        _reader.close();

    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public static String cleanupTokenType( final TokenType tokenType ) {

        return tokenType.name().toLowerCase().replace( '_', '-' );

    }

    @Override
    public void putBackToken( final GowingToken2 token ) {

        if ( _recursiveDepth > 0 ) {

            throw new HowDidWeGetHereError( "attempt to put back a token while getting the next token" );

        }

        if ( _putBackToken == null ) {

            _putBackToken = token;

        } else {

            throw new HowDidWeGetHereError( "putBackToken already full with '" + _putBackToken + "'" );

        }

    }

    private void putBackChar() {

        int ch = _lastCh;

        if ( _hasPutBackChar ) {

            throw new HowDidWeGetHereError( "putBackChar already full with '" + _putBackChar + "'" );

        }

        _hasPutBackChar = true;

        if ( ch == '\n' ) {

            _offset = _lastLineLength;
            _lnum -= 1;

        } else {

            _offset -= 1;

        }

        _putBackChar = ch;

    }

    private int nextRawCh()
            throws IOException {

        int rval;
        if ( _hasPutBackChar ) {

            _hasPutBackChar = false;

            rval = _putBackChar;

        } else {

            rval = _reader.read();

        }

        if ( rval == '\n' ) {

            _lastLineLength = _offset;
            _lnum += 1;
            _offset = 0;

        } else {

            _offset += 1;

        }

        return rval;

    }

    private int nextCh()
            throws IOException {

        int rval = nextRawCh();

        while ( rval != -1 && Character.isWhitespace( rval ) ) {

            rval = _reader.read();

            if ( rval == '\n' ) {

                _lnum += 1;
                _offset = 0;

            } else {

                _offset += 1;

            }

        }

        _lastCh = rval;

        return rval;

    }

    @SuppressWarnings("unused")
    private int peekCh()
            throws IOException {

        int ch = nextCh();
        putBackChar();

        return ch;

    }

    private String cleanupChar( final int ch ) {

        switch ( ch ) {

            case '\n':
                return "\\n";
            case '\t':
                return "\\t";
            case -1:
                return "EOF";
            default:
                return Character.toString( (char)ch );

        }

    }

    private static final SortedMap<Character, TokenType> _singleCharacterTokens;

    static {

        _singleCharacterTokens = new TreeMap<>();
        _singleCharacterTokens.put( ',', TokenType.COMMA );
        _singleCharacterTokens.put( ';', TokenType.SEMI_COLON );
        _singleCharacterTokens.put( ':', TokenType.COLON );
        _singleCharacterTokens.put( '(', TokenType.LEFT_PAREN );
        _singleCharacterTokens.put( ')', TokenType.RIGHT_PAREN );
        _singleCharacterTokens.put( '[', TokenType.LEFT_SQUARE_BRACKET );
        _singleCharacterTokens.put( ']', TokenType.RIGHT_SQUARE_BRACKET );
        _singleCharacterTokens.put( '{', TokenType.LEFT_CURLY_BRACE );
        _singleCharacterTokens.put( '}', TokenType.RIGHT_CURLY_BRACE );
        _singleCharacterTokens.put( '<', TokenType.LESS_ANGLE_BRACKET );
        _singleCharacterTokens.put( '>', TokenType.RIGHT_ANGLE_BRACKET );
        _singleCharacterTokens.put( '.', TokenType.PERIOD );
        _singleCharacterTokens.put( '@', TokenType.AT_SIGN );
        _singleCharacterTokens.put( '=', TokenType.EQUAL_SIGN );
    }

    @Override
    @NotNull
    public GowingToken2 getNextToken( final boolean identifierAllowed, final @NotNull TokenType requiredType )
            throws IOException, GowingUnpackingException {

        try {

            _recursiveDepth += 1;

            GowingToken2 rval = getNextToken( identifierAllowed );
            if ( rval.isError() ) {

                return rval;

            } else if ( rval.type() == requiredType ) {

                return rval;

            } else {

                GowingToken2 errorToken =
                        new GowingToken2( "expected " + cleanupTokenType( requiredType ) + " but got " + cleanupTokenType( rval.type() ) + " instead",
                                          _lnum,
                                          _offset
                        );
                throw new GowingUnpackingException( errorToken.stringValue(), errorToken );

            }

        } finally {

            _recursiveDepth -= 1;

        }

    }

    public byte parseHexByte( final int upper, final int lower ) {

        int uValue;
        if ( upper >= '0' && upper <= '9' ) {

            uValue = upper - '0';

        } else if ( upper >= 'a' && upper <= 'f' ) {

            uValue = 10 + upper - 'a';

        } else {

            throw new NumberFormatException( "invalid upper byte value '" + upper + "'" );

        }

        int lValue;
        if ( lower >= '0' && lower <= '9' ) {

            lValue = lower - '0';

        } else if ( lower >= 'a' && lower <= 'f' ) {

            lValue = 10 + lower - 'a';

        } else {

            throw new NumberFormatException( "invalid lower byte value '" + lower + "'" );

        }

        return (byte)( ( uValue << 4 ) | lValue );

    }

    @Override
    @NotNull
    public GowingToken2 getNextToken( final boolean identifierAllowed )
            throws IOException, GowingUnpackingException {

        if ( _putBackToken != null ) {

            GowingToken2 previousToken = _putBackToken;
            _putBackToken = null;

            return previousToken;

        }

        try {

            _recursiveDepth += 1;

            int ch;

            while ( true ) {

                //	    spinAgain = false;

                ch = nextCh();

                @SuppressWarnings("unused") char c = Character.isDefined( ch ) ? (char)ch : '?';

                TokenType singleCharacterTokenType = _singleCharacterTokens.get( (char)ch );
                if ( singleCharacterTokenType != null ) {

                    return new GowingToken2( singleCharacterTokenType, (char)ch, _lnum, _offset );

                } else if ( Character.isDigit( ch ) ) {

                    putBackChar();

                    GowingToken2 longToken = parseNumeric(
                            TokenType.LONG,
                            strValue -> Long.parseLong( strValue )
                    );

                    return longToken;

                } else if ( identifierAllowed && Character.isJavaIdentifierPart( ch ) ) {

                    putBackChar();

                    GowingToken2 identifierToken = collectIdentifier();
                    if ( identifierToken.type() == TokenType.IDENTIFIER ) {

                        EntityName identifier = identifierToken.identifierValue();
                        if ( identifier.getName().charAt( 0 ) == GowingConstants.TAG_ENTITY_REFERENCE ) {

                            ch = nextCh();

                            if ( ch == ':' ) {

                                try {

                                    int typeId = Integer.parseInt( identifier.getName().substring( 1 ) );

                                    //noinspection ConstantConditions
                                    return finishCollectingEntityReference( !identifierAllowed, _unPackerContext, typeId );

                                } catch ( NumberFormatException e ) {

                                    putBackChar();

                                    return identifierToken;

                                }

                            } else {

                                putBackChar();

                                return identifierToken;

                            }

                        }

                    }

                    return identifierToken;

                } else {

                    GowingToken2 rval;

                    if ( ch == 'e' ) {

                        Logger.logMsg( "found an unexpected 'e'" );

                    }

                    switch ( ch ) {

                        case GowingConstants.TAG_PRIMITIVE_ARRAY:

                            return getArray2( true );

                        case GowingConstants.TAG_CONTAINER_ARRAY:

                            return getArray2( false );

                        // Comment handling belongs in nextCh

                        case GowingConstants.LINE_COMMENT_CHAR:
                        case GowingConstants.LINE_METADATA_CHAR:

                        {

                            boolean isMetaData = ch == GowingConstants.LINE_METADATA_CHAR;

                            StringBuilder sb = new StringBuilder();
                            while ( ch != '\n' && ch != -1 ) {

                                sb.append( (char)ch );
                                ch = nextRawCh();

                            }

                            if ( isMetaData ) {

                                String errmsg = notifyMetaDataHandlers( sb.toString() );
                                if ( errmsg != null ) {

                                    GowingToken2 errorToken =
                                            new GowingToken2( errmsg,
                                                              _lnum,
                                                              _offset
                                            );

                                    Logger.logErr( "oops - " + errorToken );

                                    return errorToken;

                                }

                                ObtuseUtil.doNothing();

                            }

                        }

                        break;

                        case -1:

                            return new GowingToken2( TokenType.EOF, -1, _lnum, _offset );

                        case GowingConstants.NULL_VALUE:

                            return new GowingToken2( TokenType.NULL, ch, _lnum, _offset );

                        case '"':

                            putBackChar();

                            rval = collectString( TokenType.STRING );

                            return rval;

                        case GowingConstants.TAG_ENTITY_NAME:

                            rval = collectString( TokenType.ENTITY_NAME );

                            return rval;

                        case GowingConstants.TAG_BOOLEAN:

                            ch = nextCh();
                            if ( ch == 'T' ) {

                                return new GowingToken2( TokenType.BOOLEAN, true, _lnum, _offset );

                            } else if ( ch == 'F' ) {

                                return new GowingToken2( TokenType.BOOLEAN, false, _lnum, _offset );

                            } else {

                                return new GowingToken2( "expected 'T' or 'F' but found " + cleanupChar( ch ), _lnum, _offset );

                            }

                        case GowingConstants.TAG_BYTE:

                            int c1 = nextCh();
                            if ( !Character.isDefined( c1 ) ) {

                                return new GowingToken2( "expected first hex digit [0-9a-f] but found " + cleanupChar( c1 ), _lnum, _offset );

                            }

                            int c2 = nextCh();
                            if ( c2 == -1 ) {

                                return new GowingToken2( "expected second hex digit [0-9a-f] but found " + cleanupChar( c2 ), _lnum, _offset );

                            }

                            try {

                                byte value = parseHexByte( c1, c2 );

                                return new GowingToken2( TokenType.BYTE, value, _lnum, _offset );

                            } catch ( NumberFormatException e ) {

                                return new GowingToken2(
                                        "expected two digit hex value but found \"" + ( (char)c1 ) + ( (char)c2 ) + '"',
                                        _lnum,
                                        _offset
                                );

                            }

                        case GowingConstants.TAG_SHORT:

                            GowingToken2 shortToken = parseNumeric(
                                    TokenType.SHORT,
                                    strValue -> Short.parseShort( strValue )
                            );

                            return shortToken;

                        case GowingConstants.TAG_INTEGER:

                            GowingToken2 intToken = parseNumeric(
                                    TokenType.INTEGER,
                                    strValue -> Integer.parseInt( strValue )
                            );

                            return intToken;

                        case GowingConstants.TAG_LONG:

                            GowingToken2 longToken = parseNumeric(
                                    TokenType.LONG,
                                    strValue -> Long.parseLong( strValue )
                            );

                            return longToken;

                        case GowingConstants.TAG_DOUBLE:

                            GowingToken2 doubleToken = parseNumeric(
                                    TokenType.DOUBLE,
                                    strValue -> Double.parseDouble( strValue )
                            );

                            return doubleToken;

                        case GowingConstants.TAG_FLOAT:

                            GowingToken2 floatToken = parseNumeric(
                                    TokenType.FLOAT,
                                    strValue -> Float.parseFloat( strValue )
                            );

                            return floatToken;

                        case GowingConstants.TAG_FORMAT_VERSION:

                            GowingToken2 versionNumberToken = parseNumeric(
                                    TokenType.FORMAT_VERSION,
                                    strValue -> Long.parseLong( strValue )
                            );

                            //			GowingToken2 colon = getNextToken( false );
                            //			if ( colon.type() == TokenType.COLON ) {
                            //
                            //			    GowingToken2 groupName = getNextToken( false );
                            //			    if ( groupName.type() == TokenType.STRING ) {
                            //
                            //				return new GowingToken2(
                            //					TokenType.FORMAT_VERSION,
                            //				);
                            //			    }
                            //			}
                            return versionNumberToken;

                        case GowingConstants.TAG_ENTITY_REFERENCE:

                            GowingToken2 typeIdToken = parseNumeric(
                                    TokenType.INTEGER,
                                    strValue -> Integer.parseInt( strValue )
                            );

                            if ( typeIdToken.isError() ) {

                                return typeIdToken;

                            }

                            ch = nextCh();
                            if ( ch == ':' ) {

                                int typeId = typeIdToken.intValue();
                                return finishCollectingEntityReference( !identifierAllowed, _unPackerContext, typeId );

                            } else {

                                return new GowingToken2( "unexpected character " + cleanupChar( ch ), _lnum, _offset );

                            }

                            //		    int c1 = nextCh();
                            //		    if ( '0' <= c1 && c1 <= '9' ) {
                            //
                            //			c1 -= '0';
                            //
                            //		    } else if ( 'a' <= c1 && c1 <= 'f' ) {
                            //
                            //			c1 -= 'a';
                            //
                            //		    } else {
                            //
                            //			return new GowingToken2( "expected hex digit [0-9a-f] but found " + cleanupChar( c1 ), _lnum, _offset );
                            //
                            //		    }
                            //		    int c2 = nextCh();
                            //		    if ( '0' <= c2 && c2 <= '9' ) {
                            //
                            //			c2 -= '0';
                            //
                            //		    } else if ( 'a' <= c2 && c2 <= 'f' ) {
                            //
                            //			c2 -= 'a';
                            //
                            //		    } else {
                            //
                            //			return new GowingToken2( "expected hex digit [0-9a-f] but found " + cleanupChar( c2 ), _lnum, _offset );
                            //
                            //		    }
                            //
                            //		    int hexValue = ( c1 << 4 ) | c2;
                            //
                            //		    return new GowingToken2( TokenType.BYTE, hexValue, _lnum, _offset );

                            //		case Constants.TAG_SHORT:
                            //
                            //		    GowingToken2 numericValue = getIntegralValue( TokenType.SHORT, new NumericStringParser);
                            //		    if ( numericString == null ) {
                            //
                            //			return new GowingToken2( "expected short value but" );
                            //		    }

                        default:

                            return new GowingToken2( "unexpected character " + cleanupChar( ch ), _lnum, _offset );

                    }

                }

            } // while ( spinAgain );

        } finally {

            _recursiveDepth -= 1;

        }

    }

    private List<GowingMetaDataHandler> s_metadataHandlers = new ArrayList<>();

    @Override
    public void registerMetaDataHandler( final @NotNull GowingMetaDataHandler handler ) {

        s_metadataHandlers.add( handler );

    }

    private String notifyMetaDataHandlers( final @NotNull String metaDataLine ) {

        String trimmed = metaDataLine.trim();

        if ( !trimmed.startsWith( String.valueOf( GowingConstants.LINE_METADATA_CHAR ) ) ) {

            throw new HowDidWeGetHereError(
                    "StdGowingTokenizer:  invalid metadata line (doesn't start with '" +
                    GowingConstants.LINE_METADATA_CHAR + "') - " + trimmed
            );

        }

        int equalSignOffset = trimmed.indexOf( '=' );
        if ( equalSignOffset < 0 ) {

            return "invalid metadata line (no equals sign) - " + trimmed;

        }

        if ( !trimmed.endsWith( ";" ) ) {

            return "invalid metadata line (does not end with a semicolon) - " + trimmed;

        }

        String keyword = trimmed.substring( 1, equalSignOffset ).trim();

        String errmsg = StdGowingPacker.isValidMetaDataKeyword( keyword );
        if ( errmsg != null ) {

            return errmsg;

        }

        String rawValue = trimmed.substring( equalSignOffset + 1, trimmed.length() - 1 ).trim();

        if ( rawValue.endsWith( "L" ) || rawValue.endsWith( "l" ) ) {

            long value;
            try {

                value = Long.parseLong( rawValue.substring( 0, rawValue.length() - 1 ) );

            } catch ( NumberFormatException e ) {

                return "invalid metadata value (has L suffix but is not a long value) - " + keyword + '=' + rawValue;

            }

            for ( GowingMetaDataHandler handler : s_metadataHandlers ) {

                handler.processMetaData( keyword, value );

            }

            return null;

        } else if ( rawValue.endsWith( "D" ) || rawValue.endsWith( "d" ) ) {

            double value;
            try {

                value = Double.parseDouble( rawValue.substring( 0, rawValue.length() - 1 ) );

            } catch ( NumberFormatException e ) {

                return "invalid metadata value (has D suffix but is not a double value) - " + keyword + '=' + rawValue;

            }

            for ( GowingMetaDataHandler handler : s_metadataHandlers ) {

                handler.processMetaData( keyword, value );

            }

            return null;

        } else if ( rawValue.endsWith( "B" ) || rawValue.endsWith( "d" ) ) {

            boolean value;
            if ( "T".equals( rawValue.substring( 0, rawValue.length() -1 ) ) ) {

                value = true;

            } else if ( "F".equals( rawValue.substring( 0, rawValue.length() -1 ) ) ) {

                value = false;

            } else {

                return "invalid metadata value( has B suffix but is neither \"true\" or \"false\" in lower case) - " +
                       keyword + '=' + rawValue;

            }

            for ( GowingMetaDataHandler handler : s_metadataHandlers ) {

                handler.processMetaData( keyword, value );

            }

            return null;

        } else if ( rawValue.startsWith( "\"" ) && rawValue.endsWith( "\"" ) ) {

            String value = ObtuseUtil.parseNakedJavaString( rawValue.substring( 1, rawValue.length() - 1 ) );

            for ( GowingMetaDataHandler handler : s_metadataHandlers ) {

                handler.processMetaData( keyword, value );

            }

            return null;

        } else {

            return "invalid metadata value - " + keyword + '=' + rawValue;

        }

    }

    @NotNull
    private GowingToken2 getArray2( final boolean primitive )
            throws IOException {

        int ch;

        int length = 0;
        ch = nextCh();
        while ( true ) {

            if ( ch >= '0' && ch <= '9' ) {

                length = length * 10 + ( ch - '0' );

                ch = nextCh();

            } else {

                break;

            }

        }

        ObtuseUtil.doNothing();

        // ch is now the element type

        TokenType elementType;
        Object array;
        String what;
        ElementParser elementParser;

        switch ( ch ) {

            case GowingConstants.TAG_BOOLEAN:

                elementType = TokenType.BOOLEAN;
                array = primitive ? new boolean[length] : new Boolean[length];
                what = "boolean";
                elementParser = new ElementParser() {

                    @Override
                    public Object parse( final int index )
                            throws IOException, NumberFormatException {

                        int ch = nextCh();

                        if ( ch == 'T' ) {

                            return true;

                        } else if ( ch == 'F' ) {

                            return false;

                        } else {

                            return new GowingToken2( "expected " +
                                                     ( primitive ? GowingConstants.NULL_VALUE + ", " : "" ) +
                                                     "'T' or 'F' but found " +
                                                     cleanupChar( ch ), _lnum, _offset );

                        }

                    }

                    @Override
                    public String toString() {

                        return "boolean ElementParser";

                    }

                };
                break;

            case GowingConstants.TAG_BYTE:

                elementType = TokenType.BYTE;
                array = primitive ? new byte[length] : new Byte[length];
                what = "byte";
                elementParser = new ElementParser() {

                    @Override
                    public Object parse( final int index )
                            throws IOException {

                        int c1 = nextCh();
                        if ( !Character.isDefined( c1 ) ) {

                            return new GowingToken2(
                                    "expected " +
                                    ( primitive ? GowingConstants.NULL_VALUE + ", " : "" ) +
                                    "first hex digit [0-9a-f] @ array offset " +
                                    index +
                                    " but found " +
                                    cleanupChar( c1 ),
                                    _lnum,
                                    _offset
                            );

                        }

                        int c2 = nextCh();
                        if ( c2 == -1 ) {

                            return new GowingToken2(
                                    "expected " +
                                    ( primitive ? GowingConstants.NULL_VALUE + ", " : "" ) +
                                    "second hex digit [0-9a-f] @ array offset " +
                                    index +
                                    " but found " +
                                    cleanupChar( c2 ),
                                    _lnum,
                                    _offset
                            );

                        }

                        byte value = parseHexByte( c1, c2 ); // Byte.parseByte( "" + (char) c1 + (char) c2, 16 );

                        return value;

                    }

                    @Override
                    public String toString() {

                        return "byte ElementParser";

                    }

                };

                break;

            case GowingConstants.TAG_SHORT:

                elementType = TokenType.SHORT;
                array = primitive ? new short[length] : new Short[length];
                what = "short";
                elementParser = new ElementParser() {

                    @Override
                    public Object parse( final int index )
                            throws IOException {

                        String numericString = collectNumericString( "" );
                        return Short.parseShort( numericString );

                    }

                    @Override
                    public String toString() {

                        return "short ElementParser";

                    }

                };

                break;

            case GowingConstants.TAG_INTEGER:

                elementType = TokenType.INTEGER;
                array = primitive ? new int[length] : new Integer[length];
                what = "integer";
                elementParser = new ElementParser() {

                    @Override
                    public Object parse( final int index )
                            throws IOException {

                        String numericString = collectNumericString( "" );
                        return Integer.parseInt( numericString );

                    }

                    @Override
                    public String toString() {

                        return "integer ElementParser";

                    }

                };

                break;

            case GowingConstants.TAG_ENTITY_NAME:

                elementType = TokenType.USERS_ENTITY_NAME;
                array = primitive ? new EntityName[length] : new Long[length];
                what = "UsersEntityName";
                elementParser = new ElementParser() {

                    @Override
                    public Object parse( final int index )
                            throws IOException {

                        GowingToken2 usersEntityNameStringToken = collectString( TokenType.ENTITY_NAME );
                        if ( usersEntityNameStringToken.type() == TokenType.ENTITY_NAME ) {

                            return new EntityName( usersEntityNameStringToken.stringValue() );

                        } else {

                            return new GowingToken2(
                                    usersEntityNameStringToken._value + " looking for user's EntityName string",
                                    usersEntityNameStringToken._lnum,
                                    usersEntityNameStringToken._offset
                            );

                        }

                    }

                    @Override
                    public String toString() {

                        return "UsersEntityName ElementParser";

                    }

                };

                break;

            case GowingConstants.TAG_STRING:

                elementType = TokenType.STRING;
                array = new String[length];
                what = "String";
                elementParser = new ElementParser() {
                    @Override
                    public Object parse( final int index ) throws IOException, NumberFormatException {

                        GowingToken2 stringToken = collectString( TokenType.STRING );
                        if ( stringToken.type() == TokenType.STRING ) {

                            return stringToken.stringValue();

                        } else {

                            return new GowingToken2(
                                    stringToken._value + " looking for user's string",
                                    stringToken._lnum,
                                    stringToken._offset
                            );

                        }

                    }

                    @Override
                    public String toString() {

                        return "String ElementParser";
                    }

                };

                break;

            case GowingConstants.TAG_LONG:

                elementType = TokenType.LONG;
                array = primitive ? new long[length] : new Long[length];
                what = "long";
                elementParser = new ElementParser() {

                    @Override
                    public Object parse( final int index )
                            throws IOException {

                        String numericString = collectNumericString( "" );
                        return Long.parseLong( numericString );

                    }

                    @Override
                    public String toString() {

                        return "long ElementParser";

                    }

                };

                break;

            case GowingConstants.TAG_FLOAT:

                elementType = TokenType.FLOAT;
                array = primitive ? new float[length] : new Float[length];
                what = "float";
                elementParser = new ElementParser() {

                    @Override
                    public Object parse( final int index )
                            throws IOException {

                        String numericString = collectNumericString( "" );
                        return Float.parseFloat( numericString );

                    }

                    @Override
                    public String toString() {

                        return "float ElementParser";

                    }

                };

                break;

            case GowingConstants.TAG_DOUBLE:

                elementType = TokenType.DOUBLE;
                array = primitive ? new double[length] : new Double[length];
                what = "double";
                elementParser = new ElementParser() {

                    @Override
                    public Object parse( final int index )
                            throws IOException {

                        String numericString = collectNumericString( "" );
                        return Double.parseDouble( numericString );

                    }

                    @Override
                    public String toString() {

                        return "double ElementParser";

                    }

                };

                break;

            default:

                return new GowingToken2(
                        "expected scalar type letter but found '" + cleanupChar( ch ) + "'",
                        _lnum,
                        _offset
                );

        }

        ch = nextCh();

        if ( ch != '[' ) {

            return new GowingToken2( "unexpected character " + cleanupChar( ch ) + " (expected '[')", _lnum, _offset );

        }

        for ( int ix = 0; ix < length; ix += 1 ) {

            ObtuseUtil.doNothing();

            if ( ix > 0 ) {

                // A comma is allowed here if this is a primitive byte array.
                // A comma is required here for all other kinds of arrays.

                ch = nextCh();
                if ( ch != ',' ) {

                    if ( !primitive || !elementType.equals( TokenType.BYTE ) ) {

                        return new GowingToken2(
                                "expected comma in array after element " + ( ix - 1 ) + " but found " + cleanupChar( ch ),
                                _lnum,
                                _offset
                        );

                    } else {

                        // We just swallowed a mandatory comma - cough it up and put it back.

                        putBackChar();

                    }

                } else {

                    // Just swallow the comma.

                }

            }

            ch = nextCh();

            if ( ch == GowingConstants.NULL_VALUE ) {

                Array.set( array, ix, null );

                continue;

            }

            putBackChar();

            Object element;

            try {

                element = elementParser.parse( ix );

                if ( element instanceof GowingToken2 ) {

                    // Something went wrong.

                    return (GowingToken2)element;

                }

                try {

                    Array.set( array, ix, element );

                } catch ( IllegalArgumentException e ) {

                    throw new HowDidWeGetHereError( "IAE trying to save array element value " + element + " at " + ix, e );

                } catch ( ArrayIndexOutOfBoundsException e ) {

                    throw new HowDidWeGetHereError( "Array index error trying to save array element value " + element + " at " + ix, e );

                }

            } catch ( NumberFormatException e ) {

                return new GowingToken2(
                        "expected a " + what + " for element " + ix + " + but found something unparsable ",
                        _lnum,
                        _offset
                );

            }

        }

        ch = nextCh();
        if ( ch == ']' ) {

            if ( elementType.isScalarType() ) {

                return new GowingToken2( primitive ? TokenType.PRIMITIVE_ARRAY : TokenType.CONTAINER_ARRAY, elementType, array, _lnum, _offset );

            } else {

                return new GowingToken2(
                        "array has no defined element type",
                        _lnum,
                        _offset
                );

            }

        } else {

            return new GowingToken2( "unexpected character " + cleanupChar( ch ) + " (expected ']')", _lnum, _offset );

        }

    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public GowingToken2 finishCollectingEntityReference(
            final boolean entityNameOk,
            @SuppressWarnings("unused") final GowingUnPackerContext unPackerContext,
            final int typeId
    )
            throws IOException, GowingUnpackingException {

        GowingToken2 entityIdToken = parseNumeric(
                TokenType.LONG,
                strValue -> Long.parseLong( strValue )
        );

        if ( entityIdToken.isError() ) {

            return entityIdToken;

        }

        Integer version;
        int ch = nextCh();
        if ( ch == 'v' ) {

            GowingToken2 entityVersionToken = parseNumeric(
                    TokenType.INTEGER,
                    strValue -> Integer.parseInt( strValue )
            );

            if ( entityVersionToken.isError() ) {

                return entityVersionToken;

            }

            version = entityVersionToken.intValue();
            if ( version.intValue() <= 0 ) {

                throw new GowingUnpackingException( "version number is not positive (" + version + ")", entityVersionToken );

            }

        } else {

            version = null;

            putBackChar();

        }

        SortedSet<EntityName> entityNames = new TreeSet<>();

        while ( true ) {

            ch = nextCh();
            if ( ch == GowingConstants.ENTITY_NAME_CLAUSE_MARKER ) {

                // Get ourselves a token that we can use in error reporting.

                GowingToken2 entityNameClauseMarker = new GowingToken2(
                        TokenType.ENTITY_NAME_CLAUSE_MARKER,
                        GowingConstants.ENTITY_NAME_CLAUSE_MARKER,
                        _lnum,
                        _offset
                );

                if ( entityNameOk ) {

                    ch = nextCh();
                    if ( ch == '"' ) {

                        putBackChar();

                        GowingToken2 entityNameToken = collectString( TokenType.ENTITY_NAME );

                        entityNames.add( entityNameToken.identifierValue() );

                    } else {

                        throw new GowingUnpackingException( "entity name string missing in entity name clause", entityNameClauseMarker );

                    }

                } else {

                    throw new GowingUnpackingException( "entity name clause marker ('" +
                                                        GowingConstants.ENTITY_NAME_CLAUSE_MARKER +
                                                        "') not allowed in this context", entityNameClauseMarker );

                }

            } else {

                putBackChar();

                break;

            }

        }

        try {

            return new GowingToken2(
                    TokenType.ENTITY_REFERENCE,
                    new GowingEntityReference(
                            typeId,
                            entityIdToken.longValue(),
                            version,
                            entityNames
                    ),
                    _lnum,
                    _offset
            );

        } catch ( IndexOutOfBoundsException e ) {

            return new GowingToken2(
                    e.getMessage(),
                    _lnum,
                    _offset
            );

        }
    }

    private GowingToken2 collectString( final @NotNull TokenType tt )
            throws IOException {

        StringBuilder rval = new StringBuilder();
        int ch = nextRawCh();
        char delimiter = (char)ch;

        while ( Character.isDefined( ch = nextRawCh() ) && ch != delimiter ) {

            if ( ch == '\\' ) {

                ch = nextRawCh();
                if ( Character.isDefined( ch ) ) {

                    switch ( ch ) {

                        case 'b':

                            ch = '\b';
                            break;

                        case 'n':

                            ch = '\n';
                            break;

                        case 'r':

                            ch = '\r';
                            break;

                        case 't':

                            ch = '\t';
                            break;

                        case '\\':

                            ch = '\\';
                            break;

                        case '\'':

                            ch = '\'';
                            break;

                        case '"':

                            ch = '"';
                            break;

                        default:

                            return new GowingToken2(
                                    "unexpected '\\" + cleanupChar( ch ) + "' ligature in string",
                                    _lnum,
                                    _offset
                            );

                    }

                }

            }

            rval.append( (char)ch );

        }

        if ( ch == delimiter ) {

            return new GowingToken2( tt, rval.toString(), _lnum, _offset );

        } else {

            if ( ch == -1 ) {

                return new GowingToken2( "unexpected EOF", _lnum, _offset );

            } else if ( Character.isDefined( ch ) ) {

                return new GowingToken2( "unexpected " + cleanupChar( ch ), _lnum, _offset );

            } else {

                return new GowingToken2( "unexpected value (" + ObtuseUtil.hexvalue( ch ) + ") expected a char", _lnum, _offset );

            }

        }

    }

    private GowingToken2 collectIdentifier()
            throws IOException {

        StringBuilder rval = new StringBuilder();

        int ch = nextCh();
        if ( Character.isJavaIdentifierStart( ch ) ) {

            rval.append( (char)ch );
            while ( Character.isJavaIdentifierPart( ch = nextCh() ) ) {

                rval.append( (char)ch );

            }

            putBackChar();

            return new GowingToken2( TokenType.IDENTIFIER, rval.toString(), _lnum, _offset );

        } else {

            return new GowingToken2( "expecting start of an identifier, found " + cleanupChar( ch ), _lnum, _offset );

        }

    }

    private String collectNumericString( @SuppressWarnings("SameParameterValue") final @NotNull String starter )
            throws IOException {

        StringBuilder buf = new StringBuilder( starter );
        while ( true ) {

            int ch = nextCh();

            if ( Character.isDigit( ch ) || ch == '.' || ch == 'e' || ch == '+' || ch == '-' /*( buf.length() == 0 && ch == '-' )*/ ) {

                buf.append( (char)ch );

            } else {

                putBackChar();

                break;

            }

        }

        return buf.toString();

    }

    private GowingToken2 parseNumeric( final TokenType tokenType, final NumericParser numericParser )
            throws IOException {

        String numericString = collectNumericString( "" );
        try {

            return new GowingToken2( tokenType, numericParser.parse( numericString ), _lnum, _offset );

        } catch ( NumberFormatException e ) {

            return new GowingToken2( "expected " + tokenType.toString().toLowerCase() + " but got \"" + numericString + "\"", _lnum, _offset );

        }

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Util", "testing", null );

        try {

            GowingTokenizer tokenizer = new StdGowingTokenizer(
                    new StdGowingUnPackerContext( new GowingTypeIndex( "test StdGowingTokenizer" ) ),
                    new LineNumberReader( new FileReader( "test1.p2a" ) )
            );
            boolean identifierAllowed = false;
            while ( true ) {

                GowingToken2 token = tokenizer.getNextToken( identifierAllowed );
                if ( token.isError() || token.type() == TokenType.EOF ) {

                    Logger.logMsg( "last token is " + token.toString() );

                    break;

                } else {

                    Logger.logMsg( "(" + token.getLnum() + "," + token.getOffset() + "):  " + token );

                    //noinspection RedundantIfStatement
                    if ( token.type() == TokenType.LEFT_PAREN || token.type() == TokenType.COMMA ) {

                        identifierAllowed = true;

                    } else {

                        identifierAllowed = false;

                    }

                }

            }

        } catch ( IOException | GowingUnpackingException e ) {

            e.printStackTrace();

        }

    }

    public String toString() {

        return "StdGowingTokenizer( " + _unPackerContext + " )";

    }

}
