package com.obtuse.util.gowing;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseCalendarDate;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.exceptions.ParsingException;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.holders.GowingBooleanHolder;
import com.obtuse.util.gowing.p2a.holders.GowingStringHolder;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A single unit of information associated with a specified {@link GowingPackableName}.
 */

public class GowingPackableAttribute implements GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GowingPackableAttribute.class );

    private static final int VERSION = 1;
    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( GowingPackableAttribute.ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return GowingPackableAttribute.VERSION;

        }

        @Override
        public int getNewestSupportedVersion() {

            return GowingPackableAttribute.VERSION;

        }

        @NotNull
        @Override
        public GowingPackable createEntity(
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        ) {

            return new GowingPackableAttribute( unPacker, bundle );

        }

    };
    private static final EntityName N_NAME = new EntityName( "_n" );
    private static final EntityName VALUE_NAME = new EntityName( "_v" );
    private static final EntityName ATTRIBUTE_TYPE_NAME = new EntityName( "_at" );
    private static final EntityName COMPUTED_NAME = new EntityName( "_c" );
    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    private final GowingPackableType _attributeType;
    private final boolean _computed;
    private final GowingPackableName _name;
    private GowingPackableThingHolder _valueHolder;
    private Object _value;

    public GowingPackableAttribute(
            final GowingPackableName name,
            final Object value,
            final GowingPackableType attributeType,
            final boolean computed
    ) {

        super();

        _name = name;

        if ( value instanceof Number ) {
            if ( value instanceof Double || value instanceof Float ) {

                _value = ( (Number)value ).doubleValue();

            } else {

                _value = ( (Number)value ).longValue();

            }

        } else {

            _value = value;

        }

        _attributeType = attributeType;
        _computed = computed;

    }

    protected GowingPackableAttribute( final GowingUnPacker unPacker, final GowingPackedEntityBundle bundle ) {

        this(
                new GowingPackableName( bundle.MandatoryStringValue( GowingPackableAttribute.N_NAME ) ),
                null,
                GowingPackableType.valueOf( bundle.getNotNullField( GowingPackableAttribute.ATTRIBUTE_TYPE_NAME )
                                                  .StringValue() ),
                bundle.getNotNullField( GowingPackableAttribute.COMPUTED_NAME ).booleanValue()
        );

        _valueHolder = bundle.getNullableField( GowingPackableAttribute.VALUE_NAME );

    }

    private static void doit( final GowingPackableAttribute ba ) {

        switch ( ba.getAttributeType() ) {

            case INTEGRAL:
                Logger.logMsg( "" + ba.getAttributeType().getDescriptiveName() + ":  " + ba.getIntegralValue() );
                break;

            case FLOATING_POINT:
                Logger.logMsg( "" + ba.getAttributeType().getDescriptiveName() + ":  " + ba.getFloatingPointValue() );
                break;

            case APPROXIMATE_DATE:
                Logger.logMsg( "" + ba.getAttributeType().getDescriptiveName() + ":  " + ba.getObjectValue() );
                break;

            case PRECISE_DATE:
                Logger.logMsg( "" + ba.getAttributeType().getDescriptiveName() + ":  " + ba.getPreciseDate() );
                break;

            case BOOLEAN:
                Logger.logMsg( "" + ba.getAttributeType().getDescriptiveName() + ":  " + ba.getBooleanValue() );
                break;

            case STRING:
                Logger.logMsg( "" + ba.getAttributeType().getDescriptiveName() + ":  " + ba.getString() );
                break;

            case PLAIN_TEXT:
                Logger.logMsg( "" + ba.getAttributeType().getDescriptiveName() + ":  " + ba.getString() );
                break;

            case HTML_TEXT:
                Logger.logMsg( "" + ba.getAttributeType().getDescriptiveName() + ":  " + ba.getString() );
                break;

        }

        Logger.logMsg( "ba = " + ba );

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "testing", null );

        GowingPackableAttribute.doit(
                new GowingPackableAttribute(
                        new GowingPackableName( "integral" ),
                        42,
                        GowingPackableType.INTEGRAL,
                        false
                )
        );

        GowingPackableAttribute.doit(
                new GowingPackableAttribute(
                        new GowingPackableName( "floating_point" ),
                        Math.PI,
                        GowingPackableType.FLOATING_POINT,
                        false
                )
        );

        GowingPackableAttribute.doit(
                new GowingPackableAttribute(
                        new GowingPackableName( "string" ),
                        "Hello world",
                        GowingPackableType.INTEGRAL,
                        false
                )
        );

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself( final boolean isPackingSuper, final @NotNull GowingPacker packer ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                GowingPackableAttribute.ENTITY_TYPE_NAME,
                GowingPackableAttribute.VERSION,
                packer.getPackingContext()
        );

        bundle.addHolder( new GowingStringHolder( GowingPackableAttribute.N_NAME, _name.getName(), true ) );
        bundle.addHolder( new GowingStringHolder(
                GowingPackableAttribute.ATTRIBUTE_TYPE_NAME,
                _attributeType.name(),
                true
        ) );
        bundle.addHolder( new GowingBooleanHolder( GowingPackableAttribute.COMPUTED_NAME, _computed, true ) );
        GowingPackableKeyValuePair.packObj( bundle, GowingPackableAttribute.VALUE_NAME, _value, packer );

        return bundle;

    }

    @NotNull
    public final GowingInstanceId getInstanceId() {

        return _instanceId;

    }

    @Override
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        if ( _valueHolder == null ) {

            _value = null;

        } else {

            Object v = _valueHolder.getObjectValue();
            if ( v instanceof GowingEntityReference ) {

                GowingEntityReference er = (GowingEntityReference)v;
                _value = unPacker.resolveReference( er ).orElse( null );

            } else if ( getAttributeType() == GowingPackableType.PRECISE_DATE ) {

                try {

                    _value = new ObtuseCalendarDate( (String)v );

                } catch ( ParsingException e ) {

                    throw new IllegalArgumentException( "GowingPackableAttribute(" +
                                                        getName() +
                                                        ").finishUnpacking:  unable to parse calendar date \"" +
                                                        v +
                                                        "\"" );

                }

            } else {

                _value = v;

            }

        }

        _valueHolder = null;

        return true;

    }

    public boolean isComputed() {

        return _computed;

    }

    public GowingPackableName getName() {

        return _name;

    }

    public Object getObjectValue() {

        return _value;

    }

    public GowingPackableType getAttributeType() {

        return _attributeType;

    }

    public long getIntegralValue() {

        return ( (Long)_value ).longValue();

    }

    public double getFloatingPointValue() {

        return ( (Double)_value ).doubleValue();

    }

    public ObtuseCalendarDate getPreciseDate() {

        return (ObtuseCalendarDate)_value;

    }

    public String getString() {

        return (String)_value;

    }

    public String getHtmlText() {

        return (String)_value;

    }

    public boolean getBooleanValue() {

        return ( (Boolean)_value ).booleanValue();

    }

    public String toString() {

        String strValue;
        Object objectValue = getObjectValue();
        if ( objectValue == null ) {

            strValue = "null";

        } else if ( getAttributeType() == GowingPackableType.STRING ||
                    getAttributeType() == GowingPackableType.PLAIN_TEXT ||
                    getAttributeType() == GowingPackableType.HTML_TEXT ) {

            strValue = ObtuseUtil.enquoteToJavaString( (String)objectValue );

        } else {

            strValue = objectValue.toString();

        }

        return "GowingPackableAttribute( name=\"" +
               getName() +
               "\", value=" +
               strValue +
               ", type=" +
               getAttributeType() +
               ", computed=" +
               isComputed() +
               " )";

    }

}