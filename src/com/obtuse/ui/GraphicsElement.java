/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

/**
 * Something which can be drawn on a drawing window.
 * While in theory a {@link GraphicsElement} could be almost anything, in practice these are lines and parts of lines
 * (curved and/or straight).
 */

@SuppressWarnings("UnusedDeclaration")
public interface GraphicsElement {

    /**
     * Get the {@link GraphicsElement} which 'owns' this element.
     * @return this element's 'owner' (null if unknown or nonexistent).
     */

    GraphicsElement getParentElement();

}
