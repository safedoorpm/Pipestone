/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.gowing;

/**
 Something that is interested in seeing parsed metadata comments.
 */

public interface GowingMetaDataHandler {

    /**
     Do something with a parsed String metadata comment.

     @param name  the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value the parsed value of the comment (identical to what was passed to
     {@link GowingPacker#emitMetaData(String, String)} during packing).
     */

    void processMetaData( String name, String value );

    /**
     Do something with a parsed long metadata comment.

     @param name  the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value the parsed value of the comment (identical to what was passed to
     {@link GowingPacker#emitMetaData(String, long)} during packing).
     */

    void processMetaData( String name, long value );

    /**
     Do something with a parsed boolean metadata comment.

     @param name  the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value the parsed value of the comment (identical to what was passed to
     {@link GowingPacker#emitMetaData(String, boolean)} during packing).
     */

    void processMetaData( String name, boolean value );

    /**
     Do something with a parsed double metadata comment.

     @param name  the name of the comment (some non-empty combination of uppercase letters and underscores).
     @param value the parsed value of the comment.
     <p>In simple terms, if the double value {@code v} was passed to {@link GowingPacker#emitMetaData(String, double)}
     during packing then this method is passed the value of {@code Double.valueOf( Double.toString( v ) )}.</p>
     <p>See {@link Double#toString(double)} and {@link Double#valueOf(String)} for more information.</p>
     <p>In more detailed terms . . .</p>
     <ul>
     <li>NaNs passed earlier to {@code GowingPacker.emitMetaData(String,double)} will be passed to this method as a
     generic IEEE 754 double precision NaN
     (any games being played by the earlier caller of {@code GowingPacker.emitMetaData(String,double)}
     with the undefined bits in IEEE 754 NaN's are ignored - you get whatever {@code Double.valueOf( "NaN" )} returns)</li>
     <li>Infinities passed earlier to {@code GowingPacker.emitMetaData(String, double)} will be passed to this method
     as an appropriately signed IEEE 754 double precision infinity</li>
     <li>positive and negative zeros passed earlier to {@code GowingPacker.emitMetaData(String, double)} will be
     passed to this method as a corresponding IEEE 754 double precision positive or negative zero
     (it's true: the IEEE 754 standard really does define both positive and negative zeros - see the standard for more
     info)</li>
     <li>all other double values passed earlier to {@code GowingPacker.emitMetaData(String, double)} are passed to this
     method on the basis that if the double value {@code v} was passed to {@link GowingPacker#emitMetaData(String, double)}
     earlier then {@code Double.valueOf( Double.toString( v ) )} is passed to this method.</li>
     </ul>
     <p>The (relatively) short story on IEEE positive and negative zeros is this:</p>
     <ul>
     <li>you can get a negative zero by using the double constant {@code -0.0d}</li>
     <li>If you divide a positive number by positive zero then you get the IEEE 754 positive infinity value.
     Similarly, if you divide a positive number by negative zero then you get the IEEE 754 negative infinity value.
     This is an easy way to determine if a zero value is negative zero or positive zero
     (if {@code v} is an IEEE zero then {@code ( 1 / v ) > 0.0} is true if {@code v} is positive zero and is false if
     {@code v} is negative zero; note that this also means that the rather common belief that division by zero yields a
     NaN in IEEE 754 is wrong (the easiest way to get a NaN is probably to divide any IEEE 754 zero by any IEEE 754 zero))
     .</li>
     <li>if you format a negative zero by, for example, passing it to {@link Double#toString(double)} then you will get
     "-0.0" (formatting positive zero yields "0.0").</li>
     <li>a negative zero behaves exactly like a positive zero in essentially any other context that you are
     likely to encounter.</li>
     <li>probably the best way to think about negative zero is that it represents a ridiculously small value
     that is just barely on the negative side of mathematically exact zero
     (in other words, if you want to get VERY technical then it isn't really zero).
     On the other hand, if you choose to
     think of negative zero in this way then you need to keep in mind that positive zero is a value which represents
     either mathematically exact zero or a ridiculously small value that is just barely on the positive side of
     mathematically exact zero.</li>
     <li>When I say "ridiculously small value", I really do mean "ridiculously small value" in the sense that there
     is no value that can be represented in IEEE 754 floating point which is between negative zero and mathematically
     exact zero.</li>
     <li>You are forgiven if your head is starting to hurt . . .</li>
     </ul>
     */

    void processMetaData( String name, double value );

}
