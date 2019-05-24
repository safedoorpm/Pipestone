package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;

/**
 Tell an interested party about the creation of something.
 <p>Used by the {@link CreateSomethingDialog} facility.</p>
 */

public interface CreateSomethingNotifier<T,N> {

    /**
     Report on the creation of something.
     @param something the something which has just been created.
     */

    void somethingHasBeenCreated( @NotNull T something );

    /**
     Report that the creation of something has failed.
     @param somethingName the name of the something that failed creation.
     @param createSomethingHelper the {@link CreateSomethingHelper}{@code <T>} that was involved in the creation attempt.
     @return {@code true} if the entire attempt should be abandoned (i.e. make the dialog box disappear and return);
     {@code false} if the human should be given the opportunity to try a new name.
     */

    boolean creationFailed( @NotNull final N somethingName, @NotNull CreateSomethingHelper<T,N> createSomethingHelper );

}
