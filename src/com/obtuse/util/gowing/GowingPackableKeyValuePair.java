package com.obtuse.util.gowing;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUtil;
import com.obtuse.util.gowing.p2a.holders.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

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

    private boolean _keyAndValueResolved = false;

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
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        ) {

            return new GowingPackableKeyValuePair( unPacker, bundle, er );

        }

    };

    public GowingPackableKeyValuePair( final K key, final V value ) {

        super( new GowingNameMarkerThing() );

        _key = key;
        _value = value;

        if ( value instanceof Optional ) {

            throw new HowDidWeGetHereError( "huh1?" );

        }

        if ( !isObjectsClassSupported( key ) ) {

            throw new IllegalArgumentException( "GowingPackableKeyValuePair:  key objects of class " + key.getClass() + " are not supported" );

        }
        if ( !isObjectsClassSupported( value ) ) {

            throw new IllegalArgumentException( "GowingPackableKeyValuePair:  value objects of class " + value.getClass() + " are not supported" );

        }

    }

    public GowingPackableKeyValuePair(
            final GowingUnPacker unPacker,
            final @NotNull GowingPackedEntityBundle bundle,
            @SuppressWarnings("unused") final GowingEntityReference er
    ) {

        super( unPacker, bundle.getSuperBundle() );

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
    public GowingPackedEntityBundle bundleThyself( final boolean isPackingSuper, final @NotNull GowingPacker packer ) {

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
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        // A bit of careful processing of the value reference.
        // If _valueReference is null then either
        // (1) the value is null and we can just leave _value as null or
        // (2) we have already dealt with the value reference and set _valueReference to null below
        //
        // Consequently, we can use the nullity of _valueReference as a safe gate into the code that
        // actually ensures that _value has the correct value.

        // Our ability to resolve either _key or _value does not depend upon either _keyReference or _valueReference being finished.
        //
        // Once both _key and _value have been resolved, we can declare ourselves finished as soon as our key is finished since
        // a finished _key is all that is needed to rebuild a table of key->value mappings.
        //
        // Lastly, our ability to handle circular references relies on us not needing our _value to be finished before we declare
        // ourselves finished.

        // So . . .

        // Step one is to resolve _key and _value (we only do this on the first call to this method).

        if ( !_keyAndValueResolved ) {

            _key = (K)GowingUtil.fetchActualValue( unPacker, _keyReference );
            if ( _valueReference instanceof Optional ) {

                throw new HowDidWeGetHereError( "huh2?" );

            }
            _value = (V)GowingUtil.fetchActualValue( unPacker, _valueReference );

            _keyAndValueResolved = true;

        }

        // Step two is to decide if we are finished. We decide that entirely by checking if _keyReference is finished.
        // See longer comment above for why this is a necessary check and must be a sufficient check.

        //noinspection RedundantIfStatement
        if ( _keyReference instanceof GowingEntityReference && !unPacker.isEntityFinished( (GowingEntityReference)_keyReference ) ) {

            return false;

        }

        return true;

    }

    private interface HolderFactory {

        GowingAbstractPackableHolder constructHolder( EntityName name, Object obj, GowingPacker packer );

    }

    /**
     The holder factories for the standard Java classes and a few of the built-in Gowing classes.
     <p>This mapping is unmodifiable once created.</p>
     */

    private static final SortedMap<String, HolderFactory> STANDARD_JAVA_CLASS_HOLDER_FACTORIES;

    static {

        SortedMap<String,HolderFactory> factories = new TreeMap<>();
        factories.put(
                String.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingStringHolder( name, (String)obj, true )
        );

        factories.put(
                File.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingFileHolder( name, (File)obj, true )
        );

        factories.put(
                Byte.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingByteHolder( name, (Byte)obj, true )
        );

        factories.put(
                Short.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingShortHolder( name, (Short)obj, true )
        );

        factories.put(
                Integer.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingIntegerHolder( name, (Integer)obj, true )
        );

        factories.put(
                Long.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingLongHolder( name, (Long)obj, true )
        );

        factories.put(
                Float.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingFloatHolder( name, (Float)obj, true )
        );

        factories.put(
                Double.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingDoubleHolder( name, (Double)obj, true )
        );

        factories.put(
                Boolean.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingBooleanHolder( name, (Boolean)obj, true )
        );

        factories.put(
                EntityName.class.getCanonicalName(),
                ( name, obj, packer ) -> new GowingEntityNameHolder( name, (EntityName)obj, true )
        );

        STANDARD_JAVA_CLASS_HOLDER_FACTORIES = Collections.unmodifiableSortedMap( factories );

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isObjectsClassSupported( @Nullable final Object obj ) {

        return isObjectsClassSupported( obj, new HashSet<>() );

    }

    private static boolean isObjectsClassSupported( Object obj, HashSet<Object> whatWeveSeen ) {

        if ( obj instanceof String ) {

            ObtuseUtil.doNothing();

        }

        if ( obj == null || whatWeveSeen.contains( obj ) ) {

            return true;

        }

        if ( obj instanceof File ) {

            ObtuseUtil.doNothing();

        }

        whatWeveSeen.add( obj );

        final boolean rval;

        String className = obj.getClass().getCanonicalName();
        if ( obj instanceof File ) {

            className = File.class.getCanonicalName();

        }
        if ( STANDARD_JAVA_CLASS_HOLDER_FACTORIES.containsKey( className ) ) {

            rval = true;

        } else if ( obj instanceof GowingPackableCollection ) {

            boolean thisCheck = true;
            for ( Object element : (Collection)obj ) {

                if ( !isObjectsClassSupported( element ) ) {

                    thisCheck = false;
                    break;

                }

            }

            rval = thisCheck;

        } else if ( obj instanceof GowingPackable ) {

            rval = true;

        } else if ( obj instanceof EntityName ) {

            rval = true;

        } else {

            if ( obj instanceof File ) {
                ObtuseUtil.doNothing();
            }

            rval = isClassSupported( obj.getClass() );

        }

        return rval;

    }

    private static synchronized boolean isClassSupported( final @NotNull Class entityClass ) {

        boolean rval = STANDARD_JAVA_CLASS_HOLDER_FACTORIES.containsKey( entityClass.getCanonicalName() );

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

            bundle.addPackableEntityHolder( entityName, (GowingPackable)obj, packer, true );

        } else if ( obj instanceof Collection ) {

            bundle.addHolder(
                    new GowingPackableEntityHolder(
                            entityName,
                            new GowingPackableCollection<>(
                                    (Collection)obj
                            ),
                            packer,
                            true
                    )
            );

        } else {

            String className = obj.getClass().getCanonicalName();
            if ( obj instanceof File ) {

                className = File.class.getCanonicalName();

            }

            HolderFactory factory = STANDARD_JAVA_CLASS_HOLDER_FACTORIES.get( className );

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
