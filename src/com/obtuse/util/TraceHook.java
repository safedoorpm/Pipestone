package com.obtuse.util;

/*
 * Copyright © 2012 Obtuse Systems Corporation.
 */

import java.util.List;

/**
 * Provide a way to customize a trace report.
 * See {@link com.obtuse.util.Trace#addTraceHook(com.obtuse.util.TraceHook)} for more information.
 * <p/>
 */

@SuppressWarnings( { "UnusedDeclaration" } )
public interface TraceHook {

    /**
     * Build a list of strings that should go into the trace report being generated right now.
     * @return the list of strings to go into the report.
     */

    List<String> run();

    /**
     * Describe this hook.
     * @return the hook's description.
     */

    String getDescription();

}
