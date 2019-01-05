package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 Created by danny on 2018/12/15.
 */
public class Clicks {

    private static final String[] s_maskExBits = new String[32];

    static {

        Clicks.rememberBitName( InputEvent.SHIFT_DOWN_MASK, "ShiftDown" );
        Clicks.rememberBitName( InputEvent.CTRL_DOWN_MASK, "CtrlDown" );
        Clicks.rememberBitName( InputEvent.META_DOWN_MASK, "MetaDown" );
        Clicks.rememberBitName( InputEvent.ALT_DOWN_MASK, "AltDown" );
        Clicks.rememberBitName( InputEvent.BUTTON1_DOWN_MASK, "Button_1_Down" );
        Clicks.rememberBitName( InputEvent.BUTTON2_DOWN_MASK, "Button_2_Down" );
        Clicks.rememberBitName( InputEvent.BUTTON3_DOWN_MASK, "Button_3_Down" );
        Clicks.rememberBitName( InputEvent.ALT_GRAPH_DOWN_MASK, "AltGraphDown" );

        ObtuseUtil.doNothing();

    }

    /**
     Mark this as a utility class.
     */

    private Clicks() { super(); }

    /**
     Determine if the bits in the input mask are EXACTLY as specified.
     @param mask the input mask.
     @param oneBits a mask of the bits which MUST be one (on).
     Pass 0 if there are no bits which must be one.
     @param zeroBits a mask of the bits which MUST be zero (off).
     Pass 0 if there are no bits which must be zero.
     @return {@code true} if {@code mask} has all of the {@code oneBits} on and
     all of the {@code zeroBits} off in {@code mask}; {@code false} otherwise.
     This value is computed using
     <blockquote>{@code ( mask & oneBits ) == oneBits && ( mask & zeroBits ) == 0}</blockquote>
     Note that this method returns {@code true} if both {@code oneBits} and {@code zeroBits} are equal to 0.
     <p/>
     Examples:
     <ul>
     <li>{@code doesMaskDescribe( intValue, 1, 0) }
     {@code true} if {@code intValue} is odd (least significant bit is one);
     {@code false} otherwise</li>
     <li>{@code doesMaskDescribe( intValue, 0, 1) }
     {@code true} if {@code intValue} is even (least significant bit is zero);
     {@code false} otherwise</li>
     <li>{@code doesMaskDescribe( e.getModifiersEx(), InputEvent.SHIFT_DOWN_MASK, InputEvent.CTRL_DOWN_MASK )}
     {@code true} if the shift key is/was down and the ctrl key is/was NOT down (ignoring all other keys);
     {@code false} otherwise</li>
     <li>{@code doesMaskDescribe( e.getModifiersEx(), 0, InputEvent.CTRL_DOWN_MASK )}
     {@code true} if the ctrl key is/was NOT down (ignoring all other keys);
     {@code false} otherwise</li>
     </ul>
     @throws IllegalArgumentException if there are bits which must be both zero and one (i.e. {@code ( oneBits & zeroBits ) != 0 }).
     */

    public static boolean doesMaskDescribe( int mask, int oneBits, int zeroBits ) {

        if ( ( oneBits & zeroBits ) != 0 ) {

            throw new IllegalArgumentException(
                    "ObtuseUtil.doesMaskDescribe:  one bits " +
                    Integer.toBinaryString( oneBits ) + " overlap zero bits " +
                    Integer.toBinaryString( zeroBits )
            );

        }

//        Logger.logMsg(
//                "mask=" + Integer.toBinaryString( mask ) + ", " +
//                "oneBits=" + Integer.toBinaryString( oneBits ) + ", " +
//                "zeroBits=" + Integer.toBinaryString( zeroBits )
//        );

        return ( mask & oneBits ) == oneBits && ( mask & zeroBits ) == 0;

    }

    /**
     Determine if any of the 'interesting' bits in the input mask are on.
     @param mask the input mask.
     @param interestingBits a mask of the bits which are 'interesting'.
     @return {@code true} if any of the bits in {@code mask} correspond to bits in {@code interestingBits};
     {@code false} otherwise.
     This value is computed using
     <blockquote>{@code ( mask & interestingBits ) != 0}</blockquote>
     */

    public static boolean doesMaskInclude( int mask, int interestingBits ) {

        return ( mask & interestingBits ) != 0;

    }

    public static boolean isJustClick( final MouseButton mouseButton, @NotNull final MouseEvent e ) {

        return ( mouseButton.ordinal() == 0 || e.getButton() == mouseButton.ordinal() )
               &&
               e.getModifiersEx() == 0;

    }

    public static boolean isCtrlClick( final MouseButton mouseButton, @NotNull final MouseEvent e ) {

        return ( mouseButton.ordinal() == 0 || e.getButton() == mouseButton.ordinal() )
               &&
               doesMaskDescribe(
                       e.getModifiersEx(),
                       InputEvent.CTRL_DOWN_MASK,
                       ~InputEvent.CTRL_DOWN_MASK
               );

    }

