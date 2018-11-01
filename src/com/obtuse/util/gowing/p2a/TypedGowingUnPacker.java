package com.obtuse.util.gowing.p2a;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.TreeSorter;
import com.obtuse.util.gowing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;
import java.util.Optional;

/**
 Simplified interface to {@link StdGowingUnPacker}.
 */

public class TypedGowingUnPacker<T extends GowingPackable> extends StdGowingUnPacker {

    private final EntityName _expectedEntity;
    private boolean _parsed = false;
    private GowingUtil.BasicUnpackingResult _basicUnPackingResult;

//    /**
//     Create a 'standard' text-oriented unpacker.
//
//     @param typeIndex a table of the known [un]packable entity types.
//     Every top-level entity type encountered by this unpacker must have an entry in this type index to allow this unpacker
//     to know how to
//     instantiate the entity.
//     @param inputFile the file being read from.
//     <p/>While presumably the norm, it seems unreasonable to assume that this parameter will always be non-null.
//     For example, someone might want to unpack the contents of a byte array or some other in-memory object.
//     */
//
//    @SuppressWarnings("unused")
//    public TypedGowingUnPacker(
//            final @NotNull EntityName expectedEntity,
//            final GowingTypeIndex typeIndex,
//            final @NotNull File inputFile
//    ) throws FileNotFoundException {
//        this(
//                expectedEntity,
//                inputFile,
//                new LineNumberReader( new FileReader( inputFile ) ),
//                new StdGowingUnPackerContext( typeIndex )
//        );
//
//    }

    /**
     Create a 'standard' text-oriented unpacker.

     @param typeIndex a table of the known [un]packable entity types.
     Every entity type encountered by this unpacker at the top level of the input stream (i.e. as an actual entity to be
     instantiated as opposed to
     a super-type of an entity to be instantiated) must have an entry in this type index to allow this unpacker to know
     how to
     instantiate the entity.
     @param inputFile the file being read from.
     <p/>While presumably the norm, it seems unreasonable to assume that this parameter will always be non-null.
     For example, someone might want to unpack the contents of a byte array or some other in-memory object.
     @param reader    where the data is actually coming from.
     */

    @SuppressWarnings("unused")
    public TypedGowingUnPacker(
            @NotNull final EntityName expectedEntity,
            @NotNull final GowingTypeIndex typeIndex,
            @Nullable final File inputFile,
            @NotNull final Reader reader
    ) {
        this(
                expectedEntity,
                inputFile,
                reader instanceof LineNumberReader ? (LineNumberReader)reader : new LineNumberReader( reader ),
                new StdGowingUnPackerContext( typeIndex )
        );

    }

    /**
     Create a 'standard' text-oriented unpacker.

     @param inputFile       the file being read from.
     <p/>While presumably the norm, it seems unreasonable to assume that this parameter will always be non-null.
     For example, someone might want to unpack the contents of a byte array or some other in-memory object.
     @param reader          where the data is actually coming from.
     @param unPackerContext the context within which this operation is operating.
     <p/>Mostly a table of known {@link GowingPackable} entities and a {@link GowingTypeIndex} describing how to
     instantiate entities found in the input stream.
     */

    @SuppressWarnings({ "WeakerAccess", "RedundantThrows" })
    public TypedGowingUnPacker(
            @NotNull final EntityName expectedEntity,
            @Nullable final File inputFile,
            @NotNull final LineNumberReader reader,
            @NotNull final GowingUnPackerContext unPackerContext
    ) {
        super( inputFile, reader, unPackerContext );

        _expectedEntity = expectedEntity;

    }

    public boolean isParsed() {

        return _parsed;

    }

    /**
     Parse the file.
     @param what a description of what we're doing (used in messages and such).
     @return {@code true} if the parse worked; {@code false} otherwise.
     <ul>
     <li>Before this method is called, calls to {@link #getMandatoryBasicUnPackingResult()} will
     <u>always</u> fail and calls to {@link #getBasicUnPackingResult()} will <u>always</u> return
     {@link Optional}{@code .empty()} because those methods will not have a
     {@link GowingUtil.BasicUnpackingResult} instance to return.</li>
     <li>After this method returns, calls to {@link #getMandatoryBasicUnPackingResult()} will
     <u>always</u> succeed and calls to {@link #getBasicUnPackingResult()} will <u>always</u> return
     {@link Optional} instance containing a {@link GowingUtil.BasicUnpackingResult} because those methods
     will have a {@link GowingUtil.BasicUnpackingResult} instance to return.</li>
     </ul>
     */

