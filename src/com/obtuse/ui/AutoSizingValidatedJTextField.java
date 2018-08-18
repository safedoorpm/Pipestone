/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 Create a {@link ValidatedJTextField} whose width is automagically adjusted by an instance of {@link AutoAdjustingTextFieldWidthListener}.
 <p>Subject to specified minimum width and maximum width constraints,
 the width of the text field is automagically adjusted to be within about 25 pixels of the space required to display the text.</p>
 <p>See {@link AutoAdjustingTextFieldWidthListener} for more information on how the automagic adjusting of the text field works.</p>
 */

public abstract class AutoSizingValidatedJTextField extends ValidatedJTextField {

    public static final int ABSOLUTE_MINIMUM_WIDTH = 25;

    private static JTextField s_interestingField = null;

    private final int _minWidth;
    private final int _maxWidth;

    private static FontMetrics s_likelyFontMetrics = null;

    /**
     Create an auto-size adjusting {@link ValidatedJTextField}.
     @param minWidth the minimum width that the text field should be autosized to.
     @param maxWidth the maximum width that the text field should be autosized to.
     */

    public AutoSizingValidatedJTextField( final int minWidth, final int maxWidth ) {
        super();

        _minWidth = minWidth < ABSOLUTE_MINIMUM_WIDTH ? ABSOLUTE_MINIMUM_WIDTH : minWidth;
        _maxWidth = maxWidth;

        configureAutoSizeAdjustingJTextField( this, minWidth, maxWidth );

    }

    /*
    Some possibly useful words for future reference.

     <p>The equivalent auto-width adjusting functionality can be associated with a JTextField as follows:</p>
     <blockquote>
     <pre>
     JTextField jtf = new JTextField();
     jtf.setMinimumSize( new Dimension( _desired_minimum_width_, _whatever_min_height_works_ ) );
     jtf.setMaximumSize( new Dimension( _desired_maximum_width_, _whatever_min_width_works_ ) );
     jtf.getDocument().addDocumentListener( new AutoAdjustingTextFieldWidthListener( jtf );
     </pre>
     Note that either {@code jtf.getMinimumSize().height} or {@code -1} is generally the correct value to use for {@code _whatever_min_height_works_} in the above example.
     Also, either {@code jtf.getMaximumSize().height} or {@code -1} is generally the correct value to use for {@code _whatever_max_height_works_} in the above example.
     If you're not sure which to use, try using the appropriate min/max size's height and then try {@code -1} if that doesn't get the desired results.
     </blockquote>

     */

    /**
     Configure an arbitrary existing {@link JTextField} to do the auto-size adjusting trick.
     <p>This variant of {@code configureAutoSizeAdjustingJTextField} uses the specified {@code minWidth} and {@code maxWidth}
     parameters to constrain the text field's minimum, preferred and maximum widths as follows:</p>
     <ul>
     <li>the text field's minimum width will be the specified {@code minWidth} value.</li>
     <li>the text field's maximum width will be the specified {@code maxWidth} value.</li>
     <li>the text field's preferred width will be increased to the specified {@code minWidth} value if it isn't at least that high
     and it will be reduced to the specified {@code maxWidth} value if it isn't already at least that low.</li>
     </ul>
     @param jtf the textfield to teach the auto-size adjusting trick to.
     @param minWidth the minimum width that the text field should be autosized to.
     @param maxWidth the maximum width that the text field should be autosized to.
     */

    @SuppressWarnings({"unused","UnusedReturnValue"})
    public static <T extends JTextField> T configureAutoSizeAdjustingJTextField( final @NotNull T jtf, final int minWidth, final int maxWidth ) {

        if ( s_interestingField == jtf ) {

            ObtuseUtil.doNothing();

        }

        jtf.setMinimumSize( new Dimension( minWidth, jtf.getMinimumSize().height ) );
        jtf.setMaximumSize( new Dimension( maxWidth, jtf.getMaximumSize().height ) );
        jtf.setPreferredSize( new Dimension( Math.min( Math.max( minWidth, jtf.getPreferredSize().width ), maxWidth ), jtf.getPreferredSize().height ) );

        jtf.getDocument().addDocumentListener( new AutoAdjustingTextFieldWidthListener( jtf ) );

        return jtf;

    }

    public static void maybeCacheLikelyFontMetrics( final FontMetrics fontMetrics ) {

        if ( s_likelyFontMetrics == null ) {

            s_likelyFontMetrics = fontMetrics;

        }

    }

//    /**
//     Configure an arbitrary existing {@link JTextField} to do the auto-size adjusting trick.
//     <p>This variant of {@code configureAutoSizeAdjustingJTextField} uses the text field's
//     minimum size's width and maximum size's width to determine the minimum and maximum width that the text field should be constrained to.</p>
//     @param jtf the textfield to teach the auto-size adjusting trick to.
//     */
//
//    @SuppressWarnings({"unused","UnusedReturnValue"})
//    public static <T extends JTextField> T configureAutoSizeAdjustingJTextField( final @NotNull T jtf ) {
//
//        if ( s_interestingField == jtf ) {
//
//            ObtuseUtil.doNothing();
//
//        }
//
//        jtf.getDocument().addDocumentListener( new AutoAdjustingTextFieldWidthListener( jtf ) );
//
//        return jtf;
//
//    }

    public int getMinWidth() {

        return _minWidth;

    }

    public int getMaxWidth() {

        return _maxWidth;

    }

