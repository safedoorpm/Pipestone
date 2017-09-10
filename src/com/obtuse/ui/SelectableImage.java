/*
 * Based on https://docs.oracle.com/javase/tutorial/uiswing/examples/misc/TrackFocusDemoProject/src/misc/Picture.java (accessed 2017-07-24).
 *
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.obtuse.ui;

/*
 * SelectableImage.java is used by the 1.4
 * TrackFocusDemo.java and DragPictureDemo.java examples.
 */

import com.obtuse.util.BasicProgramConfigInfo;
import org.jetbrains.annotations.Nullable;
import sun.nio.cs.Surrogate;

import javax.accessibility.Accessible;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class SelectableImage extends JPanel
        implements MouseListener,
                   FocusListener,
                   Accessible {

    private boolean _selectable;
    Image _image;
    private Color _selectedColor;

    public SelectableImage( boolean selectable, @Nullable Image image ) {
        super();

        setSelectable( selectable );

        _image = image;
        addMouseListener( this );
        addFocusListener( this );

        ActionMap actionMap = getActionMap();
        actionMap.put(TransferHandler.getCutAction().getValue(Action.NAME),	TransferHandler.getCutAction());
        actionMap.put(TransferHandler.getCopyAction().getValue(Action.NAME),	TransferHandler.getCopyAction());
        actionMap.put(TransferHandler.getPasteAction().getValue(Action.NAME),	TransferHandler.getPasteAction());

    }

    public SelectableImage( @Nullable Image image ) {
        this( true, image );

    }

    public SelectableImage( boolean selectable, LayoutManager layout, boolean isDoubleBuffered ) {
        super( layout, isDoubleBuffered );

        setSelectable( selectable );

    }

    public SelectableImage( LayoutManager layout, boolean isDoubleBuffered ) {
        this( true, layout, isDoubleBuffered );
    }

    public SelectableImage() {
        this( true, null );
    }

    public void setSelectable( boolean selectable ) {

        _selectable = selectable;

        repaint();

    }

    public boolean isSelectable() {

        return _selectable;

    }

    public void setImage( Image image ) {

        _image = image;

        repaint();

    }

    public void mouseClicked( MouseEvent e ) {

        //Since the user clicked on us, let's get focus!

        requestFocusInWindow();

    }

    public void mouseEntered( MouseEvent e ) {

    }

    public void mouseExited( MouseEvent e ) {

    }

    public void mousePressed( MouseEvent e ) {

    }

    public void mouseReleased( MouseEvent e ) {

    }

    public void focusGained( FocusEvent e ) {
        //Draw the component with a red border
        //indicating that it has focus.
        repaint();
    }

    public void focusLost( FocusEvent e ) {
        //Draw the component with a black border
        //indicating that it doesn't have focus.
        repaint();
    }

    protected void paintComponent( Graphics graphics ) {

        super.paintComponent( graphics );

        Graphics g = graphics.create();

        Insets is = getInsets();
        int width = getWidth() - ( is.left + is.right );
        int height = getHeight() - ( is.top + is.bottom );
        int tlX = is.left;
        int tlY = is.top;

        //Draw in our entire space, even if isOpaque is false.
        g.setColor( Color.WHITE );
        g.fillRect( tlX, tlY, _image == null ? width : _image.getWidth( this ),
                    _image == null ? height : _image.getHeight( this )
        );

        if ( _image != null ) {

            //Draw image at its natural size of 125x125.
            g.drawImage( _image, tlX, tlY, this );

        }

        if ( _selectedColor == null ) {

            _selectedColor = new JList().getSelectionBackground();

        }

        //Add a border, red if picture currently has focus
        if ( isSelectable() && isFocusOwner() ) {

            g.setColor( _selectedColor );

        } else {

            g.setColor( Color.BLACK );

        }

        g.drawRect(
                tlX,
                tlY,
                _image == null ? width - 1 : _image.getWidth( this ),
                _image == null ? height - 1 : _image.getHeight( this )
        );

//        if ( isFocusOwner() ) {
//
//            g.setColor( new JList().getSelectionForeground() ); // SystemColor.textHighlightText );
//
//        } else {
//
//            g.setColor( Color.BLACK );
//
//        }

        g.drawLine( tlX, tlY, width, height );

        g.dispose();

    }

    public static SelectableImage makeTestImage() {

        SelectableImage si = new SelectableImage();
        si.setMinimumSize( new Dimension( 150, 150 ) );
        si.setPreferredSize( new Dimension( 150, 150  ) );
        si.setSelectable( true );

        return si;

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "SelectableImage", "testing", null );

        JFrame jf = new JFrame();
        JPanel jp = new JPanel();
        SelectableImage im1 = makeTestImage();
        jp.add( im1 );
        SelectableImage im2 = makeTestImage();
        jp.add( im2 );
        JTextField jtf = new JTextField();
        jtf.setMinimumSize( new Dimension( 50, 30 ) );
        jtf.setPreferredSize( new Dimension( 50, 30 ) );
        jp.add( jtf );
        JCheckBox jc = new JCheckBox( "selectable" );
        jc.addActionListener(
                new MyActionListener() {

                    @Override
                    public void myActionPerformed( final ActionEvent actionEvent ) {

                        for ( SelectableImage si : new SelectableImage[]{ im1, im2 } ) {

                            si.setSelectable( jc.isSelected() );
                            si.setFocusable( jc.isSelected() );

                        }

                    }

                }
        );
        jc.setSelected( false );
        jp.add( jc );
        jf.setContentPane( jp );
        jf.setMinimumSize( new Dimension( 400, 400 ) );
        jf.pack();
        jf.setVisible( true );

    }

}