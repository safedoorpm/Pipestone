/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util.junit;

import com.obtuse.util.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Unit test the {@link com.obtuse.util.ThreeDimensionalTreeMap} class.
 */

@SuppressWarnings("ClassHasNoToStringMethod")
public class ThreeDimensionalTreeMapTest {

    private ThreeDimensionalTreeMap<Integer, Integer, Integer, String> _map;

    @Before
    public void setUp() {

        _map = new ThreeDimensionalTreeMap<>();

        if ( !BasicProgramConfigInfo.isInitialized() ) {

            BasicProgramConfigInfo.init( "Obtuse", "Utils", "Test", null );

        }

    }

    @After
    public void tearDown() {

        _map = null;

    }

    @Test
    public void testConstructor() {

        // Newly constructed classes must start out empty and return null when asked for something

        Assert.assertEquals( 0, _map.size() );

        Assert.assertTrue( _map.isEmpty() );

        Assert.assertNull( _map.get( 1, 2, 3 ) );

    }

    @Test
    public void testGetPut() {

        // Put something into the map

        _map.put( 1, 2, 3, "Hello" );

        // Verify that it is in the map

        Assert.assertEquals( "Hello", _map.get( 1, 2, 3 ) );
        Assert.assertEquals( 1, _map.size() );

        // Put something else into the map

        _map.put( 3, 2, 1, "World" );

        // Verify that the two things are now in the correct places in the map

        Assert.assertEquals( "Hello", _map.get( 1, 2, 3 ) );
        Assert.assertEquals( "World", _map.get( 3, 2, 1 ) );
        Assert.assertEquals( 2, _map.size() );

        // Remove one of the things from the map

        String hello = _map.remove( 1, 2, 3 );

        // Verify that the correct thing got removed and that the correct one thing is still in the map

        Assert.assertEquals( "Hello", hello );
        Assert.assertEquals( "World", _map.get( 3, 2, 1 ) );
        Assert.assertNull( _map.get( 1, 2, 3 ) );
        Assert.assertEquals( 1, _map.size() );

        // Remove the other thing from the map

        String world = _map.remove( 3, 2, 1 );

        // Verify that the correct thing got removed and that the map is now empty

        Assert.assertEquals( "World", world );
        Assert.assertNull( _map.get( 1, 2, 3 ) );
        Assert.assertNull( _map.get( 3, 2, 1 ) );
        Assert.assertEquals( 0, _map.size() );

    }

    @Test
    public void testGetInnerMap() {

        // Verify that the inner map is not created by a call to getInnerMap with the second parameter false

        Assert.assertNull( _map.getInnerMap( 1, false ) );

        // Verify that an empty inner map is created by this call.

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap1 = _map.getInnerMap( 1, true );
        Assert.assertNotNull( innerMap1 );
        Assert.assertEquals( 0, _map.size() );
        Assert.assertEquals( 0, innerMap1.size() );

        // Verify that exactly one inner map now exists.

        Assert.assertEquals( 1, _map.outerKeys().size() );

        // Verify that the inner map returned above is the one returned by a subsequent call to getInnerMap
        // regardless of the value of the second parameter to the call.

        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );
        Assert.assertSame( innerMap1, _map.getInnerMap( 1, true ) );

        // Verify that removing the inner map returns the correct inner map and that the
        // 3D sorted map no longer contains the removed map.

        TwoDimensionalSortedMap<Integer, Integer, String> removedInnerMap = _map.removeInnerMap( 1 );
        Assert.assertNotNull( removedInnerMap );
        Assert.assertSame( innerMap1, removedInnerMap );
        Assert.assertNull( _map.getInnerMap( 1, false ) );

        // Verify that no inner maps now exist.

        Assert.assertEquals( 0, _map.outerKeys().size() );

        // Put something in the map.

        _map.put( 1, 2, 3, "Hello" );

        // Verify that exactly one inner map now exists.

        Assert.assertEquals( 1, _map.outerKeys().size() );

        // Verify that it is the only thing in the map and that it is in the correct inner map.

        Assert.assertEquals( 1, _map.size() );
        Assert.assertEquals( "Hello", _map.get( 1, 2, 3 ) );
        innerMap1 = _map.getInnerMap( 1, false );
        Assert.assertNotNull( innerMap1 );
        Assert.assertEquals( 1, innerMap1.size() );
        Assert.assertEquals( "Hello", innerMap1.get( 2, 3 ) );

