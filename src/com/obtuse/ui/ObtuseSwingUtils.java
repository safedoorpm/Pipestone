package com.obtuse.ui;

import com.obtuse.ui.layout.linear.LinearContainer;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 Created by danny on 2019/11/29.
 */
public class ObtuseSwingUtils {

    public static String describeBorder( final Border border ) {

        if ( border == null ) {

            return "<<null border>>";

        }

        if ( border instanceof EtchedBorder ) {

            EtchedBorder b = (EtchedBorder)border;
            return "EtchedBorder( eType=" +
                   ( b.getEtchType() == EtchedBorder.LOWERED ? "lowered" : "raised" ) +
                   ", hColour=" +
                   b.getHighlightColor() +
                   ", sColor=" +
                   b.getShadowColor() +
                   " )";

        } else if ( border instanceof LineBorder ) {

            LineBorder b = (LineBorder)border;
            return "LineBorder( lColour=" +
                   b.getLineColor() +
                   ", thickness=" +
                   b.getThickness() +
                   ( b.getRoundedCorners() ? ", corners=rounded" : "" ) +
                   " )";

        }

        return border.toString();

    }

    public static String describeContainer( final @NotNull Container target ) {

        StringBuilder sb = new StringBuilder( ObtuseUtil.enquoteToJavaString( target.getName() ) ).append( "(" );
        String comma = " ";
        boolean gotAtLeastOne = false;
        for ( Component c : target.getComponents() ) {

            sb.append( comma ).append( describeComponent( c ) );
            comma = ", ";

            gotAtLeastOne = true;

        }

        sb.append( gotAtLeastOne ? " " : "" ).append( ")" );

        return sb.toString();

    }

    public static String describeComponent( final @Nullable Component c ) {

        if ( c == null ) {

            return "null";

        }

        StringBuilder sb = new StringBuilder();
        if ( c instanceof LinearContainer ) {

            sb.append( "{ name=" ).append( ObtuseUtil.enquoteToJavaString( c.getName() ) ).append( " }" );

        } else if ( c instanceof AbstractButton ) {

            sb.append( c.getClass().getCanonicalName() )
              .append( "( label=" )
              .append( ObtuseUtil.enquoteToJavaString( ( (AbstractButton)c).getText() ) )
              .append( " )" );

        } else if ( c instanceof JLabel ) {

            sb.append( c.getClass().getCanonicalName() )
              .append( "( label=" )
              .append( ObtuseUtil.enquoteToJavaString( ((JLabel)c).getText() ) )
              .append( " )" );

        } else {

            sb.append( c.getClass().getCanonicalName() )
              .append( "( name=" )
              .append( ObtuseUtil.enquoteToJavaString( c.getName() ) )
              .append( " )" );

        }

        return sb.toString();

    }

    public static String showIt( final @Nullable Component component ) {

        if ( component == null ) {

            return "component is null";

        } else if ( component instanceof JTextComponent ) {

            return component.getClass().getCanonicalName() + "( " + ObtuseUtil.enquoteToJavaString( ((JTextComponent)component).getText() ) + " )";

        } else if ( component instanceof JLabel ) {

            String text = ( (JLabel)component ).getText();
            Icon icon = ( (JLabel)component ).getIcon();
            StringBuilder sb = new StringBuilder();
            sb.append( component.getClass().getCanonicalName() ).append( "(" );
            String comma = "";
            if ( text != null ) {

                sb.append( comma )
                  .append( " text=" )
                  .append( ObtuseUtil.enquoteToJavaString( text ) );
                comma = ",";

            }
            if ( icon != null ) {

                sb.append( comma )
                  .append( " icon=" )
                  .append( ObtuseUtil.enquoteJavaObject( icon ) );
                //noinspection UnusedAssignment
                comma = ",";

            }

            sb.append( " )" );

            return sb.toString();

        } else if ( component instanceof AbstractButton ) {

            return component.getClass().getCanonicalName() +"( " + ObtuseUtil.enquoteToJavaString( ((AbstractButton)component).getText() ) + " )";

        } else {

            return "{ " + component + " }";

        }

    }

