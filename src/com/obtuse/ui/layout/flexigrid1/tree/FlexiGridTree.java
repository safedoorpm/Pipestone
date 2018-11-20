package com.obtuse.ui.layout.flexigrid1.tree;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridPanelModel;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridTreeNode;
import com.obtuse.ui.layout.flexigrid1.tree.testing.TestTreeRowSlice;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.NamedEntity;
import com.obtuse.util.ObtuseCollections;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 Created by danny on 2018/09/02.
 <p>This is looking like the wrong way to build a tree-of-nodes manager.</p>
 @deprecated
 */

@Deprecated
public class FlexiGridTree extends JPanel implements NamedEntity {

//    private final JFrame _imageViewerWindow;
//    private final LancotLibraryRoot _mediaLibrary;

//    private final int _imagesPerRow;
//    private int _currentRow = -1;
//    private int _currentColumn = 0;
//    private ImageSlice _slice = null;

//    public static FlexiGridTree s_latestViewerPanel;

//    private class ImageSlice extends FlexiGridModelSlice {
//
//        public ImageSlice(
//                final @NotNull String name
//        ) {
//            super( name, FlexiGridPanelModel.Orientation.ROW );
//
//        }
//
//    }

    private JPanel _scrollablePanel;
    private final FlexiGridTreeNode _rootNode;
    private FlexiGridPanelModel<FlexiGridTreeRowSlice> _flexiGridModel;

    public FlexiGridTree(
            @NotNull final FlexiGridTreeNode rootNode
    ) {
        super();

        _rootNode = rootNode;

        _flexiGridModel = new FlexiGridPanelModel<>( "FlexiGridTree model", FlexiGridPanelModel.Orientation.ROW, false, false );
        Container container = _flexiGridModel.getFlexiGridContainer().getAsContainer();
        if ( container instanceof JPanel ) {

            _scrollablePanel = (JPanel)container;

        } else {

            throw new HowDidWeGetHereError( "FlexiGridTree:  FlexiGridPanelModel didn't contain a JPanel" );

        }

//        setLayout( new BorderLayout() );
//        add( _scrollablePanel, BorderLayout.CENTER );
//
//        _flexiGridModel.clear();
//        _flexiGridModel.add( rootNode );
//
//        for ( FlexiGridTreeRowSlice row : rootNode ) {
//
//            _flexiGridModel.add( row );
//
//        }

    }

//    public void addTestImage( @NotNull LancotMediaItem lmi, @NotNull final Image image ) {
//
//        if ( _slice == null || _currentColumn > _imagesPerRow ) {
//
//            _currentRow += 1;
//            _slice = new ImageSlice( "slice row " + _currentRow );
//            _currentColumn = 0;
//            _flexiGridModel.add( _slice );
//
//        } else {
//
//            _currentColumn += 1;
//
//        }
//
//        _slice.setComponent(
//                _currentColumn,
//                new MediaItemJPanel( lmi, image ),
//                FlexiGridBasicConstraint.HJustification.CENTER,
//                FlexiGridBasicConstraint.VJustification.CENTER
//        );
//
//    }

//    public static void main( String[] args ) {
//
//        BasicProgramConfigInfo.init( "Kenosee", "FlexiGridTree", "testing", null );
//        JFrame jf = new JFrame( "FlexiGridTree Test" );
//        ArrayList<FlexiGridTreeRowSlice> treeRows = new ArrayList<>(
//                ObtuseCollections.arrayList(
//                        new FlexiGridTreeRowSlice( new TestTreeRowSlice( 0 ) ),
//                        new FlexiGridTreeRowSlice( new TestTreeRowSlice( 1 ) ),
//                        new FlexiGridTreeRowSlice( new TestTreeRowSlice( 2 ) ),
//                        new FlexiGridTreeRowSlice( new TestTreeRowSlice( 3 ) )
//                )
//        );
//
//        FlexiGridTreeNode rootNode = new DefaultFlexiGridTreeNode( "root node", null, true ) {
//            @Override
//            public Component getGuiElements() {
//
//                return new JLabel( getName() );
//
//            }
//
//        };
//        FlexiGridTree fgt = new FlexiGridTree(
//                rootNode
//        );
//
//        JPanel jp = new JPanel();
//        jp.setLayout( new BorderLayout() );
//
//        JScrollPane scrollPane = new JScrollPane( fgt );
//        scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
//        scrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
//        jp.add( scrollPane, BorderLayout.CENTER );
//        Box buttons = new Box( BoxLayout.X_AXIS );
//        JButton addButton = new JButton( "add top child" );
//        buttons.add( addButton );
//        jp.add( buttons, BorderLayout.SOUTH );
//        jf.setContentPane( scrollPane );
//        jf.setMinimumSize( new Dimension( 500, 300 ) );
//        jf.setPreferredSize( new Dimension( 500, 300 ) );
//
//        jf.pack();
//        jf.setVisible( true );
//
//    }

}
