/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Created by danny on 2018/01/05.
 */

public interface MessageLabelInterface {

    void clear();

    void setMessage( @Nullable final String message );

    void setMessage( @Nullable final String message, @Nullable final String extraInfo );

    void setExtraInfo( @Nullable final String extraInfo );

    default void setMessage( @NotNull final Exception e ) {

        if ( e instanceof MessageLabel.AugmentedIllegalArgumentException ) {

            MessageLabel.AugmentedIllegalArgumentException ae = (MessageLabel.AugmentedIllegalArgumentException)e;

            setMessage( ae.getMessage(), ae.getExtraInfo() );

        } else {

            setMessage( e.getMessage() );

        }

    }
}
