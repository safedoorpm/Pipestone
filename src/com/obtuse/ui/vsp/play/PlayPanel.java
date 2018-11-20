package com.obtuse.ui.vsp.play;

import com.obtuse.util.BasicProgramConfigInfo;

import javax.swing.*;
import java.awt.*;

/**
 Created by danny on 2018/11/20.
 */
public class PlayPanel extends JFrame {

    private JPanel _panel1;
    private JList _list1;

    public PlayPanel() {
        super( "Hello" );

        setContentPane( _panel1 );
        setMinimumSize( new Dimension( 100, 5 ) );
        pack();
        setVisible( true );

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "Pipestone", "testing", null );
        PlayPanel pp = new PlayPanel();

    }
}
