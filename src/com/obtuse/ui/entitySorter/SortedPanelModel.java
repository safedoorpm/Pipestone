package com.obtuse.ui.entitySorter;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.Trace;
import com.obtuse.util.TreeSorter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

/**
 A model that manages the sorting of {@link SortableEntityReference} within a {@link SortedPanel}.
 */

public class SortedPanelModel<K extends Comparable<K>, E extends SortableEntity> { // extends SortableEntityReference<K,? extends SortableEntity> &SortableEntity> {

    final private TreeSorter<K,SortableEntityReference<K, E>> _treeSorter = new TreeSorter<>();

    private final String _name;

    final SortableKeySpace _keySpace;

    private SortedPanel<E> _owner = null;

    /**
     Create a sorted panel model with an initial set of entities.
     @param keySpace the key space that this panel model operates within.
     @param name the name of the model (required although only used in diagnostic messages and thrown exceptions).
     @param initialEntities a mapping of keys to entities which is to be used to initially populate this model (required although allowed to be empty).
			The contents of the provided model are copied into the newly created model
			(i.e. there is no connection between this model and the provided mapping once the model exists).
     */

    public SortedPanelModel(
	    @NotNull SortableKeySpace keySpace,
	    @NotNull String name,
	    @NotNull TreeSorter<K, E> initialEntities
    ) {
        super();

	_name = name;

	_keySpace = keySpace;

	for ( K key : initialEntities.keySet() ) {

	    for ( E value : initialEntities.getValues( key ) ) {

	        _treeSorter.add( key, new SortableEntityReference<>( this, key, value ) );

	    }

	}

    }

    /**
     Create an empty sorted panel model.
     @param keySpace the key space that this model operates within.
     @param name the name of the model (required although only used in diagnostic messages and thrown exceptions).
     */

    public SortedPanelModel( @NotNull SortableKeySpace keySpace, @NotNull String name ) {
        this( keySpace, name, new TreeSorter<>() );

    }

    public SortableKeySpace getKeySpace() {

        return _keySpace;

    }

    public int size() {

        return _treeSorter.size();

    }

    public boolean isEmpty() {

        return _treeSorter.isEmpty();

    }

    /**
     Add an entity to this model.
     If this model has an owner then the entity is immediately added to the owning sorted panel.
     An entity may appear in a model more than once.
     While probably not very useful, an entity may appear in the same model with the same key more than once.
     @return the index within this model's mapping where the just added entity appears.
     @throws ClassCastException if the entity's {@link SortableEntityReference#createEntityView} method doesn't return a {@link SortableEntityView} which is also a {@link JComponent}.
     @throws NullPointerException if the entity's {@link SortableEntityReference#createEntityView} method returns a <tt>null</tt> value.
     */

    public int reAddEntity( @NotNull K key, @NotNull E entity, @Nullable SortableEntityView<K, E> view ) {

	return reAddEntity( new SortableEntityReference<>( this, key, entity ), view );

    }

    public int reAddEntity( @NotNull SortableEntityReference<K, E> entityReference, @Nullable SortableEntityView<K, E> view ) {

	String what = "reAddEntity( " + entityReference.getActiveKey() + ", " + entityReference.getValue() + ", " + view + " )";
	verifyConsistency( "start of " + what );
//	for ( SortableEntityReference<K, E> ent : _treeSorter.getAllValues() ) {
//
//	    Logger.logMsg( "\"" + ent.getActiveKey() + "\" -> {" + ent.getValue() + "}" );
//
//	}

        // Figure out where this entity is going to land.

	int valuesBeforeKey = _treeSorter.countValuesBeforeKey( entityReference.getActiveKey() );
	int valuesAtKey = _treeSorter.countValues( entityReference.getActiveKey() );
	int index = valuesBeforeKey + valuesAtKey;

//	Logger.logMsg( "index = " + index + " ( " + valuesBeforeKey + ", " + valuesAtKey + " )" + " when looking for \"" + entityReference.getActiveKey() + "\" in " + _treeSorter );

	// Actually add the value.

	_treeSorter.add( entityReference.getActiveKey(), entityReference );

	// Add it to our owning sorted panel if we've got one.

	if ( _owner != null ) {

	    _treeSorter.cleanupDeadKeys();

	    if ( index < 0 || index >= _treeSorter.size() ) {

		throw new HowDidWeGetHereError( "index=" + index + " when tree sorter only has " + _treeSorter.size() + " elements" );

	    }

	    // Note that a class cast exception will occur if the created view is not a JComponent.

	    Trace.event( "adding entity with key \"" + entityReference.getActiveKey() + "\" with new view at " + index );
	    SortableEntityView<K, ? extends SortableEntity> newEntityView = view == null ? createEntityView( entityReference ) : view;
	    newEntityView.setActiveKey( entityReference.getActiveKey() );
	    _owner.add( (JComponent)newEntityView, index );
	    _owner.verifyConsistency( _treeSorter );

	}

	verifyConsistency( what );

	return 0;

    }

