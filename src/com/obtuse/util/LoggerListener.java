/*
 * Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.util.Date;

/**
 * Describe something that's interested in seeing all messages sent via {@link com.obtuse.util.Logger}.
 */

public interface LoggerListener {

    void logMessage( Date messageTime, String msg );

}
