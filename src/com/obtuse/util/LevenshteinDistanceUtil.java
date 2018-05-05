package com.obtuse.util;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class LevenshteinDistanceUtil {

    public static <T extends JTextComponent> T configureLevensteinDistanceDocument( T jtc, final @NotNull Collection<String> possibleWords, final int maxChoices, final int threshold, boolean trim ) {

        jtc.getDocument().addDocumentListener( new LeventsteinDistanceUtilListener<>( jtc, possibleWords, maxChoices, threshold, trim ) );

        return jtc;

    }

    public static <T extends JTextComponent> T configureLevensteinDistanceDocument(
            T jtc,
            final @NotNull String @NotNull [] possibleWords,
            final int maxChoices,
            final int threshold,
            boolean trim
    ) {

        jtc.getDocument()
           .addDocumentListener( new LeventsteinDistanceUtilListener<>(
                   jtc,
                   ObtuseCollections.arrayList( possibleWords ),
                   maxChoices,
                   threshold,
                   trim
           ) );

        return jtc;

    }

    private static class LeventsteinDistanceUtilListener<T extends JTextComponent> implements DocumentListener {

        private final int _threshold;
        private final @NotNull SortedSet<String> _possibleWordsSet;
        private final int _maxChoices;
        private final LevenshteinDistance _ld;
        private final T _textComponent;
        private final boolean _trim;

        public LeventsteinDistanceUtilListener( final @NotNull T textComponent, final @NotNull Collection<String> possibleWords, final int maxChoices, final int threshold, boolean trim ) {
            super();

            _textComponent = textComponent;

            _possibleWordsSet = new TreeSet<>();

            _maxChoices = maxChoices;

            _threshold = threshold;

            _trim = trim;

            _ld = new LevenshteinDistance( threshold );

            for ( String word : possibleWords ) {

                if ( word == null ) {

                    throw new NullPointerException( "LevenshteinDistanceUtil.LeventsteinDistanceUtilListener:  null work in possibleWords" );

                } else {

                    _possibleWordsSet.add( word );

                }

            }

            ObtuseUtil.doNothing();

        }

        @Override
        public void insertUpdate( DocumentEvent e ) {

            maybeSuggestAlternatives();

        }

        public void maybeSuggestAlternatives() {

            // No point in playing the game if there are no alternatives to suggest.

            if ( _possibleWordsSet.isEmpty() ) {

                return;

            }

            String word = _textComponent.getText();
            if ( _trim ) {

                word = word.trim();

            }
            TreeSorter<Integer,String> wordsByDistance = new TreeSorter<>();
            for ( String candidateWord : _possibleWordsSet ) {

                String actualCandidateWord = _trim ? candidateWord.trim() : candidateWord;
                Integer distance = _ld.apply( word, actualCandidateWord );

                // Only remember possible words that match at least as well as
                // the threshold specified when we were created.

                if ( distance >= 0 ) {

                    wordsByDistance.add( distance, actualCandidateWord );

                }

            }

            Logger.logMsg( "~~~ results for " + ObtuseUtil.enquoteToJavaString( word ) );

            for ( Integer distance : wordsByDistance.keySet() ) {

                Logger.logMsg( "" + ObtuseUtil.lpad( distance, 4 ) + " " + wordsByDistance.getValues( distance ) );

            }

        }

        @Override
        public void removeUpdate( DocumentEvent e ) {

            maybeSuggestAlternatives();

        }

        @Override
        public void changedUpdate( DocumentEvent e ) {

        }

        @NotNull
        public T getTextComponent() {

            return _textComponent;
        }

        @NotNull
        public Collection<String> getPossibleWordsSet() {

            return _possibleWordsSet;
        }

        public int getMaxChoices() {

            return _maxChoices;
        }

    }

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "LevenshteinDistanceUtil", null );
        JFrame jf = new JFrame();
        jf.setMinimumSize( new Dimension( 200, 100 ) );
        JPanel jp = new JPanel();
        JTextField jtf = new JTextField();
        jtf.setMinimumSize( new Dimension( 150, 25 ) );
        jtf.setPreferredSize( new Dimension( 150, 25 ) );
        JTextField jtx = configureLevensteinDistanceDocument( jtf, new String[]{"hat", "that", "cat", "jump", "dog"}, 25, 10, true );
        jp.add( jtx );
        jf.setContentPane( jp );
        jf.pack();
        jf.setVisible( true );

    }

}
