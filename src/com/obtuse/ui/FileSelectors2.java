/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseCollections;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Optional;
import java.util.Set;

/**
 * Some utility methods that make using Swing and AWT file selection dialogs a bit easier.
 * <p/>
 * Possibly of more interest, these also provide examples of how to use the file selection dialogs.
 */

@SuppressWarnings("UnusedDeclaration")
public class FileSelectors2 {

    public static class FileSelectorResult {

        private final File _currentDirectory;
        private final File[] _selection;
        private final int _returnState;

        public FileSelectorResult(
                @Nullable final File currentDirectory,
                @NotNull final File[] selection,
                int returnState
        ) {
            super();

            _currentDirectory = currentDirectory;
            _selection = ObtuseCollections.copyOf( selection );
            _returnState = returnState;

        }

        @NotNull
        public Optional<File> getCurrentDirectory() {

            return Optional.ofNullable( _currentDirectory );

        }

        private void validateReturnState() {

            if (
                    !Set.of(
                            JFileChooser.APPROVE_OPTION,
                            JFileChooser.CANCEL_OPTION,
                            JFileChooser.ERROR_OPTION
                    ).contains( _returnState )
            ) {

                throw new HowDidWeGetHereError( "FileSelectors2:  unexpected return state=" + _returnState );

            }

        }

        @NotNull
        public File[] getSelection() {

            return ObtuseCollections.copyOf( _selection );

        }

        public boolean approved() {

            return _returnState == JFileChooser.APPROVE_OPTION;

        }

        public boolean cancelled() {

            return _returnState == JFileChooser.CANCEL_OPTION;

        }

        public boolean worked() {

            return approved() || cancelled();

        }

        public boolean failed() {

            return !worked();

        }

        /**
         Determine what happened.
         @return {@link JFileChooser#CANCEL_OPTION} if user clicked the "Cancel" button.
         <br>{@link JFileChooser#APPROVE_OPTION} if the user clicked the "Doit" button.
         <br>{@link JFileChooser#ERROR_OPTION} if something went wrong or the selection dialog was dismissed.
         */

        public int getReturnState() {

            return _returnState;

        }

    }

    private FileSelectors2() {
        super();

    }

    /**
     * Use the Swing {@link JFileChooser} to select one or more files (NOT directories).
     * @param parent the component that is requesting this dialog (ignored if null).
     * @param title the title for the dialog window and the approve button text.
     * @param startingDirectory where the game should begin (defaults to the user's home directory if null).
     * @param dialogType the type of dialog (either {@link JFileChooser#OPEN_DIALOG} or {@link JFileChooser#SAVE_DIALOG}).
     * Use {@link #swingSelectFile(Component, String, File, int, boolean, int, String, FileFilter)}
     * if you want a custom dialog.
     * See {@link JFileChooser#setDialogType} for more info.
     * @param multiSelectionEnabled {@code true} if multiple files can be selected in one dialog; {@code false}.
     * @param fileFilter the optional file filter which selects which files should appear in the dialog.
     * @return the selected file if the "approve" button was clicked; null otherwise.
     */

    public static @NotNull FileSelectorResult swingSelectFile(
            final Component parent,
            final String title,
            final File startingDirectory,
            final int dialogType,
            final boolean multiSelectionEnabled,
            final FileFilter fileFilter
    ) {

        return swingSelectFile(
                parent,
                title,
                startingDirectory,
                dialogType,
                multiSelectionEnabled,
                JFileChooser.FILES_ONLY,
                null,
                fileFilter
        );

    }

    /**
     * Use the Swing {@link JFileChooser} to select a file.
     * @param parent the component that is requesting this dialog (ignored if null).
     * @param title the title for the dialog window and the approve button text (ignored if null).
     * @param startingDirectory where the game should begin (defaults to the user's home directory if null).
     * @param dialogType the type of dialog (either {@link JFileChooser#OPEN_DIALOG} or {@link JFileChooser#SAVE_DIALOG}).
     * @param multiSelectionEnabled {@code true} if multiple files can be selected in one dialog; {@code false}.
     * @param fileSelectionMode configure the chooser to select just files, just directories, or both files and directories.
     * @param customButtonLabel the custom button label.
     * <b>MUST</b> be {@code null} if {@code dialogType} is not {@link JFileChooser#CUSTOM_DIALOG}.
     * @param fileFilter the optional file filter which selects which files should appear in the dialog.
     * @return the selected file if the "approve" button was clicked; null otherwise.
     * <p>I don't think that this can return an array with null elements but I'm not sure.
     * Consequently, I've declared it to return an array with elements which might be null.
     * Sorry.</p>
     * @throws IllegalArgumentException if
     * <ol>
     *     <li>{@code customButtonLabel == null && dialogType == JFileChooser.CUSTOM_DIALOG} or</li>
     *     <li>{@code customButtonLabel != null && dialogType != JFileChooser.CUSTOM_DIALOG}</li>
     * </ol>
     */

