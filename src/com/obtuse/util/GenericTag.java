/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingBackReferenceable;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 A tag for something.
 <p>This is essentially a string although wrapping it within a custom class makes it a lot easier to ensure
 that something's tag doesn't get confused with something's name or value.</p>
 */

public class GenericTag
        extends GowingAbstractPackableEntity
        implements GowingBackReferenceable, Comparable<GenericTag> {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( GenericTag.class );

    private static final int VERSION = 1;

    private static final EntityName G_CATEGORY_TAG_NAME = new EntityName( "_ctn" );
    private static final EntityName G_TAG_NAME = new EntityName( "_tn" );

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;

        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;

        }

        @SuppressWarnings("RedundantThrows")
        @NotNull
        @Override
        public GowingPackable createEntity(
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        )
                throws GowingUnpackingException {

            String categoryTagName = bundle.MandatoryStringValue( G_CATEGORY_TAG_NAME );
            Optional<GenericTagCategory> optCategoryTag = findTagCategory( categoryTagName );

            GenericTagCategory categoryTag = optCategoryTag.orElseGet( () -> createNewTagCategory( categoryTagName ) );

            String tagName = bundle.MandatoryStringValue( G_TAG_NAME );
            GenericTag rval = alloc( categoryTag, tagName );

            return rval;

        }

    };

    public static final String TO_STRING_PREFIX = "tag:<";
    public static final String TO_STRING_SUFFIX = ">";

    public static class GenericTagCategory extends GenericTag {

        private GenericTagCategory( final @NotNull String tagName ) {
            super( TAG_CATEGORY_STRING, tagName );

        }

        private GenericTagCategory() {
            super( "", TAG_CATEGORY_STRING );
        }

    }

    private static final SortedMap<String, GenericTagCategory> s_knownTagCategories = new TreeMap<>( String::compareToIgnoreCase );

    /**
     A regular expression pattern that matches valid category tags.
     <p>A valid tag is series of two or more characters each matching the regular expression [a-zA-Z0-9]
     with underscore characters also allowed anywhere except as the first or last character of the tag.
     <p>Examples of valid tags:</p>
     <blockquote>
     <table>
     <tr><td>{@code AB}</td></tr>
     <tr><td>{@code A_B}</td></tr>
     <tr><td>{@code Alpha_Beta_Gamma}</td></tr>
     <tr><td>{@code 01}</td></tr>
     <tr><td>{@code 0_X}</td></tr>
     <tr><td>{@code 123_456_789}</td></tr>
     </table>
     </blockquote>
     <p>Examples of invalid tags:</p>
     <blockquote>
     <table>
     <tr><td>{@code _A}&nbsp;&nbsp;&nbsp;</td><td>(starts with an underscore)</td></tr>
     <tr><td>{@code B_}&nbsp;&nbsp;&nbsp;</td><td>(ends with an underscore)</td></tr>
     <tr><td>{@code A}&nbsp;&nbsp;&nbsp;</td><td>(too short)</td></tr>
     <tr><td>{@code 0-9}&nbsp;&nbsp;&nbsp;</td><td>(contains something that is not a letter, digit or underscore)</td></tr>
     </table>
     </blockquote>
     are all invalid tags.</p>
     <p>Things could get very ugly if this pattern is changed to allow characters other than {@code [a-zA-Z0-9_]}
     to appear in tags.</p>
     */

    public static final Pattern VALID_CATEGORY_TAG = Pattern.compile( "[a-zA-Z0-9][a-zA-Z0-9_]*[a-zA-Z0-9]" );

    public static final String TAG_CATEGORY_STRING = "TAG";

    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public static final GenericTagCategory TAG_CATEGORY = new GenericTagCategory();

    private static final TwoDimensionalSortedMap<String,String,GenericTag> s_knownTags = new TwoDimensionalTreeMap<>();

    private final GenericTagCategory _categoryTag;
    private final String _tagName;

    private GenericTag( final @NotNull String categoryName, final @NotNull String tagName ) {
        super( new GowingNameMarkerThing() );

        String who = this instanceof GenericTagCategory ? "GenericTagCategory" : "GenericTag";

        if ( categoryName.isEmpty() ) {

            if ( TAG_CATEGORY_STRING.equalsIgnoreCase( tagName ) ) {

                if ( this instanceof GenericTagCategory ) {

                    GenericTagCategory gtcThis = (GenericTagCategory)this;

                    _categoryTag = gtcThis;
                    _tagName = tagName;

                    if ( s_knownTagCategories.containsKey( _tagName ) ) {

                        throw new HowDidWeGetHereError( who + "(" + ObtuseUtil.enquoteToJavaString( tagName ) + "):  reserved tag already exists" );

                    }

                    System.out.println( "this tag is " + gtcThis );

                    s_knownTagCategories.put( _tagName, gtcThis );

                    return;

                } else {

                    throw new HowDidWeGetHereError(
                            who + "(" + ObtuseUtil.enquoteToJavaString( tagName ) + "):  " +
                            "reserved tags may only be created by the GenericTagCategory constructor"
                    );
                }

            } else {

                throw new HowDidWeGetHereError(
                        "GenericTag( \"\", " + ObtuseUtil.enquoteToJavaString( tagName ) + " ):  " +
                        "invalid reserved tag name " + ObtuseUtil.enquoteToJavaString( tagName ) + " " +
                        "(should have been caught earlier)"
                );

            }

        }

        _categoryTag = s_knownTagCategories.get( categoryName );

        if ( _categoryTag == null ) {

            throw new HowDidWeGetHereError(
                    "GenericTag( " + ObtuseUtil.enquoteToJavaString( categoryName ) +
                    ", " +
                    ObtuseUtil.enquoteToJavaString( tagName ) + "):  " +
                    "unknown tag category " + ObtuseUtil.enquoteToJavaString (categoryName ) + " " +
                    "should have been caught earlier)"
            );

        }

        _tagName = tagName;

    }

    @NotNull
    public GenericTag.GenericTagCategory getCategoryTag() {

        return _categoryTag;

    }

    /**
     Allocate a new tag category which does not already exist.
     @param categoryName the name of the new tag category.
     @return the newly created tag category.
     @throws IllegalArgumentException if the specified tag category name is invalid
     (not matched by {@link #VALID_CATEGORY_TAG}) or it already exists.
     */

    @NotNull
    public static GenericTagCategory createNewTagCategory( final @NotNull String categoryName ) {

        return maybeAllocTagCategory( "createNewTagCategory", categoryName, false, true );

    }

    /**
     Get an already existing tag category.
     @param categoryName the name of the existing tag category.
     @return the existing tag category.
     @throws IllegalArgumentException if the specified tag category name is invalid
     (not matched by {@link #VALID_CATEGORY_TAG}) or it does not already exist.
     */

    @NotNull
    public static GenericTagCategory getExistingTagCategory( final @NotNull String categoryName ) {

        return maybeAllocTagCategory( "getExistingTagCategory", categoryName, true, false );

    }

    /**
     Get a tag category which might already exist.
     <p>This is a kind off no-muss no-fuss laid back variant on the other {@code maybeAllocTagCategory} methods.</p>
     @param categoryName the name of the desired tag category.
     @return the desired tag category (created implicitly if it does not already exist).
     @throws IllegalArgumentException if the specified tag category name is invalid.
     */

    @NotNull
    public static GenericTagCategory maybeAllocTagCategory( final @NotNull String categoryName ) {

        return maybeAllocTagCategory( categoryName, false, false );

    }

    /**
     Get a tag category which might already exist.
     <p>See {@link #findTagCategory(String)} for a way to retrieve a tag category only if it already exists.</p>
     <p>See {@link #maybeAllocTagCategory(String)} for a way to get a tag category and create it if it doesn't already exist.</p>
     @param categoryName the name of the desired tag category.
     @param mustAlreadyExist {@code true} if the tag category must already exist; {@code false} otherwise.
     @param mustNotAlreadyExist {@code true} if the tag category must not already exist; {@code false} otherwise.
     @return the requested tag category (created if {@code mustAlreadyExist} and {@code mustNotAlreadyExist}
     are both satisfied and if it didn't already exist).
     @throws IllegalArgumentException if
     <ul>
     <li>the specified tag category name is invalid (not matched by {@link #VALID_CATEGORY_TAG})</li>
     <li>{@code mustAlreadyExist} is {@code true} and the tag does not already exist</li>
     <li>or if {@code mustNotAlreadyExist} is {@code true} and the tag does already exist</li>
     </ul>
     */

    @NotNull
    public static GenericTagCategory maybeAllocTagCategory(
            final @NotNull String categoryName,
            final boolean mustAlreadyExist,
            final boolean mustNotAlreadyExist
    ) {

        return maybeAllocTagCategory( "maybeAllocTagCategory", categoryName, mustAlreadyExist, mustNotAlreadyExist );

    }

    /**
     Get a tag category which might already exist.
     <p>This method does the heavy lifting of most of the public methods for retrieving tag category instances.</p>
     @param who who is calling this method (used in constructing {@link IllegalArgumentException} instances
     when necessary).
     @param categoryName the name of the desired tag category.
     @param mustAlreadyExist {@code true} if the tag category must already exist; {@code false} otherwise.
     @param mustNotAlreadyExist {@code true} if the tag category must not already exist; {@code false} otherwise.
     @return the requested tag category (created if {@code mustAlreadyExist} and {@code mustNotAlreadyExist}
     are both satisfied and if it didn't already exist).
     @throws IllegalArgumentException if
     <ul>
     <li>the specified tag category name is invalid (not matched by {@link #VALID_CATEGORY_TAG})</li>
     <li>{@code mustAlreadyExist} is {@code true} and the tag does not already exist</li>
     <li>or if {@code mustNotAlreadyExist} is {@code true} and the tag does already exist</li>
     </ul>
     */

    @NotNull
    private static GenericTagCategory maybeAllocTagCategory(
            final @NotNull String who,
            final @NotNull String categoryName,
            final boolean mustAlreadyExist,
            final boolean mustNotAlreadyExist
    ) {

        if ( !isTagNameValid( categoryName ) ) {

            throw new IllegalArgumentException(
                    "GenericTag." + who + "(" + ObtuseUtil.enquoteToJavaString( categoryName ) + "):  " +
                    "invalid category name"
            );

        }

        GenericTagCategory genericTag = s_knownTagCategories.get( categoryName );
        if ( genericTag == null ) {

            if ( mustAlreadyExist ) {

                throw new IllegalArgumentException(
                        "GenericTag." + who + "(" + ObtuseUtil.enquoteToJavaString( categoryName ) + "):  " +
                        "tag category named " +
                        ObtuseUtil.enquoteToJavaString( categoryName ) + " does not already exist"
                );

            }

            genericTag = new GenericTagCategory( categoryName );

            s_knownTagCategories.put( categoryName, genericTag );

        } else {

            if ( mustNotAlreadyExist ) {

                throw new IllegalArgumentException(
                        "GenericTag." + who + "(" + ObtuseUtil.enquoteToJavaString( categoryName ) + "):  " +
                        "tag category named " +
                        ObtuseUtil.enquoteToJavaString( categoryName ) + " already exists"
                );

            }

        }

        return genericTag;

    }

    /**
     Determine if a proposed tag or category tag name is valid.
     @param proposedTagName the proposed tag name or category tag name.
     @return {@code true} if the name matches the {@link #VALID_CATEGORY_TAG} pattern; {@code false} otherwise.
     */

    public static boolean isTagNameValid( final @NotNull String proposedTagName ) {

        Matcher m = VALID_CATEGORY_TAG.matcher( proposedTagName );
        //noinspection RedundantIfStatement
        if ( m.matches() ) {

            return true;

        } else {

            return false;

        }

    }

    /**
     Verify that a proposed tag name is valid.
     <p>Throws an {@link IllegalArgumentException} if the tag name is not matched by the {@link #VALID_CATEGORY_TAG} pattern.</p>
     @param who the identity of the called (used when constructing the {@code IllegalArgumentException}).
     @param proposedTagName the proposed tag name.
     @throws IllegalArgumentException if {@code proposedTagName} is not matched by the {@link #VALID_CATEGORY_TAG} pattern.
     */
    public static void checkTagNameValid( final @NotNull String who, final @NotNull String proposedTagName ) {

        if ( isTagNameValid( proposedTagName ) ) {

            return;

        }

        throw new IllegalArgumentException( who + ":  invalid tag name " + ObtuseUtil.enquoteToJavaString( proposedTagName ) );

    }

    /**
     Find and return a tag category if it currently exists.
     <p>This method does NOT check the validity of the specified tag category name and never throws any
     checked or unchecked exceptions
     (unless something is seriously broken in the implementation of this class/method).</p>
     @param categoryName the name of the desired tag category.
     @return an {@link Optional}{@code <}{@link GenericTag}{@code >} instance which contains the
     requested tag category if it currently exists and is empty otherwise.
     */

    public static Optional<GenericTagCategory> findTagCategory( final @NotNull String categoryName ) {

        GenericTagCategory tag = s_knownTagCategories.get( categoryName );

        return Optional.ofNullable( tag );

    }

    /**
     Determine if a tag category exists.
     <p>This method is EXACTLY equivalent to
     <blockquote>{@code return }{@link #findTagCategory}{@code ( categoryName ).isPresent();}
     </blockquote>
     </p>
     @param categoryName the tag category's name.
     @return {@code true} if it currently exists; {@code false} otherwise.
     */

    @SuppressWarnings("unused")
    public boolean doesTagCategoryExist( final @NotNull String categoryName ) {

        return findTagCategory( categoryName ).isPresent();

    }

    public static GenericTag alloc( final @NotNull GenericTagCategory tagCategory, final @NotNull String tagName ) {

        String tagCategoryString = tagCategory.getTagName();

        if ( TAG_CATEGORY_STRING.equals( tagCategoryString ) ) {

            throw new IllegalArgumentException(
                    "GenericTag.alloc( " +
                    ObtuseUtil.enquoteToJavaString( tagCategoryString ) + ", " +
                    ObtuseUtil.enquoteToJavaString( tagName ) + " ):  " +
                    "tag category \"" + ObtuseUtil.enquoteToJavaString( tagCategoryString ) +
                    " is reserved - use GenericTag.maybeAllocTagCategory to get your own tag category"
            );

        }

        GenericTag rval = s_knownTags.computeIfAbsent( tagCategoryString, tagName,
                                                       GenericTag::new
        );

        return rval;

    }

    @NotNull
    public String getTagName() {

        return _tagName;

    }

    public String toString() {

        return wrap();

    }

    @NotNull
    public String wrap() {

        return TO_STRING_PREFIX +
               ObtuseUtil.enquoteJavaObject( _categoryTag.getTagName() ) + "/" +
               ObtuseUtil.enquoteJavaObject( _tagName ) +
               TO_STRING_SUFFIX;

    }

    @Override
    public int compareTo( @NotNull final GenericTag rhs ) {

        /*
         Verify that instance ids are equal if-and-only-if the category tags are equal.
         */

        if ( getInstanceId().equals( rhs.getInstanceId() ) ) {

            //noinspection DuplicateExpressions
            if ( !toString().equals( rhs.toString() ) ) {

                String msg = "GenericTag.compareTo( this=" + this + ", rhs=" + rhs + "):  " +
                             "instance ids are equal but category tags are not " +
                             "(absolutely impossible but here we are) - " +
                             "instance id=" + getInstanceId() + ", " +
                             "this.getCategoryTag()=" + this.getCategoryTag() + ", " +
                             "rhs.getCategoryTag()=" + rhs.getCategoryTag();

                throw new HowDidWeGetHereError( msg );

            }

        } else //noinspection DuplicateExpressions
            if ( toString().equals( rhs.toString() ) ) {

            Logger.logMsg( "checking again:  " + getCategoryTag().equals( rhs.getCategoryTag() ) );

            String msg = "GenericTag.compareTo( this=" + this + ", rhs=" + rhs + "):  " +
                         "category tags are equal but instance ids are not (invalid but not impossible) - " +
                         "this.getCategoryTag()=" + this.getCategoryTag() + " and " +
                         "this.getTagName()=" + ObtuseUtil.enquoteToJavaString( this.getTagName() ) + ", " +
                         "rhs.getCategoryTag()=" + rhs.getCategoryTag() + " and " +
                         "rhs.getTagName()=" + ObtuseUtil.enquoteToJavaString( rhs.getTagName() ) + ", " +
                         "this.getInstanceId()=" + getInstanceId() + ", " +
                         "rhs.getInstanceId()=" + rhs.getInstanceId();

            throw new HowDidWeGetHereError( msg );

        }

        return toString().compareTo( rhs.toString() );

    }

    public boolean equals( final Object rhs ) {

        return rhs instanceof GenericTag && compareTo( (GenericTag) rhs ) == 0;

    }

    public int hashCode() {

        return getInstanceId().hashCode();
    }

    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, @NotNull final GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleRoot( packer ),
                packer.getPackingContext()
        );

        bundle.addStringHolder( G_CATEGORY_TAG_NAME, _categoryTag.getTagName(), true );
        bundle.addStringHolder( G_TAG_NAME, _tagName, true );

        return bundle;

    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) throws GowingUnpackingException {

        return true;
    }

    private static int s_errCount = 0;

    public interface DoitVoid {

        void doit();

    }

    public interface DoitObject<T> {

        T doit();

    }

    public interface DoitNotNull<T> {

        @NotNull T doit();

    }

    @SuppressWarnings("unused")
    private static void doitVoid(
            final @NotNull String what,
            @SuppressWarnings("rawtypes") final @NotNull DoitObject func
    ) {

        try {

            func.doit();

            Logger.logMsg( what + ":  worked" );

        } catch ( Throwable e ) {

            Logger.logErr( what + ":  caught unexpected exception ", e );
            s_errCount += 1;

            ObtuseUtil.doNothing();

        } finally {

            System.err.flush();
            System.out.flush();
        }
    }

    private static <T> T doitObject(
            @SuppressWarnings("SameParameterValue") final @NotNull String what,
            final @NotNull DoitObject<T> func
    ) {

        try {

            T rval = func.doit();

            Logger.logMsg( what + ":  worked, rval=" + rval );

            return rval;

        } catch ( Throwable e ) {

            Logger.logErr( what + ":  caught unexpected exception ", e );
            s_errCount += 1;

            ObtuseUtil.doNothing();

            return null;

        } finally {

            System.err.flush();
            System.out.flush();
        }

    }

    @NotNull
    private static <T> T doitNotNull(
            final @NotNull String what,
            final @NotNull DoitNotNull<T> func,
            final @NotNull T defaultValue
    ) {

        try {

            T rval = func.doit();

            Logger.logMsg( what + ":  worked, rval=" + rval );

            return rval;

        } catch ( Throwable e ) {

            Logger.logErr( what + ":  caught unexpected exception ", e );
            s_errCount += 1;

            ObtuseUtil.doNothing();

            return defaultValue;

        } finally {

            System.err.flush();
            System.out.flush();
        }

    }

    private static void boomOk(
            final @NotNull String what,
            @SuppressWarnings("rawtypes") final @NotNull DoitObject func,
            @SuppressWarnings("SameParameterValue") final @NotNull Class<? extends Throwable> expectedError
    ) {

        try {

            Object rval = func.doit();

            Logger.logErr( what + ":  should have caught an exception but got return value of " + rval );
            s_errCount += 1;

            ObtuseUtil.doNothing();

        } catch ( Throwable e ) {

            if ( expectedError.isAssignableFrom( e.getClass() ) ) {

                Logger.logMsg( what + ":  worked - caught expected exception " + e );

            } else {

                Logger.logErr( what + ":  caught unexpected exception ", e );
                s_errCount += 1;

                ObtuseUtil.doNothing();

            }

        } finally {

            System.err.flush();
            System.out.flush();
        }
    }

    @SuppressWarnings("unused")
    private static void boomOk(
            final @NotNull String what,
            final @NotNull DoitVoid func,
            @SuppressWarnings("SameParameterValue") final @NotNull Class<? extends Throwable> expectedError
    ) {

        try {

            func.doit();

            Logger.logErr( what + ":  should have caught an exception but got clean return" );
            s_errCount += 1;

            ObtuseUtil.doNothing();

        } catch ( Throwable e ) {

            if ( expectedError.isAssignableFrom( e.getClass() ) ) {

                Logger.logMsg( what + ":  worked - caught expected exception " + e );

                ObtuseUtil.doNothing();

            } else {

                Logger.logErr( what + ":  caught unexpected exception (should have caught " + expectedError.getName() + ") ", e );
                s_errCount += 1;

                ObtuseUtil.doNothing();

            }

        } finally {

            System.err.flush();
            System.out.flush();
        }

    }

    public static void main( final String[] args ) {

        BasicProgramConfigInfo.init( "Obtuse", "Pipestone", "GenericTag" );

        @NotNull GenericTagCategory tagCategory = doitNotNull(
                "create testing category",
                () -> createNewTagCategory( "testing" ),
                TAG_CATEGORY
        );

        @SuppressWarnings("unused") GenericTag tagCategoryAugmented = doitObject(
                "create augmented testing category",
                () -> getExistingTagCategory( "testing" )
        );

        boomOk(
                "duplicate tag category",
                () -> createNewTagCategory( "testing" ),
                IllegalArgumentException.class
        );

        boomOk( "create bogus tag tag", () -> alloc( TAG_CATEGORY, "hello" ), IllegalArgumentException.class );
        doitNotNull( "create hello tag", () -> alloc( tagCategory, "hello" ), tagCategory );
        doitNotNull( "create hello tag", () -> alloc( tagCategory, "hello" ), tagCategory );
        doitNotNull( "create hello tag", () -> alloc( tagCategory, TO_STRING_PREFIX + "hello" ), tagCategory );
        doitNotNull( "create hello tag", () -> alloc( tagCategory, TO_STRING_PREFIX + TO_STRING_PREFIX + "world" ), tagCategory );

        ObtuseUtil.doNothing();

        if ( s_errCount > 0 ) {

            Logger.logErr( "GenericTag:  failed (" + ObtuseUtil.pluralize( s_errCount, "error" ) + ")" );

        }

        Logger.logMsg( "0 s:  " + ObtuseUtil.pluralize( 0, "critters", "mouse", "mice" ) );
        Logger.logMsg( "1 s:  " + ObtuseUtil.pluralize( 1, "critters", "mouse", "mice" ) );
        Logger.logMsg( "2 s:  " + ObtuseUtil.pluralize( 2, "critters","mouse", "mice" ) );
        Logger.logMsg( "0 s:  " + ObtuseUtil.pluralize( 0, "mouse", "mice" ) );
        Logger.logMsg( "1 s:  " + ObtuseUtil.pluralize( 1, "mouse", "mice" ) );
        Logger.logMsg( "2 s:  " + ObtuseUtil.pluralize( 2, "mouse", "mice" ) );
        Logger.logMsg( "0 s:  " + ObtuseUtil.pluralize( 0, "bird" ) );
        Logger.logMsg( "1 s:  " + ObtuseUtil.pluralize( 1, "bird" ) );
        Logger.logMsg( "2 s:  " + ObtuseUtil.pluralize( 2, "bird" ) );
        Logger.logMsg( "0 s:  " + ObtuseUtil.pluralizes( 0, "fish" ) );
        Logger.logMsg( "1 s:  " + ObtuseUtil.pluralizes( 1, "fish" ) );
        Logger.logMsg( "2 s:  " + ObtuseUtil.pluralizes( 2, "fish" ) );

    }

}
