/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.awt.*;

/**
 * Something that owns a button created using {@link com.obtuse.util.ButtonInfo}.
 * <p/>
 */

public interface ButtonOwner {

    /**
     * Make sure that the buttons are enabled/disabled as appropriate.
     * Called by {@link com.obtuse.util.ButtonInfo} after the 'run()' method invocation in response to a button being clicked.
     */

    void setButtonStates();

    /**
     * Set the cursor appropriately as it moves over a button.
     * @param predefinedCursor the cursor to be switched to.
     */

    void setCursor( Cursor predefinedCursor );

}
