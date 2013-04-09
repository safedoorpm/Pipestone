package com.obtuse.ui;

/*
 * <p/>
 * Copyright © 2010 Daniel Boulet.
 */

/**
 * Something which can be drawn on a drawing window.
 * While in theory a {@link GraphicsElement} could be almost anything, in practice these are lines and parts of lines
 * (curved and/or straight).
 */

public interface GraphicsElement {

    /**
     * Get the {@link GraphicsElement} which 'owns' this element.
     * @return this element's 'owner' (null if unknown or nonexistent).
     */

    GraphicsElement getParentElement();

}
