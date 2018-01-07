package com.obtuse.util.gowing.p2a;

import com.obtuse.util.*;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.examples.SortedSetExample;
import com.obtuse.util.gowing.p2a.holders.GowingPackableCollection;
import com.obtuse.util.gowing.p2a.holders.GowingPackableMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack entities using a purely text-based format (no binary data) and explicitly named fields.
 <p/>
 %%% this is seriously out of date %%%
 <p/>
 An example entity:
 <blockquote><pre>
 public class Thing {
 int _x;
 Integer _y;
 Thing _next;
 public Thing( int x, Integer y, Thing next ) {
 _x = x;
 _y = y;
 _next = next;
 }
 };
 t1 = new Thing( 1, 2, null );
 t2 = new Thing( 3, null, null );
 t3 = new Thing( 4, 5, t2 );
 t1._next = t2;
 t2._next = t3;
 t3._next = t2;
 </pre>
 </blockquote>
 would yield something like
 <blockquote>
 <pre>
 t1 = E&lt;Thing,1>{ _x=i(1), _y=i{2}, _next=R1{t2} },
 t2=R1{ _x=i(3), _y=i{}, _next=R1{t3} },
 t3=R1{ _x=i(4), _y=i{5}, _next=R1{t2} };
 </pre>
 <ol>Notes:
 <li><code>t1 = </code> indicates that what follows is a description of the value known within this serialization as <code>t1</code>.</li>
 <li><code>E&lt;Thing,1></code> means
 <blockquote>
 What follows is an entity of previously unknown type <code>Thing</code>.
 Future entities of this type will be indicated by a type code value of <code>1</code>.
 </blockquote></li>
 <li>The value in curly braces immediately after <code>E&lt;Thing,1></code> is an <i>entity display</i> which describes the contents of the entity named <code>t1</code>.
 If parentheses had been used instead of curly braces then this value describes a single unnamed inline entity value.
 If curly braces are used then the contents of the entity display may be empty to indicate that there is no such entity (this is how a <code>null</code> reference is indicated).
 If parentheses are used then the contents of the entity display must not be empty.
 If the de-serializer is written in a language like Java which only supports reference variables then the use of parentheses in an entity display indicates that the value is required
 and the use of curly braces indicates that the value might be null.
 If the de-serializer is written in a language like C++ which supports inline class instance definitions as well as dynamically allocated class instance definitions then
 the use of parentheses indicates that the value is defined inline whereas the use of curly braces indicates that the value is dynamically allocated.
 In other words, if the de-serializer is written in a language like C++ then an entity's value given within parentheses is akin to writing
 <blockquote><pre>
 Thing abc = { 1, 2, null };
 </pre></blockquote>
 whereas an entity's value given within curly braces is akin to writing
 <blockquote><pre>
 Thing *abc = new Thing( 1, 2, null );
 </pre></blockquote>
 </li>
 <li>The contents of the curly braces of an entity display may be either the name of an entity described elsewhere or a full-blown display as shown above.</li>
 <li>The contents of the parentheses of an entity display must be a full-blown display as shown above.</li>
 <li><code>_x=i(1)</code> indicates that the value of the <code>_x</code> field of <code>t1</code> is the <code>int</code> value <code>1</code>.</li>
 <li><code>_y=i{2}</code> indicates that the value of the <code>_y</code> field of <code>t1</code> is a reference to the <code>int</code> value <code>2</code>.</li>
 <li><code>_next=R1{t2}</code> indicates that the value of the <code>_next</code> field of <code>t1</code> is a reference to an entity named <code>t2</code> which
 is of previously described type of type code <code>1</code> (i.e. we are describing a <code>Thing</code> which is named <code>t2</code>).</li>
 <li><code>t2=R1{ _x=i(3), _y=i{}, _next=R1{t3} }</code> indicates that the value of <code>t2</code> is a <code>Thing</code> (type code 1) with fields
 <code>_x</code> = <code>int</code> value <code>4</code>, <code>_y</code> = reference to <code>int</code> value <code>5</code>, and <code>_next</code> = <code>Thing</code> named <code>t2</code></li>
 <li>The ending semi-colon indicates that all previously described named entities are to be discarded but that all previously described entity types are to be retained.
 If the semi-colon is replaced with a period then all previously described named entities and described entity types are to be discarded.</li>
 In either case, more serialization information can optionally follow.
 <li>A special field name <code>@super</code> can be used to describe what this entity's super-class constructor should be called with.</li>
 <li>An entity display can appear anywhere that an </li>
 </ol>
 <li>Whitespace may optionally appear anywhere except within string and char constants (including within names).</li>
 </blockquote>
 */