    public static @NotNull FileSelectorResult swingSelectFile(
            @Nullable final Component parent,
            @Nullable final String title,
            @Nullable final File startingDirectory,
            final int dialogType,
            final boolean multiSelectionEnabled,
            final int fileSelectionMode,
            @Nullable final String customButtonLabel,
            final FileFilter fileFilter
    ) {

        if ( ( dialogType == JFileChooser.CUSTOM_DIALOG ) == ( customButtonLabel == null ) ) {

            if ( dialogType == JFileChooser.CUSTOM_DIALOG ) {

                throw new IllegalArgumentException(
                        "FileSelectors2.swingSelectFile:  dialogType == CUSTOM_DIALOG but customButtonLabel == null "
                );

            } else {

                throw new IllegalArgumentException(
                        "FileSelectors2.swingSelectFile:  dialogType != CUSTOM_DIALOG but customButtonLabel != null "
                );

            }


        }

        JFileChooser chooser = new JFileChooser();

        if ( title != null ) {

            chooser.setDialogTitle( title );

        }

        if ( customButtonLabel != null ) {

            chooser.setApproveButtonText( customButtonLabel );

        }

        chooser.setMultiSelectionEnabled( multiSelectionEnabled );

        if ( startingDirectory != null ) {

            chooser.setCurrentDirectory( startingDirectory );

        }

        if ( fileFilter != null ) {

            chooser.setFileFilter( fileFilter );

        }

        chooser.setFileSelectionMode( fileSelectionMode );
        int chooserRval;
        switch ( dialogType ) {

            case JFileChooser.OPEN_DIALOG:

                chooserRval = chooser.showOpenDialog( parent );
                break;

            case JFileChooser.SAVE_DIALOG:

                chooserRval = chooser.showSaveDialog( parent );
                break;

            case JFileChooser.CUSTOM_DIALOG:

                chooserRval = chooser.showDialog( parent, customButtonLabel );
                break;

            default:

                throw new IllegalArgumentException(
                        "FileSelectors.swingSelectFile:  " +
                        "unknown dialogType " + dialogType
                );

        }

        if ( chooserRval == JFileChooser.APPROVE_OPTION ) {

            // Danny 2018-10-01 after well over an hour of trying to figure out why I didn't always
            // get back the file or files I had selected. It turns out that everything worked as
            // expected if I had more than one file selected but I would sometimes get the selected
            // file and sometimes get an empty array of files when I only had one file selected.
            // It turns out that the times that I was getting the one file was when I had multi-selection
            // mode turned on. The reason that I got no files when I had one file selected with
            // multi-selection mode turned off is because getSelectedFiles (note the 's' at the end
            // of that method name) ALWAYS returns an empty array regardless of what is selected
            // UNLESS multi-selection mode is turned on.
            // The technical term for this setting a trap for the developer.
            // Yes, the behaviour is documented but this sure doesn't seem to respect the principle
            // of least surprise.
            //
            // Grumble. Grumble. Grumble.
            //
            // P.S. I tend to build APIs that force the developer to use them correctly. The difference
            // between my approach and Sun/Oracle's approach is that my APIs tend to get grumpy when used
            // incorrectly (generally by throwing an IllegalArgumentException and maybe even a HowDidWeGetHereError
            // at the caller whereas Sun/Oracle just silently allows the incorrect behaviour to occur
            // without comment/warning/whatever.
            //
            // Grumble. Grumble. Grumble.

            File[] selectedFiles;

            if ( multiSelectionEnabled ) {

                selectedFiles = chooser.getSelectedFiles();

            } else {

                File selectedFile = chooser.getSelectedFile();
                if ( selectedFile == null ) {

                    Logger.logErr(
                            "FileSelectors2.swingSelectFile:  user has approved the selection of NOTHING???"
                    );

                    selectedFiles = new File[0];

                    ObtuseUtil.doNothing();

                } else {

                    selectedFiles = new File[]{ selectedFile };

                    ObtuseUtil.doNothing();

                }

            }

            FileSelectorResult result = new FileSelectorResult(
                    chooser.getCurrentDirectory(),
                    selectedFiles,
                    JFileChooser.APPROVE_OPTION
            );

            return result;

        } else if ( chooserRval == JFileChooser.CANCEL_OPTION ) {

            FileSelectorResult result = new FileSelectorResult(
                    chooser.getCurrentDirectory(),
                    new File[0],
                    JFileChooser.CANCEL_OPTION
            );

            return result;

        } else {

            FileSelectorResult result = new FileSelectorResult(
                    chooser.getCurrentDirectory(),
                    new File[0],
                    JFileChooser.ERROR_OPTION
            );

            return result;

        }

    }

