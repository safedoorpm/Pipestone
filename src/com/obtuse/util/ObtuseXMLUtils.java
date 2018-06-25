/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.exceptions.ObtuseXmlNodeException;
import com.obtuse.util.exceptions.ParsingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

@SuppressWarnings("UnusedDeclaration")
public class ObtuseXMLUtils {

    public interface CheckNode {

        /**
         * Determine if a candidate node is the target node.
         * @param candidateNode the candidate node.
         * @return true if this is the target node; false otherwise.
         */

        boolean isThisTheNode( Node candidateNode );

        /**
         * Deal with the failure to find the target node.
         * @return null what the find operation should return (presumably <tt>null</tt> but any alternative Node works too).
         * @throws com.obtuse.util.exceptions.ObtuseXmlNodeException if that's how the failure is to be dealt with.
         */

        Node noteFailure() throws ObtuseXmlNodeException;

    }

    private ObtuseXMLUtils() {
        super();

    }

    public static void dumpXmlDocument( final PrintStream ps, final Document doc ) {

        ps.println( "document name is \"" + doc.getNodeName() + "\"" );
        NodeList contents = doc.getChildNodes();
        for ( int ix = 0; ix < contents.getLength(); ix += 1 ) {

            Node node = contents.item( ix );
            ObtuseXMLUtils.dumpXmlNode( ps, node, 1 );

        }

    }

    public static void dumpXmlNode( final PrintStream ps, final Node node, final int depth ) {

        if ( "#text".equals( node.getNodeName() ) ) {

            if ( !node.getTextContent().trim().isEmpty() ) {

                ps.println(
                        ObtuseUtil.replicate( "   ", depth ) +
                        "node is \"" +
                        node.getNodeName() +
                        "\" (text = \"" +
                        node.getTextContent()
                            .trim() +
                        "\")"
                );

            }

            if ( node.getChildNodes().getLength() == 0 && !node.hasAttributes() ) {

                return;

            }

        } else {

            ps.println( ObtuseUtil.replicate( "   ", depth ) + "node is \"" + node.getNodeName() + "\"" );

        }

        if ( node.hasAttributes() ) {

            NamedNodeMap nodeMap = node.getAttributes();
            if ( nodeMap == null ) {

                Logger.logErr( "we were supposed to find attributes but got nothing in node \"" + node.getNodeName() + "\"" );

            } else {

                for ( int ix = 0; ix < nodeMap.getLength(); ix += 1 ) {

                    Node item = nodeMap.item( ix );
                    ps.println(
                            ObtuseUtil.replicate( "   ", depth ) +
                                    item.getNodeName() + "=" + item.getNodeValue() // + " (type " + item.getNodeType() + ")"
                    );

                }

            }

        }

        NodeList contents = node.getChildNodes();
        for ( int ix = 0; ix < contents.getLength(); ix += 1 ) {

            Node childNode = contents.item( ix );
            ObtuseXMLUtils.dumpXmlNode( ps, childNode, depth + 1 );

        }

    }

    public static double[] getDoubleArray( final Node parentNode, final String targetNodeName )
            throws ObtuseXmlNodeException {

        Node arrayNode = ObtuseXMLUtils.findNode( parentNode, targetNodeName );
        NodeList elements = arrayNode.getChildNodes();
        int arrayLength = 0;
        for ( int ix = 0; ix < elements.getLength(); ix += 1 ) {

            Node element = elements.item( ix );
            if ( "item".equals( element.getNodeName() ) ) {

                arrayLength += 1;

            }

        }

        double[] rval = new double[arrayLength];
        int elementIx = 0;
        for ( int ix = 0; ix < elements.getLength(); ix += 1 ) {

            Node element = elements.item( ix );
            if ( "item".equals( element.getNodeName() ) ) {

                String elementString = element.getFirstChild().getNodeValue();

                try {

                    rval[elementIx] = Double.parseDouble( elementString );
                    elementIx += 1;

                } catch ( NumberFormatException e ) {

                    throw new ObtuseXmlNodeException( "array element " + elementIx + " cannot be parsed as a double", elementIx, e );

                }

            }

        }

        return rval;

    }

