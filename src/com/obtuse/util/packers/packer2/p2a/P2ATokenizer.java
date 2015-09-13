package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.packers.packer2.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.SortedMap;
import java.util.TreeMap;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A simple tokenizer.
 */

public class P2ATokenizer {

    private final UnPackerContext2 _unPackerContext;

    private final LineNumberReader _reader;

    private int _putBackChar;

    private boolean _hasPutBackChar;

    private P2AToken _putBackToken;

    private boolean _ignoreWhitespace;

    private int _lastLineLength;

    private int _lnum;

    private int _offset;

    private int _lastCh;

    private int _recursizeDepth = 0;

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

    public static class P2AToken {

	private final TokenType _tokenType;

	private final Object _value;

	private final int _lnum;

	private final int _offset;

	public P2AToken( TokenType tokenType, Object value, int lnum, int offset ) {
	    super();

	    _tokenType = tokenType;
	    _value = value;
	    _lnum = lnum;
	    _offset = offset;

	}

	public P2AToken( String errmsg, int lnum, int offset ) {

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
//	    return ( (EntityReference) _value ).getTypeId();
//
//	}
//
//	public long entityIdValue() {
//
//	    return ( (EntityReference) _value ).getEntityId();
//
//	}

	public EntityReference entityReference() {

	    return (EntityReference)_value;

	}

	public String stringValue() {

	    return (String) _value;

	}

	public Object getObjectValue() {

	    return _value;

	}

	public EntityName2 identifierValue() {

	    return new EntityName2( (String)_value );

	}

	public String toString() {

	    return "P2AToken( " + _tokenType + ", \"" + _value + "\", lnum=" + _lnum + ", offset=" + _offset + " )";

	}

	public String getDescription() {

	    StringBuilder rval = new StringBuilder();

	    rval.append( cleanupTokenType( _tokenType ) );
	    rval.append( " (" );
	    rval.append( _value.toString() );
	    rval.append( ") @ line " ).append( _lnum ).append( ", offset " ).append( _offset );

	    return rval.toString();

	}

	@NotNull
	public Packable2ThingHolder2 createHolder( EntityName2 entityName, P2AToken valueToken, UnPackerContext2 unPackerContext )
		throws UnPacker2ParsingException {

	    Packable2ThingHolder2 holder;
	    switch ( valueToken.type() ) {

		case NULL:
		    holder = new NullHolder2( entityName );
		    break;

		case BOOLEAN:
		    holder = new BooleanHolder2( entityName, valueToken.booleanValue(), true );
		    break;

		case BYTE:
		    holder = new ByteHolder2( entityName, valueToken.byteValue(), true );
		    break;

		case SHORT:
		    holder = new ShortHolder2( entityName, valueToken.shortValue(), true );
		    break;

		case INTEGER:
		    holder = new IntegerHolder2( entityName, valueToken.intValue(), true );
		    break;

		case LONG:
		    holder = new LongHolder2( entityName, valueToken.longValue(), true );
		    break;

		case FLOAT:
		    holder = new FloatHolder2( entityName, valueToken.floatValue(), true );
		    break;

		case DOUBLE:
		    holder = new DoubleHolder2( entityName, valueToken.doubleValue(), true );
		    break;

		case STRING:
		    holder = new StringHolder2( entityName, valueToken.stringValue(), true );
		    break;

		case ENTITY_REFERENCE:
//		    EntityTypeName2 entityTypeName = unPackerContext.findTypeByTypeReferenceId( valueToken.typeIdValue() );
//		    if ( entityTypeName == null ) {
//
//			throw new UnPacker2ParsingException( "unknown type id " + valueToken.typeIdValue(), valueToken );
//
//		    }

		    holder = new EntityReferenceHolder2( entityName, valueToken.entityReference(), true );
		    break;

		default:
		    throw new HowDidWeGetHereError( "token type " + valueToken.type() + " is not a 'value' type" );

	    }

	    return holder;

	}

    }

//	private final char[] _chars;

