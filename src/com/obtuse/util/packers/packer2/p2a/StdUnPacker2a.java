package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.packers.packer2.*;
import org.jetbrains.annotations.NotNull;
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

    private final UnPackerContext2 _unPackerContext;

    @SuppressWarnings("WeakerAccess")
    public StdUnPacker2a( TypeIndex2 typeIndex, File inputFile )
	    throws IOException {
	this( inputFile, new LineNumberReader( new FileReader( inputFile ) ), new StdUnPackerContext2( typeIndex ) );

    }

    public StdUnPacker2a( TypeIndex2 typeIndex, File inputFile, Reader reader )
	    throws IOException {
	this(
		inputFile,
		reader instanceof LineNumberReader ? (LineNumberReader)reader : new LineNumberReader( reader ),
		new StdUnPackerContext2( typeIndex )
	);

    }

    @SuppressWarnings({ "WeakerAccess", "RedundantThrows" })
    public StdUnPacker2a( File inputFile, LineNumberReader reader, UnPackerContext2 unPackerContext )
	    throws IOException {
	super();

	_unPackerContext = unPackerContext;
	_tokenizer = new P2ATokenizer( unPackerContext, reader );

    }

    private FormatVersion parseVersion()
	    throws IOException, UnPacker2ParsingException {

	P2ATokenizer.P2AToken versionToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.FORMAT_VERSION );
	@SuppressWarnings("UnusedAssignment") P2ATokenizer.P2AToken colon = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.COLON );
	P2ATokenizer.P2AToken groupName = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.STRING );
//	P2ATokenizer.P2AToken semiColon = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.SEMI_COLON );

	return new FormatVersion( versionToken, new EntityName2( groupName.stringValue() ) );

//	} else {
//
//	    throw new UnPacker2ParseError( "expected the format version, found " + versionToken.getDescription() + " instead", versionToken );
//
//	}

    }

    @Override
    @Nullable
    public UnpackedEntityGroup unPack() {

	try {

	    UnpackedEntityGroup group;

	    FormatVersion version = parseVersion();
	    @SuppressWarnings("UnusedAssignment") P2ATokenizer.P2AToken semiColon = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.SEMI_COLON );

	    group = new UnpackedEntityGroup( version );

	    while ( true ) {

		P2ATokenizer.P2AToken token = _tokenizer.getNextToken( false );

		if ( token.type() == P2ATokenizer.TokenType.LONG ) {

		    _tokenizer.putBackToken( token );
		    collectTypeAlias();
		    //noinspection UnusedAssignment
		    semiColon = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.SEMI_COLON );

		} else if ( token.type() == P2ATokenizer.TokenType.ENTITY_REFERENCE ){

		    _tokenizer.putBackToken( token );
		    PackedEntityBundle bundle = collectEntityDefinitionClause( false );
		    @SuppressWarnings("UnusedAssignment") P2ATokenizer.P2AToken semiColonToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.SEMI_COLON );

		    Packable2 entity = constructEntity( token.entityReference(), token, bundle );

		    group.add( entity );

//		    throw new UnPacker2ParseError( "no support for entity definitions yet", token );
//		    definition = parseEntityDefinition();

		} else if ( token.type() == P2ATokenizer.TokenType.EOF ) {

		    Logger.logMsg( "EOF reached" );

		    break;

		} else {

		    throw new UnPacker2ParsingException( "unexpected token " + token, token );

		}

	    }

	    Logger.logMsg( "finishing " + group.getEntities().size() + " entities" );

	    for ( Packable2 entity : group.getEntities() ) {

		entity.finishUnpacking( this );

	    }

	    return group;

	} catch ( UnPacker2ParsingException e ) {

	    Logger.logErr( "error parsing packed entity - " + e.getMessage() + " (" + e.getCauseToken() + ")" );

	    return null;

	} catch ( IOException e ) {

	    Logger.logErr( "I/O error parsing packed entity", e );

	    return null;

	}

    }

    private Packable2 constructEntity( EntityReference er, P2ATokenizer.P2AToken token, PackedEntityBundle bundle )
	    throws UnPacker2ParsingException {

	if ( _unPackerContext.isEntityKnown( er ) ) {

	    throw new UnPacker2ParsingException( "entity with er " + er + " already unpacked during this unpacking session", token );

	}

	EntityTypeInfo2 typeInfo = _unPackerContext.findTypeInfo( er.getTypeId() );
	if ( typeInfo == null ) {

	    throw new UnPacker2ParsingException( "unknown type id " + er.getTypeId() + " (" + _unPackerContext.findTypeByTypeReferenceId( er.getTypeId() ) + ")", token );

	}

	EntityFactory2 factory = typeInfo.getFactory();

	Packable2 entity = factory.createEntity( this, bundle );

	_unPackerContext.rememberPackableEntity( token, er, entity );

	return entity;

    }

    @Override
    public Packable2 resolveReference( @Nullable EntityReference er ) {

	if ( er == null ) {

	    return null;

	}

	return _unPackerContext.recallPackableEntity( er );

    }

    private void collectTypeAlias()
	    throws IOException, UnPacker2ParsingException {

	P2ATokenizer.P2AToken typeIdToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.LONG );
	@SuppressWarnings("UnusedAssignment") P2ATokenizer.P2AToken atSignToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.AT_SIGN );
	P2ATokenizer.P2AToken typeNameToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.STRING );
