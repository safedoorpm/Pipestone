/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridDivider;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridPanelModel;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintsTable;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridSliceSizes;
import com.obtuse.ui.layout.linear.LinearLayoutUtil;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.SimpleUniqueLongIdGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.function.Function;

/**
 Manage the actual layout management process.
 <p>An instance of this class is created on demand by a {@link FlexiGridLayoutManager} instance.
 The layout manager instance discards and creates a new instance of this class when the previous instance becomes obsolete
 which happens pretty much whenever anything changes with respect to the configuration of the layout manager.</p>
 */

public class FlexiGridCache1 implements FlexiGridLayoutManagerCache {

    private static final SimpleUniqueLongIdGenerator s_idGenerator =
            new SimpleUniqueLongIdGenerator( FlexiGridCache1.class.getCanonicalName() + " - entity id generator" );

    private final long _serialNumber = s_idGenerator.getUniqueId();

    private final FlexiGridLayoutManager _flexiGridLayoutManager;

    private final FlexiGridContainer1 _target;

    private final String _name;

    private final GridArray<FlexiGridItemInfo> _grid;

    private final SortedMap<Integer, FlexiGridSliceSizes> _columnSizes;
    private final SortedMap<Integer, FlexiGridSliceSizes> _rowSizes;

    private SortedMap<Integer,Integer> _xLocs;
    private SortedMap<Integer,Integer> _xWidths;
    private SortedMap<Integer,Integer> _yLocs;
    private SortedMap<Integer,Integer> _yHeights;

    private int _totalWidthLessInsets;
    private int _totalHeightLessInsets;
    private boolean _haveTotalWidthAndHeight = false;

    private final Vector<Component> _visibleComponents;

    public FlexiGridCache1(
            final @NotNull String name,
            final FlexiGridLayoutManager flexiGridLayoutManager,
            final @NotNull FlexiGridContainer1 target,
            Hashtable<Component, FlexiGridConstraintsTable> allConstraints,
            FlexiGridItemInfo.FlexiItemInfoFactory itemInfoFactory
    ) {

        super();

        // This has to happen before any logging is attempted.

        _flexiGridLayoutManager = flexiGridLayoutManager;

        logMaybe( "================================= creating FlexiGridCache1" );

        _name = name;

        _grid = new GridArray<>( name );

        _target = target;

        loadGrid( target, allConstraints, itemInfoFactory );

        // Get the column sizes.

        _visibleComponents = new Vector<>();
        _columnSizes = collectSizes(
                _grid.getRowSet(),
                row -> _grid.getRow( row ),
                dim -> dim.width,
                _visibleComponents
        );

        // Get the row sizes.

        _rowSizes = collectSizes(
                _grid.getColumnSet(),
                row -> _grid.getColumn( row ),
                dim -> dim.height,
                null
        );

        computePositions();

        ObtuseUtil.doNothing();

    }

    private void logMaybe( final String msg ) {

        _flexiGridLayoutManager.logMaybe( msg );

    }

    @Override
    public long getSerialNumber() {

        return _serialNumber;

    }

    @Override
    @NotNull
    public GridArray<FlexiGridItemInfo> getGrid() {

        return _grid;

    }

    @NotNull
    private SortedMap<Integer,FlexiGridSliceSizes> collectSizes(
            final @NotNull SortedSet<Integer> indexSet,
            final @NotNull Function<Integer, SortedMap<Integer, FlexiGridItemInfo>> getSlice,
            final @NotNull Function<Dimension, Integer> getValue,
            final @Nullable Vector<Component> visibleComponents
    ) {

        SortedMap<Integer,FlexiGridSliceSizes> sizes = new TreeMap<>();
        for ( Integer i1 : indexSet ) {

            SortedMap<Integer, FlexiGridItemInfo> elementsMap = getSlice.apply( i1 );
            for ( int i2 : elementsMap.keySet() ) {

                FlexiGridItemInfo ii = elementsMap.get( i2 );

                if ( ii.component().isVisible() ) {

                    if ( visibleComponents != null ) {

                        visibleComponents.add( ii.component() );

                    }

                    FlexiGridSliceSizes sliceSizes = sizes.computeIfAbsent( i2, integer -> new FlexiGridSliceSizes() );

                    Dimension minDim = ii.component().getMinimumSize();
                    Dimension prefDim = ii.component().getPreferredSize();
                    Dimension maxDim = ii.component().getMaximumSize();

                    sliceSizes.consume(
                            ii,
                            getValue.apply( minDim ).intValue(),
                            getValue.apply( prefDim ).intValue(),
                            getValue.apply( maxDim ).intValue()
                    );

                } else {

                    logMaybe( "FlexiGridCache1.collectSizes:  ignoring invisible component " + ii.component() );

                }

                ObtuseUtil.doNothing();

            }


            ObtuseUtil.doNothing();

        }

        return sizes;

    }

