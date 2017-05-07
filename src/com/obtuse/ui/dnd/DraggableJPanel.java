package com.obtuse.ui.dnd;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A relatively easy to use JPanel that can be a DnD target.
 */

public class DraggableJPanel extends JPanel {

    private Image _backgroundImage;

    //    private OurTransferHandler _transferHandler;
//    private Image _backgroundImage;

//    public enum OperationType {
//        UNKNOWN,
//	DRAG_TO,
//	DRAG_FROM,
//	COPY_FROM,
//	PASTE_TO
//    }
//
//    public interface OperationValidator {
//
//        void canWeDoThat( OperationType operationType, boolean yesWeCan );
//
//    }

    public void setTransferHandler( TransferHandler handler ) {

//        Logger.logErr( "call to setTransferHandler to " + handler, new IllegalArgumentException( "just testing 1" ) );

        if ( handler == null || handler instanceof OurTransferHandler ) {

            super.setTransferHandler( handler );

	} else {

            Logger.logErr( "not our transfer handler", new IllegalArgumentException( "just testing 2" ) );

	}

    }

    public void setDataFlavorHandlers( AbstractDataFlavorHandler[] dataFlavorHandlers ) {

        TransferHandler tf = getTransferHandler();
        if ( tf instanceof OurTransferHandler ) {

	    ((OurTransferHandler)tf).setDataFlavorHandlers( dataFlavorHandlers );

	} else {

            throw new IllegalArgumentException( "too early" );

	}

    }

    public void setBackgroundImage( Image backgroundImage ) {

        Logger.logMsg( "setting background image to " + backgroundImage );

        _backgroundImage = backgroundImage;
        repaint();

    }

    protected void paintComponent(Graphics g) {

	super.paintComponent(g);
	if ( _backgroundImage != null ) {

	    g.drawImage( _backgroundImage, 0, 0, null );

	}

    }

    /**
     * The OurTransferHandler has a constructor that specifies whether the
     * instance will support only the copy action or the move action.
     * This transfer handler does not support export.
     */

    private class OurTransferHandler extends TransferHandler {

	int _supportedActions;

	AbstractDataFlavorHandler[] _dataFlavorHandlers;

	public OurTransferHandler( int supportedActions, @Nullable AbstractDataFlavorHandler[] dataFlavorHandlers ) {

	    _supportedActions = supportedActions;

	    _dataFlavorHandlers = dataFlavorHandlers == null ? null : Arrays.copyOf( dataFlavorHandlers, dataFlavorHandlers.length );

	}

	public boolean canImport( TransferSupport support ) {

	    AbstractDataFlavorHandler handler = getHandler( support );

	    Logger.logMsg( "handler for " + support + " is " + handler );
	    if ( handler == null ) {

	        Logger.logMsg( "handler is null!" );

	    }

	    return handler != null;

	}

	public AbstractDataFlavorHandler getHandler( TransferSupport support ) {

	    Logger.logMsg( "" );
	    Logger.logMsg( "pondering a drop" );

	    // for the demo, we will only support drops (not clipboard paste)

	    if ( !support.isDrop() ) {

		Logger.logMsg( "not a drop!" );

//		_canWeDoThatCheckBox.setSelected( false );

		return null;

	    }

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

//	                break;

		    }

		}

		Logger.logMsg( "data flavor handler after for loop is " + dataFlavorHandler );

		if ( dataFlavorHandler == null ) {

		    Logger.logMsg( "we cannot handle data" );

		    return null;

		}

	    }

//	    if (
//		    !support.isDataFlavorSupported( DataFlavor.stringFlavor )
//		    &&
//		    !support.isDataFlavorSupported( DataFlavor.javaFileListFlavor )
//		    &&
//		    !support.isDataFlavorSupported( DataFlavor.imageFlavor )
//		    ) {
//
//		Logger.logMsg( "that is not supported" );
//		showAvailableFlavours( support );
//
//		_canWeDoThatCheckBox.setSelected( false );
//
//		return false;
//
//	    }

	    Logger.logMsg( "dataFlavorHandler is " + dataFlavorHandler );

	    int sourceDropActions = support.getSourceDropActions();

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

	    Logger.logMsg( "we can do " + _supportedActions + ", they want " + ObtuseUtil.hexvalue( sourceDropActions ) + " which was cleaned to " + ObtuseUtil.hexvalue( cleanedSourceDropActions ) );
	    showAvailableFlavours( support );

	    // check if the source actions contain the desired action -
	    // either copy or move, depending on what was specified when
	    // this instance was created.

