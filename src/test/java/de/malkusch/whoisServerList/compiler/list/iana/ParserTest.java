package de.malkusch.whoisServerList.compiler.list.iana;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain.State;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;

public class ParserTest {

    private InputStream inputStream;

    private Charset charset;

    @Before
    public void setCharset() {
        charset = Charset.forName("UTF-8");
    }

    @Before
    public void setInputStream() {
        inputStream = getClass().getResourceAsStream("/iana.whois");
    }

    @After
    public void closeInputStream() throws IOException {
        inputStream.close();
    }

    @Test
    public void testParse() throws IOException, InterruptedException {
        Parser parser = new Parser();
        parser.setKeys(IANATopLevelDomainBuilder.KEY_WHOIS,
                IANATopLevelDomainBuilder.KEY_CHANGED,
                IANATopLevelDomainBuilder.KEY_CREATED,
                IANATopLevelDomainBuilder.KEY_STATE);

        parser.parse(inputStream, charset);

        assertEquals("whois.afilias-srs.net",
                parser.getString(IANATopLevelDomainBuilder.KEY_WHOIS));
        assertEquals("2013-12-19",
                parser.getString(IANATopLevelDomainBuilder.KEY_CREATED));
        assertEquals("2014-01-11",
                parser.getString(IANATopLevelDomainBuilder.KEY_CHANGED));
        assertEquals("ACTIVE",
                parser.getString(IANATopLevelDomainBuilder.KEY_STATE));

        parser.close();
    }

    @Test
    public void testGetState()
            throws IOException, WhoisServerListException, InterruptedException {

        Parser parser = new Parser();
        parser.setKeys(IANATopLevelDomainBuilder.KEY_STATE);

        parser.parse(inputStream, charset);

        assertEquals(State.ACTIVE,
                parser.getState(IANATopLevelDomainBuilder.KEY_STATE));

        parser.close();
    }

    @Test
    public void testGetDate()
            throws IOException, WhoisServerListException, InterruptedException {

        Parser parser = new Parser();
        parser.setKeys(IANATopLevelDomainBuilder.KEY_CREATED);

        parser.parse(inputStream, charset);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2013, Calendar.DECEMBER, 19, 0, 0, 0);
        assertEquals(calendar.getTime().toString(),
                parser.getDate(IANATopLevelDomainBuilder.KEY_CREATED).toString());

        parser.close();
    }

    @Test
    public void testGetURLs() throws IOException, InterruptedException {
        Parser parser = new Parser();
        parser.parse(inputStream, charset);

        assertArrayEquals(
                new URL[] {new URL("http://internetregistry.info/")},
                parser.getURLs().toArray());

        parser.close();
    }

}
