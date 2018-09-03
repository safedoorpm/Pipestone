/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

/**
 * Something capable of launching the preferences panel.
 * <p/>Used by our Mac OS X customizations to handle Apple's special approach to launching a preferences panel.
 */

@SuppressWarnings( { "UnusedDeclaration" } )
public interface PreferencesWindowHandler {

    void handlePreferences();

}
