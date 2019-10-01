package com.obtuse.ui.gpe;

import com.obtuse.ui.layout.flexigrid1.FlexiGridContainer1;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridModelSlice;
import com.obtuse.ui.layout.flexigrid1.model.FlexiGridPanelModel;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 Manage a panel of {@link GenericPanelSlice}s and their associated {@link GenericPanelRowModel}s.
 */

public abstract class GenericPanelManager<SLICE extends GenericPanelSlice> implements GowingPackable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GenericPanelManager.class );

    @SuppressWarnings("FieldCanBeLocal") private static int VERSION = 1;

    private final GowingInstanceId _gowingInstanceId = new GowingInstanceId( getClass() );

    private static final EntityName G_DESCRIPTION = new EntityName( "_desc" );

//    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {
//
//        @Override
//        public int getOldestSupportedVersion() {
//
//            return VERSION;
//
//        }
//
//        @Override
//        public int getNewestSupportedVersion() {
//
//            return VERSION;
//
//        }
//
//        @SuppressWarnings("RedundantThrows")
//        @Override
//        public @NotNull GowingPackable createEntity(
//                @NotNull final GowingUnPacker unPacker,
//                @NotNull final GowingPackedEntityBundle bundle,
//                @NotNull final GowingEntityReference er
//        ) throws GowingUnpackingException {
//
//            return new GenericPanelManager( unPacker, bundle );
//
//        }
//
//    };

    private final List<GenericPanelRowModel<SLICE>> _models = new ArrayList<>();
    private final Set<GenericPanelRowModel<SLICE>> _modelsSet = new HashSet<>();
    private final GenericPanelSliceFactory<SLICE> _sliceFactory;
    private final FlexiGridPanelModel<FlexiGridModelSlice> _model;
    @NotNull private final FlexiGridContainer1 _container;

    protected GenericPanelManager( @NotNull final GenericPanelSliceFactory<SLICE> sliceFactory ) {
        super();

        _sliceFactory = sliceFactory;

        _model = new FlexiGridPanelModel<>( "fg1", FlexiGridPanelModel.Orientation.ROW, false, true );
        _container = _model.getFlexiGridLayoutManager()
                           .getTarget();

    }

    protected GenericPanelManager(
            final GowingUnPacker unPacker,
            final GowingPackedEntityBundle bundle,
            @NotNull final GenericPanelSliceFactory<SLICE> sliceFactory
    ) {
        super();

        _sliceFactory = sliceFactory;

        _model = new FlexiGridPanelModel<>( "fg1", FlexiGridPanelModel.Orientation.ROW, false, true );
        _container = _model.getFlexiGridLayoutManager()
                           .getTarget();

    }

    @Override
    public @NotNull GowingInstanceId getInstanceId() {

        return _gowingInstanceId;

    }

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper,
            @NotNull final GowingPacker packer
    ) {

        // If this is the base class:
        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                packer.getPackingContext()
        );

//        // If this is not the base class and we're not derived from GowingAbstractPackableEntity
//        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
//                ENTITY_TYPE_NAME,
//                VERSION,
//                super.bundleThyself( true, packer ),
//                packer.getPackingContext()
//        );
//
//        // If this is not the base class and we ARE derived from GowingAbstractPackableEntity
//        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
//                ENTITY_TYPE_NAME,
//                VERSION,
//                super.bundleRoot( packer ),
//                packer.getPackingContext()
//        );

        return bundle;

    }

    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) throws GowingUnpackingException {

        return false;

    }

    @Override
    public abstract String toString();
//    {
//
//        return "GenericPanelManager( " + "GenericPanelManager" + " )";
//
//    }

    public <T extends GenericPanelRowModel<SLICE>> void addRow( T model ) {

        // Make sure that the a particular row model doesn't get added more than once.

        if ( _modelsSet.contains( model ) ) {

            String msg =
                    "GenericPanelManager.addRow:  " +
                    "duplicates not allowed (model/row with id=" + model.getId() + " already exists)";
            throw new IllegalArgumentException( msg );

        }

        _modelsSet.add( model );
        _models.add( model );

        @NotNull SLICE slice = model.getRowSlice( _sliceFactory );

        _model.add( slice );

    }

    public GenericPanelRowModel<SLICE> getModel( int ix ) {

        return _models.get( ix );

    }

    public <T extends List<GenericPanelRowModel<SLICE>>> List<GenericPanelRowModel<SLICE>> getAllModels() {

        return _models;

    }

    public Component getRootPane() {

//        if ( _container == null ) {
//
//            throw new HowDidWeGetHereError( "GenericPanelManager.getRootPane:  too early" );
//
//        }

        return _container.getRootPane();

    }

//    public void resyncIfNecessary() {
//
////        if ( _container == null ) {
////
////            _container = new JPanel();
////
////        }
//
//        boolean syncNeeded = false;
//
//        if ( _models.size() == _container.getComponentCount() ) {
//
//            ArrayList<FlexiGridModelSlice> modelSlices = new ArrayList<>( _model.getSlicesInOrder() );
//
//            for ( int ix = 0; ix < _models.size(); ix += 1 ) {
//
//                MODEL currentModel = _models.get( ix );
//                GenericPanelSlice currentSlice = currentModel.getRowSlice( _sliceFactory );
//                if ( currentSlice == null ) {
//
//                    throw new HowDidWeGetHereError( "GenericPanelManager.resyncIfNecessary:  our model #" +
//                                                    ix +
//                                                    " has a null slice" );
//
//                }
//
//                FlexiGridModelSlice slice = modelSlices.get( ix );
//
////                Component ourPanelComponent = _container.getComponent( ix );
//                if ( slice == null ) {
//
//                    throw new HowDidWeGetHereError( "GenericPanelManager.resyncIfNecessary:  JPanel slice #" +
//                                                    ix +
//                                                    " is null" );
//
//                }
//
//                if ( currentSlice != slice ) {
//
//
//                    syncNeeded = true;
//
//                    throw new HowDidWeGetHereError( "GenericPanelManager.resyncIfNecessary:  how can the model be wrong? (#1)" );
//
////                    break;
//
//                }
//
//            }
//
//        } else {
//
//            syncNeeded = true;
//            throw new HowDidWeGetHereError( "GenericPanelManager.resyncIfNecessary:  how can the model be wrong? (#2)" );
//
//        }
//
//    }

    @NotNull
    public JPanel getJPanel() {

//        resyncIfNecessary();

        return _container;

    }

}
