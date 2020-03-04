package com.obtuse.util.gowing.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPackableThingHolder;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.holders.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;

/**
 Created by danny on 2018/11/09.
 */

public class GowingToken {

    private final StdGowingTokenizer.TokenType _tokenType;

    private final StdGowingTokenizer.TokenType _elementType;

    private final Object _value;

    private final int _lnum;

    private final int _offset;

    @SuppressWarnings("unused") private final Throwable _exception;

    /**
     Encapsulate any non-array token.

     @param tokenType the token's type.
     @param value     the token's value.
     @param lnum      the line that we found it on.
     @param offset    the offset within the line that we found it at.
     */

    public GowingToken(
            final @NotNull StdGowingTokenizer.TokenType tokenType,
            final Object value,
            final int lnum,
            final int offset
    ) {

        this( tokenType, value, lnum, offset, null );
    }

    /**
     Encapsulate any non-array token.

     @param tokenType the token's type.
     @param value     the token's value.
     @param lnum      the line that we found it on.
     @param offset    the offset within the line that we found it at.
     @param exception the exception that led to this token being created
     (intended to be used while debugging Gowing; not actually used within Gowing).
     */

    public GowingToken(
            final @NotNull StdGowingTokenizer.TokenType tokenType,
            final Object value,
            final int lnum,
            final int offset,
            @Nullable Throwable exception
    ) {

        super();

        _tokenType = tokenType;
        _elementType = null;
        _value = value;
        _lnum = lnum;
        _offset = offset;
        _exception = exception;

    }

    /**
     Encapsulate an array token.

     @param tokenType   the token's type.
     @param elementType the array's element type.
     @param value       the token's value.
     @param lnum        the line that we found it on.
     @param offset      the offset within the line that we found it at.
     */

    public GowingToken(
            final @NotNull StdGowingTokenizer.TokenType tokenType,
            final @NotNull StdGowingTokenizer.TokenType elementType,
            final Object value,
            final int lnum,
            final int offset
    ) {

        this( tokenType, elementType, value, lnum, offset, null );

    }

    /**
     Encapsulate an array token.

     @param tokenType   the token's type.
     @param elementType the array's element type.
     @param value       the token's value.
     @param lnum        the line that we found it on.
     @param offset      the offset within the line that we found it at.
     @param exception   the exception that led to this token being created
     (intended to be used while debugging Gowing; not actually used within Gowing).
     */

    public GowingToken(
            final @NotNull StdGowingTokenizer.TokenType tokenType,
            final @NotNull StdGowingTokenizer.TokenType elementType,
            final Object value,
            final int lnum,
            final int offset,
            final Throwable exception
    ) {

        super();

        _tokenType = tokenType;
        _elementType = elementType;
        _value = value;
        _lnum = lnum;
        _offset = offset;
        _exception = exception;

        if ( !elementType.isScalarType() ) {

            throw new HowDidWeGetHereError( "attempt to create an array token with a non-scalar element type " +
                                            elementType );

        }

    }

    /**
     Encapsulate an error.

     @param errmsg description of the problem.
     @param lnum   the line that we found it on.
     @param offset the offset within the line that we found it at.
     */

    public GowingToken(
            final String errmsg,
            final int lnum,
            final int offset
    ) {

        this( errmsg, lnum, offset, null );

    }

    /**
     Encapsulate an error.

     @param errmsg    description of the problem.
     @param lnum      the line that we found it on.
     @param offset    the offset within the line that we found it at.
     @param exception the exception that led to this token being created
     (intended to be used while debugging Gowing; not actually used within Gowing).
     */

    public GowingToken(
            final String errmsg,
            final int lnum,
            final int offset,
            final Throwable exception
    ) {

        _tokenType = StdGowingTokenizer.TokenType.ERROR;
        _elementType = null;
        _value = errmsg;
        _lnum = lnum;
        _offset = offset;
        _exception = exception;

    }

    public StdGowingTokenizer.TokenType type() {

        return _tokenType;

    }