    public P2ATokenizer( UnPackerContext2 unPackerContext, LineNumberReader lineNumberReader ) {

	super();

	_unPackerContext = unPackerContext;
	_reader = lineNumberReader;
	_putBackChar = ' ';
	_hasPutBackChar = false;
	_putBackToken = null;
	_recursizeDepth = 0;
	_lnum = 1;
	_offset = 0;

	_ignoreWhitespace = true;

    }

    @NotNull
    public static String cleanupTokenType( TokenType tokenType ) {

	return tokenType.name().toLowerCase().replace( '_', '-' );

    }

    public void putBackToken( P2AToken token ) {

	if ( _recursizeDepth > 0 ) {

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

    @NotNull
    public P2AToken getNextToken( boolean identifierAllowed, @NotNull TokenType requiredType )
	    throws IOException, UnPacker2ParsingException {

	try {

	    _recursizeDepth += 1;

	    P2AToken rval = getNextToken( identifierAllowed );
	    if ( rval.isError() ) {

		return rval;

	    } else if ( rval.type() == requiredType ) {

		return rval;

	    } else {

		P2AToken errorToken = new P2AToken( "expected " + cleanupTokenType( requiredType ) + " but got " + cleanupTokenType( rval.type() ) + " instead", _lnum, _offset );
		throw new UnPacker2ParsingException( errorToken.stringValue(), errorToken );


	    }

//	} catch ( IOException e ) {
//
//	    e.printStackTrace();
//
//	} catch ( UnPacker2ParseError unPacker2ParseError ) {
//
//	    unPacker2ParseError.printStackTrace();

	} finally {

	    _recursizeDepth -= 1;

	}

    }

    @NotNull
    public P2AToken getNextToken( boolean identifierAllowed )
	    throws IOException, UnPacker2ParsingException {

	if ( _putBackToken != null ) {

	    P2AToken previousToken = _putBackToken;
	    _putBackToken = null;

	    return previousToken;

	}

	try {

	    _recursizeDepth += 1;

//	boolean spinAgain;
	    int ch;

	    while ( true ) {

    //	    spinAgain = false;

		ch = nextCh();
		char c = Character.isDefined( ch ) ? (char) ch : '?';

		TokenType singleCharacterTokenType = _singleCharacterTokens.get( (char)ch );
		if ( singleCharacterTokenType != null ) {

		    return new P2AToken( singleCharacterTokenType, (char)ch, _lnum, _offset );

		} else if ( Character.isDigit( ch ) ) {

		    putBackChar();

		    P2AToken longToken = parseNumeric(
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

		    P2AToken identifierToken = collectIdentifier();
		    if ( identifierToken.type() == TokenType.IDENTIFIER ) {

			EntityName2 identifier = identifierToken.identifierValue();
			if ( identifier.getName().charAt( 0 ) == Constants.TAG_ENTITY_REFERENCE ) {

			    ch = nextCh();

			    if ( ch == ':' ) {

				try {

				    int typeId = Integer.parseInt( identifier.getName().substring( 1 ) );

				    return finishCollectingEntityReference( _unPackerContext, typeId );

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
//				P2AToken maybeColonToken = getNextToken( false );
//				if ( maybeColonToken.type() == TokenType.COLON ) {
//
//
//				}
//			    }
			}

		    }

		    return identifierToken;

		} else {

		    P2AToken rval;

		    switch ( ch ) {

			// Comment handling belongs in nextCh

			case Constants.LINE_COMMENT_CHAR:

			    while ( ch != '\n' && ch != -1 ) {

				Logger.logMsg( "ignoring '" + ( Character.isDefined( ch ) ? (char) ch : '?' ) + "'" );
				ch = nextRawCh();

			    }

    //		    spinAgain = true;

			    break;

			case -1:

			    return new P2AToken( TokenType.EOF, -1, _lnum, _offset );

			case Constants.NULL_VALUE:

			    return new P2AToken( TokenType.NULL, ch, _lnum, _offset );

			case '"':

			    putBackChar();

			    rval = collectString();

			    return rval;

			case Constants.TAG_BOOLEAN:

			    ch = nextCh();
			    if ( ch == 'T' ) {

				return new P2AToken( TokenType.BOOLEAN, true, _lnum, _offset );

			    } else if ( ch == 'F' ) {

				return new P2AToken( TokenType.BOOLEAN, false, _lnum, _offset );

			    } else {

				return new P2AToken( "expected 'T' or 'F' but found " + cleanupChar( ch ), _lnum, _offset );

			    }

			case Constants.TAG_BYTE:

			    int c1 = nextCh();
			    if ( !Character.isDefined( c1 ) ) {

				return new P2AToken( "expected first hex digit [0-9a-f] but found " + cleanupChar( c1 ), _lnum, _offset );

			    }

			    int c2 = nextCh();
			    if ( c2 == -1 ) {

				return new P2AToken( "expected second hex digit [0-9a-f] but found " + cleanupChar( c2 ), _lnum, _offset );

			    }

			    try {

				int value = Integer.parseInt( "0x" + ( (char) c1 ) + ( (char) c2 ) );

				return new P2AToken( TokenType.BYTE, value, _lnum, _offset );

			    } catch ( NumberFormatException e ) {

				return new P2AToken(
					"expected two digit hex value but found \"" + ( (char) c1 ) + ( (char) c2 ) + '"',
					_lnum,
					_offset
				);

			    }

			case Constants.TAG_SHORT:

			    P2AToken shortToken = parseNumeric(
				    TokenType.SHORT,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Short.parseShort( strValue );

					}

				    }
			    );

			    return shortToken;

			case Constants.TAG_INTEGER:

			    P2AToken intToken = parseNumeric(
				    TokenType.INTEGER,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Integer.parseInt( strValue );

					}

				    }
			    );

			    return intToken;

			case Constants.TAG_LONG:

			    P2AToken longToken = parseNumeric(
				    TokenType.LONG,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Long.parseLong( strValue );

					}

				    }
			    );

			    return longToken;

			case Constants.TAG_DOUBLE:

			    P2AToken doubleToken = parseNumeric(
				    TokenType.DOUBLE,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Double.parseDouble( strValue );

					}

				    }
			    );

			    return doubleToken;

			case Constants.TAG_FLOAT:

			    P2AToken floatToken = parseNumeric(
				    TokenType.FLOAT,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Float.parseFloat( strValue );

					}

				    }
			    );