    public static boolean isShowable( final @Nullable Component component ) {

        return !showIt( component ).startsWith( "{" );

    }

    public static void describeFullyContainerContents( final @NotNull String why, final @Nullable Component component ) {

        Logger.logMsg( "~>~>~>~>~>~>~>~>~>~>~>~>~>~>~>" );
        try {

            if ( component == null ) {

                Logger.logMsg( why + " - component is null" );

                return;

            } else {

                Logger.logMsg( why + " - " + ( component.isVisible() ? "visible" : ( "invisible " + component.getName() ) ) + " " + showIt( component ) );

            }

            describeFullyContainerContents( 1, why, component );

        } finally {

            Logger.logMsg( "<~<~<~<~<~<~<~<~<~<~<~<~<~<~<~" );

        }

    }

    public static void describeFullyContainerContents( final int depth, final @NotNull String why, final @Nullable Component component ) {

        doitDescribeFullyContainerContents( depth, "", why, component );

        ObtuseUtil.doNothing();

    }

    private static void doitDescribeFullyContainerContents( final int depth, final @NotNull String cBlConstraint, final @NotNull String why, final @Nullable Component component ) {

        if ( component == null ) {

            Logger.logMsg( ObtuseUtil.replicate( "  ", depth ) + cBlConstraint + "component is null" );

        } else {

            String description;
            String formattedComponentName = fullName( component );

            description = ( component.isVisible() ? "visible" : ( "invisible " + component.getName() ) ) + " " +
                          formattedComponentName + ObtuseUtil.fBounds( component.getBounds() ) +
                          ( component instanceof JComponent ? ( (JComponent)component).getInsets().toString() : "" ) +
                          ":  " + component;

            Logger.logMsg( ObtuseUtil.replicate( "  ", depth ) + cBlConstraint + "[[[ " + description );

            if ( component instanceof Container ) {
                LayoutManager lm = ((Container)component).getLayout();
                BorderLayout bl = lm instanceof BorderLayout ? (BorderLayout)lm : null;

                for ( Component c : ((Container)component).getComponents() ) {

                    String blConstraint = getBorderLayoutConstraint( bl, c );

                    if ( c instanceof Container ) {

                        if ( isShowable( c ) ) {

                            Logger.logMsg( ObtuseUtil.replicate( "  ", depth + 1 ) + blConstraint + showIt( c ) );

                        } else {

                            doitDescribeFullyContainerContents( depth + 1, blConstraint, why, c );

                        }

                    } else {

                        Logger.logMsg( ObtuseUtil.replicate( "  ", depth ) + blConstraint + "component " );

                    }

                }

            }

            Logger.logMsg( ObtuseUtil.replicate( "  ", depth ) + "]]]" );

        }

    }

    @NotNull
    private static String getBorderLayoutConstraint( final BorderLayout bl, final Component c ) {

        String blConstraint;
        if ( bl == null ) {

            blConstraint = "";

        } else {

            Object objConstraints = bl.getConstraints( c );
            if ( objConstraints == null ) {

                blConstraint = "null" + " ";

            } else {

                blConstraint = "" + objConstraints + " ";

            }

        }

        return blConstraint;

    }

    public static void describeQuicklyGuiEntity( final String why, final @Nullable Container container ) {

        describeGuiEntity( why, container, false, false );

    }

    public static void describeFullyGuiEntity( final String why, final @Nullable Container container ) {

        describeGuiEntity( why, container, true, true );

    }

    public static void describeGuiEntity( final String why, final @Nullable Container container, final boolean recurse, final boolean showContents ) {

        if ( container == null ) {

            Logger.logMsg( "=== " + ( why == null ? "" : why ) + ":  container is null" );

            return;

        }

        Logger.logMsg( "<<<" + ( ( why == null ? "" : why ) + ":  " ) +
                       "structure of container \"" +
                       container.getName() +
                       "\" is " +
                       ObtuseUtil.fBounds( container.getBounds() ) +
                       " - " +
                       container );

        for ( Component c : container.getComponents() ) {

            describeGuiEntity( 1, c, recurse, showContents );

        }

        Logger.logMsg( ">>>" );

        Logger.logMsg( "" );

    }

