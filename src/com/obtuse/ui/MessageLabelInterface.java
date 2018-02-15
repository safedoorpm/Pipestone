/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Optional;

/**
 Created by danny on 2018/01/05.
 */

public interface MessageLabelInterface extends ImmutableMessageLabelInterface {

    void clear();

    void setMessage( @Nullable final String message );

    void setMessage( @Nullable final String message, @Nullable final String extraInfo );

    void setExtraInfo( @Nullable final String extraInfo );

    void setMessage( final @NotNull Exception e );

//    {
//
//        if ( e instanceof ObtuseMessageLabel.AugmentedIllegalArgumentException ) {
//
//            ObtuseMessageLabel.AugmentedIllegalArgumentException ae = (ObtuseMessageLabel.AugmentedIllegalArgumentException)e;
//
//            setMessage( ae.getMessage(), ae.getExtraInfo() );
//
//        } else {
//
//            setMessage( e.getMessage() );
//
//        }
//
//    }

}
