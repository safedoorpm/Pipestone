/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

/**
 * An entity with an id number which is unique within some id-space.
 * Instance's of this class are immutable.
 * Obviously, instances of derivations of this class might not be immutable!
 * <p/>
 * This class implements a {@link #hashCode} method which satisfies the hashCode contract as specified by
 * the {@link Object#hashCode} method.
 * This class also implements a {@link #equals} method which satisfies the equals contract as specified by
 * the {@link Object#equals} method.
 * Of course, derivations of this class could implement hashCode and/or equals methods in ways which violate these contracts.
 * <p/>
 * <p>Check out {@link UniqueId} for a different spin on the whole unique identifier notion.</p>
 */

@SuppressWarnings("UnusedDeclaration")
public class UniqueEntity implements UniqueWithId {

    private final long _id;

    private final int _hashCode;

    private static final UniqueLongIdGenerator _defaultIdGenerator = new SimpleUniqueLongIdGenerator( UniqueEntity.class.getCanonicalName() );

    /**
     * Create an instance using the default {@link UniqueLongIdGenerator}.
     */

    public UniqueEntity() {
        this( UniqueEntity.getDefaultIdGenerator() );
    }

    /**
     * Create an instance using the specified {@link UniqueLongIdGenerator}.
     * @param uniqueLongIdGenerator the unique id generator to be used to compute this newly created instance's id value.
     */

    @SuppressWarnings("WeakerAccess")
    public UniqueEntity( final UniqueLongIdGenerator uniqueLongIdGenerator ) {
        super();

        _id = uniqueLongIdGenerator.getUniqueId();
        _hashCode = Long.valueOf( _id ).hashCode();

    }

    /**
     * Get this instance's id value.
     * @return this instance's id value.
     */

    public final long getId() {

        return _id;

    }

    /**
     * Get a 'default' {@link UniqueLongIdGenerator} suitable for use when invoking {@link #UniqueEntity(UniqueLongIdGenerator)}.
     * <p/>
     * This method returns a JVM-unique instance of {@link SimpleUniqueLongIdGenerator}.
     * @return a JVM-unique instance of {@link SimpleUniqueLongIdGenerator}.
     */

    public static UniqueLongIdGenerator getDefaultIdGenerator() {

        return UniqueEntity._defaultIdGenerator;

    }

    /**
     * Returns a hash code for the object.
     * <p/>
     * This method returns precisely what <tt>Long.valueOf( this.getId() ).hashCode()</tt> would return.
     * This method satisfies the hashCode contract specified by the {@link Object#hashCode} method.
     * <p/>
     * Note that the hash code value for each newly created instance is pre-computed when the instance is created.
     * This costs a few bytes of space per instance but makes calls to this method very fast.
     * @return a hash code for this instance.
     */

    public int hashCode() {

        return _hashCode;

    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p/>
     * This implementation defines "equal to" to require that the other object be an instance of this class
     * and that both this instance and the other object return the same value when their {@link #getId} method
     * is invoked.  This method satisfies the equality contract specified by the {@link Object#equals} method.
     * @param rhs the other object.
     * @return true if the other object is "equal to" this one.
     */

    public boolean equals( final Object rhs ) {

        return rhs instanceof UniqueEntity && ((UniqueEntity)rhs).getId() == getId();

    }

    public String toString() {

        return "UniqueEntity( id=" + getId() + " )";

    }

}
