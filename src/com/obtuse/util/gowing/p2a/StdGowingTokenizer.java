package com.obtuse.util.gowing.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.holders.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A simple tokenizer.
 */

public class StdGowingTokenizer implements GowingTokenizer {

    private final GowingUnPackerContext _unPackerContext;

    private final LineNumberReader _reader;

    private int _putBackChar;

    private boolean _hasPutBackChar;

    private GowingToken2 _putBackToken;

    private boolean _ignoreWhitespace;

    private int _lastLineLength;

    private int _lnum;

    private int _offset;

    private int _lastCh;

    private int _recursiveDepth = 0;

    public enum TokenType {

	ERROR,
	IDENTIFIER,
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
	BOOLEAN,
	CHAR,
	BYTE,
	SHORT,
	INTEGER,
	LONG,
	DOUBLE,
	FLOAT,
	ENTITY_REFERENCE,
	ENTITY_NAME_CLAUSE_MARKER,
	FORMAT_VERSION,
	STRING,
	NULL,
	EOF;

	public boolean isNumber() { int ord = ordinal(); return byteOrdinal <= ord && ord <= floatOrdinal; }

	private static final int byteOrdinal = BYTE.ordinal();
	private static final int floatOrdinal = FLOAT.ordinal();

    }

    public interface NumericParser {

	Number parse( String strValue );

    }

    public static class GowingToken2 {

	private final TokenType _tokenType;

	private final Object _value;

	private final int _lnum;

	private final int _offset;

	public GowingToken2( TokenType tokenType, Object value, int lnum, int offset ) {
	    super();

	    _tokenType = tokenType;
	    _value = value;
	    _lnum = lnum;
	    _offset = offset;

	}

	public GowingToken2( String errmsg, int lnum, int offset ) {

	    _tokenType = TokenType.ERROR;
	    _value = errmsg;
	    _lnum = lnum;
	    _offset = offset;

	}

	public TokenType type() {

	    return _tokenType;

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

	    return ( (Boolean) _value ).booleanValue();

	}

	public char charValue() {

	    return ( (Character) _value ).charValue();

	}

	public byte byteValue() {

	    return ( (Number) _value ).byteValue();

	}

	public short shortValue() {

	    return ( (Number) _value ).shortValue();

	}

	public int intValue() {

	    return ( (Number) _value ).intValue();

	}

	public long longValue() {

	    return ( (Number) _value ).longValue();

	}

	public double doubleValue() {

	    return ( (Number) _value ).doubleValue();

	}

	public float floatValue() {

	    return ( (Number) _value ).floatValue();

	}

//	public int typeIdValue() {
//
//	    return ( (GowingEntityReference) _value ).getTypeId();
//
//	}
//
//	public long entityIdValue() {
//
//	    return ( (GowingEntityReference) _value ).getEntityId();
//
//	}

	public GowingEntityReference entityReference() {

	    return (GowingEntityReference)_value;

	}

	public String stringValue() {

	    return (String) _value;

	}

	public Object getObjectValue() {

	    return _value;

	}

	public EntityName identifierValue() {

	    return new EntityName( (String)_value );

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
	public GowingPackableThingHolder createHolder( EntityName entityName, GowingToken2 valueToken )
		throws GowingUnPackerParsingException {

	    GowingPackableThingHolder holder;
	    switch ( valueToken.type() ) {

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

		case ENTITY_REFERENCE:
//		    EntityTypeName entityTypeName = unPackerContext.findTypeByTypeReferenceId( valueToken.typeIdValue() );
//		    if ( entityTypeName == null ) {
//
//			throw new UnPacker2ParsingException( "unknown type id " + valueToken.typeIdValue(), valueToken );
//
//		    }

		    holder = new GowingEntityReferenceHolder( entityName, valueToken.entityReference(), true );
		    break;

		default:
		    throw new HowDidWeGetHereError( "token type " + valueToken.type() + " is not a 'value' type" );

	    }

	    return holder;

	}

    }

//	private final char[] _chars;

