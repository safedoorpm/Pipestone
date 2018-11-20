package com.obtuse.ui.vsp;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.PermissiveLayoutManager;
import com.obtuse.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.obtuse.util.UniqueID;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.*;
import java.util.List;

/**
 A scrollable window that can be used to browse very large collections of things.
 */

public class VirtualScrollablePanel<E extends VirtualScrollableElement> extends JPanel {

    @SuppressWarnings("unused")
    public VirtualScrollablePanelModel getVirtualScrollablePanelModel() {

        return _virtualScrollablePanelModel;
    }

    @SuppressWarnings("unused")
    public VirtualScrollableLayoutManager getOurLayoutManager() {

        return _ourLayoutManager;
    }

    public static final String VPANEL_NAME = "vSpPanel";
    public static final String HSB_NAME = "hScrollBar";
    public static final String VSB_NAME = "vScrollBar";

    public class VirtualScrollableLayoutManager implements LayoutManager2 {

        private JScrollBar _vsb;
        private JScrollBar _hsb;
        private VirtualScrollablePanel<E> _ourTargetPanel;
        private JPanel _ourScrollableInnerPanel;
        private final VirtualScrollablePanelModel<E> _vModel;

        @SuppressWarnings("unused") private boolean _isValid = false;

        public VirtualScrollableLayoutManager(
                final @NotNull VirtualScrollablePanel<E> ourTargetPanel,
                @NotNull final VirtualScrollablePanelModel<E> vModel
        ) {
            super();

            _ourTargetPanel = ourTargetPanel;
            _vModel = vModel;

        }

        @Override
        public void addLayoutComponent( final String name, final Component comp ) {

            _isValid = false;

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.addLayoutComponent:  add( " + ObtuseUtil.enquoteToJavaString( name ) + ", " + comp + " )" );

            if ( comp == null ) {

                throw new NullPointerException( "VirtualScrollableLayoutManager.addLayoutComponent:  component is null" );

            }

            if ( VSB_NAME.equals( name ) || HSB_NAME.equals( name ) ) {

                if ( comp instanceof JScrollBar ) {

                    if ( VSB_NAME.equals( name ) ) {

                        if ( ((JScrollBar)comp).getOrientation() == Adjustable.VERTICAL ) {

                            if ( _vsb == null ) {

                                _vsb = (JScrollBar)comp;
                                _vsb.addAdjustmentListener(
                                        new AdjustmentListener() {

                                            @Override
                                            public void adjustmentValueChanged( final AdjustmentEvent e ) {

                                                vsbChanged();

                                            }

                                        }
                                );

                            } else {

                                throw new IllegalArgumentException( "VirtualScrollableLayoutManager.addLayoutComponent:  already have VSB" );

                            }

                        } else {

                            throw new IllegalArgumentException( "VirtualScrollableLayoutManager.addVSB:  VSB is not a vertical scroll bar" );

                        }

                    } else {

                        if ( ((JScrollBar)comp).getOrientation() == Adjustable.HORIZONTAL ) {

                            if ( _hsb == null ) {

                                _hsb = (JScrollBar)comp;

                            } else {

                                throw new IllegalArgumentException( "VirtualScrollableLayoutManager.addLayoutComponent:  already have HSB" );

                            }

                        } else {

                            throw new IllegalArgumentException( "VirtualScrollableLayoutManager.addVSB:  HSB is not a horizontal scroll bar" );

                        }

                    }

                }

            } else if ( VPANEL_NAME.equals( name ) ) {

                if ( comp instanceof JPanel ) {

                    if ( _ourScrollableInnerPanel == null ) {

                        _ourScrollableInnerPanel = (JPanel)comp;
                        _ourScrollableInnerPanel.setLayout( new PermissiveLayoutManager() );

                    } else {

                        throw new IllegalArgumentException( "VirtualScrollableLayoutManager.addLayoutComponent:  already have VPANEL" );

                    }

                }

            } else {

                throw new IllegalArgumentException( "VirtualScrollableLayoutManager.addLayoutComponent:  unknown name " + ObtuseUtil.enquoteToJavaString( name ) );

            }

        }

