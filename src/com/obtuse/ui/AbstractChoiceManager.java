/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import com.obtuse.util.SimpleUniqueIntegerIdGenerator;

import javax.swing.*;

/**
 Generalize using a custom class for {@link ComboBoxModel} choices.
 */

public abstract class AbstractChoiceManager /*implements Comparable<AbstractChoiceManager>*/ {

    private static SimpleUniqueIntegerIdGenerator s_idGenerator = new SimpleUniqueIntegerIdGenerator( "AbstractChoiceManager id generator", false );
    private final String _choiceName;
    private final int _sn = s_idGenerator.getUniqueId();

    protected AbstractChoiceManager( final String choiceName ) {

        super();

        _choiceName = choiceName;

    }

    public abstract void doit();

    public String getChoiceName() {

        return _choiceName;

    }

    public int getSn() {

        return _sn;

    }

    public boolean equals( final Object rhs ) {

        return rhs instanceof AbstractChoiceManager && getSn() == ((AbstractChoiceManager)rhs).getSn();

    }

    public int hashCode() {

        return Integer.hashCode( getSn() );

    }

//    public int compareTo( AbstractChoiceManager acm ) {
//
//        return getChoiceName().compareTo( acm.getChoiceName() );
//
//    }

    public String toString() {

        return _choiceName;

    }

}
