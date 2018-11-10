/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

/*
 * Wrap a message in a label.
 */

package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 A message consisting of a priority value and text.
 <p>Instances of this class are immutable.</p>
 */

@SuppressWarnings("unused")
public class ErrorMessage implements ImmutableMessageLabelInterface {

    @Nullable
    public String getText() {

        return _message;

    }

    @NotNull
    public Optional<String> getOptionalText() {

        String txt = getText();

        return Optional.ofNullable (( txt == null || txt.isEmpty() ) ? null : txt );

    }

    @NotNull
    public Optional<String> getExtraInfo() {

        return Optional.empty();

    }

    public enum Priority {
        NONE,
        INFO,
        WARNING,
        ERROR
    }

    private final Priority _priority;

    private final String _message;

    public ErrorMessage( final @NotNull Priority priority, @Nullable final String message ) {
        super();

        _priority = priority;
        _message = message == null ? "" : message;

    }

    public ErrorMessage() {
        this( Priority.NONE, "" );

    }

    @NotNull
    public Priority getPriority() {

        return _priority;

    }

    @NotNull
    public String getErrorMessage() {

        return _message;

    }

    public int compareTo( final @NotNull ErrorMessage rhs ) {

        int rval = getPriority().compareTo( rhs.getPriority() );
        if ( rval == 0 ) {

            rval = getErrorMessage().compareTo( rhs.getErrorMessage() );

        }

        return rval;

    }

    public int hashCode() {

        return ( getPriority().hashCode() << 3 ) ^ getErrorMessage().hashCode();

    }

    public boolean equals( final Object rhs ) {

        return rhs instanceof ErrorMessage && compareTo( ( (ErrorMessage)rhs) ) == 0;

    }

    public String toString() {

        return getErrorMessage();

    }

}