        // Verify that exactly one inner map now exists.

        Assert.assertEquals( 1, _map.outerKeys().size() );
        Assert.assertEquals( 1, _map.innerMaps().size() );

        // Put something else into the same inner map.

        _map.put( 1, 99, 3, "World" );

        // Verify that exactly one inner map now exists.

        Assert.assertEquals( 1, _map.outerKeys().size() );

        // Verify that both things are in the map.

        Assert.assertEquals( 2, _map.size() );
        Assert.assertEquals( "Hello", _map.get( 1, 2, 3 ) );
        Assert.assertEquals( "World", _map.get( 1, 99, 3 ) );

        // Verify that both things are in the same inner map.

        innerMap1 = _map.getInnerMap( 1, false );
        Assert.assertNotNull( innerMap1 );
        Assert.assertEquals( 2, innerMap1.size() );
        Assert.assertEquals( "Hello", innerMap1.get( 2, 3 ) );
        Assert.assertEquals( "World", innerMap1.get( 99, 3 ) );

        // Add a second inner map.

        _map.put( 99, 2, 3, "There" );

        // Verify that exactly two inner maps now exists.

        Assert.assertEquals( 2, _map.outerKeys().size() );

        // Verify that the first inner map still exists in the map.

        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );

        // Verify that the first inner map has not changed.

        Assert.assertNotNull( innerMap1 );
        Assert.assertEquals( 2, innerMap1.size() );
        Assert.assertEquals( "Hello", innerMap1.get( 2, 3 ) );
        Assert.assertEquals( "World", innerMap1.get( 99, 3 ) );

        // Verify that "There" is in the second inner map.

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap2 = _map.getInnerMap( 99, false );

        Assert.assertNotNull( innerMap2 );

        Assert.assertEquals( 1, innerMap2.size() );
        Assert.assertEquals( "There", innerMap2.get( 2, 3 ) );
        Assert.assertEquals( "Hello", innerMap1.get( 2, 3 ) );
        Assert.assertEquals( 3, _map.size() );

        // Verify that deleting the things in the map does not delete the inner maps

        Assert.assertEquals( "Hello", _map.remove( 1, 2, 3 ) );
        Assert.assertEquals( 2, _map.size() );
        Assert.assertEquals( "World", _map.remove( 1, 99, 3 ) );
        Assert.assertEquals( 1, _map.size() );
        Assert.assertEquals( "There", _map.remove( 99, 2, 3 ) );
        Assert.assertEquals( 0, _map.size() );
        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );
        Assert.assertSame( innerMap2, _map.getInnerMap( 99, false ) );

    }

    @Test
    public void testRemoveInner() {

        // Add an empty inner map

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap1 = _map.getInnerMap( 1, true );

        // Verify that the map contains exactly one inner map at outer key 1 but contains no values.

        Assert.assertNotNull( innerMap1 );
        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );
        Assert.assertEquals( 0, innerMap1.size() );
        Assert.assertEquals( 1, _map.outerKeys().size() );
        Assert.assertEquals( 0, _map.size() );

        // Add a second empty inner map

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap2 = _map.getInnerMap( 99, true );

        // Verify that the map contains exactly two inner maps at the correct outer keys but contains no values

        Assert.assertNotNull( innerMap1 );
        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );
        Assert.assertEquals( 0, innerMap1.size() );
        Assert.assertNotNull( innerMap2 );
        Assert.assertSame( innerMap2, _map.getInnerMap( 99, false ) );
        Assert.assertEquals( 0, innerMap2.size() );
        Assert.assertEquals( 2, _map.outerKeys().size() );
        Assert.assertEquals( 0, _map.size() );

        // Put something into each inner map

        _map.put( 1, 2, 3, "Hello" );
        _map.put( 99, 2, 3, "World" );

        // Verify that the map still contains exactly two inner maps at the correct outer keys and contains the things in the right places

        Assert.assertEquals( "Hello", _map.get( 1, 2, 3 ) );
        Assert.assertEquals( "World", _map.get( 99, 2, 3 ) );
        Assert.assertNotNull( innerMap1 );
        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );
        Assert.assertEquals( 1, innerMap1.size() );
        Assert.assertEquals( "Hello", innerMap1.get( 2, 3 ) );
        Assert.assertNotNull( innerMap2 );
        Assert.assertSame( innerMap2, _map.getInnerMap( 99, false ) );
        Assert.assertEquals( 1, innerMap2.size() );
        Assert.assertEquals( "World", innerMap2.get( 2, 3 ) );
        Assert.assertEquals( 2, _map.outerKeys().size() );
        Assert.assertEquals( 2, _map.size() );

        // Remove the first inner map

        TwoDimensionalSortedMap<Integer, Integer, String> removedInnerMap1 = _map.removeInnerMap( 1 );

        Assert.assertNotNull( removedInnerMap1 );

        // Verify that the removed map is the right map and that it and its contents are gone

        Assert.assertSame( removedInnerMap1, innerMap1 );
        Assert.assertNull( _map.getInnerMap( 1, false ) );
        Assert.assertNull( "Hello", _map.get( 1, 2, 3 ) );
        Assert.assertEquals( "World", _map.get( 99, 2, 3 ) );
        Assert.assertEquals( "Hello", removedInnerMap1.get( 2, 3 ) );
        Assert.assertEquals( 1, _map.outerKeys().size() );
        Assert.assertEquals( 1, _map.size() );

        // Remove the second inner map

        TwoDimensionalSortedMap<Integer, Integer, String> removedInnerMap2 = _map.removeInnerMap( 99 );

        Assert.assertNotNull( removedInnerMap2 );

        // Verify that the removed map is the right map and that it and its contents are gone

        Assert.assertSame( removedInnerMap2, innerMap2 );
        Assert.assertNull( _map.getInnerMap( 99, false ) );
        Assert.assertNull( "World", _map.get( 99, 2, 3 ) );
        Assert.assertEquals( "World", removedInnerMap2.get( 2, 3 ) );
        Assert.assertEquals( 0, _map.outerKeys().size() );
        Assert.assertEquals( 0, _map.size() );

    }

    @Test
    public void testRemove() {

        // Verify that removing something that isn't there yields null and that the map is still empty

        Assert.assertNull( _map.remove( 1, 2, 3 ) );
        Assert.assertEquals( 0, _map.size() );

        // Put something in the map

        _map.put( 1, 2, 3, "World" );

        // Verify that this results in the map containing a single inner map (remember the inner map for later)

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap1 = _map.getInnerMap( 1, false );
        Assert.assertEquals( 1, _map.outerKeys().size() );

        // Verify that it is there and that it goes away when removed.

        Assert.assertEquals( "World", _map.get( 1, 2, 3 ) );
        Assert.assertEquals( "World", _map.remove( 1, 2, 3 ) );
        Assert.assertNull( _map.get( 1, 2, 3 ) );

        // Verify that the map still contains one inner map and that it is the right one.

        Assert.assertEquals( 1, _map.outerKeys().size() );
        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );

        Assert.assertNotNull( innerMap1 );

        // Put two things in the map (one into the same inner map created above but in a different place, one in a different inner map)

        _map.put( 1, 99, 3, "Hello" );
        _map.put( 99, 4, 5, "There" );

        // Use a loop to verify that the removal at the bottom of the loop doesn't change the map.

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap2 = null;
        for ( int i = 0; i < 2; i += 1 ) {

            boolean worked = false;

            try {

                // Verify that the map now contains two inner maps and that the things are in the right maps/places

                Assert.assertEquals( 2, _map.outerKeys().size() );
                Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );
                innerMap2 = _map.getInnerMap( 99, false );
                Assert.assertNotNull( innerMap2 );
                Assert.assertEquals( "Hello", _map.get( 1, 99, 3 ) );
                Assert.assertEquals( "There", _map.get( 99, 4, 5 ) );
                Assert.assertEquals( "Hello", innerMap1.get( 99, 3 ) );
                Assert.assertNull( innerMap1.get( 4, 5 ) );
                Assert.assertEquals( "There", innerMap2.get( 4, 5 ) );
                Assert.assertNull( innerMap2.get( 99, 3 ) );

                // Remove something that isn't there

                Assert.assertNull( _map.remove( 1, 1, 1 ) );

                // The next iteration will verify that removing something that isn't there doesn't change the map

                // Set worked to true here so that the finally clause below can distinguish between successes
                // and failures (see finally clause for more details).

                worked = true;

            } finally {

                // Worked will be true unless we landed here because an exception got thrown.
                // In that case, report which iteration we are on.

                if ( !worked ) {

                    Logger.logMsg( "test is failing when i = " + i );
                    System.out.flush();

                }

            }

        }

        // Remove the first of the two things

        String hello = _map.remove( 1, 99, 3 );

        // Verify that the right thing got removed, that it is gone, that the inner maps are still there
        // and that the other thing is still where it is supposed to be

        Assert.assertEquals( 1, _map.size() );
        Assert.assertEquals( "Hello", hello );
        Assert.assertNull( _map.get( 1, 99, 3 ) );
        Assert.assertNotNull( innerMap1 );
        Assert.assertNotNull( innerMap2 );
        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );
        Assert.assertSame( innerMap2, _map.getInnerMap( 99, false ) );
        Assert.assertNull( innerMap1.get( 99, 3 ) );
        Assert.assertEquals( 0, innerMap1.size() );
        Assert.assertEquals( 1, innerMap2.size() );
        Assert.assertEquals( "There", _map.get( 99, 4, 5 ) );
        TwoDimensionalSortedMap<Integer, Integer, String> innerMap99a = _map.getInnerMap( 99, false );
        Assert.assertNotNull( innerMap99a );
        Assert.assertEquals( "There", innerMap99a.get( 4, 5 ) );

        // Remove the second of the two things

        String there = _map.remove( 99, 4, 5 );

        // Make sure that both things are gone

        Assert.assertEquals( 0, _map.size() );
        Assert.assertEquals( "There", there );
        Assert.assertNull( _map.get( 1, 99, 3 ) );
        Assert.assertNotNull( innerMap1 );
        Assert.assertNotNull( innerMap2 );
        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );
        Assert.assertSame( innerMap2, _map.getInnerMap( 99, false ) );
        Assert.assertNull( innerMap1.get( 99, 3 ) );
        Assert.assertNull( innerMap2.get( 4, 5 ) );
        Assert.assertEquals( 0, innerMap1.size() );
        Assert.assertEquals( 0, innerMap2.size() );

    }

    @Test
    public void testSize() {

        // Verify that the map is empty (this is done above but let's be complete here)

        Assert.assertEquals( 0, _map.size() );

        // Put something into the map

        _map.put( 1, 2, 3, "Hello" );

        // Verify that the map now contains one thing.

        Assert.assertEquals( 1, _map.size() );

        // Change the thing in the map

        _map.put( 1, 2, 3, "World" );

        // Verify that the map still contains one thing.

        Assert.assertEquals( 1, _map.size() );

        // Put something else in the map

        _map.put( 3, 2, 1, "There" );

        // Verify that the map now contains two things

        Assert.assertEquals( 2, _map.size() );

        // Remove the first thing

        _map.remove( 1, 2, 3 );

        // Verify that the map now contains exactly one thing.

        Assert.assertEquals( 1, _map.size() );

        // Remove something that is not there.

        _map.remove( 2, 2, 2 );

        // Verify that the map still contains exactly one thing.

        Assert.assertEquals( 1, _map.size() );

        // Remove the second thing.

        _map.remove( 3, 2, 1 );

        // Verify that the map is now empty.

        Assert.assertEquals( 0, _map.size() );

    }

    @Test
    public void testIsEmpty() {

        // Verify that the map is empty (this is done above but let's be complete here)

        Assert.assertTrue( _map.isEmpty() );

        // Put something into the map

        _map.put( 1, 2, 3, "Hello" );

        // Verify that the map now contains something.

        Assert.assertFalse( _map.isEmpty() );

        // Change the thing in the map

        _map.put( 1, 2, 3, "World" );

        // Verify that the map still contains something.

        Assert.assertFalse( _map.isEmpty() );

        // Put something else in the map

        _map.put( 3, 2, 1, "There" );

        // Verify that the map still contains something

        Assert.assertFalse( _map.isEmpty() );

        // Remove the first thing

        _map.remove( 1, 2, 3 );

        // Verify that the map still contains something.

        Assert.assertFalse( _map.isEmpty() );

        // Remove something that is not there.

        _map.remove( 2, 2, 2 );

        // Verify that the map still contains something.

        Assert.assertFalse( _map.isEmpty() );

        // Remove the second thing.

        _map.remove( 3, 2, 1 );

        // Verify that the map is now empty.

        Assert.assertTrue( _map.isEmpty() );

    }

    @Test
    public void testOuterKeys() {

        // Verify that there are no outer keys

        Assert.assertTrue( _map.outerKeys().isEmpty() );

        // Get something from an empty map

        Assert.assertNull( _map.get( 1, 2, 3 ) );

        // Verify that there are still no outer keys

        Assert.assertTrue( _map.outerKeys().isEmpty() );

        // Put something into the map

        _map.put( 1, 2, 3, "Hello" );

        // Verify that there is exactly one outer key and that it is the correct one

        Assert.assertEquals( 1, _map.outerKeys().size() );
        Assert.assertTrue( _map.outerKeys().contains( 1 ) );

        // Get the just created inner map

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap1 = _map.getInnerMap( 1, false );

        // Verify that we got an inner map

        Assert.assertNotNull( innerMap1 );

        // Verify that there is still exactly one outer key and that it is the correct one

        Assert.assertEquals( 1, _map.outerKeys().size() );
        Assert.assertTrue( _map.outerKeys().contains( 1 ) );

        // Verify that we got an inner map when we asked for one just now

        Assert.assertNotNull( innerMap1 );

        // Put something into the map in a different place but in the same inner map

        _map.put( 1, 3, 2, "World" );

        // Verify that there is still exactly one outer key and that it is in the correct place

        Assert.assertEquals( 1, _map.outerKeys().size() );
        Assert.assertTrue( _map.outerKeys().contains( 1 ) );

        // Manually create an empty inner map that previously did not exist

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap2 = _map.getInnerMap( 2, true );

        // Verify that we got an inner map which is distinct from the one we got earlier

        Assert.assertNotNull( innerMap2 );
        Assert.assertNotSame( innerMap1, innerMap2 );

        // Verify that there are now two outer keys and that they are the right ones

        Assert.assertEquals( 2, _map.outerKeys().size() );
        Assert.assertTrue( _map.outerKeys().contains( 1 ) );
        Assert.assertTrue( _map.outerKeys().contains( 2 ) );

        // Put something into a place for which there is not already an outer key

        _map.put( 3, 1, 1, "There" );
        TwoDimensionalSortedMap<Integer, Integer, String> innerMap3 = _map.getInnerMap( 3, false );

        // Verify that we now have the correct three outer keys

        Assert.assertEquals( 3, _map.outerKeys().size() );
        Assert.assertTrue( _map.outerKeys().contains( 1 ) );
        Assert.assertTrue( _map.outerKeys().contains( 2 ) );
        Assert.assertTrue( _map.outerKeys().contains( 3 ) );

        // Arguably out of scope but verify that there are three inner maps and that they are the correct maps

        Assert.assertEquals( 3, _map.innerMaps().size() );
        Assert.assertNotNull( _map.getInnerMap( 1, false ) );
        Assert.assertNotNull( _map.getInnerMap( 2, false ) );
        Assert.assertNotNull( _map.getInnerMap( 3, false ) );
        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );
        Assert.assertSame( innerMap2, _map.getInnerMap( 2, false ) );
        Assert.assertSame( innerMap3, _map.getInnerMap( 3, false ) );

        // Also arguably out of scope but verify that all three inner maps are indeed different maps

        Assert.assertNotSame( innerMap1, innerMap2 );
        Assert.assertNotSame( innerMap1, innerMap3 );
        Assert.assertNotSame( innerMap2, innerMap3 );
        Assert.assertNotSame( _map.getInnerMap( 1, false ), _map.getInnerMap( 2, false ) );
        Assert.assertNotSame( _map.getInnerMap( 1, false ), _map.getInnerMap( 3, false ) );
        Assert.assertNotSame( _map.getInnerMap( 2, false ), _map.getInnerMap( 3, false ) );

        // Remove one of the outer keys by removing its corresponding inner map

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap1a = _map.removeInnerMap( 1 );

        // Verify that the correct map was removed

        Assert.assertNotNull( innerMap1a );
        Assert.assertSame( innerMap1, innerMap1a );

        // Verify that the correct outer key was removed

        Assert.assertFalse( _map.outerKeys().contains( 1 ) );

        // Verify that there are still two outer keys and that they are the correct ones

        Assert.assertEquals( 2, _map.outerKeys().size() );
        Assert.assertTrue( _map.outerKeys().contains( 2 ) );
        Assert.assertTrue( _map.outerKeys().contains( 3 ) );

        // Remove one of the remaining two inner maps by removing the key

        boolean removed = _map.outerKeys().remove( 2 );

        // Verify that something was removed and that there is one key remaining

        Assert.assertEquals( 1, _map.outerKeys().size() );
        Assert.assertTrue( removed );

        // Verify that the correct key was removed

        Assert.assertFalse( _map.outerKeys().contains( 2 ) );
        Assert.assertTrue( _map.outerKeys().contains( 3 ) );

        // Verify that there is still one outer key and that it is the correct one

        Assert.assertEquals( 1, _map.outerKeys().size() );
        _map.outerKeys();

    }

    @Test
    public void testInnerMaps() {

        // Verify that there are no inner maps

        Assert.assertTrue( _map.innerMaps().isEmpty() );

        // Get something from an empty map

        Assert.assertNull( _map.get( 1, 2, 3 ) );

        // Verify that there are still no inner maps

        Assert.assertTrue( _map.innerMaps().isEmpty() );

        // Put something into the map

        _map.put( 1, 2, 3, "Hello" );

        // Verify that there is exactly one inner map

        Assert.assertEquals( 1, _map.innerMaps().size() );

        // Get the just created inner map

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap1 = _map.getInnerMap( 1, false );

        // Verify that we got an inner map when we asked for one just now

        Assert.assertNotNull( innerMap1 );

        // Verify that the very recent getInnerMap call did not create a second inner map

        Assert.assertEquals( 1, _map.innerMaps().size() );

        // Put something into the map in a different place but in the same inner map

        _map.put( 1, 3, 2, "World" );

        // Verify that there is exactly one inner map and that it is in the correct place

        Assert.assertEquals( 1, _map.innerMaps().size() );
        Assert.assertNotNull( _map.getInnerMap( 1, false ) );

        // Manually create an empty inner map that previously did not exist

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap2 = _map.getInnerMap( 2, true );

        // Verify that there are now two inner maps and that they are in the right places

        Assert.assertEquals( 2, _map.innerMaps().size() );
        Assert.assertNotNull( _map.getInnerMap( 1, false ) );
        Assert.assertNotNull( _map.getInnerMap( 2, false ) );
        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );
        Assert.assertSame( innerMap2, _map.getInnerMap( 2, false ) );

        // Put something into a place for which there is not already an inner map

        _map.put( 3, 1, 1, "There" );
        TwoDimensionalSortedMap<Integer, Integer, String> innerMap3 = _map.getInnerMap( 3, false );

        // Verify that there are now three inner maps and that they are in the right places

        Assert.assertEquals( 3, _map.innerMaps().size() );
        Assert.assertNotNull( _map.getInnerMap( 1, false ) );
        Assert.assertNotNull( _map.getInnerMap( 2, false ) );
        Assert.assertNotNull( _map.getInnerMap( 3, false ) );
        Assert.assertSame( innerMap1, _map.getInnerMap( 1, false ) );
        Assert.assertSame( innerMap2, _map.getInnerMap( 2, false ) );
        Assert.assertSame( innerMap3, _map.getInnerMap( 3, false ) );

        // Verify that all three inner maps are indeed different maps

        Assert.assertNotSame( innerMap1, innerMap2 );
        Assert.assertNotSame( innerMap1, innerMap3 );
        Assert.assertNotSame( innerMap2, innerMap3 );
        Assert.assertNotSame( _map.getInnerMap( 1, false ), _map.getInnerMap( 2, false ) );
        Assert.assertNotSame( _map.getInnerMap( 1, false ), _map.getInnerMap( 3, false ) );
        Assert.assertNotSame( _map.getInnerMap( 2, false ), _map.getInnerMap( 3, false ) );

        // Remove one of the inner maps

        TwoDimensionalSortedMap<Integer, Integer, String> innerMap1a = _map.removeInnerMap( 1 );

        // Verify that the correct map was removed

        Assert.assertNotNull( innerMap1a );
        Assert.assertSame( innerMap1, innerMap1a );

        // Verify that the removed inner map is gone and that there are still two other inner maps
        // and that they are the correct ones

        Assert.assertEquals( 2, _map.innerMaps().size() );
        Assert.assertNull( _map.getInnerMap( 1, false ) );
        Assert.assertNotNull( _map.getInnerMap( 2, false ) );
        Assert.assertNotNull( _map.getInnerMap( 3, false ) );

    }

    @Test
    public void testIterator() {

        // A place to hold things that we find via an iterator

        //noinspection TooBroadScope
        Vector<String> values;

        // Collect what we find in an empty map

        values = ThreeDimensionalTreeMapTest.getContents( _map );

        // Verify that we got nothing out of the empty map.

        Assert.assertTrue( values.isEmpty() );

        // Put one value into the map

        _map.put( 1, 2, 3, "Hello" );

        // Verify that map now contains one value and that it is the correct value

        values = ThreeDimensionalTreeMapTest.getContents( _map );
        Assert.assertEquals( 1, values.size() );
        Assert.assertEquals( "Hello", values.get( 0 ) );

        // Replace what was in the map with something different

        _map.put( 1, 2, 3, "World" );

        // Verify that the map now contains one value and that it is the correct value

        values = ThreeDimensionalTreeMapTest.getContents( _map );
        Assert.assertEquals( 1, values.size() );
        Assert.assertEquals( "World", values.get( 0 ) );

        // Put something different into the map at a different place in the same inner map

        _map.put( 1, 3, 2, "There" );

        // Verify that there is still exactly one inner map

        Assert.assertEquals( 1, _map.innerMaps().size() );

        // Verify that the map contains the correct two values

        values = ThreeDimensionalTreeMapTest.getContents( _map );

        Assert.assertEquals( 2, values.size() );
        Assert.assertTrue( values.contains( "World" ) );
        Assert.assertTrue( values.contains( "There" ) );

        // Put something different into the map at a different place in a different inner map

        _map.put( 99, 3, 2, "Today" );

        // Verify that there are now exactly two inner maps

        Assert.assertEquals( 2, _map.innerMaps().size() );

        // Verify that the map contains the correct three different values

        values = ThreeDimensionalTreeMapTest.getContents( _map );

        Assert.assertEquals( 3, values.size() );
        Assert.assertTrue( values.contains( "Today" ) );
        Assert.assertTrue( values.contains( "World" ) );
        Assert.assertTrue( values.contains( "There" ) );

        // Put a duplicate into the map but in a different place than the existing values

        _map.put( 99, 99, 2, "World" );

        // Verify that the map contains four values and that the correct three values appear

        values = ThreeDimensionalTreeMapTest.getContents( _map );

        Assert.assertEquals( 4, values.size() );
        Assert.assertTrue( values.contains( "Today" ) );
        Assert.assertTrue( values.contains( "World" ) );
        Assert.assertTrue( values.contains( "There" ) );

        // Verify that there are a total of four values in the map and that the correct one is duplicated

        TreeCounter<String> countedValues = ThreeDimensionalTreeMapTest.getTreeCountedContents( _map );

        Assert.assertEquals( 4, countedValues.getGrandTotal() );
        Assert.assertEquals( 1, countedValues.getCount( "Today" ) );
        Assert.assertEquals( 2, countedValues.getCount( "World" ) );
        Assert.assertEquals( 1, countedValues.getCount( "There" ) );

    }

    @Test
    public void testToString() {

        // Check the empty map case

        Assert.assertEquals( "ThreeDimensionalTreeMap( size = 0 )", _map.toString() );

        // Put a few things in the map

        _map.put( 1, 2, 3, "Hello" );
        _map.put( 3, 2, 1, "World" );

        // Check the case with two things in the map

        Assert.assertEquals( "ThreeDimensionalTreeMap( size = 2 )", _map.toString() );

    }

    private static Vector<String> getContents( final ThreeDimensionalSortedMap<Integer,Integer,Integer,String> map ) {

        Vector<String> values = new Vector<>();

        for ( String aMap : map ) {

            values.add( aMap );

        }

        return values;

    }

    @SuppressWarnings("UnusedDeclaration")
    private static SortedMap<Integer,String> getSortedContents( final ThreeDimensionalSortedMap<Integer,Integer,Integer,String> map ) {

        SortedMap<Integer,String> values = new TreeMap<>();

        int i = 0;

        for ( String aMap : map ) {

            values.put( i, aMap );

            i += 1;

        }

        return values;

    }

    private static TreeCounter<String> getTreeCountedContents( final ThreeDimensionalSortedMap<Integer,Integer,Integer,String> map ) {

        TreeCounter<String> values = new TreeCounter<>();

        for ( String aMap : map ) {

            values.count( aMap );

        }

        return values;

    }

}