//	    cleanedSourceDropActions = sourceDropActions;

	    boolean actionSupported = ( _supportedActions & cleanedSourceDropActions ) != 0;
	    if ( actionSupported ) {

		Logger.logMsg( "we can do what needs to be done - " + cleanedSourceDropActions );

		support.setDropAction( cleanedSourceDropActions );

//		_canWeDoThatCheckBox.setSelected( true );

		return dataFlavorHandler;

	    }

	    // the desired action is not supported, so reject the transfer

	    Logger.logMsg( "no can do" );

//	    _canWeDoThatCheckBox.setSelected( false );

	    return null;

	}

	private void showAvailableFlavours( TransferSupport support ) {

	    DataFlavor[] dataFlavours = support.getTransferable().getTransferDataFlavors();
	    String[] mimeTypes = new String[ dataFlavours.length];
//            Logger.logMsg( "" + dataFlavours.length + " data flavours" );
	    int ix = 0;
	    SortedSet<String> uniqueMimeTypes = new TreeSet<String>();
	    for ( DataFlavor flavour : dataFlavours ) {

		String mimeType = flavour.getMimeType();
		uniqueMimeTypes.add( mimeType.contains( ";" ) ? mimeType.substring( 0, mimeType.indexOf( ';' ) ) : ( "###" + mimeType ) );

	    }

	    Logger.logMsg( "available flavours are " + Arrays.toString( uniqueMimeTypes.toArray( new String[uniqueMimeTypes.size()] ) ) );
	}

	public boolean importData( TransferSupport support ) {

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

//	        DropLocation dl = support.getDropLocation();
//
//	        return doImport( dl, support );

		return false;

	    }

	}

//	public final boolean importData( TransferHandler.TransferSupport support ) {
//
//	    if ( !canImport( support ) ) {
//
//	        Logger.logMsg( "importData:  unable to import" );
//
//	        return false;
//
//	    }
//
//	    DropLocation dl = support.getDropLocation();
//
//	    return doImport( dl, support );
//
//	}

	public final boolean importData( JComponent jComponent, Transferable t ) {

	    Logger.logMsg( "importData( JComponent " + jComponent + ", Transferable " + t + " )" );

	    return super.importData( jComponent, t );

	}

	public void setDataFlavorHandlers( AbstractDataFlavorHandler[] dataFlavorHandlers ) {

	    _dataFlavorHandlers = dataFlavorHandlers;

	}

//	public boolean importData( TransferHandler.TransferSupport support ) {
//
//	    // if we cannot handle the import, say so
//
//	    if ( !canImport( support ) ) {
//
//		_areWeGoodToGoCheckBox.setSelected( false );
//		return false;
//
//	    }
//
//	    // fetch the drop location
//	    DropLocation dl = support.getDropLocation();
//
////            int index = dl.getIndex();
//
//	    // fetch the data and bail if this fails
//	    String data;
//	    try {
//
//		if ( support.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) ) {
//
//		    data = "<<< file list >>>";
//
//		} else if ( support.isDataFlavorSupported( DataFlavor.imageFlavor ) ) {
//
//		    data = "<<< image >>>";
//
//		} else if ( support.isDataFlavorSupported( DataFlavor.stringFlavor ) ) {
//
//		    data = (String) support.getTransferable().getTransferData( DataFlavor.stringFlavor );
//
//		} else {
//
//		    Logger.logMsg( "unsupported flavour:  " + support );
//		    data = "<<< unsupported >>>";
//
//		}
//
//	    } catch ( UnsupportedFlavorException e ) {
//
//		_areWeGoodToGoCheckBox.setSelected( false );
//
//		return false;
//
//	    } catch ( java.io.IOException e ) {
//
//		_areWeGoodToGoCheckBox.setSelected( false );
//
//		return false;
//
//	    }
//
//	    Logger.logMsg( "got something to drop:  \"" + data + "\"" );
//	    _areWeGoodToGoCheckBox.setSelected( true );
//
////            JList list = (JList) support.getComponent();
////            DefaultListModel model = (DefaultListModel) list.getModel();
////            model.insertElementAt( data, index );
////
////            Rectangle rect = list.getCellBounds( index, index );
////            list.scrollRectToVisible( rect );
////            list.setSelectedIndex( index );
////            list.requestFocusInWindow();
//
//	    return true;
//
//	}

    }

//    public interface OurTransferHandler {
//
//    }