        public void vsbChanged() {

            Logger.logMsg(
                    "the vertical scrollbar has changed:  " +
                    "min=" + _vsb.getMinimum() + "," +
                    "value=" + _vsb.getValue() + "," +
                    "extent=" + _vsb.getModel().getExtent() + "," +
                    "value+extent=" + ( _vsb.getValue() + _vsb.getModel().getExtent() ) + "," +
                    "max=" + _vsb.getMaximum() + "," +
                    "adjusting=" + _vsb.getModel().getValueIsAdjusting()
            );

            if ( !_vsb.getModel().getValueIsAdjusting() ) {

                Logger.logMsg( "done adjusting" );

            }

        }

        @Override
        public void removeLayoutComponent( final Component comp ) {

            Logger.logMsg( "VirtualScrollableLayoutManager.removeLayoutComponent:  remove( " + comp + " )" );

            throw new IllegalArgumentException( "VirtualScrollableLayoutManager.removeLayoutComponent:  unsupported operation" );

        }

        @Override
        public Dimension preferredLayoutSize( final Container parent ) {

            Dimension dimension = parent.getPreferredSize();

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.preferredLayoutSize:  " + ObtuseUtil.fDim( dimension ) );

            return null;
        }

        @Override
        public Dimension minimumLayoutSize( final Container parent ) {

            Dimension dimension = new Dimension( 100, 100 );

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.minimumLayoutSize:  " + ObtuseUtil.fDim( dimension ) );

            return dimension;
        }