//	P2ATokenizer.P2AToken semiColonToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.SEMI_COLON );

	_unPackerContext.saveTypeAlias( typeIdToken, typeNameToken );

    }

    @NotNull
    private PackedEntityBundle collectEntityDefinitionClause( boolean parsingSuperClause )
	    throws IOException, UnPacker2ParsingException {

	P2ATokenizer.P2AToken ourEntityReferenceToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.ENTITY_REFERENCE );
	if ( ourEntityReferenceToken.entityReference().getVersion() == null ) {

	    throw new UnPacker2ParsingException( "entity references in entity definitions and super clause definitions must have version ids", ourEntityReferenceToken );

	}

	@SuppressWarnings("UnusedAssignment") P2ATokenizer.P2AToken equalSignToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.EQUAL_SIGN );
	@SuppressWarnings("UnusedAssignment") P2ATokenizer.P2AToken leftParenToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.LEFT_PAREN );

	EntityTypeName2 entityTypeName = _unPackerContext.findTypeByTypeReferenceId( ourEntityReferenceToken.entityReference().getTypeId() );
	if ( entityTypeName == null ) {

	    throw new UnPacker2ParsingException( "unknown type id " + ourEntityReferenceToken.entityReference().getTypeId() + " in LHS of entity definition clause", ourEntityReferenceToken );

	}

	if ( !parsingSuperClause && ourEntityReferenceToken.entityReference().getEntityId() == 0 ) {

	    throw new UnPacker2ParsingException( "entity id may only be zero in super clauses", ourEntityReferenceToken );

	} else if ( parsingSuperClause && ourEntityReferenceToken.entityReference().getEntityId() != 0 ) {

	    throw new UnPacker2ParsingException( "entity id must be zero in super clauses", ourEntityReferenceToken );

	}

//	P2ATokenizer.P2AToken leftParen = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.LEFT_PAREN );

//	@SuppressWarnings("UnusedAssignment") boolean gotFieldDefinition = false;
	PackedEntityBundle bundle = null;

	while ( true ) {

	    P2ATokenizer.P2AToken token = _tokenizer.getNextToken( true );
	    switch ( token.type() ) {

		case ENTITY_REFERENCE:

		    {
			if ( bundle != null ) {

			    throw new UnPacker2ParsingException( "super clause must be the first clause in an entity definition clause", token );

			}

			// start of the 'super' clause

			_tokenizer.putBackToken( token );
			PackedEntityBundle superClause = collectEntityDefinitionClause( true );

			Integer version = ourEntityReferenceToken.entityReference().getVersion();
			if ( version == null ) {

			    throw new HowDidWeGetHereError( "parsing super clause - should be impossible to get here with a null version number" );

			}

			bundle = new PackedEntityBundle(
				entityTypeName,
				ourEntityReferenceToken.entityReference().getTypeId(),
				superClause,
				version,
				_unPackerContext
			);

		    }

		    break;

		case IDENTIFIER:

		    // start of a field definition clause

		    _tokenizer.putBackToken( token );

		    // If this is the first clause then create our PEB.

		    if ( bundle == null ) {

			Integer version = ourEntityReferenceToken.entityReference().getVersion();
			if ( version == null ) {

			    throw new HowDidWeGetHereError( "first field definition clause - should be impossible to get here with a null version number" );

			}

			bundle = new PackedEntityBundle(
				entityTypeName,
				ourEntityReferenceToken.entityReference().getTypeId(),
				null,
				version,
				_unPackerContext
			);

		    }

		    // Collect the field definition and add it to our PEB.

		    collectFieldDefinitionClause( bundle );

		    break;

		case RIGHT_PAREN:

		    // end of our field value display clause

		    if ( bundle == null ) {

			bundle = new PackedEntityBundle(
				entityTypeName,
				ourEntityReferenceToken.entityReference().getTypeId(),
				null,
				-1,
				_unPackerContext
			);

		    }

		    return bundle;

		case COMMA:

		    // end of this field definition, more to come

//		    gotFieldDefinition = true;

		    break;

	    }

	}

    }

