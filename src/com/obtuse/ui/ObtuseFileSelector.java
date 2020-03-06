package com.obtuse.ui;

import com.obtuse.ObtuseConstants;
import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.ui.layout.LinearOrientation;
import com.obtuse.ui.layout.linear.LinearContainer;
import com.obtuse.ui.layout.linear.LinearLayoutManager3;
import com.obtuse.ui.layout.linear.LinearLayoutUtil;
import com.obtuse.ui.layout.play.ObtuseFlowLayoutManager;
import com.obtuse.util.*;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

/*
 * Copyright © 2020 Obtuse Systems Corporation
 */

@SuppressWarnings("FieldCanBeLocal")
public class ObtuseFileSelector extends JDialog {

    public static final boolean MIXED_CASE_FILESYSTEM;

    static {

        if ( !BasicProgramConfigInfo.isInitialized() ) {

            BasicProgramConfigInfo.init(
                    "Obtuse",
                    "Pipestone",
                    "ObtuseFileSelector"
            );

        }

    }

    static {

        Random rng = new Random( System.currentTimeMillis() );

        // Start with a mixed case name to ensure that we don't accidentaly create a single case name.
        // It is statistically impossible for us to do so but let's be paranoid.

        StringBuilder checkFnameBuilder = new StringBuilder( "OfsTestFile-" );
        String fileChars =
                "abcdefghijklmnopqrxsuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // Guess that we are on a case-sensitive filesystem.

        boolean isMixedCaseFileSystem = false;
        boolean haveAnswer = false;

        for ( int ix = 0; ix < 100; ix += 1 ) {

            char ch = fileChars.charAt( rng.nextInt( fileChars.length() ) );
            checkFnameBuilder.append( ch );

            File homeDir = new File( System.getProperty( "user.home" ) );
            File randomCaseFile = new File( homeDir, checkFnameBuilder.toString() );
            File lcName = new File( homeDir, checkFnameBuilder.toString().toLowerCase() );
            File ucName = new File( homeDir, checkFnameBuilder.toString().toUpperCase() );
            if ( ix >= 20 ) {

                // If any of our test files exist then it is too risky to run the experiment now - spin again.

                if (
                        randomCaseFile.exists() ||
                        lcName.exists() ||
                        ucName.exists()
                ) {

                    continue;

                }

                // Create a file using the random case name.

                try {

                    if ( randomCaseFile.createNewFile() ) {

                        // Feeling paranoid so let's be CERTAIN that the file exists via the random name.

                        if ( randomCaseFile.exists() ) {

                            // If the file also exists via both the lower and upper case names
                            // then we are on a mixed-case filesystem.

                            //noinspection RedundantIfStatement
                            if ( lcName.exists() && ucName.exists() ) {

                                isMixedCaseFileSystem = true;

                            } else {

                                //noinspection ConstantConditions
                                isMixedCaseFileSystem = false;

                            }

                            haveAnswer = true;

                            break;

                        }

                        break;

                    }

                } catch ( IOException e ) {

                    Logger.logErr( "java.io.IOException caught", e );

                    ObtuseUtil.doNothing();

                } finally {


                    //noinspection ResultOfMethodCallIgnored
                    randomCaseFile.delete();

                }

            }

        }

        if ( !haveAnswer ) {

            throw new HowDidWeGetHereError( "ObtuseFileSelector:  cannot figure out if we are on a mixed-case filesystem" );

        }

        MIXED_CASE_FILESYSTEM = isMixedCaseFileSystem;

        ObtuseUtil.doNothing();

    }

    private static final ImageIcon s_folderClosedIcon =
            ImageIconUtils.fetchMandatoryIcon(
                    "folder_closed_16x16.png",
                    0,
                    ObtuseConstants.OBTUSE_RESOURCES_DIRECTORY
            );
    private static final ImageIcon s_documentIcon =
            ImageIconUtils.fetchMandatoryIcon(
                    "document_16x16.png",
                    0,
                    ObtuseConstants.OBTUSE_RESOURCES_DIRECTORY
            );

    private static final Color DISABLED_BG_COLOUR = new Color( 240, 240, 240 );
    private static final Color DISABLED_FG_COLOUR = new Color( 128, 128, 128 );

