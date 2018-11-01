package com.obtuse.ui.copier;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.UniqueWidget;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;
import java.util.List;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

/**
 Something that can provide data and has a notification mechanism so that consumers can be told when new data is available.
 */

public class CopierDataSource extends UniqueWidget {

    public interface Owner {

        String getToStringName();

        @SuppressWarnings("unused")
        JTextField getTextField();

    }

    private final Collection<CopierDataSource> _buddies = new ArrayList<>();

    public class ListenerInfo extends UniqueWidget {

        private final CopierActionListener _listener;

        public ListenerInfo( final @NotNull String name, final @NotNull CopierActionListener listener ) {
            super( name );

            _listener = listener;

        }

        public CopierActionListener getListener() {

            return _listener;

        }

        @Override
        public String toString() {

            return "ListenerInfo( name=" + ObtuseUtil.enquoteToJavaString( getName() ) + ", listener=" + getListener() + " )";

        }

    }

    private boolean _completelyReadOnly;

    private final ObtuseTextElement _currentValueElement;

    private final List<ListenerInfo> _listeners = new ArrayList<>();

    private Owner _owner;

    public CopierDataSource(
            final @NotNull String name,
            final @NotNull ObtuseTextElement currentValueElement,
            final boolean completelyReadOnly
    ) {
        super( name );

        _currentValueElement = currentValueElement;
        _completelyReadOnly = completelyReadOnly;

    }

    @SuppressWarnings("unused")
    public boolean isCurrentValueVisible() {

        return _currentValueElement.isVisible();

    }

    public void setCurrentValueVisible( final boolean visible ) {

        _currentValueElement.setVisible( visible );

    }

//    public boolean isReadOnly() {
//
//        return _currentValueElement instanceof ObtuseTextElement.ObtuseLabel;
//
//    }

    public boolean isCompletelyReadOnly() {

        return _completelyReadOnly;
    }

    public void setCompletelyReadOnly( final boolean completelyReadOnly ) {

        _completelyReadOnly = completelyReadOnly;
    }

    public final void setCurrentValue( final @NotNull String newCurrentValue ) {

        if ( isCompletelyReadOnly() ) {

            throw new HowDidWeGetHereError(
                    "CopierWidgetSource.setCurrentValue:  " +
                    "we are a constant value of " + ObtuseUtil.enquoteToJavaString( getCurrentValue() ) +
                    ", cannot change to " + ObtuseUtil.enquoteToJavaString( newCurrentValue )
            );

        } else {

            _currentValueElement.setText( newCurrentValue );

            fireListeners();

        }

    }

//    @SuppressWarnings("unused")
//    public final boolean supportsSetCurrentValue() {
//
//        return !isReadOnly();
//
//    }

    @NotNull
    public final String getCurrentValue() {

        return _currentValueElement.getText();

    }

    @SuppressWarnings("unused")
    public boolean hasOwner() {

        return _owner != null;

    }

    public Owner getOwner() {

        return _owner;

    }

    public JComponent getOurRepresentation() {

        if ( isCopierWidget() ) {

            throw new HowDidWeGetHereError(
                    "CopierDataSource.getOurRepresentation:  " +
                    "this method must be overridden when we are a copier widget"
            );

        }

        return _currentValueElement.getAsJComponent();

    }

    public boolean isCopierWidget() {

        return this instanceof CopierWidget;

    }

    @SuppressWarnings("unused")
    public boolean isSimpleSource() {

        return !isCopierWidget();

    }

    public void setOwnerElsewhere( final @NotNull Owner owner ) {

    }

    public final void setOwner( final @NotNull Owner owner ) {

        if ( _owner == null ) {

            _owner = owner;
            setOwnerElsewhere( owner );


        } else {

            throw new HowDidWeGetHereError( "CopierWidget.setOwner:  owner already set for " + this );

        }

        Collection<CopierDataSource> buddies = new ArrayList<>( _buddies );
        for ( CopierDataSource buddy : buddies ) {

            buddy.setOwner( owner );

        }

    }

    public CopierDataSource addFinishBuddy(final CopierDataSource rhsW ) {

        _buddies.add( rhsW );

        return this;

    }

    public void finish() {

        if ( !_buddies.isEmpty() ) {

            Logger.logMsg( "buddies list in BBDS is not empty" );

            ObtuseUtil.doNothing();

        }

        if ( _owner == null ) {

            throw new HowDidWeGetHereError( "CopierWidget.finish:  owner not set" );

        }

        Collection<CopierDataSource> buddies = new ArrayList<>( _buddies );
        for ( CopierDataSource buddy : buddies ) {

            buddy.finish();

        }

    }

    protected void fireListeners() {

        List<ListenerInfo> listeners = new ArrayList<>( _listeners );

        for ( ListenerInfo listenerInfo : listeners ) {

            listenerInfo.getListener().actionPerformed( this );

        }

    }

    @NotNull
    public final List<ListenerInfo> getAllListeners() {

        return Collections.unmodifiableList( _listeners );

    }

    @NotNull
    public final Optional<ListenerInfo> findListenerByName( final @NotNull String name ) {

        for ( ListenerInfo listenerInfo : _listeners ) {

            if ( listenerInfo.getName().equals( name ) ) {

                return Optional.of( listenerInfo );

            }

        }

        return Optional.empty();

    }

    @SuppressWarnings("unused")
    public boolean removeByName( final @NotNull String name ) {

        for ( Iterator<ListenerInfo> iterator = _listeners.iterator(); iterator.hasNext(); ) {

            ListenerInfo listenerInfo = iterator.next();

            if ( listenerInfo.getName()
                             .equals( name ) ) {

                iterator.remove();

                return true;

            }

        }

        return false;

    }

    public final void addCopierActionListener( final @NotNull String name, final @NotNull CopierActionListener actionListener ) {


        Optional<ListenerInfo> optListener = findListenerByName( name );
        if ( optListener.isPresent() ) {

            throw new HowDidWeGetHereError( "CopierDataSource.addCopierActionListener:  we already have a listener named " + ObtuseUtil.enquoteToJavaString( name ) );

        }

        _listeners.add( new ListenerInfo( name, actionListener ) );

    }

    public String toString() {

        return "CopierDataSource( " +
               "type=" + ( isCopierWidget() ? "copierWidget" : "simpleSource" ) + ", " +
               "currentValue=" + ObtuseUtil.enquoteToJavaString( getCurrentValue() ) +
               " )";

    }

}