public class StdGowingPacker implements GowingPacker {

    private final File _outputFile;

    private final GowingPackerContext _packingContext;

    private final EntityName _groupName;

    private final PrintWriter _writer;

    private final SortedSet<GowingInstanceId> _previouslyPackedEntities = new TreeSet<>();

    private boolean _finished = false;

    private SortedMap<String,Object> s_usedMetaDataKeywords = new TreeMap<>();

    private boolean _verbose = false;

    @SuppressWarnings("unused")
    public StdGowingPacker( @NotNull final EntityName groupName, @NotNull final File outputFile, final boolean verbose )
            throws FileNotFoundException {

        this( groupName, outputFile, new PrintWriter( outputFile ), new StdGowingPackerContext(), verbose );

    }

    @SuppressWarnings({ "RedundantThrows", "unused" })
    public StdGowingPacker(
            @NotNull final EntityName groupName,
            @NotNull final File outputFile,
            @NotNull final OutputStream outputStream,
            final boolean verbose
    ) throws FileNotFoundException {

        this( groupName, outputFile, new PrintWriter( outputStream, true ), new StdGowingPackerContext(), verbose );

    }

    public StdGowingPacker( @NotNull final EntityName groupName, @NotNull final File outputFile )
            throws FileNotFoundException {

        this( groupName, outputFile, new PrintWriter( outputFile ), new StdGowingPackerContext(), false );

    }

    @SuppressWarnings("RedundantThrows")
    public StdGowingPacker(
            @NotNull final EntityName groupName,
            @NotNull final File outputFile,
            @NotNull final OutputStream outputStream
    ) throws FileNotFoundException {

        this( groupName, outputFile, new PrintWriter( outputStream, true ), new StdGowingPackerContext(), false );

    }

    private StdGowingPacker(
            @NotNull final EntityName groupName,
            @NotNull final File outputFile,
            @NotNull final PrintWriter writer,
            @NotNull final GowingPackerContext packingContext,
            final boolean verbose
    ) {
        super();

        _verbose = verbose;

        _outputFile = outputFile;
        _groupName = groupName;
        _writer = writer;

        _writer.print( GowingConstants.TAG_FORMAT_VERSION );
        _writer.print(
                GowingConstants.MAJOR_FORMAT_VERSION * GowingConstants.FORMAT_VERSION_MULTIPLIER +
                GowingConstants.MINOR_FORMAT_VERSION % GowingConstants.FORMAT_VERSION_MULTIPLIER
        );
        _writer.print( ':' );
        _writer.print( ObtuseUtil.enquoteToJavaString( getGroupName().getName() ) );
        _writer.println( ';' );
        emitMetaData( GowingConstants.METADATA_OUTPUT_FILENAME, outputFile.getAbsolutePath() );
        Date now = new Date();
        emitMetaData( GowingConstants.METADATA_OUTPUT_ITS, DateUtils.formatStandardMs( now ) );
        emitMetaData( GowingConstants.METADATA_OUTPUT_RTS, now.toString() );

        _packingContext = packingContext;

    }

    public void close() {

        _writer.close();

    }

    @NotNull
    public GowingPackerContext getPackingContext() {

        return _packingContext;

    }