//    private void parseFieldValueDisplayClause()
//	    throws IOException, UnPacker2ParsingException {
//
//	throw new HowDidWeGetHereError( "unimplemented" );
//
////	boolean gotSuper = false;
////
////	P2ATokenizer.P2AToken leftParen = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.LEFT_PAREN );
////
////	while ( true ) {
////
////	    P2ATokenizer.P2AToken token = _tokenizer.getNextToken( true );
////	    switch ( token.type() ) {
////
////		case LEFT_PAREN:
////
////		    // start of the 'super' clause
////
////		    if ( gotSuper ) {
////
////			throw new UnPacker2ParseError( "unexpected 'super' clause", token );
////
////		    }
////
////		    _tokenizer.putBackToken( token );
////		    superInitializer = parseFieldValueDisplayClause();
////
////		    break;
////
////		case IDENTIFIER:
////
////		    // start of a field definition clause
////		    _tokenizer.putBackToken( token );
////		    fieldDefinitionClause = getFieldDefinitionClause();
////
////		    break;
////
////		case RIGHT_PAREN:
////
////		    // end of our field value display clause
////
////		    return fieldValueDisplayClause;
////		    break;
////
////		case COMMA:
////
////		    // end of this field definition, more to come
////
////		    break;
////
////	    }
////
////	}
//
//    }

    private void collectFieldDefinitionClause( PackedEntityBundle bundle )
	    throws IOException, UnPacker2ParsingException {

//	P2ATokenizer.P2AToken equalSize = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.EQUAL_SIGN );
//	valueToken = _tokenizer.getNextToken( false );

	P2ATokenizer.P2AToken identifierToken = _tokenizer.getNextToken( true, P2ATokenizer.TokenType.IDENTIFIER );
	@SuppressWarnings("UnusedAssignment") P2ATokenizer.P2AToken equalSignToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.EQUAL_SIGN );
	P2ATokenizer.P2AToken valueToken = _tokenizer.getNextToken( false );

	if ( valueToken.type() == P2ATokenizer.TokenType.ENTITY_REFERENCE && valueToken.entityReference().getVersion() != null ) {

	    throw new UnPacker2ParsingException( "entity reference values must not have version numbers", valueToken );

	}

	Packable2ThingHolder2 holder = valueToken.createHolder( identifierToken.identifierValue(), valueToken, _unPackerContext );

	Logger.logMsg( "got field definition:  " + identifierToken.identifierValue() + " = " + valueToken.getObjectValue() );

	if ( bundle.containsKey( holder.getName() ) ) {

	    throw new UnPacker2ParsingException( "more than one field named \"" + identifierToken.identifierValue() + "\"", identifierToken );

	}

	bundle.put( holder.getName(), holder );

    }

    @Override
    public UnPackerContext2 getUnPackerContext() {

	return _unPackerContext;

    }

    public static void main( String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Packer", "testing", null );

	try {

	    UnPacker2 unPacker = new StdUnPacker2a( new TypeIndex2( "test unpacker" ), new File( "test1.p2a" ) );

	    unPacker.getUnPackerContext().registerFactory( StdPackerContext2.TestPackableClass.FACTORY );
	    unPacker.getUnPackerContext().registerFactory( StdPackerContext2.SimplePackableClass.FACTORY );

	    UnpackedEntityGroup result = unPacker.unPack();

	    if ( result != null ) {

		for ( Packable2 entity : result.getEntities() ) {

		    Logger.logMsg( "got " + entity );

		}

	    }

	    ObtuseUtil.doNothing();

	} catch ( IOException e ) {

	    Logger.logErr( "unable to create StdUnPacker2a instance", e );

	}

    }

}
