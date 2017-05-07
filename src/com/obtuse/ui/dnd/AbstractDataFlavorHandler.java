package com.obtuse.ui.dnd;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Abstract definition of our data flavor handler.
 */

public abstract class AbstractDataFlavorHandler {

    private final DataFlavor _supportedFlavor;

    public AbstractDataFlavorHandler( DataFlavor supportedFlavor ) {

	super();

	_supportedFlavor = supportedFlavor;

    }

    public abstract boolean handleTransfer( TransferHandler.TransferSupport ts );

    public DataFlavor getSupportedDataFlavor() {

	return _supportedFlavor;

    }

}
