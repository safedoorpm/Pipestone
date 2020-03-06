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
import java.util.Optional;

/**
 Create a {@link ValidatedJTextField} whose width is automagically adjusted by an instance of {@link AutoAdjustingTextFieldWidthListener}.
 <p>Subject to specified minimum width and maximum width constraints,
 the width of the text field is automagically adjusted to be within about 25 pixels of the space required to display the text.</p>
 <p>See {@link AutoAdjustingTextFieldWidthListener} for more information on how the automagic adjusting of the text field works.</p>
 */

public abstract class AutoSizingValidatedJTextField extends ValidatedJTextField {

    public static boolean s_globalTraceMode = true;

    public static final int ABSOLUTE_MINIMUM_WIDTH = 50;
    public static final int TEXT_WIDTH_CHANGE_DELTA = 1;

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

        _minWidth = Math.max( minWidth, ABSOLUTE_MINIMUM_WIDTH );
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

    @SuppressWarnings("unused")
    public static void setGlobalTraceMode( final boolean globalTraceMode ) {

        s_globalTraceMode = globalTraceMode;

    }

    @SuppressWarnings("unused")
    public static boolean isGlobalTraceMode() {

        return s_globalTraceMode;

    }

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

        private boolean _traceMode = false;

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
            maybeAdjustTextField( _textField, s_globalTraceMode || _traceMode );

        }

        @SuppressWarnings("unused")
        public void setTraceMode( final boolean traceMode ) {

            _traceMode = traceMode;

        }

        @SuppressWarnings("unused")
        public boolean isTraceMode() {

            return _traceMode;

        }

        @Override
        public void insertUpdate( final DocumentEvent e ) {

            maybeAdjustTextField( _textField, s_globalTraceMode || _traceMode );

        }

        @Override
        public void removeUpdate( final DocumentEvent e ) {

            maybeAdjustTextField( _textField, s_globalTraceMode || _traceMode );

        }

        @Override
        public void changedUpdate( final DocumentEvent e ) {

            // nothing to be done here.

            ObtuseUtil.doNothing();

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

        } else {

            fontMetrics = graphics.getFontMetrics();

        }

        return fontMetrics;

    }

    @SuppressWarnings("Duplicates")
    public static void maybeAdjustTextField( final @NotNull JTextField tf, final boolean traceMode ) {

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

        int textWidth = fontMetrics.stringWidth( tf.getText() ) + tf.getInsets().left + tf.getInsets().right;

        Dimension curPreferredSize = new Dimension( tf.getPreferredSize() );
        Dimension minSize = tf.getMinimumSize();
        Dimension maxSize = tf.getMaximumSize();

        boolean changed = false;
        boolean growing = true;

        while ( true ) {

            if ( growing ) {

                int curPrefWidth = curPreferredSize.width;

                // Is the width of the text within TEXT_WIDTH_CHANGE_DELTA pixels of the current preferred size of the JTextField?

                boolean ifExprLeft = textWidth >= curPrefWidth - TEXT_WIDTH_CHANGE_DELTA;

                // Is there still room to make the field wider?

                boolean ifExprRight = curPrefWidth < maxSize.width;

                // If both of the above conditions are true then it is time to make the field wider.

                boolean growIt = ifExprLeft && ifExprRight;

                Logger.maybeLogMsg(
                        () -> "maybeGrowingNow:  DELTA=" + TEXT_WIDTH_CHANGE_DELTA + ", " +
                              "textWidth=" + textWidth + ", " +
                              "cPrefWidth=" + curPrefWidth + ", " +
                              "cPrefWidth-DELTA=" + ( curPrefWidth - TEXT_WIDTH_CHANGE_DELTA ) + ", " +
                              "maxWidth=" + maxSize.width + ", " +
                              "ifExprLeft=" + ifExprLeft + ", " +
                              "ifExprRight=" + ifExprRight + ", " +
                              "ifExpr=" + growIt,
                        traceMode
                );

                if ( growIt ) {

                    ObtuseUtil.doNothing();

                } else {

                    growing = false;
                    continue;

                }

            } else {

                int curPrefWidth = curPreferredSize.width;

                // Is the width of the text at least double TEXT_WIDTH_CHANGE_DELTA pixels narrower than the current preferred size of the JTextField?

                boolean ifExprLeft = textWidth < curPrefWidth - 2 * TEXT_WIDTH_CHANGE_DELTA;

                // Is there still room to make the field narrower?

                boolean ifExprRight = curPrefWidth >= minSize.width + TEXT_WIDTH_CHANGE_DELTA;

                // If both of the above conditions are true then it is time to make the field narrower.

                boolean shrinkIt = ifExprLeft && ifExprRight;

                Logger.maybeLogMsg(
                        () -> "maybeShrinkingNow:  DELTA=" + TEXT_WIDTH_CHANGE_DELTA + ", " +
                              "textWidth=" + textWidth + ", " +
                              "cPrefWidth=" + curPrefWidth + ", " +
                              "cPrefWidth-2*DELTA=" + ( curPrefWidth - 2 * TEXT_WIDTH_CHANGE_DELTA ) + ", " +
                              "minWidth=" + minSize.width + ", " +
                              "ifExprLeft=" + ifExprLeft + ", " +
                              "ifExprRight=" + ifExprRight + ", " +
                              "ifExpr=" + shrinkIt,
                        traceMode
                );

                if ( shrinkIt ) {

                    ObtuseUtil.doNothing();

                } else {

                    break;

                }

            }

            curPreferredSize.width += ( growing ? 1 : -1 ) * TEXT_WIDTH_CHANGE_DELTA;
            changed = true;

        }

        if ( changed ) {

            Logger.maybeLogMsg( () -> "grew to " + ObtuseUtil.fDim( curPreferredSize ), traceMode );

            if ( curPreferredSize.width < tf.getWidth() ) {

                ObtuseUtil.doNothing();

            }

            tf.setPreferredSize( curPreferredSize );

            Logger.maybeLogMsg(
                    () -> "tf:  " +
                          "min=" + ObtuseUtil.fDim( tf.getMinimumSize() ) + ", " +
                          "pref=" + ObtuseUtil.fDim( tf.getPreferredSize() ) + ", " +
                          "max=" + ObtuseUtil.fDim( tf.getMaximumSize() ) + ", " +
                          "size=" + ObtuseUtil.fBounds( tf.getBounds() ),
                    traceMode
            );

            if ( tf.getParent() != null ) {

                Logger.maybeLogMsg(
                        () -> "pr:  " +
                              "min=" + ObtuseUtil.fDim( tf.getParent().getMinimumSize() ) + ", " +
                              "pref=" + ObtuseUtil.fDim( tf.getParent().getPreferredSize() ) + ", " +
                              "max=" + ObtuseUtil.fDim( tf.getParent().getMaximumSize() ) + ", " +
                              "size=" + ObtuseUtil.fBounds( tf.getParent().getBounds() ),
                        traceMode
                );

                tf.revalidate();

            }

            Optional<Window> win = ObtuseGuiEventUtils.findOurTopWindow( tf );
            if ( win.isPresent() ) {

                Logger.maybeLogMsg( () -> "got the window!", traceMode );

                win.get().pack();

            }
            ObtuseUtil.doNothing();

        }

    }

    /**
     Old gold - will be deleted soon.
     @param tf the textfield of interest.
     @param traceMode whether or not this call should generate debug/trace output.
     @deprecated
     */

    @SuppressWarnings({ "Duplicates", "unused" })
    @Deprecated
    private static void obsoleteButWorkingMaybeGrowTextField( final @NotNull JTextField tf, final boolean traceMode ) {

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

        int textWidth = fontMetrics.stringWidth( tf.getText() ) + tf.getInsets().left + tf.getInsets().right;

        Dimension curPreferredSize = new Dimension( tf.getPreferredSize() );
        Dimension minSize = tf.getMinimumSize();
        Dimension maxSize = tf.getMaximumSize();

        boolean changed = false;
        while ( true ) {

            int curPrefWidth = curPreferredSize.width;

            // Is the width of the text within TEXT_WIDTH_CHANGE_DELTA pixels of the current preferred size of the JTextField?

            boolean ifExprLeft = textWidth >= curPrefWidth - TEXT_WIDTH_CHANGE_DELTA;

            // Is there still room to make the field wider?

            boolean ifExprRight = curPrefWidth < maxSize.width;

            // If both of the above conditions are true then it is time to make the field wider.

            boolean ifExpr = ifExprLeft && ifExprRight;

            Logger.maybeLogMsg(
                    () -> "maybeGrowingNow:  DELTA=" + TEXT_WIDTH_CHANGE_DELTA + ", " +
                          "textWidth=" + textWidth + ", " +
                          "cPrefWidth=" + curPrefWidth + ", " +
                          "cPrefWidth-DELTA=" + ( curPrefWidth - TEXT_WIDTH_CHANGE_DELTA ) + ", " +
                          "maxWidth=" + maxSize.width + ", " +
                          "ifExprLeft=" + ifExprLeft + ", " +
                          "ifExprRight=" + ifExprRight + ", " +
                          "ifExpr=" + ifExpr,
                    traceMode
            );

            if ( ifExpr ) {

                curPreferredSize.width += TEXT_WIDTH_CHANGE_DELTA;
                changed = true;

            } else {

                break;

            }

        }

        if ( changed ) {

            Logger.maybeLogMsg( () -> "grew to " + ObtuseUtil.fDim( curPreferredSize ), traceMode );

            if ( curPreferredSize.width < tf.getWidth() ) {

                ObtuseUtil.doNothing();

            }

            tf.setPreferredSize( curPreferredSize );

            Logger.maybeLogMsg(
                    () -> "tf:  " +
                          "min=" + ObtuseUtil.fDim( tf.getMinimumSize() ) + ", " +
                          "pref=" + ObtuseUtil.fDim( tf.getPreferredSize() ) + ", " +
                          "max=" + ObtuseUtil.fDim( tf.getMaximumSize() ) + ", " +
                          "size=" + ObtuseUtil.fBounds( tf.getBounds() ),
                    traceMode
            );

            if ( tf.getParent() != null ) {

                Logger.maybeLogMsg(
                        () -> "pr:  " +
                              "min=" + ObtuseUtil.fDim( tf.getParent().getMinimumSize() ) + ", " +
                              "pref=" + ObtuseUtil.fDim( tf.getParent().getPreferredSize() ) + ", " +
                              "max=" + ObtuseUtil.fDim( tf.getParent().getMaximumSize() ) + ", " +
                              "size=" + ObtuseUtil.fBounds( tf.getParent().getBounds() ),
                        traceMode
                );

                tf.revalidate();

            }

            Optional<Window> win = ObtuseGuiEventUtils.findOurTopWindow( tf );
            if ( win.isPresent() ) {

                Logger.maybeLogMsg( () -> "got the window!", traceMode );

                win.get().pack();

            }
            ObtuseUtil.doNothing();

        }

    }

    /**
     Old gold - will be deleted soon.
     @param tf the textfield of interest.
     @param traceMode whether or not this call should generate debug/trace output.
     @deprecated
     */

    @SuppressWarnings({ "Duplicates", "unused" })
    @Deprecated
    private static void obsoleteButWorkingMaybeShrinkTextField( final @NotNull JTextField tf, final boolean traceMode ) {

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

        int textWidth = fontMetrics.stringWidth( tf.getText() ) + tf.getInsets().left + tf.getInsets().right;

        Dimension curPreferredSize = new Dimension( tf.getPreferredSize() );
        Dimension minSize = tf.getMinimumSize();
        Dimension maxSize = tf.getMaximumSize();
        boolean changed = false;
        while ( true ) {

            int curPrefWidth = curPreferredSize.width;

            // Is the width of the text at least double TEXT_WIDTH_CHANGE_DELTA pixels narrower than the current preferred size of the JTextField?

            boolean ifExprLeft = textWidth < curPrefWidth - 2 * TEXT_WIDTH_CHANGE_DELTA;

            // Is there still room to make the field narrower?

            boolean ifExprRight = curPrefWidth >= minSize.width + TEXT_WIDTH_CHANGE_DELTA;

            // If both of the above conditions are true then it is time to make the field narrower.

            boolean ifExpr = ifExprLeft && ifExprRight;

            Logger.maybeLogMsg(
                    () -> "maybeShrinkingNow:  DELTA=" + TEXT_WIDTH_CHANGE_DELTA + ", " +
                          "textWidth=" + textWidth + ", " +
                          "cPrefWidth=" + curPrefWidth + ", " +
                          "cPrefWidth-2*DELTA=" + ( curPrefWidth - 2 * TEXT_WIDTH_CHANGE_DELTA ) + ", " +
                          "minWidth=" + minSize.width + ", " +
                          "ifExprLeft=" + ifExprLeft + ", " +
                          "ifExprRight=" + ifExprRight + ", " +
                          "ifExpr=" + ifExpr,
                    traceMode
            );

            if ( ifExpr ) {

                curPreferredSize.width -= TEXT_WIDTH_CHANGE_DELTA;
                changed = true;

            } else {

                break;

            }

        }

        if ( changed ) {

            Logger.maybeLogMsg( () -> "shrank to " + ObtuseUtil.fDim( curPreferredSize ), traceMode );

            tf.setPreferredSize( curPreferredSize );

            if ( tf.getParent() != null ) {

                tf.getParent().revalidate();

            }

            ObtuseUtil.doNothing();

        }

    }

}
