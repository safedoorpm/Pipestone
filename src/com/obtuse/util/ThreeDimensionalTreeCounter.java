/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

public class ThreeDimensionalTreeCounter<K1,K2,K3> implements ThreeDimensionalCounter<K1, K2, K3> {

    private final ThreeDimensionalSortedMap<K1,K2,K3,Integer> _counter = new ThreeDimensionalTreeMap<>();

    public ThreeDimensionalTreeCounter() {
        super();

    }

    public void count( final K1 key1, final K2 key2, final K3 key3 ) {

        Integer count = _counter.get( key1, key2, key3 );
        if ( count == null ) {

            _counter.put( key1, key2, key3, 1 );

        } else {

            _counter.put( key1, key2, key3, 1 + count.intValue() );

        }

    }

    public int getCount( final K1 key1, final K2 key2, final K3 key3 ) {

        Integer count = _counter.get( key1, key2, key3 );
        if ( count == null ) {

            return 0;

        } else {

            return count.intValue();

        }

    }

    public boolean containsKeys( final K1 key1, final K2 key2, final K3 key3 ) {

        return _counter.get( key1, key2, key3 ) != null;

    }

    public ThreeDimensionalSortedMap<K1,K2,K3,Integer> getThreeDimensionalSortedMap() {

        return _counter;

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Shared", "3Dcounter", null );

        ThreeDimensionalTreeCounter<Integer,String,Boolean> counter = new ThreeDimensionalTreeCounter<>();
        counter.count( 1, "x", true );
        counter.count( 1, "x", true );
        counter.count( 1, "x", false );
        counter.count( 1, "y", true );
        counter.count( 2, "x", true );

        for ( int ix : counter.getThreeDimensionalSortedMap().outerKeys() ) {

            for ( String str : counter.getThreeDimensionalSortedMap().getInnerMap( ix, false ).outerKeys() ) {

                for ( boolean bool : counter.getThreeDimensionalSortedMap().getInnerMap( ix, false ).getInnerMap( str, false ).keySet() ) {

                    Logger.logMsg( ObtuseUtil.lpad( ix, 6 ) + " : " + ObtuseUtil.rpad( str, 5 ) + " : " + ( bool ? "true " : "false" ) + " : " + counter.getCount( ix, str, bool ) );

                }

            }

        }

    }
}
