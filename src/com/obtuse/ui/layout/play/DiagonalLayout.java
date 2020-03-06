package com.obtuse.ui.layout.play;

import com.obtuse.ui.MyActionListener;
import com.obtuse.ui.layout.LinearOrientation;
import com.obtuse.ui.layout.linear.LinearContainer;
import com.obtuse.ui.layout.linear.LinearLayoutUtil;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/*
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

/**
 * A place to explore how layout managers actually work.
 * <p>Copied by danny from
 * <a href="https://docs.oracle.com/javase/tutorial/uiswing/examples/layout/CustomLayoutDemoProject/src/layout/DiagonalLayout.java">
 https://docs.oracle.com/javase/tutorial/uiswing/examples/layout/CustomLayoutDemoProject/src/layout/DiagonalLayout.java</a>
 * on 2020-01-22.
 * <br>
 * 1.2+ version.  Used by CustomLayoutDemo.java.
 * </p>
 */

public class DiagonalLayout implements LayoutManager2 {
    private int vgap;
    private int minWidth = 0, minHeight = 0;
    private int preferredWidth = 0, preferredHeight = 0;
    private boolean sizeUnknown = true;

    public DiagonalLayout() {
        this(5);
    }

    public DiagonalLayout(int v) {
        vgap = v;
    }

    /* Required by LayoutManager. */
    public void addLayoutComponent(String name, Component comp) {

        Logger.logMsg(
                "DiagonalLayout.addLayoutComponent( " +
                ObtuseUtil.enquoteToJavaString( name ) + ", " +
                comp +
                " )"
        );

    }

    /* Required by LayoutManager. */
    public void removeLayoutComponent(Component comp) {

        Logger.logMsg(
                "DiagonalLayout.addLayoutComponent( " +
                comp +
                " )"
        );

    }

