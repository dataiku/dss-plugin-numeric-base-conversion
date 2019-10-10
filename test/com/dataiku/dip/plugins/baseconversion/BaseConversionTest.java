package com.dataiku.dip.plugins.baseconversion;

import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Test;


public class BaseConversionTest {
    @Test
    public void testHexaToDecimal() {
        Assert.assertEquals(BaseConversion.hexaToDecimal(""),"");
        Assert.assertEquals(BaseConversion.hexaToDecimal("qwerty"),"");
        Assert.assertEquals(BaseConversion.hexaToDecimal("1"),"1");
        Assert.assertEquals(BaseConversion.hexaToDecimal("A"),"10");
        Assert.assertEquals(BaseConversion.hexaToDecimal("a"),"10");
        Assert.assertEquals(BaseConversion.hexaToDecimal("0xa"),"10");
        Assert.assertEquals(BaseConversion.hexaToDecimal("0Xa"),"");
        Assert.assertEquals(BaseConversion.hexaToDecimal("0xA"),"10");
        Assert.assertEquals(BaseConversion.hexaToDecimal("Ah"),"10");
        Assert.assertEquals(BaseConversion.hexaToDecimal("AH"),"");
        Assert.assertEquals(BaseConversion.hexaToDecimal("ah"),"10");
        Assert.assertEquals(BaseConversion.hexaToDecimal("aH"),"");
        Assert.assertEquals(BaseConversion.hexaToDecimal("11b"),"283");
        // 0x3D 3Dh 3D
        Assert.assertEquals(BaseConversion.hexaToDecimal("0x3D"),"61");
        Assert.assertEquals(BaseConversion.hexaToDecimal("3Dh"),"61");
        Assert.assertEquals(BaseConversion.hexaToDecimal("3D"),"61");
        Assert.assertEquals(BaseConversion.hexaToDecimal("FFFFFFFFFFFFFFFF"),"18446744073709551615");
        Assert.assertEquals(BaseConversion.hexaToDecimal("FFFFFFFFFFFFFFFFh"),"18446744073709551615");
        Assert.assertEquals(BaseConversion.hexaToDecimal("0xFFFFFFFFFFFFFFFF"),"18446744073709551615");
    }

    @Test
    public void testBinToDecimal() {
        Assert.assertEquals(BaseConversion.binToDecimal(""),"");
        Assert.assertEquals(BaseConversion.binToDecimal("qwerty"),"");
        Assert.assertEquals(BaseConversion.binToDecimal("1"),"1");
        Assert.assertEquals(BaseConversion.binToDecimal("11"),"3");
        Assert.assertEquals(BaseConversion.binToDecimal("11b"),"3");
        Assert.assertEquals(BaseConversion.binToDecimal("11B"),"");
        Assert.assertEquals(BaseConversion.binToDecimal("11h"),"");
        Assert.assertEquals(BaseConversion.binToDecimal("111111111111111111111111111111111111111111111111111111111111111"),"9223372036854775807");
        Assert.assertEquals(BaseConversion.binToDecimal("1111111111111111111111111111111111111111111111111111111111111111"),"18446744073709551615");
        // 11011b 11011
        Assert.assertEquals(BaseConversion.binToDecimal("11011b"),"27");
        Assert.assertEquals(BaseConversion.binToDecimal("11011"),"27");
    }

    @Test
    public void testBinToHexa() {
        Assert.assertEquals(BaseConversion.binToHexa(""),"");
        Assert.assertEquals(BaseConversion.binToHexa("qwerty"),"");
        Assert.assertEquals(BaseConversion.binToHexa("1"),"1");
        Assert.assertEquals(BaseConversion.binToHexa("1010"),"A");
        Assert.assertEquals(BaseConversion.binToHexa("1111111111111111111111111111111111111111111111111111111111111111"), "FFFFFFFFFFFFFFFF");
        Assert.assertEquals(BaseConversion.binToHexa("1111111111111111111111111111111111111111111111111111111111111111b"), "FFFFFFFFFFFFFFFF");
    }

    @Test
    public void testHexaToBin() {
        Assert.assertEquals(BaseConversion.hexaToBin(""),"");
        Assert.assertEquals(BaseConversion.hexaToBin("qwerty"),"");
        Assert.assertEquals(BaseConversion.hexaToBin("1"),"1");
        Assert.assertEquals(BaseConversion.hexaToBin("a"),"1010");
        Assert.assertEquals(BaseConversion.hexaToBin("ah"),"1010");
        Assert.assertEquals(BaseConversion.hexaToBin("0xa"),"1010");
        Assert.assertEquals(BaseConversion.hexaToBin("FFFFFFFFFFFFFFFF"),"1111111111111111111111111111111111111111111111111111111111111111");
        Assert.assertEquals(BaseConversion.hexaToBin("FFFFFFFFFFFFFFFFh"),"1111111111111111111111111111111111111111111111111111111111111111");
        Assert.assertEquals(BaseConversion.hexaToBin("0xFFFFFFFFFFFFFFFF"),"1111111111111111111111111111111111111111111111111111111111111111");
    }

    @Test
    public void testDecimalToHexa() {
        Assert.assertEquals(BaseConversion.decimalToHexa(""),"");
        Assert.assertEquals(BaseConversion.decimalToHexa("qwerty"),"");
        Assert.assertEquals(BaseConversion.decimalToHexa("1"),"1");
        Assert.assertEquals(BaseConversion.decimalToHexa("-1"),"");
        Assert.assertEquals(BaseConversion.decimalToHexa("10"),"A");
        Assert.assertEquals(BaseConversion.decimalToHexa("10.1"),"A");
        Assert.assertEquals(BaseConversion.decimalToHexa("10,1"),"A");
        Assert.assertEquals(BaseConversion.decimalToHexa("10,"),"A");
        Assert.assertEquals(BaseConversion.decimalToHexa("10."),"A");
        Assert.assertEquals(BaseConversion.decimalToHexa("18446744073709551615"),"FFFFFFFFFFFFFFFF");
    }

    @Test
    public void testDecimalTobin() {
        Assert.assertEquals(BaseConversion.decimalToBin(""),"");
        Assert.assertEquals(BaseConversion.decimalToBin("qwerty"),"");
        Assert.assertEquals(BaseConversion.decimalToBin("1"),"1");
        Assert.assertEquals(BaseConversion.decimalToBin("-1"),"");
        Assert.assertEquals(BaseConversion.decimalToBin("3"),"11");
        Assert.assertEquals(BaseConversion.decimalToBin("1a"),"");
        Assert.assertEquals(BaseConversion.decimalToBin("9223372036854775807"),"111111111111111111111111111111111111111111111111111111111111111");
        Assert.assertEquals(BaseConversion.decimalToBin("18446744073709551615"),"1111111111111111111111111111111111111111111111111111111111111111");
        Assert.assertEquals(BaseConversion.decimalToBin("18446744073709551615.1"),"1111111111111111111111111111111111111111111111111111111111111111");
        Assert.assertEquals(BaseConversion.decimalToBin("18446744073709551615,1"),"1111111111111111111111111111111111111111111111111111111111111111");
    }
}