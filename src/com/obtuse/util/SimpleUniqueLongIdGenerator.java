package com.obtuse.util;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A utility class which implements a very simple {@link UniqueLongIdGenerator}.
 <p/>
 Each instance of this class returns values from {@link #getUniqueId}
 which are the next value in the sequence 1L, 2L, 3L ...
 <p/>
 Instances of this class are thread safe.
 */

public class SimpleUniqueLongIdGenerator implements UniqueLongIdGenerator {

    public static final long DEFAULT_INITIAL_LAST_ID = 0L;

    private long _lastId = 0;

    private final String _name;

    private final boolean _allowDuplicates;

    /**
     Create a simple id generator that only generates long positive values.

     @param name            the name of this generator (used in toString and possibly useful in your debug code).
     @param allowDuplicates true if this generator is allowed to generate duplicate values
     (it will restart at 1L if it runs out of unique values); <code>false</code>
     if duplicates are strictly forbidden (the {@link #getUniqueId()} method will throw an <code>IllegalArgumentException</code>
     if it runs out of unique positive long values).
     */

    public SimpleUniqueLongIdGenerator( final String name, final boolean allowDuplicates ) {

        super();

        _name = name;

        _allowDuplicates = allowDuplicates;

    }

    /**
     Create a simple id generator that generates unique long positive values.
     <p/>The values start at 1L and increase until all positive long values have been generated.
     Generators created via this constructor will never return the same value twice.
     The {@link #getUniqueId()} method will throw an {@link }IllegalArgumentException if it runs out of
     unique positive long values (something that will take about 292 years if your computer can request
     new ids at a rate of 1,000,000,000 requests per second; I suggest that you pack a lunch).

     @param name the name of this generator (used in toString and possibly useful in your debug code).
     */

    public SimpleUniqueLongIdGenerator( final String name ) {

        this( name, false );

    }

    @Override
    public void setLastId( final long lastId ) {

        if ( _lastId != DEFAULT_INITIAL_LAST_ID ) {

            throw new IllegalArgumentException( "SimpleUniqueLongIdGenerator.setLastId:  cannot change last id after first actual id has been returned" );

        }

        _lastId = lastId;

    }

    @Override
    public boolean allowDuplicates() {

        return _allowDuplicates;

    }

    @Override
    public String getName() {

        return _name;

    }

    /**
     Get the next value in the sequence 1L, 2L, 3L ... from the perspective of this instance.
     <p/>
     For example, the following code sequence:
     <pre>
     SimpleUniqueIdGenerator outerGenerator = new SimpleUniqueIdGenerator();
     for ( int i = 0; i < 10; i += 1 ) {

     System.out.print( outerGenerator.getUniqueId() + ": " );

     SimpleUniqueIdGenerator innerGenerator = new SimpleUniqueIdGenerator();
     for ( int j = 0; j < 5; j += 1 ) {

     System.out.print( " " + innerGenerator.getUniqueId() );

     }

     System.out.println();

     }
     </pre>
     produces the following output:
     <pre>

     1:  1 2 3 4 5
     2:  1 2 3 4 5
     3:  1 2 3 4 5
     4:  1 2 3 4 5
     5:  1 2 3 4 5
     6:  1 2 3 4 5
     7:  1 2 3 4 5
     8:  1 2 3 4 5
     9:  1 2 3 4 5
     10:  1 2 3 4 5
     </pre>

     @return the next value in the sequence 1L, 2L, 3L ... from the perspective of this instance.
     This method will throw an {@link IllegalArgumentException} if it runs out of unique long values
     and {@link #allowDuplicates()} returns {@code false}.
     Alternatively, it will start again at 1L if it runs out of unique long values and
     {@link #allowDuplicates()} return {@code true}.
     <p>In either event, this instance won't run out of unique long values for about 292 years
     assuming that your computer is requesting new ids at a rate of 1,000,000,000 requests per second
     (I suggest that you pack a lunch).
     For what little it is worth, a single thread on my 2.5GHz Intel i7 (purchased in 2014)
     can do about 550,000,000 calls to this method per second.</p>
     @throws IllegalArgumentException if all positive long ids have been generated and
     {@link #allowDuplicates()} returns <code>false</code> for this instance.
     */

    public synchronized long getUniqueId() {

        if ( _lastId == Long.MAX_VALUE ) {

            if ( allowDuplicates() ) {

                _lastId = 0;

            } else {

                throw new IllegalArgumentException( getName() + ":  all positive long ids have been generated" );

            }

        }

        _lastId += 1;

        return _lastId;

    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void main( final String[] args ) {

        SimpleUniqueLongIdGenerator outerGenerator = new SimpleUniqueLongIdGenerator( "SULIG test outer" );
        for ( int ii = 0; ii < 10; ii += 1 ) {

            System.out.print( outerGenerator.getUniqueId() + ": " );

            SimpleUniqueLongIdGenerator innerGenerator = new SimpleUniqueLongIdGenerator( "SULIG test inner" );
            for ( int i = 0; i < 5; i += 1 ) {

                System.out.print( " " + innerGenerator.getUniqueId() );

            }

            System.out.println();

        }

        Measure m = new Measure( "measure getUniqueId call rate" );
        long maxCount = 1000L * 1000L * 1000L;
        for ( long xx = 0; xx < maxCount; xx += 1 ) {

            outerGenerator.getUniqueId();

        }

        long delta = m.deltaMillis();
        double callRate = maxCount / ( delta / 1000.0 );
        System.out.println(
                "" + maxCount + " calls took " + DateUtils.formatDuration( delta ) +
                " for a rate of " + ObtuseUtil.lpadReadable( (long)callRate, 0 ) +
                " calls per second"
        );

        double durationYears = getDurationYears( callRate );

        System.out.println(
                "It will take this computer about " + ObtuseUtil.lpadReadable( durationYears, 0, 1 ) +
                " years to generate all possible long ids."
        );

        System.out.println(
                "If your computer can manage 1,000,000,000 calls per second then it will take about " +
                ObtuseUtil.lpadReadable( getDurationYears( 1000L * 1000L * 1000L ), 0, 1 ) +
                " years to generate all possible long ids."
        );

//        strange();

        ObtuseUtil.doNothing();

    }

    public static double getDurationYears( final double callRate ) {

        double durationSeconds = Long.MAX_VALUE / callRate;
        return durationSeconds / ( 365.25 * 86400 );
    }

//    public static void strange() {
//
//        long maxCount = Integer.MAX_VALUE + 1L;
//
//        for ( long xx = 0; xx < maxCount; xx += 1 ) {
//
//            doSomethingQuick();
//
//        }
//
//    }
//
//    public static void doSomethingQuick() {}

    public String toString() {

        return "SULIG( name = \"" + getName() + ", last id = " + _lastId + " )";

    }

}