    public class FileLineRenderer extends JLabel implements ListCellRenderer<File> {


        int _maxWidth = 0;
        /**
         A simple cell renderer.

         @param list         the {@link JList}{@code <?>} being rendered.
         @param f            the {@link File} being rendered.
         @param index        the 0-origin index of the {@code File} being rendered.
         @param isSelected   {@code true} if the {@code File} is currently selected; {@code false} otherwise.
         @param cellHasFocus {@code true} if the {@link File} has focus; {@code false} otherwise.
         @return the rendered cell (as a {@link JLabel} with an icon indicating folder vs other in this
         implementation).
         */

        @Override
        public Component getListCellRendererComponent(
                JList<? extends File> list,
                File f,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {

            if ( "..".equals( f.getName() ) ) {

                setText(
                        "<html>.. " +
                        "&laquo;<i>actual parent directory&raquo;</i>" +
                        "</html>"
                );

            } else if ( ".".equals( f.getName() ) ) {

                setText(
                        "<html>. " +
                        "&laquo;<i>this directory&raquo;</i>" +
                        "</html>"
                );

            } else {

                setText( f.getName() );

            }

            if ( f.isDirectory() ) {

                setIcon( s_folderClosedIcon );

            } else {

                setIcon( s_documentIcon );

            }

            if ( f.getName().endsWith( "png" ) ) {

                ObtuseUtil.doNothing();

            }

            if ( _listElementSelectable[index] || f.isDirectory() ) {

                if ( isSelected ) {

                    setBackground( list.getSelectionBackground() );
                    setForeground( list.getSelectionForeground() );

                } else {

                    setBackground( list.getBackground() );
                    setForeground( list.getForeground() );

                }

                setEnabled( true );

            } else {

                setBackground( DISABLED_BG_COLOUR );
                setForeground( DISABLED_FG_COLOUR );

                setEnabled( false );

            }

            setFont( list.getFont() );
            setOpaque( true );

            if ( getPreferredSize().width > _maxWidth ) {

                _maxWidth = getPreferredSize().width;

            }

            return this;

        }

        public void resetMaxWidth() {

            _maxWidth = 0;

        }

        public int getMaxWidth() {

            return _maxWidth;

        }

        public String toString() {

            return "FileLineRenderer( maxWidth=" + _maxWidth + " )";

        }

    }

    private final LinearContainer _linearContentContainer;
    private final LinearContainer _createDirectoryPanelContainer;
    private final JPanel _contentPane;
    private final JButton _selectButton;
    private final JButton _cancelButton;
    private final JButton _packButton;
    private final JButton _homeButton;
    private final JButton _createDirectoryButton;
    private final LinearLayoutUtil.SpaceSponge _spaceSponge;
    private final JScrollPane _scrollableFileListPanel;
    private final JList<File> _fileJList;
    private final JLabel _currentDirectoryNameLabel;
    private final JLabel _errmsgLabel;
    private final JPanel _upButtons;
    private final JPanel _createDirectoryPanel;
    private final JTextField _newDirectoryNameField;
    private boolean[] _listElementSelectable;

    private boolean _createDirectoryEnabled;
    private boolean _directoryHasBeenSet = false;
    private boolean _readyForUse = false;

    private File[] _currentDirectoryContents;

    private File _currentDirectory;

    private FileFilter _fileFilter;

    private final FileLineRenderer _fileLineRenderer;

    private DefaultListModel<File> _fileJListModel = new DefaultListModel<>();

