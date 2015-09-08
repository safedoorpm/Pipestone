package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a single named thing (int, String, user-defined Glorble, etc).
 */

public interface Packable2ThingHolder2 extends Comparable<Packable2ThingHolder2> {

    boolean pack( Packer2 packer2 );

    int[] PrimitiveIntegerArrayValue();
    Integer[] ContainerIntegerArrayValue();
    long[] PrimitiveLongArrayValue();
    Long[] ContainerLongArrayValue();
    String[] StringArrayValue();
    Packable2[] PackableArrayValue();

//    void emitActualValue( Packer2 packer );

    EntityName2 getName();

    Object getObjectValue();

    /**
     Cast this instance's object to a {@link Character} and return its <code>char</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.CharacterValue().charValue()</code></blockquote>
     @return this instance's char value.
     @throws ClassCastException if this instance's object is not actually a {@link Character} instance.
     */

    char charValue();

    /**
     Cast this instance's object after casting it to a {@link Character}.
     <p/>This method is exactly equivalent to <blockquote><code>(Character)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Character}.
     @throws ClassCastException if this instance's object is not actually a {@link Character} instance.
     */

    Character CharacterValue();

    /**
     Cast this instance's object after casting it to a {@link String}.
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
     Cast this instance's object after casting it to a {@link Boolean}.
     <p/>This method is exactly equivalent to <blockquote><code>(Boolean)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Boolean}.
     @throws ClassCastException if this instance's object is not actually a {@link Boolean} instance.
     */

    Boolean BooleanValue();

    /**
     Cast this instance's object to a {@link Byte} and return its <code>byte</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.ByteValue().byteValue()</code></blockquote>
     @return this instance's byte value.
     @throws ClassCastException if this instance's object is not actually a {@link Byte} instance.
     */

    byte byteValue();

    /**
     Cast this instance's object after casting it to a {@link Byte}.
     <p/>This method is exactly equivalent to <blockquote><code>(Byte)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Byte}.
     @throws ClassCastException if this instance's object is not actually a {@link Byte} instance.
     */

    Byte ByteValue();

    /**
     Cast this instance's object to a {@link Short} and return its <code>short</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.ShortValue().shortValue()</code></blockquote>
     @return this instance's short value.
     @throws ClassCastException if this instance's object is not actually a {@link Short} instance.
     */

    short shortValue();

    /**
     Cast this instance's object after casting it to a {@link Short}.
     <p/>This method is exactly equivalent to <blockquote><code>(Short)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Short}.
     @throws ClassCastException if this instance's object is not actually a {@link Short} instance.
     */

    Short ShortValue();

    /**
     Cast this instance's object to a {@link Integer} and return its <code>int</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.IntegerValue().intValue()</code></blockquote>
     @return this instance's int value.
     @throws ClassCastException if this instance's object is not actually a {@link Integer} instance.
     */

    int intValue();

    /**
     Cast this instance's object after casting it to a {@link Integer}.
     <p/>This method is exactly equivalent to <blockquote><code>(Integer)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Integer}.
     @throws ClassCastException if this instance's object is not actually a {@link Integer} instance.
     */

    Integer IntegerValue();

    /**
     Cast this instance's object to a {@link Long} and return its <code>long</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.LongValue().longValue()</code></blockquote>
     @return this instance's long value.
     @throws ClassCastException if this instance's object is not actually a {@link Long} instance.
     */

    long longValue();

    /**
     Cast this instance's object after casting it to a {@link Long}.
     <p/>This method is exactly equivalent to <blockquote><code>(Long)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Long}.
     @throws ClassCastException if this instance's object is not actually a {@link Long} instance.
     */

    Long LongValue();

    /**
     Cast this instance's object to a {@link Float} and return its <code>float</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.FloatValue().floatValue()</code></blockquote>
     @return this instance's float value.
     @throws ClassCastException if this instance's object is not actually a {@link Float} instance.
     */

    float floatValue();

    /**
     Cast this instance's object after casting it to a {@link Float}.
     <p/>This method is exactly equivalent to <blockquote><code>(Float)(this.getObjectValue())</code></blockquote>
     @return this instance's object after casting it to a {@link Float}.
     @throws ClassCastException if this instance's object is not actually a {@link Float} instance.
     */

    Float FloatValue();

    /**
     Cast this instance's object to a {@link Double} and return its <code>double</code> value.
     <p/>This method is exactly equivalent to <blockquote><code>this.DoubleValue().doubleValue()</code></blockquote>
     @return this instance's double value.
     @throws ClassCastException if this instance's object is not actually a {@link Double} instance.
     */

    double doubleValue();

    /**
     Cast this instance's object after casting it to a {@link Double}.
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

    public void emitRepresentation( Packer2 packer2 );

}