    @Override
    public GowingInstanceId queuePackableEntity( @Nullable final GowingPackable entity ) {

        if ( entity == null ) {

            return null;

        }

        if ( _verbose ) Logger.logMsg( "queuing " + entity.getInstanceId() + " / " + entity.getInstanceId().getTypeName() );

        _packingContext.rememberPackableEntity( null, entity );

        return entity.getInstanceId();

    }

    public GowingInstanceId queuePackableEntity( @Nullable final EntityName entityName, @Nullable final GowingPackable entity ) {

        if ( entity == null ) {

            return null;

        }

        if ( _verbose ) Logger.logMsg( "queuing " + entity.getInstanceId() + " / " + entity.getInstanceId().getTypeName() + " == " + entityName );

        _packingContext.rememberPackableEntity( entityName, entity );

        return entity.getInstanceId();

    }

    public int finish() {

        if ( _finished ) {

            throw new IllegalArgumentException( "this packer can only be finished once" );

        }

        _finished = true;
        int entityCount = 0;

        for ( int pass = 1; true; pass += 1 ) {

            SortedSet<GowingInstanceId> notPackedEntities = new TreeSet<>();

            for ( GowingInstanceId instanceId : _packingContext.getSeenInstanceIds() ) {

                if ( !_previouslyPackedEntities.contains( instanceId ) ) {

                    if ( _verbose ) Logger.logMsg( "will pack " + instanceId + " on next pass" );

                    notPackedEntities.add( instanceId );

                }

            }

            if ( notPackedEntities.isEmpty() ) {

                Logger.logMsg( "Gowing is done packing global group " + _groupName + " (it took " + pass + " pass" + ( pass == 1 ? "" : "es" ) + " to pack " + entityCount + " entit" + ( entityCount == 1 ? "y" : "ies" ) + ")" );

                break;

            }

            if ( _verbose ) Logger.logMsg( "starting packing pass " + pass + " with " + notPackedEntities.size() + " yet to be packed entities" );

            for ( GowingInstanceId instanceId : notPackedEntities ) {

                EntityNames names = _packingContext.getEntityNames( instanceId );
                if ( _verbose ) Logger.logMsg( "packing " + instanceId + " / " + names );
                actuallyPackEntity( instanceId );
                entityCount += 1;
                _previouslyPackedEntities.add( instanceId );

            }

        }

        return entityCount;

    }

    /**
     Get the entities which we have packed.
     @return an unmodifiable sorted set of the {@link GowingInstanceId}s
     representing the entities which this instance has packed.
     An empty set is returned if this method is called before {@link #finish()} has been called.
     */

    public SortedSet<GowingInstanceId> getPackedEntities() {

        return Collections.unmodifiableSortedSet( _previouslyPackedEntities );

    }

    private void actuallyPackEntity( @NotNull final GowingInstanceId instanceId ) {

        if ( _verbose ) Logger.logMsg( "@@@ actually packing " + instanceId );
        EntityNames entityNames = _packingContext.getEntityNames( instanceId );
        GowingPackedEntityBundle bundle = entityNames.getEntity().bundleThyself( false, this );

        Collection<Integer> newTypeIds = _packingContext.getNewTypeIds();
        if ( !newTypeIds.isEmpty() ) {

            for ( Integer newTypeId : newTypeIds ) {

                String typeName = GowingInstanceId.lookupTypeName( newTypeId.intValue() );

                if ( _verbose ) Logger.logMsg( "recording class " + typeName );

                _writer.print( newTypeId );
                _writer.print( '@' );
                _writer.print( ObtuseUtil.enquoteToJavaString( typeName ) );
                _writer.println( ';' );

            }

        }

        emitEntityReference( instanceId, bundle.getVersion(), entityNames.getEntityNames() );

        _writer.print( " = " );

        actuallyPackEntityBody( bundle );

        _writer.println( ";" );

    }

