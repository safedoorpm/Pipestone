package com.obtuse.ui.vsp;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.PermissiveLayoutManager;
import com.obtuse.util.Logger;
import com.obtuse.util.Measure;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.UniqueID;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

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
                                        e -> vsbChanged()
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

//            Logger.logMsg(
//                    "the vertical scrollbar has changed:  " +
//                    "min=" + _vsb.getMinimum() + "," +
//                    "value=" + _vsb.getValue() + "," +
//                    "extent=" + _vsb.getModel().getExtent() + "," +
//                    "value+extent=" + ( _vsb.getValue() + _vsb.getModel().getExtent() ) + "," +
//                    "max=" + _vsb.getMaximum() + "," +
//                    "adjusting=" + _vsb.getModel().getValueIsAdjusting()
//            );

//            if ( !_vsb.getModel().getValueIsAdjusting() ) {
//
//                Logger.logMsg( "done adjusting" );
//
//            }

        }

        @Override
        public void removeLayoutComponent( final Component comp ) {

            Logger.logMsg( "VirtualScrollableLayoutManager.removeLayoutComponent:  remove( " + comp + " )" );

            throw new IllegalArgumentException( "VirtualScrollableLayoutManager.removeLayoutComponent:  unsupported operation" );

        }

        @Override
        public Dimension minimumLayoutSize( final Container parent ) {

            Dimension dimension = new Dimension( 50, 100 );

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.minimumLayoutSize:  " + ObtuseUtil.fDim( dimension ) );

            return dimension;
        }

        @Override
        public Dimension preferredLayoutSize( final Container parent ) {

            Dimension dimension = new Dimension( 300, 100 ); // parent.getPreferredSize();

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.preferredLayoutSize:  " + ObtuseUtil.fDim( dimension ) );

            return dimension;
        }

        @Override
        public Dimension maximumLayoutSize( final Container target ) {

            Dimension dimension = new Dimension( 10000, 10000 ); // target.getMaximumSize();

            if ( _verbose ) Logger.logMsg( "VirtualScrollableLayoutManager.maximumLayoutSize:  " + ObtuseUtil.fDim( dimension ) );

            return dimension;

        }

        @Override
        public void layoutContainer( final Container parent ) {

            try ( Measure ignored = new Measure( "layout VSP" ) ) {

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

                VirtualScrollablePanelModel.CurrentGoals<E> currentGoals = _vModel.getCurrentGoals( new Dimension( vPanelWidth, vPanelHeight ) );
                List<VirtualScrollableElementModel<E>> visibleElementModels = currentGoals.visibleElementModels();
                int nVisibleElements = visibleElementModels.size();

                for ( Component c : _ourScrollableInnerPanel.getComponents() ) {

                    c.setVisible( false );

                }

                _ourScrollableInnerPanel.removeAll();

                int nRendered = 0;
                Insets in = _ourScrollableInnerPanel.getInsets();
                int widestRenderedElementView = 0;

                if ( visibleElementModels.isEmpty() ) {

                    // Nothing is visible regardless of where the human scrolls to - we're pretty much done here.

                    ObtuseUtil.doNothing();

                } else {

                    SortedMap<UniqueID, ElementView<E>> assignedElementViewMapping = new TreeMap<>();
                    _vModel.allocateElementViews( visibleElementModels, assignedElementViewMapping );

                    int y = in.top;
//                    Logger.logMsg( "starting at row " + _vsb.getValue() );

                    for (
                            int ix = _vsb.getValue();
                            ix < nVisibleElements && y < vPanelHeight - in.bottom;
                            ix += 1
                    ) {

                        VirtualScrollableElementModel<E> elementModel = visibleElementModels.get( ix );

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

                        asComponent.setBounds( bounds );

                        y += prefElementSize.height;

                        if ( y <= vPanelHeight - in.bottom ) {

                            nRendered += 1;

                        }

                    }

                }

                // %%% the behaviour of the vertical scrollbar could probably be hard-wired
                // to be strictly based on the number of visible elements.

//                Logger.logMsg( "." );
//                Logger.logMsg( "." );
//                Logger.logMsg( "." );
//                Logger.logMsg( "configuring vertical scroll bar" );

                _vModel.configureVerticalScrollBar(
                        _vsb,
                        nRendered,
                        vPanelHeight - ( in.top + in.bottom )
                );

//                Logger.logMsg( "configuring horizontal scroll bar" );

                _vModel.configureHorizontalScrollBar(
                        _hsb, widestRenderedElementView,
                        vPanelWidth - ( in.left + in.right )
                );

//                Logger.logMsg( "." );
//                Logger.logMsg( "." );
//                Logger.logMsg( "." );

                _isValid = true;

                if ( _verbose ) Logger.logMsg( "components are" );
                if ( _verbose ) for ( Component c : parent.getComponents() ) {
                    Logger.logMsg( "c " + c.getClass().getCanonicalName() + " @ " + ObtuseUtil.fBounds( c.getBounds() ) );
                }
                if ( _verbose ) Logger.logMsg( "done layout" );

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

    private boolean _ourLayoutManagerSet;
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

//                        Logger.logMsg( "VirtualScrollablePanel:  we have been resized - we are now " +
//                                       ( isVisible() ? "visible" : "not visible" ) +
//                                       " at " +
//                                       ObtuseUtil.fBounds( getBounds() ) );

                        ObtuseUtil.doNothing();

                    }

                    @Override
                    public void componentMoved( final ComponentEvent e ) {

//                        Logger.logMsg( "VirtualScrollablePanel:  we have been moved - we are now " +
//                                       ( isVisible() ? "visible" : "not visible" ) +
//                                       " at " +
//                                       ObtuseUtil.fBounds( getBounds() ) );

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

    public abstract static class AbstractElementView<EV extends VirtualScrollableElement> extends JPanel implements ElementView<EV> {

        private VirtualScrollableElementModel<EV> _elementModel;

//        private UniqueID _clientUniqueID;

        protected AbstractElementView(
                @NotNull final UniqueID id,
                final @NotNull VirtualScrollableElementModel<EV> elementModel
        ) {
            super();

//            _clientUniqueID = id;
            _elementModel = elementModel;

        }

        @Override
        @NotNull
        public final Optional<UniqueID> getModelUniqueID() {

            return Optional.ofNullable( _elementModel == null ? null : _elementModel.getUniqueID() );

        }

        @Override
        public final void setElementModel(
                @NotNull final VirtualScrollableElementModel<EV> elementModel
        ) {

//            _clientUniqueID = id;
            _elementModel = elementModel;

        }

        @Override
        @NotNull
        public VirtualScrollableElementModel<EV> getElementModel() {

            return _elementModel;

        }

        @Override
        public Component asComponent() {

            return this;

        }

    }

}
