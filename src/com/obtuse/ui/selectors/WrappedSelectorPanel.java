/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import javax.swing.*;

/**
 Provide a slightly unified view on the two wrapper classes.
 <p>Probably more to come eventually.</p>
 */

public abstract class WrappedSelectorPanel<CHOICE> extends JPanel {

    public abstract boolean notifyCurrentChildChange(
            CHOICE oldChoice,
            CHOICE newChoice
    );

}
