package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.OSLevelCustomizations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 A simple About box.
 */

@SuppressWarnings("ClassHasNoToStringMethod")
public class AboutBox extends JDialog {

    private JPanel _contentPane;
    private JButton _buttonOK;
    private JLabel _copyrightLabel;
    private JLabel _applicationNameLabel;
    private JLabel _interestingStuffLabel;

    // Sneak a copyright symbol past the Java compiler on Windows

    private static final char COPYRIGHT_SYMBOL = (char)169;

    @SuppressWarnings({ "SameParameterValue" })
    public AboutBox( final @Nullable JFrame owner, boolean decorated ) {

        super( owner, true );

        setContentPane( _contentPane );

        setUndecorated( false );
        setLocation( new Point( 50, 50 ) );

        getRootPane().setDefaultButton( _buttonOK );

        _buttonOK.addActionListener(
                e -> onOK()
        );

        // We have to set the copyright notice field's text here since there appears to be
        // no other way to reliably get the copyright symbol into the notice.

        _copyrightLabel.setText(
                "<html><small>Copyright " + AboutBox.COPYRIGHT_SYMBOL + " 1867 Someone or other</small></html>"
        );

        addWindowListener(
                new WindowAdapter() {

                    public void windowClosing( final WindowEvent e ) {

                        onCancel();

                    }

                }
        );

        _contentPane.registerKeyboardAction(
                e -> onCancel(),
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        setResizable( false );

        setUndecorated( !decorated );

        pack();

    }

    public AboutBox setCopyrightOwner( final @NotNull String years, final @NotNull String copyrightOwner ) {

        return setCopyrightLabel(
                "<html><small>Copyright " + AboutBox.COPYRIGHT_SYMBOL + " " + years + " " + copyrightOwner + "</small></html>"
        );

    }

    public AboutBox setCopyrightLabel(final @NotNull String copyrightLabel ) {

        _copyrightLabel.setText( copyrightLabel );
        return this;

    }

    public AboutBox setApplicationName( final @NotNull String applicationName ) {

        return setApplicationLabel(
                "<html><b>" + applicationName + "</b></html>"
        );

    }

    public AboutBox setApplicationLabel( final @NotNull String applicationLabel ) {

        _applicationNameLabel.setText( applicationLabel );

        return this;

    }

    public AboutBox setInterestingStuff( final @NotNull String interestingStuff ) {

        return setInterestingStuffLabel(
                "<html><b>" + interestingStuff + "</b></html>"
        );

    }

    public AboutBox setInterestingStuffLabel( final @NotNull String interestingStuffLabel ) {

        _interestingStuffLabel.setText( interestingStuffLabel );

        return this;

    }

    private void onOK() {

        dispose();

    }

    private void onCancel() {

        dispose();

    }

    public static void main( final String[] args ) {

        launch();

    }

    public static void launch() {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone","AboutBox" );

        JFrame jf = new JFrame( "test JFrame" );
        JPanel jp = new JPanel();
        JLabel jl = new JLabel( "Hi there" );
        jp.setLayout( new BorderLayout() );
        jp.add( jl );
        jf.setContentPane( jp );
        jf.pack();
        jf.setVisible( true );

        AboutBox dialog = new AboutBox( jf, false ).setApplicationName( "About Box Demo" ).setCopyrightOwner( "1867", "Example" ).setInterestingStuff( "Cool stuff!" );

        OSLevelCustomizations.getCustomizer().setAboutWindowHandler(
                () -> dialog.setVisible( true )
        );

        Logger.logMsg( "we're back!" );

    }

}