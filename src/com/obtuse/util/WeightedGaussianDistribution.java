/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.exceptions.ObtuseXmlNodeException;
import org.w3c.dom.Node;

/**
 * A gaussian distribution with a weighting factor.
 */

@SuppressWarnings("UnusedDeclaration")
public class WeightedGaussianDistribution extends GaussianDistribution implements InstanceFromXML {

    private final double _weight;

    public WeightedGaussianDistribution( final double weight, final double center, final double standardDeviation ) {

        super( center, standardDeviation );

        if ( weight < 0.0 ) {

            throw new IllegalArgumentException(
                    "negative weight (" + weight + ") not allowed in WeightedGaussianDistribution"
            );

        }

        _weight = weight;

    }

    public WeightedGaussianDistribution( final MessageProxy messageProxy, final Node parentNode, final Node targetNode )
            throws ObtuseXmlNodeException {

        super(
                ObtuseXMLUtils.getMandatoryDoubleAttributeValue( targetNode, "center" ),
                ObtuseXMLUtils.getMandatoryDoubleAttributeValue( targetNode, "stddev" )
        );

        _weight = ObtuseXMLUtils.getMandatoryDoubleAttributeValue( targetNode, "weight" );

    }

    public double getWeight() {

        return _weight;

    }

    public void emitAsXml( final NestedXMLPrinter ps ) {

        ps.emitTag(
                "WeightedGaussianDistribution",
                new String[] {
                        "weight=\"" + _weight + "\"",
                        "center=\"" + getCenter() + "\"",
                        "stddev=\"" + getStandardDeviation() + "\""
                }
        );

    }

    public String toString() {

        return "WeightedGaussianDistribution( " + _weight + ", " + getCenter() + ", " + getStandardDeviation() +
               " )";

    }

}