    private void actuallyPackEntityBody( @NotNull final GowingPackedEntityBundle bundle ) {

        _writer.print( "(" );
        String comma = " ";

        GowingPackedEntityBundle superBundle = bundle.hasSuperBundle() ? bundle.getSuperBundle() : null;
        if ( superBundle != null ) {

            _writer.print( comma );

            emitEntityReference( superBundle.getTypeId(), 0, superBundle.getVersion(), null );
            _writer.print( "=" );
            actuallyPackEntityBody( superBundle );

            comma = ", ";

        } else {

            ObtuseUtil.doNothing();

        }

        for ( GowingPackableThingHolder thing : bundle.values() ) {

            _writer.print( comma );

            emitName( thing.getName() );

            _writer.print( "=" );

            thing.pack( this );

            comma = ", ";

        }

        _writer.print( " )" );

    }

    @Override
    public void emitName( @NotNull final EntityName name ) {

        _writer.print( name.getName() );

    }

    @SuppressWarnings("unused")
    private void emitEntityReference( final GowingInstanceId instanceId, final int version ) {

        emitEntityReference( instanceId, version, null );

    }

    private void emitEntityReference( final GowingInstanceId instanceId, final int version, @Nullable final Collection<EntityName> entityNames ) {

        int typeId = instanceId.getTypeId();
        long entityId = instanceId.getEntityId();
        emitEntityReference( typeId, entityId, version, entityNames );

    }

    @Override
    public void emitEntityReference( final int typeId, final long entityId ) {

        emitEntityReference( typeId, entityId, null, null );

    }

    private void emitEntityReference( final int typeId, final long entityId, @Nullable final Integer version, @Nullable final Collection<EntityName> entityNames ) {

        _writer.print( GowingConstants.TAG_ENTITY_REFERENCE );
        _writer.print( typeId );
        _writer.print( ':' );

        _writer.print( entityId );
        if ( version != null ) {

            _writer.print( 'v' );
            _writer.print( version );

        }

        if ( entityNames != null && !entityNames.isEmpty() ) {

            _writer.print( GowingConstants.ENTITY_NAME_CLAUSE_MARKER );
            _writer.print( GowingEntityReference.formatNames( entityNames ) );

        }

    }

    @Override
    public void emit( @Nullable final GowingInstanceId instanceId ) {

        if ( instanceId == null ) {

            emitNull();

        } else {

            emitEntityReference( instanceId.getTypeId(), instanceId.getEntityId() );

        }

    }

    @Override
    public void emit( @Nullable final String s ) {

        if ( s == null ) {

            emitNull();

        } else {

            _writer.print( ObtuseUtil.enquoteToJavaString( s ) );

        }

    }

    @Override
    public void emit( final char c ) {

        _writer.print( GowingConstants.TAG_CHAR );
        _writer.print( c );

    }

    @Override
    public void emit( final double d ) {

        _writer.print( GowingConstants.TAG_DOUBLE );
        _writer.print( d );

    }

