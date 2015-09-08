package com.obtuse.util.packers.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.packers.packer2.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 Pack entities using a purely text-based format (no binary data) and explicitly named fields.
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

public class StdPacker2a implements Packer2 {

    private final File _outputFile;

//    private final TypeIndex2 _typeIndex;

    private final PackingContext2 _packingContext;

    private final EntityName2 _groupName;

    private int _depth = 0;

    private final PrintWriter _writer;

    private SortedSet<InstanceId> _previouslyPackedEntities = new TreeSet<InstanceId>();

//    private String _currentSeparator;

    public StdPacker2a( @NotNull EntityName2 groupName, @NotNull TypeIndex2 typeIndex, @NotNull File outputFile )
	    throws FileNotFoundException {
	this( groupName, outputFile, new PrintWriter( outputFile ), new StdPackingContext2( typeIndex ) );

    }

    public StdPacker2a( @NotNull EntityName2 groupName, @NotNull TypeIndex2 typeIndex, @NotNull File outputFile, @NotNull OutputStream outputStream ) {
	this( groupName, outputFile, new PrintWriter( outputStream, true ), new StdPackingContext2( typeIndex ) );

    }

    private StdPacker2a( @NotNull EntityName2 groupName, @NotNull File outputFile, @NotNull PrintWriter writer, @NotNull PackingContext2 packingContext ) {

//	_typeIndex = typeIndex;
	_outputFile = outputFile;
	_groupName = groupName;
	_writer = writer;

//	_writer.print( Constants.LINE_COMMENT_CHAR );
	_writer.print( Constants.TAG_FORMAT_VERSION );
	_writer.print(
		Constants.MAJOR_FORMAT_VERSION * Constants.FORMAT_VERSION_MULTIPLIER +
		Constants.MINOR_FORMAT_VERSION % Constants.FORMAT_VERSION_MULTIPLIER
	);
	_writer.print( ':' );
	_writer.print( ObtuseUtil.enquoteForJavaString( getGroupName().getName() ) );
	_writer.println( ';' );

	_packingContext = packingContext;

    }

    public void close() {

	_writer.close();

    }

    @NotNull
    public PackingContext2 getPackingContext() {

	return _packingContext;

    }

    @Override
    public PackingId2 queuePackEntity( @Nullable Packable2 entity ) {

	if ( entity == null ) {

	    return null;

	}

	PackingId2 packingId = _packingContext.rememberPackableEntity( entity );

	return packingId;

    }

    @Override
    public void actuallyPackEntities() {

	for ( int pass = 1; true; pass += 1 ) {

	    SortedSet<InstanceId> notPackedEntities = new TreeSet<InstanceId>();

	    for ( InstanceId instanceId : _packingContext.getSeenInstanceIds() ) {

		if ( !_previouslyPackedEntities.contains( instanceId ) ) {

		    Logger.logMsg( "will pack " + instanceId + " on next pass" );

		    notPackedEntities.add( instanceId );

		}

	    }

	    if ( notPackedEntities.isEmpty() ) {

		Logger.logMsg( "done packing" );

		break;

	    }

	    Logger.logMsg( "starting packing pass " + pass + " with " + notPackedEntities.size() + " yet to be packed entities" );

	    for ( InstanceId instanceId : notPackedEntities ) {

		Logger.logMsg( "packing " + instanceId );
		actuallyPackEntity( instanceId );
		_previouslyPackedEntities.add( instanceId );

	    }

	}

    }

    private void actuallyPackEntity( @Nullable InstanceId instanceId ) {

	Collection<EntityTypeName2> newTypeNames = _packingContext.getNewTypeNames();
	if ( !newTypeNames.isEmpty() ) {

	    for ( EntityTypeName2 newTypeName : newTypeNames ) {

		int typeReferenceId = _packingContext.getTypeReferenceId( newTypeName );

		_writer.print( typeReferenceId );
		_writer.print( '@' );
		_writer.print( ObtuseUtil.enquoteForJavaString( newTypeName.getTypeName() ) );
		_writer.println( ';' );

	    }

	}

	if ( instanceId == null ) {

	    emitNull();

	    return;

	}

	StdPackingContext2.PackingAssociation pa = _packingContext.findPackingAssociation( instanceId );
	if ( pa == null ) {

	    throw new HowDidWeGetHereError( "something that we know about has no packing association (instance id = " + instanceId + ")" );

	}

	emitEntityReference( pa.getPackingId() );

//	String reference = "" + pa.getPackingId().getTypeReferenceId() + ':' + pa.getPackingId().getEntityId();

//	_writer.print( reference );

	_writer.print( " = " );

	Packable2 entity = pa.getPackable();
	PackedEntityBundle bundle = entity.bundleThyself( pa.getPackingId(), false, this );
	actuallyPackEntityBody( bundle );

	_writer.println( ";" );

//	String indent = ObtuseUtil.replicate( "\t", _depth );
//	packEntityName( indent, entityName, false );
//	InstanceReference instanceReference = new InstanceReference( _typeIndex, entity );
//	if ( _seenInstanceIds.contains( instanceReference ) ) {
//
//	    _depth += 1;
//	    try {
//
//		packInstanceReference( indent, instanceReference );
//
//	    } finally {
//
//		_depth -= 1;
//
//	    }
//
//	} else {
//
//	    _depth += 1;
//	    try {
//
//		_seenInstanceIds.add( instanceReference );
//		packEntityName( indent, entityName, true );
//
//	    } finally {
//
//		_depth -= 1;
//
//	    }
//
//	}
//
//	return instanceReference;

    }

