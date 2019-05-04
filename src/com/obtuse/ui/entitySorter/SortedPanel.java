package com.obtuse.ui.entitySorter;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.MyActionListener;
import com.obtuse.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

/**
Danny
 A {@link JPanel} that works with a {@link SortedPanelModel}.
 */

@SuppressWarnings("unchecked")
public class SortedPanel<K extends Comparable<K>,E extends SortableEntity> extends JPanel {

    private SortedPanelModel _model;

    public SortedPanel() {

        super();

    }

    public SortedPanel( final String name ) {

        super();

        setName( name );

    }

    public void describe( final String who ) {

        Logger.logMsg( "describing SortedPanel \"" + who + "\"" );

        int ix = 0;
        Component c = this;
        while ( c != null ) {

            Logger.logMsg( "ix=" + ix + ":  " + c.getClass().getCanonicalName() );

            c = c.getParent();

            ix += 1;

        }

    }

    public void setModel( @Nullable final SortedPanelModel model ) {

        Trace.event( "                                                                                                                            " +
                     this +
                     ".setModel( " +
                     model +
                     " )" );
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

            throw new IllegalArgumentException( "panel \"" +
                                                getName() +
                                                "\" cannot use model \"" +
                                                model.getName() +
                                                "\" because it is already owned by panel \"" +
                                                model.getOwner() +
                                                "\"" );

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

    public SortableEntityView getEntityView( final int ix ) {

        return (SortableEntityView)getComponent( ix );

    }

    public SortableEntityView[] getEntityViews() {

        Component[] rval = getComponents();
        SortableEntityView[] castRval = new SortableEntityView[rval.length];
        for ( int i = 0; i < rval.length; i += 1 ) {

            castRval[i] = (SortableEntityView)rval[i];

        }

        return castRval;

    }

    public SortedPanelModel<K,E> getModel() {

        return _model;

    }

    public boolean hasModel() {

        return _model != null;

    }

    public String toString() {

        return "SortedPanel( \"" +
               getName() +
               "\", " +
               getComponentCount() +
               " components" +
               ( hasModel() ? ", model=\"" + getModel().getName() + "\"" : "\", no model" ) +
               " )";

    }

    private static void testSetModel( final String label, final @NotNull SortedPanel sp, @Nullable final SortedPanelModel model ) {

        SortedPanelModel oldModel = sp.getModel();

        Trace.event( "<<<" + label );
        Trace.event( "" + sp + ":  setting model to " + model + ( oldModel == null ? "" : ", currently " + oldModel ) );

        sp.setModel( model );

        Trace.event( "" + sp + ":  result is " + model + ( oldModel == null ? "" : ", oldModel " + oldModel ) );
        Trace.event( ">>>" + label );

    }

    private static void testAdoptSortedPanel( final String label, final @NotNull SortedPanelModel model, @Nullable final SortedPanel sp ) {

        SortedPanel oldSp = model.getOwner();

        Trace.event( "<<<---" + label );
        Trace.event( "" + model + ":  adopting " + sp + ( oldSp == null ? "" : ", currently " + oldSp ) );

        model.adoptSortedPanel( sp );

        Trace.event( "" + model + ":  result is " + sp + ( oldSp == null ? "" : ", was " + oldSp ) );
        Trace.event( ">>>---" + label );

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "entitySorter" );

        SortableKeySpace sks = SortableKeySpace.getKey( "sks" );

        SortedPanel p1 = new SortedPanel( "p1" );
        p1.setLayout( new BoxLayout( p1, BoxLayout.Y_AXIS ) );

        Trace.event( "p1=" + p1 );

        testSetModel( "a1", p1, new SortedPanelModel( sks, "p1model" ) );

        testSetModel( "a2", p1, null );

        testSetModel( "a3", p1, new SortedPanelModel( sks, "p2model" ) );

        testSetModel( "a4", p1, p1.getModel() );

        testSetModel( "a5", p1, new SortedPanelModel( sks, "p3model" ) );

        Trace.event( "preparing to test SortedPanelModel.adoptSortedPanel" );

        SortedPanelModel m1 = new SortedPanelModel( sks, "m1" );

        testAdoptSortedPanel( "x1", p1.getModel(), null );
        testAdoptSortedPanel( "x2", m1, null );

        testAdoptSortedPanel( "x3", m1, p1 );

        testAdoptSortedPanel( "x4", m1, null );
        testAdoptSortedPanel( "x5", m1, p1 );
        testAdoptSortedPanel( "x6", m1, p1 );
        testAdoptSortedPanel( "x7", m1, null );
        SortedPanel p2 = new SortedPanel( "p2" );
        p2.setLayout( new BoxLayout( p2, BoxLayout.Y_AXIS ) );
        testAdoptSortedPanel( "x8", m1, p2 );

        testPanel( sks );

    }

