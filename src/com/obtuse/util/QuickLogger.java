package com.obtuse.util;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

/**
 A simple logging facility.
 */

public class QuickLogger {

    private PrintWriter _logger;
    private final String _tag;
    private final File _logFile;

    public QuickLogger( @NotNull final String loggerName ) {
        super();

        String tag = DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( new Date() );
        _tag = tag.replace( ':', '.' ).replace( ' ', '_' );
        File homeDir = new File( System.getProperty( "user.home" ) );
        File logDir = new File( homeDir, "QuickLogs" );
        logDir.mkdirs();
        _logFile = new File( logDir, loggerName + "-" + _tag + ".qlog" );

        ObtuseUtil.doNothing();

    }

    public boolean usedAtLeastOnce() {

        return _logger != null;

    }

    @NotNull
    public String getTag() {

        return _tag;

    }

    @NotNull
    public File getLogFile() {

        return _logFile;

    }

    public synchronized void log( @NotNull final String msg ) {

        log( msg, null );

    }

    public synchronized void log( String msg, Throwable throwable ) {

        if ( _logger == null ) {

            try {

                _logger = new PrintWriter( _logFile );

            } catch ( FileNotFoundException e ) {

                throw new HowDidWeGetHereError(
                        "QuickLogger:  unable to create log file " +
                        ObtuseUtil.enquoteToJavaString( _logFile.getPath() )
                );

            }

        }

        String timestampString = DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( new Date() );
        _logger.println( timestampString + " " + msg );
        if ( throwable != null ) {

            throwable.printStackTrace( _logger );

        }

        _logger.flush();

    }

    public static void main( String[] args ) {

        QuickLogger ql = new QuickLogger( "testingQuickLogger" );
        ql.log( "Hello" );
        ql.log( "Boom!", new IllegalArgumentException( "a fake IAE for testing QuickLogger" ) );

    }

}