    private void actuallyPackEntityBody( PackedEntityBundle bundle ) {

	_writer.print( "(" );
	String comma = " ";
	if ( bundle.hasSuper() ) {

	    _writer.print( comma );

	    emitEntityReference( bundle.getPackingId() );
	    actuallyPackEntityBody( bundle.getSuperBundle() );

	    comma = ", ";

	}

//	_currentSeparator = " ";
	for ( Packable2ThingHolder2 thing : bundle ) {

	    _writer.print( comma );

	    emitName( thing.getName() );

	    _writer.print( "=" );

	    thing.pack( this );

	    comma = ", ";

//	    _currentSeparator = ", ";

	}
	if ( !bundle.hasSuper() && bundle.isEmpty() ) {

	    _writer.print( ")" );

	} else {

	    _writer.print( " )" );

	}

    }

    @Override
    public void emitName( EntityName2 name ) {

	_writer.print( name.getName() );

    }

    private void emitEntityReference( PackingId2 packingId ) {

	_writer.print( Constants.TAG_ENTITY_REFERENCE );
	_writer.print( packingId.getTypeReferenceId() );
	_writer.print( ':' );
	_writer.print( packingId.getEntityId() );

    }

    @Override
    public void emit( PackingId2 packingId ) {

	if ( packingId == null ) {

	    emitNull();

	} else {

	    emitEntityReference( packingId );

//	    _writer.print( Constants.TAG_ENTITY_REFERENCE );
//	    _writer.print( packingId.getTypeReferenceId() );
//	    _writer.print( ':' );
//	    _writer.print( packingId.getEntityId() );

	}

    }

    @Override
    public void emit( String s ) {

	if ( s == null ) {

	    emitNull();

	} else {

	    _writer.print( ObtuseUtil.enquoteForJavaString( s ) );

	}

    }

    @Override
    public void emit( char c ) {

	_writer.print( Constants.TAG_CHAR );
	_writer.print( c );

    }

    @Override
    public void emit( double d ) {

	_writer.print( Constants.TAG_DOUBLE );
	_writer.print( d );

    }

    @Override
    public void emit( float f ) {

	_writer.print( Constants.TAG_FLOAT );
	_writer.print( f );

    }

    @Override
    public void emit( long l ) {

	_writer.print( Constants.TAG_LONG );
	_writer.print( l );

    }

    @Override
    public void emit( int i ) {

	_writer.print( Constants.TAG_INTEGER );
	_writer.print( i );

    }

    @Override
    public void emit( short s ) {

	_writer.print( Constants.TAG_SHORT );
	_writer.print( s );

    }

    @Override
    public void emit( byte b ) {

	_writer.print( Constants.TAG_BYTE );
	_writer.print( b );

    }

    @Override
    public void emit( boolean b ) {

	_writer.print( Constants.TAG_BOOLEAN );
	_writer.print( b ? 'T' : 'F' );

    }

    @Override
    public void emitNull() {

	_writer.print( Constants.NULL_VALUE );

    }

    @Override
    public void emit( EntityTypeName2 typeName ) {

	_writer.print( typeName.getTypeName() );

    }

//    private void packInstanceReference( String indent, InstanceReference backRef ) {
//
//	_writer.print( indent );
//
//	_writer.print( TAG_ENTITY_REFERENCE );
//
//    }

//    private void packEntityName( String indent, EntityName2 entityName, boolean newLine ) {
//
//	_writer.print( indent );
//	_writer.print( entityName.length() );
//	_writer.print( TAG_ENTITY_DEFINITION );
//	_writer.print( entityName );
//	if ( newLine ) {
//
//	    _writer.println();
//
//	}
//
//    }
//
//    public void packPackingId( PackingId2 id ) {
//
//	String packingIdParams = formatPackingId( id );
//	_writer.print( packingIdParams.length() );
//	_writer.print( TAG_PACKING_ID );
//	_writer.print( packingIdParams );
//
//    }