    public static void describeGuiEntity( final int depth, final Component component, final boolean recurse, final boolean showContents ) {

        String formattedComponentName = fullName( component );
        String description;
        if ( component instanceof Container && isShowable( component ) ) {

            description = showIt( component ); // "JLabel( " + ObtuseUtil.enquoteToJavaString( ((JLabel)component).getText() ) + " )";

        } else {

            description = formattedComponentName + ObtuseUtil.fBounds( component.getBounds() ) +
                          ( component instanceof JComponent ? ((JComponent)component).getInsets().toString() : "" ) +
                          ":  " + component;
        }
        Logger.logMsg(
                ObtuseUtil.replicate( "  ", depth ) +
                description
        );

        if ( component instanceof Container && showContents ) {

            for ( Component c : ( (Container)component ).getComponents() ) {

                describeGuiEntity( depth + 1, c, false, true );

            }

        }

        if ( recurse ) {

            Container parent = component.getParent();
            if ( parent != null ) {

                describeGuiEntity( depth + 1, parent, true, false );

            }

        }

    }

    public static String fullName( final Container parent, final Component c ) {

        return "\"" + parent.getName() + "\"/\"" + fullName( c ) + "\"" + ( c instanceof Container ? " (c)" : "(?)" );

    }

    @NotNull
    public static String fullName( final Component component ) {

        String name = component.getName();
        if ( name == null ) {

            if ( component instanceof JButton ) {

                name = "buttonLabel=\"" + ( (JButton)component ).getText() + '"';

            } else {

                name = "unknown name";

            }

        } else {

            name = "name=\"" + name + "\"";

        }

        return component.getClass().getCanonicalName() + "(" + name + ")";

    }

    public static void showWhereWeAre( final Component start, final AWTEvent e ) {

        try {

            Logger.pushNesting( "showWhereWeAre" );

            if ( e instanceof MouseEvent ) {

                MouseEvent me = (MouseEvent)e;

                String msg = "click count = " + me.getClickCount() + ", " +
                             "Click position :  ( " + me.getX() + ", " + me.getY() + " )";

                if ( me.getButton() == MouseEvent.NOBUTTON ) {

                    Logger.logMsg( "SF(\"" + start.getName() + "\"):  " + "No button clicked, " + msg );

                } else {

                    Logger.logMsg( "SF(\"" + start.getName() + "\"):  " + "Button #" + me.getButton() + " clicked, " + msg );

                }

            }

            describeGuiEntity( 0, start, true, false );

        } finally {

            Logger.popNestingLevel( "showWhereWeAre" );

        }

    }

    public static void showStructure( final Container start ) {

        java.util.List<Container> nest = new LinkedList<>();
        for ( Container c = start; c != null; c = c.getParent() ) {
            nest.add( 0, c );
        }

        int depth = 0;

        for ( Container c : nest ) {

            Logger.logMsg(
                    ObtuseUtil.replicate( "    ", depth ) +
                    c.getClass().getName() + ":  " +
                    describeBriefly( c ) //  c.getName(), c.getComponents() )
            );

            depth += 1;

        }

        Logger.logMsg( "" );
    }

    public static String describeBriefly( final Container container ) {

        StringBuilder sb = new StringBuilder( "{" );
        String comma = "";
        int count = container.getComponents().length;
        int cc = container.getComponentCount();
        ObtuseUtil.doNothing();

        for ( Component c : container.getComponents() ) {

            if ( comma.isEmpty() ) {

                sb.append( ' ' );

            }

            sb.append( comma ).append( fullName( container, c ) );
            // ) append( '"' ).append( container.getName() ).append( "\"/\"" ).append( c.getName() ).append( '"' );
            comma = ", ";

        }

        if ( !comma.isEmpty() ) {

            sb.append( ' ' );

        }

        sb.append( '}' );

        return "" +
               ObtuseUtil.fInsets( container.getInsets() ) + " ~ " +
               container.getClass().getSimpleName() +
               "( \"" +
               container.getName() +
               "\", " +
               ObtuseUtil.pluralize(
               container.getComponentCount(),
               "component" ) +
               ", " +
               sb +
               " )";

    }
}
