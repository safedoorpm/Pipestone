/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.gowing.p2a.examples;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.holders.GowingStringHolder;
import com.obtuse.util.kv.ObtuseKeyword;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 A class which does not allow two different instances to have the same name.
 <p>Consider a class in which each instance has a name and the existence of two instances with the same name is prohibited.
 It might be useful to be able to Gowing-unpack previously Gowing-packed instances of the class without ending up with
 duplicate instances in the unpacking JVM.
 This example demonstrates how this might be done.</p>
 <p>The basic approach is to
 <ul>
 <li>Gowing-pack instances of the class just as one would Gowing-pack instances of any other class</li>
 <li>When the time comes to Gowing-unpack an instance of the class, to consult a dictionary/registry to see if the
 about-to-be-Gowing-unpacked instance already exists and, if it does, to use the already existing instance instead of creating a new instance.</li>
 </ul>
 <p>In this example, this class is going to be derived from the {@link ObtuseKeyword} class which provides certain facilities which are useful
 for classes which represent the names of things.
 While these facilities are useful and the {@code ObtuseKeyword} class is itself {@link GowingPackable}, we don't really need
 the Gowing packing and unpacking facilities of the {@code ObtuseKeyword}.
 Consequently, we are going to simply ignore the fact that {@code ObtuseKeyword} is {code GowingPackable}.</p>
 */

//@SuppressWarnings("unused")
public class RestrictedNameExample extends ObtuseKeyword {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( RestrictedNameExample.class );
    private static final int VERSION = 1;

    private static final EntityName NAME = new EntityName( "_n" );

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;

        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;

        }

        /**
         The heart of this example actually takes place in this {@link RestrictedNameExample#FACTORY} class's
         {@link GowingEntityFactory#createEntity(GowingUnPacker, GowingPackedEntityBundle, GowingEntityReference)} method in a couple of short steps:
         <ol>
         <li>If the name of the instance we're being asked to create already exists then just return that instance</li>
         <li>otherwise, create a new instance using the <em>regular</em> constructor</li>
         </ol>
         @param unPacker the {@link GowingUnPacker} that's running this circus.
         @param bundle the {@link GowingPackedEntityBundle} that represents the instance that we're supposed to create.
         @param er the {@link GowingEntityReference} of the instance that we're supposed to create.
         @return an existing instance with the intended name (found in the {@code bundle}) or a newly created instance with the intended name.
         */
        @SuppressWarnings("OptionalIsPresent")
        @Override
        @NotNull
        public GowingPackable createEntity(
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        ) {

            // Get the intended name.

            String instanceName = bundle.MandatoryStringValue( NAME );

            // Look for an already existing instance with the intended name.

            Optional<RestrictedNameExample> optInstance = findByName( instanceName );
            if ( optInstance.isPresent() ) {

                // There's already an instance with the intended name - return it.

                return optInstance.get();

            } else {

                // Use the 'regular' (i.e. non-Gowing-aware) constructor to create a new
                // instance with the intended name and return that new instance forthwith.

                return new RestrictedNameExample( bundle.MandatoryStringValue( NAME ) );

            }

        }

    };

    /**
     A table of the known instances indexed by their names in string form.
     <p>*** This table must exist before we start creating instances or we get a NPE in our constructor.</p>
     */

    private static final SortedMap<String,RestrictedNameExample> s_knownDataInstanceNames = new TreeMap<>();

    /**
     Create a new instance with the specified name.
     @param name the name for the new instance.
     @throws IllegalArgumentException if an instance with the same name already exists.
     */

    public RestrictedNameExample( final @NotNull String name ) {
        super( name );

        // Avoid race conditions.

        synchronized ( s_knownDataInstanceNames ) {

            // Is there already an instance with the specified name?

            if ( findByName( name ).isPresent() ) {

                throw new IllegalArgumentException( "RestrictedNameExample:  duplicate name \"" + name + "\"" );

            }

            s_knownDataInstanceNames.put( name, this );

        }

    }

    /**
     Bundle up an instance of this class for a {@link GowingPacker} instance.
     <p>Note that we do not involve our parent class in bundling up this instance.</p>
     @param isPackingSuper irrelevant to this implementation.
     @param packer the {@link GowingPacker} responsible for this circus.
     @return the bundle containing not much more than the name of this {@code {@link RestrictedNameExample}}.
     */

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper,
            final @NotNull GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                packer.getPackingContext()
        );

        bundle.addHolder( new GowingStringHolder( NAME, getKeywordName(), true ) );

        return bundle;

    }

    /**
     Since we do not actually/formally Gowing-unpack new instances, we are done finishing before we begin.
     @param unPacker the {@link GowingPacker} responsible for this circus.
     @return {@code true} (always).
     */

    @Override
    public boolean finishUnpacking( final @NotNull GowingUnPacker unPacker ) {

        return true;

    }

    /**
     Get this instance's name.
     <p>Note that this is just a wrapper for our parent's {@link #getKeywordName()} method
     but with a method name that is a better fit for what this class does.</p>
     @return this instance's name.
     */

    public String getName() {

        return getKeywordName();

    }

    /**
     Look for an existing instance with a specified name.
     @param name the specified name.
     @return an {@link Optional}{@code <RestrictedNameExample>} instance containing the existing instance if it exists or
     {@link Optional#empty()} if no such instance exists.
     */

    public static Optional<RestrictedNameExample> findByName( final @NotNull String name ) {

        synchronized ( s_knownDataInstanceNames ) {

            return Optional.ofNullable( s_knownDataInstanceNames.get( name ) );

        }

    }

    /**
     Get an instance with a specified name, creating it if it does not already exist.
     <p>While this method could have been used above in our Gowing factory's {@code createInstance()} method,
     it seems more instructive to show the entire logic in that method).</p>
     @param name the specified name.
     @return the instance with the specified name.
     */

    public static RestrictedNameExample getInstanceByName( final @NotNull String name ) {

        synchronized ( s_knownDataInstanceNames ) {

            Optional<RestrictedNameExample> optInstance = Optional.ofNullable( s_knownDataInstanceNames.get( name ) );

            return optInstance.orElseGet( () -> new RestrictedNameExample( name ) );

        }

    }

    @Override
    public String toString() {

        return getKeywordName();

    }

    /**
     Compare ourselves to another {@link RestrictedNameExample}.
     We actually let our parent class deal with this.
     Having this method here means that an attempt to compare a {@link RestrictedNameExample} to some other instance of our parent
     class which is not an instance of this class gets an exception thrown. This feels like it could be useful for
     debuggin' purposes.
     @param rhs the other {@link RestrictedNameExample}.
     @return the result of comparing ourselves to the other {@link RestrictedNameExample} using whatever sorting order
     that our parent class uses.
     */

    public final int compareTo( final @NotNull RestrictedNameExample rhs ) {

        return super.compareTo( rhs );

    }

    /**
     Demonstrate this class in action.
     */

    public static void main( final String[] args ) {



    }

    // Fini.

    // Note that we do not implement either the Object.equals(Object) method or the Object.hashCode() method
    // since the ObtuseKeyword implementations of these methods work just fine for us.

}