    public static void testPanel( final SortableKeySpace sks ) {

        final Random rng = new Random();

        JFrame frame = new JFrame( "Testing one two three" );
        Box topPanel = new Box( BoxLayout.Y_AXIS );
        final SortedPanel<String,MyButtonEntity> panel = new SortedPanel<>( "panel" );
        panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );
        final SortedPanelModel<String, MyButtonEntity> model = new SortedPanelModel<>( sks, "model" );
        panel.setModel( model );

        JButton addButton = new JButton( "add a new button" );
        JButton moveSomethingButton = new JButton( "move something" );
        JButton cleanupButton = new JButton( "cleanup tree sorter" );

        final JScrollPane scrollPane = new JScrollPane( panel );

        addButton.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( final ActionEvent e ) {

                        double v = rng.nextDouble();

                        String newKey = v < .1 ? "DUPLICATE" : NounsList.pickNoun();
                        Trace.event( "adding new button \"" + newKey + "\"" );
                        model.addEntity(
                                newKey,
                                new MyButtonEntity( model, newKey.startsWith( "DUP" ) ? NounsList.pickNoun() : newKey )
                        );
                        scrollPane.revalidate();

                    }
                }
        );

        moveSomethingButton.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( final ActionEvent actionEvent ) {

                        int ix = rng.nextInt( panel.getComponentCount() );
                        @SuppressWarnings("unchecked")
                        SortableEntityView<String, MyButtonEntity> view = (SortableEntityView<String, MyButtonEntity>)panel.getEntityView( ix );
                        MyButtonView myView = (MyButtonView)view;
                        myView.moveSomewhere();

                    }
                }
        );

        cleanupButton.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( final ActionEvent actionEvent ) {

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

    public /*<K extends Comparable<K>>*/ void verifyConsistency( final @NotNull TreeSorter<K, ?> treeSorter ) {

        if ( treeSorter.size() == getComponentCount() ) {

            SortableEntityView<K, E> prev = null;
            int index = 0;
            for ( K key : treeSorter.keySet() ) {

                if ( ObtuseUtil.always() ) {

                    throw new HowDidWeGetHereError( "how on earth does this work:  the indexing variable is never used!!" );

                }

                for ( Object entity : treeSorter.getValues( key ) ) {

                    if ( prev == null ) {

                        prev = getEntityView( 0 );
                        index = 1;

                    } else if ( prev.getActiveKey().compareTo( key ) > 0 ) {

                        throw new IllegalArgumentException( "SortedPanel:  tree sorter is not sorted:  [" +
                                                            index +
                                                            "] = {" +
                                                            prev.getActiveKey() +
                                                            "}, [" +
                                                            ( index + 1 ) +
                                                            "] = " +
                                                            key );

                    } else {

                        SortableEntityView<K, ?> view = getEntityView( index );
                        if ( key.compareTo( view.getActiveKey() ) != 0 ) {

                            throw new IllegalArgumentException( "SortedPanel:  value at index [" +
                                                                index +
                                                                "] has active key {" +
                                                                view.getActiveKey() +
                                                                "} when we were expecting it to have key {" +
                                                                key +
                                                                "}" );

                        }

                        index += 1;

                    }

                }

            }

        }

    }

}
