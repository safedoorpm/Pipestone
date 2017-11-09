package com.obtuse.util.gowing.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.examples.SortedSetExample;
import com.obtuse.util.gowing.p2a.holders.GowingPackableCollection;
import com.obtuse.util.gowing.p2a.holders.GowingPackableMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Collection;
import java.util.Optional;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Unpack entities using a purely text-based format (no binary data) and explicitly named fields.
 */

public class StdGowingUnPacker implements GowingUnPacker {

    public static final long OLDEST_MAJOR_VERSION = 1L;
    public static final long NEWEST_MAJOR_VERSION = 1L;
    public static final long OLDEST_MINOR_VERSION = 1L;
    public static final long NEWEST_MINOR_VERSION = 1L;

    private final GowingTokenizer _tokenizer;

    private final GowingUnPackerContext _unPackerContext;
    private final File _inputFile;

    /**
     Create a 'standard' text-oriented unpacker.
     @param typeIndex a table of the known [un]packable entity types.
     Every top-level entity type encountered by this unpacker must have an entry in this type index to allow this unpacker to know how to
     instantiate the entity.
     @param inputFile the file being read from.
     <p/>While presumably the norm, it seems unreasonable to assume that this parameter will always be non-null.
     For example, someone might want to unpack the contents of a byte array or some other in-memory object.
     @throws IOException if something bad happens in I/O-land.
     */

    public StdGowingUnPacker( final GowingTypeIndex typeIndex, @NotNull final File inputFile )
            throws IOException {

        this( inputFile, new LineNumberReader( new FileReader( inputFile ) ), new StdGowingUnPackerContext( typeIndex ) );

    }

    /**
     Create a 'standard' text-oriented unpacker.
     @param typeIndex a table of the known [un]packable entity types.
     Every entity type encountered by this unpacker at the top level of the input stream (i.e. as an actual entity to be instantiated as opposed to
     a super-type of an entity to be instantiated) must have an entry in this type index to allow this unpacker to know how to
     instantiate the entity.
     @param inputFile the file being read from.
     <p/>While presumably the norm, it seems unreasonable to assume that this parameter will always be non-null.
     For example, someone might want to unpack the contents of a byte array or some other in-memory object.
     @param reader where the data is actually coming from.
     @throws IOException if something bad happens in I/O-land.
     */

    public StdGowingUnPacker( final GowingTypeIndex typeIndex, @NotNull final File inputFile, final Reader reader )
            throws IOException {

        this(
                inputFile,
                reader instanceof LineNumberReader ? (LineNumberReader)reader : new LineNumberReader( reader ),
                new StdGowingUnPackerContext( typeIndex )
        );

    }

    /**
     Create a 'standard' text-oriented unpacker.
     @param inputFile the file being read from.
     <p/>While presumably the norm, it seems unreasonable to assume that this parameter will always be non-null.
     For example, someone might want to unpack the contents of a byte array or some other in-memory object.
     @param reader where the data is actually coming from.
     @param unPackerContext the context within which this operation is operating.
     <p/>Mostly a table of known {@link GowingPackable} entities and a {@link GowingTypeIndex} describing how to instantiate entities found in the input stream.
     @throws IOException if something bad happens in I/O-land.
     */

    @SuppressWarnings({ "WeakerAccess", "RedundantThrows" })
    public StdGowingUnPacker( @Nullable final File inputFile, final LineNumberReader reader, @NotNull final GowingUnPackerContext unPackerContext )
            throws IOException {

        super();

        _inputFile = inputFile;
        _unPackerContext = unPackerContext;
        _unPackerContext.setInputFile( inputFile );

        _tokenizer = new StdGowingTokenizer( unPackerContext, reader );

        getUnPackerContext().registerFactory( GowingPackableMapping.FACTORY );
        getUnPackerContext().registerFactory( GowingPackableKeyValuePair.FACTORY );
        getUnPackerContext().registerFactory( GowingPackableCollection.FACTORY );
//	getUnPackerContext().registerFactory( EntityName.getFactory() );
//	getUnPackerContext().registerFactory( EntityTypeName.getFactory() );

    }

