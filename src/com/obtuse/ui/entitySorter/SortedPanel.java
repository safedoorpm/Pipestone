package com.obtuse.ui.entitySorter;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.ui.MyActionListener;
import com.obtuse.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

/**
 A {@link JPanel} that works with a {@link SortedPanelModel}.
 */

public class SortedPanel<K extends Comparable<K>,E> extends Box {

    private final String _name;

    private SortedPanelModel _model;

    public SortedPanel( String name, int axis ) {
        super( axis );

	_name = name;
    }

    @Override
    public String getName() {

	return _name;

    }

    public void setModel( @Nullable SortedPanelModel model ) {

        Trace.event( "                                                                                                                            " + this + ".setModel( " + model + " )" );
        // Just pass on null operations.

	if ( model == _model ) {

	    return;

	}

	// Deal with disconnections quickly.

	if ( model == null ) {

	    SortedPanelModel ourModel = _model;
	    _model = null;

	    // Note that ourModel cannot be null since we eliminated the possibility that model
	    // and ourModel, a cached copy of _model as it existed when we were called,
	    // were equal just above and we don't get here unless model is null. In other words,
	    // _model wasn't null when we were called so it isn't null now.
	    //
	    // We still check if ourModel is null since the logic leading up to this statement could change someday.

	    //noinspection ConstantConditions
	    if ( ourModel != null ) {

		ourModel.adoptSortedPanel( null );

	    }

	}

	// Make sure that the model is not already in use.

	if ( model != null && model.hasOwner() && model.getOwner() != this ) {

	    throw new IllegalArgumentException( "panel \"" + getName() + "\" cannot use model \"" + model.getName() + "\" because it is already owned by panel \"" + model.getOwner() + "\"" );

	}

	// Remove any existing model.

	if ( _model != null ) {

	    _model.adoptSortedPanel( null );

	    _model = null;

	}

	// Make sure that we are empty after any model change (if we are getting a new model then they will fill us up with their entities).

	Trace.event( "cleanup time" );

	removeAll();

	// Remember our model if we have been assigned a new one.

	if ( model != null ) {

	    _model = model;
	    _model.adoptSortedPanel( this );

	}

    }

    public SortableEntityView<K,E> getEntityView( int ix ) {

	//noinspection unchecked
	return (SortableEntityView<K, E>)getComponent( ix );

    }

    public SortableEntityView<K,E>[] getEntityViews() {

	Component[] rval = getComponents();
	@SuppressWarnings("unchecked")
	SortableEntityView<K,E>[] castRval = new SortableEntityView[rval.length];
	for ( int i = 0; i < rval.length; i += 1 ) {

	    //noinspection unchecked
	    castRval[i] = (SortableEntityView<K, E>)rval[i];

	}

	return castRval;

    }

    public SortedPanelModel getModel() {

        return _model;

    }

    public boolean hasModel() {

        return _model != null;

    }

    public String toString() {

        return "SortedPanel( \"" + getName() + "\", " + getComponentCount() + " components" + ( hasModel() ? ", model=\"" + getModel().getName() + "\"" : "\", no model" ) + " )";

    }

    private static void testSetModel( String label, @NotNull SortedPanel sp, @Nullable SortedPanelModel model ) {

        SortedPanelModel oldModel = sp.getModel();

	Trace.event( "<<<" + label );
	Trace.event( "" + sp + ":  setting model to " + model + ( oldModel == null ? "" : ", currently " + oldModel ) );

	sp.setModel( model );

	Trace.event( "" + sp + ":  result is " + model + ( oldModel == null ? "" : ", oldModel " + oldModel ) );
	Trace.event( ">>>" + label );

    }

    private static void testAdoptSortedPanel( String label, @NotNull SortedPanelModel model, @Nullable SortedPanel sp ) {

        SortedPanel oldSp = model.getOwner();

	Trace.event( "<<<---" + label );
	Trace.event( "" + model + ":  adopting " + sp + ( oldSp == null ? "" : ", currently " + oldSp ) );

	model.adoptSortedPanel( sp );

	Trace.event( "" + model + ":  result is " + sp + ( oldSp == null ? "" : ", was " + oldSp ) );
	Trace.event( ">>>---" + label );

    }

//    private static void testAdoptSortedModel( SortedPanel sp, @NotNull SortedPanelModel model ) {
//
//	Trace.event( "<<<" );
//	Trace.event( "" + model + ":  adopting " + sp );
//
//	model.adoptSortedPanel( sp );
//
//	Trace.event( "result is " + model );
//	Trace.event( ">>>" );
//
//    }

    public static void main( String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "entitySorter", null );

        SortedPanel p1 = new SortedPanel( "p1", BoxLayout.Y_AXIS );

	Trace.event( "p1=" + p1 );

	testSetModel( "a1", p1, new SortedPanelModel( "p1model" ) );

//	Trace.event( "p1 with model=" + p1 );

	testSetModel( "a2", p1, null );

//	Trace.event( "p1 with model removed=" + p1 );

