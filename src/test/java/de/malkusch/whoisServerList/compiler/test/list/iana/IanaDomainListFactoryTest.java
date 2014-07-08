package de.malkusch.whoisServerList.compiler.test.list.iana;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.list.BuildListException;
import de.malkusch.whoisServerList.compiler.list.iana.IanaDomainListFactory;

public class IanaDomainListFactoryTest {

	@Test
	public void testbuildList() throws IOException, BuildListException {
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/compiler.properties"));
		
		IanaDomainListFactory listFactory = new IanaDomainListFactory(properties);
		listFactory.buildList();
	}
	
}
