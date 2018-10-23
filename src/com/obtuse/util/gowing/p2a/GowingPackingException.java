package com.obtuse.util.gowing.p2a;

import com.obtuse.util.gowing.GowingPackedEntityBundle;
import org.jetbrains.annotations.Nullable;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Something went wrong packing something.
 <p>This extends {@link IllegalArgumentException} because they should be incredibly rare.
 Granted, that means that they will come as a surprise but I'm not sure that they are worth
 cluttering up all sorts of methods with throwable clauses for this exception.
 Then again, I'm not sure that they are not worth said cluttering up (in other words, my stance on this could change).</p>
 */

public class GowingPackingException extends IllegalArgumentException {

    private GowingPackedEntityBundle _causeBundle;

    public GowingPackingException( final String msg ) {

        this( msg, null, null );

    }

    public GowingPackingException( final String msg, @Nullable final GowingPackedEntityBundle bundle ) {

        this( msg, bundle, null );

    }

    public GowingPackingException( final String msg, @Nullable final Throwable cause ) {

        this( msg, null, cause );

    }

    public GowingPackingException(
            final String msg,
            @Nullable final GowingPackedEntityBundle causeBundle,
            @Nullable final Throwable cause
    ) {

        super( msg, cause );

        _causeBundle = causeBundle;

    }

    @Nullable
    public GowingPackedEntityBundle getCauseBundle() {

        return _causeBundle;

    }

    public void setCauseBundle( final GowingPackedEntityBundle bundle ) {

        if ( _causeBundle == null ) {

            _causeBundle = bundle;

        } else {

            throw new IllegalArgumentException( "bundle already set in " + this, this );

        }

    }

    public String toString() {

        return "GowingUnpackingException( \"" + getMessage() + "\", causeBundle = " + _causeBundle + " )";

    }

}