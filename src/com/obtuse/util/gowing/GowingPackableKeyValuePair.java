package com.obtuse.util.gowing;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.holders.*;
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

@SuppressWarnings("unchecked")
public class GowingPackableKeyValuePair<K, V> extends GowingAbstractPackableEntity {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GowingPackableKeyValuePair.class );
    private static final int VERSION = 1;

    private static final EntityName KEY_FIELD_NAME = new EntityName( "_k" );
    private static final EntityName VALUE_FIELD_NAME = new EntityName( "_v" );

    private K _key;
    private Object _keyReference;
    private V _value;
    private Object _valueReference;

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

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
        public GowingPackable createEntity(
                @NotNull final GowingUnPacker unPacker,
                @NotNull final GowingPackedEntityBundle bundle,
                final GowingEntityReference er
        ) {

            return new GowingPackableKeyValuePair( unPacker, bundle, er );

        }

    };

    public GowingPackableKeyValuePair( final K key, final V value ) {

        super( new GowingNameMarkerThing() );

        _key = key;
        _value = value;

        if ( !isObjectsClassSupported( key ) ) {

            throw new IllegalArgumentException( "GowingPackableKeyValuePair:  key objects of class " + key.getClass() + " are not supported" );

        }
        if ( !isObjectsClassSupported( value ) ) {

            throw new IllegalArgumentException( "GowingPackableKeyValuePair:  value objects of class " + key.getClass() + " are not supported" );

        }

    }

    public GowingPackableKeyValuePair(
            final GowingUnPacker unPacker,
            @NotNull final GowingPackedEntityBundle bundle,
            final GowingEntityReference er
    ) {

        super( unPacker, bundle.getSuperBundle() );

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

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( final boolean isPackingSuper, @NotNull final GowingPacker packer ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                // super.bundleThyself( true, packer ),
                super.bundleRoot( packer ),
                packer.getPackingContext()
        );

        packObj( bundle, KEY_FIELD_NAME, _key, packer, true );
        packObj( bundle, VALUE_FIELD_NAME, _value, packer, true );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) {

        if ( _keyReference instanceof GowingEntityReference && !unPacker.isEntityFinished( (GowingEntityReference)_keyReference ) ) {

            return false;

        }

        if ( _valueReference instanceof GowingEntityReference && !unPacker.isEntityFinished( (GowingEntityReference)_valueReference ) ) {

            return false;

        }

        _key = (K)fetchActualValue( unPacker, _keyReference );
        _value = (V)fetchActualValue( unPacker, _valueReference );

        _keyReference = null;
        _valueReference = null;

        return true;

    }

    /**
     If an object is a {@link GowingEntityReference} then use {@link GowingUnPacker#resolveReference(GowingEntityReference)} to get the entity; otherwise, return the object.
     <p/>This may seem like a rather specialized operation but the use case actually comes up fairly often.

     @param unPacker the unpacker that can resolve {@link GowingEntityReference}s.
     @param value    the object in question.
     @return the object of interest.
     */

    public static Object fetchActualValue( final GowingUnPacker unPacker, final Object value ) {

        if ( value instanceof GowingEntityReference ) {

            return unPacker.resolveReference( (GowingEntityReference)value );

        } else {

            return value;

        }

    }

    private interface HolderFactory {

        GowingAbstractPackableHolder constructHolder( EntityName name, Object obj, GowingPacker packer );

    }

    private static SortedMap<String, HolderFactory> _factories = new TreeMap<>();

    static {

        _factories.put(
                String.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingStringHolder( name, (String)obj, true )
        );

        _factories.put(
                Byte.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingByteHolder( name, (Byte)obj, true )
        );

        _factories.put(
                Short.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingShortHolder( name, (Short)obj, true )
        );

        _factories.put(
                Integer.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingIntegerHolder( name, (Integer)obj, true )
        );

        _factories.put(
                Long.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingLongHolder( name, (Long)obj, true )
        );

        _factories.put(
                Float.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingFloatHolder( name, (Float)obj, true )
        );

        _factories.put(
                Double.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingDoubleHolder( name, (Double)obj, true )
        );

        _factories.put(
                Boolean.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingBooleanHolder( name, (Boolean)obj, true )
        );

        _factories.put(
                EntityName.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingEntityNameHolder( name, (EntityName)obj, true )
        );

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isObjectsClassSupported( @Nullable final Object obj ) {

        if ( obj == null || obj instanceof GowingPackable ) {

            return true;

        } else //noinspection SimplifiableIfStatement
            if ( obj instanceof EntityName ) {

            return true;

        } else {

            return isClassSupported( obj.getClass() );

        }

    }

    public static boolean isClassSupported( @NotNull final Class entityClass ) {

        @SuppressWarnings("UnnecessaryLocalVariable") boolean rval = _factories.containsKey( entityClass.getCanonicalName() );

        return rval;

    }

    public static void packObj( final GowingPackedEntityBundle bundle, final EntityName entityName, final Object obj, final GowingPacker packer ) {

        packObj( bundle, entityName, obj, packer, false );

    }

    public static void packObj(
            final GowingPackedEntityBundle bundle,
            final EntityName entityName,
            final Object obj,
            final GowingPacker packer,
            final boolean classSupportVerified
    ) {

        if ( obj == null ) {

            bundle.addHolder( new GowingNullHolder( entityName ) );

        } else if ( obj instanceof GowingPackable ) {

            bundle.addHolder( new GowingPackableEntityHolder( entityName, (GowingPackable)obj, packer, true ) );

        } else {

            String className = obj.getClass().getCanonicalName();
            HolderFactory factory = _factories.get( className );

            if ( factory == null ) {

                if ( classSupportVerified ) {

                    throw new HowDidWeGetHereError( "GowingPackableKeyValuePair#packObj:  unsupported object class " +
                                                    className +
                                                    " (should have been caught by earlier verification)" );

                } else {

                    throw new IllegalArgumentException( "GowingPackableKeyValuePair#packObj:  unsupported object class " + className );

                }

            }

            bundle.addHolder( factory.constructHolder( entityName, obj, packer ) );

        }

    }

    public String toString() {

        return "GowingPackableKeyValuePair( key=" + _key + ", value = " + _value + " )";

    }

}
