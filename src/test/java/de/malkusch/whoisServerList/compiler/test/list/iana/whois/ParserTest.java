package de.malkusch.whoisServerList.compiler.test.list.iana.whois;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.list.iana.TopLevelDomainFactory;
import de.malkusch.whoisServerList.compiler.list.iana.whois.Parser;
import de.malkusch.whoisServerList.compiler.model.domain.Domain.State;

public class ParserTest {

	private InputStream inputStream;

	@Before
	public void setInputStream() {
		inputStream = getClass().getResourceAsStream("/iana.whois");
	}

	@After
	public void closeInputStream() throws IOException {
		inputStream.close();
	}

	@Test
	public void testParse() throws IOException {
		Parser parser = new Parser();
		parser.setKeys(TopLevelDomainFactory.KEY_WHOIS,
				TopLevelDomainFactory.KEY_CHANGED,
				TopLevelDomainFactory.KEY_CREATED,
				TopLevelDomainFactory.KEY_STATE);

		parser.parse(inputStream);

		assertEquals("whois.afilias-srs.net",
				parser.getString(TopLevelDomainFactory.KEY_WHOIS));
		assertEquals("2013-12-19",
				parser.getString(TopLevelDomainFactory.KEY_CREATED));
		assertEquals("2014-01-11",
				parser.getString(TopLevelDomainFactory.KEY_CHANGED));
		assertEquals("ACTIVE",
				parser.getString(TopLevelDomainFactory.KEY_STATE));

		parser.close();
	}

	@Test
	public void testGetState() throws IOException, WhoisServerListException {
		Parser parser = new Parser();
		parser.setKeys(TopLevelDomainFactory.KEY_STATE);

		parser.parse(inputStream);
		
		assertEquals(State.ACTIVE,
				parser.getState(TopLevelDomainFactory.KEY_STATE));
		
		parser.close();
	}
	
	@Test
	public void testGetDate() throws IOException, WhoisServerListException {
		Parser parser = new Parser();
		parser.setKeys(TopLevelDomainFactory.KEY_CREATED);

		parser.parse(inputStream);

		Calendar calendar = Calendar.getInstance();
		calendar.set(2013, Calendar.DECEMBER, 19, 0, 0, 0);
		assertEquals(calendar.getTime().toString(),
				parser.getDate(TopLevelDomainFactory.KEY_CREATED).toString());

		parser.close();
	}
	
	@Test
	public void testGetURLs() throws IOException {
		Parser parser = new Parser();
		parser.parse(inputStream);
		
		assertArrayEquals(new URL[] {new URL("http://internetregistry.info/")}, parser.getURLs().toArray());
		
		parser.close();
	}

}
