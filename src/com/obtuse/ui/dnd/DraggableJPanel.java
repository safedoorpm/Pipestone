package com.obtuse.ui.dnd;

import com.obtuse.ui.SelectableImage;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A relatively easy to use JPanel that can be a DnD target and can have a 'background' image.
 */

public class DraggableJPanel extends SelectableImage implements MouseListener, FocusListener {

    public void mouseClicked( final MouseEvent e ) {

        //Since the user clicked on us, let's get focus!

        requestFocusInWindow();

    }

    public void mouseEntered( final MouseEvent e ) {

        ObtuseUtil.doNothing();

    }

    public void mouseExited( final MouseEvent e ) {

        ObtuseUtil.doNothing();

    }

    public void mousePressed( final MouseEvent e ) {

        ObtuseUtil.doNothing();

    }

    public void mouseReleased( final MouseEvent e ) {

        ObtuseUtil.doNothing();

    }

    public void focusGained( final FocusEvent e ) {

        super.focusGained( e );

        //Draw the component with a red border
        //indicating that it has focus.
        repaint();

    }

    public void focusLost( final FocusEvent e ) {

        super.focusLost( e );

        //Draw the component with a black border
        //indicating that it doesn't have focus.
        repaint();

    }

    public void setTransferHandler( final TransferHandler handler ) {

        if ( handler == null || handler instanceof OurTransferHandler ) {

            super.setTransferHandler( handler );

        } else {

            Logger.logErr( "not our transfer handler", new IllegalArgumentException( "just testing 2" ) );

        }

    }

    public void setDataFlavorHandlers( final AbstractDataFlavorHandler[] dataFlavorHandlers ) {

        TransferHandler tf = getTransferHandler();
        if ( tf instanceof OurTransferHandler ) {

            ( (OurTransferHandler)tf ).setDataFlavorHandlers( dataFlavorHandlers );

        } else {

            throw new IllegalArgumentException( "too early" );

        }

    }

    /**
     The OurTransferHandler has a constructor that specifies whether the
     instance will support only the copy action or the move action.
     This transfer handler does not support export.
     */

    private static class OurTransferHandler extends TransferHandler {

        final int _supportedActions;

        AbstractDataFlavorHandler[] _dataFlavorHandlers;

        public OurTransferHandler( final int supportedActions, final @NotNull AbstractDataFlavorHandler@Nullable[] dataFlavorHandlers ) {

            _supportedActions = supportedActions;

            _dataFlavorHandlers = dataFlavorHandlers == null ? null : Arrays.copyOf( dataFlavorHandlers, dataFlavorHandlers.length );

        }

        public boolean canImport( final TransferSupport support ) {

            AbstractDataFlavorHandler handler = getHandler( support );

            Logger.logMsg( "handler for " + support + " is " + handler );
            if ( handler == null ) {

                Logger.logMsg( "handler is null!" );

            }

            return handler != null;

        }

        public AbstractDataFlavorHandler getHandler( final TransferSupport support ) {

            Logger.logMsg( "" );
            Logger.logMsg( "pondering a drop" );

            // Do we support what is on offer?

            AbstractDataFlavorHandler dataFlavorHandler = null;

            if ( _dataFlavorHandlers == null ) {

                Logger.logMsg( "no data flavor handlers specified" );

                return null;

            } else {

                for ( AbstractDataFlavorHandler handler : _dataFlavorHandlers ) {

                    if ( support.isDataFlavorSupported( handler.getSupportedDataFlavor() ) ) {

                        Logger.logMsg( "we can handle data as " + handler );

                        if ( dataFlavorHandler == null ) {

                            dataFlavorHandler = handler;

                        }

                    }

                }

                Logger.logMsg( "data flavor handler after for loop is " + dataFlavorHandler );

                if ( dataFlavorHandler == null ) {

                    Logger.logMsg( "we cannot handle data" );

                    return null;

                }

            }

            Logger.logMsg( "dataFlavorHandler is " + dataFlavorHandler );

            int sourceDropActions = support.isDrop() ? support.getSourceDropActions() : DnDConstants.ACTION_COPY;

            // If the source actions include both ACTION_MOVE and ACTION_COPY then prefer move over copy.
            // As per comment in {@link java.awt.dnd.DropTargetDragEvent}.

            int cleanedSourceDropActions;
            if ( ( sourceDropActions & DnDConstants.ACTION_MOVE ) == DnDConstants.ACTION_MOVE ) {

                cleanedSourceDropActions = DnDConstants.ACTION_MOVE;

            } else if ( ( sourceDropActions & DnDConstants.ACTION_COPY ) == DnDConstants.ACTION_COPY ) {

                cleanedSourceDropActions = DnDConstants.ACTION_COPY;

            } else {

                throw new IllegalArgumentException( "neither a move nor a copy:  " + ObtuseUtil.hexvalue( sourceDropActions ) );

            }

            Logger.logMsg( "we can do " +
                           _supportedActions +
                           ", they want " +
                           ObtuseUtil.hexvalue( sourceDropActions ) +
                           " which was cleaned to " +
                           ObtuseUtil.hexvalue( cleanedSourceDropActions ) );
            showAvailableFlavours( support );

            // check if the source actions contain the desired action -
            // either copy or move, depending on what was specified when
            // this instance was created.

            boolean actionSupported = ( _supportedActions & cleanedSourceDropActions ) != 0;
            if ( actionSupported ) {

                Logger.logMsg( "we can do what needs to be done - " + cleanedSourceDropActions );

                if ( support.isDrop() ) {

                    support.setDropAction( cleanedSourceDropActions );

                }

                return dataFlavorHandler;

            }

            // the desired action is not supported, so reject the transfer

            Logger.logMsg( "no can do" );

            return null;

        }

