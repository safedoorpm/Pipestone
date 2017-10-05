/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.play;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.MessageProxy;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 A layout manager that lays components out in a <i>golden spiral</i>.
 <p/>This layout manager lays out all the components in the container that it is assigned to into a <i>golden spiral</i>
 (see below for more information on <i>golden spirals</i>).
 Each component is forced into the shape of
 an appropriately sized square and placed on the spiral.
 <p/>A few notes are in order:
 <ul>
 <li>The components in the container will appear in the spiral in the same order as they were added to the container.</li>
 <li>This layout manager completely ignores any minimum, preferred or maximum size attributes which might have been specified
 for the components.</li>
 </ul>
 <h3>Golden Spirals, Ratios and Rectangles</h3>
 A <i>golden spiral</i> is essentially a rectangle with squares spiralling in towards a somewhat central point.
 There are four (orthogonal) orientations in which a <i>golden spiral</i> can be drawn:
 <ol>
 <li>as a horizontal rectangle with the first square occupying the left end of the rectangle</li>
 <li>as a vertical rectangle with the first square occupying the top of the rectangle</li>
 <li>as a horizontal rectangle with the first square occupying the right end of the rectangle</li>
 <li>as a vertical rectangle with the first square occupying the bottom of the rectangle</li>
 </ol>
 The nature of <i>golden rectangles</i> is such that if a square of size <code>A x A</code> completely occupies one end of <i>golden rectangle</i> of
 size <code>A x B</code> (<code>A</code> being the width of the rectangle and <code>B</code> being its length) then the rest of the rectangle will be a smaller <i>golden rectangle</i>
 of size <code>( B - A ) x A</code> (this smaller rectangle will have a width of <code>B - A</code> and a length of <code>A</code>).
 This smaller rectangle can itself be divided into a square which fully occupies one end and a yet smaller <i>golden rectangle</i> occupying the rest.
 If one is consistent about which end of each rectangle is used for the square then one ends up with a spiral of squares of rapidly decreasing size.
 This spiral is known as a <i>golden spiral</i>.
 <p/>What makes this (theoretically) infinite spiral possible is the fact that the length of the original rectangle
 is <code>(1+sqrt(5))/2</code> times the width of the original rectangle. This ratio is known as the <i>golden ratio</i>.
 <p/>See the Wikipedia Golden Ratio page at <a href="https://en.wikipedia.org/wiki/Golden_ratio">https://en.wikipedia.org/wiki/Golden_ratio</a> for more information
 (look for the section titled <i>Geometry</i> for quite respectable representation of the <i>golden spirals</i> that this layout manager produces).
 */

public class GoldenRatioLayoutManager implements LayoutManager {

    /**
     The value of the <i>golden ratio</i> (see above referenced Wikipedia article for more info).
     */

    public static final double GOLDEN_RATIO = ( 1 + Math.sqrt( 5 ) ) / 2;

    /**
     The name of this instance (used in logging messages).
     */

    private final String _name;

    /**
     The {@link MessageProxy} instance provided when this instance was created (<code>null</code> if no {@link MessageProxy} instance was provided).
     */

    private final MessageProxy _mp;

    /**
     Construct an unnamed golden ratio layout manager.
     <p/>The resulting named golden ratio layout manager will not produce any logging messages.
     */

    public GoldenRatioLayoutManager() {
	this( null );

    }

    /**
     Construct a unnamed golden ratio layout manager with an optional {@link MessageProxy} instance.
     <p/>See {@link com.obtuse.util.LoggingMessageProxy} for a simple yet reasonably useful message proxy implementation.
     @param mp the optional message proxy instance. If provided, this instance will generate log (debug) messages by sending them to the specified message proxy.
     */

    public GoldenRatioLayoutManager( @Nullable MessageProxy mp ) {
	this( mp, null );

    }

    /**
     Construct a named golden ratio layout manage with an optional {@link MessageProxy} instance.
     <p/>See {@link com.obtuse.util.LoggingMessageProxy} for a simple yet reasonably useful message proxy implementation.
     @param mp the optional message proxy instance. If provided, this instance will generate log (debug) messages by sending them to the specified message proxy.
     @param name the optional name of the about-to-be-created instance (used to tag log messages).
     */