			    return floatToken;

			case Constants.TAG_FORMAT_VERSION:

			    P2AToken versionNumberToken = parseNumeric(
				    TokenType.FORMAT_VERSION,
				    new NumericParser() {

					@Override
					public Number parse( String strValue ) {

					    return Long.parseLong( strValue );

					}

				    }
			    );

    //			P2AToken colon = getNextToken( false );
    //			if ( colon.type() == TokenType.COLON ) {
    //
    //			    P2AToken groupName = getNextToken( false );
    //			    if ( groupName.type() == TokenType.STRING ) {
    //
    //				return new P2AToken(
    //					TokenType.FORMAT_VERSION,
    //				);
    //			    }
    //			}
			    return versionNumberToken;

			case Constants.TAG_ENTITY_REFERENCE:

			    P2AToken typeIdToken = parseNumeric(
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
				return finishCollectingEntityReference( _unPackerContext, typeId );

			    } else {

				return new P2AToken( "unexpected character " + cleanupChar( ch ), _lnum, _offset );

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
    //			return new P2AToken( "expected hex digit [0-9a-f] but found " + cleanupChar( c1 ), _lnum, _offset );
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
    //			return new P2AToken( "expected hex digit [0-9a-f] but found " + cleanupChar( c2 ), _lnum, _offset );
    //
    //		    }
    //
    //		    int hexValue = ( c1 << 4 ) | c2;
    //
    //		    return new P2AToken( TokenType.BYTE, hexValue, _lnum, _offset );

    //		case Constants.TAG_SHORT:
    //
    //		    P2AToken numericValue = getIntegralValue( TokenType.SHORT, new NumericStringParser);
    //		    if ( numericString == null ) {
    //
    //			return new P2AToken( "expected short value but" );
    //		    }

			default:

			    return new P2AToken( "unexpected character " + cleanupChar( ch ), _lnum, _offset );

		    }

		}

	    } // while ( spinAgain );

	} finally {

	    _recursizeDepth -= 1;

	}

    }

    @NotNull
    public P2AToken finishCollectingEntityReference( UnPackerContext2 unPackerContext, int typeId )
	    throws IOException, UnPacker2ParsingException {

	P2AToken entityIdToken = parseNumeric(
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

	    P2AToken entityVersionToken = parseNumeric(
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

		throw new UnPacker2ParsingException( "version number is not positive (" + version + ")", entityVersionToken );

	    }

	} else {

	    version = null;

	    putBackChar();

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

	    return new P2AToken(
		    TokenType.ENTITY_REFERENCE,
		    new EntityReference(
//			    unPackerContext.findTypeByTypeReferenceId( typeId ),
			    typeId,
			    entityIdToken.longValue(),
			    version
		    ),
		    _lnum,
		    _offset
	    );

	} catch ( IndexOutOfBoundsException e ) {

	    return new P2AToken(
		    e.getMessage(),
		    _lnum,
		    _offset
	    );

	}
    }

    private P2AToken collectString()
	    throws IOException {

	StringBuilder rval = new StringBuilder();
	int ch = nextRawCh();
	char delimeter = (char)ch;

	while ( Character.isDefined( ch = nextRawCh() ) && ch != delimeter ) {

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

			    return new P2AToken(
				    "unexpected '\\" + cleanupChar( ch ) + "' ligature in string",
				    _lnum,
				    _offset
			    );

		    }

		}

	    }

	    rval.append( (char)ch );

	}

	if ( ch == delimeter ) {

	    return new P2AToken( TokenType.STRING, rval.toString(), _lnum, _offset );

	} else {

	    if ( ch == -1 ) {

		return new P2AToken( "unexpected EOF", _lnum, _offset );

	    } else if ( Character.isDefined( ch ) ) {

		return new P2AToken( "unexpected " + cleanupChar( ch ), _lnum, _offset );

	    } else {

		return new P2AToken( "unexpected value (" + ObtuseUtil.hexvalue( ch ) + ") expected a char", _lnum, _offset );

	    }

	}

    }

    private P2AToken collectIdentifier()
	    throws IOException {

	StringBuilder rval = new StringBuilder();

	int ch = nextCh();
	if ( Character.isJavaIdentifierStart( ch ) ) {

	    rval.append( (char)ch );
	    while ( Character.isJavaIdentifierPart( ch = nextCh() ) ) {

		rval.append( (char)ch );

	    }

	    putBackChar();

	    return new P2AToken( TokenType.IDENTIFIER, rval.toString(), _lnum, _offset );

	} else {

	    return new P2AToken( "expecting start of an identifier, found " + cleanupChar( ch ), _lnum, _offset );

	}

    }

    private String collectNumericString( @NotNull String starter )
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

    private P2AToken parseNumeric( TokenType tokenType, NumericParser numericParser )
	    throws IOException {

	String numericString = collectNumericString( "" );
	try {

	    return new P2AToken( tokenType, numericParser.parse( numericString ), _lnum, _offset );

	} catch ( NumberFormatException e ) {

	    return new P2AToken( "expected " + tokenType.toString().toLowerCase() + " but got \"" + numericString + "\"", _lnum, _offset );

	}

    }

    public static void main( String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Util", "testing", null );

	try {

	    P2ATokenizer tokenizer = new P2ATokenizer( new StdUnPackerContext2( new TypeIndex2( "test P2ATokenizer" ) ), new LineNumberReader( new FileReader( "test1.p2a" ) ) );
	    P2AToken token;
	    boolean identifierAllowed = false;
	    while ( ( token = tokenizer.getNextToken( identifierAllowed ) ) != null && !token.isError() && token.type() != TokenType.EOF ) {

		Logger.logMsg( "(" + token.getLnum() + "," + token.getOffset() + "):  " + token );

		if ( token.type() == TokenType.LEFT_PAREN || token.type() == TokenType.COMMA ) {

		    identifierAllowed = true;

		} else {

		    identifierAllowed = false;

		}

	    }

	    Logger.logMsg( "last token is " + token.toString() );

	} catch ( FileNotFoundException e ) {

	    e.printStackTrace();

	} catch ( IOException e ) {

	    e.printStackTrace();

	} catch ( UnPacker2ParsingException e ) {

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