    public String toString() {

        return "AutoSizingValidatedJTextField( minWidth=" + getMinWidth() + ", maxWidth=" + getMaxWidth() + " )";

    }

    public static void setInterestingField( final @Nullable JTextField interestingField ) {

        if ( s_interestingField != null ) {

            throw new HowDidWeGetHereError( "AutoSizingValidatedJTextField.setInterestingField:  a field is already interesting - " + s_interestingField );

        }

        s_interestingField = interestingField;

    }

    @Nullable
    public static JTextField getInterestingField() {

        return s_interestingField;

    }

    @SuppressWarnings("unused")
    public static boolean hasInterestingField() {

        return s_interestingField != null;

    }

    /**
     A document listener which tries to keep a {@link JTextField} wide enough to contain the text that it contains.
     <p/>The basic idea is to adjust the text field's preferred size such that it is always about 25 pixels wider than the text that it contains.
     <p>While all this adjusting of the text field's width is going on, the text field's minimum and maximum width - as specified by the width attributes
     of the text field's minimum size and maximum size - are respected. What this means is that if the text field's minimum size is 50 wide by some amount high
     then this class won't shrink the text field's preferred width below 50 pixels. The same notion applies when expanding the text field's preferred width
     except that it is then the text field's maximum size's width that sets the upper bound for the text field's preferred width.</p>
     */

    public static class AutoAdjustingTextFieldWidthListener implements DocumentListener {

        private final JTextField _textField;

        /**
         Create a document listener which manages the width of a {@link JTextField} to keep it about 25 pixels wider than the text that it contains.
         This width adjustment is performed by changing the text field's preferred size's width while respecting the text field's
         minimum and maximum sizes' widths.
         @param textField the text field to be managed.
         */

        public AutoAdjustingTextFieldWidthListener( final @NotNull JTextField textField ) {
            super();

            _textField = textField;
            _textField.setPreferredSize( new Dimension( _textField.getMinimumSize().width, _textField.getPreferredSize().height ) );
            maybeGrowTextField( _textField );

        }

        @Override
        public void insertUpdate( final DocumentEvent e ) {

            maybeGrowTextField( _textField );

        }

        @Override
        public void removeUpdate( final DocumentEvent e ) {

            maybeShrinkTextField( _textField );

        }

        @Override
        public void changedUpdate( final DocumentEvent e ) {

            // nothing to be done here.

        }

        public String toString() {

            return "AutoAdjustingTextFieldWidthListener( " +
                   "tf's text=" + ObtuseUtil.enquoteToJavaString( _textField.getText() ) + ", " +
                   "min width=" + _textField.getMinimumSize().width + ", " +
                   "prefSize=" + ObtuseUtil.fDim( _textField.getPreferredSize() ) +
                   "max width=" + _textField.getMaximumSize().width + ", " +
                   " )";

        }

    }

    @Nullable
    public static FontMetrics getLikelyFontMetrics( final @NotNull JTextField tf ) {

        Graphics graphics = tf.getGraphics();
        FontMetrics fontMetrics;
        if ( graphics == null ) {

            fontMetrics = s_likelyFontMetrics;
            Logger.logMsg( "likely font metrics are " + fontMetrics );

        } else {

            fontMetrics = graphics.getFontMetrics();
            Logger.logMsg( "actual font metrics are " + fontMetrics );

        }

        return fontMetrics;

    }

    public static void maybeGrowTextField( final @NotNull JTextField tf ) {

        if ( s_interestingField == tf ) {

            ObtuseUtil.doNothing();

        }

        FontMetrics fontMetrics = getLikelyFontMetrics( tf );

        if ( fontMetrics == null ) {

            return;

        }

        if ( s_interestingField == tf ) {

            ObtuseUtil.doNothing();

        }

        int textWidth = fontMetrics.stringWidth( tf.getText() );

        Dimension curPreferredSize = new Dimension( tf.getPreferredSize() );
        Dimension maxSize = tf.getMaximumSize();
        boolean changed = false;
        while ( textWidth > curPreferredSize.width - 25 && curPreferredSize.width <= maxSize.width ) {

            curPreferredSize.width += 25;
            changed = true;

        }

        if ( changed ) {

            Logger.logMsg( "grew to " + ObtuseUtil.fDim( curPreferredSize ) );

            tf.setPreferredSize( curPreferredSize );

            if ( tf.getParent() != null ) {

                tf.getParent().revalidate();

            }

        }

    }

    public static void maybeShrinkTextField( final @NotNull JTextField tf ) {

        if ( s_interestingField == tf ) {

            ObtuseUtil.doNothing();

        }

        FontMetrics fontMetrics = getLikelyFontMetrics( tf );

        if ( fontMetrics == null ) {

            return;

        }

        if ( s_interestingField == tf ) {

            ObtuseUtil.doNothing();

        }

        int textWidth = fontMetrics.stringWidth( tf.getText() );

        Dimension curPreferredSize = new Dimension( tf.getPreferredSize() );
        Dimension minSize = tf.getMinimumSize();
        boolean changed = false;
        while ( textWidth <= curPreferredSize.width - 25 && curPreferredSize.width - 25 >= minSize.width ) {

            curPreferredSize.width -= 25;
            changed = true;

        }

        if ( changed ) {

            Logger.logMsg( "shrank to " + ObtuseUtil.fDim( curPreferredSize ) );

            tf.setPreferredSize( curPreferredSize );

            if ( tf.getParent() != null ) {

                tf.getParent().revalidate();

            }

        }

    }

}
