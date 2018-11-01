package com.obtuse.ui.copier;

import com.obtuse.ui.AutoSizingValidatedJTextField;
import com.obtuse.ui.ValidatedJTextField;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

/**
 Provide/manage a JTextField or a JLabel within a context that allows a few of the key basic text entity
 manipulation methods like {@link #getText()} and {@link #setText(String)} to be called without having to do any casting.
 */

public interface ObtuseTextElement {

    /**
     A {@link ValidatedJTextField} that implements the {@link ObtuseTextElement} interface.
     <p>By being an extension of {@link ValidatedJTextField}, instances of this class can take advantage of the
     validation facility provided by {@code ValidatedJTextField}. The abstract methods in {@code ValidatedJTextField}
     are stubbed out here to make the use of {@code ValidatedJTextField}'s validation facility truly optional.</p>
     */

    class ObtuseTextField extends ValidatedJTextField implements ObtuseTextElement {

        /**
         Create a minimal text field.
         <p>Conceptually equivalent to the {@link JTextField#JTextField()} constructor.</p>
         */

        public ObtuseTextField() {
            super();

            reportVisibility();

        }

        /**
         Create a text field that takes advantage of the {@link AutoSizingValidatedJTextField} facility.
         <p>This constructor is conceptually equivalent to creating a minimal text field ala {@link #ObtuseTextField()}
         and then invoking
         {@link AutoSizingValidatedJTextField#configureAutoSizeAdjustingJTextField(JTextField, int, int)} to
         setup the autosizing of the field and then setting the text field's preferred width to establish it's initial screen width.
         </p>
         @param minWidth the minimum width of the field in pixels.
         @param maxWidth the maximum width of the field in pixels.
         @param initialWidth the initial width of the field in pixels.
         @param interestingField {@code true} if the newly created field should be passed to
         {@link AutoSizingValidatedJTextField#setInterestingField(JTextField)}; {@code false} otherwise.
         */

        public ObtuseTextField(final int minWidth, final int maxWidth, final int initialWidth, final boolean interestingField ) {
            super();

            if ( interestingField ) {

                AutoSizingValidatedJTextField.setInterestingField( this );

            }

            configureAutoSizeAdjustingJTextField( this, minWidth, maxWidth );

            Dimension minSize = getMinimumSize();
            Dimension maxSize = getMaximumSize();
            if ( minSize.width <= initialWidth && initialWidth <= maxSize.width ) {

                reportVisibility();

                Dimension prefSize = getPreferredSize();
                prefSize.width = initialWidth;
                setPreferredSize( prefSize );

            }

            reportVisibility();

        }

        /**
         Create a text field that takes advantage of the {@link AutoSizingValidatedJTextField} facility.
         <p>Invoking this constructor is exactly equivalent to invoking
         <blockquote>{@code new ObtuseTextField( minWidth, maxWidth, initialWidth, false );}</blockquote>
         </p>
         @param minWidth the minimum width of the field in pixels.
         @param maxWidth the maximum width of the field in pixels.
         @param initialWidth the initial width of the field in pixels.
         */

        public ObtuseTextField(final int minWidth, final int maxWidth, final int initialWidth ) {
            this( minWidth, maxWidth, initialWidth, false );

        }

        /**
         Create a text field that takes advantage of the {@link AutoSizingValidatedJTextField} facility.
         <p>Invoking this constructor is exactly equivalent to invoking
         <blockquote>{@code new ObtuseTextField( minWidth, maxWidth, initialWidth, interestingField );}</blockquote>
         and then setting the initial value of the text field to {@code initialText}.
         </p>
         @param initialText the initial contents of the newly created text field.
         @param minWidth the minimum width of the field in pixels.
         @param maxWidth the maximum width of the field in pixels.
         @param initialWidth the initial width of the field in pixels.
         @param interestingField {@code true} if the newly created field should be passed to
         {@link AutoSizingValidatedJTextField#setInterestingField(JTextField)}; {@code false} otherwise.
         */

        public ObtuseTextField(
                final String initialText,
                final int minWidth,
                final int maxWidth,
                final int initialWidth,
                final boolean interestingField
        ) {
            this( minWidth, maxWidth, initialWidth, interestingField );

            setText( initialText );

            reportVisibility();

        }

