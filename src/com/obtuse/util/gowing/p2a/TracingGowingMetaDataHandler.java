/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.gowing.p2a;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.GowingMetaDataHandler;

/**
 Created by danny on 2017/12/03.
 */
public class TracingGowingMetaDataHandler implements GowingMetaDataHandler {

    @Override
    public void processMetaData( final String name, final String value ) {

        Logger.logMsg( "got string metadata element:  " + name + "->" + ObtuseUtil.enquoteToJavaString( value ) );

    }

    @Override
    public void processMetaData( final String name, final long value ) {

        Logger.logMsg( "got long metadata element:  " + name + "->" + value );

    }

    @Override
    public void processMetaData( final String name, final boolean value ) {

        Logger.logMsg( "got boolean metadata element:  " + name + "->" + value );

    }

    @Override
    public void processMetaData( final String name, final double value ) {

        Logger.logMsg( "got double metadata element:  " + name + "->" + value );

    }

}
