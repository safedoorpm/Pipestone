package com.obtuse.ui.selectors;

import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 A {@link WrappedComboBoxSelectorPanel} that is actually a wrapped {@link JPanel} containing a {@link JLabel}.
 */

public class WrappedLabelPanel extends WrappedComboBoxSelectorPanel<String,JPanel> {

    private final JLabel _jLabel;
    private final CController<String, JPanel> _choiceController;

    public WrappedLabelPanel( final @NotNull JLabel jLabel ) {
        super( makeWrappedPanel( jLabel ) );

        _jLabel = jLabel;
        _choiceController = null;

    }

    public WrappedLabelPanel( final @NotNull String text ) {
        this( new JLabel( text ) );
    }

    public WrappedLabelPanel( final @NotNull CController<String,JPanel> choiceController ) {
        super( choiceController );

        _jLabel = null;
        _choiceController = choiceController;

    }

    private static JPanel makeWrappedPanel( final @NotNull JLabel jLabel ) {

        JPanel jPanel = new JPanel();
        jPanel.setLayout( new BorderLayout() );
        jPanel.add( jLabel, BorderLayout.CENTER );

        return jPanel;

    }

    public void setChoice( final @NotNull String text ) {

        if ( isActualSelector() ) {

            notifyCurrentChildChange( null, text );

        } else {

            _jLabel.setText( text );

        }

    }

    public String getChoice() {

        if ( isLabel() ) {

            return _jLabel.getText();

        } else {

            return getCurrentChoice();

        }

    }

    public boolean isLabel() {

        return _jLabel != null;

    }

    public boolean isActualSelector() {

        return _jLabel == null;

    }

    public JLabel getLabel() {

        return _jLabel;

    }

    public String toString() {

        return "WrappedLabelPanel( labelText=" + ObtuseUtil.enquoteJavaObject( getChoice() ) + " )";

    }

}
