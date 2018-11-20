package com.obtuse.ui.vsp.play;

import com.obtuse.ui.MyActionListener;
import com.obtuse.ui.vsp.VirtualScrollableElement;
import com.obtuse.ui.vsp.VirtualScrollableElementModel;
import com.obtuse.ui.vsp.VirtualScrollablePanel;
import com.obtuse.util.UniqueID;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 Created by danny on 2018/11/19.
 */
public class PlayElementView<EV extends VirtualScrollableElement>
        extends VirtualScrollablePanel.AbstractElementView<PlayElementData> {

    private final JCheckBox _visibleCheckBox = new JCheckBox();
    private final JLabel _jLabel = new JLabel();

//        private boolean _recycled;

    public PlayElementView(
            @NotNull final UniqueID id,
            VirtualScrollableElementModel<PlayElementData> elementModel
    ) {

        super( id, elementModel );

        setBorder( BorderFactory.createEtchedBorder() );

        setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );

        PlayElementModel playElementModel = (PlayElementModel)elementModel;

        add( _visibleCheckBox );
        _visibleCheckBox.addActionListener(
                new MyActionListener() {

                    @Override
                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                        playElementModel.setVisible( _visibleCheckBox.isSelected() );

                    }

                }
        );
        add( _jLabel );

    }

//        public PlayElementView getAsView() {
//
//            return this;
//
//        }

    @Override
    public void fill( final VirtualScrollableElementModel rawDataModel ) {

        PlayElementModel dataModel = (PlayElementModel)rawDataModel;

        _visibleCheckBox.setSelected( dataModel.isVisible() );
        _jLabel.setText( dataModel.getElementData()
                                  .getValue() );

    }

//        @Override
//        public void fill( final VirtualScrollableElementModel dataModel ) {
//
//        }

}
