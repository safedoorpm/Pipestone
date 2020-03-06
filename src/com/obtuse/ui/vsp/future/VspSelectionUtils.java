package com.obtuse.ui.vsp.future;

/*
 * <p>Copyright © 2020 Obtuse Systems Corporation</p>
 * <p>Created by danny on 2020/01/06.</p>
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.vsp.*;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.UniqueId;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A set of classes which are intended to EVENTUALLY provide a reasonably general purpose robust item selection
 * facility for {@link VirtualScrollablePanel}{@code <E>}s.
 *<p>I am not convinced that this class is relevant anymore. The VSP facility went in a different direction.</p>>
 */

@SuppressWarnings("unused")
public class VspSelectionUtils {

    public static class SelectableFileViewFactory implements ElementView.ElementViewFactory<SelectableFile> {

        @Override
        public SelectableFileView createInstance( @NotNull final VirtualScrollableElementModel<SelectableFile> elementModel ) {

            if ( elementModel  instanceof SelectableFileModel ) {

                return new SelectableFileView( (SelectableFileModel)elementModel );

            } else {

                throw new HowDidWeGetHereError(
                        "VspSelectionUtils.SelectableFileViewFactory.createInstance:  " +
                        "elementModel is not an instance of SelectableFileModel " +
                        "(it is a " + elementModel.getClass() + ")"
                );

            }

        }

    }

    private static class SelectableFileView extends VirtualScrollablePanel.AbstractElementView<SelectableFile> {

        @SuppressWarnings("FieldCanBeLocal") private final JLabel _jLabel;

        protected SelectableFileView( final @NotNull SelectableFileModel elementModel ) {
            super( elementModel, null, elementModel._selectableFile._name );

            setLayout( new BorderLayout() );

            _jLabel = new JLabel( elementModel.getSelectableFile().getFile().getName() );
            add( _jLabel, BorderLayout.WEST );

        }

        @Override
        public void freshAssignment() {

        }

        @Override
        public void fill( @NotNull final VirtualScrollableElementModel<SelectableFile> dataModel ) {

        }

    }

    public static class SelectableFileModel implements VirtualScrollableElementModel<SelectableFile>,
                                                       Comparable<SelectableFileModel> {

        private final UniqueId _uniqueId = UniqueId.getJvmLocalUniqueId();
        private final VirtualScrollablePanelModel<SelectableFile> _panelModel;

        private final SelectableFile _selectableFile;

        private SelectableFileModel(
                @NotNull FileSelectorPanelModel panelModel,
                @NotNull SelectableFile selectableFile
        ) {
            super();

            _panelModel = panelModel;
            _selectableFile = selectableFile;

        }

        @Override
        public UniqueId getUniqueId() {

            return _uniqueId;
        }

        @Override
        public boolean isVisible() {

            return false;
        }

        @Override
        public @NotNull VirtualScrollablePanelModel<SelectableFile> getMandatoryPanelModel() {

            return _panelModel;

        }

        public SelectableFile getSelectableFile() {

            return _selectableFile;

        }

        @Override
        public int compareTo( @NotNull final SelectableFileModel o ) {

            return _selectableFile.compareTo( o._selectableFile );

        }

        public int hashCode() {

            return _selectableFile._hashCode;

        }

        public boolean equals( Object rhs ) {

            return rhs instanceof SelectableFileModel &&
                   _selectableFile.equals( ((SelectableFileModel)rhs)._selectableFile );

        }

        public String toString() {

            return "SelectableFileModel( " + _selectableFile + " )";

        }

    }

    public static class SelectableFile implements VirtualScrollableElement, Comparable<SelectableFile> {

        private final File _file;

        private final String _name;

        private final int _hashCode;

        private final UniqueId _uniqueId = UniqueId.getJvmLocalUniqueId();

        private SelectableFile( File file ) {

            super();

            _file = file;

            _name = file.getName();

            _hashCode = _name.hashCode();

        }

        @NotNull
        public File getFile() {

            return _file;

        }

        @Override
        public UniqueId getUniqueId() {

            return _uniqueId;

        }

        @Override
        public int compareTo( @NotNull final SelectableFile o ) {

            return _name.compareTo( o._name );

        }

        public int hashCode() {

            return _hashCode;

        }

        public boolean equals( Object rhs ) {

            return rhs instanceof SelectableFile && _name.equals(((SelectableFile)rhs)._name );

        }

        public String toString() {

            return "SelectableFile( " + ObtuseUtil.enquoteJavaObject( _name ) + " )";

        }

    }

    private static class FileSelectorPanelModel extends AbstractVirtualScrollablePanelModel<SelectableFile> {

        @NotNull
        private File _currentDirectory;
        @SuppressWarnings("FieldCanBeLocal") private File[] _latestFilesArray;
        @SuppressWarnings("FieldCanBeLocal") private SortedMap<SelectableFileModel, SelectableFileModel>
                _latestFilesSortedMap;

        private final VirtualScrollablePanel.VirtualScrollablePanelSelectionManager _selectionManager =
                new BasicVirtualScrollablePanelSelectionManager<>( this );

        public FileSelectorPanelModel(
                @NotNull File currentDirectory
                /*@NotNull  final ElementView.ElementViewFactory<SelectableFile>
        elementViewFactory*/
        ) {
            super( new SelectableFileViewFactory() );

            _currentDirectory = currentDirectory;

        }

        @SuppressWarnings("unused")
        public void setCurrentDirectory( @NotNull final File newCurrentDirectory ) {

            _currentDirectory = newCurrentDirectory;

        }

        @Override
        public @NotNull CurrentGoals<SelectableFile> getActualCurrentGoals(
                final int firstVisibleElementIx, final @NotNull Dimension viewportSize
        ) {

            _latestFilesArray = _currentDirectory.listFiles();
            _latestFilesSortedMap = new TreeMap<>();
            if ( _latestFilesArray != null ) {

                for ( File f : _latestFilesArray ) {

                    SelectableFileModel
                            selectableFileModel = new SelectableFileModel( this, new SelectableFile( f ) );
                    _latestFilesSortedMap.put(
                            selectableFileModel,
                            selectableFileModel
                    );

                }

            }

            CurrentGoals<SelectableFile> currentGoals = new CurrentGoals<>(
                    firstVisibleElementIx,
                    0,
                    _latestFilesSortedMap.size(),
                    new ArrayList<>( _latestFilesSortedMap.values() )
            );

            return currentGoals;

        }

        @SuppressWarnings("unused")
        public Optional<VirtualScrollablePanel.VirtualScrollablePanelSelectionManager> getOptSelectionManager() {

            return Optional.of( _selectionManager );

        }

        @Override
        public boolean checkForUpdates() {

            return false;

        }

        @Override
        public void startNewElementViewAllocationRound() {

        }

    }

}
