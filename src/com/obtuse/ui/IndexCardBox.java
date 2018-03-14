/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.LinearOrientation;
import com.obtuse.ui.layout.linear.AbstractScrollableLinearContainer3;
import com.obtuse.ui.scrollable.IrregularScrollableImpl;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 Manage a list of {@link SelectableIndexCard}s.
 */

public class IndexCardBox<E extends SelectableIndexCard> extends AbstractScrollableLinearContainer3 {

    private final IrregularScrollableImpl _irregularScrollableImpl = new IrregularScrollableImpl( this, isVertical() );

    private ListSelectionModel _selectionModel;

    private ListSelectionListener selectionListener;

    private SortedSet<Integer> _lastSelection = new TreeSet<>();

    private final Consumer<IndexCardBox<E>> _doubleClickOnImageConsumer;

    public IndexCardBox( final String name, final LinearOrientation linearOrientation, final @NotNull Consumer<IndexCardBox<E>> doubleClickOnImageConsumer ) {

        super( name, linearOrientation );

        _selectionModel = createSelectionModel();

        _doubleClickOnImageConsumer = doubleClickOnImageConsumer;

        _selectionModel.addListSelectionListener(
                e -> {
                    SortedSet<Integer> selectedIndices = new TreeSet<>();
                    for ( int ix = _selectionModel.getMinSelectionIndex(); ix <= _selectionModel.getMaxSelectionIndex(); ix += 1 ) {
                        if ( _selectionModel.isSelectedIndex( ix ) ) {
                            selectedIndices.add( ix );
                        }
                    }

                    Logger.logMsg( "IndexCardBox:  selection changed:  " + Arrays.toString( selectedIndices.toArray() ) );
                    int size = getComponentCount();

                    for ( int ix = 0; ix < size; ix += 1 ) {

                        Component c = getComponent( ix );

                        if ( c instanceof SelectableIndexCard ) {

                            SelectableIndexCard indexCard = (SelectableIndexCard)c;

                            indexCard.setSelected( selectedIndices.contains( ix ) );

                        }

                    }

                    ObtuseUtil.doNothing();

                }
        );

    }

    private void setupAwtEventListener() {

        Toolkit.getDefaultToolkit().addAWTEventListener(
                event -> {

                    if ( event instanceof MouseEvent ) {

                        MouseEvent mEvent = (MouseEvent)event;
                        doMouseClick( mEvent );

                    }

                },
                AWTEvent.MOUSE_EVENT_MASK
        );

    }

    public void doMouseClick( final MouseEvent mEvent ) {

        if ( mEvent.getID() == MouseEvent.MOUSE_CLICKED | mEvent.getID() == MouseEvent.MOUSE_PRESSED ||
             mEvent.getID() == MouseEvent.MOUSE_RELEASED ) {

            @SuppressWarnings("unused") String kind;
            switch ( mEvent.getID() ) {

                case MouseEvent.MOUSE_CLICKED:
                    //noinspection UnusedAssignment
                    kind = "click";
                    break;

                case MouseEvent.MOUSE_PRESSED:
                    //noinspection UnusedAssignment
                    kind = "press";
                    break;

                case MouseEvent.MOUSE_RELEASED:
                    //noinspection UnusedAssignment
                    kind = "release";
                    break;

                default:

                    throw new HowDidWeGetHereError(
                            "expected click (" + MouseEvent.MOUSE_CLICKED + "), press (" + MouseEvent.MOUSE_PRESSED +
                            ", or release (" + MouseEvent.MOUSE_RELEASED + ") but got " + mEvent.getID() );

            }

            if ( mEvent.getID() == MouseEvent.MOUSE_CLICKED ) {

                for ( int ix = 0; ix < getModelSize(); ix += 1 ) {

                    Component c = getComponent( ix );

                    if ( c instanceof SelectableIndexCard ) {

                        SelectableIndexCard indexCard = (SelectableIndexCard)c;

                        if ( indexCard.wereWeClicked( mEvent ) ) {

                            doMouseClick( ix, indexCard, mEvent );

                            ObtuseUtil.doNothing();

                        }

                    }

                }

            }

        }

    }

