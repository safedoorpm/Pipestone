package com.obtuse.util.gowing.packer2;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.gowing.packer2.p2a.GowingEntityReference;
import com.obtuse.util.gowing.packer2.p2a.holders.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SortedMap;
import java.util.TreeMap;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry around a key-value pair in a packable format.
 */

public class GowingPackable2KeyValuePair<K,V> extends GowingAbstractPackableEntity2 {

    private static final EntityTypeName2 ENTITY_TYPE_NAME = new EntityTypeName2( GowingPackable2KeyValuePair.class.getCanonicalName() );
    private static final int VERSION = 1;

    private static final EntityName2 KEY_FIELD_NAME = new EntityName2( "_k" );
    private static final EntityName2 VALUE_FIELD_NAME = new EntityName2( "_v" );

    private K _key;
    private Object _keyReference;
    private V _value;
    private Object _valueReference;

    public static final GowingEntityFactory2 FACTORY = new GowingEntityFactory2( ENTITY_TYPE_NAME ) {

	@Override
	public int getOldestSupportedVersion() {

	    return VERSION;
	}

	@Override
	public int getNewestSupportedVersion() {

	    return VERSION;
	}

	@Override
	@NotNull
	public GowingPackable2 createEntity( @NotNull GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {

	    return new GowingPackable2KeyValuePair( unPacker, bundle, er );

	}

    };

    public GowingPackable2KeyValuePair( K key, V value ) {
	super();

	_key = key;
	_value = value;

	if ( !isObjectsClassSupported( key ) ) {

	    throw new IllegalArgumentException( "GowingPackable2KeyValuePair:  key objects of class " + key.getClass() + " are not supported" );

	}
	if ( !isObjectsClassSupported( value ) ) {

	    throw new IllegalArgumentException( "GowingPackable2KeyValuePair:  value objects of class " + key.getClass() + " are not supported" );

	}

    }

    public GowingPackable2KeyValuePair( GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) {
	super();

	Logger.logMsg( "reconstructing KVP " + er );

	_keyReference = bundle.getNotNullField( KEY_FIELD_NAME ).getObjectValue();
	_valueReference = bundle.getNotNullField( VALUE_FIELD_NAME ).getObjectValue();

    }

    public K getKey() {

	return _key;

    }

    public V getValue() {

	return _value;

    }

//    public boolean isFinished( UnPacker2 unPacker ) {
//
//	return unPacker.isEntityFinished(  );
//    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker2 packer ) {

	GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
		ENTITY_TYPE_NAME,
		VERSION,
		// super.bundleThyself( true, packer ),
		null,
		packer.getPackingContext()
	);

	packObj( bundle, KEY_FIELD_NAME, _key, packer, true );
	packObj( bundle, VALUE_FIELD_NAME, _value, packer, true );