    public GoldenRatioLayoutManager( @Nullable MessageProxy mp, @Nullable String name ) {
	super();

	_name = name;
	_mp = mp;

	logMsg( "GRLM " + getName() + " created" );

    }

    /**
     Get this instance's name.
     @return this instance's name or <code>null</code> if no name was provided when this instance was constructed.
     */

    @Nullable
    public String getName() {

	return _name == null ? "<<unknown>>" : _name;

    }

    /**
     Determine if this instance is producing log messages.
     @return <code>true</code> if this instance is producing log messages via a {@link MessageProxy} instance provided when this
     instance was constructed; <code>false</code> if no such {@link MessageProxy} instance was provided when this instance was constructed.
     */

    public boolean isTracing() {

	return _mp != null;

    }

    /**
     Does nothing of substance (required by {@link LayoutManager} interface).
     <p/>This method is required by the {@link LayoutManager} interface. Calls to this method do nothing other than possibly
     sending log message to the {@link MessageProxy} instance if one was provided when this instance was constructed.
     @param name parameter required by the {@link LayoutManager} interface.
     @param comp parameter required by the {@link LayoutManager} interface.
     */

    @Override
    public void addLayoutComponent( String name, Component comp ) {

	logMsg(
			"GoldenRatioLayoutManager.addLayoutComponent( " +
			ObtuseUtil.enquoteToJavaString( name ) +
			", " +
			GoldenRatioLayoutManager.getComponentName( comp ) +
			" )"
	);

    }

    /**
     Utility method which returns a formatted string describing a {@link Component} instance.
     @param component the component instance.
     @return a message along the lines of <code>JPanel (fred)</code> where <i>JPanel</i> would be the type of component and
     <i>fred</i> would be the value returned by <code>component.getName()</code>.
     */

    public static String getComponentName( Component component ) {

	return component.getClass().getSimpleName() + "(" + component.getName() + ")";

    }

    /**
     Does nothing of substance (required by {@link LayoutManager} interface).
     <p/>This method is required by the {@link LayoutManager} interface. Calls to this method do nothing other than possibly
     sending log message to the {@link MessageProxy} instance if one was provided when this instance was constructed.
     @param component parameter required by the {@link LayoutManager} interface.
     */

    @Override
    public void removeLayoutComponent( Component component ) {

	logMsg( "GoldenRatioLayoutManager.removeLayoutComponent( " + GoldenRatioLayoutManager.getComponentName( component ) + " )" );

    }

    /**
     Returns the preferred size for the specified container based on the components that it contains.
     <p/>This implementation simply returns the value returned when passing the specified container to this instance's {@link GoldenRatioLayoutManager#minimumLayoutSize} method.
     @param container the container of interest.
     @return the preferred size.
     */

    @NotNull
    @Override
    public Dimension preferredLayoutSize( Container container ) {

	Dimension preferredSize = minimumLayoutSize( container );

//	logMsg( "GoldenRatioLayoutManager.preferredLayoutSize( " + GoldenRatioLayoutManager.getComponentName( container ) + " ) is " + preferredSize );

	return preferredSize;

    }

    /** Returns the minimum size for the specified container based on the components that it contains.
     <p/>There is no actual minimum size for containers laid out by this layout manager.
     This method always returns <code>new Dimension( 25, 25 )</code> since anything smaller would be truly pointless.
     In reality, anything smaller than about 100x60 is going to look pretty ugly.
     @param container the container of interest.
     @return the result of invoking <code>new Dimension( 25, 25 )</code>.
     */

    @NotNull
    @Override
    public Dimension minimumLayoutSize( Container container ) {

	return new Dimension( 25, 25 );

    }

    /**
     Compute the usable size of the specified container taking into account its insets.
     <p/>This method is implemented precisely as follows:
     <blockquote><code>	Insets insets = container.getInsets();<br>
     return new Dimension( insets.left + insets.right, insets.top + insets.bottom );</code>
     </blockquote>
     @param container the container of interest.
     @return the usable size of the specified container once its insets have been taken into account.
     */

