/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui.alpha;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * Describe something which can be undone and/or redone.
 */

public abstract class UndoableBenoitEdit implements UndoableEdit {

    private boolean _alive = true;
    private boolean _hasBeenDone = false;
    private final String _presentationName;

    public UndoableBenoitEdit(
            final String presentationName
    ) {
        super();

        _presentationName = presentationName;

    }

    public final void undo() {

        if ( !canUndo() ) {

            throw new CannotUndoException();

        }

        doUndo();

        _hasBeenDone = false;

    }

    public abstract void doUndo();


    public boolean canUndo() {

        return _alive && _hasBeenDone;

    }

    public final void redo() {

        if ( !canRedo() ) {

            throw new CannotRedoException();

        }

        doRedo();

        _hasBeenDone = true;

    }

    public abstract void doRedo();

    public boolean canRedo() {

        return _alive && !_hasBeenDone;

    }

    public void die() {

        _alive = false;

    }

    public boolean addEdit( final UndoableEdit undoableEdit ) {

        return false;

    }

    public boolean replaceEdit( final UndoableEdit undoableEdit ) {

        return false;

    }

    public boolean isSignificant() {

        return true;

    }

    public String getPresentationName() {

        return _presentationName;

    }

    public String getUndoPresentationName() {

        return "undo " + _presentationName;

    }

    public String getRedoPresentationName() {

        return "redo " + _presentationName;

    }

    public String toString() {

        return "UndoableBenoitEdit( \"" + getPresentationName() + "\" )";
    }

}