    public static int[] getIntegerArray( final Node parentNode, final String targetNodeName )
            throws ObtuseXmlNodeException {

        Node arrayNode = ObtuseXMLUtils.findNode( parentNode, targetNodeName );
        NodeList elements = arrayNode.getChildNodes();
        int arrayLength = 0;
        for ( int ix = 0; ix < elements.getLength(); ix += 1 ) {

            Node element = elements.item( ix );
            if ( "item".equals( element.getNodeName() ) ) {

                arrayLength += 1;

            }

        }

        int[] rval = new int[arrayLength];
        int elementIx = 0;
        for ( int ix = 0; ix < elements.getLength(); ix += 1 ) {

            Node element = elements.item( ix );
            if ( "item".equals( element.getNodeName() ) ) {

                String elementString = element.getFirstChild().getNodeValue();

                try {

                    rval[elementIx] = Integer.parseInt( elementString );
                    elementIx += 1;

                } catch ( NumberFormatException e ) {

                    throw new ObtuseXmlNodeException( "array element " + elementIx + " cannot be parsed as an integer", elementIx, e );

                }

            }

        }

        return rval;

    }

    public static double getMandatoryDoubleAttributeValue( final Node node, final String attributeName )
            throws ObtuseXmlNodeException {

        //noinspection ConstantConditions
        return ObtuseXMLUtils.getDoubleAttributeValue( node, attributeName, true ).doubleValue();

    }

    public static Double getDoubleAttributeValue( final Node node, final String attributeName, final boolean mandatory )
            throws ObtuseXmlNodeException {

        String attributeValue = ObtuseXMLUtils.getAttributeValue( node, attributeName, mandatory );
        if ( attributeValue == null ) {

            // This can only happen if mandatory is false (otherwise getAttributeValue has already thrown an exception)

            return null;

        }

        try {

            return Double.parseDouble( attributeValue );

        } catch ( NumberFormatException e ) {

            throw new ObtuseXmlNodeException(
                    "attribute " + attributeName + "'s value \"" + attributeValue + "\" in " +
                            node.getNodeName() + " node cannot be parsed as a double",
                    e
            );

        }

    }

    public static String getMandatoryStringAttributeValue( final Node node, final String attributeName )
            throws ObtuseXmlNodeException {

        return ObtuseXMLUtils.getStringAttributeValue( node, attributeName, true );

    }