    @NotNull
    private Dimension computeSizeWithinInsets( Container container ) {

	Insets insets = container.getInsets();
	return new Dimension( insets.left + insets.right, insets.top + insets.bottom );

    }

    /**
     Layout the components in the specified container into a <i>golden spiral</i>.
     <p/>
     This layout manager operates as follows:
     <ol>
     <li>This method computes the largest <i>golden rectangle</i> which can fit entirely within the specified container's
     usable width and height, as computed by {@link #computeSizeWithinInsets}.<br><br>
     </li>
     <li>If the largest <i>golden rectangle</i> (rectangle #1) that fits is horizontally oriented then it is centered vertically
     within the container.<ol><li 'a'>
     The first component in the
     specified container is resized to be a square that completely occupies the left end of rectangle #1.
     </li><li>
     The remainder of rectangle #1 will be a smaller vertically oriented <i>golden rectangle</i> (rectangle #2).
     The second component in the container is then resized to be a square that completely occupies the upper end
     of rectangle #2.
     </li><li>
     The remainder of rectangle #2 will be a smaller horizontally oriented <i>golden rectangle</i> (rectangle #3).
     The third component in the container is then resized to be a square that completely occupies the right end
     of rectangle #3.
     </li><li>
     The remainder of rectangle #3 will be a smaller vertically oriented <i>golden rectangle</i> (rectangle #4).
     The fourth component in the container is then resized to be a square that completely occupies the lower end
     of rectangle #4.
     </li><li>
     This process continues until all components in the container have been resized and placed.</li></ol>
     </li>
     <li>Alternatively, if the largest <i>golden rectangle</i> that fits is vertically oriented then it is centered horizontally
     within the container and the procedure described in the previous step proceeds starting with rectangle #2.</li>
     </ol>
     The size of each square in a <i>golden spiral</i> is roughly 2/3 the size of the previous square.
     Consequently, the sizes of each subsequent square rapidly shrinks to the point of invisibility.
     Unless you have a very large monitor, there is probably little point in using this layout manager on a container
     which contains more than about a dozen components.
     @param container the container whose contents are to be arranged into a <i>golden spiral</i>.
     */

