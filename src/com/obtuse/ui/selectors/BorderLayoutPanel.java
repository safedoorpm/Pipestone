/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.selectors;

import com.obtuse.util.ObtuseUtil;

import javax.swing.*;
import java.awt.*;

/**
 A JPanel derivative that uses the {@link BorderLayout} layout manager and blocks any attempt to any other layout manager instance, including a subsequent
 instance of {@code BorderLayout}.
 <p>This class exists because when a {@code BorderLayout} instance is given responsibility for a {@link Container}, it seems to treat any
 components already in the {@code Container} as being permanently invisible.</p>
 <p>Here's an example of code that illustrates this issue:</p>
 <blockquote><pre>static class A extends JPanel {

    protected A() {
       super();

        add( new JLabel( "this will never appear" ) );
        setLayout( new BorderLayout() );
        add( new JLabel( "this will also never appear because of what happens in B() below" ), BorderLayout.NORTH );

    }

}

static class B extends A {

    public B() {
        super();

        add( new JLabel( "this will also never appear because of what happens next" ), BorderLayout.SOUTH );

        // Assign a new BorderLayout instance to our JPanel.
        // This new BorderLayout instance will ignore the components that are already in the JPanel.

        setLayout( new BorderLayout() );

        add( new JLabel( "this will appear" ), BorderLayout.CENTER );

    }

    public static void main( String[] args ) {

        JFrame jf = new JFrame( "BorderLayout Issue Demo" );
        jf.setContentPane( new B() );

        // Make the frame big enough to easily see what's in it.
        // This line plays no 'functional' role in causing the issue being demonstrated to occur.
        jf.setMinimumSize( new Dimension( 200, 200 ) );

        jf.pack();
        jf.setVisible( true );

    }

}
 </pre>
 </blockquote>
 */

public class BorderLayoutPanel extends JPanel {

    /**
     Part one of a demonstration of the issue that this class is intended to prevent.
     */

    static class A extends JPanel {

        protected A() {

            add( new JLabel( "this will never appear" ) );

            setLayout( new BorderLayout() );

            add( new JLabel( "this will also never appear because of what happens in B() below" ), BorderLayout.NORTH );

        }

    }

    /**
     Part two of a demonstration of the issue that this class is intended to prevent.
     */

    static class B extends A {

        public B() {
            super();

            add( new JLabel( "this will also never appear because of what happens next" ), BorderLayout.SOUTH );

            // Assign a new BorderLayout instance to our JPanel.
            // This new BorderLayout instance will ignore the components that are already in the JPanel.

            setLayout( new BorderLayout() );

            add( new JLabel( "this will appear" ), BorderLayout.CENTER );

        }

        public static void main( String[] args ) {

            JFrame jf = new JFrame( "BorderLayout Issue Demo" );

            jf.setContentPane( new B() );

            // Make the frame big enough to easily see what's in it.
            // This line plays no 'functional' role in causing the issue being demonstrated to occur.
            jf.setMinimumSize( new Dimension( 200, 200 ) );

            jf.pack();

            jf.setVisible( true );

        }

    }

    private LayoutManager _ourLayoutManager = null;

    /**
     Creates a new {@code BorderLayoutPanel} with {@code BorderLayout}
     and the specified buffering strategy.
     If {@code isDoubleBuffered} is true, the {@code JPanel}
     will use a double buffer.

     @param isDoubleBuffered a boolean, true for double-buffering, which
     uses additional memory space to achieve fast, flicker-free
     updates
     */

    public BorderLayoutPanel( boolean isDoubleBuffered ) {
        super( new BorderLayout(), isDoubleBuffered );
    }

    /**
     Creates a new {@code BorderLayoutPanel} with double buffering enable.
     */

    public BorderLayoutPanel() {

        this( true );
    }

    /**
     This method throws an {@link IllegalArgumentException} if an attempt is made to set
     our layout manager once it has been set to be a {@link BorderLayout} instance.
     <p>This intercept method exists because we spent about a day and a half discovering that we had
     set our layout manager to our intended {@code BorderLayout} layout manager, added some components in specified places,
     set out layout manager to a second {@code BorderLayout} layout manager instance, added more components in specified places.
     The symptom was that the components added before we assigned ourselves our second {@code BorderLayout} manager instance
     simply and silently disappeared (probably because the {@code BorderLayout} manager keeps track of its components itself
     so if you change to a different {@code BorderLayout} manager instance halfway through the ball game then you simply lose
     the components added before the change.</p>
     <p>Double drat rat fooey!!!</p>
     <p>-Danny</p>
     @param mgr the layout manager that we should use.
     @throws IllegalArgumentException if an attempt is made to set our layout manager after it has been set to be a {@code BorderLayout} manager.
     */

    public void setLayout( final LayoutManager mgr ) {

        if ( _ourLayoutManager != null && _ourLayoutManager instanceof BorderLayout ) {

            throw new IllegalArgumentException( "SelectorPanel:  BorderLayoutPanel does not support changing/setting the layout manager - bye!" );

        }

        super.setLayout( mgr );

        _ourLayoutManager = mgr;

        ObtuseUtil.doNothing();

    }
}
