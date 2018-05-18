/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

public class ArgParserExample {

    @SuppressWarnings({ "ClassWithoutToString", "AssignmentToStaticFieldFromInstanceMethod" })
    private static class ExampleArgParser extends ArgParser {

        private static String s_deploymentName = null;

        @SuppressWarnings("UnusedDeclaration")
        private static int s_messageIdPrefix = 0;

        @SuppressWarnings("UnusedDeclaration")
        private static Integer s_podNumber = null;

        private static int s_rmiRegistryPort = 0;

        private static int s_tracePort = -1;

        private static String s_callbackHostname = null;

	private static String s_supportEmailAddress = null;

	private ExampleArgParser() {
            //noinspection ClassWithoutToString
            super(
                    new Arg[] {

                            new ArgInt( "-messageIdPrefix" ) {

                                public void process( final String keyword, final int arg ) {

                                    ExampleArgParser.s_messageIdPrefix = arg;

                                }

                            },

                            new ArgString( "-deployment" ) {

                                public void process( @NotNull final String keyword, @NotNull final String arg ) {

                                    ExampleArgParser.s_deploymentName = arg;

                                }

                            },

                            new ArgInt( "-pod" ) {

                                public void process( final String keyword, final int arg ) {

                                    ExampleArgParser.s_podNumber = arg;

                                }

                            },

                            new ArgInt( "-traceport" ) {

                                public void process( final String keyword, final int arg ) {

                                    ExampleArgParser.s_tracePort = arg;

                                }

                            },

                            new ArgInt( "-rmiregistryport" ) {

                                public void process( final String keyword, final int arg ) {

                                    ExampleArgParser.s_rmiRegistryPort = arg;

                                }

                            },

                            new ArgString( "-supportEmailAddress" ) {

                                public void process( @NotNull final String keyword, @NotNull final String arg ) {

                                    s_supportEmailAddress = arg;

                                }

                            },

                            new ArgString( "-callbackHostname" ) {

                                public void process( @NotNull final String keyword, @NotNull final String arg ) {

                                    ExampleArgParser.s_callbackHostname = arg;

                                }

                            }

                    }
            );

        }

    }

    private ArgParserExample() {
        super();

    }

    @SuppressWarnings( { "MagicNumber" })
    public static void main( final String[] args ) {

        //noinspection ClassWithoutToString,ClassWithoutToString
        ExampleArgParser argParser = new ExampleArgParser();

        if ( !argParser.parse( args ) ) {

            System.exit( 1 );

        }

        if ( ExampleArgParser.s_supportEmailAddress == null ) {

            Logger.logErr( "ArgParserExample:  -supportEmailAddress must be specified" );

        }

        if ( ExampleArgParser.s_callbackHostname == null ) {

            Logger.logErr( "ArgParserExample:  callback hostname must be specified" );

        }

        boolean fatalError = false;
        if ( ExampleArgParser.s_rmiRegistryPort <= 1023 ) {

            Logger.logErr( "ArgParserExample:  -rmiregistryport must be at least 1024" );
            fatalError = true;

        }

        if ( ExampleArgParser.s_tracePort == 0 ) {

            Logger.logErr( "ArgParserExample:  don't know which trace port to use" );
            fatalError = true;

        }

        if ( ExampleArgParser.s_deploymentName == null ) {

            Logger.logErr( "ArgParserExample:  -deployment option must be specified" );
            fatalError = true;

        }

        if ( fatalError ) {

            System.exit( 1 );

        }

        // That's it for parsing and checking args - the 'real work' now begins . . .

    }

}
