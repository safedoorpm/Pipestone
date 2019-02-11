package com.obtuse.util;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingBackReferenceable;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 A reasonably robust {@link GowingPackable} unique id facility.
 <p/>
 Instances of this class are immutable.
 <p/>
 Instances of this class are {@link GowingPackable} and {@link GowingBackReferenceable}.
 <p/>
 This class implement the {@link Comparable}{@code <UniqueId>} interface.
 <p/>
 <p>Check out {@link UniqueEntity} for a different spin on the whole unique identifier notion.</p>
 */

public class UniqueId implements GowingPackable, GowingBackReferenceable, Comparable<UniqueId>, Serializable {

    /**
     The smallest id value that will ever be found in a {@code UniqueId} instance returned by {@link #getJvmLocalUniqueId()}.
     Each call to {@code getJvmLocalUniqueId()} after the first will return a {@code UniqueId} instance with an id value one
     greater than the id value in the {@code UniqueId} instance returned by the immediately preceding call.
     <p/>Note that {@code 1L << 40} is equal to 1,099,511,627,776L (this is still a very very long ways from the maximum
     {@code long} value of 9,223,372,036,854,775,807L).
     */

    public static final long MINIMUM_JVM_UNIQUE_ID = 1L << 40;

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( UniqueId.class );
    private static final int VERSION = 1;

    private static final EntityName G_UNIQUE_ID = new EntityName( "_uid" );

