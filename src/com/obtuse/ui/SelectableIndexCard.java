/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 Something that can be managed by an IndexCardBox.
 <p/>The main requirement is that the thing knows how to make the fact that it is selected or not apparent to the human.
 There are also some imposed by how Swing things which are selectable are (apparently) expected to behave.
 Being able to provide one's bounding box being an example of such an expectation.
 */

public interface SelectableIndexCard {

    void setSelected( boolean isSelected );

    boolean isSelected();

    Rectangle getBounds();

    boolean wereWeClicked( MouseEvent mEvent );

    String getWhat();

}
