/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.ui;

import com.obtuse.util.BasicProgramConfigInfo;
import com.obtuse.util.Logger;
import com.obtuse.util.NounsList;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Manage a JPanel containing {@link ObtuseSortableJComponent}s.
 */

public class ObtuseSortableJComponentView extends JPanel {

    public static final int NWORDS = 100;

    private static final String[] s_words;

    static {

	SortedSet<String> words = new TreeSet<String>();
//	words.add( "alpha" );
//	words.add( "beta" );
//	words.add( "gamma" );
//	words.add( "turtles" );
//	words.add( "fish" );
//	words.add( "carrots" );
//	words.add( "words" );
//	words.add( "impala" );
//	words.add( "there" );
//	words.add( "world" );
//	words.add( "flash" );
//	words.add( "junk" );
//	words.add( "stuff" );
//	words.add( "street" );
//	words.add( "glunk" );
//	words.add( "elephant" );
//	words.add( "giraffe" );
//	words.add( "desk" );
//	words.add( "hat" );
//	words.add( "kettle" );
//	words.add( "lemon" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "glunk" );
//	words.add( "zebra" );

	for ( int i = 0; i < NWORDS; i += 1 ) {

	    words.add( NounsList.pickNoun() );

	}

	s_words = words.toArray( new String[words.size()] );

	ObtuseUtil.doNothing();

    }

    @SuppressWarnings("FieldCanBeLocal")
    private final BoxLayout _layoutManager;

    @SuppressWarnings("SameParameterValue")
    public ObtuseSortableJComponentView( int axis ) {
        super();

        //noinspection MagicConstant,ThisEscapedInObjectConstruction
        _layoutManager = new BoxLayout( this, axis );
        super.setLayout( _layoutManager );

    }

    public void setLayout( LayoutManager layoutManager ) {

        if ( getLayout() == null ) {

            super.setLayout( layoutManager );

        } else {

            throw new IllegalArgumentException( "ObtuseSortableJComponentView:  attempt to change layout manager once sorted JPanel has been created" );

        }

    }

    public Component add( Component component ) {

        if ( !( component instanceof ObtuseSortableJComponent ) ) {

            throw new IllegalArgumentException( "ObtuseSortableJComponentView.add:  components must implement ObtuseSortableJComponent interface" );

        }

        ObtuseSortableJComponent obtuseSortableJComponent = (ObtuseSortableJComponent)component;
	int existingIx = findIndex( obtuseSortableJComponent );
	if ( existingIx >= 0 ) {

	    throw new IllegalArgumentException( "ObtuseSortableJComponentView.add:  component already in this panel (" + component + ")" );

	}

	for ( int ix = 0; ix < getComponentCount(); ix += 1 ) {

            ObtuseSortableJComponent existingComponent = (ObtuseSortableJComponent)getComponent( ix );
            if ( obtuseSortableJComponent.compareTo( existingComponent ) <= 0 ) {

                super.add( component, ix );

                return component;

            }

        }

        super.add( component );

        return component;

    }

    public int findIndex( ObtuseSortableJComponent component ) {

	Component[] components = getComponents();
	for ( int ix = 0; ix < components.length; ix += 1 ) {

	    if ( component == components[ix] ) {

		return ix;

	    }

	}

	return -1;

    }

    public boolean reSort( Component component ) {

	if ( component != null && !( component instanceof ObtuseSortableJComponent ) ) {

	    throw new IllegalArgumentException( "ObtuseSortableJComponentView.reSort:  requesting component is not a ObtuseSortableJComponent instance (" + component + ")" );

	}

	ObtuseSortableJComponent sortableComponent = (ObtuseSortableJComponent)component;

	// Make sure that the requesting component, if specified, is one of our components.

	if ( sortableComponent != null && findIndex( sortableComponent ) < 0 ) {

	    throw new IllegalArgumentException( "ObtuseSortableJComponentView.reSort: component requesting re-sort is not in this panel" );

	}

	// If our components are still in the correct order then we're done.

	if ( checkOrder() ) {

	    return false;

	}

	// If there was no requesting component specified then we need to do this the hard way.

	if ( sortableComponent == null ) {

	    forceReSort();

	    return true;

	}

	// Remove the component that we have been told has changed.
	// If we are not now sorted then force a re-sort.

	remove( component );
	if ( !checkOrder() ) {

	    forceReSort();

	}

	// Re-add the changed component and we're done.

	add( component );

	return true;

    }

    /**
     Force a sort of our components.
     */

    public void forceReSort() {

	// Just re-add everything.

	Component[] components = getComponents();
	removeAll();
	for ( Component component : components ) {

	    add( component );

	}

    }

    public boolean checkOrder() {

	Component[] components = getComponents();
	if ( components.length <= 1 ) {

	    return true;

	}

	ObtuseSortableJComponent prev = (ObtuseSortableJComponent) components[0];
	for ( int ix = 1; ix < components.length; ix += 1 ) {

	    ObtuseSortableJComponent next = (ObtuseSortableJComponent) components[ix];
	    if ( prev.compareTo( next ) > 0 ) {

		return false;

	    }

	    prev = next;

	}

	return true;

    }

    public Component add( Component component, int ix ) {

        throw new IllegalArgumentException(
                "ObtuseSortableJComponentView.add:  attempt to use add( Component comp, int index ), must use add( Component comp )"
        );

    }

    public void add( @NotNull Component component, Object constraints ) {

        throw new IllegalArgumentException(
                "ObtuseSortableJComponentView.add:  attempt to use add( Component comp, Object constraints ), must use add( Component comp )"
        );

    }

