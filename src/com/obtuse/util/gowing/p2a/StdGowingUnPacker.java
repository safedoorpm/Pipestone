package com.obtuse.util.gowing.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.ObtuseImageFile;
import com.obtuse.ui.layout.linear.LinearFlagName;
import com.obtuse.ui.layout.linear.LinearFlagNameValue;
import com.obtuse.util.ContextualToString;
import com.obtuse.util.*;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.examples.SortedSetExample;
import com.obtuse.util.gowing.p2a.holders.GowingPackableCollection;
import com.obtuse.util.gowing.p2a.holders.GowingPackableMapping;
import com.obtuse.util.kv.ObtuseKeyword;
import com.obtuse.util.kv.ObtuseKeywordValue;
import com.obtuse.util.names.AbsolutePath;
import com.obtuse.util.names.RelativePath;
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

    private final GowingTokenizer _tokenizer;

    private final GowingUnPackerContext _unPackerContext;
    private final File _inputFile;

    @SuppressWarnings({ "OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull" })
    private Optional<GowingUnPackedEntityGroup> _unpackedGroup = null;

    private GowingEntityReference _currentEntityReference = null;

    private boolean _closed = false;

    private boolean _verbose = false;

    private boolean _superVerbose = false;

    private final GowingTrace _t;

    /**
     Create a 'standard' text-oriented unpacker.

     @param typeIndex a table of the known [un]packable entity types.
     Every top-level entity type encountered by this unpacker must have an entry in this type index to allow this unpacker
     to know how to
     instantiate the entity.
     @param inputFile the file being read from.
     <p/>While presumably the norm, it seems unreasonable to assume that this parameter will always be non-null.
     For example, someone might want to unpack the contents of a byte array or some other in-memory object.
     @throws IOException if something bad happens in I/O-land.
     */

    public StdGowingUnPacker( final GowingTypeIndex typeIndex, final @NotNull File inputFile )
            throws IOException {

        this( inputFile, new LineNumberReader( new FileReader( inputFile ) ), new StdGowingUnPackerContext( typeIndex ) );

    }

    /**
     Create a 'standard' text-oriented unpacker.

     @param typeIndex a table of the known [un]packable entity types.
     Every entity type encountered by this unpacker at the top level of the input stream (i.e. as an actual entity to be
     instantiated as opposed to
     a super-type of an entity to be instantiated) must have an entry in this type index to allow this unpacker to know
     how to
     instantiate the entity.
     @param inputFile the file being read from.
     <p/>While presumably the norm, it seems unreasonable to assume that this parameter will always be non-null.
     For example, someone might want to unpack the contents of a byte array or some other in-memory object.
     @param reader    where the data is actually coming from.
     @throws IOException if something bad happens in I/O-land.
     */

    @SuppressWarnings("unused")
    public StdGowingUnPacker(
            final @NotNull GowingTypeIndex typeIndex,
            final @NotNull File inputFile,
            final @NotNull Reader reader
    )
            throws IOException {

        this(
                inputFile,
                reader instanceof LineNumberReader ? (LineNumberReader)reader : new LineNumberReader( reader ),
                new StdGowingUnPackerContext( typeIndex )
        );

    }

    /**
     Create a 'standard' text-oriented unpacker.

     @param inputFile       the file being read from.
     <p/>While presumably the norm, it seems unreasonable to assume that this parameter will always be non-null.
     For example, someone might want to unpack the contents of a byte array or some other in-memory object.
     @param reader          where the data is actually coming from.
     @param unPackerContext the context within which this operation is operating.
     <p/>Mostly a table of known {@link GowingPackable} entities and a {@link GowingTypeIndex} describing how to
     instantiate entities found in the input stream.
     @throws IOException if something bad happens in I/O-land.
     */

    @SuppressWarnings({ "WeakerAccess", "RedundantThrows" })
    public StdGowingUnPacker(
            @Nullable final File inputFile,
            final @NotNull LineNumberReader reader,
            final @NotNull GowingUnPackerContext unPackerContext
    )
            throws IOException {

        super();

        _inputFile = inputFile;
        _unPackerContext = unPackerContext;
        _unPackerContext.setInputFile( inputFile );

        _t = new GowingTrace( this );

        _tokenizer = new StdGowingTokenizer( unPackerContext, reader );

        getUnPackerContext().registerFactory( GowingPackableAttribute.FACTORY );
        getUnPackerContext().registerFactory( GowingPackableCollection.FACTORY );
        getUnPackerContext().registerFactory( GowingPackableKeyValuePair.FACTORY );
        getUnPackerContext().registerFactory( GowingPackableMapping.FACTORY );
        getUnPackerContext().registerFactory( GowingPackableName.FACTORY );
        getUnPackerContext().registerFactory( GowingString.FACTORY );

        // Register a bunch of the GowingPackable classes in Pipestone.
        // The goal is to register all GowingPackable classes in Pipestone but . . ..

        getUnPackerContext().registerFactory( AbsolutePath.FACTORY );
        getUnPackerContext().registerFactory( FormattedImmutableDate.FACTORY );
        getUnPackerContext().registerFactory( ContextualToString.FACTORY );
        getUnPackerContext().registerFactory( LinearFlagName.FACTORY );
        getUnPackerContext().registerFactory( LinearFlagNameValue.FACTORY );
        getUnPackerContext().registerFactory( ObtuseApproximateCalendarDate.FACTORY );
        getUnPackerContext().registerFactory( ObtuseCalendarDate.FACTORY );
        getUnPackerContext().registerFactory( ObtuseCalendarDate.FACTORY );
        getUnPackerContext().registerFactory( ObtuseImageFile.FACTORY );
        getUnPackerContext().registerFactory( ObtuseKeyword.FACTORY );
        getUnPackerContext().registerFactory( ObtuseKeywordValue.FACTORY );
        getUnPackerContext().registerFactory( RelativePath.FACTORY );
        getUnPackerContext().registerFactory( ThreeDimensionalTreeMap.FACTORY );
        getUnPackerContext().registerFactory( TwoDimensionalTreeMap.FACTORY );

    }

    public void close()
            throws IOException {

        _tokenizer.close();
        //noinspection OptionalAssignedToNull
        _unpackedGroup = null;
        _closed = true;

    }

    @Override
    public void setVerbose( final boolean verbose ) {

        _verbose = verbose;
        if ( !verbose ) {

            _superVerbose = false;

        }

    }

    @Override
    public boolean isVerbose() {

        return _verbose;

    }

    @Override
    public void setSuperVerbose( final boolean superVerbose ) {

        _superVerbose = superVerbose;
        if ( superVerbose ) {

            _verbose = true;

        }

    }

    @Override
    public boolean isSuperVerbose() {

        return _superVerbose;

    }

    @NotNull
    private GowingFormatVersion parseVersion()
            throws IOException, GowingUnpackingException {

        StdGowingTokenizer.GowingToken2 versionToken = _tokenizer.getNextToken(
                false,
                StdGowingTokenizer.TokenType.FORMAT_VERSION
        );
        @SuppressWarnings({ "UnusedAssignment", "unused" }) StdGowingTokenizer.GowingToken2 colon =
                _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.COLON );
        StdGowingTokenizer.GowingToken2 groupName = _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.STRING );

        // StdGowingPacker insists that its groupName parameter be non-null so it should be impossible to get a null group name.
        // We accept null group names somewhat gracefully because we are reading from an external file.

        return new GowingFormatVersion(
                versionToken,
                new EntityName(
                        groupName.stringValue() == null ? "<<group name missing (should be impossible)>>" : groupName.stringValue()
                )
        );

    }

    @Override
    public boolean isClosed() {

        return _closed;

    }

    private void checkClosed( final @NotNull String who ) {

        if ( isClosed() ) {

            throw new IllegalArgumentException( "StdGowingUnPacker:  " + who + " - already closed" );

        }

    }

    @Override
    public GowingEntityReference getCurrentEntityReference() {

        return _currentEntityReference;

    }

    @Override
    @NotNull
    public Optional<GowingUnPackedEntityGroup> unPack() throws GowingUnpackingException, IOException {

        long unPackStartTime = System.currentTimeMillis();

        checkClosed( "unPack()" );

        Optional<GowingUnPackedEntityGroup> rval = _unpackedGroup;
        if ( rval != null ) {

            if ( isVerbose() ) _t.verboseTrace( "returning result of previously completed unpack operation" );

            return rval;

        }

        if ( isVerbose() ) _t.verboseTrace( "starting unpack operation" );

        _unpackedGroup = Optional.empty();

        Measure stageMeasure = null;
        try {

            GowingUnPackedEntityGroup group;

            stageMeasure = new Measure( "StdGowingUnPacker - parse version" );

            GowingFormatVersion version = parseVersion();
            if ( isVerbose() ) _t.verboseTrace( "pack file version is " + version.getVersionAsString() );

            @SuppressWarnings({ "UnusedAssignment", "unused" }) StdGowingTokenizer.GowingToken2 semiColon =
                    _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.SEMI_COLON );

            group = new GowingUnPackedEntityGroup( version );

            stageMeasure.done();
            stageMeasure = new Measure( "StdGowingUnPacker - unpacking" );

            while ( true ) {

                Measure unpackOne = new Measure( "StdGowingUnPacker - unpack one" );

                StdGowingTokenizer.GowingToken2 token = _tokenizer.getNextToken( false );

                if ( token.type() == StdGowingTokenizer.TokenType.LONG ) {

                    _tokenizer.putBackToken( token );
                    collectTypeAlias();
                    //noinspection UnusedAssignment
                    semiColon = _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.SEMI_COLON );

                } else if ( token.type() == StdGowingTokenizer.TokenType.ENTITY_REFERENCE ) {

                    _tokenizer.putBackToken( token );
                    GowingPackedEntityBundle bundle = collectEntityDefinitionClause( false );
                    @SuppressWarnings({ "UnusedAssignment", "unused" }) StdGowingTokenizer.GowingToken2 semiColonToken =
                            _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.SEMI_COLON );

                    GowingPackable entity = constructEntity( token.entityReference(), token, bundle );
                    if ( _superVerbose ) {

                        _t.verboseTrace( "extracted ", token.entityReference() );

                    }

                    group.add( token.entityReference().getEntityReferenceNames(), entity );

                } else if ( token.type() == StdGowingTokenizer.TokenType.EOF ) {

                    break;

                } else {

                    throw new GowingUnpackingException( "unexpected token " + token, token );

                }

                unpackOne.done();

            }

            stageMeasure.done();
            stageMeasure = new Measure( "StdGowingUnPacker - finishing" );

            _unPackerContext.clearUnFinishedEntities();
            _unPackerContext.markEntitiesUnfinished( _unPackerContext.getSeenEntityReferences() );

            if ( isVerbose() ) {

                _t.verboseTrace( "^v" );

            }

            for ( int finishingPass = 0; true; finishingPass += 1 ) {

                int finishedCount = 0;

                Collection<GowingEntityReference> unFinishedEntities = _unPackerContext.getUnfinishedEntityReferences();
                if ( unFinishedEntities.isEmpty() ) {

                    break;

                }

                if ( isVerbose() ) {

                    _t.verboseTrace( "starting finishing pass " + finishingPass + " with " + unFinishedEntities.size() + " items still to finish" );
                }

                Measure finishingPassMeasure = new Measure( "StdGowingUnPacker - finishing pass" );

                boolean finishedSomething = false;
                for ( GowingEntityReference er : unFinishedEntities ) {

                    if ( !_unPackerContext.isEntityFinished( er ) ) {

                        Measure finishOne = new Measure( "StdGowingUnPacker - finish one" );
                        try {

                            if ( finishingPass == 4 ) {

                                Logger.logMsg( "ON PASS 4" );

                            }

                            GowingPackable entity = resolveReference( er );
                            _currentEntityReference = er;

                            if ( isVerbose() ) {

                                _t.verboseTrace( "trying to finish ", er );

                            }

                            Measure typeMeasure = new Measure( "StdGowingUnPacker - finish " + entity.getInstanceId().getTypeName() );
                            if ( entity.finishUnpacking( this ) ) {

                                if ( isVerbose() ) {

                                    // The spaces after 'finished' line up this GER reference with the one above.

                                    _t.verboseTrace( "finished", er );

                                }

                                _unPackerContext.markEntityFinished( er );
                                finishedCount += 1;
                                finishedSomething = true;

                            } else {

                                if ( _superVerbose || ( isVerbose() && finishingPass > 0 ) ) {

                                    _t.verboseTrace( "did not finish", er );

                                }

                            }

                            typeMeasure.done();

                        } finally {

                            _currentEntityReference = null;

                        }

                        finishOne.done();

                    }

                }

                long finishingPassDuration = finishingPassMeasure.done();
                if ( isVerbose() || Measure.isGloballyEnabled() ) {

                    _t.verboseTrace(
                            "StdGowingUnPacker:  finishing pass " + finishingPass +
                            " done (" + finishedCount + " items finished; " + ( unFinishedEntities.size() - finishedCount ) + " left to finish) " +
                            DateUtils.formatDuration( finishingPassDuration )
                    );
                    _t.verboseTrace( "^v" );

                }

                if ( !finishedSomething ) {

                    for ( GowingEntityReference er : unFinishedEntities ) {

                        Logger.logMsg( "Gowing.unPack:  unable to finish " + _t.describeEntity( er ) );

                    }

                    throw new GowingUnpackingDeadlockedException(
                            "nothing left that can be finished (" +
                            unFinishedEntities.size() +
                            " unfinished " +
                            ( unFinishedEntities.size() == 1 ? "entity" : "entities" ) +
                            " still unfinished)",
                            null,
                            this
                    );

                }

            }

            _t.verboseTrace(
                    "done unpacking, file-level group is " + ObtuseUtil.enquoteJavaObject( group.getGroupName() ) + "," +
                    " duration " + DateUtils.formatDuration( System.currentTimeMillis() - unPackStartTime )
            );

            rval = Optional.of( group );

        } catch ( GowingUnpackingException e ) {

            Logger.logErr( "error parsing packed entity - " + e.getMessage() + " { " + e.getCauseToken() + " }", e );

            throw e;

        } catch ( IOException e ) {

            Logger.logErr( "I/O error parsing packed entity", e );

            throw e;

        } catch ( RuntimeException e ) {

            Logger.logErr( "Unexpected runtime error parsing packed entity", e );

            throw e;

        } finally {

            if ( stageMeasure != null ) {

                stageMeasure.done();

            }

            _unpackedGroup = rval == null ? Optional.empty() : rval;

        }

        return _unpackedGroup;

    }

    @NotNull
    private GowingPackable constructEntity(
            final @NotNull GowingEntityReference er,
            final @NotNull StdGowingTokenizer.GowingToken2 token,
            final @NotNull GowingPackedEntityBundle bundle
    )
            throws GowingUnpackingException {

        if ( _unPackerContext.isEntityKnown( er ) ) {

            throw new GowingUnpackingException( "entity with er " +
                                                er +
                                                " already unpacked during this unpacking session", token );

        }

        Optional<EntityTypeName> optTypeName = _unPackerContext.findTypeByTypeReferenceId( er.getTypeId() );
        if ( optTypeName.isPresent() ) {

            EntityTypeName typeName = optTypeName.get();

            Optional<EntityTypeInfo> optTypeInfo = _unPackerContext.findTypeInfo( typeName );
            if ( optTypeInfo.isPresent() ) {

                EntityTypeInfo typeInfo = optTypeInfo.get();
                GowingEntityFactory factory = typeInfo.getFactory();

                /*
                Create the entity.
                If something goes wrong, augment the GowingUnpackingException with the current token unless the exception
                already specifies a token.
                In either case, rethrow the exception.
                 */

                GowingPackable entity;
                try {

                    if ( bundle.getVersion() < factory.getOldestSupportedVersion() ) {

                        throw new GowingUnpackingException(
                                "entity " + er + "'s version of " + bundle.getVersion() +
                                " is older than the oldest supported version of " + factory.getOldestSupportedVersion() +
                                " (maybe your factory needs to be more flexible)",
                                token
                        );

                    } else if ( bundle.getVersion() > factory.getNewestSupportedVersion() ) {

                        throw new GowingUnpackingException(
                                "entity " + er + "'s version of " + bundle.getVersion() +
                                " is newer than the newest supported version of " + factory.getNewestSupportedVersion() +
                                " (maybe your factory needs to be more flexible)",
                                token

                        );


                    }

                    entity = factory.createEntity( this, bundle, er );

                } catch ( GowingUnpackingException e ) {

                    if ( e.getCauseToken() == null ) {

                        e.setCauseToken( token );

                    }

                    throw e;

                } catch ( IllegalArgumentException e ) {

                    Logger.logErr( "maybe you should throw a GowingUnpackingException or something like that", e );

                    throw e;

                }

                _unPackerContext.rememberPackableEntity( token, er, entity );

                return entity;

            } else {

                String msg = "no factory for type id " +
                             er.getTypeId() +
                             " (" +
                             typeName +
                             ")";

                throw new GowingUnpackingException(
                        msg,
                        token
                );

            }

        } else {

            throw new GowingUnpackingException(
                    "type id " + er.getTypeId() + " not previously defined in data stream",
                    token
            );

        }

    }

    @Override
    public void registerMetaDataHandler( final @NotNull GowingMetaDataHandler handler ) {

        checkClosed( "registerMetaDataHandler( " + handler + " )" );

        _tokenizer.registerMetaDataHandler( handler );

    }

    @Override
    public GowingPackable resolveReference( @Nullable final GowingEntityReference er ) {

        if ( er == null ) {

            return null;

        }

        return _unPackerContext.recallPackableEntity( er );

    }

    public boolean isEntityFinished( @Nullable final GowingEntityReference er ) {

        if ( _unPackerContext.isEntityFinished( er ) ) {

            if ( _superVerbose ) {

                _t.verboseTrace( "is finished", _currentEntityReference, er );

            } else {

                ObtuseUtil.doNothing();

            }

            return true;

        } else {

            if ( isVerbose() ) {

                _t.verboseTrace( "not finished", _currentEntityReference, er );

            } else {

                ObtuseUtil.doNothing();

            }

            return false;

        }

    }

    public boolean areEntitiesAllFinished( final GowingEntityReference... entityReferences ) { //} @Nullable final GowingEntityReference @NotNull [] entityReferences ) {

        int ix = 0;
        for ( GowingEntityReference er : entityReferences ) {

            if ( !isEntityFinished( er ) ) {

                return false;

            }

            ix += 1;

        }

        return true;

    }

    public boolean areEntitiesAllFinished( final @NotNull Collection<GowingEntityReference> entityReferences ) {

        int ix = 0;
        for ( GowingEntityReference er : entityReferences ) {

            if ( !isEntityFinished( er ) ) {

                return false;

            }

        }

        return true;

    }

    private void collectTypeAlias()
            throws IOException, GowingUnpackingException {

        StdGowingTokenizer.GowingToken2 typeIdToken = _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.LONG );
        @SuppressWarnings({ "UnusedAssignment", "unused" }) StdGowingTokenizer.GowingToken2 atSignToken =
                _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.AT_SIGN );
        StdGowingTokenizer.GowingToken2 typeNameToken = _tokenizer.getNextToken(
                false,
                StdGowingTokenizer.TokenType.STRING
        );

        _unPackerContext.saveTypeAlias( typeIdToken, typeNameToken );

    }

    @NotNull
    private GowingPackedEntityBundle collectEntityDefinitionClause( final boolean parsingSuperClause )
            throws IOException, GowingUnpackingException {

        StdGowingTokenizer.GowingToken2 ourEntityReferenceToken = _tokenizer.getNextToken(
                false,
                StdGowingTokenizer.TokenType.ENTITY_REFERENCE
        );
        if ( ourEntityReferenceToken.entityReference().getVersion() == null ) {

            throw new GowingUnpackingException(
                    "entity references in entity definitions and super clause definitions must have version ids",
                    ourEntityReferenceToken
            );

        }

        @SuppressWarnings({ "UnusedAssignment", "unused" }) StdGowingTokenizer.GowingToken2 equalSignToken =
                _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.EQUAL_SIGN );
        @SuppressWarnings({ "UnusedAssignment", "unused" }) StdGowingTokenizer.GowingToken2 leftParenToken =
                _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.LEFT_PAREN );

        Optional<EntityTypeName> optEntityTypeName =
                _unPackerContext.findTypeByTypeReferenceId( ourEntityReferenceToken.entityReference().getTypeId() );
        if ( optEntityTypeName.isPresent() ) {

            EntityTypeName entityTypeName = optEntityTypeName.get();

            if ( !parsingSuperClause && ourEntityReferenceToken.entityReference().getEntityId() == 0 ) {

                throw new GowingUnpackingException(
                        "entity id may only be zero in super clauses",
                        ourEntityReferenceToken
                );

            } else if ( parsingSuperClause && ourEntityReferenceToken.entityReference().getEntityId() != 0 ) {

                throw new GowingUnpackingException(
                        "entity id must be zero in super clauses",
                        ourEntityReferenceToken
                );

            }

            GowingPackedEntityBundle bundle = null;

            while ( true ) {

                StdGowingTokenizer.GowingToken2 token = _tokenizer.getNextToken( true );
                switch ( token.type() ) {

                    case ENTITY_REFERENCE:

                    {
                        if ( bundle != null ) {

                            throw new GowingUnpackingException(
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
                                        "first field definition clause - should be impossible to get here with a null " +
                                        "version number" );

                            }

                            bundle = new GowingPackedEntityBundle(
                                    entityTypeName,
                                    ourEntityReferenceToken.entityReference().getTypeId(),
                                    version.intValue()
                            );

                        }

                        // Collect the field definition and add it to our PEB.

                        collectFieldDefinitionClause( bundle );

                        break;

                    case RIGHT_PAREN:

                        // end of our field value display clause

                        if ( bundle == null ) {

                            Integer version = ourEntityReferenceToken.entityReference().getVersion();
                            if ( version == null ) {

                                throw new HowDidWeGetHereError(
                                        "empty field display clause - should be impossible to get here with a null " +
                                        "version number" );

                            }                            bundle = new GowingPackedEntityBundle(
                                    entityTypeName,
                                    ourEntityReferenceToken.entityReference().getTypeId(),
                                    version.intValue()
                            );

                        }

                        return bundle;

                    case COMMA:

                        // end of this field definition, more to come

                        break;

                }

            }

        } else {

            throw new GowingUnpackingException( "unknown type id " +
                                                ourEntityReferenceToken.entityReference().getTypeId() +
                                                " in LHS of entity definition clause", ourEntityReferenceToken );

        }

    }

    private void collectFieldDefinitionClause( final GowingPackedEntityBundle bundle )
            throws IOException, GowingUnpackingException {

        StdGowingTokenizer.GowingToken2 identifierToken = _tokenizer.getNextToken(
                true,
                StdGowingTokenizer.TokenType.IDENTIFIER
        );
        @SuppressWarnings({ "UnusedAssignment", "unused" }) StdGowingTokenizer.GowingToken2 equalSignToken =
                _tokenizer.getNextToken( false, StdGowingTokenizer.TokenType.EQUAL_SIGN );
        StdGowingTokenizer.GowingToken2 valueToken = _tokenizer.getNextToken( false );

        if ( valueToken.type() == StdGowingTokenizer.TokenType.ENTITY_REFERENCE &&
             valueToken.entityReference().getVersion() != null ) {

            throw new GowingUnpackingException( "entity reference values must not have version numbers", valueToken );

        }

        GowingPackableThingHolder holder = valueToken.createHolder( identifierToken.identifierValue(), valueToken );

        if ( bundle.containsKey( holder.getName() ) ) {

            throw new GowingUnpackingException( "more than one field named \"" +
                                                identifierToken.identifierValue() +
                                                "\"", identifierToken );

        }

        bundle.put( holder.getName(), holder );

    }

    @Override
    public GowingUnPackerContext getUnPackerContext() {

        return _unPackerContext;

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Packer", "testing", null );

        try (
                GowingUnPacker unPacker = new StdGowingUnPacker(
                        new GowingTypeIndex( "test unpacker" ),
                        new File( "test1.p2a" )
                )
        ) {

            unPacker.registerMetaDataHandler(
                    new TracingGowingMetaDataHandler()
            );

            unPacker.getUnPackerContext().registerFactory( StdGowingPackerContext.TestPackableClass.FACTORY );
            unPacker.getUnPackerContext().registerFactory( StdGowingPackerContext.SimplePackableClass.FACTORY );
            unPacker.getUnPackerContext().registerFactory( SortedSetExample.FACTORY );

            Optional<GowingUnPackedEntityGroup> optResult = unPacker.unPack();

            if ( optResult.isPresent() ) {

                GowingUnPackedEntityGroup result = optResult.get();

                for ( GowingPackable entity : result.getAllEntities() ) {

                    Logger.logMsg( "got " + entity.getClass().getCanonicalName() + " " + entity );

                }

            }

            ObtuseUtil.doNothing();

        } catch ( IOException e ) {

            Logger.logErr( "I/O exception trying to unpack StdGowingUnPacker instance", e );

        } catch ( RuntimeException e ) {

            Logger.logErr( "runtime error trying to unpack StdGowingUnPacker instance", e );

        } catch ( Throwable e ) {

            Logger.logErr( "Throwable caught trying to unpack StdGowingUnPacker instance", e );

        }

    }

    public String toString() {

        return "StdGowingUnPacker( input file = " + _inputFile + " )";

    }

}
