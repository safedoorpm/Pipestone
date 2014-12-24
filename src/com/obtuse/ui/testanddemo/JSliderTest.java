/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui.testanddemo;

import javax.swing.*;

/**
 * Simple test program.
 */

@SuppressWarnings("UnusedDeclaration")
public class JSliderTest extends JFrame {

    private JSlider _slider1;
    private JSlider _slider2;
    private JSlider _slider3;
    private JSlider _slider4;
    private JPanel _panel;

    public JSliderTest() {
        super();
        setContentPane( _panel );

        pack();

    }

    public static void main( String[] args ) {

        JSliderTest test = new JSliderTest();
        test.setVisible( true );

    }
}