    public StdGowingTokenizer.TokenType elementType() {

        return _elementType == null ? StdGowingTokenizer.TokenType.ERROR : _elementType;

    }

    public int getLnum() {

        return _lnum;

    }

    public int getOffset() {

        return _offset;

    }

    public boolean isError() {

        return _tokenType == StdGowingTokenizer.TokenType.ERROR;

    }

    public boolean isNull() {

        return _tokenType == StdGowingTokenizer.TokenType.NULL;

    }

    public boolean booleanValue() {

        return ( (Boolean)_value ).booleanValue();

    }

    public byte byteValue() {

        return ( (Number)_value ).byteValue();

    }

    public short shortValue() {

        return ( (Number)_value ).shortValue();

    }

    public int intValue() {

        return ( (Number)_value ).intValue();

    }

    public long longValue() {

        return ( (Number)_value ).longValue();

    }

    public double doubleValue() {

        return ( (Number)_value ).doubleValue();

    }

    public float floatValue() {

        return ( (Number)_value ).floatValue();

    }

    @SuppressWarnings("unused")
    public File fileValue() {

        return new File( (String)_value );

    }

    public GowingEntityReference entityReference() {

        return (GowingEntityReference)_value;

    }

    public String stringValue() {

        return (String)_value;

    }

    public Object getObjectValue() {

        return _value;

    }

    public EntityName identifierValue() {

        return new EntityName( (String)_value );

    }

    @SuppressWarnings("unused")
    public String valueToString() {

        Object value = getObjectValue();

        if ( value == null ) {

            return "null";

        }

        if ( type() == StdGowingTokenizer.TokenType.CONTAINER_ARRAY ) {

            return Arrays.toString( (Object[])value );

        } else if ( type() == StdGowingTokenizer.TokenType.PRIMITIVE_ARRAY ) {

            switch ( elementType() ) {

                case BOOLEAN:

                    return Arrays.toString( (boolean[])value );

                case BYTE:
                    return Arrays.toString( (byte[])value );

                case SHORT:
                    return Arrays.toString( (short[])value );

                case INTEGER:
                    return Arrays.toString( (int[])value );

                case LONG:
                    return Arrays.toString( (long[])value );

                case FLOAT:
                    return Arrays.toString( (float[])value );

                case DOUBLE:
                    return Arrays.toString( (double[])value );

                case STRING:
                    return Arrays.toString( (String[])value );

                default:
                    throw new HowDidWeGetHereError( "array token has element type " + elementType() );

            }

        } else {

            return value.toString();

        }

    }

    public String toString() {

        return "GowingToken( " + _tokenType + ", \"" + _value + "\", lnum=" + _lnum + ", offset=" + _offset + " )";

    }

    public String getDescription() {

        @SuppressWarnings("StringBufferReplaceableByString") StringBuilder rval = new StringBuilder();

        rval.append( StdGowingTokenizer.cleanupTokenType( _tokenType ) );
        rval.append( " (" );
        rval.append( _value.toString() );
        rval.append( ") @ line " )
            .append( _lnum )
            .append( ", offset " )
            .append( _offset );

        return rval.toString();

    }

