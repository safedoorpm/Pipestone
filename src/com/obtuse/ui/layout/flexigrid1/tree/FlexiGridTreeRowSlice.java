package com.obtuse.ui.layout.flexigrid1.tree;

import com.obtuse.ui.layout.flexigrid1.FlexiGridItemInfo;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridModelSlice;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridPanelModel;
import com.obtuse.ui.layout.flexigrid1.tree.testing.TestTreeRowSlice;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintsTable;
import com.obtuse.util.SimpleUniqueIntegerIdGenerator;
import com.obtuse.util.UniqueIntegerIdGenerator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 Created by danny on 2018/11/13.
 <p>This is looking like the wrong way to build a tree-of-nodes manager.</p>
 @deprecated
 */

@Deprecated
public class FlexiGridTreeRowSlice extends FlexiGridModelSlice {

    private static UniqueIntegerIdGenerator s_rowNameGenerator = new SimpleUniqueIntegerIdGenerator( "FlexiGridTreeRowSlice name generator" );

    //    public class FlexiGridTreeRowComponent extends  {
//
//        public FlexiGridTreeRowComponent() {
//            super();
//        }
//
//        public FlexiGridTreeRowComponent add( FlexiGridItemInfo  )
//    }
    private int _depth = 0;

    private final Box _box;

    private JPanel _indenter = new JPanel();

    private FlexiGridTreeRowSlice _parent;

    private List<FlexiGridTreeRowSlice> _children = new ArrayList<>();

    private final List<JComponent> _elements = new ArrayList<>();

    public FlexiGridTreeRowSlice( final int depth ) {
        super( "fgtr_" + s_rowNameGenerator.getUniqueId(), FlexiGridPanelModel.Orientation.ROW );

        _depth = depth;

        FlexiGridConstraintsTable constraintsTable = new FlexiGridConstraintsTable(
                new FlexiGridBasicConstraint( getName(), -1, 1 ).setHorizontalJustification( FlexiGridBasicConstraint.HJustification.LEFT )
        );

        _box = new Box( BoxLayout.X_AXIS );
        _indenter.setMinimumSize( new Dimension( 30 * depth, 5 ) );
        _indenter.setPreferredSize( new Dimension( 30 * depth, 5 ) );
        _indenter.setMaximumSize( new Dimension( 30 * depth, 5 ) );
        _indenter.setBackground( Color.RED );
        _box.add( _indenter );

//        setComponent(
//                0,
//                new FlexiGridItemInfo(
//                        getName() + "c1",
//                        -1,
//                        0,
////                        new JLabel( getName() + "c1" ),
//                        treeRowSlice,
//                        constraintsTable
//                )
//        );

    }

    public int getDepth() {

        return _depth;

    }

}