    @NotNull
    public static File[] getSelectedFilesArray( final boolean multiSelectionEnabled, final JFileChooser chooser ) {

        File[] ourRval;
        if ( multiSelectionEnabled ) {

            File[] selectedFiles = chooser.getSelectedFiles();
            if ( selectedFiles == null ) {

                File singleFile = chooser.getSelectedFile();
                if ( singleFile == null ) {

                    ourRval = new File[0];

                } else {

                    ourRval = new File[1];
                    ourRval[0] = singleFile;

                }

            } else {

                ourRval = new File[selectedFiles.length];
                System.arraycopy( selectedFiles, 0, ourRval, 0, selectedFiles.length );

            }

        } else {

            File singleFile = chooser.getSelectedFile();
            if ( singleFile == null ) {

                ourRval = new File[0];

            } else {

                ourRval = new File[1];
                ourRval[0] = singleFile;

            }

        }

        return ourRval;

    }

    /**
     * Use the AWT {@link FileDialog} to select a file.
     * @param parent the {@link Frame} that is requesting this dialog (ignored if null).
     * @param title the title for the dialog window.
     * @param startingDirectory where the game should begin (defaults to the user's home directory if null).
     * @param mode the mode of the dialog.
     * See {@link FileDialog#setMode} for more info.
     * @param filenameFilter the optional file filter which selects which files should appear in the dialog.
     * @return the selected file if the "approve" button was clicked; null otherwise.
     */

    public static File awtSelectFile(
            final Frame parent,
            final String title,
            final File startingDirectory,
            final int mode,
            final FilenameFilter filenameFilter
    ) {

        return FileSelectors2.awtSelectFile( new FileDialog( parent, title, mode ), startingDirectory, filenameFilter );

    }

    /**
     * Use the AWT {@link FileDialog} to select a file.
     * @param parent the {@link Dialog} that is requesting this dialog (ignored if null).
     * @param title the title for the dialog window.
     * @param startingDirectory where the game should begin (defaults to the user's home directory if null).
     * @param mode the mode of the dialog.
     * See {@link FileDialog#setMode} for more info.
     * @param filenameFilter the optional file filter which selects which files should appear in the dialog.
     * @return the selected file if the "approve" button was clicked; null otherwise.
     */

    public static File awtSelectFile(
            final Dialog parent,
            final String title,
            final File startingDirectory,
            final int mode,
            final FilenameFilter filenameFilter
    ) {

        return FileSelectors2.awtSelectFile( new FileDialog( parent, title, mode ), startingDirectory, filenameFilter );

    }

    /**
     * Use the AWT {@link FileDialog} to select a file.
     * <p/>
     * This is a utility method used by the other two awtSelectFile methods to do the real work of selecting a file
     * once the {@link FileDialog} instance has been created.  It is probably not very useful to anyone else but there
     * does not seem to be much harm in making it public.
     * @param dialog the FileDialog which is to be used to select a file.
     * @param startingDirectory where the game should begin (defaults to the user's home directory if null).
     * @param filenameFilter the optional file filter which selects which files should appear in the dialog.
     * @return the selected file if the "approve" button was clicked; null otherwise.
     */

    public static File awtSelectFile(
            final FileDialog dialog,
            final File startingDirectory,
            final FilenameFilter filenameFilter
    ) {

        if ( filenameFilter != null ) {

            dialog.setFilenameFilter( filenameFilter );

        }

        if ( startingDirectory != null ) {

            dialog.setDirectory( startingDirectory.getAbsolutePath() );

        }

        dialog.setVisible( true );

        String fileName = dialog.getFile();
        if ( fileName == null ) {

            return null;

        } else {

            String directory = dialog.getDirectory();

            return new File( new File( directory ), fileName );

        }

    }

}