    public ObtuseFileSelector(
            @Nullable final Window owner,
            final boolean modal,
            @MagicConstant(valuesFromClass = ListSelectionModel.class) int selectionMode
    ) {

        super( modal ? owner : null, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS );

        _linearContentContainer = LinearLayoutUtil.createPanel3( "obtuseFileSelector", LinearOrientation.VERTICAL );
        _contentPane = _linearContentContainer.getAsJPanel();

        _currentDirectoryNameLabel = new JLabel();
        _upButtons = new JPanel();
        _upButtons.setBorder( BorderFactory.createEtchedBorder() );
        _upButtons.setName( "upButtons" );
        _upButtons.setLayout( new ObtuseFlowLayoutManager( 5, 0 ) );
        _contentPane.add( _upButtons );

        _spaceSponge = new LinearLayoutUtil.SpaceSponge();
        _spaceSponge.setLayout( new BorderLayout() );
        _scrollableFileListPanel = new JScrollPane(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
        );
        _scrollableFileListPanel.setBorder( BorderFactory.createEtchedBorder() );

        _spaceSponge.add( _scrollableFileListPanel, BorderLayout.CENTER );

        _fileJList = new JList<>();
        _fileJList.setFixedCellHeight( 18 );

        _scrollableFileListPanel.setViewportView( _fileJList );
        _contentPane.add( _spaceSponge );

        _createDirectoryPanelContainer = LinearLayoutUtil.createPanel3( "obtuseFileSelector", LinearOrientation.HORIZONTAL );
        _createDirectoryPanel = _createDirectoryPanelContainer.getAsJPanel();
        JLabel createDirectoryPromptJLabel = new JLabel( "Directory name:" );
        _createDirectoryPanel.add( createDirectoryPromptJLabel, LinearLayoutManager3.TRACK_PARENTS_BREADTH_CONSTRAINT );
        _newDirectoryNameField = new JTextField();
        LinearLayoutUtil.SpaceSponge newDirectoryNameSponge = new LinearLayoutUtil.SpaceSponge();
        newDirectoryNameSponge.setLayout( new BorderLayout() );
        newDirectoryNameSponge.add( _newDirectoryNameField, BorderLayout.CENTER );
        _createDirectoryPanel.add( newDirectoryNameSponge );
        _createDirectoryButton = new JButton( "Create" );
        _createDirectoryButton.addActionListener(
                new MyActionListener() {

                    @Override
                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                        doCreateDirectory();

                    }

                }
        );
        _createDirectoryPanel.add( _createDirectoryButton, LinearLayoutManager3.TRACK_PARENTS_BREADTH_CONSTRAINT );
        _contentPane.add( _createDirectoryPanel );
        enableCreateDirectory( false );
        _errmsgLabel = new JLabel();
        _contentPane.add( _errmsgLabel );

        JPanel bottomButtonsPanel = new JPanel();
        bottomButtonsPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );

