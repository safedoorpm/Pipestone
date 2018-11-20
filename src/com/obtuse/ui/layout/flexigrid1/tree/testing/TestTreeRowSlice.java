package com.obtuse.ui.layout.flexigrid1.tree.testing;

import com.obtuse.ObtuseConstants;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridTreeNode;
import com.obtuse.util.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 Created by danny on 2018/11/13.
 */

@Deprecated
public class TestTreeRowSlice extends JPanel implements ImageButtonOwner {

    // Define the button image file names.

    /**
     The name of the grey (closed) folder.
     */

    private static final String s_folder_grey_20x20_name_no_suffix = "Closed_folder_grey_20x20";

    /**
     The name of the normal (opened) folder.
     */

    private static final String s_folder_normal_20x20_name_no_suffix = "Closed_folder_normal_20x20";

    /**
     The name of the highlighted folder which appears when the button is held down by the human.
     */

    private static final String s_folder_orange_20x20_name_no_suffix = "Closed_folder_orange_20x20";

    // Define the image icon fiels for the three folder appearances.

    /**
     The {@link ImageIcon} for the grey (closed) folder.
     */

    private static final ImageIcon s_folder_grey_20x20;

    /**
     The {@link ImageIcon} for the normal (opened) folder.
     */

    private static final ImageIcon s_folder_normal_20x20;

    /**
     The {@link ImageIcon} for the orange (highlight) folder.
     */

    private static final ImageIcon s_folder_orange_20x20;

    // Load the three image icons.

    static {

        s_folder_grey_20x20 = ImageIconUtils.fetchIconImage(
                s_folder_grey_20x20_name_no_suffix + ".png",
                0,
                ObtuseConstants.OBTUSE_RESOURCES_DIRECTORY
        );

        s_folder_normal_20x20 = ImageIconUtils.fetchIconImage(
                s_folder_normal_20x20_name_no_suffix + ".png",
                0,
                ObtuseConstants.OBTUSE_RESOURCES_DIRECTORY
        );

        s_folder_orange_20x20 = ImageIconUtils.fetchIconImage(
                s_folder_orange_20x20_name_no_suffix + ".png",
                0,
                ObtuseConstants.OBTUSE_RESOURCES_DIRECTORY
        );

    }

    @SuppressWarnings("FieldCanBeLocal") private static final int s_buttonMarginSize = 3;

    /**
     The {@link Box} that contains everything for this line/folder.
     */
    private final Box _box;

    /**
     The JPanel that provides the indentation space via appropriate setting of its min/max/pref sizes.
     */

    @SuppressWarnings("FieldCanBeLocal") private final JPanel _indenter = new JPanel();

    /**
     The {@link ImageButton} for the grey->normal (closed to opened) button.
     */

    @SuppressWarnings({ "FieldCanBeLocal", "unused" }) private final ImageButton _greyToNormalImageButton;

    /**
     The {@link ImageButton} for the normal->grey (opened to closed) button.
     */

    @SuppressWarnings({ "FieldCanBeLocal", "unused" }) private final ImageButton _normalToGreyImageButton;

    /**
     The {@link JLabel} which IS the grey->normal (closed to opened) button.
     */

    @SuppressWarnings("FieldCanBeLocal") private final JLabel _greyToNormalButton;

    /**
     The {@link JLabel} which IS the normal->grey (open to closed) button.
     */

    @SuppressWarnings("FieldCanBeLocal") private final JLabel _normalToGreyButton;

    /**
     The index of the grey->normal button in our {@link Box}.
     */

    private final int _greyToNormalButtonIx;

    /**
     The index of the normal->grey button in our {@link Box}.
     */

    private final int _normalToGreyButtonIx;

    /**
     Create a row in the tree.
     @param node the node that this slice describes.
     */

