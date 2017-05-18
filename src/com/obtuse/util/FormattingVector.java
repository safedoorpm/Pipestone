/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.util.List;
import java.util.Vector;

/**
 * An extension of the {@link java.util.Vector} which has a toString that formats the list's contents.
 */
@SuppressWarnings("UnusedDeclaration")
public class FormattingVector<E> extends Vector<E> {

    public static final int DEFAULT_MAX_ELEMENTS_TO_FORMAT = 10;
    public static final int DEFAULT_TAIL_ELEMENTS_TO_ALWAYS_FORMAT = 3;

    private int _maxElementsToFormat = DEFAULT_MAX_ELEMENTS_TO_FORMAT;
    private int _tailElementsToAlwaysFormat = DEFAULT_TAIL_ELEMENTS_TO_ALWAYS_FORMAT;

    public FormattingVector() {
        super();

    }

    public FormattingVector( java.util.Collection<? extends E> es ) {
        super( es );

    }

    private StringBuilder formatNakedRange( int startIx, int endIx ) {

        StringBuilder sb = new StringBuilder();
        String comma = "";
        for ( int ix = startIx; ix <= endIx; ix += 1 ) {

            sb.append( comma ).append( get( ix ).toString() );

            comma = ", ";

        }

        return sb;

    }

    public void setMaxElementsToFormat( int maxElementsToFormat ) {

	_maxElementsToFormat = maxElementsToFormat;

    }

    public int getMaxElementsToFormat() {

	return _maxElementsToFormat;

    }

    public void setTailElementsToAlwaysFormat( int tailElementsToAlwaysFormat ) {

	_tailElementsToAlwaysFormat = tailElementsToAlwaysFormat;

    }

    public int getTailElementsToAlwaysFormat() {

	return _tailElementsToAlwaysFormat;

    }

    public String toString() {

        StringBuilder rval = new StringBuilder( "{" );

        if ( !isEmpty() ) {

            rval.append( " " );

            if ( size() > _maxElementsToFormat ) {

                rval.append( formatNakedRange( 0, _maxElementsToFormat - ( _tailElementsToAlwaysFormat + 1 ) ) )
                        .append( " ... " )
                        .append( formatNakedRange( size() - _tailElementsToAlwaysFormat, size() - 1 ) );

            } else {

                rval.append( formatNakedRange( 0, size() - 1 ) );

            }

        }

        rval.append( " }" );

        return rval.toString();

    }

    public static void main( String[] args ) {

        List<Integer> list = new FormattingVector<>();

        for ( int i = 0; i < 15; i += 1 ) {

            System.out.println( "" + list );

            list.add( i );

        }

        System.exit( 0 );

    }

}