    @Override
    public void layoutContainer( Container container ) {

	Insets insets = container.getInsets();
	@SuppressWarnings("UnnecessaryLocalVariable") int originalAvailableWidth = container.getWidth() - ( insets.left + insets.right );
	int availableWidth = originalAvailableWidth;
	@SuppressWarnings("UnnecessaryLocalVariable") int originalAvailableHeight = container.getHeight() - ( insets.top + insets.bottom );
	int availableHeight = originalAvailableHeight;

	logMsg(
		"GoldenRatioLayoutManager.layoutContainer( " + GoldenRatioLayoutManager.getComponentName( container ) + " ):  " +
		ObtuseUtil.fDim( "minSize", container.getMinimumSize() ) + ", " +
		ObtuseUtil.fDim( "maxSize", container.getMaximumSize() ) + ", " +
		ObtuseUtil.fDim( "prefSize", container.getPreferredSize() ) + ", " +
		ObtuseUtil.fDim( "size", container.getSize() ) + ", " +
		ObtuseUtil.fDim( "availableSize", new Dimension( availableWidth, availableHeight ) )
	);

	Dimension bestRectangleSize = computeBestValidRectangleSize(
		null,
		availableWidth,
		availableHeight,
		(int)( availableHeight * GoldenRatioLayoutManager.GOLDEN_RATIO ),
		availableHeight
	);
	bestRectangleSize = computeBestValidRectangleSize(
		bestRectangleSize,
		availableWidth,
		availableHeight,
		(int)( availableHeight / GoldenRatioLayoutManager.GOLDEN_RATIO ),
		availableHeight
	);
	bestRectangleSize = computeBestValidRectangleSize(
		bestRectangleSize,
		availableWidth,
		availableHeight,
		availableWidth,
		(int)( availableWidth * GoldenRatioLayoutManager.GOLDEN_RATIO )
	);
	bestRectangleSize = computeBestValidRectangleSize(
		bestRectangleSize,
		availableWidth,
		availableHeight,
		availableWidth,
		(int) ( availableWidth / GoldenRatioLayoutManager.GOLDEN_RATIO )
	);
	logMsg( ObtuseUtil.fDim( "final", bestRectangleSize ) );

//	double widthOverHeight = availableWidth / (double)availableHeight;
//	if ( 1 / widthOverHeight > GoldenRatioLayoutManager.GOLDEN_RATIO ) {
//
//	    availableHeight = adjustHeight( "a", availableWidth, availableHeight, widthOverHeight, false );
//
//	} else if ( widthOverHeight > GoldenRatioLayoutManager.GOLDEN_RATIO ) {
//
//	    availableWidth = adjustWidth( "b", availableWidth, availableHeight, widthOverHeight, true );
//
//	} else {
//
//	    logMsg( "just right" );
//
//	    if ( availableWidth > availableHeight ) {
//
//		availableHeight = adjustHeight( "c", availableWidth, availableHeight, widthOverHeight, true );
//
//	    } else {
//
//		availableWidth = adjustWidth( "d", availableWidth, availableHeight, widthOverHeight, false );
//
//	    }
//
//	}
//
//	logMsg( "so far:  " + fDim( "best", bestRectangleSize ) + ", " + fDim( "other", new Dimension( availableWidth, availableHeight) ) + ", " + fDim( "original", new Dimension( originalAvailableWidth, originalAvailableHeight ) ) );
//
//	if ( Math.abs( originalAvailableWidth - availableWidth ) <= 1 ) {
//
//	    availableWidth = originalAvailableWidth;
//
//	}
//
//	if ( Math.abs( originalAvailableHeight - availableHeight ) <= 1 ) {
//
//	    availableHeight = originalAvailableHeight;
//
//	}
//
//	logMsg( "aW=" + availableWidth + ", aH=" + availableHeight );
//
//	if ( !fuzzyEquals( bestRectangleSize, new Dimension( availableWidth, availableHeight ) ) ) {
////	if ( bestRectangleSize.width != availableWidth || bestRectangleSize.height != availableHeight ) {
//
//	    logMsg( "trouble:  " + fDim( "best", bestRectangleSize ) + ", " + fDim( "other", new Dimension( availableWidth, availableHeight) ) );
//	    ObtuseUtil.doNothing();
//
//	}

	availableWidth = bestRectangleSize.width;
	availableHeight = bestRectangleSize.height;

	int x, y;
	int stage;
	if ( availableWidth > availableHeight ) {

	    x = insets.left;
	    y = insets.top;
	    stage = 0;

	} else {

	    x = insets.left;
	    y = insets.top/* + availableHeight*/;
	    stage = 3;

	}

	int maxWidth = availableWidth;
	int maxHeight = availableHeight;
	Component[] components = container.getComponents();
	int i = 0;
	double nextHeight = stage == 0 ? availableWidth / GOLDEN_RATIO : availableHeight;
	double nextWidth = stage == 0 ? availableWidth : availableHeight / GOLDEN_RATIO;

	int ulx = x, uly = y;
	int urx = x + availableWidth, ury = y;
	int llx = x, lly = y + availableHeight;
	int lrx = x + availableWidth, lry = y + availableHeight;

	int newX, newY;

	while ( true ) {

	    if ( i == components.length ) {

		break;

	    }

	    if ( nextWidth == 0 ) {

		logMsg( "width too narrow at i=" + i + ", stage=" + stage );

		break;

	    }

	    if ( nextHeight == 0 ) {

		logMsg( "height too narrow at i=" + i + ", stage=" + stage );

		break;

	    }

	    if ( components[i].isVisible() ) {

		int delta;

		switch ( stage ) {

		    case 0:
			newX = ulx + (int)( ( urx - ulx ) / GOLDEN_RATIO );
			logMsg(
				"stage 0:  " +
				ObtuseUtil.fDim( "ul", new Dimension( ulx, uly ) ) + ", " +
				ObtuseUtil.fDim( "ur", new Dimension( urx, ury ) ) + ", " +
				ObtuseUtil.fDim( "ll", new Dimension( llx, lly ) ) + ", " +
				ObtuseUtil.fDim( "lr", new Dimension( lrx, lry ) ) + ", " +
				"newX=" + newX
			);
			setBounds( stage, components[ i ], ulx, uly, newX - ulx, lly - uly, nextWidth / nextHeight );
			ulx = newX;
			llx = newX;
			nextWidth = nextWidth - nextHeight;
			stage = 1;

			break;

		    case 1:
			newY = uly + (int)( ( lry - ury ) / GOLDEN_RATIO );
			logMsg(
				"stage 1:  " +
				ObtuseUtil.fDim( "ul", new Dimension( ulx, uly ) ) + ", " +
				ObtuseUtil.fDim( "ur", new Dimension( urx, ury ) ) + ", " +
				ObtuseUtil.fDim( "ll", new Dimension( llx, lly ) ) + ", " +
				ObtuseUtil.fDim( "lr", new Dimension( lrx, lry ) ) + ", " +
				"newY=" + newY
			);
			setBounds( stage, components[ i ], ulx, uly, urx - ulx, newY - uly, nextHeight / nextWidth );
			uly = newY;
			ury = newY;
			nextHeight -= nextWidth;
			stage = 2;
			break;

		    case 2:
			newX = urx - (int)( ( urx - ulx ) / GOLDEN_RATIO );
			logMsg(
				"stage 2:  " +
				ObtuseUtil.fDim( "ul", new Dimension( ulx, uly ) ) + ", " +
				ObtuseUtil.fDim( "ur", new Dimension( urx, ury ) ) + ", " +
				ObtuseUtil.fDim( "ll", new Dimension( llx, lly ) ) + ", " +
				ObtuseUtil.fDim( "lr", new Dimension( lrx, lry ) ) + ", " +
				"newX=" + newX
			);
			setBounds( stage, components[i], newX, uly, urx - newX, lry - ury, nextWidth / nextHeight );
			urx = newX;
			lrx = newX;
			nextWidth -= nextHeight;
			stage = 3;
			break;

		    case 3:
			newY = lly - (int)( ( lly - uly ) / GOLDEN_RATIO );
			logMsg(
				"stage 3:  " +
				ObtuseUtil.fDim( "ul", new Dimension( ulx, uly ) ) + ", " +
				ObtuseUtil.fDim( "ur", new Dimension( urx, ury ) ) + ", " +
				ObtuseUtil.fDim( "ll", new Dimension( llx, lly ) ) + ", " +
				ObtuseUtil.fDim( "lr", new Dimension( lrx, lry ) ) + ", " +
				"newY=" + newY
			);
			setBounds( stage, components[i], llx, newY, lrx - llx, lry - newY, nextHeight / nextHeight );
			lly = newY;
			lry = newY;
			nextHeight -= nextWidth;
			stage = 0;
			break;

		    default:
			throw new HowDidWeGetHereError( "absurd stage (" + stage + ")" );

		}

	    }

	    i += 1;

	}

//	while ( true ) {
//
//	    if ( i == components.length ) {
//
//		break;
//
//	    }
//
//	    if ( nextWidth == 0 ) {
//
//		logMsg( "width too narrow at i=" + i + ", stage=" + stage );
//
//		break;
//
//	    }
//
//	    if ( nextHeight == 0 ) {
//
//		logMsg( "height too narrow at i=" + i + ", stage=" + stage );
//
//		break;
//
//	    }
//
//	    if ( components[i].isVisible() ) {
//
//		int delta;
//
//		switch ( stage ) {
//
//		    case 0:
//			setBounds( stage, components[ i ], x, y, nextHeight, nextHeight, nextWidth / nextHeight );
//			x = adj( x + nextHeight );
//			nextWidth = nextWidth - nextHeight;
//			stage = 1;
//
//			break;
//
//		    case 1:
//			setBounds( stage, components[ i ], x, y, nextWidth, nextWidth, nextHeight / nextWidth );
//			y = adj( y + nextWidth );
//			nextHeight -= nextWidth;
//			stage = 2;
//			break;
//
//		    case 2:
//			setBounds( stage, components[i], adj( x + nextWidth - nextHeight ), y, nextHeight, nextHeight, nextWidth / nextHeight );
//			nextWidth -= nextHeight;
//			stage = 3;
//			break;
//
//		    case 3:
//			setBounds( stage, components[i], x, adj( y + nextHeight - nextWidth ), nextWidth, nextWidth, nextHeight / nextHeight );
//			nextHeight -= nextWidth;
//			stage = 0;
//			break;
//
//		    default:
//			throw new HowDidWeGetHereError( "absurd stage (" + stage + ")" );
//
//		}
//
//	    }
//
//	    i += 1;
//
//	}

//	for ( Component c : container.getComponents() ) {
//
//	    if ( c.isVisible() ) {
//
//		Dimension cDim = c.getPreferredSize();
//		c.setBounds( x + ( maxWidth - cDim.width ) / 2, y, cDim.width, cDim.height );
//
//		y += Math.max( cDim.width, cDim.height );
//
//	    }
//
//	}

    }

