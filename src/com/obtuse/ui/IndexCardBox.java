/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.LinearOrientation;
import com.obtuse.ui.layout.linear.AbstractScrollableLinearContainer3;
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

@SuppressWarnings("unused")
public class IndexCardBox<E extends SelectableIndexCard> extends AbstractScrollableLinearContainer3 {

    private final MyScrollableImplementation _MyScrollableImplementation = new MyScrollableImplementation( this, isVertical() );

    private ListSelectionModel _selectionModel;

    private ListSelectionListener selectionListener;

    private SortedSet<Integer> _lastSelection = new TreeSet<>();

    private final Consumer<IndexCardBox<E>> _doubleClickOnImageConsumer;

//    private ListModel<E> _dataModel;

    //    private int _verticalUnitIncrement = 10;

    public IndexCardBox( String name, LinearOrientation linearOrientation, @NotNull Consumer<IndexCardBox<E>> doubleClickOnImageConsumer ) {

        super( name, linearOrientation );

        _selectionModel = createSelectionModel();

        _doubleClickOnImageConsumer = doubleClickOnImageConsumer;

//	setupAwtEventListener();
//
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

//			    Logger.logMsg( "its a mouse event:  " + event );

                        MouseEvent mEvent = (MouseEvent)event;
                        doMouseClick( mEvent );

                    }

                },
                AWTEvent.MOUSE_EVENT_MASK
        );

    }

    public void doMouseClick( MouseEvent mEvent ) {

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

    public void doMouseClick( @NotNull SelectableIndexCard indexCard, @NotNull MouseEvent mEvent ) {

        if ( mEvent.getID() == MouseEvent.MOUSE_CLICKED ) {

            int ix = getComponentIx( indexCard );
            if ( ix >= 0 ) {

                doMouseClick( ix, indexCard, mEvent );

            } else {

                throw new MessageLabel.AugmentedIllegalArgumentException( "IndexCardBox:  SIC not found in our list of components:  " + indexCard );

            }

        } else {

            throw new MessageLabel.AugmentedIllegalArgumentException( "IndexCardBox:  mouse event is not a mouse click (id=" + mEvent.getID() + ")" );

        }

    }

    public int getComponentIx( SelectableIndexCard indexCard ) {

        for ( int ix = 0; ix < getModelSize(); ix += 1 ) {

            Component c = getComponent( ix );

            if ( indexCard == c ) {

                return ix;

            }

        }

        return -1;

    }

    private void doMouseClick( int ix, @NotNull SelectableIndexCard indexCard, @NotNull MouseEvent mEvent ) {

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

    private void doSingleClick( int ix, SelectableIndexCard indexCard, MouseEvent mEvent ) {

        Logger.logMsg( "####" );

        Logger.logMsg( "doSingleClick:  modifier keys are " + MouseEvent.getModifiersExText( mEvent.getModifiersEx() ) );
        boolean gotOne = false;
        for ( int i = 0; i < 31; i += 1 ) {

            if ( ( mEvent.getModifiersEx() & ( 1 << i ) ) != 0 ) {

                Logger.logMsg( "modifiersEx:  1 << " + i + " is set" );
                gotOne = true;

            }

        }

        if ( gotOne ) {

            Logger.logMsg( "----" );

        }

        for ( int i = 0; i < 31; i += 1 ) {

            if ( ( mEvent.getModifiers() & ( 1 << i ) ) != 0 ) {

                Logger.logMsg( "modifiers:    1 << " + i + " (" + ObtuseUtil.getBitMaskName( 1 << i ) + ") is set" );

            }

        }

        if ( mEvent.getModifiers() == ( InputEvent.BUTTON1_MASK ) ) {

            // They just clicked on something - that something becomes the only selected thing

            Logger.logMsg( "CLICK" );

//	    if ( isSelectedIndex( ix ) ) {

            setSelectedIndex( ix );

//	    }

        } else if ( mEvent.getModifiers() == ( InputEvent.META_MASK | InputEvent.BUTTON1_MASK ) ) {

            // They just alt-clicked on something - that something's selection state flips (all other selected things remain selected)

            Logger.logMsg( "ALT-CLICK" );

            if ( isSelectedIndex( ix ) ) {

                removeSelectionInterval( ix, ix );

            } else {

                addSelectionInterval( ix, ix );

            }

        } // that's it for now.

//	setSelectedValue( indexCard, false );

        Logger.logMsg( "####" );

    }

    private void doDoubleClick( int ix, SelectableIndexCard indexCard, MouseEvent mEvent ) {

        Logger.logMsg( "double click on " + indexCard.getWhat() );

        if ( _doubleClickOnImageConsumer != null ) {

            _doubleClickOnImageConsumer.accept( this );

        }

    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {

        return _MyScrollableImplementation.getPreferredScrollableViewportSize();

    }

    @Override
    public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {

        return _MyScrollableImplementation.getScrollableUnitIncrement( visibleRect, orientation, direction );

    }

    @Override
    public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {

        return _MyScrollableImplementation.getScrollableBlockIncrement( visibleRect, orientation, direction );

    }

    @Override
    public boolean getScrollableTracksViewportWidth() {

        return _MyScrollableImplementation.getScrollableTracksViewportWidth();

    }

    @Override
    public boolean getScrollableTracksViewportHeight() {

        return _MyScrollableImplementation.getScrollableTracksViewportHeight();

    }

//    public ListModel<E> getModel() {
//
//	return _dataModel;
//
//    }

//    public void setModel(ListModel<E> model) {
//
//	if (model == null) {
//
//	    throw new IllegalArgumentException("model must be non null");
//
//	}
//
//	ListModel<E> oldValue = _dataModel;
//	_dataModel = model;
//
//	firePropertyChange("model", oldValue, _dataModel);
//
//	clearSelection();
//
//    }

    protected ListSelectionModel createSelectionModel() {

        return new DefaultListSelectionModel();

    }

    public ListSelectionModel getSelectionModel() {

        return _selectionModel;

    }

    protected void fireSelectionValueChanged( int firstIndex, int lastIndex, boolean isAdjusting ) {

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

        public void valueChanged( ListSelectionEvent e ) {

            fireSelectionValueChanged(
                    e.getFirstIndex(),
                    e.getLastIndex(),
                    e.getValueIsAdjusting()
            );
        }

    }

    public void addListSelectionListener( ListSelectionListener listener ) {

        if ( selectionListener == null ) {

            selectionListener = new ListSelectionHandler();
            getSelectionModel().addListSelectionListener( selectionListener );

        }

        listenerList.add( ListSelectionListener.class, listener );

    }

    public void removeListSelectionListener( ListSelectionListener listener ) {

        listenerList.remove( ListSelectionListener.class, listener );

    }

    public ListSelectionListener[] getListSelectionListeners() {

        return listenerList.getListeners( ListSelectionListener.class );

    }

    public void setSelectionModel( ListSelectionModel selectionModel ) {

        if ( selectionModel == null ) {

            throw new IllegalArgumentException( "selectionModel must be non null" );

        }

        /* Remove the forwarding ListSelectionListener from the old
     * selectionModel, and add it to the new one, if necessary.
         */
        if ( selectionListener != null ) {

            _selectionModel.removeListSelectionListener( selectionListener );
            selectionModel.addListSelectionListener( selectionListener );

        }

        ListSelectionModel oldValue = _selectionModel;
        _selectionModel = selectionModel;
        firePropertyChange( "selectionModel", oldValue, selectionModel );

    }

    public void setSelectionMode( int selectionMode ) {

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

    public boolean isSelectedIndex( int index ) {

        return getSelectionModel().isSelectedIndex( index );

    }

    public boolean isSelectionEmpty() {

        return getSelectionModel().isSelectionEmpty();

    }

    public void clearSelection() {

        getSelectionModel().clearSelection();

    }

    public void setSelectionInterval( int anchor, int lead ) {

        getSelectionModel().setSelectionInterval( anchor, lead );

    }

    public void addSelectionInterval( int anchor, int lead ) {

        getSelectionModel().addSelectionInterval( anchor, lead );

    }

    public void removeSelectionInterval( int index0, int index1 ) {

        getSelectionModel().removeSelectionInterval( index0, index1 );

    }

    public void setValueIsAdjusting( boolean b ) {

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
    public E getModelElementAt( int index ) {

        try {

            return (E)getComponent( index );

        } catch ( ClassCastException e ) {

            Logger.logErr( "IndexCardBox.getModelElementAt:  unable to fetch component @ " + index + " - not a proper type for this method" );

            throw e;

        }

    }

    public void setSelectedIndex( int index ) {

//	for ( E entity : getSelectedValuesList() ) {
//
//	    entity.setSelected( false );
//
//	}

        if ( index >= getModelSize() ) {

            return;

        }

        getSelectionModel().setSelectionInterval( index, index );
//	if ( index >= 0 ) {
//
//	    getModelElementAt( index ).setSelected( true );
//
//	}

    }

    public void setSelectedIndices( int[] indices ) {

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

//	ListModel<E> dm = getModel();

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

    public void setSelectedValue( Object anObject, @SuppressWarnings("SameParameterValue") boolean shouldScroll ) {

        if ( anObject == null ) {

            setSelectedIndex( -1 );

        } else if ( !anObject.equals( getSelectedValue() ) ) {

            int i, c;

//	    ListModel<E> dm = getModel();
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

    public void ensureIndexIsVisible( int index ) {

        Rectangle cellBounds = getCellBounds( index, index );
        if ( cellBounds != null ) {

            scrollRectToVisible( cellBounds );

        }

    }

    public Rectangle getCellBounds( int index ) {

        @SuppressWarnings("UnnecessaryLocalVariable")
        int row = index;        // keep it simple for now.
        if ( row == -1 ) {

            return null;

        }

        E c = getModelElementAt( index );

        Rectangle bounds = c.getBounds();

        return bounds;

    }

    public Rectangle getCellBounds( int index0, int index1 ) {

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

    private void verifySelectable( @NotNull Component comp ) {

        if ( !( comp instanceof SelectableIndexCard ) ) {

            throw new IllegalArgumentException(
                    "IndexCardBox:  only components implementing SelectableIndexCard may be added to an IndexCardBox (comp is a " +
                    comp.getClass().getCanonicalName() + ")"
            );

        }

    }

    /**
     Add a component to the box.
     Throws an {@link com.obtuse.ui.MessageLabel.AugmentedIllegalArgumentException}
     if the component does not implement the {@link SelectableIndexCard} interface.
     Otherwise equivalent to {@link Container#add(Component comp)} (see that method for more authoritative info).

     @param comp the component to be added.
     @return the added component.
     @throws com.obtuse.ui.MessageLabel.AugmentedIllegalArgumentException if {@code comp} is {@code null}
     or does not implement the {@link SelectableIndexCard} interface.
     */

    public Component add( @NotNull Component comp ) {

        verifySelectable( comp );

        return super.add( comp );

    }

    /**
     Add a component to the box.
     Throws an {@link com.obtuse.ui.MessageLabel.AugmentedIllegalArgumentException}
     if the component does not implement the {@link SelectableIndexCard} interface.
     Otherwise equivalent to {@link Container#add(String name, Component comp)} (see that method for more authoritative info).
     <p/>According to Sun/Oracle, this method is obsolete as of 1.1.  Please use the method <code>add(Component, Object)</code> instead.

     @param comp the component to be added.
     @return the added component.
     @throws com.obtuse.ui.MessageLabel.AugmentedIllegalArgumentException if {@code comp} is {@code null}
     or does not implement the {@link SelectableIndexCard} interface.
     */

    public Component add( @NotNull String name, Component comp ) {

        verifySelectable( comp );

        return super.add( name, comp );

    }

    /**
     Add a component to the box.
     Throws an {@link com.obtuse.ui.MessageLabel.AugmentedIllegalArgumentException}
     if the component does not implement the {@link SelectableIndexCard} interface.
     Otherwise equivalent to {@link Container#add(Component comp, int ix)} (see that method for more authoritative info).

     @param comp the component to be added.
     @param ix   the position in the container's list at which to insert
     the component; <code>-1</code> means insert at the end component
     @return the added component.
     @throws com.obtuse.ui.MessageLabel.AugmentedIllegalArgumentException if {@code comp} is {@code null}
     or does not implement the {@link SelectableIndexCard} interface.
     */

    public Component add( @NotNull Component comp, int ix ) {

        verifySelectable( comp );

        return super.add( comp, ix );

    }

    /**
     Add a component to the box.
     Throws an {@link com.obtuse.ui.MessageLabel.AugmentedIllegalArgumentException}
     if the component does not implement the {@link SelectableIndexCard} interface.
     Otherwise equivalent to {@link Container#add(Component comp, Object constraints)} (see that method for more authoritative info).

     @param comp        the component to be added.
     @param constraints an object expressing layout constraints for this component.
     @throws com.obtuse.ui.MessageLabel.AugmentedIllegalArgumentException if {@code comp} is {@code null}
     or does not implement the {@link SelectableIndexCard} interface.
     */

    public void add( @NotNull Component comp, Object constraints ) {

        verifySelectable( comp );

        super.add( comp, constraints );

    }

    /**
     Add a component to the box.
     Throws an {@link com.obtuse.ui.MessageLabel.AugmentedIllegalArgumentException}
     if the component does not implement the {@link SelectableIndexCard} interface.
     Otherwise equivalent to {@link Container#add(Component comp, Object constraints, int index)} (see that method for more authoritative info).

     @param comp        the component to be added.
     @param constraints an object expressing layout constraints for this component.
     @param index       the position in the container's list at which to insert
     the component; <code>-1</code> means insert at the end component
     @throws com.obtuse.ui.MessageLabel.AugmentedIllegalArgumentException if {@code comp} is {@code null}
     or does not implement the {@link SelectableIndexCard} interface.
     */

    public void add( @NotNull Component comp, Object constraints, int index ) {

        verifySelectable( comp );

        super.add( comp, constraints, index );

    }

}