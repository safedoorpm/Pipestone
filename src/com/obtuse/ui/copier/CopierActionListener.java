package com.obtuse.ui.copier;

import com.obtuse.exceptions.HowDidWeGetHereError;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

/**
 Announce a change in a {@link CopierDataSource} instance.
 */

public abstract class CopierActionListener {

    public final void actionPerformed( final @NotNull CopierDataSource dataSource ) {

        try {

            myActionPerformed( dataSource );

        } catch ( HowDidWeGetHereError e ) {

            throw e;

        } catch ( Throwable e ) {

            throw new HowDidWeGetHereError( "error processing CopierActionListener event", e );

        }

    }

    protected abstract void myActionPerformed( final @NotNull CopierDataSource dataSource );

}