    private void setBounds( int stage, Component c, int x, int y, int w, int h, double ratio ) {

	logMsg(
		"                                                                      stage " +
		stage +
		":  c.setBounds( " +
		x +
		", " +
		y +
		", " +
		w +
		", " +
		h +
		" ), ratio=" +
		ratio
	);
	c.setBounds( x, y, w, h );

    }

//    private void setBounds( int stage, Component c, int x, int y, double w, double h, double ratio ) {
//
//	logMsg( "stage " + stage + ":  c.setBounds( " + x + ", " + y + ", " + w + "=" + adj(w) + ", " + h + "=" + adj(h) + " ), ratio=" + ratio );
//	c.setBounds( x, y, adj(w), adj(h) );
//
//    }

    private static int adj( double d ) {

	return round( d );

    }

    private static int round( double d ) {

	return (int) Math.floor( (double) ( d + 0.5F ) );

    }

    private boolean fuzzyEquals( Dimension lhs, Dimension rhs ) {

	return Math.abs( lhs.width - rhs.width ) <= 1 && Math.abs( lhs.height - rhs.height ) <= 1;

    }

    private Dimension computeBestValidRectangleSize(
	    Dimension bestRectangleSize,
	    int availableWidth,
	    int availableHeight,
	    int proposedWidth,
	    int proposedHeight
    ) {

	if ( proposedWidth == availableWidth - 1 || proposedWidth == availableWidth + 1 ) {

	    proposedWidth = availableWidth;

	}

	if ( proposedHeight == availableHeight - 1 || proposedHeight == availableHeight + 1 ) {

	    proposedHeight = availableHeight;

	}

	if ( proposedHeight > availableHeight ) {

	    logMsg( "fail1:  " + ObtuseUtil.fDim( "best", bestRectangleSize ) );
	    return bestRectangleSize;

	}

	if ( proposedWidth > availableWidth ) {

	    logMsg( "fail2:  " + ObtuseUtil.fDim( "best", bestRectangleSize ) );
	    return bestRectangleSize;

	}

	Dimension newSize = new Dimension( proposedWidth, proposedHeight );
	if ( bestRectangleSize == null ) {

	    logMsg( "first:  " + ObtuseUtil.fDim( "new", newSize ) );
	    return newSize;

	}

	if ( newSize.width * newSize.height > bestRectangleSize.width * bestRectangleSize.height ) {

	    logMsg( "new:  " + ObtuseUtil.fDim( "new", newSize ) );
	    return newSize;

	} else {

	    logMsg( "old:  " + ObtuseUtil.fDim( "best", bestRectangleSize ) );
	    return bestRectangleSize;

	}

    }