    private void setSizes(Container parent) {
        int nComps = parent.getComponentCount();
        Dimension d;

        //Reset preferred/minimum width and height.
        preferredWidth = 0;
        preferredHeight = 0;
        minWidth = 0;
        minHeight = 0;

        for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);
            if (c.isVisible()) {
                d = c.getPreferredSize();

                if (i > 0) {
                    preferredWidth += d.width/2;
                    preferredHeight += vgap;
                } else {
                    preferredWidth = d.width;
                }
                preferredHeight += d.height;

                minWidth = Math.max(c.getMinimumSize().width,
                                    minWidth);
                minHeight = preferredHeight;
            }
        }
    }


    /* Required by LayoutManager. */
    public Dimension preferredLayoutSize(Container parent) {
        Logger.logMsg(
                "DiagonalLayout.preferredLayoutSize( " +
                parent +
                " )"
        );

        Dimension dim = new Dimension(0, 0);
        @SuppressWarnings("unused") int nComps = parent.getComponentCount();

        setSizes(parent);

        //Always add the container's insets!
        Insets insets = parent.getInsets();
        dim.width = preferredWidth
                    + insets.left + insets.right;
        dim.height = preferredHeight
                     + insets.top + insets.bottom;

        sizeUnknown = false;

        return dim;
    }

    /* Required by LayoutManager. */
    public Dimension minimumLayoutSize(Container parent) {
        Logger.logMsg(
                "DiagonalLayout.minimumLayoutSize( " +
                parent +
                " )"
        );

        Dimension dim = new Dimension(0, 0);
        @SuppressWarnings("unused") int nComps = parent.getComponentCount();

        //Always add the container's insets!
        Insets insets = parent.getInsets();
        dim.width = minWidth
                    + insets.left + insets.right;
        dim.height = minHeight
                     + insets.top + insets.bottom;

        sizeUnknown = false;

        return dim;
    }

    /* Required by LayoutManager. */
    /*
     * This is called when the panel is first displayed,
     * and every time its size changes.
     * Note: You CAN'T assume preferredLayoutSize or
     * minimumLayoutSize will be called -- in the case
     * of applets, at least, they probably won't be.
     */
    public void layoutContainer(Container parent) {

        Logger.logMsg(
                "DiagonalLayout.layoutContainer( " +
                "pw=" + preferredWidth + ", " +
                "ph=" + preferredHeight + ", " +
                "vg=" + vgap + ", " +
                parent +
                " )"
        );

        Insets insets = parent.getInsets();
        int maxWidth = parent.getWidth()
                       - (insets.left + insets.right);
        int maxHeight = parent.getHeight()
                        - (insets.top + insets.bottom);
        int nComps = parent.getComponentCount();
        int previousWidth = 0, previousHeight = 0;
        int x = 0, y = insets.top;
        @SuppressWarnings("unused") int rowh = 0, start = 0;
        int xFudge = 0, yFudge = 0;
        boolean oneColumn = false;

        // Go through the components' sizes, if neither
        // preferredLayoutSize nor minimumLayoutSize has
        // been called.
        if (sizeUnknown) {
            setSizes(parent);
        }

        if (maxWidth <= minWidth) {
            oneColumn = true;
        }

        if (maxWidth != preferredWidth) {
            xFudge = (maxWidth - preferredWidth)/(nComps - 1);
        }

        if (maxHeight > preferredHeight) {
            yFudge = (maxHeight - preferredHeight)/(nComps - 1);
        }

        for (int i = 0 ; i < nComps ; i++) {
            Component c = parent.getComponent(i);
            if (c.isVisible()) {
                Dimension d = c.getPreferredSize();

                // increase x and y, if appropriate
                if (i > 0) {
                    if (!oneColumn) {
                        x += previousWidth/2 + xFudge;
                    }
                    y += previousHeight + vgap + yFudge;
                }

                // If x is too large,
                if ((!oneColumn) &&
                    (x + d.width) >
                    (parent.getWidth() - insets.right)) {
                    // reduce x to a reasonable number.
                    x = parent.getWidth()
                        - insets.bottom - d.width;
                }

                // If y is too large,
                if ( ( y + d.height ) > ( parent.getHeight() - insets.bottom ) ) {

                    // do nothing.
                    // Another choice would be to do what we do to x.

                    ObtuseUtil.doNothing();

                }

                // Set the component's size and position.
                c.setBounds(x, y, d.width, d.height);

                previousWidth = d.width;
                previousHeight = d.height;
            }
        }
    }

    public String toString() {
        return getClass().getName() + "( vgap=" + vgap + " )";
    }

    @Override
    public void addLayoutComponent( final Component comp, final Object constraints ) {

        Logger.logMsg(
                "DiagonalLayout.addLayoutComponent( " +
                comp + ", " +
                constraints +
                " )"
        );

    }

    @Override
    public Dimension maximumLayoutSize( final Container target ) {

        Logger.logMsg(
                "DiagonalLayout.maximumLayoutSize( " +
                target +
                " )"
        );

        return new Dimension( 32767, 32767 );

    }

    @Override
    public float getLayoutAlignmentX( final Container target ) {

        Logger.logMsg(
                "DiagonalLayout.getLayoutAlignmentX( " +
                target +
                " )"
        );


        return 0;
    }

    @Override
    public float getLayoutAlignmentY( final Container target ) {

        Logger.logMsg(
                "DiagonalLayout.getLayoutAlignmentY( " +
                target +
                " )"
        );

        return 0;
    }

    @Override
    public void invalidateLayout( final Container target ) {

        Logger.logMsg(
                "DiagonalLayout.invalidateLayout( " +
                target +
                " )"
        );

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init(
                "Obtuse",
                "com.obtuse.ui.layout.play",
                "DiagonalLayout"
        );

        JFrame jf = new JFrame( "Testing DiagonalLayout" );

        JPanel jpd = new JPanel();
        jpd.setLayout( new DiagonalLayout( 0 ) );

        JButton add = new JButton( "Add A Button" );
        add.addActionListener(
                new MyActionListener() {

                    @Override
                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                        jpd.add( new JButton( "button" + jpd.getComponentCount() ) );
                        jpd.revalidate();

                    }

                }
        );
        jpd.add( add );
        jpd.add( new JButton( "button 1" ) );
        jpd.add( new JButton( "button 2" ) );
        jpd.add( new JButton( "button 3" ) );
        jpd.add( new JButton( "button 4" ) );
        jpd.setBorder( BorderFactory.createEtchedBorder() );

        LinearContainer lc = LinearLayoutUtil.createPanel3( "DiagonalLayout test", LinearOrientation.VERTICAL );
        lc.add( jpd );
        LinearLayoutUtil.SpaceSponge sponge = new LinearLayoutUtil.SpaceSponge();
        sponge.setBackground( Color.GRAY );
        lc.add( sponge );
        jf.setContentPane( lc.getAsJPanel() );
        jf.pack();
        jf.setVisible( true );

    }
}
