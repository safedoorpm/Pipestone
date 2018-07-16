/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

/**
 Something that validates and manages an editable value.
 */

public interface EditValueAdvocate<T> {

    /**
     * Vet a candidate value as a pre-condition to storing this value as the model's new stored value.
     * @param candidateValue the value to be validated.
     * @return {@code true} if the candidate value is acceptable
     * (will always result in {@link #storeNewValue} being called with this same candidate value);
     * {@code false} otherwise.
     */

    boolean isValueValid( T candidateValue );

    /**
     * Store a validated value into the model.
     * <p/>This method is only called and is always called immediately after a call to {@link #isValueValid}.
     * @param newValue the new value for the model.
     * @param causedByReturnKey true if this store is being triggered by an ActionListener.actionPerformed call; false otherwise.
     */

    default void storeNewValue( final @NotNull T newValue, final boolean causedByReturnKey ) {

        Logger.logMsg( "call to default storeNewValue( " + ObtuseUtil.enquoteJavaObject( newValue ) + ", " + causedByReturnKey +" ) in instance of " + getClass().getCanonicalName() );

    }

    /**
     * Specify the rollback value that this instance should provide upon request.
     * @param rollbackValue the value that this instance should provide when it is asked for its rollback value (see {@link #getRollbackValue()} for more info).
     */

    @SuppressWarnings("unused")
    void setRollbackValue( @NotNull T rollbackValue );

    /**
     * Called when the user has clicked the ESC key to obtain the value to roll the JTextField back to.
     * @return the rollback value.
     */

    T getRollbackValue();

}