    @NotNull
    private String formatPackingId( PackingId2 id ) {

//	int entityTypeReference = getEntityTypeReferenceId( id.getEntityTypeName() );
//	String taggedTypeReference = formatTagged( TAG_TYPE_NAME_REFERENCE, Integer.toString( entityTypeReference ) );
//	String rval = taggedTypeReference + ',' + formatTagged( TAG_TYPE_ID, Long.toString( id.getEntityId() ) );
//
//	return rval;

	return "formatPackingId(not implemented)";

    }

    private String formatTagged( char tag, String sValue ) {

	StringBuilder buf = new StringBuilder();
	buf.append( sValue.length() );
	buf.append( tag );
	buf.append( sValue );

	return buf.toString();

    }

//    public int getEntityTypeReferenceId( EntityTypeName2 entityTypeName ) {
//
//	Integer typeReferenceId = _seenTypeNames.get( entityTypeName );
//	String typeReferenceIdString;
//	if ( typeReferenceId == null ) {
//
//	    typeReferenceId = _seenTypeNames.size() + 1;
//	    _seenTypeNames.put( entityTypeName, typeReferenceId );
//	    typeReferenceIdString = Integer.toString( typeReferenceId );
//	    _writer.print( typeReferenceIdString.length() );
//	    _writer.print( TAG_TYPE_NAME );
//	    _writer.print( typeReferenceIdString );
//
////	} else {
////
////	    typeReferenceIdString = Integer.toString( typeReferenceId );
//
//	}
//
//	return typeReferenceId;
//    }

    public File getOutputFile() {

	return _outputFile;

    }

    public static void main( String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Packer2", "test", null );

	try {

	    TypeIndex2 typeIndex = new TypeIndex2( "testing" );
//	    StdPackingContext2 packingContext = new StdPackingContext2( typeIndex );

	    StdPacker2a p2a = new StdPacker2a( new EntityName2( "test group name" ), typeIndex, new File( "test1.p2a" ) );

//	    Packable2ThingHolder2 pInt;
//	    pInt = new IntegerHolder2( new EntityName2( "intValue" ), 42, true );
//	    pInt.pack( p2a, "" );
//
//	    pInt = new ShortHolder2( new EntityName2( "shortValue" ), (short)42, true );
//	    pInt.pack( p2a, ", " );
//
//	    pInt = new BooleanHolder2( new EntityName2( "booleanValue" ), true, true );
//	    pInt.pack( p2a, ", " );
//
//	    pInt = new FloatHolder2( new EntityName2( "floatValue" ), 0.1f, true );
//	    pInt.pack( p2a, ", " );
//
//	    pInt = new DoubleHolder2( new EntityName2( "doubleValue" ), 0.1d, true );
//	    pInt.pack( p2a, ", " );
//
//	    pInt = new StringHolder2( new EntityName2( "stringValue" ), "Hello \"world\"", true );
//	    pInt.pack( p2a, ", " );

	    StdPackingContext2.TestPackableClass test = new StdPackingContext2.TestPackableClass( "hello world", new StdPackingContext2.TestPackableClass( "inner reference", null ) );
	    p2a.queuePackEntity( test );
//	    p2a.actuallyPackEntity( test.getInstanceId() );

	    test = new StdPackingContext2.TestPackableClass( "howdy doody", null );
	    p2a.queuePackEntity( test );

	    p2a.actuallyPackEntities();

//	    p2a.actuallyPackEntity( test.getInstanceId() );

//	    pInt = new InstanceReferenceHolder2(
//		    new EntityName2( "packableValue" ),
//		    typeIndex,
//		    StdPackingContext2.TestPackableClass.FACTORY,
//		    new StdPackingContext2.TestPackableClass( packingContext, "hello world" ),
//		    false
//	    );
//	    pInt.pack( p2a, ",\n" );

	    // Now with null values.

//	    pInt = new IntegerHolder2( new EntityName2( "intValue" ), null, false );
//	    pInt.pack( p2a, ", " );
//
//	    pInt = new ShortHolder2( new EntityName2( "shortValue" ), null, false );
//	    pInt.pack( p2a, ", " );
//
//	    pInt = new BooleanHolder2( new EntityName2( "booleanValue" ), null, false );
//	    pInt.pack( p2a, ", " );
//
//	    pInt = new FloatHolder2( new EntityName2( "floatValue" ), null, false );
//	    pInt.pack( p2a, ", " );
//
//	    pInt = new DoubleHolder2( new EntityName2( "doubleValue" ), null, false );
//	    pInt.pack( p2a, ", " );
//
//	    pInt = new StringHolder2( new EntityName2( "stringValue" ), null, false );
//	    pInt.pack( p2a, ", " );

	    p2a.close();

	    ObtuseUtil.doNothing();

	} catch ( FileNotFoundException e ) {

	    e.printStackTrace();

	}

    }

    public EntityName2 getGroupName() {

	return _groupName;

    }

}
