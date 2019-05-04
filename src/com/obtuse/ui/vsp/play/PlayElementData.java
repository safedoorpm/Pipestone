package com.obtuse.ui.vsp.play;

import com.obtuse.ui.vsp.AbstractVirtualScrollablePanelModel;
import com.obtuse.ui.vsp.VirtualScrollableElement;
import com.obtuse.ui.vsp.VirtualScrollableElementModel;
import com.obtuse.ui.vsp.VirtualScrollablePanel;
import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.UniqueId;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 An instance of the things we want to be able to scroll through.
 */

public class PlayElementData implements VirtualScrollableElement {

    private final UniqueId _UniqueId = UniqueId.getJvmLocalUniqueId();
    private final String _value;

    private boolean _isVisible = true;

    public PlayElementData( @NotNull final String value ) {
        super();

        _value = value;

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Kenosee", "Lancot", "Testing" );

        @NotNull Collection<PlayElementData> elementData = new ArrayList<>();
        ArrayList<VirtualScrollableElementModel<PlayElementData>> elementDataModels = new ArrayList<>();

        elementDataModels.add( new PlayElementModel( new PlayElementData( "fred" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "barney" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "wilma" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "betty" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "pebbles" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "bam-bam" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "dino" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "gazoo" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "donald duck" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "bugs bunny" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "popeye" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "huey" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "dewey" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "louie" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "shoe" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "yogi bear" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "tweety bird" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "sylvester" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "mickey mouse" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "charlie brown" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "snoopy" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "linus" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "rocky" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "bullwinkle" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "daffy duck" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "mr. magoo" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "george jetson" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "boris" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "natasha" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "superman" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "batman" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "robin" ) ) );
        elementDataModels.add( new PlayElementModel( new PlayElementData( "wonder woman" ) ) );

        @SuppressWarnings("Convert2MethodRef")
        AbstractVirtualScrollablePanelModel<PlayElementData>
                vspm = new PlayElementPanelModel( elementDataModels );

        VirtualScrollablePanel<PlayElementData> vsPanel = new VirtualScrollablePanel<>( vspm );

        JFrame jf = new JFrame( "Test VirtualScrollablePanel" );
        jf.setMinimumSize( new Dimension( 200, 200 ) );
        jf.setPreferredSize( new Dimension( 800, 600 ) );

        jf.setContentPane( vsPanel );
        jf.pack();
        jf.setVisible( true );

    }

    @Override
    public UniqueId getUniqueId() {

        return _UniqueId;

    }

    public boolean isVisible() {

        return _isVisible;

    }

    @SuppressWarnings("unused")
    public void setVisible( final boolean isVisible ) {

        _isVisible = isVisible;

    }

    public String getValue() {

        return _value;

    }

    public String toString() {

        return "PlayElementData(" +
               " uid=" + _UniqueId.format() + "," +
               " visibility=" + _isVisible + "," +
               " value=" + ObtuseUtil.enquoteToJavaString( _value ) + "" +
               " )";

    }

}
