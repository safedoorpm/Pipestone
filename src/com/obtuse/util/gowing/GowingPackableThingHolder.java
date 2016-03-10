package com.obtuse.util.gowing;

import com.obtuse.util.gowing.p2a.GowingEntityReference;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a single named thing (int, String, user-defined Glorble, etc).
 */

public interface GowingPackableThingHolder extends Comparable<GowingPackableThingHolder> {

    boolean pack( GowingPacker packer2 );

    int[] PrimitiveIntegerArrayValue();
    Integer[] ContainerIntegerArrayValue();
    long[] PrimitiveLongArrayValue();
    Long[] ContainerLongArrayValue();
    String[] StringArrayValue();
    GowingPackable[] PackableArrayValue();

//    void emitActualValue( Packer2 packer );

    EntityName getName();

    Object getObjectValue();

    boolean isNull();

    /**
     Return this instance's object after casting it to a {@link GowingEntityReference}.
     <p/>This method is exactly equivalent to <blockquote><code>(EntityTypeReference)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link GowingEntityReference}.
     @throws ClassCastException if this instance's object is not actually a {@link GowingEntityReference} instance.
     */

    GowingEntityReference EntityTypeReference();

    /**
     Cast this instance's object to a {@link Character} and return its <code>char</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.CharacterValue().charValue()</code></blockquote>
     @return this instance's char value.
     @throws ClassCastException if this instance's object is not actually a {@link Character} instance.
     */

    char charValue();

    /**
     Return this instance's object after casting it to a {@link Character}.
     <p/>This method is exactly equivalent to <blockquote><code>(Character)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Character}.
     @throws ClassCastException if this instance's object is not actually a {@link Character} instance.
     */

    Character CharacterValue();

    /**
     Return this instance's object after casting it to a {@link String}.
     <p/>This method is exactly equivalent to <blockquote><code>(String)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link String}.
     @throws ClassCastException if this instance's object is not actually a {@link String} instance.
     */

    String StringValue();

    /**
     Cast this instance's object to a {@link Boolean} and return its <code>boolean</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.BooleanValue().booleanValue()</code></blockquote>
     @return this instance's char value.
     @throws ClassCastException if this instance's object is not actually a {@link Boolean} instance.
     */

    boolean booleanValue();

    /**
     Return this instance's object after casting it to a {@link Boolean}.
     <p/>This method is exactly equivalent to <blockquote><code>(Boolean)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Boolean}.
     @throws ClassCastException if this instance's object is not actually a {@link Boolean} instance.
     */

    Boolean BooleanValue();

    /**
     Cast this instance's object to a {@link Number} and return its <code>byte</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.NumberValue().byteValue()</code></blockquote>
     @return this instance's byte value.
     @throws ClassCastException if this instance's object is not actually a {@link Number} instance.
     */

    byte byteValue();

    /**
     Return this instance's object after casting it to a {@link Byte}.
     <p/>This method is exactly equivalent to <blockquote><code>(Byte)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Byte}.
     @throws ClassCastException if this instance's object is not actually a {@link Byte} instance.
     */

    Byte ByteValue();

    /**
     Cast this instance's object to a {@link Number} and return its <code>short</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.NumberValue().shortValue()</code></blockquote>
     @return this instance's short value.
     @throws ClassCastException if this instance's object is not actually a {@link Number} instance.
     */

    short shortValue();

    /**
     Return this instance's object after casting it to a {@link Short}.
     <p/>This method is exactly equivalent to <blockquote><code>(Short)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Short}.
     @throws ClassCastException if this instance's object is not actually a {@link Short} instance.
     */

    Short ShortValue();

    /**
     Cast this instance's object to a {@link Number} and return its <code>int</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.NumberValue().intValue()</code></blockquote>
     @return this instance's int value.
     @throws ClassCastException if this instance's object is not actually a {@link Number} instance.
     */

    int intValue();

    /**
     Return this instance's object after casting it to a {@link Integer}.
     <p/>This method is exactly equivalent to <blockquote><code>(Integer)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Integer}.
     @throws ClassCastException if this instance's object is not actually a {@link Integer} instance.
     */

    Integer IntegerValue();

    /**
     Cast this instance's object to a {@link Number} and return its <code>long</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.NumberValue().longValue()</code></blockquote>
     @return this instance's long value.
     @throws ClassCastException if this instance's object is not actually a {@link Number} instance.
     */

    long longValue();

    /**
     Return this instance's object after casting it to a {@link Long}.
     <p/>This method is exactly equivalent to <blockquote><code>(Long)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Long}.
     @throws ClassCastException if this instance's object is not actually a {@link Long} instance.
     */

    Long LongValue();

    /**
     Cast this instance's object to a {@link Number} and return its <code>float</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.NumberValue().floatValue()</code></blockquote>
     @return this instance's float value.
     @throws ClassCastException if this instance's object is not actually a {@link Number} instance.
     */

    float floatValue();

    /**
     Return this instance's object after casting it to a {@link Float}.
     <p/>This method is exactly equivalent to <blockquote><code>(Float)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Float}.
     @throws ClassCastException if this instance's object is not actually a {@link Float} instance.
     */

    Float FloatValue();

    /**
     Cast this instance's object to a {@link Number} and return its <code>double</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.NumberValue().doubleValue()</code></blockquote>
     @return this instance's double value.
     @throws ClassCastException if this instance's object is not actually a {@link Number} instance.
     */

    double doubleValue();

    /**
     Return this instance's object after casting it to a {@link Double}.
     <p/>This method is exactly equivalent to <blockquote><code>(Double)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Double}.
     @throws ClassCastException if this instance's object is not actually a {@link Double} instance.
     */

    Double DoubleValue();

    /**
     Cast this instance's object after casting it to a {@link Number}.
     <p/>This method is exactly equivalent to <blockquote><code>(Number)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Number}.
     @throws ClassCastException if this instance's object is not actually a {@link Number} instance.
     */

    Number NumberValue();

    /**
     Emit the portable representation of this instance's object.
     @param packer2 the packer to use to get the job done.
     */

    void emitRepresentation( GowingPacker packer2 );

}