    public void close()
            throws IOException {

        _tokenizer.close();

    }

    private GowingFormatVersion parseVersion()
            throws IOException, GowingUnPackerParsingException {

        StdGowingTokenizer.GowingToken2 versionToken = _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.FORMAT_VERSION );
        @SuppressWarnings("UnusedAssignment") StdGowingTokenizer.GowingToken2 colon =
                _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.COLON );
        StdGowingTokenizer.GowingToken2 groupName = _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.STRING );
//	P2ATokenizer.GowingToken2 semiColon = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.SEMI_COLON );

        return new GowingFormatVersion( versionToken, new EntityName( groupName.stringValue() ) );

//	} else {
//
//	    throw new UnPacker2ParseError( "expected the format version, found " + versionToken.getDescription() + " instead", versionToken );
//
//	}

    }

    @Override
    @NotNull
    public Optional<GowingDePackedEntityGroup> unPack() {

        try {

            GowingDePackedEntityGroup group;

            GowingFormatVersion version = parseVersion();
            @SuppressWarnings("UnusedAssignment") StdGowingTokenizer.GowingToken2 semiColon =
                    _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.SEMI_COLON );

            group = new GowingDePackedEntityGroup( version );

            while ( true ) {

                StdGowingTokenizer.GowingToken2 token = _tokenizer.getNextToken( false );

                if ( token.type() == StdGowingTokenizer.TokenType.LONG ) {

                    _tokenizer.putBackToken( token );
                    collectTypeAlias();
                    //noinspection UnusedAssignment
                    semiColon = _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.SEMI_COLON );

                } else if ( token.type() == StdGowingTokenizer.TokenType.ENTITY_REFERENCE ) {

                    _tokenizer.putBackToken( token );
                    GowingPackedEntityBundle bundle = collectEntityDefinitionClause( false );
                    @SuppressWarnings("UnusedAssignment") StdGowingTokenizer.GowingToken2 semiColonToken =
                            _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.SEMI_COLON );

                    GowingPackable entity = constructEntity( token.entityReference(), token, bundle );

                    group.add( token.entityReference().getEntityReferenceNames(), entity );

//		    throw new UnPacker2ParseError( "no support for entity definitions yet", token );
//		    definition = parseEntityDefinition();

                } else if ( token.type() == StdGowingTokenizer.TokenType.EOF ) {

//		    Logger.logMsg( "EOF reached" );

                    break;

                } else {

                    throw new GowingUnPackerParsingException( "unexpected token " + token, token );

                }

            }

