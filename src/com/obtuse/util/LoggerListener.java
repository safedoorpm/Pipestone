package com.obtuse.util;

import java.util.Date;

/*
 * Copyright © 2012 Obtuse Systems Corporation.
 */

/**
 * Describe something that's interested in seeing all messages sent via {@link com.obtuse.util.Logger}.
 */

public interface LoggerListener {

    void logMessage( Date messageTime, String msg );

}