    public void add( Component component, Object constraints, int ix ) {

        throw new IllegalArgumentException(
                "ObtuseSortableJComponentView.add:  attempt to use add( Component comp, Object constraints, int index ), must use add( Component comp )"
        );

    }

    public Component add( String name, Component component ) {

        throw new IllegalArgumentException(
                "ObtuseSortableJComponentView.add:  attempt to use add( String name, Component comp ), must use add( Component comp )"
        );

    }

//    private static class MyJPanel extends JPanel implements ObtuseSortableJComponent {
//
//        private final String _label;
//
//        private MyJPanel( String label ) {
//            super();
//
//            _label = label;
//
//        }
//
//        public int compareTo( @NotNull ObtuseSortableJComponent rhs ) {
//
//            return _label.compareTo( ((MyJPanel)rhs).getLabel() );
//
//        }
//
//        public String getLabel() {
//
//            return _label;
//
//        }
//
//    }

    private static Random s_rng = new Random( System.currentTimeMillis() );

    private static int s_nextIx = 0;

    private static JPanel makeJPanel( final String label ) {

        final ObtuseSortableJPanel panel = new ObtuseSortableJPanel( label );
        panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );
        final JButton before = new JButton( "add before" );
	JButton after = new JButton( "add after" );
	final JLabel ourLabel = new JLabel( label );
	panel.add( new JLabel( "" + s_nextIx ) );
	s_nextIx += 1;
	panel.add( before );
	panel.add( after );
	panel.add( ourLabel );

	before.addActionListener(
                new ActionListener() {

                    public void actionPerformed( ActionEvent actionEvent ) {

                        Logger.logMsg( "someone clicked the before \"" + panel.getSortingKey() + "\" button" );
			int wordIx = findWord( panel.getSortingKey() );
			if ( wordIx > 0 ) {

			    int newIx = s_rng.nextInt( wordIx + 1 );
			    if ( newIx > wordIx ) {

				throw new IllegalArgumentException( "rng problem 1 in makeJPanel" + " (wordIx=" + wordIx + ", newIx=" + newIx + ")" );

			    }

			    Logger.logMsg( "\"" + panel.getSortingKey() + "\" becomes \"" + s_words[newIx] + "\"" );

			    panel.setSortingKey( s_words[newIx] );
			    String newLabel = panel.getSortingKey();
			    if ( !newLabel.equals( s_words[newIx] ) ) {

				throw new IllegalArgumentException( "panel key change did not stick" );

			    }

			    ourLabel.setText( panel.getSortingKey() );

			    if ( !newLabel.equals( ourLabel.getText() ) ) {

				throw new IllegalArgumentException( "button label change did not stick" );

			    }

//			    panel.invalidate();

			}

                    }

                }
        );

	after.addActionListener(
		new ActionListener() {

		    public void actionPerformed( ActionEvent actionEvent ) {

			Logger.logMsg( "someone clicked the after \"" + panel.getSortingKey() + "\" button" );
			int wordIx = findWord( panel.getSortingKey() );
			if ( wordIx < s_words.length - 1 ) {

			    int newIx = wordIx + s_rng.nextInt( s_words.length - wordIx );

			    if ( newIx < wordIx ) {

				throw new IllegalArgumentException( "rng problem 2 in makeJPanel" + " (wordIx=" + wordIx + ", newIx=" + newIx + ")" );

			    }

			    if ( newIx >= s_words.length ) {

				throw new IllegalArgumentException( "rng problem 3 in makeJPanel" + " (wordIx=" + wordIx + ", newIx=" + newIx + ")" );

			    }

			    Logger.logMsg( "\"" + panel.getSortingKey() + "\" becomes \"" + s_words[newIx] + "\"" );

			    panel.setSortingKey( s_words[newIx] );
			    String newLabel = panel.getSortingKey();
			    if ( !newLabel.equals( s_words[newIx] ) ) {

				throw new IllegalArgumentException( "panel key change did not stick" );

			    }

			    ourLabel.setText( panel.getSortingKey() );

			    if ( !newLabel.equals( ourLabel.getText() ) ) {

				throw new IllegalArgumentException( "button label change did not stick" );

			    }

//			    panel.invalidate();

			}

		    }

		}
	);

        return panel;

    }

    private static int findWord( String word ) {

	for ( int i = 0; i < s_words.length; i += 1 ) {

	    if ( word.equals( s_words[i] ) ) {

		return i;

	    }

	}

	throw new IllegalArgumentException( "word \"" + word + " not found" );

    }

    @SuppressWarnings("UnqualifiedStaticUsage")
    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "ObtuseSortableJComponentView", null );

        JFrame topFrame = new JFrame();

        ObtuseSortableJComponentView sjp = new ObtuseSortableJComponentView( BoxLayout.Y_AXIS );
	for ( int i = 0; i < NWORDS; i += 1 ) {

	    sjp.add( makeJPanel( s_words[i] ) );

	}
//        sjp.add( makeJPanel( "There" ) );
//        sjp.add( makeJPanel( "World" ) );
//        sjp.add( makeJPanel( "Hello" ) );
//        sjp.add( makeJPanel( " starts with space" ) );
//        sjp.add( makeJPanel( "Zee Last One" ) );

	JScrollPane pane = new JScrollPane( sjp );
        topFrame.setContentPane( pane );
        topFrame.pack();
        topFrame.setVisible( true );

    }

}