    public StdGowingTokenizer( GowingUnPackerContext unPackerContext, LineNumberReader lineNumberReader ) {

	super();

	_unPackerContext = unPackerContext;
	_reader = lineNumberReader;
	_putBackChar = ' ';
	_hasPutBackChar = false;
	_putBackToken = null;
	_recursiveDepth = 0;
	_lnum = 1;
	_offset = 0;

	_ignoreWhitespace = true;

    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public static String cleanupTokenType( TokenType tokenType ) {

	return tokenType.name().toLowerCase().replace( '_', '-' );

    }

    @Override
    public void putBackToken( GowingToken2 token ) {

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

    private int peekCh()
	    throws IOException {

	int ch = nextCh();
	putBackChar();

	return ch;

    }

    private boolean ignoreWhitespace() {

	return _ignoreWhitespace;

    }

    private void setIgnoreWhitespace( boolean ignoreWhitespace ) {

	_ignoreWhitespace = ignoreWhitespace;

    }

    private String cleanupChar( int ch ) {

	switch ( ch ) {

	    case '\n': return "\\n";
	    case '\t': return "\\t";
	    case -1: return "EOF";
	    default: return Character.toString( (char)ch );

	}

    }

    private static final SortedMap<Character,TokenType> _singleCharacterTokens;
    static {

	_singleCharacterTokens = new TreeMap<Character, TokenType>();
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
    public GowingToken2 getNextToken( boolean identifierAllowed, @NotNull TokenType requiredType )
	    throws IOException, GowingUnPackerParsingException {

	try {

	    _recursiveDepth += 1;

	    GowingToken2 rval = getNextToken( identifierAllowed );
	    if ( rval.isError() ) {

		return rval;

	    } else if ( rval.type() == requiredType ) {

		return rval;

	    } else {

		GowingToken2 errorToken = new GowingToken2( "expected " + cleanupTokenType( requiredType ) + " but got " + cleanupTokenType( rval.type() ) + " instead", _lnum, _offset );
		throw new GowingUnPackerParsingException( errorToken.stringValue(), errorToken );


	    }

//	} catch ( IOException e ) {
//
//	    e.printStackTrace();
//
//	} catch ( UnPacker2ParseError unPacker2ParseError ) {
//
//	    unPacker2ParseError.printStackTrace();

	} finally {

	    _recursiveDepth -= 1;

	}

    }

    @Override
    @NotNull
    public GowingToken2 getNextToken( boolean identifierAllowed )
	    throws IOException, GowingUnPackerParsingException {

	if ( _putBackToken != null ) {

	    GowingToken2 previousToken = _putBackToken;
	    _putBackToken = null;

	    return previousToken;

	}

	try {

	    _recursiveDepth += 1;

//	boolean spinAgain;
	    int ch;

	    while ( true ) {

    //	    spinAgain = false;

		ch = nextCh();
		@SuppressWarnings("UnusedAssignment") char c = Character.isDefined( ch ) ? (char) ch : '?';

		TokenType singleCharacterTokenType = _singleCharacterTokens.get( (char)ch );
		if ( singleCharacterTokenType != null ) {

		    return new GowingToken2( singleCharacterTokenType, (char)ch, _lnum, _offset );

		} else if ( Character.isDigit( ch ) ) {

		    putBackChar();

		    GowingToken2 longToken = parseNumeric(
			    TokenType.LONG,
			    new NumericParser() {

				@Override
				public Number parse( String strValue ) {

				    return Long.parseLong( strValue );

				}

			    }
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

				    return finishCollectingEntityReference( !identifierAllowed, _unPackerContext, typeId );

				} catch ( NumberFormatException e ) {

				    putBackChar();

				    return identifierToken;

				}

			    } else {

				putBackChar();

				return identifierToken;

			    }

//			    try {
//
//				int typeId = Integer.parseInt( identifier.getName().substring( 1 ) );
//
//				GowingToken2 maybeColonToken = getNextToken( false );
//				if ( maybeColonToken.type() == TokenType.COLON ) {
//
//
//				}
//			    }
			}

		    }

		    return identifierToken;

		} else {

		    GowingToken2 rval;

		    switch ( ch ) {

			// Comment handling belongs in nextCh

			case GowingConstants.LINE_COMMENT_CHAR:
			case GowingConstants.LINE_METADATA_CHAR:

			    StringBuilder sb = new StringBuilder( "" + (char)ch );
			    while ( ch != '\n' && ch != -1 ) {

//				Logger.logMsg( "ignoring '" + ( Character.isDefined( ch ) ? (char) ch : '?' ) + "'" );
				sb.append( (char)ch );
				ch = nextRawCh();

			    }

			    Logger.logMsg( "ignoring " + ObtuseUtil.enquoteForJavaString( sb.toString() ) );

    //		    spinAgain = true;

			    break;

			case -1:

			    return new GowingToken2( TokenType.EOF, -1, _lnum, _offset );

			case GowingConstants.NULL_VALUE:

			    return new GowingToken2( TokenType.NULL, ch, _lnum, _offset );

			case '"':

			    putBackChar();

			    rval = collectString();

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

				int value = Integer.parseInt( "0x" + ( (char) c1 ) + ( (char) c2 ) );

				return new GowingToken2( TokenType.BYTE, value, _lnum, _offset );

			    } catch ( NumberFormatException e ) {

				return new GowingToken2(
					"expected two digit hex value but found \"" + ( (char) c1 ) + ( (char) c2 ) + '"',
					_lnum,
					_offset
				);

			    }

			case GowingConstants.TAG_SHORT:

			    GowingToken2 shortToken = parseNumeric(
				    TokenType.SHORT,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Short.parseShort( strValue );

					}

				    }
			    );

			    return shortToken;

			case GowingConstants.TAG_INTEGER:

			    GowingToken2 intToken = parseNumeric(
				    TokenType.INTEGER,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Integer.parseInt( strValue );

					}

				    }
			    );