    /**
     A factory used by {@link Gowing} when unpacking an instance of this class.
     <p/>You can probably just ignore the existence of this factory.
     */

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;
        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;
        }

        @Override
        public @NotNull GowingPackable createEntity(
                @NotNull final GowingUnPacker unPacker,
                @NotNull final GowingPackedEntityBundle bundle,
                @NotNull final GowingEntityReference er
        ) {

            return new UniqueId( bundle.longValue( G_UNIQUE_ID ) );

        }

    };

    private static long s_nextJvmLocalUniqueId = MINIMUM_JVM_UNIQUE_ID + 1;

    private final transient GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    private final long _id;

    /**
     Create an instance containing a specified {@code long} value.
     <p/>
     Whether or not the {@code UniqueId} instances you create are actually as unique as you need them to be
     depends on how carefully you construct your {@code UniqueId} instances ((arguably) more accurately, how carefully you
     are when selecting id values for the {@code UniqueId} instances that you create).
     @param id the specified {@code long} value.
     */

    public UniqueId( final long id ) {
        super();

        _id = id;

    }

    /**
     A static method that creates a {@code UniqueId} instance.
     @param id the {@code long} id value that is to be encapsulated within the newly created {@code UniqueId} instance.
     @return the newly created {@code UniqueId} instance.
     */

    public static UniqueId get( final long id ) {

        return new UniqueId( id );

    }

    /**
     Intended to be used to get a JVM-unique {@link UniqueId} instance.
     <p/>
     <u>IMPORTANT: if you do not 100% understand the following information, do everybody (including yourself)
     a favour and find yourself a different way to create your {@code UniqueId} instances.</u>
     <p/>
     Each call to this method returns a {@link UniqueId} instance which is
     different than any other {@code UniqueId} instance returned by previous or subsequent calls (from
     within the same JVM) to this method.
     <p/>Consequently, if you <b>ALWAYS</b> use this method to create {@code UniqueId} instances
     then all the {@code UniqueId} instances you create within a given JVM instance will be unique.
     <u>Of course, if you fail to <b>ALWAYS</b> use this method to create {@code UniqueId} instances then
     at least some if not many of the {@code UniqueId} instances returned by this method might be equal to
     {@code UniqueId} instances created within the same JVM by other means.</u>

     @return this method returns a {@link UniqueId} instance with a different <em>id</em> than any other {@code UniqueId}
     instance returned by any previous or subsequent call (from within the same JVM instance) to this method.
     <p/>Note that if you have two or more JVM instances that use this method then calls to this method from within one
     the various different JVM instances can and very very likely will return {@code UniqueId} instances that are
     equal to the {@code UniqueId} instances returned by this method in the other other JVM instances.
     */

    public static UniqueId getJvmLocalUniqueId() {

        return new UniqueId( s_nextJvmLocalUniqueId++ );

    }

    /**
     Format this instance's {@code long} id value as a hex string.
     @return a 16 character long string of hex digits (0-9, a-f)
     which represents the value of this {@code UniqueId} instance.
     */

    public String format() {

        return ObtuseUtil.hexvalue( _id );

    }

    /**
     Format an arbitrary {@code long} value as a hex string.
     <p/>Note that this method just passes the provided {@code long} value to {@link ObtuseUtil#hexvalue(long)}
     and returns the result.
     @param id an arbitrary {@code long} value.
     @return the value of the {@code id} parameter formatted as a 16 character long string of
     hex digits (0-9, a-f).
     */

    public static String format( long id ) {

        return ObtuseUtil.hexvalue( id );

    }

    /**
     Get the {@code long} id encapsulated by this instance.
     @return the {@code long} id provided when this instance was created.
     */

    public long getLongID() {

        return _id;

    }

    /**
     Returns a string of the format "{(" + id + ")}".
     @return the id value encapsulated by this instance surrounded by "{(" and ")}".
     For example, if the id value of this instance is 123456 then
     this method will return a {@link String} equal to "{(123456)}".
     */

    public String toString() {

        return "{(" + _id + ")}";

    }

    /**
     Compare this instance to an arbitrary {@code UniqueId} instance.
     @param other the other instance.
     @return the value 0 if {@code this.getLongID() == other.getLongID()} ;
     a value less than 0 if {@code this.getLongID() < other.getLongID()} ;
     and a value greater than 0 if {@code this.getLongID() > other.getLongID()}
     */

    @Override
    public int compareTo( @NotNull final UniqueId other ) {

        return Long.compare( _id, other._id );

    }

    /**
     Compare this instance to an arbitrary {@link Object}.
     @param rhs the other instance.
     @return {@code true} if {@code rhs} is an instance of this class and if this instance's {@code id}
     and the {@code rhs} instance's {@code id} are equal; {@code false} otherwise.
     */

    @Override
    public boolean equals( Object rhs ) {

        return rhs instanceof UniqueId && compareTo( (UniqueId)rhs ) == 0;

    }

    /**
     Compute a hashcode for this instance.
     @return exactly equivalent to invoking {@code Long.hashCode( this.getLongID() )}.
     */

    @Override
    public int hashCode() {

        return Long.hashCode( _id );

    }

    /**
     Get this instance's {@link GowingInstanceId} value.
     <p/>You can probably just ignore the existence of this method.
     @return this instance' {@link GowingInstanceId} value.
     */

    @Override
    public @NotNull GowingInstanceId getInstanceId() {

        return _instanceId;
    }

    /**
     Bundle this instance into a {@link GowingPackedEntityBundle}.
     <p/>This method is used by the {@link Gowing} facility when packing an instance of this class.
     <p/>You can probably just ignore the existence of this method.
     @param packer the {@link GowingPacker} running the packing operation.
     @return the resulting {@link GowingPackedEntityBundle}.
     */

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, @NotNull final GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                packer.getPackingContext()
        );

        bundle.addLongHolder( G_UNIQUE_ID, _id );

        return bundle;

    }

    /**
     Part two of unpacking a Gowing-packed instance of this class.
     <p/>
     Note that since this class implements the {@link GowingBackReferenceable} instance and adheres to the
     requires specified within that interface's JavaDocs, this method does nothing other than returning {@code true}.
     <p/>You can probably just ignore the existence of this method.
     @param unPacker the {@link GowingPacker} responsible for this circus.
     @return {@code true} (always).
     @throws GowingUnpackingException never (this method does nothing which might conceivably provoke a throwing of this exception).
     */

    @SuppressWarnings("RedundantThrows")
    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) throws GowingUnpackingException {

        return true;

    }

}
