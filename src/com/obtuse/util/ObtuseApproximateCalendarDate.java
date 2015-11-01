package com.obtuse.util;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.exceptions.ParsingException;
import com.obtuse.util.gowing.packer2.*;
import com.obtuse.util.gowing.packer2.p2a.GowingEntityReference;
import com.obtuse.util.gowing.packer2.p2a.GowingUnPacker2ParsingException;
import com.obtuse.util.gowing.packer2.p2a.holders.GowingStringHolder2;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 Represent an approximate calendar date.
 <p/>
 Instances of this class are immutable and implement {@link GowingPackable2}.
 */

public class ObtuseApproximateCalendarDate extends GowingAbstractPackableEntity2 implements Comparable<ObtuseApproximateCalendarDate> {

    private static final EntityTypeName2 ENTITY_TYPE_NAME = new EntityTypeName2( ObtuseApproximateCalendarDate.class );

    private static final int VERSION = 1;

    private static final EntityName2 DS_NAME = new EntityName2( "_ds" );

    private static final EntityName2 PRECISION_NAME = new EntityName2( "_pr" );

    public static GowingEntityFactory2 FACTORY = new GowingEntityFactory2( ENTITY_TYPE_NAME ) {

	@Override
	public int getOldestSupportedVersion() {

	    return VERSION;
	}

	@Override
	public int getNewestSupportedVersion() {

	    return VERSION;
	}

	@NotNull
	@Override
	public GowingPackable2 createEntity( @NotNull GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er )
		throws GowingUnPacker2ParsingException {

	    return new ObtuseApproximateCalendarDate( unPacker, bundle, er );

	}

    };

    public enum DatePrecision {

	DATE,
	MONTH,
	YEAR,
	DECADE
    }

    private final ObtuseCalendarDate _calendarDate;

    private final DatePrecision _precision;

    public ObtuseApproximateCalendarDate( ObtuseCalendarDate calendarDate, @NotNull DatePrecision precision ) {
	super();

	_calendarDate = calendarDate;

	_precision = precision;

    }

    public ObtuseApproximateCalendarDate( GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) throws GowingUnPacker2ParsingException {
	this( ObtuseApproximateCalendarDate.makeCalendarDate( bundle ),
	      ObtuseApproximateCalendarDate.makePrecision( bundle ) );

    }

    private static ObtuseCalendarDate makeCalendarDate( GowingPackedEntityBundle bundle ) throws GowingUnPacker2ParsingException {

	try {

	    return new ObtuseCalendarDate( bundle.getNotNullField( ObtuseApproximateCalendarDate.DS_NAME ).StringValue() );

	} catch ( ParsingException e ) {

	    throw new GowingUnPacker2ParsingException( e + " recovering date string" );

	}

    }

    private static DatePrecision makePrecision( GowingPackedEntityBundle bundle ) throws GowingUnPacker2ParsingException {

	try {

	    return DatePrecision.valueOf( bundle.getNotNullField( ObtuseApproximateCalendarDate.PRECISION_NAME ).StringValue() );

	} catch ( IllegalArgumentException e ) {

	    throw new GowingUnPacker2ParsingException( e + " recovering precision" );

	}

    }

    @NotNull
    @Override
    public GowingPackedEntityBundle bundleThyself(
	    boolean isPackingSuper, GowingPacker2 packer
    ) {

	GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
		ObtuseApproximateCalendarDate.ENTITY_TYPE_NAME,
		ObtuseApproximateCalendarDate.VERSION,
		null,
		packer.getPackingContext()
	);

	bundle.addHolder( new GowingStringHolder2( ObtuseApproximateCalendarDate.DS_NAME, getCalendarDate().getDateString(), true ) );
	bundle.addHolder( new GowingStringHolder2( ObtuseApproximateCalendarDate.PRECISION_NAME, getPrecision().name(), true ) );

	return bundle;

    }

    @Override
    public boolean finishUnpacking( GowingUnPacker2 unPacker ) {

	return true;

    }

    //    public ObtuseApproximateCalendarDate( GowingUnPacker2 unPacker, GowingPackedEntityBundle bundle, GowingEntityReference er ) throws GowingUnPacker2ParsingException {
//	super();
//
//	try {
//
//	    _calendarDate = new ObtuseCalendarDate( bundle.getNotNullField( ObtuseApproximateCalendarDate.DS_NAME ).StringValue() );
//
//	} catch ( ParsingException e ) {
//
//	    throw new GowingUnPacker2ParsingException( e + " recovering date string" );
//
//	}
//
//	try {
//
//	    _precision = DatePrecision.valueOf( bundle.getNotNullField( ObtuseApproximateCalendarDate.PRECISION_NAME ).StringValue() );
//
//	} catch ( IllegalArgumentException e ) {
//
//	    throw new GowingUnPacker2ParsingException( e + " recovering precision" );
//
//	}
//
//    }

    public ObtuseCalendarDate getCalendarDate() {

	return _calendarDate;

    }

    public DatePrecision getPrecision() {

	return _precision;

    }