        @Override
        public void layoutContainer( final Container parent ) {

            Logger.pushNesting( ">>>" );

            try {

                Logger.logMsg( "VirtualScrollableLayoutManager.layoutContainer:  working" );

                if ( parent != _ourTargetPanel ) {

                    throw new HowDidWeGetHereError( "VirtualScrollableLayoutManager.layoutContainer:  wrong container (expected " + _ourTargetPanel + ", got asked to layout " + parent + ")" );

                }

                if ( _vsb == null || _hsb == null || _ourScrollableInnerPanel == null ) {

                    throw new IllegalArgumentException(
                            "VirtualScrollableLayoutManager.addLayoutComponent:  not all components have reported in (" +
                            "vsb is " + ( _vsb == null ? "absent" : "present" ) + ", " +
                            "hsb is " + ( _hsb == null ? "absent" : "present" ) + ", " +
                            "vPanel is " + ( _ourScrollableInnerPanel == null ? "absent" : "present" ) +
                            ")"
                    );

                }

                Dimension hsbPrefSize = _hsb.getPreferredSize();
                Dimension vsbPrefSize = _vsb.getPreferredSize();
                Dimension vPanelPrefSize = _ourScrollableInnerPanel.getPreferredSize();

                if ( _verbose ) Logger.logMsg( "vPanel is " + ObtuseUtil.fDim( _ourScrollableInnerPanel.getSize() ) + ", pref=" + ObtuseUtil.fDim( vPanelPrefSize ) );
                if ( _verbose ) Logger.logMsg( "VSB is " + ObtuseUtil.fDim( _vsb.getSize() ) + ", pref=" + ObtuseUtil.fDim( vsbPrefSize ) );
                if ( _verbose ) Logger.logMsg( "HSB is " + ObtuseUtil.fDim( _hsb.getSize() ) + ", pref=" + ObtuseUtil.fDim( hsbPrefSize ) );

                final int vsbWidth = vsbPrefSize.width; // _vsb.getWidth();
                final int hsbHeight = hsbPrefSize.height; // _hsb.getHeight();
                final int cWidth = parent.getWidth();
                final int cHeight = parent.getHeight();

                final int vsbHeight = cHeight - hsbHeight;
                final int hsbWidth = cWidth - vsbWidth;

                final int vPanelWidth = cWidth - vsbWidth;
                final int vPanelHeight = cHeight - hsbHeight;

                setBounds( VPANEL_NAME, _ourScrollableInnerPanel, new Rectangle( 0, 0, vPanelWidth, vPanelHeight ) );
                setBounds( VSB_NAME, _vsb, new Rectangle( vPanelWidth, 0, vsbWidth, vsbHeight ) );
                setBounds( HSB_NAME, _hsb, new Rectangle( 0, vPanelHeight, hsbWidth, hsbHeight ) );

                VirtualScrollablePanelModel.CurrentGoals<E> currentGoals = _vModel.getCurrentGoals();
                List<VirtualScrollableElementModel<E>> visibleElements = currentGoals.visibleElements();
                int nVisibleElements = visibleElements.size();

                for ( Component c : _ourScrollableInnerPanel.getComponents() ) {

                    c.setVisible( false );

                }

                _ourScrollableInnerPanel.removeAll();

                int nRendered = 0;
                Insets in = _ourScrollableInnerPanel.getInsets();
                int widestRenderedElementView = 0;

                if ( visibleElements.isEmpty() ) {

                    // Nothing is visible regardless of where the human scrolls to - we're pretty much done here.

                    ObtuseUtil.doNothing();

                } else {

                    SortedMap<UniqueID, ElementView<E>> assignedElementViewMapping = new TreeMap<>();
                    _vModel.allocateElementViews( visibleElements, assignedElementViewMapping );

                    int y = in.top;
                    Logger.logMsg( "starting at row " + _vsb.getValue() );

    //                int count = 0;
                    for (
//                            int ix = currentGoals.elementIndexOfTopRow();
                            int ix = _vsb.getValue();
                            ix < nVisibleElements && y < vPanelHeight - in.bottom;
                            ix += 1
                    ) { // VirtualScrollableElementModel<E> elementModel : visibleElements ) {

                        VirtualScrollableElementModel<E> elementModel = visibleElements.get( ix );

//                        if ( y >= vPanelHeight - in.bottom ) {
//
//                            Logger.logMsg( "bailing after laying out " + ObtuseUtil.pluralize( nRendered, "element" ) );
//
//                            break;
//
//                        }

    //                    count += 1;

                        ElementView<E> elementView = assignedElementViewMapping.get( elementModel.getUniqueID() );
                        if ( elementView == null ) {

                            throw new HowDidWeGetHereError(
                                    "VirtualScrollableLayoutManager.layoutContainer:  " +
                                    "id " + elementModel.getUniqueID().format() +
                                    " did not get an element view assigned to it"
                            );

                        }

                        Component asComponent = elementView.asComponent();

                        // This is always true since we start by emptying the inner scrollable panel.
                        // This might prove useful in the future and is cheap to do so . . .

                        if ( asComponent.getParent() != _ourScrollableInnerPanel ) {

                            _ourScrollableInnerPanel.add( asComponent );

                        }

                        asComponent.setVisible( true );

                        elementView.fill( elementModel );
                        _vModel.noteViewFilled( elementView );

                        widestRenderedElementView = Math.max(
                                widestRenderedElementView,
                                elementView.asComponent().getWidth()
                        );

                        Dimension prefElementSize = asComponent.getPreferredSize();
                        Rectangle bounds = new Rectangle(
                                in.left,
                                y,
                                vPanelWidth - ( in.left + in.right ),
                                prefElementSize.height
                        );
                        if ( _verbose ) Logger.logMsg( "setting bounds of element view to " + ObtuseUtil.fBounds( bounds ) );

//                        nRendered += 1;

                        asComponent.setBounds( bounds );

                        Logger.logMsg( "rendered row " + ix );

                        y += prefElementSize.height;

                        if ( y <= vPanelHeight - in.bottom ) {

                            nRendered += 1;

                        }

                    }

                }

                // %%% the behaviour of the vertical scrollbar could probably be hard-wired
                // to be strictly based on the number of visible elements.

                Logger.logMsg( "." );
                Logger.logMsg( "." );
                Logger.logMsg( "." );
                Logger.logMsg( "configuring vertical scroll bar" );

                _vModel.configureVerticalScrollBar(
                        _vsb,
                        nRendered,
                        vPanelHeight - ( in.top + in.bottom )
                );

                Logger.logMsg( "configuring horizontal scroll bar" );

                _vModel.configureHorizontalScrollBar(
                        _hsb, widestRenderedElementView,
                        vPanelWidth - ( in.left + in.right )
                );

                Logger.logMsg( "." );
                Logger.logMsg( "." );
                Logger.logMsg( "." );

                _isValid = true;

                if ( _verbose ) Logger.logMsg( "components are" );
                if ( _verbose ) for ( Component c : parent.getComponents() ) {
                    Logger.logMsg( "c " + c.getClass().getCanonicalName() + " @ " + ObtuseUtil.fBounds( c.getBounds() ) );
                }
                if ( _verbose ) Logger.logMsg( "done layout" );

            } finally {

                Logger.popNestingLevel( ">>>" );

            }

        }

