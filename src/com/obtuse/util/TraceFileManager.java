/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

/**
 * Describe something that wants to be informed when a trace file is emitted.
 */

public interface TraceFileManager {

    void newTraceFile( String traceFileName, long timeStamp );

}