    /**
    Validate the components and load them into our {@link GridArray}.
    <p>It is currently impossible to add a component without constraints given how {@link FlexiGridLayoutManager#addLayoutComponent} is implemented.
    This code is here in case that code changes.</p>
    */

    private void loadGrid(
            final @NotNull FlexiGridContainer1 target,
            final Hashtable<Component, FlexiGridConstraintsTable> allConstraints,
            final FlexiGridItemInfo.FlexiItemInfoFactory itemInfoFactory
    ) {

        @NotNull Optional<FlexiGridPanelModel> optModel = _flexiGridLayoutManager.getFlexiGridPanelModel();
        optModel.ifPresent( flexiGridPanelModel -> flexiGridPanelModel.renumber( "FlexiGridCache1.loadGrid", false ) );

        for ( Component c : target.getComponents() ) {

            FlexiGridConstraintsTable constraint = allConstraints.get( c );
            if ( constraint == null ) {

                throw new IllegalArgumentException( "FlexiGridCache1.loadGrid:  all components must have been added with constraints, the one named " + ObtuseUtil.enquoteToJavaString( c.getName() ) + " was added without any constraints" );

            }

        }

        for ( Component component : target.getComponents() ) {

            if ( component.isVisible() ) {

                FlexiGridBasicConstraint bc = _flexiGridLayoutManager.getMandatoryBasicConstraint( component );

                Logger.logMsg( "constraint for " + LinearLayoutUtil.describeComponent( component ) + " is " + bc );

                FlexiGridConstraintsTable constraintsTable = allConstraints.get( component );
                FlexiGridItemInfo ii = itemInfoFactory.createInstance( bc.getRow(), bc.getCol(), component, constraintsTable );
                _grid.put( ii, false );

                ObtuseUtil.doNothing();

            }

        }

        ObtuseUtil.doNothing();

    }

    @NotNull
    public FlexiGridContainer getTarget() {

        return _target;

    }

    public int getVisibleComponentCount() {

        return _visibleComponents.size();

    }

    public Component getVisibleComponent( final int ix ) {

        return _visibleComponents.get( ix );

    }

    public Dimension getMinimumSize() {

        if ( !_haveTotalWidthAndHeight ) {

            throw new IllegalArgumentException( "FlexiGridCache1.getMinimumSize:  minimum size not yet available" );

        }

        Dimension size = new Dimension( _totalWidthLessInsets, _totalHeightLessInsets );

        addInsets( size );

        return size;

    }

    public Dimension getPreferredSize() {

        if ( !_haveTotalWidthAndHeight ) {

            throw new IllegalArgumentException( "FlexiGridCache1.getPreferredSize:  preferred size not yet available" );

        }

        Dimension size = new Dimension( _totalWidthLessInsets, _totalHeightLessInsets );

        addInsets( size );

        return size;

    }

    public Dimension getMaximumSize() {

        if ( !_haveTotalWidthAndHeight ) {

            throw new IllegalArgumentException( "FlexiGridCache1.getMaximumSize:  maximum size not yet available" );

        }

        Dimension size = new Dimension( Short.MAX_VALUE, Short.MAX_VALUE );

        return size;

    }

    public void addInsets( final Dimension size ) {

        Insets insets = _target.getInsets();

        size.width = (int)Math.min( (long)size.width + (long)insets.left + (long)insets.right, Integer.MAX_VALUE );
        size.height = (int)Math.min( (long)size.height + (long)insets.top + (long)insets.bottom, Integer.MAX_VALUE );

    }

    public float getLayoutAlignmentX() {

        @SuppressWarnings("UnnecessaryLocalVariable")
        float alignmentX = 0f;

        return alignmentX;

    }

    public float getLayoutAlignmentY() {

        @SuppressWarnings("UnnecessaryLocalVariable")
        float alignmentY = 0f;

        return alignmentY;

    }

