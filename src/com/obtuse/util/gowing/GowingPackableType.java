package com.obtuse.util.gowing;

import com.obtuse.ui.entitySorter.SortableEntityView;
import com.obtuse.util.DateUtils;
import com.obtuse.util.exceptions.ParsingException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.MalformedURLException;
import java.util.Arrays;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 The fundamental packable values supported by {@link GowingPackableAttribute}.
 */

public enum GowingPackableType {

    /**
     A collection of attributes.
     */

    COLLECTION {

	public String getDescriptiveName() {

	    return "Collection";

	}

	public JComponent getViewComponent( final SortableEntityView timelinePanel, final GowingPackableAttribute attribute, final int requestedMaxWidth ) {

	    return new JLabel( "no support for viewing " + this + " (yet)" );

	}

	public String validate( @NotNull final String s ) {

	    return "validation of collections not support (yet?)";
//	    try {
//
//		//noinspection ResultOfMethodCallIgnored
//		Long.parseLong( s );
//
//		return null;
//
//	    } catch ( NumberFormatException e ) {
//
//		return e.getMessage();
//
//	    }

	}

    },

    /**
     An exact integral value.
     */

    INTEGRAL {

	public String getDescriptiveName() {

	    return "Integral";

	}

	public JComponent getViewComponent( final SortableEntityView timelinePanel, final GowingPackableAttribute attribute, final int requestedMaxWidth ) {

	    return new JLabel( "no support for viewing " + this + " (yet)" );

	}

	public String validate( @NotNull final String s ) {

	    try {

		//noinspection ResultOfMethodCallIgnored
		Long.parseLong( s );

		return null;

	    } catch ( NumberFormatException e ) {

		return e.getMessage();

	    }

	}

    },

    /**
     A floating point value.
     */

    FLOATING_POINT {

	public String getDescriptiveName() {

	    return "Floating point";

	}

	public JComponent getViewComponent( final SortableEntityView timelinePanel, final GowingPackableAttribute attribute, final int requestedMaxWidth ) {

	    return new JLabel( "no support for viewing " + this + " (yet)" );

	}

	public String validate( @NotNull final String s ) {

	    try {

		//noinspection ResultOfMethodCallIgnored
		Double.parseDouble( s );

		return null;

	    } catch ( NumberFormatException e ) {

		return e.getMessage();

	    }

	}

    },

    /**
     An approximate calendar date value.
     */

    APPROXIMATE_DATE {

	public String getDescriptiveName() {

	    return "Approximate date";

	}

	public JComponent getViewComponent( final SortableEntityView timelinePanel, final GowingPackableAttribute attribute, final int requestedMaxWidth ) {

	    return new JLabel( "no support for viewing " + this + " (yet)" );

	}

	public String validate( @NotNull final String s ) {

	    return "approximate dates are not (yet) supported";

	}

    },

    /**
     An exact calendar date value.
     */

    PRECISE_DATE {

	public String getDescriptiveName() {

	    return "Precise date";

	}

	public JComponent getViewComponent( final SortableEntityView timelinePanel, final GowingPackableAttribute attribute, final int requestedMaxWidth ) {

	    return new JLabel( "no support for viewing " + this + " (yet)" );

	}

	public String validate( @NotNull final String s ) {

	    try {

		DateUtils.parseYYYY_MM_DD( s, 0 );

		return null;

	    } catch ( ParsingException e ) {

		return "date must be in the format YYYY-MM-DD";

	    }

	}

    },

    /**
     A yes/no or true/false value.
     */

    BOOLEAN {

	public String getDescriptiveName() {

	    return "Boolean";

	}

	public JComponent getViewComponent( final SortableEntityView timelinePanel, final GowingPackableAttribute attribute, final int requestedMaxWidth ) {

	    return new JLabel( "no support for viewing " + this + " (yet)" );

	}

	public String validate( @NotNull final String s ) {

	    String strUC = s.toUpperCase();

	    if (
		    "T".equals( strUC ) ||
		    "F".equals( strUC ) ||
		    "TRUE".equals( strUC ) ||
		    "FALSE".equals( strUC ) ||
		    "Y".equals( strUC ) ||
		    "N".equals( strUC ) ||
		    "YES".equals( strUC ) ||
		    "NO".equals( strUC )
		    ) {

		return null;

	    }

	    return "value must be one of y, n, t, f, yes, no, true, or false in any mixture of upper or lower case";

	}

    },

    /**
     A string of text which is typically if not always shown as a single line.
     */

    STRING {

	public String getDescriptiveName() {

	    return "String";

	}

	public JComponent getViewComponent( final SortableEntityView timelinePanel, final GowingPackableAttribute attribute, final int requestedMaxWidth ) {

	    return new JLabel( "no support for viewing " + this + " (yet)" );

	}

	public String validate( @NotNull final String s ) {

	    return null;

	}

    },

    /**
     A string of text which often requires multiple lines to display it.
     */

    PLAIN_TEXT {

	public String getDescriptiveName() {

	    return "Plain Text";

	}

	public JComponent getViewComponent( final SortableEntityView timelinePanel, final GowingPackableAttribute attribute, final int requestedMaxWidth ) {

	    return new JLabel( "no support for viewing " + this + " (yet)" );

	}

	public String validate( @NotNull final String s ) {

	    return null;

	}

    },

    /**
     A URL.
     */

    URL {

	public String getDescriptiveName() {

	    return "URL";

	}

	public JComponent getViewComponent( final SortableEntityView timelinePanel, final GowingPackableAttribute attribute, final int requestedMaxWidth ) {

	    return new JLabel( "no support for viewing " + this + " (yet)" );

	}

	public String validate( @NotNull final String s ) {

	    try {

		new java.net.URL( s );

		return null;

	    } catch ( MalformedURLException e ) {

		return "invalid URL";

	    }

	}

    },

    /**
     An image.
     */

    IMAGE {

	public String getDescriptiveName() {

	    return "Image";

	}

	public JComponent getViewComponent( final SortableEntityView timelinePanel, final GowingPackableAttribute attribute, final int requestedMaxWidth ) {

	    return new JLabel( "no support for viewing " + this + " (yet)" );

	}

	private final String[] s_validImageFileSuffixes = { ".jpg", ".jpeg", ".gif", ".png" };

	public String validate( @NotNull final String s ) {

	    String rval = GowingPackableType.URL.validate( s );
	    if ( rval == null ) {

		String sLC = s.toLowerCase();
		for ( String suffix : s_validImageFileSuffixes ) {

		    if ( sLC.endsWith( suffix ) ) {

			return null;

		    }

		}

		return "invalid file suffix (must be one of " + Arrays.toString( s_validImageFileSuffixes ).replace( "[", "" ).replace( "]", "" ) + ")";

	    }

	    return rval;

	}

    },

    /**
     A typically multi-line amount of text.
     */

    HTML_TEXT {

	public String getDescriptiveName() {

	    return "HTML text";

	}

	public JComponent getViewComponent( final SortableEntityView timelinePanel, final GowingPackableAttribute attribute, final int requestedMaxWidth ) {

	    return new JLabel( "no support for viewing " + this + " (yet)" );

	}

	public String validate( @NotNull final String s ) {

	    return null;

	}

    };

    public abstract String getDescriptiveName();

    public abstract String validate( @NotNull String v );

//    public abstract JComponent getViewComponent( SortableEntityView timelinePanel, GowingPackableAttribute attribute, int requestedMaxWidth );

    public boolean isCollection() { return this == GowingPackableType.COLLECTION; }

    public boolean isSimpleValidationSupported() { return this != GowingPackableType.COLLECTION; }

}