//	    Logger.logMsg( "finishing " + group.getAllEntities().size() + " entities" );

            _unPackerContext.clearUnFinishedEntities();
            _unPackerContext.markEntitiesUnfinished( _unPackerContext.getSeenEntityReferences() );

            while ( true ) {

                Collection<GowingEntityReference> unFinishedEntities = _unPackerContext.getUnfinishedEntities();
                if ( unFinishedEntities.isEmpty() ) {

//		    Logger.logMsg( "done finishing" );
                    break;

                }

//		Logger.logMsg( "starting finishing pass" );

                boolean finishedSomething = false;
                for ( GowingEntityReference er : unFinishedEntities ) {

                    if ( !_unPackerContext.isEntityFinished( er ) ) {

                        GowingPackable entity = resolveReference( er );
                        if ( entity.finishUnpacking( this ) ) {

                            _unPackerContext.markEntityFinished( er );
                            finishedSomething = true;
//			    Logger.logMsg( "finished " + er );

                        }

                    }

                }

                if ( !finishedSomething ) {

                    throw new HowDidWeGetHereError( "nothing left that can be finished (" +
                                                    unFinishedEntities.size() +
                                                    " unfinished entit" +
                                                    ( unFinishedEntities.size() == 1 ? "y" : "ies" ) +
                                                    " still unfinished)" );

                }
//		for ( Packable2 entity : group.getAllEntities() ) {
//
//		    entity.finishUnpacking( this );
//
//		}
            }

            return Optional.of( group );

        } catch ( GowingUnPackerParsingException e ) {

            Logger.logErr( "error parsing packed entity - " + e.getMessage() + " (" + e.getCauseToken() + ")", e );

            return Optional.empty();

        } catch ( IOException e ) {

            Logger.logErr( "I/O error parsing packed entity", e );

            return Optional.empty();

        }

    }

    @NotNull
    private GowingPackable constructEntity(
            final GowingEntityReference er,
            final StdGowingTokenizer.GowingToken2 token,
            @NotNull final GowingPackedEntityBundle bundle
    )
            throws GowingUnPackerParsingException {

        if ( _unPackerContext.isEntityKnown( er ) ) {

            throw new GowingUnPackerParsingException( "entity with er " + er + " already unpacked during this unpacking session", token );

        }

        Optional<EntityTypeName> maybeTypeName = _unPackerContext.findTypeByTypeReferenceId( er.getTypeId() );
        if ( maybeTypeName.isPresent() ) {

            EntityTypeName typeName = maybeTypeName.get();

            Optional<EntityTypeInfo> maybeTypeInfo = _unPackerContext.findTypeInfo( typeName );
            if ( maybeTypeInfo.isPresent() ) {

                EntityTypeInfo typeInfo = maybeTypeInfo.get();
                GowingEntityFactory factory = typeInfo.getFactory();

		/*
        Create the entity.
		If something goes wrong, augment the GowingUnPackerParsingException with the current token unless the exception already specifies a token.
		In either case, rethrow the exception.
		 */

                GowingPackable entity;
                try {

                    entity = factory.createEntity( this, bundle, er );

//		    return entity;

                } catch ( GowingUnPackerParsingException e ) {

                    if ( e.getCauseToken() == null ) {

                        e.setCauseToken( token );

                    }

                    throw e;

                }

                _unPackerContext.rememberPackableEntity( token, er, entity );

                return entity;

            } else {

                throw new GowingUnPackerParsingException( "no factory for type id " +
                                                          er.getTypeId() +
                                                          " (" +
                                                          _unPackerContext.findTypeByTypeReferenceId( er.getTypeId() ) +
                                                          ")", token );

            }

        } else {

            throw new GowingUnPackerParsingException( "type id " + er.getTypeId() + " not previously defined in data stream", token );

        }

//	EntityTypeInfo typeInfo = _unPackerContext.findTypeInfo( typeName );
//	if ( typeInfo == null ) {
//
//	    throw new GowingUnPackerParsingException( "no factory for type id " + er.getTypeId() + " (" + _unPackerContext.findTypeByTypeReferenceId( er.getTypeId() ) + ")", token );
//
//	}
//
//	GowingEntityFactory factory = typeInfo.getFactory();
//
//	/*
//	Create the entity.
//	If something goes wrong, augment the GowingUnPackerParsingException with the current token unless the exception already specifies a token.
//	In either case, rethrow the exception.
//	 */
//
//	GowingPackable entity;
//	try {
//
//	    entity = factory.createEntity( this, bundle, er );
//
//	} catch ( GowingUnPackerParsingException e ) {
//
//	    if ( e.getCauseToken() == null ) {
//
//		e.setCauseToken( token );
//
//	    }
//
//	    throw e;
//
//	}
//
//	_unPackerContext.rememberPackableEntity( token, er, entity );
//
//	return entity;

    }

    @Override
    public GowingPackable resolveReference( @Nullable final GowingEntityReference er ) {

        if ( er == null ) {

            return null;

        }

        return _unPackerContext.recallPackableEntity( er );

    }

    public boolean isEntityFinished( final GowingEntityReference er ) {

        return _unPackerContext.isEntityFinished( er );

    }

    private void collectTypeAlias()
            throws IOException, GowingUnPackerParsingException {

        StdGowingTokenizer.GowingToken2 typeIdToken = _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.LONG );
        @SuppressWarnings("UnusedAssignment") StdGowingTokenizer.GowingToken2 atSignToken =
                _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.AT_SIGN );
        StdGowingTokenizer.GowingToken2 typeNameToken = _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.STRING );