    public void doMouseClick( final @NotNull SelectableIndexCard indexCard, final @NotNull MouseEvent mEvent ) {

        if ( mEvent.getID() == MouseEvent.MOUSE_CLICKED ) {

            int ix = getComponentIx( indexCard );
            if ( ix >= 0 ) {

                doMouseClick( ix, indexCard, mEvent );

            } else {

                throw new ObtuseMessageLabel.AugmentedIllegalArgumentException( "IndexCardBox:  SIC not found in our list of components:  " + indexCard );

            }

        } else {

            throw new ObtuseMessageLabel.AugmentedIllegalArgumentException( "IndexCardBox:  mouse event is not a mouse click (id=" + mEvent.getID() + ")" );

        }

    }

    public int getComponentIx( final SelectableIndexCard indexCard ) {

        for ( int ix = 0; ix < getModelSize(); ix += 1 ) {

            Component c = getComponent( ix );

            if ( indexCard == c ) {

                return ix;

            }

        }

        return -1;

    }

    private void doMouseClick( final int ix, final @NotNull SelectableIndexCard indexCard, final @NotNull MouseEvent mEvent ) {

        switch ( mEvent.getClickCount() ) {

            case 1:

                doSingleClick( ix, indexCard, mEvent );
                break;

            case 2:

                doDoubleClick( ix, indexCard, mEvent );
                break;

            default:

                // Just ignore other click counts

                Logger.logMsg( "click count " + mEvent.getClickCount() + " ignored" );

                break;

        }

    }

    private void doSingleClick( final int ix, final SelectableIndexCard indexCard, final MouseEvent mEvent ) {

        Logger.logMsg( "{{{{" );

        Logger.logMsg( "doSingleClick:  modifier keys are " + MouseEvent.getModifiersExText( mEvent.getModifiersEx() ) );
        boolean gotOne = false;
        for ( int i = 0; i < 31; i += 1 ) {

            if ( ( mEvent.getModifiersEx() & ( 1 << i ) ) != 0 ) {

                Logger.logMsg( "modifiersEx:  1 << " + i + " is set" );
                gotOne = true;

            }

        }

        adjustSelection( ix, mEvent, false );

        Logger.logMsg( "}}}}" );

    }

