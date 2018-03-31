package com.obtuse.ui.dnd;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 Some simple data flavor handlers which will probably get the job done in many situations.
 */

public class BasicFlavorHandlers {

    private BasicFlavorHandlers() {

        super();
    }

    public abstract static class SimpleStringDataFlavorHandler extends AbstractDataFlavorHandler {

        public SimpleStringDataFlavorHandler() {

            super( DataFlavor.stringFlavor );
        }

        @Override
        public boolean handleTransfer( final TransferHandler.TransferSupport ts ) {

            try {

                Logger.logMsg( "handling a string:  " +
                               ObtuseUtil.enquoteToJavaString( (String)ts.getTransferable().getTransferData( DataFlavor.stringFlavor ) ) );

                handleString( ts, (String)ts.getTransferable().getTransferData( DataFlavor.stringFlavor ) );

            } catch ( UnsupportedFlavorException | IOException e ) {

                Logger.logErr( "unable to import data", e );

                return false;

            }

            return true;

        }

        protected abstract void handleString(
                TransferHandler.TransferSupport transferSupport,
                String transferString
        );

    }

    public abstract static class SimpleFilesDataFlavorHandler extends AbstractDataFlavorHandler {

        public SimpleFilesDataFlavorHandler() {

            super( DataFlavor.javaFileListFlavor );
        }

        @Override
        public boolean handleTransfer( final TransferHandler.TransferSupport ts ) {

            try {

                Object transferData = ts.getTransferable().getTransferData( DataFlavor.javaFileListFlavor );
                @SuppressWarnings("unchecked") List<File> list = (List)transferData;
                Object[] objects = list.toArray( new File[0] );
                File[] files = (File[])objects;
                Logger.logMsg( "files array is " + files.getClass() );
                Logger.logMsg( "handling a file list:  " + Arrays.toString( files ) );

                handleFilesList( ts, files );

            } catch ( UnsupportedFlavorException | IOException e ) {

                Logger.logErr( "unable to import data", e );

                return false;

            }

            return true;

        }

        public abstract void handleFilesList(
                TransferHandler.TransferSupport transferSupport,
                File[] transferFiles
        );

    }


    public abstract static class SimpleImageDataFlavorHandler extends AbstractDataFlavorHandler {

        public SimpleImageDataFlavorHandler() {

            super( DataFlavor.imageFlavor );
        }

        @Override
        public boolean handleTransfer( final TransferHandler.TransferSupport ts ) {

            Logger.logMsg( "handling an image" );

            try {

                Object transferData = ts.getTransferable().getTransferData( DataFlavor.imageFlavor );
                Logger.logMsg( "transferData is " + ( transferData == null ? "<<null>>" : transferData.toString() ) );

                handleImage( ts, (Image)transferData );

            } catch ( UnsupportedFlavorException | IOException e ) {

                Logger.logErr( "unable to import data", e );

                return false;

            }

            return true;

        }

        protected abstract void handleImage(
                TransferHandler.TransferSupport transferSupport,
                Image transferImage
        );

    }

}