        _homeButton = new JButton( "Home" );
        _homeButton.addActionListener(
                new MyActionListener() {

                    @Override
                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                        setDirectory( new File( System.getProperty( "user.home" ) ) );

                    }

                }
        );

        bottomButtonsPanel.add( _homeButton );

        JButton blorkButton = new JButton( "Blork" );
        blorkButton.addActionListener(
                new MyActionListener() {
                    @Override
                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                        if ( _errmsgLabel.getText().isEmpty() ) {

                            _errmsgLabel.setText( "This is a fairly long message to see what happens when we have fairly long messages." );

                        } else {

                            _errmsgLabel.setText( "" );

                        }
                    }
                }
        );

        bottomButtonsPanel.add( blorkButton );

        bottomButtonsPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
        _packButton = new JButton( "Pack" );
        _packButton.addActionListener(
                new MyActionListener() {

                    @Override
                    protected void myActionPerformed( final ActionEvent actionEvent ) {

                        _errmsgLabel.setText( "~" );
                        pack();
                        _errmsgLabel.setText( "" );

                    }

                }
        );

        bottomButtonsPanel.add( _packButton );

        _selectButton = new JButton( "Select" );
        bottomButtonsPanel.add( _selectButton );

        _cancelButton = new JButton( "Cancel" );
        bottomButtonsPanel.add( _cancelButton );

        _contentPane.add( bottomButtonsPanel );

        JPanel intermediary = new JPanel();
        intermediary.setBorder( BorderFactory.createEmptyBorder( 10, 10, 0, 10 ) );
        intermediary.setLayout( new BorderLayout() );
        intermediary.add( _contentPane, BorderLayout.CENTER );
        setContentPane( intermediary );
        setModal( modal );

        getRootPane().setDefaultButton( _selectButton );

        _scrollableFileListPanel.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );

        _fileLineRenderer = new FileLineRenderer();
        _fileJList.setCellRenderer( _fileLineRenderer );

        _fileJList.setModel( _fileJListModel );
        _fileJList.setSelectionModel( new DefaultListSelectionModel() );

        // Make sure that the "Select" button is only enabled if at least one thing has been selected.

        ListSelectionModel listSelectionModel = _fileJList.getSelectionModel();
        listSelectionModel.setSelectionMode( selectionMode );
        listSelectionModel.addListSelectionListener(
                e -> {

                    @NotNull List<File> narrowedSelection = narrowSelection();

                    if ( narrowedSelection.isEmpty() ) {

                        _selectButton.setEnabled( false );

                    } else {

                        _selectButton.setEnabled( true );

                    }

                }
        );

        _newDirectoryNameField.getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate( final DocumentEvent e ) {

                        checkNewDirectoryNameField();

                    }

                    @Override
                    public void removeUpdate( final DocumentEvent e ) {

                        checkNewDirectoryNameField();

                    }

                    @Override
                    public void changedUpdate( final DocumentEvent e ) {

                        checkNewDirectoryNameField();

                    }

                }
        );

        _newDirectoryNameField.addFocusListener(
                new FocusListener() {

                    @Override
                    public void focusGained( final FocusEvent e ) {

                        getRootPane().setDefaultButton( _createDirectoryButton );

                    }

                    @Override
                    public void focusLost( final FocusEvent e ) {

                        getRootPane().setDefaultButton( _selectButton );

                    }

                }
        );

        checkNewDirectoryNameField();

        setErrMsg( "~" );

        _selectButton.addActionListener(
                new MyActionListener() {

                    public void myActionPerformed( ActionEvent e ) {

                        onSelect();

                    }

                }
        );

        _cancelButton.addActionListener(
                new MyActionListener() {

                    public void myActionPerformed( ActionEvent e ) {

                        onCancel();

                    }

                }
        );

        // call onCancel() when cross is clicked

        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing( WindowEvent e ) {

                        onCancel();
                    }
                }
        );

        // call onCancel() on ESCAPE

        _contentPane.registerKeyboardAction(
                new MyActionListener() {
                    public void myActionPerformed( ActionEvent e ) {

                        onCancel();
                    }
                },
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        _fileJList.addMouseListener(
                new MouseAdapter() {

                    public void mouseClicked( MouseEvent e ) {

                        if ( Clicks.isLeftClick( e ) && e.getClickCount() == 2 ) {

                            int index = _fileJList.locationToIndex( e.getPoint() );
                            File clickedFile = _currentDirectoryContents[index];
                            System.out.println(
                                    "Double clicked on " +
                                    ( clickedFile.isDirectory() ? "directory" : "file" ) +
                                    ObtuseUtil.enquoteJavaObject( clickedFile ) + " @ " + index
                            );

                            if ( clickedFile.isDirectory() ) {

                                ObtuseUtil.doNothing();

                                setDirectory( new File( _currentDirectory, clickedFile.getName() ) );

                                ObtuseUtil.doNothing();

                            }

                        }

                    }

                }
        );

    }

    private void checkNewDirectoryNameField() {

        if ( _newDirectoryNameField.getText().isEmpty() ) {

            _createDirectoryButton.setEnabled( false );

        } else {

            _createDirectoryButton.setEnabled( true );

        }

    }

    private void doCreateDirectory() {

        String newDirectoryName = _newDirectoryNameField.getText();
        File newDirectory = new File( getCurrentDirectory(), newDirectoryName );

        if ( newDirectory.exists() ) {

            setErrMsg( "directory " + ObtuseUtil.enquoteToJavaString( newDirectoryName ) + " already exists" );

        } else {

            if ( newDirectory.mkdirs() ) {

                setDirectory( getCurrentDirectory() );

                makeEntryVisible( newDirectory, true );


            } else {

                setErrMsg( "cannot create directory " + ObtuseUtil.enquoteToJavaString( newDirectoryName ) );

            }

        }

    }

    private void makeEntryVisible(
            final File entryName,
            @SuppressWarnings("SameParameterValue") final boolean makeEntrySelected
    ) {

        Comparator<String> c = MIXED_CASE_FILESYSTEM ? String::compareToIgnoreCase : String::compareTo;

        String newDirectoryName = entryName.getName();

        ObtuseUtil.doNothing();

        for ( int ix = 0; ix < _fileJListModel.size(); ix += 1 ) {

            File thisEntry = _fileJListModel.get( ix );
            if ( c.compare( newDirectoryName, thisEntry.getName() ) == 0 ) {

                _fileJList.ensureIndexIsVisible( ix );

                if ( makeEntrySelected ) {

                    _fileJList.clearSelection();
                    _fileJList.setSelectedIndex( ix );

                }

                return;

            }

        }

        ObtuseUtil.doNothing();

    }

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    public ObtuseFileSelector setFileFilter( @Nullable FileFilter fileFilter ) {

        if ( _fileFilter != fileFilter ) {

            _fileFilter = fileFilter;

            setDirectory( _currentDirectory );

        }

        return this;

    }

    @NotNull
    public ObtuseFileSelector enableCreateDirectory( final boolean enableCreateDirectory ) {

        _createDirectoryEnabled = enableCreateDirectory;
        _createDirectoryPanel.setVisible( enableCreateDirectory );

        return this;

    }

    @SuppressWarnings("unused")
    public boolean isCreateDirectoryEnabled() {

        return _createDirectoryEnabled;

    }

    @SuppressWarnings("unused")
    @NotNull
    public Optional<FileFilter> getOptFileFilter() {

        return Optional.ofNullable( _fileFilter );

    }

    @NotNull
    public ObtuseFileSelector run() {

        if ( !_directoryHasBeenSet ) {

            throw new IllegalArgumentException(
                    "ObtuseFileSelector.run:  current directory has not yet been set"
            );

        }

        setMinimumSize( new Dimension( 300, 400 ) );
        setMaximumSize( new Dimension( 32767, 32767 ) );

        _errmsgLabel.setText( "~" );
        pack();
        _errmsgLabel.setMinimumSize( new Dimension( 0, _errmsgLabel.getHeight() ) );
        _errmsgLabel.setPreferredSize( new Dimension( 0, _errmsgLabel.getHeight() ) );
        _errmsgLabel.setText( "" );

        _readyForUse = true;

        return resume();

    }

    @NotNull
    public ObtuseFileSelector resume() {

        if ( _readyForUse ) {

            setVisible( true );

            return this;

        } else {

            return run();

        }

    }

    private @NotNull List<File> narrowSelection() {

        return narrowSelection( _fileJList.getSelectionModel() );

    }

    @NotNull
    private List<File> narrowSelection( final ListSelectionModel lsm ) {

        List<File> ns = new ArrayList<>();
        int[] selectedIndices = lsm.getSelectedIndices();
        for ( int selectedIndex : selectedIndices ) {

            if ( _listElementSelectable[selectedIndex] ) {

                // Get the selected destination (could be something like /a/b/c/d/. or /a/b/c/d/..).

                File selection = _fileJListModel.getElementAt( selectedIndex );

                // Transform any selection that ends in either /.. or /. into equivalent canonical paths.

                if ( "..".equals( selection.getName() ) || ".".equals( selection.getName() ) ) {

                    try {

                        selection = selection.getCanonicalFile();

                    } catch ( IOException e ) {

                        Logger.logErr(
                                "ObtuseFileSelector.narrowSelection:  " +
                                "IOException caught getting canonical file for " +
                                ObtuseUtil.enquoteJavaObject( selection ) +
                                " - using non-canonical version",
                                e
                        );

                        ObtuseUtil.doNothing();

                    }

                }

                ns.add( selection );

            }

        }

        return ns;

    }

    public ObtuseFileSelector setDirectory( @NotNull File dir ) {

        // Someone has at least tried.

        _directoryHasBeenSet = true;

        setErrMsg( "~" );
        Logger.logMsg( "we've been asked to set directory to " +
                       ObtuseUtil.enquoteJavaObject( dir.getAbsoluteFile() ) );
        File newCurrentDirectory;

        _fileLineRenderer.resetMaxWidth();

        if ( ".".equals( dir.getName() ) || "..".equals( dir.getName() ) ) {

            try {

                newCurrentDirectory = dir.getCanonicalFile();

                ObtuseUtil.doNothing();

            } catch ( IOException e ) {

                Logger.logErr( "java.io.IOException caught", e );

                setErrMsg( "Unable to canonicalize " + ObtuseUtil.enquoteJavaObject( dir ) );

                return this;

            }

            ObtuseUtil.doNothing();

        } else {

            newCurrentDirectory = new File( dir.getPath() );

            ObtuseUtil.doNothing();

        }

        if ( !newCurrentDirectory.exists() ) {


            setErrMsg( ObtuseUtil.enquoteJavaObject( newCurrentDirectory ) + " does not exist" );

            return this;

        } else if ( !newCurrentDirectory.isDirectory() ) {

            setErrMsg( ObtuseUtil.enquoteJavaObject( newCurrentDirectory ) + " is not a directory" );

            return this;

        }

        File[] unsortedContents = newCurrentDirectory.listFiles();
        if ( unsortedContents == null ) {

            setErrMsg( "unable to list files in " + ObtuseUtil.enquoteJavaObject( newCurrentDirectory ) );

            return this;

        }

        _fileJListModel.clear();

        _selectButton.setEnabled( false );

        SortedSet<File> sortedDirList = new TreeSet<>();
        SortedSet<File> sortedFileList = new TreeSet<>();
        for ( File f : unsortedContents ) {

            if ( f.isDirectory() ) {

                if ( !"..".equals( f.getName() ) ) {

                    sortedDirList.add( f );

                }

            } else {

                sortedFileList.add( f );

            }

        }

        List<File> sortedContents = new ArrayList<>();
        sortedContents.add( new File( newCurrentDirectory, "." ) );
        sortedContents.add( new File( newCurrentDirectory, ".." ) );
        sortedContents.addAll( sortedDirList );
        sortedContents.addAll( sortedFileList );
        _currentDirectoryContents = sortedContents.toArray( new File[0] );
        _listElementSelectable = new boolean[sortedContents.size()];

        int ix = 0;
        for ( File f : sortedContents ) {

            _fileJListModel.addElement( f );
            if ( _fileFilter == null ) {

                _listElementSelectable[ix] = true;

            } else //noinspection RedundantIfStatement
                if ( _fileFilter.accept( f ) ) {

                _listElementSelectable[ix] = true;

            } else {

                _listElementSelectable[ix] = false;

            }

            ix += 1;

        }

        _currentDirectory = newCurrentDirectory;

        _upButtons.removeAll();

        for ( File cd = _currentDirectory; cd != null; cd = cd.getParentFile() ) {

            String nakedName = cd.getName() + "/";
            String quotedName = ObtuseUtil.enquoteJavaObject( nakedName );
            JLabel upButton = new JLabel( quotedName ) {

                public String toString() {

                    return "upButton(" + quotedName + ")";

                }

            };

            upButton.setIconTextGap( 0 );
            upButton.setHorizontalTextPosition( SwingConstants.LEFT );
            File constantCd = cd;

            ImageButton.makeImageButton(
                    new ImageButtonOwner() {
                        @Override
                        public void setButtonStates() {

                            ObtuseUtil.doNothing();

                        }

                        @Override
                        public void setCursor( final Cursor predefinedCursor ) {

                            ObtuseUtil.doNothing();

                        }
                    },
                    "move to " + ObtuseUtil.enquoteJavaObject( constantCd ),
                    upButton,
                    () -> setDirectory( constantCd ),
                    ImageButton.makeTextImageIcon(
                            nakedName,
                            Color.BLUE,
                             getBackground(),
                            upButton.getFont()
                                    .getSize2D(),
                            0
                    ),
                    .7f
            );

            _upButtons.add( upButton, 0 );

        }

        // We have to do a repaint here because the path being displayed could be shorter than
        // it was previously and (it would seem that) nothing less than a repaint will ensure
        // that the entire old path will get erased before this new path is painted.

        _upButtons.repaint();
        _upButtons.revalidate();

        ObtuseUtil.doNothing();

        _currentDirectoryNameLabel.setText( "Hello " /*+ System.currentTimeMillis()*/ );

        _fileJList.ensureIndexIsVisible( 0 );

        return this;

    }

    @SuppressWarnings("unused")
    @NotNull
    public File getCurrentDirectory() {

        return _currentDirectory;

    }

    public void setErrMsg( @NotNull final String errmsg ) {

        _errmsgLabel.setText( errmsg );

    }

    @NotNull
    public Optional<List<File>> getOptMultiSelection() {

        @NotNull List<File> currentSelection = narrowSelection();
        if ( currentSelection.isEmpty() ) {

            return Optional.empty();

        } else {

            return Optional.of( currentSelection );

        }

    }

    @NotNull
    public Optional<File> getOptSingleSelection() {

        Optional<List<File>> optMultiSelection = getOptMultiSelection();

        if ( optMultiSelection.isEmpty() ) {

            return Optional.empty();

        } else {

            List<File> multiSelection = optMultiSelection.get();
            if ( multiSelection.size() == 1 ) {

                return Optional.of( multiSelection.get( 0 ) );

            } else {

                throw new IllegalArgumentException(
                        "ObtuseFileSelector.getOptSingleSelection:  " +
                        "actual selection contains " +
                        ObtuseUtil.pluralize( multiSelection.size(), "selection" )
                );

            }

        }

    }

    private void onSelect() {

        setVisible( false );

        dispose();

    }

    private void onCancel() {

        setVisible( false );

        _fileJList.getSelectionModel()
                  .clearSelection();

        dispose();

    }

    public static void main( String[] args ) {

        boolean useJFileChooser = false;
        //noinspection ConstantConditions
        if ( useJFileChooser ) {

            JFrame jf = new JFrame( "Swing Chooser" );
            JButton jb = new JButton( "Launch File Chooser" );
            jf.setContentPane( jb );
            jf.pack();

            jb.addActionListener(
                    new MyActionListener() {

                        @Override
                        protected void myActionPerformed( final ActionEvent actionEvent ) {

                            File canonicalFile = null;
                            try {

                                canonicalFile = new File( "." ).getCanonicalFile();

                            } catch ( IOException e ) {

                                Logger.logErr( "java.io.IOException caught", e );

                                ObtuseUtil.doNothing();

                            }

                            FileSelectors2.FileSelectorResult result = FileSelectors2.swingSelectFile(
                                    null,
                                    "Please choose wisely",
                                    canonicalFile,
                                    JFileChooser.OPEN_DIALOG,
                                    true,
                                    new FileFilter() {
                                        @Override
                                        public boolean accept( final File pathName ) {

                                            if ( pathName.isDirectory() ) {

                                                return true;

                                            } else //noinspection RedundantIfStatement
                                                if ( pathName.getName()
                                                                .endsWith( ".png" ) ) {

                                                return true;

                                            } else {

                                                return false;

                                            }

                                        }

                                        @Override
                                        public String getDescription() {

                                            return "thing";

                                        }

                                    }
                            );

                            Logger.logMsg( "FileSelectors2.swingSelectFile( ... ) return " + result );

                            ObtuseUtil.doNothing();

                        }

                    }
            );

            jf.setVisible( true );

            ObtuseUtil.doNothing();

        } else {

            JFrame jf = new JFrame( "Obtuse File Selector" );
            jf.setMinimumSize( new Dimension( 300, 300 ) );

            ObtuseFileSelector dialog = new ObtuseFileSelector(
                    jf,
                    false,
                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
            );

            dialog.enableCreateDirectory( true )
                  .setDirectory( new File( System.getProperty( "user.home" ) ) )
                  .setFileFilter(
                          new FileFilter() {
                              private Set<String> _suffixes = Set.of(
                                      ".png",
                                      ".jpg"
                              );

                              @Override
                              public boolean accept( final File pathName ) {

                                  if ( pathName.isDirectory() ) {

                                      return true;

                                  } else {

                                      String suffix = ObtuseUtil.extractSuffix( pathName.getName() );
                                      //noinspection RedundantIfStatement
                                      if ( _suffixes.contains( suffix ) ) {

                                          return true;

                                      }

                                      return false;

                                  }

                              }

                              @Override
                              public String getDescription() {

                                  return "thing";

                              }

                          }
                  ).run();

            Optional<List<File>> optSelection = dialog.getOptMultiSelection();

            Logger.logMsg( "got " + optSelection );

        }

        ObtuseUtil.doNothing();

        // Just return - Swing takes over from here.

    }

}
