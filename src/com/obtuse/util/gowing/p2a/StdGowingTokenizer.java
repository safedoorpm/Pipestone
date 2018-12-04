package com.obtuse.util.gowing.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.Measure;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.ParsingLocation;
import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingMetaDataHandler;
import com.obtuse.util.gowing.GowingUnPackerContext;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnexpectedEofException;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
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

    private GowingToken _putBackToken;

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
        FILE {
            public boolean isScalarType() {

                return true;

            }
        },
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

    public ParsingLocation curLoc() {

        return new ParsingLocation( _lnum, _offset );

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
    public void putBackToken( final GowingToken token ) {

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

    private int nextRawChNoEOF() throws IOException, GowingUnexpectedEofException {

        int rval = nextRawCh();
        if ( rval == -1 ) {

            throw new GowingUnexpectedEofException( _lnum, _offset );

        }

        return rval;

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
    public GowingToken getNextToken( final boolean identifierAllowed, final @NotNull TokenType requiredType )
            throws IOException, GowingUnpackingException {

        try ( Measure ignored = new Measure( "Gowing-getNextToken-" + _recursiveDepth ) ) {

            _recursiveDepth += 1;

            GowingToken rval = getNextToken( identifierAllowed, "gNT" );
            if ( rval.isError() ) {

                return rval;

            } else if ( rval.type() == requiredType ) {

                return rval;

            } else {

                GowingToken errorToken =
                        new GowingToken( "expected " + cleanupTokenType( requiredType ) + " but got " + cleanupTokenType( rval.type() ) + " instead",
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
    public GowingToken getNextToken( final boolean identifierAllowed, final String where )
            throws IOException, GowingUnpackingException {

        if ( _putBackToken != null ) {

            try ( Measure ignored = new Measure( "Gowing-getNextToken-inner-quick-" + where + '-' + _recursiveDepth ) ) {

                GowingToken previousToken = _putBackToken;
                _putBackToken = null;

                return previousToken;

            }

        }

        try ( Measure ignored = new Measure( "Gowing-getNextToken-inner-" + where + '-' + _recursiveDepth ) ) {

            _recursiveDepth += 1;

            int ch;

            while ( true ) {

                //	    spinAgain = false;

                ch = nextCh();

                @SuppressWarnings("unused") char c = Character.isDefined( ch ) ? (char)ch : '?';

                TokenType singleCharacterTokenType = _singleCharacterTokens.get( (char)ch );
                if ( singleCharacterTokenType != null ) {

                    return new GowingToken( singleCharacterTokenType, (char)ch, _lnum, _offset );

                } else if ( Character.isDigit( ch ) ) {

                    putBackChar();

                    GowingToken longToken = parseNumeric(
                            TokenType.LONG,
                            Long::parseLong
                    );

                    return longToken;

                } else if ( identifierAllowed && Character.isJavaIdentifierPart( ch ) ) {

                    putBackChar();

                    GowingToken identifierToken = collectIdentifier();
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

                    GowingToken rval;

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

                                        GowingToken errorToken =
                                                new GowingToken( errmsg,
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

                            return new GowingToken( TokenType.EOF, -1, _lnum, _offset );

                        case GowingConstants.NULL_VALUE:

                            return new GowingToken( TokenType.NULL, ch, _lnum, _offset );

                        case '"':

                            putBackChar();

                            rval = collectString( TokenType.STRING );

                            return rval;

                        case GowingConstants.TAG_FILE:

                            rval = collectString( TokenType.FILE );

                            return rval;

                        case GowingConstants.TAG_ENTITY_NAME:

                            rval = collectString( TokenType.ENTITY_NAME );

                            return rval;

                        case GowingConstants.TAG_BOOLEAN:

                            ch = nextCh();
                            if ( ch == 'T' ) {

                                return new GowingToken( TokenType.BOOLEAN, true, _lnum, _offset );

                            } else if ( ch == 'F' ) {

                                return new GowingToken( TokenType.BOOLEAN, false, _lnum, _offset );

                            } else {

                                return new GowingToken( "expected 'T' or 'F' but found " + cleanupChar( ch ), _lnum, _offset );

                            }

                        case GowingConstants.TAG_BYTE:

                            int c1 = nextCh();
                            if ( !Character.isDefined( c1 ) ) {

                                return new GowingToken( "expected first hex digit [0-9a-f] but found " + cleanupChar( c1 ), _lnum, _offset );

                            }

                            int c2 = nextCh();
                            if ( c2 == -1 ) {

                                return new GowingToken( "expected second hex digit [0-9a-f] but found " + cleanupChar( c2 ), _lnum, _offset );

                            }

                            try {

                                byte value = parseHexByte( c1, c2 );

                                return new GowingToken( TokenType.BYTE, value, _lnum, _offset );

                            } catch ( NumberFormatException e ) {

                                return new GowingToken(
                                        "expected two digit hex value but found \"" + ( (char)c1 ) + ( (char)c2 ) + '"',
                                        _lnum,
                                        _offset
                                );

                            }

                        case GowingConstants.TAG_SHORT:

                            GowingToken shortToken = parseNumeric(
                                    TokenType.SHORT,
                                    Short::parseShort
                            );

                            return shortToken;

                        case GowingConstants.TAG_INTEGER:

                            GowingToken intToken = parseNumeric(
                                    TokenType.INTEGER,
                                    Integer::parseInt
                            );

                            return intToken;

                        case GowingConstants.TAG_LONG:

                            GowingToken longToken = parseNumeric(
                                    TokenType.LONG,
                                    Long::parseLong
                            );

                            return longToken;

                        case GowingConstants.TAG_DOUBLE:

                            GowingToken doubleToken = parseNumeric(
                                    TokenType.DOUBLE,
                                    Double::parseDouble
                            );

                            return doubleToken;

                        case GowingConstants.TAG_FLOAT:

                            GowingToken floatToken = parseNumeric(
                                    TokenType.FLOAT,
                                    Float::parseFloat
                            );

                            return floatToken;

                        case GowingConstants.TAG_FORMAT_VERSION:

                            GowingToken versionNumberToken = parseNumeric(
                                    TokenType.FORMAT_VERSION,
                                    Long::parseLong
                            );

                            return versionNumberToken;

                        case GowingConstants.TAG_ENTITY_REFERENCE:

                            GowingToken typeIdToken = parseNumeric(
                                    TokenType.INTEGER,
                                    Integer::parseInt
                            );

                            if ( typeIdToken.isError() ) {

                                return typeIdToken;

                            }

                            ch = nextCh();
                            if ( ch == ':' ) {

                                int typeId = typeIdToken.intValue();
                                return finishCollectingEntityReference( !identifierAllowed, _unPackerContext, typeId );

                            } else {

                                return new GowingToken( "unexpected character " + cleanupChar( ch ), _lnum, _offset );

                            }

                        default:

                            return new GowingToken( "unexpected character " + cleanupChar( ch ), _lnum, _offset );

                    }

                }

            } // while ( spinAgain );

        } finally {

            _recursiveDepth -= 1;

        }

    }

    private final List<GowingMetaDataHandler> s_metadataHandlers = new ArrayList<>();

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

        } else if ( rawValue.endsWith( "B" ) || rawValue.endsWith( "b" ) ) {

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
    private GowingToken getArray2( final boolean primitive )
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

                            return new GowingToken( "expected " +
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

                            return new GowingToken(
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

                            return new GowingToken(
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

                        GowingToken usersEntityNameStringToken = collectString( TokenType.ENTITY_NAME );
                        if ( usersEntityNameStringToken.type() == TokenType.ENTITY_NAME ) {

                            return new EntityName( usersEntityNameStringToken.stringValue() );

                        } else {

                            return new GowingToken(
                                    usersEntityNameStringToken.getObjectValue() + " looking for user's EntityName string",
                                    usersEntityNameStringToken.getLnum(),
                                    usersEntityNameStringToken.getOffset()
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

                        GowingToken stringToken = collectString( TokenType.STRING );
                        if ( stringToken.type() == TokenType.STRING ) {

                            return stringToken.stringValue();

                        } else {

                            return new GowingToken(
                                    stringToken.getObjectValue() + " looking for user's string",
                                    stringToken.getLnum(),
                                    stringToken.getOffset()
                            );

                        }

                    }

                    @Override
                    public String toString() {

                        return "String ElementParser";
                    }

                };

                break;

            case GowingConstants.TAG_FILE:

                elementType = TokenType.FILE;
                array = new String[length];
                what = "File";
                elementParser = new ElementParser() {
                    @Override
                    public Object parse( final int index ) throws IOException, NumberFormatException {

                        GowingToken fileToken = collectString( TokenType.FILE );
                        if ( fileToken.type() == TokenType.FILE ) {

                            return fileToken.stringValue();

                        } else {

                            return new GowingToken(
                                    fileToken.getObjectValue() + " looking for user's File",
                                    fileToken.getLnum(),
                                    fileToken.getOffset()
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

                return new GowingToken(
                        "expected scalar type letter but found '" + cleanupChar( ch ) + "'",
                        _lnum,
                        _offset
                );

        }

        ch = nextCh();

        if ( ch != '[' ) {

            return new GowingToken( "unexpected character " + cleanupChar( ch ) + " (expected '[')", _lnum, _offset );

        }

        for ( int ix = 0; ix < length; ix += 1 ) {

            ObtuseUtil.doNothing();

            if ( ix > 0 ) {

                // A comma is allowed here if this is a primitive byte array.
                // A comma is required here for all other kinds of arrays.

                ch = nextCh();
                if ( ch != ',' ) {

                    if ( !primitive || !elementType.equals( TokenType.BYTE ) ) {

                        return new GowingToken(
                                "expected comma in array after element " + ( ix - 1 ) + " but found " + cleanupChar( ch ),
                                _lnum,
                                _offset
                        );

                    } else {

                        // We just swallowed a mandatory comma - cough it up and put it back.

                        putBackChar();

                    }

                } // else {

                    // Just swallow the comma.

                // }

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

                if ( element instanceof GowingToken ) {

                    // Something went wrong.

                    return (GowingToken)element;

                }

                try {

                    Array.set( array, ix, element );

                } catch ( IllegalArgumentException e ) {

                    throw new HowDidWeGetHereError( "IAE trying to save array element value " + element + " at " + ix, e );

                } catch ( ArrayIndexOutOfBoundsException e ) {

                    throw new HowDidWeGetHereError( "Array index error trying to save array element value " + element + " at " + ix, e );

                }

            } catch ( NumberFormatException e ) {

                return new GowingToken(
                        "expected a " + what + " for element " + ix + " + but found something unparsable ",
                        _lnum,
                        _offset
                );

            }

        }

        ch = nextCh();
        if ( ch == ']' ) {

            if ( elementType.isScalarType() ) {

                return new GowingToken( primitive ? TokenType.PRIMITIVE_ARRAY : TokenType.CONTAINER_ARRAY, elementType, array, _lnum, _offset );

            } else {

                return new GowingToken(
                        "array has no defined element type",
                        _lnum,
                        _offset
                );

            }

        } else {

            return new GowingToken( "unexpected character " + cleanupChar( ch ) + " (expected ']')", _lnum, _offset );

        }

    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public GowingToken finishCollectingEntityReference(
            final boolean entityNameOk,
            @SuppressWarnings("unused") final GowingUnPackerContext unPackerContext,
            final int typeId
    )
            throws IOException, GowingUnpackingException {

        GowingToken entityIdToken = parseNumeric(
                TokenType.LONG,
                Long::parseLong
        );

        if ( entityIdToken.isError() ) {

            return entityIdToken;

        }

        Integer version;
        int ch = nextCh();
        if ( ch == 'v' ) {

            GowingToken entityVersionToken = parseNumeric(
                    TokenType.INTEGER,
                    Integer::parseInt
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

                GowingToken entityNameClauseMarker = new GowingToken(
                        TokenType.ENTITY_NAME_CLAUSE_MARKER,
                        GowingConstants.ENTITY_NAME_CLAUSE_MARKER,
                        _lnum,
                        _offset
                );

                if ( entityNameOk ) {

                    ch = nextCh();
                    if ( ch == '"' ) {

                        putBackChar();

                        GowingToken entityNameToken = collectString( TokenType.ENTITY_NAME );

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

            return new GowingToken(
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

            return new GowingToken(
                    e.getMessage(),
                    _lnum,
                    _offset
            );

        }
    }

    private GowingToken collectString( final @NotNull TokenType tt )
            throws IOException {

        try {

            StringBuilder rval = new StringBuilder();
            int ch = nextRawChNoEOF();
            char delimiter = (char)ch;

            while ( ( ch = nextRawChNoEOF() ) != delimiter ) {

                if ( !Character.isDefined( ch ) ) {

                    ObtuseUtil.doNothing();

                }

                if ( ch == '\\' ) {

                    ch = nextRawChNoEOF();
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

                                return new GowingToken(
                                        "unexpected '\\" + cleanupChar( ch ) + "' ligature in string",
                                        _lnum,
                                        _offset
                                );

                        }

                    } else {

                        // Just accept undefined Unicode codepoints as we find them.
                        // History has shown that we emit them sometimes so we better swallow them as well.

                        ObtuseUtil.doNothing();

                    }

                }

                rval.append( (char)ch );

            }

            // We don't want changes to the logic above to result in subtle bugs
            // so we're going to handle the case where we get here with {@code ch != delimiter}.

            //noinspection ConstantConditions
            if ( ch == delimiter ) {

                return new GowingToken( tt, rval.toString(), _lnum, _offset );

            } else {

                if ( Character.isDefined( ch ) ) {

                    return new GowingToken( "unexpected " + cleanupChar( ch ), _lnum, _offset );

                } else {

                    return new GowingToken( "unexpected value (" + ObtuseUtil.hexvalue( ch ) + ") expected a char", _lnum, _offset );

                }

            }

        } catch ( GowingUnexpectedEofException e ) {

            return new GowingToken( "unexpected EOF", _lnum, _offset, e );

        }

    }

    private GowingToken collectIdentifier()
            throws IOException {

        StringBuilder rval = new StringBuilder();

        int ch = nextCh();
        if ( Character.isJavaIdentifierStart( ch ) ) {

            rval.append( (char)ch );
            while ( Character.isJavaIdentifierPart( ch = nextCh() ) ) {

                rval.append( (char)ch );

            }

            putBackChar();

            return new GowingToken( TokenType.IDENTIFIER, rval.toString(), _lnum, _offset );

        } else {

            return new GowingToken( "expecting start of an identifier, found " + cleanupChar( ch ), _lnum, _offset );

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

    private GowingToken parseNumeric( final TokenType tokenType, final NumericParser numericParser )
            throws IOException {

        String numericString = collectNumericString( "" );
        try {

            return new GowingToken( tokenType, numericParser.parse( numericString ), _lnum, _offset );

        } catch ( NumberFormatException e ) {

            return new GowingToken( "expected " + tokenType.toString().toLowerCase() + " but got \"" + numericString + "\"", _lnum, _offset );

        }

    }

    public String toString() {

        return "StdGowingTokenizer( " + _unPackerContext + " )";

    }

}