//    @NotNull
//    @Override
//    public GowingPackedEntityBundle bundleThyself(
//	    boolean isPackingSuper, GowingPacker2 packer
//    ) {
//
//	GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
//		ObtuseApproximateCalendarDate.ENTITY_TYPE_NAME,
//		ObtuseApproximateCalendarDate.VERSION,
//		null,
//		packer.getPackingContext()
//	);
//
//	bundle.addHolder( new GowingStringHolder2( ObtuseApproximateCalendarDate.DS_NAME, _calendarDate.getDateString(), true ) );
//	bundle.addHolder( new GowingStringHolder2( ObtuseApproximateCalendarDate.PRECISION_NAME, _precision.name(), true ) );
//
//	return bundle;
//
//    }
//
//    @Override
//    public boolean finishUnpacking( GowingUnPacker2 unPacker ) {
//
//	return true;
//
//    }

    /**
     Format an approximate calendar date while taking into account its precision.
     <p/>Dates are rounded as follows:
     <ul>
     <li>date accurate dates are not rounded at all (1998-07-12 yields 1998-07-12).</li>
     <li>month accurate dates have the day of month chopped off (1998-07-12 yields 1998-07).</li>
     <li>year accurate dates have the month and day of month chopped off (1998-07-12 yields 1998).</li>
     <li>decade accurate dates have the month and day of month chopped off and the last digit of the year replaced with 0.</li>
     </ul>
     @return this instance's formatted date taking into account its precision.
     */

    public String format() {

	switch ( _precision ) {

	    case DATE: return getCalendarDate().getDateString();

	    case MONTH: return getCalendarDate().getDateString().substring( 0, 7 );

	    case YEAR: return getCalendarDate().getDateString().substring( 0, 4 );

	    case DECADE: return getCalendarDate().getDateString().substring( 0, 3 ) + '0';

//		/*
//		Round to the nearest decade where years ending in 8 and 9 round up and all other years round down.
//		In other words, 1998 through 2007 round to the decade 2000, 2008 through 2018 round to the decade 2010, etc.
//		 */
//
//		int year = Integer.parseInt( getCalendarDate().getDateString().substring( 0, 4 ) );
//		int yearDigit = year % 10;
//		int adjustedYearDigit = yearDigit - 3;
//		int decade = year / 10;
//		if ( adjustedYearDigit < 5 ) {
//
//		    return "" + ( decade * 10 );
//
//		} else {
//
//		    return "" + ( ( decade + 1 ) * 10 );
//
//		}

	    default:
		return getCalendarDate().getDateString();

	}

    }

//    /**
//     Compare two instances based on the actual date that was provided when the instances were created.
//     @param rhs the other instance.
//     @return -1, 0 or 1 as specified by {@link Comparable#compareTo(Object)}.
//     */
//
//    private final int compareTo( ObtuseApproximateCalendarDate rhs ) {
//
//	throw new IllegalArgumentException( "ObtuseApproximateCalendarDate cannot safely implement the Comparable interface as there is no safe way to" +
//					    " incorporate the date's precision into the comparison and yet this class's equals method must incorporate" +
//					    " the date's precision to provide sensible results." );
//
//    }

    /**
     Compare two instances primarily based on the specified dates and secondarily on the specified precisions.
     @param rhs the other instance.
     @return -1, 0 or 1 depending on whether this instance is less than, equal to or greater than the other instance.
     */

    public int compareTo( @NotNull ObtuseApproximateCalendarDate rhs ) {

	int rval = getCalendarDate().compareTo( rhs.getCalendarDate() );
	if ( rval == 0 ) {

	    return getPrecision().ordinal() - rhs.getPrecision().ordinal();

	} else {

	    return rval;

	}

    }

    /**
     An implementation of equals which is, by definition, consistent with {@link #compareTo} when equals returns true.
     @param rhs the other instance.
     @return true if both instance's specified date and precision are the same; false otherwise.
     */

    public boolean equals( Object rhs ) {

	return rhs instanceof ObtuseApproximateCalendarDate && compareTo( (ObtuseApproximateCalendarDate)rhs ) == 0;

    }

    public String toString() {

	return "BurkeApproximateCalendarDate( " + format() + " )";

    }

    public static void main( String[] args ) {

	BasicProgramConfigInfo.init( "Obtuse", "Util", "Testing", null );

	Random rng = new Random( 12321421 );
	for ( DatePrecision precision : DatePrecision.values() ) {

	    for ( int year = 1980; year < 2020; year += 1 ) {

		try {

		    ObtuseCalendarDate calendarDate = new ObtuseCalendarDate(
			    "" +
			    year +
			    "-" + ObtuseUtil.lpad( 1 + rng.nextInt( 12 ), 2, '0' ) +
			    "-" + ObtuseUtil.lpad( 1 + rng.nextInt( 28 ), 2, '0' ) +
			    ""
		    );
		    ObtuseApproximateCalendarDate date = new ObtuseApproximateCalendarDate(
			    calendarDate,
			    precision
		    );
		    Logger.logMsg(
			    "" +
			    calendarDate +
			    " rounds with " +
			    precision.name().toLowerCase() +
			    " precision formats as " +
			    date.format()
		    );

		} catch ( ParsingException e ) {

		    e.printStackTrace();

		}

	    }

	    Logger.logMsg( "" );

	}

    }

}