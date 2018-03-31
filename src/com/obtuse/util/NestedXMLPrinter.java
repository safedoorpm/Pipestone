/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Collection;
import java.util.Stack;

@SuppressWarnings("UnusedDeclaration")
public class NestedXMLPrinter implements Closeable {

    private int _nestingLevel = 0;
    private String _currentIndentString = "";
    private boolean _needIndent = true;

    private Stack<String> _tagStack = new Stack<>();

    private final int _indentPerLevel;
    private final PrintStream _ps;
    private final String _perIndentString;

    public NestedXMLPrinter( final int indentPerLevel, final PrintStream ps ) {
        super();

        _indentPerLevel = indentPerLevel;
        _perIndentString = ObtuseUtil.replicate( " ", indentPerLevel );
        _ps = ps;

    }

    public void flush() {

        _ps.flush();

    }

    public void emitOpenTag( final String tagName ) {

        println( "<" + tagName + ">" );
        nest( tagName );

    }

    public void emitOpenTag( final String tagName, final @NotNull String@NotNull[] attributes ) {

        emitTag( tagName, attributes, true );

    }

    public void emitOpenTag( final Class<?> tagClass, final String[] attributes ) {

        emitTag( tagClass.getSimpleName(), attributes, true );

    }

    public void emitOpenTag( final Class<?> tagClass ) {

        emitOpenTag( tagClass.getSimpleName() );

    }

    public void emitTag( final String tagName, final String[] attributes, final boolean leaveOpen ) {

        print( "<" + tagName );

        if ( attributes != null ) {

            for ( String attribute : attributes ) {

                if ( attribute != null ) {

                    print( " " + attribute );

                }

            }

        }

        if ( leaveOpen ) {

            println( ">" );
            nest( tagName );

        } else {

            println( "/>" );

        }

    }

    public void emitTag( final String tagName, final String[] attributes ) {

        emitTag( tagName, attributes, false );

    }

    public void emitTag( final String tagName, final Collection<String> attributes, final boolean leaveOpen ) {

        emitTag( tagName, attributes == null ? null : attributes.toArray( new String[0] ), leaveOpen );

    }

    public void emitTag( final String tagName, final Collection<String> attributes ) {

        emitTag( tagName, attributes, false );

    }

    public void emitTag( final String tagName, final String content ) {

        println( "<" + tagName + ">" + content + "</" + tagName + ">" );

    }

    public void emitTag( final @NotNull String tagName ) {

        emitTag( tagName, (String[])null );

    }

    public void emitTag( final Class<?> tagClass ) {

        emitTag( tagClass.getSimpleName() );

    }

    public void emitCloseTag( final String tagName ) {

        unNest( tagName );
        println( "</" + tagName + ">" );

    }

    public void emitCloseTag( final Class<?> tagClass ) {

        emitCloseTag( tagClass.getSimpleName() );

    }

    public void emitArray( final @NotNull String arrayName, final double@NotNull[] values, final int precision ) {

        emitOpenTag( arrayName );
        for ( double v : values ) {

            emitTag( "item", ObtuseUtil.lpad( v, 0, precision ) );

        }
        emitCloseTag( arrayName );

    }

    public void emitArray( final @NotNull String arrayName, final double@NotNull[] values ) {

        emitOpenTag( arrayName );
        for ( double v : values ) {

            emitTag( "item", "" + v );

        }
        emitCloseTag( arrayName );

    }

    public void emitArray( final @NotNull String arrayName, final long@NotNull[] values ) {

        emitOpenTag( arrayName );
        for ( long v : values ) {

            emitTag( "item", "" + v );

        }
        emitCloseTag( arrayName );

    }

    public void emitArray( final @NotNull String arrayName, final int@NotNull[] values ) {

        emitOpenTag( arrayName );
        for ( int v : values ) {

            emitTag( "item", "" + v );

        }
        emitCloseTag( arrayName );

    }

    public void emitArray( final String arrayName, final boolean@NotNull[] values ) {

        emitOpenTag( arrayName );
        for ( boolean v : values ) {

            emitTag( "item", v ? "T" : "F" );

        }
        emitCloseTag( arrayName );

    }

    public void emitArray( final @NotNull String arrayName, final @NotNull Object@NotNull[] values ) {

        emitOpenTag( arrayName );
        for ( Object v : values ) {

            emitTag( "item", v.toString() );

        }
        emitCloseTag( arrayName );

    }

    public void nest( final @NotNull String tag ) {

        _nestingLevel += 1;
        _tagStack.push( tag );
        _currentIndentString = ObtuseUtil.replicate( _perIndentString, _nestingLevel );

    }

    public void nest() {

        nest( "" );

    }

    public void unNest( final @NotNull String tagName ) {

        String expectedTag = _tagStack.pop();
        if ( expectedTag.equals( tagName ) ) {

            _nestingLevel -= 1;
            _currentIndentString = ObtuseUtil.replicate( "   ", _nestingLevel );

        } else {

            throw new IllegalArgumentException( "attempt to un-nest tag \"" + tagName + "\" when next tag should be \"" + expectedTag + "\"" );

        }

    }

    public void unNest() {

        unNest( "" );

    }

    public void print( final @NotNull String text ) {

        doIndent();
        _ps.print( text );

    }

    public void println( final @NotNull String text ) {

        if ( !text.isEmpty() ) {

            doIndent();

        }

        _ps.println( text );
        _needIndent = true;

    }

    public void doIndent() {

        if ( _needIndent ) {

            _ps.print( _currentIndentString );
            _needIndent = false;

        }

    }

    public int getIndentPerLevel() {

        return _indentPerLevel;

    }

    public void close() {

        _ps.close();

    }

}