    public static boolean isLeftClick( @NotNull final MouseEvent e ) {

        return ( e.getButton() == MouseButton.LEFT.ordinal() )
               &&
               e.getModifiersEx() == 0;

    }

    public static boolean isShiftClick( final MouseButton mouseButton, @NotNull final MouseEvent e ) {

        return ( mouseButton.ordinal() == 0 || e.getButton() == mouseButton.ordinal() )
               &&
               doesMaskDescribe(
                       e.getModifiersEx(),
                       InputEvent.SHIFT_DOWN_MASK,
                       ~InputEvent.SHIFT_DOWN_MASK
               );

    }

    public static boolean isOptClick( final MouseButton mouseButton, @NotNull final MouseEvent e ) {

        return ( mouseButton.ordinal() == 0 || e.getButton() == mouseButton.ordinal() )
               &&
               doesMaskDescribe(
                       e.getModifiersEx(),
                       InputEvent.ALT_DOWN_MASK,
                       ~InputEvent.ALT_DOWN_MASK
               );

    }

    public static boolean isCmdClick( final MouseButton mouseButton, @NotNull final MouseEvent e ) {

        return ( mouseButton.ordinal() == 0 || e.getButton() == mouseButton.ordinal() )
               &&
               doesMaskDescribe(
                       e.getModifiersEx(),
                       InputEvent.META_DOWN_MASK,
                       ~InputEvent.META_DOWN_MASK
               );

    }

    public static boolean isCmdLeftClick( final MouseEvent e ) {

        return isCmdClick( MouseButton.LEFT, e );
    }

    public static boolean isOptLeftClick( final MouseEvent e ) {

        return isOptClick( MouseButton.LEFT, e );
    }

    public static boolean isCtrlLeftClick( final MouseEvent e ) {

        return isCtrlClick( MouseButton.LEFT, e );
    }

    public static boolean isShiftLeftClick( final MouseEvent e ) {

        return isShiftClick( MouseButton.LEFT, e );
    }

    public static String getInputBitName( final int mask ) {

        int numberOfTrailingZeros = Integer.numberOfTrailingZeros( mask );
        int lowestOneBit = Integer.lowestOneBit( mask );
        int highestOneBit = Integer.highestOneBit( mask );

        if ( mask < 0 || highestOneBit != lowestOneBit || numberOfTrailingZeros >= 32 ) {

            return "<mask " + Integer.toBinaryString( mask ) + ">";

        } else {

            return "<" + s_maskExBits[numberOfTrailingZeros] + ">";

        }

    }

    public static String describeInputBitMask( final int mask ) {

        StringBuilder sb = new StringBuilder(  Integer.toBinaryString( mask ) ).append( "b=<" );
        String comma = "";
        for ( int shift = 0; shift < 32; shift += 1 ) {

            if ( ( mask & ( 1 << shift ) ) != 0 ) {

                String maskName = getBitNameByShiftAmount( shift );
                sb.append( comma ).append( maskName );

                comma = ",";

            }

        }

        sb.append( ">" );

        return sb.toString();

    }

    public static String getBitNameByShiftAmount( int shift ) {

        if ( shift < 0 || shift > s_maskExBits.length || s_maskExBits[shift] == null ) {

            return "1 << " + shift;

        }

        return s_maskExBits[shift];

    }

    private static void rememberBitName( final int maskValue, final String name ) {

        if ( maskValue != 0 ) {

            if ( Integer.highestOneBit( maskValue ) == Integer.lowestOneBit( maskValue ) ) {

                int bitIx = Integer.numberOfTrailingZeros( maskValue );

                if ( s_maskExBits[bitIx] == null ) {

                    s_maskExBits[bitIx] = name;

                }

            }

        }

    }

    public enum MouseButton { ANY, LEFT, MIDDLE, RIGHT }

//    /**
//     Determine if the bits in the input mask are EXACTLY as specified.
//     @param mask the input mask.
//     @param requiredBits a mask of the bits which MUST be on.
//     @param ignoredBits a mask of the bits which MUST be ignored
//     (this parameter takes precedence over our {@code requiredBits} parameter -
//     bits which must be on and must be ignored are ignored).
//     @return {@code true} if {@code mask} has all of the {@code requiredBits}
//     set after ignoring any bits in {@code mask} or in {@code ignoredBits}; {@code false} otherwise.
//     This value is computed using
//     <blockquote>{@code ( ( mask & ~ignoredBits ) & requiredBits ) == ( requiredBits & ~ignoredBits )}</blockquote>
//     */
//
//    public static boolean areBitsAsSpecified( int mask, int requiredBits, int ignoredBits ) {
//
//        return ( ( mask & ~ignoredBits ) & requiredBits ) == ( requiredBits & ~ignoredBits );
//
//    }


//    public static boolean isRightClick( @NotNull final MouseEvent e ) {
//
//        return ( e.getButton() == 3 )
//               &&
//               e.getModifiersEx() == 0;
//
//    }

}