    @Override
    public void emit( @NotNull final double[] v ) {

        _writer.print( GowingConstants.TAG_PRIMITIVE_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_DOUBLE );
        _writer.print( '[' );

        String comma = "";
        for ( double b : v ) {

            _writer.print( comma );
            _writer.print( b );

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( final Double@NotNull[] v ) {

        _writer.print( GowingConstants.TAG_CONTAINER_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_DOUBLE );
        _writer.print( '[' );

        String comma = "";
        for ( Double b : v ) {

            _writer.print( comma );
            if ( b == null ) {

                _writer.print( GowingConstants.NULL_VALUE );

            } else {

                _writer.print( b );

            }

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( final float f ) {

        _writer.print( GowingConstants.TAG_FLOAT );
        _writer.print( f );

    }

    @Override
    public void emit( @NotNull final float[] v ) {

        _writer.print( GowingConstants.TAG_PRIMITIVE_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_FLOAT );
        _writer.print( '[' );

        String comma = "";
        for ( float b : v ) {

            _writer.print( comma );
            _writer.print( b );

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( @Nullable final Float@NotNull[] v ) {

        _writer.print( GowingConstants.TAG_CONTAINER_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_FLOAT );
        _writer.print( '[' );

        String comma = "";
        for ( Float b : v ) {

            _writer.print( comma );
            if ( b == null ) {

                _writer.print( GowingConstants.NULL_VALUE );

            } else {

                _writer.print( b );

            }

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( final long l ) {

        _writer.print( GowingConstants.TAG_LONG );
        _writer.print( l );

    }

    @Override
    public void emit( @NotNull final long[] v ) {

        _writer.print( GowingConstants.TAG_PRIMITIVE_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_LONG );
        _writer.print( '[' );

        String comma = "";
        for ( long b : v ) {

            _writer.print( comma );
            _writer.print( b );

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( final Long@NotNull[] v ) {

        _writer.print( GowingConstants.TAG_CONTAINER_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_LONG );
        _writer.print( '[' );

        String comma = "";
        for ( Long b : v ) {

            _writer.print( comma );
            if ( b == null ) {

                _writer.print( GowingConstants.NULL_VALUE );

            } else {

                _writer.print( b );

            }

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( final int i ) {

        _writer.print( GowingConstants.TAG_INTEGER );
        _writer.print( i );

    }

    @Override
    public void emit( @NotNull final int[] v ) {

        _writer.print( GowingConstants.TAG_PRIMITIVE_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_INTEGER );
        _writer.print( '[' );

        String comma = "";
        for ( int b : v ) {

            _writer.print( comma );
            _writer.print( b );

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( final Integer@NotNull[] v ) {

        _writer.print( GowingConstants.TAG_CONTAINER_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_INTEGER );
        _writer.print( '[' );

        String comma = "";
        for ( Integer b : v ) {

            _writer.print( comma );
            if ( b == null ) {

                _writer.print( GowingConstants.NULL_VALUE );

            } else {

                _writer.print( b );

            }

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( final short s ) {

        _writer.print( GowingConstants.TAG_SHORT );
        _writer.print( s );

    }

    @Override
    public void emit( @NotNull final short[] v ) {

        _writer.print( GowingConstants.TAG_PRIMITIVE_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_SHORT );
        _writer.print( '[' );

        String comma = "";
        for ( short b : v ) {

            _writer.print( comma );
            _writer.print( b );

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( final Short@NotNull[] v ) {

        _writer.print( GowingConstants.TAG_CONTAINER_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_SHORT );
        _writer.print( '[' );

        String comma = "";
        for ( Short b : v ) {

            _writer.print( comma );
            if ( b == null ) {

                _writer.print( GowingConstants.NULL_VALUE );

            } else {

                _writer.print( b );

            }

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( final byte b ) {

        _writer.print( GowingConstants.TAG_BYTE );
        _writer.print( ObtuseUtil.hexvalue( b ) );

    }

    private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    @Override
    public void emit( @NotNull final byte[] v ) {

        _writer.print( GowingConstants.TAG_PRIMITIVE_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_BYTE );
        _writer.print( '[' );

        for ( byte b : v ) {

            int high = ( b >> 4 ) & 0xf;
            int low = (int)b & 0xf;

            _writer.print( HEX_CHARS[high] );
            _writer.print( HEX_CHARS[low] );

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( @Nullable final Byte@NotNull[] v ) {

        _writer.print( GowingConstants.TAG_CONTAINER_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_BYTE );
        _writer.print( '[' );
        String comma = "";
        for ( Byte b : v ) {

            _writer.print( comma );
            comma = ",";

            if ( b == null ) {

                _writer.print( GowingConstants.NULL_VALUE );

            } else {

                int high = ( b.intValue() >> 4 ) & 0xf;
                int low = b.intValue() & 0xf;

                _writer.print( HEX_CHARS[high] );
                _writer.print( HEX_CHARS[low] );

            }

        }
        _writer.print( ']' );

    }

    @Override
    public void emit( final boolean b ) {

        _writer.print( GowingConstants.TAG_BOOLEAN );
        _writer.print( b ? 'T' : 'F' );

    }

    @Override
    public void emit( final boolean@NotNull[] v ) {

        _writer.print( GowingConstants.TAG_PRIMITIVE_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_BOOLEAN );
        _writer.print( '[' );

        String comma = "";
        for ( boolean b : v ) {

            _writer.print( comma );
            _writer.print( b ? 'T' : 'F' );

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emit( final Boolean@NotNull[] v ) {

        _writer.print( GowingConstants.TAG_CONTAINER_ARRAY );
        _writer.print( v.length );
        _writer.print( GowingConstants.TAG_BOOLEAN );
        _writer.print( '[' );

        String comma = "";
        for ( Boolean b : v ) {

            _writer.print( comma );
            if ( b == null ) {

                _writer.print( GowingConstants.NULL_VALUE );

            } else {

                _writer.print( b.booleanValue() ? 'T' : 'F' );

            }

            comma = ",";

        }

        _writer.print( ']' );

    }

    @Override
    public void emitNull() {

        _writer.print( GowingConstants.NULL_VALUE );

    }

    @Override
    public void emit( final EntityTypeName typeName ) {

        _writer.print( typeName.getTypeName() );

    }

    private static final Pattern s_validMetaDataKeywordPattern = Pattern.compile( "[A-Z_]+" );

    private void checkOutboundMetaDataKeyword( @NotNull final String keyword, @Nullable final Object value ) {

        String errmsg = isValidMetaDataKeyword( keyword );
        if ( errmsg != null ) {

            throw new IllegalArgumentException( "StdGowingPacker.emitMetaData:  " + errmsg );

        }

        if ( s_usedMetaDataKeywords.containsKey( keyword ) ) {

            throw new IllegalArgumentException(
                    "StdGowingPacker.emitMetaData:  " +
                    ( keyword.startsWith( "_" ) ? "reserved " : "" ) +
                    "metadata keyword " +
                    ObtuseUtil.enquoteToJavaString( keyword ) +
                    " already used for " +
                    (
                            value == null
                            ?
                            "null"
                            :
                            (
                                    value.getClass().getCanonicalName() + " value " +
                                    ObtuseUtil.enquoteToJavaString( String.valueOf( s_usedMetaDataKeywords.get( keyword ) ) )
                            )
                    )
            );

        }

        s_usedMetaDataKeywords.put( keyword, value );

    }

    public static String isValidMetaDataKeyword( final @NotNull String keyword ) {

        Matcher m = s_validMetaDataKeywordPattern.matcher( keyword );
        if ( !m.matches() ) {

            return "metadata keyword " +
                   ObtuseUtil.enquoteToJavaString( keyword ) +
                   " is invalid (must be some non-empty combination of uppercase letters and underscores;" +
                   " all keywords starting with an underscore are reserved)";

        }

        if ( keyword.startsWith( "_" ) ) {

            if ( !GowingConstants.RESERVED_KEYWORDS.contains( keyword ) ) {

                return "invalid reserved keyword " + ObtuseUtil.enquoteToJavaString( keyword );

            }

        }

        return null;

    }

    @Override
    public void emitMetaData( @NotNull final String name, @NotNull final String value ) {

        checkOutboundMetaDataKeyword( name, value );

        _writer.println( "" + GowingConstants.LINE_METADATA_CHAR + name + '=' + ObtuseUtil.enquoteToJavaString( value ) + ';' );

    }

    @Override
    public void emitMetaData( @NotNull final String name, final long value ) {

        checkOutboundMetaDataKeyword( name, value );

        _writer.println( "" + GowingConstants.LINE_METADATA_CHAR + name + '=' + Long.toString( value ) + "L;" );

    }

    @Override
    public void emitMetaData( @NotNull final String name, final boolean value ) {

        checkOutboundMetaDataKeyword( name, value );

        _writer.println( "" + GowingConstants.LINE_METADATA_CHAR + name + '=' + ( value ? 'T' : 'F' ) + "B;" );

    }

    @Override
    public void emitMetaData( @NotNull final String name, final double value ) {

        checkOutboundMetaDataKeyword( name, value );

        _writer.println( "" + GowingConstants.LINE_METADATA_CHAR + name + '=' + Double.toString( value ) + "D;" );

    }

    @Override
    public File getOutputFile() {

        return _outputFile;

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "GowingPacker", "test", null );

        try {

            StdGowingPacker p2a = new StdGowingPacker( new EntityName( "test group name" ), new File( "test1.p2a" ) );
            p2a.emitMetaData( GowingConstants.METADATA_NEXT_ID, 12345654321L );
            p2a.emitMetaData( "TEST_DOUBLE", Math.PI );
            p2a.emitMetaData( "TEST_NAN", Double.NaN );
            p2a.emitMetaData( "TEST_PINF", Double.POSITIVE_INFINITY );
            p2a.emitMetaData( "TEST_NINF", Double.NEGATIVE_INFINITY );
            p2a.emitMetaData( "TEST_ZERO", 0.0 );
            p2a.emitMetaData( "TEST_NZERO", -0.0 );
            p2a.emitMetaData( "TEST_STRING", "Testing one two three" );
            p2a.emitMetaData( "TEST_BOOLEAN_TRUE", true );
            p2a.emitMetaData( "TEST_BOOLEAN_FALSE", false );

            StdGowingPackerContext.TestPackableClass test =
                    new StdGowingPackerContext.TestPackableClass(
                            "hello world",
                            new StdGowingPackerContext.TestPackableClass(
                                    "inner reference",
                                    null,
                                    null
                            ),
                            null
                    );
            p2a.queuePackableEntity( test );

            test = new StdGowingPackerContext.TestPackableClass( "howdy doody", null, new StdGowingPackerContext
                    .SimplePackableClass( "grump!" ) );
            p2a.queuePackableEntity( test );

            Map<String, EntityName> testMap = new TreeMap<>();
            testMap.put( "hello", new EntityName( "HELLO" ) );
            testMap.put( "there", new EntityName( "THERE" ) );
            testMap.put( "world", new EntityName( "WORLD" ) );
            GowingPackableMapping testMapMapping = new GowingPackableMapping<>( testMap );
            p2a.queuePackableEntity( new EntityName( "fred" ), testMapMapping );

            GowingPackableCollection<Object> p2Collection = new GowingPackableCollection<>();
            p2Collection.add( "Hello" );
            p2Collection.add( "There" );
            GowingPackableCollection<String> p2C2 = new GowingPackableCollection<>();
            p2C2.addAll( Arrays.asList( "Mercury", "Venus", "Mars", "Jupiter" ) );
            p2Collection.add( p2C2 );
            p2a.queuePackableEntity( new EntityName( "fred" ), p2Collection );
            p2a.queuePackableEntity( new EntityName( "barney" ), p2Collection );
            p2a.queuePackableEntity( new EntityName( "betty" ), p2Collection );
            p2a.queuePackableEntity( new EntityName( "wilma" ), p2Collection );
            p2a.queuePackableEntity( new EntityName( "betty" ), p2Collection );
            SortedSetExample sse = new SortedSetExample( "testSortedSet", null, new String[]{ "alpha", "beta", "gamma" } );
            p2a.queuePackableEntity( sse );
            p2a.queuePackableEntity( ObtuseCalendarDate.parseCalendarDate( "1957-10-04" ) );
            p2a.queuePackableEntity( ObtuseApproximateCalendarDate.parseQuietly( "October 4, 1957" ) );

            p2a.finish();

            p2a.close();

            ObtuseUtil.doNothing();

        } catch ( FileNotFoundException e ) {

            e.printStackTrace();

        }

    }

    @Override
    public EntityName getGroupName() {

        return _groupName;

    }

    public String toString() {

        return "StdGowingPacker( gn=" + _groupName + ", of=" + _outputFile + " )";

    }

}