	return bundle;

    }

    @Override
    public boolean finishUnpacking( GowingUnPacker2 unPacker ) {

	if ( _keyReference instanceof GowingEntityReference && !unPacker.isEntityFinished( (GowingEntityReference)_keyReference ) ) {

	    return false;

	}

	if ( _valueReference instanceof GowingEntityReference && !unPacker.isEntityFinished( (GowingEntityReference)_valueReference ) ) {

	    return false;

	}

	//noinspection unchecked
	_key = (K)fetchActualValue( unPacker, _keyReference );
	//noinspection unchecked
	_value = (V)fetchActualValue( unPacker, _valueReference );

//	if ( _keyReference instanceof GowingEntityReference ) {
//
////	    _key = (K) unPacker.resolveReference( ((EntityReferenceHolder2)_keyReference).EntityTypeReference() );
//	    _key = (K) unPacker.resolveReference( (GowingEntityReference)_keyReference );
//
//	} else {
//
//	    _key = (K) _keyReference;
//
//	}
//
//	if ( _valueReference instanceof GowingEntityReference ) {
//
////	    _value = (V) unPacker.resolveReference( ((EntityReferenceHolder2)_valueReference).EntityTypeReference() );
//	    _value = (V) unPacker.resolveReference( (GowingEntityReference)_valueReference );
//
//	} else {
//
//	    _value = (V) _valueReference;
//
//	}

	_keyReference = null;
	_valueReference = null;

	return true;

    }

    /**
     If an object is an {@link GowingEntityReference} then use {@link GowingUnPacker2#resolveReference(GowingEntityReference)} to get the entity; otherwise, return the object.
     <p/>This may seem like a rather specialized operation but the use case actually comes up fairly often.
     @param unPacker the unpacker that can resolve {@link GowingEntityReference}s.
     @param value the object in question.
     @return the object of interest.
     */

    public static Object fetchActualValue( GowingUnPacker2 unPacker, Object value ) {

	if ( value instanceof GowingEntityReference ) {

	    return unPacker.resolveReference( (GowingEntityReference) value );

	} else {

	    return value;

	}

    }

    private static interface HolderFactory {

	GowingAbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, GowingPacker2 packer );

    }

    private static SortedMap<String,HolderFactory> _factories = new TreeMap<String, HolderFactory>();
    static {

	_factories.put(
		String.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public GowingAbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, GowingPacker2 packer ) {

			return new GowingStringHolder2( name, (String)obj, true );

		    }

		}
	);

	_factories.put(
		Byte.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public GowingAbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, GowingPacker2 packer ) {

			return new GowingByteHolder2( name, (Byte)obj, true );

		    }

		}
	);

	_factories.put(
		Short.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public GowingAbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, GowingPacker2 packer ) {

			return new GowingShortHolder2( name, (Short)obj, true );

		    }

		}
	);

	_factories.put(
		Integer.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public GowingIntegerHolder2 constructHolder( EntityName2 name, Object obj, GowingPacker2 packer ) {

			return new GowingIntegerHolder2( name, (Integer)obj, true );

		    }

		}
	);

	_factories.put(
		Long.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public GowingAbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, GowingPacker2 packer ) {

			return new GowingLongHolder2( name, (Long)obj, true );

		    }

		}
	);

	_factories.put(
		Float.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public GowingAbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, GowingPacker2 packer ) {

			return new GowingFloatHolder2( name, (Float)obj, true );

		    }

		}
	);

	_factories.put(
		Double.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public GowingAbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, GowingPacker2 packer ) {

			return new GowingDoubleHolder2( name, (Double)obj, true );

		    }

		}
	);

	_factories.put(
		Boolean.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public GowingAbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, GowingPacker2 packer ) {

			return new GowingBooleanHolder2( name, (Boolean)obj, true );

		    }

		}
	);

    }

    public static boolean isObjectsClassSupported( @Nullable Object obj ) {

	if ( obj == null || obj instanceof GowingPackable2 ) {

	    return true;

	} else {

	    return isClassSupported( obj.getClass() );

	}

    }

    public static boolean isClassSupported( @NotNull Class entityClass ) {

	return _factories.containsKey( entityClass.getCanonicalName() );

    }

    public static void packObj( GowingPackedEntityBundle bundle, EntityName2 entityName, Object obj, GowingPacker2 packer ) {

	packObj( bundle, entityName, obj, packer, false );

    }

    public static void packObj( GowingPackedEntityBundle bundle, EntityName2 entityName, Object obj, GowingPacker2 packer, boolean classSupportVerified ) {

	if ( obj == null ) {

	    bundle.addHolder( new GowingNullHolder2( entityName ) );

	} else if ( obj instanceof GowingPackable2 ) {

	    bundle.addHolder( new GowingPackableEntityHolder2( entityName, (GowingPackable2) obj, packer, true ) );

	} else {

	    String className = obj.getClass().getCanonicalName();
	    HolderFactory factory = _factories.get( className );

	    if ( factory == null ) {

		if ( classSupportVerified ) {

		    throw new HowDidWeGetHereError( "GowingPackable2KeyValuePair#packObj:  unsupported object class " + className + " (should have been caught by earlier verification)" );

		} else {

		    throw new IllegalArgumentException( "GowingPackable2KeyValuePair#packObj:  unsupported object class " + className );

		}

	    }

	    bundle.addHolder( factory.constructHolder( entityName, obj, packer ) );

	}

    }

//    private void packObj( PackedEntityBundle bundle, String name, Object obj, Packer2 packer ) {
//
//	EntityName2 entityName = new EntityName2( name );
//	if ( obj == null ) {
//
//	    bundle.addHolder( new NullHolder2( entityName ) );
//
//	} else if ( obj instanceof Packable2 ) {
//
//	    bundle.addHolder( new PackableEntityHolder2( entityName, (Packable2) obj, packer, true ) );
//
//	} else {
//
//	    if ( obj instanceof String ) {
//
//		bundle.addHolder( new StringHolder2( entityName, (String)obj, true ) );
//
//	    } else if ( obj instanceof Byte ) {
//
//		bundle.addHolder( new ByteHolder2( entityName, (Byte) obj, true ) );
//
//	    } else if ( obj instanceof Short ) {
//
//		bundle.addHolder( new ShortHolder2( entityName, (Short) obj, true ) );
//
//	    } else if ( obj instanceof Integer ) {
//
//		bundle.addHolder( new IntegerHolder2( entityName, (Integer) obj, true ) );
//
//	    } else if ( obj instanceof Long ) {
//
//		bundle.addHolder( new LongHolder2( entityName, (Long) obj, true ) );
//
//	    } else if ( obj instanceof Float ) {
//
//		bundle.addHolder( new FloatHolder2( entityName, (Float) obj, true ) );
//
//	    } else if ( obj instanceof Double ) {
//
//		bundle.addHolder( new DoubleHolder2( entityName, (Double) obj, true ) );
//
//	    } else if ( obj instanceof Boolean ) {
//
//		bundle.addHolder( new BooleanHolder2( entityName, (Boolean) obj, true ) );
//
//	    }
//
//	    Class<? extends Object> objClass = obj.getClass();
//	}
//
//    }

    public String toString() {

	return "GowingPackable2KeyValuePair( key=" + _key + ", value = " + _value + " )";

    }

}
