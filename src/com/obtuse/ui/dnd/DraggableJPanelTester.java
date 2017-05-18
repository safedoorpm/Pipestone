package com.obtuse.ui.dnd;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 %%% Something clever goes here.
 */
public class DraggableJPanelTester extends JFrame {

    @SuppressWarnings({ "FieldCanBeLocal", "unused" })
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
		new AbstractDataFlavorHandler[] {

			new BasicFlavorHandlers.SimpleImageDataFlavorHandler() {

			    @Override
			    protected void handleImage(
				    TransferHandler.TransferSupport transferSupport,
				    Image transferImage
			    ) {

				_draggableJPanel.setBackgroundImage( transferImage );

			    }

			},

			new BasicFlavorHandlers.SimpleFilesDataFlavorHandler() {

			    @Override
			    public void handleFilesList(
			    	TransferHandler.TransferSupport transferSupport,
				    File[] transferFiles
			    ) {

				Logger.logMsg( "handling a file list:  " + Arrays.toString( transferFiles ) );

			    }

			},

			new BasicFlavorHandlers.SimpleStringDataFlavorHandler() {

			    @Override
			    protected void handleString(
				    TransferHandler.TransferSupport transferSupport,
				    String transferString
			    ) {

				Logger.logMsg( "handling a string:  " + ObtuseUtil.enquoteForJavaString( transferString ) );

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