    private int adjustWidth( String why, int availableWidth, int availableHeight, double widthOverHeight, boolean horizontal ) {

	logMsg( "adjust width(" + why + "):  w/h=" + widthOverHeight );

	int newAvailableWidth = horizontal ? (int)Math.round( availableHeight * GoldenRatioLayoutManager.GOLDEN_RATIO ) : (int)Math.round( availableHeight / GoldenRatioLayoutManager.GOLDEN_RATIO );
	if ( newAvailableWidth > availableWidth ) {

	    throw new IllegalArgumentException(
		    "width adjustment failed:  availableWidth=" + availableWidth + ", availableHeight=" + availableHeight + ", newAvailableWidth=" + newAvailableWidth + ", w/h=" + widthOverHeight
	    );

	}

	if ( horizontal ) {

	    if ( availableHeight > newAvailableWidth ) {

		throw new IllegalArgumentException(
			"width adjustment failed (not horizontal):  availableWidth=" + availableWidth + ", availableHeight=" + availableHeight + ", newAvailableWidth=" + newAvailableWidth + ", h/w=" + 1 / widthOverHeight
		);

	    }

	} else {

	    if ( availableHeight < newAvailableWidth ) {

		throw new IllegalArgumentException(
			"width adjustment failed (not vertical):  availableWidth=" + availableWidth + ", availableHeight=" + availableHeight + ", newAvailableWidth=" + newAvailableWidth + ", h/w=" + 1 / widthOverHeight
		);

	    }

	}

	logMsg(
		"### adjusting width:  availableHeight=" +
		availableHeight +
		", oldAvailableWidth=" +
		availableWidth +
		", newAvailableWidth=" +
		newAvailableWidth +
		", ratio=" +
		availableHeight / (double) newAvailableWidth
	);

	return newAvailableWidth;

    }

