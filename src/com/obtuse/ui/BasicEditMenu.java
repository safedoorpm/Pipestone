package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 Created by danny on 2019/04/08.
 */
public class BasicEditMenu extends JMenu {

    private JMenuItem _cutMenuItem;
    private JMenuItem _copyMenuItem;
    private JMenuItem _pasteMenuItem;
    private JMenuItem _selectAllMenuItem;

    public BasicEditMenu( final @NotNull String menuName ) {

        super( menuName );

    }

    public JMenuItem getCutMenuItem() {

        return _cutMenuItem;

    }

    public void setCutMenuItem( final JMenuItem cutMenuItem ) {

        if ( _cutMenuItem != null ) {

            remove( _cutMenuItem );

        }

        _cutMenuItem = cutMenuItem;
        add( cutMenuItem );

    }

    public JMenuItem getCopyMenuItem() {

        return _copyMenuItem;

    }

    public void setCopyMenuItem( final JMenuItem copyMenuItem ) {

        if ( _copyMenuItem != null ) {

            remove( _copyMenuItem );

        }

        _copyMenuItem = copyMenuItem;
        add( copyMenuItem );

    }

    public JMenuItem getPasteMenuItem() {

        return _pasteMenuItem;

    }

    public void setPasteMenuItem( final JMenuItem pasteMenuItem ) {

        if ( _pasteMenuItem != null ) {

            remove( _pasteMenuItem );

        }

        _pasteMenuItem = pasteMenuItem;
        add( pasteMenuItem );

    }

    public JMenuItem getSelectAllMenuItem() {

        return _selectAllMenuItem;

    }

    public void setSelectAllMenuItem( final JMenuItem selectAllMenuItem ) {

        if ( _selectAllMenuItem != null ) {

            remove( _selectAllMenuItem );

        }

        _selectAllMenuItem = selectAllMenuItem;
        add( selectAllMenuItem );

    }

    public String toString() {

        return "BasicEditMenu()";

    }

}