        /**
         Create a text field that takes advantage of the {@link AutoSizingValidatedJTextField} facility.
         <p>Invoking this constructor is exactly equivalent to invoking
         <blockquote>{@code new ObtuseTextField( initialText, minWidth, maxWidth, initialWidth, false );}</blockquote>
         </p>
         @param initialText the initial contents of the newly created text field.
         @param minWidth the minimum width of the field in pixels.
         @param maxWidth the maximum width of the field in pixels.
         @param initialWidth the initial width of the field in pixels.
         */

        public ObtuseTextField(
                final String initialText,
                final int minWidth,
                final int maxWidth,
                final int initialWidth
        ) {
            this( initialText, minWidth, maxWidth, initialWidth, false );

        }

        /**
         Create a text field with specified initial contents.
         <p>Invoking this constructor is equivalent to invoking {@link ObtuseTextField#ObtuseTextField()} and then
         invoking {@code setText( initialText )} on the result.</p>
         @param initialText the initial contents of the field.
         */

        @SuppressWarnings("unused")
        public ObtuseTextField(final String initialText ) {

            super( initialText );

            reportVisibility();

        }

        /**
         Trace/debug method that reports this field's visibility via {@link Logger#logMsg(String)}.
         */

        public void reportVisibility() {

            if ( !isVisible() ) {

                ObtuseUtil.doNothing();

            }

            Logger.logMsg( "ObtuseTextElement( " + ObtuseUtil.enquoteToJavaString( getName() ) + " ) is " + ( isVisible() ? "" : "not " ) + "visible" );

            ObtuseUtil.doNothing();

        }

        /**
         Provides a breakpoint to intercept calls to {@link JComponent#setPreferredSize(Dimension)} when this is
         the {@link AutoSizingValidatedJTextField}'s 'interesting field' ("use the source Luke").
         @param preferredSize the preferred size for this field.
         */

        public void setPreferredSize( final Dimension preferredSize ) {

            if ( AutoSizingValidatedJTextField.getInterestingField() == this ) {

                ObtuseUtil.doNothing();

            }

            super.setPreferredSize( preferredSize );

        }

//        public void paint( final Graphics g ) {
//
//            if ( AutoSizingValidatedJTextField.getInterestingField() == this ) {
//
//                Logger.logMsg(
//                        "ObtuseTextElement.paint:  " +
//                        "minSize=" + ObtuseUtil.fDim( getMinimumSize() ) + ", " +
//                        "prefSize=" + ObtuseUtil.fDim( getPreferredSize() ) + ", " +
//                        "maxSize=" + ObtuseUtil.fDim( getMaximumSize() )
//                );
//
//                ObtuseUtil.doNothing();
//
//            }
//
//            super.paint( g );
//
//        }

        public String toString() {

            return "ObtuseTextField( " + ObtuseUtil.enquoteToJavaString( getText() ) + " )";

        }

        /**
         Provide a default implementation of {@link com.obtuse.ui.EditValueAdvocate#isValueValid}
         which always returns {@code true}.
         @param candidateValue the value to be validated.
         @return {@code true} (always)
         */

        @Override
        public boolean isValueValid( final String candidateValue ) {

            return true;

        }

        /**
         A default implementation of {@link ValidatedJTextField#setRollbackValue(String)} that always throws
         and {@link IllegalArgumentException}.
         @param rollbackValue ignored.
         */

        @Override
        public void setRollbackValue( final @NotNull String rollbackValue ) {

            throw new IllegalArgumentException( "ObtuseTextElement.setRollbackValue:  not implemented" );

        }

        /**
         A default implementation of {@link ValidatedJTextField#getRollbackValue()} that always throws
         and {@link IllegalArgumentException}.
         */

        @Override
        public String getRollbackValue() {

            throw new IllegalArgumentException( "ObtuseTextElement.getRollbackValue:  not implemented" );

        }

    }

    /**
     A very slightly extended implementation of {@link JLabel} that implements the {@link ObtuseTextElement} interface.
     */

    class ObtuseLabel extends JLabel implements ObtuseTextElement {

        public static final String DEFAULT_PREFIX = "(";
        public static final String DEFAULT_SUFFIX = ")";

        private final String _prefix = DEFAULT_PREFIX;
        private final String _suffix = DEFAULT_SUFFIX;
        private String _text = null;

        /**
         Create a minimal label.
         */

        public ObtuseLabel() {
            super();

        }