        private void setBounds( String name, Component c, Rectangle r ) {

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.setBounds( " + ObtuseUtil.enquoteToJavaString( name ) + " to " + ObtuseUtil.fBounds( r ) );
            c.setBounds( r );

        }

        @Override
        public void addLayoutComponent( final Component comp, final Object constraints ) {

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.addLayoutComponent:  add( " + comp + ", " + ObtuseUtil.enquoteJavaObject( constraints ) + " )" );
            addLayoutComponent( (String)constraints, comp );

        }

        @Override
        public Dimension maximumLayoutSize( final Container target ) {

            Dimension dimension = target.getMaximumSize();

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.maximumLayoutSize:  " + ObtuseUtil.fDim( dimension ) );

            return dimension;

        }

        @Override
        public float getLayoutAlignmentX( final Container target ) {

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.getLayoutAlignmentX:  returning 0.5f" );

            return 0.5f;

        }

        @Override
        public float getLayoutAlignmentY( final Container target ) {

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.getLayoutAlignmentY:  returning 0.5f" );

            return 0.5f;

        }

        @Override
        public void invalidateLayout( final Container target ) {

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.invalidateLayout:  invalidated" );

            _isValid = false;

        }

    }

    public void setVerbose( boolean verbose ) {

        _verbose = verbose;

    }

    public boolean isVerbose() {

        return _verbose;

    }

    private boolean _verbose;

    private final VirtualScrollablePanelModel _virtualScrollablePanelModel;

    private boolean _ourLayoutManagerSet = false;
    private final VirtualScrollableLayoutManager _ourLayoutManager;

