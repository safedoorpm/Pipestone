package com.obtuse.ui.macos;

import com.obtuse.util.SimpleUniqueIntegerIdGenerator;
import org.jetbrains.annotations.NotNull;

import java.awt.desktop.QuitEvent;

/**
 Created by danny on 2019/03/16.
 */

public abstract class ObtuseQuitHandler {

    private static final SimpleUniqueIntegerIdGenerator s_idGenerator = new SimpleUniqueIntegerIdGenerator( "ObtuseQuitHandler sn" );

    private final int _sn = s_idGenerator.getUniqueId();

    private final String _name;

    protected ObtuseQuitHandler( @NotNull final String name ) {
        super();

        _name = name;

    }

    @NotNull
    public String getName() {

        return _name;

    }

    @NotNull
    public String getFullName() {

        return getName() + "/" + getSerialNumber();

    }

    public int getSerialNumber() {

        return _sn;

    }

    public abstract boolean quitRequested( final QuitEvent e );

    public int hashCode() {

        return Integer.hashCode( _sn );

    }

    public boolean equals( Object rhs ) {

        return rhs instanceof ObtuseQuitHandler && ((ObtuseQuitHandler)rhs)._sn == _sn;

    }

}
