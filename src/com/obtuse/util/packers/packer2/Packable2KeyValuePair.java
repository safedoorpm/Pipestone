package com.obtuse.util.packers.packer2;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.packers.packer2.p2a.EntityReference;
import com.obtuse.util.packers.packer2.p2a.holders.*;
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

public class Packable2KeyValuePair<K,V> extends AbstractPackableEntity2 {

    private static final EntityTypeName2 ENTITY_TYPE_NAME = new EntityTypeName2( Packable2KeyValuePair.class.getCanonicalName() );
    private static final int VERSION = 1;

    private static final EntityName2 KEY_FIELD_NAME = new EntityName2( "_k" );
    private static final EntityName2 VALUE_FIELD_NAME = new EntityName2( "_v" );

    private K _key;
    private Object _keyReference;
    private V _value;
    private Object _valueReference;

    public static final EntityFactory2 FACTORY = new EntityFactory2( ENTITY_TYPE_NAME ) {

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
	public Packable2 createEntity( @NotNull UnPacker2 unPacker, PackedEntityBundle bundle, EntityReference er ) {

	    return new Packable2KeyValuePair( unPacker, bundle, er );

	}

    };

    public Packable2KeyValuePair( K key, V value ) {
	super();

	_key = key;
	_value = value;

	if ( !isObjectsClassSupported( key ) ) {

	    throw new IllegalArgumentException( "Packable2KeyValuePair:  key objects of class " + key.getClass() + " are not supported" );

	}
	if ( !isObjectsClassSupported( value ) ) {

	    throw new IllegalArgumentException( "Packable2KeyValuePair:  value objects of class " + key.getClass() + " are not supported" );

	}

    }

    public Packable2KeyValuePair( UnPacker2 unPacker, PackedEntityBundle bundle, EntityReference er ) {
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
    public PackedEntityBundle bundleThyself( boolean isPackingSuper, Packer2 packer ) {

	PackedEntityBundle bundle = new PackedEntityBundle(
		ENTITY_TYPE_NAME,
		VERSION,
		super.bundleThyself( true, packer ),
		packer.getPackingContext()
	);

	packObj( bundle, KEY_FIELD_NAME, _key, packer, true );
	packObj( bundle, VALUE_FIELD_NAME, _value, packer, true );

	return bundle;

    }

    @Override
    public boolean finishUnpacking( UnPacker2 unPacker ) {

	if ( _keyReference instanceof EntityReference && !unPacker.isEntityFinished( (EntityReference)_keyReference ) ) {

	    return false;

	}

	if ( _valueReference instanceof EntityReference && !unPacker.isEntityFinished( (EntityReference)_valueReference ) ) {

	    return false;

	}

	if ( _keyReference instanceof EntityReference ) {

//	    _key = (K) unPacker.resolveReference( ((EntityReferenceHolder2)_keyReference).EntityTypeReference() );
	    _key = (K) unPacker.resolveReference( (EntityReference)_keyReference );

	} else {

	    _key = (K) _keyReference;

	}

	if ( _valueReference instanceof EntityReference ) {

//	    _value = (V) unPacker.resolveReference( ((EntityReferenceHolder2)_valueReference).EntityTypeReference() );
	    _value = (V) unPacker.resolveReference( (EntityReference)_valueReference );

	} else {

	    _value = (V) _valueReference;

	}

	_keyReference = null;
	_valueReference = null;

	return true;

    }

    private static interface HolderFactory {

	AbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, Packer2 packer );

    }

    private static SortedMap<String,HolderFactory> _factories = new TreeMap<String, HolderFactory>();
    static {

	_factories.put(
		String.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public AbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, Packer2 packer ) {

			return new StringHolder2( name, (String)obj, true );

		    }

		}
	);

	_factories.put(
		Byte.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public AbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, Packer2 packer ) {

			return new ByteHolder2( name, (Byte)obj, true );

		    }

		}
	);

	_factories.put(
		Short.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public AbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, Packer2 packer ) {

			return new ShortHolder2( name, (Short)obj, true );

		    }

		}
	);

	_factories.put(
		Integer.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public IntegerHolder2 constructHolder( EntityName2 name, Object obj, Packer2 packer ) {

			return new IntegerHolder2( name, (Integer)obj, true );

		    }

		}
	);

	_factories.put(
		Long.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public AbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, Packer2 packer ) {

			return new LongHolder2( name, (Long)obj, true );

		    }

		}
	);

	_factories.put(
		Float.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public AbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, Packer2 packer ) {

			return new FloatHolder2( name, (Float)obj, true );

		    }

		}
	);

	_factories.put(
		Double.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public AbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, Packer2 packer ) {

			return new DoubleHolder2( name, (Double)obj, true );

		    }

		}
	);

	_factories.put(
		Boolean.class.getCanonicalName(),
		new HolderFactory() {

		    @Override
		    public AbstractPackableHolder2 constructHolder( EntityName2 name, Object obj, Packer2 packer ) {

			return new BooleanHolder2( name, (Boolean)obj, true );

		    }

		}
	);

    }

    public static boolean isObjectsClassSupported( @Nullable Object obj ) {

	if ( obj == null || obj instanceof Packable2 ) {

	    return true;

	} else {

	    return isClassSupported( obj.getClass() );

	}

    }

    public static boolean isClassSupported( @NotNull Class entityClass ) {

	return _factories.containsKey( entityClass.getCanonicalName() );

    }

    public static void packObj( PackedEntityBundle bundle, EntityName2 entityName, Object obj, Packer2 packer ) {

	packObj( bundle, entityName, obj, packer, false );

    }

    public static void packObj( PackedEntityBundle bundle, EntityName2 entityName, Object obj, Packer2 packer, boolean classSupportVerified ) {

	if ( obj == null ) {

	    bundle.addHolder( new NullHolder2( entityName ) );

	} else if ( obj instanceof Packable2 ) {

	    bundle.addHolder( new PackableEntityHolder2( entityName, (Packable2) obj, packer, true ) );

	} else {

	    String className = obj.getClass().getCanonicalName();
	    HolderFactory factory = _factories.get( className );

	    if ( factory == null ) {

		if ( classSupportVerified ) {

		    throw new HowDidWeGetHereError( "Packable2KeyValuePair#packObj:  unsupported object class " + className + " (should have been caught by earlier verification)" );

		} else {

		    throw new IllegalArgumentException( "Packable2KeyValuePair#packObj:  unsupported object class " + className );

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

	return "Packable2KeyValuePair( key=" + _key + ", value = " + _value + " )";

    }

}
