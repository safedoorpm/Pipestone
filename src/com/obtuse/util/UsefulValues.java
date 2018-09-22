package com.obtuse.util;

/**
 A place to put useful values.
 */

public class UsefulValues {

    /**
     Platform-dependent line separator.
     <p>For example, it will be "\n" in JVMs running on Unix-like system whereas
     it will be "\r\n" in JVMs running on a Microsoft operating system.</p>
     */

    public static final String NEWLINE = System.getProperty( "line.separator" );

}
