package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.packers.packer2.*;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Unpack entities using a purely text-based format (no binary data) and explicitly named fields.
 */

public class StdUnPacker2a implements UnPacker2 {

    public static final long OLDEST_MAJOR_VERSION = 1L;
    public static final long NEWEST_MAJOR_VERSION = 1L;
    public static final long OLDEST_MINOR_VERSION = 1L;
    public static final long NEWEST_MINOR_VERSION = 1L;

    private final P2ATokenizer _tokenizer;

    private final PackingContext2 _packingContext;

    public StdUnPacker2a( TypeIndex2 typeIndex, File inputFile )
	    throws IOException {
	this( inputFile, new LineNumberReader( new FileReader( inputFile ) ), new StdPackingContext2( typeIndex ) );

    }

    public StdUnPacker2a( TypeIndex2 typeIndex, File inputFile, Reader reader )
	    throws IOException {
	this(
		inputFile,
		reader instanceof LineNumberReader ? (LineNumberReader)reader : new LineNumberReader( reader ),
		new StdPackingContext2( typeIndex )
	);

    }

    public StdUnPacker2a( File inputFile, LineNumberReader reader, PackingContext2 packingContext )
	    throws IOException {
	super();

	_packingContext = packingContext;
	_tokenizer = new P2ATokenizer( reader );

    }

    private FormatVersion parseVersion()
	    throws IOException, UnPacker2ParseError {

	P2ATokenizer.P2AToken versionToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.FORMAT_VERSION );
	P2ATokenizer.P2AToken colon = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.COLON );
	P2ATokenizer.P2AToken groupName = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.STRING );
//	P2ATokenizer.P2AToken semiColon = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.SEMI_COLON );

	return new FormatVersion( versionToken, new EntityName2( groupName.stringValue() ) );

//	} else {
//
//	    throw new UnPacker2ParseError( "expected the format version, found " + versionToken.getDescription() + " instead", versionToken );
//
//	}

    }

    @Nullable
    public UnpackedEntityGroup parse() {

	try {

	    UnpackedEntityGroup group;

	    FormatVersion version = parseVersion();
	    P2ATokenizer.P2AToken semiColon = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.SEMI_COLON );

	    group = new UnpackedEntityGroup( version );

	    while ( true ) {

		P2ATokenizer.P2AToken token = _tokenizer.getNextToken( false );

		if ( token.type() == P2ATokenizer.TokenType.LONG ) {

		    _tokenizer.putBackToken( token );
		    collectTypeAlias();
		    semiColon = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.SEMI_COLON );

		} else if ( token.type() == P2ATokenizer.TokenType.ENTITY_REFERENCE ){

		    _tokenizer.putBackToken( token );
		    collectObjectInstance( false );

//		    throw new UnPacker2ParseError( "no support for entity definitions yet", token );
//		    definition = parseEntityDefinition();

		} else if ( token.type() == P2ATokenizer.TokenType.EOF ) {

		    Logger.logMsg( "EOF reached" );

		    break;

		} else {

		    throw new UnPacker2ParseError( "unexpected token " + token, token );

		}

	    }

	    return group;

	} catch ( UnPacker2ParseError e ) {

	    Logger.logErr( "error parsing packed entity - " + e.getMessage() + " (" + e.getCauseToken() + ")" );

	    return null;

	} catch ( IOException e ) {

	    Logger.logErr( "I/O error parsing packed entity", e );

	    return null;

	}

    }

    private void collectTypeAlias()
	    throws IOException, UnPacker2ParseError {

	P2ATokenizer.P2AToken typeIdToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.LONG );
	P2ATokenizer.P2AToken atSignToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.AT_SIGN );
	P2ATokenizer.P2AToken typeNameToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.STRING );
