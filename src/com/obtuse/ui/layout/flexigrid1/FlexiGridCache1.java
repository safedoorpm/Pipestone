/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridDivider;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridPanelModel;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridBasicConstraint;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridConstraintsTable;
import com.obtuse.ui.layout.flexigrid1.util.FlexiGridSliceConstraints;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.SimpleUniqueLongIdGenerator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
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

    private final SortedMap<Integer, FlexiGridSliceConstraints> _sizesByColumn;
    private final SortedMap<Integer, FlexiGridSliceConstraints> _sizesByRow;

    private SortedMap<Integer,Integer> _xLocs;
    private SortedMap<Integer,Integer> _xWidths;
    private SortedMap<Integer,Integer> _yLocs;
    private SortedMap<Integer,Integer> _yHeights;

    private int _totalWidthLessInsets;
    private int _totalHeightLessInsets;
    private boolean _haveTotalWidthAndHeight = false;

    private final Vector<Component> _visibleComponents;
    private TreeSet<Integer> _fillerColumns = new TreeSet<>();
    private TreeSet<Integer> _fillerRows = new TreeSet<>();

    public FlexiGridCache1(
            final @NotNull String name,
            final @NotNull FlexiGridLayoutManager flexiGridLayoutManager,
            final @NotNull FlexiGridContainer1 target,
            final @NotNull Hashtable<Component, FlexiGridConstraintsTable> allConstraints,
            final @NotNull FlexiGridItemInfo.FlexiItemInfoFactory itemInfoFactory
    ) {

        super();

        // This has to happen before any logging is attempted.

        _flexiGridLayoutManager = flexiGridLayoutManager;

        logMaybe( "================================= creating FlexiGridCache1" );

        _name = name;

        _grid = new GridArray<>( name );

        _target = target;

        loadGrid( target, allConstraints, itemInfoFactory );

        logMaybe( "dimensions are " + ObtuseUtil.fDim( _grid.getDimension() ) + ", columns are " + _grid.getColumnSet() + "; rows are " + _grid.getRowSet() );

        // Get the column sizes.

        _visibleComponents = new Vector<>();
        _sizesByColumn = collectSizes( true );
        showSizes( _sizesByColumn, true );

        // Get the row sizes.

        _sizesByRow = collectSizes( false );
        showSizes( _sizesByRow, false );

        Dimension gridSize = _grid.getDimension();
        System.out.print( "     " + "  " + ObtuseUtil.replicate( " ", 25 ) );
        for ( int cc : _grid.getColumnSet() ) {

            FlexiGridSliceConstraints columnConstraints = _sizesByColumn.get( cc );

            System.out.print(
                    " | " +
                    ObtuseUtil.center( "" + cc + " - " + columnConstraints.min() + '/' + columnConstraints.pref() + '/' + columnConstraints.max(), 25 )
            );

        }
        System.out.println( " |" );
        for ( int row : _grid.getRowSet() ) {

            System.out.print( ObtuseUtil.lpad( row, 5 ) );
            FlexiGridSliceConstraints rowConstraints = _sizesByRow.get( row );
            System.out.print( "  " + ObtuseUtil.center( "" + rowConstraints.min() + '/' + rowConstraints.pref() + '/' + rowConstraints.max(), 25 ) );

            SortedMap<Integer, FlexiGridItemInfo> rowElements = _grid.getRow( row );

            for ( int column : rowElements.keySet() ) {

                Optional<GridArray.ItemInfo> element = _grid.get( row, column );
                if ( element.isPresent() ) {

                    FlexiGridSliceConstraints columnConstraints = _sizesByColumn.get( column );

                    System.out.print(
                            " | " +
                            ObtuseUtil.center( "w=" + columnConstraints.pref() + ", h=" + rowConstraints.pref(), 25 )
                    );

                } else {
//                if ( element.isPresent() ) {
//
//                    FlexiGridSliceConstraints columnConstraints = _sizesByColumn.get( column );
//                    System.out.print(
//                            " | " +
//                            ObtuseUtil.center( "" + columnConstraints.min() + '/' + columnConstraints.pref() + '/' + columnConstraints.max(), 25 )
//                    );
//
//                } else {

                    System.out.print( " | " + ObtuseUtil.replicate( " ", 25 ) );

                }

            }

            System.out.println(" |" );

        }

        computePositions();

        ObtuseUtil.doNothing();

    }

    private void showSizes( final SortedMap<Integer, FlexiGridSliceConstraints> sizes, final boolean byColumn ) {

        Logger.logMsg( "sizes by " + ( byColumn ? "column" : "row" ) + " are:" );
        for ( int ix : sizes.keySet() ) {

            FlexiGridSliceConstraints sliceConstraints = sizes.get( ix );
            Logger.logMsg( ObtuseUtil.lpad( ix, 3 ) + " is " + sliceConstraints );

        }

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

    /**
     Get a row-oriented or a column-oriented view of the min/pref/max sizes for each component.

     @param getColumnSizes {@code true} if this call is to obtain a map indexed by <b>column</b> number containing the size information for each <b>column</b>;
     {@code false} if this call is to obtain a map indexed by <b>row</b> number containing the size information for each <b>row</b>.

     @return a {@link SortedMap}{@code <}{@link Integer}{@code ,}{@link FlexiGridSliceConstraints}{@code >} of size information oriented by column or by row depending on the value of {@code getColumnSizes}:
     <ul>
     <li>If {@code getColumnSizes} is {@code true} then
     invoking <em>{@code return-value}</em>{@code .get(columnNumber)} will yield a {@link FlexiGridSliceConstraints} instance for the specified <b>column</b> (zero-origin) if the column is non-empty ({@code null} is returned for empty columns)</li>
     <li>If {@code getColumnSizes} is {@code false} then
     invoking <em>{@code return-value}</em>{@code .get(rowNumber)} will yield a {@link FlexiGridSliceConstraints} instance for the specified <b>row</b> (zero-origin) if the row is non-empty ({@code null} is returned for empty rows)</li>
     </ul>
     */

    @NotNull
    private SortedMap<Integer, FlexiGridSliceConstraints> collectSizes( final boolean getColumnSizes ) {

        SortedSet<Integer> indexSet = getColumnSizes
                ?
                _grid.getRowSet()
                :
                _grid.getColumnSet();

        Function<Integer, SortedMap<Integer, FlexiGridItemInfo>> getSlice =
                getColumnSizes
                        ?
                        row -> _grid.getRow( row.intValue() )
                        :
                        row -> _grid.getColumn( row.intValue() );

        Function<Dimension, Integer> getValue =
                getColumnSizes
                        ?
                        dim -> dim.width
                        :
                        dim -> dim.height;

        SortedMap<Integer, FlexiGridSliceConstraints> constraints = new TreeMap<>();
        for ( Integer i1 : indexSet ) {

            SortedMap<Integer, FlexiGridItemInfo> elementsMap = getSlice.apply( i1 );
            for ( int i2 : elementsMap.keySet() ) {

                FlexiGridItemInfo ii = elementsMap.get( i2 );

                Logger.logMsg( "doing (" + i1 + "," + i2 + ")" );

                Component comp = ii.component();
                if ( comp.isVisible() ) {

                    if ( getColumnSizes ) {

                        _visibleComponents.add( comp );

                    }

                    FlexiGridSliceConstraints sliceConstraints = constraints.computeIfAbsent( i2, integer -> new FlexiGridSliceConstraints() );

//                    logMaybe(
//                            "component @(" + i1 + "," + i2 + ")" +
//                            ( comp instanceof JLabel
//                                      ?
//                                      ( "JLabel( " + ObtuseUtil.enquoteToJavaString( ((JLabel)comp).getText() ) + ")" )
//                                      :
//
//
//                                    )
//                            " is a " + comp
//                    );
                    ObtuseUtil.doNothing();

                    Dimension minDim = comp.getMinimumSize();
                    Dimension prefDim = comp.getPreferredSize();
                    Dimension maxDim = comp.getMaximumSize();

                    if ( comp instanceof JTextField ) {

                        JTextField tf = (JTextField)comp;
                        Logger.logMsg(
                                "component @(" + i1 + "," + i2 + ")=" +
                                "JTextField( " + ObtuseUtil.enquoteToJavaString( tf.getText() ) + ") with " +
                                ObtuseUtil.fDim( minDim ) + "/" + ObtuseUtil.fDim( prefDim ) + "/" + ObtuseUtil.fDim( maxDim )
                        );

                        if ( minDim.width == 200 ) {

                            ObtuseUtil.doNothing();

                        }

                    } else if ( comp instanceof JLabel ) {

                        JLabel jl = (JLabel)comp;
                        Logger.logMsg(
                                "component @(" + i1 + "," + i2 + ")=" +
                                "JLabel( " + ObtuseUtil.enquoteToJavaString( jl.getText() ) + ")"
                        );

                    } else {

                        Logger.logMsg(
                                "component @(" + i1 + "," + i2 + ") is a " +
                                comp.getClass().getName()
                        );

                    }

                    sliceConstraints.consume(
                            ii,
                            getValue.apply( minDim ).intValue(),
                            getValue.apply( prefDim ).intValue(),
                            getValue.apply( maxDim ).intValue()
                    );

                } else {

                    logMaybe( "FlexiGridCache1.collectSizes:  ignoring invisible component " + comp );

                }

                ObtuseUtil.doNothing();

            }

            ObtuseUtil.doNothing();

        }

        return constraints;

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

        _fillerColumns = new TreeSet<>();
        _fillerRows = new TreeSet<>();

        for ( Component c : target.getComponents() ) {

            FlexiGridConstraintsTable constraint = allConstraints.get( c );
            if ( constraint == null ) {

                throw new IllegalArgumentException( "FlexiGridCache1.loadGrid:  all components must have been added with constraints, the one named " + ObtuseUtil.enquoteToJavaString( c.getName() ) + " was added without any constraints" );

            }

        }

        _fillerColumns.clear();
        _fillerRows.clear();

        for ( Component component : target.getComponents() ) {

            if ( component.isVisible() ) {

                FlexiGridBasicConstraint bc = _flexiGridLayoutManager.getMandatoryBasicConstraint( component );
                logMaybe( "component found for row=" + bc.getRow() + " col=" + bc.getCol() );
                FlexiGridConstraintsTable constraintsTable = allConstraints.get( component );
                FlexiGridItemInfo ii = itemInfoFactory.createInstance( bc.getRow(), bc.getCol(), component, constraintsTable );
                _grid.put( ii, false );
                if ( bc.isHorizontalFiller() ) {

                    _fillerColumns.add( bc.getCol() );

                }

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

        float alignmentX = 0f;

        return alignmentX;

    }

    public float getLayoutAlignmentY() {

        float alignmentY = 0f;

        return alignmentY;

    }

    public void computePositions() {

        int fillerColumn;
        int fillerPadding = 0;
        int fillerColumnMinimum = 0;
        if ( _fillerColumns.isEmpty() ) {

            fillerColumn = -1;

        } else if ( _fillerColumns.size() > 1 ) {

            throw new HowDidWeGetHereError( "there can be only one filler column but we've got " + _fillerColumns );

        } else {

            fillerColumn = _fillerColumns.first();
            fillerColumnMinimum = _sizesByColumn.get( fillerColumn ).min();

        }

        for ( int pass = 0; pass < ( _fillerColumns.isEmpty() ? 1 : 2 ); pass += 1 ) {

            if ( pass == 1 ) {

                logMaybe( "filler column is " + fillerColumn + ", container size is " + ObtuseUtil.fBounds( _target.getBounds() ) );
                Insets in = _target.getInsets();
                int delta = _target.getBounds().width - ( _totalWidthLessInsets + in.left + in.right );
                logMaybe( "total width less insets is " + _totalWidthLessInsets + ", delta = " + delta );
                if ( delta > 0 ) {

                    fillerPadding = delta;

                } else if ( delta < 0 ) {

                    int passOneWidth = _xWidths.get( fillerColumn );

                    // If we're supposed to shrink the filler field, how much room to shrink it do we have?

                    int shrinkRoom = passOneWidth - fillerColumnMinimum;
                    if ( shrinkRoom > 0 ) {

                        if ( -delta > shrinkRoom ) {

                            fillerPadding = -shrinkRoom;

                        } else {

                            fillerPadding = -delta;

                        }

                    } else {

                        break;

                    }

//                    if ( passOneWidth + delta >= fillerColumnMinimum ) {
//
//                        // we're just fine
//
//                    } else {
//
//                        delta += fillerColumn -
//                    }
//                    int newPrefWidth = passOneWidth + delta;
//                    if ( newPrefWidth < fillerColumnMinimum ) {
//
//                        newPrefWidth = fillerColumnMinimum;
//                        delta = newPrefWidth - passOneWidth;
//
//                    }
//
//                    if ( newPrefWidth == passOneWidth ) {
//
//                        break;
//
//                    }
//
//                    fillerPadding = delta;

                    ObtuseUtil.doNothing();

                } else {

                    break;

                }

            }

            _xLocs = new TreeMap<>();
            _xWidths = new TreeMap<>();
            _totalWidthLessInsets = computeLocationsAndSizes(
                    0,
                    _xLocs,
                    _xWidths,
                    _grid.getColumnSet(),
                    _sizesByColumn,
                    fillerColumn,
                    fillerPadding,
                    true
            );

        }

        _yLocs = new TreeMap<>();
        _yHeights = new TreeMap<>();
        _totalHeightLessInsets = computeLocationsAndSizes(
                0,
                _yLocs,
                _yHeights,
                _grid.getRowSet(),
                _sizesByRow,
                -1,
                0,
                false
        );

        _haveTotalWidthAndHeight = true;

        // Go through the columns that exist.

        logMaybe( "after computePositions():" );

        for ( int col : _grid.getColumnSet() ) {

            // Get the column's min, pref and max sizes.

            FlexiGridSliceConstraints columnSizes = _sizesByColumn.get( col );
            if ( columnSizes == null ) {

                logMaybe( "FlexiGridCache1.computePositions:  no _sizesByColumn @ " + col );

            }
            logMaybe( "col " + col + " yielded " + columnSizes );

            for ( int row : _grid.getRowSet() ) {

                FlexiGridSliceConstraints rowSizes = _sizesByRow.get( row );
                if ( rowSizes == null ) {

                    logMaybe( "FlexiGridCache1.computePositions:  no _sizesByRow @ " + row );

                }
                logMaybe( "col " + col + " yielded " + columnSizes );

            }

        }

        ObtuseUtil.doNothing();

    }

    private int computeLocationsAndSizes(
            @SuppressWarnings("SameParameterValue") int startOffset,
            final SortedMap<Integer,Integer> locations,
            final SortedMap<Integer,Integer> sizes,
            final SortedSet<Integer> indexSet,
            final SortedMap<Integer, FlexiGridSliceConstraints> sliceSizesMap,
            final int fillerIx,
            final int fillerPadding,
            final boolean horizontal
    ) {

        ObtuseUtil.doNothing();

        for ( int ix : indexSet ) {

            FlexiGridSliceConstraints minPrefMaxSizes = sliceSizesMap.get( ix );

            if ( minPrefMaxSizes == null ) {

                ObtuseUtil.doNothing();

            } else {

                locations.put( ix, startOffset );
                int adjustedPrefSize = minPrefMaxSizes.pref();
                if ( fillerIx == ix && fillerPadding > 0 ) {

                    adjustedPrefSize += fillerPadding;
                    logMaybe( "filler of " + fillerPadding + " added to ix " + fillerIx );

                }

                sizes.put( ix, adjustedPrefSize );
                Insets margins = minPrefMaxSizes.getItemInfo()
                                                .getInfo()
                                                .getMargins();
                if ( margins.top > 0 || margins.bottom > 0 ) {

                    ObtuseUtil.doNothing();

                }
                startOffset += adjustedPrefSize +
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

        for ( int row : rowSet ) {

            SortedMap<Integer, FlexiGridItemInfo> rowMap = _grid.getRow( row );

            Integer xNominalHeight = _yHeights.get( row );
            if ( xNominalHeight == null ) {

                throw new HowDidWeGetHereError( "FlexiGridCache1.setComponentBounds:  nominal height is null" );

            }

            int nominalHeight = xNominalHeight.intValue();

            int nominalY = _yLocs.get( row ).intValue();

            for ( FlexiGridItemInfo element : rowMap.values() ) {

                FlexiGridConstraintsTable constraintsTable = element.getInfo();

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

//                %%% this is way too late to do this
//                int nominalWidth = Math.max(
//                        Math.min( xNominalWidth.intValue(), constraintsTable.getMaxWidth() ),
//                        constraintsTable.getMinWidth()
//                );
                int nominalWidth = xNominalWidth.intValue();

                int nominalX = _xLocs.get( col ).intValue();

                // If we're dealing with a divider then pretend that its preferred width/height is the nominal width/height of its orientation.
                // For example, if it is row-oriented then its preferred width is forced to be the planned width for this column
                // but if it is column-oriented then its preferred height is forced to be the planned height for this row.

                boolean isFullWidth = false;
                boolean isFullHeight = false;

                Rectangle boundingRectangle = null;
                if ( component instanceof FlexiGridDivider ) {

                    FlexiGridDivider divider = (FlexiGridDivider)component;
                    if ( divider.getOrientation().isRowOrientation() ) {

                        // Row oriented full length dividers must be alone in their row.

                        if ( divider.isFullLength() ) {

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

                    case FILL:

                        preferredWidth = nominalWidth;

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

                    ObtuseUtil.doNothing();

                } else if ( isFullHeight ) {

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

                    ObtuseUtil.doNothing();

                }

                //                logMaybe( "component " + element + " @ [" + element.row() + "," + element.column() + "] has bounding box " + ObtuseUtil.fBounds( boundingRectangle ) );

                element.component().setBounds( boundingRectangle );

                if ( element.component() instanceof JTextField ) {

                    ObtuseUtil.doNothing();

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