    public int addEntity( @NotNull SortableEntityReference<K, E> entityReference ) {

        return this.reAddEntity( entityReference, null );

    }

    public int addEntity( @NotNull K key, @NotNull E entity ) {

        return addEntity( new SortableEntityReference<K, E>( this, key, entity ) );

    }

    public void verifyConsistency( String when ) {

//        Logger.logMsg( "verifying consistency when " + when );

        if ( !_treeSorter.isEmpty() ) {

            K previousKey = null;
            int index = 0;
	    for ( K key : _treeSorter.keySet() ) {

	        for ( SortableEntityReference<K, E> entityReference : _treeSorter.getValues( key ) ) {

	            if ( !entityReference.getActiveKey().equals( key ) ) {

	                throw new IllegalArgumentException( "entity reference has key \"" + entityReference.getActiveKey() + "\" but should have key \"" + key + "\"" );

		    }

		    if ( previousKey == null ) {

			previousKey = key;
			index = 1;

		    } else {

//		        Trace.event( "[" + ( index - 1 ) + "] = \"" + previousKey + "\", [" + index + "] = \"" + key + "\"" );

			if ( previousKey.compareTo( key ) > 0 ) {

			    throw new IllegalArgumentException( "SortedPanelModel:  tree sorter is not sorted:  [" + ( index - 1 ) + "] = " + previousKey + ", [" + index + "] = " + key );

			} else {

			    previousKey = key;
			    index += 1;

			}

		    }

		}

	    }

	    if ( hasOwner() ) {

	        _owner.verifyConsistency( _treeSorter );

	    }

	}

	Trace.event( "SortedPanelModel \"" + getName() + "\" verified:  when = " + when );

    }

    @NotNull
    private SortableEntityView<K,? extends SortableEntity> createEntityView( @NotNull SortableEntityReference<K, E> entityReference ) {

	SortableEntityView<K, ? extends SortableEntity> entityView = entityReference.createEntityView();

	return entityView;

    }

    /**
     Return this model's name.
     @return this model's name.
     */

    @NotNull
    public String getName() {

        return _name;

    }

    /**
     Tell this model to associated itself with the specified {@link SortedPanel}.
     <p/>Each model instance is able to manage only one sorted panel.
     The sorted panel which this model is managing is said to be this model's owner (a tad Machiavellian but there it is).
     If a model containing sorted entities but without an owner is associated with a sorted panel,
     the entities in the model are immediately used to populate the sorted panel.
     <p/>
     This method does nothing if the owner passed into this method is already our owner.
     This method also does nothing if the owner passed into this method is <tt>null</tt> and we do not currently have an owner.
     @param owner the sorted panel that this model is to manage. If <tt>null</tt> then any existing ownership relationship is terminated.
     @throws IllegalArgumentException if an attempt is made to change our ownership to a different sorted panel when we are already owned by a sorted panel.
     Avoid this by calling this method with a <tt>null</tt> owner and then again with the new owner.
     */

