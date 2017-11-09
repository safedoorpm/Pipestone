package com.obtuse.ui.entitySorter;

import com.obtuse.ui.MyActionListener;
import com.obtuse.util.Logger;
import com.obtuse.util.NounsList;

import javax.swing.*;
import java.awt.event.ActionEvent;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 %%% Something clever goes here.
 */

public class MyButtonView extends Box implements SortableEntityView<String,MyButtonEntity> {

    private final SortedPanelModel<String, MyButtonEntity> _myModel;

    private final JLabel _ourLabel;

    private MyButtonEntity _myEntity;

//    private String _baseLabel;

    private String _key;

    private final String _word;

    public MyButtonView(
            final MyButtonEntity myEntity,
            final SortedPanelModel<String, MyButtonEntity> myModel,
            final String word,
            final String key
    ) {
//	    super( NounsList.pickNoun() + " " + label + " #" + getNextCount() );
	super( BoxLayout.X_AXIS );

//	    _baseLabel = label;
	_key = key;
	_word = word;
	_myModel = myModel;
	_myEntity = myEntity;

	_ourLabel = new JLabel( makeLabel( _key, word ) );
//	    ourLabel.addActionListener(
//		    new MyActionListener() {
//
//			@Override
//			public void myActionPerformed( ActionEvent e ) {
//
//			    String newKey = NounsList.pickNoun();
//			    Logger.logMsg( "adding new button \"" + newKey + "\"" );
//			    myModel.addEntity(
//				    newKey,
//				    new MyButtonEntity( myModel, newKey )
//			    );
//
//			}
//
//		    }
//	    );

	add( _ourLabel );

	JButton moveButton = new JButton( "move" );
	moveButton.addActionListener(
		new MyActionListener() {

		    @Override
		    public void myActionPerformed( final ActionEvent actionEvent ) {

			moveSomewhere();

		    }

		}
	);

	add( moveButton );

    }

    public void moveSomewhere() {

	String oldKey = _key;
	String ourWord = _word;
	String newKey = NounsList.pickNoun();
//	Logger.logMsg( "######## moving word \"" + ourWord + "\" from \"" + oldKey + "\" to \"" + newKey + "\"" );
	_ourLabel.setText( makeLabel( newKey, _word ) );
	if ( newKey.equals( oldKey ) ) {

	    throw new IllegalArgumentException( "attempt to move word to current location \"" + oldKey + "\"" );

	}

	_myModel.changeSortingKey( _key, newKey, _myEntity );
	if ( !_key.equals( newKey ) ) {

	    throw new IllegalArgumentException( "\"" + _word + "\" should be at \"" + newKey + "\" but we are at \"" + _key + "\"" );

	}

//	Logger.logMsg( "######## moved word \"" + ourWord + "\" from \"" + oldKey + "\" to \"" + newKey + "\"" );

    }

    public MyButtonEntity getEntity() {

        return _myEntity;

    }

    private static String makeLabel( final String key, final String word ) {

        return "\"" + key + "\" -> \"" + word + "\"";

    }

    public String toString() {

	return "MyButtonView( \"" + getActiveKey() + "\" )";

    }

    @Override
    public String getActiveKey() {

	return _key;

    }

    @Override
    public void setActiveKey( final String key ) {

	_key = key;

    }

}