//	P2ATokenizer.GowingToken2 semiColonToken = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.SEMI_COLON );

        _unPackerContext.saveTypeAlias( typeIdToken, typeNameToken );

    }

    @NotNull
    private GowingPackedEntityBundle collectEntityDefinitionClause( final boolean parsingSuperClause )
            throws IOException, GowingUnPackerParsingException {

        StdGowingTokenizer.GowingToken2 ourEntityReferenceToken = _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.ENTITY_REFERENCE );
        if ( ourEntityReferenceToken.entityReference().getVersion() == null ) {

            throw new GowingUnPackerParsingException(
                    "entity references in entity definitions and super clause definitions must have version ids",
                    ourEntityReferenceToken
            );

        }

        @SuppressWarnings("UnusedAssignment") StdGowingTokenizer.GowingToken2 equalSignToken =
                _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.EQUAL_SIGN );
        @SuppressWarnings("UnusedAssignment") StdGowingTokenizer.GowingToken2 leftParenToken =
                _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.LEFT_PAREN );

        Optional<EntityTypeName> maybeEntityTypeName =
                _unPackerContext.findTypeByTypeReferenceId( ourEntityReferenceToken.entityReference().getTypeId() );
        if ( maybeEntityTypeName.isPresent() ) {

            EntityTypeName entityTypeName = maybeEntityTypeName.get();

            if ( !parsingSuperClause && ourEntityReferenceToken.entityReference().getEntityId() == 0 ) {

                throw new GowingUnPackerParsingException( "entity id may only be zero in super clauses", ourEntityReferenceToken );

            } else if ( parsingSuperClause && ourEntityReferenceToken.entityReference().getEntityId() != 0 ) {

                throw new GowingUnPackerParsingException( "entity id must be zero in super clauses", ourEntityReferenceToken );

            }

//	P2ATokenizer.GowingToken2 leftParen = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.LEFT_PAREN );

//	@SuppressWarnings("UnusedAssignment") boolean gotFieldDefinition = false;
            GowingPackedEntityBundle bundle = null;

            while ( true ) {

                StdGowingTokenizer.GowingToken2 token = _tokenizer.getNextToken( true );
                switch ( token.type() ) {

                    case ENTITY_REFERENCE:

                    {
                        if ( bundle != null ) {

                            throw new GowingUnPackerParsingException(
                                    "super clause must be the first clause in an entity definition clause",
                                    token
                            );

                        }

                        // start of the 'super' clause

                        _tokenizer.putBackToken( token );
                        GowingPackedEntityBundle superClause = collectEntityDefinitionClause( true );

                        Integer version = ourEntityReferenceToken.entityReference().getVersion();
                        if ( version == null ) {

                            throw new HowDidWeGetHereError(
                                    "parsing super clause - should be impossible to get here with a null version number" );

                        }

                        bundle = new GowingPackedEntityBundle(
                                entityTypeName,
                                ourEntityReferenceToken.entityReference().getTypeId(),
                                superClause,
                                version.intValue(),
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

                                throw new HowDidWeGetHereError(
                                        "first field definition clause - should be impossible to get here with a null version number" );

                            }

                            bundle = new GowingPackedEntityBundle(
                                    entityTypeName,
                                    ourEntityReferenceToken.entityReference().getTypeId(),
                                    null,
                                    version.intValue(),
                                    _unPackerContext
                            );

                        }

                        // Collect the field definition and add it to our PEB.

                        collectFieldDefinitionClause( bundle );

                        break;

                    case RIGHT_PAREN:

                        // end of our field value display clause

                        if ( bundle == null ) {

                            bundle = new GowingPackedEntityBundle(
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

        } else {

            throw new GowingUnPackerParsingException( "unknown type id " +
                                                      ourEntityReferenceToken.entityReference().getTypeId() +
                                                      " in LHS of entity definition clause", ourEntityReferenceToken );

        }

    }

//    private void parseFieldValueDisplayClause()
//	    throws IOException, UnPacker2ParsingException {
//
//	throw new HowDidWeGetHereError( "unimplemented" );
//
////	boolean gotSuper = false;
////
////	P2ATokenizer.GowingToken2 leftParen = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.LEFT_PAREN );
////
////	while ( true ) {
////
////	    P2ATokenizer.GowingToken2 token = _tokenizer.getNextToken( true );
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

    private void collectFieldDefinitionClause( final GowingPackedEntityBundle bundle )
            throws IOException, GowingUnPackerParsingException {

//	P2ATokenizer.GowingToken2 equalSize = _tokenizer.getNextToken( false, P2ATokenizer.TokenType.EQUAL_SIGN );
//	valueToken = _tokenizer.getNextToken( false );

        StdGowingTokenizer.GowingToken2 identifierToken = _tokenizer.getNextToken( true, StdGowingTokenizer.TokenType.IDENTIFIER );
        @SuppressWarnings("UnusedAssignment") StdGowingTokenizer.GowingToken2 equalSignToken =
                _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.EQUAL_SIGN );
        StdGowingTokenizer.GowingToken2 valueToken = _tokenizer.getNextToken( false );

        if ( valueToken.type() == StdGowingTokenizer.TokenType.ENTITY_REFERENCE && valueToken.entityReference().getVersion() != null ) {

            throw new GowingUnPackerParsingException( "entity reference values must not have version numbers", valueToken );

        }

        GowingPackableThingHolder holder = valueToken.createHolder( identifierToken.identifierValue(), valueToken );

//        Logger.maybeLogMsg(
//                () -> "got field definition:  " +
//                      identifierToken.identifierValue() +
//                      " = " +
//                      valueToken.valueToString() +
//                      " (" +
//                      (
//                              valueToken.getObjectValue() == null
//                                      ?
//                                      "<<unknown type>>"
//                                      :
//                                      describeType( valueToken )
//                      ) +
//                      ")"
//        );

        if ( bundle.containsKey( holder.getName() ) ) {

            throw new GowingUnPackerParsingException( "more than one field named \"" + identifierToken.identifierValue() + "\"", identifierToken );

        }

        bundle.put( holder.getName(), holder );

    }

    private String describeType( final StdGowingTokenizer.GowingToken2 valueToken ) {

        if ( valueToken.type() == StdGowingTokenizer.TokenType.PRIMITIVE_ARRAY ) {

            return valueToken.elementType().name().toLowerCase() + "[]";

        } else if ( valueToken.type() == StdGowingTokenizer.TokenType.CONTAINER_ARRAY ) {

            String elementTypeName = valueToken.elementType().name();

            return elementTypeName.charAt( 0 ) +
                   elementTypeName.substring( 1 ).toLowerCase() +
                   "[]";

        } else {

            return valueToken.getObjectValue().getClass().getCanonicalName();

        }

    }

    @Override
    public GowingUnPackerContext getUnPackerContext() {

        return _unPackerContext;

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Packer", "testing", null );

        try {

            GowingUnPacker unPacker = new StdGowingUnPacker( new GowingTypeIndex( "test unpacker" ), new File( "test1.p2a" ) );

            unPacker.getUnPackerContext().registerFactory( StdGowingPackerContext.TestPackableClass.FACTORY );
            unPacker.getUnPackerContext().registerFactory( StdGowingPackerContext.SimplePackableClass.FACTORY );
            unPacker.getUnPackerContext().registerFactory( SortedSetExample.FACTORY );

            Optional<GowingDePackedEntityGroup> maybeResult = unPacker.unPack();

            //noinspection OptionalIsPresent
            if ( maybeResult.isPresent() ) {

                GowingDePackedEntityGroup result = maybeResult.get();

                for ( GowingPackable entity : result.getAllEntities() ) {

                    Logger.logMsg( "got " + entity );

                }

            }

            ObtuseUtil.doNothing();

        } catch ( IOException e ) {

            Logger.logErr( "unable to create StdGowingUnPacker instance", e );

        }

    }

    public String toString() {

        return "StdGowingUnPacker( input file = " + _inputFile + " )";

    }

}
