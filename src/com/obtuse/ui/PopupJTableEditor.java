package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 A JTable cell editor which launches a popup editor to perform the actual cell editing.
 <blockquote>
 This class is a relatively lightly edited copy of the answer by <a href="http://stackoverflow.com/users/131872/camickr">camickr</a> to a
 <a href="http://stackoverflow.com/questions/20590017">question</a>
 asked by <a href="http://stackoverflow.com/users/1373258/thomas-n">Thomas N.</a>
 <p/>The question and answer were found on the incredibly useful <a href="http://stackoverflow.com">Stack Overflow</a> website.
 This was the only answer to the question when the original version of this class was copied from the site on 2016-06-11.
 The original name of this class, as it appeared on the Stack Overflow site, was <code>TablePopupEditor</code>.
 <p/>User contributions on the site are licensed under <a href="https://creativecommons.org/licenses/by-sa/3.0/">cc-by-sa 3.0</a>.
 It is not clear to me (Daniel Boulet) who claims copyright to the answer that I copied to serve as the basis for this class.
 </blockquote>
 */

public class PopupJTableEditor extends DefaultCellEditor {

    private PopupDialog _popup;

    private String _currentText = "";

    private JTextField _editorComponent;

    public PopupJTableEditor( @NotNull final String popupWindowTitle ) {

        super( new JTextField() );

//	setClickCountToStart( 1 );

        //  Use a JButton as the editor component

        _editorComponent = new JTextField();
        _editorComponent.setBackground( Color.white );
//	_editorComponent.setBorderPainted( false );
//	_editorComponent.setContentAreaFilled( false );
        _editorComponent.setHorizontalAlignment( SwingConstants.LEFT );

        // Make sure focus goes back to the table when the dialog is closed
        _editorComponent.setFocusable( false );

        //  Set up the dialog where we do the actual editing

        _popup = new PopupDialog( popupWindowTitle );

    }

    public Object getCellEditorValue() {

        return _currentText;

    }

    public Component getTableCellEditorComponent( final JTable table, final Object value, final boolean isSelected, final int row, final int column ) {

        SwingUtilities.invokeLater(
                () -> {

                    _popup.setText( _currentText );
//              popup.setLocationRelativeTo( editorComponent );
                    Point p = _editorComponent.getLocationOnScreen();
                    _popup.setLocation( p.x, p.y + _editorComponent.getSize().height );
                    _popup.setVisible( true );
                    fireEditingStopped();

                }
        );

        _currentText = value.toString();
        _editorComponent.setText( _currentText );

        return _editorComponent;

    }

    /*
    *   Simple dialog containing the actual editing component
    */

    class PopupDialog extends JDialog implements ActionListener {

        public static final String OK_BUTTON_LABEL = "Ok";

        private JTextArea textArea;

        public PopupDialog( @NotNull final String popupWindowTitle ) {

            super( (Frame)null, popupWindowTitle, true );

            setMinimumSize( new Dimension( 400, 400 ) );

            textArea = new JTextArea( 5, 20 );
            textArea.setLineWrap( true );
            textArea.setWrapStyleWord( true );
            KeyStroke keyStroke = KeyStroke.getKeyStroke( "ENTER" );
            textArea.getInputMap().put( keyStroke, "none" );
            JScrollPane scrollPane = new JScrollPane( textArea );
            scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
            getContentPane().add( scrollPane );

            JButton cancel = new JButton( "Cancel" );
            cancel.addActionListener( this );
            JButton ok = new JButton( OK_BUTTON_LABEL );
            ok.setPreferredSize( cancel.getPreferredSize() );
            ok.addActionListener( this );

            JPanel buttons = new JPanel();
            buttons.add( ok );
            buttons.add( cancel );
            getContentPane().add( buttons, BorderLayout.SOUTH );
            pack();

            MyActionListener escListener = new MyActionListener() {

                @Override
                public void myActionPerformed( final ActionEvent e ) {

                    setVisible( false );

                }

            };

            getRootPane().registerKeyboardAction(
                    escListener,
                    KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                    JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            getRootPane().setDefaultButton( ok );
        }

        public void setText( final String text ) {

            textArea.setText( text );

        }

	/*
    *   Save the changed text before hiding the popup
	*/

        public void actionPerformed( final ActionEvent e ) {

            String actionCommand = e.getActionCommand();
            if ( OK_BUTTON_LABEL.equals( actionCommand ) ) {

                _currentText = textArea.getText();

            }

            textArea.requestFocusInWindow();
            setVisible( false );

        }

    }

    private static void createAndShowUI() {

        String[] columnNames = { "Item", "Description" };
        Object[][] data =
                {
                        { "Item 1", "Description of Item 1" },
                        { "Item 2", "Description of Item 2" },
                        { "Item 3", "Description of Item 3" }
                };

        JTable table = new JTable( data, columnNames );
        table.getColumnModel().getColumn( 0 ).setMinWidth( 400 );
        table.getColumnModel().getColumn( 1 ).setPreferredWidth( 300 );
        table.setPreferredScrollableViewportSize( table.getPreferredSize() );
        table.putClientProperty( "JTable.autoStartsEdit", Boolean.FALSE );
        JScrollPane scrollPane = new JScrollPane( table );

        // Use the popup editor on the second column

        PopupJTableEditor popupEditor = new PopupJTableEditor( "Hi There" );
        table.getColumnModel().getColumn( 1 ).setCellEditor( popupEditor );

        JFrame frame = new JFrame( "Popup Editor Test" );
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.add( new JTextField(), BorderLayout.NORTH );
        frame.add( scrollPane );
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );

    }

    public static void main( final String[] args ) {

        EventQueue.invokeLater(
                PopupJTableEditor::createAndShowUI
        );

    }

}