//	P2ATokenizer.P2AToken semiColonToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.SEMI_COLON );

	_packingContext.saveTypeAlias( typeIdToken, typeNameToken );

    }

    private PackedEntityBundle collectObjectInstance( boolean parsingSuperClause )
	    throws IOException, UnPacker2ParseError {

	P2ATokenizer.P2AToken typeIdToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.ENTITY_REFERENCE );
	P2ATokenizer.P2AToken equalSign = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.EQUAL_SIGN );
	P2ATokenizer.P2AToken leftParen = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.LEFT_PAREN );

	EntityTypeName2 entityTypeName = _packingContext.findTypeByTypeReferenceId( typeIdToken.typeIdValue() );
	if ( entityTypeName == null ) {

	    throw new UnPacker2ParseError( "unknown type id " + typeIdToken.typeIdValue(), typeIdToken );

	}

	if ( !parsingSuperClause && typeIdToken.entityIdValue() == 0 ) {

	    throw new UnPacker2ParseError( "entity id may only be zero in super clauses", typeIdToken );

	} else if ( parsingSuperClause && typeIdToken.entityIdValue() != 0 ) {

	    throw new UnPacker2ParseError( "entity id must be zero in super clauses", typeIdToken );

	}

	PackedEntityBundle superClause = null;

//	P2ATokenizer.P2AToken leftParen = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.LEFT_PAREN );

	while ( true ) {

	    P2ATokenizer.P2AToken token = _tokenizer.getNextToken( true );
	    switch ( token.type() ) {

		case ENTITY_REFERENCE:

		    // start of the 'super' clause

		    if ( superClause != null ) {

			throw new UnPacker2ParseError( "unexpected 'super' clause", token );

		    }

		    _tokenizer.putBackToken( token );
		    superClause = collectObjectInstance( true );

		    break;

		case IDENTIFIER:

		    // start of a field definition clause
		    _tokenizer.putBackToken( token );
		    collectFieldDefinitionClause();

		    break;

		case RIGHT_PAREN:

		    // end of our field value display clause

		    PackedEntityBundle rval = new PackedEntityBundle(
			    entityTypeName,
			    typeIdToken.entityIdValue(),
			    superClause,
			    getPackingContext() );

		    return rval;

		case COMMA:

		    // end of this field definition, more to come

		    break;

	    }

	}


    }

    private void parseFieldValueDisplayClause()
	    throws IOException, UnPacker2ParseError {

	throw new HowDidWeGetHereError( "unimplemented" );

//	boolean gotSuper = false;
//
//	P2ATokenizer.P2AToken leftParen = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.LEFT_PAREN );
//
//	while ( true ) {
//
//	    P2ATokenizer.P2AToken token = _tokenizer.getNextToken( true );
//	    switch ( token.type() ) {
//
//		case LEFT_PAREN:
//
//		    // start of the 'super' clause
//
//		    if ( gotSuper ) {
//
//			throw new UnPacker2ParseError( "unexpected 'super' clause", token );
//
//		    }
//
//		    _tokenizer.putBackToken( token );
//		    superInitializer = parseFieldValueDisplayClause();
//
//		    break;
//
//		case IDENTIFIER:
//
//		    // start of a field definition clause
//		    _tokenizer.putBackToken( token );
//		    fieldDefinitionClause = getFieldDefinitionClause();
//
//		    break;
//
//		case RIGHT_PAREN:
//
//		    // end of our field value display clause
//
//		    return fieldValueDisplayClause;
//		    break;
//
//		case COMMA:
//
//		    // end of this field definition, more to come
//
//		    break;
//
//	    }
//
//	}

    }

    private void collectFieldDefinitionClause()
	    throws IOException, UnPacker2ParseError {

//	P2ATokenizer.P2AToken equalSize = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.EQUAL_SIGN );
//	valueToken = _tokenizer.getNextToken( false );

    }

    public static void main( String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Packer", "testing", null );

	try {

	    StdUnPacker2a unPacker = new StdUnPacker2a( new TypeIndex2( "test unpacker" ), new File( "test1.p2a" ) );

	    UnpackedEntityGroup result = unPacker.parse();

	    ObtuseUtil.doNothing();

	} catch ( IOException e ) {

	    Logger.logErr( "unable to create StdUnPacker2a instance", e );

	}

    }

    public PackingContext2 getPackingContext() {

	return _packingContext;

    }

}