//    public enum Mode {
//        NONE,
//	COPY,
//	COPY_OR_MOVE,
//	LINK,
//	MOVE;
//    }

    /**
     Create a draggable {@link JPanel} using a specified {@link LayoutManager} and buffering strategy.
     @param how what types of drag operations are to be supported.
     Specified using either {@link TransferHandler}.NONE or some combination of {@link TransferHandler}.COPY, {@link TransferHandler}.MOVE, {@link TransferHandler}.COPY_OR_MOVE, or {@link TransferHandler}.LINK or-ed together.
     @param layout the layout manager to be used to manage the panel.
     @param isDoubleBuffered a boolean, true for double-buffering, which uses additional memory space to achieve fast, flicker-free updates.
     */

    public DraggableJPanel( int how, LayoutManager layout, boolean isDoubleBuffered ) {
        super( layout, isDoubleBuffered );

//	_transferHandler = new OurTransferHandler( how, new DataFlavor[]{ DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor } );
//		new AbstractDataFlavorHandler[] {
//
//			new AbstractDataFlavorHandler( DataFlavor.imageFlavor ) {
//
//			    @Override
//			    public boolean handleTransfer( TransferHandler.TransferSupport ts ) {
//
//				Logger.logMsg( "handling an image" );
//
//				try {
//
//				    Object transferData = ts.getTransferable().getTransferData( DataFlavor.imageFlavor );
//				    Logger.logMsg( "transferData is " + ( transferData == null ? "<<null>>" : transferData.toString() ) );
////				    Logger.logMsg( "handling a file list:  " + Arrays.toString( files ) );
//
//				} catch ( UnsupportedFlavorException | IOException e ) {
//
//				    Logger.logErr( "unable to import data", e );
//
//				    return false;
//
//				}
//
//				return true;
//
//			    }
//
//			},
//
//			new AbstractDataFlavorHandler( DataFlavor.javaFileListFlavor ) {
//
//			    @Override
//			    public boolean handleTransfer( TransferHandler.TransferSupport ts ) {
//
//				try {
//
//				    Object transferData = ts.getTransferable().getTransferData( DataFlavor.javaFileListFlavor );
//				    File[] files = (File[])((java.util.List)transferData).toArray();
//				    Logger.logMsg( "files array is " + files.getClass() );
//				    Logger.logMsg( "handling a file list:  " + Arrays.toString( files ) );
//
//				} catch ( UnsupportedFlavorException | IOException e ) {
//
//				    Logger.logErr( "unable to import data", e );
//
//				    return false;
//
//				}
//
//				return true;
//
//			    }
//
//			},
//
//			new AbstractDataFlavorHandler( DataFlavor.stringFlavor ) {
//
//			    @Override
//			    public boolean handleTransfer( TransferHandler.TransferSupport ts ) {
//
//				try {
//
//				    Logger.logMsg( "handling a string:  " + ObtuseUtil.enquoteForJavaString( (String)ts.getTransferable().getTransferData( DataFlavor.stringFlavor ) ) );
//
//				} catch ( UnsupportedFlavorException | IOException e ) {
//
//				    Logger.logErr( "unable to import data", e );
//
//				    return false;
//
//				}
//
//				return true;
//
//			    }
//
//			}
//
//		}
//	);

	setTransferHandler( new OurTransferHandler( how, null ) );

    }

    /**
     Create a draggable {@link JPanel} using a {@link FlowLayout} manager and the specified buffering strategy.
     @param how what types of drag operations are to be supported.
     Specified using either {@link TransferHandler}.NONE or some combination of {@link TransferHandler}.COPY, {@link TransferHandler}.MOVE, {@link TransferHandler}.COPY_OR_MOVE, or {@link TransferHandler}.LINK or-ed together.
     @param isDoubleBuffered a boolean, true for double-buffering, which uses additional memory space to achieve fast, flicker-free updates.
     */

    public DraggableJPanel( int how, boolean isDoubleBuffered ) {
        this( how, new FlowLayout(), isDoubleBuffered );
    }

    /**
     Create a draggable {@link JPanel} using double buffering and a specified {@link LayoutManager}.
     @param how what types of drag operations are to be supported.
     Specified using either {@link TransferHandler}.NONE or some combination of {@link TransferHandler}.COPY, {@link TransferHandler}.MOVE, {@link TransferHandler}.COPY_OR_MOVE, or {@link TransferHandler}.LINK or-ed together.
     @param layout the layout manager to be used to manage the panel.
     */

    public DraggableJPanel( int how, LayoutManager layout ) {
        this( how, layout, true );
    }

    /**
     Create a draggable {@link JPanel} using double buffering and a {@link FlowLayout} manager.
     @param how what types of drag operations are to be supported.
     Specified using either {@link TransferHandler}.NONE or some combination of {@link TransferHandler}.COPY, {@link TransferHandler}.MOVE, {@link TransferHandler}.COPY_OR_MOVE, or {@link TransferHandler}.LINK or-ed together.
     */

    public DraggableJPanel( int how ) {
        this( how, true );
    }

}
