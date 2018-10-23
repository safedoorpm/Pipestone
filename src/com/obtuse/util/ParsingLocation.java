package com.obtuse.util;

import com.obtuse.exceptions.HowDidWeGetHereError;
import org.jetbrains.annotations.NotNull;

/**
 Describe where something happened.
 */

public class ParsingLocation implements Comparable<ParsingLocation> {

    public static final ParsingLocation UNKNOWN = new ParsingLocation( -1 );
    public static final ParsingLocation VERY_EARLY = new ParsingLocation( 0, 0 );

    /**
     The line where the accident happened.
     <ul><li>-1 if this is the {@link #UNKNOWN} instance or the line number is unknown</li>
     <li>0 if something bad happened before parsing really got started</li>
     <li>otherwise, the positive line number where things went wrong</li>
     </ul>
     */

    public final int lnum;

    /**
     The offset within the line where the accident happened.
     <p>Set to -1 if this is the {@link #UNKNOWN} instance or if the offset is not known.</p>
     */

    public final int offset;

    /**
     Specify if this is the singleton {@link #UNKNOWN} instance (or an equivalent instance).
     */

    public final boolean unknown;

    /**
     Specify if this is the singleton {@link #VERY_EARLY} instance (or an equivalent instance).
     */

    public final boolean early;

    /**
     Create a location description.
     @param lnum the line where the accident happened.
     @param offset the offset within the line where the accident happend.
     */

    public ParsingLocation( final int lnum, final int offset ) {
        super();

        if ( lnum < -1 ) {

            throw new IllegalArgumentException(
                    "ParsingLocation:  invalid lnum (" + lnum + ") - " +
                    "must be -1 (indicating unknown lnum), " +
                    "0 if problem happened before parsing got started, " +
                    "or an actual non-negative offset within the line"
            );

        }

        if ( offset < -1 ) {

            throw new IllegalArgumentException(
                    "ParsingLocation:  " +
                    "invalid offset (" + offset + ") " +
                    "must be -1 (indicating unknown offset or very early) or an actual non-negative offset within the line"
            );

        }

        this.lnum = lnum;
        this.offset = offset;

        unknown = lnum == -1 && offset == -1;
        early = lnum == 0 && offset == -1;

    }

    private ParsingLocation( final int lnum ) {
        super();

        if ( lnum != -1 ) {

            throw new HowDidWeGetHereError( "ParsingLocation:  this is a single-purpose constructor used to create the UKNOWN ParsingLocation instance - please pass lnum=-1" );

        }

        this.lnum = lnum;
        this.offset = -1;

        unknown = true;
        early = false;

    }

    public int hashCode() {

        return ( lnum << 7 ) ^ offset;

    }

    public boolean equals( Object rhs ) {

        return rhs instanceof ParsingLocation && compareTo( (ParsingLocation)rhs ) == 0;

    }

    @Override
    public int compareTo( @NotNull final ParsingLocation o ) {

        int lnumComparison = Integer.compare( lnum, o.lnum );
        if ( lnumComparison == 0 ) {

            return Integer.compare( offset, o.offset );

        } else {

            return lnumComparison;

        }

    }

    public String toString() {

        return "ParsingLocation( lnum=" + lnum + ", offset=" + offset + " )";

    }

    public String pretty() {

        return "[" + lnum + "@" + offset + "]";

    }

}
