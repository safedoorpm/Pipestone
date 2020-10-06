package com.obtuse.ui.demo;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 A Java program demonstrating how to pan and zoom an image inside a {@link JScrollPane}.
 <p>Apparently written and shared by Craig Wood in a c. 2007 posting on <a href="https://coderanch.com">https://coderanch.com</a>.</p>
 <p>Found at
 <a href="https://coderanch.com/t/346846/java/Wanted-Java-zooming-panning-scroll">https://coderanch.com/t/346846/java/Wanted-Java-zooming-panning-scroll</a>
 (this page is probably worth a visit as Craig Wood posted a few different versions of his demo program in response to requests and questions from other participants).</p>
 <p>I, Daniel Boulet, did some essentially cosmetic rework on the {@link #createAnImage()} method to get rid of a couple of warnings from my IDE (IntelliJ IDEA).
 I don't seem to have broken anything.</p>
 */

public class PanAndZoom implements ChangeListener {

    BufferedImage _image;
    JLabel _label;

    public void stateChanged( ChangeEvent e ) {

        int value = ( (JSlider)e.getSource() ).getValue();
        double scale = value / 100.0;
        BufferedImage scaled = getScaledImage( scale );
        _label.setIcon( new ImageIcon( scaled ) );
        _label.revalidate();  // signal scrollpane

    }

    private BufferedImage getScaledImage( double scale ) {

        int w = (int)( scale * _image.getWidth() );
        int h = (int)( scale * _image.getHeight() );
        BufferedImage bi = new BufferedImage( w, h, _image.getType() );
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        AffineTransform at = AffineTransform.getScaleInstance( scale, scale );
        g2.drawRenderedImage( _image, at );
        g2.dispose();

        return bi;

    }

    private JLabel getContent() {

        return _label;

    }

    private static BufferedImage createAnImage() {

        int w = 500;
        int h = 500;
        double wD = 500;
        double hD = 500;
        int type = BufferedImage.TYPE_INT_RGB; // many options
        BufferedImage image = new BufferedImage( w, h, type );
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setRenderingHint(
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE
        );
        g2.setPaint( new Color( 240, 200, 200 ) );
        g2.fillRect( 0, 0, w, h );
        g2.setPaint( Color.blue );
        g2.draw( new Rectangle2D.Double( wD / 16, hD / 16, wD * 7 / 8, hD * 7 / 8 ) );
        g2.setPaint( Color.green.darker() );
        g2.draw( new Line2D.Double( wD / 16, hD * 15 / 16, wD * 15 / 16, hD / 16 ) );
        Ellipse2D e = new Ellipse2D.Double( wD / 4, hD / 4, wD / 2, hD / 2 );
        g2.setPaint( new Color( 240, 240, 200 ) );
        g2.fill( e );
        g2.setPaint( Color.red );
        g2.draw( e );
        g2.dispose();

        return image;

    }

    private JSlider getControl() {

        JSlider slider = new JSlider( JSlider.HORIZONTAL, 50, 200, 100 );
        slider.setMajorTickSpacing( 50 );
        slider.setMinorTickSpacing( 10 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.addChangeListener( this );

        return slider;

    }

    public PanAndZoom( BufferedImage image ) {

        super();

        _image = image;

        _label = new JLabel( new ImageIcon( _image ) );
        _label.setHorizontalAlignment( JLabel.CENTER );

    }

    public static void main( String[] args ) {

        PanAndZoom app = new PanAndZoom( createAnImage() );
        JFrame f = new JFrame();

        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.getContentPane()
         .add( new JScrollPane( app.getContent() ) );
        f.getContentPane()
         .add( app.getControl(), "Last" );
        f.setSize( 400, 400 );
        f.setLocation( 200, 200 );

        f.setVisible( true );

    }

}