package de.malkusch.whoisServerList.compiler.list.iana;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.helper.DomainUtil;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.list.iana.IanaDomainListFactory;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

public class IanaDomainListFactoryTest {

	@Test
	public void testbuildList() throws IOException, BuildListException, InterruptedException {
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/compiler.properties"));
		
		IanaDomainListFactory listFactory = new IanaDomainListFactory(properties);
		List<TopLevelDomain> domains = listFactory.buildList();
		
		assertTrue(domains.size() > 500);
		
		for (TopLevelDomain domain : domains) {
			assertFalse(domain.getName().isEmpty());
			
			assertNotNull(domain.getState());
			
			if (! domain.getWhoisServers().isEmpty()) {
				assertEquals(1, domain.getWhoisServers().size());
				WhoisServer server = domain.getWhoisServers().get(0);
				assertFalse(server.getHost().isEmpty());
				
			}
			
			if (DomainUtil.isCountryCode(domain.getName())) {
				CountryCodeTopLevelDomain ccDomain = (CountryCodeTopLevelDomain) domain;
				assertEquals(domain.getName().toUpperCase(), ccDomain.getCountryCode());
				
			}
		}
	}
	
}
