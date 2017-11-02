/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package org.apache.commons.text.similarity;

import com.obtuse.util.*;

import java.util.Comparator;

/**
 Created by danny on 2017/09/14.
 */

public class TestDriveLevenshteinDistance {

    public static void main( String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "ObtuseUtil", "testing", null );

        String[] words = { "boulet", "boutin", "boulay", "boule", "cat", "caatct" };
        LevenshteinDistance ld = new LevenshteinDistance();
        for ( String w1 : words ) {

            TreeSorter<Double, String> results = new TreeSorter<>();
//                    new Comparator<Double>() {
//                        @Override
//                        public int compare( final Double o1, final Double o2 ) {
//
//                            return Double.compare( o2.doubleValue(), o1.doubleValue() );
//
//                        }
//                    }
//            );

            for ( String w2 : words ) {

                double measure = ld.apply( w1, w2 );
//                Logger.logMsg( "computeSimilarityMeasure( " + enquoteToJavaString( w1 ) + ", " + enquoteToJavaString( w2 ) + ":  " +
//                               measure );
                results.add( measure, w1 + "<>" + w2 );

            }

            Logger.logMsg( w1 + ":" );

            for ( double m : results.keySet() ) {

                StringBuilder sb = new StringBuilder();
                String comma = "";
                for ( String w : results.getValues( m ) ) {

                    sb.append( comma ).append( w );
                    comma = ", ";

                }

                Logger.logMsg( ObtuseUtil.lpad( m, 8, 4 ) + ":  " + sb );

            }

            Logger.logMsg( "---" );

        }

        long startTime = System.currentTimeMillis();
        int wordCount = 0;
        int trials = 100*1000*1000;

        for ( String w1 : NounsList.getAllWords() ) {

            for ( String w2 : NounsList.getAllWords() ) {

                ld.apply( w1, w2 );
                wordCount += 1;

            }

            if ( wordCount > trials ) {

                break;

            }

        }

        long endTime = System.currentTimeMillis();
        System.out.println(
                "" + ObtuseUtil.readable( trials ) + " in " + DateUtils.formatDuration( (endTime - startTime), 3 ) + " for " +
                ObtuseUtil.readable( (int)( trials / ( (endTime - startTime) / 1000.0 ) ) ) + " measurements per second"
        );

    }

}
