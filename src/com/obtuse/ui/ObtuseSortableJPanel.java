/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * A sortable {@link JPanel} that sorts based on a string key.
 * <p/>
 * The string key is initially provided via this class's constructors.
 * It can be updated via a {@link #setSortingKey(String)} method.
 * If this instance's key is updated and this instance's parent is a {@link ObtuseSortableJComponentView} then this instance's parent's {@link ObtuseSortableJComponentView#reSort} method is invoked.
 */

@SuppressWarnings("UnusedDeclaration")
public class ObtuseSortableJPanel extends JPanel implements ObtuseSortableJComponent {

    private String _sortingKey;

    public ObtuseSortableJPanel( @NotNull String sortingKey ) {
        super();

	_sortingKey = sortingKey;

    }

    public void setSortingKey( String newSortingKey ) {

        _sortingKey = newSortingKey;

	Container parent = getParent();
	if ( parent instanceof ObtuseSortableJComponentView ) {

	    ((ObtuseSortableJComponentView)parent).reSort( this );
	    if ( !((ObtuseSortableJComponentView)parent).checkOrder() ) {

		throw new IllegalArgumentException( "ObtuseSortableJPanel.setSortingKey: re-sort failed" );

	    }

	}

    }

    public String getSortingKey() {

        return _sortingKey;

    }

    public int compareTo( @NotNull ObtuseSortableJComponent obtuseSortableJComponent ) {

        if ( obtuseSortableJComponent instanceof ObtuseSortableJPanel ) {

            return _sortingKey.compareTo( ((ObtuseSortableJPanel) obtuseSortableJComponent ).getSortingKey() );

        } else {

            throw new IllegalArgumentException( "ObtuseSortableJPanel:  can only be compared to ObtuseSortableJPanel instances" );

        }

    }

    public String toString() {

	return "ObtuseSortableJPanel( \"" + getSortingKey() + "\" )";

    }

}
