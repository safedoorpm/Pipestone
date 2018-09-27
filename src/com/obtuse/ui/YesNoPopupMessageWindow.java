/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings({ "UnusedDeclaration" })
public abstract class YesNoPopupMessageWindow
        extends JDialog {

    private final String _line1;
    private final String _line2;
    private JPanel _contentPane;

    private JButton _alternativeButton;

    private JButton _defaultButton;

    private JLabel _firstMessageField;

    private JLabel _secondMessageField;

    private boolean _answer;

    private boolean _gotAnswer;

    private final Long _answerLock = 0L;

    protected YesNoPopupMessageWindow(
            final String line1,
            final String line2,
            final String defaultLabel,
            final String alternativeLabel
    ) {

        super();

        _line1 = line1;
        _line2 = line2;

        setContentPane( _contentPane );
        setModal( true );

        getRootPane().setDefaultButton( _defaultButton );

        _alternativeButton.setText( alternativeLabel );
        _alternativeButton.addActionListener(
                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent e ) {

                        onAlternativeChoice();

                    }

                }
        );

        _defaultButton.setText( defaultLabel );
        _defaultButton.addActionListener(
                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent e ) {

                        onDefaultChoice();

                    }

                }
        );

        _firstMessageField.setText( "<html>" + line1 );
        if ( line2 == null ) {

            _secondMessageField.setVisible( false );

        } else {

            _secondMessageField.setText( "<html>" + line2 );

        }

        // call onCancel() when cross is clicked
        setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );

        //noinspection RefusedBequest
        addWindowListener(
                new WindowAdapter() {

                    public void windowClosing( final WindowEvent e ) {

                        ObtuseUtil.doNothing();

                    }
                }
        );

        // call onCancel() on ESCAPE
        _contentPane.registerKeyboardAction(
                new MyActionListener() {

                    public void myActionPerformed( final ActionEvent e ) {

                        ObtuseUtil.doNothing();

                    }

                },
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        pack();
        setResizable( false );

    }

    protected YesNoPopupMessageWindow(
            final String line1, final String defaultLabel, final String alternativeLabel
    ) {

        this( line1, null, defaultLabel, alternativeLabel );
    }

    @SuppressWarnings({ "InstanceMethodNamingConvention" })
    public void go() {

        _answer = false;
        _gotAnswer = false;

        setVisible( true );

        synchronized ( _answerLock ) {

            while ( !_gotAnswer ) {

                try {

                    wait();

                } catch ( InterruptedException e ) {

                    // just ignore it.

                }

            }

        }

        ObtuseUtil.doNothing();

    }

    @SuppressWarnings({ "SameParameterValue" })
    public void fakeAnswer( final boolean answer ) {

        SwingUtilities.invokeLater(
                () -> {

                    if ( answer ) {

                        onDefaultChoice();

                    } else {

                        onAlternativeChoice();

                    }

                }
        );
    }

    private synchronized void onDefaultChoice() {

        setVisible( false );

        if ( !_gotAnswer ) {

            _answer = true;
            _gotAnswer = true;
            defaultChoice();

        }

        notifyAll();

        dispose();
    }

    private synchronized void onAlternativeChoice() {

        setVisible( false );

        if ( !_gotAnswer ) {
            _answer = false;
            _gotAnswer = true;
            alternativeChoice();
        }
        notifyAll();

        dispose();
    }

    /**
     Determine if an answer has been selected yet.

     @return true if an answer has been selected, false otherwise.
     */

    public boolean hasAnswer() {

        return _gotAnswer;
    }

    /**
     Gets the answer to the question.

     @return true if the default was selected, false otherwise.
     @throws IllegalArgumentException if no answer has been selected yet (see {@link #hasAnswer}).
     */

    public boolean getAnswer() {

        if ( hasAnswer() ) {

            return _answer;

        } else {

            throw new IllegalArgumentException( "no answer yet" );

        }
    }

    protected abstract void defaultChoice();

    protected abstract void alternativeChoice();

    /**
     Throw up a popup window and wait for a response.
     Note that this method does not return until after the human has clicked a button and the corresponding {@link Runnable} has been executed.

     @param line1               the first and (generally?) most prominent line of the popup window.
     @param line2               the second and (generally?) less prominent line of the popup window.
     @param defaultLabel        what should appear on the 'default' button (should generally be the safest choice).
     @param alternativeLabel    what should appear on the other button (should generally be the most dangerous choice).
     @param defaultRunnable     what to do if the human clicks the 'default' button.
     @param alternativeRunnable what to do if the human clicks the other button.
     @return {@code true} if the 'default' button is clicked; {@code false} otherwise.
     */

    @SuppressWarnings({ "SameParameterValue" })
    public static boolean doit(
            @NotNull final String line1,
            @Nullable final String line2,
            @NotNull final String defaultLabel,
            @NotNull final String alternativeLabel,
            @Nullable final Runnable defaultRunnable,
            @Nullable final Runnable alternativeRunnable
    ) {

        //noinspection ClassWithoutToString
        YesNoPopupMessageWindow maybe = new YesNoPopupMessageWindow(
                line1,
                line2,
                defaultLabel,
                alternativeLabel
        ) {

            protected void defaultChoice() {

                if ( defaultRunnable != null ) {
                    defaultRunnable.run();
                }
            }

            protected void alternativeChoice() {

                if ( alternativeRunnable != null ) {
                    alternativeRunnable.run();
                }
            }
        };

        Runnable doitRunnable = new Runnable() {

            @Override
            public void run() {

                maybe.go();

            }

        };

        if ( SwingUtilities.isEventDispatchThread() ) {

            doitRunnable.run();

        } else {

            SwingUtilities.invokeLater(

                    doitRunnable

            );

        }

        Logger.logMsg(
                "YesNoPopupMessageWindow.doit(" + ObtuseUtil.enquoteToJavaString( line1 ) + "):  " +
                "human clicked the " + ( maybe.getAnswer() ? "default" : "alternative" ) + " button"
        );

        return maybe.getAnswer();

    }

    /**
     Throw up a popup window and wait for a response.
     Note that this method does not return until after the human has clicked a button and the corresponding {@link Runnable} has been executed.

     @param line1               the only line of the popup window.
     @param defaultLabel        what should appear on the 'default' button (should generally be the safest choice).
     @param alternativeLabel    what should appear on the other button (should generally be the most dangerous choice).
     @param defaultRunnable     what to do if the human clicks the 'default' button.
     @param alternativeRunnable what to do if the human clicks the other button.
     @return {@code true} if the 'default' button is clicked; {@code false} otherwise.
     */

    public static boolean doit(
            @NotNull final String line1,
            @NotNull final String defaultLabel,
            @NotNull final String alternativeLabel,
            @Nullable final Runnable defaultRunnable,
            @Nullable final Runnable alternativeRunnable
    ) {

        @SuppressWarnings("UnnecessaryLocalVariable") boolean answer =
                YesNoPopupMessageWindow.doit(
                        line1,
                        null,
                        defaultLabel,
                        alternativeLabel,
                        defaultRunnable,
                        alternativeRunnable
                );

        return answer;

    }

    public String toString() {

        if ( _line2 == null ) {

            return "YesNoPopupWindow( " + ObtuseUtil.enquoteToJavaString( _line1 ) + " )";

        } else {

            return "YesNoPopupWindow( " +
                   ObtuseUtil.enquoteToJavaString( _line1 ) + ", " +
                   ObtuseUtil.enquoteToJavaString( _line2 ) +
                   " )";

        }

    }

    public static void main( final String[] args ) {

        final YesNoPopupMessageWindow dialog = new YesNoPopupMessageWindow(
                "Are we having fun yet?<br>More words<br>Even more words<br>Still more words",
                "You have five seconds to decide!",
                "Yes",
                "No"
        ) {

            public void defaultChoice() {
                //noinspection UseOfSystemOutOrSystemErr
                System.out.println( "go said yes" );
            }

            public void alternativeChoice() {
                //noinspection UseOfSystemOutOrSystemErr
                System.out.println( "go said no" );
            }
        };
        //noinspection RefusedBequest
        new Thread( () -> {
            //noinspection MagicNumber
            ObtuseUtil.safeSleepMillis( javax.management.timer.Timer.ONE_SECOND * 50L );
            dialog.fakeAnswer( true );
        } ).start();
        dialog.go();
    }

}