    void adoptSortedPanel( SortedPanel owner ) {

	Trace.event( "                                                                                                                            " + this + ".adoptSortedPanel( " + owner + " )" );

	// Just pass on null operations.
	// This has the side effect of eliminating any chance of infinite mutual recursion between us and {@link SortedPanel.setModel}.

	if ( owner == _owner ) {

	    return;

	}

	if ( owner == null || _owner == null ) {

            // If we already have an owner then say goodbye.
	    // Not strictly necessary but cleaner.

            if ( _owner != null ) {

                _owner.setModel( null );

	    }

	    // Either set or clear our owner.

	    _owner = owner;

	    // If we now have an owner then say hello.

	    if ( _owner != null ) {

	        _owner.setModel( this );	// Implicitly removes any components already in our new owner.

		// Ask each of our entities to create a view which we then put into a sorted panel.

		Trace.event( "fillup time" );

		for ( K key : _treeSorter.keySet() ) {

		    // Note that a class cast exception will occur if the created view is not a JComponent.

		    for ( SortableEntityReference<K, E> entityReference : _treeSorter.getValues( key ) ) {

			_owner.add( (JComponent) createEntityView( entityReference ) );

		    }

		}

	    }

	} else if ( _owner != owner ) {

	    throw new IllegalArgumentException( "this model (" + getName() + ") already has an owner (" + _owner.getName() + ")" );

	} else {

	    // We're already the owner of the specified sorted panel. Provide a breakpoint for debugging.

	    ObtuseUtil.doNothing();

	}

    }

    public int changeSortingKey( K oldKey, K newKey, E myEntity ) { //} SortableEntityReference<K,E> myEntity ) {

	String what = "changeSortingKey( " + oldKey + ", " + newKey + ", " + myEntity + " )";
	verifyConsistency( "start of " + what );

	SortableEntityReference<K, E> myEntityReference = new SortableEntityReference<>( this, oldKey, myEntity );
	int oldIndex = _treeSorter.getFullValueIndex( oldKey, target -> target.getValue() == myEntity );
	if ( oldIndex < 0 ) {

	    throw new HowDidWeGetHereError( "cannot find entity {" + myEntity + "} in " + _treeSorter );

	}

	SortableEntityView<K, E> oldView = (SortableEntityView<K, E>) _owner.getComponent( oldIndex );
	_owner.remove( oldIndex );

	Collection<SortableEntityReference<K, E>> removedValueList = _treeSorter.removeValue(
		oldKey,
		target -> {

		    boolean rval = target.getValue() == myEntity;
//		    if ( rval ) {
//
//			Logger.logMsg( "found {" + myEntity + "}: entry is {" + target.getValue() + "}" );
//
//		    } else {
//
//			Logger.logMsg( "looking for {" + myEntity + "}: entry is {" + target.getValue() + "}" );
//
//		    }

		    return rval;
		}
	);

	if ( removedValueList.isEmpty() ) {

//	    _treeSorter.removeValue(
//		    oldKey,
//		    target -> {
//
//			boolean rval = target.getValue() == myEntity;
////			if ( rval ) {
////
////			    Logger.logMsg( "found {" + myEntity + "}: entry is {" + target.getValue() + "}" );
////
////			} else {
////
////			    Logger.logMsg( "looking for {" + myEntity + "}: entry is {" + target.getValue() + "}" );
////
////			}
//
//			return rval;
//		    }
//	    );

	    throw new HowDidWeGetHereError( "entity being moved is not in our tree sorter:  oldKey=\"" + oldIndex + "\", newKey=\"" + newKey + "\", entity = {" + myEntity + "}" );

	}

	int count = reAddEntity( newKey, myEntity, oldView );

	verifyConsistency( " end of " + what );

	return count;

//	// Figure out where this entity is going to land.
//
//	int index = _treeSorter.countValuesBeforeKey( newKey ) + _treeSorter.countValues( newKey );
//
//	// Actually add the value.
//
//	_treeSorter.add( newKey, myEntity );

    }

    /**
     Get this model's owner.
     @return this model's owner (could be <tt>null</tt> if this model does not currently have an owner).
     */

    public SortedPanel getOwner() {

        return _owner;

    }

    /**
     Provides a short description of this model (intended to be at least somewhat useful in diagnostic/debug messages).
     @return a short description of this model.
     */

    public String toString() {

        return "SortedPanelModel( \"" + getName() + "\", " + _treeSorter.size() + " elements" + ( hasOwner() ? ", owner=\"" + _owner.getName() + "\"" : ", unowned" ) + " )";

    }

    /**
     Determine if this model has an owner.
     @return <tt>true</tt> if this model has an owner; <tt>false</tt> otherwise.
     */

    public boolean hasOwner() {

	return _owner != null;

    }

    public int cleanupDeadKeys() {

	return _treeSorter.cleanupDeadKeys();

    }

}