    public VirtualScrollablePanel( @NotNull final VirtualScrollablePanelModel<E> virtualScrollablePanelModel ) {
        super();

        // Make it impossible to change our layout manager once we've set it here.

        VirtualScrollableLayoutManager ourLayoutManager = new VirtualScrollableLayoutManager( this,
                                                                                              virtualScrollablePanelModel
        );

        setLayout( ourLayoutManager );

        _ourLayoutManagerSet = true;
        _ourLayoutManager = ourLayoutManager;

        JPanel virtualScrollablePanel = new JPanel();
        virtualScrollablePanel.setName( "vSP" );
        virtualScrollablePanel.setBorder( BorderFactory.createEtchedBorder() );
        add( virtualScrollablePanel, VPANEL_NAME );

        JScrollBar hScrollBar = new JScrollBar(
                Adjustable.HORIZONTAL,
                0,
                25,
                0,
                1000
        );
        add( hScrollBar, HSB_NAME );

        JScrollBar vScrollBar = new JScrollBar(
                Adjustable.VERTICAL, 0,
                40,
                0,
                1000
        );
        add( vScrollBar, VSB_NAME );

        _virtualScrollablePanelModel = virtualScrollablePanelModel;

        addComponentListener(
                new ComponentListener() {
                    @Override
                    public void componentResized( final ComponentEvent e ) {

                        Logger.logMsg( "VirtualScrollablePanel:  we have been resized - we are now " +
                                       ( isVisible() ? "visible" : "not visible" ) +
                                       " at " +
                                       ObtuseUtil.fBounds( getBounds() ) );

                        ObtuseUtil.doNothing();

                    }

                    @Override
                    public void componentMoved( final ComponentEvent e ) {

                        Logger.logMsg( "VirtualScrollablePanel:  we have been moved - we are now " +
                                       ( isVisible() ? "visible" : "not visible" ) +
                                       " at " +
                                       ObtuseUtil.fBounds( getBounds() ) );

                        ObtuseUtil.doNothing();

                    }

                    @Override
                    public void componentShown( final ComponentEvent e ) {

                        Logger.logMsg( "VirtualScrollablePanel:  we have been shown - we are now " +
                                       ( isVisible() ? "visible" : "not visible" ) +
                                       " at " +
                                       ObtuseUtil.fBounds( getBounds() ) );

                        ObtuseUtil.doNothing();

                    }

                    @Override
                    public void componentHidden( final ComponentEvent e ) {

                        Logger.logMsg( "VirtualScrollablePanel:  we have been hidden - we are now " +
                                       ( isVisible() ? "visible" : "not visible" ) +
                                       " at " +
                                       ObtuseUtil.fBounds( getBounds() ) );

                        ObtuseUtil.doNothing();

                    }

                }
        );

    }

    public void setLayout( LayoutManager layout ) {

        if ( _ourLayoutManagerSet ) {

            throw new IllegalArgumentException(
                    "VirtualScrollablePanel.setLayout:  cannot change layout manager once our layout manager has been installed" );

        }

        super.setLayout( layout );

    }

