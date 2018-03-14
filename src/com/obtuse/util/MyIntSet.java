/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 A compact and fast set of longs.
 Note that instances of this class are backed by a dynamically sized bitmap array of <code>long</code>.
 This implementation assumes that the integers are relatively tightly clustered together.
 Sets with huge gaps in the member values will result in considerably more memory being consumed than one might like.
 */

public class MyIntSet implements Iterable<Long>, Serializable {

    private long[] _valueBits = null;
    private long _startValue = 0L;

    public MyIntSet() {

        super();

    }

    /**
     Add an int to this set.
     <p/>A call to this method could cause the underlying array of <code>long</code> values to grow such that it is
     long enough to describe all int values from the smallest long value ever added to this set through to the largest long value ever
     added to this set.
     <p/>Note that while the array may grow as a result of a call to any of this class's <code>add*()</code> methods, the array never
     shrinks.

     @param value the long to be added.
     */

    public void add( final long value ) {

        if ( _valueBits == null ) {

            // Take a wild guess that the first value is in the middle of a modest range of bits.

            //noinspection MagicNumber
            _valueBits = new long[25];
            //noinspection MagicNumber
            _startValue = value - 12 * Long.SIZE;

        }

        if ( value < _startValue ) {

            long newStartValue = _startValue - ( ( _startValue - value ) / Long.SIZE + 10 ) * Long.SIZE;
            long lastValue = _startValue + Long.SIZE * _valueBits.length - 1;
            long[] newArray = new long[(int)( ( lastValue + 1 - newStartValue ) / Long.SIZE )];
            System.arraycopy(
                    _valueBits,
                    0,
                    newArray,
                    (int)( _startValue - newStartValue ) / Long.SIZE,
                    _valueBits.length
            );

            _valueBits = newArray;
            _startValue = newStartValue;

        } else if ( value >= _startValue + _valueBits.length * Long.SIZE ) {

            long lastValue = _startValue + Long.SIZE * _valueBits.length - 1;
            long newLastValue = lastValue + ( ( value - lastValue ) / Long.SIZE + 10 ) * Long.SIZE;
            long[] newArray = new long[(int)( ( newLastValue + 1 - _startValue ) / Long.SIZE )];
            System.arraycopy( _valueBits, 0, newArray, 0, _valueBits.length );

            _valueBits = newArray;

        }

        //noinspection UnnecessaryParentheses
        _valueBits[( (int)( value - _startValue ) / Long.SIZE )] |= 1L << ( ( value - _startValue ) & ( Long.SIZE - 1 ) );

    }

    public boolean contains( final long value ) {

        if ( _valueBits == null ) {

            return false;

        }

        if ( value < _startValue ) {

            return false;

        } else if ( value >= _startValue + _valueBits.length * Long.SIZE ) {

            return false;

        }

        return ( _valueBits[(int)( ( value - _startValue ) / Long.SIZE )] & ( 1L << ( ( value - _startValue ) & ( Long.SIZE - 1 ) ) ) ) != 0;

    }

    public boolean remove( final long value ) {

        if ( _valueBits == null ) {

            return false;

        }

        if ( value < _startValue ) {

            return false;

        } else if ( value >= _startValue + _valueBits.length * Long.SIZE ) {

            return false;

        }

        int ix = (int)( ( value - _startValue ) / Long.SIZE );
        long element = _valueBits[ix];
        long mask = ( 1L << ( ( value - _startValue ) & ( Long.SIZE - 1 ) ) );
        boolean rval = ( element & mask ) != 0;
        _valueBits[ix] &= ~mask;

        return rval;

    }

    @NotNull
    public Iterator<Long> iterator() {

        for ( int i = 0; i < _valueBits.length; i += 1 ) {

            for ( int j = 0; j < Long.SIZE; j += 1 ) {

                //noinspection UnnecessaryParentheses
                if ( ( _valueBits[i] & ( 1L << j ) ) != 0 ) {

                    final int nextI = i;
                    final int nextJ = j;
                    return new Iterator<Long>() {

                        private int _nextI = nextI;
                        private int _nextJ = nextJ;
                        private boolean _done = false;

                        public boolean hasNext() {

                            return !_done;

                        }

                        public Long next() {

                            if ( _done ) {

                                throw new NoSuchElementException( "no more elements" );

                            } else {

                                long rval = _startValue + _nextI * Long.SIZE + _nextJ;
                                if ( _nextJ == Long.SIZE ) {
                                    _nextI += 1;
                                    _nextJ = 0;
                                } else {
                                    _nextJ += 1;
                                }

                                for ( int i = _nextI; i < _valueBits.length; i += 1 ) {

                                    for ( int j = _nextJ; j < Long.SIZE; j += 1 ) {

                                        //noinspection UnnecessaryParentheses
                                        if ( ( _valueBits[i] & ( 1L << j ) ) != 0 ) {

                                            _nextI = i;
                                            _nextJ = j;
                                            return rval;

                                        }

                                    }

                                    _nextJ = 0;

                                }

                                _done = true;
                                return rval;

                            }
                        }

                        public void remove() {

                            throw new UnsupportedOperationException( "remove not supported" );

                        }

                    };

                }

            }
        }

        return new Iterator<Long>() {

            public boolean hasNext() {

                return false;

            }

            public Long next() {

                throw new NoSuchElementException( "set is empty" );

            }

            public void remove() {

                throw new UnsupportedOperationException( "remove not supported" );

            }


        };

    }

    public String toString() {

        StringBuilder rval2 = new StringBuilder();
        String comma = "";
        //noinspection ForLoopReplaceableByForEach,ForLoopWithMissingComponent
        for ( Iterator<Long> iter = iterator(); iter.hasNext(); ) {

            rval2.append( comma )
                 .append( iter.next() );
            comma = ", ";

        }

        return "MyIntSet( " + rval2.toString() + " )";

    }

    @SuppressWarnings("MagicNumber")
    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "MyIntSet", null );
        LoggingMessageProxy mp = new LoggingMessageProxy();

        MyIntSet set = new MyIntSet();

        for ( int i = 0; i < 1000; i += 29 ) {

            if ( set.contains( i ) ) {

                mp.error( "#1 i=" + i + " found in set when it should not be present" );

            }

            set.add( i );
            if ( !set.contains( i ) ) {

                mp.error( "#2 i=" + i + " not successfully added to the set" );

            }

            if ( set.contains( i + 1 ) ) {

                mp.error( "#3 i=" + ( i + 1 ) + " found in set when it should not be present" );

            }

        }

        for ( int i = -23; i > -1000; i -= 23 ) {

            if ( set.contains( i ) ) {

                mp.error( "#4 i=" + i + " found in set when it should not be present" );

            }

            set.add( i );
            if ( !set.contains( i ) ) {

                mp.error( "#5 i=" + i + " not successfully added to the set" );

            }

            if ( set.contains( i + 1 ) ) {

                mp.error( "#6 i=" + ( i + 1 ) + " found in set when it should not be present" );

            }

        }

        if ( mp.hasLoggedErrors() || mp.hasLoggedInfos() ) {

            Logger.logErr( "### " + mp.getErrorCount() + " error and " + mp.getInfoCount() + " messages" );
            System.exit( 1 );

        } else {

            Logger.logMsg( "MyIntSet test passed" );

        }

        System.exit( 0 );

    }

}
