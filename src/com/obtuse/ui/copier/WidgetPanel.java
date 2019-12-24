package com.obtuse.ui.copier;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.ObtuseSwingUtils;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.UniqueWidget;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

/**
 A widget that represents a slightly paranoid {@link JPanel}.
 <p>See methods below for details.</p>
 */

@SuppressWarnings("unused")
public class WidgetPanel extends UniqueWidget {

    public enum Type {
        OTHER,
        COPIER
    }

    private final JPanel _container = new JPanel();

    private boolean _finished = false;

    private static boolean s_showBorders = false;

    private final Type _type;

    private final long _parentId;

    public WidgetPanel(final @NotNull String name, final long id, final @NotNull Type type ) {
        super( "panel for " + name );

        _parentId = id;

        _container.setName( getName() );

        if ( s_showBorders ) {

            _container.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder( Color.BLUE, 1 ),
                            BorderFactory.createEmptyBorder( 1, 1, 1, 1 )
                    )
            );

        }

        _container.setLayout( new BoxLayout( _container, BoxLayout.X_AXIS ) );

        _type = type;

    }

    public long getParentId() {

        return _parentId;

    }

    public static void setShowBorders( final boolean showBorders ) {

        s_showBorders = showBorders;

    }

    public static boolean showBorders() {

        return s_showBorders;

    }

    public Component add( final @NotNull Component comp ) {

        checkFinished( "add", false );

        if ( comp.getParent() == null ) {

            return _container.add( comp );

        } else if ( comp.getParent() == _container ) {

            throw new IllegalArgumentException(
                    "WidgetPanel.add:  " +
                    "component already in this panel (probably a bug) - " +
                    "remove it before adding it here again or don't add it again"
            );

        } else {

            throw new IllegalArgumentException(
                    "WidgetPanel.add:  " +
                    "component already in another panel (probably a bug) - " +
                    "remove it from there before adding it here"
            );

        }

    }

    public void finish() {

        checkFinished( "finish", false );

        markFinished();

        Logger.logMsg( getName() + ":" );

        ObtuseSwingUtils.describeFullyContainerContents( "just finished", getDisplayContainer() );

        ObtuseUtil.doNothing();

    }

    public void checkFinished( final @NotNull String methodName, final boolean mustBeFinished ) {

        if ( mustBeFinished == _finished ) {

            return;

        }

        if ( mustBeFinished ) {

            throw new HowDidWeGetHereError( "CopierWidget." + methodName + "(" + getName() + "):  too early to call this method - not finished yet" );

        } else {

            throw new HowDidWeGetHereError( "CopierWidget." + methodName + "(" + getName() + "):  too late to call this method - already finished" );

        }

    }

    public final boolean isFinished() {

        return _finished;

    }

    protected void markFinished() {

        checkFinished( "markFinished", false );

        _finished = true;

    }

    public int getComponentCount() {

        return _container.getComponentCount();

    }

    @NotNull
    public Component getComponent( final int index ) {

        return _container.getComponent( index );

    }

    public void remove( final int index ) {

        _container.remove( index );

    }

    @NotNull
    public JComponent getDisplayContainer() {

        return _container;

    }

    public String toString() {

        return "WidgetPanel( name=" + getName() + ", type=" + _type + " )";

    }

}