    public static class DefaultVirtualScrollablePanelModel<E extends VirtualScrollableElement>
            implements VirtualScrollablePanelModel<E> {

        private SortedMap<UniqueID,ElementView<E>> _previouslyAssignedElementViews = new TreeMap<>();
        private java.util.List<ElementView<E>> _emptyElementModels = new ArrayList<>();

        private final java.util.List<VirtualScrollableElementModel<E>> _elementModelsList = new ArrayList<>();
        private final SortedMap<UniqueID,VirtualScrollableElementModel<E>> _elementModelsMap = new TreeMap<>();
        private final ElementView.ElementViewFactory<E> _elementViewFactory;

        /**
         The number of visible elements that the most recently obtained {@link CurrentGoals} instance reported.
         */

        private int _nVisibleElements = 0;

        /**
         The number of element views that were actually filled and rendered during the latest (re-)layout of the inner scrollable panel.
         */

        private int _nElementViewsFilledAndRendered = 0;

        public DefaultVirtualScrollablePanelModel(
                @NotNull Collection<VirtualScrollableElementModel<E>> elements,
                @NotNull final ElementView.ElementViewFactory<E> elementViewFactory
        ) {
            super();
            _elementViewFactory = elementViewFactory;

            addElementModels( elements );

        }

        public void addElementModel( final VirtualScrollableElementModel<E> elementModel ) {

            if ( !_elementModelsMap.containsKey( elementModel.getUniqueID() ) ) {

                _elementModelsList.add( elementModel );
                _elementModelsMap.put( elementModel.getUniqueID(), elementModel );

            }

        }

        public void addElementModels( @NotNull final Collection<VirtualScrollableElementModel<E>> elementModels ) {

            for ( VirtualScrollableElementModel<E> elementModel : elementModels ) {

                elementModel.setPanelModel( this );
                addElementModel( elementModel );

            }

        }

        @Override
        public void allocateElementViews(
                @NotNull Collection<VirtualScrollableElementModel<E>> dataElements,
                @NotNull SortedMap<UniqueID,ElementView<E>> assignedElementViewMapping
        ) {

            // We're starting a new round of deciding what to actually render.

            _nElementViewsFilledAndRendered = 0;

//            java.util.List<ElementView<E>> assignedElementViews = new ArrayList<>();

            java.util.List<UniqueID> stillNeedViews = new ArrayList<>();

            HashMap<UniqueID,VirtualScrollableElementModel<E>> idToElementModelMap = new HashMap<>();

            // Nobody has a view officially assigned to them.

            assignedElementViewMapping.clear();

            // Need a set of the ids that were previously assigned element views.

            HashSet<UniqueID> oldIdsSet = new HashSet<>( _previouslyAssignedElementViews.keySet() );

            // Remember how many elements are in the _previouslyAssignedElementViews map.

            int existingAssignmentCount = _previouslyAssignedElementViews.size();

            // Reconfirm element views that were previously assigned to the ids we've been asked to get views for.

            for ( VirtualScrollableElementModel<E> elementModel : dataElements ) {

                UniqueID id = elementModel.getUniqueID();
                idToElementModelMap.put( id, elementModel );
                ElementView<E> previouslyAssigned = _previouslyAssignedElementViews.get( id );
                if ( previouslyAssigned == null ) {

                    stillNeedViews.add( id );

                } else {

                    assignedElementViewMapping.put( id, previouslyAssigned );
//                    assignedElementViews.add( previouslyAssigned );
                    oldIdsSet.remove( id );

                }

            }

            ObtuseUtil.doNothing();

            // If we already have them, assign completely empty views to ids that don't yet have views.

            for ( UniqueID id : List.copyOf( stillNeedViews ) ) {

                if ( _emptyElementModels.isEmpty() ) {

                    break;

                } else {

                    ElementView<E> availableView = _emptyElementModels.remove( 0 );
                    UniqueID firstId = stillNeedViews.remove( 0 );
                    if ( firstId.equals( id ) ) {

                        availableView.setUniqueID( id );
                        assignedElementViewMapping.put( id, availableView );
//                        assignedElementViews.add( availableView );

                    } else {

                        throw new HowDidWeGetHereError(
                                "DefaultVirtualScrollablePanelModel.allocateElementViews:  " +
                                "expected first id to be " + firstId.format() + " but it was " + id.format()
                        );

                    }

                }

            }

            ObtuseUtil.doNothing();

            // If there are any elements needing views,
            // take them randomly from the previously assigned views that are not needed right now.

            List<UniqueID> oldIdsList = new ArrayList<>( oldIdsSet );
            while ( !stillNeedViews.isEmpty() && !oldIdsList.isEmpty() ) {

                int ix = RandomCentral.nextInt( oldIdsList.size() );
                UniqueID oldId = oldIdsList.remove( ix );
                ElementView<E> availableView = _previouslyAssignedElementViews.remove( oldId );
                UniqueID id = stillNeedViews.remove( 0 );
                availableView.setUniqueID( id );
                _previouslyAssignedElementViews.put( id, availableView );
                assignedElementViewMapping.put( id, availableView );
//                assignedElementViews.add( availableView );

            }

            // We should still have EXACTLY the same number of views in the _previouslyAssignedElementViews map.

            if ( existingAssignmentCount != _previouslyAssignedElementViews.size() ) {

                // Oops!!!

                throw new HowDidWeGetHereError(
                        "DefaultVirtualScrollablePanelModel.allocateElementViews:  " +
                        "size of _previouslyAssignedElementViews changed from " + existingAssignmentCount +
                        " to " + _previouslyAssignedElementViews.size()
                );

            }

            ObtuseUtil.doNothing();

            // If there are STILL ids needing views then give them bright and shiny new views.

            while ( !stillNeedViews.isEmpty() ) {

                UniqueID id = stillNeedViews.remove( 0 );
                ElementView<E> newView = _elementViewFactory.createInstance( id, idToElementModelMap.get(id) );
                newView.setUniqueID( id );
                _previouslyAssignedElementViews.put( id, newView );
                assignedElementViewMapping.put( id, newView );

            }

            // We are DONE!

            ObtuseUtil.doNothing();

        }

        @Override
        public void noteViewFilled( final ElementView<E> elementView ) {

            _nElementViewsFilledAndRendered += 1;

        }

        @NotNull
        public CurrentGoals<E> getCurrentGoals() {

            java.util.List<VirtualScrollableElementModel<E>> rval = new ArrayList<>();
            for ( VirtualScrollableElementModel<E> em : _elementModelsList ) {

                if ( em.isVisible() ) {

                    rval.add( em );

                }

            }

            _nVisibleElements = rval.size();

            VirtualScrollableElementModel<E> nullEm = null;

            return new CurrentGoals<>( 0, rval );

        }

        @Override
        public boolean configureVerticalScrollBar(
                @NotNull final JScrollBar verticalScrollBar,
                final int nRenderedElementViews,
                final int innerScrollableWindowHeight
        ) {

            int min = 0;
            @SuppressWarnings("UnnecessaryLocalVariable") int extent = nRenderedElementViews;
            int max = _nVisibleElements;
            boolean didSomething = false;
            if ( verticalScrollBar.getMinimum() != min ) {

//                verticalScrollBar.setMinimum( min );
                didSomething = true;

            }

            if ( verticalScrollBar.getMaximum() != max ) {

//                verticalScrollBar.setMaximum( max );
                didSomething = true;

            }

            if ( verticalScrollBar.getBlockIncrement() != extent ) {

//                verticalScrollBar.setBlockIncrement( extent );
//                verticalScrollBar.getModel().setExtent( extent );
                didSomething = true;

            }

            if ( didSomething ) {

                verticalScrollBar.getModel().setRangeProperties(
                        verticalScrollBar.getValue(),
                        extent,
                        min,
                        max,
                        false
                );
                verticalScrollBar.setBlockIncrement( nRenderedElementViews );
                verticalScrollBar.setUnitIncrement( 1 );

            }

            Logger.logMsg( "model is now " + verticalScrollBar.getModel() );
            return didSomething;

        }

        @Override
        public boolean configureHorizontalScrollBar(
                @NotNull final JScrollBar horizontalScrollBar,
                final int widestRenderedElementView,
                final int innerScrollableWindowWidth
        ) {

            int min = 0;
            int extent = Math.min( widestRenderedElementView, innerScrollableWindowWidth );
            int max = Math.max( widestRenderedElementView, innerScrollableWindowWidth );
            boolean didSomething = false;
            if ( horizontalScrollBar.getMinimum() != min ) {

                horizontalScrollBar.setMinimum( min );
                didSomething = true;

            }

            if ( horizontalScrollBar.getMaximum() != max ) {

                horizontalScrollBar.setMaximum( max );
                didSomething = true;

            }

            if ( horizontalScrollBar.getBlockIncrement() != extent ) {

                horizontalScrollBar.setBlockIncrement( extent );
                didSomething = true;

            }

            return didSomething;

        }

        @Override
        public int getApproximateElementHeight() {

            return 10;

        }

        @Override
        public int getApproximateElementWidth() {

            return 50;

        }

        public String toString() {

            return "DefaultVirtualScrollablePanelModel( size=" + _elementModelsList.size() + " )";

        }

    }

    public static abstract class AbstractElementView<EV extends VirtualScrollableElement> extends JPanel implements ElementView<EV> {

        private final VirtualScrollableElementModel<EV> _elementModel;

        private UniqueID _clientUniqueID;

        protected AbstractElementView(
                @NotNull final UniqueID id,
                final @NotNull VirtualScrollableElementModel<EV> elementModel
        ) {
            super();

            _clientUniqueID = id;
            _elementModel = elementModel;

        }

        @Override
        @NotNull
        public final Optional<UniqueID> getUniqueID() {

            return Optional.ofNullable( _clientUniqueID );

        }

        @Override
        public final void setUniqueID( @Nullable UniqueID id ) {

            _clientUniqueID = id;

        }

        @Override
        public Component asComponent() {

            return this;

        }

    }

}