    private int adjustHeight( String why, int availableWidth, int availableHeight, double widthOverHeight, boolean horizontal ) {

	logMsg( "adjust height(" + why + "):  h/w=" + 1 / widthOverHeight );
	int newAvailableHeight = horizontal ? (int)Math.round( availableWidth / GoldenRatioLayoutManager.GOLDEN_RATIO ) : (int)Math.round( availableWidth * GoldenRatioLayoutManager.GOLDEN_RATIO );
	if ( newAvailableHeight > availableHeight ) {

	    throw new IllegalArgumentException(
		    "height adjustment failed:  availableWidth=" + availableWidth + ", availableHeight=" + availableHeight + ", newAvailableHeight=" + newAvailableHeight + ", h/w=" + 1 / widthOverHeight
	    );

	}

	if ( horizontal ) {

	    if ( newAvailableHeight > availableWidth ) {

		throw new IllegalArgumentException(
			"height adjustment failed (not horizontal):  availableWidth=" + availableWidth + ", availableHeight=" + availableHeight + ", newAvailableHeight=" + newAvailableHeight + ", h/w=" + 1 / widthOverHeight
		);

	    }

	} else {

	    if ( newAvailableHeight < availableWidth ) {

		throw new IllegalArgumentException(
			"height adjustment failed (not vertical):  availableWidth=" + availableWidth + ", availableHeight=" + availableHeight + ", newAvailableHeight=" + newAvailableHeight + ", h/w=" + 1 / widthOverHeight
		);

	    }

	}

	logMsg(
		"### adjusting height:  availableWidth=" +
		availableWidth +
		", oldAvailableHeight=" +
		availableHeight +
		", newAvailableHeight=" +
		newAvailableHeight +
		", ratio=" +
		availableWidth / (double) newAvailableHeight
	);

	return newAvailableHeight;

    }

    private void logMsg( String msg ) {

	if ( _mp != null ) {

	    _mp.info( msg );

	}

    }

//    public static String fDim( String name, Dimension d ) {
//
//	if ( d == null ) {
//
//	    return name + "=null";
//
//	} else {
//
//	    return name + "=(" + d.width + "," + d.height + ")";
//
//	}
//
//    }

    public String toString() {

	return "GoldenRatioLayoutManager()";

    }

}