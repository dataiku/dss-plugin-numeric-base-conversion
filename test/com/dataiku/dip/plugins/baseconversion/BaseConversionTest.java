package com.dataiku.dip.plugins.baseconversion;

import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Test;


public class BaseConversionTest {
    @Test
    public void testHexadecimalToDecimal() {
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal(""),"");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("qwerty"),"");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("1"),"1");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("A"),"10");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("a"),"10");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("0xa"),"10");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("0Xa"),"");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("0xA"),"10");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("Ah"),"10");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("AH"),"");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("ah"),"10");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("aH"),"");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("11b"),"283");
        // 0x3D 3Dh 3D
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("0x3D"),"61");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("3Dh"),"61");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("3D"),"61");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("FFFFFFFFFFFFFFFF"),"18446744073709551615");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("FFFFFFFFFFFFFFFFh"),"18446744073709551615");
        Assert.assertEquals(BaseConversion.hexadecimalToDecimal("0xFFFFFFFFFFFFFFFF"),"18446744073709551615");
    }

    @Test
    public void testBinaryToDecimal() {
        Assert.assertEquals(BaseConversion.binaryToDecimal(""),"");
        Assert.assertEquals(BaseConversion.binaryToDecimal("qwerty"),"");
        Assert.assertEquals(BaseConversion.binaryToDecimal("1"),"1");
        Assert.assertEquals(BaseConversion.binaryToDecimal("11"),"3");
        Assert.assertEquals(BaseConversion.binaryToDecimal("11b"),"3");
        Assert.assertEquals(BaseConversion.binaryToDecimal("11B"),"");
        Assert.assertEquals(BaseConversion.binaryToDecimal("11h"),"");
        Assert.assertEquals(BaseConversion.binaryToDecimal("111111111111111111111111111111111111111111111111111111111111111"),"9223372036854775807");
        Assert.assertEquals(BaseConversion.binaryToDecimal("1111111111111111111111111111111111111111111111111111111111111111"),"18446744073709551615");
        // 11011b 11011
        Assert.assertEquals(BaseConversion.binaryToDecimal("11011b"),"27");
        Assert.assertEquals(BaseConversion.binaryToDecimal("11011"),"27");
    }

    @Test
    public void testBinaryToHexadecimal() {
        Assert.assertEquals(BaseConversion.binaryToHexadecimal(""),"");
        Assert.assertEquals(BaseConversion.binaryToHexadecimal("qwerty"),"");
        Assert.assertEquals(BaseConversion.binaryToHexadecimal("1"),"1");
        Assert.assertEquals(BaseConversion.binaryToHexadecimal("1010"),"A");
        Assert.assertEquals(BaseConversion.binaryToHexadecimal("1111111111111111111111111111111111111111111111111111111111111111"), "FFFFFFFFFFFFFFFF");
        Assert.assertEquals(BaseConversion.binaryToHexadecimal("1111111111111111111111111111111111111111111111111111111111111111b"), "FFFFFFFFFFFFFFFF");
    }

    @Test
    public void testHexadecimalToBinary() {
        Assert.assertEquals(BaseConversion.hexadecimalToBinary(""),"");
        Assert.assertEquals(BaseConversion.hexadecimalToBinary("qwerty"),"");
        Assert.assertEquals(BaseConversion.hexadecimalToBinary("1"),"1");
        Assert.assertEquals(BaseConversion.hexadecimalToBinary("a"),"1010");
        Assert.assertEquals(BaseConversion.hexadecimalToBinary("ah"),"1010");
        Assert.assertEquals(BaseConversion.hexadecimalToBinary("0xa"),"1010");
        Assert.assertEquals(BaseConversion.hexadecimalToBinary("FFFFFFFFFFFFFFFF"),"1111111111111111111111111111111111111111111111111111111111111111");
        Assert.assertEquals(BaseConversion.hexadecimalToBinary("FFFFFFFFFFFFFFFFh"),"1111111111111111111111111111111111111111111111111111111111111111");
        Assert.assertEquals(BaseConversion.hexadecimalToBinary("0xFFFFFFFFFFFFFFFF"),"1111111111111111111111111111111111111111111111111111111111111111");
    }

    @Test
    public void testDecimalToHexadecimal() {
        Assert.assertEquals(BaseConversion.decimalToHexadecimal(""),"");
        Assert.assertEquals(BaseConversion.decimalToHexadecimal("qwerty"),"");
        Assert.assertEquals(BaseConversion.decimalToHexadecimal("1"),"1");
        Assert.assertEquals(BaseConversion.decimalToHexadecimal("-1"),"");
        Assert.assertEquals(BaseConversion.decimalToHexadecimal("10"),"A");
        Assert.assertEquals(BaseConversion.decimalToHexadecimal("10.1"),"");
        Assert.assertEquals(BaseConversion.decimalToHexadecimal("10,1"),"");
        Assert.assertEquals(BaseConversion.decimalToHexadecimal("10,"),"");
        Assert.assertEquals(BaseConversion.decimalToHexadecimal("10."),"");
        Assert.assertEquals(BaseConversion.decimalToHexadecimal("18446744073709551615"),"FFFFFFFFFFFFFFFF");
    }

    @Test
    public void testDecimalTobinary() {
        Assert.assertEquals(BaseConversion.decimalToBinary(""),"");
        Assert.assertEquals(BaseConversion.decimalToBinary("qwerty"),"");
        Assert.assertEquals(BaseConversion.decimalToBinary("1"),"1");
        Assert.assertEquals(BaseConversion.decimalToBinary("-1"),"");
        Assert.assertEquals(BaseConversion.decimalToBinary("3"),"11");
        Assert.assertEquals(BaseConversion.decimalToBinary("1a"),"");
        Assert.assertEquals(BaseConversion.decimalToBinary("9223372036854775807"),"111111111111111111111111111111111111111111111111111111111111111");
        Assert.assertEquals(BaseConversion.decimalToBinary("18446744073709551615"),"1111111111111111111111111111111111111111111111111111111111111111");
        Assert.assertEquals(BaseConversion.decimalToBinary("18446744073709551615.1"),"");
        Assert.assertEquals(BaseConversion.decimalToBinary("18446744073709551615,1"),"");
    }
}