    public boolean parse( @NotNull final String what ) {

        if ( _parsed ) {

            throw new IllegalArgumentException( "TypedGowingUnPacker.parse:  attempt to parse more than once" );

        } else {

            try {

                _basicUnPackingResult = new GowingUtil.BasicUnpackingResult(
                        what,
                        unPack()
                );

                _parsed = true;

                return true;

            } catch ( GowingUnpackingException e ) {

                Logger.logErr( "com.obtuse.util.gowing.p2a.GowingUnpackingException caught", e );

                _basicUnPackingResult = new GowingUtil.BasicUnpackingResult( what, e );

                return false;

            } catch ( IOException e ) {

                Logger.logErr( "java.io.IOException caught", e );

                _basicUnPackingResult = new GowingUtil.BasicUnpackingResult( what, e );

                return false;

            }

        }

    }

    private void checkParsed( String who ) {

        if ( !_parsed ) {

            throw new IllegalArgumentException( "TypedGowingUnPacker." + who + ":  you must call parse() first" );

        }

    }

    /**
     Get the values associated with the {@link EntityName} instance provided when this instance was created.
     Get the {@code T} instances found by the earlier call to {@link #parse(String)}.
     <p>Note that these are the {@code T} instances associated with
     the {@link EntityName} instance provided when this instance was created.
     @return a {@link Optional}{@code <T>} containing the values associated with the {@link EntityName}
     provided when this instance was created (returns {@code Optional.empty()} if no such values were found
     by the earlier call to {@link #parse(String)}.
     @throws IllegalArgumentException if {@link #parse(String)} has not yet been called.
     */

    public Optional<List<T>> getOptResults() {

        checkParsed( "getOptResults" );

        TreeSorter<EntityName, GowingPackable> namedClasses = _basicUnPackingResult.getMandatoryUnpackedEntities().getNamedClasses();
        if ( namedClasses.containsKey( _expectedEntity ) ) {

            @SuppressWarnings("unchecked") List<T> values = (List<T>)namedClasses.getValues( _expectedEntity );
            return Optional.of( values );

        } else {

            return Optional.empty();

        }

    }

    /**
     Get the number of {@code T} instances that a call to {@link #getOptResults()} will return if called now.
     @return the number of {@code T} instances that a call to {@code getOptResults()} would return if called now:
     <ul>
     <li>{@code -1} if {@code getOptResults()} would return {@code Optional.empty()}</li>
     <li>the number of {@code T} instances if {@code getOptResults()} would return a {@code Optional} containing a list of {@code T} instances.</li>
     </ul>
     <p>Note that {@code -1} is returned if the earlier call to {@link #parse(String)} returned {@code false}.</p>
     <p>Also note that a call to {@link #getOptSingleResult()} will throw an {@link IllegalArgumentException} exception
     if a call to this method returns a count higher than {@code 1} then a call to {@link #getOptSingleResult()} will throw
     an {@link IllegalArgumentException}.
     </p>
     @throws IllegalArgumentException if this method is called before the {@link #parse(String)} method has been called.
     */

    public int getResultCount() {

        checkParsed( "getResultCount" );

        Optional<List<T>> optResults = getOptResults();

        return optResults.map( List::size ).orElse( 0 );

    }

    @SuppressWarnings("unused")
    public Optional<T> getOptSingleResult() {

        checkParsed( "getOptSingleResult" );

        Optional<List<T>> optValues = getOptResults();
        if ( optValues.isPresent() ) {

            List<T> values = optValues.get();
            if ( values.size() > 1 ) {

                throw new IllegalArgumentException(
                        "TypedGowingUnPacker.getSingleValue:  key " +
                        ObtuseUtil.enquoteJavaObject( _expectedEntity ) +
                        " does not have a unique value (it has " + values.size() +
                        " values in this result)"
                );

            } else {

                return Optional.of( values.get(0) );

            }

        }

        return Optional.empty();

    }

    @SuppressWarnings("unused")
    public Optional<GowingUtil.BasicUnpackingResult> getBasicUnPackingResult() {

        checkParsed( "getBasicUnPackingResult" );

        return Optional.ofNullable( _basicUnPackingResult );

    }

    @NotNull
    public GowingUtil.BasicUnpackingResult getMandatoryBasicUnPackingResult() {

        return _basicUnPackingResult;

    }

    public String toString() {

        @NotNull Optional<File> optInputFile = getOptInputFile();
        if ( _parsed ) {

            return "TypedGowingUnPacker( " +
                   "inputFile=" + ObtuseUtil.enquoteJavaObject( optInputFile.orElse( null ) ) +
                   ", basicResult=" + _basicUnPackingResult +
                   " )";

        } else {

            return "TypedGowingUnPacker( " +
                   "inputFile=" + ObtuseUtil.enquoteJavaObject( optInputFile.orElse( null ) ) +
                   ", not parsed (yet) )";

        }

    }

}
