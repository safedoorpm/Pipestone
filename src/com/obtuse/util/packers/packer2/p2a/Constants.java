package com.obtuse.util.packers.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Configuration constants.
 <p/>
 Be careful as there are probably a few real "learning opportunities" in here.
 */

public class Constants {

    /**
     What starts a line comment (can appear anywhere although it is ignored in strings).
     */

    static final char LINE_COMMENT_CHAR = '#';

    /**
     A null reference.
     */

    public static final char NULL_VALUE = 'ø';

    /**
     A zero-origin array value.
     */

    public static final char TAG_ARRAY = 'a';

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
     A packing format version tag.
     */

    public static final char TAG_FORMAT_VERSION = '&';

    static final long MAJOR_FORMAT_VERSION = 1L;

    static final long MINOR_FORMAT_VERSION = 1L;

    public static final long FORMAT_VERSION_MULTIPLIER = 1000000L;

    /**
     A byte value.
     */

    public static final char TAG_BYTE = 'x';

}
