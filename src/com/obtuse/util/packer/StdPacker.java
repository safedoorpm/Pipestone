package com.obtuse.util.packer;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A 'standard' implementation of an entity packer.
 */

public class StdPacker implements Packer {

    public static final PackingId NULL_ENTITY_ID = new PackingId( (short) 0, 0L );

    private final File _file;

    private SortedMap<PackingId,Packable> _packedThings = new TreeMap<PackingId, Packable>();

    public StdPacker( File file ) {
        super();

        _file = file;

    }

    /**
     * Pack a packable entity.
     * @param entity the entity to be packed (must not be null).
     * @return the entity's packed id (only unique within the context of this packer).
     * @throws NullPointerException if <code>entity</code> is null.
     */

    @NotNull
    @Override
    public PackingId packEntity( Packable entity ) {

        if ( entity == null ) {

            throw new NullPointerException( "mandatory entity not provided" );

        }

        return null;

    }

    @Override
    @NotNull
    public PackingId packOptionalEntity( Packable optEntity ) {

        if ( optEntity == null ) {

            return NULL_ENTITY_ID;

        } else {

            return packEntity( optEntity );

        }

    }

}
