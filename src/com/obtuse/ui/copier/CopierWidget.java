package com.obtuse.ui.copier;

import com.obtuse.ObtuseConstants;
import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

/**
 Provide a way to copy a value into this field's data field.
 */

public class CopierWidget
        extends CopierDataSource
        implements ImageButtonOwner {

    // It might be useful someday to have the Unicode characters for some left arrows so here are the ones
    // that I found while exploring the idea of using a text label for the left arrow.
    // private static String choices = "⇐⬅︎←";

    private static class NotFinishedMarker extends JLabel {

        private NotFinishedMarker() {
            super( "NOT FINISHED" );

        }

    }

    public static final ImageIcon LEFT_ARROW_ICON_20x20;
    public static final ImageIcon LEFT_ARROW_ICON_16x16;

    static {

        Optional<ImageIcon> optImageIcon = ImageIconUtils.fetchIconImage(
                "button-Go_back_16x16.png",
                0,
                ObtuseConstants.OBTUSE_RESOURCES_DIRECTORY
        );
        LEFT_ARROW_ICON_16x16 = optImageIcon.orElse( null );

        optImageIcon = ImageIconUtils.fetchIconImage(
                "button-Go_back_20x20.png",
                0, ObtuseConstants.OBTUSE_RESOURCES_DIRECTORY
        );
        LEFT_ARROW_ICON_20x20 = optImageIcon.orElse( null );

        if ( LEFT_ARROW_ICON_20x20 == null || LEFT_ARROW_ICON_16x16 == null ) {

            throw new HowDidWeGetHereError(
                    "CopierWidget:  " +
                    "unable to load one or both left arrow icons " +
                    "(left@16=" + LEFT_ARROW_ICON_16x16 + ", left@20=" + LEFT_ARROW_ICON_20x20 + ")"
            );

        }

    }

    private final CopierDataSource _source;
    private final WidgetPanel _widgetPanel;

    private boolean _finished = false;

    @SuppressWarnings("FieldCanBeLocal") private static final String s_copyValueButtonName = "Go_back_16x16";
    @SuppressWarnings("FieldCanBeLocal") private static final int s_copyValueButtonMarginSize = 3;
    private final JLabel _copyValueButton;

    private final ImageButton _copyValueImageButton;

    final String _copierName;

    public CopierWidget(
            final @NotNull String name,
            final @NotNull ObtuseTextElement currentValueElement,
            final @NotNull CopierDataSource ourSource,
            final boolean shouldCurrentValueBeVisible
    ) {
        super( name + "'s source", currentValueElement, false );

        _copierName = name;

        setCurrentValueVisible( shouldCurrentValueBeVisible );

        _source = ourSource;

        _widgetPanel = new WidgetPanel( getName(), getId(), WidgetPanel.Type.COPIER );

        _widgetPanel.add( new NotFinishedMarker() );

        // We are a source which means that we have a current value.
        // The representation of this current value is a ObtuseTextElement provided by the caller of this constructor.
        // Whether or not this representation is visible is simply not our concern.
        // Therefore, we make our current value representation the first thing in our widget panel.
        // That's the sum total of our concern regarding whether or not our current value's representation is visible.

        _widgetPanel.add( currentValueElement.getAsJComponent() );

        // Create our 'left arrow' copy button and put it into the widget panel.

        _copyValueButton = new JLabel();

        _copyValueImageButton = ImageButton.makeImageButton(
                this,
                name + "'s copy value button",
                _copyValueButton,
                this::copyValueButtonClicked,
                s_copyValueButtonName,
                ObtuseConstants.OBTUSE_RESOURCES_DIRECTORY,
                ImageButton.getDefaultDarkeningFactor()
        );

        Dimension size = _copyValueButton.getMinimumSize();
        _copyValueButton.setMinimumSize( new Dimension( size.width + s_copyValueButtonMarginSize * 2, size.height ) );
        _copyValueButton.setMaximumSize( new Dimension( size.width + s_copyValueButtonMarginSize * 2, size.height ) );

        _copyValueButton.setBorder(
                BorderFactory.createEmptyBorder( 0, s_copyValueButtonMarginSize, 0, s_copyValueButtonMarginSize )
        );

        _widgetPanel.add( _copyValueButton );

        // Add the representation of our source to our widget panel.

        _widgetPanel.add( _source.getOurRepresentation() );

    }

    public void setCopyButtonEnabled( final boolean enabled ) {

        _copyValueImageButton.setEnabled( enabled );

    }

    public CopierDataSource getSource() {

        return _source;

    }

    @SuppressWarnings("unused")
    public boolean isCopyButtonEnabled() {

        return _copyValueImageButton.isEnabled();

    }

    @SuppressWarnings("unused")
    @NotNull
    public String getCopierName() {

        return _copierName;

    }

    public WidgetPanel getWidgetPanel() {

        return _widgetPanel;

    }

    public JComponent getOurRepresentation() {

        return _widgetPanel.getDisplayContainer();

    }

    @Override
    public void setButtonStates() {

        // Nothing to be done here - copyValueButtonClicked() is called directly via 'action' Runnable
        // in call to ImageButton.makeImageButton elsewhere in this class.

        ObtuseUtil.doNothing();

    }

    @Override
    public void setCursor( final Cursor predefinedCursor ) {

        Component rootPane = _copyValueButton.getRootPane();
        rootPane.setCursor( predefinedCursor );

    }

    public void copyValueButtonClicked() {

        Logger.logMsg( "copy value button clicked" );

        String updateValue = _source.getCurrentValue();

        Logger.logMsg(
                "CopierWidget(" + getName() + "):  " +
                "replacing " + ObtuseUtil.enquoteToJavaString( getCurrentValue() ) +
                " with " + ObtuseUtil.enquoteToJavaString( updateValue )
        );

        setCurrentValue( updateValue );

    }

    public void setOwnerElsewhere( final @NotNull CopierDataSource.Owner owner ) {

        _source.setOwner( owner );

    }

    @NotNull
    public static CopierWidget createDualLayerCopierWidget(
            final @NotNull String baseName,
            final @NotNull String actualCopyInValue,
            final @NotNull ObtuseTextElement rhsSourceTextElement,
            final @NotNull String initialIntermediateValue,
            final @NotNull CopierBusinessLogic businessLogic
    ) {

        // Create a source for the RHS copier widget.

        CopierDataSource rhsSource = new CopierDataSource( "rhs", rhsSourceTextElement, false );

        // Create the RHS copier widget.
        // It will have its value updated with the current contents of the RHS's text element
        // when its (the RHS copier widget's) arrow button is clicked.

        CopierWidget rhsW = createCopierWidget(
                baseName + " rhs",
                actualCopyInValue,
                rhsSource,
                false
        );

        // Create a text label that will hold the most recent value to come out of the RHS copier widget.
        // This text label will remain invisible as it is only accessed when the RHS copier widget sends
        // the appropriate event.

        ObtuseTextElement.ObtuseLabel rhsFilteredCurrentValue = new ObtuseTextElement.ObtuseLabel(
                initialIntermediateValue
        );

        // Create a CopierDataSource which yields the current contents of the just created
        // text label when its (the CopierDataSource's) current value is requested.

        CopierDataSource rhsWidgetAsFilteredSource = new CopierDataSource(
                "rhsCopierFilter",
                rhsFilteredCurrentValue,
                false
        );

        // Make sure that the RHS copier widget gets finished when the newly created CopierDataSource
        // instance is finished.

        rhsWidgetAsFilteredSource.addFinishBuddy( rhsW );

        // Create the LHS copier widget.
        // It will have its value updated with the current contents of the just created CopierDataSource
        // whenever its (the about-to-be-created CopierWidget's) arrow button is pressed.

        CopierWidget lhsW = createCopierWidget(
                baseName + " lhs",
                "",
                rhsWidgetAsFilteredSource,
                false
        );

        // Mark the LHS copier widget's arrow as disabled.
        // It will be enabled during finishing if it turns out that the recently created CopierDataSource has a
        // non-empty value.

        lhsW.setCopyButtonEnabled( false );

        // Create an action listener for the RHS copier widget.
        // The action listener will will be triggered when the RHS copier widget's arrow button is clicked.
        // The action will treat the then current value of the RHS copier widget as a WikiTreeId which it will
        // use to fetch the profile corresponding to said WikiTreeId from the WikiTree server (assuming the
        // profile is not already in this JVM's cache).
        //
        // The visible text field that is between the LHS copier widget and the RHS copier widget will then
        // contain either the name of the person who's profile has just been fetched or an error message.

        rhsW.addCopierActionListener(
                baseName + " rhs CopierActionListener",
                makeMyActionListener(
                        lhsW,
                        rhsSource,
                        businessLogic
                )

        );

        // Append the RHS copier widget's GUI representation to the LHS copier widget's GUI representation.
        // This completes the visual appearance of the dual layer copier widget that we have been constructing.

        lhsW.getWidgetPanel().add( rhsW.getOurRepresentation() );

        // Our work here is now done.

        return lhsW;

    }

    public static CopierActionListener makeMyActionListener(
            final CopierWidget lhsWidget,
            final @NotNull CopierDataSource rhsSource,
            final CopierBusinessLogic businessLogic
    ) {

        return new CopierActionListener() {

            @Override
            protected void myActionPerformed( final @NotNull CopierDataSource dataSource ) {

                boolean worked = businessLogic.transmogrifyRhsValue( lhsWidget, rhsSource );

                Logger.logMsg( "update for " + dataSource.getName() + " " + ( worked ? "worked" : "failed" ) );

                ObtuseUtil.doNothing();

            }

        };

    }

    public static CopierWidget createCopierWidget(
            final @NotNull String name,
            final @NotNull String ourInitialValue,
            final CopierDataSource dataSource,
            final boolean shouldCurrentValueBeVisible
    ) {

        ObtuseTextElement.ObtuseLabel ourValue = new ObtuseTextElement.ObtuseLabel( ourInitialValue );

        CopierWidget rval = new CopierWidget(
                name,
                ourValue,
                dataSource,
                shouldCurrentValueBeVisible
        );

        return rval;

    }

    public String toString() {

        try {

            String enquotedName = ObtuseUtil.enquoteToJavaString( getName() );
            CopierDataSource.Owner owner = getOwner();
            String fieldName = owner == null ? "null(no owner)" : owner.getToStringName();

            return "CopierWidget( name=" +
                   enquotedName + ", field=" +
                   fieldName + " )";

        } catch ( RuntimeException e ) {

            e.printStackTrace();

            throw e;

        }

    }

    public void finish() {

        checkFinished( "finish", false );

        if ( _widgetPanel.getComponentCount() > 0 && _widgetPanel.getComponent( 0 ) instanceof NotFinishedMarker ) {

            _widgetPanel.remove( 0 );

            Logger.logMsg( "CopierWidget:  need to finish ourselves" );

            super.finish();

            _source.finish();

            _widgetPanel.finish();

            Logger.logMsg( "listeners:" );

            for ( ListenerInfo listenerInfo : getAllListeners() ) {

                Logger.logMsg( "    " + listenerInfo );

            }

            markFinished();

        } else {

            throw new HowDidWeGetHereError( "CopierWidget.finish:  no NotFinishedMarker" );

        }

    }

    public void checkFinished( final @NotNull String methodName, final boolean mustBeFinished ) {

        if ( mustBeFinished == _finished ) {

            return;

        }

        if ( mustBeFinished ) {

            throw new HowDidWeGetHereError( "CopierWidget." + methodName + ":  too early to call this method - not finished yet" );

        } else {

            throw new HowDidWeGetHereError( "CopierWidget." + methodName + "):  too late to call this method - already finished" );

        }

    }

    @SuppressWarnings("unused")
    public final boolean isFinished() {

        return _finished;

    }

    protected void markFinished() {

        checkFinished( "markFinished", false );

        _finished = true;

    }

}
