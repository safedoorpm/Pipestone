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

    public SimpleUniqueLongIdGenerator( String name, boolean allowDuplicates ) {

        super();

        _name = name;

        _allowDuplicates = allowDuplicates;

    }

    /**
     Create a simple id generator that generates unique positive values.
     <p/>The values start at 1L and increase until all positive long values have been generated.
     Generators created via this constructor will never return the same value twice. The {@link #getUniqueId()} method will throw
     an <code>IllegalArgumentException</code> if it runs out of unique positive long values).

     @param name the name of this generator (used in toString and possibly useful in your debug code).
     */

    public SimpleUniqueLongIdGenerator( String name ) {

        this( name, false );

    }

    /**
     Determine if this instance is allowed to generate duplicate ids.
     This will only occur if this instance runs out of unique long ids.

     @return <code>true</code> if duplicates are allowed; <code>false</code> otherwise.
     */

    public boolean allowDuplicates() {

        return _allowDuplicates;

    }

    /**
     Get the name of this instance.

     @return the name of this instance as provided to the constructor when it was created.
     */

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
     This method will never return the same value twice if {@link #allowDuplicates()} return <code>false</code> for this instance.
     This method will restart the sequence from 1L should it run out of unique positive long values if {@link #allowDuplicates()} returns <code>true</code> for this instance.
     @throws IllegalArgumentException if all positive long ids have been generated and {@link #allowDuplicates()} returns <code>false</code> for this instance.
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
    public static void main( String[] args ) {

        SimpleUniqueLongIdGenerator outerGenerator = new SimpleUniqueLongIdGenerator( "SULIG test outer" );
        for ( int ii = 0; ii < 10; ii += 1 ) {

            System.out.print( outerGenerator.getUniqueId() + ": " );

            SimpleUniqueLongIdGenerator innerGenerator = new SimpleUniqueLongIdGenerator( "SULIG test inner" );
            for ( int i = 0; i < 5; i += 1 ) {

                System.out.print( " " + innerGenerator.getUniqueId() );

            }

            System.out.println();

        }

    }

    public String toString() {

        return "SULIG( name = \"" + getName() + ", last id = " + _lastId + " )";

    }

}
