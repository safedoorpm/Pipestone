package com.obtuse.ui.vsp;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.PermissiveLayoutManager;
import com.obtuse.util.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Optional;

/**
 A scrollable window that can be used to browse very large collections of things.
 */

public class VirtualScrollablePanel<E extends VirtualScrollableElement> extends JPanel {

    private final JPanel _virtualScrollablePanel;
    private final JScrollBar _hScrollBar;
    private final JScrollBar _vScrollBar;

    private boolean _verbose;

    //    private final VirtualScrollablePanelModel _virtualScrollablePanelModel;
    private final VirtualScrollablePanelModel<E> _virtualScrollablePanelModel;

    private boolean _ourLayoutManagerSet;
    private final VirtualScrollableLayoutManager _ourLayoutManager;

    private void handleMouseWheel(
            @NotNull final String name,
            @NotNull final JScrollBar scrollBar,
            final int unitsToScroll
    ) {

        //noinspection StatementWithEmptyBody
        if ( scrollBar.getValueIsAdjusting() ) {

//            Logger.logMsg( name + " scroll bar's value is adjusting!" );

        } else {

            ObtuseUtil.doNothing();

            int newValue = scrollBar.getValue() - unitsToScroll;

            scrollBar.setValue( newValue );

//            Logger.logMsg( name + " scroll bar's value is now " + scrollBar.getValue() );

            ObtuseUtil.doNothing();

        }

    }

