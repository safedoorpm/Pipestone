package com.obtuse.util.junit;

import com.obtuse.util.ObtuseUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;

/*
 * Copyright © 2012 Obtuse Systems Corporation
 */

/**
 * %%% Something clever goes here.
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class ObtuseUtilTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetSerializedVersion() throws Exception {

    }

    @Test
    public void testWriteSerializableObjectToFile_Serializable_File_boolean() throws Exception {

    }

    @Test
    public void testWriteSerializableObjectToFile_Serializable_File() throws Exception {

    }

    @Test
    public void testRecoverSerializedVersion_File() throws Exception {

    }

    @Test
    public void testRecoverSerializedVersion_File_boolean() throws Exception {

    }

    @Test
    public void testRecoverSerializedVersion_byteArray() throws Exception {

    }

    @Test
    public void testRecoverSerializedVersion_InputStream() throws Exception {

    }

    @Test
    public void testRecoverSerializedVersion_byteArray_boolean() throws Exception {

    }

    @Test
    public void testReadEntireFile_String_int_boolean() throws Exception {

    }

    @Test
    public void testReadEntireFile_File_int_boolean() throws Exception {

    }

    @Test
    public void testReadEntireStream() throws Exception {

    }

    @Test
    public void testWriteBytesToFile_byteArray_String_boolean() throws Exception {

    }

    @Test
    public void testWriteBytesToFile_byteArray_File_boolean() throws Exception {

    }

    @Test
    public void testWriteBytesToStream() throws Exception {

    }

    @Test
    public void testGetSerializedSize() throws Exception {

    }

    @Test
    public void testValidateArgs() throws Exception {

    }

    @Test
    public void testReadable_long() throws Exception {

    }

    @Test
    public void testLpadReadable_long_int() throws Exception {

    }

    @Test
    public void testLpadReadable_float_int_int() throws Exception {

    }

    @Test
    public void testLpadReadable_double_int_int() throws Exception {

    }

    @Test
    public void testRpadReadable_long_int() throws Exception {

    }

    @Test
    public void testRpadReadable_float_int_int() throws Exception {

    }

    @Test
    public void testRpadReadable_double_int_int() throws Exception {

    }

    @Test
    public void testLpadReadable0_float_int_int() throws Exception {

    }

    @Test
    public void testLpadReadable0_double_int_int() throws Exception {

    }

    @Test
    public void testRpadReadable0_float_int_int() throws Exception {

    }

    @Test
    public void testRpadReadable0_double_int_int() throws Exception {

    }

    @Test
    public void testLpad_float_int_int() throws Exception {

    }

    @Test
    public void testLpad_double_int_int() throws Exception {

    }

    @Test
    public void testLpad0_double_int_int() throws Exception {

    }

    @Test
    public void testLpad_String_int_int() throws Exception {

    }

    @Test
    public void testGeneratePaddingString() throws Exception {

    }

    @Test
    public void testLpad_String_int() throws Exception {

    }

    @Test
    public void testLpad_long_int_char() throws Exception {

    }

    @Test
    public void testLpad_long_int() throws Exception {

    }

    @Test
    public void testRpad_String_int_char() throws Exception {

    }

    @Test
    public void testRpad_String_int() throws Exception {

    }

    @Test
    public void testRpad_long_int_char() throws Exception {

    }

    @Test
    public void testRpad_long_int() throws Exception {

    }

    @Test
    public void testReplicate_String_count() throws Exception {

    }

    @Test
    public void testHexvalue_long() throws Exception {

    }

    @Test
    public void testHexvalue_int() throws Exception {

    }

    @Test
    public void testHexvalue_byte() throws Exception {

    }

    @Test
    public void testHexvalue_byteArray() throws Exception {

    }

    @Test
    public void testSafeSleepMillis() throws Exception {

    }

    @Test
    public void testDump() throws Exception {

    }

    @Test
    public void testHtmlEscape() throws Exception {

    }

    @Test
    public void testCloseQuietly_Closeable() throws Exception {

    }

    @Test
    public void testCloseQuietly_ServerSocket() throws Exception {

    }

    @Test
    public void testCloseQuietly_Socket() throws Exception {

    }

    @Test
    public void testCloseQuietly_ZipFile() throws Exception {

    }

    @Test
    public void testCloseQuietly_ResultSet() throws Exception {

    }

    @Test
    public void testCloseQuietly_PreparedStatement() throws Exception {

    }

    @Test
    public void testCloseQuietly_PostgresConnection() throws Exception {

    }

    @Test
    public void testDoNothing() throws Exception {

    }

    @Test
    public void testEnquoteForCSV() throws Exception {

    }

    @Test
    public void testEnquoteForJavaString() throws Exception {

    }

    /**
     * MD5 checksum of "hello world" computed using the command
     * <blockquote>$ echo -n "hello world" | md5</blockquote>
     * on a Mac running Mac OS X 10.8.2
     */

    private static String s_expectedMd5Value = "5eb63bbbe01eeed093cb22bb8f5acdc3";
    @Test
    public void testComputeMD5_File() throws Exception {

        // Verify that we can correctly compute the MD5 checksum of a file containing "hello world" (no trailing \n).

        // This may seem like an overly simplistic test.  Please keep in mind that we are NOT testing
        // any particular Java implementation of the MD5 algorithm.  Rather, we are testing that
        // the ObtuseUtil#computeMD5(java.io.InputStream) method correctly invokes the Java MD5 mechanism.

        File testFile = new File( "ObtuseUtilTest-MD5-testdata.txt" );
        if (
                ObtuseUtil.writeBytesToFile(
                        "hello world".getBytes( "UTF-8" ),
                        testFile,
                        false
                )
        ) {

            Assert.assertEquals( ObtuseUtilTest.s_expectedMd5Value, ObtuseUtil.computeMD5( testFile ) );

            Assert.assertTrue( testFile.delete() );

        }

    }

    @Test
    public void testComputeMD5_InputStream() throws Exception {

        // Verify that we can correctly compute the MD5 checksum of the string "hello world".

        // This may seem like an overly simplistic test.  Please keep in mind that we are NOT testing
        // any particular Java implementation of the MD5 algorithm.  Rather, we are testing that
        // the ObtuseUtil#computeMD5(java.io.InputStream) method correctly invokes the Java MD5 mechanism.

        ByteArrayInputStream is = new ByteArrayInputStream( "hello world".getBytes( "UTF-8" ) );
        Assert.assertEquals( ObtuseUtilTest.s_expectedMd5Value, ObtuseUtil.computeMD5( is ) );

    }

    @Test
    public void testSafeDivide_int_int() throws Exception {

        // Verify that dividing by 0 yields the default replacement value of 0

        Assert.assertEquals( 0, ObtuseUtil.safeDivide( 1, 0 ) );

        // Verify that dividing by non-zero yields the correct answer of 3 rather than the default replacement value of 0

        Assert.assertEquals( 2, ObtuseUtil.safeDivide( 4, 2 ) );

    }

    @Test
    public void testSafeDivide_int_int_int() throws Exception {

        // Verify that dividing by 0 yields our replacement value 2

        Assert.assertEquals( 2, ObtuseUtil.safeDivide( 1, 0, 2 ) );

        // Verify that dividing by non-zero yields the correct answer of 3 rather than the replacement value of 5

        Assert.assertEquals( 3, ObtuseUtil.safeDivide( 6, 2, 5 ) );

    }

    @Test
    public void testSafeDivide_long_long() throws Exception {

        // Verify that dividing by 0L yields the default replacement value of 0L

        Assert.assertEquals( 0L, ObtuseUtil.safeDivide( 1L, 0L ) );

        // Verify that dividing by non-zero yields the correct answer of 3L rather than the default replacement value of 0L

        Assert.assertEquals( 2L, ObtuseUtil.safeDivide( 4L, 2L ) );

    }

    @Test
    public void testSafeDivide_long_long_long() throws Exception {

        // Verify that dividing by 0L yields our replacement value 2L

        Assert.assertEquals( 2L, ObtuseUtil.safeDivide( 1L, 0L, 2L ) );

        // Verify that dividing by non-zero yields the correct answer of 3L rather than the replacement value of 5L

        Assert.assertEquals( 3L, ObtuseUtil.safeDivide( 6L, 2L, 5L ) );

    }

    @Test
    public void testSafeDivide_double_double() throws Exception {

        // We can expect exact results when the denominator is 0 because the replacement value is 0.0

        Assert.assertEquals( 0.0, ObtuseUtil.safeDivide( 1.0, 0.0 ), 0.0 );

        // We can also expect exact results if we are careful what the numerator and denominator are

        Assert.assertEquals( 3.0, ObtuseUtil.safeDivide( 6.0, 2.0 ), 0.0 );


    }

    @Test
    public void testSafeDivide_double_double_double() throws Exception {

        // We can expect exact results when the denominator is 0 because we provide the replacement values

        Assert.assertEquals( 2.0, ObtuseUtil.safeDivide( 1.0, 0.0, 2.0 ), 0.0 );

        // We can also expect exact results if we are careful what the numerator and denominator are

        Assert.assertEquals( 3.0, ObtuseUtil.safeDivide( 6.0, 2.0, 5.0 ), 0.0 );

    }

    @Test
    public void testAddAll() throws Exception {

    }

}
