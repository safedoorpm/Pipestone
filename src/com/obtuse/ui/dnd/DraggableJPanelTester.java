package com.obtuse.ui.dnd;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 %%% Something clever goes here.
 */
public class DraggableJPanelTester extends JFrame {

    private JPanel _javaPanel;
    private DraggableJPanel _draggableJPanel;

    private JPanel _topPanel;

    public DraggableJPanelTester() {
	super();

	setContentPane( _topPanel );
	setMinimumSize( new Dimension( 300, 300 ) );

	pack();
	setVisible( true );

    }

    private void createUIComponents() {

	_draggableJPanel = new DraggableJPanel( TransferHandler.COPY );

	_javaPanel = _draggableJPanel;
        _draggableJPanel.setDataFlavorHandlers(
		new DraggableJPanel.AbstractDataFlavorHandler[] {

			new DraggableJPanel.AbstractDataFlavorHandler( DataFlavor.imageFlavor ) {

			    @Override
			    public boolean handleTransfer( TransferHandler.TransferSupport ts ) {

				Logger.logMsg( "handling an image" );

				try {

				    Object transferData = ts.getTransferable().getTransferData( DataFlavor.imageFlavor );
				    Logger.logMsg( "transferData is " + ( transferData == null ? "<<null>>" : transferData.toString() ) );
				    _draggableJPanel.setBackgroundImage( (Image)transferData );
//				    Logger.logMsg( "handling a file list:  " + Arrays.toString( files ) );

				} catch ( UnsupportedFlavorException | IOException e ) {

				    Logger.logErr( "unable to import data", e );

				    return false;

				}

				return true;

			    }

			},

			new DraggableJPanel.AbstractDataFlavorHandler( DataFlavor.javaFileListFlavor ) {

			    @Override
			    public boolean handleTransfer( TransferHandler.TransferSupport ts ) {

				try {

				    Object transferData = ts.getTransferable().getTransferData( DataFlavor.javaFileListFlavor );
				    File[] files = (File[])((java.util.List)transferData).toArray();
				    Logger.logMsg( "files array is " + files.getClass() );
				    Logger.logMsg( "handling a file list:  " + Arrays.toString( files ) );

				} catch ( UnsupportedFlavorException | IOException e ) {

				    Logger.logErr( "unable to import data", e );

				    return false;

				}

				return true;

			    }

			},

			new DraggableJPanel.AbstractDataFlavorHandler( DataFlavor.stringFlavor ) {

			    @Override
			    public boolean handleTransfer( TransferHandler.TransferSupport ts ) {

				try {

				    Logger.logMsg( "handling a string:  " + ObtuseUtil.enquoteForJavaString( (String)ts.getTransferable().getTransferData( DataFlavor.stringFlavor ) ) );

				} catch ( UnsupportedFlavorException | IOException e ) {

				    Logger.logErr( "unable to import data", e );

				    return false;

				}

				return true;

			    }

			}

		}

	);

        _draggableJPanel.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );

    }

    public static void main( String[] args ) {

	BasicProgramConfigInfo.init( "Savrola", "Burke2", "Testing", null );

        DraggableJPanelTester tester = new DraggableJPanelTester();

    }

}
