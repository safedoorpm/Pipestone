package com.obtuse.util.gowing.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import java.util.Collections;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 Configuration constants.
 <p/>
 Be careful as there are probably a few real "learning opportunities" in here.
 */

public class GowingConstants {

    /**
     What starts a line comment (can appear anywhere although it is ignored in strings).
     */

    static final char LINE_COMMENT_CHAR = '#';

    /**
     Declare some metadata (can appear anywhere for now; should only appear as a new statement on a line by itself).
     */

    static final char LINE_METADATA_CHAR = '~';

    /**
     A null reference.
     */

    public static final char NULL_VALUE = 'ø';

    /**
     A zero-origin primitive array value.
     <p/>Always followed by the array's length and a tag indicating the array's element type.
     */

    public static final char TAG_PRIMITIVE_ARRAY = 'a';

    /**
     A zero-origin container array value.
     <p/>Always followed by the array's length and a tag indicating the array's element type.
     */

    public static final char TAG_CONTAINER_ARRAY = 'A';

    /**
     An {@link com.obtuse.util.gowing.EntityName} value.
     */

    public static final char TAG_ENTITY_NAME = 'e';

    /**
     A boolean value.
     */

    public static final char TAG_BOOLEAN = 'b';

    /**
     A character value.
     */

    public static final char TAG_CHAR = 'c';

    /**
     A double value.
     */

    public static final char TAG_DOUBLE = 'd';

    /**
     A float value.
     */

    public static final char TAG_FLOAT = 'f';

    /**
     A short value.
     */

    public static final char TAG_SHORT = 'h';

    /**
     An int value.
     */

    public static final char TAG_INTEGER = 'i';

    /**
     A long value.
     */

    public static final char TAG_LONG = 'l';

    /**
     Reference to an entity described elsewhere.
     */

    public static final char TAG_ENTITY_REFERENCE = 'r';

    /**
     A string value.
     */

    public static final char TAG_STRING = 's';

    /**
     An entity name clause marker.
     */

    public static final char ENTITY_NAME_CLAUSE_MARKER = '/';

    /**
     A packing format version tag.
     */

    public static final char TAG_FORMAT_VERSION = '&';

    static final long MAJOR_FORMAT_VERSION = 1L;

    static final long MINOR_FORMAT_VERSION = 1L;

    public static final long FORMAT_VERSION_MULTIPLIER = 1000000L;

    // Metadata tags

    /**
     The long next id that our multi-generational id generator is to emit.
     */

    public static final String METADATA_NEXT_ID = "_NEXT_ID";

    /**
     The String name of the output file being generated.
     */

    public static final String METADATA_OUTPUT_FILENAME = "_OUTPUT_FILENAME";

    /**
     The time that the output file was written in a 'standard' format.
     <p>The 'standard' format is what would be produced by the following Java expression:</p>
     <blockquote><tt>new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" ).format( dateTime )</tt></blockquote>
     Note that the above format is one of the ISO 8601 date and time formats described at
     <blockquote><a href="http://support.sas.com/documentation/cdl/en/lrdict/64316/HTML/default/viewer.htm#a003169814.htm">http://support.sas.com/documentation/cdl/en/lrdict/64316/HTML/default/viewer.htm#a003169814.htm</a></blockquote>
     */

    public static final String METADATA_OUTPUT_ITS = "_OUTPUT_ITS";

    /**
     The time that the output file was written in the somewhat more readable format produced by the {@link Date#toString()} method.
     */

    public static final String METADATA_OUTPUT_RTS = "_OUTPUT_RTS";

    /**
     A sorted set containing all of the valid reserved keywords.
     <p>Note that all keywords starting with an underscore are also reserved.</p>
     */

    public static final SortedSet<String> RESERVED_KEYWORDS;
    static {

        SortedSet<String> reservedKeywords = new TreeSet<>();
        reservedKeywords.add( METADATA_NEXT_ID );
        reservedKeywords.add( METADATA_OUTPUT_FILENAME );
        reservedKeywords.add( METADATA_OUTPUT_ITS );
        reservedKeywords.add( METADATA_OUTPUT_RTS );

        RESERVED_KEYWORDS = Collections.unmodifiableSortedSet( reservedKeywords );

    }

    /**
     A byte value.
     */

    public static final char TAG_BYTE = 'x';

}
