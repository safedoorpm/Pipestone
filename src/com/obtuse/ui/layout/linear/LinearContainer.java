package com.obtuse.ui.layout.linear;

import com.obtuse.ui.layout.ConstraintTuple;
import com.obtuse.ui.layout.LinearOrientation;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Specify how a linear container implementation behaves.
 This interface primarily exists to allow for testing and development of new linear container implementations.
 <p/>Note that most of these methods are the ones in Swing and AWT that users of linear containers tend to call.
 Specifying them here avoids the code clutter associated with a whole pile of calls to {@link #getAsContainer()}.
 */

public interface LinearContainer {

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

    /**
     Swing/AWT method.
     <p/> {@link JComponent#remove(int)} for more info.
     */

    void remove( int ix );

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
     Return this instance as a {@link JComponent}.

     @return this instance cast as a {@link JComponent}.
     */

    default JComponent getAsJComponent() {

        return (JComponent)this;

    }

    /**
     Get this linear container's orientation.

     @return this linear container's orientation.
     @see LinearOrientation#VERTICAL
     @see LinearOrientation#HORIZONTAL
     */

    LinearOrientation getOrientation();

    boolean isVertical();

    boolean isHorizontal();

    /**
     Provide a way to constrain the length of a component within a linear container.
     <p/>Constraining a component's length ensures that its height (in the case of vertically oriented components) or
     its width (in the case of horizontally oriented components) is within a specified minimum to maximum range.
     <p/>It is not clear that this ability to constrain a component's length is useful.

     @param minLength the minimum allowed length of a component in this linear container.
     Components which are shorter than this will be forced to be this long.
     @param maxLength the maximum allowed length of a component in this linear container.
     Components which are longer than this will be forced to be this long.
     */

    void setLengthConstraints( int minLength, int maxLength );

    /**
     Provide a way to constrain the length of a component within a linear container.
     <p/>See {@link #setLengthConstraints(int, int)} and {@link ConstraintTuple} for more information.
     */

    void setLengthConstraints( ConstraintTuple lengthConstraints );

    /**
     Provide a way to constrain the breadth of a component within a linear container.
     <p/>Constraining a component's breadth ensures that its width (in the case of vertically oriented components) or
     its height (in the case of horizontally oriented components) is within a specified minimum to maximum range.

     @param minBreadth the minimum allowed breadth of a component in this linear container.
     Components which are narrower than this will be forced to have this breadth.
     @param maxBreadth the maximum allowed breadth of a component in this linear container.
     Components which are broader than this will be forced to have this breadth.
     */

    void setBreadthConstraints( int minBreadth, int maxBreadth );

    /**
     Provide a way to constrain the length of a component within a linear container.

     @param breadthConstraints the breadth constraints for this instance.
     A {@code null} value removes this instance's breadth constraints.
     <p/>See {@link #setLengthConstraints(int, int)} and {@link ConstraintTuple} for more information.
     */

    void setBreadthConstraints( ConstraintTuple breadthConstraints );

    /**
     Apply this instance's breadth constraints to a specified value.

     @param v the specified value.
     @return the specified value if this instance has no breadth constraints.
     Otherwise, the value forced into the range [minimum breadth,maximum breadth] (values which are too low yield the minimum
     breadth, values which are too high yield the maximum breadth, all other values yield themselves).
     */

    default int applyBreadthConstraints( int v ) {

        return applyConstraints( getBreadthConstraints(), v );

    }

    /**
     Apply this instance's length constraints to a specified value.

     @param v the specified value.
     @return the specified value if this instance has no length constraints.
     Otherwise, the value forced into the range [minimum length,maximum length] (values which are too low yield the minimum
     length, values which are too high yield the maximum length, all other values yield themselves).
     */

    default int applyLengthConstraints( int v ) {

        return applyConstraints( getLengthConstraints(), v );

    }

    /**
     Apply a {@link ConstraintTuple} to a specified value.

     @param doublet the {@link ConstraintTuple} to be applied to the specified value (how often does one get to legitimately
     name a parameter after an anagram of one's first initial and last name? Pretty cool! - D. Boulet).
     @param v       the specified value.
     @return the specified value if {@code doublet} is {@code null}.
     Otherwise, the value forced into the range {@code [doublet.minimum,doublet.maximum]} (values which are too low yield the {@code doublet.minimum} value,
     values which are too high yield the {@code doublet.maximum} value, all other values yield themselves).
     */

    default int applyConstraints( ConstraintTuple doublet, int v ) {

        if ( doublet == null ) {

            return v;

        } else {

            @SuppressWarnings("UnnecessaryLocalVariable")
            int newValue = Math.min( Math.max( doublet.minimum, v ), doublet.maximum );

            return newValue;

        }

    }

    /**
     Get this instance's length constraints.

     @return this instance's length {@link ConstraintTuple} or {@code null} if this instance has no length {@link ConstraintTuple}.
     */

    ConstraintTuple getLengthConstraints();

    /**
     Get this instance's breadth constraints.

     @return this instance's breadth {@link ConstraintTuple} or {@code null} if this instance has no breadth {@link ConstraintTuple}.
     */

    ConstraintTuple getBreadthConstraints();

}