			    return intToken;

			case GowingConstants.TAG_LONG:

			    GowingToken2 longToken = parseNumeric(
				    TokenType.LONG,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Long.parseLong( strValue );

					}

				    }
			    );

			    return longToken;

			case GowingConstants.TAG_DOUBLE:

			    GowingToken2 doubleToken = parseNumeric(
				    TokenType.DOUBLE,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Double.parseDouble( strValue );

					}

				    }
			    );

			    return doubleToken;

			case GowingConstants.TAG_FLOAT:

			    GowingToken2 floatToken = parseNumeric(
				    TokenType.FLOAT,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Float.parseFloat( strValue );

					}

				    }
			    );

			    return floatToken;

			case GowingConstants.TAG_FORMAT_VERSION:

			    GowingToken2 versionNumberToken = parseNumeric(
				    TokenType.FORMAT_VERSION,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Long.parseLong( strValue );

					}

				    }
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
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Integer.parseInt( strValue );

					}

				    }
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

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public GowingToken2 finishCollectingEntityReference( boolean entityNameOk, GowingUnPackerContext unPackerContext, int typeId )
	    throws IOException, GowingUnPackerParsingException {

	GowingToken2 entityIdToken = parseNumeric(
		TokenType.LONG,
		new NumericParser() {

		    @Override
		    public Number parse( String strValue ) {

			return Long.parseLong( strValue );

		    }

		}
	);

	if ( entityIdToken.isError() ) {

	    return entityIdToken;

	}

	Integer version;
	int ch = nextCh();
	if ( ch == 'v' ) {

	    GowingToken2 entityVersionToken = parseNumeric(
		    TokenType.INTEGER,
		    new NumericParser() {

			@Override
			public Number parse( String strValue ) {

			    return Integer.parseInt( strValue );

			}

		    }
	    );

	    if ( entityVersionToken.isError() ) {

		return entityVersionToken;

	    }

	    version = entityVersionToken.intValue();
	    if ( version <= 0 ) {

		throw new GowingUnPackerParsingException( "version number is not positive (" + version + ")", entityVersionToken );

	    }

	} else {

	    version = null;

	    putBackChar();

	}

//	GowingToken2 entityNameString = null;

	SortedSet<EntityName> entityNames = new TreeSet<EntityName>();

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

			GowingToken2 entityNameToken = collectString();

			entityNames.add( entityNameToken.identifierValue() );

		    } else {

			throw new GowingUnPackerParsingException( "entity name string missing in entity name clause", entityNameClauseMarker );

		    }

		} else {

		    throw new GowingUnPackerParsingException( "entity name clause marker ('" + GowingConstants.ENTITY_NAME_CLAUSE_MARKER + "') not allowed in this context", entityNameClauseMarker );

		}

	    } else {

		putBackChar();

		break;

	    }

	}

