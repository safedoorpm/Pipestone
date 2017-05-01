package com.obtuse.ui.future;

import com.obtuse.ui.MyActionListener;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 * A possible starting point for how we'll indicate a drag-and-drop source or destination.
 */

public class DragAndDropEndpoints extends JFrame {

    private JPanel _panel1;
    private JButton _quitButton;
    private JPanel _left;
    private JPanel _right;

    public DragAndDropEndpoints() {
        super();

        setContentPane( _panel1 );

        _quitButton.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( ActionEvent actionEvent ) {

                        System.exit( 0 );

                    }

                }
        );

//        _left.setBorder( BorderFactory.createEtchedBorder() );
//        _right.setBorder( BorderFactory.createEtchedBorder() );
//        _left.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );
//        _right.setBorder( BorderFactory.createEtchedBorder( BevelBorder.LOWERED ) );

//        setMinimumSize( new Dimension( 400, 400 ) );
        pack();

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "FirstFridays", "testing", null );

        DragAndDropEndpoints dade = new DragAndDropEndpoints();
        dade.setVisible( true );

    }

    private void createUIComponents() {

        _left = new JPanel() {

            public void paintComponent( Graphics g ) {

                super.paintComponent( g );

                Graphics2D g2d = (Graphics2D)g;

		RenderingHints rh = new RenderingHints(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON
		);
		g2d.setRenderingHints(rh);

		Insets in = getInsets();
                drawHill( g2d, true, in.left, in.top, getWidth() - in.right, getHeight() - in.bottom );

            }

        };

        _right = new JPanel() {

            public void paintComponent( Graphics g ) {

                super.paintComponent( g );

                Graphics2D g2d = (Graphics2D)g;
                Insets in = getInsets();
                drawHill( g2d, false, in.left, in.top, getWidth() - in.right, getHeight() - in.bottom );

            }

        };

    }

    private void drawHill( Graphics2D g2d, boolean up, int x0, int y0, int x1, int y1 ) {

        Color oldColor = g2d.getColor();

        Point center = new Point( ( x0 + x1 ) / 2, ( y0 + y1 ) / 2 );
        Point[] corners = new Point[] {
                new Point( x0, y0 ),
                new Point( x1, y0 ),
                new Point( x1, y1 ),
                new Point( x0, y1 )
        };

//        Color[] colors = new Color[] {
//                SystemColor.controlLtHighlight.darker(),
//                SystemColor.controlShadow.darker().darker(),
//                SystemColor.controlShadow.darker(),
//                SystemColor.controlLtHighlight
//        };

        Color[] colors = new Color[] {
                new Color( 240, 240, 240 + 10 ).brighter(),
                new Color( 224, 224, 224 + 10 ), // .darker(),
                new Color( 208, 208, 208 + 10 ), // .darker(),
                Color.WHITE
        };

        Logger.logMsg( "up = " + up );

        for ( int vv = 0; vv < 4; vv += 1 ) {

            int v = ( vv + ( up ? 0 : 2 ) ) & 3;
            Logger.logMsg( "v = " + v );
            int[] xs = new int[] { corners[vv].x, center.x, corners[(vv + 1) & 3].x };
            int[] ys = new int[] { corners[vv].y, center.y, corners[(vv + 1) & 3].y };

            g2d.setColor( colors[v] );
            g2d.fillPolygon( xs, ys, 3 );

        }

    }

}
