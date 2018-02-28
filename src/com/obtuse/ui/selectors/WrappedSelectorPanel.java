/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 Provide a slightly unified view on the two wrapper classes.
 <p>Probably more to come eventually.</p>
 */

public abstract class WrappedSelectorPanel<CHOICE> extends JPanel {

    public WrappedSelectorPanel() {
        super();

    }

    public abstract boolean notifyCurrentChildChange(
            CHOICE oldChoice,
            CHOICE newChoice
    );

    public void setSubsidiaryPanelBorder( final TitledBorder subsidiary_panel_dataCategoryStateNode ) {

    }
}