    public void computePositions() {

        _xLocs = new TreeMap<>();
        _xWidths = new TreeMap<>();
        _yLocs = new TreeMap<>();
        _yHeights = new TreeMap<>();
        _totalWidthLessInsets = computeLocationsAndSizes(
                0,
                _xLocs,
                _xWidths,
                _grid.getColumnSet(),
                _columnSizes,
                true
        );

        _totalHeightLessInsets = computeLocationsAndSizes(
                0,
                _yLocs,
                _yHeights,
                _grid.getRowSet(),
                _rowSizes,
                false
        );

        _haveTotalWidthAndHeight = true;

        // Go through the columns that exist.

        for ( int col : _grid.getColumnSet() ) {

            // Get the column's min, pref and max sizes.

            FlexiGridSliceSizes columnSizes = _columnSizes.get( col );
            if ( columnSizes == null ) {

                logMaybe( "FlexiGridCache1.computePositions:  no _columnSizes @ " + col );

            }

            for ( int row : _grid.getRowSet() ) {

                FlexiGridSliceSizes rowSizes = _rowSizes.get( row );
                if ( rowSizes == null ) {

                    logMaybe( "FlexiGridCache1.computePositions:  no _rowSizes @ " + row );

                }

            }

        }

        ObtuseUtil.doNothing();

    }

    private int computeLocationsAndSizes(
            @SuppressWarnings("SameParameterValue") int startOffset,
            final SortedMap<Integer,Integer> locations,
            final SortedMap<Integer,Integer> sizes,
            final SortedSet<Integer> indexSet,
            final SortedMap<Integer, FlexiGridSliceSizes> sliceSizesMap,
            final boolean horizontal
    ) {

        String direction = horizontal ? "horizontal" : "vertical";

        for ( int ix : indexSet ) {

            FlexiGridSliceSizes minPrefMaxSizes = sliceSizesMap.get( ix );

//            logMaybe( "FlexiGridCache1.computeLocationsAndSizes(" + direction + "): " + minPrefMaxSizes );

            //noinspection StatementWithEmptyBody
            if ( minPrefMaxSizes == null ) {

//                logMaybe( "FlexiGridCache1.computeLocationsAndSizes(" + direction + "):  no minPrefMaxSizes for ix=" + ix );

            } else {

                locations.put( ix, startOffset );
                sizes.put( ix, minPrefMaxSizes.pref() );
                Insets margins = minPrefMaxSizes.getItemInfo()
                                                .getInfo()
                                                .getMargins();
                startOffset += minPrefMaxSizes.pref() +
                               (
                                       horizontal
                                               ?
                                               margins.left + margins.right
                                               :
                                               margins.top + margins.bottom
                               );

            }

        }

        return startOffset;

    }