        private void showAvailableFlavours( final TransferSupport support ) {

            DataFlavor[] dataFlavours = support.getTransferable().getTransferDataFlavors();
            SortedSet<String> uniqueMimeTypes = new TreeSet<>();
            for ( DataFlavor flavour : dataFlavours ) {

                String mimeType = flavour.getMimeType();
                uniqueMimeTypes.add( mimeType.contains( ";" ) ? mimeType.substring( 0, mimeType.indexOf( ';' ) ) : ( "###" + mimeType ) );

            }

            Logger.logMsg( "available flavours are " + Arrays.toString( uniqueMimeTypes.toArray( new String[0] ) ) );
        }

        public boolean importData( final TransferSupport support ) {

            Logger.logMsg( "importData( TransferSupport " + support + " )" );

            AbstractDataFlavorHandler handler = getHandler( support );
            if ( handler == null ) {

                Logger.logMsg( "We don't support importing of " + support );

                return false;

            } else {

                Logger.logMsg( "trying to import " + support );

                try {

                    if ( handler.handleTransfer( support ) ) {

                        return true;

                    }

                } catch ( Exception e ) {

                    Logger.logErr( "something went wrong in the handler", e );

                }

                return false;

            }

        }

        public final boolean importData( final JComponent jComponent, final Transferable t ) {

            Logger.logMsg( "importData( JComponent " + jComponent + ", Transferable " + t + " )" );

            return super.importData( jComponent, t );

        }

        public void setDataFlavorHandlers( final AbstractDataFlavorHandler[] dataFlavorHandlers ) {

            _dataFlavorHandlers = dataFlavorHandlers;

        }

    }

    /**
     Create a draggable {@link JPanel} using a specified {@link LayoutManager} and buffering strategy.

     @param how              what types of drag operations are to be supported.
     Specified using either {@link TransferHandler}.NONE or some combination of {@link TransferHandler}.COPY, {@link TransferHandler}.MOVE, {@link TransferHandler}.COPY_OR_MOVE, or {@link TransferHandler}.LINK or-ed together.
     @param layout           the layout manager to be used to manage the panel.
     @param isDoubleBuffered a boolean, true for double-buffering, which uses additional memory space to achieve fast, flicker-free updates.
     */

    public DraggableJPanel( final int how, final LayoutManager layout, final boolean isDoubleBuffered ) {

        super( layout, isDoubleBuffered );

        setFocusable( true );
        addMouseListener( this );
        addFocusListener( this );

        ActionMap actionMap = getActionMap();
        actionMap.put( TransferHandler.getCutAction().getValue( Action.NAME ), TransferHandler.getCutAction() );
        actionMap.put( TransferHandler.getCopyAction().getValue( Action.NAME ), TransferHandler.getCopyAction() );
        actionMap.put( TransferHandler.getPasteAction().getValue( Action.NAME ), TransferHandler.getPasteAction() );

        setTransferHandler( new OurTransferHandler( how, null ) );

    }

    /**
     Create a draggable {@link JPanel} using a {@link FlowLayout} manager and the specified buffering strategy.

     @param how              what types of drag operations are to be supported.
     Specified using either {@link TransferHandler}.NONE or some combination of {@link TransferHandler}.COPY, {@link TransferHandler}.MOVE, {@link TransferHandler}.COPY_OR_MOVE, or {@link TransferHandler}.LINK or-ed together.
     @param isDoubleBuffered a boolean, true for double-buffering, which uses additional memory space to achieve fast, flicker-free updates.
     */

    public DraggableJPanel( final int how, final boolean isDoubleBuffered ) {
        this( how, new FlowLayout(), isDoubleBuffered );

    }

    /**
     Create a draggable {@link JPanel} using double buffering and a specified {@link LayoutManager}.

     @param how    what types of drag operations are to be supported.
     Specified using either {@link TransferHandler}.NONE or some combination of {@link TransferHandler}.COPY, {@link TransferHandler}.MOVE, {@link TransferHandler}.COPY_OR_MOVE, or {@link TransferHandler}.LINK or-ed together.
     @param layout the layout manager to be used to manage the panel.
     */

    public DraggableJPanel( final int how, final LayoutManager layout ) {

        this( how, layout, true );
    }

    /**
     Create a draggable {@link JPanel} using double buffering and a {@link FlowLayout} manager.

     @param how what types of drag operations are to be supported.
     Specified using either {@link TransferHandler}.NONE or some combination of {@link TransferHandler}.COPY, {@link TransferHandler}.MOVE, {@link TransferHandler}.COPY_OR_MOVE, or {@link TransferHandler}.LINK or-ed together.
     */

    public DraggableJPanel( final int how ) {

        this( how, true );
    }

}