        /**
         Create a minimal label with an initial text value.
         @param initialText the initial text value.
         */

        public ObtuseLabel(final @Nullable String initialText ) {
            super();

            setText( initialText );

        }

        /**
         Create a minimal label with an initial {@link Icon} value.
         @param initialIcon the initial {@link Icon} value.
         */

        public ObtuseLabel(final @Nullable Icon initialIcon ) {
            super( initialIcon );

        }

        /**
         Create a minimal label with initial text and {@link Icon} values.
         @param initialText the initial text value.
         This text value is
         @param initialIcon the initial {@link Icon} value.
         @param horizontalAlignment the icon's initial horizontal alignment
         (see {@link JLabel#JLabel(String,Icon,int)} for more info).
         */

        public ObtuseLabel(
                final @Nullable String initialText,
                final @Nullable Icon initialIcon,
                final int horizontalAlignment
        ) {
            super(
                    null,
                    initialIcon,
                    horizontalAlignment
            );

            setText( initialText );

        }

        @Override
        public Optional<String> getOptionalPrefix() {

            return Optional.of( _prefix );

        }

        @Override
        public Optional<String> getOptionalSuffix() {

            return Optional.of( _suffix );

        }

        @Override
        public String getText() {

            return _text;

        }

        @Override
        public void setText( final @Nullable String text ) {

            _text = text;
            if ( _text == null ) {

                super.setText( null );

            } else {

                super.setText( getOptionalPrefix().orElse( "" ) + _text + getOptionalSuffix().orElse( "" ) );

            }

        }

        public String toString() {

            return "ObtuseLabelField( " + ObtuseUtil.enquoteToJavaString( getText() ) + " )";

        }

    }

    boolean isVisible();

    void setVisible( final boolean visible );

    @SuppressWarnings("unused")
    default Optional<String> getOptionalPrefix() {

        return Optional.empty();

    }

    @SuppressWarnings("unused")
    default void setOptionalPrefix( final @NotNull String prefix ) {

        ObtuseUtil.doNothing();

    }

    @SuppressWarnings("unused")
    default Optional<String> getOptionalSuffix() {

        return Optional.empty();

    }

    @SuppressWarnings("unused")
    default void setOptionalSuffix( final @NotNull String suffix ) {

        ObtuseUtil.doNothing();

    }

    default JComponent getAsJComponent() {

        return (JComponent)this;

    }

    default boolean isLabel() {

        return this instanceof JLabel;

    }

    default boolean isTextField() {

        return this instanceof JTextField;

    }

    Dimension getMinimumSize();

    Dimension getPreferredSize();

    Dimension getMaximumSize();

    void setMinimumSize( Dimension size );

    void setPreferredSize( Dimension size );

    void setMaximumSize( Dimension size );

    void setText( final String text );

    String getText();

    /**
     Configure an arbitrary existing {@link JTextField} to do the auto-size adjusting trick.
     <p>A variant of {@link AutoSizingValidatedJTextField#configureAutoSizeAdjustingJTextField(JTextField, int, int)}
     which works with {@link ObtuseTextField} instances.
     You'll get a {@link IllegalArgumentException} thrown if you pass it anything else like, for example, a
     {@link ObtuseLabel}.
     See {@link AutoSizingValidatedJTextField#configureAutoSizeAdjustingJTextField(JTextField, int, int)} for more info.

     @param bte      the {@link ObtuseTextElement} (that must also be a {@link ObtuseTextField}) to teach the auto-size
     adjusting trick to.
     @param minWidth the minimum width that the text field should be autosized to.
     @param maxWidth the maximum width that the text field should be autosized to.
     */

    @SuppressWarnings({ "unused", "UnusedReturnValue" })
    static ObtuseTextElement configureAutoSizeAdjustingJTextField(
            final @NotNull ObtuseTextElement bte,
            final int minWidth,
            final int maxWidth
    ) {

        if ( bte.isTextField() ) {

            JTextField jtf = (JTextField)bte;

            AutoSizingValidatedJTextField.configureAutoSizeAdjustingJTextField( jtf, minWidth, maxWidth );

        } else {

            throw new IllegalArgumentException(
                    "ObtuseTextElement.configureAutoSizeAdjustingJTextField:  " +
                    "specified ObtuseTextElement is not an actual text FIELD (it's probably a JLabel)"
            );

        }

        return bte;

    }

}