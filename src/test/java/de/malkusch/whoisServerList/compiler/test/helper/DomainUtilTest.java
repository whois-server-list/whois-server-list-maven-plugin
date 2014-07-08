package de.malkusch.whoisServerList.compiler.test.helper;

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
	}

}
