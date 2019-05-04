package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;

/**
 Created by danny on 2019/05/03.
 */

public interface CreateSomethingHelper<T> {

    /**
     The singular name for the type/kind of thing being created.
     @return the singular name for the type/kind of thing being created.
     For example, if this helper is being used to create pear trees then this method should return "pear tree".
     */

    String getSingularTypeName();

    /**
     Determine if something by a specified name already exists.
     @param name the name of the something to be checked.
     @return {@code true} if something by that name already exists; {@code false} otherwise.
     */

    boolean doesSomethingExist( @NotNull final String name );

    /**
     Actually create the something.
     @param name the name of the something which is to be created.
     @return the new something.
     */

    T createSomething( @NotNull final String name );

}
