/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintsTable;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridSliceSizes;
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

        _name = name;

        _grid = new GridArray<>( name );

        _flexiGridLayoutManager = flexiGridLayoutManager;

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

//                    } else {
//
//                        Logger.logMsg( "no visible components instance provided" );

                    }

                    FlexiGridSliceSizes sliceSizes = sizes.computeIfAbsent( i2, integer -> new FlexiGridSliceSizes() );

                    Dimension minDim = ii.component().getMinimumSize();
                    Dimension prefDim = ii.component().getPreferredSize();
                    Dimension maxDim = ii.component().getMaximumSize();

                    sliceSizes.consume( ii, getValue.apply( minDim ), getValue.apply( prefDim ), getValue.apply( maxDim ) );

                } else {

                    Logger.logMsg( "FlexiGridCache1.collectSizes:  ignoring invisible component " + ii.component() );

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

        for ( Component c : target.getComponents() ) {

            FlexiGridConstraintsTable constraint = allConstraints.get( c );
            if ( constraint == null ) {

                throw new IllegalArgumentException( "FlexiGridCache1.loadGrid:  all components must have been added with constraints, the one named " + ObtuseUtil.enquoteToJavaString( c.getName() ) + " was added without any constraints" );

            }

        }

        for ( Component component : target.getComponents() ) {

//            FlexiGridConstraint constraint = constraintsTable.get( FlexiGridConstraintCategory.BASIC );
//            if ( constraint instanceof FlexiGridBasicConstraint ) {

            if ( component.isVisible() ) {

                FlexiGridBasicConstraint bc = _flexiGridLayoutManager.getMandatoryBasicConstraint( component );
//                    FlexiGridBasicConstraint bc = (FlexiGridBasicConstraint)constraint;

//                Logger.logMsg( "FlexiGridCache1.loadGrid:  remembering " + bc.locationString() );

                FlexiGridConstraintsTable constraintsTable = allConstraints.get( component );
                FlexiGridItemInfo ii = itemInfoFactory.createInstance( bc.getRow(), bc.getCol(), component, constraintsTable );
                _grid.put( ii, false );

//            } else {
//
//                Logger.logMsg( "FlexiGridCache1.loadGrid:  ignoring invisible component " + component );

            }

//            } else {
//
//                throw new IllegalArgumentException( "FlexiGridCache1.loadGrid:  component " + component.getName() + " does not have the mandatory FlexiGridBasicConstraint" );
//
//            }

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

    private void addInsets( final Dimension size ) {

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

//        Insets insets = _target.getInsets();

        _xLocs = new TreeMap<>(); // int[_grid.getDimension().width];
        _xWidths = new TreeMap<>(); // new int[_grid.getDimension().width];
        _yLocs = new TreeMap<>(); // new int[_grid.getDimension().height];
        _yHeights = new TreeMap<>(); // new int[_grid.getDimension().height];
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

        // Go through the columns that exist

        for ( int col : _grid.getColumnSet() ) {

            // Get the column's min, pref and max sizes.

            FlexiGridSliceSizes columnSizes = _columnSizes.get( col );
            if ( columnSizes == null ) {

                Logger.logMsg( "FlexiGridCache1.computePositions:  no _columnSizes @ " + col );

            }

            for ( int row : _grid.getRowSet() ) {

                FlexiGridSliceSizes rowSizes = _rowSizes.get( row );
                if ( rowSizes == null ) {

                    Logger.logMsg( "FlexiGridCache1.computePositions:  no _rowSizes @ " + row );

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
            boolean horizontal
    ) {

        String direction = horizontal ? "horizontal" : "vertical";

        for ( int ix : indexSet ) {

            FlexiGridSliceSizes minPrefMaxSizes = sliceSizesMap.get( ix );

//            Logger.logMsg( "FlexiGridCache1.computeLocationsAndSizes(" + direction + "): " + minPrefMaxSizes );

            //noinspection StatementWithEmptyBody
            if ( minPrefMaxSizes == null ) {

//                Logger.logMsg( "FlexiGridCache1.computeLocationsAndSizes(" + direction + "):  no minPrefMaxSizes for ix=" + ix );

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

        for ( int row : _grid.getRowSet() ) {

            SortedMap<Integer, FlexiGridItemInfo> rowMap = _grid.getRow( row );

            Integer nominalHeight = _yHeights.get( row );
            if ( nominalHeight == null ) {

                Logger.logMsg( "FlexiGridCache1.setComponentBounds:  nominal height is null" );

            } else {

                Integer nominalY = _yLocs.get( row );
                for ( FlexiGridItemInfo element : rowMap.values() ) {

                    int col = element.column();

                    Integer nominalWidth = _xWidths.get( col );
                    if ( nominalWidth == null ) {

                        Logger.logMsg( "FlexiGridCache1.setComponentBounds:  nominal width is null" );

                    } else {

                        Integer nominalX = _xLocs.get( col );

                        FlexiGridConstraintsTable constraintsTable = element.getInfo();

                        int extraHSpace = nominalWidth - element.component().getPreferredSize().width;
                        if ( extraHSpace > nominalWidth ) {

                            extraHSpace = nominalWidth;

                        } else if ( extraHSpace > 0 ) {

                            ObtuseUtil.doNothing();

                        }

                        int leftIndent = 0;

                        switch ( constraintsTable.getHorizontalJustification() ) {

                            case LEFT:

                                // nothing to be done

                                break;

                            case CENTER:

                                leftIndent = extraHSpace / 2;

                                break;

                            case RIGHT:

                                leftIndent = extraHSpace;

                                break;

                        }

                        Component component = element.component();
                        Dimension preferredSize = component.getPreferredSize();
//                        Logger.logMsg( "nominalHeight is " + nominalHeight + ", component is " + component + ", preferredSize is " + preferredSize );


                        int extraVSpace = nominalHeight - preferredSize.height;
                        if ( extraVSpace > nominalHeight ) {

                            extraVSpace = nominalHeight;

                        } else if ( extraVSpace > 0 ) {

                            ObtuseUtil.doNothing();

                        }

                        int topIndent = 0;

                        switch ( constraintsTable.getVerticalJustification() ) {

                            case TOP:

                                // nothing to be done

                                break;

                            case CENTER:

                                topIndent = extraVSpace / 2;

                                break;

                            case BOTTOM:

                                topIndent = extraVSpace;

                                break;

                        }

                        Rectangle boundingRectangle = new Rectangle(
                                (int)Math.min( ( (long)insets.left + leftIndent + nominalX + element.getInfo().getMargins().left ), Integer.MAX_VALUE ),
                                (int)Math.min( ( (long)insets.top + topIndent + nominalY + element.getInfo().getMargins().top ), Integer.MAX_VALUE ),
                                Math.min( nominalWidth,
                                          element.component()
                                                 .getPreferredSize().width
                                ),
                                Math.min( nominalHeight,
                                          element.component()
                                                 .getPreferredSize().height
                                )
                        );

                        //                Logger.logMsg( "component " + element + " @ [" + element.row() + "," + element.column() + "] has bounding box " + ObtuseUtil.fBounds( boundingRectangle ) );

                        element.component().setBounds( boundingRectangle );

                    }

                }

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