    public static String getStringAttributeValue( final Node node, final String attributeName, final boolean mandatory )
            throws ObtuseXmlNodeException {

        String attributeValue = ObtuseXMLUtils.getAttributeValue( node, attributeName, mandatory );
        if ( attributeValue == null ) {

            // This can only happen if mandatory is false (otherwise getAttributeValue has already thrown an exception)

            return null;

        }

        return attributeValue;

    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public static boolean getMandatoryBooleanAttributeValue( final Node node, final String attributeName )
            throws ObtuseXmlNodeException {

        //noinspection ConstantConditions
        return ObtuseXMLUtils.getBooleanAttributeValue( node, attributeName, true ).booleanValue();

    }

    private static final String[] TRUE_VALUES = { "y", "yes", "t", "true", "on" };
    private static final String[] FALSE_VALUES = { "n", "no", "f", "false", "off" };

    private static final String FORMATTED_LEGIT_VALUES;
    static {

        StringBuilder sb = new StringBuilder();
        String comma = "";
        for ( String trueValue : ObtuseXMLUtils.TRUE_VALUES ) {

            sb.append( comma ).append( '"' ).append( trueValue ).append( '"' );
            comma = ", ";

        }

        for ( String falseValue : ObtuseXMLUtils.FALSE_VALUES ) {

            sb.append( comma ).append( '"' ).append( falseValue ).append( '"' );
            comma = ", ";

        }

        String formattedLegitValues = sb.toString();
        int lastCommaIx = formattedLegitValues.lastIndexOf( ',' );
        FORMATTED_LEGIT_VALUES = formattedLegitValues.substring( 0, lastCommaIx ) + " or" + formattedLegitValues.substring( lastCommaIx + 1 );

    }

    public static Boolean getBooleanAttributeValue( final Node node, final String attributeName, final boolean mandatory )
            throws ObtuseXmlNodeException {

        String attributeValue = ObtuseXMLUtils.getAttributeValue( node, attributeName, mandatory );
        if ( attributeValue == null ) {

            // This can only happen if mandatory is false (otherwise getAttributeValue has already thrown an exception)

            return null;

        }

        for ( String trueValue : ObtuseXMLUtils.TRUE_VALUES ) {

            if ( trueValue.equalsIgnoreCase( attributeValue ) ) {

                return true;

            }

        }

        for ( String falseValue : ObtuseXMLUtils.FALSE_VALUES ) {

            if ( falseValue.equalsIgnoreCase( attributeValue ) ) {

                return false;

            }

        }

        throw new ObtuseXmlNodeException(
                "attribute " + attributeName + "'s value \"" + attributeValue + "\" in " +
                        node.getNodeName() + " node cannot be parsed as a boolean (must be " +
                        ObtuseXMLUtils.FORMATTED_LEGIT_VALUES + " in any mixture of upper or lower case)"
        );

    }

    public static int getMandatoryIntegerAttributeValue( final Node node, final String attributeName )
            throws ObtuseXmlNodeException {

        //noinspection ConstantConditions
        return ObtuseXMLUtils.getIntegerAttributeValue( node, attributeName, true ).intValue();

    }

    public static Integer getIntegerAttributeValue( final Node node, final String attributeName, final boolean mandatory )
            throws ObtuseXmlNodeException {

        String attributeValue = ObtuseXMLUtils.getAttributeValue( node, attributeName, mandatory );
        if ( attributeValue == null ) {

            // This can only happen if mandatory is false (otherwise getAttributeValue has already thrown an exception)

            return null;
        }

        try {

            return Integer.parseInt( attributeValue );

        } catch ( NumberFormatException e ) {

            throw new ObtuseXmlNodeException(
                    "attribute " + attributeName + "'s value \"" + attributeValue + "\" in " +
                            node.getNodeName() + " node cannot be parsed as an int",
                    e
            );

        }

    }

    public static ObtuseCalendarDate getMandatoryCalendarDateAttributeValue( final Node node, final String attributeName )
            throws ObtuseXmlNodeException {

        return ObtuseXMLUtils.getCalendarDateAttributeValue( node, attributeName, true );

    }

    public static ObtuseCalendarDate getCalendarDateAttributeValue( final Node node, final String attributeName, final boolean mandatory )
            throws ObtuseXmlNodeException {

        String attributeValue = ObtuseXMLUtils.getAttributeValue( node, attributeName, mandatory );
        if ( attributeValue == null ) {

            // This can only happen if mandatory is false (otherwise getAttributeValue has already thrown an exception)

            return null;

        }

        try {

            @SuppressWarnings("UnnecessaryLocalVariable")
	    ObtuseCalendarDate rval = new ObtuseCalendarDate( attributeValue );
            return rval;

        } catch ( ParsingException e ) {

            throw new ObtuseXmlNodeException(
                    "attribute \"" + attributeName + "\" in node \"" + node.getNodeName() +
                            "\" is not a calendar date in \"yyyy-mm-dd\" format (value=\"" + attributeValue + "\")"
            );

        }

    }

    public static FormattedImmutableDate getMandatoryDateTimeAttributeValue( final Node node, final String attributeName )
            throws ObtuseXmlNodeException {

        return ObtuseXMLUtils.getDateTimeAttributeValue( node, attributeName, true );

    }

    public static FormattedImmutableDate getDateTimeAttributeValue( final Node node, final String attributeName, final boolean mandatory )
            throws ObtuseXmlNodeException {

        String attributeValue = ObtuseXMLUtils.getAttributeValue( node, attributeName, mandatory );
        if ( attributeValue == null ) {

            // This can only happen if mandatory is false (otherwise getAttributeValue has already thrown an exception)

            return null;

        }

        try {

            @SuppressWarnings("UnnecessaryLocalVariable")
            ImmutableDate rval = DateUtils.parseYYYY_MM_DD_HH_MM( attributeValue, 0 );
            return new FormattedImmutableDate( rval );

        } catch ( ParsingException e ) {

            throw new ObtuseXmlNodeException(
                    "attribute \"" + attributeName + "\" in node \"" + node.getNodeName() +
                            "\" is not a date time in \"yyyy-mm-dd hh:mm\" format (value=\"" + attributeValue + "\")"
            );

        }

    }

    public static String getAttributeValue( final Node node, final String attributeName, final boolean mandatory )
            throws ObtuseXmlNodeException {

        NamedNodeMap attributes = node.getAttributes();
        Node attributeNode = attributes.getNamedItem( attributeName );

        if ( attributeNode == null ) {

            if ( mandatory ) {

                throw new ObtuseXmlNodeException( "attribute " + attributeName + " not found in " + node.getNodeName() + " node." );

            } else {

                return null;

            }

        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        String attributeValue = attributeNode.getNodeValue();
        return attributeValue;

    }

    public static Collection<InstanceFromXML> getInstancesFromXML(
            final MessageProxy messageProxy,
            final @NotNull Node parentNode,
            final String targetNodeName,
            final String elementNodeName,
            final Class<? extends InstanceFromXML> elementClass
    ) throws ObtuseXmlNodeException {

        Collection<InstanceFromXML> rval = new LinkedList<>();
        Node arrayNode = ObtuseXMLUtils.findNode( parentNode, targetNodeName );
        NodeList elements = arrayNode.getChildNodes();

        int elementIx = 0;
        for ( int ix = 0; ix < elements.getLength(); ix += 1 ) {

            Node element = elements.item( ix );
            if ( elementNodeName.equals( element.getNodeName() ) ) {

                try {

                    rval.add(
                            ObtuseXMLUtils.loadInstanceFromXML(
                                    messageProxy,
                                    null,
                                    element,
                                    elementClass.getPackage(),
                                    elementClass,
                                    "element"
                            )
                    );
                    elementIx += 1;

                } catch ( NumberFormatException e ) {

                    throw new ObtuseXmlNodeException( "array element " + elementIx + " cannot be parsed as a double", elementIx, e );

                }

            }

        }

        return rval;

    }

    public static InstanceFromXML getInstanceFromXML( final MessageProxy messageProxy, final Node parentNode, final String targetNodeName, final String name )
            throws ObtuseXmlNodeException {

        Node instance = ObtuseXMLUtils.findNode( parentNode, targetNodeName );
        if ( instance == null ) {

            throw new ObtuseXmlNodeException( targetNodeName + " node not found in " + parentNode.getNodeName() + " node." );

        }

        return ObtuseXMLUtils.loadInstanceFromXML( messageProxy,
                parentNode,
                instance,
                InstanceFromXML.class.getPackage(),
                InstanceFromXML.class,
                name
        );

    }

    public static InstanceFromXML loadInstanceFromXML(
            final MessageProxy messageProxy,
            @Nullable final Node parent,
            final Node targetNode,
            @Nullable final Package optionalExpectedPackage,
            final Class<? extends InstanceFromXML> expectedClass,
            final String name
    ) {

        return ObtuseXMLUtils.loadInstanceFromXML(
                messageProxy,
                parent,
                targetNode,
                optionalExpectedPackage == null ? null : new Package[] { optionalExpectedPackage },
                expectedClass,
                name
        );

    }
    public static InstanceFromXML loadInstanceFromXML(
            final MessageProxy messageProxy,
            @Nullable final Node parent,
            final Node targetNode,
            @Nullable final Package[] optionalExpectedPackages,
            final Class<? extends InstanceFromXML> expectedClass,
            final String name
    ) {

        Package[] expectedPackages = optionalExpectedPackages == null ?
                new Package[]{ expectedClass.getPackage() }
                :
                optionalExpectedPackages;

        String targetNodeName = targetNode.getNodeName();

        Class<?> targetClass = null;

        for ( Package targetPackage : expectedPackages ) {

            try {

                targetClass = Class.forName(
                        targetPackage.getName() + '.' + targetNodeName
                );
                break;

            } catch ( ClassNotFoundException e ) {

                // Not there - try somewhere else

            }

        }

        if ( targetClass == null ) {

            StringBuilder msg = new StringBuilder();
            if ( expectedPackages.length == 1 ) {

                msg.append( "The " ).append( name ).append( " class must be in the " ).append( expectedPackages[0].getName() ).append( " package." );

            } else {

                msg.append( "The " ).append( name ).append( " class must be in one of the following packages:<blockquote>" );
                for ( Package targetPackage : expectedPackages ) {

                    msg.append( targetPackage.getName() ).append( ' ' );

                }
                msg.append( "</blockquote>" );

            }

            messageProxy.error(
                    "Unknown/unsupported " + name + ":  " + targetNodeName,
                    msg.toString(),
                    "OK"
            );

            return null;

        }

        InstanceFromXML configClassInstance = null;
        try {

            Constructor<?> constructor;
            if ( parent == null ) {

                constructor = targetClass.getConstructor( MessageProxy.class, Node.class, Node.class );
                //noinspection ConstantConditions
                configClassInstance = (InstanceFromXML)constructor.newInstance(
                        messageProxy,
                        parent,
                        targetNode
                );

            } else {

                constructor = targetClass.getConstructor( MessageProxy.class, Node.class );
                configClassInstance = (InstanceFromXML)constructor.newInstance(
                        messageProxy,
                        parent
                );

            }

            if ( !expectedClass.isInstance( configClassInstance ) ) {

                messageProxy.error(
                        "Restoration of " + targetNodeName + " yielded the wrong class of object.",
                        "Expected to get a " + expectedClass.getSimpleName() + " but got a " +
                                configClassInstance.getClass().getSimpleName() + " instead.<br>" +
                                "Please notify Danny (provide him with the .xml file you just tried to load).",
                        "I Promise To Provide Danny With A Copy Of The XML File That I Just Tried To Load"
                );

                return null;

            }

        } catch ( NoSuchMethodException e ) {

            messageProxy.error(
                    "The " + name + " class " + targetNodeName + " does not support recovery from XML.",
                    "Please notify Danny (provide him with the .xml file you just tried to load).",
                    "I Promise To Provide Danny With A Copy Of The XML File That I Just Tried To Load"
            );

        } catch ( InvocationTargetException e ) {

            Logger.logErr( "InvocationTargetException instantiating object", e );

            Throwable cause = e.getCause();
            if ( cause instanceof ObtuseXmlNodeException ) {

                messageProxy.error(
                        "Unable to create " + targetNodeName + " instance using provided .xml file.",
                        ObtuseXMLUtils.formatCause( cause ) + "<br>" +
                                "The .xml configuration save file is probably out-of-date or contains a syntax error.<br>" +
                                "Please notify Danny if you conclude that something else is wrong.",
                        "I Promise To Provide Danny With A Copy Of The XML File If I Conclude That Something Else Is Wrong"
                );

            } else {

                messageProxy.error(
                        "Unable to create " + targetNodeName + " instance using provided .xml file.",
                        ObtuseXMLUtils.formatCause(cause) + "<br>" +
                                "Please notify Danny.",
                        "I Promise To Provide Danny With A Copy Of The XML File That I Just Tried To Load"
                );

            }

        } catch ( InstantiationException e ) {

            messageProxy.error(
                    "Unable to create " + targetNodeName + " instance.",
                    e.getMessage() + "<br>" +
                            "Please notify Danny.",
                    "I Promise To Provide Danny With A Copy Of The XML File That I Just Tried To Load"
            );

        } catch ( IllegalAccessException e ) {

            messageProxy.error(
                    "Unable to create " + targetNodeName + " instance (illegal access exception).",
                    e.getMessage() + "<br>" +
                            "Please notify Danny.",
                    "I Promise To Provide Danny With A Copy Of The XML File That I Just Tried To Load"
            );

        }

        return configClassInstance;

    }

    private static String formatCause( final Throwable cause ) {

        return (
                cause.getMessage() == null
                        ? "No detail message provided."
                        : "Detailed error message was:  " + cause.getMessage()
        ) + "<br>";

    }

    public static Node findNode( final Node parentNode, final Class<?> targetClass )
            throws ObtuseXmlNodeException {

        return ObtuseXMLUtils.findNode( parentNode, targetClass.getSimpleName() );

    }

    public static Node findNode( final Node parentNode, final String targetNodeName )
            throws ObtuseXmlNodeException {

        return ObtuseXMLUtils.findNode(
                parentNode,
                new CheckNode() {

                    public boolean isThisTheNode( final Node candidateNode ) {

                        return targetNodeName.equals( candidateNode.getNodeName() );

                    }

                    public Node noteFailure()
                            throws ObtuseXmlNodeException {

                        throw new ObtuseXmlNodeException( targetNodeName + " node not found in " + parentNode.getNodeName() + " node." );

                    }

                }
        );

    }

    public static Node findNodeThatEndsWith( final Node parentNode, final String suffix )
            throws ObtuseXmlNodeException {

        return ObtuseXMLUtils.findNode(
                parentNode,
                new CheckNode() {

                    public boolean isThisTheNode( final Node candidateNode ) {

                        return candidateNode.getNodeName().endsWith( suffix );

                    }

                    public Node noteFailure()
                            throws ObtuseXmlNodeException {

                        throw new ObtuseXmlNodeException(
                                "No node found with name ending with \"" + suffix + "\" in " +
                                        parentNode.getNodeName() + " node."
                        );

                    }

                }
        );

    }

    public static Node findNode( final Node parentNode, final CheckNode test )
            throws ObtuseXmlNodeException {

        NodeList nodes = parentNode.getChildNodes();
        for ( int ix = 0; ix < nodes.getLength(); ix += 1 ) {

            Node candidateNode = nodes.item( ix );
            if ( test.isThisTheNode( candidateNode ) ) {

                return candidateNode;

            }

        }

        return test.noteFailure();

    }

    public static String constructAttributeAssignment( final String attributeName, final String attributeValue, final boolean mandatory )
            throws ObtuseXmlNodeException {

        if ( attributeValue == null ) {

            if ( mandatory ) {

                throw new ObtuseXmlNodeException( "required attribute \"" + attributeName + "\" not provided." );

            }

            return null;

        }

        return attributeName + "=\"" + attributeValue + "\"";

    }

    public static String constructAttributeAssignment( final String attributeName, final Boolean attributeValue, final boolean mandatory )
            throws ObtuseXmlNodeException {

        if ( attributeValue == null ) {

            if ( mandatory ) {

                throw new ObtuseXmlNodeException( "required attribute \"" + attributeName + "\" not provided." );

            }

            return null;

        }

        return attributeName + "=\"" + attributeValue + "\"";

    }

    public static String constructAttributeAssignment( final String attributeName, final Double attributeValue, final boolean mandatory )
            throws ObtuseXmlNodeException {

        if ( attributeValue == null ) {

            if ( mandatory ) {

                throw new ObtuseXmlNodeException( "required attribute \"" + attributeName + "\" not provided." );

            }

            return null;

        }

        return attributeName + "=\"" + attributeValue + "\"";

    }

    public static String constructAttributeAssignment( final String attributeName, final Integer attributeValue, final boolean mandatory )
            throws ObtuseXmlNodeException {

        if ( attributeValue == null ) {

            if ( mandatory ) {

                throw new ObtuseXmlNodeException( "required attribute \"" + attributeName + "\" not provided." );

            }

            return null;

        }

        return attributeName + "=\"" + attributeValue + "\"";

    }

    public static String constructAttributeAssignment( final String attributeName, final Date attributeValue, final boolean mandatory )
            throws ObtuseXmlNodeException {

        if ( attributeValue == null ) {

            if ( mandatory ) {

                throw new ObtuseXmlNodeException( "required attribute \"" + attributeName + "\" not provided." );

            }

            return null;

        }

        return attributeName + "=\"" + DateUtils.formatYYYY_MM_DD_HH_MM( attributeValue ) + "\"";

    }

}
