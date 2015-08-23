package de.malkusch.whoisServerList.compiler.helper;

import static org.junit.Assert.*;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.helper.DomainUtil;

public class DomainUtilTest {

    @Test
    public void testNormalize() {
        assertEquals("de", DomainUtil.normalize("DE"));
        assertEquals("de", DomainUtil.normalize("de"));
        assertEquals("de", DomainUtil.normalize("De"));
        assertEquals("قطر", DomainUtil.normalize("قطر"));
    }

    @Test
    public void testIsCountryCode() {
        assertTrue(DomainUtil.isCountryCode("de"));
        assertTrue(DomainUtil.isCountryCode("DE"));
        assertTrue(DomainUtil.isCountryCode("uk"));
    }
    
    @Test
    public void testGetCountryCode() {
        assertEquals(null, DomainUtil.getCountryCode("xx"));
        assertEquals("DE", DomainUtil.getCountryCode("de"));
        assertEquals("GB", DomainUtil.getCountryCode("uk"));
        assertEquals("GB", DomainUtil.getCountryCode("gb"));
        assertEquals("SH", DomainUtil.getCountryCode("ac"));
        assertEquals("RU", DomainUtil.getCountryCode("su"));
        assertEquals("EU", DomainUtil.getCountryCode("eu"));
        assertEquals("TL", DomainUtil.getCountryCode("tp"));
    }

}