    public VirtualScrollablePanel( @NotNull final VirtualScrollablePanelModel<E> virtualScrollablePanelModel ) {
        super();

        // Make it impossible to change our layout manager once we've set it here.

        VirtualScrollableLayoutManager ourLayoutManager = new VirtualScrollableLayoutManager( this );

        setLayout( ourLayoutManager );

        _ourLayoutManagerSet = true;
        _ourLayoutManager = ourLayoutManager;

        _virtualScrollablePanel = new JPanel() {
            public void paint( Graphics g ) {

                super.paint( g );

//                g.setColor( Color.BLACK );
//                g.drawLine( 0, 0, getWidth() - 1, getHeight() - 1 );

            }

        };

        _virtualScrollablePanel.setName( "vSP" );
        _virtualScrollablePanel.setBorder( BorderFactory.createEtchedBorder() );
        add( _virtualScrollablePanel, VPANEL_NAME );

        _virtualScrollablePanel.addMouseWheelListener(
                new MouseWheelListener() {

                    @Override
                    public void mouseWheelMoved( final MouseWheelEvent e ) {

//                        Logger.logMsg( "mouseWheelMoved:  " + e );

                        ObtuseUtil.doNothing();

                        if ( e.getScrollType() == MouseWheelEvent.WHEEL_BLOCK_SCROLL ) {

                            Logger.logMsg( "got a block scroll!" );

                            ObtuseUtil.doNothing();

                        } else {

                            boolean isVerticalScroll = !e.isShiftDown();
//                            Logger.logMsg(
//                                    "got a unit scroll, unitsToScroll=" + e.getUnitsToScroll() +
//                                    ", isVerticalScroll=" + isVerticalScroll
//                            );

                            if ( isVerticalScroll ) {

                                handleMouseWheel( "vertical", _vScrollBar, e.getUnitsToScroll() );

                                ObtuseUtil.doNothing();

                            } else {

                                handleMouseWheel( "horizontal", _hScrollBar, e.getUnitsToScroll() );

                                ObtuseUtil.doNothing();

                            }

                            ObtuseUtil.doNothing();

                        }

                    }

                }
        );

        _hScrollBar = new JScrollBar(
                Adjustable.HORIZONTAL,
                0,
                25,
                0,
                1000
        );
        add( _hScrollBar, HSB_NAME );

        _vScrollBar = new JScrollBar(
                Adjustable.VERTICAL, 0,
                40,
                0,
                1000
        );
        add( _vScrollBar, VSB_NAME );

        _virtualScrollablePanelModel = virtualScrollablePanelModel;

        addComponentListener(
                new ComponentListener() {
                    @Override
                    public void componentResized( final ComponentEvent e ) {

                        ObtuseUtil.doNothing();

                    }

                    @Override
                    public void componentMoved( final ComponentEvent e ) {

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

    public void setInnerBackground( @NotNull Color color ) {

        _virtualScrollablePanel.setBackground( color );

    }

    public void setInnerForeground( @NotNull Color color ) {

        _virtualScrollablePanel.setForeground( color );

    }

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

        private JScrollBar _vScrollBar;
        private JScrollBar _hScrollBar;
        private VirtualScrollablePanel<E> _ourTargetPanel;
        private JPanel _ourScrollableInnerPanel;

        @SuppressWarnings("unused") private boolean _isValid = false;

        private Stats _elementViewHeightStats = new Stats();

        public VirtualScrollableLayoutManager(
                final @NotNull VirtualScrollablePanel<E> ourTargetPanel
                /*,
                @NotNull final VirtualScrollablePanelModel<E> vModel*/
        ) {
            super();

            _ourTargetPanel = ourTargetPanel;

        }

        @Override
        public void addLayoutComponent( final String name, final Component comp ) {

            _isValid = false;

            if ( _verbose ) {

                Logger.logMsg(
                        "VirtualScrollableLayoutManager.addLayoutComponent:  " +
                        "add( " + ObtuseUtil.enquoteToJavaString( name ) + ", " + comp + " )"
                );

            }

            if ( comp == null ) {

                throw new NullPointerException(
                        "VirtualScrollableLayoutManager.addLayoutComponent:  component is null"
                );

            }

            if ( VSB_NAME.equals( name ) || HSB_NAME.equals( name ) ) {

                if ( comp instanceof JScrollBar ) {

                    if ( VSB_NAME.equals( name ) ) {

                        if ( ((JScrollBar)comp).getOrientation() == Adjustable.VERTICAL ) {

                            if ( _vScrollBar == null ) {

                                _vScrollBar = (JScrollBar)comp;
                                _vScrollBar.addAdjustmentListener(
                                        e -> vsbChanged()
                                );

                            } else {

                                throw new IllegalArgumentException(
                                        "VirtualScrollableLayoutManager.addLayoutComponent:  already have VSB"
                                );

                            }

                        } else {

                            throw new IllegalArgumentException(
                                    "VirtualScrollableLayoutManager.addVSB:  VSB is not a vertical scroll bar"
                            );

                        }

                    } else {

                        if ( ((JScrollBar)comp).getOrientation() == Adjustable.HORIZONTAL ) {

                            if ( _hScrollBar == null ) {

                                _hScrollBar = (JScrollBar)comp;

                            } else {

                                throw new IllegalArgumentException(
                                        "VirtualScrollableLayoutManager.addLayoutComponent:  already have HSB"
                                );

                            }

                        } else {

                            throw new IllegalArgumentException(
                                    "VirtualScrollableLayoutManager.addVSB:  HSB is not a horizontal scroll bar"
                            );

                        }

                    }

                }

            } else if ( VPANEL_NAME.equals( name ) ) {

                if ( comp instanceof JPanel ) {

                    if ( _ourScrollableInnerPanel == null ) {

                        _ourScrollableInnerPanel = (JPanel)comp;
                        _ourScrollableInnerPanel.setLayout( new PermissiveLayoutManager() );

                    } else {

                        throw new IllegalArgumentException(
                                "VirtualScrollableLayoutManager.addLayoutComponent:  already have VPANEL"
                        );

                    }

                }

            } else {

                throw new IllegalArgumentException(
                        "VirtualScrollableLayoutManager.addLayoutComponent:  " +
                        "unknown name " + ObtuseUtil.enquoteToJavaString( name )
                );

            }

        }

        public void vsbChanged() {

        }

        @Override
        public void removeLayoutComponent( final Component comp ) {

            Logger.logMsg( "VirtualScrollableLayoutManager.removeLayoutComponent:  remove( " + comp + " )" );

            throw new IllegalArgumentException(
                    "VirtualScrollableLayoutManager.removeLayoutComponent:  unsupported operation"
            );

        }

        @Override
        public Dimension minimumLayoutSize( final Container parent ) {

            Dimension dimension = new Dimension( 50, 100 );

            if ( _verbose ) {

                Logger.logMsg(
                        "VirtualScrollableLayoutManager.minimumLayoutSize:  " + ObtuseUtil.fDim( dimension )
                );

            }

            return dimension;
        }

        @Override
        public Dimension preferredLayoutSize( final Container parent ) {

            Dimension dimension = new Dimension( 300, 100 ); // parent.getPreferredSize();

            if ( _verbose ) {

                Logger.logMsg(
                        "VirtualScrollableLayoutManager.preferredLayoutSize:  " +
                        ObtuseUtil.fDim( dimension )
                );

            }

            return dimension;
        }

        @Override
        public Dimension maximumLayoutSize( final Container target ) {

            Dimension dimension = new Dimension( 10000, 10000 ); // target.getMaximumSize();

            if ( _verbose ) {

                Logger.logMsg(
                        "VirtualScrollableLayoutManager.maximumLayoutSize:  " +
                        ObtuseUtil.fDim( dimension )
                );

            }

            return dimension;

        }

        @Override
        public void layoutContainer( final Container parent ) {

            try ( Measure ignored = new Measure( "layout VSP" ) ) {

                if ( parent != _ourTargetPanel ) {

                    throw new HowDidWeGetHereError(
                            "VirtualScrollableLayoutManager.layoutContainer:  " +
                            "wrong container (expected " + _ourTargetPanel + ", got asked to layout " + parent + ")"
                    );

                }

                if ( _vScrollBar == null || _hScrollBar == null || _ourScrollableInnerPanel == null ) {

                    throw new IllegalArgumentException(
                            "VirtualScrollableLayoutManager.addLayoutComponent:  " +
                            "not all components have reported in (" +
                            "vsb is " + ( _vScrollBar == null ? "absent" : "present" ) + ", " +
                            "hsb is " + ( _hScrollBar == null ? "absent" : "present" ) + ", " +
                            "vPanel is " + ( _ourScrollableInnerPanel == null ? "absent" : "present" ) +
                            ")"
                    );

                }

                Dimension hsbPrefSize = _hScrollBar.getPreferredSize();
                Dimension vsbPrefSize = _vScrollBar.getPreferredSize();
                Dimension vPanelPrefSize = _ourScrollableInnerPanel.getPreferredSize();

                if ( _verbose ) {

                    Logger.logMsg(
                            "vPanel is " + ObtuseUtil.fDim( _ourScrollableInnerPanel.getSize() ) +
                            ", pref=" + ObtuseUtil.fDim( vPanelPrefSize )
                    );
                    Logger.logMsg(
                            "VSB is " + ObtuseUtil.fDim( _vScrollBar.getSize() ) +
                            ", pref=" + ObtuseUtil.fDim( vsbPrefSize )
                    );
                    Logger.logMsg(
                            "HSB is " + ObtuseUtil.fDim( _hScrollBar.getSize() ) +
                            ", pref=" + ObtuseUtil.fDim( hsbPrefSize )
                    );

                }

                final int vsbWidth = vsbPrefSize.width; // _vScrollBar.getWidth();
                final int hsbHeight = hsbPrefSize.height; // _hScrollBar.getHeight();
                final int cWidth = parent.getWidth();
                final int cHeight = parent.getHeight();

                final int vsbHeight = cHeight - hsbHeight;
                final int hsbWidth = cWidth - vsbWidth;

                final int vPanelWidth = cWidth - vsbWidth;
                final int vPanelHeight = cHeight - hsbHeight;

//                Logger.logMsg(
//                        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" +
//                        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
//                );

                // Signal to the panel model that we're about to layout their panel and it would be a
                // great time for the model's 'worldview' to be up-to-date and in sync with reality.

                _virtualScrollablePanelModel.checkForUpdates();

                setBounds(
                        VPANEL_NAME,
                        _ourScrollableInnerPanel,
                        new Rectangle( 0, 0, vPanelWidth, vPanelHeight )
                );
                setBounds(
                        VSB_NAME,
                        _vScrollBar,
                        new Rectangle( vPanelWidth, 0, vsbWidth, vsbHeight )
                );
                setBounds(
                        HSB_NAME,
                        _hScrollBar,
                        new Rectangle( 0, vPanelHeight, hsbWidth, hsbHeight )
                );

                VirtualScrollablePanelModel.CurrentGoals<E> currentGoals =
                        _virtualScrollablePanelModel.getCurrentGoals(
                                _vScrollBar.getValue(),
                                new Dimension( vPanelWidth, vPanelHeight )
                        );

                // If the current goals don't provide us with anything to show but there is something to see
                // with a bit of scrolling, try again with the first visible row set to the last actual row.
                // If there is simply nothing to see regardless of scrolling, continue and we'll handle that
                // case below.

                if ( !currentGoals.isAnythingVisible() && currentGoals.getScrollableElementsCount() > 0 ) {

                    // If this still yields an "nothing to see" result then we're going to bail out
                    // down below (i.e. leave the viewer with an empty panel).

                    currentGoals = _virtualScrollablePanelModel.getCurrentGoals(
                            currentGoals.getScrollableElementsCount() - 1,
                            new Dimension( vPanelWidth, vPanelHeight )
                    );

                    ObtuseUtil.doNothing();

                }

                for ( Component c : _ourScrollableInnerPanel.getComponents() ) {

                    c.setVisible( false );

                }

                _ourScrollableInnerPanel.removeAll();

                int nRendered = 0;

                Insets in = _ourScrollableInnerPanel.getInsets();
                int widestRenderedElementView = 0;

                // If we bail now then nothing will be visible in the inner panel.
                // Let's short circuit this game if we KNOW that there is nothing visible.

                if ( currentGoals.isAnythingVisible() ) {

                    // Tell _virtualScrollablePanelModel that we are starting a new round of allocating element views.
                    // This allows the _virtualScrollablePanelModel to recycle old views if they so choose.

                    _virtualScrollablePanelModel.startNewElementViewAllocationRound();

                    int y = in.top;

                    // The current goals simply MUST include the first visible element number.
                    // If this requirement is not met then it's better to explode here with a
                    // useful error message than to stumble into an NPE below.

                    if (
                            currentGoals.getFirstProvidedElementNumber() > currentGoals.getFirstVisibleElementNumber()
                    ) {

                        throw new IllegalArgumentException(
                                "VirtualScrollablePanel.layoutContainer:  " +
                                "first provided element number is " + currentGoals.getFirstProvidedElementNumber() +
                                " but the first element we need to display is " +
                                currentGoals.getFirstVisibleElementNumber()
                        );

                    }

                    for (
                            int ix = currentGoals.getFirstVisibleElementNumber();
                            ix <= currentGoals.getLastProvidedElementNumber() && y < vPanelHeight - in.bottom;
                            ix += 1
                    ) {

                        VirtualScrollableElementModel<E> elementModel = currentGoals.getElementAt( ix );

                        ElementView<E> elementView = _virtualScrollablePanelModel.createInstance( elementModel );
                        if ( elementView == null ) {

                            throw new HowDidWeGetHereError(
                                    "VirtualScrollableLayoutManager.layoutContainer:  " +
                                    "id " + elementModel.getUniqueId().format() +
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
                        _virtualScrollablePanelModel.noteViewFilled( elementView );

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
                        if ( _verbose ) {

                            Logger.logMsg( "setting bounds of element view to " + ObtuseUtil.fBounds( bounds ) );

                        }

                        asComponent.setBounds( bounds );

                        y += prefElementSize.height;

                        if ( y <= vPanelHeight - in.bottom ) {

                            nRendered += 1;

                        }

                    }

                    ObtuseUtil.doNothing();

                }

                _virtualScrollablePanelModel.configureVerticalScrollBar(
                        _vScrollBar,
                        nRendered,
                        currentGoals.getScrollableElementsCount()
                );

                _virtualScrollablePanelModel.configureHorizontalScrollBar(
                        _hScrollBar, widestRenderedElementView,
                        vPanelWidth - ( in.left + in.right )
                );

                _isValid = true;

                if ( _verbose ) {

                    Logger.logMsg( "components are" );
                    for ( Component c : parent.getComponents() ) {

                        Logger.logMsg(
                                "c " + c.getClass().getCanonicalName() + " @ " + ObtuseUtil.fBounds( c.getBounds() )
                        );

                    }

                    Logger.logMsg( "done layout" );
                }


            }

        }

        private void setBounds( String name, Component c, Rectangle r ) {

            if ( _verbose ) {

                Logger.logMsg(
                        "VirtualScrollableLayoutManager.setBounds( " + ObtuseUtil.enquoteToJavaString( name ) +
                        " to " + ObtuseUtil.fBounds( r )
                );

            }

            c.setBounds( r );

        }

        @Override
        public void addLayoutComponent( final Component comp, final Object constraints ) {

            if ( _verbose ) {

                Logger.logMsg(
                        "VirtualScrollableLayoutManager.addLayoutComponent:  " +
                        "add( " + comp + ", " + ObtuseUtil.enquoteJavaObject( constraints ) + " )"
                );

            }

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

    public void setLayout( LayoutManager layout ) {

        if ( _ourLayoutManagerSet ) {

            throw new IllegalArgumentException(
                    "VirtualScrollablePanel.setLayout:  " +
                    "cannot change layout manager once our layout manager has been installed"
            );

        }

        super.setLayout( layout );

    }

    public abstract static class AbstractElementView<EV extends VirtualScrollableElement>
            extends JPanel implements ElementView<EV> {

        private VirtualScrollableElementModel<EV> _elementModel;

        protected AbstractElementView(
                final @NotNull VirtualScrollableElementModel<EV> elementModel
        ) {
            super();

            _elementModel = elementModel;

        }

        @Override
        @NotNull
        public final Optional<UniqueId> getModelUniqueId() {

            return Optional.ofNullable( _elementModel == null ? null : _elementModel.getUniqueId() );

        }

        @Override
        public final void setElementModel(
                @NotNull final VirtualScrollableElementModel<EV> elementModel
        ) {

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
