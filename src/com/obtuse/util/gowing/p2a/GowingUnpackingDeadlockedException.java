/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.gowing.p2a;

import com.obtuse.util.gowing.GowingPackable;
import com.obtuse.util.gowing.GowingUnPacker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 Thrown when an unpacking operation fails because it became impossible to finish any of the remaining-to-be-finished entities.
 <p>Looking at the list of what is not yet finished is probably a good place to start when diagnosing the cause of this issue
 (see {@link #getUnfinishedEntityReferences()} for more info).</p>
 <p>Note that an instance of this class could refer to a fairly substantial amount of memory since the {@link GowingUnPacker}
 reference held by an instance of this class directly or indirectly refers to every {@link GowingPackable} entity unpacked
 since the unpacking operation started (note that this includes both finished and unfinished unpacked entities).
 Consequently, it would probably be wise to ensure that instances of this class are discarded when they are no longer useful.</p>
 <p>The {@code GowingUnPacker} referenced by this node has access to a great deal of potentially useful information (depending on
 how aggressive you want to be about diagnosing what went wrong). A few 'interesting' places to 'look' include:
 <ul>
 <li>Get the {@link GowingEntityReference}s for every {@link GowingPackable} that has been encountered during the unpack operation:
 <br>{@code this.getUnPacker().getUnPackerContext().getSeenEntityReferences()}</li>
 <li>Get a map containing every {@link GowingPackable} that has been encountered during the unpack operation:
 <br>{@code this.getUnPacker().getUnPackerContext().getSeenEntitiesMap()}</li>
 </ul>
 <p>If this exception is thrown then poking around inside the {@link GowingUnPacker} is unlikely to cause you any harm
 since the unpacking operation is done and dead.
 <u>On the other hand, it would be wise to be quite careful when poking around inside a {@code GowingUnPacker} instance either before or during
 its unpack operation (judicious use of the Gowing source code is more than merely advised).</u></p>
 */

public class GowingUnpackingDeadlockedException extends RuntimeException {

    private final GowingUnPacker _unPacker;
    private final SortedSet<GowingEntityReference> _unfinishedEntityReferences;
    private final SortedMap<GowingEntityReference,GowingPackable> _unfinishedEntitiesMap;

    public GowingUnpackingDeadlockedException( final @NotNull String msg ) {
        super( msg );

        _unPacker = null;
        _unfinishedEntityReferences = new TreeSet<>();
        _unfinishedEntitiesMap = new TreeMap<>();

    }

    public GowingUnpackingDeadlockedException( final @NotNull String msg, final @Nullable Throwable cause, final @NotNull GowingUnPacker unPacker ) {
        super( msg, cause );

        _unPacker = unPacker;

        _unfinishedEntityReferences = _unPacker.getUnPackerContext().getUnfinishedEntityReferences();

        SortedMap<GowingEntityReference,GowingPackable> unfinishedEntitiesMap = new TreeMap<>();
        for ( GowingEntityReference er : _unfinishedEntityReferences ) {

            GowingPackable packable = _unPacker.resolveReference( er );
            unfinishedEntitiesMap.put( er, packable );

        }

        _unfinishedEntitiesMap = unfinishedEntitiesMap;

    }

    /**
     Get a sorted set containing the still unfinished entity references at the time that this instance was created.
     @return a set containing the entity references representing the unfinished entities that existed at the moment that this exception was thrown.
     This set was created when this exception instance was created and 'belongs' to whoever holds a reference to this exception instance (or to the set).
     */

    public SortedSet<GowingEntityReference> getUnfinishedEntityReferences() {

        return _unfinishedEntityReferences;

    }

    /**
     Get a sorted map containing the still unfinished entities at the time that this instance was created.
     <p></p>
     @return a map containing the entity references representing the unfinished entities that existed at the moment that this exception was thrown.
     <p>There will be a one-to-one correspondence between the {@link GowingEntityReference}s in the set
     returned by {@link #getUnfinishedEntityReferences()} and the keys in the sorted map returned by this method.
     Also, every key in the sorted map will refer to an actual unfinished entity (i.e. there are no {@code null} values in the map).</p>
     <p>This map was created when this exception instance was created and 'belongs' to whoever holds a reference to this exception instance (or to the set).</p>
     */

    public SortedMap<GowingEntityReference, GowingPackable> getUnfinishedEntitiesMap() {

        return _unfinishedEntitiesMap;

    }

    /**
     Get the {@link GowingUnPacker} instance from within this instance.
     @return the {@link GowingUnPacker} instance from within this instance.
     Note that there is no {@code GowingUnPacker} instance if this instance was created using the {@link #GowingUnpackingDeadlockedException(String)} constructor.
     Use {@link #hasUnPacker()} to determine if there's an {@code GowingUnPacker} instance to get.
     @throws NullPointerException if there's no {@code GowingUnPacker} instance to get.
     */

    @NotNull
    public GowingUnPacker getMandatoryUnPacker() {

        if ( _unPacker == null ) {

            throw new NullPointerException( "no GowingUnPacker to get" );

        }

        return _unPacker;

    }

    /**
     Determine if there's a {@link GowingUnPacker} instance within this instance.
     @return {@code true} if there is; {@code false} if there isn't.
     */

    public boolean hasUnPacker() {

        return _unPacker != null;

    }

}