//	    } else {
//
//		throw new UnPacker2ParsingException( "no version provided in entity reference that requires one", entityIdToken );
//
//	    }
//
//	} else {
//
//	    int ch = peekCh();
//	    if ( ch == 'v' ) {
//
//		throw new UnPacker2ParsingException( "unexpected version clause on entity reference", entityIdToken );
//
//	    }
//
//	    version = null;	// signal that there is no version expected
//
//	}

	try {

	    return new GowingToken2(
		    TokenType.ENTITY_REFERENCE,
		    new GowingEntityReference(
//			    unPackerContext.findTypeByTypeReferenceId( typeId ),
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

    private GowingToken2 collectString()
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

	    return new GowingToken2( TokenType.STRING, rval.toString(), _lnum, _offset );

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

    private String collectNumericString( @SuppressWarnings("SameParameterValue") @NotNull String starter )
	    throws IOException {

	StringBuilder buf = new StringBuilder( starter );
	while ( true ) {

	    int ch = nextCh();

	    if ( Character.isDigit( ch ) || ch == '.' || ch == 'e' || ch == '+' || ch == '-' /*( buf.length() == 0 && ch == '-' )*/ ) {

		buf.append( (char) ch );

	    } else {

		putBackChar();

		break;

	    }

	}

	return buf.toString();

    }

    private String collectFloatingPointString( char precisionIndicator )
	    throws IOException {

	StringBuilder buf = new StringBuilder();
	while ( true ) {

	    int ch = nextCh();

	    if ( Character.isDigit( ch ) || ( buf.length() == 0 && ch == '-' ) ) {

		buf.append( (char) ch );

	    } else {

		if ( ch != precisionIndicator ) {

		    putBackChar();

		}

		break;

	    }

	}

	return buf.toString();

    }

    private GowingToken2 parseNumeric( TokenType tokenType, NumericParser numericParser )
	    throws IOException {

	String numericString = collectNumericString( "" );
	try {

	    return new GowingToken2( tokenType, numericParser.parse( numericString ), _lnum, _offset );

	} catch ( NumberFormatException e ) {

	    return new GowingToken2( "expected " + tokenType.toString().toLowerCase() + " but got \"" + numericString + "\"", _lnum, _offset );

	}

    }

    public static void main( String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Util", "testing", null );

	try {

	    GowingTokenizer tokenizer = new StdGowingTokenizer( new StdGowingUnPackerContext( new GowingTypeIndex( "test StdGowingTokenizer" ) ), new LineNumberReader( new FileReader( "test1.p2a" ) ) );
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

	} catch ( FileNotFoundException e ) {

	    e.printStackTrace();

	} catch ( IOException e ) {

	    e.printStackTrace();

	} catch ( GowingUnPackerParsingException e ) {

	    e.printStackTrace();

	}

//	Logger.logMsg( "parsing small enough short . . . " + Short.parseShort( "32767" ) );
//	Logger.logMsg( "parsing small enough short . . . " + Short.parseShort( "-32768" ) );
//	Logger.logMsg( "parsing too large short . . ." );
//	Logger.logMsg( "got " + Short.parseShort( "32768" ) );
//	System.exit( 0 );

//	try {
//
//	    LineNumberReader lnr = new LineNumberReader( new StringReader( "i43230573975978" ) );
//
//	    P2ATokenizer tokenizer = new P2ATokenizer( lnr );
//
////	    for ( int i = 0; i < )
//	} catch ( FileNotFoundException e ) {
//
//	    e.printStackTrace();
//
//	}

    }

}
