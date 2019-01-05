package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

/**
 Created by danny on 2018/12/29.
 */

public class UniqueIdPair {

    private final UniqueId _namespaceId;
    private final UniqueId _itemId;

    public UniqueIdPair( final UniqueId namespaceId, final UniqueId itemId ) {
        super();

        _namespaceId = namespaceId;
        _itemId = itemId;

    }

    @NotNull
    protected UniqueId getNamespaceId() {

        return _namespaceId;

    }

    @NotNull
    protected UniqueId getItemId() {

        return _itemId;

    }

    @NotNull
    public String format() {

        return "item " + formatItemId() + " from namespace " + formatNamespaceId();

    }

    @NotNull
    protected String formatItemId() {

        return _itemId.format();

    }

    @NotNull
    protected String formatNamespaceId() {

        return _namespaceId.format();

    }

    public static int compare( @NotNull final UniqueIdPair lhs, @NotNull final UniqueIdPair rhs ) {

        int rval = lhs._namespaceId.compareTo( rhs._namespaceId );
        if ( rval == 0 ) {

            rval = lhs._itemId.compareTo( rhs._itemId );

        }

        return rval;

    }

    public int hashCode() {

        return Long.hashCode( _namespaceId.hashCode() ^ ( _itemId.hashCode() << 3 ) );

    }

    public String toString() {

        return "UniqueIdPair( " + _namespaceId.format() + ", " + _itemId.format() + " )";

    }

}
