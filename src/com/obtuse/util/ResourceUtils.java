/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Manage resources.
 */

public class ResourceUtils {

    private ResourceUtils() {
        super();
    }

    @SuppressWarnings("SameParameterValue")
    public static BufferedInputStream openResource( final String fileName, final String resourceBaseDirectory )
            throws IOException {

        String resourcePath = resourceBaseDirectory + '/' + fileName;
        URL url;
        try {

            url = ImageIconUtils.class.getClassLoader().getResource( resourcePath );

        } catch ( Throwable e ) {

            throw new FileNotFoundException( resourcePath + " (Resource not found)" );

        }

        if ( url == null ) {

            throw new FileNotFoundException( resourcePath + " (Resource not found)" );

        }

        InputStream inputStream = url.openStream();

        return new BufferedInputStream( inputStream );

    }

}
