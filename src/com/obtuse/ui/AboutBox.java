package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.ImageIconUtils;
import com.obtuse.util.Logger;
import com.obtuse.util.OSLevelCustomizations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 A simple About box.
 */

@SuppressWarnings("ClassHasNoToStringMethod")
public class AboutBox extends JDialog {

    private static String[] s_resourcesDirectories = {
            "com/obtuse/ui/resources"
    };

    private JPanel _contentPane;
    private JButton _buttonOK;
    private JLabel _copyrightLabel;
    private JLabel _applicationNameLabel;
    private JLabel _interestingStuffLabel;
    private JPanel _imagePanel;
    private JPanel _interestingStuffPanel;
    private JPanel _copyrightNoticePanel;
    private JPanel _imagePanelBorder;

    private ImageIcon _imageIcon;

    // Sneak a copyright symbol past the Java compiler on Windows

    private static final char COPYRIGHT_SYMBOL = (char)169;
    private Point _lastMouseLocation = null;

    @SuppressWarnings({ "SameParameterValue" })
    public AboutBox( final @Nullable JFrame owner, boolean decorated ) {

        super( owner, true );

        finishInitialization( decorated );

    }

    @SuppressWarnings({ "SameParameterValue" })
    public AboutBox( final @Nullable Dialog owner, boolean modal, boolean decorated ) {

        super( owner, modal );

        finishInitialization( decorated );

    }

    private void finishInitialization( final boolean decorated ) {

        setContentPane( _contentPane );

        _imagePanel.setVisible( false );

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

        if ( !decorated ) {

            addMouseListener(
                    new MouseAdapter() {
                        @Override
                        public void mousePressed( final MouseEvent e ) {

                            _lastMouseLocation = e.getPoint();

                        }

                    }

            );

            addMouseMotionListener(
                    new MouseMotionAdapter() {
                        @Override
                        public void mouseDragged( final MouseEvent e ) {

                            setLocation( e.getXOnScreen() - _lastMouseLocation.x, e.getYOnScreen() - _lastMouseLocation.y );

                        }

                    }
            );

        }

        setInterestingStuff( "" );
        setCopyrightLabel( null );

        pack();

    }

    @SuppressWarnings("unused")
    public static void setResourcesDirectories( @Nullable String[] resourceDirectories ) {

        SortedSet<String> seenDirectories = new TreeSet<>();
        ArrayList<String> resDirs = new ArrayList<>();
        String defaultResourcesDirectory = "com/obtuse/ui/resources";
        resDirs.add( defaultResourcesDirectory );
        seenDirectories.add( defaultResourcesDirectory );

        if ( resourceDirectories != null ) {

            for ( String resourcesDirectory : resourceDirectories ) {

                if ( !seenDirectories.contains( resourcesDirectory ) ) {

                    resDirs.add( resourcesDirectory );
                    seenDirectories.add( resourcesDirectory );

                }

            }

        }

        s_resourcesDirectories = resDirs.toArray( new String[0] );

    }

    @SuppressWarnings("unused")
    public static String[] getResourcesDirectories() {

        return Arrays.copyOf( s_resourcesDirectories, s_resourcesDirectories.length );

    }

    public AboutBox setImageBorder( @Nullable Border border ) {

        _imagePanelBorder.setBorder( border );

        return this;

    }

    public AboutBox setImage( @Nullable ImageIcon imageIcon ) {

        if ( imageIcon == null ) {

            _imageIcon = null;
            _imagePanel.setVisible( false );
            _imagePanelBorder.setVisible( false );

            return this;

        }

        @Nullable ImageIcon scaledImageIcon = imageIcon;

        int iconWidth = imageIcon.getIconWidth();
        int iconHeight = imageIcon.getIconHeight();
        if ( iconWidth > 300 || iconHeight > 300 ) {
//        if ( iconWidth > iconHeight ) {
//
//            if ( iconWidth > 300 ) {

                scaledImageIcon = ImageIconUtils.getScaledImageIcon( 300, imageIcon );

//            }
//
//        } else {
//
//            if ( iconHeight > 300 ) {
//
//                scaledImageIcon = ImageIconUtils.getScaledImageIcon( 300, imageIcon );
//
//            }

        }

        _imageIcon = scaledImageIcon;
        _imagePanel.setPreferredSize( new Dimension( scaledImageIcon.getIconWidth(), scaledImageIcon.getIconHeight() ) );
        _imagePanel.setVisible( true );
        _imagePanelBorder.setVisible( true );

        pack();

        return this;

    }

    public AboutBox setCopyrightOwner( final @Nullable String years, final @NotNull String copyrightOwner ) {

        return setCopyrightLabel(
                "<html><small>Copyright " +
                AboutBox.COPYRIGHT_SYMBOL + " " +
                ( years == null || years.isEmpty() ? "" : ( years + " " ) ) +
                copyrightOwner + "</small></html>"
        );

    }

    public AboutBox setCopyrightLabel(final @Nullable String copyrightText ) {

        if ( copyrightText == null || copyrightText.isEmpty() ) {

            _copyrightLabel.setText( "" );
            _copyrightNoticePanel.setVisible( false );

        } else {

            _copyrightLabel.setText( copyrightText );
            _copyrightNoticePanel.setVisible( true );

        }

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

    public AboutBox setInterestingStuffLabel( final @Nullable String interestingStuffText ) {

        if ( interestingStuffText == null || interestingStuffText.isEmpty() ) {

            _interestingStuffLabel.setText( "" );

            _interestingStuffPanel.setVisible( false );

        } else {

            _interestingStuffLabel.setText( interestingStuffText );

            _interestingStuffPanel.setVisible( true );

        }

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
        jf.setMinimumSize( new Dimension( 200, 200 ) );
        jf.pack();
        jf.setVisible( true );

        ImageIcon imageIcon = ImageIconUtils.fetchIconImage(
//                "kodak_No_2_folding_autographic_brownie_transparent_background_400x400.png",
                "kodak_No_2_folding_autographic_brownie_transparent_background_400x400.png",
//                "kodak_No_3A_autographic_junior_1923_catalog_greyscale_600dpi.png",
                0,
                AboutBox.class.getClassLoader(),
                s_resourcesDirectories
        ).orElse( null );

        AboutBox dialog = new AboutBox( jf, false )
                .setApplicationName( "About Box Demo" )
                .setCopyrightOwner( "1867", "Example" )
                .setInterestingStuff( "Cool stuff!" )
                .setImage( imageIcon )
//                .setImageBorder( BorderFactory.createLineBorder( Color.GREEN ) )
                .setImageBorder( BorderFactory.createEtchedBorder() )
                ;

        OSLevelCustomizations.getCustomizer().setAboutWindowHandler(
                () -> dialog.setVisible( true )
        );

        Logger.logMsg( "we're back!" );

    }

    private void createUIComponents() {

        _imagePanel = new JPanel() {

            public void paint( Graphics g ) {

                if ( _imageIcon != null ) {

                    g.drawImage( _imageIcon.getImage(), 0, 0, this );

                }

            }

        };

    }
}