    private void doDoubleClick( final int ix, final SelectableIndexCard indexCard, final MouseEvent mEvent ) {

        Logger.logMsg( "double click on " + indexCard.getWhat() );

        if ( _doubleClickOnImageConsumer != null ) {

            _doubleClickOnImageConsumer.accept( this );

        }

    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {

        Dimension rval = _irregularScrollableImpl.getPreferredScrollableViewportSize();
        ObtuseUtil.report( "### preferred scrollable viewport size is " + ObtuseUtil.fDim( rval ), new NumberFormatException( "where we are" ) );
        return rval;

    }

    @Override
    public int getScrollableUnitIncrement( final Rectangle visibleRect, final int orientation, final int direction ) {

        int scrollableUnitIncrement = _irregularScrollableImpl.getScrollableUnitIncrement( visibleRect, orientation, direction );
        ObtuseUtil.report( "### scrollable unit increment is " + scrollableUnitIncrement, new NumberFormatException( "where we are" ) );
        return scrollableUnitIncrement;

    }

    @Override
    public int getScrollableBlockIncrement( final Rectangle visibleRect, final int orientation, final int direction ) {

        int scrollableBlockIncrement = _irregularScrollableImpl.getScrollableBlockIncrement( visibleRect, orientation, direction );
        ObtuseUtil.report( "### scrollable block increment is " + scrollableBlockIncrement, new NumberFormatException( "where we are" ) );
        return scrollableBlockIncrement;

    }

    @Override
    public boolean getScrollableTracksViewportWidth() {

        boolean rval = _irregularScrollableImpl.getScrollableTracksViewportWidth();
        ObtuseUtil.report( "### scrollable tracks viewport width is " + rval, new NumberFormatException( "where we are" ) );
        return rval;

    }

    @Override
    public boolean getScrollableTracksViewportHeight() {

        boolean rval = _irregularScrollableImpl.getScrollableTracksViewportHeight();
        ObtuseUtil.report( "### scrollable tracks viewport height is " + rval, new NumberFormatException( "where we are" ) );
        return rval;

    }

    protected ListSelectionModel createSelectionModel() {

        return new DefaultListSelectionModel();

    }

    public ListSelectionModel getSelectionModel() {

        return _selectionModel;

    }

    /**
     Adjust a selection based on mouse/keyboard input.
     <p/>This method 'borrowed' from the Oracle Java source in order to ensure that I implement the exactly correct list selection and de-selection look-and-feel.
     The starting point for this method was found in the Java 8 source for the {@code javax.swing.plaf.basic.BasicListUI} class.
     It has been changed primarily if not entirely to account for the fact that we are managing components in a JPanel as opposed to elements in a JList.
     That file is
     <blockquote>Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
     <br>ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.</blockquote>
     @param row the row that the mouse is pointing at.
     @param e the mouse event that got us here.
     */

    private void adjustSelection( final int row, final MouseEvent e, final boolean isFileList ) {

        if ( row < 0 ) {

            // If shift is down in multi-select, we should do nothing.
            // For single select or non-shift-click, clear the selection
            if (
                    isFileList && e.getID() == MouseEvent.MOUSE_PRESSED &&
                    (
                            !e.isShiftDown() ||
                            _selectionModel.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION
                    )
            ) {

                _selectionModel.clearSelection();

            }

        } else {

            int anchorIndex = adjustIndex( _selectionModel.getAnchorSelectionIndex(), getComponentCount() );
            boolean anchorSelected;

            if ( anchorIndex == -1 ) {

                anchorIndex = 0;
                anchorSelected = false;

            } else {

                anchorSelected = _selectionModel.isSelectedIndex( anchorIndex );

            }

            if ( anchorSelected && isMenuShortcutKeyDown( e ) ) {

                if ( e.isShiftDown() ) {

                    _selectionModel.addSelectionInterval( anchorIndex, row );

    // anchorSelected is always true at this point. Consequently, the only statement inside this first if is the only statement in this
    // sequence of code that is ever executed. That's why that one statement of code is the only code within our containing if block
    // and why the code below is commented out.

    //                    if ( anchorSelected ) {
    //
    //                        _selectionModel.addSelectionInterval( anchorIndex, row );
    //
    //                    } else {
    //
    //                        _selectionModel.removeSelectionInterval( anchorIndex, row );
    //
    //                        if ( isFileList ) {
    //
    //                            _selectionModel.addSelectionInterval( row, row );
    //                            _selectionModel.setAnchorSelectionIndex( anchorIndex );
    //
    //                        }
    //
    //                    }

                } else if ( _selectionModel.isSelectedIndex( row ) ) {

                    _selectionModel.removeSelectionInterval( row, row );

                } else {

                    _selectionModel.addSelectionInterval( row, row );

                }

            } else if ( e.isShiftDown() ) {

                _selectionModel.setSelectionInterval( anchorIndex, row );

            } else {

                _selectionModel.setSelectionInterval( row, row );

            }

        }

    }

    private static int adjustIndex( final int index, final int listSize ) {

        return index < listSize ? index : -1;

    }

    static boolean isMenuShortcutKeyDown( final InputEvent event) {

        int modifiers = event.getModifiers();
        int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        Logger.logMsg(
                "modifiers = " + modifiers + " == { " + MouseEvent.getMouseModifiersText( modifiers ) + " }" +
                ", mask = " + menuShortcutKeyMask + " == { " + MouseEvent.getMouseModifiersText( menuShortcutKeyMask ) + " }" +
                ", anded = " + ( modifiers & menuShortcutKeyMask ) + " == { " + MouseEvent.getMouseModifiersText( modifiers & menuShortcutKeyMask ) + " }"
        );

        return ( modifiers & menuShortcutKeyMask ) != 0;

    }

    protected void fireSelectionValueChanged( final int firstIndex, final int lastIndex, final boolean isAdjusting ) {

        Object[] listeners = listenerList.getListenerList();
        ListSelectionEvent e = null;

        for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {

            if ( listeners[i] == ListSelectionListener.class ) {

                if ( e == null ) {

                    e = new ListSelectionEvent( this, firstIndex, lastIndex, isAdjusting );
                }

                ( (ListSelectionListener)listeners[i + 1] ).valueChanged( e );

            }

        }

    }

    private class ListSelectionHandler implements ListSelectionListener, Serializable {

        public void valueChanged( final ListSelectionEvent e ) {

            fireSelectionValueChanged(
                    e.getFirstIndex(),
                    e.getLastIndex(),
                    e.getValueIsAdjusting()
            );
        }

    }

    public void addListSelectionListener( final ListSelectionListener listener ) {

        if ( selectionListener == null ) {

            selectionListener = new ListSelectionHandler();
            getSelectionModel().addListSelectionListener( selectionListener );

        }

        listenerList.add( ListSelectionListener.class, listener );

    }

    public void removeListSelectionListener( final ListSelectionListener listener ) {

        listenerList.remove( ListSelectionListener.class, listener );

    }

    public ListSelectionListener[] getListSelectionListeners() {

        return listenerList.getListeners( ListSelectionListener.class );

    }

    public void setSelectionModel( final ListSelectionModel selectionModel ) {

        if ( selectionModel == null ) {

            throw new IllegalArgumentException( "selectionModel must be non null" );

        }

        // Remove the forwarding ListSelectionListener from the old selectionModel
        // and add it to the new one, if necessary.

        if ( selectionListener != null ) {

            _selectionModel.removeListSelectionListener( selectionListener );
            selectionModel.addListSelectionListener( selectionListener );

        }

        ListSelectionModel oldValue = _selectionModel;
        _selectionModel = selectionModel;
        firePropertyChange( "selectionModel", oldValue, selectionModel );

    }

    public void setSelectionMode( final int selectionMode ) {

        getSelectionModel().setSelectionMode( selectionMode );

    }

    public int getSelectionMode() {

        return getSelectionModel().getSelectionMode();

    }

    public int getAnchorSelectionIndex() {

        return getSelectionModel().getAnchorSelectionIndex();

    }

    public int getLeadSelectionIndex() {

        return getSelectionModel().getLeadSelectionIndex();

    }

    public int getMinSelectionIndex() {

        return getSelectionModel().getMinSelectionIndex();

    }

    public int getMaxSelectionIndex() {

        return getSelectionModel().getMaxSelectionIndex();

    }

    public boolean isSelectedIndex( final int index ) {

        return getSelectionModel().isSelectedIndex( index );

    }

    public boolean isSelectionEmpty() {

        return getSelectionModel().isSelectionEmpty();

    }

    public void clearSelection() {

        getSelectionModel().clearSelection();

    }

    public void setSelectionInterval( final int anchor, final int lead ) {

        getSelectionModel().setSelectionInterval( anchor, lead );

    }

    public void addSelectionInterval( final int anchor, final int lead ) {

        getSelectionModel().addSelectionInterval( anchor, lead );

    }

    public void removeSelectionInterval( final int index0, final int index1 ) {

        getSelectionModel().removeSelectionInterval( index0, index1 );

    }

    public void setValueIsAdjusting( final boolean b ) {

        getSelectionModel().setValueIsAdjusting( b );

    }

    public boolean getValueIsAdjusting() {

        return getSelectionModel().getValueIsAdjusting();

    }

    public int[] getSelectedIndices() {

        ListSelectionModel sm = getSelectionModel();
        int iMin = sm.getMinSelectionIndex();
        int iMax = sm.getMaxSelectionIndex();

        if ( ( iMin < 0 ) || ( iMax < 0 ) ) {

            return new int[0];

        }

        int[] rvTmp = new int[1 + ( iMax - iMin )];
        int n = 0;
        for ( int i = iMin; i <= iMax; i++ ) {

            if ( sm.isSelectedIndex( i ) ) {

                rvTmp[n++] = i;

            }

        }

        int[] rv = new int[n];
        System.arraycopy( rvTmp, 0, rv, 0, n );

        return rv;

    }

    public int getModelSize() {

        return getComponentCount();

    }

    @SuppressWarnings("unchecked")
    public E getModelElementAt( final int index ) {

        try {

            return (E)getComponent( index );

        } catch ( ClassCastException e ) {

            Logger.logErr( "IndexCardBox.getModelElementAt:  unable to fetch component @ " + index + " - not a proper type for this method" );

            throw e;

        }

    }

    public void setSelectedIndex( final int index ) {

        if ( index >= getModelSize() ) {

            return;

        }

        getSelectionModel().setSelectionInterval( index, index );

    }

    public void setSelectedIndices( final int@NotNull[] indices ) {

        ListSelectionModel sm = getSelectionModel();
        sm.clearSelection();
        int size = getModelSize();
        for ( int i : indices ) {

            if ( i < size ) {

                sm.addSelectionInterval( i, i );

            }

        }

    }

    public java.util.List<E> getSelectedValuesList() {

        ListSelectionModel sm = getSelectionModel();

        int iMin = sm.getMinSelectionIndex();
        int iMax = sm.getMaxSelectionIndex();

        if ( ( iMin < 0 ) || ( iMax < 0 ) ) {

            return Collections.emptyList();

        }

        java.util.List<E> selectedItems = new ArrayList<>();
        for ( int i = iMin; i <= iMax; i++ ) {

            if ( sm.isSelectedIndex( i ) ) {

                selectedItems.add( getModelElementAt( i ) );

            }

        }

        return selectedItems;

    }

    public int getSelectedIndex() {

        return getMinSelectionIndex();

    }

    public E getSelectedValue() {

        int i = getMinSelectionIndex();

        return ( i == -1 ) ? null : getModelElementAt( i );

    }

    public void setSelectedValue( final Object anObject, @SuppressWarnings("SameParameterValue") final boolean shouldScroll ) {

        if ( anObject == null ) {

            setSelectedIndex( -1 );

        } else if ( !anObject.equals( getSelectedValue() ) ) {

            int i, c;

            for ( i = 0, c = getModelSize(); i < c; i++ ) {

                if ( anObject.equals( getModelElementAt( i ) ) ) {

                    setSelectedIndex( i );
                    if ( shouldScroll ) {

                        ensureIndexIsVisible( i );

                    }

                    repaint();  // FIX-ME setSelectedIndex does not redraw all the time with the basic l&f

                    return;

                }

            }

            setSelectedIndex( -1 );

        }

        repaint(); // FIX-ME setSelectedIndex does not redraw all the time with the basic l&f

    }

    public void ensureIndexIsVisible( final int index ) {

        Rectangle cellBounds = getCellBounds( index, index );
        if ( cellBounds != null ) {

            scrollRectToVisible( cellBounds );

        }

    }

    public Rectangle getCellBounds( final int index ) {

        @SuppressWarnings("UnnecessaryLocalVariable")
        int row = index;        // keep it simple for now.
        if ( row == -1 ) {

            return null;

        }

        E c = getModelElementAt( index );

        Rectangle bounds = c.getBounds();

        return bounds;

    }

    public Rectangle getCellBounds( final int index0, final int index1 ) {

        int minIndex = Math.min( index0, index1 );
        int maxIndex = Math.max( index0, index1 );

        if ( minIndex >= getModelSize() ) {

            return null;

        }

        Rectangle firstBounds = getCellBounds( index0 );
        if ( firstBounds == null || minIndex == maxIndex ) {

            return firstBounds;

        }

        Rectangle lastBounds = getCellBounds( maxIndex );

        if ( lastBounds != null ) {

            if ( firstBounds.x != lastBounds.x ) {

                throw new HowDidWeGetHereError(
                        "IndexCardBox:  cells don't line up - firstBounds=" + firstBounds + ", lastBounds=" + lastBounds );

            }

            firstBounds.add( lastBounds );

        }

        return firstBounds;

    }

    private void verifySelectable( final @NotNull Component comp ) {

        if ( !( comp instanceof SelectableIndexCard ) ) {

            throw new IllegalArgumentException(
                    "IndexCardBox:  only components implementing SelectableIndexCard may be added to an IndexCardBox (comp is a " +
                    comp.getClass().getCanonicalName() + ")"
            );

        }

    }

    /**
     Add a component to the box.
     Throws an {@link ObtuseMessageLabel.AugmentedIllegalArgumentException}
     if the component does not implement the {@link SelectableIndexCard} interface.
     Otherwise equivalent to {@link Container#add(Component comp)} (see that method for more authoritative info).

     @param comp the component to be added.
     @return the added component.
     @throws ObtuseMessageLabel.AugmentedIllegalArgumentException if {@code comp} is {@code null}
     or does not implement the {@link SelectableIndexCard} interface.
     */

    public Component add( final @NotNull Component comp ) {

        verifySelectable( comp );

        return super.add( comp );

    }

    /**
     Add a component to the box.
     Throws an {@link ObtuseMessageLabel.AugmentedIllegalArgumentException}
     if the component does not implement the {@link SelectableIndexCard} interface.
     Otherwise equivalent to {@link Container#add(String name, Component comp)} (see that method for more authoritative info).
     <p/>According to Sun/Oracle, this method is obsolete as of 1.1.  Please use the method <code>add(Component, Object)</code> instead.

     @param comp the component to be added.
     @return the added component.
     @throws ObtuseMessageLabel.AugmentedIllegalArgumentException if {@code comp} is {@code null}
     or does not implement the {@link SelectableIndexCard} interface.
     */

    public Component add( final @NotNull String name, final Component comp ) {

        verifySelectable( comp );

        return super.add( name, comp );

    }

    /**
     Add a component to the box.
     Throws an {@link ObtuseMessageLabel.AugmentedIllegalArgumentException}
     if the component does not implement the {@link SelectableIndexCard} interface.
     Otherwise equivalent to {@link Container#add(Component comp, int ix)} (see that method for more authoritative info).

     @param comp the component to be added.
     @param ix   the position in the container's list at which to insert
     the component; <code>-1</code> means insert at the end component
     @return the added component.
     @throws ObtuseMessageLabel.AugmentedIllegalArgumentException if {@code comp} is {@code null}
     or does not implement the {@link SelectableIndexCard} interface.
     */

    public Component add( final @NotNull Component comp, final int ix ) {

        verifySelectable( comp );

        return super.add( comp, ix );

    }

    /**
     Add a component to the box.
     Throws an {@link ObtuseMessageLabel.AugmentedIllegalArgumentException}
     if the component does not implement the {@link SelectableIndexCard} interface.
     Otherwise equivalent to {@link Container#add(Component comp, Object constraints)} (see that method for more authoritative info).

     @param comp        the component to be added.
     @param constraints an object expressing layout constraints for this component.
     @throws ObtuseMessageLabel.AugmentedIllegalArgumentException if {@code comp} is {@code null}
     or does not implement the {@link SelectableIndexCard} interface.
     */

    public void add( final @NotNull Component comp, final Object constraints ) {

        verifySelectable( comp );

        super.add( comp, constraints );

    }

    /**
     Add a component to the box.
     Throws an {@link ObtuseMessageLabel.AugmentedIllegalArgumentException}
     if the component does not implement the {@link SelectableIndexCard} interface.
     Otherwise equivalent to {@link Container#add(Component comp, Object constraints, int index)} (see that method for more authoritative info).

     @param comp        the component to be added.
     @param constraints an object expressing layout constraints for this component.
     @param index       the position in the container's list at which to insert
     the component; <code>-1</code> means insert at the end component
     @throws ObtuseMessageLabel.AugmentedIllegalArgumentException if {@code comp} is {@code null}
     or does not implement the {@link SelectableIndexCard} interface.
     */

    public void add( final @NotNull Component comp, final Object constraints, final int index ) {

        verifySelectable( comp );

        super.add( comp, constraints, index );

    }

    public String toString() {

        return "IndexCardBox<>()";

    }

}