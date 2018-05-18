/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1;

import com.obtuse.ui.layout.layoutTracer.LayoutTracer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 Describe a container that supports the {@link FlexiGridLayoutManager}.
 */

public interface FlexiGridContainer {

    /**
     Get the container's name.
     */

    String getName();

    /**
     Get the size of this instance.
     @return the size of this instance.
     */
    Dimension getSize();

    /**
     Swing/AWT method.
     <p/> {@link JComponent#setAlignmentX(float)} for more info.
     */

    void setAlignmentX( float v );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#setAlignmentY(float)} for more info.
     */

    void setAlignmentY( float v );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#setBorder(Border)} for more info.
     */

    void setBorder( Border border );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#add(Component)} for more info.
     */

    Component add( Component comp );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#add(Component, int)} for more info.
     */

    Component add( Component comp, int ix );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#add(Component, Object)} for more info.
     */

    void add( Component comp, Object constraints );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#getComponentCount()} for more info.
     */

    int getComponentCount();

    /**
     Swing/AWT method.
     <p/> {@link JComponent#getComponent(int)} for more info.
     */

    Component getComponent( int ix );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#getInsets()} for more info.
     */

    Insets getInsets();

    /**
     Swing/AWT method.
     <p/> {@link JComponent#setMinimumSize(Dimension)} for more info.
     */

    void setMinimumSize( Dimension dimension );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#setMaximumSize(Dimension)} for more info.
     */

    void setMaximumSize( Dimension dimension );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#setPreferredSize(Dimension)} for more info.
     */

    void setPreferredSize( Dimension dimension );

    Component add( @NotNull Component component, Object constraints, int index, long key );

    void removeAll( long key );

    void removeAll();

    void remove( int index, long key );

    void remove( int index );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#setOpaque(boolean)} for more info.
     */

    void setOpaque( boolean opaque );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#isOpaque()} for more info.
     */

    boolean isOpaque();

    /**
     Swing/AWT method.
     <p/> {@link JComponent#setBackground(Color)} for more info.
     */

    void setBackground( Color color );

    /**
     Swing/AWT method.
     <p/> {@link JComponent#revalidate()} for more info.
     */

    void revalidate();

    /**
     Swing/AWT method.
     <p/> {@link JComponent#invalidate()} for more info.
     */

    void invalidate();

    /**
     Swing/AWT method.
     <p/> {@link JComponent#validate()} for more info.
     */

    void validate();

    /**
     Swing/AWT method.
     <p/> {@link JComponent#isVisible()} for more info.
     */

    boolean isVisible();

    /**
     Swing/AWT method.
     <p/> {@link JComponent#setVisible(boolean)} for more info.
     */

    void setVisible( boolean visible );

    /**
     Return this instance as a {@link Container}.

     @return this instance cast as a {@link Container}.
     */

    default Container getAsContainer() {

        return (Container)this;

    }

    /**
     Get our layout manager (possibly after unwrapping it from a {@link LayoutTracer}).
     */

    FlexiGridLayoutManager getFlexiGridLayoutManager();

    /**
     Return this instance as a {@link JComponent}.

     @return this instance cast as a {@link JComponent}.
     */

    default JComponent getAsJComponent() {

        return (JComponent)this;

    }

    void remove( Component component, long key );

    void remove( Component component );
}