	testSetModel( "a3", p1, new SortedPanelModel( "p2model" ) );

//	Trace.event( "p1 with a new model=" + p1 );

	testSetModel( "a4", p1, p1.getModel() );

//	Trace.event( "p1 with the same model after it was reassigned=" + p1 );

	testSetModel( "a5", p1, new SortedPanelModel( "p3model" ) );

//	Trace.event( "p1 with another new model=" + p1 );

	Trace.event( "preparing to test SortedPanelModel.adoptSortedPanel" );

	SortedPanelModel m1 = new SortedPanelModel( "m1" );

	testAdoptSortedPanel( "x1", p1.getModel(), null );
	testAdoptSortedPanel( "x2", m1, null );

	testAdoptSortedPanel( "x3", m1, p1 );

	testAdoptSortedPanel( "x4", m1, null );
	testAdoptSortedPanel( "x5", m1, p1 );
	testAdoptSortedPanel( "x6", m1, p1 );
	testAdoptSortedPanel( "x7", m1, null );
	testAdoptSortedPanel( "x8", m1, new SortedPanel( "p2", BoxLayout.Y_AXIS ) );

	testPanel();

    }

    public static void testPanel() {

        final Random rng = new Random();

	JFrame frame = new JFrame( "Testing one two three" );
	Box topPanel = new Box( BoxLayout.Y_AXIS );
	final SortedPanel<String,MyButtonEntity> panel = new SortedPanel<String, MyButtonEntity>( "panel", BoxLayout.Y_AXIS );
	final SortedPanelModel<String,MyButtonEntity> model = new SortedPanelModel<String, MyButtonEntity>( "model" );
	panel.setModel( model );

	JButton addButton = new JButton( "add a new button" );
	JButton moveSomethingButton = new JButton( "move something" );
	JButton cleanupButton = new JButton( "cleanup tree sorter" );

	final JScrollPane scrollPane = new JScrollPane( panel );

	addButton.addActionListener(
		new MyActionListener() {

		    @Override
		    public void myActionPerformed( ActionEvent e ) {

			double v = rng.nextDouble();
//			Logger.logMsg( "v = " + v );

			String newKey = v < .1 ? "DUPLICATE" : NounsList.pickNoun();
			Trace.event( "adding new button \"" + newKey + "\"" );
			model.addEntity(
				newKey,
				new MyButtonEntity( model, newKey.startsWith( "DUP" ) ? NounsList.pickNoun() : newKey )
			);
			scrollPane.validate();

		    }
		}
	);

	moveSomethingButton.addActionListener(
		new MyActionListener() {

		    @Override
		    public void myActionPerformed( ActionEvent actionEvent ) {

		        int ix = rng.nextInt( panel.getComponentCount() );
			SortableEntityView<String,MyButtonEntity> view = (SortableEntityView<String, MyButtonEntity>) panel.getEntityView( ix );
			MyButtonView myView = (MyButtonView)view;
			myView.moveSomewhere();

		    }
		}
	);

	cleanupButton.addActionListener(
		new MyActionListener() {

		    @Override
		    public void myActionPerformed( ActionEvent actionEvent ) {

		        int count = model.cleanupDeadKeys();

		        Trace.event( "cleaning up tree sorter removed " + count + " keys" );

		    }

		}
	);

	topPanel.add( addButton );
	topPanel.add( moveSomethingButton );
	topPanel.add( cleanupButton );
	topPanel.add( scrollPane );

	frame.setContentPane( topPanel );
	frame.setMinimumSize( new Dimension( 200, 200 ) );

	model.addEntity(
		NounsList.pickNoun(),
		new MyButtonEntity( model, NounsList.pickNoun() )
	);

	frame.pack();

	frame.setVisible( true );

    }

    public void verifyConsistency( @NotNull TreeSorter<K,?> treeSorter ) {

        if ( treeSorter.size() == getComponentCount() ) {

	    SortableEntityView<K,E> prev = null;
            int index = 0;
	    for ( K key : treeSorter.keySet() ) {

		for ( Object entity : treeSorter.getValues( key ) ) {

//		    Trace.event( "SortedPanel.verifyConsistency:  key = " + key + ", index = " + index );
		    if ( prev == null ) {

			prev = getEntityView( 0 );
			index = 1;

		    } else if ( prev.getActiveKey().compareTo( key ) > 0 ) {

			throw new IllegalArgumentException( "SortedPanel:  tree sorter is not sorted:  [" + index + "] = {" + prev.getActiveKey() + "}, [" + ( index + 1 ) + "] = " + key );

		    } else {

			SortableEntityView<K,?> view = getEntityView( index );
			if ( key.compareTo( view.getActiveKey() ) != 0 ) {

			    throw new IllegalArgumentException( "SortedPanel:  value at index [" + index + "] has active key {" + view.getActiveKey() + "} when we were expecting it to have key {" + key + "}" );

			}

			index += 1;

		    }

		}

	    }

	}

    }

}