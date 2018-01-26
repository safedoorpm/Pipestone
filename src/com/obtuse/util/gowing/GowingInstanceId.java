package com.obtuse.util.gowing;

import com.obtuse.util.SimpleUniqueIntegerIdGenerator;
import com.obtuse.util.SimpleUniqueLongIdGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.SortedMap;
import java.util.TreeMap;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A value which uniquely identifies something which implements the {@link GowingPackable} interface.
 <p/>
 Notes:
 <ol>
 <li>Every packable entity must have a JVM-wide-unique instance id which never changes during the entity's lifespan within a particular JVM.</li>
 <li>The instance id of a packable entity must be created by invoking one of this class's constructors.</li>
 <li>Packable entities <b>MUST NOT</b> assume that their instance id will remain the same if they are packed and then later unpacked.</li>
 </ol>
 */

public final class GowingInstanceId implements Comparable<GowingInstanceId> {

    private static final SimpleUniqueLongIdGenerator s_idGenerator =
            new SimpleUniqueLongIdGenerator( GowingInstanceId.class.getCanonicalName() + " - entity id generator" );
    private static final SimpleUniqueIntegerIdGenerator s_typeIdGenerator =
            new SimpleUniqueIntegerIdGenerator( GowingInstanceId.class.getCanonicalName() + " - type id generator" );

    private static final SortedMap<String, Integer> s_typeNamesToTypeIds = new TreeMap<>();
    private static final SortedMap<Integer, String> s_typeIdsToTypeNames = new TreeMap<>();

    private final Long _entityId;

    private final int _typeId;

    private final String _typeName;

    public GowingInstanceId( final EntityTypeName typeName ) {

        this( typeName.getTypeName() );

    }

    public GowingInstanceId( final String typeName ) {

        super();

        _typeId = allocateTypeId( typeName );

        _typeName = typeName;
        _entityId = s_idGenerator.getUniqueId();

    }

    public GowingInstanceId( final Class<? extends GowingPackable> classObject ) {

        this( classObject.getCanonicalName() );

    }

    public static int allocateTypeId( final String typeName ) {

        synchronized ( s_typeNamesToTypeIds ) {

            Integer typeId = lookupTypeId( typeName );
            if ( typeId == null ) {

                typeId = s_typeIdGenerator.getUniqueId();
                s_typeNamesToTypeIds.put( typeName, typeId );
                s_typeIdsToTypeNames.put( typeId, typeName );

            }

            return typeId.intValue();

        }

    }

    public static String lookupTypeName( final int typeId ) {

        return s_typeIdsToTypeNames.get( typeId );

    }

    @SuppressWarnings("WeakerAccess")
    public static Integer lookupTypeId( final String typeName ) {

        return s_typeNamesToTypeIds.get( typeName );

    }

    public long getEntityId() {

        return _entityId.longValue();

    }

    public int getTypeId() {

        return _typeId;

    }

    public String getTypeName() {

        return _typeName;

    }

    public int compareTo( @NotNull final GowingInstanceId rhs ) {

        int rval = _typeName.compareTo( rhs._typeName );
        if ( rval == 0 ) {

            rval = _entityId.compareTo( rhs._entityId );

        }

        return rval;

    }

    public boolean equals( final Object rhs ) {

        return rhs instanceof GowingInstanceId && compareTo( (GowingInstanceId)rhs ) == 0;

    }

    public int hashCode() {

        return _entityId.hashCode();

    }

    public String shortForm() {

        return "" + _typeId + ":" + _entityId;

    }

    public String toString() {

        return "GowingInstanceId( " + _typeName + ", " + _entityId + " )";

    }

}
