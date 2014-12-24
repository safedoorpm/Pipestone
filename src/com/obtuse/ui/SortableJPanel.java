/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * %%% something clever goes here.
 */

@SuppressWarnings("UnusedDeclaration")
public class SortableJPanel extends JPanel implements SortableJComponent {

    private String _sortingKey;

    public SortableJPanel( String sortingKey ) {
        super();

    }

    public void setSortingKey( String newSortingKey ) {

        _sortingKey = newSortingKey;

    }

    public String getSortingKey() {

        return _sortingKey;

    }

    public int compareTo( @NotNull SortableJComponent sortableJComponent ) {

        if ( sortableJComponent instanceof SortableJPanel ) {

            return _sortingKey.compareTo( ((SortableJPanel)sortableJComponent).getSortingKey() );

        } else {

            throw new IllegalArgumentException( "SortableJPanel:  can only be compared to SortableJPanel instances" );

        }

    }

}
