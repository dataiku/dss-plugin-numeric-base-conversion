package com.dataiku.dip.plugins.numericbaseconversion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.dataiku.dip.plugins.numericbaseconversion.NumericBaseConversion.ProcessingMode;


public class NumericBaseConversionTest {
    @Test
    public void testHexadecimalToDecimal() {
        assertEquals(hexaToDecimal(""), "");
        assertEquals(hexaToDecimal("qwerty"), "");
        assertEquals(hexaToDecimal("1"), "1");
        assertEquals(hexaToDecimal("A"), "10");
        assertEquals(hexaToDecimal("a"), "10");
        assertEquals(hexaToDecimal("0xa"), "10");
        assertEquals(hexaToDecimal("0Xa"), "10");
        assertEquals(hexaToDecimal("0xA"), "10");
        assertEquals(hexaToDecimal("Ah"), "10");
        assertEquals(hexaToDecimal("AH"), "10");
        assertEquals(hexaToDecimal("ah"), "10");
        assertEquals(hexaToDecimal("aH"), "10");
        assertEquals(hexaToDecimal("aJ"), "");
        assertEquals(hexaToDecimal("11b"), "283");
        // 0x3D 3Dh 3D
        assertEquals(hexaToDecimal("0x3D"), "61");
        assertEquals(hexaToDecimal("0X3D"), "61");
        assertEquals(hexaToDecimal("3Dh"), "61");
        assertEquals(hexaToDecimal("3DH"), "61");
        assertEquals(hexaToDecimal("3D"), "61");
        assertEquals(hexaToDecimal("FFFFFFFFFFFFFFFF"), "18446744073709551615");
        assertEquals(hexaToDecimal("FFFFFFFFFFFFFFFFh"), "18446744073709551615");
        assertEquals(hexaToDecimal("0xFFFFFFFFFFFFFFFF"), "18446744073709551615");
        assertEquals(hexaToDecimal("0XFF"), "255");
    }

    @Test
    public void testBinaryToDecimal() {
        assertEquals(binaryToDecimal(""), "");
        assertEquals(binaryToDecimal("qwerty"), "");
        assertEquals(binaryToDecimal("1"), "1");
        assertEquals(binaryToDecimal("11"), "3");
        assertEquals(binaryToDecimal("11b"), "3");
        assertEquals(binaryToDecimal("11B"), "3");
        assertEquals(binaryToDecimal("11h"), "");
        assertEquals(binaryToDecimal("11H"), "");
        assertEquals(binaryToDecimal("111111111111111111111111111111111111111111111111111111111111111"), "9223372036854775807");
        assertEquals(binaryToDecimal("1111111111111111111111111111111111111111111111111111111111111111"), "18446744073709551615");
        // 11011b 11011
        assertEquals(binaryToDecimal("11011b"), "27");
        assertEquals(binaryToDecimal("11011"), "27");
    }

    @Test
    public void testBinaryToHexadecimal() {
        assertEquals(binaryToHexa(""), "");
        assertEquals(binaryToHexa("qwerty"), "");
        assertEquals(binaryToHexa("1"), "1");
        assertEquals(binaryToHexa("1010"), "A");
        assertEquals(binaryToHexa("1010b"), "A");
        assertEquals(binaryToHexa("1111111111111111111111111111111111111111111111111111111111111111"), "FFFFFFFFFFFFFFFF");
        assertEquals(binaryToHexa("1111111111111111111111111111111111111111111111111111111111111111b"), "FFFFFFFFFFFFFFFF");
    }

    @Test
    public void testHexadecimalToBinary() {
        assertEquals(hexaToBinary(""), "");
        assertEquals(hexaToBinary("qwerty"), "");
        assertEquals(hexaToBinary("1"), "1");
        assertEquals(hexaToBinary("a"), "1010");
        assertEquals(hexaToBinary("ah"), "1010");
        assertEquals(hexaToBinary("0xa"), "1010");
        assertEquals(hexaToBinary("FFFFFFFFFFFFFFFF"), "1111111111111111111111111111111111111111111111111111111111111111");
        assertEquals(hexaToBinary("FFFFFFFFFFFFFFFFh"), "1111111111111111111111111111111111111111111111111111111111111111");
        assertEquals(hexaToBinary("0xFFFFFFFFFFFFFFFF"), "1111111111111111111111111111111111111111111111111111111111111111");
    }

    @Test
    public void testDecimalToHexadecimal() {
        assertEquals(decimalToHexa(""), "");
        assertEquals(decimalToHexa("qwerty"), "");
        assertEquals(decimalToHexa("1"), "1");
        assertEquals(decimalToHexa("-1"), "");
        assertEquals(decimalToHexa("10"), "A");
        assertEquals(decimalToHexa("10.1"), "");
        assertEquals(decimalToHexa("10,1"), "");
        assertEquals(decimalToHexa("10,"), "");
        assertEquals(decimalToHexa("10."), "");
        assertEquals(decimalToHexa("18446744073709551615"), "FFFFFFFFFFFFFFFF");
    }

    @Test
    public void testDecimalTobinary() {
        assertEquals(decimalToBinary(""), "");
        assertEquals(decimalToBinary("qwerty"), "");
        assertEquals(decimalToBinary("1"), "1");
        assertEquals(decimalToBinary("-1"), "");
        assertEquals(decimalToBinary("3"), "11");
        assertEquals(decimalToBinary("1a"), "");
        assertEquals(decimalToBinary("9223372036854775807"), "111111111111111111111111111111111111111111111111111111111111111");
        assertEquals(decimalToBinary("18446744073709551615"), "1111111111111111111111111111111111111111111111111111111111111111");
        assertEquals(decimalToBinary("18446744073709551615.1"), "");
        assertEquals(decimalToBinary("18446744073709551615,1"), "");
    }

    @Test(expected = NullPointerException.class)
    public void decimalToBinaryShouldThrowExceptionWhenNull() {
        decimalToBinary(null);
    }

    @Test(expected = NullPointerException.class)
    public void decimalToHexaShouldThrowExceptionWhenNull() {
        decimalToHexa(null);
    }

    @Test(expected = NullPointerException.class)
    public void hexaToBinaryShouldThrowExceptionWhenNull() {
        hexaToBinary(null);
    }

    @Test(expected = NullPointerException.class)
    public void hexaToDecimalShouldThrowExceptionWhenNull() {
        hexaToDecimal(null);
    }

    @Test(expected = NullPointerException.class)
    public void binaryToHexaShouldThrowExceptionWhenNull() {
        binaryToHexa(null);
    }

    @Test(expected = NullPointerException.class)
    public void binaryToDecimalShouldThrowExceptionWhenNull() {
        binaryToDecimal(null);
    }

    private static String decimalToBinary(String str) {
        return convert(ProcessingMode.DECIMAL_TO_BINARY, str);
    }

    private static String binaryToDecimal(String str) {
        return convert(ProcessingMode.BINARY_TO_DECIMAL, str);
    }

    private static String hexaToDecimal(String str) {
        return convert(ProcessingMode.HEXA_TO_DECIMAL, str);
    }

    private static String decimalToHexa(String str) {
        return convert(ProcessingMode.DECIMAL_TO_HEXA, str);
    }

    private static String binaryToHexa(String str) {
        return convert(ProcessingMode.BINARY_TO_HEXA, str);
    }

    private static String hexaToBinary(String str) {
        return convert(ProcessingMode.HEXA_TO_BINARY, str);
    }

    private static String convert(ProcessingMode processingMode, String str) {
        return NumericBaseConversion.newConverter(processingMode).convert(str);
    }
}