    public TestTreeRowSlice( @NotNull final FlexiGridTreeNode node ) {
        super();

        /*
         We use the {@link BorderLayout} manager to avoid having to screw around with strange layout manager nonsense.
         */

        setLayout( new BorderLayout() );

        /*
         Create the grey->normal (closed to opened) button.
         */

        _greyToNormalButton = new JLabel();
        _greyToNormalImageButton = ImageButton.makeImageButton(
                this,
                getName() + "'s grey->normal button",
                _greyToNormalButton,
                this::toggleOpenedButtonClicked,
                s_folder_grey_20x20,
                s_folder_orange_20x20
        );

        /*
         Create the normal->grey (opened to closed) button.
         */

        _normalToGreyButton = new JLabel();
        _normalToGreyImageButton = ImageButton.makeImageButton(
                this,
                getName() + "'s normal->grey button",
                _normalToGreyButton,
                this::toggleOpenedButtonClicked,
                s_folder_normal_20x20,
                s_folder_orange_20x20
        );

        /*
         Make sure that the grey->normal (closed to opened) button has a bit of padding around it.
         */

        Dimension size = _greyToNormalButton.getMinimumSize();
        _greyToNormalButton.setMinimumSize( new Dimension( size.width + s_buttonMarginSize * 2, size.height ) );
        _greyToNormalButton.setMaximumSize( new Dimension( size.width + s_buttonMarginSize * 2, size.height ) );
        _greyToNormalButton.setBorder(
                BorderFactory.createEmptyBorder( 0, s_buttonMarginSize, 0, s_buttonMarginSize )
        );

        /*
         Make sure that the normal->closed (opened to closed) button also has a bit of padding around it.
         */

        size = _normalToGreyButton.getMinimumSize();
        _normalToGreyButton.setMinimumSize( new Dimension( size.width + s_buttonMarginSize * 2, size.height ) );
        _normalToGreyButton.setMaximumSize( new Dimension( size.width + s_buttonMarginSize * 2, size.height ) );

        _normalToGreyButton.setBorder(
                BorderFactory.createEmptyBorder( 0, s_buttonMarginSize, 0, s_buttonMarginSize )
        );

        _greyToNormalButton.setVisible( true );
        _normalToGreyButton.setVisible( false );

        /*
         Create and fill in the {@link Box}.
         Remember where the two buttons land on the way by.
         */

        _box = new Box( BoxLayout.X_AXIS );
        int depth = 1;
        _indenter.setMinimumSize( new Dimension( 30 * depth, 5 ) );
        _indenter.setPreferredSize( new Dimension( 30 * depth, 5 ) );
        _indenter.setMaximumSize( new Dimension( 30 * depth, 5 ) );
        _indenter.setBackground( Color.RED );
        _box.add( _indenter );


        _greyToNormalButtonIx = _box.getComponentCount();
        _box.add( _greyToNormalButton );

        _normalToGreyButtonIx = _box.getComponentCount();
        _box.add( _normalToGreyButton );

        _box.add( new JCheckBox());
        JLabel jLabel = new JLabel( NounsList.pickNoun() );
        jLabel.setBorder( BorderFactory.createEtchedBorder() );
        _box.add( jLabel );

        add( _box, BorderLayout.CENTER );

    }

    private void toggleOpened() {

        Logger.logMsg( "doing " + this );

        boolean isOpened = isOpened();
        _box.getComponent( _greyToNormalButtonIx ).setVisible( isOpened );
        _box.getComponent( _normalToGreyButtonIx ).setVisible( !isOpened );

        Logger.logMsg( "folder is " + ( isOpened() ? "opened" : "closed" ) );

    }

    public boolean isOpened() {

        Component button = _box.getComponent( _greyToNormalButtonIx );
        return !button.isVisible();

    }

    public void toggleOpenedButtonClicked() {

        Logger.logMsg(
                ( _box.getComponent( _greyToNormalButtonIx ).isVisible() ? "grey->normal" : "normal->grey" ) + " button clicked"
        );

        toggleOpened();

//        Logger.logMsg(
//                "CopierWidget(" + getName() + "):  " +
//                "replacing " + ObtuseUtil.enquoteToJavaString( getCurrentValue() ) +
//                " with " + ObtuseUtil.enquoteToJavaString( updateValue )
//        );

    }

//    public void normalToGreyButtonClicked() {
//
//        Logger.logMsg( "normal->grey button clicked" );
//
//        toggleOpened();
//
////        Logger.logMsg(
////                "CopierWidget(" + getName() + "):  " +
////                "replacing " + ObtuseUtil.enquoteToJavaString( getCurrentValue() ) +
////                " with " + ObtuseUtil.enquoteToJavaString( updateValue )
////        );
//
//    }

    @Override
    public void setButtonStates() {

        // Nothing to be done here - copyValueButtonClicked() is called directly via 'action' Runnable
        // in call to ImageButton.makeImageButton elsewhere in this class.

        ObtuseUtil.doNothing();

    }

    public String toString() {

        return "TestTreeRowSlice( name=" + getName() + ", state=" + ( isOpened() ? "opened" : "closed" ) + " )";

    }

}