    public void setComponentBounds() {

        Insets insets = _target.getInsets();

        SortedSet<Integer> rowSet = _grid.getRowSet();
        SortedSet<Integer> columnSet = _grid.getColumnSet();

        for ( int row : rowSet ) {

            SortedMap<Integer, FlexiGridItemInfo> rowMap = _grid.getRow( row );

            Integer xNominalHeight = _yHeights.get( row );
            if ( xNominalHeight == null ) {

                throw new HowDidWeGetHereError( "FlexiGridCache1.setComponentBounds:  nominal height is null" );

            }

            int nominalHeight = xNominalHeight.intValue();

            int nominalY = _yLocs.get( row ).intValue();

            for ( FlexiGridItemInfo element : rowMap.values() ) {

                int col = element.column();

                SortedMap<Integer, FlexiGridItemInfo> columnMap = _grid.getColumn( col );
                if ( columnMap == null ) {

                    throw new HowDidWeGetHereError( "FlexiGridCache1.setComponentBounds:  no column map for column " + col );

                }

                Component component = element.component();
                Dimension preferredSize = component.getPreferredSize();
                int preferredWidth = preferredSize.width;
                int preferredHeight = preferredSize.height;

                Integer xNominalWidth = _xWidths.get( col );
                if ( xNominalWidth == null ) {

                    throw new HowDidWeGetHereError( "FlexiGridCache1.setComponentBounds:  nominal width is null" );

                }

                int nominalWidth = xNominalWidth.intValue();

                int nominalX = _xLocs.get( col ).intValue();

                FlexiGridConstraintsTable constraintsTable = element.getInfo();

                // If we're dealing with a divider then pretend that its preferred width/height is the nominal width/height of its orientation.
                // For example, if it is row-oriented then its preferred width is forced to be the planned width for this column
                // but if it is column-oriented then its preferred height is forced to be the planned height for this row.

                boolean isFullWidth = false;
                boolean isFullHeight = false;

                Rectangle boundingRectangle = null;
                if ( component instanceof FlexiGridDivider  ) {

                    FlexiGridDivider divider = (FlexiGridDivider)component;
                    if ( divider.getOrientation().isRowOrientation() ) {

                        // Row oriented full length dividers must be alone in their row.

                        if ( ObtuseUtil.always() && divider.isFullLength() ) {

                            if ( rowMap.values().size() > 1 ) {

                                throw new IllegalArgumentException( "FlexiGridCache1.setComponentBounds:  row-oriented divider @row=" + row + " is not alone in its row" );

                            }

                            isFullWidth = true;
                            preferredWidth = _totalWidthLessInsets - 2;
                            preferredHeight = divider.getPreferredSize().height;
                            boundingRectangle = new Rectangle(
                                    insets.left + 1,
                                    insets.top + nominalY + element.getInfo().getMargins().top,
                                    preferredWidth,
                                    preferredHeight
                            );

                            ObtuseUtil.doNothing();

                        } else {

                            preferredWidth = nominalWidth;

                        }

                    } else {

                        // Column-oriented full length dividers must be alone in their column.

                        if ( ObtuseUtil.always() && divider.isFullLength() ) {

                            if ( columnMap.values().size() > 1 ) {

                                throw new IllegalArgumentException( "FlexiGridCache1.setComponentBounds:  column-oriented divider @column=" + col + " is not alone in its column" );

                            }

                            isFullHeight = true;
                            preferredHeight = _totalHeightLessInsets - 2;
                            preferredWidth = divider.getPreferredSize().width;

                            boundingRectangle = new Rectangle(
                                    insets.left + nominalX + element.getInfo().getMargins().left,
                                    insets.top + 1,
                                    preferredWidth,
                                    preferredHeight
                            );

                            ObtuseUtil.doNothing();

                        } else {

                            preferredHeight = nominalHeight;

                        }

                    }

                }

                int extraHSpace = nominalWidth - preferredWidth;
                if ( extraHSpace > nominalWidth ) {

                    extraHSpace = nominalWidth;

                } else if ( extraHSpace > 0 ) {

                    ObtuseUtil.doNothing();

                }

                int leftIndent = 0;

                switch ( constraintsTable.getHorizontalJustification() ) {

                    case LEFT:

                        // nothing to be done.

                        break;

                    case CENTER:

                        leftIndent = extraHSpace / 2;

                        break;

                    case RIGHT:

                        leftIndent = extraHSpace;

                        break;

                }

                int extraVSpace = nominalHeight - preferredHeight;
                if ( extraVSpace > nominalHeight ) {

                    extraVSpace = nominalHeight;

                } else if ( extraVSpace > 0 ) {

                    ObtuseUtil.doNothing();

                }

                int topIndent = 0;

                switch ( constraintsTable.getVerticalJustification() ) {

                    case TOP:

                        // nothing to be done.

                        break;

                    case CENTER:

                        topIndent = extraVSpace / 2;

                        break;

                    case BOTTOM:

                        topIndent = extraVSpace;

                        break;

                }

                if ( isFullWidth ) {

//                    Logger.logMsg(
//                            "FlexiGridCache1.setComponentBounds(" + row + "," + col + "):  " +
//                            "full width yields bb=" + ObtuseUtil.fBounds( boundingRectangle )
//                    );

                    ObtuseUtil.doNothing();

                } else if ( isFullHeight ) {

//                    Logger.logMsg(
//                            "FlexiGridCache1.setComponentBounds(" + row + "," + col + "):  " +
//                            "full height yields bb=" + ObtuseUtil.fBounds( boundingRectangle )
//                    );

                    ObtuseUtil.doNothing();

                } else {

                    boundingRectangle = new Rectangle(
                            (int)Math.min(
                                    ( (long)insets.left + leftIndent + nominalX + element.getInfo().getMargins().left ),
                                    Integer.MAX_VALUE
                            ),
                            (int)Math.min(
                                    ( (long)insets.top + topIndent + nominalY + element.getInfo().getMargins().top ),
                                    Integer.MAX_VALUE
                            ),
                            Math.min(
                                    nominalWidth,
                                    preferredWidth
                            ),
                            Math.min(
                                    nominalHeight,
                                    preferredHeight
                            )
                    );

//                    Logger.logMsg(
//                            "FlexiGridCache1.setComponentBounds(" + row + "," + col + "):  " +
//                            "normal height yields bb=" + ObtuseUtil.fBounds( boundingRectangle )
//                    );

                    ObtuseUtil.doNothing();

                }

                //                logMaybe( "component " + element + " @ [" + element.row() + "," + element.column() + "] has bounding box " + ObtuseUtil.fBounds( boundingRectangle ) );

                element.component().setBounds( boundingRectangle );

            }

        }

        ObtuseUtil.doNothing();

    }

    public String toString() {

        return "FlexiGridCache1( target name=" + _target.getName() + " )";

    }

    public FlexiGridLayoutManager getFlexiGridLayoutManager() {

        return _flexiGridLayoutManager;

    }

    public String getName() {

        return _name;
    }
}