    @SuppressWarnings("RedundantThrows")
    @NotNull
    public GowingPackableThingHolder createHolder( final EntityName entityName, final GowingToken valueToken )
            throws GowingUnpackingException {

        GowingPackableThingHolder holder;
        StdGowingTokenizer.TokenType tokenType = valueToken.type();
        StdGowingTokenizer.TokenType elementType = valueToken.elementType();
        switch ( tokenType ) {

            case NULL:
                holder = new GowingNullHolder( entityName );
                break;

            case ERROR:
                throw new IllegalArgumentException( "caught an error (this is a good place for a breakpoint)" );

            case BOOLEAN:
                holder = new GowingBooleanHolder( entityName, valueToken.booleanValue(), true );
                break;

            case BYTE:
                holder = new GowingByteHolder( entityName, valueToken.byteValue(), true );
                break;

            case SHORT:
                holder = new GowingShortHolder( entityName, valueToken.shortValue(), true );
                break;

            case INTEGER:
                holder = new GowingIntegerHolder( entityName, valueToken.intValue(), true );
                break;

            case LONG:
                holder = new GowingLongHolder( entityName, valueToken.longValue(), true );
                break;

            case FLOAT:
                holder = new GowingFloatHolder( entityName, valueToken.floatValue(), true );
                break;

            case DOUBLE:
                holder = new GowingDoubleHolder( entityName, valueToken.doubleValue(), true );
                break;

            case STRING:
                holder = new GowingStringHolder( entityName, valueToken.stringValue(), true );
                break;

            case FILE:
                //noinspection DuplicateBranchesInSwitch
                holder = new GowingStringHolder( entityName, valueToken.stringValue(), true );
                break;

            case PRIMITIVE_ARRAY:
                if ( elementType.isScalarType() ) {

                    switch ( elementType ) {

                        case BOOLEAN:
                            holder = new GowingBooleanHolder(
                                    entityName,
                                    (boolean[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        case BYTE:
                            holder = new GowingByteHolder( entityName, (byte[])valueToken.getObjectValue(), true );
                            break;

                        case SHORT:
                            holder = new GowingShortHolder(
                                    entityName,
                                    (short[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        case INTEGER:
                            holder = new GowingIntegerHolder(
                                    entityName,
                                    (int[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        case LONG:
                            holder = new GowingLongHolder( entityName, (long[])valueToken.getObjectValue(), true );
                            break;

                        case FLOAT:
                            holder = new GowingFloatHolder(
                                    entityName,
                                    (float[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        case DOUBLE:
                            holder = new GowingDoubleHolder(
                                    entityName,
                                    (double[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        default:
                            throw new HowDidWeGetHereError( "unsupported primitive array type " + elementType );

                    }

                } else {

                    throw new HowDidWeGetHereError(
                            "attempt to create a primitive array holder with an invalid element type " +
                            elementType );

                }

                break;

            case CONTAINER_ARRAY:
                if ( elementType.isScalarType() ) {

                    switch ( elementType ) {

                        case STRING:
                            holder = new GowingStringHolder(
                                    entityName,
                                    (String[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        case BOOLEAN:
                            holder = new GowingBooleanHolder(
                                    entityName,
                                    (Boolean[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        case BYTE:
                            holder = new GowingByteHolder( entityName, (Byte[])valueToken.getObjectValue(), true );
                            break;

                        case SHORT:
                            holder = new GowingShortHolder(
                                    entityName,
                                    (Short[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        case INTEGER:
                            holder = new GowingIntegerHolder(
                                    entityName,
                                    (Integer[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        case LONG:
                            holder = new GowingLongHolder( entityName, (Long[])valueToken.getObjectValue(), true );
                            break;

                        case FLOAT:
                            holder = new GowingFloatHolder(
                                    entityName,
                                    (Float[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        case DOUBLE:
                            holder = new GowingDoubleHolder(
                                    entityName,
                                    (Double[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        case FILE:
                            holder = new GowingFileHolder(
                                    entityName,
                                    (String[])valueToken.getObjectValue(),
                                    true
                            );
                            break;

                        default:
                            throw new HowDidWeGetHereError( "unsupported container array type " + elementType );

                    }

                } else {

                    throw new HowDidWeGetHereError(
                            "attempt to create a primitive array holder with an invalid element type " +
                            elementType );

                }

                break;

            case ENTITY_NAME:

                holder = new GowingEntityNameHolder( entityName, new EntityName( valueToken.stringValue() ), true );
                break;

            case ENTITY_REFERENCE:

                holder = new GowingEntityReferenceHolder( entityName, valueToken.entityReference(), true );
                break;

            default:

                throw new HowDidWeGetHereError( "token type " + tokenType + " is not a 'value' type" );

        }

        return holder;

    }

}
